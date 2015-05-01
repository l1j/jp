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
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.storage.TrapStorage;

public class L1SkillTrap extends L1Trap {
	private final int _skillId;
	private final int _skillTimeSeconds;

	public L1SkillTrap(TrapStorage storage) {
		super(storage);

		_skillId = storage.getInt("skill_id");
		_skillTimeSeconds = storage.getInt("skill_time_seconds");
	}

	@Override
	public void onTrod(L1PcInstance trodFrom, L1Object trapObj) {
		sendEffect(trapObj);

		new L1SkillUse().handleCommands(trodFrom, _skillId, trodFrom.getId(),
				trodFrom.getX(), trodFrom.getY(), null, _skillTimeSeconds,
				L1SkillUse.TYPE_GMBUFF);
	}

}
