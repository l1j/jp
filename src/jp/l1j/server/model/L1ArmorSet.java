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

package jp.l1j.server.model;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import jp.l1j.server.datatables.ArmorSetTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1ArmorSets;
import static jp.l1j.server.model.skill.L1SkillId.*;

public abstract class L1ArmorSet {
	public abstract void giveEffect(L1PcInstance pc);

	public abstract void cancelEffect(L1PcInstance pc);

	public abstract boolean isValid(L1PcInstance pc);

	public abstract boolean isPartOfSet(int id);

	public abstract boolean isEquippedRingOfArmorSet(L1PcInstance pc);

	public static ArrayList<L1ArmorSet> getAllSet() {
		return _allSet;
	}

	private static ArrayList<L1ArmorSet> _allSet = new ArrayList<L1ArmorSet>();

	/*
	 * ここで初期化してしまうのはいかがなものか・・・美しくない気がする
	 */
	static {
		L1ArmorSetImpl impl;

		for (L1ArmorSets armorSets : ArmorSetTable.getInstance().getAllList()) {
			try {

				impl = new L1ArmorSetImpl(getArray(armorSets.getSets(), ","));
				if (armorSets.getPolyId() != -1) {
					impl.addEffect(new PolymorphEffect(armorSets.getPolyId()));
				}
				impl.addEffect(new BaseBonusEffect(armorSets.getAc(),
						armorSets.getHp(), armorSets.getHpr(),
						armorSets.getMp(), armorSets.getMpr(),
						armorSets.getSp(), armorSets.getMr()));
				impl.addEffect(new ReductionBonusEffect(
						armorSets.getDamageReduction(),
						armorSets.getWeightReduction()));
				impl.addEffect(new ModifierBonusEffect(
						armorSets.getHitModifier(),
						armorSets.getDmgModifier(),
						armorSets.getBowHitModifier(),
						armorSets.getBowDmgModifier()));
				impl.addEffect(new StatusBonusEffect(armorSets.getStr(),
						armorSets.getDex(), armorSets.getCon(),
						armorSets.getWis(), armorSets.getCha(),
						armorSets.getInt()));
				impl.addEffect(new DefenseBonusEffect(armorSets.getDefenseFire(),
						armorSets.getDefenseWater(), armorSets.getDefenseEarth(),
						armorSets.getDefenseWind()));
				impl.addEffect(new ResistBonusEffect(armorSets.getResistStun(),
						armorSets.getResistStone(), armorSets.getResistSleep(),
						armorSets.getResistFreeze(), armorSets.getResistHold(),
						armorSets.getResistBlind()));
				impl.addEffect(new SpecialBonusEffect(armorSets.getIsHaste(),
						armorSets.getExpBonus(), armorSets.getPotionRecoveryRate()));
				_allSet.add(impl);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static int[] getArray(String s, String sToken) {
		StringTokenizer st = new StringTokenizer(s, sToken);
		int size = st.countTokens();
		String temp = null;
		int[] array = new int[size];
		for (int i = 0; i < size; i++) {
			temp = st.nextToken();
			array[i] = Integer.parseInt(temp);
		}
		return array;
	}
}

interface L1ArmorSetEffect {
	public void giveEffect(L1PcInstance pc);

	public void cancelEffect(L1PcInstance pc);
}

class L1ArmorSetImpl extends L1ArmorSet {
	private final int _ids[];
	private final ArrayList<L1ArmorSetEffect> _effects;
	private static Logger _log = Logger.getLogger(L1ArmorSetImpl.class
			.getName());

	protected L1ArmorSetImpl(int ids[]) {
		_ids = ids;
		_effects = new ArrayList<L1ArmorSetEffect>();
	}

	public void addEffect(L1ArmorSetEffect effect) {
		_effects.add(effect);
	}

	public void removeEffect(L1ArmorSetEffect effect) {
		_effects.remove(effect);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		for (L1ArmorSetEffect effect : _effects) {
			effect.cancelEffect(pc);
		}
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		for (L1ArmorSetEffect effect : _effects) {
			effect.giveEffect(pc);
		}
	}

	@Override
	public final boolean isValid(L1PcInstance pc) {
		return pc.getInventory().checkEquipped(_ids);
	}

	@Override
	public boolean isPartOfSet(int id) {
		for (int i : _ids) {
			if (id == i) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isEquippedRingOfArmorSet(L1PcInstance pc) {
		L1PcInventory pcInventory = pc.getInventory();
		L1ItemInstance armor = null;
		boolean isSetContainRing = false;

		// セット装備にリングが含まれているか調べる
		for (int id : _ids) {
			armor = pcInventory.findItemId(id);
			if (armor.getItem().getType2() == 2
					&& armor.getItem().getType() == 11) { // ring
				isSetContainRing = true;
				break;
			}
		}

		// リングを2つ装備していて、それが両方セット装備か調べる
		if (armor != null && isSetContainRing) {
			int itemId = armor.getItem().getItemId();
			if (pcInventory.getTypeEquipped(2, 11) == 2) {
				L1ItemInstance ring[] = new L1ItemInstance[2];
				ring = pcInventory.getRingEquipped();
				if (ring[0].getItem().getItemId() == itemId
						&& ring[1].getItem().getItemId() == itemId) {
					return true;
				}
			}
		}
		return false;
	}

}

class BaseBonusEffect implements L1ArmorSetEffect {
	private final int _ac;
	private final int _hp;
	private final int _hpr;
	private final int _mp;
	private final int _mpr;
	private final int _sp;
	private final int _mr;

	public BaseBonusEffect(int ac, int hp, int hpr, int mp, int mpr,
			int sp, int mr) {
		_ac = ac;
		_hp = hp;
		_hpr = hpr;
		_mp = mp;
		_mpr = mpr;
		_sp = sp;
		_mr = mr;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		pc.addAc(_ac);
		pc.addMaxHp(_hp);
		pc.addHpr(_hpr);
		pc.addMaxMp(_mp);
		pc.addMpr(_mpr);
		pc.addSp(_sp);
		pc.addMr(_mr);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		pc.addAc(-_ac);
		pc.addMaxHp(-_hp);
		pc.addHpr(-_hpr);
		pc.addMaxMp(-_mp);
		pc.addMpr(-_mpr);
		pc.addSp(-_sp);
		pc.addMr(-_mr);
	}
}

class ReductionBonusEffect implements L1ArmorSetEffect {
	private final int _damageReduction;
	private final int _weightReduction;

	public ReductionBonusEffect(int damageReduction, int weightReduction) {
		_damageReduction = damageReduction;
		_weightReduction = weightReduction;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		pc.addDamageReductionByArmor(_damageReduction);
		pc.addWeightReduction(_weightReduction);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		pc.addDamageReductionByArmor(-_damageReduction);
		pc.addWeightReduction(-_weightReduction);
	}
}

class ModifierBonusEffect implements L1ArmorSetEffect {
	private final int _hitModifier;
	private final int _dmgModifier;
	private final int _bowHitModifier;
	private final int _bowDmgModifier;

	public ModifierBonusEffect(int hitModifier, int dmgModifier,
			int bowHitModifier, int bowDmgModifier) {
		_hitModifier = hitModifier;
		_dmgModifier = dmgModifier;
		_bowHitModifier = bowHitModifier;
		_bowDmgModifier = bowDmgModifier;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		pc.addHitModifierByArmor(_hitModifier);
		pc.addDmgModifierByArmor(_dmgModifier);
		pc.addBowHitModifierByArmor(_bowHitModifier);
		pc.addBowDmgModifierByArmor(_bowDmgModifier);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		pc.addHitModifierByArmor(-_hitModifier);
		pc.addDmgModifierByArmor(-_dmgModifier);
		pc.addBowHitModifierByArmor(-_bowHitModifier);
		pc.addBowDmgModifierByArmor(-_bowDmgModifier);
	}
}

class StatusBonusEffect implements L1ArmorSetEffect {
	private final int _str;
	private final int _dex;
	private final int _con;
	private final int _wis;
	private final int _cha;
	private final int _int;

	public StatusBonusEffect(int addStr, int addDex, int addCon, int addWis,
			int addCha, int addInt) {
		_str = addStr;
		_dex = addDex;
		_con = addCon;
		_wis = addWis;
		_cha = addCha;
		_int = addInt;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		pc.addStr((byte) _str);
		pc.addDex((byte) _dex);
		pc.addCon((byte) _con);
		pc.addWis((byte) _wis);
		pc.addCha((byte) _cha);
		pc.addInt((byte) _int);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		pc.addStr((byte) -_str);
		pc.addDex((byte) -_dex);
		pc.addCon((byte) -_con);
		pc.addWis((byte) -_wis);
		pc.addCha((byte) -_cha);
		pc.addInt((byte) -_int);
	}
}

class DefenseBonusEffect implements L1ArmorSetEffect {
	private final int _defenseFire;
	private final int _defenseWater;
	private final int _defenseEarth;
	private final int _defenseWind;

	public DefenseBonusEffect(int defenseFire, int defenseWater,
			int defenseEarth, int defenseWind) {
		_defenseFire = defenseFire;
		_defenseWater = defenseWater;
		_defenseEarth = defenseEarth;
		_defenseWind = defenseWind;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		pc.addFire(_defenseFire);
		pc.addWater(_defenseWater);
		pc.addEarth(_defenseEarth);
		pc.addWind(_defenseWind);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		pc.addFire(-_defenseFire);
		pc.addWater(-_defenseWater);
		pc.addEarth(-_defenseEarth);
		pc.addWind(-_defenseWind);
	}
}

class ResistBonusEffect implements L1ArmorSetEffect {
	private final int _resistStun;
	private final int _resistStone;
	private final int _resistSleep;
	private final int _resistFreeze;
	private final int _resistHold;
	private final int _resistBlind;

	public ResistBonusEffect(int resistStun, int resistStone, int resistSleep,
			int resistFreeze, int resistHold, int resistBlind) {
		_resistStun = resistStun;
		_resistStone = resistStone;
		_resistSleep = resistSleep;
		_resistFreeze = resistFreeze;
		_resistHold = resistHold;
		_resistBlind = resistBlind;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		pc.addResistStun(_resistStun);
		pc.addResistStone(_resistStone);
		pc.addResistSleep(_resistSleep);
		pc.addResistFreeze(_resistFreeze);
		pc.addResistHold(_resistHold);
		pc.addResistBlind(_resistBlind);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		pc.addResistStun(-_resistStun);
		pc.addResistStone(-_resistStone);
		pc.addResistSleep(-_resistSleep);
		pc.addResistFreeze(-_resistFreeze);
		pc.addResistHold(-_resistHold);
		pc.addResistBlind(-_resistBlind);
	}
}

class SpecialBonusEffect implements L1ArmorSetEffect {
	private final boolean _isHaste;
	private final int _expBonus;
	private final int _potionRecoveryRate;

	public SpecialBonusEffect(boolean isHaste, int expBonus, int potionRecoveryRate) {
		_isHaste = isHaste;
		_expBonus = expBonus;
		_potionRecoveryRate = potionRecoveryRate;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		if (_isHaste) {
			pc.addHasteItemEquipped(1);
		}
		pc.addExpBonusPct(_expBonus);
		pc.addPotionRecoveryRatePct(_potionRecoveryRate);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		if (_isHaste) {
			pc.addDamageReductionByArmor(-1);
		}
		pc.addExpBonusPct(-_expBonus);
		pc.addPotionRecoveryRatePct(-_potionRecoveryRate);
	}
}

class PolymorphEffect implements L1ArmorSetEffect {
	private int _gfxId;

	public PolymorphEffect(int gfxId) {
		_gfxId = gfxId;
	}

	@Override
	public void giveEffect(L1PcInstance pc) {
		if (_gfxId == 6080 || _gfxId == 6094) {
			if (pc.getSex() == 0) {
				_gfxId = 6094;
			} else {
				_gfxId = 6080;
			}
			if (!isRemainderOfCharge(pc)) { // 残チャージ数なし
				return;
			}
		}
		L1PolyMorph.doPoly(pc, _gfxId, 0, L1PolyMorph.MORPH_BY_ITEMMAGIC);
	}

	@Override
	public void cancelEffect(L1PcInstance pc) {
		if (_gfxId == 6080) {
			if (pc.getSex() == 0) {
				_gfxId = 6094;
			}
		}
		if (pc.getTempCharGfx() != _gfxId) {
			return;
		}
		L1PolyMorph.undoPoly(pc);
	}

	private boolean isRemainderOfCharge(L1PcInstance pc) {
		boolean isRemainderOfCharge = false;
		if (pc.getInventory().checkItem(20383, 1)) {
			L1ItemInstance item = pc.getInventory().findItemId(20383);
			if (item != null) {
				if (item.getChargeCount() != 0) {
					isRemainderOfCharge =true;
				}
			}
		}
		return isRemainderOfCharge;
	}

}
