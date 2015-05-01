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

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_AttackMissPacket extends ServerBasePacket {

	private static final String _S__OB_ATTACKMISSPACKET = "[S] S_AttackMissPacket";

	private byte[] _byte = null;

	public S_AttackMissPacket(L1Character attacker, int targetId) {
		writeC(Opcodes.S_OPCODE_ATTACKPACKET);
		writeC(1);
		writeD(attacker.getId());
		writeD(targetId);
		writeH(0);
		writeC(attacker.getHeading());
		writeD(0);
		writeC(0);
	}

	public S_AttackMissPacket(L1Character attacker, int targetId, int actId) {
		writeC(Opcodes.S_OPCODE_ATTACKPACKET);
		writeC(actId);
		writeD(attacker.getId());
		writeD(targetId);
		writeH(0);
		writeC(attacker.getHeading());
		writeD(0);
		writeC(0);
	}

	public S_AttackMissPacket(int attackId, int targetId) {
		writeC(Opcodes.S_OPCODE_ATTACKPACKET);
		writeC(1);
		writeD(attackId);
		writeD(targetId);
		writeH(0);
		writeC(0);
		writeD(0);
	}

	public S_AttackMissPacket(int attackId, int targetId, int actId) {
		writeC(Opcodes.S_OPCODE_ATTACKPACKET);
		writeC(actId);
		writeD(attackId);
		writeD(targetId);
		writeH(0);
		writeC(0);
		writeD(0);
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
		return _S__OB_ATTACKMISSPACKET;
	}
}
