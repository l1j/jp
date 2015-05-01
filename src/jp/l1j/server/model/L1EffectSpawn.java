/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jp.l1j.server.model;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.instance.L1EffectInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.map.L1Map;
import jp.l1j.server.model.map.L1WorldMap;
import jp.l1j.server.model.skill.L1SkillId;
import jp.l1j.server.packets.server.S_NpcPack;
import jp.l1j.server.templates.L1Npc;
import static jp.l1j.server.model.skill.L1SkillId.*;

// Referenced classes of package jp.l1j.server.model:
// L1EffectSpawn

public class L1EffectSpawn {

	private static Logger _log = Logger.getLogger(L1EffectSpawn.class
			.getName());

	private static L1EffectSpawn _instance;

	private Constructor _constructor;

	private L1EffectSpawn() {
	}

	public static L1EffectSpawn getInstance() {
		if (_instance == null) {
			_instance = new L1EffectSpawn();
		}
		return _instance;
	}

	/**
	 * エフェクトオブジェクトを生成し設置する
	 * 
	 * @param npcId
	 *            エフェクトNPCのテンプレートID
	 * @param time
	 *            存在時間(ms)
	 * @param locX
	 *            設置する座標X
	 * @param locY
	 *            設置する座標Y
	 * @param mapId
	 *            設置するマップのID
	 * @return 生成されたエフェクトオブジェクト
	 */
	public L1EffectInstance spawnEffect(int npcId, int time, int locX,
			int locY, short mapId) {
		return spawnEffect(npcId, time, locX, locY, mapId, null, 0);
	}

	public L1EffectInstance spawnEffect(int npcId, int time, int locX,
			int locY, short mapId, L1PcInstance user, int skiiId) {
		L1Npc template = NpcTable.getInstance().getTemplate(npcId);
		L1EffectInstance effect = null;

		if (template == null) {
			return null;
		}

		String className = (new StringBuilder()).append(
				"jp.l1j.server.model.instance.").append(
				template.getImpl()).append("Instance").toString();

		try {
			_constructor = Class.forName(className).getConstructors()[0];
			Object obj[] = { template };
			effect = (L1EffectInstance) _constructor.newInstance(obj);

			effect.setId(IdFactory.getInstance().nextId());
			effect.setGfxId(template.getGfxId());
			effect.setX(locX);
			effect.setY(locY);
			effect.setHomeX(locX);
			effect.setHomeY(locY);
			effect.setHeading(0);
			effect.setMap(mapId);
			effect.setUser(user);
			effect.setSkillId(skiiId);
			L1World.getInstance().storeObject(effect);
			L1World.getInstance().addVisibleObject(effect);

			for (L1PcInstance pc : L1World.getInstance()
					.getRecognizePlayer(effect)) {
				effect.addKnownObject(pc);
				pc.addKnownObject(effect);
				pc.sendPackets(new S_NpcPack(effect));
				pc.broadcastPacket(new S_NpcPack(effect));
			}
			L1NpcDeleteTimer timer = new L1NpcDeleteTimer(effect, time);
			timer.begin();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

		return effect;
	}

	public void doSpawnFireWall(L1Character cha, int targetX, int targetY) {
		L1Npc firewall = NpcTable.getInstance().getTemplate(81157); // ファイアーウォール
		int duration = SkillTable.getInstance().findBySkillId(
				FIRE_WALL).getBuffDuration();

		if (firewall == null) {
			throw new NullPointerException(
					"FireWall data not found:npcid=81157");
		}

		L1Character base = cha;
		for (int i = 0; i < 8; i++) {
			int a = base.targetDirection(targetX, targetY);
			int x = base.getX();
			int y = base.getY();
			if (a == 1) {
				x++;
				y--;
			} else if (a == 2) {
				x++;
			} else if (a == 3) {
				x++;
				y++;
			} else if (a == 4) {
				y++;
			} else if (a == 5) {
				x--;
				y++;
			} else if (a == 6) {
				x--;
			} else if (a == 7) {
				x--;
				y--;
			} else if (a == 0) {
				y--;
			}
			if (!base.isAttackPosition(x, y, 1)) {
				x = base.getX();
				y = base.getY();
			}
			L1Map map = L1WorldMap.getInstance().getMap(cha.getMapId());
			if (!map.isArrowPassable(x, y, cha.getHeading())) {
				break;
			}

			L1EffectInstance effect = spawnEffect(81157, duration * 1000, x, y,
					cha.getMapId());
			if (effect == null) {
				break;
			}
			for (L1Object objects : L1World.getInstance()
					.getVisibleObjects(effect, 0)) {
				if (objects instanceof L1EffectInstance) {
					L1EffectInstance npc = (L1EffectInstance) objects;
					if (npc.getNpcTemplate().getNpcId() == 81157) {
						npc.deleteMe();
					}
				}
			}
			if (targetX == x && targetY == y) {
				break;
			}
			base = effect;
		}

	}
}
