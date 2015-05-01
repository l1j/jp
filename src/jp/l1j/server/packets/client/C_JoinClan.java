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
import jp.l1j.server.packets.server.S_MessageYN;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.utils.FaceToFace;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_JoinClan extends ClientBasePacket {

	private static final String C_JOIN_CLAN = "[C] C_JoinClan";
	private static Logger _log = Logger.getLogger(C_JoinClan.class.getName());

	public C_JoinClan(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc.isGhost()) {
			return;
		}

		L1PcInstance target = FaceToFace.faceToFace(pc);
		if (target != null) {
			JoinClan(pc, target);
		}
	}

	private void JoinClan(L1PcInstance player, L1PcInstance target) {
		// 自分が君主クラス
		if (player.isCrown()) {
			// 自分が血盟君主
			if (player.getClanRank() == L1Clan.CLAN_RANK_LEADER) {
				// 相手が君主クラス
				if (target.isCrown()) {
					// 相手が血盟君主ではない
					if (target.getClanRank() != L1Clan.CLAN_RANK_LEADER) {
						// \f1%0は血盟を創設していない状態です。
						player.sendPackets(new S_ServerMessage(90, target.getName()));
						return;
					}
				// 相手が君主クラスではない
				} else {
					// \f1%0はプリンスやプリンセスではありません。
					player.sendPackets(new S_ServerMessage(92, target.getName()));
					return;
				}
				// 自分が城またはアジトを所有している
				String player_clan_name = player.getClanName();
				L1Clan player_clan = L1World.getInstance().getClan(player_clan_name);
				if (player_clan != null && (player_clan.getCastleId() != 0 || 
						player_clan.getHouseId() != 0)) {
					// \f1城やアジトを所有した状態で血盟を解散することはできません。
					player.sendPackets(new S_ServerMessage(665));
					return;
				}
			// 自分が他の連合血盟に所属
			} else if (player.getClanRank() > 0) {
				// \f1あなたはすでに血盟に加入しています。
				player.sendPackets(new S_ServerMessage(89));
				return;
			// 自分が血盟君主ではなく、他の連合血盟にも所属していない
			} else {
				// 相手が君主クラス
				if (target.isCrown()) {
					// 相手が血盟君主ではない
					if (target.getClanRank() != L1Clan.CLAN_RANK_LEADER) {
						// \f1%0は血盟を創設していない状態です。
						player.sendPackets(new S_ServerMessage(90, target.getName()));
						return;
					}
				// 相手が君主クラスではない
				} else {
					// \f1%0はプリンスやプリンセスではありません。
					player.sendPackets(new S_ServerMessage(92, target.getName()));
					return;
				}
			}
		// 自分が君主クラスではない
		} else {
			// 他の血盟に所属中
			if (player.getClanRank() > 0) {
				// \f1あなたはすでに血盟に加入しています。
				player.sendPackets(new S_ServerMessage(89));
				return;
			// 未所属
			} else {
				// 相手が血盟君主、副君主またはガーディアンではない
				if (target.getClanRank() != L1Clan.CLAN_RANK_LEADER
						&& target.getClanRank() != L1Clan.CLAN_RANK_SUBLEADER
						&& target.getClanRank() != L1Clan.CLAN_RANK_GUARDIAN) {
					// %0はあなたを血盟員として受け入れることができません。
					player.sendPackets(new S_ServerMessage(188, target.getName()));
					return;
				}
			}
		}

		if (player.getRejoinClanTime() != null
				&& player.getRejoinClanTime().getTime() > System.currentTimeMillis()) {
			// 血盟戦中に脱退したため、あと%0時間は血盟に加入できません。
			int time = (int) (player.getRejoinClanTime().getTime() - System.currentTimeMillis())
					/ (60 * 60 * 1000);
			player.sendPackets(new S_ServerMessage(1925, time + ""));
			return;
		}
		
		target.setTempID(player.getId()); // 相手のオブジェクトIDを保存しておく
		target.sendPackets(new S_MessageYN(97, player.getName())); // %0が血盟に加入したがっています。承諾しますか？（Y/N）
	}

	@Override
	public String getType() {
		return C_JOIN_CLAN;
	}
}
