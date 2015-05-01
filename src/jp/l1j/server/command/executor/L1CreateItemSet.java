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

import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.command.GMCommandConfigs;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Item;
import jp.l1j.server.templates.L1ItemSetItem;

public class L1CreateItemSet implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1CreateItemSet.class.getName());

	private L1CreateItemSet() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1CreateItemSet();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			String name = new StringTokenizer(arg).nextToken();
			List<L1ItemSetItem> list = GMCommandConfigs.getInstance().getItemSets().get(name);
			if (list == null) {
				pc.sendPackets(new S_SystemMessage(I18N_DOES_NOT_EXIST_ITEM_SET));
				// アイテムセットが存在しません。
				return;
			}
			for (L1ItemSetItem item : list) {
				L1Item temp = ItemTable.getInstance().getTemplate(item.getId());
				if (!temp.isStackable() && 0 != item.getEnchant()) {
					for (int i = 0; i < item.getAmount(); i++) {
						L1ItemInstance inst = ItemTable.getInstance().createItem(item.getId());
						inst.setEnchantLevel(item.getEnchant());
						pc.getInventory().storeItem(inst);
					}
				} else {
					pc.getInventory().storeItem(item.getId(), item.getAmount());
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_1,
					cmdName, I18N_ITEM_SET_NAME)));
			// .%s %s の形式で入力してください。
		}
	}
}
