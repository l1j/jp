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
package jp.l1j.server.model;

import java.util.logging.Logger;

import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.UbTable;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_NpcPack;

public class L1UbSpawn implements Comparable<L1UbSpawn> {
	private int _id;
	private int _ubId;
	private int _pattern;
	private int _group;
	private int _npcTemplateId;
	private int _amount;
	private int _spawnDelay;
	private int _sealCount;
	private String _name;

	// --------------------start getter/setter--------------------
	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
	}

	public int getUbId() {
		return _ubId;
	}

	public void setUbId(int ubId) {
		_ubId = ubId;
	}

	public int getPattern() {
		return _pattern;
	}

	public void setPattern(int pattern) {
		_pattern = pattern;
	}

	public int getGroup() {
		return _group;
	}

	public void setGroup(int group) {
		_group = group;
	}

	public int getNpcTemplateId() {
		return _npcTemplateId;
	}

	public void setNpcTemplateId(int npcTemplateId) {
		_npcTemplateId = npcTemplateId;
	}

	public int getAmount() {
		return _amount;
	}

	public void setAmount(int amount) {
		_amount = amount;
	}

	public int getSpawnDelay() {
		return _spawnDelay;
	}

	public void setSpawnDelay(int spawnDelay) {
		_spawnDelay = spawnDelay;
	}

	public int getSealCount() {
		return _sealCount;
	}

	public void setSealCount(int i) {
		_sealCount = i;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	// --------------------end getter/setter--------------------

	public void spawnOne() {
		L1UltimateBattle ub = UbTable.getInstance().getUb(_ubId);
		L1Location loc = ub.getLocation().randomLocation(
				(ub.getLocX2() - ub.getLocX1()) / 2, false);
		L1MonsterInstance mob = new L1MonsterInstance(NpcTable.getInstance()
				.getTemplate(getNpcTemplateId()));
		if (mob == null) {
			_log.warning("mob == null");
			return;
		}

		mob.setId(IdFactory.getInstance().nextId());
		mob.setHeading(5);
		mob.setX(loc.getX());
		mob.setHomeX(loc.getX());
		mob.setY(loc.getY());
		mob.setHomeY(loc.getY());
		mob.setMap((short) loc.getMapId());
		mob.setStoreDroped(!(3 < getGroup()));
		mob.setUbSealCount(getSealCount());
		mob.setUbId(getUbId());

		L1World.getInstance().storeObject(mob);
		L1World.getInstance().addVisibleObject(mob);

		S_NpcPack S_NpcPack = new S_NpcPack(mob);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(mob)) {
			pc.addKnownObject(mob);
			mob.addKnownObject(pc);
			pc.sendPackets(S_NpcPack);
		}
		// モンスターのＡＩを開始
		mob.onNpcAI();
		mob.updateLight();
// mob.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
	}

	public void spawnAll() {
		for (int i = 0; i < getAmount(); i++) {
			spawnOne();
		}
	}

	public int compareTo(L1UbSpawn rhs) {
		// XXX - 本当はもっと厳密な順序付けがあるはずだが、必要なさそうなので後回し
		if (getId() < rhs.getId()) {
			return -1;
		}
		if (getId() > rhs.getId()) {
			return 1;
		}
		return 0;
	}

	private static Logger _log = Logger.getLogger(L1UbSpawn.class
			.getName());
}
