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

package jp.l1j.server.model.instance;

import java.util.logging.Logger;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.datatables.NpcTalkDataTable;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1CastleLocation;
import jp.l1j.server.model.L1NpcTalkData;
import jp.l1j.server.model.L1Quest;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.npc.L1NpcHtml;
import jp.l1j.server.packets.server.S_NpcTalkReturn;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Npc;

// Referenced classes of package jp.l1j.server.model:
// L1NpcInstance, L1Teleport, L1NpcTalkData, L1PcInstance,
// L1TeleporterPrices, L1TeleportLocations

public class L1TeleporterInstance extends L1NpcInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public L1TeleporterInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance pc) {
		onAction(pc, 0);
	}

	@Override
	public void onAction(L1PcInstance pc, int skillId) {
		L1Attack attack = new L1Attack(pc, this, skillId);
		attack.calcHit();
		attack.action();
		attack.addChaserAttack();
		attack.addEvilAttack();
		attack.calcDamage();
		attack.calcStaffOfMana();
		attack.addPcPoisonAttack(pc, this);
		attack.commit();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int objid = getId();
		L1NpcTalkData talking = NpcTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().getNpcId());
		int npcid = getNpcTemplate().getNpcId();
		L1Quest quest = pc.getQuest();
		String htmlid = null;

		if (talking != null) {
			if (npcid == 50014) { // ディロン
				if (pc.isWizard()) { // ウィザード
					if (quest.getStep(L1Quest.QUEST_LEVEL30) == 1
							&& !pc.getInventory().checkItem(40579)) { // アンデッドの骨
						htmlid = "dilong1";
					} else {
						htmlid = "dilong3";
					}
				}
			} else if (npcid == 70779) { // ゲートアント
				if (pc.getTempCharGfx() == 1037 || pc.getTempCharGfx() == 2437) { // ジャイアントアント変身
					htmlid = "ants3";
				} else if (pc.getTempCharGfx() == 1039 || pc.getTempCharGfx() == 2438) {// ジャイアントアントソルジャー変身
					if (pc.isCrown()) { // 君主
						if (quest.getStep(L1Quest.QUEST_LEVEL30) == 1) {
							if (pc.getInventory().checkItem(40547)) { // 住民たちの遺品
								htmlid = "antsn";
							} else {
								htmlid = "ants1";
							}
						} else { // Step1以外
							htmlid = "antsn";
						}
					} else { // 君主以外
						htmlid = "antsn";
					}
				}
			} else if (npcid == 70853) { // フェアリープリンセス
				if (pc.isElf()) { // エルフ
					if (quest.getStep(L1Quest.QUEST_LEVEL30) == 1) {
						if (!pc.getInventory().checkItem(40592)) { // 呪われた精霊書
							RandomGenerator random = RandomGeneratorFactory
									.getSharedRandom();
							if (random.nextInt(100) < 50) { // 50%でダークマールダンジョン
								htmlid = "fairyp2";
							} else { // ダークエルフダンジョン
								htmlid = "fairyp1";
							}
						}
					}
				}
			} else if (npcid == 50031) { // セピア
				if (pc.isElf()) { // エルフ
					if (quest.getStep(L1Quest.QUEST_LEVEL45) == 2) {
						if (!pc.getInventory().checkItem(40602)) { // ブルーフルート
							htmlid = "sepia1";
						}
					}
				}
			} else if (npcid == 50043) { // ラムダ
				if (quest.getStep(L1Quest.QUEST_LEVEL50) == L1Quest.QUEST_END) {
					htmlid = "ramuda2";
				} else if (quest.getStep(L1Quest.QUEST_LEVEL50) == 1) { // ディガルディン同意済み
					if (pc.isCrown()) { // 君主
						if (_isNowDely) { // テレポートディレイ中
							htmlid = "ramuda4";
						} else {
							htmlid = "ramudap1";
						}
					} else { // 君主以外
						htmlid = "ramuda1";
					}
				} else {
					htmlid = "ramuda3";
				}
			}
			// 歌う島のテレポーター
			else if (npcid == 50082) {
				if (pc.getLevel() < 13) {
					htmlid = "en0221";
				} else {
					if (pc.isElf()) {
						htmlid = "en0222e";
					} else if (pc.isDarkelf()) {
						htmlid = "en0222d";
					} else {
						htmlid = "en0222";
					}
				}
			}
			// バルニア
			else if (npcid == 50001) {
				if (pc.isElf()) {
					htmlid = "barnia3";
				} else if (pc.isKnight() || pc.isCrown()) {
					htmlid = "barnia2";
				} else if (pc.isWizard() || pc.isDarkelf()) {
					htmlid = "barnia1";
				}
			}
			// デジェネレイト ソウル
			else if (npcid == 71095) {
				if (pc.isDarkelf()) {
					if (pc.getLevel() >= 50) {
						int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
						if (lv50_step == L1Quest.QUEST_END) {
							htmlid = "csoulq3";
						} else if (lv50_step >= 3) {
							boolean find = false;
							for (Object objs : L1World.getInstance().getVisibleObjects(306).values()) {
								if (objs instanceof L1PcInstance) {
									L1PcInstance _pc = (L1PcInstance) objs;
									if (_pc != null) { // 思念のダンジョン内に他PCがいる場合
										find = true;
										htmlid = "csoulqn"; // おまえの邪念はまだ満たされてない。
										break;
									}
								}
							}
							if (!find) {
								htmlid = "csoulq1";
							} else {
								htmlid = "csoulqn";
							}
						}
					}
				}
			}
			// カレン
			else if (npcid == 71013) {
				if (pc.isDarkelf()) {
					if (pc.getLevel() < 13) {
						htmlid = "karen1";
					} else {
						htmlid = "karen4";
					}
				} else {
					htmlid = "karen2";
				}
			}
			// シルベリアテレポーター イリュージョニストアシャ
			else if (npcid == 80173) {
				if (pc.isIllusionist()) {
					htmlid = "asha1";
				} else {
					htmlid = "asha2";
				}
			}
			// ベヒモステレポーター ドラゴンナイトピエナ
			else if (npcid == 80174) {
				if (pc.isDragonKnight()) {
					htmlid = "feaena1";
				} else {
					htmlid = "feaena2";
				}
			}

			// html表示
			if (htmlid != null) { // htmlidが指定されている場合
				pc.sendPackets(new S_NpcTalkReturn(objid, htmlid));
			} else {
				if (pc.getLawful() < -1000) { // プレイヤーがカオティック
					pc.sendPackets(new S_NpcTalkReturn(talking, objid, 2));
				} else {
					pc.sendPackets(new S_NpcTalkReturn(talking, objid, 1));
				}
			}
		} else {
			_log.finest((new StringBuilder())
					.append("No actions for npc id : ").append(objid)
					.toString());
		}
	}

	@Override
	public void onFinalAction(L1PcInstance pc, String action) {
		int objid = getId();
		L1NpcTalkData talking = NpcTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().getNpcId());
		if (action.equalsIgnoreCase("teleportURL")) {
			L1NpcHtml html = new L1NpcHtml(talking.getTeleportURL());
			pc.sendPackets(new S_NpcTalkReturn(objid, html));
			// TODO テレポーター金額表示 start
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 50015: // 話せる島−ルーカス
				price = new String[] { String.valueOf((int) (1500 * rate)) };
				break;
			case 50020: // ケント−スタンリー
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (200 * rate)),
					String.valueOf((int) (200 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (600 * rate)),
					String.valueOf((int) (7100 * rate)) };
				break;
			case 50024: // グルーディン-アスター
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (200 * rate)),
					String.valueOf((int) (200 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (6800 * rate)) };
				break;
			case 50036: // ギラン-ウィルマ
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (700 * rate)),
					String.valueOf((int) (7400 * rate)) };
				break;
			case 50039: // ウェルダン-レスリー
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (400 * rate)),
					String.valueOf((int) (400 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (800 * rate)),
					String.valueOf((int) (7700 * rate)) };
				break;
			case 50044: // アデン-シリウス
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (900 * rate)),
					String.valueOf((int) (7400 * rate)) };
				break;
			case 50046: // アデン-エレリス
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (900 * rate)),
					String.valueOf((int) (7400 * rate)) };
				break;
			case 50051: // オーレン-キリウス
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (900 * rate)),
					String.valueOf((int) (8000 * rate)) };
				break;
			case 50054: // ウィンダーウッド-トレイ
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (200 * rate)),
					String.valueOf((int) (200 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (6500 * rate)) };
				break;
			case 50056: // シルバーナイトタウン-メット
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (700 * rate)),
					String.valueOf((int) (6800 * rate)) };
				break;
			case 50064: // 火田村−デュバル
				price = new String[] { String.valueOf((int) (100 * rate)) };
				break;
			case 50066: // ハイネ-リオル
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (400 * rate)),
					String.valueOf((int) (400 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (800 * rate)),
					String.valueOf((int) (7100 * rate)) };
				break;
			case 50068: // 沈黙の洞窟-ディアノス
				price = new String[] {
					String.valueOf((int) (1500 * rate)),
					String.valueOf((int) (800 * rate)),
					String.valueOf((int) (600 * rate)),
					String.valueOf((int) (1800 * rate)),
					String.valueOf((int) (1800 * rate)),
					String.valueOf((int) (1000 * rate)) };
				break;
			case 50072: // オームの洞窟-ディアルズ
				price = new String[] {
					String.valueOf((int) (2200 * rate)),
					String.valueOf((int) (1800 * rate)),
					String.valueOf((int) (1000 * rate)),
					String.valueOf((int) (1600 * rate)),
					String.valueOf((int) (2200 * rate)),
					String.valueOf((int) (1200 * rate)),
					String.valueOf((int) (1300 * rate)),
					String.valueOf((int) (2000 * rate)),
					String.valueOf((int) (2000 * rate)) };
				break;				
			case 50079: // ディアド要塞-ダニエル
				price = new String[] {
					String.valueOf((int) (550 * rate)),
					String.valueOf((int) (550 * rate)),
					String.valueOf((int) (550 * rate)),
					String.valueOf((int) (600 * rate)),
					String.valueOf((int) (600 * rate)),
					String.valueOf((int) (600 * rate)),
					String.valueOf((int) (650 * rate)),
					String.valueOf((int) (700 * rate)),
					String.valueOf((int) (750 * rate)),
					String.valueOf((int) (750 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (700 * rate)) };
				break;
			case 80146: // シルベリア-シャリエル
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (900 * rate)),
					String.valueOf((int) (8000 * rate)) };
				break;
			case 80132: // ベヒモス-デカピア
				price = new String[] {
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (50 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (120 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (180 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (240 * rate)),
					String.valueOf((int) (400 * rate)),
					String.valueOf((int) (400 * rate)),
					String.valueOf((int) (800 * rate)),
					String.valueOf((int) (7700 * rate)) };
				break;
			case 50026: // グルーディン-市場テレポーター
				price = new String[] {
					String.valueOf((int) (550 * rate)),
					String.valueOf((int) (700 * rate)),
					String.valueOf((int) (810 * rate)) };
				break;
			case 50033: // ギラン-市場テレポーター
				price = new String[] {
					String.valueOf((int) (560 * rate)),
					String.valueOf((int) (720 * rate)),
					String.valueOf((int) (560 * rate)) };
				break;
			case 50049: // オーレン-市場テレポーター
				price = new String[] {
					String.valueOf((int) (1150 * rate)),
					String.valueOf((int) (980 * rate)),
					String.valueOf((int) (590 * rate)) };
				break;
			case 50059: // シルバーナイトタウン-市場テレポーター
				price = new String[] {
					String.valueOf((int) (580 * rate)),
					String.valueOf((int) (680 * rate)),
					String.valueOf((int) (680 * rate)) };
				break;
			case 80259: // オーレン-ピーター
				price = new String[] {
					String.valueOf((int) (5000 * rate)),
					String.valueOf((int) (5000 * rate)),
					String.valueOf((int) (5000 * rate)),
					String.valueOf((int) (10000 * rate)),
					String.valueOf((int) (10000 * rate)) };
				break;
			case 71078: // ドワーフ城-ポワール
				price = new String[] { String.valueOf((int) (10000 * rate)) };
				break;
			case 71080: // ハイネ城-治安団長アミス
				price = new String[] { String.valueOf((int) (10000 * rate)) };
				break;
			case 91003: // ギラン城-侍従長マモン
				price = new String[] { String.valueOf((int) (10000 * rate)) };
				break;
			case 91006: // アデン城-使者フローラ
				price = new String[] { String.valueOf((int) (10000 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
			// TODO テレポーター金額表示end
		}
		// TODO テレポーター狩場金額表示 start
		else if (action.equalsIgnoreCase("teleportURLA")) {
			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 80146: // 　シルベリア-シャリエル
				html = "sharial3";
				price = new String[] {
					String.valueOf((int) (220 * rate)),
					String.valueOf((int) (330 * rate)),
					String.valueOf((int) (330 * rate)),
					String.valueOf((int) (330 * rate)),
					String.valueOf((int) (440 * rate)),
					String.valueOf((int) (440 * rate)),
					String.valueOf((int) (550 * rate)),
					String.valueOf((int) (550 * rate)),
					String.valueOf((int) (550 * rate)),
					String.valueOf((int) (550 * rate)) };
				break;
			case 80132: // 　ベヒモス-デカピア
				html = "dekabia3";
				price = new String[] {
					String.valueOf((int) (100 * rate)),
					String.valueOf((int) (220 * rate)),
					String.valueOf((int) (220 * rate)),
					String.valueOf((int) (220 * rate)),
					String.valueOf((int) (330 * rate)),
					String.valueOf((int) (330 * rate)),
					String.valueOf((int) (330 * rate)),
					String.valueOf((int) (330 * rate)),
					String.valueOf((int) (440 * rate)),
					String.valueOf((int) (440 * rate)) };
				break;
			case 50079: // 　ディアド要塞-ダニエル
				html = "telediad3";
				price = new String[] {
					String.valueOf((int) (700 * rate)),
					String.valueOf((int) (800 * rate)),
					String.valueOf((int) (800 * rate)),
					String.valueOf((int) (1000 * rate)),
					String.valueOf((int) (10000 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
			// TODO テレポーター狩場金額 end
			// } else if (action.equalsIgnoreCase("teleportURLA")) {

			// L1NpcHtml html = new L1NpcHtml(talking.getTeleportURLA());
			// pc.sendPackets(new S_NpcTalkReturn(objid, html));
		} else if (action.equalsIgnoreCase("teleportURLC")) {
			String html = "";
			// TODO テレポーター金額表示 start
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 50020: // ケント−スタンリー
			case 50024: // グルーディン-アスター
			case 50036: // ギラン-ウィルマ
			case 50039: // ウェルダン-レスリー
			case 50044: // アデン-シリウス
			case 50046: // アデン-エレリス
			case 50051: // オーレン-キリウス
			case 50054: // ウィンダーウッド-トレイ
			case 50056: // シルバーナイトタウン-メット
			case 50066: // ハイネ-リオル
				html = "guide_1_2";
				price = new String[] {
					String.valueOf((int) (310 * rate)),
					String.valueOf((int) (310 * rate)),
					String.valueOf((int) (310 * rate)),
					String.valueOf((int) (310 * rate)),
					String.valueOf((int) (710 * rate)),
					String.valueOf((int) (710 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLB")) {
			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 50020: // ケント−スタンリー
			case 50024: // グルーディン-アスター
			case 50036: // ギラン-ウィルマ
			case 50039: // ウェルダン-レスリー
			case 50044: // アデン-シリウス
			case 50046: // アデン-エレリス
			case 50051: // オーレン-キリウス
			case 50054: // ウィンダーウッド-トレイ
			case 50056: // シルバーナイトタウン-メット
			case 50066: // ハイネ-リオル
				html = "guide_1_1";
				price = new String[] {
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (300 * rate)),
					String.valueOf((int) (300 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLD")) {
			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 50020: // ケント−スタンリー
			case 50024: // グルーディン-アスター
			case 50036: // ギラン-ウィルマ
			case 50039: // ウェルダン-レスリー
			case 50044: // アデン-シリウス
			case 50046: // アデン-エレリス
			case 50051: // オーレン-キリウス
			case 50054: // ウィンダーウッド-トレイ
			case 50056: // シルバーナイトタウン-メット
			case 50066: // ハイネ-リオル
				html = "guide_1_3";
				price = new String[] {
					String.valueOf((int) (320 * rate)),
					String.valueOf((int) (320 * rate)),
					String.valueOf((int) (320 * rate)),
					String.valueOf((int) (320 * rate)),
					String.valueOf((int) (420 * rate)),
					String.valueOf((int) (720 * rate)),
					String.valueOf((int) (420 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLF")) {
			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 50020: // ケント−スタンリー
			case 50024: // グルーディン-アスター
			case 50036: // ギラン-ウィルマ
			case 50039: // ウェルダン-レスリー
			case 50044: // アデン-シリウス
			case 50046: // アデン-エレリス
			case 50051: // オーレン-キリウス
			case 50054: // ウィンダーウッド-トレイ
			case 50056: // シルバーナイトタウン-メット
			case 50066: // ハイネ-リオル
				html = "guide_2_2";
				price = new String[] {
					String.valueOf((int) (410 * rate)),
					String.valueOf((int) (410 * rate)),
					String.valueOf((int) (610 * rate)),
					String.valueOf((int) (510 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLE")) {
			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 50020: // ケント−スタンリー
			case 50024: // グルーディン-アスター
			case 50036: // ギラン-ウィルマ
			case 50039: // ウェルダン-レスリー
			case 50044: // アデン-シリウス
			case 50046: // アデン-エレリス
			case 50051: // オーレン-キリウス
			case 50054: // ウィンダーウッド-トレイ
			case 50056: // シルバーナイトタウン-メット
			case 50066: // ハイネ-リオル
				html = "guide_2_1";
				price = new String[] {
					String.valueOf((int) (400 * rate)),
					String.valueOf((int) (400 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (500 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLG")) {
			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 50020: // ケント−スタンリー
			case 50024: // グルーディン-アスター
			case 50036: // ギラン-ウィルマ
			case 50039: // ウェルダン-レスリー
			case 50044: // アデン-シリウス
			case 50046: // アデン-エレリス
			case 50051: // オーレン-キリウス
			case 50054: // ウィンダーウッド-トレイ
			case 50056: // シルバーナイトタウン-メット
			case 50066: // ハイネ-リオル
				html = "guide_2_3";
				price = new String[] {
					String.valueOf((int) (420 * rate)),
					String.valueOf((int) (720 * rate)),
					String.valueOf((int) (620 * rate)),
					String.valueOf((int) (620 * rate)),
					String.valueOf((int) (620 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLI")) {
			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 50020: // ケント−スタンリー
			case 50024: // グルーディン-アスター
			case 50036: // ギラン-ウィルマ
			case 50039: // ウェルダン-レスリー
			case 50044: // アデン-シリウス
			case 50046: // アデン-エレリス
			case 50051: // オーレン-キリウス
			case 50054: // ウィンダーウッド-トレイ
			case 50056: // シルバーナイトタウン-メット
			case 50066: // ハイネ-リオル
				html = "guide_3_2";
				price = new String[] {
					String.valueOf((int) (510 * rate)),
					String.valueOf((int) (510 * rate)),
					String.valueOf((int) (510 * rate)),
					String.valueOf((int) (510 * rate)),
					String.valueOf((int) (1010 * rate)),
					String.valueOf((int) (810 * rate)),
					String.valueOf((int) (610 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLH")) {
			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 50020: // ケント−スタンリー
			case 50024: // グルーディン-アスター
			case 50036: // ギラン-ウィルマ
			case 50039: // ウェルダン-レスリー
			case 50044: // アデン-シリウス
			case 50046: // アデン-エレリス
			case 50051: // オーレン-キリウス
			case 50054: // ウィンダーウッド-トレイ
			case 50056: // シルバーナイトタウン-メット
			case 50066: // ハイネ-リオル
				html = "guide_3_1";
				price = new String[] {
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (500 * rate)),
					String.valueOf((int) (800 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLK")) {
			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 50020: // ケント−スタンリー
			case 50024: // グルーディン-アスター
			case 50036: // ギラン-ウィルマ
			case 50039: // ウェルダン-レスリー
			case 50044: // アデン-シリウス
			case 50046: // アデン-エレリス
			case 50051: // オーレン-キリウス
			case 50054: // ウィンダーウッド-トレイ
			case 50056: // シルバーナイトタウン-メット
			case 50066: // ハイネ-リオル
				html = "guide_4";
				price = new String[] {
					String.valueOf((int) (520 * rate)),
					String.valueOf((int) (520 * rate)),
					String.valueOf((int) (520 * rate)),
					String.valueOf((int) (520 * rate)),
					String.valueOf((int) (520 * rate)),
					String.valueOf((int) (720 * rate)),
					String.valueOf((int) (720 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLJ")) {
			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().getNpcId();
			double rate = getFeeRateByNpcId(npcid); // 物価(税率 - 10%)を加算;
			switch (npcid) {
			case 50020: // ケント−スタンリー
			case 50024: // グルーディン-アスター
			case 50036: // ギラン-ウィルマ
			case 50039: // ウェルダン-レスリー
			case 50044: // アデン-シリウス
			case 50046: // アデン-エレリス
			case 50051: // オーレン-キリウス
			case 50054: // ウィンダーウッド-トレイ
			case 50056: // シルバーナイトタウン-メット
			case 50066: // ハイネ-リオル
				html = "guide_3_3";
				price = new String[] {
					String.valueOf((int) (520 * rate)),
					String.valueOf((int) (520 * rate)),
					String.valueOf((int) (520 * rate)),
					String.valueOf((int) (520 * rate)),
					String.valueOf((int) (520 * rate)),
					String.valueOf((int) (720 * rate)),
					String.valueOf((int) (720 * rate)) };
				break;
			default:
				price = new String[] { "" };
			}
			pc.sendPackets(new S_NpcTalkReturn(objid, html, price));
		} else if(action.equalsIgnoreCase("teleportURLL")) { // TODO SKTテレポーター メット
			String html = "guidestart";
			pc.sendPackets(new S_NpcTalkReturn(objid, html));
		}
		if (action.startsWith("teleport ")) {
			_log.finest((new StringBuilder()).append("Setting action to : ")
					.append(action).toString());
			doFinalAction(pc, action);
		}
	}

	private void doFinalAction(L1PcInstance pc, String action) {
		int objid = getId();

		int npcid = getNpcTemplate().getNpcId();
		String htmlid = null;
		boolean isTeleport = true;

		if (npcid == 50014) { // ディロン
			if (!pc.getInventory().checkItem(40581)) { // アンデッドのキー
				isTeleport = false;
				htmlid = "dilongn";
			}
		} else if (npcid == 50043) { // ラムダ
			if (_isNowDely) { // テレポートディレイ中
				isTeleport = false;
			}
		} else if (npcid == 50625) { // 古代人（Lv50クエスト古代の空間2F）
			if (_isNowDely) { // テレポートディレイ中
				isTeleport = false;
			}
		}

		if (isTeleport) { // テレポート実行
			try {
				// ミュータントアントダンジョン(君主Lv30クエスト)
				if (action.equalsIgnoreCase("teleport mutant-dungen")) {
					// 3マス以内のPc
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(pc, 3)) {
						if (otherPc.getClanId() == pc.getClanId()
								&& otherPc.getId() != pc.getId()) {
							L1Teleport.teleport(otherPc, 32740, 32800,
									(short) 217, 5, true);
						}
					}
					L1Teleport.teleport(pc, 32740, 32800, (short) 217, 5, true);
				}
				// 試練のダンジョン（ウィザードLv30クエスト）
				else if (action.equalsIgnoreCase("teleport mage-quest-dungen")) {
					L1Teleport.teleport(pc, 32791, 32788, (short) 201, 5, true);
				} else if (action.equalsIgnoreCase("teleport 29")) { // ラムダ
					L1PcInstance kni = null;
					L1PcInstance elf = null;
					L1PcInstance wiz = null;
					// 3マス以内のPc
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(pc, 3)) {
						L1Quest quest = otherPc.getQuest();
						if (otherPc.isKnight() // ナイト
								&& quest.getStep(L1Quest.QUEST_LEVEL50) == 1) { // ディガルディン同意済み
							if (kni == null) {
								kni = otherPc;
							}
						} else if (otherPc.isElf() // エルフ
								&& quest.getStep(L1Quest.QUEST_LEVEL50) == 1) { // ディガルディン同意済み
							if (elf == null) {
								elf = otherPc;
							}
						} else if (otherPc.isWizard() // ウィザード
								&& quest.getStep(L1Quest.QUEST_LEVEL50) == 1) { // ディガルディン同意済み
							if (wiz == null) {
								wiz = otherPc;
							}
						}
					}
					if (kni != null && elf != null && wiz != null) { // 全クラス揃っている
						L1Teleport.teleport(pc, 32723, 32850, (short) 2000, 2,
								true);
						L1Teleport.teleport(kni, 32750, 32851, (short) 2000, 6,
								true);
						L1Teleport.teleport(elf, 32878, 32980, (short) 2000, 6,
								true);
						L1Teleport.teleport(wiz, 32876, 33003, (short) 2000, 0,
								true);
						TeleportDelyTimer timer = new TeleportDelyTimer();
						GeneralThreadPool.getInstance().execute(timer);
					}
				} else if (action.equalsIgnoreCase("teleport barlog")) {
					// 古代人（Lv50クエスト古代の空間2F）
					L1Teleport
							.teleport(pc, 32755, 32844, (short) 2002, 5, true);
					TeleportDelyTimer timer = new TeleportDelyTimer();
					GeneralThreadPool.getInstance().execute(timer);
				}
			} catch (Exception e) {
			}
		}
		if (htmlid != null) { // 表示するhtmlがある場合
			pc.sendPackets(new S_NpcTalkReturn(objid, htmlid));
		}
	}

	private double getFeeRateByNpcId(int npcid) {
		double rate = 1.0;
		int tax = L1CastleLocation.getCastleTaxRateByNpcId(npcid);
		if (tax > 0) {
			rate = rate + (tax - 10) / 100;
		}
		return rate;
	}
	
	class TeleportDelyTimer implements Runnable {

		public TeleportDelyTimer() {
		}

		public void run() {
			try {
				_isNowDely = true;
				Thread.sleep(900000); // 15分
			} catch (Exception e) {
				_isNowDely = false;
			}
			_isNowDely = false;
		}
	}

	private boolean _isNowDely = false;
	private static Logger _log = Logger
			.getLogger(jp.l1j.server.model.instance.L1TeleporterInstance.class
					.getName());
}