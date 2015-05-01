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
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1InvGfxId implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1InvGfxId.class.getName());

	private L1InvGfxId() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1InvGfxId();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int gfxid = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);
			for (int i = 0; i < count; i++) {
				L1ItemInstance item = ItemTable.getInstance().createItem(40005);
				item.getItem().setGfxId(gfxid + i);
				item.getItem().setName(String.valueOf(gfxid + i));
				pc.getInventory().storeItem(item);
			}
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_2,
					cmdName, I18N_GFX_ID, I18N_AMOUNT)));
			// .%s %s %s の形式で入力してください。
		}
	}
}
