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
import jp.l1j.server.model.L1Spawn;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class SpawnNpcTable {
	private static Logger _log = Logger.getLogger(SpawnNpcTable.class.getName());

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	
	private static SpawnNpcTable _instance;

	private Map<Integer, L1Spawn> _spawntable = new HashMap<Integer, L1Spawn>();

	private int _highestId;

	public static SpawnNpcTable getInstance() {
		if (_instance == null) {
			_instance = new SpawnNpcTable();
		}
		return _instance;
	}

	private SpawnNpcTable() {
		fillNpcSpawnTable();
	}

	private void fillNpcSpawnTable() {
		int spawnCount = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawn_npcs");
			rs = pstm.executeQuery();
			while (rs.next()) {
				if (Config.ALT_GMSHOP == false) {
					int npcid = rs.getInt(1);
					if (npcid >= Config.ALT_GMSHOP_MIN_ID && npcid <= Config.ALT_GMSHOP_MAX_ID) {
						continue;
					}
				}
				if (Config.HINOMOTO_UPDATE_1 == false) {
					int npcid = rs.getInt("id");
					if (npcid == 50490) {
						continue;
					}
				}
				if (Config.RISING_SUN_EVENT == false) {
					int npcid = rs.getInt("id");
					if (npcid == 50483) {
						continue;
					}
				}
				if (Config.HALLOWEEN_EVENT == false) {
					int npcid = rs.getInt("id");
					if (npcid >= 130852 && npcid <= 130862 || npcid >= 26656
							&& npcid <= 26734) {
						continue;
					}
				}
				if (Config.JP_PRIVILEGED == false) {
					int npcid = rs.getInt("id");
					if (npcid >= 1310368 && npcid <= 1310379) {
						continue;
					}
				}
				if (Config.TALKING_SCROLL_QUEST == false) {
					int npcid = rs.getInt("id");
					if (npcid >= 87537 && npcid <= 87551 || npcid >= 1310387
							&& npcid <= 1310389) {
						continue;
					}
				}
				if (Config.TALKING_SCROLL_QUEST == true) {
					int npcid = rs.getInt("id");
					if (npcid >= 90066 && npcid <= 90069) {
						continue;
					}
				}
				int npcId = rs.getInt("npc_id");
				L1Npc l1npc = NpcTable.getInstance().getTemplate(npcId);
				L1Spawn l1spawn;
				if (l1npc == null) {
					_log.warning("mob data for id:" + npcId
							+ " missing in npc table");
					l1spawn = null;
				} else {
					if (rs.getInt("count") == 0) {
						continue;
					}
					l1spawn = new L1Spawn(l1npc);
					l1spawn.setId(rs.getInt("id"));
					l1spawn.setAmount(rs.getInt("count"));
					l1spawn.setLocX(rs.getInt("loc_x"));
					l1spawn.setLocY(rs.getInt("loc_y"));
					l1spawn.setRandomx(rs.getInt("random_x"));
					l1spawn.setRandomy(rs.getInt("random_y"));
					l1spawn.setLocX1(0);
					l1spawn.setLocY1(0);
					l1spawn.setLocX2(0);
					l1spawn.setLocY2(0);
					int heading = rs.getInt("heading");
					if (heading < 0 || heading > 7) {
						heading = _random.nextInt(8);
					}
					l1spawn.setHeading(heading);
					l1spawn.setMinRespawnDelay(rs.getInt("respawn_delay"));
					l1spawn.setMapId(rs.getShort("map_id"));
					l1spawn.setMovementDistance(rs.getInt("movement_distance"));
					l1spawn.setName(l1npc.getName());
					l1spawn.init();
					spawnCount += l1spawn.getAmount();
					_spawntable.put(new Integer(l1spawn.getId()), l1spawn);
					if (l1spawn.getId() > _highestId) {
						_highestId = l1spawn.getId();
					}
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		_log.fine("loaded npc: " + _spawntable.size() + " records");
	}

	public void storeSpawn(L1PcInstance pc, L1Npc npc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int count = 1;
			String note = npc.getName();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO spawn_npcs SET npc_id=?,note=?,count=?,loc_x=?,loc_y=?,heading=?,map_id=?");
			pstm.setInt(1, npc.getNpcId());
			pstm.setString(2, note);
			pstm.setInt(3, count);
			pstm.setInt(4, pc.getX());
			pstm.setInt(5, pc.getY());
			pstm.setInt(6, pc.getHeading());
			pstm.setInt(7, pc.getMapId());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public L1Spawn getTemplate(int i) {
		return _spawntable.get(i);
	}

	public void addNewSpawn(L1Spawn l1spawn) {
		_highestId++;
		l1spawn.setId(_highestId);
		_spawntable.put(l1spawn.getId(), l1spawn);
	}
}
