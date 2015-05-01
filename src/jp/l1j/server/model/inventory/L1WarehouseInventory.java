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
package jp.l1j.server.model.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.Server;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.templates.L1InventoryItem;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class L1WarehouseInventory extends L1Inventory {
	private static Logger _log = Logger.getLogger(L1WarehouseInventory.class.getName());
	private static final long serialVersionUID = 1L;
	private final int _ownerId;
	private final int _ownerLocation;

	public L1WarehouseInventory(int ownerId, int ownerLocation) {
		_ownerId = ownerId;
		_ownerLocation = ownerLocation;
	}

	// DBの読込
	@Override
	public void loadItems() {
		List<L1InventoryItem> inventoryItems = L1InventoryItem
				.findByOwnerIdAndLocation(_ownerId, _ownerLocation);

		List<L1ItemInstance> items = L1InventoryItem
				.instantiate(inventoryItems);
		for (L1ItemInstance item : items) {
			_items.add(item);
			L1World.getInstance().storeObject(item);
		}
	}

	// DBへ登録
	@Override
	public void insertItem(L1ItemInstance item) {
		item.setOwner(_ownerId, _ownerLocation);
		item.save();
	}

	// DBを更新
	@Override
	public void updateItem(L1ItemInstance item) {
		item.save();
	}

	// DBのクラン倉庫のアイテムを全て削除(血盟解散時のみ使用)
	public synchronized void deleteAllItems() {
		L1InventoryItem.deleteAll(_ownerId);
	}
	
	/**
	 * クラン倉庫の使用履歴を記録
	 * @param pc    クラン倉庫の使用者
	 * @param item  取引するアイテム
	 * @param count アイテム数量
	 * @param type  受領区分(0:預入, 1:受取)
	 */
	public void writeHistory(L1PcInstance pc, L1ItemInstance item, int count, int type){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO clan_warehouse_histories SET clan_id=?, char_name=?, type=?, item_name=?, item_count=?, record_time=?");
			pstm.setInt(1, pc.getClanId());
			pstm.setString(2, pc.getName());
			pstm.setInt(3, type);
			pstm.setString(4, item.getWarehouseHistoryName());
			pstm.setInt(5, count);
			pstm.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			pstm.execute();
		}
		catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
	
	@Override
	public int getOwnerLocation() {
		return _ownerLocation;
	}
}