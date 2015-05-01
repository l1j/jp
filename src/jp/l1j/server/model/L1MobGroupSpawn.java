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

import java.util.logging.Level;
import java.util.logging.Logger;

import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.datatables.MobGroupTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1MobGroup;
import jp.l1j.server.templates.L1NpcCount;

// Referenced classes of package jp.l1j.server.model:
// L1MobGroupSpawn

public class L1MobGroupSpawn {

	private static Logger _log = Logger.getLogger(L1MobGroupSpawn.class
			.getName());

	private static L1MobGroupSpawn _instance;

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	private boolean _isRespawnScreen;

	private boolean _isInitSpawn;

	private L1MobGroupSpawn() {
	}

	public static L1MobGroupSpawn getInstance() {
		if (_instance == null) {
			_instance = new L1MobGroupSpawn();
		}
		return _instance;
	}

	public void doSpawn(L1NpcInstance leader, int groupId,
			boolean isRespawnScreen, boolean isInitSpawn) {

		L1MobGroup mobGroup = MobGroupTable.getInstance().getTemplate(groupId);
		if (mobGroup == null) {
			return;
		}

		L1NpcInstance mob;
		_isRespawnScreen = isRespawnScreen;
		_isInitSpawn = isInitSpawn;

		L1MobGroupInfo mobGroupInfo = new L1MobGroupInfo();

		mobGroupInfo.setRemoveGroup(mobGroup.isRemoveGroupIfLeaderDie());
		mobGroupInfo.addMember(leader);

		for (L1NpcCount minion : mobGroup.getMinions()) {
			if (minion.isZero()) {
				continue;
			}
			for (int i = 0; i < minion.getCount(); i++) {
				mob = spawn(leader, minion.getId());
				if (mob != null) {
					mobGroupInfo.addMember(mob);
				}
			}
		}
	}

	private L1NpcInstance spawn(L1NpcInstance leader, int npcId) {
		L1NpcInstance mob = null;
		try {
			mob = NpcTable.getInstance().newNpcInstance(npcId);

			mob.setId(IdFactory.getInstance().nextId());

			mob.setHeading(leader.getHeading());
			mob.setMap(leader.getMapId());
			mob.setMovementDistance(leader.getMovementDistance());
			mob.setRest(leader.isRest());

			mob.setX(leader.getX() + _random.nextInt(5) - 2);
			mob.setY(leader.getY() + _random.nextInt(5) - 2);
			// マップ外、障害物上、画面内沸き不可で画面内にPCがいる場合、リーダーと同じ座標
			if (!canSpawn(mob)) {
				mob.setX(leader.getX());
				mob.setY(leader.getY());
			}
			mob.setHomeX(mob.getX());
			mob.setHomeY(mob.getY());

			if (mob instanceof L1MonsterInstance) {
				((L1MonsterInstance) mob).initHideForMinion(leader);
			}

			mob.setSpawn(leader.getSpawn());
			mob.setreSpawn(leader.isReSpawn());
			mob.setSpawnNumber(leader.getSpawnNumber());

			if (mob instanceof L1MonsterInstance) {
				if (mob.getMapId() == 666) {
					((L1MonsterInstance) mob).setStoreDroped(true);
				}
			}

			L1World.getInstance().storeObject(mob);
			L1World.getInstance().addVisibleObject(mob);

			if (mob instanceof L1MonsterInstance) {
				if (!_isInitSpawn && mob.getHiddenStatus() == 0) {
					mob.onNpcAI(); // モンスターのＡＩを開始
				}
			}
			mob.updateLight();
			mob.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return mob;
	}

	private boolean canSpawn(L1NpcInstance mob) {
		if (mob.getMap().isInMap(mob.getLocation())
				&& mob.getMap().isPassable(mob.getLocation())) {
			if (_isRespawnScreen) {
				return true;
			}
			if (L1World.getInstance().getVisiblePlayer(mob).size() == 0) {
				return true;
			}
		}
		return false;
	}

}
