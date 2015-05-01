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

package jp.l1j.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import jp.l1j.server.utils.collections.Lists;

/**
 * SQLのQuery発行をより簡潔にします。 いくつかのメソッドはテストが十分ではありません。
 * 現在、L1Jではアイテム保管テーブルの大幅変更が予定されていますが、その際に多くのメソッドは修正される予定です。
 * 
 * @author Vala
 * @since 2011.06.05
 */
public class L1QueryUtil {
	public interface EntityFactory<T> {
		public T fromResultSet(ResultSet rs) throws SQLException;
	}

	private static void setupPrepareStatement(PreparedStatement pstm,
			Object[] args) throws SQLException {
		for (int i = 0; i < args.length; i++) {
			pstm.setObject(i + 1, args[i]);
		}
	}

	public static <T> T selectFirst(EntityFactory<T> factory, String sql,
			Object... args) {
		List<T> result = selectAll(factory, sql, args);
		return result.isEmpty() ? null : result.get(0);
	}

	public static <T> List<T> selectAll(EntityFactory<T> factory, String sql,
			Object... args) {
		List<T> result = Lists.newArrayList();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(sql);
			setupPrepareStatement(pstm, args);
			rs = pstm.executeQuery();
			while (rs.next()) {
				T entity = factory.fromResultSet(rs);
				if (entity == null) {
					throw new NullPointerException(factory.getClass()
							.getSimpleName()
							+ " returned null.");
				}
				result.add(entity);
			}
		} catch (SQLException e) {
			throw new L1SqlException(e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return result;
	}

	public static boolean execute(Connection con, String sql, Object... args) {
		PreparedStatement pstm = null;
		try {
			pstm = con.prepareStatement(sql);
			setupPrepareStatement(pstm, args);
			return pstm.execute();
		} catch (SQLException e) {
			throw new L1SqlException(e);
		} finally {
			SqlUtil.close(pstm);
		}
	}

	public static boolean execute(String sql, Object... args) {
		Connection con = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			return execute(con, sql, args);
		} catch (SQLException e) {
			throw new L1SqlException(e);
		} finally {
			SqlUtil.close(con);
		}
	}
}
