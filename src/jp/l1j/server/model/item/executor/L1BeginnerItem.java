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
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1BeginnerItem {

	private static Logger _log = Logger.getLogger(L1BeginnerItem.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "BeginnerItems")
	private static class ItemList implements Iterable<L1BeginnerItem> {
		@XmlElement(name = "Items")
		private List<L1BeginnerItem> _list;

		public Iterator<L1BeginnerItem> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Item {
		@XmlAttribute(name = "ItemId")
		private int _itemId;

		public int getItemId() {
			return _itemId;
		}

		@XmlAttribute(name = "Amount")
		private int _amount;

		public int getAmount() {
			return _amount;
		}
		
		@XmlAttribute(name = "ChargeCount")
		private int _chargeCount;

		public int getChargeCount() {
			return _chargeCount;
		}
		
		@XmlAttribute(name = "EnchantLevel")
		private int _enchantLevel;

		public int getEnchantLevel() {
			return _enchantLevel;
		}
	}

	private static final String _path = "./data/xml/Item/BeginnerItems.xml";

	private static HashMap<String, L1BeginnerItem> _dataMap = new HashMap<String, L1BeginnerItem>();

	public static L1BeginnerItem get(String classInitial) {
		return _dataMap.get(classInitial);
	}

	@XmlAttribute(name = "ClassInitial")
	private String _classInitial;

	private String getClassInitial() {
		return _classInitial;
	}

	@XmlElement(name = "Item")
	private CopyOnWriteArrayList<Item> _items;

	private List<Item> getItems() {
		return _items;
	}
	
	private boolean init() {
		for (Item each : getItems()) {
			if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
				System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, each.getItemId()));
				// %s はアイテムリストに存在しません。
				return false;
			}
		}
		return true;
	}
	
	private static void loadXml(HashMap<String, L1BeginnerItem> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1BeginnerItem.ItemList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemList list = (ItemList) um.unmarshal(file);

			for (L1BeginnerItem each : list) {
				if (each.init()) {
					dataMap.put(each.getClassInitial(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading beginner items...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<String, L1BeginnerItem> dataMap = new HashMap<String, L1BeginnerItem>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean storeToInventory(L1PcInstance pc) {
		boolean result = false;
		for (Item each : getItems()) {
			L1ItemInstance item = pc.getInventory().findItemId(each.getItemId());
			if (item != null) {
				if (item.getItem().getType2() == 0) { // etcitem
					item.setCount(item.getCount() + each.getAmount());
					item.save();
					pc.sendPackets(new S_ServerMessage(143, item.getItem().getName()));
					result = true;
				}
			} else {
				item = pc.getInventory().storeItem(each.getItemId(), each.getAmount());
				if (each.getChargeCount() > 0) {
					item.setChargeCount(each.getChargeCount());
				}
				if (each.getEnchantLevel() > 0) {
					item.setEnchantLevel(each.getEnchantLevel());
				}
				item.save();
				pc.sendPackets(new S_ServerMessage(143, item.getItem().getName()));
				result = true;
			}
		}
		return result;
	}
}
