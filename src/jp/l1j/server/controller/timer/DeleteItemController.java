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

package jp.l1j.server.controller.timer;

import java.util.List;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.model.L1HouseLocation;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SystemMessage;

public class DeleteItemController {
	private DeleteTimer _deleteTimer;

	private static Logger _log = Logger.getLogger(DeleteItemController.class.getName());

	public DeleteItemController() {
	}

	private class DeleteTimer implements Runnable {
		public DeleteTimer() {
		}

		@Override
		public void run() {
			int time = Config.ALT_ITEM_DELETION_TIME * 60 * 1000 - 10 * 1000;
			for (;;) {
				try {
					Thread.sleep(time);
				} catch (Exception exception) {
					_log.warning("L1DeleteItemOnGround error: " + exception);
					break;
				}
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(String.format(I18N_REMOVE_ITEMS_AFTER_FEW_SECONDS, 10)));
				// ワールドマップ上のアイテムが、%d秒後に削除されます。
				try {
					Thread.sleep(10000);
				} catch (Exception exception) {
					_log.warning("L1DeleteItemOnGround error: " + exception);
					break;
				}
				deleteItem();
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(I18N_REMOVED_ITEMS_ON_WORLD_MAP));
				// ワールドマップ上のアイテムが削除されました。
			}
		}
	}

	public void initialize() {
		if (!Config.ALT_ITEM_DELETION_TYPE.equalsIgnoreCase("auto")) {
			return;
		}
		_deleteTimer = new DeleteTimer();
		GeneralThreadPool.getInstance().execute(_deleteTimer); // タイマー開始
	}

	private void deleteItem() {
		int numOfDeleted = 0;
		for (L1Object obj : L1World.getInstance().getObject()) {
			if (!(obj instanceof L1ItemInstance)) {
				continue;
			}
			L1ItemInstance item = (L1ItemInstance) obj;
			if (item.getX() == 0 && item.getY() == 0) { // 地面上のアイテムではなく、誰かの所有物
				continue;
			}
			if (item.getItem().getItemId() == 40515) { // 精霊の石
				continue;
			}
			if (L1HouseLocation.isInHouse(item.getX(), item.getY(), item
					.getMapId())) { // アジト内
				continue;
			}
			List<L1PcInstance> players = L1World.getInstance().getVisiblePlayer(item, Config.ALT_ITEM_DELETION_RANGE);
			if (players.isEmpty()) { // 指定範囲内にプレイヤーが居なければ削除
				L1Inventory groundInventory = L1World.getInstance().getInventory(item.getX(), item.getY(), item.getMapId());
				groundInventory.removeItem(item);
				numOfDeleted++;
			}
		}
		_log.fine("Automatic deletion of items on the world map: " + numOfDeleted + "items");
	}
}
