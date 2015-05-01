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
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.packets.server.S_Emblem;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_Clan extends ClientBasePacket {

	private static final String C_CLAN = "[C] C_Clan";
	private static Logger _log = Logger.getLogger(C_Clan.class.getName());

	public C_Clan(byte abyte0[], ClientThread client) {
		super(abyte0);
		int clanId = readD();

		L1PcInstance pc = client.getActiveChar();
		L1Clan clan = ClanTable.getInstance().getTemplate(clanId);
		String name = clan.getClanName();
		pc.sendPackets(new S_Emblem(clan.getClanId()));
	}

	@Override
	public String getType() {
		return C_CLAN;
	}

}
