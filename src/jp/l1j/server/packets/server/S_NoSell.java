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
package jp.l1j.server.packets.server;

import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1NpcInstance;

public class S_NoSell extends ServerBasePacket {
	private static final String _S__25_NoSell = "[S] _S__25_NoSell";

	private byte[] _byte = null;

	public S_NoSell(L1NpcInstance npc) {
		buildPacket(npc);
	}

	private void buildPacket(L1NpcInstance npc) {
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(npc.getId());
		writeS("nosell");
		writeC(1);
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
		return _S__25_NoSell;
	}
}
