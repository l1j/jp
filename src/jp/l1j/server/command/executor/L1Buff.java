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

package jp.l1j.server.command.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Skill;

public class L1Buff implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Buff.class.getName());

	private L1Buff() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Buff();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			Collection<L1PcInstance> players = null;
			String s = tok.nextToken();
			if (s.equals("me")) {
				players = new ArrayList<L1PcInstance>();
				players.add(pc);
				s = tok.nextToken();
			} else if (s.equals("all")) {
				players = L1World.getInstance().getAllPlayers();
				s = tok.nextToken();
			} else {
				players = L1World.getInstance().getVisiblePlayer(pc);
			}

			int skillId = Integer.parseInt(s);
			int time = 0;
			if (tok.hasMoreTokens()) {
				time = Integer.parseInt(tok.nextToken());
			}

			L1Skill skill = SkillTable.getInstance().findBySkillId(skillId);

			if (skill.getTarget().equals("buff")) {
				for (L1PcInstance tg : players) {
					new L1SkillUse().handleCommands(pc, skillId, tg.getId(),
							tg.getX(), tg.getY(), null, time, L1SkillUse.TYPE_SPELLSC);
				}
			} else if (skill.getTarget().equals("none")) {
				for (L1PcInstance tg : players) {
					new L1SkillUse().handleCommands(tg, skillId, tg.getId(),
							tg.getX(), tg.getY(), null, time, L1SkillUse.TYPE_GMBUFF);
				}
			} else {
				pc.sendPackets(new S_SystemMessage(String.format(I18N_IS_NOT_BUFF_SKILL, skillId)));
				// %d はBUFFスキルではありません。
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_3,
					cmdName, "[all|me]", I18N_SKILL_ID, I18N_SECONDS)));
			// .%s %s %s %s の形式で入力してください。
		}
	}
}
