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
import jp.l1j.server.model.instance.L1PcInstance;

public class S_CharVisualUpdate extends ServerBasePacket {
	private static final String _S__0B_S_CharVisualUpdate = "[C] S_CharVisualUpdate";
	private static Logger _log = Logger.getLogger(S_CharVisualUpdate.class
			.getName());
	private final L1PcInstance _cha;

	public S_CharVisualUpdate(L1PcInstance cha) {
		_cha = cha;

		writeC(Opcodes.S_OPCODE_CHARVISUALUPDATE);
		writeD(_cha.getId());
		writeC(_cha.getCurrentWeapon());
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return _S__0B_S_CharVisualUpdate;
	}
}
