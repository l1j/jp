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
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_MAP_LIST;
import jp.l1j.server.templates.L1GetBackRestart;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class RestartLocationTable {
	private static Logger _log = Logger.getLogger(RestartLocationTable.class.getName());

	private static RestartLocationTable _instance;

	private static HashMap<Integer, L1GetBackRestart> _restartLocations = new HashMap<Integer, L1GetBackRestart>();

	public static RestartLocationTable getInstance() {
		if (_instance == null) {
			_instance = new RestartLocationTable();
		}
		return _instance;
	}

	private RestartLocationTable() {
		loadRestartLocations(_restartLocations);
	}
	
	public void loadRestartLocations(HashMap<Integer, L1GetBackRestart> restartLocations) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM restart_locations");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int area = rs.getInt("area");
				short mapId = rs.getShort("map_id");
				boolean isErr = false;
				if (MapTable.getInstance().locationname(area) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, area));
					// %s はマップリストに存在しません。
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
				L1GetBackRestart gbr = new L1GetBackRestart();
				gbr.setArea(area);
				gbr.setLocX(rs.getInt("loc_x"));
				gbr.setLocY(rs.getInt("loc_y"));
				gbr.setMapId(mapId);
				restartLocations.put(new Integer(area), gbr);
			}
			System.out.println("loading restart locations...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	public void reload() {
		HashMap<Integer, L1GetBackRestart> restartLocations = new HashMap<Integer, L1GetBackRestart>();
		loadRestartLocations(restartLocations);
		_restartLocations = restartLocations;
	}
	
	public L1GetBackRestart[] getGetBackRestartTableList() {
		return _restartLocations.values().toArray(new L1GetBackRestart[_restartLocations.size()]);
	}
}
