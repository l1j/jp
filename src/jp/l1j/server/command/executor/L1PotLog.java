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

import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1PotLog implements L1CommandExecutor {

	private L1PotLog() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1PotLog();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		if (arg.equalsIgnoreCase("off")) {
			pc.setPotLog(false);
			pc.sendPackets(new S_SystemMessage(I18N_CHANGED_TO_HIDE_USE_OF_POTIONS));
			// ポーションの使用を非表示に変更しました。
		} else if (arg.equalsIgnoreCase("on")) {
			pc.setPotLog(true);
			pc.sendPackets(new S_SystemMessage(I18N_CHANGED_TO_DISPLAY_USE_OF_POTIONS));
			// ポーションの使用を表示に変更しました。
		} else {
			if (pc.getPotLog()) {
				pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_ON, cmdName)));
				// .%s on|off と入力してください。現在はONです。
			} else {
				pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_ON, cmdName)));
				// .%s on|off と入力してください。現在はOFFです。
			}
		}
	}
}
