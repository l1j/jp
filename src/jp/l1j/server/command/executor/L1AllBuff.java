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

import java.util.StringTokenizer;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.L1PolyMorph;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.skill.L1BuffUtil;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Skill;

public class L1AllBuff implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1AllBuff.class.getName());

	private L1AllBuff() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AllBuff();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		int[] allBuffSkill = { LIGHT, DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX,
				MEDITATION, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, BERSERKERS,
				IMMUNE_TO_HARM, ADVANCE_SPIRIT, REDUCTION_ARMOR, BOUNCE_ATTACK,
				SOLID_CARRIAGE, ENCHANT_VENOM, BURNING_SPIRIT, VENOM_RESIST,
				DOUBLE_BRAKE, UNCANNY_DODGE, DRESS_EVASION, GLOWING_AURA,
				BRAVE_AURA, RESIST_MAGIC, CLEAR_MIND, ELEMENTAL_PROTECTION,
				AQUA_PROTECTER, BURNING_WEAPON, IRON_SKIN, EXOTIC_VITALIZE,
				WATER_LIFE, ELEMENTAL_FIRE, SOUL_OF_FLAME, ADDITIONAL_FIRE };
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String name = st.nextToken();
			L1PcInstance target = L1World.getInstance().getPlayer(name);
			if (target == null) {
				pc.sendPackets(new S_ServerMessage(73, name)); // \f1%0はゲームをしていません。
				return;
			}

			L1BuffUtil.haste(target, 3600 * 1000);
			L1BuffUtil.brave(target, 3600 * 1000);
			L1PolyMorph.doPoly(target, 5641, 7200, L1PolyMorph.MORPH_BY_GM);
			for (int i = 0; i < allBuffSkill.length; i++) {
				L1Skill skill = SkillTable.getInstance().findBySkillId(
						allBuffSkill[i]);
				new L1SkillUse().handleCommands(target, allBuffSkill[i], target
						.getId(), target.getX(), target.getY(), null, skill
						.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_1,
					cmdName, I18N_CHAR_NAME)));
			// // .%s %s の形式で入力してください。
		}
	}
}
