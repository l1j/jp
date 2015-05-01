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

import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.utils.ReflectionUtil;

public class L1Npc extends L1Object implements Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public L1Npc clone() {
		try {
			return (L1Npc) (super.clone());
		} catch (CloneNotSupportedException e) {
			throw (new InternalError(e.getMessage()));
		}
	}

	public L1Npc() {
	}

	private int _npcId;

	public int getNpcId() {
		return _npcId;
	}

	public void setNpcId(int i) {
		_npcId = i;
	}

	private String _name;

	public String getName() {
		return _name;
	}

	public void setName(String s) {
		_name = s;
	}

	private String _impl;

	public String getImpl() {
		return _impl;
	}

	public void setImpl(String s) {
		_impl = s;
	}

	private int _level;

	public int getLevel() {
		return _level;
	}

	public void setLevel(int i) {
		_level = i;
	}

	private int _hp;

	public int getHp() {
		return _hp;
	}

	public void setHp(int i) {
		_hp = i;
	}

	private int _mp;

	public int getMp() {
		return _mp;
	}

	public void setMp(int i) {
		_mp = i;
	}

	private int _ac;

	public int getAc() {
		return _ac;
	}

	public void setAc(int i) {
		_ac = i;
	}

	private byte _str;

	public byte getStr() {
		return _str;
	}

	public void setStr(byte i) {
		_str = i;
	}

	private byte _con;

	public byte getCon() {
		return _con;
	}

	public void setCon(byte i) {
		_con = i;
	}

	private byte _dex;

	public byte getDex() {
		return _dex;
	}

	public void setDex(byte i) {
		_dex = i;
	}

	private byte _wis;

	public byte getWis() {
		return _wis;
	}

	public void setWis(byte i) {
		_wis = i;
	}

	private byte _int;

	public byte getInt() {
		return _int;
	}

	public void setInt(byte i) {
		_int = i;
	}

	private int _mr;

	public int getMr() {
		return _mr;
	}

	public void setMr(int i) {
		_mr = i;
	}

	private int _exp;

	public int getExp() {
		return _exp;
	}

	public void setExp(int i) {
		_exp = i;
	}

	private int _lawful;

	public int getLawful() {
		return _lawful;
	}

	public void setLawful(int i) {
		_lawful = i;
	}

	private String _size;

	public String getSize() {
		return _size;
	}

	public void setSize(String s) {
		_size = s;
	}

	private int _weakAttr;

	public int getWeakAttr() {
		return _weakAttr;
	}

	public void setWeakAttr(int i) {
		_weakAttr = i;
	}

	private int _ranged;

	public int getRanged() {
		return _ranged;
	}

	public void setRanged(int i) {
		_ranged = i;
	}

	private boolean _agroSosc;

	public boolean isAgroSosc() {
		return _agroSosc;
	}

	public void setAgroSosc(boolean flag) {
		_agroSosc = flag;
	}

	private boolean _agroCoi;

	public boolean isAgroCoi() {
		return _agroCoi;
	}

	public void setAgroCoi(boolean flag) {
		_agroCoi = flag;
	}

	private boolean _tameable;

	public boolean isTamable() {
		return _tameable;
	}

	public void setTamable(boolean flag) {
		_tameable = flag;
	}

	private int _passiSpeed;

	public int getPassiSpeed() {
		return _passiSpeed;
	}

	public void setPassiSpeed(int i) {
		_passiSpeed = i;
	}

	private int _atkSpeed;

	public int getAtkSpeed() {
		return _atkSpeed;
	}

	public void setAtkSpeed(int i) {
		_atkSpeed = i;
	}

	private boolean _agro;

	public boolean isAgro() {
		return _agro;
	}

	public void setAgro(boolean flag) {
		_agro = flag;
	}

	private int _gfxId;

	public int getGfxId() {
		return _gfxId;
	}

	public void setGfxId(int i) {
		_gfxId = i;
	}

	private String _nameId;

	public String getNameId() {
		return _nameId;
	}

	public void setNameId(String s) {
		_nameId = s;
	}

	private int _undead;

	public int getUndead() {
		return _undead;
	}

	public void setUndead(int i) {
		_undead = i;
	}

	private int _poisonAtk;

	public int getPoisonAtk() {
		return _poisonAtk;
	}

	public void setPoisonAtk(int i) {
		_poisonAtk = i;
	}

	private int _paralysIsAtk;

	public int getParalysIsAtk() {
		return _paralysIsAtk;
	}

	public void setParalysIsAtk(int i) {
		_paralysIsAtk = i;
	}

	private int _family;

	public int getFamily() {
		return _family;
	}

	public void setFamily(int i) {
		_family = i;
	}

	private int _agroFamily;

	public int getAgroFamily() {
		return _agroFamily;
	}

	public void setAgroFamily(int i) {
		_agroFamily = i;
	}

	private int _agroGfxId1;

	public int isAgroGfxId1() {
		return _agroGfxId1;
	}

	public void setAgroGfxId1(int i) {
		_agroGfxId1 = i;
	}

	private int _agroGfxId2;

	public int isAgroGfxId2() {
		return _agroGfxId2;
	}

	public void setAgroGfxId2(int i) {
		_agroGfxId2 = i;
	}

	private boolean _pickUpItem;

	public boolean isPickUpItem() {
		return _pickUpItem;
	}

	public void setPickUpItem(boolean flag) {
		_pickUpItem = flag;
	}

	private int _digestItem;

	public int getDigestItem() {
		return _digestItem;
	}

	public void setDigestItem(int i) {
		_digestItem = i;
	}

	private boolean _braveSpeed;

	public boolean isBraveSpeed() {
		return _braveSpeed;
	}

	public void setBraveSpeed(boolean flag) {
		_braveSpeed = flag;
	}

	private int _hprInterval;

	public int getHprInterval() {
		return _hprInterval;
	}

	public void setHprInterval(int i) {
		_hprInterval = i;
	}

	private int _hpr;

	public int getHpr() {
		return _hpr;
	}

	public void setHpr(int i) {
		_hpr = i;
	}

	private int _mprInterval;

	public int getMprInterval() {
		return _mprInterval;
	}

	public void setMprInterval(int i) {
		_mprInterval = i;
	}

	private int _mpr;

	public int getMpr() {
		return _mpr;
	}

	public void setMpr(int i) {
		_mpr = i;
	}

	private boolean _teleport;

	public boolean isTeleport() {
		return _teleport;
	}

	public void setTeleport(boolean flag) {
		_teleport = flag;
	}

	private int _randomLevel;

	public int getRandomLevel() {
		return _randomLevel;
	}

	public void setRandomLevel(int i) {
		_randomLevel = i;
	}

	private int _randomHp;

	public int getRandomHp() {
		return _randomHp;
	}

	public void setRandomHp(int i) {
		_randomHp = i;
	}

	private int _randomMp;

	public int getRandomMp() {
		return _randomMp;
	}

	public void setRandomMp(int i) {
		_randomMp = i;
	}

	private int _randomAc;

	public int getRandomAc() {
		return _randomAc;
	}

	public void setRandomAc(int i) {
		_randomAc = i;
	}

	private int _randomExp;

	public int getRandomExp() {
		return _randomExp;
	}

	public void setRandomExp(int i) {
		_randomExp = i;
	}

	private int _randomLawful;

	public int getRandomLawful() {
		return _randomLawful;
	}

	public void setRandomLawful(int i) {
		_randomLawful = i;
	}

	private int _damageReduction;

	public int getDamageReduction() {
		return _damageReduction;
	}

	public void setDamageReduction(int i) {
		_damageReduction = i;
	}

	private boolean _hard;

	public boolean isHard() {
		return _hard;
	}

	public void setHard(boolean flag) {
		_hard = flag;
	}

	private boolean _doppel;

	public boolean isDoppel() {
		return _doppel;
	}

	public void setDoppel(boolean flag) {
		_doppel = flag;
	}

	private boolean _tu;

	public boolean enableTU() {
		return _tu;
	}

	public void setEnableTU(boolean i) {
		_tu = i;
	}

	private boolean _erase;

	public boolean enableErase() {
		return _erase;
	}

	public void setEnableErase(boolean i) {
		_erase = i;
	}

	private int bowActId = 0;

	public int getBowActId() {
		return bowActId;
	}

	public void setBowActId(int i) {
		bowActId = i;
	}

	private int _karma;

	public int getKarma() {
		return _karma;
	}

	public void setKarma(int i) {
		_karma = i;
	}

	private int _transformId;

	public int getTransformId() {
		return _transformId;
	}

	public void setTransformId(int transformId) {
		_transformId = transformId;
	}

	private int _transformGfxId;

	public int getTransformGfxId() {
		return _transformGfxId;
	}

	public void setTransformGfxId(int i) {
		_transformGfxId = i;
	}

	private int _altAtkSpeed;

	public int getAltAtkSpeed() {
		return _altAtkSpeed;
	}

	public void setAltAtkSpeed(int altAtkSpeed) {
		_altAtkSpeed = altAtkSpeed;
	}

	private int _atkMagicSpeed;

	public int getAtkMagicSpeed() {
		return _atkMagicSpeed;
	}

	public void setAtkMagicSpeed(int atkMagicSpeed) {
		_atkMagicSpeed = atkMagicSpeed;
	}

	private int _subMagicSpeed;

	public int getSubMagicSpeed() {
		return _subMagicSpeed;
	}

	public void setSubMagicSpeed(int subMagicSpeed) {
		_subMagicSpeed = subMagicSpeed;
	}

	private int _lightSize;

	public int getLightSize() {
		return _lightSize;
	}

	public void setLightSize(int lightSize) {
		_lightSize = lightSize;
	}

	private boolean _amountFixed;

	/**
	 * map_idsテーブルで設定されたモンスター量倍率の影響を受けるかどうかを返す。
	 * 
	 * @return 影響を受けないように設定されている場合はtrueを返す。
	 */
	public boolean isAmountFixed() {
		return _amountFixed;
	}

	public void setAmountFixed(boolean fixed) {
		_amountFixed = fixed;
	}

	private boolean _changeHead;

	public boolean getChangeHead() {
		return _changeHead;
	}

	public void setChangeHead(boolean changeHead) {
		_changeHead = changeHead;
	}

	private boolean _isCantResurrect;

	public boolean isCantResurrect() {
		return _isCantResurrect;
	}

	public void setCantResurrect(boolean isCantResurrect) {
		_isCantResurrect = isCantResurrect;
	}

	private boolean _isEqualityDrop;
	
	public boolean isEqualityDrop() {
		return _isEqualityDrop;
	}
	
	public void setEqualityDrop(boolean isEqualityDrop) {
		_isEqualityDrop = isEqualityDrop;
	}

	// TODO boss_end_log用start
	private boolean _boss;

	public boolean getBoss() {
		return _boss;
	}

	public void setBoss(boolean boss) {
		_boss = boss;
	}

	// TODO boss_end_log用end

	/**
	 * このNPCテンプレートを元にNPCをインスタンス化します。
	 */
	public <T extends L1NpcInstance> T newInstance() {
		String className = "jp.l1j.server.model.instance." + _impl
				+ "Instance";
		return ReflectionUtil.<T, L1Npc> newInstance(className, L1Npc.class,
				this);
	}
}
