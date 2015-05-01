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
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_Pledge;
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_Pledge extends ClientBasePacket {

	private static final String C_PLEDGE = "[C] C_Pledge";
	private static Logger _log = Logger.getLogger(C_Pledge.class.getName());

	public C_Pledge(byte abyte0[], ClientThread clientthread) {
		super(abyte0);
		L1PcInstance pc = clientthread.getActiveChar();

		if (pc.getClanId() > 0) {
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
				pc.sendPackets(new S_Pledge("pledgeM", pc.getId(),
						clan.getClanName(), clan.getOnlineMembersFPWithRank(),
						clan.getAllMembersFPWithRank()));
			} else {
				pc.sendPackets(new S_Pledge("pledge", pc.getId(),
						clan.getClanName(), clan.getOnlineMembersFP()));
			}
		} else {
			pc.sendPackets(new S_ServerMessage(1064)); // 血盟に属していません。
			// pc.sendPackets(new S_Pledge("pledge", pc.getId()));
		}
	}

	@Override
	public String getType() {
		return C_PLEDGE;
	}

}
