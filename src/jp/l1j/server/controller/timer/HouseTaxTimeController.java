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

package jp.l1j.server.controller.timer;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.datatables.AuctionHouseTable;
import jp.l1j.server.datatables.ClanTable;
import jp.l1j.server.datatables.HouseTable;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.templates.L1AuctionHouse;
import jp.l1j.server.templates.L1House;

public class HouseTaxTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(HouseTaxTimeController.class.getName());

	private static HouseTaxTimeController _instance;

	public static HouseTaxTimeController getInstance() {
		if (_instance == null) {
			_instance = new HouseTaxTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				checkTaxDeadline();
				Thread.sleep(600000);
			}
		} catch (Exception e1) {
		}
	}

	public Calendar getRealTime() {
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(tz);
		return cal;
	}

	private void checkTaxDeadline() {
		for (L1House house : HouseTable.getInstance().getHouseTableList()) {
			if (!house.isOnSale()) { // 競売中のアジトはチェックしない
				if (house.getTaxDeadline().before(getRealTime())) {
					sellHouse(house);
				}
			}
		}
	}

	private void sellHouse(L1House house) {
		AuctionHouseTable boardTable = new AuctionHouseTable();
		L1AuctionHouse board = new L1AuctionHouse();
		if (board != null) {
			// 競売掲示板に新規書き込み
			int houseId = house.getHouseId();
			board.setHouseId(houseId);
			TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
			Calendar cal = Calendar.getInstance(tz);
			cal.add(Calendar.DATE, 5); // 5日後
			cal.set(Calendar.MINUTE, 0); // 分、秒は切り捨て
			cal.set(Calendar.SECOND, 0);
			board.setDeadline(cal);
			board.setPrice(100000);
			board.setOwnerId(0);
			board.setOwnerName(null);
			board.setBidderId(0);
			board.setBidderName(null);
			boardTable.insertAuctionBoard(board);
			house.setOnSale(true); // 競売中に設定
			house.setPurchaseBasement(true); // 地下アジト未購入に設定
			cal.add(Calendar.DATE, Config.HOUSE_TAX_INTERVAL);
			house.setTaxDeadline(cal);
			HouseTable.getInstance().updateHouse(house); // DBに書き込み
			// 以前の所有者のアジトを消す
			for (L1Clan clan : L1World.getInstance().getAllClans()) {
				if (clan.getHouseId() == houseId) {
					clan.setHouseId(0);
					ClanTable.getInstance().updateClan(clan);
				}
			}
		}
	}
}
