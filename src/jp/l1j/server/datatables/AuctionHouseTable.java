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
import jp.l1j.server.templates.L1AuctionHouse;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class AuctionHouseTable {
	private static Logger _log = Logger.getLogger(AuctionHouseTable.class.getName());

	private static AuctionHouseTable _instance;

	private final Map<Integer, L1AuctionHouse> _boards =
			new ConcurrentHashMap<Integer, L1AuctionHouse>();

	private static AuctionHouseTable getInstance() {
		if (_instance == null) {
			_instance = new AuctionHouseTable();
		}
		return _instance;
	}

	private Calendar timestampToCalendar(Timestamp ts) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		return cal;
	}

	public AuctionHouseTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM auction_houses ORDER BY house_id");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1AuctionHouse board = new L1AuctionHouse();
				board.setHouseId(rs.getInt("house_id"));
				board.setDeadline(timestampToCalendar((Timestamp) rs.getObject("deadline")));
				board.setPrice(rs.getInt("price"));
				board.setOwnerId(rs.getInt("owner_id"));
				board.setOwnerName(CharacterTable.getInstance().getCharName(board.getOwnerId()));
				board.setBidderId(rs.getInt("bidder_id"));
				board.setBidderName(CharacterTable.getInstance().getCharName(board.getBidderId()));
				_boards.put(board.getHouseId(), board);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public L1AuctionHouse[] getAuctionBoardTableList() {
		return _boards.values().toArray(new L1AuctionHouse[_boards.size()]);
	}

	public L1AuctionHouse getAuctionBoardTable(int houseId) {
		return _boards.get(houseId);
	}

	public void insertAuctionBoard(L1AuctionHouse board) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(String.format("INSERT INTO auction_houses SET %s, %s, %s, %s, %s",
				"house_id=?", "deadline=?", "price=?", "owner_id=?", "bidder_id=?"));
			pstm.setInt(1, board.getHouseId());
			String fm = DateFormat.getDateTimeInstance().format(board.getDeadline().getTime());
			pstm.setString(2, fm);
			pstm.setInt(3, board.getPrice());
			pstm.setInt(4, board.getOwnerId());
			pstm.setInt(5, board.getBidderId());
			pstm.execute();
			_boards.put(board.getHouseId(), board);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void updateAuctionBoard(L1AuctionHouse board) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(String.format("UPDATE auction_houses SET %s, %s, %s, %s WHERE house_id=?",
				"deadline=?", "price=?", "owner_id=?", "bidder_id=?"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String fm = sdf.format(board.getDeadline().getTime());
			pstm.setString(1, fm);
			pstm.setInt(2, board.getPrice());
			pstm.setInt(3, board.getOwnerId());
			pstm.setInt(4, board.getBidderId());
			pstm.setInt(5, board.getHouseId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void deleteAuctionBoard(int houseId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM auction_houses WHERE house_id=?");
			pstm.setInt(1, houseId);
			pstm.execute();
			_boards.remove(houseId);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
}
