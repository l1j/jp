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
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1MaterialChoice {

	private static Logger _log = Logger.getLogger(L1MaterialChoice.class.getName());

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1MaterialChoice> {
		@XmlElement(name = "Item")
		private List<L1MaterialChoice> _list;

		public Iterator<L1MaterialChoice> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "ItemId")
		private int _itemId;

		public int getItemId() {
			return _itemId;
		}

		@XmlAttribute(name = "NeedItemId")
		private int _needItemId;

		public int getNeedItemId() {
			return _needItemId;
		}

		@XmlAttribute(name = "Chance")
		private int _chance;

		public int getChance() {
			return _chance;
		}

		@XmlAttribute(name = "Success")
		private int _success;

		public int getSuccess() {
			return _success;
		}

		@XmlAttribute(name = "Failure")
		private int _failure;

		public int getFailure() {
			return _failure;
		}

		@XmlAttribute(name = "GfxId")
		private int _gfxId;

		public int getGfxId() {
			return _gfxId;
		}
	}

	private static final String _path = "./data/xml/Item/MaterialChoice.xml";

	private static HashMap<Integer, L1MaterialChoice> _dataMap = new HashMap<Integer, L1MaterialChoice>();

	public static L1MaterialChoice get(int id) {
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
		for (Effect each : getEffects()) {
			if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
				System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, each.getItemId()));
				// %s はアイテムリストに存在しません。
				return false;
			}
			if (ItemTable.getInstance().getTemplate(each.getNeedItemId()) == null) {
				System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, each.getNeedItemId()));
				// %s はアイテムリストに存在しません。
				return false;
			}
		}
		return true;
	}

	private static void loadXml(HashMap<Integer, L1MaterialChoice> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1MaterialChoice.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1MaterialChoice each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading material choices...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1MaterialChoice> dataMap = new HashMap<Integer, L1MaterialChoice>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item, L1ItemInstance target) {

		int maxChargeCount = item.getItem().getMaxChargeCount();
		int chargeCount = item.getChargeCount();
		if (maxChargeCount > 0 && chargeCount <= 0) {
			// \f1何も起きませんでした。
			pc.sendPackets(new S_ServerMessage(79));
			return false;
		}

		Effect effect = null;
		for (Effect each : getEffects()) {
			if (each.getNeedItemId() > 0
					&& target.getItem().getItemId() != each.getNeedItemId()) {
				continue;
			}
			effect = each;
			break;
		}

		if (effect == null) {
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
		}

		int chance = 100;
		if (effect.getChance() > 0) {
			chance = effect.getChance();
		}

		if (effect.getNeedItemId() > 0) {
			if (pc.getInventory().checkItem(effect.getNeedItemId(), 1)) {
				pc.getInventory().consumeItem(effect.getNeedItemId(), 1);
			} else {
				pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
				return false;
			}
		}

		if (chance >= _random.nextInt(100) + 1) {
			L1ItemInstance newItem = pc.getInventory().storeItem(effect.getItemId(), 1);
			if (effect.getSuccess() > 0) {
				pc.sendPackets(new S_ServerMessage(effect.getSuccess()));
			}
			pc.sendPackets(new S_ServerMessage(403, newItem.getLogName()));
		} else {
			if (effect.getFailure() > 0) {
				pc.sendPackets(new S_ServerMessage(effect.getFailure()));
			}
		}
		
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
