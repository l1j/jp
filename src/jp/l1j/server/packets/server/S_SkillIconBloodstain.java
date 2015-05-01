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

public class S_SkillIconBloodstain extends ServerBasePacket {
	private static final String S_SKILL_ICON_BLOODSTAIN = "[S] S_SkillIconBloodstain";

	public S_SkillIconBloodstain(int i, int j) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(0x64);
		writeC(i); // 82:アンタラスの血痕 85:パプリオンの血痕 88:リンドビオルの血痕　91:ヴァラカスの血痕
		writeH(j); // 分
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_SKILL_ICON_BLOODSTAIN;
	}
}
