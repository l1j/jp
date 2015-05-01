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
import jp.l1j.server.model.L1Buddy;

public class S_Buddy extends ServerBasePacket {
	private static final String _S_Buddy = "[S] _S_Buddy";
	private static final String _HTMLID = "buddy";

	private byte[] _byte = null;

	public S_Buddy(int objId, L1Buddy buddy) {
		buildPacket(objId, buddy);
	}

	private void buildPacket(int objId, L1Buddy buddy) {
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(objId);
		writeS(_HTMLID);
		writeH(0x02);
		writeH(0x02);

		writeS(buddy.getBuddyListString());
		writeS(buddy.getOnlineBuddyListString());
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return _S_Buddy;
	}
}
