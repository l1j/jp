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

package jp.l1j.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.instance.L1PcInstance;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.skill.executor.L1BuffSkillExecutor;
import jp.l1j.server.templates.L1CharacterBuff;
import jp.l1j.server.templates.L1Skill;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;
import jp.l1j.server.utils.collections.Lists;

public class CharBuffTable {
	private CharBuffTable() {
	}

	private static Logger _log = Logger.getLogger(CharBuffTable.class.getName());

	private static final int[] BUFF_SKILL_IDS = {
		MIRROR_IMAGE, UNCANNY_DODGE, RESIST_FEAR,
		STATUS_BRAVE, STATUS_HASTE, STATUS_BLUE_POTION, STATUS_UNDERWATER_BREATH,
		STATUS_WISDOM_POTION, STATUS_CHAT_PROHIBITED, STATUS_POISON,
		STATUS_POISON_SILENCE, STATUS_POISON_PARALYZING, STATUS_POISON_PARALYZED,
		STATUS_CURSE_PARALYZING, STATUS_CURSE_PARALYZED, STATUS_FLOATING_EYE,
		STATUS_HOLY_WATER, STATUS_HOLY_MITHRIL_POWDER, STATUS_HOLY_WATER_OF_EVA,
		STATUS_ELFBRAVE, STATUS_RIBRAVE, STATUS_CUBE_IGNITION_TO_ALLY,
		STATUS_CUBE_QUAKE_TO_ALLY, STATUS_CUBE_SHOCK_TO_ALLY,
		STATUS_CUBE_BALANCE, STATUS_THIRD_SPEED, STATUS_FLORA_POTION_STR,
		STATUS_FLORA_POTION_DEX, STATUS_FREEZE, STATUS_CURSE_BARLOG,
		STATUS_CURSE_YAHEE, STATUS_WEAKNESS_EXPOSURE_LV1,
		STATUS_WEAKNESS_EXPOSURE_LV2, STATUS_WEAKNESS_EXPOSURE_LV3,
		STATUS_DESTRUCTION_NOSTRUM, STATUS_EXP_UP, STATUS_EXP_UP_II,
		POTION_OF_SWORDMAN, POTION_OF_MAGICIAN, POTION_OF_RECOVERY,
		POTION_OF_MEDITATION, POTION_OF_LIFE, POTION_OF_MAGIC,
		POTION_OF_MAGIC_RESIST, POTION_OF_STR, POTION_OF_DEX, POTION_OF_CON,
		POTION_OF_INT, POTION_OF_WIS, POTION_OF_RAGE, POTION_OF_CONCENTRATION,
		COOKING_1_0_N, COOKING_1_1_N, COOKING_1_2_N, COOKING_1_3_N,
		COOKING_1_4_N, COOKING_1_5_N, COOKING_1_6_N, COOKING_1_7_N,
		COOKING_1_0_S, COOKING_1_1_S, COOKING_1_2_S, COOKING_1_3_S,
		COOKING_1_4_S, COOKING_1_5_S, COOKING_1_6_S, COOKING_1_7_S,
		COOKING_2_0_N, COOKING_2_1_N, COOKING_2_2_N, COOKING_2_3_N,
		COOKING_2_4_N, COOKING_2_5_N, COOKING_2_6_N, COOKING_2_7_N,
		COOKING_2_0_S, COOKING_2_1_S, COOKING_2_2_S, COOKING_2_3_S,
		COOKING_2_4_S, COOKING_2_5_S, COOKING_2_6_S, COOKING_2_7_S,
		COOKING_3_0_N, COOKING_3_1_N, COOKING_3_2_N, COOKING_3_3_N,
		COOKING_3_4_N, COOKING_3_5_N, COOKING_3_6_N, COOKING_3_7_N,
		COOKING_3_0_S, COOKING_3_1_S, COOKING_3_2_S, COOKING_3_3_S,
		COOKING_3_4_S, COOKING_3_5_S, COOKING_3_6_S, COOKING_3_7_S,
		COOKING_4_1, COOKING_4_2, COOKING_4_3, COOKING_4_4,
		ELIXIR_OF_IVORY_TOWER, BLOODSTAIN_OF_ANTHARAS, BLOODSTAIN_OF_FAFURION,
		BLOODSTAIN_OF_LINDVIOR, BLOODSTAIN_OF_VALAKAS, BLESS_OF_CRAY,
		BLESS_OF_SAEL, BLESS_OF_GUNTER, MAGIC_EYE_OF_ANTHARAS, MAGIC_EYE_OF_FAFURION,
		MAGIC_EYE_OF_LINDVIOR, MAGIC_EYE_OF_VALAKAS, MAGIC_EYE_OF_BIRTH,
		MAGIC_EYE_OF_SHAPE, MAGIC_EYE_OF_LIFE, STONE_OF_DRAGON,
		BLESS_OF_COMA1, BLESS_OF_COMA2, BLESS_OF_SAMURAI
		};

	private static List<Integer> buffSkillIds() {
		List<Integer> result = SkillTable.getInstance().findBuffSkillIds();
		for (int id : BUFF_SKILL_IDS) {
			result.add(id);
		}
		return result;
	}

	private static void store(int objId, int skillId, int time, int polyId, int attrKind) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("REPLACE INTO character_buffs SET char_id=?, skill_id=?, remaining_time=?, poly_id=?, attr_kind=?");
			pstm.setInt(1, objId);
			pstm.setInt(2, skillId);
			pstm.setInt(3, time);
			pstm.setInt(4, polyId);
			pstm.setInt(5, attrKind);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public static void delete(int charId) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_buffs WHERE char_id=?");
			pstm.setInt(1, charId);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	private static boolean saveByExecutor(int skillId, L1PcInstance pc) {
		L1Skill skill = SkillTable.getInstance().findBySkillId(skillId);
		if (skill == null) {
			return false;
		}
		L1BuffSkillExecutor exe = skill.newBuffSkillExecutor();
		if (exe == null) {
			return false;
		}
		L1CharacterBuff buff = exe.getCharacterBuff(pc);
		if (buff == null) {
			return false;
		}
		store(buff.getCharcterId(), buff.getSkillId(), buff.getRemainingTime(),
				buff.getPolyId(), buff.getAttrKind());
		return true;
	}

	public static void save(L1PcInstance pc) {
		for (int skillId : buffSkillIds()) {
			int timeSec = pc.getSkillEffectTimeSec(skillId);
			if (0 < timeSec) {
				if (saveByExecutor(skillId, pc)) {
					continue;
				}
				int polyId = 0;
				if (skillId == SHAPE_CHANGE) {
					polyId = pc.getTempCharGfx();
				}
				store(pc.getId(), skillId, timeSec, polyId, 0);
			}
		}
	}

	private static L1CharacterBuff fromResultSet(ResultSet rs) throws SQLException {
		int charcterId = rs.getInt("char_id");
		int skillId = rs.getInt("skill_id");
		int remainingTime = rs.getInt("remaining_time");
		int polyId = rs.getInt("poly_id");
		int attrKind = rs.getInt("attr_kind");
		return new L1CharacterBuff(charcterId, skillId, remainingTime, polyId, attrKind);
	}

	public static List<L1CharacterBuff> findByCharacterId(int id) {
		List<L1CharacterBuff> result = Lists.newArrayList();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_buffs WHERE char_id = ?");
			pstm.setInt(1, id);
			rs = pstm.executeQuery();
			while (rs.next()) {
				result.add(fromResultSet(rs));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return result;
	}
}
