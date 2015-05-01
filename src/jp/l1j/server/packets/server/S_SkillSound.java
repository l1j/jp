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

public class S_SkillSound extends ServerBasePacket {
	private static final String S_SKILL_SOUND = "[S] S_SkillSound";

	private byte[] _byte = null;

	public S_SkillSound(int objid, int gfxid, int aid) {

		buildPacket(objid, gfxid, aid);
	}

	public S_SkillSound(int objid, int gfxid) {
		buildPacket(objid, gfxid, 0);
	}

	private void buildPacket(int objid, int gfxid, int aid) {
		// aidは使われていない
		writeC(Opcodes.S_OPCODE_SKILLSOUNDGFX);
		writeD(objid);
		writeH(gfxid);
		writeH(0);
		writeD(0x00000000);
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
		return S_SKILL_SOUND;
	}
}
