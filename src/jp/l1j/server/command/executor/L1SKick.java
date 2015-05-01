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
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_Disconnect;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1SKick implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1SKick.class.getName());

	private L1SKick() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1SKick();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			L1PcInstance target = L1World.getInstance().getPlayer(arg);
			if (target != null) {
				pc.sendPackets(new S_SystemMessage(String.format(I18N_ACCOUNT_KICK, target.getName())));
				// %s をキックしました。
				
				// SKTへ移動させる
				target.setX(33080);
				target.setY(33392);
				target.setMap((short) 4);
				target.saveInventory();
				target.sendPackets(new S_Disconnect());
				ClientThread targetClient = target.getNetConnection();
				targetClient.kick();
				_log.warning(String.format(I18N_DISCONNECTED_THE_CONNECTION,
						cmdName, targetClient.getAccountName(), targetClient.getHostname()));
				// %s コマンドにより、%s(%s)との接続を強制切断しました。
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
