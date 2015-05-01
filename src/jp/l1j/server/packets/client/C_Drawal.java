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
import jp.l1j.server.datatables.CastleTable;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.item.L1ItemId;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1Castle;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_Drawal extends ClientBasePacket {

	private static final String C_DRAWAL = "[C] C_Drawal";
	private static Logger _log = Logger.getLogger(C_Drawal.class.getName());

	public C_Drawal(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);
		int i = readD();
		int j = Math.abs(readD());

		L1PcInstance pc = clientthread.getActiveChar();
		L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
		if (clan != null) {
			int castle_id = clan.getCastleId();
			if (castle_id != 0) {
				L1Castle l1castle = CastleTable.getInstance().getCastleTable(
						castle_id);
				int money = l1castle.getPublicMoney();
				money -= j;
				L1ItemInstance item = ItemTable.getInstance().createItem(
						L1ItemId.ADENA);
				if (item != null) {
					l1castle.setPublicMoney(money);
					CastleTable.getInstance().updateCastle(l1castle);
					if (pc.getInventory().checkAddItem(item, j) == L1Inventory.OK) {
						pc.getInventory().storeItem(L1ItemId.ADENA, j);
					} else {
						L1World.getInstance().getInventory(pc.getX(),
								pc.getY(), pc.getMapId()).storeItem(
								L1ItemId.ADENA, j);
					}
					pc.sendPackets(new S_ServerMessage(143, "$457", "$4" + " ("
							+ j + ")"));
				}
			}
		}
	}

	@Override
	public String getType() {
		return C_DRAWAL;
	}

}
