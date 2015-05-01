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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Account;
import jp.l1j.server.templates.L1InventoryItem;
import jp.l1j.server.templates.L1Item;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.L1SqlException;
import jp.l1j.server.utils.SqlUtil;
import jp.l1j.server.utils.collections.Lists;

public class L1Present implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Present.class.getName());

	private L1Present() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Present();
	}

	private static void present(String accountName, int itemid, int enchant, int count) {
		L1Item temp = ItemTable.getInstance().getTemplate(itemid);
		if (temp == null) {
			return;
		}

		List<L1Account> accounts = Lists.newArrayList();
		if (accountName.compareToIgnoreCase("*") == 0) {
			accounts = L1Account.findAll();
		} else {
			accounts.add(L1Account.findByName(accountName));
		}

		present(accounts, temp, enchant, count);
	}

	public static void present(int minLevel, int maxLevel, int itemid, int enchant, int count) {
		L1Item temp = ItemTable.getInstance().getTemplate(itemid);
		if (temp == null) {
			return;
		}

		List<L1Account> accounts = L1Account.findByCharacterLevel(minLevel, maxLevel);
		present(accounts, temp, enchant, count);
	}

	private static void present(List<L1Account> accountList, L1Item itemTemplate, int enchantLevel, int count) {
		Connection con = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			con.setAutoCommit(false);

			for (L1Account account : accountList) {
				L1InventoryItem item = new L1InventoryItem();
				item.setOwnerId(account.getId());
				item.setEnchantLevel(enchantLevel);
				if (itemTemplate.isStackable()) {
					item.setId(IdFactory.getInstance().nextId());
					item.setItemId(itemTemplate.getItemId());
					item.setLocation(1);
					item.setItemCount(count);
					item.save(con);
				} else {
					for (int i = 0; i < count; i++) {
						item.setId(IdFactory.getInstance().nextId());
						item.setItemId(itemTemplate.getItemId());
						item.setLocation(1);
						item.setItemCount(1);
						item.save(con);
					}
				}
			}

			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			SqlUtil.rollback(con);
			throw new L1SqlException(String.format(I18N_COMMAND_ERROR, "present"), e);
			// .%s コマンドエラー
		} finally {
			SqlUtil.close(con);
		}
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String account = st.nextToken();
			int itemid = Integer.parseInt(st.nextToken(), 10);
			int enchant = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);

			L1Item temp = ItemTable.getInstance().getTemplate(itemid);
			if (temp == null) {
				pc.sendPackets(new S_SystemMessage(I18N_DOES_NOT_EXIST_ITEM));
				// アイテムが存在しません。
				return;
			}

			present(account, itemid, enchant, count);
			pc.sendPackets(new S_SystemMessage(String.format(I18N_GAVE_THE_ITEM_2,
				temp.getIdentifiedNameId(), count)));
			// %s を %d 個与えました。
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_5,
				cmdName, I18N_CHAR_NAME, I18N_ITEM_ID, I18N_ENCHANT, I18N_AMOUNT, I18N_CHAR_NAME+"=* is All")));
			// .%s %s %s %s %s %s の形式で入力してください。
		}
	}
}
