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

import java.io.IOException;
import jp.l1j.server.codes.Opcodes;

public class S_HowManyMake extends ServerBasePacket {
	public S_HowManyMake(int objId, int max, String htmlId) {
		writeC(Opcodes.S_OPCODE_INPUTAMOUNT);
		writeD(objId);
		writeD(0); // ?
		writeD(0); // スピンコントロールの初期価格
		writeD(0); // 価格の下限
		writeD(max); // 価格の上限
		writeH(0); // ?
		writeS("request");
		writeS(htmlId);
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}
}
