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

package jp.l1j.server.templates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class L1BoardPost {
	private static Logger _log = Logger.getLogger(L1BoardPost.class.getName());

	private final int _id;
	private final String _name;
	private final String _date;
	private final String _title;
	private final String _content;

	public int getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public String getDate() {
		return _date;
	}

	public String getTitle() {
		return _title;
	}

	public String getContent() {
		return _content;
	}

	private String today(String timeZoneID) {
		// XXX SimpleDateFormatを使ったほうが美しいが、ロケールで動作が変わる恐れがある
		// return new SimpleDateFormat("yy/MM/dd").format(new Date());
		TimeZone tz = TimeZone.getTimeZone(timeZoneID);
		Calendar cal = Calendar.getInstance(tz);
		int year = cal.get(Calendar.YEAR) - 2000;
		int month = cal.get(Calendar.MONTH) + 1;
		int date = cal.get(Calendar.DATE);
		return String.format("%02d/%02d/%02d", year, month, date);
	}

	private L1BoardPost(int id, String name, String title, String content) {
		_id = id;
		_name = name;
		_date = today(Config.TIME_ZONE);
		_title = title;
		_content = content;
	}

	private L1BoardPost(ResultSet rs) throws SQLException {
		_id = rs.getInt("id");
		_name = rs.getString("name");
		_date = rs.getString("date");
		_title = rs.getString("title");
		_content = rs.getString("content");
	}

	public synchronized static L1BoardPost create(String name, String title, String content) {
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		PreparedStatement pstm2 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT max(id) + 1 as newid FROM board_posts");
			rs = pstm1.executeQuery();
			rs.next();
			int id = rs.getInt("newid");
			L1BoardPost topic = new L1BoardPost(id, name, title, content);
			pstm2 = con.prepareStatement("INSERT INTO board_posts SET id=?, name=?, date=?, title=?, content=?");
			pstm2.setInt(1, topic.getId());
			pstm2.setString(2, topic.getName());
			pstm2.setString(3, topic.getDate());
			pstm2.setString(4, topic.getTitle());
			pstm2.setString(5, topic.getContent());
			pstm2.execute();
			return topic;
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm1);
			SqlUtil.close(pstm2);
			SqlUtil.close(con);
		}
		return null;
	}

	public void delete() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM board_posts WHERE id=?");
			pstm.setInt(1, getId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public static L1BoardPost findById(int id) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM board_posts WHERE id=?");
			pstm.setInt(1, id);
			rs = pstm.executeQuery();
			if (rs.next()) {
				return new L1BoardPost(rs);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
		return null;
	}

	private static PreparedStatement makeIndexStatement(Connection con, int id,
			int limit) throws SQLException {
		PreparedStatement result = null;
		int offset = 1;
		if (id == 0) {
			result = con.prepareStatement("SELECT * FROM board_posts ORDER BY id DESC LIMIT ?");
		} else {
			result = con.prepareStatement("SELECT * FROM board_posts WHERE id < ? ORDER BY id DESC LIMIT ?");
			result.setInt(1, id);
			offset++;
		}
		result.setInt(offset, limit);
		return result;
	}

	public static List<L1BoardPost> index(int limit) {
		return index(0, limit);
	}

	public static List<L1BoardPost> index(int id, int limit) {
		List<L1BoardPost> result = new ArrayList<L1BoardPost>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = makeIndexStatement(con, id, limit);
			rs = pstm.executeQuery();
			while (rs.next()) {
				result.add(new L1BoardPost(rs));
			}
			return result;
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
		return null;
	}
}
