/**
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
package jp.l1j.server.packets.server;

import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;

public class S_SendLocation extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_SendLocation.class.getName());

	private static final String S_SEND_LOCATION = "[S] S_SendLocation";

	public S_SendLocation(int type, String senderName, int mapId, int x, int y, int msgId) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(0x6f);
		writeS(senderName);
		writeH(mapId);
		writeH(x);
		writeH(y);
		writeC(msgId); // メッセージID ex:senderNameがmsgId地域に位置情報を送りました
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_SEND_LOCATION;
	}
}