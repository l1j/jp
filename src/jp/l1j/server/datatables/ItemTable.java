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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.templates.L1Armor;
import jp.l1j.server.templates.L1EtcItem;
import jp.l1j.server.templates.L1Item;
import jp.l1j.server.templates.L1Weapon;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class ItemTable {
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(ItemTable.class.getName());

	private static final Map<String, Integer> _armorTypes = new HashMap<String, Integer>();

	private static final Map<String, Integer> _weaponTypes = new HashMap<String, Integer>();

	private static final Map<String, Integer> _weaponId = new HashMap<String, Integer>();

	private static final Map<String, Integer> _materialTypes = new HashMap<String, Integer>();

	private static final Map<String, Integer> _etcItemTypes = new HashMap<String, Integer>();

	private static final Map<String, Integer> _useTypes = new HashMap<String, Integer>();

	private static ItemTable _instance;
	
	private static Map<Integer, L1Item> _allTemplates;

	private static Map<Integer, L1EtcItem> _allEtcItems;

	private static Map<Integer, L1Armor> _allArmors;

	private static Map<Integer, L1Weapon> _allWeapons;
	
	private static Map<String, Integer> _allNames;
	
	private static Map<String, Integer> _allWithoutSpaceNames;

	static {
		_etcItemTypes.put("arrow", new Integer(0));
		_etcItemTypes.put("wand", new Integer(1));
		_etcItemTypes.put("light", new Integer(2));
		_etcItemTypes.put("gem", new Integer(3));
		_etcItemTypes.put("totem", new Integer(4));
		_etcItemTypes.put("firecracker", new Integer(5));
		_etcItemTypes.put("potion", new Integer(6));
		_etcItemTypes.put("food", new Integer(7));
		_etcItemTypes.put("scroll", new Integer(8));
		_etcItemTypes.put("questitem", new Integer(9));
		_etcItemTypes.put("spellbook", new Integer(10));
		_etcItemTypes.put("petitem", new Integer(11));
		_etcItemTypes.put("other", new Integer(12));
		_etcItemTypes.put("material", new Integer(13));
		_etcItemTypes.put("event", new Integer(14));
		_etcItemTypes.put("sting", new Integer(15));
		_etcItemTypes.put("treasure_box", new Integer(16));
		_etcItemTypes.put("magic_doll", new Integer(17));
		_etcItemTypes.put("spellscroll", new Integer(18));
		_etcItemTypes.put("spellwand", new Integer(19));
		_etcItemTypes.put("spellicon", new Integer(20));
		_etcItemTypes.put("protect_scroll", new Integer(21));
		_etcItemTypes.put("unique_scroll", new Integer(22));

		_useTypes.put("none", new Integer(-1)); // 使用不可能
		_useTypes.put("normal", new Integer(0));
		_useTypes.put("weapon", new Integer(1));
		_useTypes.put("armor", new Integer(2));
		// _useTypes.put("wand1", new Integer(3));
		// _useTypes.put("wand", new Integer(4));
		// ワンドを振るアクションをとる(C_RequestExtraCommandが送られる)
		_useTypes.put("spell_long", new Integer(5)); // 地面 / オブジェクト選択(遠距離)
		_useTypes.put("ntele", new Integer(6));
		_useTypes.put("identify", new Integer(7));
		_useTypes.put("res", new Integer(8));
		_useTypes.put("choice", new Integer(14));
		_useTypes.put("instrument", new Integer(15));
		_useTypes.put("sosc", new Integer(16));
		_useTypes.put("spell_short", new Integer(17)); // 地面 / オブジェクト選択(近距離)
		_useTypes.put("t_shirt", new Integer(18));
		_useTypes.put("cloak", new Integer(19));
		_useTypes.put("glove", new Integer(20));
		_useTypes.put("boots", new Integer(21));
		_useTypes.put("helm", new Integer(22));
		_useTypes.put("ring", new Integer(23));
		_useTypes.put("amulet", new Integer(24));
		_useTypes.put("shield", new Integer(25));
		_useTypes.put("guarder", new Integer(25));
		_useTypes.put("dai", new Integer(26));
		_useTypes.put("zel", new Integer(27));
		_useTypes.put("blank", new Integer(28));
		_useTypes.put("btele", new Integer(29));
		_useTypes.put("spell_buff", new Integer(30)); // オブジェクト選択(遠距離)
		_useTypes.put("belt", new Integer(37));
		_useTypes.put("spell_point", new Integer(39)); // 地面選択
		_useTypes.put("earring", new Integer(40));
		_useTypes.put("fishing_rod", new Integer(42));
		_useTypes.put("pattern_back", new Integer(45));
		_useTypes.put("pattern_left", new Integer(44));
		_useTypes.put("pattern_right", new Integer(43));
		_useTypes.put("talisman_left", new Integer(47));
		_useTypes.put("talisman_right", new Integer(48));
		_useTypes.put("elixir", new Integer(58));
		_useTypes.put("healing", new Integer(59));
		_useTypes.put("cure", new Integer(60));
		_useTypes.put("haste", new Integer(61));
		_useTypes.put("brave", new Integer(62));
		_useTypes.put("third_speed", new Integer(63));
		_useTypes.put("magic_eye", new Integer(64));
		_useTypes.put("magic_healing", new Integer(65));
		_useTypes.put("bless_eva", new Integer(66));
		_useTypes.put("magic_regeneration", new Integer(67));
		_useTypes.put("wisdom", new Integer(68));
		_useTypes.put("flora", new Integer(69));
		_useTypes.put("poly", new Integer(70));
		_useTypes.put("npc_talk", new Integer(71));
		_useTypes.put("roulette", new Integer(72));
		_useTypes.put("teleport", new Integer(73));
		_useTypes.put("spawn", new Integer(74));
		_useTypes.put("furniture", new Integer(75));
		_useTypes.put("material", new Integer(77));
		_useTypes.put("extra", new Integer(78));

		_armorTypes.put("none", new Integer(0));
		_armorTypes.put("helm", new Integer(1));
		_armorTypes.put("armor", new Integer(2));
		_armorTypes.put("t_shirt", new Integer(3));
		_armorTypes.put("cloak", new Integer(4));
		_armorTypes.put("glove", new Integer(5));
		_armorTypes.put("boots", new Integer(6));
		_armorTypes.put("shield", new Integer(7));
		_armorTypes.put("guarder", new Integer(8));
		_armorTypes.put("amulet", new Integer(10));
		_armorTypes.put("ring", new Integer(11));
		_armorTypes.put("earring", new Integer(12));
		_armorTypes.put("belt", new Integer(13));
		_armorTypes.put("pattern_back", new Integer(14));
		_armorTypes.put("pattern_left", new Integer(15));
		_armorTypes.put("pattern_right", new Integer(16));
		_armorTypes.put("talisman_left", new Integer(17));
		_armorTypes.put("talisman_right", new Integer(18));

		_weaponTypes.put("sword", new Integer(1));
		_weaponTypes.put("twohandsword", new Integer(2));
		_weaponTypes.put("dagger", new Integer(3));
		_weaponTypes.put("bow", new Integer(4));
		_weaponTypes.put("arrow", new Integer(5));
		_weaponTypes.put("spear", new Integer(6));
		_weaponTypes.put("blunt", new Integer(7));
		_weaponTypes.put("staff", new Integer(8));
		_weaponTypes.put("claw", new Integer(9));
		_weaponTypes.put("dualsword", new Integer(10));
		_weaponTypes.put("gauntlet", new Integer(11));
		_weaponTypes.put("sting", new Integer(12));
		_weaponTypes.put("chainsword", new Integer(13));
		_weaponTypes.put("kiringku", new Integer(14));

		_weaponId.put("sword", new Integer(4));
		_weaponId.put("twohandsword", new Integer(50));
		_weaponId.put("dagger", new Integer(46));
		_weaponId.put("bow", new Integer(20));
		_weaponId.put("arrow", new Integer(66));
		_weaponId.put("spear", new Integer(24));
		_weaponId.put("blunt", new Integer(11));
		_weaponId.put("staff", new Integer(40));
		_weaponId.put("claw", new Integer(58));
		_weaponId.put("dualsword", new Integer(54));
		_weaponId.put("gauntlet", new Integer(62));
		_weaponId.put("sting", new Integer(2922));
		_weaponId.put("chainsword", new Integer(24));
		_weaponId.put("kiringku", new Integer(58));

		_materialTypes.put("none", new Integer(0));
		_materialTypes.put("liquid", new Integer(1));
		_materialTypes.put("web", new Integer(2));
		_materialTypes.put("vegetation", new Integer(3));
		_materialTypes.put("animalmatter", new Integer(4));
		_materialTypes.put("paper", new Integer(5));
		_materialTypes.put("cloth", new Integer(6));
		_materialTypes.put("leather", new Integer(7));
		_materialTypes.put("wood", new Integer(8));
		_materialTypes.put("bone", new Integer(9));
		_materialTypes.put("dragonscale", new Integer(10));
		_materialTypes.put("iron", new Integer(11));
		_materialTypes.put("steel", new Integer(12));
		_materialTypes.put("copper", new Integer(13));
		_materialTypes.put("silver", new Integer(14));
		_materialTypes.put("gold", new Integer(15));
		_materialTypes.put("platinum", new Integer(16));
		_materialTypes.put("mithril", new Integer(17));
		_materialTypes.put("blackmithril", new Integer(18));
		_materialTypes.put("glass", new Integer(19));
		_materialTypes.put("gemstone", new Integer(20));
		_materialTypes.put("mineral", new Integer(21));
		_materialTypes.put("orichalcum", new Integer(22));
	}

	public static ItemTable getInstance() {
		if (_instance == null) {
			_instance = new ItemTable();
		}
		return _instance;
	}

	private ItemTable() {
		load();
	}

	private Map<Integer, L1EtcItem> allEtcItem() {
		Map<Integer, L1EtcItem> result = new HashMap<Integer, L1EtcItem>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1EtcItem item = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from etc_items");
			rs = pstm.executeQuery();
			while (rs.next()) {
				item = new L1EtcItem();
				item.setItemId(rs.getInt("id"));
				item.setName(rs.getString("name"));
				item.setUnidentifiedNameId(rs.getString("unidentified_name_id"));
				item.setIdentifiedNameId(rs.getString("identified_name_id"));
				item.setType((_etcItemTypes.get(rs.getString("item_type"))).intValue());
				item.setUseType(_useTypes.get(rs.getString("use_type")).intValue());
				// item.setType1(0); // 使わない
				item.setType2(0);
				item.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());
				item.setWeight(rs.getInt("weight"));
				item.setGfxId(rs.getInt("inv_gfx_id"));
				item.setGroundGfxId(rs.getInt("grd_gfx_id"));
				item.setItemDescId(rs.getInt("item_desc_id"));
				item.setMinLevel(rs.getInt("min_level"));
				item.setMaxLevel(rs.getInt("max_level"));
				item.setBless(rs.getInt("bless"));
				item.setTradable(rs.getInt("tradable") == 1 ? true : false);
				item.setDeletable(rs.getInt("deletable") == 1 ? true : false);
				item.setSealable(rs.getInt("sealable") == 1 ? true : false);
				item.setDmgSmall(rs.getInt("dmg_small"));
				item.setDmgLarge(rs.getInt("dmg_large"));
				item.setStackable(rs.getInt("stackable") == 1 ? true : false);
				item.setMaxChargeCount(rs.getInt("max_charge_count"));
				item.setLocX(rs.getInt("loc_x"));
				item.setLocY(rs.getInt("loc_y"));
				item.setMapId(rs.getShort("map_id"));
				item.setDelayId(rs.getInt("delay_id"));
				item.setDelayTime(rs.getInt("delay_time"));
				item.setDelayEffect(rs.getInt("delay_effect"));
				item.setFoodVolume(rs.getInt("food_volume"));
				item.setToBeSavedAtOnce((rs.getInt("save_at_once") == 1) ? true : false);
				item.setChargeTime(rs.getInt("charge_time"));
				item.setExpirationTime(rs.getString("expiration_time"));
				result.put(new Integer(item.getItemId()), item);
			}
		} catch (NullPointerException e) {
			_log.log(Level.SEVERE, String.format(I18N_LOAD_ITEM_FAILED, item.getName(), item.getItemId()));
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return result;
	}

	private Map<Integer, L1Weapon> allWeapon() {
		Map<Integer, L1Weapon> result = new HashMap<Integer, L1Weapon>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1Weapon weapon = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from weapons");
			rs = pstm.executeQuery();
			while (rs.next()) {
				weapon = new L1Weapon();
				weapon.setItemId(rs.getInt("id"));
				weapon.setName(rs.getString("name"));
				weapon.setUnidentifiedNameId(rs.getString("unidentified_name_id"));
				weapon.setIdentifiedNameId(rs.getString("identified_name_id"));
				weapon.setType((_weaponTypes.get(rs.getString("type"))).intValue());
				weapon.setType1((_weaponId.get(rs.getString("type"))).intValue());
				weapon.setType2(1);
				weapon.setUseType(1);
				weapon.setIsTwohanded(rs.getBoolean("is_twohanded"));
				weapon.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());
				weapon.setWeight(rs.getInt("weight"));
				weapon.setGfxId(rs.getInt("inv_gfx_id"));
				weapon.setGroundGfxId(rs.getInt("grd_gfx_id"));
				weapon.setItemDescId(rs.getInt("item_desc_id"));
				weapon.setDmgSmall(rs.getInt("dmg_small"));
				weapon.setDmgLarge(rs.getInt("dmg_large"));
				weapon.setRange(rs.getInt("range"));
				weapon.setSafeEnchant(rs.getInt("safe_enchant"));
				weapon.setUseRoyal(rs.getInt("use_royal") == 0 ? false : true);
				weapon.setUseKnight(rs.getInt("use_knight") == 0 ? false : true);
				weapon.setUseElf(rs.getInt("use_elf") == 0 ? false : true);
				weapon.setUseWizard(rs.getInt("use_wizard") == 0 ? false : true);
				weapon.setUseDarkelf(rs.getInt("use_darkelf") == 0 ? false : true);
				weapon.setUseDragonknight(rs.getInt("use_dragonknight") == 0 ? false : true);
				weapon.setUseIllusionist(rs.getInt("use_illusionist") == 0 ? false : true);
				weapon.setHitModifier(rs.getInt("hit_modifier"));
				weapon.setDmgModifier(rs.getInt("dmg_modifier"));
				weapon.setStr(rs.getByte("str"));
				weapon.setDex(rs.getByte("dex"));
				weapon.setCon(rs.getByte("con"));
				weapon.setInt(rs.getByte("int"));
				weapon.setWis(rs.getByte("wis"));
				weapon.setCha(rs.getByte("cha"));
				weapon.setHp(rs.getInt("hp"));
				weapon.setMp(rs.getInt("mp"));
				weapon.setHpr(rs.getInt("hpr"));
				weapon.setMpr(rs.getInt("mpr"));
				weapon.setSp(rs.getInt("sp"));
				weapon.setMr(rs.getInt("mr"));
				weapon.setDoubleDmgChance(rs.getInt("double_dmg_chance"));
				weapon.setWeaknessExposure(rs.getInt("weakness_exposure"));
				weapon.setMagicDmgModifier(rs.getInt("magic_dmg_modifier"));
				weapon.setCanbeDmg(rs.getBoolean("can_be_dmg"));
				weapon.setMinLevel(rs.getInt("min_level"));
				weapon.setMaxLevel(rs.getInt("max_level"));
				weapon.setBless(rs.getInt("bless"));
				weapon.setTradable(rs.getInt("tradable") == 1 ? true : false);
				weapon.setDeletable(rs.getInt("deletable") == 1 ? true : false);
				weapon.setIsHaste(rs.getBoolean("is_haste"));
				weapon.setChargeTime(rs.getInt("charge_time"));
				weapon.setExpirationTime(rs.getString("expiration_time"));
				result.put(new Integer(weapon.getItemId()), weapon);
			}
		} catch (NullPointerException e) {
			_log.log(Level.SEVERE, String.format(I18N_LOAD_ITEM_FAILED, weapon.getName(), weapon.getItemId()));
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return result;
	}

	private Map<Integer, L1Armor> allArmor() {
		Map<Integer, L1Armor> result = new HashMap<Integer, L1Armor>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1Armor armor = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from armors");
			rs = pstm.executeQuery();
			while (rs.next()) {
				armor = new L1Armor();
				armor.setItemId(rs.getInt("id"));
				armor.setName(rs.getString("name"));
				armor.setUnidentifiedNameId(rs.getString("unidentified_name_id"));
				armor.setIdentifiedNameId(rs.getString("identified_name_id"));
				armor.setType((_armorTypes.get(rs.getString("type"))).intValue());
				// armor.setType1((_armorId
				// .get(rs.getString("armor_type"))).intValue()); // 使わない
				armor.setType2(2);
				armor.setUseType((_useTypes.get(rs.getString("type"))).intValue());
				armor.setMaterial((_materialTypes.get(rs.getString("material"))).intValue());
				armor.setGrade(rs.getInt("grade"));
				armor.setWeight(rs.getInt("weight"));
				armor.setGfxId(rs.getInt("inv_gfx_id"));
				armor.setGroundGfxId(rs.getInt("grd_gfx_id"));
				armor.setItemDescId(rs.getInt("item_desc_id"));
				armor.setSafeEnchant(rs.getInt("safe_enchant"));
				armor.setUseRoyal(rs.getInt("use_royal") == 0 ? false : true);
				armor.setUseKnight(rs.getInt("use_knight") == 0 ? false : true);
				armor.setUseElf(rs.getInt("use_elf") == 0 ? false : true);
				armor.setUseWizard(rs.getInt("use_wizard") == 0 ? false : true);
				armor.setUseDarkelf(rs.getInt("use_darkelf") == 0 ? false : true);
				armor.setUseDragonknight(rs.getInt("use_dragonknight") == 0 ? false : true);
				armor.setUseIllusionist(rs.getInt("use_illusionist") == 0 ? false : true);
				armor.setMinLevel(rs.getInt("min_level"));
				armor.setMaxLevel(rs.getInt("max_level"));
				armor.setAc(rs.getInt("ac"));
				armor.setStr(rs.getByte("str"));
				armor.setCon(rs.getByte("con"));
				armor.setDex(rs.getByte("dex"));
				armor.setWis(rs.getByte("wis"));
				armor.setCha(rs.getByte("cha"));
				armor.setInt(rs.getByte("int"));
				armor.setHp(rs.getInt("hp"));
				armor.setHpr(rs.getInt("hpr"));
				armor.setMp(rs.getInt("mp"));
				armor.setMpr(rs.getInt("mpr"));
				armor.setSp(rs.getInt("sp"));
				armor.setMr(rs.getInt("mr"));
				armor.setDamageReduction(rs.getInt("damage_reduction"));
				armor.setWeightReduction(rs.getInt("weight_reduction"));
				armor.setHitModifierByArmor(rs.getInt("hit_modifier"));
				armor.setDmgModifierByArmor(rs.getInt("dmg_modifier"));
				armor.setBowHitModifierByArmor(rs.getInt("bow_hit_modifier"));
				armor.setBowDmgModifierByArmor(rs.getInt("bow_dmg_modifier"));
				armor.setDefenseEarth(rs.getInt("defense_earth"));
				armor.setDefenseWater(rs.getInt("defense_water"));
				armor.setDefenseWind(rs.getInt("defense_wind"));
				armor.setDefenseFire(rs.getInt("defense_fire"));
				armor.setResistStun(rs.getInt("resist_stun"));
				armor.setResistStone(rs.getInt("resist_stone"));
				armor.setResistSleep(rs.getInt("resist_sleep"));
				armor.setResistFreeze(rs.getInt("resist_freeze"));
				armor.setResistHold(rs.getInt("resist_hold"));
				armor.setResistBlind(rs.getInt("resist_blind"));
				armor.setBless(rs.getInt("bless"));
				armor.setTradable(rs.getInt("tradable") == 1 ? true : false);
				armor.setDeletable(rs.getInt("deletable") == 1 ? true : false);
				armor.setChargeTime(rs.getInt("charge_time"));
				armor.setExpirationTime(rs.getString("expiration_time"));
				armor.setIsHaste(rs.getInt("is_haste") == 0 ? false : true);
				armor.setExpBonus(rs.getInt("exp_bonus"));
				armor.setPotionRecoveryRate(rs.getInt("potion_recovery_rate"));
				result.put(new Integer(armor.getItemId()), armor);
			}
		} catch (NullPointerException e) {
			_log.log(Level.SEVERE, String.format(I18N_LOAD_ITEM_FAILED, armor.getName(), armor.getItemId()));
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return result;
	}

	private void load() {
		PerformanceTimer timer = new PerformanceTimer();
		_allEtcItems = allEtcItem();
		_allWeapons = allWeapon();
		_allArmors = allArmor();
		_allTemplates = new HashMap<Integer, L1Item>();
		_allNames = new HashMap<String, Integer>();
		_allWithoutSpaceNames = new HashMap<String, Integer>();
		buildFastLookupTable(_allTemplates, _allEtcItems, _allWeapons, _allArmors,
				_allNames, _allWithoutSpaceNames);
		System.out.println("loading items...OK! " + timer.elapsedTimeMillis() + "ms");
	}
	
	public void reload() {
		PerformanceTimer timer = new PerformanceTimer();
		Map<Integer, L1EtcItem> allEtcItems = allEtcItem();
		Map<Integer, L1Weapon> allWeapons = allWeapon();
		Map<Integer, L1Armor> allArmors = allArmor();
		Map<Integer, L1Item> allTemplates = new HashMap<Integer, L1Item>();
		Map<String, Integer> allNames = new HashMap<String, Integer>();
		Map<String, Integer> allWithoutSpaceNames = new HashMap<String, Integer>();
		buildFastLookupTable(allTemplates, allEtcItems, allWeapons, allArmors,
				allNames, allWithoutSpaceNames);
		_allEtcItems = allEtcItems;
		_allWeapons = allWeapons;
		_allArmors = allArmors;
		_allTemplates = allTemplates;
		_allNames = allNames;
		_allWithoutSpaceNames = allWithoutSpaceNames;
		System.out.println("loading items...OK! " + timer.elapsedTimeMillis() + "ms");
	}
	
	private void buildFastLookupTable(Map<Integer, L1Item> allTemplates,
			Map<Integer, L1EtcItem> allEtcItems,
			Map<Integer, L1Weapon> allWeapons,
			Map<Integer, L1Armor> allArmors,
			Map<String, Integer> allNames,
			Map<String, Integer> allWithoutSpaceNames) {
		for (Iterator<Integer> iter = allEtcItems.keySet().iterator(); iter.hasNext();) {
			Integer id = iter.next();
			L1EtcItem item = allEtcItems.get(id);
			allTemplates.put(new Integer(id.intValue()), item);
			allNames.put(item.getName(), new Integer(id.intValue()));
			allWithoutSpaceNames.put(item.getName().replace(" ", ""), new Integer(id.intValue()));
		}
		for (Iterator<Integer> iter = allWeapons.keySet().iterator(); iter.hasNext();) {
			Integer id = iter.next();
			L1Weapon item = allWeapons.get(id);
			allTemplates.put(new Integer(id.intValue()), item);
			allNames.put(item.getName(), new Integer(id.intValue()));
			allWithoutSpaceNames.put(item.getName().replace(" ", ""), new Integer(id.intValue()));
		}
		for (Iterator<Integer> iter = allArmors.keySet().iterator(); iter.hasNext();) {
			Integer id = iter.next();
			L1Armor item = allArmors.get(id);
			allTemplates.put(new Integer(id.intValue()), item);
			allNames.put(item.getName(), new Integer(id.intValue()));
			allWithoutSpaceNames.put(item.getName().replace(" ", ""), new Integer(id.intValue()));
		}
	}

	public L1Item getTemplate(int id) {
		return _allTemplates.get(id);
	}

	public L1ItemInstance createItem(int itemId) {
		L1Item temp = getTemplate(itemId);
		if (temp == null) {
			return null;
		}
		L1ItemInstance item = new L1ItemInstance();
		item.setId(IdFactory.getInstance().nextId());
		item.setItem(temp);
		L1World.getInstance().storeObject(item);
		return item;
	}

	public int findItemIdByName(String name) {
		return _allNames.containsKey(name) ? _allNames.get(name) : 0;
	}

	public int findItemIdByNameWithoutSpace(String name) {
		String n = name.replace(" ", "");
		return _allWithoutSpaceNames.containsKey(n) ? _allWithoutSpaceNames.get(n) : 0;
	}
}
