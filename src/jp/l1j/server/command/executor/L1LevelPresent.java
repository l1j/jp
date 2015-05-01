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
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Item;

public class L1LevelPresent implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1LevelPresent.class.getName());

	private L1LevelPresent() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1LevelPresent();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {

		try {
			StringTokenizer st = new StringTokenizer(arg);
			int minlvl = Integer.parseInt(st.nextToken(), 10);
			int maxlvl = Integer.parseInt(st.nextToken(), 10);
			int itemid = Integer.parseInt(st.nextToken(), 10);
			int enchant = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);

			L1Item temp = ItemTable.getInstance().getTemplate(itemid);
			if (temp == null) {
				pc.sendPackets(new S_SystemMessage(I18N_DOES_NOT_EXIST_ITEM));
				// アイテムが存在しません。
				return;
			}

			L1Present.present(minlvl, maxlvl, itemid, enchant, count);
			pc.sendPackets(new S_SystemMessage(String.format(I18N_GAVE_THE_ITEM_1,
				temp.getName(), count, I18N_MIN_LEVEL, minlvl, I18N_MAX_LEVEL, maxlvl)));
			// %s を %d 個与えました。(%s: %d, %s: %d)
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_5,
				cmdName, I18N_MIN_LEVEL, I18N_MAX_LEVEL, I18N_ITEM_ID, I18N_ENCHANT, I18N_AMOUNT)));
			// // .%s %s %s %s %s %s の形式で入力してください。
		}
	}
}
