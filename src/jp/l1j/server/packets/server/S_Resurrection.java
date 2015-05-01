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
import jp.l1j.server.model.instance.L1PcInstance;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_Resurrection extends ServerBasePacket {

	public S_Resurrection(L1PcInstance target, L1PcInstance use, int type) {
		writeC(Opcodes.S_OPCODE_RESURRECTION);
		writeD(target.getId());
		writeC(type);
		writeD(use.getId());
		writeD(target.getClassId());
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return "[S] S_Resurrection";
	}

	private static final String _S__FF_RESURRECTION = "[S] S_Resurrection";
}
