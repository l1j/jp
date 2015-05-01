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
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.item.L1ItemId;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1AuctionHouse;
import jp.l1j.server.templates.L1House;
import jp.l1j.server.templates.L1InventoryItem;

public class AuctionTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(AuctionTimeController.class.getName());

	private static AuctionTimeController _instance;

	public static AuctionTimeController getInstance() {
		if (_instance == null) {
			_instance = new AuctionTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				checkAuctionDeadline();
				Thread.sleep(60000);
			}
		} catch (Exception e1) {
		}
	}

	public Calendar getRealTime() {
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(tz);
		return cal;
	}

	private void checkAuctionDeadline() {
		AuctionHouseTable boardTable = new AuctionHouseTable();
		for (L1AuctionHouse board : boardTable.getAuctionBoardTableList()) {
			if (board.getDeadline().before(getRealTime())) {
				endAuction(board);
			}
		}
	}

	private void endAuction(L1AuctionHouse board) {
		int houseId = board.getHouseId();
		int price = board.getPrice();
		int oldOwnerId = board.getOwnerId();
		String bidderName = board.getBidderName();
		int bidderId = board.getBidderId();

		if (oldOwnerId != 0 && bidderId != 0) { // 以前の所有者あり・落札者あり
			L1PcInstance oldOwnerPc = (L1PcInstance) L1World.getInstance().findObject(oldOwnerId);
			int payPrice = (int) (price * 0.9);
			if (oldOwnerPc != null) { // 以前の所有者がオンライン中
				oldOwnerPc.getInventory().storeItem(L1ItemId.ADENA, payPrice);
				// あなたが所有していた家が最終価格%1アデナで落札されました。%n
				// 手数料10%%を除いた残りの金額%0アデナを差し上げます。%nありがとうございました。%n%n
				oldOwnerPc.sendPackets(new S_ServerMessage(527, String.valueOf(payPrice)));
			} else { // 以前の所有者がオフライン中
				L1ItemInstance item = ItemTable.getInstance().createItem(L1ItemId.ADENA);
				item.setCount(payPrice);
				item.setOwner(oldOwnerId, L1InventoryItem.LOC_CHARACTER);
				item.save();
			}

			L1PcInstance bidderPc = (L1PcInstance) L1World.getInstance().findObject(bidderId);
			if (bidderPc != null) { // 落札者がオンライン中
				// おめでとうございます。%nあなたが参加された競売は最終価格%0アデナの価格で落札されました。%n
				// 様がご購入された家はすぐにご利用できます。%nありがとうございました。%n%n
				bidderPc.sendPackets(new S_ServerMessage(524, String.valueOf(price), bidderName));
			}
			deleteHouseInfo(houseId);
			setHouseInfo(houseId, bidderId);
			deleteNote(houseId);
		} else if (oldOwnerId == 0 && bidderId != 0) { // 以前の所有者なし・落札者あり
			L1PcInstance bidderPc = (L1PcInstance) L1World.getInstance().findObject(bidderId);
			if (bidderPc != null) { // 落札者がオンライン中
				// おめでとうございます。%nあなたが参加された競売は最終価格%0アデナの価格で落札されました。%n
				// 様がご購入された家はすぐにご利用できます。%nありがとうございました。%n%n
				bidderPc.sendPackets(new S_ServerMessage(524, String.valueOf(price), bidderName));
			}
			setHouseInfo(houseId, bidderId);
			deleteNote(houseId);
		} else if (oldOwnerId != 0 && bidderId == 0) { // 以前の所有者あり・落札者なし
			L1PcInstance oldOwnerPc = (L1PcInstance) L1World.getInstance().findObject(oldOwnerId);
			if (oldOwnerPc != null) { // 以前の所有者がオンライン中
				// あなたが申請なさった競売は、競売期間内に提示した金額以上での支払いを表明した方が現れなかったため、結局取り消されました。%n
				// 従って、所有権があなたに戻されたことをお知らせします。%nありがとうございました。%n%n
				oldOwnerPc.sendPackets(new S_ServerMessage(528));
			}
			deleteNote(houseId);
		} else if (oldOwnerId == 0 && bidderId == 0) { // 以前の所有者なし・落札者なし
			// 締め切りを5日後に設定し再度競売にかける
			Calendar cal = getRealTime();
			cal.add(Calendar.DATE, 5); // 5日後
			cal.set(Calendar.MINUTE, 0); // 分、秒は切り捨て
			cal.set(Calendar.SECOND, 0);
			board.setDeadline(cal);
			AuctionHouseTable boardTable = new AuctionHouseTable();
			boardTable.updateAuctionBoard(board);
		}
	}

	/**
	 * 以前の所有者のアジトを消す
	 * 
	 * @param houseId
	 * 
	 * @return
	 */
	private void deleteHouseInfo(int houseId) {
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (clan.getHouseId() == houseId) {
				clan.setHouseId(0);
				ClanTable.getInstance().updateClan(clan);
			}
		}
	}

	/**
	 * 落札者のアジトを設定する
	 * 
	 * @param houseId
	 *            bidderId
	 * 
	 * @return
	 */
	private void setHouseInfo(int houseId, int bidderId) {
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (clan.getLeaderId() == bidderId) {
				clan.setHouseId(houseId);
				ClanTable.getInstance().updateClan(clan);
				break;
			}
		}
	}

	/**
	 * アジトの競売状態をOFFに設定し、競売掲示板から消す
	 * 
	 * @param houseId
	 * 
	 * @return
	 */
	private void deleteNote(int houseId) {
		// アジトの競売状態をOFFに設定する
		L1House house = HouseTable.getInstance().getHouseTable(houseId);
		house.setOnSale(false);
		Calendar cal = getRealTime();
		cal.add(Calendar.DATE, Config.HOUSE_TAX_INTERVAL);
		cal.set(Calendar.MINUTE, 0); // 分、秒は切り捨て
		cal.set(Calendar.SECOND, 0);
		house.setTaxDeadline(cal);
		HouseTable.getInstance().updateHouse(house);

		// 競売掲示板から消す
		AuctionHouseTable boardTable = new AuctionHouseTable();
		boardTable.deleteAuctionBoard(houseId);
	}

}
