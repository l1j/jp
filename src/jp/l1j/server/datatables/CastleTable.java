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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.templates.L1Castle;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public final class CastleTable {
	private static Logger _log = Logger.getLogger(CastleTable.class.getName());

	private static CastleTable _instance;

	private final Map<Integer, L1Castle> _castles = new ConcurrentHashMap<Integer, L1Castle>();

	public static CastleTable getInstance() {
		if (_instance == null) {
			_instance = new CastleTable();
		}
		return _instance;
	}

	private CastleTable() {
		load();
	}

	private Calendar timestampToCalendar(Timestamp ts) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		return cal;
	}

	private void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM castles");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1Castle castle = new L1Castle(rs.getInt(1), rs.getString(2));
				castle.setWarTime(timestampToCalendar((Timestamp) rs.getObject(3)));
				castle.setTaxRate(rs.getInt(4));
				castle.setPublicMoney(rs.getInt(5));
				_castles.put(castle.getId(), castle);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public L1Castle[] getCastleTableList() {
		return _castles.values().toArray(new L1Castle[_castles.size()]);
	}

	public L1Castle getCastleTable(final int id) {
		return _castles.get(id);
	}

	public void updateCastle(final L1Castle castle) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE castles SET name=?, war_time=?, tax_rate=?, public_money=? WHERE id=?");
			pstm.setString(1, castle.getName());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String fm = sdf.format(castle.getWarTime().getTime());
			pstm.setString(2, fm);
			pstm.setInt(3, castle.getTaxRate());
			pstm.setInt(4, castle.getPublicMoney());
			pstm.setInt(5, castle.getId());
			pstm.execute();
			_castles.put(castle.getId(), castle);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
}
