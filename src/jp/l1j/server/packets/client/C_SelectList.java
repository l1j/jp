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

import java.util.logging.Logger;
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.model.item.L1ItemId;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_SelectList extends ClientBasePacket {

	private static final String C_SELECT_LIST = "[C] C_SelectList";
	private static Logger _log = Logger.getLogger(C_SelectList.class
			.getName());

	public C_SelectList(byte abyte0[], ClientThread clientthread) {
		super(abyte0);
		// アイテム毎にリクエストが来る。
		int itemObjectId = readD();
		int npcObjectId = readD();
		L1PcInstance pc = clientthread.getActiveChar();

		if (npcObjectId != 0) { // 武器の修理
			L1Object obj = L1World.getInstance().findObject(npcObjectId);
			if (obj != null) {
				if (obj instanceof L1NpcInstance) {
					L1NpcInstance npc = (L1NpcInstance) obj;
					int difflocx = Math.abs(pc.getX() - npc.getX());
					int difflocy = Math.abs(pc.getY() - npc.getY());
					// 3マス以上離れた場合アクション無効
					if (difflocx > 3 || difflocy > 3) {
						return;
					}
				}
			}

			L1PcInventory pcInventory = pc.getInventory();
			L1ItemInstance item = pcInventory.getItem(itemObjectId);
			int cost = item.getDurability() * 200;
			if (!pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
				return;
			}
			item.setDurability(0);
			pcInventory.updateItem(item, L1PcInventory.COL_DURABILITY);
		}
	}

	@Override
	public String getType() {
		return C_SELECT_LIST;
	}
}
