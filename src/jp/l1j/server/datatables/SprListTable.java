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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class SprListTable {
	private static Logger _log = Logger.getLogger(SprListTable.class.getName());

	private static SprListTable _instance;

	private final ArrayList<Integer> _sprList = new ArrayList<Integer>();

	public static SprListTable getInstance() {
		if (_instance == null) {
			_instance = new SprListTable();
		}
		return _instance;
	}

	private SprListTable() {
		loadPolymorphs();
	}

	private void loadPolymorphs() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT distinct spr_id FROM spr_actions order by spr_id");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int sprid = rs.getInt("spr_id");
				_sprList.add(sprid);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "error while creating spr_list table", e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public ArrayList<Integer> getTemplate() {
		return _sprList;
	}
}
