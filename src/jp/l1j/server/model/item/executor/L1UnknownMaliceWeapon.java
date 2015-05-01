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
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1UnknownMaliceWeapon {

	private static Logger _log = Logger.getLogger(L1UnknownMaliceWeapon.class.getName());
	
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1UnknownMaliceWeapon> {
		@XmlElement(name = "Item")
		private List<L1UnknownMaliceWeapon> _list;

		public Iterator<L1UnknownMaliceWeapon> iterator() {
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
	}

	private static final String _path = "./data/xml/Item/UnknownMaliceWeapon.xml";

	private static HashMap<Integer, L1UnknownMaliceWeapon> _dataMap = new HashMap<Integer, L1UnknownMaliceWeapon>();

	public static L1UnknownMaliceWeapon get(int id) {
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
		Effect effect = getEffect();
		if (ItemTable.getInstance().getTemplate(effect.getItemId()) == null) {
			System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, effect.getItemId()));
			// %s はアイテムリストに存在しません。
			return false;
		}
		return true;
	}
	
	private static void loadXml(HashMap<Integer, L1UnknownMaliceWeapon> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1UnknownMaliceWeapon.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1UnknownMaliceWeapon each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading unknown malice weapons...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1UnknownMaliceWeapon> dataMap = new HashMap<Integer, L1UnknownMaliceWeapon>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item) {
		
		int maxChargeCount = item.getItem().getMaxChargeCount();
		int chargeCount = item.getChargeCount();
		if (maxChargeCount > 0 && chargeCount <= 0) {
			// \f1何も起きませんでした。
			pc.sendPackets(new S_ServerMessage(79));
			return false;
		}
		
		int newAttrEnchantKind[] = { 1, 2, 4, 8 };

		Effect effect = getEffect();
		L1ItemInstance newItem = ItemTable.getInstance().createItem(effect.getItemId());
		newItem.setCount(1);
		if (newItem != null) {
			if (pc.getInventory().checkAddItem(newItem, 1) == L1Inventory.OK) {
				pc.getInventory().storeItem(newItem);
			} else { // 持てない場合は地面に落とす 処理のキャンセルはしない（不正防止）
				L1World.getInstance().getInventory(pc.getX(),
						pc.getY(), pc.getMapId()).storeItem(newItem);
			}
		} else {
			return false;
		}
		pc.sendPackets(new S_ServerMessage(1410, newItem.getLogName(),
				"$245", "$247")); // f1%0に強力な魔法の力が染み入ります。

		int rnd_attr = _random.nextInt(newAttrEnchantKind.length); // 属性の確定
		// 属性の付与
		newItem.setAttrEnchantKind(newAttrEnchantKind[rnd_attr]);
		pc.getInventory().updateItem(newItem,
				L1PcInventory.COL_ATTR_ENCHANT_KIND);
		pc.getInventory().saveItem(newItem);
		newItem.setAttrEnchantLevel(1);
		pc.getInventory().updateItem(newItem,
				L1PcInventory.COL_ATTR_ENCHANT_LEVEL);
		pc.getInventory().saveItem(newItem);
		
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
