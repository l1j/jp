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

import java.util.logging.Logger;
import java.util.TimerTask;
import jp.l1j.server.model.instance.L1DollInstance;

import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.packets.server.S_OwnCharStatus;
import jp.l1j.server.packets.server.S_SkillIconGFX;
import jp.l1j.server.packets.server.S_SkillSound;

public class L1ChargeTimer extends TimerTask {
	private static Logger _log = Logger.getLogger(L1ChargeTimer.class.getName());

	public L1ChargeTimer(L1PcInstance pc, L1ItemInstance item) {
		_pc = pc;
		_item = item;
	}

	@Override
	public void run() {
		if ((_item.getChargeTime() - 1) > 0) {
			_item.setChargeTime(_item.getChargeTime() - 1);
			_pc.getInventory().updateItem(_item, L1PcInventory.COL_CHARGE_TIME);
		} else {
			if (_item.getItem().getType2() == 1
					|| _item.getItem().getType2() == 2) { // 武器、防具
				_pc.getEquipSlot().remove(_item); // 装備を外してから削除
				_pc.getInventory().removeItem(_item, 1);
			} else if (_item.getItem().getType2() == 0
					&& _item.getItem().getType() == 2) { // light系アイテム
				_item.setNowLighting(false); // ライトを消す
				_pc.updateLight();
			} else if (_item.getItem().getType2() == 0
					&& _item.getItem().getType() == 17) { // 課金マジックドール
				L1DollInstance doll = null;
				Object[] dollList = _pc.getDollList().values().toArray();
				for (Object dollObject : dollList) { // 召喚解除
					doll = (L1DollInstance) dollObject;
					if (doll.getItemObjId() == _item.getId()) {
						_pc.sendPackets(new S_SkillSound(doll.getId(), 5936));
						_pc.broadcastPacket(new S_SkillSound(doll.getId(), 5936));
						doll.deleteDoll();
						_pc.sendPackets(new S_SkillIconGFX(56, 0));
						_pc.sendPackets(new S_OwnCharStatus(_pc));
						break;
					}
				}
			}
			_item.setChargeTime(0);
			_pc.getInventory().updateItem(_item, L1PcInventory.COL_CHARGE_TIME);
			this.cancel();
		}
	}

	private final L1PcInstance _pc;
	private final L1ItemInstance _item;
}
