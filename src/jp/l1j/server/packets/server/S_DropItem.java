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

public class S_DropItem extends ServerBasePacket {

	private static final String _S__OB_DropItem = "[S] S_DropItem";
	private static Logger _log = Logger.getLogger(S_DropItem.class.getName());

	private byte[] _byte = null;

	public S_DropItem(L1ItemInstance item) {
		buildPacket(item);
	}

	private void buildPacket(L1ItemInstance item) {
		// int addbyte = 0;
		// int addbyte1 = 1;
		// int addbyte2 = 13;
		// int setting = 4;

		String itemName = item.getItem().getUnidentifiedNameId();
		// 鑑定
		int isId = item.isIdentified() ? 1 : 0;
		if (isId == 1) {
			itemName = item.getItem().getIdentifiedNameId();
		}
		writeC(Opcodes.S_OPCODE_DROPITEM);
		writeH(item.getX());
		writeH(item.getY());
		writeD(item.getId());
		writeH(item.getItem().getGroundGfxId());
		writeC(0);
		writeC(0);
		if (item.isNowLighting()) {
			writeC(item.getItem().getLightRange());
		} else {
			writeC(0);
		}
		writeC(0);
		writeD(item.getCount());
		writeC(0);
		writeC(0);
		if (item.getCount() > 1) {
			if (item.getItem().getItemId() == 40312 && item.getKeyId() != 0) { // 宿屋のキー
				writeS(itemName + item.getInnKeyName() + " (" + item.getCount() + ")");
			} else {
				writeS(itemName + " (" + item.getCount() + ")");
			}
		} else {
			int itemId = item.getItem().getItemId();
			if (itemId == 20383 && isId == 1) { // 騎馬用ヘルム
				writeS(item.getItem().getName() + " [" + item
						.getChargeCount() + "]");
			} else if ((itemId == 40006 || itemId == 40007
					|| itemId == 40008 || itemId == 40009
					|| itemId == 140006 || itemId == 140008) && isId == 1) { // ワンド類
				writeS(item.getItem().getName() + " (" + item
						.getChargeCount() + ")");
			} else if (item.getItem().getLightRange() != 0 && item
					.isNowLighting()) {
				writeS(item.getItem().getName() + " ($10)");
			} else if (item.getItem().getItemId() == 40312 && item.getKeyId() != 0) { // 宿屋のキー
				writeS(itemName + item.getInnKeyName());
			} else {
				writeS(item.getItem().getName());
			}
		}
		writeC(0);
		writeD(0);
		writeD(0);
		writeC(255);
		writeC(0);
		writeC(0);
		writeC(0);
		writeH(65535);
		// writeD(0x401799a);
		writeD(0);
		writeC(8);
		writeC(0);
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
		return _S__OB_DropItem;
	}

}