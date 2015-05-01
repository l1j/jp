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

package jp.l1j.server.templates;

import java.sql.ResultSet;
import java.sql.SQLException;
import jp.l1j.server.utils.IntRange;
import jp.l1j.server.utils.L1QueryUtil.EntityFactory;

public class L1MobSkill {
	private int _npcId;
	private int _actionNo;
	private int _type;
	private int _triRnd;
	private int _triHp;
	private int _triCompanionHp;
	private int _triRange;
	private int _triCount;
	private int _changeTarget;
	private int _range;
	private int _areaWidth;
	private int _areaHeight;
	private int _leverage;
	private int _skillId;
	private int _gfxId;
	private int _actId;
	private int _summonId;
	private int _summonMin;
	private int _summonMax;
	private int _polyId;
	private int _chatId;
	
	public static final int TYPE_NONE = 0;
	public static final int TYPE_PHYSICAL_ATTACK = 1;
	public static final int TYPE_MAGIC_ATTACK = 2;
	public static final int TYPE_SUMMON = 3;
	public static final int TYPE_POLY = 4;

	public static final int CHANGE_TARGET_NO = 0;
	public static final int CHANGE_TARGET_COMPANION = 1;
	public static final int CHANGE_TARGET_ME = 2;
	public static final int CHANGE_TARGET_RANDOM = 3;

	private L1MobSkill() {
	}

	public int getNpcId() {
		return _npcId;
	}

	public int getActionNo() {
		return _actionNo;
	}

	/**
	 * @return スキルのタイプ 0→何もしない、1→物理攻撃、2→魔法攻撃、3→サモン
	 */
	public int getType() {
		return _type;
	}

	/**
	 * @return スキル発動条件：ランダムな確率（0%～100%）でスキル発動
	 */
	public int getTriggerRandom() {
		return _triRnd;
	}

	/**
	 * @return スキル発動条件：HPがこの%以下で発動
	 */
	public int getTriggerHp() {
		return _triHp;
	}

	/**
	 * @return スキル発動条件：同族のHPがこの%以下で発動
	 */
	public int getTriggerCompanionHp() {
		return _triCompanionHp;
	}

	/**
	 * @return スキル発動条件：triRange<0の場合、対象との距離がabs(triRange)以下のとき発動
	 *         triRange>0の場合、対象との距離がtriRange以上のとき発動
	 */
	public int getTriggerRange() {
		return _triRange;
	}

	/**
	 * @return スキル発動条件：スキルの発動回数がtriCount以下のとき発動
	 */
	public int getTriggerCount() {
		return _triCount;
	}

	/**
	 * @return スキル発動時、ターゲットを変更するか?
	 */
	public int getChangeTarget() {
		return _changeTarget;
	}

	/**
	 * @return rangeまでの距離ならば攻撃可能、物理攻撃をするならば近接攻撃の場合でも1以上
	 */
	public int getRange() {
		return _range;
	}

	/**
	 * @return 範囲攻撃の横幅、単体攻撃ならば0、範囲攻撃するならば0以上。
	 *         WidthとHeightの設定は攻撃者からみて横幅をWidth、奥行きをHeightとする。
	 *         Widthは+-あるので、1を指定すれば、ターゲットを中心として左右1までが対象となる。
	 */
	public int getAreaWidth() {
		return _areaWidth;
	}

	/**
	 * @return 範囲攻撃の高さ、単体攻撃ならば0、範囲攻撃するならば1以上
	 */
	public int getAreaHeight() {
		return _areaHeight;
	}

	/**
	 * @return ダメージの倍率、1/10で表す。物理攻撃、魔法攻撃共に有効
	 */
	public int getLeverage() {
		return _leverage;
	}

	/**
	 * @return 魔法を使う場合のSkillId
	 */
	public int getSkillId() {
		return _skillId;
	}

	/**
	 * @return 物理攻撃のモーショングラフィック
	 */
	public int getGfxId() {
		return _gfxId;
	}

	/**
	 * @return 物理攻撃のグラフィックのアクションID
	 */
	public int getActId() {
		return _actId;
	}

	/**
	 * @return サモンするモンスターのNPCID
	 */
	public int getSummonId() {
		return _summonId;
	}

	/**
	 * @return サモンするモンスターの最小数
	 */
	public int getSummonMin() {
		return _summonMin;
	}

	/**
	 * @return サモンするモンスターの最大数
	 */
	public int getSummonMax() {
		return _summonMax;
	}

	/**
	 * @return サモンするモンスター数を保持したIntRangeオブジェクト
	 */
	public IntRange getSummonCountRange() {
		return new IntRange(_summonMin, _summonMax);
	}

	/**
	 * @return 強制変身させるPolyID
	 */
	public int getPolyId() {
		return _polyId;
	}

	/**
	 * @return スキル発生時のチャットID
	 */
	public int getChatId() {
		return _chatId;
	}

	/**
	 * distanceが指定idxスキルの発動条件を満たしているか
	 */
	public boolean isTriggerDistance(int distance) {
		int triggerRange = getTriggerRange();

		if ((triggerRange < 0 && distance <= Math.abs(triggerRange))
				|| (triggerRange > 0 && distance >= triggerRange)) {
			return true;
		}
		return false;
	}

	public static class Factory implements EntityFactory<L1MobSkill> {
		@Override
		public L1MobSkill fromResultSet(ResultSet rs) throws SQLException {
			L1MobSkill result = new L1MobSkill();
			result._npcId = rs.getInt("npc_id");
			result._actionNo = rs.getInt("act_no");
			result._type = rs.getInt("type");
			result._triRnd = rs.getInt("tri_rnd");
			result._triHp = rs.getInt("tri_hp");
			result._triCompanionHp = rs.getInt("tri_companion_hp");
			result._triRange = rs.getInt("tri_range");
			result._triCount = rs.getInt("tri_count");
			result._changeTarget = rs.getInt("change_target");
			result._range = rs.getInt("range");
			result._areaWidth = rs.getInt("area_width");
			result._areaHeight = rs.getInt("area_height");
			result._leverage = rs.getInt("leverage");
			result._skillId = rs.getInt("skill_id");
			result._gfxId = rs.getInt("gfx_id");
			result._actId = rs.getInt("act_id");
			result._summonId = rs.getInt("summon_id");
			result._summonMin = rs.getInt("summon_min");
			result._summonMax = rs.getInt("summon_max");
			result._polyId = rs.getInt("poly_id");
			result._chatId = rs.getInt("chat_id");
			return result;
		}
	}
}
