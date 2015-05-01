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

import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.datatables.MagicDollTable;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.instance.L1DollInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;

public class L1MagicDoll {

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	public static int getHitAddByDoll(L1Character _master) { // 近接命中力増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getHit();
			}
		}
		return s;
	}

	public static int getDamageAddByDoll(L1Character _master) { // 　近接攻撃力増加
		int s = 0;
		int chance = _random.nextInt(100) + 1;
		boolean isAdd = false;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				if (doll.getDmgChance() > 0 && !isAdd) {
					if (doll.getDmgChance() >= chance) {
						s += doll.getDmg();
						isAdd = true;
					}
				} else if (doll.getDmg() != 0) {
					s += doll.getDmg();
				}
			}
		}
		if (isAdd) {
			if (_master instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) _master;
				pc.sendPackets(new S_SkillSound(_master.getId(), 6319));
			}
			_master.broadcastPacket(new S_SkillSound(_master.getId(), 6319));
		}
		return s;
	}

	public static int getDamageReductionByDoll(L1Character _master) { // ダメージリダクション
		int s = 0;
		int chance = _random.nextInt(100) + 1;
		boolean isReduction = false;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				if (doll.getDmgReductionChance() > 0 && !isReduction) {
					if (doll.getDmgReductionChance() >= chance) {
						s += doll.getDmgReduction();
						isReduction = true;
					}
				} else if (doll.getDmgReduction() != 0) {
					s += doll.getDmgReduction();
				}
			}
		}
		if (isReduction) {
			if (_master instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) _master;
				pc.sendPackets(new S_SkillSound(_master.getId(), 6320));
			}
			_master.broadcastPacket(new S_SkillSound(_master.getId(), 6320));
		}
		return s;
	}

	public static int getDamageEvasionByDoll(L1Character _master) { // 回避率
		int chance = _random.nextInt(100) + 1;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				if (doll.getDmgEvasionChance() >= chance) {
					if (_master instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) _master;
						pc.sendPackets(new S_SkillSound(_master.getId(), 6320));
					}
					_master.broadcastPacket(new S_SkillSound(_master.getId(),
							6320));
					return 1;
				}
			}
		}
		return 0;
	}

	public static int getBowHitAddByDoll(L1Character _master) { // 弓命中率増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getBowHit();
			}
		}
		return s;
	}

	public static int getBowDamageByDoll(L1Character _master) { // 弓攻撃増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getBowDmg();
			}
		}
		return s;
	}

	public static int getAcByDoll(L1Character _master) { // AC増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getAc();
			}
		}
		return s;
	}

	public static int getStrByDoll(L1Character _master) { // STR増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getStr();
			}
		}
		return s;
	}

	public static int getConByDoll(L1Character _master) { // CON増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getCon();
			}
		}
		return s;
	}

	public static int getDexByDoll(L1Character _master) { // DEX増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getDex();
			}
		}
		return s;
	}

	public static int getIntByDoll(L1Character _master) { // INT増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getInt();
			}
		}
		return s;
	}

	public static int getWisByDoll(L1Character _master) { // WIS増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getWis();
			}
		}
		return s;
	}

	public static int getChaByDoll(L1Character _master) { // CHA増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getCha();
			}
		}
		return s;
	}

	public static int getResistStoneByDoll(L1Character _master) { // 石化耐性増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getResistStone();
			}
		}
		return s;
	}

	public static int getResistStunByDoll(L1Character _master) { // スタン耐性増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getResistStun();
			}
		}
		return s;
	}

	public static int getResistHoldByDoll(L1Character _master) { // ホールド耐性増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getResistHold();
			}
		}
		return s;
	}

	public static int getResistBlindByDoll(L1Character _master) { // 暗闇耐性増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getResistBlind();
			}
		}
		return s;
	}

	public static int getResistFreezeByDoll(L1Character _master) { // 凍結耐性増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getResistFreeze();
			}
		}
		return s;
	}

	public static int getResistSleepByDoll(L1Character _master) { // 睡眠耐性増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getResistSleep();
			}
		}
		return s;
	}

	public static int getWeightReductionByDoll(L1Character _master) { // 重量軽減
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getWeightReduction();
			}
		}
		return s;
	}

	public static int getHpByDoll(L1Character _master) { // HP増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getHp();
			}
		}
		return s;
	}

	public static int getMpByDoll(L1Character _master) { // MP増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getMp();
			}
		}
		return s;
	}

	public static int getMrByDoll(L1Character _master) { // MR増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getMr();
			}
		}
		return s;
	}

	public static int getExpBonusByDoll(L1Character _master) { // EXP獲得増加
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				s += doll.getExpBonus();
			}
		}
		return s;
	}

	public static boolean enableMakeItem(L1DollInstance _doll) {
		L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(_doll.getItemId());
		if (doll != null) {
			L1Item item = ItemTable.getInstance().getTemplate((doll.getMakeItemId()));
			if (item != null && doll.getMakeTime() > 0) {
				return true;
			}
		}
		return false;
	}

	public static int getMakeItemIdByDoll(L1DollInstance _doll) { // アイテム獲得
		L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(_doll.getItemId());
		if (doll == null) {
			return 0;
		}
		L1Item item = ItemTable.getInstance().getTemplate((doll.getMakeItemId()));
		return item != null ? item.getItemId() : 0;
	}

	public static int getMakeTimeByDoll(L1DollInstance _doll) { // アイテム製作間隔
		L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(_doll.getItemId());
		return doll != null ? doll.getMakeTime() : 0;
	}

	public static boolean enableHpr(L1DollInstance _doll) { // HPR判定
		L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(_doll.getItemId());
		if (doll != null) {
			if (doll.getHprTime() > 0 && doll.getHpr() > 0) {
				return true;
			}
		}
		return false;
	}

	public static int getHprByDoll(L1DollInstance _doll) { // HP回復
		L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(_doll.getItemId());
		return doll != null ? doll.getHpr() : 0;
	}

	public static int getHprTimeByDoll(L1DollInstance _doll) { // HP回復間隔
		L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(_doll.getItemId());
		return doll != null ? doll.getHprTime() : 0;
	}

	public static int getNatHprByDoll(L1Character _master) { // HP自然回復
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if(doll != null) {
				if(doll.getHprTime() == 0 && doll.getHpr() != 0) {
					s += doll.getHpr();
				}
			}
		}
		return s;
	}

	public static boolean enableMpr(L1DollInstance _doll) { // MPR判定
		L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(_doll.getItemId());
		if (doll != null) {
			if (doll.getMprTime() > 0 && doll.getMpr() > 0) {
				return true;
			}
		}
		return false;
	}

	public static int getMprByDoll(L1DollInstance _doll) { // MP回復
		L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(_doll.getItemId());
		return doll != null ? doll.getMpr() : 0;
	}

	public static int getMprTimeByDoll(L1DollInstance _doll) { // MP回復間隔
		L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(_doll.getItemId());
		return doll != null ? doll.getMprTime() : 0;
	}

	public static int getNatMprByDoll(L1Character _master) { // MP自然回復
		int s = 0;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if(doll != null) {
				if(doll.getMprTime() == 0 && doll.getMpr() != 0) {
					s += doll.getMpr();
				}
			}
		}
		return s;
	}

	public static int getEffectByDoll(L1Character _master, int type) { // スキル
		int chance = _random.nextInt(100) + 1;
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				if (doll.getSkillId() == type) {
					if (doll.getSkillChance() >= chance) {
						return type;
					}
				}
			}
		}
		return 0;
	}

	public static boolean isBlessOfEva(L1Character _master) { // エヴァの祝福効果
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				if (doll.getDollId() >= 80226 && doll.getDollId() <= 80231) {
					// マジックドール：マーメイド
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isHaste(L1Character _master) { // ヘイスト
		for (Object obj : _master.getDollList().values().toArray()) {
			L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(
					((L1DollInstance) obj).getItemId());
			if (doll != null) {
				if (doll.getDollId() >= 80232 && doll.getDollId() <= 80237) {
					// マジックドール：ブルート
					return true;
				}
			}
		}
		return false;
	}

	public static int getSummonTime(L1DollInstance _doll) { // マジックドール召喚時間
		L1MagicDoll doll = MagicDollTable.getInstance().getTemplate(_doll.getItemId());
		return doll != null ? doll.getSummonTime() : 0;
	}

	private int _itemId;
	private int _dollId;
	private int _ac;
	private int _str;
	private int _con;
	private int _dex;
	private int _int;
	private int _wis;
	private int _cha;
	private int _hp;
	private int _mp;
	private int _hpr;
	private int _mpr;
	private int _hprTime;
	private int _mprTime;
	private int _mr;
	private int _dmg;
	private int _bowDmg;
	private int _dmgChance;
	private int _hit;
	private int _bowHit;
	private int _dmgReduction;
	private int _dmgReductionChance;
	private int _dmgEvasionChance;
	private int _weightReduction;
	private int _resistStun;
	private int _resistStone;
	private int _resistSleep;
	private int _resistFreeze;
	private int _resistHold;
	private int _resistBlind;
	private int _expBonus;
	private int _makeItemId;
	private int _makeTime;
	private int _skillId;
	private int _skillChance;
	private int _summonTime;

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(int i) {
		_itemId = i;
	}

	public int getDollId() {
		return _dollId;
	}

	public void setDollId(int i) {
		_dollId = i;
	}

	public int getAc() {
		return _ac;
	}

	public void setAc(int i) {
		_ac = i;
	}

	public int getStr() {
		return _str;
	}

	public void setStr(int i) {
		_str = i;
	}

	public int getCon() {
		return _con;
	}

	public void setCon(int i) {
		_con = i;
	}

	public int getDex() {
		return _dex;
	}

	public void setDex(int i) {
		_dex = i;
	}

	public int getInt() {
		return _int;
	}

	public void setInt(int i) {
		_int = i;
	}

	public int getWis() {
		return _wis;
	}

	public void setWis(int i) {
		_wis = i;
	}

	public int getCha() {
		return _cha;
	}

	public void setCha(int i) {
		_cha = i;
	}

	public int getHp() {
		return _hp;
	}

	public void setHp(int i) {
		_hp = i;
	}

	public int getMp() {
		return _mp;
	}

	public void setMp(int i) {
		_mp = i;
	}

	public int getHpr() {
		return _hpr;
	}

	public void setHpr(int i) {
		_hpr = i;
	}

	public int getMpr() {
		return _mpr;
	}

	public void setMpr(int i) {
		_mpr = i;
	}

	public int getHprTime() {
		return _hprTime;
	}

	public void setHprTime(int i) {
		_hprTime = i;
	}

	public int getMprTime() {
		return _mprTime;
	}

	public void setMprTime(int i) {
		_mprTime = i;
	}

	public int getMr() {
		return _mr;
	}

	public void setMr(int i) {
		_mr = i;
	}

	public int getDmg() {
		return _dmg;
	}

	public void setDmg(int i) {
		_dmg = i;
	}

	public int getBowDmg() {
		return _bowDmg;
	}

	public void setBowDmg(int i) {
		_bowDmg = i;
	}

	public int getDmgChance() {
		return _dmgChance;
	}

	public void setDmgChance(int i) {
		_dmgChance = i;
	}

	public int getHit() {
		return _hit;
	}

	public void setHit(int i) {
		_hit = i;
	}

	public int getBowHit() {
		return _bowHit;
	}

	public void setBowHit(int i) {
		_bowHit = i;
	}

	public int getDmgReduction() {
		return _dmgReduction;
	}

	public void setDmgReduction(int i) {
		_dmgReduction = i;
	}

	public int getDmgReductionChance() {
		return _dmgReductionChance;
	}

	public void setDmgReductionChance(int i) {
		_dmgReductionChance = i;
	}

	public int getDmgEvasionChance() {
		return _dmgEvasionChance;
	}

	public void setDmgEvasionChance(int i) {
		_dmgEvasionChance = i;
	}

	public int getWeightReduction() {
		return _weightReduction;
	}

	public void setWeightReduction(int i) {
		_weightReduction = i;
	}

	public int getResistStun() {
		return _resistStun;
	}

	public void setResistStun(int i) {
		_resistStun = i;
	}

	public int getResistStone() {
		return _resistStone;
	}

	public void setResistStone(int i) {
		_resistStone = i;
	}

	public int getResistSleep() {
		return _resistSleep;
	}

	public void setResistSleep(int i) {
		_resistSleep = i;
	}

	public int getResistFreeze() {
		return _resistFreeze;
	}

	public void setResistFreeze(int i) {
		_resistFreeze = i;
	}

	public int getResistHold() {
		return _resistHold;
	}

	public void setResistHold(int i) {
		_resistHold = i;
	}

	public int getResistBlind() {
		return _resistBlind;
	}

	public void setResistBlind(int i) {
		_resistBlind = i;
	}

	public int getExpBonus() {
		return _expBonus;
	}

	public void setExpBonus(int i) {
		_expBonus = i;
	}

	public int getMakeItemId() {
		return _makeItemId;
	}

	public void setMakeItemId(int i) {
		_makeItemId = i;
	}

	public int getMakeTime() {
		return _makeTime;
	}

	public void setMakeTime(int i) {
		_makeTime = i;
	}

	public int getSkillId() {
		return _skillId;
	}

	public void setSkillId(int i) {
		_skillId = i;
	}

	public int getSkillChance() {
		return _skillChance;
	}

	public void setSkillChance(int i) {
		_skillChance = i;
	}

	public int getSummonTime() {
		return _summonTime;
	}

	public void setSummonTime(int i) {
		_summonTime = i;
	}
}
