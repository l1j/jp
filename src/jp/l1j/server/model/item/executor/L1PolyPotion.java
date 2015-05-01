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
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.L1PolyMorph;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.model.skill.L1BuffUtil;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1PolyPotion {

	private static Logger _log = Logger.getLogger(L1PolyPotion.class.getName());
	
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1PolyPotion> {
		@XmlElement(name = "Item")
		private List<L1PolyPotion> _list;

		public Iterator<L1PolyPotion> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "PolyId")
		private int _polyId;

		public int getPolyId() {
			return _polyId;
		}

		@XmlAttribute(name = "Time")
		private int _time;

		public int getTime() {
			return _time;
		}

		@XmlAttribute(name = "ClassInitial")
		private String _classInitial = null;

		public String getClassInitial() {
			return _classInitial;
		}

		@XmlAttribute(name = "Gender")
		private String _gender = null;

		public String getGender() {
			return _gender;
		}

		@XmlAttribute(name = "MinLevel")
		private int _minLevel = 0;

		public int getMinLevel() {
			return _minLevel;
		}

		@XmlAttribute(name = "MaxLevel")
		private int _maxLevel = 0;

		public int getMaxLevel() {
			return _maxLevel;
		}

		@XmlAttribute(name = "Chance")
		private int _chance = 0;

		public int getChance() {
			return _chance;
		}
	}

	private static final String _path = "./data/xml/Item/PolyPotion.xml";

	private static HashMap<Integer, L1PolyPotion> _dataMap = new HashMap<Integer, L1PolyPotion>();

	public static L1PolyPotion get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
	}

	@XmlAttribute(name = "Remove")
	private int _remove;

	private int getRemove() {
		return _remove;
	}

	@XmlAttribute(name = "Type")
	private String _type;

	private String getType() {
		return _type;
	}

	@XmlElement(name = "Effect")
	private CopyOnWriteArrayList<Effect> _effects;

	private List<Effect> getEffects() {
		return _effects;
	}
	
	private boolean init() {
		if (ItemTable.getInstance().getTemplate(getItemId()) == null) {
			System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, getItemId()));
			// %s はアイテムリストに存在しません。
			return false;
		}
//		for (Effect each : getEffects()) {
//			if (PolyTable.getInstance().getTemplate(each.getPolyId()) == null) {
//				System.out.println(String.format(I18N_DOES_NOT_EXIST_POLY_LIST, each.getPolyId()));
//				// %s の確率が100%ではありません。
//				return false;
//			}
//		}
		return true;
	}
	
	private static void loadXml(HashMap<Integer, L1PolyPotion> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1PolyPotion.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1PolyPotion each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading poly potions...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1PolyPotion> dataMap = new HashMap<Integer, L1PolyPotion>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item) {
		if (pc.hasSkillEffect(71) == true) { // ディケイ ポーションの状態
			pc.sendPackets(new S_ServerMessage(698)); // 魔力によって何も飲むことができません。
			return false;
		}

		// アブソルート バリアの解除
		L1BuffUtil.cancelBarrier(pc);

		int maxChargeCount = item.getItem().getMaxChargeCount();
		int chargeCount = item.getChargeCount();
		if (maxChargeCount > 0 && chargeCount <= 0) {
			// \f1何も起きませんでした。
			pc.sendPackets(new S_ServerMessage(79));
			return false;
		}
		
		Effect effect = null;
		int chance = 0;
		int rnd = _random.nextInt(100);
		for (Effect each : getEffects()) {
			if (getType() != null && getType().equalsIgnoreCase("RANDOM")) {
				chance += each.getChance();
				if (rnd >= chance) {
					continue;
				}
			}
			if (each.getClassInitial() != null) {
				if (each.getClassInitial().equalsIgnoreCase("P") && !pc.isCrown()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("K") && !pc.isKnight()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("E") && !pc.isElf()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("W") && !pc.isWizard()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("D") && !pc.isDarkelf()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("R") && !pc.isDragonKnight()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("I") && !pc.isIllusionist()) {
					continue;
				}
			}
			if (each.getGender() != null) {
				if (each.getGender().equalsIgnoreCase("M") && pc.getSex() != 0) {
					continue;
				} else if (each.getGender().equalsIgnoreCase("F") && pc.getSex() != 1) {
					continue;
				}
			}
			if (each.getMinLevel() != 0 && each.getMaxLevel() != 0) {
				if (!(pc.getLevel() >= each.getMinLevel() && pc.getLevel() <= each.getMaxLevel())) {
					continue;
				}
			}
			effect = each;
			break;
		}

		if (effect == null) {
			return false;
		}
		L1PolyMorph.doPoly(pc, effect.getPolyId(), effect.getTime(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
		
		if (getRemove() > 0) {
			if (chargeCount > 0) {
				item.setChargeCount(chargeCount - getRemove());
				pc.getInventory().updateItem(item,
						L1PcInventory.COL_CHARGE_COUNT);
			} else {
				pc.getInventory().removeItem(item, getRemove());
			}
		}
				
		return true;
	}

}
