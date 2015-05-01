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

package jp.l1j.server.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.templates.L1Command;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class L1Commands {
	private static Logger _log = Logger.getLogger(L1Commands.class.getName());

	private static L1Command fromResultSet(ResultSet rs) throws SQLException {
		return new L1Command(rs.getString("name"), rs.getInt("access_level"),
				rs.getString("class_name"));
	}

	public static L1Command get(String name) {
		/*
		 * デバッグやテスト容易性の為に毎回DBに読みに行きます。 キャッシュするより理論上パフォーマンスは下がりますが、無視できる範囲です。
		 */
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM commands WHERE name=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			if (!rs.next()) {
				return null;
			}
			return fromResultSet(rs);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, String.format(I18N_LOAD_FROM_THE_TABLE_FAILED,
					"commands"), e);
			// %s テーブルからのコマンドの読み込みに失敗しました。
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return null;
	}

	public static List<L1Command> availableCommandList(int accessLevel) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List<L1Command> result = new ArrayList<L1Command>();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM commands WHERE access_level <= ?");
			pstm.setInt(1, accessLevel);
			rs = pstm.executeQuery();
			while (rs.next()) {
				result.add(fromResultSet(rs));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, String.format(I18N_LOAD_FROM_THE_TABLE_FAILED,
					"commands"), e);
			// %s テーブルからのコマンドの読み込みに失敗しました。
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return result;
	}
}
