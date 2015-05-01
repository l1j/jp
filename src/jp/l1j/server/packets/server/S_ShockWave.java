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

import java.util.logging.Logger;

import jp.l1j.server.codes.Opcodes;

public class S_ShockWave extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_ShockWave.class.getName());

	private static final String S_ShockWave = "[S] S_ShockWave";

	public S_ShockWave() {
		writeC(Opcodes.S_OPCODE_ACTIVESPELLS);
		writeC(0x53);
		writeC(0x02);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x01);
		writeC(0x1d);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_ShockWave;
	}
}