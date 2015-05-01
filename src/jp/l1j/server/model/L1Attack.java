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
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.controller.timer.WarTimeController;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.gametime.L1GameTimeClock;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import jp.l1j.server.model.poison.L1DamagePoison;
import jp.l1j.server.model.poison.L1ParalysisPoison;
import jp.l1j.server.model.poison.L1SilencePoison;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_AttackMissPacket;
import jp.l1j.server.packets.server.S_AttackPacket;
import jp.l1j.server.packets.server.S_AttackPacketForNpc;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_GreenMessage;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillIconGFX;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.packets.server.S_UseArrowSkill;
import jp.l1j.server.packets.server.S_UseAttackSkill;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1MagicDoll;
import jp.l1j.server.templates.L1Skill;
import jp.l1j.server.types.Point;

public class L1Attack {
	private static Logger _log = Logger.getLogger(L1Attack.class.getName());

	private L1PcInstance _pc = null;

	private L1Character _target = null;

	private L1PcInstance _targetPc = null;

	private L1NpcInstance _npc = null;

	private L1NpcInstance _targetNpc = null;

	private L1Magic _magic = null;

	private final int _targetId;

	private int _targetX;

	private int _targetY;

	private int _statusDamage = 0;

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	private int _hitRate = 0;

	private int _calcType;

	private static final int PC_PC = 1;

	private static final int PC_NPC = 2;

	private static final int NPC_PC = 3;

	private static final int NPC_NPC = 4;

	private byte _effectId = 0;

	private boolean _isHit = false;

	private int _damage = 0;

	private int _drainMana = 0;

	private int _drainHp = 0;

	private int _attckGrfxId = 0;

	private int _attckActId = 0;

	// 攻撃者がプレイヤーの場合の武器情報
	private L1ItemInstance weapon = null;

	private int _weaponId = 0;

	private int _weaponType = 0;

	private int _weaponType2 = 0;

	private int _weaponAddHit = 0;

	private int _weaponAddDmg = 0;

	private int _weaponSmall = 0;

	private int _weaponLarge = 0;

	private int _weaponRange = 1;

	private int _weaponBless = 0;

	private int _weaponEnchant = 0;

	private int _weaponMaterial = 0;

	private int _weaponDoubleDmgChance = 0;

	private int _weaponWeaknessExposureChance = 0;

	private int _weaponAttrEnchantKind = 0;

	private int _weaponAttrEnchantLevel = 0;

	private L1ItemInstance _arrow = null;

	private L1ItemInstance _sting = null;

	private int _leverage = 10; // 1/10倍で表現する。

	private int _skillId;

	private double _skillDamage = 0;

	public void setLeverage(int i) {
		_leverage = i;
	}

	private int getLeverage() {
		return _leverage;
	}

	// 攻撃者がプレイヤーの場合のステータスによる補正
	// private static final int[] strHit = { -2, -2, -2, -2, -2, -2, -2, -2, -2,
	// -2, -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9,
	// 9, 10, 10, 11, 11, 12, 12, 13, 13, 14 };

	// private static final int[] dexHit = { -2, -2, -2, -2, -2, -2, -2, -2, -2,
	// -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8,
	// 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14 };

	/*
	 * private static final int[] strHit = { -2, -2, -2, -2, -2, -2, -2, //
	 * 0〜7まで -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, //
	 * 8〜26まで 7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12, //
	 * 27〜44まで 13, 13, 13, 14, 14, 14, 15, 15, 15, 16, 16, 16, 17, 17, 17}; //
	 * 45〜59まで
	 *
	 * private static final int[] dexHit = { -2, -2, -2, -2, -2, -2, -1, -1, 0,
	 * 0, // 1〜10まで 1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
	 * 15, 16, // 11〜30まで 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
	 * 30, 31, // 31〜45まで 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44,
	 * 45, 46 }; // 46〜60まで
	 *
	 * private static final int[] strDmg = new int[128];
	 *
	 * static { // ＳＴＲダメージ補正 int dmg = -6; for (int str = 0; str <= 22; str++) {
	 * // ０〜２２は２毎に＋１ if (str % 2 == 1) { dmg++; } strDmg[str] = dmg; } for (int
	 * str = 23; str <= 28; str++) { // ２３〜２８は３毎に＋１ if (str % 3 == 2) { dmg++; }
	 * strDmg[str] = dmg; } for (int str = 29; str <= 32; str++) { //
	 * ２９〜３２は２毎に＋１ if (str % 2 == 1) { dmg++; } strDmg[str] = dmg; } for (int
	 * str = 33; str <= 39; str++) { // ３３〜３９は１毎に＋１ dmg++; strDmg[str] = dmg; }
	 * for (int str = 40; str <= 46; str++) { // ４０〜４６は１毎に＋２ dmg += 2;
	 * strDmg[str] = dmg; } for (int str = 47; str <= 127; str++) { //
	 * ４７〜１２７は１毎に＋１ dmg++; strDmg[str] = dmg; } }
	 *
	 * private static final int[] dexDmg = new int[128];
	 *
	 * static { // ＤＥＸダメージ補正 for (int dex = 0; dex <= 14; dex++) { // ０〜１４は０
	 * dexDmg[dex] = 0; } dexDmg[15] = 1; dexDmg[16] = 2; dexDmg[17] = 3;
	 * dexDmg[18] = 4; dexDmg[19] = 4; dexDmg[20] = 4; dexDmg[21] = 5;
	 * dexDmg[22] = 5; dexDmg[23] = 5; int dmg = 5; for (int dex = 24; dex <=
	 * 127; dex++) { // ２４〜１２７は１毎に＋１ dmg++; dexDmg[dex] = dmg; } }
	 */

	private static final int[] strHit = { -2, -2, -2, -2, -2, -2, -2, // 1〜7
			-2, -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 5, 6, 6, 6, // 8〜26
			7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12, // 27〜44
			13, 13, 13, 14, 14, 14, 15, 15, 15, 16, 16, 16, 17, 17, 17 }; // 45〜59
	private static final int[] dexHit = { -2, -2, -2, -2, -2, -2, -1, -1, 0, 0, // 1〜10
			1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, // 11〜30
			17, 18, 19, 19, 19, 20, 20, 20, 21, 21, 21, 22, 22, 22, 23, // 31〜45
			23, 23, 24, 24, 24, 25, 25, 25, 26, 26, 26, 27, 27, 27, 28 }; // 46〜60

	private static final int[] strDmg = new int[128];

	static {
		// STRダメージ補正
		int dmg = -6;
		for (int str = 0; str <= 22; str++) { // 0〜22は2毎に+1
			if (str % 2 == 1) {
				dmg++;
			}
			strDmg[str] = dmg;
		}
		for (int str = 23; str <= 28; str++) { // 23〜28は3毎に+1
			if (str % 3 == 2) {
				dmg++;
			}
			strDmg[str] = dmg;
		}
		for (int str = 29; str <= 32; str++) { // 29〜32は2毎に+1
			if (str % 2 == 1) {
				dmg++;
			}
			strDmg[str] = dmg;
		}
		for (int str = 33; str <= 34; str++) { // 33〜34は1毎に+1
			dmg++;
			strDmg[str] = dmg;
		}
		for (int str = 35; str <= 127; str++) { // 35〜127は4毎に+1
			if (str % 4 == 1) {
				dmg++;
			}
			strDmg[str] = dmg;
		}
	}

	private static final int[] dexDmg = new int[128];

	static {
		// DEXダメージ補正
		for (int dex = 0; dex <= 14; dex++) {
			// 0〜14は0
			dexDmg[dex] = 0;
		}
		dexDmg[15] = 1;
		dexDmg[16] = 2;
		dexDmg[17] = 3;
		dexDmg[18] = 4;
		dexDmg[19] = 4;
		dexDmg[20] = 4;
		dexDmg[21] = 5;
		dexDmg[22] = 5;
		dexDmg[23] = 5;
		int dmg = 5;
		for (int dex = 24; dex <= 35; dex++) { // 24〜35は3毎に+1
			if (dex % 3 == 0) {
				dmg++;
			}
			dexDmg[dex] = dmg;
		}
		for (int dex = 36; dex <= 127; dex++) { // 36〜127は4毎に1
			if (dex % 4 == 0) {
				dmg++;
			}
			dexDmg[dex] = dmg;
		}
	}

	public void setActId(int actId) {
		_attckActId = actId;
	}

	public void setGfxId(int gfxId) {
		_attckGrfxId = gfxId;
	}

	public int getActId() {
		return _attckActId;
	}

	public int getGfxId() {
		return _attckGrfxId;
	}

	public L1Attack(L1Character attacker, L1Character target) {
		this(attacker, target, 0);
	}

	public L1Attack(L1Character attacker, L1Character target, int skillId) {
		_skillId = skillId;
		if (_skillId != 0) {
			L1Skill skills = SkillTable.getInstance().findBySkillId(_skillId);
			_skillDamage = skills.getDamageValue();
		} else {
			_skillDamage = 0;
		}

		if (attacker instanceof L1PcInstance) {
			_pc = (L1PcInstance) attacker;
			if (target instanceof L1PcInstance) {
				_targetPc = (L1PcInstance) target;
				_calcType = PC_PC;
			} else if (target instanceof L1NpcInstance) {
				_targetNpc = (L1NpcInstance) target;
				_calcType = PC_NPC;
			}
			// 武器情報の取得
			weapon = _pc.getWeapon();
			if (weapon != null) {
				_weaponId = weapon.getItem().getItemId();
				_weaponType = weapon.getItem().getType1();
				_weaponType2 = weapon.getItem().getType();
				_weaponAddHit = weapon.getItem().getHitModifier()
						+ weapon.getHitByMagic();
				_weaponAddDmg = weapon.getItem().getDmgModifier()
						+ weapon.getDmgByMagic();
				_weaponSmall = weapon.getItem().getDmgSmall();
				_weaponLarge = weapon.getItem().getDmgLarge();
				_weaponRange = weapon.getItem().getRange();
				_weaponBless = weapon.getItem().getBless();
				if (_weaponType != 20 && _weaponType != 62) { // 近接武器
					_weaponEnchant = weapon.getEnchantLevel()
							- weapon.getDurability(); // 損傷分マイナス
				} else {
					_weaponEnchant = weapon.getEnchantLevel();
				}
				_weaponMaterial = weapon.getItem().getMaterial();
				if (_weaponType == 20) { // アローの取得
					_arrow = _pc.getInventory().getArrow();
					if (_arrow != null) {
						_weaponBless = _arrow.getItem().getBless();
						_weaponMaterial = _arrow.getItem().getMaterial();
					}
				}
				if (_weaponType == 62) { // スティングの取得
					_sting = _pc.getInventory().getSting();
					if (_sting != null) {
						_weaponBless = _sting.getItem().getBless();
						_weaponMaterial = _sting.getItem().getMaterial();
					}
				}
				_weaponDoubleDmgChance = weapon.getItem().getDoubleDmgChance();
				_weaponWeaknessExposureChance = weapon.getItem()
						.getWeaknessExposure();
				_weaponAttrEnchantKind = weapon.getAttrEnchantKind();
				_weaponAttrEnchantLevel = weapon.getAttrEnchantLevel();
			}
			// ステータスによる追加ダメージ補正
			if (_weaponType == 20) { // 弓の場合はＤＥＸ値参照
				_statusDamage = dexDmg[_pc.getDex()];
			} else { // それ以外はＳＴＲ値参照
				_statusDamage = strDmg[_pc.getStr()];
			}
		} else if (attacker instanceof L1NpcInstance) {
			_npc = (L1NpcInstance) attacker;
			if (target instanceof L1PcInstance) {
				_targetPc = (L1PcInstance) target;
				_calcType = NPC_PC;
			} else if (target instanceof L1NpcInstance) {
				_targetNpc = (L1NpcInstance) target;
				_calcType = NPC_NPC;
			}
		}
		_target = target;
		_targetId = target.getId();
		_targetX = target.getX();
		_targetY = target.getY();
	}

	/* ■■■■■■■■■■■■■■■■ 命中判定 ■■■■■■■■■■■■■■■■ */

	public boolean calcHit() {
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			if (_weaponRange != -1) {
				if (_pc.getLocation()
						.getTileLineDistance(_target.getLocation()) > _weaponRange + 1) { // BIGのモンスターに対応するため射程範囲
					// +
					// 1
					_isHit = false; // 射程範囲外
					return _isHit;
				}
			} else {
				if (!_pc.getLocation().isInScreen(_target.getLocation())) {
					_isHit = false; // 射程範囲外
					return _isHit;
				}
			}
			if (_weaponType == 20 && _weaponId != 190 && _arrow == null) {
				_isHit = false; // 矢がない場合はミス
			} else if (_weaponType == 62 && _sting == null) {
				_isHit = false; // スティングがない場合はミス
			} else if (!(_pc.glanceCheck(_pc.getX(), _pc.getY(), _targetX, _targetY)
							|| _pc.glanceCheck(_targetX, _targetY, _pc.getX(), _pc.getY()))) {
				_isHit = false; // 攻撃者がプレイヤーの場合は障害物判定
			} else if (_weaponId == 247 || _weaponId == 248 || _weaponId == 249) {
				_isHit = false; // 試練の剣B〜C 攻撃無効
			} else if (_calcType == PC_PC) {
				_isHit = calcPcPcHit();
			} else if (_calcType == PC_NPC) {
				_isHit = calcPcNpcHit();
			}
			if (_calcType == PC_NPC && _weaponId != 246 &&
					_targetNpc.getNpcTemplate().getNpcId() == 45878) {
				_isHit = false; // 試練の剣A以外でドレイクの幽霊への攻撃を無効
			}
		} else if (_calcType == NPC_PC) {
			_isHit = calcNpcPcHit();
		} else if (_calcType == NPC_NPC) {
			_isHit = calcNpcNpcHit();
		}
		return _isHit;
	}

	// ●●●● プレイヤー から プレイヤー への命中判定 ●●●●
	/*
	 * ＰＣへの命中率 ＝（PCのLv＋クラス補正＋STR補正＋DEX補正＋武器補正＋DAIの枚数/2＋魔法補正）×0.68−10
	 * これで算出された数値は自分が最大命中(95%)を与える事のできる相手側PCのAC そこから相手側PCのACが1良くなる毎に自命中率から1引いていく
	 * 最小命中率5% 最大命中率95%
	 */
	private boolean calcPcPcHit() {

		if (_targetPc.isFreeze()) {
			_hitRate = 0;
			return false;
		}

		// マジックドール效果 - ダメージ回避
		if (L1MagicDoll.getDamageEvasionByDoll(_targetPc) > 0) {
			_hitRate = 0;
			return false;
		}

		if (_weaponType2 == 14) {
			_hitRate = 100; // キーリンクの命中率は100%
			return true;
		}
		_hitRate = _pc.getLevel();

		if (_pc.getStr() > 59) {
			_hitRate += strHit[58];
		} else {
			_hitRate += strHit[_pc.getStr() - 1];
		}

		if (_pc.getDex() > 60) {
			_hitRate += dexHit[59];
		} else {
			_hitRate += dexHit[_pc.getDex() - 1];
		}

		if (_weaponType != 20 && _weaponType != 62) {
			_hitRate += _weaponAddHit + _pc.getHitup() + _pc.getOriginalHitup()
					+ (_weaponEnchant / 2);
		} else {
			_hitRate += _weaponAddHit + _pc.getBowHitup()
					+ _pc.getOriginalBowHitup() + (_weaponEnchant / 2);
		}

		if (_weaponType != 20 && _weaponType != 62) { // 防具による追加命中
			_hitRate += _pc.getHitModifierByArmor();
		} else {
			_hitRate += _pc.getBowHitModifierByArmor();
		}

		if (80 < _pc.getInventory().getWeight240() // 重量による命中補正
				&& 120 >= _pc.getInventory().getWeight240()) {
			_hitRate -= 1;
		} else if (121 <= _pc.getInventory().getWeight240()
				&& 160 >= _pc.getInventory().getWeight240()) {
			_hitRate -= 3;
		} else if (161 <= _pc.getInventory().getWeight240()
				&& 200 >= _pc.getInventory().getWeight240()) {
			_hitRate -= 5;
		}

		if (_pc.hasSkillEffect(COOKING_2_0_N) // 料理による追加命中
				|| _pc.hasSkillEffect(COOKING_2_0_S)
				|| _pc.hasSkillEffect(COOKING_4_1)) {
			if (_weaponType != 20 && _weaponType != 62) {
				_hitRate += 1;
			}
		}
		if (_pc.hasSkillEffect(COOKING_3_2_N) // 料理による追加命中
				|| _pc.hasSkillEffect(COOKING_3_2_S)) {
			if (_weaponType != 20 && _weaponType != 62) {
				_hitRate += 2;
			}
		}
		if (_pc.hasSkillEffect(COOKING_2_3_N) // 料理による追加命中
				|| _pc.hasSkillEffect(COOKING_2_3_S)
				|| _pc.hasSkillEffect(COOKING_3_0_N)
				|| _pc.hasSkillEffect(COOKING_3_0_S)
				|| _pc.hasSkillEffect(COOKING_4_2)) {
			if (_weaponType == 20 || _weaponType == 62) {
				_hitRate += 1;
			}
		}

		int attackerDice = _random.nextInt(20) + 1 + _hitRate - 10;

		// 回避率
		attackerDice -= _targetPc.getDodge();
		attackerDice += _targetPc.getNdodge();

		int defenderDice = 0;

		int defenderValue = (int) (_targetPc.getAc() * 1.5) * -1;

		if (_targetPc.getAc() >= 0) {
			defenderDice = 10 - _targetPc.getAc();
		} else if (_targetPc.getAc() < 0) {
			defenderDice = 10 + _random.nextInt(defenderValue) + 1;
		}

		int fumble = _hitRate - 9;
		int critical = _hitRate + 10;

		if (attackerDice <= fumble) {
			_hitRate = 0;
		} else if (attackerDice >= critical) {
			_hitRate = 100;
		} else {
			if (attackerDice > defenderDice) {
				_hitRate = 100;
			} else if (attackerDice <= defenderDice) {
				_hitRate = 0;
			}
		}

		int rnd = _random.nextInt(100) + 1;
		if (_weaponType == 20 && _hitRate > rnd) { // 弓の場合、ヒットした場合でもERでの回避を再度行う。
			return calcErEvasion();
		}

		return _hitRate >= rnd;

		/*
		 * final int MIN_HITRATE = 5;
		 *
		 * _hitRate = _pc.getLevel();
		 *
		 * if (_pc.getStr() > 39) { _hitRate += strHit[39]; } else { _hitRate +=
		 * strHit[_pc.getStr()]; }
		 *
		 * if (_pc.getDex() > 39) { _hitRate += dexHit[39]; } else { _hitRate +=
		 * dexHit[_pc.getDex()]; }
		 *
		 * if (_weaponType != 20 && _weaponType != 62) { _hitRate +=
		 * _weaponAddHit + _pc.getHitup() + _pc.getOriginalHitup() +
		 * (_weaponEnchant / 2); } else { _hitRate += _weaponAddHit +
		 * _pc.getBowHitup() + _pc .getOriginalBowHitup() + (_weaponEnchant /
		 * 2); }
		 *
		 * if (_weaponType != 20 && _weaponType != 62) { // 防具による追加命中 _hitRate
		 * += _pc.getHitModifierByArmor(); } else { _hitRate +=
		 * _pc.getBowHitModifierByArmor(); }
		 *
		 * int hitAc = (int) (_hitRate 0.68 - 10) -1;
		 *
		 * if (hitAc <= _targetPc.getAc()) { _hitRate = 95; } else { _hitRate =
		 * 95 - (hitAc - _targetPc.getAc()); }
		 *
		 * if (_targetPc.hasSkillEffect(UNCANNY_DODGE)) { _hitRate -= 20; }
		 *
		 * if (_targetPc.hasSkillEffect(MIRROR_IMAGE)) { _hitRate -= 20; }
		 *
		 * if (_pc.hasSkillEffect(COOKING_2_0_N) // 料理による追加命中 ||
		 * _pc.hasSkillEffect(COOKING_2_0_S)) { if (_weaponType != 20 &&
		 * _weaponType != 62) { _hitRate += 1; } } if
		 * (_pc.hasSkillEffect(COOKING_3_2_N) // 料理による追加命中 ||
		 * _pc.hasSkillEffect(COOKING_3_2_S)) { if (_weaponType != 20 &&
		 * _weaponType != 62) { _hitRate += 2; } } if
		 * (_pc.hasSkillEffect(COOKING_2_3_N) // 料理による追加命中 ||
		 * _pc.hasSkillEffect(COOKING_2_3_S) ||
		 * _pc.hasSkillEffect(COOKING_3_0_N) ||
		 * _pc.hasSkillEffect(COOKING_3_0_S)) { if (_weaponType == 20 ||
		 * _weaponType == 62) { _hitRate += 1; } }
		 *
		 * if (_hitRate < MIN_HITRATE) { _hitRate = MIN_HITRATE; }
		 *
		 * if (_weaponType2 == 14) { _hitRate = 100; // キーリンクの命中率は100% }
		 *
		 * if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) { _hitRate = 0; } if
		 * (_targetPc.hasSkillEffect(ICE_LANCE)) { _hitRate = 0; } if
		 * (_targetPc.hasSkillEffect(FREEZING_BLIZZARD)) { _hitRate = 0; } if
		 * (_targetPc.hasSkillEffect(FREEZING_BREATH)) { _hitRate = 0; } if
		 * (_targetPc.hasSkillEffect(EARTH_BIND)) { _hitRate = 0; } int rnd =
		 * _random.nextInt(100) + 1; if (_weaponType == 20 && _hitRate > rnd) {
		 * // 弓の場合、ヒットした場合でもERでの回避を再度行う。 return calcErEvasion(); }
		 *
		 * return _hitRate >= rnd;
		 */
	}

	// ●●●● プレイヤー から ＮＰＣ への命中判定 ●●●●
	private boolean calcPcNpcHit() {
		// ＮＰＣへの命中率
		// ＝（PCのLv＋クラス補正＋STR補正＋DEX補正＋武器補正＋DAIの枚数/2＋魔法補正）×5−{NPCのAC×（-5）}
		if (_targetNpc.isFreeze()) {
			_hitRate = 0;
			return false;
		}
		if (_weaponType2 == 14) {
			_hitRate = 100; // キーリンクの命中率は100%
			// 特定条件有攻可能 NPC判定
			if (_pc.isAttackMiss(_pc, _targetNpc.getNpcTemplate().getNpcId())) {
				_hitRate = 0;
				return false;
			}
			return true;
		}

		_hitRate = _pc.getLevel();

		if (_pc.getStr() > 59) {
			_hitRate += strHit[58];
		} else {
			_hitRate += strHit[_pc.getStr() - 1];
		}

		if (_pc.getDex() > 60) {
			_hitRate += dexHit[59];
		} else {
			_hitRate += dexHit[_pc.getDex() - 1];
		}

		if (_weaponType != 20 && _weaponType != 62) {
			_hitRate += _weaponAddHit + _pc.getHitup() + _pc.getOriginalHitup()
					+ (_weaponEnchant / 2);
		} else {
			_hitRate += _weaponAddHit + _pc.getBowHitup()
					+ _pc.getOriginalBowHitup() + (_weaponEnchant / 2);
		}

		if (_weaponType != 20 && _weaponType != 62) { // 防具による追加命中
			_hitRate += _pc.getHitModifierByArmor();
		} else {
			_hitRate += _pc.getBowHitModifierByArmor();
		}

		if (80 < _pc.getInventory().getWeight240() // 重量による命中補正
				&& 120 >= _pc.getInventory().getWeight240()) {
			_hitRate -= 1;
		} else if (121 <= _pc.getInventory().getWeight240()
				&& 160 >= _pc.getInventory().getWeight240()) {
			_hitRate -= 3;
		} else if (161 <= _pc.getInventory().getWeight240()
				&& 200 >= _pc.getInventory().getWeight240()) {
			_hitRate -= 5;
		}

		if (_pc.hasSkillEffect(COOKING_2_0_N) // 料理による追加命中
				|| _pc.hasSkillEffect(COOKING_2_0_S)
				|| _pc.hasSkillEffect(COOKING_4_1)) {
			if (_weaponType != 20 && _weaponType != 62) {
				_hitRate += 1;
			}
		}
		if (_pc.hasSkillEffect(COOKING_3_2_N) // 料理による追加命中
				|| _pc.hasSkillEffect(COOKING_3_2_S)) {
			if (_weaponType != 20 && _weaponType != 62) {
				_hitRate += 2;
			}
		}
		if (_pc.hasSkillEffect(COOKING_2_3_N) // 料理による追加命中
				|| _pc.hasSkillEffect(COOKING_2_3_S)
				|| _pc.hasSkillEffect(COOKING_3_0_N)
				|| _pc.hasSkillEffect(COOKING_3_0_S)
				|| _pc.hasSkillEffect(COOKING_4_2)) {
			if (_weaponType == 20 || _weaponType == 62) {
				_hitRate += 1;
			}
		}

		int attackerDice = _random.nextInt(20) + 1 + _hitRate - 10;

		// 回避率
		attackerDice -= _targetNpc.getDodge();
		attackerDice += _targetNpc.getNdodge();

		int defenderDice = 10 - _targetNpc.getAc();

		int fumble = _hitRate - 9;
		int critical = _hitRate + 10;

		if (attackerDice <= fumble) {
			_hitRate = 0;
		} else if (attackerDice >= critical) {
			_hitRate = 100;
		} else {
			if (attackerDice > defenderDice) {
				_hitRate = 100;
			} else if (attackerDice <= defenderDice) {
				_hitRate = 0;
			}
		}

		// 特定条件有攻可能 NPC判定
		if (_pc.isAttackMiss(_pc, _targetNpc.getNpcTemplate().getNpcId())) {
			_hitRate = 0;
		}

		int rnd = _random.nextInt(100) + 1;

		return _hitRate >= rnd;
	}

	// ●●●● ＮＰＣ から プレイヤー への命中判定 ●●●●
	private boolean calcNpcPcHit() {

		if (_targetPc.isFreeze()) {
			_hitRate = 0;
			return false;
		}

		if ((_npc instanceof L1PetInstance)
				|| (_npc instanceof L1SummonInstance)) {
			// 目標攻判定、NOPVP
			if ((_targetPc.getZoneType() == 1) || (_npc.getZoneType() == 1)
					|| (_targetPc.checkNonPvP(_targetPc, _npc))) {
				_hitRate = 0;
				return false;
			}
		}

		// マジックドール効果 - ダメージ回避
		if (L1MagicDoll.getDamageEvasionByDoll(_targetPc) > 0) {
			_hitRate = 0;
			return false;
		}

		_hitRate += _npc.getLevel();

		if (_npc instanceof L1PetInstance) { // ペットの武器による追加命中
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}

		_hitRate += _npc.getHitup();

		int attackerDice = _random.nextInt(20) + 1 + _hitRate - 1;

		// 回避率
		attackerDice -= _targetPc.getDodge();
		attackerDice += _targetPc.getNdodge();

		int defenderDice = 0;

		int defenderValue = (_targetPc.getAc()) * -1;

		if (_targetPc.getAc() >= 0) {
			defenderDice = 10 - _targetPc.getAc();
		} else if (_targetPc.getAc() < 0) {
			defenderDice = 10 + _random.nextInt(defenderValue) + 1;
		}

		int fumble = _hitRate;
		int critical = _hitRate + 19;

		if (attackerDice <= fumble) {
			_hitRate = 0;
		} else if (attackerDice >= critical) {
			_hitRate = 100;
		} else {
			if (attackerDice > defenderDice) {
				_hitRate = 100;
			} else if (attackerDice <= defenderDice) {
				_hitRate = 0;
			}
		}

		int rnd = _random.nextInt(100) + 1;

		// NPCの攻撃レンジが10以上の場合で、2以上離れている場合弓攻撃とみなす
		if (_npc.getNpcTemplate().getRanged() >= 10
				&& _hitRate > rnd
				&& _npc.getLocation().getTileLineDistance(
						new Point(_targetX, _targetY)) >= 2) {
			return calcErEvasion();
		}
		return _hitRate >= rnd;
	}

	// ●●●● ＮＰＣ から ＮＰＣ への命中判定 ●●●●
	private boolean calcNpcNpcHit() {

		if (_targetNpc.isFreeze()) {
			_hitRate = 0;
			return false;
		}

		_hitRate += _npc.getLevel();

		if (_npc instanceof L1PetInstance) { // ペットの武器による追加命中
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}

		_hitRate += _npc.getHitup();

		int attackerDice = _random.nextInt(20) + 1 + _hitRate - 1;

		// 回避率
		attackerDice -= _targetNpc.getDodge();
		attackerDice += _targetNpc.getNdodge();

		int defenderDice = 0;

		int defenderValue = (_targetNpc.getAc()) * -1;

		if (_targetNpc.getAc() >= 0) {
			defenderDice = 10 - _targetNpc.getAc();
		} else if (_targetNpc.getAc() < 0) {
			defenderDice = 10 + _random.nextInt(defenderValue) + 1;
		}

		int fumble = _hitRate;
		int critical = _hitRate + 19;

		if (attackerDice <= fumble) {
			_hitRate = 0;
		} else if (attackerDice >= critical) {
			_hitRate = 100;
		} else {
			if (attackerDice > defenderDice) {
				_hitRate = 100;
			} else if (attackerDice <= defenderDice) {
				_hitRate = 0;
			}
		}

		int rnd = _random.nextInt(100) + 1;
		return _hitRate >= rnd;
	}

	// ●●●● ＥＲによる回避判定 ●●●●
	private boolean calcErEvasion() {
		int er = _targetPc.getEr();

		int rnd = _random.nextInt(100) + 1;
		return er < rnd;
	}

	/* ■■■■■■■■■■■■■■■ ダメージ算出 ■■■■■■■■■■■■■■■ */

	public int calcDamage() {
		if (_calcType == PC_PC) {
			_damage = calcPcPcDamage();
		} else if (_calcType == PC_NPC) {
			_damage = calcPcNpcDamage();
		} else if (_calcType == NPC_PC) {
			_damage = calcNpcPcDamage();
		} else if (_calcType == NPC_NPC) {
			_damage = calcNpcNpcDamage();
		}
		return _damage;
	}

	// ●●●● プレイヤー から プレイヤー へのダメージ算出 ●●●●
	public int calcPcPcDamage() {
		int weaponMaxDamage = _weaponSmall;

		int weaponDamage = 0;
		if (_weaponType == 58
				&& (_random.nextInt(100) + 1) <= _weaponDoubleDmgChance) { // クリティカルヒット
			weaponDamage = weaponMaxDamage;
			_pc.sendPackets(new S_SkillSound(_pc.getId(), 3671));
			_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3671));
		} else if (_weaponType == 0 || _weaponType == 20 || _weaponType == 62) { // 素手、弓、ガントトレット
			weaponDamage = 0;
		} else if (_weaponId == 190) { // 弓-矢が無くてサイハの場合
				weaponMaxDamage = 12;
		} else {
			weaponDamage = _random.nextInt(weaponMaxDamage) + 1;
		}
		if (_pc.hasSkillEffect(SOUL_OF_FLAME)) {
			if (_weaponType != 20 && _weaponType != 62) {
				weaponDamage = weaponMaxDamage;
			}
		}

		int weaponTotalDamage = weaponDamage + _weaponAddDmg + _weaponEnchant;
		if (_weaponType == 54
				&& (_random.nextInt(100) + 1) <= _weaponDoubleDmgChance) { // ダブルヒット
			weaponTotalDamage *= 2;
			_pc.sendPackets(new S_SkillSound(_pc.getId(), 3398));
			_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3398));
		}

		weaponTotalDamage += calcAttrEnchantDmg(); // 属性強化ダメージボーナス

		if (_pc.hasSkillEffect(DOUBLE_BRAKE)
				&& (_weaponType == 54 || _weaponType == 58)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				weaponTotalDamage *= 2;
			}
		}

		if (_weaponId == 262 && _random.nextInt(100) + 1 <= 75) { // ディストラクション装備かつ成功確率
			// (暫定)75%
			weaponTotalDamage += calcDestruction(weaponTotalDamage);
		}

		if (_weaponId == 729  && _random.nextInt(100) + 1 <= 70) {
			// ブラッド サッカー装備かつ成功確率 (暫定)70%
			weaponTotalDamage += calcDestruction(weaponTotalDamage);
		}

		double dmg;
		if (_weaponType != 20 && _weaponType != 62) {
			dmg = weaponTotalDamage + _statusDamage + _pc.getDmgup()
					+ _pc.getOriginalDmgup();
		} else {
			dmg = weaponTotalDamage + _statusDamage + _pc.getBowDmgup()
					+ _pc.getOriginalBowDmgup();
		}

		if (_weaponType == 20) { // 弓
			if (_arrow != null) {
				int add_dmg = _arrow.getItem().getDmgSmall();
				if (add_dmg == 0) {
					add_dmg = 1;
				}
				dmg = dmg + _random.nextInt(add_dmg) + 1;
			} else if (_weaponId == 190) { // サイハの弓
				dmg = dmg + _random.nextInt(15) + 1;
			}
		} else if (_weaponType == 62) { // ガントトレット
			int add_dmg = _sting.getItem().getDmgSmall();
			if (add_dmg == 0) {
				add_dmg = 1;
			}
			dmg = dmg + _random.nextInt(add_dmg) + 1;
		}

		dmg = calcBuffDamage(dmg);

		if ((_weaponId == 701 || _weaponId == 702)) { // 極寒シリーズ
			L1WeaponSkill.getIceWeaponDamage(_pc, _targetPc, weapon);
		} else if (_weaponId == 121 || _weaponId == 703) { // アイスクイーンスタッフ
			L1WeaponSkill.getIceQueenStaffDamage(_pc, _targetPc, weapon);
		} else if ((_weaponId == 705) || (_weaponId == 706)) { // DE破壊シリーズ ベノムブレイズ
			dmg += L1WeaponSkill.getVenomBlazeDamage(_pc, _target, weapon);
		} else if (_weaponId == 15) { //カーツソード リニューアル
			L1WeaponSkill.getKurtzWeaponDamage(_pc, _target, weapon);
		} else if (_weaponId == 124) { // バフォメットスタッフ
			dmg += L1WeaponSkill.getBaphometStaffDamage(_pc, _targetPc);
		} else if (_weaponId == 2 || _weaponId == 200002) { // ダイスダガー
			dmg = L1WeaponSkill.getDiceDaggerDamage(_pc, _targetPc, weapon);
		} else if (_weaponId == 204 || _weaponId == 100204) { // 真紅のクロスボウ
			L1WeaponSkill.giveFettersEffect(_pc, _targetPc);
		} else if (_weaponId == 260) { // レイジングウィンド
			dmg += L1WeaponSkill.getRagingWindDamage(_pc, _targetPc, weapon);
		} else if (_weaponId == 264) { // ライトニングエッジ
			dmg += L1WeaponSkill.getLightningEdgeDamage(_pc, _targetPc, weapon);
		} else if (_weaponId == 263) { // フリージングランサー
			dmg += L1WeaponSkill.getFreezingLancerDamage(_pc, _targetPc, weapon);
		} else if (_weaponId == 261) { // エンジェルスタッフ
			L1WeaponSkill.giveAngelStaffTurnUndead(_pc, _targetPc, weapon);
		} else if ((_weaponId == 704) || (_weaponId == 191)) { // エンジェルスレイヤー
			dmg += L1WeaponSkill.getAngelSlayerWeaponDamage(_pc, _target, weapon);
		} else if (_weaponId == 276 || _weaponId == 277 || _weaponId == 278
				|| _weaponId == 279 || _weaponId == 280 || _weaponId == 281) { // マリスエレメント武器
			L1WeaponSkill.getMaliceWeaponDamage(_pc, _targetPc, weapon);
		} else if (_weaponType2 != 14) {
			// キーリング以外の武器にＤＢでスキルが設定されている場合
			dmg += L1WeaponSkill.getWeaponSkillDamage(_pc, _target, _weaponId, _weaponEnchant);
		}

		if (_pc.hasSkillEffect(BURNING_SLASH)) {
			_pc.sendPackets(new S_SkillSound(_targetPc.getId(), 6591));
			_pc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 6591));
			_pc.killSkillEffectTimer(BURNING_SLASH);
		}

		if (_weaponType == 0) { // 素手
			dmg = (_random.nextInt(5) + 4) / 4;
		}

		if (_weaponType2 != 14
				&& (_skillId == BONE_BREAK || _skillId == SMASH_ENERGY)) {
			dmg += _skillDamage;
		}

		if (_weaponType2 == 14) { // キーリンク
			dmg = L1WeaponSkill.getKiringkuDamage(_pc, _target);
			dmg += calcAttrEnchantDmg();
			 // ＤＢでキーリングにスキルが設定されている場合
			dmg += L1WeaponSkill.getWeaponSkillDamage(_pc, _target, _weaponId, _weaponEnchant);
		}

		if (_weaponType != 20 && _weaponType != 62) { // 防具による追加ダメージ
			dmg += _pc.getDmgModifierByArmor();
		} else {
			dmg += _pc.getBowDmgModifierByArmor();
		}

		if (_weaponType != 20 && _weaponType != 62) { // マジックドール効果
			L1MagicDoll.getDamageAddByDoll(_pc);
		}

		if (_pc.hasSkillEffect(COOKING_2_0_N) // 料理による追加ダメージ
				|| _pc.hasSkillEffect(COOKING_2_0_S)
				|| _pc.hasSkillEffect(COOKING_3_2_N)
				|| _pc.hasSkillEffect(COOKING_3_2_S)) {
			if (_weaponType != 20 && _weaponType != 62) {
				dmg += 1;
			}
		}
		
		if (_pc.hasSkillEffect(COOKING_4_1)) { // 料理による追加ダメージ
			if (_weaponType != 20 && _weaponType != 62) {
				dmg += 2;
			}
		}
		
		if (_pc.hasSkillEffect(COOKING_2_3_N) // 料理による追加ダメージ
				|| _pc.hasSkillEffect(COOKING_2_3_S)
				|| _pc.hasSkillEffect(COOKING_3_0_N)
				|| _pc.hasSkillEffect(COOKING_3_0_S)) {
			if (_weaponType == 20 || _weaponType == 62) {
				dmg += 1;
			}
		}

		if (_pc.hasSkillEffect(COOKING_4_2)) { // 料理による追加ダメージ
			if (_weaponType == 20 || _weaponType == 62) {
				dmg += 2;
			}
		}
		
		if (_pc.hasSkillEffect(MAGIC_EYE_OF_VALAKAS) // 魔眼による追加ダメージ
				|| _pc.hasSkillEffect(MAGIC_EYE_OF_LIFE)) {
			int _damageChance = _random.nextInt(100) + 1;
			if (_damageChance <= 10) {
				dmg += 2;
			}
		}

		dmg -= _targetPc.getDamageReductionByArmor(); // 防具によるダメージ軽減

		// マジックドール效果 - ダメージリダクション
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
		if (_targetPc.hasSkillEffect(ARMOR_BREAK)) {
			dmg *= 1.58; // 対象の被ダメージ58%増加
		}
		if (_targetPc.hasSkillEffect(DRAGON_SKIN)) {
			dmg -= 5; // キャラクターケアアップデート
		}
		if (_targetPc.hasSkillEffect(PATIENCE)) {
			dmg -= 2;
		}
		if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 2;
		}
		if (_targetPc.isFreeze()) {
			dmg = 0;
		}

		if (dmg <= 0) {
			_isHit = false;
			_drainHp = 0; // ダメージ無しの場合は吸収による回復はしない
		}

		// マジックドールスキル
		L1SkillUse l1skilluse = new L1SkillUse();
		if (L1MagicDoll.getEffectByDoll(_pc, SLOW) == SLOW) {
			l1skilluse.handleCommands(_pc,SLOW, // スロー
				_targetPc.getId(), _targetPc.getX(), _targetPc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF);
		}
		if (L1MagicDoll.getEffectByDoll(_pc, CURSE_PARALYZE) == CURSE_PARALYZE) {
			l1skilluse.handleCommands(_pc,CURSE_PARALYZE, // カーズパラライズ
				_targetPc.getId(), _targetPc.getX(), _targetPc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF);
		}
		if (L1MagicDoll.getEffectByDoll(_pc, VAMPIRIC_TOUCH) == VAMPIRIC_TOUCH) {
				L1Skill l1skills = SkillTable.getInstance().findBySkillId(
						VAMPIRIC_TOUCH); // バンパイアリックタッチ
				L1Magic magic = new L1Magic(_pc, _targetPc);

				_pc.sendPackets(new S_SkillSound(_pc.getId(), l1skills
						.getCastGfx()));
				_pc.broadcastPacket(new S_SkillSound(_pc.getId(), l1skills
						.getCastGfx()));

				int damage = magic.calcMagicDamage(l1skills.getSkillId());
				_targetPc.sendPackets(new S_DoActionGFX(_targetPc.getId(),
						ActionCodes.ACTION_Damage));
				_targetPc.broadcastPacket(new S_DoActionGFX(
						_targetPc.getId(), ActionCodes.ACTION_Damage));
				_targetPc.removeSkillEffect(ERASE_MAGIC); // イレースマジック中なら、攻撃魔法で解除
				_targetPc.receiveDamage(_pc, damage, false);
				_pc.setCurrentHp(_pc.getCurrentHp() + damage);
		}

		return (int) dmg;
	}

	// ●●●● プレイヤー から ＮＰＣ へのダメージ算出 ●●●●
	public int calcPcNpcDamage() {

		int weaponMaxDamage = 0;
		if (_targetNpc.getNpcTemplate().getSize().equalsIgnoreCase("small")
				&& _weaponSmall > 0) {
			weaponMaxDamage = _weaponSmall;
		} else if (_targetNpc.getNpcTemplate().getSize().equalsIgnoreCase(
				"large")
				&& _weaponLarge > 0) {
			weaponMaxDamage = _weaponLarge;
		}

		int weaponDamage = 0;
		if (_weaponType == 58
				&& (_random.nextInt(100) + 1) <= _weaponDoubleDmgChance) { // クリティカルヒット
			weaponDamage = weaponMaxDamage;
			_pc.sendPackets(new S_SkillSound(_pc.getId(), 3671));
			_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3671));
		} else if (_weaponType == 0 || _weaponType == 20 || _weaponType == 62) {
			// 素手 、 弓、ガントトレット
			weaponDamage = 0;
			if (_weaponId == 190) { // 弓-矢が無くてサイハの場合
				if (_targetNpc.getNpcTemplate().getSize().equalsIgnoreCase("small")
						&& _weaponSmall > 0) {
					weaponMaxDamage = 12;
				} else if (_targetNpc.getNpcTemplate().getSize().equalsIgnoreCase(
						"large")
						&& _weaponLarge > 0) {
					weaponMaxDamage = 10;
				}
			}
		} else {
			weaponDamage = _random.nextInt(weaponMaxDamage) + 1;
		}
		if (_pc.hasSkillEffect(SOUL_OF_FLAME)) {
			if (_weaponType != 20 && _weaponType != 62) {
				weaponDamage = weaponMaxDamage;
			}
		}

		int weaponTotalDamage = weaponDamage + _weaponAddDmg + _weaponEnchant;

		weaponTotalDamage += calcMaterialBlessDmg(); // 銀祝福ダメージボーナス
		if (_weaponType == 54
				&& (_random.nextInt(100) + 1) <= _weaponDoubleDmgChance) { // ダブルヒット
			weaponTotalDamage *= 2;
			_pc.sendPackets(new S_SkillSound(_pc.getId(), 3398));
			_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3398));
		}

		weaponTotalDamage += calcAttrEnchantDmg(); // 属性強化ダメージボーナス

		if (_pc.hasSkillEffect(DOUBLE_BRAKE)
				&& (_weaponType == 54 || _weaponType == 58)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				weaponTotalDamage *= 2;
			}
		}

		if (_weaponId == 262 && _random.nextInt(100) + 1 <= 75) {
			// ディストラクション装備かつ成功確率 (暫定)75%
			weaponTotalDamage += calcDestruction(weaponTotalDamage);
		}

		if (_weaponId == 729  && _random.nextInt(100) + 1 <= 70) {
			// ブラッドサッカー装備かつ成功確率 (暫定)70%
			weaponTotalDamage += calcDestruction(weaponTotalDamage);
		}

		double dmg;
		if (_weaponType != 20 && _weaponType != 62) {
			dmg = weaponTotalDamage + _statusDamage + _pc.getDmgup()
					+ _pc.getOriginalDmgup();
		} else {
			dmg = weaponTotalDamage + _statusDamage + _pc.getBowDmgup()
					+ _pc.getOriginalBowDmgup();
		}

		if (_weaponType == 20) { // 弓
			if (_arrow != null) {
				int add_dmg = 0;
				if (_targetNpc.getNpcTemplate().getSize().equalsIgnoreCase(
						"large")) {
					add_dmg = _arrow.getItem().getDmgLarge();
				} else {
					add_dmg = _arrow.getItem().getDmgSmall();
				}
				if (add_dmg == 0) {
					add_dmg = 1;
				}
				if (_targetNpc.getNpcTemplate().isHard()) {
					add_dmg /= 2;
				}
				dmg = dmg + _random.nextInt(add_dmg) + 1;
			} else if (_weaponId == 190) { // サイハの弓
				dmg = dmg + _random.nextInt(15) + 1;
			}
		} else if (_weaponType == 62) { // ガントトレット
			int add_dmg = 0;
			if (_targetNpc.getNpcTemplate().getSize()
					.equalsIgnoreCase("large")) {
				add_dmg = _sting.getItem().getDmgLarge();
			} else {
				add_dmg = _sting.getItem().getDmgSmall();
			}
			if (add_dmg == 0) {
				add_dmg = 1;
			}
			dmg = dmg + _random.nextInt(add_dmg) + 1;
		}

		dmg = calcBuffDamage(dmg);

		if ((_weaponId == 701 || _weaponId == 702)) { //極寒シリーズ
			L1WeaponSkill.getIceWeaponDamage(_pc, _target, weapon);
		} else if (_weaponId == 121 || _weaponId == 703) { //アイスクイーンスタッフ
			L1WeaponSkill.getIceQueenStaffDamage(_pc, _target, weapon);
		} else if ((_weaponId == 705) || (_weaponId == 706)) { // DE破壊シリーズ ベノムブレイズ
			dmg += L1WeaponSkill.getVenomBlazeDamage(_pc, _target, weapon);
		} else if (_weaponId == 15) { //カーツソード リニューアル
			L1WeaponSkill.getKurtzWeaponDamage(_pc, _target, weapon);
		} else if (_weaponId == 124) { // バフォメットスタッフ
			dmg += L1WeaponSkill.getBaphometStaffDamage(_pc, _target);
		} else if (_weaponId == 2 || _weaponId == 200002) { // ダイスダガー
			dmg = L1WeaponSkill.getDiceDaggerDamage(_pc, _targetPc, weapon);
		} else if (_weaponId == 204 || _weaponId == 100204) { // 真紅のクロスボウ
			L1WeaponSkill.giveFettersEffect(_pc, _targetNpc);
		} else if (_weaponId == 260) { // レイジングウィンド
			dmg += L1WeaponSkill.getRagingWindDamage(_pc, _target, weapon);
		} else if (_weaponId == 264) { // ライトニングエッジ
			dmg += L1WeaponSkill.getLightningEdgeDamage(_pc, _target, weapon);
		} else if (_weaponId == 263) { // フリージングランサー
			dmg += L1WeaponSkill.getFreezingLancerDamage(_pc, _target, weapon);
		} else if (_weaponId == 261) { // エンジェルスタッフ
			L1WeaponSkill.giveAngelStaffTurnUndead(_pc, _target, weapon);
		} else if ((_weaponId == 704) || (_weaponId == 191)) { // エンジェルスレイヤー
			dmg += L1WeaponSkill.getAngelSlayerWeaponDamage(_pc, _target, weapon);
		} else if (_weaponId == 276 || _weaponId == 277 || _weaponId == 278
				|| _weaponId == 279 || _weaponId == 280 || _weaponId == 281) { // マリスエレメント武器
			L1WeaponSkill.getMaliceWeaponDamage(_pc, _target, weapon);
		} else if (_weaponType2 != 14) {
			// キーリング以外の武器にＤＢでスキルが設定されている場合
			dmg += L1WeaponSkill.getWeaponSkillDamage(_pc, _target, _weaponId, _weaponEnchant);
		}

		if (_pc.hasSkillEffect(BURNING_SLASH)) {
			_pc.sendPackets(new S_SkillSound(_targetNpc.getId(), 6591));
			_pc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 6591));
			_pc.killSkillEffectTimer(BURNING_SLASH);
		}

		if (_weaponType == 0) { // 素手
			dmg = (_random.nextInt(5) + 4) / 4;
		}

		if (_weaponType2 != 14
				&& (_skillId == BONE_BREAK || _skillId == SMASH_ENERGY)) {
			dmg += _skillDamage;
		}

		if (_weaponType2 == 14) { // キーリンク
			dmg = L1WeaponSkill.getKiringkuDamage(_pc, _target);
			dmg += calcAttrEnchantDmg();
			 // ＤＢでキーリングにスキルが設定されている場合
			dmg += L1WeaponSkill.getWeaponSkillDamage(_pc, _target, _weaponId, _weaponEnchant);
		}

		if (_weaponType != 20 && _weaponType != 62) { // 防具による追加ダメージ
			dmg += _pc.getDmgModifierByArmor();
		} else {
			dmg += _pc.getBowDmgModifierByArmor();
		}

		if (_weaponType != 20 && _weaponType != 62) {// マジックドールによる追加ダメージ
			dmg += L1MagicDoll.getDamageAddByDoll(_pc);
		}

		if (_pc.hasSkillEffect(COOKING_2_0_N) // 料理による追加ダメージ
				|| _pc.hasSkillEffect(COOKING_2_0_S)
				|| _pc.hasSkillEffect(COOKING_3_2_N)
				|| _pc.hasSkillEffect(COOKING_3_2_S)) {
			if (_weaponType != 20 && _weaponType != 62) {
				dmg += 1;
			}
		}
		
		if (_pc.hasSkillEffect(COOKING_4_1)) { // 料理による追加ダメージ
			if (_weaponType != 20 && _weaponType != 62) {
				dmg += 2;
			}
		}
		
		if (_pc.hasSkillEffect(COOKING_2_3_N) // 料理による追加ダメージ
				|| _pc.hasSkillEffect(COOKING_2_3_S)
				|| _pc.hasSkillEffect(COOKING_3_0_N)
				|| _pc.hasSkillEffect(COOKING_3_0_S)) {
			if (_weaponType == 20 || _weaponType == 62) {
				dmg += 1;
			}
		}
		
		if (_pc.hasSkillEffect(COOKING_4_2)) { // 料理による追加ダメージ
			if (_weaponType == 20 || _weaponType == 62) {
				dmg += 2;
			}
		}
		
		if (_pc.hasSkillEffect(MAGIC_EYE_OF_VALAKAS) // 魔眼による追加ダメージ
				|| _pc.hasSkillEffect(MAGIC_EYE_OF_LIFE)) {
			int _damageChance = _random.nextInt(100) + 1;
			if (_damageChance <= 10) {
				dmg += 2;
			}
		}

		dmg -= calcNpcDamageReduction();

		if (_targetNpc.hasSkillEffect(ARMOR_BREAK)) {
			dmg *= 1.58; // 対象の被ダメージ58%増加
		}

		// プレイヤーからペット、サモンに攻撃
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

		// 特定NPC 固定ダメージ判定
		int fixedDamage = _pc.getFixedDamage(_targetNpc.getNpcTemplate().getNpcId());
		if (fixedDamage >= 0) {
			dmg = fixedDamage;
		}

		if (_targetNpc.isFreeze()) {
			dmg = 0;
		}

		if (dmg <= 0) {
			_isHit = false;
			_drainHp = 0; // ダメージ無しの場合は吸収による回復はしない
		}

		// マジックドールスキル
		L1SkillUse l1skilluse = new L1SkillUse();
		if (L1MagicDoll.getEffectByDoll(_pc, SLOW) == SLOW) {
			l1skilluse.handleCommands(_pc,SLOW, // スロー
				_targetNpc.getId(), _targetNpc.getX(), _targetNpc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF);
		}
		if (L1MagicDoll.getEffectByDoll(_pc, CURSE_PARALYZE) == CURSE_PARALYZE) {
			l1skilluse.handleCommands(_pc,CURSE_PARALYZE, // カーズパラライズ
				_targetNpc.getId(), _targetNpc.getX(), _targetNpc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF);
		}
		if (L1MagicDoll.getEffectByDoll(_pc, VAMPIRIC_TOUCH) == VAMPIRIC_TOUCH) {
				L1Skill l1skills = SkillTable.getInstance().findBySkillId(
						VAMPIRIC_TOUCH); // バンパイアリックタッチ
				L1Magic magic = new L1Magic(_pc, _targetNpc);

				_pc.sendPackets(new S_SkillSound(_pc.getId(), l1skills
						.getCastGfx()));
				_pc.broadcastPacket(new S_SkillSound(_pc.getId(), l1skills
						.getCastGfx()));

				int damage = magic.calcMagicDamage(l1skills.getSkillId());
				_targetNpc.broadcastPacket(new S_DoActionGFX(
						_targetNpc.getId(), ActionCodes.ACTION_Damage));
				_targetNpc.receiveDamage(_pc, damage);
				_pc.setCurrentHp(_pc.getCurrentHp() + damage);
		}


		return (int) dmg;
	}

	// ●●●● ＮＰＣ から プレイヤー へのダメージ算出 ●●●●
	private int calcNpcPcDamage() {
		int lvl = _npc.getLevel();
		double dmg = 0D;
		if (lvl < 10) {
			dmg = _random.nextInt(lvl) + 10D + _npc.getStr() / 2 + 1;
		} else {
			dmg = _random.nextInt(lvl) + _npc.getStr() / 2 + 1;
		}

		if (_npc instanceof L1PetInstance) {
			dmg += (lvl / 16); // ペットはLV16毎に追加打撃
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();
		}

		dmg += _npc.getDmgup();

		if (isUndeadDamage()) {
			dmg *= 1.1;
		}

		dmg = dmg * getLeverage() / 10;

		dmg -= calcPcDefense();

		if (_npc.isWeaponBreaked()) { // ＮＰＣがウェポンブレイク中。
			dmg /= 2;
		}

		dmg -= _targetPc.getDamageReductionByArmor(); // 防具によるダメージ軽減

		// マジックドール效果 - ダメージリダクション
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
		if (_targetPc.hasSkillEffect(ARMOR_BREAK)) {
			dmg *= 1.58; // 対象の被ダメージ58%増加
		}
		if (_targetPc.hasSkillEffect(DRAGON_SKIN)) {
			//dmg -= 2;
			//dmg -= 3;  リニューアル後
			dmg -= 5; // キャラクターケアアップデート
		}
		if (_targetPc.hasSkillEffect(PATIENCE)) {
			dmg -= 2;
		}
		if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 2;
		}
		if (_targetPc.isFreeze()) {
			dmg = 0;
		}

		// ペット、サモンからプレイヤーに攻撃
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

		if (dmg <= 0) {
			_isHit = false;
		}

		addNpcPoisonAttack(_npc, _targetPc);

		return (int) dmg;
	}

	// ●●●● ＮＰＣ から ＮＰＣ へのダメージ算出 ●●●●
	private int calcNpcNpcDamage() {
		int lvl = _npc.getLevel();
		double dmg = 0;

		if (_npc instanceof L1PetInstance) {
			dmg = _random.nextInt(_npc.getNpcTemplate().getLevel())
					+ _npc.getStr() / 2 + 1;
			dmg += (lvl / 16); // ペットはLV16毎に追加打撃
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();
		} else {
			dmg = _random.nextInt(lvl) + _npc.getStr() / 2 + 1;
		}

		if (isUndeadDamage()) {
			dmg *= 1.1;
		}

		dmg = dmg * getLeverage() / 10;

		dmg -= calcNpcDamageReduction();

		if (_npc.isWeaponBreaked()) { // ＮＰＣがウェポンブレイク中。
			dmg /= 2;
		}

		if (_targetNpc.hasSkillEffect(ARMOR_BREAK)) {
			dmg *= 1.58; // 対象の被ダメージ58%増加
		}
		
		addNpcPoisonAttack(_npc, _targetNpc);

		// 特定NPC 固定ダメージ判定
		int fixedDamage = _npc.getFixedDamage(_targetNpc.getNpcTemplate().getNpcId());
		if (fixedDamage >= 0) {
			dmg = fixedDamage;
		}

		if (_targetNpc.isFreeze()) {
			dmg = 0;
		}

		if (dmg <= 0) {
			_isHit = false;
		}

		return (int) dmg;
	}

	// ●●●● プレイヤーのダメージ強化魔法 ●●●●
	private double calcBuffDamage(double dmg) {
		// 火武器、バーサーカーのダメージは1.5倍しない
		if (_pc.hasSkillEffect(BURNING_SPIRIT)
				|| (_pc.hasSkillEffect(ELEMENTAL_FIRE) && _weaponType != 20
						&& _weaponType != 62 && _weaponType2 != 14)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				double tempDmg = dmg;
				if (_pc.hasSkillEffect(FIRE_WEAPON)) {
					tempDmg -= 4;
				}
				if (_pc.hasSkillEffect(FIRE_BLESS)) {
					tempDmg -= 4;
				}
				if (_pc.hasSkillEffect(BURNING_WEAPON)) {
					tempDmg -= 6;
				}
				if (_pc.hasSkillEffect(BERSERKERS)) {
					tempDmg -= 5;
				}
				double diffDmg = dmg - tempDmg;
				dmg = tempDmg * 1.5 + diffDmg;
			}
		}

		// チェーンソードによる弱点露出
		if (_pc.getExposureTargetId() != _target.getId()) { // ターゲットが違う場合、弱点露出削除
			if (_pc.hasSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV1)) {
				_pc.killSkillEffectTimer(STATUS_WEAKNESS_EXPOSURE_LV1);
				_pc.removeSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV1);
			}
			if (_pc.hasSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV1)) {
				_pc.killSkillEffectTimer(STATUS_WEAKNESS_EXPOSURE_LV2);
				_pc.removeSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV2);
			}
			if (_pc.hasSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV1)) {
				_pc.killSkillEffectTimer(STATUS_WEAKNESS_EXPOSURE_LV3);
				_pc.removeSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV3);
			}
			_pc.sendPackets(new S_SkillIconGFX(75, 0));
		}
		if (_pc.isFoeSlayer()) {
			if (_pc.hasSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV1)) {
				dmg += 20;
			} else if (_pc.hasSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV2)) {
				dmg += 40;
			} else if (_pc.hasSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV3)) {
				dmg += 60;
			}
		} else if (_weaponType2 == 13
				&& (_random.nextInt(100) + 1) <= _weaponWeaknessExposureChance) {
			if (_pc.hasSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV1)) {
				_pc.killSkillEffectTimer(STATUS_WEAKNESS_EXPOSURE_LV1);
				_pc.removeSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV1);
				_pc.setSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV2, 15000);
				_pc.sendPackets(new S_SkillIconGFX(75, 2));
			} else if (_pc.hasSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV2)) {
				_pc.killSkillEffectTimer(STATUS_WEAKNESS_EXPOSURE_LV2);
				_pc.removeSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV2);
				_pc.setSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV3, 15000);
				_pc.sendPackets(new S_SkillIconGFX(75, 3));
			} else if (_pc.hasSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV3)) {
				// LV3の場合は上書きしない。
			} else {
				_pc.setSkillEffect(STATUS_WEAKNESS_EXPOSURE_LV1, 15000);
				_pc.setExposureTargetId(_target.getId());
				_pc.sendPackets(new S_SkillIconGFX(75, 1));
			}
		}

		// バーニングスラッシュ
		if (_pc.hasSkillEffect(BURNING_SLASH)) {
			dmg += _random.nextInt(4) + 7;
		}

		return dmg;
	}

	// ●●●● プレイヤーのＡＣによるダメージ軽減 ●●●●
	private int calcPcDefense() {
		int ac = Math.max(0, 10 - _targetPc.getAc());
		int acDefMax = _targetPc.getClassFeature().getAcDefenseMax(ac);
		return _random.nextInt(acDefMax + 1);
	}

	// ●●●● ＮＰＣのダメージリダクションによる軽減 ●●●●
	private int calcNpcDamageReduction() {
		return _targetNpc.getNpcTemplate().getDamageReduction();
	}

	// ●●●● 武器の材質と祝福による追加ダメージ算出 ●●●●
	private int calcMaterialBlessDmg() {
		int damage = 0;
		int undead = _targetNpc.getNpcTemplate().getUndead();
		if ((_weaponMaterial == 14 || _weaponMaterial == 17 || _weaponMaterial == 22)
				&& (undead == 1 || undead == 3 || undead == 5)) { // 銀・ミスリル・オリハルコン、かつ、アンデッド系・アンデッド系ボス・銀特効モンスター
			damage += _random.nextInt(20) + 1;
		}
		if ((_weaponMaterial == 17 || _weaponMaterial == 22) && undead == 2) { // ミスリル・オリハルコン、かつ、悪魔系
			damage += _random.nextInt(3) + 1;
		}
		if (_weaponBless == 1 && (undead == 1 || undead == 2 || undead == 3)) { // 祝福武器、かつ、アンデッド系・悪魔系・アンデッド系ボス
			damage += _random.nextInt(4) + 1 + 2;
		}
		if (_pc.getWeapon() != null && _weaponType != 20 && _weaponType != 62
				&& weapon.getHolyDmgByMagic() != 0
				&& (undead == 1 || undead == 3)) {
			damage += weapon.getHolyDmgByMagic();
		}
		return damage;
	}

	// ●●●● 武器の属性強化による追加ダメージ算出 ●●●●
	private int calcAttrEnchantDmg() {
		int damage = 0;
		// int weakAttr = _targetNpc.getNpcTemplate().getWeakAttr();
		// if ((weakAttr & 1) == 1 && _weaponAttrEnchantKind == 1 // 地
		// || (weakAttr & 2) == 2 && _weaponAttrEnchantKind == 2 // 火
		// || (weakAttr & 4) == 4 && _weaponAttrEnchantKind == 4 // 水
		// || (weakAttr & 8) == 8 && _weaponAttrEnchantKind == 8) { // 風
		// damage = _weaponAttrEnchantLevel;
		// }
		if (_weaponAttrEnchantLevel == 1) {
			damage = 1;
		} else if (_weaponAttrEnchantLevel == 2) {
			damage = 3;
		} else if (_weaponAttrEnchantLevel == 3) {
			damage = 5;
		} else if (_weaponAttrEnchantLevel == 4) {
			damage = 7;
		} else if (_weaponAttrEnchantLevel == 5) {
			damage = 9;
		}

		// 耐性処理は本来、耐性合計値ではなく、各値を個別に処理して総和する。
		int resist = 0;
		if (_calcType == PC_PC) {
			if (_weaponAttrEnchantKind == 1) { // 地
				resist = _targetPc.getEarth();
			} else if (_weaponAttrEnchantKind == 2) { // 火
				resist = _targetPc.getFire();
			} else if (_weaponAttrEnchantKind == 4) { // 水
				resist = _targetPc.getWater();
			} else if (_weaponAttrEnchantKind == 8) { // 風
				resist = _targetPc.getWind();
			}
		} else if (_calcType == PC_NPC) {
			int weakAttr = _targetNpc.getNpcTemplate().getWeakAttr();
			if ((_weaponAttrEnchantKind == 1 && weakAttr == 1) // 地
					|| (_weaponAttrEnchantKind == 2 && weakAttr == 2) // 火
					|| (_weaponAttrEnchantKind == 4 && weakAttr == 4) // 水
					|| (_weaponAttrEnchantKind == 8 && weakAttr == 8)) { // 風
				resist = -50;
			}
		}

		int resistFloor = (int) (0.32 * Math.abs(resist));
		if (resist >= 0) {
			resistFloor *= 1;
		} else {
			resistFloor *= -1;
		}

		double attrDeffence = resistFloor / 32.0;
		double attrCoefficient = 1 - attrDeffence;

		damage *= attrCoefficient;

		return damage;
	}

	// ●●●● ＮＰＣのアンデッドの夜間攻撃力の変化 ●●●●
	private boolean isUndeadDamage() {
		boolean flag = false;
		int undead = _npc.getNpcTemplate().getUndead();
		boolean isNight = L1GameTimeClock.getInstance().currentTime().isNight();
		if (isNight && (undead == 1 || undead == 3 || undead == 4)) { // 18〜6時、かつ
			// 、
			// アンデッド系・アンデッド系ボス・弱点無効のアンデッド系
			flag = true;
		}
		return flag;
	}

	// ●●●● ＮＰＣの毒攻撃を付加 ●●●●
	private void addNpcPoisonAttack(L1Character attacker, L1Character target) {
		if (_npc.getNpcTemplate().getPoisonAtk() != 0) { // 毒攻撃あり
			if (15 >= _random.nextInt(100) + 1) { // 15%の確率で毒攻撃
				if (_npc.getNpcTemplate().getPoisonAtk() == 1) { // 通常毒
					// 3秒周期でダメージ5
					L1DamagePoison.doInfection(attacker, target, 3000, 5);
				} else if (_npc.getNpcTemplate().getPoisonAtk() == 2) { // 沈黙毒
					L1SilencePoison.doInfection(target);
				} else if (_npc.getNpcTemplate().getPoisonAtk() == 4) { // 麻痺毒
					// 20秒後に45秒間麻痺
					L1ParalysisPoison.doInfection(target, 8000, 16000);
				}
			}
		} else if (_npc.getNpcTemplate().getParalysIsAtk() != 0) { // 麻痺攻撃あり
		}
	}

	// ■■■■ マナスタッフ、鋼鉄のマナスタッフ、マナバゼラードのMP吸収量算出 ■■■■
	public void calcStaffOfMana() {
		if (_weaponId == 126 || _weaponId == 127) { // SOMまたは鋼鉄のSOM
			int som_lvl = _weaponEnchant + 3; // 最大MP吸収量を設定
			if (som_lvl < 0) {
				som_lvl = 0;
			}
			// MP吸収量をランダム取得
			_drainMana = _random.nextInt(som_lvl) + 1;
			// 最大MP吸収量を9に制限
			if (_drainMana > Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK) {
				_drainMana = Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK;
			}
		} else if (_weaponId == 259) { // マナバーラード
			if (_calcType == PC_PC) {
				if (_targetPc.getMr() <= _random.nextInt(100) + 1) { // 確率はターゲットのMRに依存
					_drainMana = 1; // 吸収量は1固定
				}
			} else if (_calcType == PC_NPC) {
				if (_targetNpc.getMr() <= _random.nextInt(100) + 1) { // 確率はターゲットのMRに依存
					_drainMana = 1; // 吸収量は1固定
				}
			}
		}
	}

	// ■■■■ ディストラクションのHP吸収量算出 ■■■■
	private int calcDestruction(int dmg) {
		_drainHp = (dmg / 8) + 1;
		if (_drainHp <= 0) {
			_drainHp = 1;
		}
		return _drainHp;
	}

	// ■■■■ ＰＣの毒攻撃を付加 ■■■■
	public void addPcPoisonAttack(L1Character attacker, L1Character target) {
		int chance = _random.nextInt(100) + 1;
		if (((_weaponId == 13) || (_weaponId == 44 // FOD、古代のダークエルフソード
				) || ((_weaponId != 0) && _pc.hasSkillEffect(ENCHANT_VENOM))) // エンチャント
				// ベノム中
				&& (chance <= 10)) {
			// 通常毒、3秒周期、ダメージHP-5
			L1DamagePoison.doInfection(attacker, target, 3000, 5);
		} else {
			// マジックドール：ラミア
			if (L1MagicDoll.getEffectByDoll(attacker, ENCHANT_VENOM) == ENCHANT_VENOM) {
				L1DamagePoison.doInfection(attacker, target, 3000, 5);
			}
		}
	}

	// ■■■■ チェイサーによる攻撃を付加 ■■■■
	public void addChaserAttack() {
		if (5 > _random.nextInt(100) + 1) {
			if (_weaponId == 265 || _weaponId == 266 || _weaponId == 267
					|| _weaponId == 268) { // テーベ武器
				if (!_target.hasSkillEffect(CHASER_OF_THEBES)) {
					_target.setSkillEffect(CHASER_OF_THEBES, 0);
					L1Chaser chaser = new L1Chaser(_pc, _target,
							L1Skill.ATTR_EARTH, 7025);
					chaser.begin();
				}
			} else if (_weaponId == 282 || _weaponId == 283) { // ククルカン武器
				if (!_target.hasSkillEffect(CHASER_OF_KUKULCAN)) {
					_target.setSkillEffect(CHASER_OF_KUKULCAN, 0);
					L1Chaser chaser = new L1Chaser(_pc, _target,
							L1Skill.ATTR_WATER, 7179);
					chaser.begin();
				}
			}
		}
	}

	// ■■■■ イビルスキルによる攻撃を付加 ■■■■
	public void addEvilAttack() {
		if (5 > _random.nextInt(100) + 1) {
			if ((_weaponId >= 292 && _weaponId <= 294) ||
					(_weaponId >= 298 && _weaponId <= 300) ||
					(_weaponId == 304 || _weaponId == 307 || _weaponId == 308)) {
				// イビルリバース武器
				L1Evil Evil = new L1Evil(_pc, _target,
						L1Skill.ATTR_WATER, 8150);
				Evil.begin();
			} else if ((_weaponId >= 295 && _weaponId <= 297) ||
					(_weaponId == 301 || _weaponId == 302)||
					(_weaponId == 305 || _weaponId == 306 || _weaponId == 309)) {
				// イビルトリック武器
				L1Evil Evil = new L1Evil(_pc, _target,
						L1Skill.ATTR_WATER, 8152);
				Evil.begin();
			}
		}
	}

	/* ■■■■■■■■■■■■■■ 攻撃モーション送信 ■■■■■■■■■■■■■■ */

	public void action() {
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			actionPc();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			actionNpc();
		}
	}

	// ●●●● プレイヤーの攻撃モーション送信 ●●●●
	private void actionPc() {
		boolean isFly = false;
		int attackGrfxId = -1;

		_pc.setHeading(_pc.targetDirection(_targetX, _targetY)); // 向きのセット

		if (_weaponType == 20) { // 弓
			isFly = true;
			if (_arrow != null) { // 矢がある場合
				attackGrfxId = 66;
				_pc.getInventory().removeItem(_arrow, 1);
			} else if (_weaponId == 190) { // 弓-矢が無くてサイハの場合
				attackGrfxId = 2349;
			}

			if (_pc.getTempCharGfx() == 8719)
				attackGrfxId = 8721;
			if (_pc.getTempCharGfx() == 8900)
				attackGrfxId = 8904;
			if (_pc.getTempCharGfx() == 8913)
				attackGrfxId = 8916;
			if (_pc.getTempCharGfx() == 7959 || _pc.getTempCharGfx() == 7967 ||
				_pc.getTempCharGfx() == 7968 || _pc.getTempCharGfx() == 7969 ||
				_pc.getTempCharGfx() == 7970)
				attackGrfxId = 7972;

		} else if (_weaponType == 62 && _sting != null) { // ガントレット - スティング有
			isFly = true;
			attackGrfxId = 2989;
			_pc.getInventory().removeItem(_sting, 1);
		}

		if (isFly) { // 遠距離攻撃(弓、ガントレット)
			if (attackGrfxId != -1) {
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, attackGrfxId,
						_targetX, _targetY, _isHit));
				_pc.broadcastPacket(new S_UseArrowSkill(_pc, _targetId, attackGrfxId,
						_targetX, _targetY, _isHit));
				if (_isHit) {
					_target.broadcastPacketExceptTargetSight(new S_DoActionGFX(
							_targetId, ActionCodes.ACTION_Damage), _pc);
				}
			}
		} else { // 近距離攻撃
			if (_isHit) {
				_pc.sendPackets(new S_AttackPacket(_pc, _targetId,
						ActionCodes.ACTION_Attack));
				_pc.broadcastPacket(new S_AttackPacket(_pc, _targetId,
						ActionCodes.ACTION_Attack));
				_target.broadcastPacketExceptTargetSight(new S_DoActionGFX(
						_targetId, ActionCodes.ACTION_Damage), _pc);
			} else {
				if (_targetId > 0) {
					_pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
					_pc.broadcastPacket(new S_AttackMissPacket(_pc, _targetId));
				} else {
					_pc.sendPackets(new S_AttackPacket(_pc, 0,
							ActionCodes.ACTION_Attack));
					_pc.broadcastPacket(new S_AttackPacket(_pc, 0,
							ActionCodes.ACTION_Attack));
				}
			}
		}
	}

	// ●●●● ＮＰＣの攻撃モーション送信 ●●●●
	private void actionNpc() {
		int _npcObjectId = _npc.getId();
		int bowActId = 0;
		int actId = 0;

		_npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // 向きのセット

		// ターゲットとの距離が2以上あれば遠距離攻撃
		boolean isLongRange = (_npc.getLocation().getTileLineDistance(
				new Point(_targetX, _targetY)) > 1);
		bowActId = _npc.getNpcTemplate().getBowActId();

		if (getActId() > 0) {
			actId = getActId();
		} else {
			actId = ActionCodes.ACTION_Attack;
		}

		if (!_isHit) { // Miss
			_damage = 0;
		}

		// 距離が2以上、攻撃者の弓のアクションIDがある場合は遠攻撃
		if (isLongRange && (bowActId > 0)) {
			_npc.broadcastPacket(new S_UseArrowSkill(_npc, _targetId, bowActId,
					_targetX, _targetY, _isHit));
		} else {
			if (_isHit) {
				if (getGfxId() > 0) {
					_npc
							.broadcastPacket(new S_UseAttackSkill(_target,
									_npcObjectId, getGfxId(), _targetX,
									_targetY, actId));
					_target.broadcastPacketExceptTargetSight(new S_DoActionGFX(
							_targetId, ActionCodes.ACTION_Damage), _npc);
				} else {
					_npc.broadcastPacket(new S_AttackPacketForNpc(_target,
							_npcObjectId, actId));
					_target.broadcastPacketExceptTargetSight(new S_DoActionGFX(
							_targetId, ActionCodes.ACTION_Damage), _npc);
				}
			} else {
				if (getGfxId() > 0) {
					_npc.broadcastPacket(new S_UseAttackSkill(_target,
							_npcObjectId, getGfxId(), _targetX, _targetY,
							actId, 0));
				} else {
					_npc.broadcastPacket(new S_AttackMissPacket(_npc,
							_targetId, actId));
				}
			}
		}
	}

	/*
	 * // 飛び道具（矢、スティング）がミスだったときの軌道を計算 public void calcOrbit(int cx, int cy, int
	 * head) // 起点Ｘ 起点Ｙ 今向いてる方向 { float dis_x = Math.abs(cx - _targetX); //
	 * Ｘ方向のターゲットまでの距離 float dis_y = Math.abs(cy - _targetY); // Ｙ方向のターゲットまでの距離
	 * float dis = Math.max(dis_x, dis_y); // ターゲットまでの距離 float avg_x = 0; float
	 * avg_y = 0; if (dis == 0) { // 目標と同じ位置なら向いてる方向へ真っ直ぐ if (head == 1) { avg_x
	 * = 1; avg_y = -1; } else if (head == 2) { avg_x = 1; avg_y = 0; } else if
	 * (head == 3) { avg_x = 1; avg_y = 1; } else if (head == 4) { avg_x = 0;
	 * avg_y = 1; } else if (head == 5) { avg_x = -1; avg_y = 1; } else if (head
	 * == 6) { avg_x = -1; avg_y = 0; } else if (head == 7) { avg_x = -1; avg_y
	 * = -1; } else if (head == 0) { avg_x = 0; avg_y = -1; } } else { avg_x =
	 * dis_x / dis; avg_y = dis_y / dis; }
	 *
	 * int add_x = (int) Math.floor((avg_x 15) + 0.59f); // 上下左右がちょっと優先な丸め int
	 * add_y = (int) Math.floor((avg_y 15) + 0.59f); // 上下左右がちょっと優先な丸め
	 *
	 * if (cx > _targetX) { add_x *= -1; } if (cy > _targetY) { add_y *= -1; }
	 *
	 * _targetX = _targetX + add_x; _targetY = _targetY + add_y; }
	 */

	/* ■■■■■■■■■■■■■■■ 計算結果反映 ■■■■■■■■■■■■■■■ */

	public void commit() {
		if (_isHit) {
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				commitPc();
			} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
				commitNpc();
			}
		}

		// ダメージ値及び命中率確認用メッセージ
		if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.getAttackLog()) {
			return;
		}
		if ((_calcType == PC_PC || _calcType == NPC_PC) && !_targetPc.getAttackLog()) {
			return;
		}

		String msg0 = "";
		String msg1 = I18N_ATTACK_TO; // に
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";
		if (_calcType == PC_PC || _calcType == PC_NPC) { // アタッカーがＰＣの場合
			msg0 = _pc.getName();
		} else if (_calcType == NPC_PC) { // アタッカーがＮＰＣの場合
			msg0 = _npc.getName();
		}

		if (_calcType == NPC_PC || _calcType == PC_PC) { // ターゲットがＰＣの場合
			msg4 = _targetPc.getName();
			msg2 = "HitR" + _hitRate + "% THP" + _targetPc.getCurrentHp();
		} else if (_calcType == PC_NPC) { // ターゲットがＮＰＣの場合
			msg4 = _targetNpc.getName();
			msg2 = "Hit" + _hitRate + "% Hp" + _targetNpc.getCurrentHp();
		}
		msg3 = _isHit ? String.format(I18N_ATTACK_DMG, _damage) : I18N_ATTACK_MISS;
		// %dのダメージを与えました。 : 攻撃をミスしました。

		if (_calcType == PC_PC || _calcType == PC_NPC) { // アタッカーがＰＣの場合
			//_pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3, msg4));
			// \f1%0が%4%1%3 %2
			_pc.sendPackets(new S_SystemMessage(I18N_ATTACK_GAVE_TEXT_COLOR +
				MessageFormat.format(I18N_ATTACK_FORMAT, msg0, msg4, msg3, msg2)));
			// {0}が{1}に{2} {3}
		}
		if (_calcType == NPC_PC || _calcType == PC_PC) { // ターゲットがＰＣの場合
			// _targetPc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3, msg4));
			// \f1%0が%4%1%3 %2
			_targetPc.sendPackets(new S_SystemMessage(I18N_ATTACK_RECEIVED_TEXT_COLOR +
				MessageFormat.format(I18N_ATTACK_FORMAT, msg0, msg4, msg3, msg2)));
			// {0}が{1}に{2} {3}
		}
	}

	// ●●●● プレイヤーに計算結果を反映 ●●●●
	private void commitPc() {
		if (_calcType == PC_PC) {
			if (_drainMana > 0 && _targetPc.getCurrentMp() > 0) {
				if (_drainMana > _targetPc.getCurrentMp()) {
					_drainMana = _targetPc.getCurrentMp();
				}
				short newMp = (short) (_targetPc.getCurrentMp() - _drainMana);
				_targetPc.setCurrentMp(newMp);
				newMp = (short) (_pc.getCurrentMp() + _drainMana);
				_pc.setCurrentMp(newMp);
			}
			if (_drainHp > 0) { // HP吸収による回復
				short newHp = (short) (_pc.getCurrentHp() + _drainHp);
				_pc.setCurrentHp(newHp);
			}
			_targetPc.receiveDamage(_pc, _damage, false);
		} else if (_calcType == NPC_PC) {
			_targetPc.receiveDamage(_npc, _damage, false);
		}
	}

	// ●●●● ＮＰＣに計算結果を反映 ●●●●
	private void commitNpc() {
		if (_calcType == PC_NPC) {
			if (_drainMana > 0) {
				int drainValue = _targetNpc.drainMana(_drainMana);
				int newMp = _pc.getCurrentMp() + drainValue;
				_pc.setCurrentMp(newMp);
				if (drainValue > 0) {
					int newMp2 = _targetNpc.getCurrentMp() - drainValue;
					_targetNpc.setCurrentMpDirect(newMp2);
				}
			}
			if (_drainHp > 0) { // HP吸収による回復
				short newHp = (short) (_pc.getCurrentHp() + _drainHp);
				_pc.setCurrentHp(newHp);
			}
			damageNpcWeaponDurability(); // 武器を損傷させる。
			_targetNpc.receiveDamage(_pc, _damage);
		} else if (_calcType == NPC_NPC) {
			_targetNpc.receiveDamage(_npc, _damage);
		}
	}

	/* ■■■■■■■■■■■■■■■ カウンターバリア ■■■■■■■■■■■■■■■ */

	// ■■■■ カウンターバリア時の攻撃モーション送信 ■■■■
	public void actionCounterBarrier() {
		if (_calcType == PC_PC) {
			_pc.setHeading(_pc.targetDirection(_targetX, _targetY)); // 向きのセット
			_pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
			_pc.broadcastPacket(new S_AttackMissPacket(_pc, _targetId));
			_pc.sendPackets(new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
			_pc.broadcastPacket(new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
			_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 10710));
			_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 10710));
		} else if (_calcType == NPC_PC) {
			int actId = 0;
			_npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // 向きのセット
			if (getActId() > 0) {
				actId = getActId();
			} else {
				actId = ActionCodes.ACTION_BowAttack | ActionCodes.ACTION_ThrowingKnifeAttack;
			}
			if (getGfxId() > 0) {
				_npc
						.broadcastPacket(new S_UseAttackSkill(_target, _npc
								.getId(), getGfxId(), _targetX, _targetY,
								actId, 0));
			} else {
				_npc.broadcastPacket(new S_AttackMissPacket(_npc, _targetId,
						actId));
			}
			_npc.broadcastPacket(new S_DoActionGFX(_npc.getId(),
					ActionCodes.ACTION_Damage));
			_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), 10710));
			_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 10710));
		}
	}

	// ■■■■ 相手の攻撃に対してカウンターバリアが有効かを判別 ■■■■
	public boolean isShortDistance() {
		boolean isShortDistance = true;
		if (_calcType == PC_PC) {
			if (_weaponType == 20 || _weaponType == 62 || _weaponType2 == 14) { // 弓、ガントレット、キーリンク
				isShortDistance = false;
			}
		} else if (_calcType == NPC_PC) {
			boolean isLongRange = (_npc.getLocation().getTileLineDistance(
					new Point(_targetX, _targetY)) > 1);
			int bowActId = _npc.getNpcTemplate().getBowActId();
			// 距離が2以上、攻撃者の弓のアクションIDがある場合は遠攻撃
			if (isLongRange && bowActId > 0) {
				isShortDistance = false;
			}
		}
		return isShortDistance;
	}

	// ■■■■ カウンターバリアのダメージを反映 ■■■■
	public void commitCounterBarrier() {
		int damage = calcCounterBarrierDamage();
		if (damage == 0) {
			return;
		}
		if (_calcType == PC_PC) {
			_pc.receiveDamage(_targetPc, damage, false);
		} else if (_calcType == NPC_PC) {
			_npc.receiveDamage(_targetPc, damage);
		}
	}

	// ●●●● カウンターバリアのダメージを算出 ●●●●
	private int calcCounterBarrierDamage() {
		int damage = 0;
		L1ItemInstance weapon = null;
		weapon = _targetPc.getWeapon();
		if (weapon != null) {
			if (weapon.getItem().getType() == 2) { // 両手剣(BIG最大ダメージ+強化数+追加ダメージ)*2
				damage = (weapon.getItem().getDmgLarge()
						+ weapon.getEnchantLevel() + weapon.getItem().getDmgModifier()) * 2;
			}
		}
		return damage;
	}

	/*
	 * 武器を損傷させる。 対NPCの場合、損傷確率は10%とする。祝福武器は3%とする。
	 */
	private void damageNpcWeaponDurability() {
		int chance = 10;
		int bchance = 3;

		/*
		 * 損傷しないNPC、素手、損傷しない武器使用、SOF中の場合何もしない。
		 */
		if (_calcType != PC_NPC
				|| _targetNpc.getNpcTemplate().isHard() == false
				|| _weaponType == 0 || weapon.getCanBeDmg() == false
				|| _pc.hasSkillEffect(SOUL_OF_FLAME)) {
			return;
		}
		// 通常の武器・呪われた武器
		if ((_weaponBless == 0 || _weaponBless == 2)
				&& ((_random.nextInt(100) + 1) < chance)) {
			// \f1あなたの%0が損傷しました。
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon, 1);
		}
		// 祝福された武器
		if (_weaponBless == 1 && ((_random.nextInt(100) + 1) < bchance)) {
			// \f1あなたの%0が損傷しました。
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon, 1);
		}
	}

	/**
	 * パプリオンハイドロ系防具
	 * 一定確率でHP回復
	 */
	public void getHprByArmor() { // HP回復 (一定確率)
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int chance = _random.nextInt(100) + 1;
			@SuppressWarnings("unused")
			int healHp = 0;
			if (chance > 0) {
				healHp += 100 + _random.nextInt(50) + 1;
			}
			_pc.sendPackets(new S_SkillSound(_pc.getId(), 2187));
			_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 2187));
		}
		return;
	}

	/**
	 * リンドビオルストーム系防具
	 * 一定確率で弓攻撃反射
	 */
	public void actionStormBarrier() { // 弓攻撃反射 (一定確率)
			if (_calcType == PC_PC) {
				_pc.setHeading(_pc.targetDirection(_targetX, _targetY)); // 向きのセット
				_pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
				_pc.broadcastPacket(new S_AttackMissPacket(_pc, _targetId));
				_pc.sendPackets(new S_DoActionGFX(_pc.getId(),
						ActionCodes.ACTION_Damage));
				_pc.broadcastPacket(new S_DoActionGFX(_pc.getId(),
						ActionCodes.ACTION_Damage));
				_pc.sendPackets(new S_SkillSound(_targetPc.getId(), 10419));
				_pc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 10419));
			} else if (_calcType == NPC_PC) {
				int actId = 0;
				_npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // 向きのセット
					if (getActId() > 0) {
						actId = getActId();
					} else {
						actId = ActionCodes.ACTION_BowAttack;
					}
					if (getGfxId() > 0) {
						_npc.broadcastPacket(new S_UseAttackSkill(_target, _npc
										.getId(), getGfxId(), _targetX, _targetY,
										actId, 0));
					} else {
						_npc.broadcastPacket(new S_AttackMissPacket(_npc, _targetId,
								actId));
					}
					_npc.broadcastPacket(new S_DoActionGFX(_npc.getId(),
							ActionCodes.ACTION_Damage));
					_npc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 10419));
				}
			}
	// ■■■■ 相手の攻撃に対してリンドビオルの加護が有効かを判別 ■■■■
	public boolean isLongDistance() {
		boolean isLongDistance = false;
		if (_calcType == PC_PC) {
			boolean isRange = (_pc.getLocation().getTileLineDistance(
					new Point(_targetX, _targetY)) > 1);
			if (_weaponType == 20 || _weaponType == 62) {
				if (isRange) {
				isLongDistance = true;
				}
			}
		} if (_calcType == NPC_PC) {
			boolean isRange = (_npc.getLocation().getTileLineDistance(
					new Point(_targetX, _targetY)) > 1);
			int bowActId = _npc.getNpcTemplate().getBowActId();
			// 距離が2以上、攻撃者の弓のアクションIDがある場合は遠攻撃
			if (isRange && bowActId > 0) {
				isLongDistance = true;
			}
		}
	return isLongDistance;
	}
	// ■■■■ リンドビオルの加護のダメージを反映 ■■■■
	public void commitStormBarrier() {
		int damage = calcStormBarrierDamage();
		if (damage == 0) {
			return;
		}
		if (_calcType == PC_PC) {
			_pc.receiveDamage(_targetPc, damage, false);
		} else if (_calcType == NPC_PC) {
			_npc.receiveDamage(_targetNpc, damage);
		}
	}

	// ●●●● リンドビオルの加護のダメージを算出 ●●●●
	private int calcStormBarrierDamage() {
		int damage = 0;
		L1ItemInstance weapon = null;
		weapon = _targetPc.getWeapon();
				damage = (weapon.getItem().getDmgSmall()
						+ weapon.getEnchantLevel() + weapon.getItem().getDmgModifier());
		return damage;
	}
}