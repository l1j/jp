/**
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
package jp.l1j.server.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1DragonSlayer;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1NpcDeleteTimer;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_NpcPack;
import jp.l1j.server.random.RandomGeneratorFactory;

public class L1SpawnUtil {
	private static Logger _log = Logger.getLogger(L1SpawnUtil.class.getName());

	public static void spawn(L1PcInstance pc, int npcId, int randomRange,
			int timeMillisToDelete) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(IdFactory.getInstance().nextId());
			npc.setMap(pc.getMapId());
			if (randomRange == 0) {
				npc.getLocation().set(pc.getLocation());
				npc.getLocation().forward(pc.getHeading());
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(pc.getX() + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					npc.setY(pc.getY() + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation())
							&& npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.getLocation().set(pc.getLocation());
					npc.getLocation().forward(pc.getHeading());
				}
			}
			
			if (npc.getNpcId() == 91051) { // ドラゴンポータル(地)
				for (int i = 0; i < 6; i++) {
					if (!L1DragonSlayer.getInstance().getPortalNumber()[i]) {
						L1DragonSlayer.getInstance().setPortalNumber(i, true);
						L1DragonSlayer.getInstance().resetDragonSlayer(i);
						npc.setPortalNumber(i);
						L1DragonSlayer.getInstance().portalPack()[i] = npc;
						break;
					}
				}
			} else if (npc.getNpcId() == 91052) { // ドラゴンポータル(水)
				for (int i = 6; i < 12; i++) {
					if (!L1DragonSlayer.getInstance().getPortalNumber()[i]) {
						L1DragonSlayer.getInstance().setPortalNumber(i, true);
						L1DragonSlayer.getInstance().resetDragonSlayer(i);
						npc.setPortalNumber(i);
						L1DragonSlayer.getInstance().portalPack()[i] = npc;
						break;
					}
				}
			} else if (npc.getNpcId() == 91053) { // ドラゴンポータル(風)
				for (int i = 12; i < 18; i++) {
					if (!L1DragonSlayer.getInstance().getPortalNumber()[i]) {
						L1DragonSlayer.getInstance().setPortalNumber(i, true);
						L1DragonSlayer.getInstance().resetDragonSlayer(i);
						npc.setPortalNumber(i);
						L1DragonSlayer.getInstance().portalPack()[i] = npc;
						break;
					}
				}
			} else if (npc.getNpcId() == 91054) { // ドラゴンポータル(火)
				for (int i = 18; i < 24; i++) {
					if (!L1DragonSlayer.getInstance().getPortalNumber()[i]) {
						L1DragonSlayer.getInstance().setPortalNumber(i, true);
						L1DragonSlayer.getInstance().resetDragonSlayer(i);
						npc.setPortalNumber(i);
						L1DragonSlayer.getInstance().portalPack()[i] = npc;
						break;
					}
				}
			}
			
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(pc.getHeading());

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			npc.updateLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
			if (0 < timeMillisToDelete) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc,
						timeMillisToDelete);
				timer.begin();
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	public static void summonMonster(L1Character master, int summonId) {
		try {
			L1NpcInstance mob = NpcTable.getInstance().newNpcInstance(summonId);
			mob.setId(IdFactory.getInstance().nextId());
			L1Location loc = master.getLocation().randomLocation(8, false);
			mob.setX(loc.getX());
			mob.setY(loc.getY());
			mob.setHomeX(loc.getX());
			mob.setHomeY(loc.getY());
			short mapid = master.getMapId();
			mob.setMap(mapid);
			int heading = RandomGeneratorFactory.getSharedRandom().nextInt(8);
			mob.setHeading(heading);
			L1World.getInstance().storeObject(mob);
			L1World.getInstance().addVisibleObject(mob);
			L1Object object = L1World.getInstance().findObject(mob.getId());
			L1MonsterInstance newnpc = (L1MonsterInstance) object;
			newnpc.setStoreDroped(true); // 召喚されたモンスターはドロップ無し
			if (summonId == 45061 // カーズドスパルトイ
					|| summonId == 45161 // スパルトイ
					|| summonId == 45181 // スパルトイ
					|| summonId == 45455) { // デッドリースパルトイ
				newnpc.broadcastPacket(new S_DoActionGFX(newnpc.getId(),
						ActionCodes.ACTION_Hide));
				newnpc.setStatus(13);
				newnpc.broadcastPacket(new S_NpcPack(newnpc));
				newnpc.broadcastPacket(new S_DoActionGFX(newnpc.getId(),
						ActionCodes.ACTION_Appear));
				newnpc.setStatus(0);
				newnpc.broadcastPacket(new S_NpcPack(newnpc));
			}
			newnpc.onNpcAI();
			newnpc.updateLight();
			newnpc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
}
