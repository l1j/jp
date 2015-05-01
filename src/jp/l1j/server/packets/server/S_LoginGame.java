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

/**
 * 3.0cのUnKnown1から改名
 * 正式にLoginGameと命名
 */

public class S_LoginGame extends ServerBasePacket {
	public S_LoginGame() {
		writeC(Opcodes.S_OPCODE_LOGINTOGAME);
		writeC(0x03);
		writeC(0x15);;//TODO 3.63封包變更
		writeC(0x8b);;//TODO 3.63封包變更
		writeC(0x7b);;//TODO 3.63封包變更
		writeC(0x94);;//TODO 3.63封包變更
		writeC(0xf0);;//TODO 3.63封包變更
		writeC(0x2f);;//TODO 3.63封包變更
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
