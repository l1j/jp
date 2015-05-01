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

package jp.l1j.server.model;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1DollInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1MagicDoll;

public class MakeItemByDoll extends TimerTask {
	private static Logger _log = Logger.getLogger(MakeItemByDoll.class.getName());

	private final L1PcInstance _pc;
	private final L1DollInstance _doll;

	public MakeItemByDoll(L1PcInstance pc, L1DollInstance doll) {
		_pc = pc;
		_doll = doll;
	}

	@Override
	public void run() {
		try {
			if (_pc.isDead()) {
				return;
			}
			makeItem();
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void makeItem() {
		L1ItemInstance temp = ItemTable.getInstance().createItem(L1MagicDoll.getMakeItemIdByDoll(_doll));
		if (temp != null) {
			if (_pc.getInventory().checkAddItem(temp, 1) == L1Inventory.OK) {
				L1ItemInstance item = _pc.getInventory().storeItem(temp.getItemId(), 1);
				//_pc.sendPackets(new S_SkillSound(_pc.getId(), 6319));
				//_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 6319));
				_pc.sendPackets(new S_ServerMessage(403, item.getItem().getName()));
				// %0 を手に入れました
			}
		}
	}
}
