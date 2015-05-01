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
import jp.l1j.server.datatables.NpcTalkDataTable;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1NpcTalkData;
import jp.l1j.server.packets.server.S_NpcTalkReturn;
import jp.l1j.server.templates.L1Npc;

public class L1AddWarehouseInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static Logger _log = Logger.getLogger(L1AddWarehouseInstance.class.getName());

	/**
	 * @param template
	 */
	public L1AddWarehouseInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance pc) {
		onAction(pc, 0);
	}
	
	@Override
	public void onAction(L1PcInstance pc, int skillId) {
		L1Attack attack = new L1Attack(pc, this, skillId);
		attack.calcHit();
		attack.action();
		attack.addChaserAttack();
		attack.addEvilAttack();
		attack.calcDamage();
		attack.calcStaffOfMana();
		attack.addPcPoisonAttack(pc, this);
		attack.commit();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int objid = getId();
		L1NpcTalkData talking = NpcTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().getNpcId());
		int npcId = getNpcTemplate().getNpcId();
		String htmlid = null;

		if (talking != null) {
			
			if (htmlid != null) { // htmlidが指定されている場合
				pc.sendPackets(new S_NpcTalkReturn(objid, htmlid));
			} else {
				if (pc.getUseAdditionalWarehouse()) {
					pc.sendPackets(new S_NpcTalkReturn(talking, objid, 1));
				} else {
					pc.sendPackets(new S_NpcTalkReturn(talking, objid, 2));
				}
			}
		}
	}

	@Override
	public void onFinalAction(L1PcInstance pc, String Action) {
		if (Action.equalsIgnoreCase("retrieve-char")) {
			_log.finest("Retrive items in " + pc.getName() + "'s storage");
		}
	}
}
