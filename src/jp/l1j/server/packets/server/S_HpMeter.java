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
import jp.l1j.server.model.L1Character;

public class S_HpMeter extends ServerBasePacket {
	private static final String _typeString = "[S] S_HpMeter";

	private byte[] _byte = null;

	public S_HpMeter(int objId, int hpRatio) {
		buildPacket(objId, hpRatio);
	}

	public S_HpMeter(L1Character cha) {
		int objId = cha.getId();
		int hpRatio = 100;
		if (0 < cha.getMaxHp()) {
			hpRatio = 100 * cha.getCurrentHp() / cha.getMaxHp();
		}

		buildPacket(objId, hpRatio);
	}

	private void buildPacket(int objId, int hpRatio) {
		writeC(Opcodes.S_OPCODE_HPMETER);
		writeD(objId);
		writeC(hpRatio);
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
		return _typeString;
	}
}
