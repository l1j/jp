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
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1Chat implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Chat.class.getName());

	private L1Chat() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Chat();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			if (st.hasMoreTokens()) {
				String flag = st.nextToken();
				String msg;
				if (flag.compareToIgnoreCase("on") == 0) {
					L1World.getInstance().setWorldChatElabled(true);
					msg = I18N_ENABLED_THE_GLOBAL_CHAT;
					// グローバルチャットを可能にしました。
				} else if (flag.compareToIgnoreCase("off") == 0) {
					L1World.getInstance().setWorldChatElabled(false);
					msg = I18N_DISABLED_THE_GLOBAL_CHAT;
					// グローバルチャットを不可にしました。
				} else {
					throw new Exception();
				}
				pc.sendPackets(new S_SystemMessage(msg));
			} else {
				String msg;
				if (L1World.getInstance().isWorldChatElabled()) {
					msg = I18N_GLOBAL_CHAT_IS_POSSIBLE;
					// グローバルチャットは可能です。無効にするには .chat off を入力してください。
				} else {
					msg = I18N_GLOBAL_CHAT_IS_IMPOSSIBLE;
					// グローバルチャットは不可です。有効にするには .chat on を入力してください。
				}
				pc.sendPackets(new S_SystemMessage(msg));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_1,
					cmdName, "[on|off]")));
			// .%s %s の形式で入力してください。
		}
	}
}
