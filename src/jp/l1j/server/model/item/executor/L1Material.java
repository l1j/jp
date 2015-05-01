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
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1Material {

	private static Logger _log = Logger.getLogger(L1Material.class.getName());
	
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1Material> {
		@XmlElement(name = "Item")
		private List<L1Material> _list;

		public Iterator<L1Material> iterator() {
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
		
		@XmlAttribute(name = "Amount")
		private int _amount;

		public int getAmount() {
			return _amount;
		}
		
		@XmlAttribute(name = "NeedItemId")
		private String _needItemId = "";

		public String getNeedItemId() {
			return _needItemId;
		}

		@XmlAttribute(name = "GfxId")
		private int _gfxId;

		public int getGfxId() {
			return _gfxId;
		}
	}

	private static final String _path = "./data/xml/Item/Material.xml";

	private static HashMap<Integer, L1Material> _dataMap = new HashMap<Integer, L1Material>();

	public static L1Material get(int id) {
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

	@XmlAttribute(name = "QuestId")
	private int _questId;

	private int getQuestId() {
		return _questId;
	}

	@XmlAttribute(name = "QuestStep")
	private int _questStep;

	private int getQuestStep() {
		return _questStep;
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
	
	private static void loadXml(HashMap<Integer, L1Material> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1Material.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1Material each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading materials...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1Material> dataMap = new HashMap<Integer, L1Material>();
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

		if (getQuestId() > 0) {
			if (pc.getQuest().getStep(getQuestId()) != getQuestStep()) {
				pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
				return false;
			}
		}
		
		Effect effect = getEffect();

		String[] ids;
		if (!effect.getNeedItemId().equals("")) {
			ids = effect.getNeedItemId().split(",", 0);
			for (String id: ids) {
				if (!pc.getInventory().checkItem(Integer.parseInt(id), 1)) {
					pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
					return false;
				}
			}
			for (String id: ids) {
				pc.getInventory().consumeItem(Integer.parseInt(id), 1);
			}
		}
		
		int amount = effect.getAmount() > 0 ? effect.getAmount() : 1;
		L1ItemInstance newItem = pc.getInventory().storeItem(effect.getItemId(), amount);
		if (effect.getGfxId() > 0) {
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
		}
		pc.sendPackets(new S_ServerMessage(403, newItem.getLogName()));
		
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
