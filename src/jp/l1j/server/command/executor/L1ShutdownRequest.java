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
import jp.l1j.configure.Config;
import jp.l1j.locale.I18N;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.GameServer;
import jp.l1j.server.datatables.ShutdownRequestTable;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1ShutdownRequest implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Shutdown.class.getName());

	private L1ShutdownRequest() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ShutdownRequest();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
	
		if (Config.SHUTDOWN_REQUEST_MAX > 0
						&& ShutdownRequestTable.countByIp(pc) == 0) {
			ShutdownRequestTable.create(pc);
			pc.sendPackets(new S_SystemMessage(I18N_ACCEPTED_SHUTDOWN_REQUEST));
			// シャットダウン要求を受け付けました。
		} else {
			pc.sendPackets(new S_ServerMessage(79));
			// \f1何も起きませんでした。
		}
		
		if(Config.SHUTDOWN_REQUEST_MAX > 0
						&& ShutdownRequestTable.countAll() >= Config.SHUTDOWN_REQUEST_MAX){
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(
							I18N_SHUTDOWN_REQUEST_MAX));
			// シャットダウン要求が一定数に達しました。シャットダウンを開始します。
			GameServer.getInstance().shutdownWithCountdown(Config.SHUTDOWN_DELAY);
		}
	}
}
