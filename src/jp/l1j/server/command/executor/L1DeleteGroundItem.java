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

package jp.l1j.server.command.executor;

import java.util.ArrayList;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.PetTable;
import jp.l1j.server.datatables.SpawnFurnitureTable;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1FurnitureInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;

public class L1DeleteGroundItem implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1DeleteGroundItem.class.getName());

	private L1DeleteGroundItem() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1DeleteGroundItem();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		for (L1Object l1object : L1World.getInstance().getObject()) {
			if (l1object instanceof L1ItemInstance) {
				L1ItemInstance l1iteminstance = (L1ItemInstance) l1object;
				if (l1iteminstance.getX() == 0 && l1iteminstance.getY() == 0) {
					// 地面上のアイテムではなく、誰かの所有物
					continue;
				}

				ArrayList<L1PcInstance> players =
						L1World.getInstance().getVisiblePlayer(l1iteminstance, 0);
				if (0 == players.size()) {
					L1Inventory groundInventory =
							L1World.getInstance().getInventory(l1iteminstance.getX(),
							l1iteminstance.getY(), l1iteminstance.getMapId());
					int itemId = l1iteminstance.getItem().getItemId();
					if (itemId == 40314 || itemId == 40316) { // ペットのアミュレット
						PetTable.getInstance().deletePet(l1iteminstance.getId());
					} else if (itemId >= 41383 && itemId <= 41400) { // 家具
						if (l1object instanceof L1FurnitureInstance) {
							L1FurnitureInstance furniture = (L1FurnitureInstance) l1object;
							if (furniture.getItemObjId() == l1iteminstance.getId()) {
								// 既に引き出している家具
								SpawnFurnitureTable.getInstance().deleteFurniture(furniture);
							}
						}
					}
					groundInventory.deleteItem(l1iteminstance);
					L1World.getInstance().removeVisibleObject(l1iteminstance);
					L1World.getInstance().removeObject(l1iteminstance);
				}
			}
		}
		L1World.getInstance().broadcastServerMessage(I18N_REMOVED_THE_ITEMS_ON_THE_WORLD_MAP);
		// GMがワールドマップ上のアイテムを削除しました。
	}
}
