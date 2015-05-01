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
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1TreasureBox {

	private static Logger _log = Logger.getLogger(L1TreasureBox.class.getName());

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "TreasureBoxList")
	private static class TreasureBoxList implements Iterable<L1TreasureBox> {
		@XmlElement(name = "TreasureBox")
		private List<L1TreasureBox> _list;

		public Iterator<L1TreasureBox> iterator() {
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

		@XmlAttribute(name = "Count")
		private int _count;

		public int getCount() {
			return _count;
		}

		@XmlAttribute(name = "Min")
		private int _min;

		public int getMin() {
			return _min;
		}

		@XmlAttribute(name = "Max")
		private int _max;

		public int getMax() {
			return _max;
		}

		private int _chance;

		@XmlAttribute(name = "Chance")
		private void setChance(double chance) {
			_chance = (int) (chance * 10000);
		}

		public double getChance() {
			return _chance;
		}
	}

	private static enum TYPE {
		RANDOM, SPECIFIC
	}

	private static final String _path = "./data/xml/Item/TreasureBox.xml";

	private static HashMap<Integer, L1TreasureBox> _dataMap = new HashMap<Integer, L1TreasureBox>();

	/**
	 * 指定されたIDのTreasureBoxを返す。
	 * 
	 * @param id
	 *            - TreasureBoxのID。普通はアイテムのItemIdになる。
	 * @return 指定されたIDのTreasureBox。見つからなかった場合はnull。
	 */
	public static L1TreasureBox get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _boxId;

	private int getBoxId() {
		return _boxId;
	}

	@XmlAttribute(name = "Type")
	private TYPE _type;

	private TYPE getType() {
		return _type;
	}

	@XmlElement(name = "Item")
	private CopyOnWriteArrayList<Item> _items;

	private List<Item> getItems() {
		return _items;
	}

	private int _totalChance;

	private int getTotalChance() {
		return _totalChance;
	}

	private boolean init() {
		if (ItemTable.getInstance().getTemplate(getBoxId()) == null) {
			System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, getBoxId()));
			// %s はアイテムリストに存在しません。
			return false;
		}
		for (Item each : getItems()) {
			_totalChance += each.getChance();
			if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
				_log.warning(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, each.getItemId()));
				// %s はアイテムリストに存在しません。
				return false;
			}
		}
		if (getTotalChance() != 0 && getTotalChance() != 1000000) {
			_log.warning(String.format(I18N_PROBABILITIES_ERROR, getBoxId()));
			// %s の確率が100%ではありません。
			return false;
		}
		return true;
	}

	private static void loadXml(HashMap<Integer, L1TreasureBox> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1TreasureBox.TreasureBoxList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			TreasureBoxList list = (TreasureBoxList) um.unmarshal(file);

			for (L1TreasureBox each : list) {
				if (each.init()) {
					dataMap.put(each.getBoxId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading treasure boxes...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1TreasureBox> dataMap = new HashMap<Integer, L1TreasureBox>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	/**
	 * TreasureBoxを開けるPCにアイテムを入手させる。PCがアイテムを持ちきれなかった場合は アイテムは地面に落ちる。
	 * 
	 * @param pc
	 *            - TreasureBoxを開けるPC
	 * @return 開封した結果何らかのアイテムが出てきた場合はtrueを返す。 持ちきれず地面に落ちた場合もtrueになる。
	 */
	public boolean open(L1PcInstance pc) {
		L1ItemInstance item = null;

		if ((getBoxId() == 40576 && !pc.isElf()) // 魂の結晶の破片（白）
				|| (getBoxId() == 40577 && !pc.isWizard()) // 魂の結晶の破片（黒）
				|| (getBoxId() == 40578 && !pc.isKnight())) { // 魂の結晶の破片（赤）
			pc.sendPackets(new S_ServerMessage(264)); // \f1あなたのクラスではこのアイテムは使用できません。
			return false;
		}

		if (getBoxId() == 50499) {// デスマッチ記念品の箱
			if (pc.getMapId() != 5153) {
				return false;
			}
		}

		if (getType().equals(TYPE.SPECIFIC)) {
			// 出るアイテムが決まっているもの
			for (Item each : getItems()) {
				item = ItemTable.getInstance().createItem(each.getItemId());
				if (item != null) {
					if (each.getCount() > 0) {
						item.setCount(each.getCount());
						storeItem(pc, item);
					} else if (each.getMax() > 0 && each.getMin() > 0
							&& each.getMax() > each.getMin()) {
						int rnd = each.getMax() - each.getMin();
						int count = each.getMin() + _random.nextInt(rnd + 1);
						item.setCount(count);
						storeItem(pc, item);
					}
				}
			}

		} else if (getType().equals(TYPE.RANDOM)) {
			// 出るアイテムがランダムに決まるもの
			RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
			int chance = 0;

			int r = random.nextInt(getTotalChance());

			for (Item each : getItems()) {
				chance += each.getChance();

				if (r < chance) {
					item = ItemTable.getInstance().createItem(each.getItemId());
					if (item != null) {
						if (each.getCount() > 0) {
							item.setCount(each.getCount());
							storeItem(pc, item);
						} else if (each.getMax() > 0 && each.getMin() > 0
								&& each.getMax() > each.getMin()) {
							int rnd = each.getMax() - each.getMin();
							int count = each.getMin() + _random.nextInt(rnd + 1);
							item.setCount(count);
							storeItem(pc, item);
						}
					}
					break;
				}
			}
		}

		if (item == null) {
			return false;
		} else {
			int itemId = getBoxId();

			// 魂の結晶の破片、魔族のスクロール、ブラックエントの実
			if (itemId == 40576 || itemId == 40577 || itemId == 40578
					|| itemId == 40411 || itemId == 49013) {
				pc.death(null); // キャラクターを死亡させる
			}
			// ドラゴンキー
			if (itemId == 50501) {
				for (L1PcInstance listner : L1World.getInstance().getAllPlayers()) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						if (listner.isShowTradeChat() || listner.isShowWorldChat()) {
							listner.sendPackets(new S_ServerMessage(2922));
							// 鋼鉄ギルド ドワーフ：魔術師 ゲレン様が、
							// たった今アデン大陸にドラゴンキーが現れたとおっしゃっています。
							// 選ばれしドラゴン スレイヤーに栄光と祝福を！
						}
					}
				}
			}
			return true;
		}
	}

	private static void storeItem(L1PcInstance pc, L1ItemInstance item) {
		L1Inventory inventory;

		if (pc.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
			inventory = pc.getInventory();
		} else {
			// 持てない場合は地面に落とす 処理のキャンセルはしない（不正防止）
			inventory = L1World.getInstance().getInventory(pc.getLocation());
		}
		inventory.storeItem(item);
		pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0を手に入れました。
	}
}
