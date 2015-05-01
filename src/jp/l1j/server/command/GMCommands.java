/**
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

package jp.l1j.server.command;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.command.executor.L1CommandExecutor;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Command;

// Referenced classes of package jp.l1j.server:
// ClientThread, Shutdown, IpTable, MobTable,
// PolyTable, IdFactory
//

public class GMCommands {
	private static Logger _log = Logger.getLogger(GMCommands.class.getName());

	private static GMCommands _instance;

	private GMCommands() {
	}

	public static GMCommands getInstance() {
		if (_instance == null) {
			_instance = new GMCommands();
		}
		return _instance;
	}

	private boolean executeDatabaseCommand(L1PcInstance pc, String name,
			String arg) {
		try {
			L1Command command = L1Commands.get(name);
			if (command == null) {
				return false;
			}
			if (pc.getAccessLevel() < command.getLevel()) {
				pc.sendPackets(new S_SystemMessage(String.format(I18N_CANNOT_USE_THE_COMMAND, name)));
				// コマンド %s は使用できません。
				return true;
			}

			Class<?> cls = Class.forName(command.getExecutorClassFullName());
			L1CommandExecutor exe = (L1CommandExecutor) cls.getMethod("getInstance").invoke(null);
			exe.execute(pc, name, arg);
			_log.info(String.format(I18N_USED_THE_COMMAND, pc.getName(), name, arg));
			// %s が .%s %s コマンドを使用しました。
			return true;
		} catch (Exception e) {
			_log.log(Level.SEVERE, "error gm command", e);
		}
		return false;
	}

	public void handleCommands(L1PcInstance gm, String cmdLine) {
		StringTokenizer token = new StringTokenizer(cmdLine);
		// 最初の空白までがコマンド、それ以降は空白を区切りとしたパラメータとして扱う
		String cmd = token.nextToken();
		String param = "";
		while (token.hasMoreTokens()) {
			param = new StringBuilder(param).append(token.nextToken()).append(' ').toString();
		}
		param = param.trim();

		// データベース化されたコマンド
		if (executeDatabaseCommand(gm, cmd, param)) {
			if (!cmd.equalsIgnoreCase("r")) {
				_lastCommands.put(gm.getId(), cmdLine);
			}
			return;
		}
		if (cmd.equalsIgnoreCase("r")) {
			if (!_lastCommands.containsKey(gm.getId())) {
				gm.sendPackets(new S_SystemMessage(String.format(I18N_CANNOT_USE_THE_COMMAND, cmd)));
				// コマンド %s は使用できません。
				return;
			}
			redo(gm, param);
			return;
		}
		gm.sendPackets(new S_SystemMessage(String.format(I18N_DOES_NOT_EXIST_COMMAND, cmd)));
		// コマンド %s は存在しません。
	}

	private static Map<Integer, String> _lastCommands = new HashMap<Integer, String>();

	private void redo(L1PcInstance pc, String arg) {
		try {
			String lastCmd = _lastCommands.get(pc.getId());
			if (arg.isEmpty()) {
				pc.sendPackets(new S_SystemMessage(String.format(I18N_REEXECUTE_THE_COMMAND, lastCmd)));
				// コマンド %s を再実行します。
				handleCommands(pc, lastCmd);
			} else {
				// 引数を変えて実行
				StringTokenizer token = new StringTokenizer(lastCmd);
				String cmd = token.nextToken() + " " + arg;
				pc.sendPackets(new S_SystemMessage(String.format(I18N_EXECUTE_THE_COMMAND, cmd)));
				// コマンド %s を実行します。
				handleCommands(pc, cmd);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			pc.sendPackets(new S_SystemMessage(I18N_REEXECUTE_THE_COMMAND_ERROR));
			// コマンド %s を再実行します。
		}
	}
}
