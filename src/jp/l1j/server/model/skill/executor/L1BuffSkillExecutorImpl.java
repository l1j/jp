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

package jp.l1j.server.model.skill.executor;

import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.templates.L1CharacterBuff;

public abstract class L1BuffSkillExecutorImpl extends L1SkillExecutorImpl
		implements L1BuffSkillExecutor {
	@Override
	public void restoreEffect(L1PcInstance target, L1CharacterBuff buff) {
		addEffect(null, target, buff.getRemainingTime());
	}

	@Override
	public L1CharacterBuff getCharacterBuff(L1PcInstance pc) {
		return null;
	}
}
