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
import jp.l1j.server.datatables.ClanTable;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import static jp.l1j.server.model.item.L1ItemId.ADENA;
import jp.l1j.server.packets.server.S_ServerMessage;

public class C_CreateClan extends ClientBasePacket {
	private static final String C_CREATE_CLAN = "[C] C_CreateClan";
	
	private static Logger _log = Logger.getLogger(C_CreateClan.class.getName());

	public C_CreateClan(byte abyte0[], ClientThread clientthread) throws Exception {
		super(abyte0);
		String s = readS();
		int i = s.length();
		L1PcInstance pc = clientthread.getActiveChar();
		if (!pc.isCrown()) {
			pc.sendPackets(new S_ServerMessage(85));
			// \f1プリンスとプリンセスだけが血盟を創設できます。
			return;
		}
		if (pc.getClanId() != 0) {
			pc.sendPackets(new S_ServerMessage(86));
			// \f1すでに血盟を結成されているので作成できません。
			return;
		}
		if (!pc.getInventory().checkItem(ADENA, 30000)) {
			pc.sendPackets(new S_ServerMessage(189));
			// 189 \f1アデナが不足しています。
			return;
		}
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (clan.getClanName().toLowerCase().equals(s.toLowerCase())) {
				pc.sendPackets(new S_ServerMessage(99));
				// \f1同じ名前の血盟が存在します。
				return;
			}
		}
		L1Clan clan = ClanTable.getInstance().createClan(pc, s); // クラン創設
		if (clan != null) {
			pc.sendPackets(new S_ServerMessage(84, s));
			// \f1%0血盟が創設されました。
		}
	}

	@Override
	public String getType() {
		return C_CREATE_CLAN;
	}
}
