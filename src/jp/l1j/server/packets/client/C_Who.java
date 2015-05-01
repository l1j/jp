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

package jp.l1j.server.packets.client;

import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_WhoAmount;
import jp.l1j.server.packets.server.S_WhoCharinfo;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_Who extends ClientBasePacket {

	private static final String C_WHO = "[C] C_Who";
	private static Logger _log = Logger.getLogger(C_Who.class.getName());

	public C_Who(byte[] decrypt, ClientThread client) {
		super(decrypt);
		String s = readS();
		L1PcInstance find = L1World.getInstance().getPlayer(s);
		L1PcInstance pc = client.getActiveChar();

		if (find != null) {
			S_WhoCharinfo s_whocharinfo = new S_WhoCharinfo(find);
			pc.sendPackets(s_whocharinfo);
		} else {
			if (Config.ALT_WHO_COMMAND) {
				String amount = String.valueOf(L1World.getInstance()
						.getAllPlayers().size());
				S_WhoAmount s_whoamount = new S_WhoAmount(amount);
				pc.sendPackets(s_whoamount);
			}
			// 対象が居ない場合はメッセージ表示する？わかる方修正お願いします。
		}
	}

	@Override
	public String getType() {
		return C_WHO;
	}
}
