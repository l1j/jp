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

package jp.l1j.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_MAP_LIST;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_NPC_LIST;
import jp.l1j.server.model.L1Spawn;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.NumberUtil;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class SpawnTable {
	private static Logger _log = Logger.getLogger(SpawnTable.class.getName());

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	
	private static SpawnTable _instance;

	private Map<Integer, L1Spawn> _spawntable = new HashMap<Integer, L1Spawn>();

	private int _highestId;

	public static SpawnTable getInstance() {
		if (_instance == null) {
			_instance = new SpawnTable();
		}
		return _instance;
	}

	private SpawnTable() {
		fillSpawnTable();
	}

	private void fillSpawnTable() {
		int spawnCount = 0;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawn_mobs");
			rs = pstm.executeQuery();
			L1Spawn spawnDat;
			L1Npc template1;
			while (rs.next()) {
				if (Config.HALLOWEEN_EVENT == false) {
					int npcid = rs.getInt("id");
					if (npcid >= 26656 && npcid <= 26734) {
						continue;
					}
				}
				int npcTemplateId = rs.getInt("npc_id");
				short mapId = rs.getShort("map_id");
				boolean isErr = false;
				if (NpcTable.getInstance().getTemplate(npcTemplateId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_NPC_LIST, npcTemplateId));
					// %s はNPCリストに存在しません。
					isErr = true;
				}
				if (MapTable.getInstance().locationname(mapId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, mapId));
					// %s はマップリストに存在しません。
					isErr = true;
				}
				if (isErr) {
					continue;
				}
				template1 = NpcTable.getInstance().getTemplate(npcTemplateId);
				int count;
				if (template1 == null) {
					_log.warning("mob data for id:" + npcTemplateId + " missing in npc table");
					spawnDat = null;
				} else {
					if (rs.getInt("count") == 0) {
						continue;
					}
					double amount_rate = MapTable.getInstance()
							.getMonsterAmount(mapId);
					count = calcCount(template1, rs.getInt("count"),
							amount_rate);
					if (count == 0) {
						continue;
					}
					spawnDat = new L1Spawn(template1);
					spawnDat.setId(rs.getInt("id"));
					spawnDat.setAmount(count);
					spawnDat.setGroupId(rs.getInt("group_id"));
					spawnDat.setLocX(rs.getInt("loc_x"));
					spawnDat.setLocY(rs.getInt("loc_y"));
					spawnDat.setRandomx(rs.getInt("random_x"));
					spawnDat.setRandomy(rs.getInt("random_y"));
					spawnDat.setLocX1(rs.getInt("loc_x1"));
					spawnDat.setLocY1(rs.getInt("loc_y1"));
					spawnDat.setLocX2(rs.getInt("loc_x2"));
					spawnDat.setLocY2(rs.getInt("loc_y2"));
					int heading = rs.getInt("heading");
					if (heading < 0 || heading > 7) {
						heading = _random.nextInt(8);
					}
					spawnDat.setHeading(heading);
					spawnDat.setMinRespawnDelay(rs.getInt("min_respawn_delay"));
					spawnDat.setMaxRespawnDelay(rs.getInt("max_respawn_delay"));
					spawnDat.setMapId(mapId);
					spawnDat.setRespawnScreen(rs.getBoolean("respawn_screen"));
					spawnDat.setMovementDistance(rs.getInt("movement_distance"));
					spawnDat.setRest(rs.getBoolean("rest"));
					spawnDat.setSpawnType(rs.getInt("near_spawn"));
					spawnDat.setTime(SpawnTimeTable.getInstance().get(spawnDat.getId()));
					spawnDat.setName(template1.getName());
					if (count > 1 && spawnDat.getLocX1() == 0) {
						// 複数かつ固定spawnの場合は、個体数 * 6 の範囲spawnに変える。
						// ただし範囲が30を超えないようにする
						int range = Math.min(count * 6, 30);
						spawnDat.setLocX1(spawnDat.getLocX() - range);
						spawnDat.setLocY1(spawnDat.getLocY() - range);
						spawnDat.setLocX2(spawnDat.getLocX() + range);
						spawnDat.setLocY2(spawnDat.getLocY() + range);
					}
					// start the spawning
					spawnDat.init();
					spawnCount += spawnDat.getAmount();
				}
				_spawntable.put(new Integer(spawnDat.getId()), spawnDat);
				if (spawnDat.getId() > _highestId) {
					_highestId = spawnDat.getId();
				}
			}
			_log.fine("loaded mob: " + _spawntable.size() + " records");
			System.out.println("loading mobs...OK! " + timer.elapsedTimeMillis() + " ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		_log.fine("loaded all mob: " + spawnCount + " records");
	}

	public L1Spawn getTemplate(int Id) {
		return _spawntable.get(new Integer(Id));
	}

	public void addNewSpawn(L1Spawn spawn) {
		_highestId++;
		spawn.setId(_highestId);
		_spawntable.put(new Integer(spawn.getId()), spawn);
	}

	public static void storeSpawn(L1PcInstance pc, L1Npc npc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int count = 1;
			int randomXY = 12;
			int minRespawnDelay = 60;
			int maxRespawnDelay = 120;
			String note = npc.getName();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(String.format("INSERT INTO spawn_mobs SET %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s", 
				"npc_id=?", "note=?", "count=?", "loc_x=?", "loc_y=?", 
				"random_x=?", "random_y=?", "heading=?",
				"min_respawn_delay=?", "max_respawn_delay=?", "map_id=?"));
			pstm.setInt(1, npc.getNpcId());
			pstm.setString(2, note);
			pstm.setInt(3, count);
			pstm.setInt(4, pc.getX());
			pstm.setInt(5, pc.getY());
			pstm.setInt(6, randomXY);
			pstm.setInt(7, randomXY);
			pstm.setInt(8, pc.getHeading());
			pstm.setInt(9, minRespawnDelay);
			pstm.setInt(10, maxRespawnDelay);
			pstm.setInt(11, pc.getMapId());
			pstm.execute();
		} catch (Exception e) {
			NpcTable._log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	private static int calcCount(L1Npc npc, int count, double rate) {
		if (rate == 0) {
			return 0;
		}
		if (rate == 1 || npc.isAmountFixed()) {
			return count;
		} else {
			return NumberUtil.randomRound((count * rate));
		}
	}
}
