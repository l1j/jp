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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.utils.L1QueryUtil;
import jp.l1j.server.utils.L1QueryUtil.EntityFactory;

public class L1BeginnerItem {
	private int _id;
	private int _itemId;
	private int _itemCount;
	private int _chargeCount;
	private int _enchantLevel;
	private String _classInitial;

	private static class Factory implements EntityFactory<L1BeginnerItem> {
		@Override
		public L1BeginnerItem fromResultSet(ResultSet rs) throws SQLException {
			L1BeginnerItem result = new L1BeginnerItem();
			result._id = rs.getInt("id");
			result._itemId = rs.getInt("item_id");
			result._itemCount = rs.getInt("item_count");
			result._chargeCount = rs.getInt("charge_count");
			result._enchantLevel = rs.getInt("enchant_level");
			result._classInitial = rs.getString("class_initial");
			return result;
		}
	}

	public static List<L1BeginnerItem> findByClass(String classNameInitial) {
		return L1QueryUtil.selectAll(new Factory(),
				"SELECT * FROM beginner_items WHERE class_initial IN(?, ?)",
				"A", classNameInitial);
	}

	public int getId() {
		return _id;
	}

	public int getItemId() {
		return _itemId;
	}

	public int getItemCount() {
		return _itemCount;
	}

	public int getChargeCount() {
		return _chargeCount;
	}

	public int getEnchantLevel() {
		return _enchantLevel;
	}

	public String getClassInitial() {
		return _classInitial;
	}

	/**
	 * このアイテムを対象プレイヤーのインベントリデータベースへコミットします。
	 * 
	 * @param characterId
	 *            対象プレイヤー
	 */
	public void storeToInventory(L1PcInstance pc) {
		L1ItemInstance item = pc.getInventory().findItemId(_itemId);
		if (item == null) {
			item = pc.getInventory().storeItem(_itemId, _itemCount);
			item.setChargeCount(_chargeCount);
			item.setEnchantLevel(_enchantLevel);
			item.save();
			pc.sendPackets(new S_ServerMessage(143, item.getItem().getName()));
		}
	}
}
