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

public class S_TradeAddItem extends ServerBasePacket {
	private static Logger _log = Logger.getLogger(S_TradeAddItem.class
			.getName());

	private static final String S_TRADE_ADD_ITEM = "[S] S_TradeAddItem";

	public S_TradeAddItem(L1ItemInstance item, int count, int type) {
		writeC(Opcodes.S_OPCODE_TRADEADDITEM);
		writeC(type); // 0:トレードウィンドウ上段 1:トレードウィンドウ下段
		writeH(item.getItem().getGfxId());
		writeS(item.getNumberedViewName(count));
		// 0:祝福  1:通常 2:呪い 3:未鑑定
		// 128:祝福&封印 129:&封印 130:呪い&封印 131:未鑑定&封印
		writeC(item.getStatusForPacket());
		writeC(0x00);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_TRADE_ADD_ITEM;
	}
}
