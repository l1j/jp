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

public class S_DragonGate extends ServerBasePacket {
	private static final String S_DRAGON_GATE = "[S] S_DragonGate";

	private byte[] _byte = null;

	public S_DragonGate(L1PcInstance pc, boolean[] i) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(0x66); // = 102
		writeD(pc.getId());
		// true クリック可，false クリック不可
		writeC(i[0] ? 1 : 0); // アンタラス
		writeC(i[1] ? 1 : 0); // パプリオン
		writeC(i[2] ? 1 : 0); // リンドビオル
		writeC(i[3] ? 1 : 0); // ヴァラカス
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
		return S_DRAGON_GATE;
	}
}
