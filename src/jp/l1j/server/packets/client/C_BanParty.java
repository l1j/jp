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
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_BanParty extends ClientBasePacket {

	private static final String C_BAN_PARTY = "[C] C_BanParty";
	private static Logger _log = Logger.getLogger(C_BanParty.class.getName());

	public C_BanParty(byte decrypt[], ClientThread client) throws Exception {
		super(decrypt);
		String s = readS();

		L1PcInstance player = client.getActiveChar();
		if (!player.getParty().isLeader(player)) {
			// パーティーリーダーでない場合
			player.sendPackets(new S_ServerMessage(427)); // パーティーのリーダーのみが追放できます。
			return;
		}

		for (L1PcInstance member : player.getParty().getMembers()) {
			if (member.getName().toLowerCase().equals(s.toLowerCase())) {
				player.getParty().kickMember(member);
				return;
			}
		}
		// 見つからなかった
		player.sendPackets(new S_ServerMessage(426, s)); // %0はパーティーメンバーではありません。
	}

	@Override
	public String getType() {
		return C_BAN_PARTY;
	}

}
