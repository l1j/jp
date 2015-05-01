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

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_LoginToServerOK extends ClientBasePacket {

	private static final String C_LOGIN_TO_SERVER_OK = "[C] C_LoginToServerOK";

	private static Logger _log = Logger.getLogger(C_LoginToServerOK.class
			.getName());

	public C_LoginToServerOK(byte[] decrypt, ClientThread client) {
		super(decrypt);

		int type = readC();
		int button = readC();

		L1PcInstance pc = client.getActiveChar();

		if (type == 255) { // 全体チャット && Whisper
			if (button == 95 || button == 127) {
				pc.setShowWorldChat(true); // open
				pc.setCanWhisper(true); // open
			} else if (button == 91 || button == 123) {
				pc.setShowWorldChat(true); // open
				pc.setCanWhisper(false); // close
			} else if (button == 94 || button == 126) {
				pc.setShowWorldChat(false); // close
				pc.setCanWhisper(true); // open
			} else if (button == 90 || button == 122) {
				pc.setShowWorldChat(false); // close
				pc.setCanWhisper(false); // close
			}
		} else if (type == 0) { // 全体チャット
			if (button == 0) { // close
				pc.setShowWorldChat(false);
			} else if (button == 1) { // open
				pc.setShowWorldChat(true);
			}
		} else if (type == 2) { // Whisper
			if (button == 0) { // close
				pc.setCanWhisper(false);
			} else if (button == 1) { // open
				pc.setCanWhisper(true);
			}
		} else if (type == 6) { // 商売チャット
			if (button == 0) { // close
				pc.setShowTradeChat(false);
			} else if (button == 1) { // open
				pc.setShowTradeChat(true);
			}
		} else if (type == 9) { // 血盟チャット
			if (button == 0) { // open
				pc.setShowClanChat(true);
			} else if (button == 1) { // close
				pc.setShowClanChat(false);
				}
		} else if (type == 10) { // パーティーチャット
			if (button == 0) { // close
				pc.setShowPartyChat(false);
			} else if (button == 1) { // open
				pc.setShowPartyChat(true);
			}
		}
	}

	@Override
	public String getType() {
		return C_LOGIN_TO_SERVER_OK;
	}
}
