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

public class S_AttackPacket extends ServerBasePacket {
	private static final String S_ATTACK_PACKET = "[S] S_AttackPacket";
	private static Logger _log = Logger.getLogger(S_AttackPacket.class
			.getName());

	private byte[] _byte = null;

	public S_AttackPacket(L1PcInstance pc, int objid, int type) {
		buildpacket(pc, objid, type);
	}

	private void buildpacket(L1PcInstance pc, int objid, int type) {
		writeC(Opcodes.S_OPCODE_ATTACKPACKET);
		writeC(type);
		writeD(pc.getId());
		writeD(objid);
		// writeC(0x01); // damage 3.0c
		writeH(0x01); // damage 3.3c
		writeC(pc.getHeading());
		writeH(0x0000); // target x
		writeH(0x0000); // target y
		writeC(0x00); // 0x00:none 0x04:Claw 0x08:CounterMirror
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
		return S_ATTACK_PACKET;
	}
}