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

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class L1Item implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	// add
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// throw (new InternalError(e.getMessage()));
			return null;
		}
	}

	public L1Item() {
	}

	// ■■■■■■ L1EtcItem,L1Weapon,L1Armor に共通する項目 ■■■■■■

	private int _type2; // ● 0=L1EtcItem, 1=L1Weapon, 2=L1Armor

	/**
	 * @return 0 if L1EtcItem, 1 if L1Weapon, 2 if L1Armor
	 */
	public int getType2() {
		return _type2;
	}

	public void setType2(int type) {
		_type2 = type;
	}

	private int _itemId; // ● アイテムＩＤ

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(int itemId) {
		_itemId = itemId;
	}

	private String _name; // ● アイテム名

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	private String _unidentifiedNameId; // ● 未鑑定アイテムのネームＩＤ

	public String getUnidentifiedNameId() {
		return _unidentifiedNameId;
	}

	public void setUnidentifiedNameId(String unidentifiedNameId) {
		_unidentifiedNameId = unidentifiedNameId;
	}

	private String _identifiedNameId; // ● 鑑定済みアイテムのネームＩＤ

	public String getIdentifiedNameId() {
		return _identifiedNameId;
	}

	public void setIdentifiedNameId(String identifiedNameId) {
		_identifiedNameId = identifiedNameId;
	}

	private int _type; // ● 詳細なタイプ

	/**
	 * アイテムの種類を返す。<br>
	 * 
	 * @return <p>
	 *         [etcitem]<br>
	 *         0:arrow, 1:wand, 2:light, 3:gem, 4:totem, 5:firecracker,
	 *         6:potion, 7:food, 8:scroll, 9:questitem, 10:spellbook,
	 *         11:petitem, 12:other, 13:material, 14:event, 15:sting,
	 *		   16:treasure_box, 17:magic_doll, 18:spellscroll, 19:spellwand,
	 *         20:spellicon, 21:protect_scroll
	 *         </p>
	 *         <p>
	 *         [weapon]<br>
	 *         1:sword, 2:twohandsword, 3:dagger, 4:bow, 5:arrow, 6:spear,
	 *		   7:blunt, 8:staff, 9:claw, 10:dualsword, 11:gauntlet, 12:sting,
	 *		   13:chainsword, 14:kiringku
	 *         </p>
	 *         <p>
	 *         [armor]<br>
	 *         1:helm, 2:t_shirts, 3:armor, 4:cloak, 5:glove, 6:boots, 7:shield,
	 *         8:guarder, 10:amulet, 11:ring, 12:earring, 13:belt, 
	 *		   14:pattern_back, 15:pattern_left, 16:pattern_right,
	 *		   17:talisman_left, 18:talisman_right
	 */
	public int getType() {
		return _type;
	}

	public void setType(int type) {
		_type = type;
	}

	private int _type1; // ● タイプ

	/**
	 * アイテムの種類を返す。<br>
	 * 
	 * @return <p>
	 *         [weapon]<br>
	 *         4:sword, 50:twohandsword, 46:dagger, 20:bow, 66:arrow, 24:spear,
	 *		   11:blunt, 40:staff, 58:claw, 54:dualsword, 62:gauntlet, 2922:sting,
	 *		   24:chainsword, 58:kiringku
	 *         </p>
	 */
	public int getType1() {
		return _type1;
	}

	public void setType1(int type1) {
		_type1 = type1;
	}

	private int _material; // ● 素材

	/**
	 * アイテムの素材を返す
	 * 
	 * @return 0:none 1:液体 2:web 3:植物性 4:動物性 5:紙 6:布 7:皮 8:木 9:骨 10:竜の鱗 11:鉄
	 *         12:鋼鉄 13:銅 14:銀 15:金 16:プラチナ 17:ミスリル 18:ブラックミスリル 19:ガラス 20:宝石
	 *         21:鉱物 22:オリハルコン
	 */
	public int getMaterial() {
		return _material;
	}

	public void setMaterial(int material) {
		_material = material;
	}

	private int _grade; // グレード

	/**
	 * グレードを返す
	 * 
	 * @return 0:上級 1:中級 2:下級 3:特
	 */
	public int getGrade() {
		return _grade;
	}

	public void setGrade(int grade) {
		_grade = grade;
	}

	private int _weight; // ● 重量

	public int getWeight() {
		return _weight;
	}

	public void setWeight(int weight) {
		_weight = weight;
	}

	private int _gfxId; // ● インベントリ内のグラフィックＩＤ

	public int getGfxId() {
		return _gfxId;
	}

	public void setGfxId(int gfxId) {
		_gfxId = gfxId;
	}

	private int _groundGfxId; // ● 地面に置いた時のグラフィックＩＤ

	public int getGroundGfxId() {
		return _groundGfxId;
	}

	public void setGroundGfxId(int groundGfxId) {
		_groundGfxId = groundGfxId;
	}

	private int _minLevel; // ● 使用、装備可能最小ＬＶ

	private int _itemDescId;

	/**
	 * 鑑定時に表示されるItemDesc.tblのメッセージIDを返す。
	 */
	public int getItemDescId() {
		return _itemDescId;
	}

	public void setItemDescId(int descId) {
		_itemDescId = descId;
	}

	public int getMinLevel() {
		return _minLevel;
	}

	public void setMinLevel(int level) {
		_minLevel = level;
	}

	private int _maxLevel; // ● 使用、装備可能最大ＬＶ

	public int getMaxLevel() {
		return _maxLevel;
	}

	public void setMaxLevel(int maxlvl) {
		_maxLevel = maxlvl;
	}

	private int _bless; // ● 祝福状態

	public int getBless() {
		return _bless;
	}

	public void setBless(int i) {
		_bless = i;
	}

	private boolean _tradable; // ● トレード可／不可

	public boolean isTradable() {
		return _tradable;
	}

	public void setTradable(boolean flag) {
		_tradable = flag;
	}

	private boolean _deletable; // ● 削除可／不可

	public boolean isDeletable() {
		return _deletable;
	}

	public void setDeletable(boolean flag) {
		_deletable = flag;
	}

	private boolean _saveAtOnce;

	/**
	 * アイテムの個数が変化した際にすぐにDBに書き込むべきかを返す。
	 */
	public boolean isToBeSavedAtOnce() {
		return _saveAtOnce;
	}

	public void setToBeSavedAtOnce(boolean flag) {
		_saveAtOnce = flag;
	}

	// ■■■■■■ L1EtcItem,L1Weapon に共通する項目 ■■■■■■

	private int _dmgSmall = 0; // ● 最小ダメージ

	public int getDmgSmall() {
		return _dmgSmall;
	}

	public void setDmgSmall(int dmgSmall) {
		_dmgSmall = dmgSmall;
	}

	private int _dmgLarge = 0; // ● 最大ダメージ

	public int getDmgLarge() {
		return _dmgLarge;
	}

	public void setDmgLarge(int dmgLarge) {
		_dmgLarge = dmgLarge;
	}

	// ■■■■■■ L1EtcItem,L1Armor に共通する項目 ■■■■■■

	// ■■■■■■ L1Weapon,L1Armor に共通する項目 ■■■■■■

	private int _safeEnchant = 0; // ● ＯＥ安全圏

	public int getSafeEnchant() {
		return _safeEnchant;
	}

	public void setSafeEnchant(int safeEnchant) {
		_safeEnchant = safeEnchant;
	}

	private boolean _useRoyal = false; // ● ロイヤルクラスが装備できるか

	public boolean isUseRoyal() {
		return _useRoyal;
	}

	public void setUseRoyal(boolean flag) {
		_useRoyal = flag;
	}

	private boolean _useKnight = false; // ● ナイトクラスが装備できるか

	public boolean isUseKnight() {
		return _useKnight;
	}

	public void setUseKnight(boolean flag) {
		_useKnight = flag;
	}

	private boolean _useElf = false; // ● エルフクラスが装備できるか

	public boolean isUseElf() {
		return _useElf;
	}

	public void setUseElf(boolean flag) {
		_useElf = flag;
	}

	private boolean _useWizard = false; // ● メイジクラスが装備できるか

	public boolean isUseWizard() {
		return _useWizard;
	}

	public void setUseWizard(boolean flag) {
		_useWizard = flag;
	}

	private boolean _useDarkelf = false; // ● ダークエルフクラスが装備できるか

	public boolean isUseDarkelf() {
		return _useDarkelf;
	}

	public void setUseDarkelf(boolean flag) {
		_useDarkelf = flag;
	}

	private boolean _useDragonknight = false; // ● ドラゴンナイト裝備できるか

	public boolean isUseDragonknight() {
		return _useDragonknight;
	}

	public void setUseDragonknight(boolean flag) {
		_useDragonknight = flag;
	}

	private boolean _useIllusionist = false; // ● イリュージョニスト裝備できるか

	public boolean isUseIllusionist() {
		return _useIllusionist;
	}

	public void setUseIllusionist(boolean flag) {
		_useIllusionist = flag;
	}

	private byte _str = 0; // ● ＳＴＲ補正

	public byte getStr() {
		return _str;
	}

	public void setStr(byte addStr) {
		_str = addStr;
	}

	private byte _dex = 0; // ● ＤＥＸ補正

	public byte getDex() {
		return _dex;
	}

	public void setDex(byte addDex) {
		_dex = addDex;
	}

	private byte _con = 0; // ● ＣＯＮ補正

	public byte getCon() {
		return _con;
	}

	public void setCon(byte addCon) {
		_con = addCon;
	}

	private byte _int = 0; // ● ＩＮＴ補正

	public byte getInt() {
		return _int;
	}

	public void setInt(byte addInt) {
		_int = addInt;
	}

	private byte _wis = 0; // ● ＷＩＳ補正

	public byte getWis() {
		return _wis;
	}

	public void setWis(byte addWis) {
		_wis = addWis;
	}

	private byte _cha = 0; // ● ＣＨＡ補正

	public byte getCha() {
		return _cha;
	}

	public void setCha(byte addCha) {
		_cha = addCha;
	}

	private int _hp = 0; // ● ＨＰ補正

	public int getHp() {
		return _hp;
	}

	public void setHp(int addHp) {
		_hp = addHp;
	}

	private int _mp = 0; // ● ＭＰ補正

	public int getMp() {
		return _mp;
	}

	public void setMp(int addMp) {
		_mp = addMp;
	}

	private int _hpr = 0; // ● ＨＰＲ補正

	public int getHpr() {
		return _hpr;
	}

	public void setHpr(int addHpr) {
		_hpr = addHpr;
	}

	private int _mpr = 0; // ● ＭＰＲ補正

	public int getMpr() {
		return _mpr;
	}

	public void setMpr(int addMpr) {
		_mpr = addMpr;
	}

	private int _sp = 0; // ● ＳＰ補正

	public int getSp() {
		return _sp;
	}

	public void setSp(int addSp) {
		_sp = addSp;
	}

	private int _mr = 0; // ● ＭＲ

	public int getMr() {
		return _mr;
	}

	public void setMr(int addMr) {
		_mr = addMr;
	}

	private boolean _isHaste = false; // ● ヘイスト効果の有無

	public boolean isHaste() {
		return _isHaste;
	}

	public void setIsHaste(boolean flag) {
		_isHaste = flag;
	}

	private int _chargeTime = 0; // ● 使用可能な残り時間(秒)

	public int getChargeTime() {
		return _chargeTime;
	}

	public void setChargeTime(int i) {
		_chargeTime = i;
	}

	private int _expirationTime = 0; // ● 使用可能な有効期限(秒)
	
	public int getExpirationTime() {
		return _expirationTime;
	}
	
	public void setExpirationTime(String s) {
		int _day = getTimeParse(s, "d");
		int _hour = getTimeParse(s, "h");
		int _minute = getTimeParse(s, "m");
		int _time = ((_day * 60 * 60 * 24) + (_hour * 60 * 60)
				+ (_minute * 60));
		_expirationTime = _time;
	}

	private static int getTimeParse(String target, String search) {
		if (target == null) {
			return 0;
		}
		int n = 0;
		Matcher matcher = Pattern.compile("\\d+" + search).matcher(target);
		if (matcher.find()) {
			String match = matcher.group();
			n = Integer.parseInt(match.replace(search, ""));
		}
		return n;
	}
	
	private int _useType;

	/**
	 * 使用したときのリアクションを決定するタイプを返す。
	 */
	public int getUseType() {
		return _useType;
	}

	public void setUseType(int useType) {
		_useType = useType;
	}

	private int _foodVolume;

	/**
	 * 肉などのアイテムに設定されている満腹度を返す。
	 */
	public int getFoodVolume() {
		return _foodVolume;
	}

	public void setFoodVolume(int volume) {
		_foodVolume = volume;
	}

	/**
	 * ランプなどのアイテムに設定されている明るさを返す。
	 */
	public int getLightRange() {
		if (_itemId == 40001) { // ランプ
			return 11;
		} else if (_itemId == 40002) { // ランタン
			return 13;
		} else if (_itemId == 40004) { // パンプキンランタン
			return 13;
		} else if (_itemId == 40005) { // キャンドル
			return 8;
		} else {
			return 0;
		}
	}

	/**
	 * 魔法触媒の種類を返す。
	 */
	public int getMagicCatalystType() {
		int type = 0;
		
		switch (getItemId()) {
		case 40318: // 魔力の石
			type = 166; // 材料によるアイコンパッケージ
			break;
		case 40319: // 精霊の玉
			type = 569;
			break;
		case 40321: // ダーク ストーン
			type = 837;
			break;
		case 49158: // ユグドラの実
			type = 3674;
			break;
		case 49157: // 刻印のボーンピース
			type = 3605;
			break;
		case 49156: // 属性石
			type = 3606;
			break;
		}
		
		return type;
	}
	
	// ■■■■■■ L1EtcItem でオーバーライドする項目 ■■■■■■
	public boolean isStackable() {
		return false;
	}

	public int getLocX() {
		return 0;
	}

	public int getLocY() {
		return 0;
	}

	public short getMapId() {
		return 0;
	}

	public int getDelayId() {
		return 0;
	}

	public int getDelayTime() {
		return 0;
	}

	public int getMaxChargeCount() {
		return 0;
	}

	public boolean isSealable() {
		return false;
	}

	// ■■■■■■ L1Weapon でオーバーライドする項目 ■■■■■■
	public int getRange() {
		return 0;
	}

	public int getHitModifier() {
		return 0;
	}

	public int getDmgModifier() {
		return 0;
	}

	public int getDoubleDmgChance() {
		return 0;
	}

	public int getMagicDmgModifier() {
		return 0;
	}

	public boolean getCanbeDmg() {
		return false;
	}

	public boolean isTwohanded() {
		return false;
	}

	public int getWeaknessExposure() {
		return 0;
	}

	// ■■■■■■ L1Armor でオーバーライドする項目 ■■■■■■
	public int getAc() {
		return 0;
	}

	//public int getGrade() {
		//return 0;
	//}

	public int getDamageReduction() {
		return 0;
	}

	public int getWeightReduction() {
		return 0;
	}

	public int getHitModifierByArmor() {
		return 0;
	}

	public int getDmgModifierByArmor() {
		return 0;
	}

	public int getBowHitModifierByArmor() {
		return 0;
	}

	public int getBowDmgModifierByArmor() {
		return 0;
	}

	public int getDefenseWater() {
		return 0;
	}

	public int getDefenseFire() {
		return 0;
	}

	public int getDefenseEarth() {
		return 0;
	}

	public int getDefenseWind() {
		return 0;
	}

	public int getResistStun() {
		return 0;
	}

	public int getResistStone() {
		return 0;
	}

	public int getResistSleep() {
		return 0;
	}

	public int getResistFreeze() {
		return 0;
	}

	public int getResistHold() {
		return 0;
	}

	public int getResistBlind() {
		return 0;
	}

	public int getExpBonus() {
		return 0;
	}

	public int getPotionRecoveryRate() {
		return 0;
	}
}
