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
package jp.l1j.server.model.item.executor;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.packets.server.S_OwnCharStatus;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SpMr;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1UniqueEnchantScroll {

	private static Logger _log = Logger.getLogger(L1UniqueEnchantScroll.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1UniqueEnchantScroll> {
		@XmlElement(name = "Item")
		private List<L1UniqueEnchantScroll> _list;

		public Iterator<L1UniqueEnchantScroll> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "Random")
		private boolean _random;

		public boolean getRandom() {
			return _random;
		}
		
		@XmlAttribute(name = "AC")
		private int _ac;

		public int getAc() {
			return _ac;
		}

		@XmlAttribute(name = "STR")
		private int _str;

		public int getStr() {
			return _str;
		}

		@XmlAttribute(name = "CON")
		private int _con;

		public int getCon() {
			return _con;
		}

		@XmlAttribute(name = "DEX")
		private int _dex;

		public int getDex() {
			return _dex;
		}
		
		@XmlAttribute(name = "INT")
		private int _int;

		public int getInt() {
			return _int;
		}

		@XmlAttribute(name = "WIS")
		private int _wis;

		public int getWis() {
			return _wis;
		}

		@XmlAttribute(name = "CHA")
		private int _cha;

		public int getCha() {
			return _cha;
		}

		@XmlAttribute(name = "HP")
		private int _hp;

		public int getHp() {
			return _hp;
		}

		@XmlAttribute(name = "HPR")
		private int _hpr;

		public int getHpr() {
			return _hpr;
		}

		@XmlAttribute(name = "MP")
		private int _mp;

		public int getMp() {
			return _mp;
		}

		@XmlAttribute(name = "MPR")
		private int _mpr;

		public int getMpr() {
			return _mpr;
		}

		@XmlAttribute(name = "MR")
		private int _mr;

		public int getMr() {
			return _mr;
		}

		@XmlAttribute(name = "SP")
		private int _sp;

		public int getSp() {
			return _sp;
		}

		@XmlAttribute(name = "Hit")
		private int _hit;

		public int getHit() {
			return _hit;
		}

		@XmlAttribute(name = "Dmg")
		private int _dmg;

		public int getDmg() {
			return _dmg;
		}

		@XmlAttribute(name = "BowHit")
		private int _bowHit;

		public int getBowHit() {
			return _bowHit;
		}

		@XmlAttribute(name = "BowDmg")
		private int _bowDmg;

		public int getBowDmg() {
			return _bowDmg;
		}

		@XmlAttribute(name = "Earth")
		private int _earth;

		public int getEarth() {
			return _earth;
		}

		@XmlAttribute(name = "Water")
		private int _water;

		public int getWater() {
			return _water;
		}

		@XmlAttribute(name = "Wind")
		private int _wind;

		public int getWind() {
			return _wind;
		}

		@XmlAttribute(name = "Fire")
		private int _fire;

		public int getFire() {
			return _fire;
		}

		@XmlAttribute(name = "Stun")
		private int _stun;

		public int getStun() {
			return _stun;
		}

		@XmlAttribute(name = "Stone")
		private int _stone;

		public int getStone() {
			return _stone;
		}

		@XmlAttribute(name = "Sleep")
		private int _sleep;

		public int getSleep() {
			return _sleep;
		}

		@XmlAttribute(name = "Freeze")
		private int _freeze;

		public int getFreeze() {
			return _freeze;
		}

		@XmlAttribute(name = "Hold")
		private int _hold;

		public int getHold() {
			return _hold;
		}

		@XmlAttribute(name = "Blind")
		private int _blind;

		public int getBlind() {
			return _blind;
		}

		@XmlAttribute(name = "EXP")
		private int _exp;

		public int getExp() {
			return _exp;
		}

		@XmlAttribute(name = "Haste")
		private boolean _haste;

		public boolean getHaste() {
			return _haste;
		}

		@XmlAttribute(name = "CanDmg")
		private boolean _canDmg;

		public boolean getCanDmg() {
			return _canDmg;
		}
	}

	private static final String _path = "./data/xml/Item/UniqueEnchantScroll.xml";

	private static HashMap<Integer, L1UniqueEnchantScroll> _dataMap = new HashMap<Integer, L1UniqueEnchantScroll>();

	public static L1UniqueEnchantScroll get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
	}
	
	@XmlAttribute(name = "Rate")
	private int _rate;

	public int getRate() {
		return _rate;
	}
	
	@XmlAttribute(name = "Type")
	private int _type;

	public int getType() {
		return _type;
	}
	
	@XmlAttribute(name = "Safety")
	private boolean _safety;

	public boolean getSafety() {
		return _safety;
	}

	@XmlAttribute(name = "Remove")
	private int _remove;

	private int getRemove() {
		return _remove;
	}

	@XmlElement(name = "Effect")
	private Effect _effect;
	
	private Effect getEffect() {
		return _effect;
	}
	
	private static void loadXml(HashMap<Integer, L1UniqueEnchantScroll> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1UniqueEnchantScroll.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1UniqueEnchantScroll each : list) {
				if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, each.getItemId()));
					// %s はアイテムリストに存在しません。
				} else {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading unique enchant scrolls...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1UniqueEnchantScroll> dataMap = new HashMap<Integer, L1UniqueEnchantScroll>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item, L1ItemInstance target) {
		Effect effect = getEffect();
		int rate = getRate();
		int type = target.getItem().getType2();
		int res = 0;
		
		if (rate <= 0) {
			// 成功率が0%
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
		}
		
		if (type != 1 && type != 2) {
			// 武器・防具以外
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
		}
		
		if ((getType() & type) != type) {
			// 対象アイテムのタイプと不一致
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
		}
		
		if (getSafety() && target.getEnchantLevel() > target.getItem().getSafeEnchant()) {
			// SafetyがTrueの場合は、未エンチャントまたは強化安全圏内の装備に対して使用可能
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
		}

		if (effect.getRandom()) {
			// ランダム属性がTrueの場合、setUniqueOptionsに処理を委ねる
			target.setUniqueOptions(rate);
		} else {
			// ユニークオプションをリセット
			if (target.isUnique()) {
				target.resetUniqueOptions();
			}
		}
		
		if (effect.getAc() > 0 && target.getItem().getType2() == 2) { // 防具のみ
			res = target.calcUniqueOption(effect.getAc(), rate);
			if (res > 0) {
				target.setAc(-res);
				if (target.isEquipped()) { // 装備中
					pc.addAc(-res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getStr() > 0) {
			res = target.calcUniqueOption(effect.getStr(), rate);
			if (res > 0) {
				target.setStr(res);
				if (target.isEquipped()) { // 装備中
					pc.addStr(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getCon() > 0) {
			res = target.calcUniqueOption(effect.getCon(), rate);
			if (res > 0) {
				target.setCon(res);
				if (target.isEquipped()) { // 装備中
					pc.addCon(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getDex() > 0) {
			res = target.calcUniqueOption(effect.getDex(), rate);
			if (res > 0) {
				target.setDex(res);
				if (target.isEquipped()) { // 装備中
					pc.addDex(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getWis() > 0) {
			res = target.calcUniqueOption(effect.getWis(), rate);
			if (res > 0) {
				target.setWis(res);
				if (target.isEquipped()) { // 装備中
					pc.addWis(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getInt() > 0) {
			res = target.calcUniqueOption(effect.getInt(), rate);
			if (res > 0) {
				target.setInt(res);
				if (target.isEquipped()) { // 装備中
					pc.addInt(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getCha() > 0) {
			res = target.calcUniqueOption(effect.getCha(), rate);
			if (res > 0) {
				target.setCha(res);
				if (target.isEquipped()) { // 装備中
					pc.addCha(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getHp() > 0) {
			res = target.calcUniqueOption(effect.getHp(), rate);
			if (res > 0) {
				target.setHp(res);
				if (target.isEquipped()) { // 装備中
					pc.addMaxHp(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getHpr() > 0) {
			res = target.calcUniqueOption(effect.getHpr(), rate);
			if (res > 0) {
				target.setHpr(res);
				if (target.isEquipped()) { // 装備中
					pc.addHpr(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getMp() > 0) {
			res = target.calcUniqueOption(effect.getMp(), rate);
			if (res > 0) {
				target.setMp(res);
				if (target.isEquipped()) { // 装備中
					pc.addMaxMp(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getMpr() > 0) {
			res = target.calcUniqueOption(effect.getMpr(), rate);
			if (res > 0) {
				target.setMpr(res);
				if (target.isEquipped()) { // 装備中
					pc.addMpr(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getMr() > 0) {
			res = target.calcUniqueOption(effect.getMr(), rate);
			if (res > 0) {
				target.setMr(res);
				if (target.isEquipped()) { // 装備中
					pc.addMr(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getSp() > 0 && target.getItem().getType2() == 1) { // 武器のみ
			res = target.calcUniqueOption(effect.getSp(), rate);
			if (res > 0) {
				target.setSp(res);
				if (target.isEquipped()) { // 装備中
					pc.addSp(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getHit() > 0) {
			res = target.calcUniqueOption(effect.getHit(), rate);
			if (res > 0) {
				target.setHitModifier(res);
				if (target.isEquipped()) { // 装備中
					pc.addHitModifierByArmor(res);
				}
				target.setIsUnique(true);
			}
		}

		if (effect.getDmg() > 0) {
			res = target.calcUniqueOption(effect.getDmg(), rate);
			if (res > 0) {
				target.setDmgModifier(res);
				if (target.isEquipped()) { // 装備中
					pc.addDmgModifierByArmor(res);
				}
				target.setIsUnique(true);
			}
		}

		if (effect.getBowHit() > 0 && target.getItem().getType2() == 2) { // 防具のみ
			res = target.calcUniqueOption(effect.getBowHit(), rate);
			if (res > 0) {
				target.setBowHitModifier(res);
				if (target.isEquipped()) { // 装備中
					pc.addBowHitModifierByArmor(res);
				}
				target.setIsUnique(true);
			}
		}

		if (effect.getBowDmg() > 0 && target.getItem().getType2() == 2) { // 防具のみ
			res = target.calcUniqueOption(effect.getBowDmg(), rate);
			if (res > 0) {
				target.setBowDmgModifier(res);
				if (target.isEquipped()) { // 装備中
					pc.addBowDmgModifierByArmor(res);
				}
				target.setIsUnique(true);
			}
		}

		if (effect.getEarth() > 0) {
			res = target.calcUniqueOption(effect.getEarth(), rate);
			if (res > 0) {
				target.setDefenseEarth(res);
				if (target.isEquipped()) { // 装備中
					pc.addEarth(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getWater() > 0) {
			res = target.calcUniqueOption(effect.getWater(), rate);
			if (res > 0) {
				target.setDefenseWater(res);
				if (target.isEquipped()) { // 装備中
					pc.addWater(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getFire() > 0) {
			res = target.calcUniqueOption(effect.getFire(), rate);
			if (res > 0) {
				target.setDefenseFire(res);
				if (target.isEquipped()) { // 装備中
					pc.addFire(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getWind() > 0) {
			res = target.calcUniqueOption(effect.getWind(), rate);
			if (res > 0) {
				target.setDefenseWind(res);
				if (target.isEquipped()) { // 装備中
					pc.addWind(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getStun() > 0) {
			res = target.calcUniqueOption(effect.getStun(), rate);
			if (res > 0) {
				target.setResistStun(res);
				if (target.isEquipped()) { // 装備中
					pc.addResistStun(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getStone() > 0) {
			res = target.calcUniqueOption(effect.getStone(), rate);
			if (res > 0) {
				target.setResistStone(res);
				if (target.isEquipped()) { // 装備中
					pc.addResistStone(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getSleep() > 0) {
			res = target.calcUniqueOption(effect.getSleep(), rate);
			if (res > 0) {
				target.setResistSleep(res);
				if (target.isEquipped()) { // 装備中
					pc.addResistSleep(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getFreeze() > 0) {
			res = target.calcUniqueOption(effect.getFreeze(), rate);
			if (res > 0) {
				target.setResistFreeze(res);
				if (target.isEquipped()) { // 装備中
					pc.addResistFreeze(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getHold() > 0) {
			res = target.calcUniqueOption(effect.getHold(), rate);
			if (res > 0) {
				target.setResistHold(res);
				if (target.isEquipped()) { // 装備中
					pc.addResistHold(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getBlind() > 0) {
			res = target.calcUniqueOption(effect.getBlind(), rate);
			if (res > 0) {
				target.setResistBlind(res);
				if (target.isEquipped()) { // 装備中
					pc.addResistBlind(res);
				}
				target.setIsUnique(true);
			}
		}
		
		if (effect.getExp() > 0) {
			res = target.calcUniqueOption(effect.getExp(), rate);
			if (res > 0) {
				target.setExpBonus(res);
				if (target.isEquipped()) { // 装備中
					pc.addExpBonusPct(res);
				}
				target.setIsUnique(true);
			}
		}

		if (effect.getHaste()) {
			res = target.calcUniqueOption(1, rate);
			if (res > 0) {
				target.setIsHaste(true);
				if (target.isEquipped()) { // 装備中
					pc.setMoveSpeed(res);
				}
				if (target.getItem().isHaste() == false) {
					target.setIsUnique(true);
				}
			} else {
				target.setIsHaste(target.getItem().isHaste());
			}
		}

		if (effect.getCanDmg() && target.getItem().getType2() == 1) { // 武器のみ
			res = target.calcUniqueOption(1, rate);
			if (res > 0) {
				target.setCanBeDmg(target.getItem().getCanbeDmg());
			} else {
				target.setCanBeDmg(false);
				if (target.getItem().getCanbeDmg() == true) {
					target.setIsUnique(true);
				}
			}
		}

		pc.getInventory().updateItem(target, L1PcInventory.COL_ATTR_ENCHANT_LEVEL);
		pc.getInventory().saveItem(target);
		pc.sendPackets(new S_SpMr(pc));
		pc.sendPackets(new S_OwnCharStatus(pc));
		
		if (getRemove() > 0) {
			if (item.getChargeCount() > 0) {
				item.setChargeCount(item.getChargeCount() - getRemove());
				pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
			} else {
				pc.getInventory().removeItem(item, getRemove());
			}
		}		
		
		return true;
	}
}
