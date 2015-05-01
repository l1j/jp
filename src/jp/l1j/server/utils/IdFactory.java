/**
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

package jp.l1j.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IdFactory {
	private static Logger _log = Logger.getLogger(IdFactory.class.getName());

	private int _curId;

	private Object _monitor = new Object();

	private static final int FIRST_ID = 0x10000000;

	private static IdFactory _instance = new IdFactory();

	private IdFactory() {
		loadState();
	}

	public static IdFactory getInstance() {
		return _instance;
	}

	public int nextId() {
		synchronized (_monitor) {
			return _curId++;
		}
	}

	private void loadState() {
		// DBからMAXIDを求める
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT MAX(id) + 1 AS nextid FROM (SELECT id FROM inventory_items UNION ALL SELECT id FROM character_bookmarks UNION ALL SELECT id FROM characters UNION ALL SELECT id FROM clans UNION ALL SELECT id AS id FROM pets) t");
			rs = pstm.executeQuery();

			int id = 0;
			if (rs.next()) {
				id = rs.getInt("nextid");
			}
			if (id < FIRST_ID) {
				id = FIRST_ID;
			}
			_curId = id;
			_log.info("現在のオブジェクトID: " + _curId);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
}
