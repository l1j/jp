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

import jp.l1j.server.codes.Opcodes;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_Door extends ServerBasePacket {
	private static final String S_DOOR = "[S] S_Door";
	private byte[] _byte = null;

	private static final int PASS = 0;
	private static final int NOT_PASS = 1;

	public S_Door(int x, int y, int direction, boolean isPassable) {
		buildPacket(x, y, direction, isPassable);
	}

	private void buildPacket(int x, int y, int direction, boolean isPassable) {
		writeC(Opcodes.S_OPCODE_ATTRIBUTE);
		writeH(x);
		writeH(y);
		writeC(direction); // ドアの方向 0: ／ 1: ＼
		writeC(isPassable ? PASS : NOT_PASS);
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
		return S_DOOR;
	}
}
