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
import jp.l1j.server.model.L1Character;
import jp.l1j.server.packets.server.S_SkillIconWindShackle;
import jp.l1j.server.templates.L1CharacterBuff;

public class L1WindShackle extends L1BuffSkillExecutorImpl {

	@Override
	public void addEffect(L1Character user, L1Character target,
			int durationSeconds) {
		if (!(target instanceof L1PcInstance)) {
			return;
		}

		L1PcInstance pc = (L1PcInstance) target;
		pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), durationSeconds));
		pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(),
				durationSeconds));
	}

	@Override
	public void removeEffect(L1Character target) {
		addEffect(null, target, 0);
	}

	@Override
	public void restoreEffect(L1PcInstance target, L1CharacterBuff buff) {
		addEffect(null, target, buff.getRemainingTime());
	}
}
