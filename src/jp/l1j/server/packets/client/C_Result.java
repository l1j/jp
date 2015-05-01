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

package jp.l1j.server.packets.client;

import java.util.ArrayList;
import java.util.logging.Logger;
import jp.l1j.server.ClientThread;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.PetTable;
import jp.l1j.server.datatables.ShopTable;
import jp.l1j.server.model.instance.L1DollInstance;
import jp.l1j.server.model.instance.L1FurnitureInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.item.L1ItemId;
import jp.l1j.server.model.shop.L1Shop;
import jp.l1j.server.model.shop.L1ShopBuyOrderList;
import jp.l1j.server.model.shop.L1ShopSellOrderList;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.templates.L1Pet;
import jp.l1j.server.templates.L1PrivateShopBuyList;
import jp.l1j.server.templates.L1PrivateShopSellList;

public class C_Result extends ClientBasePacket {

	private static Logger _log = Logger.getLogger(C_Result.class.getName());
	private static final String C_RESULT = "[C] C_Result";

	public C_Result(byte abyte0[], ClientThread clientthread) throws Exception {
		super(abyte0);
		int npcObjectId = readD();
		int resultType = readC();
		int size = readC();
		int unknown = readC();

		L1PcInstance pc = clientthread.getActiveChar();
		int level = pc.getLevel();

		int npcId = 0;
		String npcImpl = "";
		boolean isPrivateShop = false;
		boolean tradable = true;
		L1Object findObject = L1World.getInstance().findObject(npcObjectId);
		if (findObject != null) {
			int diffLocX = Math.abs(pc.getX() - findObject.getX());
			int diffLocY = Math.abs(pc.getY() - findObject.getY());
			// 3マス以上離れた場合アクション無効
			if (diffLocX > 3 || diffLocY > 3) {
				return;
			}
			if (findObject instanceof L1NpcInstance) {
				L1NpcInstance targetNpc = (L1NpcInstance) findObject;
				npcId = targetNpc.getNpcTemplate().getNpcId();
				npcImpl = targetNpc.getNpcTemplate().getImpl();
			} else if (findObject instanceof L1PcInstance) {
				isPrivateShop = true;
			}
		}

		if (resultType == 0 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Merchant")) { // アイテム購入
			L1Shop shop = ShopTable.getInstance().get(npcId);
			L1ShopBuyOrderList orderList = shop.newBuyOrderList();
			for (int i = 0; i < size; i++) {
				orderList.add(readD(), readD());
			}
			shop.sellItems(pc, orderList);
		} else if (resultType == 1 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Merchant")) { // アイテム売却
			L1Shop shop = ShopTable.getInstance().get(npcId);
			L1ShopSellOrderList orderList = shop.newSellOrderList(pc);
			for (int i = 0; i < size; i++) {
				orderList.add(readD(), readD());
			}
			shop.buyItems(orderList);
		} else if (resultType == 2 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) { // 自分の倉庫に格納
			int objectId, count;
			for (int i = 0; i < size; i++) {
				tradable = true;
				objectId = readD();
				count = readD();
				L1Object object = pc.getInventory().getItem(objectId);
				L1ItemInstance item = (L1ItemInstance) object;
				if (!item.getItem().isTradable()) {
					tradable = false;
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							.getName())); // \f1%0は捨てたりまたは他人に讓ることができません。
				}
				Object[] petlist = pc.getPetList().values().toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							tradable = false;
							// \f1%0は捨てたりまたは他人に讓ることができません。
							pc.sendPackets(new S_ServerMessage(210, item
									.getItem().getName()));
							break;
						}
					}
				}
				Object[] dolllist = pc.getDollList().values().toArray();
				for (Object dollObject : dolllist) {
					if (dollObject instanceof L1DollInstance) {
						L1DollInstance doll = (L1DollInstance) dollObject;
						if (item.getId() == doll.getItemObjId()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(1181)); // 該当のマジックドールは現在使用中です。
							break;
						}
					}
				}
				if (pc.getWarehouseInventory().checkAddItemToWarehouse(item,
						count, L1Inventory.WAREHOUSE_TYPE_PERSONAL) == L1Inventory.SIZE_OVER) {
					pc.sendPackets(new S_ServerMessage(75)); // \f1これ以上ものを置く場所がありません。
					break;
				}
				if (tradable) {
					pc.getInventory().tradeItem(objectId, count, pc.getWarehouseInventory());
					pc.updateLight();
				}
			}
			pc.saveInventory();
		} else if (resultType == 3 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) { // 自分の倉庫から取り出し
			int objectId, count;
			L1ItemInstance item;
			for (int i = 0; i < size; i++) {
				objectId = readD();
				count = readD();
				item = pc.getWarehouseInventory().getItem(objectId);
				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) // 容量重量確認及びメッセージ送信
				{
					if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
						pc.getWarehouseInventory().tradeItem(item, count,
								pc.getInventory());
					} else {
						pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足しています。
						break;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(270)); // \f1持っているものが重くて取引できません。
					break;
				}
			}
		} else if (resultType == 17 && size != 0
				&& npcImpl.equalsIgnoreCase("L1AddWarehouse")) { // 追加倉庫に格納
			int objectId, count;
			for (int i = 0; i < size; i++) {
				tradable = true;
				objectId = readD();
				count = readD();
				L1Object object = pc.getInventory().getItem(objectId);
				L1ItemInstance item = (L1ItemInstance) object;
				int itemId = item.getItemId();
				if (itemId == 40314 || itemId == 40316 // ペットのアミュレット
						|| itemId == 49016 || itemId == 49017 // 便箋(使用済)
						|| itemId == 49018 || itemId == 49019 // 血盟便箋(使用済)
						|| itemId == 49020 || itemId == 49021 // クリスマスカード(使用済)
						|| itemId == 49022 || itemId == 49023 // バレンタインカード(使用済)
						|| itemId == 49024 || itemId == 49025 ) { // ホワイトデーカード(使用済)
					tradable = false;
					pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
					// \f1%0は捨てたりまたは他人に讓ることができません。
				}
				int itemObjectId = item.getId();
				for (L1Object l1object : L1World.getInstance().getObject()) {
					if (l1object instanceof L1FurnitureInstance) {
						L1FurnitureInstance furniture = (L1FurnitureInstance) l1object;
						if (furniture.getItemObjId() == itemObjectId) { // 既に引き出している家具
							tradable = false;
							pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
							// \f1%0は捨てたりまたは他人に讓ることができません。
							break;
						}
					}
				}
				Object[] dolllist = pc.getDollList().values().toArray();
				for (Object dollObject : dolllist) {
					if (dollObject instanceof L1DollInstance) {
						L1DollInstance doll = (L1DollInstance) dollObject;
						if (item.getId() == doll.getItemObjId()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(1181)); // 該当のマジックドールは現在使用中です。
							break;
						}
					}
				}
				if (pc.getAdditionalWarehouseInventory().checkAddItemToWarehouse(item,
						count, L1Inventory.WAREHOUSE_TYPE_ADDITIONAL) == L1Inventory.SIZE_OVER) {
					pc.sendPackets(new S_ServerMessage(75)); // \f1これ以上ものを置く場所がありません。
					tradable = false;
					break;
				}
				if (tradable) {
					pc.getInventory().tradeItem(objectId, count, pc.getAdditionalWarehouseInventory());
					pc.updateLight();
				}
			}
			pc.saveInventory();
		} else if (resultType == 3 && size != 0
				&& npcImpl.equalsIgnoreCase("L1AddWarehouse")) { // 追加倉庫から取り出し
			int objectId, count;
			L1ItemInstance item;
			for (int i = 0; i < size; i++) {
				objectId = readD();
				count = readD();
				item = pc.getAdditionalWarehouseInventory().getItem(objectId);
				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) // 容量重量確認及びメッセージ送信
				{
					if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
						pc.getAdditionalWarehouseInventory().tradeItem(item, count,
								pc.getInventory());
					} else {
						pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足しています。
						break;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(270)); // \f1持っているものが重くて取引できません。
					break;
				}
			}
		} else if (resultType == 4 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) { // クラン倉庫に格納
			int objectId, count;
			if (pc.getClanId() != 0) { // クラン所属
				for (int i = 0; i < size; i++) {
					tradable = true;
					objectId = readD();
					count = readD();
					L1Clan clan = L1World.getInstance().getClan(
							pc.getClanName());
					L1Object object = pc.getInventory().getItem(objectId);
					L1ItemInstance item = (L1ItemInstance) object;
					if (clan != null) {
						if (!item.getItem().isTradable()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(210, item
									.getItem().getName())); // \f1%0は捨てたりまたは他人に讓ることができません。
						}
						if (item.isSealed()) { // 封印された装備
							tradable = false;
							pc.sendPackets(new S_ServerMessage(210, item
									.getItem().getName())); // \f1%0は捨てたりまたは他人に讓ることができません。
						}
						Object[] petlist = pc.getPetList().values().toArray();
						for (Object petObject : petlist) {
							if (petObject instanceof L1PetInstance) {
								L1PetInstance pet = (L1PetInstance) petObject;
								if (item.getId() == pet.getItemObjId()) {
									tradable = false;
									// \f1%0は捨てたりまたは他人に讓ることができません。
									pc.sendPackets(new S_ServerMessage(210,
											item.getItem().getName()));
									break;
								}
							}
						}
						Object[] dolllist = pc.getDollList().values().toArray();
						for (Object dollObject : dolllist) {
							if (dollObject instanceof L1DollInstance) {
								L1DollInstance doll = (L1DollInstance) dollObject;
								if (item.getId() == doll.getItemObjId()) {
									tradable = false;
									pc.sendPackets(new S_ServerMessage(1181)); // 該当のマジックドールは現在使用中です。
									break;
								}
							}
						}
						if (clan.getWarehouse().checkAddItemToWarehouse(item,
								count, L1Inventory.WAREHOUSE_TYPE_CLAN) == L1Inventory.SIZE_OVER) {
							pc.sendPackets(new S_ServerMessage(75)); // \f1これ以上ものを置く場所がありません。
							break;
						}
						if (tradable) {
							pc.getInventory().tradeItem(objectId, count,
									clan.getWarehouse());
							clan.getWarehouse().writeHistory(pc, item, count, 0); // クラン倉庫使用履歴(預入)
							pc.updateLight();
						}
					}
				}

				//
				pc.saveInventory();
			} else {
				pc.sendPackets(new S_ServerMessage(208)); // \f1血盟倉庫を使用するには血盟に加入していなくてはなりません。
			}
		} else if (resultType == 5 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) { // クラン倉庫から取り出し
			int objectId, count;
			L1ItemInstance item;

			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				for (int i = 0; i < size; i++) {
					objectId = readD();
					count = readD();
					item = clan.getWarehouse().getItem(objectId);
					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認及びメッセージ送信
						if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
							clan.getWarehouse().tradeItem(item, count,
									pc.getInventory());
							clan.getWarehouse().writeHistory(pc, item, count, 1); // クラン倉庫使用履歴(受取)
						} else {
							pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足しています。
							break;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(270)); // \f1持っているものが重くて取引できません。
						break;
					}
				}
				clan.setWarehouseUsingChar(0); // クラン倉庫のロックを解除
			}
		} else if (resultType == 5 && size == 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf")) { // クラン倉庫から取り出し中にCancel、または、ESCキー
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				clan.setWarehouseUsingChar(0); // クラン倉庫のロックを解除
			}
		} else if (resultType == 8 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5
				&& pc.isElf()) { // 自分のエルフ倉庫に格納
			int objectId, count;
			for (int i = 0; i < size; i++) {
				tradable = true;
				objectId = readD();
				count = readD();
				L1Object object = pc.getInventory().getItem(objectId);
				L1ItemInstance item = (L1ItemInstance) object;
				if (!item.getItem().isTradable()) {
					tradable = false;
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							.getName())); // \f1%0は捨てたりまたは他人に讓ることができません。
				}
				Object[] petlist = pc.getPetList().values().toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							tradable = false;
							// \f1%0は捨てたりまたは他人に讓ることができません。
							pc.sendPackets(new S_ServerMessage(210, item
									.getItem().getName()));
							break;
						}
					}
				}
				Object[] dolllist = pc.getDollList().values().toArray();
				for (Object dollObject : dolllist) {
					if (dollObject instanceof L1DollInstance) {
						L1DollInstance doll = (L1DollInstance) dollObject;
						if (item.getId() == doll.getItemObjId()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(1181)); // 該当のマジックドールは現在使用中です。
							break;
						}
					}
				}
				if (pc.getElfWarehouseInventory().checkAddItemToWarehouse(item,
						count, L1Inventory.WAREHOUSE_TYPE_PERSONAL) == L1Inventory.SIZE_OVER) {
					pc.sendPackets(new S_ServerMessage(75)); // \f1これ以上ものを置く場所がありません。
					break;
				}
				if (tradable) {
					pc.getInventory().tradeItem(objectId, count,
							pc.getElfWarehouseInventory());
					pc.updateLight();
				}
			}

			//
			pc.saveInventory();
		} else if (resultType == 9 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5
				&& pc.isElf()) { // 自分のエルフ倉庫から取り出し
			int objectId, count;
			L1ItemInstance item;
			for (int i = 0; i < size; i++) {
				objectId = readD();
				count = readD();
				item = pc.getElfWarehouseInventory().getItem(objectId);
				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認及びメッセージ送信
					if (pc.getInventory().consumeItem(40494, 2)) { // ミスリル
						pc.getElfWarehouseInventory().tradeItem(item, count,
								pc.getInventory());
					} else {
						pc.sendPackets(new S_ServerMessage(337, "$767")); // \f1%0が不足しています。
						break;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(270)); // \f1持っているものが重くて取引できません。
					break;
				}
			}
		} else if (resultType == 0 && size != 0 && isPrivateShop) { // 個人商店からアイテム購入
			int order;
			int count;
			int price;
			ArrayList<L1PrivateShopSellList> sellList;
			L1PrivateShopSellList pssl;
			int itemObjectId;
			int sellPrice;
			int sellTotalCount;
			int sellCount;
			L1ItemInstance item;
			boolean[] isRemoveFromList = new boolean[8];

			L1PcInstance targetPc = null;
			if (findObject instanceof L1PcInstance) {
				targetPc = (L1PcInstance) findObject;
				if (targetPc == null) {
					return;
				}
			}
			if (targetPc.isTradingInPrivateShop()) {
				return;
			}
			sellList = targetPc.getSellList();
			synchronized (sellList) {
				// 売り切れが発生し、閲覧中のアイテム数とリスト数が異なる
				if (pc.getPartnersPrivateShopItemCount() != sellList.size()) {
					return;
				}
				targetPc.setTradingInPrivateShop(true);

				for (int i = 0; i < size; i++) { // 購入予定の商品
					order = readD();
					count = readD();
					pssl = (L1PrivateShopSellList) sellList.get(order);
					itemObjectId = pssl.getItemObjectId();
					sellPrice = pssl.getSellPrice();
					sellTotalCount = pssl.getSellTotalCount(); // 売る予定の個数
					sellCount = pssl.getSellCount(); // 売った累計
					item = targetPc.getInventory().getItem(itemObjectId);
					if (item == null) {
						continue;
					}
					if (count > sellTotalCount - sellCount) {
						count = sellTotalCount - sellCount;
					}
					if (count == 0) {
						continue;
					}

					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認及びメッセージ送信
						for (int j = 0; j < count; j++) { // オーバーフローをチェック
							if (sellPrice * j > 2000000000) {
								pc.sendPackets(new S_ServerMessage(904, // 総販売価格は%dアデナを超過できません。
										"2000000000"));
								targetPc.setTradingInPrivateShop(false);
								return;
							}
						}
						price = count * sellPrice;
						if (pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
							L1ItemInstance adena = pc.getInventory()
									.findItemId(L1ItemId.ADENA);
							if (targetPc != null && adena != null) {
								if (targetPc.getInventory().tradeItem(item,
										count, pc.getInventory()) == null) {
									targetPc.setTradingInPrivateShop(false);
									return;
								}
								pc.getInventory().tradeItem(adena, price,
										targetPc.getInventory());
								String message = item.getItem().getName()
										+ " (" + String.valueOf(count) + ")";
								targetPc.sendPackets(new S_ServerMessage(877, // %1%o
										// %0に販売しました。
										pc.getName(), message));
								pssl.setSellCount(count + sellCount);
								sellList.set(order, pssl);
								if (pssl.getSellCount() == pssl
										.getSellTotalCount()) { // 売る予定の個数を売った
									isRemoveFromList[order] = true;
								}
							}
						} else {
							pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足しています。
							break;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(270)); // \f1持っているものが重くて取引できません。
						break;
					}
				}
				// 売り切れたアイテムをリストの末尾から削除
				for (int i = 7; i >= 0; i--) {
					if (isRemoveFromList[i]) {
						sellList.remove(i);
					}
				}
				targetPc.setTradingInPrivateShop(false);
			}
		} else if (resultType == 1 && size != 0 && isPrivateShop) { // 個人商店にアイテム売却
			int count;
			int order;
			ArrayList<L1PrivateShopBuyList> buyList;
			L1PrivateShopBuyList psbl;
			int itemObjectId;
			L1ItemInstance item;
			int buyPrice;
			int buyTotalCount;
			int buyCount;
			L1ItemInstance targetItem;
			boolean[] isRemoveFromList = new boolean[8];

			L1PcInstance targetPc = null;
			if (findObject instanceof L1PcInstance) {
				targetPc = (L1PcInstance) findObject;
				if (targetPc == null) {
					return;
				}
			}
			if (targetPc.isTradingInPrivateShop()) {
				return;
			}
			targetPc.setTradingInPrivateShop(true);
			buyList = targetPc.getBuyList();

			for (int i = 0; i < size; i++) {
				itemObjectId = readD();
				count = readCH();
				order = readC();
				item = pc.getInventory().getItem(itemObjectId);
				if (item == null) {
					continue;
				}
				psbl = (L1PrivateShopBuyList) buyList.get(order);
				buyPrice = psbl.getBuyPrice();
				buyTotalCount = psbl.getBuyTotalCount(); // 買う予定の個数
				buyCount = psbl.getBuyCount(); // 買った累計
				if (count > buyTotalCount - buyCount) {
					count = buyTotalCount - buyCount;
				}
				if (item.isEquipped()) {
					pc.sendPackets(new S_ServerMessage(905)); // 装備しているアイテムは販売できません。
					continue;
				}

				if (targetPc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認及びメッセージ送信
					for (int j = 0; j < count; j++) { // オーバーフローをチェック
						if (buyPrice * j > 2000000000) {
							targetPc.sendPackets(new S_ServerMessage(904, // 総販売価格は%dアデナを超過できません。
									"2000000000"));
							return;
						}
					}
					if (targetPc.getInventory().checkItem(L1ItemId.ADENA,
							count * buyPrice)) {
						L1ItemInstance adena = targetPc.getInventory()
								.findItemId(L1ItemId.ADENA);
						if (adena != null) {
							targetPc.getInventory().tradeItem(adena,
									count * buyPrice, pc.getInventory());
							pc.getInventory().tradeItem(item, count,
									targetPc.getInventory());
							psbl.setBuyCount(count + buyCount);
							buyList.set(order, psbl);
							if (psbl.getBuyCount() == psbl.getBuyTotalCount()) { // 買う予定の個数を買った
								isRemoveFromList[order] = true;
							}
						}
					} else {
						targetPc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足しています。
						break;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(271)); // \f1相手が物を持ちすぎていて取引できません。
					break;
				}
			}
			// 買い切ったアイテムをリストの末尾から削除
			for (int i = 7; i >= 0; i--) {
				if (isRemoveFromList[i]) {
					buyList.remove(i);
				}
			}
			targetPc.setTradingInPrivateShop(false);
		} else if ((resultType == 12) && (size != 0)
				&& npcImpl.equalsIgnoreCase("L1Merchant")) { // ペットの引き出し
			int petCost, petCount, divisor, itemObjectId, itemCount = 0;

			for (int i = 0; i < size; i++) {
				petCost = 0;
				petCount = 0;
				divisor = 6;
				itemObjectId = readD();
				itemCount = readD();

				if (itemCount == 0) {
					continue;
				}
				for (L1NpcInstance petNpc : pc.getPetList().values()) 
					petCost += petNpc.getPetcost();

				int charisma = pc.getCha();
				if (pc.isCrown()) { // 君主
					charisma += 6;
				} else if (pc.isElf()) { // エルフ
					charisma += 12;
				} else if (pc.isWizard()) { // WIZ
					charisma += 6;
				} else if (pc.isDarkelf()) { // DE
					charisma += 6;
				} else if (pc.isDragonKnight()) { // ドラゴンナイト
					charisma += 6;
				} else if (pc.isIllusionist()) { // イリュージョニスト
					charisma += 6;
				}

				L1Pet l1pet = PetTable.getInstance().getTemplate(itemObjectId);
				if (l1pet != null) {
					npcId = l1pet.getNpcId();
					charisma -= petCost;
					if ((npcId == 45313) || (npcId == 45710 // タイガー、バトルタイガー
							) || (npcId == 45711) || (npcId == 45712)) { // 紀州犬の子犬、紀州犬
						divisor = 12;
					} else {
						divisor = 6;
					}
					petCount = charisma / divisor;
					if (petCount <= 0) {
						pc.sendPackets(new S_ServerMessage(489)); // 引き取ろうとするペットが多すぎます。
						return;
					}
					L1Npc npcTemp = NpcTable.getInstance().getTemplate(npcId);
					L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
					pet.setPetcost(divisor);
				}
			}
		}
	}

	@Override
	public String getType() {
		return C_RESULT;
	}

}