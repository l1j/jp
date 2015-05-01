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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.command.GMCommands;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1Favorite implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Favorite.class.getName());
	private static final Map<Integer, String> _faviCom = new HashMap<Integer, String>();

	private L1Favorite() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Favorite();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			if (!_faviCom.containsKey(pc.getId())) {
				_faviCom.put(pc.getId(), "");
			}
			String faviCom = _faviCom.get(pc.getId());
			if (arg.startsWith("set")) {
				// コマンドの登録
				StringTokenizer st = new StringTokenizer(arg);
				st.nextToken();
				if (!st.hasMoreTokens()) {
					pc.sendPackets(new S_SystemMessage(I18N_COMMAND_IS_EMPTY));
					// コマンドが空です。
					return;
				}
				StringBuilder cmd = new StringBuilder();
				String temp = st.nextToken(); // コマンドタイプ
				if (temp.equalsIgnoreCase(cmdName)) {
					pc.sendPackets(new S_SystemMessage(String.format(I18N_CAN_NOT_BE_REGISTERD, cmdName)));
					// %s コマンドは登録できません。
					return;
				}
				cmd.append(temp + " ");
				while (st.hasMoreTokens()) {
					cmd.append(st.nextToken() + " ");
				}
				faviCom = cmd.toString().trim();
				_faviCom.put(pc.getId(), faviCom);
				pc.sendPackets(new S_SystemMessage(String.format(I18N_REGISTERD_COMMAND, faviCom)));
				// %s コマンドを登録しました。
			} else if (arg.startsWith("show")) {
				pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_THAT_ARE_REGISTERD, faviCom)));
				// 登録されているコマンド: %s
			} else if (faviCom.isEmpty()) {
				pc.sendPackets(new S_SystemMessage(I18N_DOES_NOT_EXIST_COMMAND_THAT_ARE_REGISTERD));
				// 登録されているコマンドは存在しません。
			} else {
				StringBuilder cmd = new StringBuilder();
				StringTokenizer st = new StringTokenizer(arg);
				StringTokenizer st2 = new StringTokenizer(faviCom);
				while (st2.hasMoreTokens()) {
					String temp = st2.nextToken();
					if (temp.startsWith("%")) {
						cmd.append(st.nextToken() + " ");
					} else {
						cmd.append(temp + " ");
					}
				}
				while (st.hasMoreTokens()) {
					cmd.append(st.nextToken() + " ");
				}
				pc.sendPackets(new S_SystemMessage(String.format(I18N_EXECUTE_THE_COMMAND, cmd)));
				// .%s を実行します。
				GMCommands.getInstance().handleCommands(pc, cmd.toString());
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_2,
					cmdName + " set " + I18N_COMMAND_NAME + " | ",
					cmdName + " show | ",
					cmdName + " [" + I18N_PARAM + "]")));
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			// .%s %s %s の形式で入力してください。
		}
	}
}
