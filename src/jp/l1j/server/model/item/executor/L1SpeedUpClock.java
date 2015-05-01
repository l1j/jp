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
import java.sql.Timestamp;
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
import jp.l1j.server.packets.server.S_ItemName;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1SpeedUpClock {

	private static Logger _log = Logger.getLogger(L1SpeedUpClock.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1SpeedUpClock> {
		@XmlElement(name = "Item")
		private List<L1SpeedUpClock> _list;

		public Iterator<L1SpeedUpClock> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "ItemId")
		private int _itemId;
		
		private int getItemId() {
			return _itemId;
		}
		
		@XmlAttribute(name = "MinTime")
		private int _minTime;
		
		private int getMinTime() {
			return _minTime;
		}
	}

	private static final String _path = "./data/xml/Item/SpeedUpClock.xml";

	private static HashMap<Integer, L1SpeedUpClock> _dataMap = new HashMap<Integer, L1SpeedUpClock>();

	public static L1SpeedUpClock get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
	}

	@XmlAttribute(name = "Time")
	private int _time;

	private int getTime() {
		return _time;
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
		}
		return true;
	}
	
	private static void loadXml(HashMap<Integer, L1SpeedUpClock> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1SpeedUpClock.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1SpeedUpClock each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading speed up clocks...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1SpeedUpClock> dataMap = new HashMap<Integer, L1SpeedUpClock>();
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
			if (each.getItemId() == target.getItemId()) {
				effect = each;
				break;
			}
		}
		
		if (effect == null) {
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
		}
		
		if (effect.getMinTime() > 0) {
			long expirationTime = target.getExpirationTime().getTime()
					- System.currentTimeMillis();
			if (expirationTime >= effect.getMinTime() * 1000) {
				target.setExpirationTime(
						new Timestamp(target.getExpirationTime().getTime()
						- getTime() * 1000));
				target.save();
				pc.sendPackets(new S_ItemName(target));
			} else {
				pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
				return false;
			}
		} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
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
