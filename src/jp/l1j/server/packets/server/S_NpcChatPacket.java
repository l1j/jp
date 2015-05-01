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
import jp.l1j.server.model.instance.L1NpcInstance;

public class S_NpcChatPacket extends ServerBasePacket {
	private static final String S_NPC_CHAT_PACKET = "[S] S_NpcChatPacket";

	private static Logger _log = Logger.getLogger(S_NpcChatPacket.class
			.getName());

	private byte[] _byte = null;

	public S_NpcChatPacket(L1NpcInstance npc, String chat, int type) {
		buildPacket(npc, chat, type);
	}

	private void buildPacket(L1NpcInstance npc, String chat, int type) {
		switch (type) {
		case 0: // normal chat
			writeC(Opcodes.S_OPCODE_NPCSHOUT); // Key is 16 , can use
												// desc-?.tbl
			writeC(type); // Color
			writeD(npc.getId());
			writeS(npc.getName() + ": " + chat);
			break;

		case 2: // shout
			writeC(Opcodes.S_OPCODE_NPCSHOUT); // Key is 16 , can use
												// desc-?.tbl
			writeC(type); // Color
			writeD(npc.getId());
			writeS("<" + npc.getName() + "> " + chat);
			break;

		case 3: // world chat
			writeC(Opcodes.S_OPCODE_NPCSHOUT);
			writeC(type); // XXX 白色になる
			writeD(npc.getId());
			writeS("[" + npc.getName() + "] " + chat);
			break;

		default:
			break;
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_NPC_CHAT_PACKET;
	}
}
