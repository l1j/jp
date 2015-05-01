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
import jp.l1j.server.model.L1NpcTalkData;

public class S_NpcTalkActionTpUrl extends ServerBasePacket {
	private static final String _S__25_TalkReturnAction = "[S] S_NpcTalkActionTpUrl";
	private byte[] _byte = null;

	public S_NpcTalkActionTpUrl(L1NpcTalkData cha, Object[] prices, int objid) {
		buildPacket(cha, prices, objid);
	}

	private void buildPacket(L1NpcTalkData npc, Object[] prices, int objid) {
		String htmlid = "";
		htmlid = npc.getTeleportURL();
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(objid);
		writeS(htmlid);
		writeH(0x01); // 不明
		writeH(prices.length); // 引数の数

		for (Object price : prices) {
			writeS(String.valueOf(((Integer) price).intValue()));
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return _S__25_TalkReturnAction;
	}
}
