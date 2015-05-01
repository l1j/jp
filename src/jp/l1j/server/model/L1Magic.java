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
package jp.l1j.server.model;

import java.text.MessageFormat;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.controller.timer.WarTimeController;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_GreenMessage;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1MagicDoll;
import jp.l1j.server.templates.L1Skill;

public class L1Magic {
	private int _calcType;

	private final int PC_PC = 1;

	private final int PC_NPC = 2;

	private final int NPC_PC = 3;

	private final int NPC_NPC = 4;

	private L1PcInstance _pc = null;

	private L1PcInstance _targetPc = null;

	private L1NpcInstance _npc = null;

	private L1NpcInstance _targetNpc = null;

	private int _leverage = 10; // 1/10倍で表現する。

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	public void setLeverage(int i) {
		_leverage = i;
	}

	private int getLeverage() {
		return _leverage;
	}

	public L1Magic(L1Character attacker, L1Character target) {
		if (attacker instanceof L1PcInstance) {
			if (target instanceof L1PcInstance) {
				_calcType = PC_PC;
				_pc = (L1PcInstance) attacker;
				_targetPc = (L1PcInstance) target;
			} else {
				_calcType = PC_NPC;
				_pc = (L1PcInstance) attacker;
				_targetNpc = (L1NpcInstance) target;
			}
		} else {
			if (target instanceof L1PcInstance) {
				_calcType = NPC_PC;
				_npc = (L1NpcInstance) attacker;
				_targetPc = (L1PcInstance) target;
			} else {
				_calcType = NPC_NPC;
				_npc = (L1NpcInstance) attacker;
				_targetNpc = (L1NpcInstance) target;
			}
		}
	}

	/* ■■■■■■■■■■■■■■■ 魔法共通関数 ■■■■■■■■■■■■■■ */
	private int getSpellPower() {
		int spellPower = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			spellPower = _pc.getSp();
			// 魔眼によるSP上昇
			if (_pc.hasSkillEffect(MAGIC_EYE_OF_LINDVIOR)
						|| _pc.hasSkillEffect(MAGIC_EYE_OF_SHAPE)
						|| _pc.hasSkillEffect(MAGIC_EYE_OF_LIFE)) {
				int chance = _random.nextInt(100) + 1;
				if (chance <= 10) {
					spellPower += 2;
				}
			}
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			spellPower = _npc.getSp();
			// 魔眼によるSP上昇
			if (_npc.hasSkillEffect(MAGIC_EYE_OF_LINDVIOR)
						|| _npc.hasSkillEffect(MAGIC_EYE_OF_SHAPE)
						|| _npc.hasSkillEffect(MAGIC_EYE_OF_LIFE)) {
				int chance = _random.nextInt(100) + 1;
				if (chance <= 10) {
					spellPower += 2;
				}
			}
		}
		return spellPower;
	}

	private int getMagicLevel() {
		int magicLevel = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicLevel = _pc.getMagicLevel();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			magicLevel = _npc.getMagicLevel();
		}
		return magicLevel;
	}

	private int getMagicBonus() {
		int magicBonus = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicBonus = _pc.getMagicBonus();
			// 魔眼によるSP上昇
			if (_pc.hasSkillEffect(MAGIC_EYE_OF_LINDVIOR)
						|| _pc.hasSkillEffect(MAGIC_EYE_OF_SHAPE)
						|| _pc.hasSkillEffect(MAGIC_EYE_OF_LIFE)) {
				int chance = _random.nextInt(100) + 1;
				if (chance <= 10) {
					magicBonus += 2;
				}
			}
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			magicBonus = _npc.getMagicBonus();
			// 魔眼によるSP上昇
			if (_npc.hasSkillEffect(MAGIC_EYE_OF_LINDVIOR)
						|| _npc.hasSkillEffect(MAGIC_EYE_OF_SHAPE)
						|| _npc.hasSkillEffect(MAGIC_EYE_OF_LIFE)) {
				int chance = _random.nextInt(100) + 1;
				if (chance <= 10) {
					magicBonus += 2;
				}
			}
		}
		return magicBonus;
	}

	private int getMagicCritical() {
		int critical = _pc.getOriginalMagicCritical();
		if (_pc.hasSkillEffect(BLOODSTAIN_OF_LINDVIOR)) { // リンドビオルの血痕
			critical += 1; // クリティカル+1%
		}
		return critical;
	}
	
	private int getLawful() {
		int lawful = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			lawful = _pc.getLawful();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			lawful = _npc.getLawful();
		}
		return lawful;
	}

	private int getTargetMr() {
		int mr = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			mr = _targetPc.getMr();
		} else {
			mr = _targetNpc.getMr();
		}
		return mr;
	}

	/* ■■■■■■■■■■■■■■ 成功判定 ■■■■■■■■■■■■■ */
	// ●●●● 確率系魔法の成功判定 ●●●●
	// 計算方法
	// 攻撃側ポイント：LV + ((MagicBonus * 3) * 魔法固有係数)
	// 防御側ポイント：((LV / 2) + (MR * 3)) / 2
	// 攻撃成功率：攻撃側ポイント - 防御側ポイント
	public boolean calcProbabilityMagic(int skillId) {
		int probability = 0;
		boolean isSuccess = false;

		// 攻撃者がGM権限の場合100%成功
		if (_pc != null && _pc.isGm()) {
			return true;
		}

		if (_calcType == PC_NPC && _targetNpc != null) {
			int npcId = _targetNpc.getNpcTemplate().getNpcId();
			if (npcId == 45878 && _pc.getWeapon().getItem().getItemId() != 246 ) { // ドレイクの幽霊
				return false;
			}
			if (npcId >= 45912 && npcId <= 45915 // 恨みに満ちたソルジャー＆ソルジャーゴースト
					&& !_pc.hasSkillEffect(STATUS_HOLY_WATER)) {
				return false;
			}
			if (npcId == 45916 // 恨みに満ちたハメル将軍
					&& !_pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
				return false;
			}
			if (npcId == 45941 // 呪われた巫女サエル
					&& !_pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
				return false;
			}
			if (npcId == 45752 // バルログ(変身前)
					&& !_pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				return false;
			}
			if (npcId == 45753 // バルログ(変身後)
					&& !_pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				return false;
			}
			if (npcId == 45675 // ヤヒ(変身前)
					&& !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 81082 // ヤヒ(変身後)
					&& !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 45625 // 混沌
					&& !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 45674 // 死
					&& !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 45685 // 堕落
					&& !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId >= 46068 && npcId <= 46091 // 欲望の洞窟側mob
					&& _pc.getTempCharGfx() == 6035) {
				return false;
			}
			if (npcId >= 46092 && npcId <= 46106 // 影の神殿側mob
					&& _pc.getTempCharGfx() == 6034) {
				return false;
			}
		}

		if (!checkZone(skillId)) {
			return false;
		}
		if (skillId == CANCELLATION || skillId == MASS_CANCELLATION) {
			if (_calcType == PC_PC && _pc != null && _targetPc != null) {
				// 自分自身の場合は100%成功
				if (_pc.getId() == _targetPc.getId()) {
					return true;
				}
				// 同じクランの場合は100%成功
				if (_pc.getClanId() > 0
						&& (_pc.getClanId() == _targetPc.getClanId())) {
					return true;
				}
				// 同じパーティの場合は100%成功
				if (_pc.isInParty()) {
					if (_pc.getParty().isMember(_targetPc)) {
						return true;
					}
				}
				// それ以外の場合、セーフティゾーン内では無効
				if (_pc.getZoneType() == 1 || _targetPc.getZoneType() == 1) {
					return false;
				}
			}
			// 対象がNPC、使用者がNPCの場合は100%成功
			if (_calcType == PC_NPC || _calcType == NPC_PC
					|| _calcType == NPC_NPC) {
				return true;
			}
		}

		// アースバインド中はWB、キャンセレーション以外無効
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (_targetPc.hasSkillEffect(EARTH_BIND)) {
				if (skillId != WEAPON_BREAK && skillId != CANCELLATION && skillId != MASS_CANCELLATION) {
					return false;
				}
			}
		} else {
			if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
				if (skillId != WEAPON_BREAK && skillId != CANCELLATION && skillId != MASS_CANCELLATION) {
					return false;
				}
			}
		}

		probability = calcProbability(skillId);

		int rnd = _random.nextInt(100) + 1;

		if (probability >= rnd) {
			isSuccess = true;
		} else {
			isSuccess = false;
		}

		// 確率系魔法メッセージ
		if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.getAttackLog()) {
			return isSuccess;
		}
		if ((_calcType == PC_PC || _calcType == NPC_PC) && !_targetPc.getAttackLog()) {
			return isSuccess;
		}

		String msg0 = "";
		String msg1 = I18N_ATTACK_TO;
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";

		if (_calcType == PC_PC || _calcType == PC_NPC) { // アタッカーがＰＣの場合
			msg0 = _pc.getName();
		} else if (_calcType == NPC_PC) { // アタッカーがＮＰＣの場合
			msg0 = _npc.getName();
		}

		msg2 = "probability:" + probability + "%";
		if (_calcType == NPC_PC || _calcType == PC_PC) { // ターゲットがＰＣの場合
			msg4 = _targetPc.getName();
		} else if (_calcType == PC_NPC) { // ターゲットがＮＰＣの場合
			msg4 = _targetNpc.getName();
		}
		if (isSuccess == true) {
			msg3 = I18N_SKILL_SUCCESS; // 成功
		} else {
			msg3 = I18N_SKILL_FAILED; // 失敗
		}

		if (_calcType == PC_PC || _calcType == PC_NPC) { // アタッカーがＰＣの場合
			// _pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3, msg4));
			// \f1%0が%4%1%3 %2
			_pc.sendPackets(new S_SystemMessage(I18N_SKILL_GAVE_TEXT_COLOR +
				MessageFormat.format(I18N_ATTACK_FORMAT, msg0, msg4, msg3, msg2)));
			// {0}が{1}に{2} {3}
		}
		if (_calcType == NPC_PC || _calcType == PC_PC) { // ターゲットがＰＣの場合
			// _targetPc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3, msg4));
			// \f1%0が%4%1%3 %2
			_targetPc.sendPackets(new S_SystemMessage(I18N_SKILL_RECEIVED_TEXT_COLOR +
				MessageFormat.format(I18N_ATTACK_FORMAT, msg0, msg4, msg3, msg2), true));
			// {0}が{1}に{2} {3}
		}
		
		return isSuccess;
	}

	private boolean checkZone(int skillId) {
		if (_pc != null && _targetPc != null) {
			if (_pc.getZoneType() == 1 || _targetPc.getZoneType() == 1) { // セーフティーゾーン
				if (skillId == WEAPON_BREAK || skillId == SLOW
						|| skillId == CURSE_PARALYZE || skillId == MANA_DRAIN
						|| skillId == DARKNESS || skillId == WEAKNESS
						|| skillId == DISEASE || skillId == DECAY_POTION
						|| skillId == MASS_SLOW || skillId == ENTANGLE
						|| skillId == ERASE_MAGIC || skillId == EARTH_BIND
						|| skillId == AREA_OF_SILENCE || skillId == AREA_WIND_SHACKLE
						|| skillId == WIND_SHACKLE || skillId == STRIKER_GALE
						|| skillId == SHOCK_STUN || skillId == MASS_SHOCK_STUN
						|| skillId == FOG_OF_SLEEPING || skillId == ICE_LANCE
						|| skillId == FREEZING_BLIZZARD
						|| skillId == POLLUTE_WATER
						|| skillId == ELEMENTAL_FALL_DOWN
						|| skillId == RETURN_TO_NATURE || skillId == BONE_BREAK
						|| skillId == CONFUSION || skillId == MIND_BREAK
						|| skillId == JOY_OF_PAIN || skillId == CURSE_POISON
						|| skillId == CURSE_BLIND || skillId == GUARD_BRAKE
						|| skillId == RESIST_FEAR || skillId == HORROR_OF_DEATH
						|| skillId == PHANTASM || skillId == PANIC) {
					return false;
				}
			}
		}
		return true;
	}

	private int calcProbability(int skillId) {
		L1Skill l1skills = SkillTable.getInstance().findBySkillId(skillId);
		int attackLevel = 0;
		int defenseLevel = 0;
		int mr = 0;
		int probability = 0;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			attackLevel = _pc.getLevel();
		} else {
			attackLevel = _npc.getLevel();
		}

		if (_calcType == PC_PC || _calcType == NPC_PC) {
			defenseLevel = _targetPc.getLevel();
		} else {
			defenseLevel = _targetNpc.getLevel();
			if (skillId == RETURN_TO_NATURE) {
				if (_targetNpc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _targetNpc;
					defenseLevel = summon.getMaster().getLevel();
				}
			}
		}

		if (skillId == SHOCK_STUN || skillId == MASS_SHOCK_STUN ||
				skillId == COUNTER_BARRIER ||
				skillId == ELEMENTAL_FALL_DOWN || skillId == RETURN_TO_NATURE ||
				skillId == ENTANGLE || skillId == ERASE_MAGIC || skillId == EARTH_BIND ||
				skillId == AREA_OF_SILENCE || skillId == WIND_SHACKLE || skillId == AREA_WIND_SHACKLE ||
				skillId == STRIKER_GALE || skillId == POLLUTE_WATER ||
				skillId == ARMOR_BREAK ||
				skillId == ELZABE_AREA_SILENCE) {
			// 成功確率は 魔法固有係数 × LV差 + 基本確率
			probability = (int) (((l1skills.getProbabilityDice()) / 10D) * (attackLevel - defenseLevel))
					+ l1skills.getProbabilityValue();

			// オリジナルINTによる魔法命中
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getOriginalMagicHit();
			}
//		} else if (skillId == COUNTER_BARRIER) {
//			// 成功確率は 基本確率 + LV差1毎に+-1%
//			probability = l1skills.getProbabilityValue() + attackLevel
//					- defenseLevel;
//
//			// オリジナルINTによる魔法命中
//			if (_calcType == PC_PC || _calcType == PC_NPC) {
//				probability += 2 * _pc.getOriginalMagicHit();
//			}
// 念のため、リニューアル前の計算式を残しておく
//		} else if (skillId == GUARD_BRAKE || skillId == RESIST_FEAR
//				|| skillId == HORROR_OF_DEATH) {
//			int dice = l1skills.getProbabilityDice();
//			int value = l1skills.getProbabilityValue();
//			int diceCount = 0;
//			diceCount = getMagicBonus() + getMagicLevel();
//
//			if (diceCount < 1) {
//				diceCount = 1;
//			}
//
//			for (int i = 0; i < diceCount; i++) {
//				probability += (_random.nextInt(dice) + 1 + value);
//			}
//
//			probability = probability * getLeverage() / 10;
//
//			// オリジナルINTによる魔法命中
//			if (_calcType == PC_PC || _calcType == PC_NPC) {
//				probability += 2 * _pc.getOriginalMagicHit();
//			}
//
//			if (probability >= getTargetMr()) {
//				probability = 100;
//			} else {
//				probability = 0;
//			}
		} else if (skillId == GUARD_BRAKE || skillId == RESIST_FEAR
				|| skillId == HORROR_OF_DEATH) { // ガードブレイク、フィアー、ホラーオブデス
			probability = 100; // リニューアル後、必中となった
		} else if (skillId == THUNDER_GRAB) { // サンダーグラップ
			// 成功確率は probability_value(50%) * (自分のレベル / 対象のレベル) + ランダム(0〜-20)
			probability = l1skills.getProbabilityValue()
					* (attackLevel / Math.max(1, defenseLevel))
					- _random.nextInt(21);

			// オリジナルINTによる魔法命中
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getOriginalMagicHit();
			}
			// } else if (skillId == BONE_BREAK) {
			// probability = l1skills.getProbabilityValue();
		} else if (skillId == PHANTASM) { // ファンタズム
			// 成功確率は npc=probability_value / pc=probability_value+20%
			if (_calcType == PC_NPC) {
				probability = l1skills.getProbabilityValue();
			} else {
				probability = l1skills.getProbabilityValue() + 20;
			}
		} else {
			int dice = l1skills.getProbabilityDice();
			int diceCount = 0;
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				if (_pc.isWizard()) {
					diceCount = getMagicBonus() + getMagicLevel() + 1;
				} else if (_pc.isElf()) {
					diceCount = getMagicBonus() + getMagicLevel() - 1;
				} else {
					diceCount = getMagicBonus() + getMagicLevel() - 1;
				}
			} else {
				diceCount = getMagicBonus() + getMagicLevel();
			}
			if (diceCount < 1) {
				diceCount = 1;
			}

			for (int i = 0; i < diceCount; i++) {
				probability += (_random.nextInt(dice) + 1);
			}
			probability = probability * getLeverage() / 10;

			// オリジナルINTによる魔法命中
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getOriginalMagicHit();
			}

			probability -= getTargetMr();

			if (skillId == TAMING_MONSTER) {
				double probabilityRevision = 1;
				if ((_targetNpc.getMaxHp() * 1 / 4) > _targetNpc.getCurrentHp()) {
					probabilityRevision = 1.3;
				} else if ((_targetNpc.getMaxHp() * 2 / 4) > _targetNpc
						.getCurrentHp()) {
					probabilityRevision = 1.2;
				} else if ((_targetNpc.getMaxHp() * 3 / 4) > _targetNpc
						.getCurrentHp()) {
					probabilityRevision = 1.1;
				}
				probability *= probabilityRevision;
			}
		}

		// 状態異常に対する耐性
		if (skillId == EARTH_BIND) {
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistHold();
			}
		} else if (skillId == SHOCK_STUN || skillId == MASS_SHOCK_STUN) {
			// || skillId == BONE_BREAK) {
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= 2 * _targetPc.getResistStun();
			}
		} else if (skillId == CURSE_PARALYZE) {
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistStone();
			}
		} else if (skillId == FOG_OF_SLEEPING) {
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistSleep();
			}
		} else if (skillId == ICE_LANCE || skillId == FREEZING_BLIZZARD) {
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistFreeze();
			}
		} else if (skillId == CURSE_BLIND || skillId == DARKNESS
				|| skillId == SHADOW_SLEEP) {
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistBlind();
			}
		}

		return probability;
	}

	/* ■■■■■■■■■■■■■■ 魔法ダメージ算出 ■■■■■■■■■■■■■■ */

	public int calcMagicDamage(int skillId) {
		int damage = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			damage = calcPcMagicDamage(skillId);
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			damage = calcNpcMagicDamage(skillId);
		}

		damage = calcMrDefense(damage);

		damage = calcExceptionMagicDamage(skillId, damage);

		return damage;
	}

	/* ■■■■■■■■■■■■■■ 魔法武器用ダメージ算出 ■■■■■■■■■■■■■■
	 *
	 * 固定ダメージの時に使用。
	 */
	public int calcWeaponSkillDamage(int dmg, int attr) {

		if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			if (_targetNpc.getNpcId() == 99006) { // 幻牛鬼
				return 0;
			}
		}

		int charaIntelligence = 0;

		if (_calcType == PC_PC || _calcType == PC_NPC) { // XXX
			int spByItem = _pc.getSp() - _pc.getTrueSp(); // アイテムによるSP変動
			charaIntelligence = _pc.getInt() + spByItem - 12;
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			int spByItem = _npc.getSp() - _npc.getTrueSp(); // アイテムによるSP変動
			charaIntelligence = _npc.getInt() + spByItem - 12;
		}

		double attrDeffence = calcAttrResistance(attr);

		double coefficient = Math.max(0D, 1.0 + attrDeffence) + 3.0 / 32.0 * charaIntelligence;
		if (coefficient < 0) {
			coefficient = 0;
		}

		dmg *= coefficient;

		return calcMrDefense(dmg);
	}

	// ●●●● プレイヤー へのファイアーウォールの魔法ダメージ算出 ●●●●
	public int calcPcFireWallDamage() {
		int dmg = 0;
		double attrDeffence = calcAttrResistance(L1Skill.ATTR_FIRE);
		L1Skill l1skills = SkillTable.getInstance().findBySkillId(FIRE_WALL);
		dmg = (int) ((1.0 + attrDeffence) * l1skills.getDamageValue());

		if (_targetPc.isFreeze()) {
			dmg = 0;
		}

		if (dmg < 0) {
			dmg = 0;
		}

		return dmg;
	}

	// ●●●● ＮＰＣ へのファイアーウォールの魔法ダメージ算出 ●●●●
	public int calcNpcFireWallDamage() {
		int dmg = 0;
		double attrDeffence = calcAttrResistance(L1Skill.ATTR_FIRE);
		L1Skill l1skills = SkillTable.getInstance().findBySkillId(FIRE_WALL);
		dmg = (int) ((1.0 + attrDeffence) * l1skills.getDamageValue());

		if (_targetNpc.isFreeze()) {
			dmg = 0;
		}

		if (dmg < 0) {
			dmg = 0;
		}

		if (_targetNpc.getNpcId() == 99006) { // 幻牛鬼
			dmg = 0;
		}

		return dmg;
	}

	// ●●●● プレイヤー・ＮＰＣ から プレイヤー への魔法ダメージ算出 ●●●●
	private int calcPcMagicDamage(int skillId) {
		int dmg = 0;
		if (skillId == FINAL_BURN) {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				dmg = _pc.getCurrentMp();
			} else {
				dmg = _npc.getCurrentMp();
			}
		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (dmg * getLeverage()) / 10;
		}

		dmg -= _targetPc.getDamageReductionByArmor(); // 防具によるダメージ軽減

		// TODO マジックドール効果 　ダメージリダクション
		dmg -= L1MagicDoll.getDamageReductionByDoll(_targetPc);


		if (_targetPc.hasSkillEffect(COOKING_4_1) // 料理によるダメージ軽減
				|| _targetPc.hasSkillEffect(COOKING_4_2)
				|| _targetPc.hasSkillEffect(COOKING_4_3)
				|| _targetPc.hasSkillEffect(COOKING_4_4)) {
			dmg -= 2;
		}

		if (_targetPc.isCookingReduction()) { // 幻想料理によるダメージ軽減
			dmg -= 5;
		}
		
		if (_targetPc.hasSkillEffect(COOKING_1_7_S) // デザートによるダメージ軽減
				|| _targetPc.hasSkillEffect(COOKING_2_7_S)
				|| _targetPc.hasSkillEffect(COOKING_3_7_S)) {
			dmg -= 5;
		}

		if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
			dmg -= _targetPc.getLevel() / 10;
		}
		if (_targetPc.hasSkillEffect(DRAGON_SKIN)) {
			//dmg -= 2;
			//dmg -= 3;  リニューアル後
			dmg -= 5; // キャラクターケアアップデート
		}

		if (_targetPc.hasSkillEffect(PATIENCE)) {
			dmg -= 2;
		}

		if (_calcType == NPC_PC) { // ペット、サモンからプレイヤーに攻撃
			boolean isNowWar = false;
			int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
			if (castleId > 0) {
				isNowWar = WarTimeController.getInstance().isNowWar(castleId);
			}
			if (!isNowWar) {
				if (_npc instanceof L1PetInstance) {
					dmg /= 8;
				}
				if (_npc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _npc;
					if (summon.isExsistMaster()) {
						dmg /= 8;
					}
				}
			}
		}

		if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 2;
		}
		if (_targetPc.isFreeze()) {
			dmg = 0;
		}

		if (_targetPc.hasSkillEffect(COUNTER_MIRROR)) {
			if (_calcType == PC_PC) {
				if (_targetPc.getInt() >= _random.nextInt(100)) {
					_pc.sendPackets(new S_DoActionGFX(_pc.getId(),
							ActionCodes.ACTION_Damage));
					_pc.broadcastPacket(new S_DoActionGFX(_pc.getId(),
							ActionCodes.ACTION_Damage));
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(),
							4395));
					_targetPc.broadcastPacket(new S_SkillSound(_targetPc
							.getId(), 4395));
					_pc.receiveDamage(_targetPc, dmg, false);
					dmg = 0;
					_targetPc.killSkillEffectTimer(COUNTER_MIRROR);
				}
			} else if (_calcType == NPC_PC) {
				int npcId = _npc.getNpcTemplate().getNpcId();
				if (npcId == 45681 || npcId == 45682 || npcId == 45683
						|| npcId == 45684) {
				} else if (!_npc.getNpcTemplate().enableErase()) {
				} else {
					if (_targetPc.getInt() >= _random.nextInt(100)) {
						_npc.broadcastPacket(new S_DoActionGFX(_npc.getId(),
								ActionCodes.ACTION_Damage));
						_targetPc.sendPackets(new S_SkillSound(_targetPc
								.getId(), 4395));
						_targetPc.broadcastPacket(new S_SkillSound(_targetPc
								.getId(), 4395));
						_npc.receiveDamage(_targetPc, dmg);
						dmg = 0;
						_targetPc.killSkillEffectTimer(COUNTER_MIRROR);
					}
				}
			}
		}

		if (dmg < 0) {
			dmg = 0;
		}

		return dmg;
	}

	// ●●●● プレイヤー・ＮＰＣ から ＮＰＣ へのダメージ算出 ●●●●
	private int calcNpcMagicDamage(int skillId) {
		int dmg = 0;
		if (skillId == FINAL_BURN) {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				dmg = _pc.getCurrentMp();
			} else {
				dmg = _npc.getCurrentMp();
			}
		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (dmg * getLeverage()) / 10;
		}

		if (_calcType == PC_NPC) { // プレイヤーからペット、サモンに攻撃
			boolean isNowWar = false;
			int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
			if (castleId > 0) {
				isNowWar = WarTimeController.getInstance().isNowWar(castleId);
			}
			if (!isNowWar) {
				if (_targetNpc instanceof L1PetInstance) {
					dmg /= 8;
				}
				if (_targetNpc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _targetNpc;
					if (summon.isExsistMaster()) {
						dmg /= 8;
					}
				}
			}
		}

		if (_targetNpc.isFreeze()) {
			dmg = 0;
		}

		if (_calcType == PC_NPC && _targetNpc != null) {
			int npcId = _targetNpc.getNpcTemplate().getNpcId();
			if (npcId == 45878 && _pc.getWeapon().getItem().getItemId() != 246 ) { // ドレイクの幽霊
				dmg = 0;
			}
			if (npcId >= 45912 && npcId <= 45915 // 恨みに満ちたソルジャー＆ソルジャーゴースト
					&& !_pc.hasSkillEffect(STATUS_HOLY_WATER)) {
				dmg = 0;
			}
			if (npcId == 45916 // 恨みに満ちたハメル将軍
					&& !_pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
				dmg = 0;
			}
			if (npcId == 45941 // 呪われた巫女サエル
					&& !_pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
				dmg = 0;
			}
			if (npcId == 45752 // バルログ(変身前)
					&& !_pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				dmg = 0;
			}
			if (npcId == 45753 // バルログ(変身後)
					&& !_pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				dmg = 0;
			}
			if (npcId == 45675 // ヤヒ(変身前)
					&& !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 81082 // ヤヒ(変身後)
					&& !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 45625 // 混沌
					&& !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 45674 // 死
					&& !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 45685 // 堕落
					&& !_pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId >= 46068 && npcId <= 46091 // 欲望の洞窟側mob
					&& _pc.getTempCharGfx() == 6035) {
				dmg = 0;
			}
			if (npcId >= 46092 && npcId <= 46106 // 影の神殿側mob
					&& _pc.getTempCharGfx() == 6034) {
				dmg = 0;
			}
			if (npcId == 99006) { // 幻牛鬼
				dmg = 2;
			}
		}

		return dmg;
	}

	// ●●●● damage_dice、damage_dice_count、damage_value、SPから魔法ダメージを算出 ●●●●
	private int calcMagicDiceDamage(int skillId) {
		L1Skill l1skills = SkillTable.getInstance().findBySkillId(skillId);
		int dice = l1skills.getDamageDice();
		int diceCount = l1skills.getDamageDiceCount();
		double value = l1skills.getDamageValue();
		int magicDamage = 0;
		int charaIntelligence = 0;

		for (int i = 0; i < diceCount; i++) {
			magicDamage += (_random.nextInt(dice) + 1);
		}
		magicDamage += value;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int weaponAddDmg = 0; // 武器による追加ダメージ
			L1ItemInstance weapon = _pc.getWeapon();
			if (weapon != null) {
				weaponAddDmg = weapon.getItem().getMagicDmgModifier();
			}
			magicDamage += weaponAddDmg;
		}

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int spByItem = _pc.getSp() - _pc.getTrueSp(); // アイテムによるSP変動
			charaIntelligence = _pc.getInt() + spByItem - 12;
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			int spByItem = _npc.getSp() - _npc.getTrueSp(); // アイテムによるSP変動
			charaIntelligence = _npc.getInt() + spByItem - 12;
		}
		if (charaIntelligence < 1) {
			charaIntelligence = 1;
		}

		double attrDeffence = calcAttrResistance(l1skills.getAttr());

		double coefficient = (1.0 + attrDeffence + charaIntelligence * 3.0 / 32.0);
		if (coefficient < 0) {
			coefficient = 0;
		}

		magicDamage *= coefficient;

		if (_calcType == PC_PC || _calcType == NPC_PC) { // 連続魔法ダメージ軽減
			long nowTime = System.currentTimeMillis();
			long oldTime = _targetPc.getOldTime();

			if (oldTime != 0) {
				long interval = nowTime - oldTime;
				int index = _targetPc.getNumberOfDamaged() - 1;

				if (2000 > interval) {
					double coefficient_r = 2.0 / 3.0;
					if (index == 0) {
						_targetPc.setOldTime(nowTime);
					}
					if (index > 8) {
						index = 8;
					}
					double coefficient_R = Math.pow(coefficient_r, index);

					magicDamage *= coefficient_R;
					if (index < 8) {
						_targetPc.addNumberOfDamaged(1);
					}
				} else {
					if (4000 > interval && index > 0) {
						_targetPc.setNumberOfDamaged(2);
						_targetPc.setOldTime(oldTime + 2000);
					} else {
						_targetPc.setNumberOfDamaged(1);
						_targetPc.setOldTime(nowTime);
					}
				}
			} else {
				_targetPc.addNumberOfDamaged(1);
				_targetPc.setOldTime(nowTime);
			}
		}

		double criticalCoefficient = 1.5; // 魔法クリティカル
		int rnd = _random.nextInt(100) + 1;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			if (l1skills.getSkillLevel() <= 6) {
				if (rnd <= (10 + getMagicCritical())) {
					magicDamage *= criticalCoefficient;
				}
			}
		}

		if (_calcType == PC_PC || _calcType == PC_NPC) { // オリジナルINTによる魔法ダメージ
			magicDamage += _pc.getOriginalMagicDamage();
		}
		if (_calcType == PC_PC || _calcType == PC_NPC) { // アバターによる追加ダメージ
			if (_pc.hasSkillEffect(ILLUSION_AVATAR)) {
				magicDamage += 10;
			}
		}

		return magicDamage;
	}

	// ●●●● ヒール回復量（対アンデッドにはダメージ）を算出 ●●●●
	public int calcHealing(int skillId) {
		L1Skill l1skills = SkillTable.getInstance().findBySkillId(skillId);
		int dice = l1skills.getDamageDice();
		double value = l1skills.getDamageValue();
		int magicDamage = 0;

		int magicBonus = getMagicBonus();
		if (magicBonus > 10) {
			magicBonus = 10;
		}

		int diceCount = (int) (value + magicBonus);
		for (int i = 0; i < diceCount; i++) {
			magicDamage += (_random.nextInt(dice) + 1);
		}

		double alignmentRevision = 1.0;
		if (getLawful() > 0) {
			alignmentRevision += (getLawful() / 32768.0);
		}

		magicDamage *= alignmentRevision;

		magicDamage = (magicDamage * getLeverage()) / 10;

		return magicDamage;
	}

	// ●●●● ＭＲによるダメージ軽減 ●●●●
	private int calcMrDefense(int dmg) {
		int mr = getTargetMr();

		double mrFloor = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			if (mr <= 100) {
				mrFloor = Math.floor((mr - _pc.getOriginalMagicHit()) / 2);
			} else if (mr >= 100) {
				mrFloor = Math.floor((mr - _pc.getOriginalMagicHit()) / 10);
			}
			double mrCoefficient = 0;
			if (mr <= 100) {
				mrCoefficient = 1 - 0.01 * mrFloor;
			} else if (mr >= 100) {
				mrCoefficient = 0.6 - 0.01 * mrFloor;
			}
			dmg *= mrCoefficient;
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			int rnd = _random.nextInt(100) + 1;
			if (mr >= rnd) {
				dmg /= 2;
			}
		}
		// 魔眼によるダメージ軽減
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (_targetPc.hasSkillEffect(MAGIC_EYE_OF_FAFURION)
						|| _targetPc.hasSkillEffect(MAGIC_EYE_OF_BIRTH)
						|| _targetPc.hasSkillEffect(MAGIC_EYE_OF_SHAPE)
						|| _targetPc.hasSkillEffect(MAGIC_EYE_OF_LIFE)) {
				int _resistChance = _random.nextInt(100) + 1;
				if (_resistChance <= 10) {
					dmg /= 2;
				}
			}
		}

		return dmg;
	}

	// ●●●● 属性によるダメージ軽減 ●●●●
	// attr:0.無属性魔法,1.地魔法,2.火魔法,4.水魔法,8.風魔法(,16.光魔法)
	private double calcAttrResistance(int attr) {
		int resist = 0;
		int sign = 1;
		double attrDefCoeff = 0.0D;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			attrDefCoeff = calcPcArmorCoefficient(attr) + calcPcMagicCoefficient(attr);
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			if (attr == L1Skill.ATTR_EARTH) {
				resist = _targetNpc.getEarth();
			} else if (attr == L1Skill.ATTR_FIRE) {
				resist = _targetNpc.getFire();
			} else if (attr == L1Skill.ATTR_WATER) {
				resist = _targetNpc.getWater();
			} else if (attr == L1Skill.ATTR_WIND) {
				resist = _targetNpc.getWind();
			}
			if (resist >= 0) {
				sign = 1;
			} else {
				sign = -1;
			}
			attrDefCoeff =  -1 / 32.0 * sign * Math.floor(0.32 * Math.abs(resist));
		}
		return attrDefCoeff;
	}

	/**
	 * 魔法による属性軽減係数の算出。
	 */
	private double calcPcMagicCoefficient(int attr) {
		double sumCoeff = 0D;
		int attrDeff = 0;

		if (_targetPc.hasSkillEffect(ELEMENTAL_PROTECTION)) {
			if (attr == _targetPc.getElfAttr()) {
				attrDeff = 50;
				sumCoeff += -1 / 32.0 * Math.floor(0.32 * Math.abs(attrDeff));
			}
		}
		if (_targetPc.hasSkillEffect(RESIST_ELEMENTAL)) {
			attrDeff = 10;
			sumCoeff += -1 / 32.0 * Math.floor(0.32 * Math.abs(attrDeff));
		}
		if (_targetPc.hasSkillEffect(COOKING_1_0_N) || _targetPc.hasSkillEffect(COOKING_1_0_S)) {
			attrDeff = 10;
			sumCoeff += -1 / 32.0 * Math.floor(0.32 * Math.abs(attrDeff));
		}
		if (_targetPc.hasSkillEffect(COOKING_3_4_N) || _targetPc.hasSkillEffect(COOKING_3_4_S)) {
			attrDeff = 10;
			sumCoeff += -1 / 32.0 * Math.floor(0.32 * Math.abs(attrDeff));
		}
		if (_targetPc.hasSkillEffect(COOKING_4_1)
						|| _targetPc.hasSkillEffect(COOKING_4_2)
						|| _targetPc.hasSkillEffect(COOKING_4_3)) {
			attrDeff = 10;
			sumCoeff += -1 / 32.0 * Math.floor(0.32 * Math.abs(attrDeff));
		}
		if (_targetPc.hasSkillEffect(STATUS_CUBE_IGNITION_TO_ALLY) && attr == L1Skill.ATTR_FIRE) {
			attrDeff = 30;
			sumCoeff += -1 / 32.0 * Math.floor(0.32 * Math.abs(attrDeff));
		}
		if (_targetPc.hasSkillEffect(STATUS_CUBE_QUAKE_TO_ALLY) && attr == L1Skill.ATTR_EARTH) {
			attrDeff = 30;
			sumCoeff += -1 / 32.0 * Math.floor(0.32 * Math.abs(attrDeff));
		}
		if (_targetPc.hasSkillEffect(STATUS_CUBE_SHOCK_TO_ALLY) && attr == L1Skill.ATTR_WIND) {
			attrDeff = 30;
			sumCoeff += -1 / 32.0 * Math.floor(0.32 * Math.abs(attrDeff));
		}
		if (_targetPc.hasSkillEffect(ELEMENTAL_FALL_DOWN) && attr == _targetPc.getAddAttrKind()) {
			attrDeff -= 50;
			sumCoeff += -1 / 32.0 * -1 * Math.floor(0.32 * Math.abs(attrDeff));
		}
		return sumCoeff;
	}

	/**
	 * PCの装備による属性軽減係数。魔法や料理による属性耐性係数は算出されない。
	 */
	private double calcPcArmorCoefficient(int attr) {
		double sumCoeff = 0D;
		int attrDeff = 0;
		int sign = 1;
		for (L1ItemInstance armor : _targetPc.getEquipSlot().getArmors()) {
			if ((attr & L1Skill.ATTR_EARTH) == L1Skill.ATTR_EARTH) {
				attrDeff = armor.getItem().getDefenseEarth();
			} else if ((attr & L1Skill.ATTR_FIRE) == L1Skill.ATTR_FIRE) {
				attrDeff = armor.getItem().getDefenseFire();
			} else if ((attr & L1Skill.ATTR_WATER) == L1Skill.ATTR_WATER) {
				attrDeff = armor.getItem().getDefenseWater();
			} else if ((attr & L1Skill.ATTR_WIND) == L1Skill.ATTR_WIND) {
				attrDeff = armor.getItem().getDefenseWind();
			}

			if (attrDeff != 0) {
				if (attrDeff < 0) {
					sign = -1;
				} else {
					sign = 1;
				}
				sumCoeff += -1 / 32.0 * sign * Math.floor(0.32 * Math.abs(attrDeff));
				attrDeff = 0;
			}
		}
		return sumCoeff;
	}

	/* ■■■■■■■■■■■■■■■ 計算結果反映 ■■■■■■■■■■■■■■■ */

	public void commit(int damage, int drainMana) {
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			commitPc(damage, drainMana);
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			commitNpc(damage, drainMana);
		}

		// ダメージ値及び命中率確認用メッセージ
		if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.getAttackLog()) {
			return;
		}
		if ((_calcType == PC_PC || _calcType == NPC_PC) && !_targetPc.getAttackLog()) {
			return;
		}

		String msg0 = "";
		String msg1 = I18N_ATTACK_TO;
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";

		if (_calcType == PC_PC || _calcType == PC_NPC) {// アタッカーがＰＣの場合
			msg0 = _pc.getName();
		} else if (_calcType == NPC_PC) { // アタッカーがＮＰＣの場合
			msg0 = _npc.getName();
		}

		if (_calcType == NPC_PC || _calcType == PC_PC) { // ターゲットがＰＣの場合
			msg4 = _targetPc.getName();
			msg2 = "THP" + _targetPc.getCurrentHp();
		} else if (_calcType == PC_NPC) { // ターゲットがＮＰＣの場合
			msg4 = _targetNpc.getName();
			msg2 = "THp" + _targetNpc.getCurrentHp();
		}

		msg3 = String.format(I18N_SKILL_DMG, damage); // %dのスキルダメージを与えました。

		if (_calcType == PC_PC || _calcType == PC_NPC) { // アタッカーがＰＣの場合
			//_pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3, msg4));
			// \f1%0が%4%1%3 %2
			_pc.sendPackets(new S_SystemMessage(I18N_SKILL_GAVE_TEXT_COLOR +
				MessageFormat.format(I18N_ATTACK_FORMAT, msg0, msg4, msg3, msg2)));
			// {0}が{1}に{2} {3}
		}
		if (_calcType == NPC_PC || _calcType == PC_PC) { // ターゲットがＰＣの場合
			//_targetPc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3, msg4));
			// \f1%0が%4%1%3 %2
			_targetPc.sendPackets(new S_SystemMessage(I18N_SKILL_RECEIVED_TEXT_COLOR +
				MessageFormat.format(I18N_ATTACK_FORMAT, msg0, msg4, msg3, msg2), true));
			// {0}が{1}に{2} {3}
		}
	}

	// ●●●● プレイヤーに計算結果を反映 ●●●●
	private void commitPc(int damage, int drainMana) {
		if (_calcType == PC_PC) {
			if (drainMana > 0 && _targetPc.getCurrentMp() > 0) {
				if (drainMana > _targetPc.getCurrentMp()) {
					drainMana = _targetPc.getCurrentMp();
				}
				int newMp = _pc.getCurrentMp() + drainMana;
				_pc.setCurrentMp(newMp);
			}
			_targetPc.receiveManaDamage(_pc, drainMana);
			_targetPc.receiveDamage(_pc, damage, true);
		} else if (_calcType == NPC_PC) {
			_targetPc.receiveDamage(_npc, damage, true);
		}
	}

	// ●●●● ＮＰＣに計算結果を反映 ●●●●
	private void commitNpc(int damage, int drainMana) {
		if (_calcType == PC_NPC) {
			if (drainMana > 0) {
				int drainValue = _targetNpc.drainMana(drainMana);
				int newMp = _pc.getCurrentMp() + drainValue;
				_pc.setCurrentMp(newMp);
			}
			_targetNpc.ReceiveManaDamage(_pc, drainMana);
			_targetNpc.receiveDamage(_pc, damage);
		} else if (_calcType == NPC_NPC) {
			_targetNpc.receiveDamage(_npc, damage);
		}
	}

	// MRに依存しない魔法ダメージ計算処理
	private int calcExceptionMagicDamage(int skillId, int dmg) {

		L1Skill l1skills = SkillTable.getInstance().findBySkillId(skillId);
		if (skillId == MIND_BREAK) {
			// マインドブレク sp * value
			int sp = getSpellPower();
			dmg = (int) (l1skills.getDamageValue() * sp);
			// MPを5減少させる
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				if (_targetPc.getCurrentMp() >= 5) {
					_targetPc.setCurrentMp(_targetPc.getCurrentMp() - 5);
				} else {
					_targetPc.setCurrentMp(0);
				}
			} else if (_calcType == NPC_NPC || _calcType == PC_NPC) {
				if (_targetNpc.getCurrentMp() >= 5) {
					_targetNpc.setCurrentMp(_targetNpc.getCurrentMp() - 5);
				} else {
					_targetNpc.setCurrentMp(0);
				}
			}
		} else if (skillId == CONFUSION) {
			// コンフュージョン sp * value
			int sp = getSpellPower();
			dmg = (int) (l1skills.getDamageValue() * sp);
		} else if (skillId == JOY_OF_PAIN) {
			// ジョイオブペイン (MaxHp-currentHp)/5
			dmg = ((_pc.getMaxHp() - _pc.getCurrentHp()) / 5);
		}

		return dmg;
	}
}