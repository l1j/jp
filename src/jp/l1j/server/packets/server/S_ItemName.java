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

import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1ItemInstance;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket, S_SendInvOnLogin

public class S_ItemName extends ServerBasePacket {

	private static final String S_ITEM_NAME = "[S] S_ItemName";

	private static Logger _log = Logger.getLogger(S_ItemName.class
			.getName());

	/**
	 * アイテムの名前を変更する。装備や強化状態が変わったときに送る。
	 */
	public S_ItemName(L1ItemInstance item) {
		if (item == null) {
			return;
		}
		// jumpを見る限り、このOpcodeはアイテム名を更新させる目的だけに使用される模様（装備後やOE後専用？）
		// 後に何かデータを続けて送っても全て無視されてしまう
		writeC(Opcodes.S_OPCODE_ITEMNAME);
		writeD(item.getId());
		writeS(item.getViewName());
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_ITEM_NAME;
	}
}
