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

package jp.l1j.server.packets.server;

import java.util.List;
import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.datatables.ShopTable;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.shop.L1AssessedItem;
import jp.l1j.server.model.shop.L1Shop;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket, S_SystemMessage

public class S_ShopBuyList extends ServerBasePacket {

	private static Logger _log = Logger
			.getLogger(S_ShopBuyList.class.getName());

	private static final String S_SHOP_BUY_LIST = "[S] S_ShopBuyList";

	public S_ShopBuyList(int objid, L1PcInstance pc) {
		L1Object object = L1World.getInstance().findObject(objid);
		if (!(object instanceof L1NpcInstance)) {
			return;
		}
		L1NpcInstance npc = (L1NpcInstance) object;
		int npcId = npc.getNpcTemplate().getNpcId();
		L1Shop shop = ShopTable.getInstance().get(npcId);
		if (shop == null) {
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		List<L1AssessedItem> assessedItems = shop
				.assessItems(pc.getInventory());
		if (assessedItems.isEmpty()) {
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		writeC(Opcodes.S_OPCODE_SHOWSHOPSELLLIST);
		writeD(objid);
		writeH(assessedItems.size());

		for (L1AssessedItem item : assessedItems) {
			writeD(item.getTargetId());
			writeD((int) item.getAssessedPrice());
		}
		writeH(0x0007);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_SHOP_BUY_LIST;
	}
}