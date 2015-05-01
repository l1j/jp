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

import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;

/**
 * 入力された引数をそのまま返すコマンド。 テスト、デバッグ及びコマンド実装サンプル用。
 */
public class L1Echo implements L1CommandExecutor {
	private L1Echo() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Echo();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		pc.sendPackets(new S_SystemMessage(arg));
	}
}
