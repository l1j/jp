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
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;

public class S_RetrievePledgeList extends ServerBasePacket {
	public S_RetrievePledgeList(int objid, L1PcInstance pc) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
		if (clan == null) {
			return;
		}

		if (clan.getWarehouseUsingChar() != 0
				&& clan.getWarehouseUsingChar() != pc.getId()) // 自キャラ以外がクラン倉庫使用中
		{
			pc.sendPackets(new S_ServerMessage(209)); // \f1他の血盟員が倉庫を使用中です。しばらく経ってから利用してください。
			return;
		}

		if (pc.getInventory().getSize() < 180) {
			int size = clan.getWarehouse().getSize();
			if (size > 0) {
				clan.setWarehouseUsingChar(pc.getId()); // クラン倉庫をロック
				writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
				writeD(objid);
				writeH(size);
				writeC(5); // 血盟倉庫
				for (Object itemObject : clan.getWarehouse().getItems()) {
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
				
				// TODO 3.53C start
				writeD(30); // アデナ30
				writeD(0x00000000);
				writeH(0x00);
				// TODO 3.53C end
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
