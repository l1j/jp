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

package jp.l1j.server.model.inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.datatables.InnKeyTable;
import jp.l1j.server.datatables.ItemRateTable;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.datatables.RaceTicketTable;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.item.L1ItemId;
import jp.l1j.server.model.item.L1ItemRate;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1InventoryItem;
import jp.l1j.server.templates.L1Item;
import jp.l1j.server.templates.L1RaceTicket;
import jp.l1j.server.utils.IdFactory;

public class L1Inventory extends L1Object {
	private static Logger _log = Logger.getLogger(L1Inventory.class.getName());

	private static final ItemRateTable _itemRates = ItemRateTable.getInstance();
	
	private static final long serialVersionUID = 1L;

	protected List<L1ItemInstance> _items = new CopyOnWriteArrayList<L1ItemInstance>();

	public static final int MAX_AMOUNT = 2000000000; // 2G

	public static final int MAX_WEIGHT = 1500;

	public L1Inventory() {
		//
	}

	// インベントリ内のアイテムの総数
	public int getSize() {
		return _items.size();
	}

	// インベントリ内の全てのアイテム
	public List<L1ItemInstance> getItems() {
		return _items;
	}

	// インベントリ内の総重量
	public int getWeight() {
		int weight = 0;

		for (L1ItemInstance item : _items) {
			weight += item.getWeight();
		}

		return weight;
	}

	// 引数のアイテムを追加しても容量と重量が大丈夫か確認
	public static final int OK = 0;

	public static final int SIZE_OVER = 1;

	public static final int WEIGHT_OVER = 2;

	public static final int AMOUNT_OVER = 3;

	public int checkAddItem(L1ItemInstance item, int count) {
		if (item == null) {
			return -1;
		}
		if (item.getCount() <= 0 || count <= 0) {
			return -1;
		}
		if (getSize() > Config.MAX_NPC_ITEM
				|| (getSize() == Config.MAX_NPC_ITEM && (!item.isStackable() || !checkItem(item
						.getItem().getItemId())))) { // 容量確認
			return SIZE_OVER;
		}

		int weight = getWeight() + item.getItem().getWeight() * count / 1000
				+ 1;
		if (weight < 0 || (item.getItem().getWeight() * count / 1000) < 0) {
			return WEIGHT_OVER;
		}
		if (weight > (MAX_WEIGHT * Config.RATE_WEIGHT_LIMIT_PET)) { // その他の重量確認（主にサモンとペット）
			return WEIGHT_OVER;
		}

		L1ItemInstance itemExist = findItemId(item.getItemId());
		if (itemExist != null && (itemExist.getCount() + count) > MAX_AMOUNT) {
			return AMOUNT_OVER;
		}

		return OK;
	}

	// 引数のアイテムを追加しても倉庫の容量が大丈夫か確認
	public static final int WAREHOUSE_TYPE_PERSONAL = 0; // 個人倉庫

	public static final int WAREHOUSE_TYPE_CLAN = 1; // 血盟倉庫
	
	public static final int WAREHOUSE_TYPE_ADDITIONAL = 2; // 追加倉庫

	public int checkAddItemToWarehouse(L1ItemInstance item, int count, int type) {
		if (item == null) {
			return -1;
		}
		if (item.getCount() <= 0 || count <= 0) {
			return -1;
		}

		int maxSize = 100;
		if (type == WAREHOUSE_TYPE_PERSONAL) {
			maxSize = Config.MAX_PERSONAL_WAREHOUSE_ITEM;
		} else if (type == WAREHOUSE_TYPE_CLAN) {
			maxSize = Config.MAX_CLAN_WAREHOUSE_ITEM;
		} else if (type == WAREHOUSE_TYPE_ADDITIONAL) {
			maxSize = Config.MAX_ADDITIONAL_WAREHOUSE_ITEM;
		}
		if (getSize() > maxSize || (getSize() == maxSize
				&& (!item.isStackable() || !checkItem(item.getItem().getItemId())))) { // 容量確認
			return SIZE_OVER;
		}

		return OK;
	}

	// 新しいアイテムの格納
	public synchronized L1ItemInstance storeItem(int id, int count) {
		if (count <= 0) {
			return null;
		}
		L1Item temp = ItemTable.getInstance().getTemplate(id);
		if (temp == null) {
			return null;
		}

		if (temp.isStackable()) {
			L1ItemInstance item = new L1ItemInstance(temp, count);
			// 宿屋のキー Id発行
			if (findKeyId(id) == null) { // 新しく生成する必要がある場合のみIDの発行とL1Worldへの登録を行う
				item.setId(IdFactory.getInstance().nextId());
				L1World.getInstance().storeObject(item);
			}

			return storeItem(item);
		} else if (temp.isStackable()) {
			L1ItemInstance item = new L1ItemInstance(temp, count);

			if (findItemId(id) == null) { // 新しく生成する必要がある場合のみIDの発行とL1Worldへの登録を行う
				item.setId(IdFactory.getInstance().nextId());
				L1World.getInstance().storeObject(item);
			}

			return storeItem(item);
		}

		// スタックできないアイテムの場合
		L1ItemInstance result = null;
		for (int i = 0; i < count; i++) {
			L1ItemInstance item = new L1ItemInstance(temp, 1);
			item.setId(IdFactory.getInstance().nextId());
			L1World.getInstance().storeObject(item);
			storeItem(item);
			result = item;
		}
		// 最後に作ったアイテムを返す。配列を戻すようにメソッド定義を変更したほうが良いかもしれない。
		return result;
	}

	// DROP、購入、GMコマンドで入手した新しいアイテムの格納
	public synchronized L1ItemInstance storeItem(L1ItemInstance item) {
		if (item.getCount() <= 0) {
			return null;
		}
		int itemId = item.getItem().getItemId();
		if (item.isStackable()) {
			L1ItemInstance findItem = findItemId(itemId);
			if (itemId == 40309) {// レースチケット
				findItem = findItemNameId(item.getItem().getIdentifiedNameId());
			} else if (itemId == 40312) { // 宿屋のキー
				findItem = findKeyId(itemId);
			} else {
				findItem = findItemId(itemId);
			}
			if (findItem != null) {
				findItem.setCount(findItem.getCount() + item.getCount());
				updateItem(findItem);
				return findItem;
			}
		}
		/* 新規格納 */
		if (itemId == 40309) {// レースチケット
			String[] temp = item.getItem().getIdentifiedNameId().split(" ");
			temp = temp[temp.length - 1].split("-");
			L1RaceTicket ticket = new L1RaceTicket();
			ticket.setItemObjId(item.getId());
			ticket.setRound(Integer.parseInt(temp[0]));
			ticket.setAllotmentPercentage(0.0);
			ticket.setVictory(0);
			ticket.setRunnerNum(Integer.parseInt(temp[1]));
			RaceTicketTable.getInstance().storeNewTiket(ticket);
		}
		item.setX(getX());
		item.setY(getY());
		item.setMap(getMapId());
		int chargeCount = item.getItem().getMaxChargeCount();
		if (itemId == 40006 || itemId == 40007 || itemId == 40008
				|| itemId == 140006 || itemId == 140008 || itemId == 41401) {
			RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
			chargeCount = Math.max( chargeCount - random.nextInt(5), 1 );
		}
		if (itemId == 20383) {
			chargeCount = 50;
		}
		item.setChargeCount(chargeCount);
		item.setChargeTime(item.getItem().getChargeTime());
		if (item.getItem().getExpirationTime() > 0) {
			item.setExpirationTime(new Timestamp(System.currentTimeMillis()
					+ item.getItem().getExpirationTime() * 1000));
		}
		// 宿屋のキー記録
		if (item.getItem().getItemId() == 40312) {
			if (!InnKeyTable.hasKey(item)) {
				InnKeyTable.storeKey(item);
			}
		}
		_items.add(item);
		insertItem(item);
		return item;
	}

	// /trade、倉庫から入手したアイテムの格納
	public synchronized L1ItemInstance storeTradeItem(L1ItemInstance item) {
		if (item.getItem().getItemId() == 40312) { // 宿屋のキー
			L1ItemInstance findItem = findKeyId(item.getId()); // 宿屋のキー Id照合
			if (findItem != null) {
				findItem.setCount(findItem.getCount() + item.getCount());
				updateItem(findItem);
				return findItem;
			}
		} else if (item.isStackable()) {
			L1ItemInstance findItem = findItemId(item.getItem().getItemId());
			if (findItem != null) {
				findItem.setCount(findItem.getCount() + item.getCount());
				updateItem(findItem);
				item.delete();
				findItem.save();
				return findItem;
			}
		}
		item.setX(getX());
		item.setY(getY());
		item.setMap(getMapId());
		// 宿屋のキー記録
		if (item.getItem().getItemId() == 40312) {
			if (!InnKeyTable.hasKey(item)) {
				InnKeyTable.storeKey(item);
			}
		}
		_items.add(item);
		insertItem(item);
		return item;
	}

	/**
	 * インベントリから指定されたアイテムIDのアイテムを削除する。L1ItemInstanceへの参照
	 * がある場合はremoveItemの方を使用するのがよい。 （こちらは矢とか魔石とか特定のアイテムを消費させるときに使う）
	 * 
	 * @param itemId
	 *            - 削除するアイテムのitemId(objidではない)
	 * @param count
	 *            - 削除する個数
	 * @return 実際に削除された場合はtrueを返す。
	 */
	public boolean consumeItem(int itemId, int count) {
		if (count <= 0) {
			_log.log(Level.INFO, "Count <= 0", new IllegalArgumentException());
			return false;
		}
		if (ItemTable.getInstance().getTemplate(itemId).isStackable()) {
			L1ItemInstance item = findItemId(itemId);
			if (item != null && item.getCount() >= count) {
				removeItem(item, count);
				return true;
			}
		} else {
			L1ItemInstance[] itemList = findItemsId(itemId);
			if (itemList.length == count) {
				for (int i = 0; i < count; i++) {
					removeItem(itemList[i], 1);
				}
				return true;
			} else if (itemList.length > count) { // 指定個数より多く所持している場合
				Arrays.sort(itemList, new EnchantLevelComparator()); // エンチャント順にソートし、エンチャント数の少ないものから消費させる
				for (int i = 0; i < count; i++) {
					removeItem(itemList[i], 1);
				}
				return true;
			}
		}
		return false;
	}

	private static class EnchantLevelComparator implements
			Comparator<L1ItemInstance> {
		public int compare(L1ItemInstance item1, L1ItemInstance item2) {
			return item1.getEnchantLevel() - item2.getEnchantLevel();
		}
	}

	// 指定したアイテムから指定個数を削除（使ったりゴミ箱に捨てられたとき）戻り値：実際に削除した数
	public int removeItem(int objectId, int count) {
		L1ItemInstance item = getItem(objectId);
		return removeItem(item, count);
	}

	public int removeItem(L1ItemInstance item) {
		return removeItem(item, item.getCount());
	}

	public int removeItem(L1ItemInstance item, int count) {
		if (item == null) {
			_log.log(Level.INFO, "Item is null", new IllegalArgumentException());
			return 0;
		}
		if (item.getCount() <= 0 || count <= 0) {
			_log.log(Level.INFO, "Invalid item count", new IllegalArgumentException());
			return 0;
		}
		if (item.getCount() < count) {
			count = item.getCount();
			_log.log(Level.INFO, "Invalid item count", new IllegalArgumentException());
		}

		if (item.getCount() == count) {
			deleteItem(item);
			L1World.getInstance().removeObject(item);
		} else {
			item.setCount(item.getCount() - count);
			updateItem(item);
		}
		return count;
	}

	
	// ゴミ箱に捨てられたアイテムをアデナに換金（価格設定されていないアイテムは削除）
	public int recycleItem(L1PcInstance pc, L1ItemInstance item) {
		int count = 0;
		
		if (item == null) {
			_log.log(Level.INFO, "Item is null", new IllegalArgumentException());
			return count;
		}
		if (item.getCount() <= 0 || item.getCount() <= 0) {
			_log.log(Level.INFO, "Invalid item count", new IllegalArgumentException());
			return count;
		}

		L1ItemRate rate = _itemRates.get(item.getItemId());
		if (rate != null && rate.getPurchasingPrice() > 0) {
			count = (int) (rate.getPurchasingPrice() * item.getCount());
			
			if (count > MAX_AMOUNT) {
				count = MAX_AMOUNT;
			}
			
			L1ItemInstance adena = ItemTable.getInstance().createItem(L1ItemId.ADENA);
			adena.setCount(count);
			
			if (checkAddItem(adena, count) == OK) {
				storeItem(adena);
				pc.sendPackets(new S_ServerMessage(143, "ゴミ箱", adena.getLogName()));
				// f1%0が%1をくれました。
			} else {
				L1Inventory ground = L1World.getInstance().getInventory(
						pc.getX(), pc.getY(), pc.getMapId());
				tradeItem(adena, count, ground);
				// 持てないので足元に落とす
			}
		}
		
		deleteItem(item);
		L1World.getInstance().removeObject(item);
		
		return count;
	}

	// _itemsから指定オブジェクトを削除(L1PcInstance、L1DwarfInstance、L1GroundInstanceでこの部分をオーバライドする)
	public void deleteItem(L1ItemInstance item) {
		onRemoveItem(item);
		_items.remove(item);
		item.delete();
	}

	// 引数のインベントリにアイテムを移譲
	public synchronized L1ItemInstance tradeItem(int objectId, int count,
			L1Inventory inventory) {
		L1ItemInstance item = getItem(objectId);
		return tradeItem(item, count, inventory);
	}

	public synchronized L1ItemInstance tradeItem(L1ItemInstance item,
			int count, L1Inventory inventory) {
		if (item == null) {
			return null;
		}
		if (item.getCount() <= 0 || count <= 0) {
			return null;
		}
		if (item.isEquipped()) {
			return null;
		}
		if (!checkItem(item.getItem().getItemId(), count)) {
			return null;
		}
		L1ItemInstance carryItem;
		if (item.getCount() <= count) {
			onRemoveItem(item);
			_items.remove(item);
			carryItem = item;
		} else {
			item.setCount(item.getCount() - count);
			updateItem(item);
			carryItem = ItemTable.getInstance().createItem(
					item.getItem().getItemId());
			carryItem.setCount(count);
			carryItem.setEnchantLevel(item.getEnchantLevel());
			carryItem.setIdentified(item.isIdentified());
			carryItem.setDurability(item.getDurability());
			carryItem.setChargeCount(item.getChargeCount());
			carryItem.setChargeTime(item.getChargeTime());
			carryItem.setLastUsed(item.getLastUsed());
			// 宿屋関連
			if (carryItem.getItem().getItemId() == 40312) {
				carryItem.setInnNpcId(item.getInnNpcId()); // 宿屋NPC
				carryItem.setHall(item.checkRoomOrHall()); // 宿屋小部屋or大部屋
				carryItem.setDueTime(item.getDueTime()); // 利用時間
			}
		}
		return inventory.storeTradeItem(carryItem);
	}

	/*
	 * アイテムを損傷・損耗させる（武器・防具も含む） アイテムの場合、損耗なのでマイナスするが 武器・防具は損傷度を表すのでプラスにする。
	 */
	public L1ItemInstance receiveDamage(L1ItemInstance item, int count) {
		int itemType = item.getItem().getType2();
		int currentDurability = item.getDurability();

		// if (item == null) {
		// return null;
		// }

		if ((currentDurability == 0 && itemType == 0) || currentDurability < 0) {
			item.setDurability(0);
			return null;
		}

		// 武器・防具のみ損傷度をプラス
		if (itemType == 0) {
			int minDurability = (item.getEnchantLevel() + 5) * -1;
			int durability = currentDurability - count;
			if (durability < minDurability) {
				durability = minDurability;
			}
			if (currentDurability > durability) {
				item.setDurability(durability);
			}
		} else {
			int maxDurability = item.getEnchantLevel() + 5;
			int durability = currentDurability + count;
			if (durability > maxDurability) {
				durability = maxDurability;
			}
			if (currentDurability < durability) {
				item.setDurability(durability);
			}
		}

		updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	public L1ItemInstance recoveryDamage(L1ItemInstance item) {
		int itemType = item.getItem().getType2();
		int durability = item.getDurability();

		if ((durability == 0 && itemType != 0) || durability < 0) {
			item.setDurability(0);
			return null;
		}

		if (itemType == 0) {
			// 耐久度をプラスしている。
			item.setDurability(durability + 1);
		} else {
			// 損傷度をマイナスしている。
			item.setDurability(durability - 1);
		}

		updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	// アイテムＩＤから検索
	public L1ItemInstance findItemId(int id) {
		for (L1ItemInstance item : _items) {
			if (item.getItem().getItemId() == id) {
				return item;
			}
		}
		return null;
	}

	public L1ItemInstance findKeyId(int id) {
		for (L1ItemInstance item : _items) {
			if (item.getId() == id) {
				return item;
			}
		}
		return null;
	}

	public L1ItemInstance[] findItemsId(int id) {
		ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (L1ItemInstance item : _items) {
			if (item.getItemId() == id) {
				itemList.add(item);
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	public L1ItemInstance[] findItemsIdNotEquipped(int id) {
		ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (L1ItemInstance item : _items) {
			if (item.getItemId() == id) {
				if (!item.isEquipped()) {
					itemList.add(item);
				}
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	// オブジェクトＩＤから検索
	public L1ItemInstance getItem(int objectId) {
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getId() == objectId) {
				return item;
			}
		}
		return null;
	}

	// 特定のアイテムを指定された個数以上所持しているか確認（矢とか魔石の確認）
	public boolean checkItem(int id) {
		return checkItem(id, 1);
	}

	public boolean checkItem(int id, int count) {
		if (count == 0) {
			return true;
		}
		if (ItemTable.getInstance().getTemplate(id).isStackable()) {
			L1ItemInstance item = findItemId(id);
			if (item != null && item.getCount() >= count) {
				return true;
			}
		} else {
			Object[] itemList = findItemsId(id);
			if (itemList.length >= count) {
				return true;
			}
		}
		return false;
	}

	// 強化された特定のアイテムを指定された個数以上所持しているか確認
	// 装備中のアイテムは所持していないと判別する
	public boolean checkEnchantItem(int id, int enchant, int count) {
		int num = 0;
		for (L1ItemInstance item : _items) {
			if (item.isEquipped()) { // 装備しているものは該当しない
				continue;
			}
			if (item.getItemId() == id && item.getEnchantLevel() == enchant) {
				num++;
				if (num == count) {
					return true;
				}
			}
		}
		return false;
	}

	// 強化された特定のアイテムを消費する
	// 装備中のアイテムは所持していないと判別する
	public boolean consumeEnchantItem(int id, int enchant, int count) {
		for (L1ItemInstance item : _items) {
			if (item.isEquipped()) { // 装備しているものは該当しない
				continue;
			}
			if (item.getItemId() == id && item.getEnchantLevel() == enchant) {
				removeItem(item);
				return true;
			}
		}
		return false;
	}

	// 特定のアイテムを指定された個数以上所持しているか確認
	// 装備中のアイテムは所持していないと判別する
	public boolean checkItemNotEquipped(int id, int count) {
		if (count == 0) {
			return true;
		}
		return count <= countItems(id);
	}

	// 特定のアイテムを全て必要な個数所持しているか確認（イベントとかで複数のアイテムを所持しているか確認するため）
	public boolean checkItem(int[] ids) {
		int len = ids.length;
		int[] counts = new int[len];
		for (int i = 0; i < len; i++) {
			counts[i] = 1;
		}
		return checkItem(ids, counts);
	}

	public boolean checkItem(int[] ids, int[] counts) {
		for (int i = 0; i < ids.length; i++) {
			if (!checkItem(ids[i], counts[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * このインベントリ内にある、指定されたIDのアイテムの数を数える。
	 * 
	 * @return
	 */
	public int countItems(int id) {
		if (ItemTable.getInstance().getTemplate(id).isStackable()) {
			L1ItemInstance item = findItemId(id);
			if (item != null) {
				return item.getCount();
			}
		} else {
			L1ItemInstance[] itemList = findItemsIdNotEquipped(id);
			return itemList.length;
		}
		return 0;
	}

	public void shuffle() {
		Collections.shuffle(_items);
	}

	// インベントリ内の全てのアイテムを消す（所有者を消すときなど）
	public void clearItems() {
		for (L1ItemInstance item : _items) {
			L1World.getInstance().removeObject(item);
			item.save();
		}
		_items.clear();
	}

	/**
	 * スタック可能なアイテムリストからnameIdと同じ値を持つitemを返す
	 * 
	 * @param nameId
	 * @return item nullならば見つからなかった。
	 */
	public L1ItemInstance findItemNameId(String nameId) {
		for (L1ItemInstance item : _items) {
			if (nameId.equals(item.getItem().getIdentifiedNameId())) {
				return item;
			}
		}
		return null;
	}

	/**
	 * エンチャント済みのアイテムを供給
	 * @param id
	 * @param enchant
	 * @param count
	 * @return
	 */
	public final boolean getEnchantItem(L1PcInstance pc, final int id, final int enchant, final int count) {

		L1Item temp = ItemTable.getInstance().getTemplate(id);
		int isId = 0;

		if (temp != null) {
			if (temp.isStackable()) {
				L1ItemInstance item = ItemTable.getInstance().createItem(
						id);
				item.setEnchantLevel(0);
				item.setCount(count);
				if (isId == 1) {
					item.setIdentified(true);
				}
				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
					pc.getInventory().storeItem(item);
					pc.sendPackets(new S_ServerMessage(403, // %0を手に入れました。
							item.getLogName()));
				}
			} else {
				L1ItemInstance item = null;
				int createCount;
				for (createCount = 0; createCount < count; createCount++) {
					item = ItemTable.getInstance().createItem(id);
					item.setEnchantLevel(enchant);
					if (isId == 1) {
						item.setIdentified(true);
					}
					if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
						pc.getInventory().storeItem(item);
						if (item.getItem().getType2() == 1
								|| item.getItem().getType2() == 2) {
							item.setIsHaste(item.getItem().isHaste());
							if (item.getItem().getType2() == 1) {
								item.setCanBeDmg(item.getItem().getCanbeDmg());
							}
							item.save();
						}
					} else {
						break;
					}
				}
				if (createCount > 0) {
					pc.sendPackets(new S_ServerMessage(403, // %0を手に入れました。
							item.getLogName()));
				}
			}
		}
		return false;
	}

	// オーバーライド用
	public void loadItems() {
	}

	public void insertItem(L1ItemInstance item) {
		item.setOwner(0, L1InventoryItem.LOC_NONE);
		item.save();
	}

	public void updateItem(L1ItemInstance item) {
	}

	public void updateItem(L1ItemInstance item, int colmn) {
	}

	public void onRemoveItem(L1ItemInstance item) {
	}

	public int getOwnerLocation() {
		return L1InventoryItem.LOC_NONE;
	}

	/**
	 *  NpcInstanceでのチェック用
	 * @param id
	 * @return
	 */
	public boolean checkEquipped(int id) {
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getItem().getItemId() == id && item.isEquipped()) {
				return true;
			}
		}
		return false;
	}
}