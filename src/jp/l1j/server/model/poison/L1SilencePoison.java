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
package jp.l1j.server.model.poison;


import jp.l1j.server.model.L1Character;
import static jp.l1j.server.model.skill.L1SkillId.*;

public class L1SilencePoison extends L1Poison {
	private final L1Character _target;

	public static boolean doInfection(L1Character cha) {
		if (!L1Poison.isValidTarget(cha)) {
			return false;
		}

		cha.setPoison(new L1SilencePoison(cha));
		return true;
	}

	private L1SilencePoison(L1Character cha) {
		_target = cha;

		doInfection();
	}

	private void doInfection() {
		_target.setPoisonEffect(1);
		sendMessageIfPlayer(_target, 310);

		_target.setSkillEffect(STATUS_POISON_SILENCE, 0);
	}

	@Override
	public int getEffectId() {
		return 1;
	}

	@Override
	public void cure() {
		_target.setPoisonEffect(0);
		sendMessageIfPlayer(_target, 311);

		_target.killSkillEffectTimer(STATUS_POISON_SILENCE);
		_target.setPoison(null);
	}
}
