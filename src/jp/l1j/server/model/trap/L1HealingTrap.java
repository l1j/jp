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
package jp.l1j.server.model.trap;

import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.storage.TrapStorage;
import jp.l1j.server.utils.Dice;

public class L1HealingTrap extends L1Trap {
	private final Dice _dice;
	private final int _base;
	private final int _diceCount;

	public L1HealingTrap(TrapStorage storage) {
		super(storage);

		_dice = new Dice(storage.getInt("dice"));
		_base = storage.getInt("base");
		_diceCount = storage.getInt("dice_count");
	}

	@Override
	public void onTrod(L1PcInstance trodFrom, L1Object trapObj) {
		sendEffect(trapObj);

		int pt = _dice.roll(_diceCount) + _base;

		trodFrom.healHp(pt);
	}
}
