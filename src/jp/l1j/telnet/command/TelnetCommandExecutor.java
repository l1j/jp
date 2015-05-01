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

package jp.l1j.telnet.command;

import java.util.StringTokenizer;
import static jp.l1j.telnet.command.TelnetCommandResult.*;

public class TelnetCommandExecutor {
	private static TelnetCommandExecutor _instance = new TelnetCommandExecutor();

	public static TelnetCommandExecutor getInstance() {
		return _instance;
	}

	public TelnetCommandResult execute(String cmd) {
		try {
			StringTokenizer tok = new StringTokenizer(cmd, " ");
			String name = tok.nextToken();

			TelnetCommand command = TelnetCommandList.get(name);
			if (command == null) {
				return new TelnetCommandResult(CMD_NOT_FOUND, cmd
						+ " not found");
			}

			String args = "";
			if (name.length() + 1 < cmd.length()) {
				args = cmd.substring(name.length() + 1);
			}
			return command.execute(args);
		} catch (Exception e) {
			return new TelnetCommandResult(CMD_INTERNAL_ERROR, e
					.getLocalizedMessage());
		}
	}
}
