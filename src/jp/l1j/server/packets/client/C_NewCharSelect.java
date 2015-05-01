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
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_PacketBox;

public class C_NewCharSelect extends ClientBasePacket {
	private static final String C_NEW_CHAR_SELECT = "[C] C_NewCharSelect";
	private static Logger _log = Logger.getLogger(C_NewCharSelect.class
			.getName());

	public C_NewCharSelect(byte[] decrypt, ClientThread client) {
		super(decrypt);
		client.sendPacket(new S_PacketBox(S_PacketBox.LOGOUT)); // 2.70C->3.0で追加
		client.CharReStart(true);
		if (client.getActiveChar() != null) {
			L1PcInstance pc = client.getActiveChar();
			_log.fine("Disconnect from: " + pc.getName());
			ClientThread.quitGame(pc);

			synchronized (pc) {
				pc.logout();
				client.setActiveChar(null);
			}
		} else {
			_log.fine("Disconnect Request from Account : "
					+ client.getAccountName());
		}
	}

	@Override
	public String getType() {
		return C_NEW_CHAR_SELECT;
	}
}
