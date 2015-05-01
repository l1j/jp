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

import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1MessageId;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1Item;

public class L1ItemUtil {

	public static void createNewItem(L1PcInstance pc, int itemId, int count) {
		L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
		item.setCount(count);
		if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
			pc.getInventory().storeItem(item);
		} else { // 持てない場合は地面に落とす 処理のキャンセルはしない（不正防止）
			L1World.getInstance().getInventory(pc.getX(), pc.getY(),
					pc.getMapId()).storeItem(item);
		}
		pc.sendPackets(new S_ServerMessage(L1MessageId.OBTAINED, item
				.getLogName()));
	}

	public static boolean isFood(L1Item item) {
		return item.getType2() == 0 && item.getType() == 7;
	}

}
