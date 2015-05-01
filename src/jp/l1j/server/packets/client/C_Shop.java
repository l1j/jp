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
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.ClientThread;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_DoActionShop;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1PrivateShopBuyList;
import jp.l1j.server.templates.L1PrivateShopSellList;

public class C_Shop extends ClientBasePacket {
	private static final String C_SHOP = "[C] C_Shop";
	
	private static Logger _log = Logger.getLogger(C_Shop.class.getName());

	public C_Shop(byte abyte0[], ClientThread clientthread) {
		super(abyte0);
		L1PcInstance pc = clientthread.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
		int mapId = pc.getMapId();
		if (mapId != 340 && mapId != 350 && mapId != 360 && mapId != 370) {
			pc.sendPackets(new S_ServerMessage(876)); // この場所では個人商店を開けません。
			return;
		}
		ArrayList<L1PrivateShopSellList> sellList = pc.getSellList();
		ArrayList<L1PrivateShopBuyList> buyList = pc.getBuyList();
		L1ItemInstance checkItem;
		boolean tradable = true;
		int type = readC();
		if (type == 0) { // 開始
			int sellTotalCount = readH();
			int sellObjectId;
			int sellPrice;
			int sellCount;
			for (int i = 0; i < sellTotalCount; i++) {
				sellObjectId = readD();
				sellPrice = readD();
				sellCount = readD();
				// 取引可能なアイテムかチェック
				checkItem = pc.getInventory().getItem(sellObjectId);
				if (!checkItem.getItem().isTradable()) {
					tradable = false;
					pc.sendPackets(new S_SystemMessage(String.format(I18N_TRADING_IS_IMPOSSIBLE,
							checkItem.getItem().getName())));
					// %sは取引不可能です。
				}
				Object[] petlist = pc.getPetList().values().toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (checkItem.getId() == pet.getItemObjId()) {
							tradable = false;
							pc.sendPackets(new S_SystemMessage(String.format(I18N_TRADING_IS_IMPOSSIBLE,
									checkItem.getItem().getName())));
							// %sは取引不可能です。
							break;
						}
					}
				}
				L1PrivateShopSellList pssl = new L1PrivateShopSellList();
				pssl.setItemObjectId(sellObjectId);
				pssl.setSellPrice(sellPrice);
				pssl.setSellTotalCount(sellCount);
				sellList.add(pssl);
			}
			int buyTotalCount = readH();
			int buyObjectId;
			int buyPrice;
			int buyCount;
			for (int i = 0; i < buyTotalCount; i++) {
				buyObjectId = readD();
				buyPrice = readD();
				buyCount = readD();
				// 取引可能なアイテムかチェック
				checkItem = pc.getInventory().getItem(buyObjectId);
				if (!checkItem.getItem().isTradable()) {
					tradable = false;
					pc.sendPackets(new S_SystemMessage(String.format(I18N_TRADING_IS_IMPOSSIBLE,
							checkItem.getItem().getName())));
					// %sは取引不可能です。
				}
				if (checkItem.isSealed()) { // 封印された装備
					// \f1%0は捨てたりまたは他人に讓ることができません。
					pc.sendPackets(new S_ServerMessage(210, checkItem.getItem().getName()));
					return;
				}
				// 異常スタック取引防止用
				if (checkItem.getCount() > 1
						&& checkItem.getItem().isStackable() == false) {
					pc.sendPackets(new S_SystemMessage(String.format(I18N_CAN_NOT_TO_STACK, checkItem.getItem().getName())));
					// %sはスタックすることができません。取引不可能です。
					return;
				}
				Object[] petlist = pc.getPetList().values().toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (checkItem.getId() == pet.getItemObjId()) {
							tradable = false;
							pc.sendPackets(new S_SystemMessage(String.format(I18N_TRADING_IS_IMPOSSIBLE,
									checkItem.getItem().getName())));
							// %sは取引不可能です。
							break;
						}
					}
				}
				L1PrivateShopBuyList psbl = new L1PrivateShopBuyList();
				psbl.setItemObjectId(buyObjectId);
				psbl.setBuyPrice(buyPrice);
				psbl.setBuyTotalCount(buyCount);
				buyList.add(psbl);
			}
			if (!tradable) { // 取引不可能なアイテムが含まれている場合、個人商店終了
				sellList.clear();
				buyList.clear();
				pc.setPrivateShop(false);
				pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
				pc.broadcastPacket(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
				return;
			}
			byte[] chat = readByte();
			pc.setShopChat(chat);
			pc.setPrivateShop(true);
			pc.sendPackets(new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, chat));
			pc.broadcastPacket(new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, chat));
		} else if (type == 1) { // 終了
			sellList.clear();
			buyList.clear();
			pc.setPrivateShop(false);
			pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
			pc.broadcastPacket(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
		}
	}

	@Override
	public String getType() {
		return C_SHOP;
	}
}
