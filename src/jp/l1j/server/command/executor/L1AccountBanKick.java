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

import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.CharacterTable;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_Disconnect;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Account;

public class L1AccountBanKick implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1AccountBanKick.class.getName());

	private L1AccountBanKick() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AccountBanKick();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			L1PcInstance target = L1World.getInstance().getPlayer(arg);
			if (target == null) {
				target = CharacterTable.getInstance().restoreCharacter(arg);
			}

			if (target != null) {
				// アカウントをBANする
				L1Account.ban(target.getAccountName());
					pc.sendPackets(new S_SystemMessage(String.format(I18N_ACCOUNT_BAN,
									target.getName())));
				// %s をBANしました。
				if (target.getOnlineStatus() == 1) {
					target.sendPackets(new S_Disconnect());
				}
			} else {
				pc.sendPackets(new S_SystemMessage(String.format(I18N_DOES_NOT_EXIST_CHAR, arg)));
				// %s はゲームワールド内に存在しません。
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_1,
					cmdName, I18N_CHAR_NAME)));
			// .%s %s の形式で入力してください。
		}
	}
}
