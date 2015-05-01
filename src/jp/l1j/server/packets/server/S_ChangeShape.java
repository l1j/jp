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
// ServerBasePacket, S_SkillIconGFX, S_CharVisualUpdate

public class S_ChangeShape extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ChangeShape(int objId, int polyId) {
		buildPacket(objId, polyId, false);
	}

	public S_ChangeShape(int objId, int polyId, boolean weaponTakeoff) {
		buildPacket(objId, polyId, weaponTakeoff);
	}

	private void buildPacket(int objId, int polyId, boolean weaponTakeoff) {
		writeC(Opcodes.S_OPCODE_POLY);
		writeD(objId);
		writeH(polyId);
		// 何故29なのか不明
		writeH(weaponTakeoff ? 0 : 29);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}
}
