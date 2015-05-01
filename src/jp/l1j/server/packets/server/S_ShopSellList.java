/**
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
package jp.l1j.server.packets.server;

import java.io.IOException;
import java.util.List;
import jp.l1j.configure.Config;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.datatables.ShopTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1TaxCalculator;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.shop.L1Shop;
import jp.l1j.server.templates.L1Item;
import jp.l1j.server.templates.L1ShopItem;

public class S_ShopSellList extends ServerBasePacket {


	/**
	 * 店の品物リストを表示する。キャラクターがBUYボタンを押した時に送る。
	 */
	public S_ShopSellList(int objId) {
		writeC(Opcodes.S_OPCODE_SHOWSHOPBUYLIST);
		writeD(objId);

		L1Object npcObj = L1World.getInstance().findObject(objId);
		if (!(npcObj instanceof L1NpcInstance)) {
			writeH(0);
			return;
		}
		int npcId = ((L1NpcInstance) npcObj).getNpcTemplate().getNpcId();

		L1TaxCalculator calc = new L1TaxCalculator(npcId);
		L1Shop shop = ShopTable.getInstance().get(npcId);
		List<L1ShopItem> shopItems = shop.getSellingItems();

		writeH(shopItems.size());

		// L1ItemInstanceのgetStatusBytesを利用するため
		L1ItemInstance dummy = new L1ItemInstance();

		for (int i = 0; i < shopItems.size(); i++) {
			L1ShopItem shopItem = shopItems.get(i);
			L1Item item = shopItem.getItem();
			int price = calc.layTax((int) (shopItem.getPrice()
							* Config.RATE_SHOP_SELLING_PRICE * shopItem.getPackCount()));
			writeD(i);
			writeH(shopItem.getItem().getGfxId());
			writeD(price);
			
			if (shopItem.getPackCount() > 1) {
				writeS(item.getName() + " (" + shopItem.getPackCount() + ")");
			} else {
				//XXX
				if(item.getItemId()==40309){//レースチケット
					String[] temp=item.getName().split(" ");
					String buf=temp[temp.length-1];
					temp=buf.split("-");
					writeS(buf+" $"+(1212+Integer.parseInt(temp[temp.length-1])));
				}else{
					writeS(item.getName());
				}
			}
			
			L1Item template = ItemTable
					.getInstance().getTemplate(item.getItemId());
			if (template == null) {
				writeC(0);
			} else {
				dummy.setItem(template);
				byte[] status = dummy.getStatusBytes();
				writeC(status.length);
				for (byte b : status) {
					writeC(b);
				}
			}
		}
		writeH(0x07); // 0x00:kaimo 0x01:pearl 0x07:adena
	}

	@Override
	public byte[] getContent() throws IOException {
		return _bao.toByteArray();
	}
}
