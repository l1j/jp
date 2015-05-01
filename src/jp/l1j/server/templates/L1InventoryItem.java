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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;
import jp.l1j.server.datatables.InnKeyTable;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.L1QueryUtil;
import jp.l1j.server.utils.L1QueryUtil.EntityFactory;
import jp.l1j.server.utils.L1SqlException;
import jp.l1j.server.utils.SqlUtil;
import jp.l1j.server.utils.collections.Lists;

public class L1InventoryItem {
	private static Logger _log = Logger.getLogger(L1InventoryItem.class.getName());

	private int _storedId = 0;
	private int _id;
	private int _ownerId;
	private int _location;
	private int _itemId;
	private int _itemCount;
	private boolean _isEquipped;
	private int _enchantLevel;
	private boolean _isIdentified;
	private int _durability;
	private int _chargeCount;
	private int _chargeTime;
	private Timestamp _expirationTime;
	private Timestamp _lastUsed;
	private boolean _isSealed;
	private boolean _isProtected;
	private int _protectItemId;
	private int _attrEnchantKind;
	private int _attrEnchantLevel;
	private int _ac;
	private int _str;
	private int _con;
	private int _dex;
	private int _wis;
	private int _cha;
	private int _int;
	private int _hp;
	private int _hpr;
	private int _mp;
	private int _mpr;
	private int _mr;
	private int _sp;
	private int _hitModifier;
	private int _dmgModifier;
	private int _bowHitModifier;
	private int _bowDmgModifier;
	private int _defenseWater;
	private int _defenseWind;
	private int _defenseFire;
	private int _defenseEarth;
	private int _resistStun;
	private int _resistStone;
	private int _resistSleep;
	private int _resistFreeze;
	private int _resistHold;
	private int _resistBlind;
	private int _expBonus;
	private int _potionRecoveryRate;
	private boolean _isHaste;
	private boolean _canBeDmg;
	private boolean _isUnique;

	public static final int LOC_NONE = -1;
	public static final int LOC_CHARACTER = 0;
	public static final int LOC_WAREHOUSE = 1;
	public static final int LOC_ELF_WAREHOUSE = 2;
	public static final int LOC_CLAN_WAREHOUSE = 3;
	public static final int LOC_ADDITIONAL_WAREHOUSE = 4;

	public L1InventoryItem() {
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
	}

	public int getOwnerId() {
		return _ownerId;
	}

	public void setOwnerId(int ownerId) {
		_ownerId = ownerId;
	}

	public int getLocation() {
		return _location;
	}

	public void setLocation(int location) {
		_location = location;
	}

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(int itemId) {
		_itemId = itemId;
	}

	public int getItemCount() {
		return _itemCount;
	}

	public void setItemCount(int itemCount) {
		_itemCount = itemCount;
	}

	public boolean isEquipped() {
		return _isEquipped;
	}

	public void setEquipped(boolean isEquipped) {
		_isEquipped = isEquipped;
	}

	public int getEnchantLevel() {
		return _enchantLevel;
	}

	public void setEnchantLevel(int enchantLevel) {
		_enchantLevel = enchantLevel;
	}

	public boolean isIdentified() {
		return _isIdentified;
	}

	public void setIdentified(boolean isIdentified) {
		_isIdentified = isIdentified;
	}

	public int getDurability() {
		return _durability;
	}

	public void setDurability(int durability) {
		_durability = durability;
	}

	public int getChargeCount() {
		return _chargeCount;
	}

	public void setChargeCount(int chargeCount) {
		_chargeCount = chargeCount;
	}

	public int getChargeTime() {
		return _chargeTime;
	}

	public void setChargeTime(int chargeTime) {
		_chargeTime = chargeTime;
	}

	public Timestamp getExpirationTime() {
		return _expirationTime;
	}

	public void setExpirationTime(Timestamp expirationTime) {
		_expirationTime = expirationTime;
	}

	public Timestamp getLastUsed() {
		return _lastUsed;
	}

	public void setLastUsed(Timestamp lastUsed) {
		_lastUsed = lastUsed;
	}

	public boolean isSealed() {
		return _isSealed;
	}

	public void setSealed(boolean isSealed) {
		_isSealed = isSealed;
	}

	public boolean isProtected() {
		return _isProtected;
	}

	public void setProtected(boolean isProtected) {
		_isProtected = isProtected;
	}

	public int getProtectItemId() {
		return _protectItemId;
	}
	
	public void setProtectItemId(int protectItemId) {
		_protectItemId = protectItemId;
	}
	
	public int getAttrEnchantKind() {
		return _attrEnchantKind;
	}

	public void setAttrEnchantKind(int attrEnchantKind) {
		_attrEnchantKind = attrEnchantKind;
	}

	public int getAttrEnchantLevel() {
		return _attrEnchantLevel;
	}

	public void setAttrEnchantLevel(int attrEnchantLevel) {
		_attrEnchantLevel = attrEnchantLevel;
	}
	
	public int getAc() {
		return _ac;
	}

	public void setAc(int _ac) {
		this._ac = _ac;
	}
	
	public int getStr() {
		return _str;
	}

	public void setStr(int _str) {
		this._str = _str;
	}
	
	public int getCon() {
		return _con;
	}

	public void setCon(int _con) {
		this._con = _con;
	}
	
	public int getDex() {
		return _dex;
	}

	public void setDex(int _dex) {
		this._dex = _dex;
	}
	
	public int getWis() {
		return _wis;
	}

	public void setWis(int _wis) {
		this._wis = _wis;
	}
	
	public int getCha() {
		return _cha;
	}

	public void setCha(int _cha) {
		this._cha = _cha;
	}
	
	public int getInt() {
		return _int;
	}

	public void setInt(int _int) {
		this._int = _int;
	}
	
	public int getHp() {
		return _hp;
	}

	public void setHp(int _hp) {
		this._hp = _hp;
	}
	
	public int getHpr() {
		return _hpr;
	}

	public void setHpr(int _hpr) {
		this._hpr = _hpr;
	}
	
	public int getMp() {
		return _mp;
	}

	public void setMp(int _mp) {
		this._mp = _mp;
	}
	
	public int getMpr() {
		return _mpr;
	}

	public void setMpr(int _mpr) {
		this._mpr = _mpr;
	}
	
	public int getMr() {
		return _mr;
	}

	public void setMr(int _mr) {
		this._mr = _mr;
	}
	
	public int getSp() {
		return _sp;
	}

	public void setSp(int _sp) {
		this._sp = _sp;
	}

	public int getHitModifier() {
		return _hitModifier;
	}

	public void setHitModifier(int _hitModifier) {
		this._hitModifier = _hitModifier;
	}
	
	public int getDmgModifier() {
		return _dmgModifier;
	}

	public void setDmgModifier(int _dmgModifier) {
		this._dmgModifier = _dmgModifier;
	}

	public int getBowHitModifier() {
		return _bowHitModifier;
	}

	public void setBowHitModifier(int _bowHitModifier) {
		this._bowHitModifier = _bowHitModifier;
	}
	
	public int getBowDmgModifier() {
		return _bowDmgModifier;
	}

	public void setBowDmgModifier(int _bowDmgModifier) {
		this._bowDmgModifier = _bowDmgModifier;
	}

	public int getDefenseEarth() {
		return _defenseEarth;
	}

	public void setDefenseEarth(int _defenseEarth) {
		this._defenseEarth = _defenseEarth;
	}
		
	public int getDefenseWater() {
		return _defenseWater;
	}

	public void setDefenseWater(int _defenseWater) {
		this._defenseWater = _defenseWater;
	}
	
	public int getDefenseFire() {
		return _defenseFire;
	}

	public void setDefenseFire(int _defenseFire) {
		this._defenseFire = _defenseFire;
	}
	
	public int getDefenseWind() {
		return _defenseWind;
	}

	public void setDefenseWind(int _defenseWind) {
		this._defenseWind = _defenseWind;
	}
	
	public int getResistStun() {
		return _resistStun;
	}

	public void setResistStun(int _resistStun) {
		this._resistStun = _resistStun;
	}
	
	public int getResistStone() {
		return _resistStone;
	}

	public void setResistStone(int _resistStone) {
		this._resistStone = _resistStone;
	}
	
	public int getResistSleep() {
		return _resistSleep;
	}

	public void setResistSleep(int _resistSleep) {
		this._resistSleep = _resistSleep;
	}
	
	public int getResistFreeze() {
		return _resistFreeze;
	}

	public void setResistFreeze(int _resistFreeze) {
		this._resistFreeze = _resistFreeze;
	}
	
	public int getResistHold() {
		return _resistHold;
	}

	public void setResistHold(int _resistHold) {
		this._resistHold = _resistHold;
	}
	
	public int getResistBlind() {
		return _resistBlind;
	}

	public void setResistBlind(int _resistBlind) {
		this._resistBlind = _resistBlind;
	}
	
	public int getExpBonus() {
		return _expBonus;
	}

	public void setExpBonus(int _expBonus) {
		this._expBonus = _expBonus;
	}

	public boolean isHaste() {
		return _isHaste;
	}
	
	public void setIsHaste(boolean _isHaste) {
		this._isHaste = _isHaste;
	}
	
	public boolean getCanBeDmg() {
		return _canBeDmg;
	}
	
	public void setCanBeDmg(boolean _canBeDmg) {
		this._canBeDmg = _canBeDmg;
	}
	
	public boolean isUnique() {
		return _isUnique;
	}
	
	public void setIsUniuqe(boolean _isUnique) {
		this._isUnique = _isUnique;
	}

	public int getPotionRecoveryRate() {
		return _potionRecoveryRate;
	}

	public void setPotionRecoveryRate(int _potionRecoveryRate) {
		this._potionRecoveryRate = _potionRecoveryRate;
	}
	
	private static class Factory implements
			L1QueryUtil.EntityFactory<L1InventoryItem> {
		@Override
		public L1InventoryItem fromResultSet(ResultSet rs) throws SQLException {
			L1InventoryItem result = new L1InventoryItem();
			result._id = result._storedId = rs.getInt("id");
			result._ownerId = rs.getInt("owner_id");
			result._location = rs.getInt("location");
			result._itemId = rs.getInt("item_id");
			result._itemCount = rs.getInt("item_count");
			result._isEquipped = rs.getBoolean("is_equipped");
			result._enchantLevel = rs.getInt("enchant_level");
			result._isIdentified = rs.getBoolean("is_identified");
			result._durability = rs.getInt("durability");
			result._chargeCount = rs.getInt("charge_count");
			result._chargeTime = rs.getInt("charge_time");
			result._expirationTime = rs.getTimestamp("expiration_time");
			result._lastUsed = rs.getTimestamp("last_used");
			result._isSealed = rs.getBoolean("is_sealed");
			result._isProtected = rs.getBoolean("is_protected");
			result._protectItemId = rs.getInt("protect_item_id");
			result._attrEnchantKind = rs.getInt("attr_enchant_kind");
			result._attrEnchantLevel = rs.getInt("attr_enchant_level");
			result._ac = rs.getInt("ac");
			result._str = rs.getInt("str");
			result._con = rs.getInt("con");
			result._dex = rs.getInt("dex");
			result._wis = rs.getInt("wis");
			result._cha = rs.getInt("cha");
			result._int = rs.getInt("int");
			result._hp = rs.getInt("hp");
			result._hpr = rs.getInt("hpr");
			result._mp = rs.getInt("mp");
			result._mpr = rs.getInt("mpr");
			result._mr = rs.getInt("mr");
			result._sp = rs.getInt("sp");
			result._hitModifier = rs.getInt("hit_modifier");
			result._dmgModifier = rs.getInt("dmg_modifier");
			result._bowHitModifier = rs.getInt("bow_hit_modifier");
			result._bowDmgModifier = rs.getInt("bow_dmg_modifier");
			result._defenseEarth = rs.getInt("defense_earth");
			result._defenseWater = rs.getInt("defense_water");
			result._defenseFire = rs.getInt("defense_fire");
			result._defenseWind = rs.getInt("defense_wind");
			result._resistStun = rs.getInt("resist_stun");
			result._resistStone = rs.getInt("resist_stone");
			result._resistSleep = rs.getInt("resist_sleep");
			result._resistFreeze = rs.getInt("resist_freeze");
			result._resistHold = rs.getInt("resist_hold");
			result._resistBlind = rs.getInt("resist_blind");
			result._expBonus = rs.getInt("exp_bonus");
			result._isHaste = rs.getBoolean("is_haste");
			result._canBeDmg = rs.getBoolean("can_be_dmg");
			result._isUnique = rs.getBoolean("is_unique");
			result._potionRecoveryRate = rs.getInt("potion_recovery_rate");
			return result;
		}
	}

	public static L1InventoryItem findById(int id) {
		return L1QueryUtil.selectFirst(new Factory(),
				"SELECT * FROM inventory_items WHERE id = ?", id);
	}

	public static List<L1InventoryItem> findByOwnerId(int ownerId) {
		return L1QueryUtil.selectAll(new Factory(),
				"SELECT * FROM inventory_items WHERE owner_id = ?", ownerId);
	}

	public static List<L1InventoryItem> findByOwnerIdAndLocation(int ownerId,
			int location) {
		return L1QueryUtil.selectAll(new Factory(),
				"SELECT * FROM inventory_items WHERE owner_id = ? AND location = ?",
				ownerId, location);
	}

	private L1QueryBuilder buildQuery() {
		L1QueryBuilder qb = new L1QueryBuilder("inventory_items", _storedId);
		qb.addColumn("id", _id);
		qb.addColumn("owner_id", _ownerId);
		qb.addColumn("location", _location);
		qb.addColumn("item_id", _itemId);
		qb.addColumn("item_count", _itemCount);
		qb.addColumn("is_equipped", _isEquipped);
		qb.addColumn("enchant_level", _enchantLevel);
		qb.addColumn("is_identified", _isIdentified);
		qb.addColumn("durability", _durability);
		qb.addColumn("charge_count", _chargeCount);
		qb.addColumn("charge_time", _chargeTime);
		qb.addColumn("expiration_time", _expirationTime);
		qb.addColumn("last_used", _lastUsed);
		qb.addColumn("is_sealed", _isSealed);
		qb.addColumn("is_protected", _isProtected);
		qb.addColumn("protect_item_id", _protectItemId);
		qb.addColumn("attr_enchant_kind", _attrEnchantKind);
		qb.addColumn("attr_enchant_level", _attrEnchantLevel);
		qb.addColumn("ac", _ac);
		qb.addColumn("str", _str);
		qb.addColumn("con", _con);
		qb.addColumn("dex", _dex);
		qb.addColumn("wis", _wis);
		qb.addColumn("cha", _cha);
		qb.addColumn("int", _int);
		qb.addColumn("hp", _hp);
		qb.addColumn("hpr", _hpr);
		qb.addColumn("mp", _mp);
		qb.addColumn("mpr", _mpr);
		qb.addColumn("mr", _mr);
		qb.addColumn("sp", _sp);
		qb.addColumn("hit_modifier", _hitModifier);
		qb.addColumn("dmg_modifier", _dmgModifier);
		qb.addColumn("bow_hit_modifier", _bowHitModifier);
		qb.addColumn("bow_dmg_modifier", _bowDmgModifier);
		qb.addColumn("defense_earth", _defenseEarth);
		qb.addColumn("defense_water", _defenseWater);
		qb.addColumn("defense_fire", _defenseFire);
		qb.addColumn("defense_wind", _defenseWind);
		qb.addColumn("resist_stun", _resistStun);
		qb.addColumn("resist_stone", _resistStone);
		qb.addColumn("resist_sleep", _resistSleep);
		qb.addColumn("resist_freeze", _resistFreeze);
		qb.addColumn("resist_hold", _resistHold);
		qb.addColumn("resist_blind", _resistBlind);
		qb.addColumn("exp_bonus", _expBonus);
		qb.addColumn("is_haste", _isHaste);
		qb.addColumn("can_be_dmg", _canBeDmg);
		qb.addColumn("is_unique", _isUnique);
		qb.addColumn("potion_recovery_rate", _potionRecoveryRate);
		qb.buildQuery();
		return qb;
	}

	private void insert(Connection con) {
		String sql = "INSERT INTO inventory_items VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		L1QueryUtil.execute(con, sql, _id, _ownerId, _location, _itemId,
				_itemCount, _isEquipped, _enchantLevel, _isIdentified,
				_durability, _chargeCount, _chargeTime, _expirationTime, _lastUsed,
				_isSealed, _isProtected, _protectItemId, _attrEnchantKind, _attrEnchantLevel,
				_ac, _str, _con, _dex, _wis, _cha, _int, _hp, _hpr, _mp, _mpr, _mr, _sp,
				_hitModifier, _dmgModifier, _bowHitModifier, _bowDmgModifier,
				_defenseEarth, _defenseWater, _defenseFire, _defenseWind,
				_resistStun, _resistStone, _resistSleep, _resistFreeze,
				_resistHold, _resistBlind, _expBonus, _isHaste, _canBeDmg,
				_isUnique, _potionRecoveryRate);
		_storedId = _id;
	}

	private void update(Connection con) {
		L1QueryBuilder qb = buildQuery();
		L1QueryUtil.execute(con, qb.getQuery(), qb.getArgs());
	}

	private boolean isStoreNecessary() {
		if (_storedId == 0 && _location == LOC_NONE) {
			return false;
		}
		return true;
	}

	public void save() {
		if (!isStoreNecessary()) {
			return;
		}

		Connection con = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			save(con);
		} catch (SQLException e) {
			throw new L1SqlException(e);
		} finally {
			SqlUtil.close(con);
		}
	}

	public void save(Connection con) {
		if (!isStoreNecessary()) {
			return;
		}

		if (_location == LOC_NONE || _itemCount == 0) {
			delete(con);
			return;
		}

		if (_storedId == 0) {
			insert(con);
		} else {
			update(con);
		}
		
		_storedId = _id;
	}

	public void delete() {
		if (_storedId == 0) {
			return;
		}
		Connection con = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			delete(con);
		} catch (SQLException e) {
			throw new L1SqlException(e);
		} finally {
			SqlUtil.close(con);
		}
	}

	public void delete(Connection con) {
		if (_storedId == 0) {
			return;
		}
		L1QueryUtil.execute(con, "DELETE FROM inventory_items WHERE id = ?",
				_storedId);
		_storedId = 0;
	}

	public static void deleteAll(int ownerId) {
		Connection con = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			deleteAll(con, ownerId);
		} catch (SQLException e) {
			throw new L1SqlException(e);
		} finally {
			SqlUtil.close(con);
		}
	}

	public static void deleteAll(Connection con, int ownerId) {
		L1QueryUtil.execute(con,
				"DELETE FROM inventory_items WHERE owner_id = ?", ownerId);
	}

	private static class CountFactory implements EntityFactory<Integer> {
		@Override
		public Integer fromResultSet(ResultSet rs) throws SQLException {
			return rs.getInt("cnt");
		}
	}

	public static int countByOwnerId(int ownerId) {
		Integer result = L1QueryUtil.selectFirst(new CountFactory(),
				"SELECT COUNT(*) FROM inventory_items WHERE owner_id = ?",
				ownerId);
		return result == null ? 0 : result;
	}

	public static List<L1ItemInstance> instantiate(List<L1InventoryItem> items) {
		List<L1ItemInstance> result = Lists.newArrayList();

		for (L1InventoryItem item : items) {
			int itemId = item.getItemId();
			L1Item itemTemplate = ItemTable.getInstance().getTemplate(itemId);
			if (itemTemplate == null) {
				_log.warning(String.format("item id:%d not found", itemId));
				continue;
			}
			L1ItemInstance instance = new L1ItemInstance(item);
			if (instance.getItem().getItemId() == 40312) { // 宿屋のキー記録
				InnKeyTable.hasKey(instance);
			}
			result.add(instance);
		}
		return result;
	}
}
