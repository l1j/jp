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

import java.io.IOException;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1NpcInstance;

public class S_HowManyKey extends ServerBasePacket {

	/*
	 * 【Server】 id:14 size:40 time:1300606757968
	 *  0000	0e cc 1e 00 00 2c 01 00 00 01 00 00 00 01 00 00    .....,..........
	 *  0010	00 08 00 00 00 00 00 69 6e 6e 32 00 00 02 00 24    .......inn2....$
	 *  0020	39 35 35 00 33 30 30 00                            955.300.
	 */

	public S_HowManyKey(L1NpcInstance npc, int price, int min, int max, String htmlId) {
		writeC(Opcodes.S_OPCODE_INPUTAMOUNT);
		writeD(npc.getId());
		writeD(price); // 金額
		writeD(min); // スタート数量
		writeD(min); // スタート数量
		writeD(max); // 購買最大数量
		writeH(0); // ?
		writeS(htmlId); // npc会話htmlId
		writeH(1); // ?
		writeH(0x02); // writeS 数量
		writeS(npc.getName()); // NPC名称
		writeS(String.valueOf(price)); // 表示金額
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}
}