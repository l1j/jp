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
package jp.l1j.server.model.instance;

import java.util.logging.Logger;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.L1HauntedHouse;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.inventory.L1Inventory;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_RemoveObject;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1Npc;

public class L1FieldObjectInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1FieldObjectInstance.class.getName());
	
	public L1FieldObjectInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance pc) {
		if (getNpcTemplate().getNpcId() == 81171) { // おばけ屋敷のゴールの炎
			if (L1HauntedHouse.getInstance().getHauntedHouseStatus() ==
					L1HauntedHouse.STATUS_PLAYING) {
				int winnersCount = L1HauntedHouse.getInstance().
						getWinnersCount();
				int goalCount = L1HauntedHouse.getInstance().getGoalCount();
				if (winnersCount == goalCount + 1) {
					L1ItemInstance item = ItemTable.getInstance()
							.createItem(49280); // 勇者のパンプキン袋(銅)
					int count = 1;
					if (item != null) {
						if (pc.getInventory().checkAddItem(item, count) ==
								L1Inventory.OK) {
							item.setCount(count);
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(403, item
									.getLogName())); // %0を手に入れました。
						}
					}
					L1HauntedHouse.getInstance().endHauntedHouse();
				} else if (winnersCount > goalCount + 1) {
					L1HauntedHouse.getInstance().setGoalCount(goalCount + 1);
					L1HauntedHouse.getInstance().removeMember(pc);
					L1ItemInstance item = null;
					if (winnersCount == 3) {
						if (goalCount == 1) {
							item = ItemTable.getInstance()
									.createItem(49278); // 勇者のパンプキン袋(金)
						} else if (goalCount == 2) {
							item = ItemTable.getInstance()
									.createItem(49279); // 勇者のパンプキン袋(銀)
						}
					} else if (winnersCount == 2) {
						item = ItemTable.getInstance()
								.createItem(49279); // 勇者のパンプキン袋(銀)
					}
					int count = 1;
					if (item != null) {
						if (pc.getInventory().checkAddItem(item, count) ==
								L1Inventory.OK) {
							item.setCount(count);
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(403, item
									.getLogName())); // %0を手に入れました。
						}
					}
					L1SkillUse l1skilluse = new L1SkillUse();
					l1skilluse.handleCommands(pc,
							CANCELLATION, pc.getId(), pc.getX(),
							pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);
					L1Teleport.teleport(pc, 32624, 32813, (short) 4, 5, true);
				}
			}
		}
	}

	@Override
	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		removeAllKnownObjects();
	}
}
