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
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class CharacterConfigTable {
	private static Logger _log = Logger.getLogger(CharacterConfigTable.class.getName());

	private static CharacterConfigTable _instance;

	public CharacterConfigTable() {
	}

	public static CharacterConfigTable getInstance() {
		if (_instance == null) {
			_instance = new CharacterConfigTable();
		}
		return _instance;
	}

	public void storeCharacterConfig(int objectId, int length, byte[] data) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO character_configs SET char_id=?, length=?, data=?");
			pstm.setInt(1, objectId);
			pstm.setInt(2, length);
			pstm.setBytes(3, data);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void updateCharacterConfig(int objectId, int length, byte[] data) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE character_configs SET length=?, data=? WHERE char_id=?");
			pstm.setInt(1, length);
			pstm.setBytes(2, data);
			pstm.setInt(3, objectId);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void deleteCharacterConfig(int objectId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_configs WHERE char_id=?");
			pstm.setInt(1, objectId);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public int countCharacterConfig(int objectId) {
		int result = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT count(*) as cnt FROM character_configs WHERE char_id=?");
			pstm.setInt(1, objectId);
			rs = pstm.executeQuery();
			if (rs.next()) {
				result = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return result;
	}
}
