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

import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_AddSkill;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Skill;

public class L1AddSkill implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1AddSkill.class.getName());

	private L1AddSkill() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AddSkill();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			int cnt = 0; // ループカウンタ
			String skill_name = ""; // スキル名
			int skill_id = 0; // スキルID
			
			int object_id = pc.getId(); // キャラクタのobjectidを取得
			pc.sendPackets(new S_SkillSound(object_id, '\343')); // 魔法習得の効果音を鳴らす
			pc.broadcastPacket(new S_SkillSound(object_id, '\343'));

			int max_level = pc.getClassFeature().getMaxSpellLevel();
			int s0 = max_level >= 1 ? 255 : 0;
			int s1 = max_level >= 2 ? 255 : 0;
			int s2 = max_level >= 3 ? 127 : 0;
			int s3 = max_level >= 4 ? 255 : 0;
			int s4 = max_level >= 5 ? 255 : 0;
			int s5 = max_level >= 6 ? 255 : 0;
			int s6 = max_level >= 7 ? 255 : 0;
			int s7 = max_level >= 8 ? 255 : 0;
			int s8 = max_level >= 9 ? 255 : 0;
			int s9 = max_level >= 10 ? 255 : 0;
			
			if (pc.isCrown()) {
				pc.sendPackets(new S_AddSkill(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9,
						0, 0, 0, 0, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
				learnMagic(pc, max_level); // 一般魔法
				for (cnt = 113; cnt <= 120; cnt++) // プリ魔法
				{
					L1Skill l1skills = SkillTable.getInstance().findBySkillId(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}
			} else if (pc.isKnight()) {
				pc.sendPackets(new S_AddSkill(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9,
						192, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
				learnMagic(pc, max_level); // 一般魔法
				for (cnt = 87; cnt <= 91; cnt++) // ナイト魔法
				{
					L1Skill l1skills = SkillTable.getInstance().findBySkillId(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}
			} else if (pc.isElf()) {
				pc.sendPackets(new S_AddSkill(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9,
						0, 0, 0, 0, 0, 0, 127, 3, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0));
				learnMagic(pc, max_level); // 一般魔法
				for (cnt = 129; cnt <= 176; cnt++) // エルフ魔法
				{
					L1Skill l1skills = SkillTable.getInstance().findBySkillId(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}
			} else if (pc.isWizard()) {
				pc.sendPackets(new S_AddSkill(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
				learnMagic(pc, max_level); // 一般魔法
			} else if (pc.isDarkelf()) {
				pc.sendPackets(new S_AddSkill(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9,
						0, 0, 255, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
				learnMagic(pc, max_level); // 一般魔法
				for (cnt = 97; cnt <= 112; cnt++) // DE魔法
				{
					L1Skill l1skills = SkillTable.getInstance().findBySkillId(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}
			} else if (pc.isDragonKnight()) {
				pc.sendPackets(new S_AddSkill(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 240, 255, 7, 0, 0, 0));
				learnMagic(pc, max_level); // 一般魔法
				for (cnt = 181; cnt <= 195; cnt++) // ドラゴンナイト秘技
				{
					L1Skill l1skills = SkillTable.getInstance().findBySkillId(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}
			} else if (pc.isIllusionist()) {
				pc.sendPackets(new S_AddSkill(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 15));
				learnMagic(pc, max_level); // 一般魔法
				for (cnt = 201; cnt <= 220; cnt++) // イリュージョニスト魔法
				{
					L1Skill l1skills = SkillTable.getInstance().findBySkillId(cnt); // スキル情報を取得
					skill_name = l1skills.getName();
					skill_id = l1skills.getSkillId();
					SkillTable.getInstance().spellMastery(object_id, skill_id, skill_name, 0, 0); // DBに登録
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_ERROR, cmdName)));
			// %s コマンドエラー
		}
	}
	
	private void learnMagic(L1PcInstance pc, int max_level) {
		int cnt = 0; // ループカウンタ
		String skill_name = ""; // スキル名
		int skill_id = 0; // スキルID
			
		for (cnt = 1; cnt <= max_level * 8; cnt++) {
			L1Skill l1skills = SkillTable.getInstance().findBySkillId(cnt); // スキル情報を取得
			skill_name = l1skills.getName();
			skill_id = l1skills.getSkillId();
			SkillTable.getInstance().spellMastery(pc.getId(), skill_id, skill_name, 0, 0); // DBに登録
		}
	}
}
