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

public class S_MpUpdate extends ServerBasePacket {
	public S_MpUpdate(int currentmp, int maxmp) {
		writeC(Opcodes.S_OPCODE_MPUPDATE);

		if (currentmp < 0) {
			writeH(0);
		} else if (currentmp > 32767) {
			writeH(32767);
		} else {
			writeH(currentmp);
		}

		if (maxmp < 1) {
			writeH(1);
		} else if (maxmp > 32767) {
			writeH(32767);
		} else {
			writeH(maxmp);
		}

		// writeH(currentmp);
		// writeH(maxmp);
		// writeC(0);
		// writeD(GameTimeController.getInstance().getGameTime());
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
