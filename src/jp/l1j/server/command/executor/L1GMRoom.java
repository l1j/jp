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
import jp.l1j.server.command.GMCommandConfigs;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1GMRoom implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1GMRoom.class.getName());

	private L1GMRoom() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GMRoom();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			int i = 0;
			try {
				i = Integer.parseInt(arg);
			} catch (NumberFormatException e) {
			}

			if (i == 1) {
				L1Teleport.teleport(pc, 32737, 32796, (short) 99, 5, false);
			} else if (i == 2) {
				L1Teleport.teleport(pc, 32734, 32799, (short) 17100, 5, false); // 17100!?
			} else if (i == 3) {
				L1Teleport.teleport(pc, 32644, 32955, (short) 0, 5, false);
			} else if (i == 4) {
				L1Teleport.teleport(pc, 33429, 32814, (short) 4, 5, false);
			} else if (i == 5) {
				L1Teleport.teleport(pc, 32894, 32535, (short) 300, 5, false);
			} else {
				L1Location loc = GMCommandConfigs.getInstance().getRooms().get(arg.toLowerCase());
				if (loc == null) {
					pc.sendPackets(new S_SystemMessage(String.format(I18N_DOES_NOT_EXIST_THE_ROOM, arg)));
					// ルーム: %s は存在しません。
					return;
				}
				L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), 5, false);
			}
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_1,
					cmdName, "1|2|3|4|5|" + I18N_ROOM_NAME)));
			// .%s %s の形式で入力してください。
		}
	}
}
