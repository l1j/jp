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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.L1QueryUtil;
import jp.l1j.server.utils.L1SqlException;
import jp.l1j.server.utils.SqlUtil;

public class MapTimerTable {
	private static Logger _log = Logger.getLogger(MapTimerTable.class.getName());

	private int _charId;
	
	private int _mapId;
	
	private int _areaId;
	
	private int _enterTime;

	public MapTimerTable() {
	}

	public MapTimerTable(int charId, int mapId, int areaId, int enterTime) {
		_charId = charId;
		_mapId = mapId;
		_areaId = areaId;
		_enterTime = enterTime;
	}

	public int getCharObjId() {
		return _charId;
	}
	
	public void setCharId(int charObjId) {
		_charId = charObjId;
	}
	
	public int getMapId() {
		return _mapId;
	}
	
	public void setMapId(int mapId) {
		_mapId = mapId;
	}

	public int getAreaId() {
		return _areaId;
	}
	
	public void setAreaId(int areaId) {
		_areaId = areaId;
	}
		
	public int getEnterTime() {
		return _enterTime;
	}
	
	public void setEnterTime(int enterTime) {
		_enterTime = enterTime;
	}
	
	private static class Factory implements L1QueryUtil.EntityFactory<MapTimerTable> {
		@Override
		public MapTimerTable fromResultSet(ResultSet rs) throws SQLException {
			MapTimerTable result = new MapTimerTable();
			result._charId = rs.getInt("char_id");
			result._mapId = rs.getInt("map_id");
			result._areaId = rs.getInt("area_id");
			result._enterTime = rs.getInt("enter_time");
			return result;
		}
	}

	public static MapTimerTable find(int charId, int areaId) {
		String sql = "SELECT * FROM map_timers WHERE char_id = ? AND area_id = ?";
		return L1QueryUtil.selectFirst(new Factory(), sql, charId, areaId);
	}

	private void store(Connection con) {
		String sql = "INSERT INTO map_timers SET char_id=?, map_id=?, area_id=?, enter_time=?";
		L1QueryUtil.execute(con, sql, _charId, _mapId, _areaId, _enterTime);
	}

	public void save() {
		Connection con = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			delete(con, _charId, _areaId);
			store(con);
		} catch (SQLException e) {
			throw new L1SqlException(e);
		} finally {
			SqlUtil.close(con);
		}
	}

	private static void delete(Connection con, int charId, int areaId) {
		String sql = "DELETE FROM map_timers WHERE char_id = ? AND area_id = ?";
		L1QueryUtil.execute(con, sql, charId, areaId);
	}

	private static void delete(Connection con, int areaId) {
		String sql = "DELETE FROM map_timers WHERE area_id = ?";
		L1QueryUtil.execute(con, sql, areaId);
	}

	public static void remove(int areaId) {
		Connection con = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			delete(con, areaId);
		} catch (SQLException e) {
			throw new L1SqlException(e);
		} finally {
			SqlUtil.close(con);
		}
	}

	public static void reset(int areaId) {
		for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.getMapLimiter().getAreaId() == areaId) {
				pc.stopMapLimiter();
			}
		}
		remove(areaId);
		for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.getMapLimiter() == null) {
				pc.startMapLimiter();
			}
		}
	}
}
