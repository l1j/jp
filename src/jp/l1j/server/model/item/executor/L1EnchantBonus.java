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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1EnchantBonus {

	private static Logger _log = Logger.getLogger(L1EnchantBonus.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1EnchantBonus> {
		@XmlElement(name = "Item")
		private List<L1EnchantBonus> _list;

		public Iterator<L1EnchantBonus> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {

		@XmlAttribute(name = "Ac")
		private int _ac = 0;

		public int getAc() {
			return _ac;
		}

		@XmlAttribute(name = "Str")
		private int _str = 0;

		public int getStr() {
			return _str;
		}

		@XmlAttribute(name = "Dex")
		private int _dex = 0;

		public int getDex() {
			return _dex;
		}

		@XmlAttribute(name = "Con")
		private int _con = 0;

		public int getCon() {
			return _con;
		}

		@XmlAttribute(name = "Int")
		private int _int = 0;

		public int getInt() {
			return _int;
		}

		@XmlAttribute(name = "Wis")
		private int _wis = 0;

		public int getWis() {
			return _wis;
		}

		@XmlAttribute(name = "Cha")
		private int _cha = 0;

		public int getCha() {
			return _cha;
		}

		@XmlAttribute(name = "Hp")
		private int _hp = 0;

		public int getHp() {
			return _hp;
		}

		@XmlAttribute(name = "Mp")
		private int _mp = 0;

		public int getMp() {
			return _mp;
		}

		@XmlAttribute(name = "Hpr")
		private int _hpr = 0;

		public int getHpr() {
			return _hpr;
		}

		@XmlAttribute(name = "Mpr")
		private int _mpr = 0;

		public int getMpr() {
			return _mpr;
		}

		@XmlAttribute(name = "Mr")
		private int _mr;

		public int getMr() {
			return _mr;
		}

		@XmlAttribute(name = "Sp")
		private int _sp;

		public int getSp() {
			return _sp;
		}

		@XmlAttribute(name = "Hit")
		private int _hit = 0;

		public int getHitModifier() {
			return _hit;
		}

		@XmlAttribute(name = "Dmg")
		private int _dmg = 0;

		public int getDmgModifier() {
			return _dmg;
		}

		@XmlAttribute(name = "BowHit")
		private int _bowHit = 0;

		public int getBowHitModifier() {
			return _bowHit;
		}

		@XmlAttribute(name = "BowDmg")
		private int _bowDmg = 0;

		public int getBowDmgModifier() {
			return _bowDmg;
		}

		@XmlAttribute(name = "WeightReduction")
		private int _wr = 0;

		public int getWeightReduction() {
			return _wr;
		}

		@XmlAttribute(name = "DamageReduction")
		private int _dr = 0;

		public int getDamageReduction() {
			return _dr;
		}

		@XmlAttribute(name = "Earth")
		private int _earth = 0;

		public int getDefenseEarth() {
			return _earth;
		}

		@XmlAttribute(name = "Water")
		private int _water = 0;

		public int getDefenseWater() {
			return _water;
		}

		@XmlAttribute(name = "Fire")
		private int _fire = 0;

		public int getDefenseFire() {
			return _fire;
		}

		@XmlAttribute(name = "Wind")
		private int _wind = 0;

		public int getDefenseWind() {
			return _wind;
		}

		@XmlAttribute(name = "ResistStun")
		private int _resistStun = 0;

		public int getResistStun() {
			return _resistStun;
		}

		@XmlAttribute(name = "ResistStone")
		private int _resistStone = 0;

		public int getResistStone() {
			return _resistStone;
		}

		@XmlAttribute(name = "ResistSleep")
		private int _resistSleep = 0;

		public int getResistSleep() {
			return _resistSleep;
		}

		@XmlAttribute(name = "ResistFreeze")
		private int _resistFreeze = 0;

		public int getResistFreeze() {
			return _resistFreeze;
		}

		@XmlAttribute(name = "ResistHold")
		private int _resistHold = 0;

		public int getResistHold() {
			return _resistHold;
		}

		@XmlAttribute(name = "ResistBlind")
		private int _resistBlind = 0;

		public int getResistBlind() {
			return _resistBlind;
		}

		@XmlAttribute(name = "Exp")
		private int _exp = 0;

		public int getExpBonus() {
			return _exp;
		}

		@XmlAttribute(name = "PotRate")
		private int _potRate = 0;

		public int getPotionRecoveryRate() {
			return _potRate;
		}

		@XmlAttribute(name = "Level")
		private int _level = 0;

		public int getLevel() {
			return _level;
		}
	}

	private static final String _path = "./data/xml/Item/EnchantBonus.xml";

	private static HashMap<Integer, L1EnchantBonus> _dataMap = new HashMap<Integer, L1EnchantBonus>();

	public static L1EnchantBonus get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
	}

	@XmlElement(name = "Effect")
	private Effect _effect;

	private Effect getEffect() {
		return _effect;
	}
	
	private boolean init() {
		if (ItemTable.getInstance().getTemplate(getItemId()) == null) {
			System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, getItemId()));
			// %s はアイテムリストに存在しません。
			return false;
		}
		return true;
	}
	
	private static void loadXml(HashMap<Integer, L1EnchantBonus> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1EnchantBonus.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1EnchantBonus each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading increase per enchant items...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1EnchantBonus> dataMap = new HashMap<Integer, L1EnchantBonus>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public int getAc(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result -= i * effect.getAc();
		}
		
		return result;
	}
	
	public int getAc(L1ItemInstance item) {
		return 0;
	}

	public int getStr(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getStr();
		}
		
		return result;
	}

	public int getDex(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getDex();
		}
		
		return result;
	}

	public int getCon(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getCon();
		}
		
		return result;
	}

	public int getInt(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getInt();
		}
		
		return result;
	}

	public int getWis(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getWis();
		}
		
		return result;
	}

	public int getCha(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getCha();
		}
		
		return result;
	}

	public int getHp(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getHp();
		}
		
		return result;
	}

	public int getHpr(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getHpr();
		}
		
		return result;
	}

	public int getMp(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getMp();
		}
		
		return result;
	}

	public int getMpr(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getMpr();
		}
		
		return result;
	}

	public int getMr(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getMr();
		}
		
		return result;
	}

	public int getSp(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getSp();
		}
		
		return result;
	}

	public int getHitModifier(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getHitModifier();
		}
		
		return result;
	}

	public int getDmgModifier(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getDmgModifier();
		}
		
		return result;
	}

	public int getBowHitModifier(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getBowHitModifier();
		}
		
		return result;
	}

	public int getBowDmgModifier(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getBowDmgModifier();
		}
		
		return result;
	}

	public int getWeightReduction(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getWeightReduction();
		}
		
		return result;
	}

	public int getDamageReduction(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getDamageReduction();
		}
		
		return result;
	}

	public int getDefenseEarth(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getDefenseEarth();
		}
		
		return result;
	}

	public int getDefenseWater(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getDefenseWater();
		}
		
		return result;
	}

	public int getDefenseFire(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getDefenseFire();
		}
		
		return result;
	}

	public int getDefenseWind(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getDefenseWind();
		}
		
		return result;
	}

	public int getResistStun(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getResistStun();
		}
		
		return result;
	}

	public int getResistStone(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getResistStone();
		}
		
		return result;
	}

	public int getResistSleep(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getResistSleep();
		}
		
		return result;
	}

	public int getResistFreeze(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getResistFreeze();
		}
		
		return result;
	}

	public int getResistHold(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getResistHold();
		}
		
		return result;
	}

	public int getResistBlind(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getResistBlind();
		}
		
		return result;
	}

	public int getExpBonus(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getExpBonus();
		}
		
		return result;
	}

	public int getPotionRecoveryRate(int i) {
		int result = 0;
		Effect effect = getEffect();
		
		if (effect != null) {
			result += i * effect.getPotionRecoveryRate();
		}
		
		return result;
	}
}
