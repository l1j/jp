/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 *
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 *
 */
package jp.l1j.server.command.executor;

import java.util.ArrayList;
import java.util.Collection;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.skill.L1BuffUtil;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Skill;

/**
 * GMコマンド：エンチャント変身なし
 */
public class L1PcBuff implements L1CommandExecutor {
	private L1PcBuff() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1PcBuff();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {

		try {
			Collection<L1PcInstance> players = null;
			if (arg.equalsIgnoreCase("me")) { // 自分のみ対象
				players = new ArrayList<L1PcInstance>();
				players.add(pc);
			}
			else if (arg.equalsIgnoreCase("all")) { // ワールド内全てのプレイヤー対象
				players = L1World.getInstance().getAllPlayers();
			}
			else { // 画面内にいるプレイヤー対象
				players = L1World.getInstance().getVisiblePlayer(pc);
			}

			int[] pcBuffSkill = { // L1SkillId から必要なBuffを入力
				LIGHT, DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR,
				BLESS_WEAPON, IMMUNE_TO_HARM, ADVANCE_SPIRIT, REDUCTION_ARMOR,
				BOUNCE_ATTACK, SOLID_CARRIAGE, ENCHANT_VENOM, BURNING_SPIRIT,
				VENOM_RESIST, UNCANNY_DODGE, DRESS_EVASION, GLOWING_AURA,
				BRAVE_AURA, RESIST_MAGIC, CLEAR_MIND, RESIST_ELEMENTAL,
				AQUA_PROTECTER, BURNING_WEAPON, IRON_SKIN, EXOTIC_VITALIZE,
				WATER_LIFE, ELEMENTAL_FIRE, SOUL_OF_FLAME, ADDITIONAL_FIRE
			};

			for (L1PcInstance tg : players) {
				L1BuffUtil.haste (tg, 3600 * 1000);	// 一段加速
				L1BuffUtil.brave (tg, 3600 * 1000);	// 二段加速
				L1BuffUtil.thirdSpeed (tg);			// 三段加速

				for (int element : pcBuffSkill) {
					L1Skill skill = SkillTable.getInstance().getTemplate(element);
					new L1SkillUse().handleCommands(tg, element, tg.getId(),
							tg.getX(), tg.getY(), null, skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
				}
				tg.sendPackets(new S_SystemMessage(I18N_BUFF_SKILLS_BY_GM));
				// GMからBuffスキルを受けました。
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_1,
					cmdName, "[all|me]")));
			// .%s %s の形式で入力してください。
		}
	}
}
