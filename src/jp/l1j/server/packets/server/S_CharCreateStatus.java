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

public class S_CharCreateStatus extends ServerBasePacket {
	private static final String S_CHAR_CREATE_STATUS = "[S] S_CharCreateStatus";

	public static final int REASON_OK = 0x02;

	public static final int REASON_ALREADY_EXSISTS = 0x06;

	public static final int REASON_INVALID_NAME = 0x09;

	public static final int REASON_WRONG_AMOUNT = 0x15;

	public S_CharCreateStatus(int reason) {
		writeC(Opcodes.S_OPCODE_NEWCHARWRONG);
		writeC(reason);
		writeD(0x00000000);
		writeD(0x0000);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_CHAR_CREATE_STATUS;
	}
}
