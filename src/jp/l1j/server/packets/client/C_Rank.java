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

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.ClientThread;
import jp.l1j.server.datatables.CharacterTable;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_PacketBox;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;

public class C_Rank extends ClientBasePacket {
	private static final String C_RANK = "[C] C_Rank";
	
	private static Logger _log = Logger.getLogger(C_Rank.class.getName());
	
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	
	private static Calendar _cal = Calendar.getInstance();

	public C_Rank(byte abyte0[], ClientThread clientthread) throws Exception {
		super(abyte0);
		int data = readC();
		int rank = readC();
		String name = readS();
		L1PcInstance pc = clientthread.getActiveChar();
		L1PcInstance targetPc = L1World.getInstance().getPlayer(name);
		if (data == 1) {
			if (pc == null) {
				return;
			}
			if (targetPc == null) {
				targetPc = CharacterTable.getInstance().restoreCharacter(name);
				if (targetPc == null) {
					pc.sendPackets(new S_ServerMessage(109, name));
					// %0という名前の人はいません。
					return;
				}
			}
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan == null) {
				return;
			}
			if (rank != L1Clan.CLAN_RANK_SUBLEADER
					&& rank != L1Clan.CLAN_RANK_GUARDIAN
					&& rank != L1Clan.CLAN_RANK_ELITE
					&& rank != L1Clan.CLAN_RANK_REGULAR) {
				// ランクを変更する人の名前とランクを入力してください。[ランク = ガーディアン、一般、修練]
				pc.sendPackets(new S_ServerMessage(2150));
				return;
			}
			int pcRank = pc.getClanRank();
			int targetPcRank = targetPc.getClanRank();
			if ((pcRank == L1Clan.CLAN_RANK_LEADER
					|| pcRank == L1Clan.CLAN_RANK_SUBLEADER)
					&& pc.getLevel() < 25) {
				// この権限を遂行するには、レベル25以上でなければなりません。
				pc.sendPackets(new S_ServerMessage(2471));
			}
			if (pcRank == L1Clan.CLAN_RANK_GUARDIAN && pc.getLevel() < 40) {
				// この権限を遂行するには、レベル40以上でなければなりません。
				pc.sendPackets(new S_ServerMessage(2472));
			}
			if (pcRank == L1Clan.CLAN_RANK_ELITE
					|| pcRank == L1Clan.CLAN_RANK_REGULAR
				// 一般、修練は、階級任命の権限はない
				|| (pcRank == L1Clan.CLAN_RANK_LEADER
					&& rank == L1Clan.CLAN_RANK_LEADER)
				|| (pcRank == L1Clan.CLAN_RANK_SUBLEADER
					&& (rank == L1Clan.CLAN_RANK_LEADER
					|| rank == L1Clan.CLAN_RANK_SUBLEADER))
				|| (pcRank == L1Clan.CLAN_RANK_GUARDIAN
					&& (rank == L1Clan.CLAN_RANK_LEADER
					|| rank == L1Clan.CLAN_RANK_SUBLEADER
					|| rank == L1Clan.CLAN_RANK_GUARDIAN))
				// 任命しようとしているランクが自分のランクと同じか上位
				|| (pcRank == L1Clan.CLAN_RANK_LEADER
					&& targetPcRank == L1Clan.CLAN_RANK_LEADER)
				|| (pcRank == L1Clan.CLAN_RANK_SUBLEADER
					&& (targetPcRank == L1Clan.CLAN_RANK_LEADER
					|| targetPcRank == L1Clan.CLAN_RANK_SUBLEADER))
				|| (pcRank == L1Clan.CLAN_RANK_GUARDIAN
					&& (targetPcRank == L1Clan.CLAN_RANK_LEADER
					|| targetPcRank == L1Clan.CLAN_RANK_SUBLEADER
					|| targetPcRank == L1Clan.CLAN_RANK_GUARDIAN))
				){ // 任命対象が自分のランクと同じか上位
				pc.sendPackets(new S_ServerMessage(2068)); // 自分より低いランクのみ変更できます。
				return;
			}
			if (pc.getClanId() == targetPc.getClanId()) { // 同じクラン
				try {
					targetPc.setClanRank(rank);
					targetPc.save(); // DBにキャラクター情報を書き込む
					if (targetPc.getOnlineStatus() == 1) {
						targetPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, rank));
						// あなたのランクが%sに変更されました。
					}
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			} else {
				pc.sendPackets(new S_ServerMessage(414)); // 同じ血盟員ではありません。
				return;
			}
		} else if (data == 2) {
			pc.sendPackets(new S_ServerMessage(74, I18N_CLAN_LIST)); // 血盟リスト
		} else if (data == 3) {
			pc.sendPackets(new S_ServerMessage(74, I18N_JOIN_TO_CLAN)); // 血盟に加入
		} else if (data == 4) {
			pc.sendPackets(new S_ServerMessage(74, I18N_LEAVE_THE_CLAN)); // 血盟から脱退
		} else if (data == 5) { // TODO 生存の叫び(CTRL+E)
			if (pc.getWeapon() == null) {
				pc.sendPackets(new S_ServerMessage(1973));
				// TODO \F2武器を装着すると使用できます。
				return;
			}
			if (pc.getCurrentHp() >= pc.getMaxHp()) {
				pc.sendPackets(new S_ServerMessage(1974));
				// TODO 生存の叫びは、まだ使用できません。。 
				return;
			}
			if (pc.getFood() >= 225) { // 満腹時
				int addHp = 0;
				int gfxId1 = 744;
				int gfxId2 = 8683;
				long time = pc.getServivalScream();
				long curTime = _cal.getTimeInMillis() / 1000; // 秒
				int n = (int) ((curTime - time) / 60); // 分
				if (n <= 0) {
					pc.sendPackets(new S_ServerMessage(1974));
					// 生存の叫びは、まだ使用できません。
					return;
				} else if (n >= 1 && n <= 29) {
					addHp = (int) (pc.getMaxHp() * (n / 100.0D));
					// 1分につき、1%のHP回復
				} else if (n >= 30) {
					int lv = pc.getWeapon().getEnchantLevel();
					if (lv <= 6) { // TODO マイナス強化の場合、本来の動作が不明
						gfxId1 = 8907;
						gfxId2 = 8684;
						addHp = (int) (pc.getMaxHp() * ((20 + _random.nextInt(20)) / 100.0D));
						// 20%～40%のHP回復
					} else if (lv == 7 || lv == 8){
						gfxId1 = 8909;
						gfxId2 = 8685;
						addHp = (int) (pc.getMaxHp() * ((30 + _random.nextInt(20)) / 100.0D));
						// 30%～50%のHP回復
					} else if (lv == 9 || lv == 10) {
						gfxId1 = 8910;
						gfxId2 = 8773;
						addHp = (int) (pc.getMaxHp() * ((50 + _random.nextInt(10)) / 100.0D));
						// 50%～60%の回復
					} else if (lv  >= 11) {
						gfxId1 = 8908;
						gfxId2 = 8686;
						addHp = (int) (pc.getMaxHp() * (0.7));
						// 70%のHP回復
					}
					S_SkillSound spr1 = new S_SkillSound(pc.getId(), gfxId1);
					S_SkillSound spr2 = new S_SkillSound(pc.getId(), gfxId2);
					pc.sendPackets(spr1);
					pc.broadcastPacket(spr1);
					pc.sendPackets(spr2);
					pc.broadcastPacket(spr2);
				}
				if (addHp != 0) {
					pc.setFood(0);
					pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, (short) 0));
					pc.setCurrentHp(pc.getCurrentHp() + addHp);
				}
			}
		} else if (data == 6) { // TODO 生存の叫び(Alt+0)
			int gfx = 8683;
			long time = pc.getServivalScream();
			long curTime = _cal.getTimeInMillis() / 1000; // 秒
			int n = (int) ((curTime - time) / 60); // 分	
			if (pc.getWeapon() == null) {
				pc.sendPackets(new S_ServerMessage(1973));
				// TODO \F2武器を装着すると使用できます。  
				return;
			}
			if (n >= 30) {
				int lv = pc.getWeapon().getEnchantLevel();
				if (lv <= 6) { // TODO マイナス強化の場合、本来の動作が不明
					gfx = 8684;
				} else if (lv >= 7 && lv <= 8){
					gfx = 8685;
				} else if (lv >= 9 && lv <= 10) {
					gfx = 8773;
				} else if (lv  >= 11) {
					gfx = 8686;
				}
			}
			S_SkillSound spr = new S_SkillSound(pc.getId(), gfx);
			pc.sendPackets(spr);
			pc.broadcastPacket(spr);
		} else if (data == 9){ // マップタイマーの残り時間を表示
			pc.sendPackets(new S_PacketBox(S_PacketBox.DISPLAY_MAP_TIME ,
					pc.getEnterTime(53),   // ギラン監獄
					pc.getEnterTime(78),   // 象牙の塔
					pc.getEnterTime(451),  // ラスタバド ダンジョン
					pc.getEnterTime(30))); // ドラゴンバレー ダンジョン
		}
	}

	@Override
	public String getType() {
		return C_RANK;
	}
}
