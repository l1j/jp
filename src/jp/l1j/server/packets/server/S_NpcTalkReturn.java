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
import jp.l1j.server.model.npc.L1NpcHtml;

public class S_NpcTalkReturn extends ServerBasePacket {
	private static final String _S__25_TalkReturn = "[S] _S__25_TalkReturn";
	private byte[] _byte = null;

	public S_NpcTalkReturn(L1NpcTalkData npc, int objid, int action,
			String[] data) {

		String htmlid = "";

		if (action == 1) {
			htmlid = npc.getNormalAction();
		} else if (action == 2) {
			htmlid = npc.getChaoticAction();
		} else {
			throw new IllegalArgumentException();
		}

		buildPacket(objid, htmlid, data);
	}

	public S_NpcTalkReturn(L1NpcTalkData npc, int objid, int action) {
		this(npc, objid, action, null);
	}

	public S_NpcTalkReturn(int objid, String htmlid, String[] data) {
		buildPacket(objid, htmlid, data);
	}

	public S_NpcTalkReturn(int objid, String htmlid) {
		buildPacket(objid, htmlid, null);
	}

	public S_NpcTalkReturn(int objid, L1NpcHtml html) {
		buildPacket(objid, html.getName(), html.getArgs());
	}

	// TODO テレポーター金額表示 start
	public S_NpcTalkReturn(int objid, L1NpcHtml html, String[] data) {
		buildPacket(objid, html.getName(), data);
	}
	// TODO テレポーター金額表示 end

	private void buildPacket(int objid, String htmlid, String[] data) {

		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(objid);
		writeS(htmlid);
		if (data != null && 1 <= data.length) {
			writeH(0x01); // 不明バイト 分かる人居たら修正願います
			writeH(data.length); // 引数の数
			for (String datum : data) {
				writeS(datum);
			}
		} else {
			writeH(0x00);
			writeH(0x00);
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
		return _S__25_TalkReturn;
	}
}
