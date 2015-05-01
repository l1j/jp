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

import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_CharTitle;
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_Title extends ClientBasePacket {

	private static final String C_TITLE = "[C] C_Title";
	private static Logger _log = Logger.getLogger(C_Title.class.getName());

	public C_Title(byte abyte0[], ClientThread clientthread) {
		super(abyte0);
		L1PcInstance pc = clientthread.getActiveChar();
		String charName = readS();
		String title = readS();

		if (charName.isEmpty() || title.isEmpty()) {
			// \f1次のように入力してください：「/title \f0キャラクター名 呼称\f1」
			pc.sendPackets(new S_ServerMessage(196));
			return;
		}
		L1PcInstance target = L1World.getInstance().getPlayer(charName);
		if (target == null) {
			return;
		}

		if (pc.isGm()) {
			changeTitle(target, title);
			return;
		}

		if (pc.getClanRank() == L1Clan.CLAN_RANK_LEADER // 血盟君主または副君主
				|| pc.getClanRank() == L1Clan.CLAN_RANK_SUBLEADER) {
			if (pc.getId() == target.getId()) { // 自分
				if (pc.getLevel() < 10) {
					// \f1血盟員の場合、呼称を持つにはレベル10以上でなければなりません。
					pc.sendPackets(new S_ServerMessage(197));
					return;
				}
				changeTitle(pc, title);
			} else { // 他人
				if (pc.getClanId() != target.getClanId()) {
					// \f1血盟員でなければ他人に呼称を与えることはできません。
					pc.sendPackets(new S_ServerMessage(199));
					return;
				}
				if (target.getLevel() < 10) {
					// \f1%0のレベルが10未満なので呼称を与えることはできません。
					pc.sendPackets(new S_ServerMessage(202, charName));
					return;
				}
				changeTitle(target, title);
				L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
				if (clan != null) {
					for (L1PcInstance clanPc : clan.getOnlineClanMember()) {
						// \f1%0が%1に「%2」という呼称を与えました。
						clanPc.sendPackets(new S_ServerMessage(203, pc
								.getName(), charName, title));
					}
				}
			}
		} else {
			if (pc.getId() == target.getId()) { // 自分
				if (pc.getClanId() != 0 && !Config.CHANGE_TITLE_BY_ONESELF) {
					// \f1血盟員に呼称を与えられるのはプリンスとプリンセスだけです。
					pc.sendPackets(new S_ServerMessage(198));
					return;
				}
				if (target.getLevel() < 40) {
					// \f1血盟員ではないのに呼称を持つには、レベル40以上でなければなりません。
					pc.sendPackets(new S_ServerMessage(200));
					return;
				}
				changeTitle(pc, title);
			} else { // 他人
				if (pc.isCrown()) { // 連合に所属した君主
					if (pc.getClanId() == target.getClanId()) {
						// \f1%0はあなたの血盟ではありません。
						pc.sendPackets(new S_ServerMessage(201, pc
								.getClanName()));
						return;
					}
				}
			}
		}
	}

	private void changeTitle(L1PcInstance pc, String title) {
		int objectId = pc.getId();
		pc.setTitle(title);
		pc.sendPackets(new S_CharTitle(objectId, title));
		pc.broadcastPacket(new S_CharTitle(objectId, title));
		try {
			pc.save(); // DBにキャラクター情報を書き迂む
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private boolean isClanLeader(L1PcInstance pc) {
		boolean isClanLeader = false;
		if (pc.getClanId() != 0) { // クラン所属
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 君主、かつ、血盟主
					isClanLeader = true;
				}
			}
		}
		return isClanLeader;
	}

	@Override
	public String getType() {
		return C_TITLE;
	}

}
