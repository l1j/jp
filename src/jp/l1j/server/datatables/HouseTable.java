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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.templates.L1House;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

// Referenced classes of package jp.l1j.server:
// IdFactory

public class HouseTable {
	private static Logger _log = Logger.getLogger(HouseTable.class.getName());

	private static HouseTable _instance;

	private final Map<Integer, L1House> _house = new ConcurrentHashMap<Integer, L1House>();

	public static HouseTable getInstance() {
		if (_instance == null) {
			_instance = new HouseTable();
		}
		return _instance;
	}

	private Calendar timestampToCalendar(Timestamp ts) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		return cal;
	}

	public HouseTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM houses ORDER BY id");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1House house = new L1House();
				house.setHouseId(rs.getInt("id"));
				house.setHouseName(rs.getString("name"));
				house.setHouseArea(rs.getInt("area"));
				house.setLocation(rs.getString("location"));
				house.setKeeperId(rs.getInt("npc_id"));
				house.setOnSale(rs.getInt("is_on_sale") == 1 ? true : false);
				house.setPurchaseBasement(rs.getInt("is_purchase_basement") == 1 ? true : false);
				house.setTaxDeadline(timestampToCalendar((Timestamp) rs.getObject("tax_deadline")));
				_house.put(house.getHouseId(), house);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public L1House[] getHouseTableList() {
		return _house.values().toArray(new L1House[_house.size()]);
	}

	public L1House getHouseTable(int houseId) {
		return _house.get(houseId);
	}

	public L1House findByKeeperId(int keeperId) {
		for (L1House house : _house.values()) {
			if (house.getKeeperId() == keeperId) {
				return house;
			}
		}
		return null;
	}

	public void updateHouse(L1House house) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(String.format("UPDATE houses SET %s, %s, %s, %s, %s, %s, %s WHERE id=?",
				"name=?", "area=?", "location=?", "npc_id=?",
				"is_on_sale=?", "is_purchase_basement=?", "tax_deadline=?"));
			pstm.setString(1, house.getHouseName());
			pstm.setInt(2, house.getHouseArea());
			pstm.setString(3, house.getLocation());
			pstm.setInt(4, house.getKeeperId());
			pstm.setInt(5, house.isOnSale() == true ? 1 : 0);
			pstm.setInt(6, house.isPurchaseBasement() == true ? 1 : 0);
			String fm = DateFormat.getDateTimeInstance().format(house.getTaxDeadline().getTime());
			pstm.setString(7, fm);
			pstm.setInt(8, house.getHouseId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public static List<Integer> getHouseIdList() {
		List<Integer> houseIdList = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT id FROM houses ORDER BY id");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int houseId = rs.getInt("id");
				houseIdList.add(Integer.valueOf(houseId));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return houseIdList;
	}
}
