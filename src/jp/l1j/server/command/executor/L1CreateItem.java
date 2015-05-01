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

package jp.l1j.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Item;

public class L1CreateItem implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1CreateItem.class.getName());

	private L1CreateItem() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1CreateItem();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String nameid = st.nextToken();
			int count = 1;
			if (st.hasMoreTokens()) {
				count = Integer.parseInt(st.nextToken());
			}
			int enchant = 0;
			if (st.hasMoreTokens()) {
				enchant = Integer.parseInt(st.nextToken());
			}
			int isId = 0;
			if (st.hasMoreTokens()) {
				isId = Integer.parseInt(st.nextToken());
			}
			int itemid = 0;
			try {
				itemid = Integer.parseInt(nameid);
			} catch (NumberFormatException e) {
				itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameid);
				if (itemid == 0) {
					pc.sendPackets(new S_SystemMessage(I18N_DOES_NOT_EXIST_ITEM));
					// アイテムが存在しません。
					return;
				}
			}
			L1Item temp = ItemTable.getInstance().getTemplate(itemid);
			if (temp != null) {
				if (temp.isStackable()) {
					L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
					item.setEnchantLevel(0);
					item.setCount(count);
					if (isId == 1) {
						item.setIdentified(true);
					}
					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
						pc.getInventory().storeItem(item);
						pc.sendPackets(new S_ServerMessage(403, // %0を手に入れました。
								item.getLogName() + "(ID:" + itemid + ")"));
					}
				} else {
					L1ItemInstance item = null;
					int createCount;
					for (createCount = 0; createCount < count; createCount++) {
						item = ItemTable.getInstance().createItem(itemid);
						item.setEnchantLevel(enchant);
						if (isId == 1) {
							item.setIdentified(true);
						}
						if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
							pc.getInventory().storeItem(item);
							if (item.getItem().getType2() == 1
									|| item.getItem().getType2() == 2) {
								item.setIsHaste(item.getItem().isHaste());
								if (item.getItem().getType2() == 1) {
									item.setCanBeDmg(item.getItem().getCanbeDmg());
								}
								item.save();
							}
						} else {
							break;
						}
					}
					if (createCount > 0) {
						pc.sendPackets(new S_ServerMessage(403, // %0を手に入れました。
								item.getLogName() + "(ID:" + itemid + ")"));
					}
				}
			} else {
				pc.sendPackets(new S_SystemMessage(I18N_DOES_NOT_EXIST_ITEM));
				// アイテムが存在しません。
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_4,
					cmdName, I18N_ITEM_ID+"|"+I18N_ITEM_NAME, "["+I18N_AMOUNT+"]",
					"["+I18N_ENCHANT+"}", "["+I18N_IDENTIFY+"]")));
			// .%s %s %s %s %s の形式で入力してください。
		}
	}
}
