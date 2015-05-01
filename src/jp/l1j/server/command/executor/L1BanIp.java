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
import jp.l1j.server.datatables.IpTable;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1BanIp implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1BanIp.class.getName());

	private L1BanIp() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1BanIp();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(arg);
			// IPを指定
			String s1 = stringtokenizer.nextToken();

			// add/delを指定(しなくてもOK)
			String s2 = null;
			try {
				s2 = stringtokenizer.nextToken();
			} catch (Exception e) {
			}

			IpTable iptable = IpTable.getInstance();
			boolean isBanned = iptable.isBannedIp(s1);
			String host = null;

			for (L1PcInstance tg : L1World.getInstance().getAllPlayers()) {
				if (s1.equals(tg.getNetConnection().getIp())) {
					host = tg.getNetConnection().getHostname();
					pc.sendPackets(new S_SystemMessage(String.format(I18N_CONNECTED_PLAYER,
							s1, tg.getName())));
					// %s で接続中のプレイヤー: %s
				}
			}

			if ("add".equals(s2) && !isBanned) {
				iptable.banIp(s1, host); // BANリストへIPを加える
				pc.sendPackets(new S_SystemMessage(String.format(I18N_ADDED_TO_THE_BAN_LIST, s1)));
				// %s をBANリストに登録しました。
			} else if ("del".equals(s2) && isBanned) {
				if (iptable.liftBanIp(s1, host)) { // BANリストからIPを削除する
					pc.sendPackets(new S_SystemMessage(String.format(I18N_REMOVED_FROM_THE_BAN_LIST, s1)));
					// %s をBANリストから削除しました。
				}
			} else {
				// BANの確認
				if (isBanned) {
					pc.sendPackets(new S_SystemMessage(String.format(I18N_EXIST_IN_THE_BAN_LIST, s1)));
					// %s はBANリストに存在します。
				} else {
					pc.sendPackets(new S_SystemMessage(String.format(I18N_DOES_NOT_EXIST_IN_THE_BAN_LIST, s1)));
					// %s はBANリストに存在しません。
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_2,
					cmdName, "IP", "[add|del]")));
			// .%s %s %s の形式で入力してください。
		}
	}
}
