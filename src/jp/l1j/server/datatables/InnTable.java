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
import jp.l1j.server.templates.L1Inn;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class InnTable {
	private static Logger _log = Logger.getLogger(InnTable.class.getName());

	private static class Inn {
		private final Map<Integer, L1Inn> _inn = new HashMap<Integer, L1Inn>();
	}

	private static final Map<Integer, Inn> _dataMap =new HashMap<Integer, Inn>();

	private static InnTable _instance;

	public static InnTable getInstance() {
		if (_instance == null) {
			_instance = new InnTable();
		}
		return _instance;
	}

	private InnTable() {
		load();
	}

	private void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Inn inn = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM inns");
			rs = pstm.executeQuery();
			L1Inn l1inn;
			int roomNumber;
			while (rs.next()) {
				int key = rs.getInt("npc_id");
				if (!_dataMap.containsKey(key)) {
					inn = new Inn();
					_dataMap.put(key, inn);
				} else {
					inn = _dataMap.get(key);
				}
				l1inn = new L1Inn();
				l1inn.setInnNpcId(rs.getInt("npc_id"));
				roomNumber = rs.getInt("room_number");
				l1inn.setRoomNumber(roomNumber);
				l1inn.setKeyId(rs.getInt("key_id"));
				l1inn.setLodgerId(rs.getInt("lodger_id"));
				l1inn.setHall(rs.getBoolean("hall"));
				l1inn.setDueTime(rs.getTimestamp("due_time"));
				inn._inn.put(Integer.valueOf(roomNumber), l1inn);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void updateInn(L1Inn inn) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE inns SET key_id=?,lodger_id=?,hall=?,due_time=? WHERE npc_id=? and room_number=?");
			pstm.setInt(1, inn.getKeyId());
			pstm.setInt(2, inn.getLodgerId());
			pstm.setBoolean(3, inn.isHall());
			pstm.setTimestamp(4, inn.getDueTime());
			pstm.setInt(5, inn.getInnNpcId());
			pstm.setInt(6, inn.getRoomNumber());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public L1Inn getTemplate(int npcid, int roomNumber) {
		if (_dataMap.containsKey(npcid)) {
			return _dataMap.get(npcid)._inn.get(roomNumber);
		}
		return null;
	}
}