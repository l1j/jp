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
import jp.l1j.server.model.L1War;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_CharTitle;
import jp.l1j.server.packets.server.S_MessageYN;
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_LeaveClan extends ClientBasePacket {

	private static final String C_LEAVE_CLAN = "[C] C_LeaveClan";
	private static Logger _log = Logger.getLogger(C_LeaveClan.class.getName());

	public C_LeaveClan(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);

		L1PcInstance player = clientthread.getActiveChar();
		String player_name = player.getName();
		String clan_name = player.getClanName();
		int clan_id = player.getClanId();
		if (clan_id == 0) {// クラン未所属
			return;
		}

		L1Clan clan = L1World.getInstance().getClan(clan_name);
		if (clan != null) {
			int i;
			if (player.isCrown() && player.getId() == clan.getLeaderId()) { // プリンスまたはプリンセス、かつ、血盟主
				int castleId = clan.getCastleId();
				int houseId = clan.getHouseId();
				if (castleId != 0 || houseId != 0) {
					player.sendPackets(new S_ServerMessage(665)); // \f1城やアジトを所有した状態で血盟を解散することはできません。
					return;
				}
				for (L1War war : L1World.getInstance().getWarList()) {
					if (war.CheckClanInWar(clan_name)) {
						player.sendPackets(new S_ServerMessage(302)); // \f1解散させることができません。
						return;
					}
				}
				// 本当に解散してもよろしいですか？（Y/N)
				player.sendPackets(new S_MessageYN(453));

			} else { // 血盟主以外
				// 本当に血盟を脱退しますか？（Y/N）
				player.sendPackets(new S_MessageYN(1906));
			}
		} else {
			player.setClanid(0);
			player.setClanname("");
			player.setClanRank(0);
			player.setTitle("");
			player.sendPackets(new S_CharTitle(player.getId(), ""));
			player.broadcastPacket(new S_CharTitle(player.getId(), ""));
			player.save(); // DBにキャラクター情報を書き込む
			player.sendPackets(new S_ServerMessage(178, player_name, clan_name));
			// \f1%0が%1血盟を脱退しました。
		}
	}

	@Override
	public String getType() {
		return C_LEAVE_CLAN;
	}

}
