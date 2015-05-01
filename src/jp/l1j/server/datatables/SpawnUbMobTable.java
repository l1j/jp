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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.L1UbPattern;
import jp.l1j.server.model.L1UbSpawn;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class SpawnUbMobTable {
	private static Logger _log = Logger.getLogger(SpawnUbMobTable.class.getName());

	private static SpawnUbMobTable _instance;

	private HashMap<Integer, L1UbSpawn> _spawnTable = new HashMap<Integer, L1UbSpawn>();;

	public static SpawnUbMobTable getInstance() {
		if (_instance == null) {
			_instance = new SpawnUbMobTable();
		}
		return _instance;
	}

	private SpawnUbMobTable() {
		loadSpawnTable();
	}

	private void loadSpawnTable() {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawn_ub_mobs");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1Npc npcTemp = NpcTable.getInstance().getTemplate(rs.getInt("npc_id"));
				if (npcTemp == null) {
					continue;
				}
				L1UbSpawn spawnDat = new L1UbSpawn();
				spawnDat.setId(rs.getInt("id"));
				spawnDat.setUbId(rs.getInt("ub_id"));
				spawnDat.setPattern(rs.getInt("pattern"));
				spawnDat.setGroup(rs.getInt("group_id"));
				spawnDat.setName(npcTemp.getName());
				spawnDat.setNpcTemplateId(rs.getInt("npc_id"));
				spawnDat.setAmount(rs.getInt("count"));
				spawnDat.setSpawnDelay(rs.getInt("spawn_delay"));
				spawnDat.setSealCount(rs.getInt("seal_count"));
				_spawnTable.put(spawnDat.getId(), spawnDat);
			}
		} catch (SQLException e) {
			// problem with initializing spawn, go to next one
			_log.warning("spawn couldnt be initialized:" + e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		_log.fine("loaded ub mob: " + _spawnTable.size() + " records");
	}

	public L1UbSpawn getSpawn(int spawnId) {
		return _spawnTable.get(spawnId);
	}

	/**
	 * 指定されたUBIDに対するパターンの最大数を返す。
	 * 
	 * @param ubId
	 *            調べるUBID。
	 * @return パターンの最大数。
	 */
	public int getMaxPattern(int ubId) {
		int n = 0;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT MAX(pattern) FROM spawn_ub_mobs WHERE ub_id=?");
			pstm.setInt(1, ubId);
			rs = pstm.executeQuery();
			if (rs.next()) {
				n = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return n;
	}

	public L1UbPattern getPattern(int ubId, int patternNumer) {
		L1UbPattern pattern = new L1UbPattern();
		for (L1UbSpawn spawn : _spawnTable.values()) {
			if (spawn.getUbId() == ubId && spawn.getPattern() == patternNumer) {
				pattern.addSpawn(spawn.getGroup(), spawn);
			}
		}
		pattern.freeze();
		return pattern;
	}
}
