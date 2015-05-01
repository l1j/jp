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
import jp.l1j.server.datatables.CharacterTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_BanClan extends ClientBasePacket {

	private static final String C_BAN_CLAN = "[C] C_BanClan";
	private static Logger _log = Logger.getLogger(C_BanClan.class.getName());

	public C_BanClan(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);
		String s = readS();

		L1PcInstance pc = clientthread.getActiveChar();
		L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
		if (clan != null) {
			String clanMemberName[] = clan.getAllMembers();
			int i;
			if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 君主、かつ、血盟主
				for (i = 0; i < clanMemberName.length; i++) {
					if (pc.getName().toLowerCase().equals(s.toLowerCase())) { // 君主自身
						return;
					}
				}
				L1PcInstance target = L1World.getInstance().getPlayer(s);
				if (target == null) {
					target = CharacterTable.getInstance().restoreCharacter(s);
					if (target == null) {
						pc.sendPackets(new S_ServerMessage(109, s)); // %0という名前の人はいません。
						return;
					}
				}
				target.setClanid(0);
				target.setClanname("");
				target.setClanRank(0);
				target.save(); // DBにキャラクター情報を書き込む
				clan.delMemberName(target.getName());
				if (target.getOnlineStatus() == 1) {
					target.sendPackets(new S_ServerMessage(238, pc.getClanName()));
					// あなたは%0血盟から追放されました。
				}
				pc.sendPackets(new S_ServerMessage(240, target.getName()));
				// %0があなたの血盟から追放されました。
			} else {
				pc.sendPackets(new S_ServerMessage(518)); // この命令は血盟の君主のみが利用できます。
			}
		}
	}

	@Override
	public String getType() {
		return C_BAN_CLAN;
	}
}
