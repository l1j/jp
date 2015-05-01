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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.L1BossSpawn;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class BossSpawnTable {
	private static Logger _log = Logger.getLogger(BossSpawnTable.class.getName());
	
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	private BossSpawnTable() {
	}

	public static void fillSpawnTable() {
		int spawnCount = 0;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawn_boss_mobs");
			rs = pstm.executeQuery();
			L1BossSpawn spawnDat;
			L1Npc template1;
			while (rs.next()) {
				int npcTemplateId = rs.getInt("npc_id");
				template1 = NpcTable.getInstance().getTemplate(npcTemplateId);
				if (template1 == null) {
					_log.warning("mob data for id:" + npcTemplateId + " missing in npc table");
					spawnDat = null;
				} else {
					spawnDat = new L1BossSpawn(template1);
					spawnDat.setId(rs.getInt("id"));
					spawnDat.setNpcid(npcTemplateId);
					L1Npc npc = NpcTable.getInstance().getTemplate(npcTemplateId);
					spawnDat.setLocation(npc != null ? npc.getName() : null);
					spawnDat.setCycleType(rs.getString("cycle_type"));
					spawnDat.setAmount(rs.getInt("count"));
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
					spawnDat.setMapId(rs.getShort("map_id"));
					spawnDat.setRespawnScreen(rs.getBoolean("respawn_screen"));
					spawnDat.setMovementDistance(rs.getInt("movement_distance"));
					spawnDat.setRest(rs.getBoolean("rest"));
					spawnDat.setSpawnType(rs.getInt("spawn_type"));
					spawnDat.setPercentage(rs.getInt("percentage"));
					spawnDat.setName(template1.getName());

					// start the spawning
					spawnDat.init();
					spawnCount += spawnDat.getAmount();
				}
			}

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		_log.fine("loaded boss: " + spawnCount + " records");
	}
}
