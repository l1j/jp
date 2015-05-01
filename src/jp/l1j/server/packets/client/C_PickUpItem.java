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
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.ClientThread;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.item.L1ItemId;
import jp.l1j.server.packets.server.S_AttackPacket;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SystemMessage;

public class C_PickUpItem extends ClientBasePacket {
	private static final String C_PICK_UP_ITEM = "[C] C_PickUpItem";
	
	private static Logger _log = Logger.getLogger(C_PickUpItem.class.getName());

	public C_PickUpItem(byte decrypt[], ClientThread client) throws Exception {
		super(decrypt);
		int x = readH();
		int y = readH();
		int objectId = readD();
		int pickupCount = readD();
		L1PcInstance pc = client.getActiveChar();
		if (pc.isDead() || pc.isGhost() || objectId == pc.getId()) {
			return;
		}
		if (pc.isInvisble()) { // インビジ状態
			return;
		}
		if (pc.isInvisDelay()) { // インビジディレイ状態
			return;
		}
		L1Inventory groundInventory = L1World.getInstance().getInventory(x, y, pc.getMapId());
		L1Object object = groundInventory.getItem(objectId);
		if (object != null && !pc.isDead()) {
			L1ItemInstance item = (L1ItemInstance) object;
			if (item.getItemOwnerId() != 0 && pc.getId() != item.getItemOwnerId()) {
				pc.sendPackets(new S_ServerMessage(623)); // アイテムが拾えませんでした。
				return;
			}
			if (pc.getLocation().getTileLineDistance(item.getLocation()) > 3) {
				return;
			}
			if (item.getItem().getItemId() == L1ItemId.ADENA) {
				L1ItemInstance inventoryItem = pc.getInventory().findItemId(L1ItemId.ADENA);
				int inventoryItemCount = 0;
				if (inventoryItem != null) {
					inventoryItemCount = inventoryItem.getCount();
				}
				// 拾った後に2Gを超過しないようにチェック
				if ((long) inventoryItemCount + (long) pickupCount > 2000000000L) {
					pc.sendPackets(new S_SystemMessage(I18N_CAN_NOT_PICK_UP_ADENA));
					return;
				}
			}
			if (pc.getInventory().checkAddItem( // 容量重量確認及びメッセージ送信
					item, pickupCount) == L1Inventory.OK) {
				if (item.getX() != 0 && item.getY() != 0) { // ワールドマップ上のアイテム
					groundInventory.tradeItem(item, pickupCount, pc.getInventory());
					pc.updateLight();
					pc.sendPackets(new S_AttackPacket(pc, objectId, ActionCodes.ACTION_Pickup));
					if (!pc.isGmInvis()) {
						pc.broadcastPacket(new S_AttackPacket(pc, objectId, ActionCodes.ACTION_Pickup));
					}
				}
			}
		}
	}

	@Override
	public String getType() {
		return C_PICK_UP_ITEM;
	}
}