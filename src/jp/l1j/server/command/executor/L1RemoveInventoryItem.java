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

import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;

public class L1RemoveInventoryItem implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1AllBuff.class.getName());

	private L1RemoveInventoryItem() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1RemoveInventoryItem();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		for(L1ItemInstance item : pc.getInventory().getItems()){
			if (item == null) { // 削除しようとしたアイテムが無い場合
				continue;
			}
			
			if (!item.getItem().isDeletable()) { // 削除できないアイテム
				continue;
			}

			if (item.isEquipped()) { // 装備しているアイテム
				continue;
			}
			
			if (item.isSealed()) { // 封印されたアイテム
				continue;
			}
			
			boolean isPetItem = false;
			Object[] petlist = pc.getPetList().values().toArray();
			for (Object petObject : petlist) {
				if (petObject instanceof L1PetInstance) {
					L1PetInstance pet = (L1PetInstance) petObject;
					if (item.getId() == pet.getItemObjId()) {
						isPetItem = true;
						break;
					}
				}
			}
			if (isPetItem) { // ペットアイテム
				continue;
			}
						
			if (Config.RECYCLE_SYSTEM) {
				pc.getInventory().recycleItem(pc, item); // ゴミをアデナに換金
			} else {
				pc.getInventory().removeItem(item, item.getCount());
			}
		}
		pc.updateLight();
	}
}
