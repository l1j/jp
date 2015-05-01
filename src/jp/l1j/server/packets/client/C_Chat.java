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
import jp.l1j.configure.Config;
import jp.l1j.server.ClientThread;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.command.GMCommands;
import jp.l1j.server.datatables.ChatLogTable;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_ChatPacket;
import jp.l1j.server.packets.server.S_NpcChatPacket;
import jp.l1j.server.packets.server.S_PacketBox;
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_Chat extends ClientBasePacket {

	private static final String C_CHAT = "[C] C_Chat";
	private static Logger _log = Logger.getLogger(C_Chat.class.getName());

	public C_Chat(byte abyte0[], ClientThread clientthread) {
		super(abyte0);

		L1PcInstance pc = clientthread.getActiveChar();
		int chatType = readC();
		String chatText = readS();
		if (pc.hasSkillEffect(SILENCE) || pc.hasSkillEffect(AREA_OF_SILENCE)
				|| pc.hasSkillEffect(STATUS_POISON_SILENCE)
				|| pc.hasSkillEffect(ELZABE_AREA_SILENCE)) {
			return;
		}
		if (pc.hasSkillEffect(1005)) { // チャット禁止中
			pc.sendPackets(new S_ServerMessage(242)); // 現在チャット禁止中です。
			return;
		}

		// XXX デスマッチ どこまで制限されるのか不明 GMのみ許可する
		if (// pc.getMap().getBaseMapId()==5153 && !pc.isGm()){
		pc.getMapId() == 5153 && !pc.isGm()) {
			return;
		}

		if (chatType == 0) { // 通常チャット
			if (pc.isGhost() && !(pc.isGm() || pc.isMonitor())) {
				return;
			}
			// GMコマンド
			if (chatText.startsWith(".") && chatText.length() > 1) {
				String cmd = chatText.substring(1);
				GMCommands.getInstance().handleCommands(pc, cmd);
				return;
			}

			// トレードチャット
			// 本来はchatType==12になるはずだが、行頭の$が送信されない
			if (chatText.startsWith("$")) {
				String text = chatText.substring(1);
				chatWorld(pc, text, 12);
				if (!pc.isGm()) {
					pc.checkChatInterval();
				}
				return;
			}

			ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
					Opcodes.S_OPCODE_NORMALCHAT, 0);
			if (!pc.getExcludingList().contains(pc.getName())) {
				pc.sendPackets(s_chatpacket);
			}
			for (L1PcInstance listner : L1World.getInstance()
					.getRecognizePlayer(pc)) {
				if (listner.getMapId() > 10000
						&& listner.getInnKeyId() != pc.getInnKeyId()) { // 宿屋内判定
					break;
				} else if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(s_chatpacket);
				}
			}
			// ドッペル処理
			for (L1Object obj : pc.getKnownObjects()) {
				if (obj instanceof L1MonsterInstance) {
					L1MonsterInstance mob = (L1MonsterInstance) obj;
					if (mob.getNpcTemplate().isDoppel()
							&& mob.getName().equals(pc.getName())) {
						mob.broadcastPacket(new S_NpcChatPacket(mob, chatText,
								0));
					}
				}
			}
		} else if (chatType == 2) { // 叫び
			if (pc.isGhost()) {
				return;
			}
			ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
					Opcodes.S_OPCODE_NORMALCHAT, 2);
			if (!pc.getExcludingList().contains(pc.getName())) {
				pc.sendPackets(s_chatpacket);
			}
			for (L1PcInstance listner : L1World.getInstance().getVisiblePlayer(
					pc, 50)) {
				if (listner.getMapId() > 10000
						&& listner.getInnKeyId() != pc.getInnKeyId()) { // 宿屋内判定
					break;
				} else if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(s_chatpacket);
				}
			}

			// ドッペル処理
			for (L1Object obj : pc.getKnownObjects()) {
				if (obj instanceof L1MonsterInstance) {
					L1MonsterInstance mob = (L1MonsterInstance) obj;
					if (mob.getNpcTemplate().isDoppel()
							&& mob.getName().equals(pc.getName())) {
						for (L1PcInstance listner : L1World.getInstance()
								.getVisiblePlayer(mob, 50)) {
							listner.sendPackets(new S_NpcChatPacket(mob,
									chatText, 2));
						}
					}
				}
			}
		} else if (chatType == 3) { // 全体チャット
			chatWorld(pc, chatText, chatType);
		} else if (chatType == 4) { // 血盟チャット
			if (pc.getClanId() != 0) { // クラン所属中
				L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
				int rank = pc.getClanRank();
				if (clan != null
						&& (rank == L1Clan.CLAN_RANK_REGULAR
						|| rank == L1Clan.CLAN_RANK_ELITE
						|| rank == L1Clan.CLAN_RANK_GUARDIAN
						|| rank == L1Clan.CLAN_RANK_SUBLEADER
						|| rank == L1Clan.CLAN_RANK_LEADER)) {
					ChatLogTable.getInstance().storeChat(pc, null, chatText,
							chatType);
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
							Opcodes.S_OPCODE_GLOBALCHAT, 4);
					L1PcInstance[] clanMembers = clan.getOnlineClanMember();
					for (L1PcInstance listner : clanMembers) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							if (listner.isShowClanChat() && chatType == 4)// 血盟
								listner.sendPackets(s_chatpacket);
						}
					}
				}
			}
		} else if (chatType == 11) { // パーティーチャット
			if (pc.isInParty()) { // パーティー中
				ChatLogTable.getInstance().storeChat(pc, null, chatText,
						chatType);
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
						Opcodes.S_OPCODE_GLOBALCHAT, 11);
				L1PcInstance[] partyMembers = pc.getParty().getMembers();
				for (L1PcInstance listner : partyMembers) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						if (listner.isShowPartyChat() && chatType == 11)// パーティー
							listner.sendPackets(s_chatpacket);
					}
				}
			}
		} else if (chatType == 12) { // トレードチャット
			chatWorld(pc, chatText, chatType);
		} else if (chatType == 13) { // 連合チャット
			if (pc.getClanId() != 0) { // クラン所属中
				L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
				int rank = pc.getClanRank();
				if (clan != null
						&& (rank == L1Clan.CLAN_RANK_GUARDIAN
						|| rank == L1Clan.CLAN_RANK_SUBLEADER
						|| rank == L1Clan.CLAN_RANK_LEADER)) {
					ChatLogTable.getInstance().storeChat(pc, null, chatText,
							chatType);
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
							Opcodes.S_OPCODE_GLOBALCHAT, 13);
					L1PcInstance[] clanMembers = clan.getOnlineClanMember();
					for (L1PcInstance listner : clanMembers) {
						int listnerRank = listner.getClanRank();
						if (!listner.getExcludingList().contains(pc.getName())
								&& (listnerRank == L1Clan.CLAN_RANK_GUARDIAN
								|| listnerRank == L1Clan.CLAN_RANK_SUBLEADER
								|| listnerRank == L1Clan.CLAN_RANK_LEADER)) {
							listner.sendPackets(s_chatpacket);
						}
					}
				}
			}
		} else if (chatType == 14) { // チャットパーティー
			if (pc.isInChatParty()) { // チャットパーティー中
				ChatLogTable.getInstance().storeChat(pc, null, chatText,
						chatType);
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
						Opcodes.S_OPCODE_NORMALCHAT, 14);
				L1PcInstance[] partyMembers = pc.getChatParty().getMembers();
				for (L1PcInstance listner : partyMembers) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						listner.sendPackets(s_chatpacket);
					}
				}
			}
		}
		if (!pc.isGm()) {
			pc.checkChatInterval();
		}
	}

	private void chatWorld(L1PcInstance pc, String chatText, int chatType) {
		if (pc.isGm()) {
			ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
			L1World.getInstance().broadcastPacketToAll(
					new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_GLOBALCHAT,
							chatType));
		} else if (pc.getLevel() >= Config.GLOBAL_CHAT_LEVEL) {
			if (L1World.getInstance().isWorldChatElabled()) {
				if (pc.getFood() >= 6) {
					pc.setFood(pc.getFood() - 5);
					ChatLogTable.getInstance().storeChat(pc, null, chatText,
							chatType);
					pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc
							.getFood()));
					for (L1PcInstance listner : L1World.getInstance()
							.getAllPlayers()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							if (listner.isShowTradeChat() && chatType == 12) {
								listner.sendPackets(new S_ChatPacket(pc,
										chatText, Opcodes.S_OPCODE_GLOBALCHAT,
										chatType));
							} else if (listner.isShowWorldChat()
									&& chatType == 3) {
								listner.sendPackets(new S_ChatPacket(pc,
										chatText, Opcodes.S_OPCODE_GLOBALCHAT,
										chatType));
							}
						}
					}
				} else {
					pc.sendPackets(new S_ServerMessage(462)); // \f1空腹のためチャットできません。
				}
			} else {
				pc.sendPackets(new S_ServerMessage(510)); // 現在ワールドチャットは停止中となっております。しばらくの間ご了承くださいませ。
			}
		} else {
			pc.sendPackets(new S_ServerMessage(195, String
					.valueOf(Config.GLOBAL_CHAT_LEVEL))); // レベル%0未満のキャラクターはチャットができません。
		}
	}

	@Override
	public String getType() {
		return C_CHAT;
	}
}