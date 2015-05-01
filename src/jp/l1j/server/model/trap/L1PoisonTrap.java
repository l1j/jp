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
import jp.l1j.server.model.poison.L1DamagePoison;
import jp.l1j.server.model.poison.L1ParalysisPoison;
import jp.l1j.server.model.poison.L1SilencePoison;
import jp.l1j.server.storage.TrapStorage;

public class L1PoisonTrap extends L1Trap {
	private final String _type;
	private final int _delay;
	private final int _time;
	private final int _damage;

	public L1PoisonTrap(TrapStorage storage) {
		super(storage);

		_type = storage.getString("poison_type");
		_delay = storage.getInt("poison_delay");
		_time = storage.getInt("poison_time");
		_damage = storage.getInt("poison_damage");
	}

	@Override
	public void onTrod(L1PcInstance trodFrom, L1Object trapObj) {
		sendEffect(trapObj);

		if (_type.equals("d")) {
			L1DamagePoison.doInfection(trodFrom, trodFrom, _time, _damage);
		} else if (_type.equals("s")) {
			L1SilencePoison.doInfection(trodFrom);
		} else if (_type.equals("p")) {
			L1ParalysisPoison.doInfection(trodFrom, _delay, _time);
		}
	}
}
