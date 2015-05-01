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
import java.util.logging.Logger;
import jp.l1j.server.command.L1Commands;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_OutputRawString;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Command;

public class L1CommandHelp implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1CommandHelp.class.getName());

	private L1CommandHelp() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1CommandHelp();
	}

	private String join(List<L1Command> list, String with) {
		StringBuilder result = new StringBuilder();
		for (L1Command cmd : list) {
			if (result.length() > 0) {
				result.append(with);
			}
			result.append(cmd.getName());
		}
		return result.toString();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		List<L1Command> list = L1Commands.availableCommandList(pc.getAccessLevel());
		pc.sendPackets(new S_OutputRawString(pc.getId(), "Command Help", join(list, ", ")));
	}
}
