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

import java.io.IOException;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1TrapInstance;

public class S_Trap extends ServerBasePacket {
	public S_Trap(L1TrapInstance trap, String name) {

		writeC(Opcodes.S_OPCODE_DROPITEM);
		writeH(trap.getX());
		writeH(trap.getY());
		writeD(trap.getId());
		writeH(7); // adena
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeD(0);
		writeC(0);
		writeC(0);
		writeS(name);
		writeC(0);
		writeD(0);
		writeD(0);
		writeC(255);
		writeC(0);
		writeC(0);
		writeC(0);
		writeH(65535);
		// writeD(0x401799a);
		writeD(0);
		writeC(8);
		writeC(0);
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}
}
