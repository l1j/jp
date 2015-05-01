/**
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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.l1j.configure.Config;
import jp.l1j.server.ClientThread;
import jp.l1j.server.datatables.CharacterTable;
import jp.l1j.server.datatables.ClanRecommendTable;
import jp.l1j.server.datatables.ClanTable;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1Quest;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_CharTitle;
import jp.l1j.server.packets.server.S_PledgeRecommendation;
import jp.l1j.server.packets.server.S_ServerMessage;

public class C_PledgeRecommendation extends ClientBasePacket {
	private static final String C_PledgeRecommendation = "[C] C_PledgeRecommendation";
	private static Logger _log = Logger.getLogger(C_AddBookmark.class.getName());

	public C_PledgeRecommendation(byte[] decrypt, ClientThread client) throws Exception {
		super(decrypt);

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		
		int data = readC();
		
		if(data == 0){ // 推薦血盟リストへ登録
			int clanType = readC(); // 血盟タイプ: 戦闘/狩り/親睦
			String message = readS(); // 血盟紹介文
			if(ClanRecommendTable.getInstance().isRecorded(pc.getClanId())){
				ClanRecommendTable.getInstance().updateRecommendRecord(pc.getClanId(),
								clanType, message);
			} else {
				ClanRecommendTable.getInstance().addRecommendRecord(pc.getClanId(),
								clanType, message);
			}
			pc.sendPackets(new S_PledgeRecommendation(true, pc.getClanId()));
		} else if(data == 1){ // 登録を削除
			ClanRecommendTable.getInstance().removeRecommendRecord(pc.getClanId());
			pc.sendPackets(new S_PledgeRecommendation(false, pc.getClanId()));
		} else if(data == 2){ // 推薦血盟リストを開く
			pc.sendPackets(new S_PledgeRecommendation(data, pc.getName()));
		} else if(data == 3){ // 申請リストを開く
			pc.sendPackets(new S_PledgeRecommendation(data, pc.getName()));
		} else if(data == 4){ // 要請リストを開く
			if(pc.getClanRank() > 0){
				pc.sendPackets(new S_PledgeRecommendation(data, pc.getClanId()));
			}
		} else if(data == 5){ // 加入申請
			// 自分が君主クラス
			if (pc.isCrown()) {
				// 自分が血盟君主
				if (pc.getClanRank() == L1Clan.CLAN_RANK_LEADER) {
					// 自分が城またはアジトを所有している
					String player_clan_name = pc.getClanName();
					L1Clan player_clan = L1World.getInstance().getClan(player_clan_name);
					if (player_clan != null && (player_clan.getCastleId() != 0 || 
							player_clan.getHouseId() != 0)) {
						pc.sendPackets(new S_ServerMessage(665));
						// \f1城やアジトを所有した状態で血盟を解散することはできません。
						return;
					}
				// 自分が他の連合血盟に所属
				} else if (pc.getClanRank() > 0) {
					pc.sendPackets(new S_ServerMessage(89));
					// \f1あなたはすでに血盟に加入しています。
					return;
				}
			// 自分が君主クラスではない
			} else {
				// 他の血盟に所属中
				if (pc.getClanRank() > 0) {
					pc.sendPackets(new S_ServerMessage(89));
					// \f1あなたはすでに血盟に加入しています。
					return;
				}
			}
			if (pc.getRejoinClanTime() != null
					&& pc.getRejoinClanTime().getTime() > System.currentTimeMillis()) {
				int time = (int) (pc.getRejoinClanTime().getTime() - System.currentTimeMillis())
						/ (60 * 60 * 1000);
				pc.sendPackets(new S_ServerMessage(1925, time + ""));
				// 血盟戦中に脱退したため、あと%0時間は血盟に加入できません。
				return;
			}
			int clan_id = readD();
			ClanRecommendTable.getInstance().addRecommendApply(clan_id, pc.getName());
			pc.sendPackets(new S_PledgeRecommendation(data, clan_id, 0));
		} else if(data == 6){ // 加入を許可, 加入を拒否, 申請を削除
			int index = readD();
			int type = readC();
			if(type == 1){ // 加入を許可
				L1Clan clan = pc.getClan();
				L1PcInstance joinPc = L1World.getInstance().getPlayer(
								ClanRecommendTable.getInstance().getApplyPlayerName(index));
				if (clan != null) {
					int maxMember = 0;
					int charisma = pc.getCha();
					boolean lv45quest = false;
					if (pc.getQuest().isEnd(L1Quest.QUEST_LEVEL45)) {
						lv45quest = true;
					}
					if (pc.getLevel() >= 50) { // Lv50以上
						if (lv45quest == true) { // Lv45クエストクリア済み
							maxMember = charisma * 9;
						} else {
							maxMember = charisma * 3;
						}
					} else { // Lv50未満
						if (lv45quest == true) { // Lv45クエストクリア済み
							maxMember = charisma * 6;
						} else {
							maxMember = charisma * 2;
						}
					}
					if (Config.MAX_CLAN_MEMBER > 0 && Config.MAX_CLAN_MEMBER < maxMember) {
						// Clan人数の上限の設定あり
						maxMember = Config.MAX_CLAN_MEMBER;
					}
					if (joinPc.getClanId() == 0) { // クラン未加入
						String clanMembersName[] = clan.getAllMembers();
						if (maxMember <= clanMembersName.length) { // 空きがない
							pc.sendPackets(new S_ServerMessage(3260));
							// これ以上血盟員を受け入れることができません。
							return;
						}
						for (L1PcInstance clanMembers : clan.getOnlineClanMember()) {
							// \f1%0が血盟の一員として受け入れられました。
							clanMembers.sendPackets(new S_ServerMessage(94, joinPc.getName()));
						}
						joinPc.setClanid(clan.getClanId());
						joinPc.setClanname(clan.getClanName());
						// joinPc.setClanMemberNotes("");
						joinPc.setClanRank(L1Clan.CLAN_RANK_REGULAR);
						joinPc.setTitle("");
						joinPc.setRejoinClanTime(null);
						joinPc.sendPackets(new S_CharTitle(joinPc.getId(), ""));
						joinPc.broadcastPacket(new S_CharTitle(joinPc.getId(), ""));
						joinPc.save(); // DBにキャラクター情報を書き込む
						clan.addMemberName(joinPc.getName());
						// ClanMembersTable.getInstance().newMember(joinPc);
						joinPc.sendPackets(new S_ServerMessage(95, clan.getClanName()));
						// \f1%0血盟に加入しました。
						/* TODO
						joinPc.sendPackets(new S_ClanName(joinPc, true));
						joinPc.sendPackets(new S_CharReset(joinPc.getId(), clan.getClanId()));
						joinPc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, pc.getClan().getEmblemStatus()));
						joinPc.sendPackets(new S_ClanAttention());
						for(L1PcInstance player : clan.getOnlineClanMember()){
							player.sendPackets(new S_CharReset(joinPc.getId(), joinPc.getClan().getEmblemId()));
							player.broadcastPacket(new S_CharReset(player.getId(), joinPc.getClan().getEmblemId()));
						}
						*/
					} else { // クラン加入済み（クラン連合）
						if (Config.CLAN_ALLIANCE) {
							changeClan(pc, joinPc, maxMember);
						} else { // TODO
							pc.sendPackets(new S_ServerMessage(2739));
							// キャラクターが血盟に加入しています。
						}
					}
				}
				ClanRecommendTable.getInstance().removeRecommendApply(index);
			} else if(type == 2){ // 加入を拒否
				ClanRecommendTable.getInstance().removeRecommendApply(index);
			} else if(type == 3){ // 申請を削除
				ClanRecommendTable.getInstance().removeRecommendApply(index);
			}
			pc.sendPackets(new S_PledgeRecommendation(data, index, type));
		} 
	}
	
	private void changeClan(L1PcInstance pc, L1PcInstance joinPc, int maxMember) {
		try {
			L1Clan clan = pc.getClan();
			String clanMemberName[] = clan.getAllMembers();
			int clanNum = clanMemberName.length;
			L1Clan oldClan = joinPc.getClan();
			String oldClanMemberName[] = oldClan.getAllMembers();
			int oldClanNum = oldClanMemberName.length;
			if (maxMember < clanNum + oldClanNum) { // 空きがない
				pc.sendPackets(new S_ServerMessage(3260));
				// これ以上血盟員を受け入れることができません。
				return;
			}
			L1PcInstance clanMember[] = clan.getOnlineClanMember();
			for (int cnt = 0; cnt < clanMember.length; cnt++) {
				clanMember[cnt].sendPackets(new S_ServerMessage(94, joinPc.getName()));
				// \f1%0が血盟の一員として受け入れられました。
			}
			for (int i = 0; i < oldClanMemberName.length; i++) {
				L1PcInstance oldClanMember = L1World.getInstance().getPlayer(
						oldClanMemberName[i]);
				if (oldClanMember == null) {
					oldClanMember = CharacterTable.getInstance().restoreCharacter(
									oldClanMemberName[i]);
				}
				oldClanMember.setClanid(clan.getClanId());
				oldClanMember.setClanname(clan.getClanName());
				// oldClanMember.setClanMemberNotes("");
				// 血盟連合に加入した君主は、副君主
				// 君主が連れてきた血盟員は、一般血盟員
				if (oldClanMember.getId() == joinPc.getId()) {
					oldClanMember.setClanRank(L1Clan.CLAN_RANK_SUBLEADER);
				} else {
					oldClanMember.setClanRank(L1Clan.CLAN_RANK_REGULAR);
				}
				oldClanMember.setTitle("");
				oldClanMember.setRejoinClanTime(null);
				oldClanMember.sendPackets(new S_CharTitle(joinPc.getId(), ""));
				oldClanMember.broadcastPacket(new S_CharTitle(joinPc.getId(), ""));
				oldClanMember.save(); // DBにキャラクター情報を書き込む
				clan.addMemberName(oldClanMember.getName());
				// ClanMembersTable.getInstance().newMember(oldClanMember);
				if (oldClanMember.getOnlineStatus() == 1) {
					oldClanMember.sendPackets(new S_ServerMessage(95, clan.getClanName()));
					// \f1%0血盟に加入しました。
				}
				/* TODO
				joinPc.sendPackets(new S_ClanName(joinPc, true));
				joinPc.sendPackets(new S_CharReset(joinPc.getId(), clan.getClanId()));
				joinPc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, pc.getClan().getEmblemStatus()));
				joinPc.sendPackets(new S_ClanAttention());
				for(L1PcInstance player : clan.getOnlineClanMember()){
					player.sendPackets(new S_CharReset(joinPc.getId(), joinPc.getClan().getEmblemId()));
					player.broadcastPacket(new S_CharReset(player.getId(), joinPc.getClan().getEmblemId()));
				}
				*/
			}
			// 旧クラン削除
			String emblem_file = String.valueOf(oldClan.getClanId());
			File file = new File("emblem/" + emblem_file);
			file.delete();
			ClanTable.getInstance().deleteClan(oldClan.getClanName());
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String getType() {
		return C_PledgeRecommendation;
	}
}
