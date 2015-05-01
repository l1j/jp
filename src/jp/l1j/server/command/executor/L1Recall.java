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

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1Recall implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Recall.class.getName());

	private L1Recall() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Recall();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			Collection<L1PcInstance> targets = null;
			if (arg.equalsIgnoreCase("all")) {
				targets = L1World.getInstance().getAllPlayers();
			} else {
				targets = new ArrayList<L1PcInstance>();
				L1PcInstance tg = L1World.getInstance().getPlayer(arg);
				if (tg == null) {
					pc.sendPackets(new S_SystemMessage(String.format(I18N_DOES_NOT_EXIST_CHAR, arg)));
					// %s はゲームワールド内に存在しません。
					return;
				}
				targets.add(tg);
			}

			for (L1PcInstance target : targets) {
				if (target.isGm()) {
					continue;
				}
				L1Teleport.teleportToTargetFront(target, pc, 2);
				pc.sendPackets(new S_SystemMessage(String.format(I18N_RECALLED, target.getName())));
				// %s を召還しました。
				target.sendPackets(new S_SystemMessage(I18N_RECALLED_BY_GM));
				// GMから召還されました。
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_1,
					cmdName, "all|"+I18N_CHAR_NAME)));
			// .%s %s の形式で入力してください。
		}
	}
}
