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
// ServerBasePacket

public class S_ItemStatus extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(
			S_ItemStatus.class.getName());
	private static final String S_ITEM_STATUS = "[S] S_ItemStatus";

	/**
	 * アイテムの名前、状態、特性、重量などの表示を変更する
	 */
	public S_ItemStatus(L1ItemInstance item) {
		writeC(Opcodes.S_OPCODE_ITEMSTATUS);
		writeD(item.getId());
		writeS(item.getViewName());
		writeD(item.getCount());
		if (!item.isIdentified()) {
			// 未鑑定の場合ステータスを送る必要はない
			writeC(0);
		} else {
			byte[] status = item.getStatusBytes();
			writeC(status.length);
			for (byte b : status) {
				writeC(b);
			}
		}
	}

	@Override
	public byte[] getContent() {
		return _bao.toByteArray();
	}

	@Override
	public String getType() {
		return S_ITEM_STATUS;
	}
}
