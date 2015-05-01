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

import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;

public class L1ExpirationTimer extends TimerTask {
	private static Logger _log = Logger.getLogger(L1ExpirationTimer.class.getName());

	public L1ExpirationTimer(L1PcInstance pc, L1ItemInstance item) {
		_pc = pc;
		_item = item;
	}

	@Override
	public void run() {
		if (_item.getExpirationTime().getTime() > System.currentTimeMillis()) {
		} else {
			if (_item.getItem().getType2() == 1
					|| _item.getItem().getType2() == 2) { // 武器、防具
				_pc.getEquipSlot().remove(_item); // 装備を外す
			} else {
				int itemId = _item.getItem().getItemId();
				if (itemId == 50565) { // キルトンの契約書
					_pc.getInventory().storeItem(50567, 1);
					// 配送員ミミックの笛：タイガー飼育場
				} else if (itemId == 50566) { // メーリンの契約書
					_pc.getInventory().storeItem(50568, 1);
					// 配送員ミミックの笛：紀州犬のかご
				} else if (itemId == 50579) { // モポの契約書：ジュース100本
					_pc.getInventory().storeItem(50587, 1);
					// 配送員ミミックの笛：ジュース100本
				} else if (itemId == 50580) { // モポの契約書：ジュース200本
					_pc.getInventory().storeItem(50588, 1);
					// 配送員ミミックの笛：ジュース200本
				} else if (itemId == 50581) { // モポの契約書：ジュース300本
					_pc.getInventory().storeItem(50589, 1);
					// 配送員ミミックの笛：ジュース300本
				} else if (itemId == 50582) { // モポの契約書：エキス100本
					_pc.getInventory().storeItem(50590, 1);
					// 配送員ミミックの笛：エキス100本
				} else if (itemId == 50583) { // モポの契約書：エキス200本
					_pc.getInventory().storeItem(50591, 1);
					// 配送員ミミックの笛：エキス200本
				} else if (itemId == 50584) { // モポの契約書：エキス300本
					_pc.getInventory().storeItem(50592, 1);
					// 配送員ミミックの笛：エキス3q00本
				}
			}
			_pc.getInventory().removeItem(_item, 1);
			this.cancel();
		}
	}

	private final L1PcInstance _pc;
	private final L1ItemInstance _item;
}
