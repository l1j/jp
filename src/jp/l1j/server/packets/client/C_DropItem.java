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

package jp.l1j.server.packets.client;

import java.util.logging.Logger;
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.instance.L1DollInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.L1ItemCheck;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_ServerMessage;

public class C_DropItem extends ClientBasePacket {
	private static Logger _log = Logger.getLogger(C_DropItem.class.getName());
	private static final String C_DROP_ITEM = "[C] C_DropItem";

	public C_DropItem(byte[] decrypt, ClientThread client) throws Exception {
		super(decrypt);
		int x = readH();
		int y = readH();
		int objectId = readD();
		int count = readD();

		if (count > 0x77359400 || count < 0) {
			count = 0;
		}
		
		L1PcInstance pc = client.getActiveChar();
		if (pc.isGhost()) {
			return;
		} else if (pc.getMapId() > 10000) { // 宿屋内判定
			pc.sendPackets(new S_ServerMessage(77)); // \f1これ以上ものを置く場所がありません。
			return;
		}

		L1ItemInstance item = pc.getInventory().getItem(objectId);
		if (item != null) {
			if (!item.getItem().isTradable()) {
				// \f1%0は捨てたりまたは他人に讓ることができません。
				pc.sendPackets(new S_ServerMessage(210, item.getItem()
						.getName()));
				return;
			}

			Object[] petlist = pc.getPetList().values().toArray();
			for (Object petObject : petlist) {
				if (petObject instanceof L1PetInstance) {
					L1ItemCheck checkItem = new L1ItemCheck();
					if (checkItem.ItemCheck(item, pc)) {
						return;
					}
					
					L1PetInstance pet = (L1PetInstance) petObject;
					if (item.getId() == pet.getItemObjId()) {
						// \f1%0は捨てたりまたは他人に讓ることができません。
						pc.sendPackets(new S_ServerMessage(210, item.getItem()
								.getName()));
						return;
					}
				}
			}
			// マジックドール使用中判定
			Object[] dollList = pc.getDollList().values().toArray();
			for (Object dollObject : dollList) {
				if (dollObject instanceof L1DollInstance) {
					L1DollInstance doll = (L1DollInstance) dollObject;
					if (doll.getItemObjId() == item.getId()) {
						pc.sendPackets(new S_ServerMessage(1181)); // 該当のマジックドールは現在使用中です。
						return;
					}
				}
			}

			if (item.isEquipped()) {
				// \f1削除できないアイテムや装備しているアイテムは捨てられません。
				pc.sendPackets(new S_ServerMessage(125));
				return;
			}
			if (item.isSealed()) { // 封印された装備
				// \f1%0は捨てたりまたは他人に讓ることができません。
				pc.sendPackets(new S_ServerMessage(210, item.getItem()
						.getName()));
				return;
			}

			pc.getInventory().tradeItem(item, count,
					L1World.getInstance().getInventory(x, y, pc.getMapId()));
			pc.updateLight();
		}
	}

	@Override
	public String getType() {
		return C_DROP_ITEM;
	}
}
