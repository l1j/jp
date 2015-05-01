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
// ServerBasePacket

public class S_SkillIconWindShackle extends ServerBasePacket {

	public S_SkillIconWindShackle(int objectId, int time) {
		int buffTime = (time / 4); // なぜか4倍されるため4で割っておく
		writeC(Opcodes.S_OPCODE_SKILLICONGFX);
		writeC(0x2c);
		writeD(objectId);
		writeH(buffTime);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
