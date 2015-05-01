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
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;

public class S_RetrieveElfList extends ServerBasePacket {
	public S_RetrieveElfList(int objid, L1PcInstance pc) {
		if (pc.getInventory().getSize() < 180) {
			int size = pc.getElfWarehouseInventory().getSize();
			if (size > 0) {
				writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
				writeD(objid);
				writeH(size);
				writeC(9); // エルフ倉庫
				for (Object itemObject : pc.getElfWarehouseInventory().getItems()) {
					L1ItemInstance item = (L1ItemInstance) itemObject;
					writeD(item.getId());
					writeC(0);
					writeH(item.getGfxId());
					writeC(item.getStatusForPacket());
					writeD(item.getCount());
					writeC(item.isIdentified() ? 1 : 0);
					writeS(item.getViewName());
				}
				// writeH(0x001e); // TODO 3.52C
			} else {
				pc.sendPackets(new S_ServerMessage(1625));
			}
		} else {
			pc.sendPackets(new S_ServerMessage(263)); // \f1一人のキャラクターが持って歩けるアイテムは最大180個までです。
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}

}
