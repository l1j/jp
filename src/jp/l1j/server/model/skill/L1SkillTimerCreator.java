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
package jp.l1j.server.model.skill;

import jp.l1j.configure.Config;
import jp.l1j.server.model.L1Character;

public class L1SkillTimerCreator {
	public static L1SkillTimer create(L1Character cha, int skillId,
			int timeMillis) {
		if (Config.SKILLTIMER_IMPLTYPE == 1) {
			return new L1SkillTimerTimerImpl(cha, skillId, timeMillis);
		} else if (Config.SKILLTIMER_IMPLTYPE == 2) {
			return new L1SkillTimerThreadImpl(cha, skillId, timeMillis);
		}

		// 不正な値の場合は、とりあえずTimer
		return new L1SkillTimerTimerImpl(cha, skillId, timeMillis);
	}
}
