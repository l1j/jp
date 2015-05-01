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
import jp.l1j.server.model.instance.L1MonsterInstance;

public class S_MoveNpcPacket extends ServerBasePacket {
	private static final String _S__1F_S_MOVENPCPACKET = "[S] S_MoveNpcPacket";
	private static Logger _log = Logger.getLogger(S_MoveNpcPacket.class
			.getName());

	public S_MoveNpcPacket(L1MonsterInstance npc, int x, int y, int heading) {
		// npc.setMoving(true);

		writeC(Opcodes.S_OPCODE_MOVEOBJECT);
		writeD(npc.getId());
		writeH(x);
		writeH(y);
		writeC(heading);
		writeC(0x81);
		writeD(0x00000000);

		// npc.setMoving(false);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return _S__1F_S_MOVENPCPACKET;
	}
}
