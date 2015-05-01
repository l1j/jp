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
import jp.l1j.server.templates.L1SpawnTime;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class SpawnTimeTable {
	private static Logger _log = Logger.getLogger(SpawnTimeTable.class.getName());

	private static SpawnTimeTable _instance;

	private final Map<Integer, L1SpawnTime> _times = new HashMap<Integer, L1SpawnTime>();

	public static SpawnTimeTable getInstance() {
		if (_instance == null) {
			_instance = new SpawnTimeTable();
		}
		return _instance;
	}

	private SpawnTimeTable() {
		load();
	}

	public L1SpawnTime get(int id) {
		return _times.get(id);
	}

	private void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawn_times");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("npc_id");
				L1SpawnTime.L1SpawnTimeBuilder builder = new L1SpawnTime.L1SpawnTimeBuilder(id);
				builder.setTimeStart(rs.getTime("time_start"));
				builder.setTimeEnd(rs.getTime("time_end"));
				builder.setDeleteAtEndTime(rs.getBoolean("delete_at_endtime"));
				_times.put(id, builder.build());
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
}
