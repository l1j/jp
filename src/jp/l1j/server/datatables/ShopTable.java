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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_ITEM_LIST;
import jp.l1j.server.model.shop.L1Shop;
import jp.l1j.server.templates.L1ShopItem;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class ShopTable {
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(ShopTable.class.getName());

	private static ShopTable _instance;

	private static Map<Integer, L1Shop> _allShops = new HashMap<Integer, L1Shop>();

	public static ShopTable getInstance() {
		if (_instance == null) {
			_instance = new ShopTable();
		}
		return _instance;
	}

	private ShopTable() {
		load();
	}

	private ArrayList<Integer> enumNpcIds() {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT DISTINCT npc_id FROM shops");
			rs = pstm.executeQuery();
			while (rs.next()) {
				ids.add(rs.getInt("npc_id"));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
		return ids;
	}

	private L1Shop loadShop(int npcId, ResultSet rs) throws SQLException {
		List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();
		List<L1ShopItem> purchasingList = new ArrayList<L1ShopItem>();
		List<Integer> purchasingItemIds = new ArrayList<Integer>();
		while (rs.next()) {
			int itemId = rs.getInt("item_id");
			boolean isErr = false;
			if (ItemTable.getInstance().getTemplate(itemId) == null) {
				System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, itemId));
				// %s はアイテムリストに存在しません。
				isErr = true;
			}
			if (isErr) {
				continue;
			}
			double sellingPrice = rs.getDouble("selling_price");
			double purchasingPrice = rs.getDouble("purchasing_price");
			int packCount = rs.getInt("pack_count");
			packCount = packCount == 0 ? 1 : packCount;
			if (0 <= sellingPrice) {
				L1ShopItem item = new L1ShopItem(itemId, sellingPrice, packCount);
				sellingList.add(item);
			}
			if (0 <= purchasingPrice) {
				if (purchasingItemIds.indexOf(itemId) < 0) {
					L1ShopItem item = new L1ShopItem(itemId, purchasingPrice, packCount);
					purchasingList.add(item);
					purchasingItemIds.add(itemId);
				}
			}
		}
		return new L1Shop(npcId, sellingList, purchasingList);
	}

	private void loadShops(Map<Integer, L1Shop> allShops) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT npc_id, shops.item_id, order_id, selling_price, pack_count, purchasing_price FROM shops LEFT OUTER JOIN item_rates ON (shops.item_id = item_rates.item_id) WHERE npc_id=? ORDER BY order_id");
			for (int npcId : enumNpcIds()) {
				pstm.setInt(1, npcId);
				rs = pstm.executeQuery();
				L1Shop shop = loadShop(npcId, rs);
				allShops.put(npcId, shop);
				rs.close();
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	private L1Shop loadGeneralShop(int npcId, ResultSet rs) throws SQLException {
		List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();
		List<L1ShopItem> purchasingList = new ArrayList<L1ShopItem>();
		while (rs.next()) {
			int itemId = rs.getInt("item_id");
			boolean isErr = false;
			if (ItemTable.getInstance().getTemplate(itemId) == null) {
				System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, itemId));
				// %s はアイテムリストに存在しません。
				isErr = true;
			}
			if (isErr) {
				continue;
			}
			double sellingPrice = rs.getDouble("selling_price");
			double purchasingPrice = rs.getDouble("purchasing_price");
			int packCount = 1;
			if (0 <= sellingPrice) {
				L1ShopItem item = new L1ShopItem(itemId, sellingPrice, packCount);
				sellingList.add(item);
			}
			if (0 <= purchasingPrice) {
				L1ShopItem item = new L1ShopItem(itemId, purchasingPrice, packCount);
				purchasingList.add(item);
			}
		}
		return new L1Shop(npcId, sellingList, purchasingList);
	}

	// アデン商団の買取リスト（item_ratesテーブルに登録されている全アイテム）
	private void loadGeneralShops(Map<Integer, L1Shop> allShops) {
		int[] generalMerchantIds = { 70023, 70037, 70064, 70076 }; // アデン商団
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT item_id, selling_price, purchasing_price FROM item_rates");
			for (int npcId : generalMerchantIds) {
				rs = pstm.executeQuery();
				L1Shop shop = loadGeneralShop(npcId, rs);
				allShops.put(npcId, shop);
				rs.close();
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	private void load() {
		PerformanceTimer timer = new PerformanceTimer();
		loadShops(_allShops);
		loadGeneralShops(_allShops);
		System.out.println("loading shops...OK! " + timer.elapsedTimeMillis() + "ms");
	}
	
	public void reload() {
		PerformanceTimer timer = new PerformanceTimer();
		Map<Integer, L1Shop> allShops = new HashMap<Integer, L1Shop>();
		loadShops(allShops);
		loadGeneralShops(allShops);
		_allShops = allShops;
		System.out.println("loading shops...OK! " + timer.elapsedTimeMillis() + "ms");
	}
		
	public L1Shop get(int npcId) {
		return _allShops.get(npcId);
	}
}
