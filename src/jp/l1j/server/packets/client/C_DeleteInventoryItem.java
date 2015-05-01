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
import jp.l1j.configure.Config;
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_DeleteInventoryItem extends ClientBasePacket {

	private static Logger _log = Logger.getLogger(C_DeleteInventoryItem.class
			.getName());
	private static final String C_DELETE_INVENTORY_ITEM = "[C] C_DeleteInventoryItem";

	public C_DeleteInventoryItem(byte[] decrypt, ClientThread client) {
		super(decrypt);
		int itemObjectId = readD();
		L1PcInstance pc = client.getActiveChar();
		L1ItemInstance item = pc.getInventory().getItem(itemObjectId);

		// 削除しようとしたアイテムがサーバー上に無い場合
		if (item == null) {
			return;
		}

		if (!item.getItem().isDeletable()) {
			// \f1削除できないアイテムや装備しているアイテムは捨てられません。
			pc.sendPackets(new S_ServerMessage(125));
			return;
		}

		Object[] petlist = pc.getPetList().values().toArray();
		for (Object petObject : petlist) {
			if (petObject instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petObject;
				if (item.getId() == pet.getItemObjId()) {
					// \f1%0は捨てたりまたは他人に讓ることができません。
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							.getName()));
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
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
			return;
		}

		if (Config.RECYCLE_SYSTEM) {
			pc.getInventory().recycleItem(pc, item); // ゴミをアデナに換金
		} else {
			pc.getInventory().removeItem(item, item.getCount());
		}
		pc.updateLight();
	}

	@Override
	public String getType() {
		return C_DELETE_INVENTORY_ITEM;
	}
}
