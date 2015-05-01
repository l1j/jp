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
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class InnKeyTable {
	private static Logger _log = Logger.getLogger(InnKeyTable.class.getName());

	private InnKeyTable() {
	}

	public static void storeKey(L1ItemInstance item) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO inn_keys SET item_obj_id=?, id=?, npc_id=?, hall=?, due_time=?");
			pstm.setInt(1, item.getId());
			pstm.setInt(2, item.getKeyId());
			pstm.setInt(3, item.getInnNpcId());
			pstm.setBoolean(4, item.checkRoomOrHall());
			pstm.setTimestamp(5, item.getDueTime());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public static void deleteKey(L1ItemInstance item) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM inn_keys WHERE item_obj_id=?");
			pstm.setInt(1, item.getId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public static boolean hasKey(L1ItemInstance item) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM inn_keys WHERE item_obj_id=?");
			pstm.setInt(1, item.getId());
			rs = pstm.executeQuery();
			while (rs.next()) {
				int itemObj = rs.getInt("item_obj_id");
				if (item.getId() == itemObj) {
					item.setKeyId(rs.getInt("id"));
					item.setInnNpcId(rs.getInt("npc_id"));
					item.setHall(rs.getBoolean("hall"));
					item.setDueTime(rs.getTimestamp("due_time"));
					return true;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return false;
	}	
}