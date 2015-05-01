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

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import jp.l1j.server.datatables.NpcTalkDataTable;
import jp.l1j.server.datatables.TownTable;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1BugBearRace;
import jp.l1j.server.model.L1CastleLocation;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1NpcTalkData;
import jp.l1j.server.model.L1Quest;
import jp.l1j.server.model.L1TownLocation;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.gametime.L1GameTimeClock;
import jp.l1j.server.model.skill.L1SkillId;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_ChangeHeading;
import jp.l1j.server.packets.server.S_NpcTalkReturn;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillHaste;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.templates.L1Npc;

public class L1MerchantInstance extends L1NpcInstance {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static Logger _log = Logger.getLogger(L1MerchantInstance.class
			.getName());

	/**
	 * @param template
	 */
	public L1MerchantInstance(L1Npc template) {
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
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int objid = getId();
		L1NpcTalkData talking = NpcTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().getNpcId());
		int npcid = getNpcTemplate().getNpcId();
		L1Quest quest = pc.getQuest();
		String htmlid = null;
		String[] htmldata = null;

		int pcX = pc.getX();
		int pcY = pc.getY();
		int npcX = getX();
		int npcY = getY();

		if (getNpcTemplate().getChangeHead()) {
			if (pcX == npcX && pcY < npcY) {
				setHeading(0);
			} else if (pcX > npcX && pcY < npcY) {
				setHeading(1);
			} else if (pcX > npcX && pcY == npcY) {
				setHeading(2);
			} else if (pcX > npcX && pcY > npcY) {
				setHeading(3);
			} else if (pcX == npcX && pcY > npcY) {
				setHeading(4);
			} else if (pcX < npcX && pcY > npcY) {
				setHeading(5);
			} else if (pcX < npcX && pcY == npcY) {
				setHeading(6);
			} else if (pcX < npcX && pcY < npcY) {
				setHeading(7);
			}
			broadcastPacket(new S_ChangeHeading(this));

			synchronized (this) {
				if (_monitor != null) {
					_monitor.cancel();
				}
				setRest(true);
				_monitor = new RestMonitor();
				_restTimer.schedule(_monitor, REST_MILLISEC);
			}
		}

		if (talking != null) {
			if (npcid == 70841) { // ルーディエル
				if (pc.isElf()) { // エルフ
					htmlid = "luudielE1";
				} else if (pc.isDarkelf()) { // ダークエルフ
					htmlid = "luudielCE1";
				} else {
					htmlid = "luudiel1";
				}
			} else if (npcid == 70522) { // グンター
				if (pc.isCrown()) { // 君主
					if (pc.getLevel() >= 15) {
						int lv15_step = quest.getStep(L1Quest.QUEST_LEVEL15);
						if (lv15_step == 2 || lv15_step == L1Quest.QUEST_END) { // クリア済み
							htmlid = "gunterp11";
						} else {
							htmlid = "gunterp9";
						}
					} else { // Lv15未満
						htmlid = "gunterp12";
					}
				} else if (pc.isKnight()) { // ナイト
					int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
					if (lv30_step == 0) { // 未開始
						htmlid = "gunterk9";
					} else if (lv30_step == 1) {
						htmlid = "gunterkE1";
					} else if (lv30_step == 2) { // グンター同意済み
						htmlid = "gunterkE2";
					} else if (lv30_step >= 3) { // グンター終了済み
						htmlid = "gunterkE3";
					}
				} else if (pc.isElf()) { // エルフ
					htmlid = "guntere1";
				} else if (pc.isWizard()) { // ウィザード
					htmlid = "gunterw1";
				} else if (pc.isDarkelf()) { // ダークエルフ
					htmlid = "gunterde1";
				}
			} else if (npcid == 70653) { // マシャー
				if (pc.isCrown()) { // 君主
					if (pc.getLevel() >= 45) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL30)) { // lv30クリア済み
							int lv45_step = quest
									.getStep(L1Quest.QUEST_LEVEL45);
							if (lv45_step == L1Quest.QUEST_END) { // クリア済み
								htmlid = "masha4";
							} else if (lv45_step >= 1) { // 同意済み
								htmlid = "masha3";
							} else { // 未同意
								htmlid = "masha1";
							}
						}
					}
				} else if (pc.isKnight()) { // ナイト
					if (pc.getLevel() >= 45) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL30)) { // Lv30クエスト終了済み
							int lv45_step = quest
									.getStep(L1Quest.QUEST_LEVEL45);
							if (lv45_step == L1Quest.QUEST_END) { // クリア済み
								htmlid = "mashak3";
							} else if (lv45_step == 0) { // 未開始
								htmlid = "mashak1";
							} else if (lv45_step >= 1) { // 同意済み
								htmlid = "mashak2";
							}
						}
					}
				} else if (pc.isElf()) { // エルフ
					if (pc.getLevel() >= 45) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL30)) { // Lv30クエスト終了済み
							int lv45_step = quest
									.getStep(L1Quest.QUEST_LEVEL45);
							if (lv45_step == L1Quest.QUEST_END) { // クリア済み
								htmlid = "mashae3";
							} else if (lv45_step >= 1) { // 同意済み
								htmlid = "mashae2";
							} else { // 未同意
								htmlid = "mashae1";
							}
						}
					}
				}
			} else if (npcid == 70554) { // ゼロ
				if (pc.isCrown()) { // 君主
					if (pc.getLevel() >= 15) {
						int lv15_step = quest.getStep(L1Quest.QUEST_LEVEL15);
						if (lv15_step == 1) { // ゼロクリア済み
							htmlid = "zero5";
						} else if (lv15_step == L1Quest.QUEST_END) { // ゼロ、グンタークリア済み
							htmlid = "zero1";// 6
						} else {
							htmlid = "zero1";
						}
					} else { // Lv15未満
						htmlid = "zero6";
					}
				}
			} else if (npcid == 70783) { // アリア
				if (pc.isCrown()) { // 君主
					if (pc.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) { // lv15試練クリア済み
							int lv30_step = quest
									.getStep(L1Quest.QUEST_LEVEL30);
							if (lv30_step == L1Quest.QUEST_END) { // クリア済み
								htmlid = "aria3";
							} else if (lv30_step == 1) { // 同意済み
								htmlid = "aria2";
							} else { // 未同意
								htmlid = "aria1";
							}
						}
					}
				}
			} else if (npcid == 70782) { // サーチアント
				if (pc.getTempCharGfx() == 1037 || pc.getTempCharGfx() == 2437) {// ジャイアントアント変身
					if (pc.isCrown()) { // 君主
						if (quest.getStep(L1Quest.QUEST_LEVEL30) == 1) {
							htmlid = "ant1";
						} else {
							htmlid = "ant3";
						}
					} else { // 君主以外
						htmlid = "ant3";
					}
				}
			} else if (npcid == 70545) { // リチャード
				if (pc.isCrown()) { // 君主
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (lv45_step >= 1 && lv45_step != L1Quest.QUEST_END) { // 開始かつ未終了
						if (pc.getInventory().checkItem(40586)) { // 王家の紋章(左)
							htmlid = "richard4";
						} else {
							htmlid = "richard1";
						}
					}
				}
			} else if (npcid == 70739) { // ディカルデン
				int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
				int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
				if ((pc.getLevel() >= 50) && (lv45_step == L1Quest.QUEST_END)) {
					if (pc.isCrown()) {
						if (lv50_step == 0) {
							htmlid = "dicardingp1";
						} else if (lv50_step == L1Quest.QUEST_END) {
							htmlid = "dicardingp15";
						} else if (lv50_step >= 5) {
							if (pc.getInventory().checkItem(49241, 1)) {
								htmlid = "dicardingp13";
							} else {
								htmlid = "dicardingp11";
							}
						} else if (lv50_step >= 4) {
							htmlid = "dicardingp10";
						} else if (lv50_step >= 3) {
							htmlid = "dicardingp8";
						} else if (lv50_step >= 2) {
							htmlid = "dicardingp5";
						} else if (lv50_step >= 1) {
							htmlid = "dicardingp4";
						}
					} else if (pc.isKnight()) {
						if (lv50_step == 0) {
							htmlid = "dicardingk1";
						} else if (lv50_step == L1Quest.QUEST_END) {
							htmlid = "dicardingk16";
						} else if (lv50_step >= 5) {
							if (pc.getInventory().checkItem(49241, 1)) {
								htmlid = "dicardingk14";
							} else {
								htmlid = "dicardingk12";
							}
						} else if (lv50_step >= 4) {
							htmlid = "dicardingk11";
						} else if (lv50_step >= 3) {
							if (pc.getInventory().checkItem(49161, 10)) {
								htmlid = "dicardingk9";
							} else {
								htmlid = "dicardingk8";
							}
						} else if (lv50_step >= 2) {
							htmlid = "dicardingk5";
						} else if (lv50_step >= 1) {
							htmlid = "dicardingk4";
						}
					} else if (pc.isElf()) {
						if (lv50_step == 0) {
							htmlid = "dicardinge1";
						} else if (lv50_step == L1Quest.QUEST_END) {
							htmlid = "dicardinge17";
						} else if (lv50_step >= 5) {
							if (pc.getInventory().checkItem(49241, 1)) {
								htmlid = "dicardinge15";
							} else {
								htmlid = "dicardinge13";
							}
						} else if (lv50_step >= 4) {
							htmlid = "dicardinge9";
						} else if (lv50_step >= 3) {
							htmlid = "dicardinge8";
						} else if (lv50_step >= 2) {
							htmlid = "dicardinge5";
						} else if (lv50_step >= 1) {
							htmlid = "dicardinge4";
						}
					} else if (pc.isWizard()) {
						if (lv50_step == 0) {
							htmlid = "dicardingw1";
						} else if (lv50_step == L1Quest.QUEST_END) {
							htmlid = "dicardingw15";
						} else if (lv50_step >= 5) {
							if (pc.getInventory().checkItem(49241, 1)) {
								htmlid = "dicardingw13";
							} else {
								htmlid = "dicardingw11";
							}
						} else if (lv50_step >= 4) {
							htmlid = "dicardingw10";
						} else if (lv50_step >= 3) {
							htmlid = "dicardingw6";
						} else if (lv50_step >= 2) {
							if (pc.getInventory().checkItem(49164, 1)) {
								htmlid = "dicardingw5";
							}
						} else if (lv50_step >= 1) {
							htmlid = "dicardingw4";
						}
					}
				}
			} else if (npcid == 91307) { // 捨てられた肉体
				if (pc.getInventory().checkItem(49241, 1)) {
					htmlid ="50q_pout1";
				} else {
					htmlid = "50q_pout";
				}
			} else if (npcid == 91308) { // 捨てられた肉体
				if (pc.isCrown()) {
					htmlid = "rtf01";
				} else if (pc.isKnight()) {
					htmlid = "rtf02";
				} else if (pc.isElf()) {
					htmlid = "rtf03";
				} else if (pc.isWizard()) {
					htmlid = "rtf04";
				}
			} else if (npcid == 91299) { // 聖所の入口
				int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
				int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
				if ((pc.getLevel() >= 50) && (lv45_step == L1Quest.QUEST_END)) {
					if (pc.isCrown() && (lv50_step == 4)) {
						htmlid = "50quest_p";
					} else if (pc.isKnight() && (lv50_step == 4)) {
						htmlid = "50quest_k";
					} else if (pc.isElf() && (lv50_step == 5)) {
						htmlid = "50quest_e";
					} else if (pc.isWizard() && (lv50_step == 4)) {
						htmlid = "50quest_w";
					}
				}
			} else if (npcid == 91298) { // 行政官キーホル
				int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
				if (pc.isCrown()) {
					if (lv50_step == 3) {
						htmlid = "kiholl1";
					} else {
						htmlid = "kiholl0";
					}
				} else {
					htmlid = "kiholl0";
				}
			} else if (npcid == 91311) { // ディカルデンの諜報員
				int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
				if (pc.isWizard()) {
					if (lv50_step == L1Quest.QUEST_END) {
						htmlid = "dspym5";
					} else if (lv50_step >= 3) {
						htmlid = "dspym4";
					} else if (lv50_step >= 2) {
						htmlid = "dspym3";
					} else if (lv50_step == 1) {
						htmlid = "dspym1";
					}
				} else {
					htmlid = "dspym5";
				}
			} else if (npcid == 70776) { // メグ
				if (pc.isCrown()) { // 君主
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (lv45_step == 1) {
						htmlid = "meg1";
					} else if (lv45_step >= 2 && lv45_step <= 3) { // メグ同意済み
						htmlid = "meg2";
					} else if (lv45_step >= 4) { // メグクリア済み
						htmlid = "meg3";
					}
				}
			} else if (npcid == 71200) { // 白魔術師 ピエタ
				if (pc.isCrown()) { // 君主
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (lv45_step == L1Quest.QUEST_END) {
						htmlid = "pieta8";
					} else if (lv45_step > 2) {
						if (pc.getInventory().checkItem(40568)) {
							htmlid = "pieta8";
						} else {
							htmlid = "pieta6";
						}
					} else if (lv45_step == 2) {
						if(pc.getInventory().checkItem(41422)) {
							htmlid = "pieta4";
						} else {
							htmlid = "pieta2";
						}
					} else {
						htmlid = "pieta1";
					}
				} else {
					htmlid = "pieta1";
				}
				// } else if (npcid == 71200) { // 白魔術師 ピエタ
				// if (pc.isCrown()) { // 君主
				// int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
				// if (lv45_step >= 6 && lv45_step == L1Quest.QUEST_END ) {
				// //メグクリア済みor終了
				// htmlid = "pieta9";
				// } else if (lv45_step == 2) { // クエスト開始前・メグ同意済み
				// htmlid = "pieta2";
				// } else if (lv45_step == 2 ||
				// pc.getInventory().checkItem(41422) ) {//
				// 輝きを失った魂保持
				// htmlid = "pieta4";
				// } else if (lv45_step == 3) { // 輝きを失った魂入後
				// htmlid = "pieta6";
				// } else {//lv45未満orクエスト30未
				// htmlid = "pieta8";
				// }
				// } else { // 君主以外
				// htmlid = "pieta1";
				// }
				// } else if (npcid == 70751) { // ブラッド
				// if (pc.isCrown()) { // 君主
				// if (pc.getLevel() >= 45) {
				// if (quest.getStep(L1Quest.QUEST_LEVEL45) == 2) { //
				// メグ同意済み
				// htmlid = "brad1";
				// }
				// }
				// }
			} else if (npcid == 70798) { // リッキー
				if (pc.isKnight()) { // ナイト
					if (pc.getLevel() >= 15) {
						int lv15_step = quest.getStep(L1Quest.QUEST_LEVEL15);
						if (lv15_step >= 1) { // リッキークリア済み
							htmlid = "riky5";
						} else {
							htmlid = "riky1";
						}
					} else { // Lv15未満
						htmlid = "riky6";
					}
				}
			} else if (npcid == 70802) { // アノン
				if (pc.isKnight()) { // ナイト
					if (pc.getLevel() >= 15) {
						int lv15_step = quest.getStep(L1Quest.QUEST_LEVEL15);
						if (lv15_step == L1Quest.QUEST_END) { // アノンクリア済み
							htmlid = "aanon7";
						} else if (lv15_step == 1) { // リッキークリア済み
							htmlid = "aanon4";
						}
					}
				}
			} else if (npcid == 70775) { // マーク
				if (pc.isKnight()) { // ナイト
					if (pc.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) { // LV15クエスト終了済み
							int lv30_step = quest
									.getStep(L1Quest.QUEST_LEVEL30);
							if (lv30_step == 0) { // 未開始
								htmlid = "mark1";
							} else {
								htmlid = "mark2";
							}
						}
					}
				}
			} else if (npcid == 70794) { // ゲラド
				if (pc.isCrown()) { // 君主
					htmlid = "gerardp1";
				} else if (pc.isKnight()) { // ナイト
					int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
					if (lv30_step == L1Quest.QUEST_END) { // ゲラド終了済み
						htmlid = "gerardkEcg";
					} else if (lv30_step < 3) { // グンター未終了
						htmlid = "gerardk7";
					} else if (lv30_step == 3) { // グンター終了済み
						htmlid = "gerardkE1";
					} else if (lv30_step == 4) { // ゲラド同意済み
						htmlid = "gerardkE2";
					} else if (lv30_step == 5) { // ラミアの鱗 終了済み
						htmlid = "gerardkE3";
					} else if (lv30_step >= 6) { // 復活のポーション同意済み
						htmlid = "gerardkE4";
					}
				} else if (pc.isElf()) { // エルフ
					htmlid = "gerarde1";
				} else if (pc.isWizard()) { // ウィザード
					htmlid = "gerardw1";
				} else if (pc.isDarkelf()) { // ダークエルフ
					htmlid = "gerardde1";
				}
			} else if (npcid == 70555) { // ジム
				if (pc.getTempCharGfx() == 2374) { // スケルトン変身
					if (pc.isKnight()) { // ナイト
						if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
							htmlid = "jimcg";
						} else {
							htmlid = "jim2";
						}
					} else { // ナイト以外
						htmlid = "jim4";
					}
				}
			} else if (npcid == 70715) { // ジーム
				if (pc.isKnight()) { // ナイト
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (lv45_step == 1) { // マシャー同意済み
						htmlid = "jimuk1";
					} else if (lv45_step >= 2) { // ジーム同意済み
						htmlid = "jimuk2";
					}
				}
			} else if (npcid == 70711) { // ジャイアント エルダー
				if (pc.isKnight()) { // ナイト
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (lv45_step == 2) { // ジーム同意済み
						if (pc.getInventory().checkItem(20026)) { // ナイトビジョン
							htmlid = "giantk1";
						}
					} else if (lv45_step == 3) { // ジャイアントエルダー同意済み
						htmlid = "giantk2";
					} else if (lv45_step >= 4) { // 古代のキー：上半分
						htmlid = "giantk3";
					}
				}
			} else if (npcid == 70826) { // オス
				if (pc.isElf()) { // エルフ
					if (pc.getLevel() >= 15) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							htmlid = "oth5";
						} else {
							htmlid = "oth1";
						}
					} else { // レベル１５未満
						htmlid = "oth6";
					}
				}
			} else if (npcid == 70844) { // 森とエルフの母
				if (pc.isElf()) { // エルフ
					if (pc.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) { // Lv15終了済み
							int lv30_step = quest
									.getStep(L1Quest.QUEST_LEVEL30);
							if (lv30_step == L1Quest.QUEST_END) { // 終了済み
								htmlid = "motherEE3";
							} else if (lv30_step >= 1) { // 同意済み
								htmlid = "motherEE2";
							} else if (lv30_step <= 0) { // 未同意
								htmlid = "motherEE1";
							}
						} else { // Lv15未終了
							htmlid = "mothere1";
						}
					} else { // Lv30未満
						htmlid = "mothere1";
					}
				}
			} else if (npcid == 70724) { // ヘイト
				if (pc.isElf()) { // エルフ
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (lv45_step >= 4) { // ヘイト終了済み
						htmlid = "heit5";
					} else if (lv45_step >= 3) { // フルート交換済み
						htmlid = "heit3";
					} else if (lv45_step >= 2) { // ヘイト同意済み
						htmlid = "heit2";
					} else if (lv45_step >= 1) { // マシャー同意済み
						htmlid = "heit1";
					}
				}
			} else if (npcid == 70531) { // ゼム
				if (pc.isWizard()) { // ウィザード
					if (pc.getLevel() >= 15) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) { // 終了済み
							htmlid = "jem6";
						} else {
							htmlid = "jem1";
						}
					}
				}
			} else if (npcid == 70009) { // ゲレン
				if (pc.isCrown()) { // 君主
					htmlid = "gerengp1";
				} else if (pc.isKnight()) { // ナイト
					htmlid = "gerengk1";
				} else if (pc.isElf()) { // エルフ
					htmlid = "gerenge1";
				} else if (pc.isWizard()) { // ウィザード
					if (pc.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							int lv30_step = quest
									.getStep(L1Quest.QUEST_LEVEL30);
							if (lv30_step >= 4) { // ゲレン終了済み
								htmlid = "gerengw3";
							} else if (lv30_step >= 3) { // 要求済み
								htmlid = "gerengT4";
							} else if (lv30_step >= 2) { // アンデッドの骨交換済み
								htmlid = "gerengT3";
							} else if (lv30_step >= 1) { // 同意済み
								htmlid = "gerengT2";
							} else { // 未同意
								htmlid = "gerengT1";
							}
						} else { // Lv15クエスト未終了
							htmlid = "gerengw3";
						}
					} else { // Lv30未満
						htmlid = "gerengw3";
					}
				} else if (pc.isDarkelf()) { // ダークエルフ
					htmlid = "gerengde1";
				}
			} else if (npcid == 70763) { // タラス
				if (pc.isWizard()) { // ウィザード
					int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
					if (lv30_step == L1Quest.QUEST_END) {
						if (pc.getLevel() >= 45) {
							int lv45_step = quest
									.getStep(L1Quest.QUEST_LEVEL45);
							if (lv45_step >= 1
									&& lv45_step != L1Quest.QUEST_END) { // 同意済み
								htmlid = "talassmq2";
							} else if (lv45_step <= 0) { // 未同意
								htmlid = "talassmq1";
							}
						}
					} else if (lv30_step == 4) {
						htmlid = "talassE1";
					} else if (lv30_step == 5) {
						htmlid = "talassE2";
					}
				}
			} else if (npcid == 81105) { // 神秘の岩
				if (pc.isWizard()) { // ウィザード
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (lv45_step >= 3) { // 神秘の岩終了済み
						htmlid = "stoenm3";
					} else if (lv45_step >= 2) { // 神秘の岩 同意済み
						htmlid = "stoenm2";
					} else if (lv45_step >= 1) { // タラス 同意済み
						htmlid = "stoenm1";
					}
				}
			} else if (npcid == 70739) { // ディガルディン
				if (pc.getLevel() >= 50) {
					int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
					if (lv50_step == L1Quest.QUEST_END) {
						if (pc.isCrown()) { // 君主
							htmlid = "dicardingp3";
						} else if (pc.isKnight()) { // ナイト
							htmlid = "dicardingk3";
						} else if (pc.isElf()) { // エルフ
							htmlid = "dicardinge3";
						} else if (pc.isWizard()) { // ウィザード
							htmlid = "dicardingw3";
						} else if (pc.isDarkelf()) { // ダークエルフ
							htmlid = "dicarding";
						}
					} else if (lv50_step >= 1) { // ディガルディン 同意済み
						if (pc.isCrown()) { // 君主
							htmlid = "dicardingp2";
						} else if (pc.isKnight()) { // ナイト
							htmlid = "dicardingk2";
						} else if (pc.isElf()) { // エルフ
							htmlid = "dicardinge2";
						} else if (pc.isWizard()) { // ウィザード
							htmlid = "dicardingw2";
						} else if (pc.isDarkelf()) { // ダークエルフ
							htmlid = "dicarding";
						}
					} else if (lv50_step >= 0) {
						if (pc.isCrown()) { // 君主
							htmlid = "dicardingp1";
						} else if (pc.isKnight()) { // ナイト
							htmlid = "dicardingk1";
						} else if (pc.isElf()) { // エルフ
							htmlid = "dicardinge1";
						} else if (pc.isWizard()) { // ウィザード
							htmlid = "dicardingw1";
						} else if (pc.isDarkelf()) { // ダークエルフ
							htmlid = "dicarding";
						}
					} else {
						htmlid = "dicarding";
					}
				} else { // Lv50未満
					htmlid = "dicarding";
				}
			} else if (npcid == 70885) { // カーン
				if (pc.isDarkelf()) { // ダークエルフ
					if (pc.getLevel() >= 15) {
						int lv15_step = quest.getStep(L1Quest.QUEST_LEVEL15);
						if (lv15_step == L1Quest.QUEST_END) { // 終了済み
							htmlid = "kanguard3";
						} else if (lv15_step >= 1) { // 同意済み
							htmlid = "kanguard2";
						} else { // 未同意
							htmlid = "kanguard1";
						}
					} else { // Lv15未満
						htmlid = "kanguard5";
					}
				}
			} else if (npcid == 70892) { // ロンドゥ
				if (pc.isDarkelf()) { // ダークエルフ
					if (pc.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							int lv30_step = quest
									.getStep(L1Quest.QUEST_LEVEL30);
							if (lv30_step == L1Quest.QUEST_END) { // 終了済み
								htmlid = "ronde5";
							} else if (lv30_step >= 2) { // 名簿交換済み
								htmlid = "ronde3";
							} else if (lv30_step >= 1) { // 同意済み
								htmlid = "ronde2";
							} else { // 未同意
								htmlid = "ronde1";
							}
						} else { // Lv15クエスト未終了
							htmlid = "ronde7";
						}
					} else { // Lv30未満
						htmlid = "ronde7";
					}
				}
			} else if (npcid == 70895) { // ブルディカ
				if (pc.isDarkelf()) { // ダークエルフ
					if (pc.getLevel() >= 45) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
							int lv45_step = quest
									.getStep(L1Quest.QUEST_LEVEL45);
							if (lv45_step == L1Quest.QUEST_END) { // 終了済み
								if (pc.getLevel() < 50) { // Lv50未満
									htmlid = "bluedikaq3";
								} else {
									int lv50_step = quest
											.getStep(L1Quest.QUEST_LEVEL50);
									if (lv50_step == L1Quest.QUEST_END) { // 終了済み
										htmlid = "bluedikaq8";
									} else if (lv50_step >= 1) { // 同意済み
										htmlid = "bluedikaq7";
									} else { // 未同意
										htmlid = "bluedikaq6";
									}
								}
							} else if (lv45_step >= 1) { // 同意済み
								htmlid = "bluedikaq2";
							} else { // 未同意
								htmlid = "bluedikaq1";
							}
						} else { // Lv30クエスト未終了
							htmlid = "bluedikaq5";
						}
					} else { // Lv45未満
						htmlid = "bluedikaq5";
					}
				}
			} else if (npcid == 70904) { // クプ
				if (pc.isDarkelf()) {
					if (quest.getStep(L1Quest.QUEST_LEVEL45) == 1) { // ブルディカ同意済み
						htmlid = "koup12";
					}
				}
			} else if (npcid == 70906) { // キマ
				if (pc.isDarkelf()) {
					if (pc.getLevel() >= 50) {
						int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
						if ((lv50_step == L1Quest.QUEST_END) || (lv50_step >= 4)) {
							htmlid = "kimaq4";
						} else if (lv50_step == 3) {
							htmlid = "kimaq3";
						} else {
							if (lv50_step >= 1) {
								htmlid = "kimaq1";
								quest.setStep(L1Quest.QUEST_LEVEL50, 2);
							} else {
								htmlid = "kima1";
							}
						}
					}
				}
			} else if (npcid == 70824) { // アサシンマスターの追従者
				if (pc.isDarkelf()) {
					if (pc.getTempCharGfx() == 3634 || pc.getTempCharGfx() == 8783) { // アサシン変身
						int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
						if (lv45_step == 1) {
							htmlid = "assassin1";
						} else if (lv45_step == 2) {
							htmlid = "assassin2";
						} else {
							htmlid = "assassin3";
						}
					} else { // ダークエルフ以外
						htmlid = "assassin3";
					}
				}
			} else if (npcid == 70744) { // ロジェ
				if (pc.isDarkelf()) { // ダークエルフ
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (lv45_step >= 5) { // ロジェ２回目同意済み
						htmlid = "roje14";
					} else if (lv45_step >= 4) { // イエティの頭部 交換済み
						htmlid = "roje13";
					} else if (lv45_step >= 3) { // ロジェ 同意済み
						htmlid = "roje12";
					} else if (lv45_step >= 2) { // アサシンマスターの追従者 同意済み
						htmlid = "roje11";
					} else { // アサシンマスターの追従者 未同意
						htmlid = "roje15";
					}
				}
			} else if (npcid == 70811) { // ライラ
				if (quest.getStep(L1Quest.QUEST_LYRA) >= 1) { // 契約済み
					htmlid = "lyraEv3";
				} else { // 未契約
					htmlid = "lyraEv1";
				}
			} else if (npcid == 70087) { // セディア
				if (pc.isDarkelf()) {
					htmlid = "sedia";
				}
			} else if (npcid == 70099) { // クーパー
				if (!quest.isEnd(L1Quest.QUEST_OILSKINMANT)) {
					if (pc.getLevel() > 13) {
						htmlid = "kuper1";
					}
				}
			} else if (npcid == 70796) { // ダンハム
				if (!quest.isEnd(L1Quest.QUEST_OILSKINMANT)) {
					if (pc.getLevel() > 13) {
						htmlid = "dunham1";
					}
				}
			} else if (npcid == 70011) { // 話せる島の船着き管理人
				int time = L1GameTimeClock.getInstance().currentTime()
						.getSeconds() % 86400;
				if (time < 60 * 60 * 6 || time > 60 * 60 * 20) { // 20:00〜6:00
					htmlid = "shipEvI6";
				}
			} else if (npcid == 70553) { // ケント城 侍従長 イスマエル
				boolean hascastle = checkHasCastle(pc,
						L1CastleLocation.KENT_CASTLE_ID);
				if (hascastle) { // 城主クラン員
					if (checkClanLeader(pc)) { // 血盟主
						htmlid = "ishmael1";
						pc.setTax(true);
					} else {
						htmlid = "ishmael6";
						htmldata = new String[] { pc.getName() };
					}
				} else {
					htmlid = "ishmael7";
				}
			} else if (npcid == 70822) { // オークの森 セゲム アトゥバ
				boolean hascastle = checkHasCastle(pc,
						L1CastleLocation.OT_CASTLE_ID);
				if (hascastle) { // 城主クラン員
					if (checkClanLeader(pc)) { // 血盟主
						htmlid = "seghem1";
						pc.setTax(true);
					} else {
						htmlid = "seghem6";
						htmldata = new String[] { pc.getName() };
					}
				} else {
					htmlid = "seghem7";
				}
			} else if (npcid == 70784) { // ウィンダウッド城 侍従長 オスモンド
				boolean hascastle = checkHasCastle(pc,
						L1CastleLocation.WW_CASTLE_ID);
				if (hascastle) { // 城主クラン員
					if (checkClanLeader(pc)) { // 血盟主
						htmlid = "othmond1";
						pc.setTax(true);
					} else {
						htmlid = "othmond6";
						htmldata = new String[] { pc.getName() };
					}
				} else {
					htmlid = "othmond7";
				}
			} else if (npcid == 70623) { // ギラン城 侍従長 オービル
				boolean hascastle = checkHasCastle(pc,
						L1CastleLocation.GIRAN_CASTLE_ID);
				if (hascastle) { // 城主クラン員
					if (checkClanLeader(pc)) { // 血盟主
						htmlid = "orville1";
						pc.setTax(true);
					} else {
						htmlid = "orville6";
						htmldata = new String[] { pc.getName() };
					}
				} else {
					htmlid = "orville7";
				}
			} else if (npcid == 70880) { // ハイネ城 侍従長 フィッシャー
				boolean hascastle = checkHasCastle(pc,
						L1CastleLocation.HEINE_CASTLE_ID);
				if (hascastle) { // 城主クラン員
					if (checkClanLeader(pc)) { // 血盟主
						htmlid = "fisher1";
						pc.setTax(true);
					} else {
						htmlid = "fisher6";
						htmldata = new String[] { pc.getName() };
					}
				} else {
					htmlid = "fisher7";
				}
			} else if (npcid == 70665) { // ドワーフ城 侍従長 ポテンピン
				boolean hascastle = checkHasCastle(pc,
						L1CastleLocation.DOWA_CASTLE_ID);
				if (hascastle) { // 城主クラン員
					if (checkClanLeader(pc)) { // 血盟主
						htmlid = "potempin1";
						pc.setTax(true);
					} else {
						htmlid = "potempin6";
						htmldata = new String[] { pc.getName() };
					}
				} else {
					htmlid = "potempin7";
				}
			} else if (npcid == 70721) { // アデン城 侍従長 ティモン
				boolean hascastle = checkHasCastle(pc,
						L1CastleLocation.ADEN_CASTLE_ID);
				if (hascastle) { // 城主クラン員
					if (checkClanLeader(pc)) { // 血盟主
						htmlid = "timon1";
						pc.setTax(true);
					} else {
						htmlid = "timon6";
						htmldata = new String[] { pc.getName() };
					}
				} else {
					htmlid = "timon7";
				}
			} else if (npcid == 81155) { // ディアド要塞 オーレ
				boolean hascastle = checkHasCastle(pc,
						L1CastleLocation.DIAD_CASTLE_ID);
				if (hascastle) { // 城主クラン員
					if (checkClanLeader(pc)) { // 血盟主
						htmlid = "olle1";
						pc.setTax(true);
					} else {
						htmlid = "olle6";
						htmldata = new String[] { pc.getName() };
					}
				} else {
					htmlid = "olle7";
				}
			} else if (npcid == 80057) { // アルフォンス
				int karmaLevel = pc.getKarmaLevel();
				String[] html1 = { "alfons1", "cbk1", "cbk2", "cbk3", "cbk4",
						"cbk5", "cbk6", "cbk7", "cbk8" }; // 0 ~ 8
				String[] html2 = { "cyk1", "cyk2", "cyk3", "cyk4", "cyk5",
						"cyk6", "cyk7", "cyk8" }; // -1 ~ -8
				if (karmaLevel < 0) {
					htmlid = html2[Math.abs(karmaLevel) - 1];
				} else if (karmaLevel >= 0) {
					htmlid = html1[karmaLevel];
				} else {
					htmlid = "alfons1";
				}
			} else if (npcid == 80058) { // 次元の扉(砂漠)
				int level = pc.getLevel();
				if (level <= 44) {
					htmlid = "cpass03";
				} else if (level <= 51 && 45 <= level) {
					htmlid = "cpass02";
				} else {
					htmlid = "cpass01";
				}
			} else if (npcid == 80059) { // 次元の扉(土)
				if (pc.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (pc.getInventory().checkItem(40921)) { // 元素の支配者
					htmlid = "wpass02";
				} else if (pc.getInventory().checkItem(40917)) { // 地の支配者
					htmlid = "wpass14";
				} else if (pc.getInventory().checkItem(40912) // 風の通行証
						|| pc.getInventory().checkItem(40910) // 水の通行証
						|| pc.getInventory().checkItem(40911)) { // 火の通行証
					htmlid = "wpass04";
				} else if (pc.getInventory().checkItem(40909)) { // 地の通行証
					int count = getNecessarySealCount(pc);
					if (pc.getInventory().checkItem(40913, count)) { // 地の印章
						createRuler(pc, 1, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (pc.getInventory().checkItem(40913)) { // 地の印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
			} else if (npcid == 80060) { // 次元の扉(風)
				if (pc.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (pc.getInventory().checkItem(40921)) { // 元素の支配者
					htmlid = "wpass02";
				} else if (pc.getInventory().checkItem(40920)) { // 風の支配者
					htmlid = "wpass13";
				} else if (pc.getInventory().checkItem(40909) // 地の通行証
						|| pc.getInventory().checkItem(40910) // 水の通行証
						|| pc.getInventory().checkItem(40911)) { // 火の通行証
					htmlid = "wpass04";
				} else if (pc.getInventory().checkItem(40912)) { // 風の通行証
					int count = getNecessarySealCount(pc);
					if (pc.getInventory().checkItem(40916, count)) { // 風の印章
						createRuler(pc, 8, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (pc.getInventory().checkItem(40916)) { // 風の印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
			} else if (npcid == 80061) { // 次元の扉(水)
				if (pc.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (pc.getInventory().checkItem(40921)) { // 元素の支配者
					htmlid = "wpass02";
				} else if (pc.getInventory().checkItem(40918)) { // 水の支配者
					htmlid = "wpass11";
				} else if (pc.getInventory().checkItem(40909) // 地の通行証
						|| pc.getInventory().checkItem(40912) // 風の通行証
						|| pc.getInventory().checkItem(40911)) { // 火の通行証
					htmlid = "wpass04";
				} else if (pc.getInventory().checkItem(40910)) { // 水の通行証
					int count = getNecessarySealCount(pc);
					if (pc.getInventory().checkItem(40914, count)) { // 水の印章
						createRuler(pc, 4, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (pc.getInventory().checkItem(40914)) { // 水の印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
			} else if (npcid == 80062) { // 次元の扉(火)
				if (pc.getKarmaLevel() > 0) {
					htmlid = "cpass03";
				} else if (pc.getInventory().checkItem(40921)) { // 元素の支配者
					htmlid = "wpass02";
				} else if (pc.getInventory().checkItem(40919)) { // 火の支配者
					htmlid = "wpass12";
				} else if (pc.getInventory().checkItem(40909) // 地の通行証
						|| pc.getInventory().checkItem(40912) // 風の通行証
						|| pc.getInventory().checkItem(40910)) { // 水の通行証
					htmlid = "wpass04";
				} else if (pc.getInventory().checkItem(40911)) { // 火の通行証
					int count = getNecessarySealCount(pc);
					if (pc.getInventory().checkItem(40915, count)) { // 火の印章
						createRuler(pc, 2, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (pc.getInventory().checkItem(40915)) { // 火の印章
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
			} else if (npcid == 80065) { // バルログの密偵
				if (pc.getKarmaLevel() < 3) {
					htmlid = "uturn0";
				} else {
					htmlid = "uturn1";
				}
			} else if (npcid == 80047) { // ヤヒの召使
				if (pc.getKarmaLevel() > -3) {
					htmlid = "uhelp1";
				} else {
					htmlid = "uhelp2";
				}
			} else if (npcid == 80049) { // 揺らぐ者
				if (pc.getKarma() <= -10000000) {
					htmlid = "betray11";
				} else {
					htmlid = "betray12";
				}
			} else if (npcid == 80050) { // ヤヒの執政官
				if (pc.getKarmaLevel() > -1) {
					htmlid = "meet103";
				} else {
					htmlid = "meet101";
				}
			} else if (npcid == 80053) { // ヤヒの鍛冶屋
				int karmaLevel = pc.getKarmaLevel();
				if (karmaLevel == 0) {
					htmlid = "aliceyet";
				} else if (karmaLevel >= 1) {
					if (pc.getInventory().checkItem(196)
							|| pc.getInventory().checkItem(197)
							|| pc.getInventory().checkItem(198)
							|| pc.getInventory().checkItem(199)
							|| pc.getInventory().checkItem(200)
							|| pc.getInventory().checkItem(201)
							|| pc.getInventory().checkItem(202)
							|| pc.getInventory().checkItem(203)) {
						htmlid = "alice_gd";
					} else {
						htmlid = "gd";
					}
				} else if (karmaLevel <= -1) {
					if (pc.getInventory().checkItem(40991)) {
						if (karmaLevel <= -1) {
							htmlid = "Mate_1";
						}
					} else if (pc.getInventory().checkItem(196)) {
						if (karmaLevel <= -2) {
							htmlid = "Mate_2";
						} else {
							htmlid = "alice_1";
						}
					} else if (pc.getInventory().checkItem(197)) {
						if (karmaLevel <= -3) {
							htmlid = "Mate_3";
						} else {
							htmlid = "alice_2";
						}
					} else if (pc.getInventory().checkItem(198)) {
						if (karmaLevel <= -4) {
							htmlid = "Mate_4";
						} else {
							htmlid = "alice_3";
						}
					} else if (pc.getInventory().checkItem(199)) {
						if (karmaLevel <= -5) {
							htmlid = "Mate_5";
						} else {
							htmlid = "alice_4";
						}
					} else if (pc.getInventory().checkItem(200)) {
						if (karmaLevel <= -6) {
							htmlid = "Mate_6";
						} else {
							htmlid = "alice_5";
						}
					} else if (pc.getInventory().checkItem(201)) {
						if (karmaLevel <= -7) {
							htmlid = "Mate_7";
						} else {
							htmlid = "alice_6";
						}
					} else if (pc.getInventory().checkItem(202)) {
						if (karmaLevel <= -8) {
							htmlid = "Mate_8";
						} else {
							htmlid = "alice_7";
						}
					} else if (pc.getInventory().checkItem(203)) {
						htmlid = "alice_8";
					} else {
						htmlid = "alice_no";
					}
				}
			} else if (npcid == 80055) { // ヤヒの補佐官
				int amuletLevel = 0;
				if (pc.getInventory().checkItem(20358)
						|| pc.getAdditionalWarehouseInventory().checkItem(20358)) { // 奴隷のアミュレット
					amuletLevel = 1;
				} else if (pc.getInventory().checkItem(20359)
						|| pc.getAdditionalWarehouseInventory().checkItem(20359)) { // 約束のアミュレット
					amuletLevel = 2;
				} else if (pc.getInventory().checkItem(20360)
						|| pc.getAdditionalWarehouseInventory().checkItem(20360)) { // 解放のアミュレット
					amuletLevel = 3;
				} else if (pc.getInventory().checkItem(20361)
						|| pc.getAdditionalWarehouseInventory().checkItem(20361)) { // 猟犬のアミュレット
					amuletLevel = 4;
				} else if (pc.getInventory().checkItem(20362)
						|| pc.getAdditionalWarehouseInventory().checkItem(20362)) { // 魔族のアミュレット
					amuletLevel = 5;
				} else if (pc.getInventory().checkItem(20363)
						|| pc.getAdditionalWarehouseInventory().checkItem(20363)) { // 勇士のアミュレット
					amuletLevel = 6;
				} else if (pc.getInventory().checkItem(20364)
						|| pc.getAdditionalWarehouseInventory().checkItem(20364)) { // 将軍のアミュレット
					amuletLevel = 7;
				} else if (pc.getInventory().checkItem(20365)
						|| pc.getAdditionalWarehouseInventory().checkItem(20365)) { // 大将軍のアミュレット
					amuletLevel = 8;
				}
				if (pc.getKarmaLevel() == -1) {
					if (amuletLevel >= 1) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet1";
					}
				} else if (pc.getKarmaLevel() == -2) {
					if (amuletLevel >= 2) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet2";
					}
				} else if (pc.getKarmaLevel() == -3) {
					if (amuletLevel >= 3) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet3";
					}
				} else if (pc.getKarmaLevel() == -4) {
					if (amuletLevel >= 4) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet4";
					}
				} else if (pc.getKarmaLevel() == -5) {
					if (amuletLevel >= 5) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet5";
					}
				} else if (pc.getKarmaLevel() == -6) {
					if (amuletLevel >= 6) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet6";
					}
				} else if (pc.getKarmaLevel() == -7) {
					if (amuletLevel >= 7) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet7";
					}
				} else if (pc.getKarmaLevel() == -8) {
					if (amuletLevel >= 8) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet8";
					}
				} else {
					htmlid = "uamulet0";
				}
			} else if (npcid == 80056) { // 業の管理者
				if (pc.getKarma() <= -10000000) {
					htmlid = "infamous11";
				} else {
					htmlid = "infamous12";
				}
			} else if (npcid == 80064) { // バルログの執政官
				if (pc.getKarmaLevel() < 1) {
					htmlid = "meet003";
				} else {
					htmlid = "meet001";
				}
			} else if (npcid == 80066) { // 揺らめく者
				if (pc.getKarma() >= 10000000) {
					htmlid = "betray01";
				} else {
					htmlid = "betray02";
				}
			} else if (npcid == 80071) { // バルログの補佐官
				int earringLevel = 0;
				if (pc.getInventory().checkItem(21020)
						|| pc.getAdditionalWarehouseInventory().checkItem(21020)) { // 踊躍のイアリング
					earringLevel = 1;
				} else if (pc.getInventory().checkItem(21021)
						|| pc.getAdditionalWarehouseInventory().checkItem(21021)) { // 双子のイアリング
					earringLevel = 2;
				} else if (pc.getInventory().checkItem(21022)
						|| pc.getAdditionalWarehouseInventory().checkItem(21022)) { // 友好のイアリング
					earringLevel = 3;
				} else if (pc.getInventory().checkItem(21023)
						|| pc.getAdditionalWarehouseInventory().checkItem(21023)) { // 極知のイアリング
					earringLevel = 4;
				} else if (pc.getInventory().checkItem(21024)
						|| pc.getAdditionalWarehouseInventory().checkItem(21024)) { // 暴走のイアリング
					earringLevel = 5;
				} else if (pc.getInventory().checkItem(21025)
						|| pc.getAdditionalWarehouseInventory().checkItem(21025)) { // 従魔のイアリング
					earringLevel = 6;
				} else if (pc.getInventory().checkItem(21026)
						|| pc.getAdditionalWarehouseInventory().checkItem(21026)) { // 血族のイアリング
					earringLevel = 7;
				} else if (pc.getInventory().checkItem(21027)
						|| pc.getAdditionalWarehouseInventory().checkItem(21027)) { // 奴隷のイアリング
					earringLevel = 8;
				}
				if (pc.getKarmaLevel() == 1) {
					if (earringLevel >= 1) {
						htmlid = "lringd";
					} else {
						htmlid = "lring1";
					}
				} else if (pc.getKarmaLevel() == 2) {
					if (earringLevel >= 2) {
						htmlid = "lringd";
					} else {
						htmlid = "lring2";
					}
				} else if (pc.getKarmaLevel() == 3) {
					if (earringLevel >= 3) {
						htmlid = "lringd";
					} else {
						htmlid = "lring3";
					}
				} else if (pc.getKarmaLevel() == 4) {
					if (earringLevel >= 4) {
						htmlid = "lringd";
					} else {
						htmlid = "lring4";
					}
				} else if (pc.getKarmaLevel() == 5) {
					if (earringLevel >= 5) {
						htmlid = "lringd";
					} else {
						htmlid = "lring5";
					}
				} else if (pc.getKarmaLevel() == 6) {
					if (earringLevel >= 6) {
						htmlid = "lringd";
					} else {
						htmlid = "lring6";
					}
				} else if (pc.getKarmaLevel() == 7) {
					if (earringLevel >= 7) {
						htmlid = "lringd";
					} else {
						htmlid = "lring7";
					}
				} else if (pc.getKarmaLevel() == 8) {
					if (earringLevel >= 8) {
						htmlid = "lringd";
					} else {
						htmlid = "lring8";
					}
				} else {
					htmlid = "lring0";
				}
			} else if (npcid == 80072) { // バルログの鍛冶屋
				int karmaLevel = pc.getKarmaLevel();
				String[] html = { "lsmith0", "lsmith1", "lsmith2", "lsmith3",
						"lsmith4", "lsmith5", "lsmith7", "lsmith8" };
				if (karmaLevel <= 8) {
					htmlid = html[karmaLevel - 1];
				} else {
					htmlid = "";
				}
			} else if (npcid == 80074) { // 業の管理者
				if (pc.getKarma() >= 10000000) {
					htmlid = "infamous01";
				} else {
					htmlid = "infamous02";
				}
			} else if (npcid == 80104) { // アデン騎馬団員
				if (!pc.isCrown()) { // 君主
					htmlid = "horseseller4";
				}
			} else if (npcid == 70528) { // 話せる島の村 タウンマスター
				htmlid = talkToTownmaster(pc,
						L1TownLocation.TOWNID_TALKING_ISLAND);
			} else if (npcid == 70546) { // ケント村 タウンマスター
				htmlid = talkToTownmaster(pc, L1TownLocation.TOWNID_KENT);
			} else if (npcid == 70567) { // グルーディン村 タウンマスター
				htmlid = talkToTownmaster(pc, L1TownLocation.TOWNID_GLUDIO);
			} else if (npcid == 70815) { // 火田村 タウンマスター
				htmlid = talkToTownmaster(pc,
						L1TownLocation.TOWNID_ORCISH_FOREST);
			} else if (npcid == 70774) { // ウッドベック村 タウンマスター
				htmlid = talkToTownmaster(pc, L1TownLocation.TOWNID_WINDAWOOD);
			} else if (npcid == 70799) { // シルバーナイトタウン タウンマスター
				htmlid = talkToTownmaster(pc,
						L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
			} else if (npcid == 70594) { // ギラン都市 タウンマスター
				htmlid = talkToTownmaster(pc, L1TownLocation.TOWNID_GIRAN);
			} else if (npcid == 70860) { // ハイネ都市 タウンマスター
				htmlid = talkToTownmaster(pc, L1TownLocation.TOWNID_HEINE);
			} else if (npcid == 70654) { // ウェルダン村 タウンマスター
				htmlid = talkToTownmaster(pc, L1TownLocation.TOWNID_WERLDAN);
			} else if (npcid == 70748) { // 象牙の塔の村 タウンマスター
				htmlid = talkToTownmaster(pc, L1TownLocation.TOWNID_OREN);
			} else if (npcid == 70534) { // 話せる島の村 タウンアドバイザー
				htmlid = talkToTownadviser(pc,
						L1TownLocation.TOWNID_TALKING_ISLAND);
			} else if (npcid == 70556) { // ケント村 タウンアドバイザー
				htmlid = talkToTownadviser(pc, L1TownLocation.TOWNID_KENT);
			} else if (npcid == 70572) { // グルーディン村 タウンアドバイザー
				htmlid = talkToTownadviser(pc, L1TownLocation.TOWNID_GLUDIO);
			} else if (npcid == 70830) { // 火田村 タウンアドバイザー
				htmlid = talkToTownadviser(pc,
						L1TownLocation.TOWNID_ORCISH_FOREST);
			} else if (npcid == 70788) { // ウッドベック村 タウンアドバイザー
				htmlid = talkToTownadviser(pc, L1TownLocation.TOWNID_WINDAWOOD);
			} else if (npcid == 70806) { // シルバーナイトタウン タウンアドバイザー
				htmlid = talkToTownadviser(pc,
						L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
			} else if (npcid == 70631) { // ギラン都市 タウンアドバイザー
				htmlid = talkToTownadviser(pc, L1TownLocation.TOWNID_GIRAN);
			} else if (npcid == 70876) { // ハイネ都市 タウンアドバイザー
				htmlid = talkToTownadviser(pc, L1TownLocation.TOWNID_HEINE);
			} else if (npcid == 70663) { // ウェルダン村 タウンアドバイザー
				htmlid = talkToTownadviser(pc, L1TownLocation.TOWNID_WERLDAN);
			} else if (npcid == 70761) { // 象牙の塔の村 タウンアドバイザー
				htmlid = talkToTownadviser(pc, L1TownLocation.TOWNID_OREN);
			} else if (npcid == 70997) { // ドロモンド
				htmlid = talkToDoromond(pc);
			} else if (npcid == 70998) { // 歌う島のガイド
				htmlid = talkToSIGuide(pc);
			} else if (npcid == 70999) { // アレックス(歌う島)
				htmlid = talkToAlex(pc);
			} else if (npcid == 71000) { // アレックス(訓練場)
				htmlid = talkToAlexInTrainingRoom(pc);
			} else if (npcid == 71002) { // キャンセレーション師
				htmlid = cancellation(pc);
			} else if (npcid == 70506) { // ルバー
				htmlid = talkToRuba(pc);
			} else if (npcid == 71005) { // ポピレア
				htmlid = talkToPopirea(pc);
			} else if (npcid == 71009) { // ブリアナ
				if (pc.getLevel() < 13) {
					htmlid = "jpe0071";
				}
			} else if (npcid == 71011) { // チコリー
				if (pc.getLevel() < 13) {
					htmlid = "jpe0061";
				}
			} else if (npcid == 71014) { // 村の自警団(右)
				if (pc.getLevel() < 13) {
					htmlid = "en0241";
				}
			} else if (npcid == 71015) { // 村の自警団(上)
				if (pc.getLevel() < 13) {
					htmlid = "en0261";
				} else if (pc.getLevel() >= 13 && pc.getLevel() < 25) {
					htmlid = "en0262";
				}
			} else if (npcid == 71031) { // 傭兵ライアン
				if (pc.getLevel() < 25) {
					htmlid = "en0081";
				}
			} else if (npcid == 71032) { // 冒険者エータ
				if (pc.isElf()) {
					htmlid = "en0091e";
				} else if (pc.isDarkelf()) {
					htmlid = "en0091d";
				} else if (pc.isKnight()) {
					htmlid = "en0091k";
				} else if (pc.isWizard()) {
					htmlid = "en0091w";
				} else if (pc.isCrown()) {
					htmlid = "en0091p";
				}
			} else if (npcid == 71034) { // ラビ
				if (pc.getInventory().checkItem(41227)) { // アレックスの紹介状
					if (pc.isElf()) {
						htmlid = "en0201e";
					} else if (pc.isDarkelf()) {
						htmlid = "en0201d";
					} else if (pc.isKnight()) {
						htmlid = "en0201k";
					} else if (pc.isWizard()) {
						htmlid = "en0201w";
					} else if (pc.isCrown()) {
						htmlid = "en0201p";
					}
				}
			} else if (npcid == 71033) { // ハーミット
				if (pc.getInventory().checkItem(41228)) { // ラビのお守り
					if (pc.isElf()) {
						htmlid = "en0211e";
					} else if (pc.isDarkelf()) {
						htmlid = "en0211d";
					} else if (pc.isKnight()) {
						htmlid = "en0211k";
					} else if (pc.isWizard()) {
						htmlid = "en0211w";
					} else if (pc.isCrown()) {
						htmlid = "en0211p";
					}
				}
			} else if (npcid == 71026) { // ココ
				if (pc.getLevel() < 10) {
					htmlid = "en0113";
				} else if (pc.getLevel() >= 10 && pc.getLevel() < 25) {
					htmlid = "en0111";
				} else if (pc.getLevel() > 25) {
					htmlid = "en0112";
				}
			} else if (npcid == 71027) { // クン
				if (pc.getLevel() < 10) {
					htmlid = "en0283";
				} else if (pc.getLevel() >= 10 && pc.getLevel() < 25) {
					htmlid = "en0281";
				} else if (pc.getLevel() > 25) {
					htmlid = "en0282";
				}
			} else if (npcid == 71021) { // 骨細工師マッティー
				if (pc.getLevel() < 12) {
					htmlid = "en0197";
				} else if (pc.getLevel() >= 12 && pc.getLevel() < 25) {
					htmlid = "en0191";
				}
			} else if (npcid == 71022) { // 骨細工師ジーナン
				if (pc.getLevel() < 12) {
					htmlid = "jpe0155";
				} else if (pc.getLevel() >= 12 && pc.getLevel() < 25) {
					if (pc.getInventory().checkItem(41230)
							|| pc.getInventory().checkItem(41231)
							|| pc.getInventory().checkItem(41232)
							|| pc.getInventory().checkItem(41233)
							|| pc.getInventory().checkItem(41235)
							|| pc.getInventory().checkItem(41238)
							|| pc.getInventory().checkItem(41239)
							|| pc.getInventory().checkItem(41240)) {
						htmlid = "jpe0158";
					}
				}
			} else if (npcid == 71023) { // 骨細工師ケーイ
				if (pc.getLevel() < 12) {
					htmlid = "jpe0145";
				} else if (pc.getLevel() >= 12 && pc.getLevel() < 25) {
					if (pc.getInventory().checkItem(41233)
							|| pc.getInventory().checkItem(41234)) {
						htmlid = "jpe0143";
					} else if (pc.getInventory().checkItem(41238)
							|| pc.getInventory().checkItem(41239)
							|| pc.getInventory().checkItem(41240)) {
						htmlid = "jpe0147";
					} else if (pc.getInventory().checkItem(41235)
							|| pc.getInventory().checkItem(41236)
							|| pc.getInventory().checkItem(41237)) {
						htmlid = "jpe0144";
					}
				}
			} else if (npcid == 71020) { // ジョン
				if (pc.getLevel() < 12) {
					htmlid = "jpe0125";
				} else if (pc.getLevel() >= 12 && pc.getLevel() < 25) {
					if (pc.getInventory().checkItem(41231)) {
						htmlid = "jpe0123";
					} else if (pc.getInventory().checkItem(41232)
							|| pc.getInventory().checkItem(41233)
							|| pc.getInventory().checkItem(41234)
							|| pc.getInventory().checkItem(41235)
							|| pc.getInventory().checkItem(41238)
							|| pc.getInventory().checkItem(41239)
							|| pc.getInventory().checkItem(41240)) {
						htmlid = "jpe0126";
					}
				}
			} else if (npcid == 71019) { // 弟子ヴィート
				if (pc.getLevel() < 12) {
					htmlid = "jpe0114";
				} else if (pc.getLevel() >= 12 && pc.getLevel() < 25) {
					if (pc.getInventory().checkItem(41239)) { // ヴィートへの手紙
						htmlid = "jpe0113";
					} else {
						htmlid = "jpe0111";
					}
				}
			} else if (npcid == 71018) { // フェーダ
				if (pc.getLevel() < 12) {
					htmlid = "jpe0133";
				} else if (pc.getLevel() >= 12 && pc.getLevel() < 25) {
					if (pc.getInventory().checkItem(41240)) { // フェーダへの手紙
						htmlid = "jpe0132";
					} else {
						htmlid = "jpe0131";
					}
				}
			} else if (npcid == 71025) { // ケスキン
				if (pc.getLevel() < 10) {
					htmlid = "jpe0086";
				} else if (pc.getLevel() >= 10 && pc.getLevel() < 25) {
					if (pc.getInventory().checkItem(41226)) { // パゴの薬
						htmlid = "jpe0084";
					} else if (pc.getInventory().checkItem(41225)) { // ケスキンの発注書
						htmlid = "jpe0083";
					} else if (pc.getInventory().checkItem(40653)
							|| pc.getInventory().checkItem(40613)) { // 赤い鍵・黒い鍵
						htmlid = "jpe0081";
					}
				}
			} else if (npcid == 70512) { // 治療師（歌う島 村の中）
				if (pc.getLevel() >= 25) {
					htmlid = "jpe0102";
				}
			} else if (npcid == 70514) { // ヘイスト師
				if (pc.getLevel() >= 25) {
					htmlid = "jpe0092";
				}
			} else if (npcid == 71038) { // 長老 ノナメ
				if (pc.getInventory().checkItem(41060)) { // ノナメの推薦書
					if (pc.getInventory().checkItem(41090) // ネルガのトーテム
							|| pc.getInventory().checkItem(41091) // ドゥダ-マラのトーテム
							|| pc.getInventory().checkItem(41092)) { // アトゥバのトーテム
						htmlid = "orcfnoname7";
					} else {
						htmlid = "orcfnoname8";
					}
				} else {
					htmlid = "orcfnoname1";
				}
			} else if (npcid == 71040) { // 調査団長 アトゥバ ノア
				if (pc.getInventory().checkItem(41060)) { // ノナメの推薦書
					if (pc.getInventory().checkItem(41065)) { // 調査団の証書
						if (pc.getInventory().checkItem(41086) // スピリッドの根
								|| pc.getInventory().checkItem(41087) // スピリッドの表皮
								|| pc.getInventory().checkItem(41088) // スピリッドの葉
								|| pc.getInventory().checkItem(41089)) { // スピリッドの木の枝
							htmlid = "orcfnoa6";
						} else {
							htmlid = "orcfnoa5";
						}
					} else {
						htmlid = "orcfnoa2";
					}
				} else {
					htmlid = "orcfnoa1";
				}
			} else if (npcid == 71041) { // ネルガ フウモ
				if (pc.getInventory().checkItem(41060)) { // ノナメの推薦書
					if (pc.getInventory().checkItem(41064)) { // 調査団の証書
						if (pc.getInventory().checkItem(41081) // オークのバッジ
								|| pc.getInventory().checkItem(41082) // オークのアミュレット
								|| pc.getInventory().checkItem(41083) // シャーマンパウダー
								|| pc.getInventory().checkItem(41084) // イリュージョンパウダー
								|| pc.getInventory().checkItem(41085)) { // 予言者のパール
							htmlid = "orcfhuwoomo2";
						} else {
							htmlid = "orcfhuwoomo8";
						}
					} else {
						htmlid = "orcfhuwoomo1";
					}
				} else {
					htmlid = "orcfhuwoomo5";
				}
			} else if (npcid == 71042) { // ネルガ バクモ
				if (pc.getInventory().checkItem(41060)) { // ノナメの推薦書
					if (pc.getInventory().checkItem(41062)) { // 調査団の証書
						if (pc.getInventory().checkItem(41071) // 銀のお盆
								|| pc.getInventory().checkItem(41072) // 銀の燭台
								|| pc.getInventory().checkItem(41073) // バンディッドの鍵
								|| pc.getInventory().checkItem(41074) // バンディッドの袋
								|| pc.getInventory().checkItem(41075)) { // 汚れた髪の毛
							htmlid = "orcfbakumo2";
						} else {
							htmlid = "orcfbakumo8";
						}
					} else {
						htmlid = "orcfbakumo1";
					}
				} else {
					htmlid = "orcfbakumo5";
				}
			} else if (npcid == 71043) { // ドゥダ-マラ ブカ
				if (pc.getInventory().checkItem(41060)) { // ノナメの推薦書
					if (pc.getInventory().checkItem(41063)) { // 調査団の証書
						if (pc.getInventory().checkItem(41076) // 汚れた地のコア
								|| pc.getInventory().checkItem(41077) // 汚れた水のコア
								|| pc.getInventory().checkItem(41078) // 汚れた火のコア
								|| pc.getInventory().checkItem(41079) // 汚れた風のコア
								|| pc.getInventory().checkItem(41080)) { // 汚れた精霊のコア
							htmlid = "orcfbuka2";
						} else {
							htmlid = "orcfbuka8";
						}
					} else {
						htmlid = "orcfbuka1";
					}
				} else {
					htmlid = "orcfbuka5";
				}
			} else if (npcid == 71044) { // ドゥダ-マラ カメ
				if (pc.getInventory().checkItem(41060)) { // ノナメの推薦書
					if (pc.getInventory().checkItem(41061)) { // 調査団の証書
						if (pc.getInventory().checkItem(41066) // 汚れた根
								|| pc.getInventory().checkItem(41067) // 汚れた枝
								|| pc.getInventory().checkItem(41068) // 汚れた抜け殻
								|| pc.getInventory().checkItem(41069) // 汚れたタテガミ
								|| pc.getInventory().checkItem(41070)) { // 汚れた妖精の羽
							htmlid = "orcfkame2";
						} else {
							htmlid = "orcfkame8";
						}
					} else {
						htmlid = "orcfkame1";
					}
				} else {
					htmlid = "orcfkame5";
				}
			} else if (npcid == 71055) { // ルケイン（海賊島の秘密）
				if (pc.getQuest().getStep(L1Quest.QUEST_RESTA) == 3) {
					htmlid = "lukein13";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == L1Quest.QUEST_END
						&& pc.getQuest().getStep(L1Quest.QUEST_RESTA) == 2
						&& pc.getInventory().checkItem(40631)) {
					htmlid = "lukein10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == L1Quest.QUEST_END) {
					htmlid = "lukein0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 11) {
					if (pc.getInventory().checkItem(40716)) {
						htmlid = "lukein9";
					}
				} else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) >= 1
						&& pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) <= 10) {
					htmlid = "lukein8";
				}
			} else if (npcid == 71063) { // 小さな箱-１番目（海賊島の秘密）
				if (pc.getQuest().getStep(L1Quest.QUEST_TBOX1) == L1Quest.QUEST_END) {
				} else if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 1) {
					htmlid = "maptbox";
				}
			} else if (npcid == 71064) { // 小さな箱-2番目-ｂ地点（海賊島の秘密）
				if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 2) {
					htmlid = talkToSecondtbox(pc);
				}
			} else if (npcid == 71065) { // 小さな箱-2番目-c地点（海賊島の秘密）
				if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 3) {
					htmlid = talkToSecondtbox(pc);
				}
			} else if (npcid == 71066) { // 小さな箱-2番目-d地点（海賊島の秘密）
				if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 4) {
					htmlid = talkToSecondtbox(pc);
				}
			} else if (npcid == 71067) { // 小さな箱-3番目-e地点（海賊島の秘密）
				if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 5) {
					htmlid = talkToThirdtbox(pc);
				}
			} else if (npcid == 71068) { // 小さな箱-3番目-f地点（海賊島の秘密）
				if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 6) {
					htmlid = talkToThirdtbox(pc);
				}
			} else if (npcid == 71069) { // 小さな箱-3番目-g地点（海賊島の秘密）
				if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 7) {
					htmlid = talkToThirdtbox(pc);
				}
			} else if (npcid == 71070) { // 小さな箱-3番目-h地点（海賊島の秘密）
				if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 8) {
					htmlid = talkToThirdtbox(pc);
				}
			} else if (npcid == 71071) { // 小さな箱-3番目-i地点（海賊島の秘密）
				if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 9) {
					htmlid = talkToThirdtbox(pc);
				}
			} else if (npcid == 71072) { // 小さな箱-3番目-j地点（海賊島の秘密）
				if (pc.getQuest().getStep(L1Quest.QUEST_LUKEIN1) == 10) {
					htmlid = talkToThirdtbox(pc);
				}
			} else if (npcid == 71056) { // シミズ（消えた息子）
				if (pc.getQuest().getStep(L1Quest.QUEST_RESTA) == 4) {
					if (pc.getInventory().checkItem(49277)) {
						htmlid = "SIMIZZ11";
					} else {
						htmlid = "SIMIZZ0";
					}
				} else if (pc.getQuest().getStep(L1Quest.QUEST_SIMIZZ) == 2) {
					htmlid = "SIMIZZ0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_SIMIZZ) == L1Quest.QUEST_END) {
					htmlid = "SIMIZZ15";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_SIMIZZ) == 1) {
					htmlid = "SIMIZZ6";
				}
			} else if (npcid == 71057) { // ドイル（宝の地図1）
				if (pc.getQuest().getStep(L1Quest.QUEST_DOIL) == L1Quest.QUEST_END) {
					htmlid = "doil4b";
				}
			} else if (npcid == 71059) { // ルディアン（宝の地図2）
				if (pc.getQuest().getStep(L1Quest.QUEST_RUDIAN) == L1Quest.QUEST_END) {
					htmlid = "rudian1c";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_RUDIAN) == 1) {
					htmlid = "rudian7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_DOIL) == L1Quest.QUEST_END) {
					htmlid = "rudian1b";
				} else {
					htmlid = "rudian1a";
				}
			} else if (npcid == 71060) { // レスタ（宝の地図3）
				if (pc.getQuest().getStep(L1Quest.QUEST_RESTA) == L1Quest.QUEST_END) {
					htmlid = "resta1e";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_SIMIZZ) == L1Quest.QUEST_END) {
					htmlid = "resta14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_RESTA) == 4) {
					htmlid = "resta13";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_RESTA) == 3) {
					htmlid = "resta11";
					pc.getQuest().setStep(L1Quest.QUEST_RESTA, 4);
				} else if (pc.getQuest().getStep(L1Quest.QUEST_RESTA) == 2) {
					htmlid = "resta16";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_SIMIZZ) == 2
						&& pc.getQuest().getStep(L1Quest.QUEST_CADMUS) == 1
						|| pc.getInventory().checkItem(40647)) {
					htmlid = "resta1a";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_CADMUS) == 1
						|| pc.getInventory().checkItem(40647)) {
					htmlid = "resta1c";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_SIMIZZ) == 2) {
					htmlid = "resta1b";
				}
			} else if (npcid == 71061) { // カドムス（宝の地図4）
				if (pc.getQuest().getStep(L1Quest.QUEST_CADMUS) == L1Quest.QUEST_END) {
					htmlid = "cadmus1c";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_CADMUS) == 3) {
					htmlid = "cadmus8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_CADMUS) == 2) {
					htmlid = "cadmus1a";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_DOIL) == L1Quest.QUEST_END) {
					htmlid = "cadmus1b";
				}
			} else if (npcid == 71036) { // カミーラ（ドレイクの真実）
				if (pc.getQuest().getStep(L1Quest.QUEST_KAMYLA) == L1Quest.QUEST_END) {
					htmlid = "kamyla26";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_KAMYLA) == 4
						&& pc.getInventory().checkItem(40717)) {
					htmlid = "kamyla15";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_KAMYLA) == 4) {
					htmlid = "kamyla14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_KAMYLA) == 3
						&& pc.getInventory().checkItem(40630)) {
					htmlid = "kamyla12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_KAMYLA) == 3) {
					htmlid = "kamyla11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_KAMYLA) == 2
						&& pc.getInventory().checkItem(40644)) {
					htmlid = "kamyla9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_KAMYLA) == 1) {
					htmlid = "kamyla8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_CADMUS) == L1Quest.QUEST_END
						&& pc.getInventory().checkItem(40621)) {
					htmlid = "kamyla1";
				}
			} else if (npcid == 71089) { // フランコ（ドレイクの真実）
				if (pc.getQuest().getStep(L1Quest.QUEST_KAMYLA) == 2) {
					htmlid = "francu12";
				}
			} else if (npcid == 71090) { // 試練のクリスタル2（ドレイクの真実）
				if (pc.getQuest().getStep(L1Quest.QUEST_CRYSTAL) == 1
						&& pc.getInventory().checkItem(40620)) {
					htmlid = "jcrystal2";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_CRYSTAL) == 1) {
					htmlid = "jcrystal3";
				}
			} else if (npcid == 71091) { // 試練のクリスタル3（ドレイクの真実）
				if (pc.getQuest().getStep(L1Quest.QUEST_CRYSTAL) == 2
						&& pc.getInventory().checkItem(40654)) {
					htmlid = "jcrystall2";
				}
			} else if (npcid == 71074) { // リザードマンの長老
				if (pc.getQuest().getStep(L1Quest.QUEST_LIZARD) == L1Quest.QUEST_END) {
					htmlid = "lelder0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_LIZARD) == 3
						&& pc.getInventory().checkItem(40634)) {
					htmlid = "lelder12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_LIZARD) == 3) {
					htmlid = "lelder11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_LIZARD) == 2
						&& pc.getInventory().checkItem(40633)) {
					htmlid = "lelder7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_LIZARD) == 2) {
					htmlid = "lelder7b";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_LIZARD) == 1) {
					htmlid = "lelder7b";
				} else if (pc.getLevel() >= 40) {
					htmlid = "lelder1";
				}
			} else if (npcid == 71076) { // ヤングリザードマンファイター
				if (pc.getQuest().getStep(L1Quest.QUEST_LIZARD) == L1Quest.QUEST_END) {
					htmlid = "ylizardb";
				} else {
				}
			} else if (npcid == 80079) { // ケプリシャ
				if (pc.getQuest().getStep(L1Quest.QUEST_KEPLISHA) == L1Quest.QUEST_END
						&& !pc.getInventory().checkItem(41312)) {
					htmlid = "keplisha6";
				} else {
					if (pc.getInventory().checkItem(41314)) { // 占星術師のお守り
						htmlid = "keplisha3";
					} else if (pc.getInventory().checkItem(41313)) { // 占星術師の玉
						htmlid = "keplisha2";
					} else if (pc.getInventory().checkItem(41312)) { // 占星術師の壺
						htmlid = "keplisha4";
					}
				}
			} else if (npcid == 80102) { // フィリス
				if (pc.getInventory().checkItem(41329)) { // 剥製の製作依頼書
					htmlid = "fillis3";
				}
			} else if (npcid == 71167) { // フリム
				if (pc.getTempCharGfx() == 3887) {// キャリングダークエルフ変身
					htmlid = "frim1";
				}
			} else if (npcid == 71141) { // 坑夫オーム1
				if (pc.getTempCharGfx() == 3887) {// キャリングダークエルフ変身
					htmlid = "moumthree1";
				}
			} else if (npcid == 71142) { // 坑夫オーム2
				if (pc.getTempCharGfx() == 3887) {// キャリングダークエルフ変身
					htmlid = "moumtwo1";
				}
			} else if (npcid == 71145) { // 坑夫オーム3
				if (pc.getTempCharGfx() == 3887) {// キャリングダークエルフ変身
					htmlid = "moumone1";
				}
			} else if (npcid == 71198) { // 傭兵団長 ティオン
				if (pc.getQuest().getStep(71198) == 1) {
					htmlid = "tion4";
				} else if (pc.getQuest().getStep(71198) == 2) {
					htmlid = "tion5";
				} else if (pc.getQuest().getStep(71198) == 3) {
					htmlid = "tion6";
				} else if (pc.getQuest().getStep(71198) == 4) {
					htmlid = "tion7";
				} else if (pc.getQuest().getStep(71198) == 5) {
					htmlid = "tion5";
				} else if (pc.getInventory().checkItem(21059, 1)) {
					htmlid = "tion19";
				}
			} else if (npcid == 71199) { // ジェロン
				if (pc.getQuest().getStep(71199) == 1) {
					htmlid = "jeron3";
				} else if (pc.getInventory().checkItem(21059, 1)
						|| pc.getQuest().getStep(71199) == 255) {
					htmlid = "jeron7";
				}

			} else if (npcid == 81200) { // 特典アイテム管理人
				if (pc.getInventory().checkItem(21069) // 新生のベルト
						|| pc.getInventory().checkItem(21074)) { // 親睦のイアリング
					htmlid = "c_belt";
				}
			} else if (npcid == 80076) { // 倒れた航海士
				if (pc.getInventory().checkItem(41058)) { // 完成した航海日誌
					htmlid = "voyager8";
				} else if (pc.getInventory().checkItem(49082) // 未完成の航海日誌
						|| pc.getInventory().checkItem(49083)) {
					// ページを追加していない状態
					if (pc.getInventory().checkItem(41038) // 航海日誌 1ページ
							|| pc.getInventory().checkItem(41039) // 航海日誌
							// 2ページ
							|| pc.getInventory().checkItem(41039) // 航海日誌
							// 3ページ
							|| pc.getInventory().checkItem(41039) // 航海日誌
							// 4ページ
							|| pc.getInventory().checkItem(41039) // 航海日誌
							// 5ページ
							|| pc.getInventory().checkItem(41039) // 航海日誌
							// 6ページ
							|| pc.getInventory().checkItem(41039) // 航海日誌
							// 7ページ
							|| pc.getInventory().checkItem(41039) // 航海日誌
							// 8ページ
							|| pc.getInventory().checkItem(41039) // 航海日誌
							// 9ページ
							|| pc.getInventory().checkItem(41039)) { // 航海日誌
						// 10ページ
						htmlid = "voyager9";
					} else {
						htmlid = "voyager7";
					}
				} else if (pc.getInventory().checkItem(49082) // 未完成の航海日誌
						|| pc.getInventory().checkItem(49083)
						|| pc.getInventory().checkItem(49084)
						|| pc.getInventory().checkItem(49085)
						|| pc.getInventory().checkItem(49086)
						|| pc.getInventory().checkItem(49087)
						|| pc.getInventory().checkItem(49088)
						|| pc.getInventory().checkItem(49089)
						|| pc.getInventory().checkItem(49090)
						|| pc.getInventory().checkItem(49091)) {
					// ページを追加した状態
					htmlid = "voyager7";
				}
			} else if (npcid == 80048) { // 空間の歪み
				int level = pc.getLevel();
				if (level <= 44) {
					htmlid = "entgate3";
				} else if (level >= 45 && level <= 51) {
					htmlid = "entgate2";
				} else {
					htmlid = "entgate";
				}
			} else if (npcid == 71168) { // 真冥王 ダンテス
				if (pc.getInventory().checkItem(41028)) { // デスナイトの書
					htmlid = "dantes1";
				}
			} else if (npcid == 80067) { // 諜報員(欲望の洞窟)
				if (pc.getQuest().getStep(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END) {
					htmlid = "minicod10";
				} else if (pc.getKarmaLevel() >= 1) {
					htmlid = "minicod07";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_DESIRE) == 1
						&& pc.getTempCharGfx() == 6034) { // コラププリースト変身
					htmlid = "minicod03";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_DESIRE) == 1
						&& pc.getTempCharGfx() != 6034) {
					htmlid = "minicod05";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END // 影の神殿側クエスト終了
						|| pc.getInventory().checkItem(41121) // カヘルの指令書
						|| pc.getInventory().checkItem(41122)) { // カヘルの命令書
					htmlid = "minicod01";
				} else if (pc.getInventory().checkItem(41130) // 血痕の指令書
						&& pc.getInventory().checkItem(41131)) { // 血痕の命令書
					htmlid = "minicod06";
				} else if (pc.getInventory().checkItem(41130)) { // 血痕の命令書
					htmlid = "minicod02";
				}
			} else if (npcid == 81202) { // 諜報員(影の神殿)
				if (pc.getQuest().getStep(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END) {
					htmlid = "minitos10";
				} else if (pc.getKarmaLevel() <= -1) {
					htmlid = "minitos07";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_SHADOWS) == 1
						&& pc.getTempCharGfx() == 6035) { // レッサーデーモン変身
					htmlid = "minitos03";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_SHADOWS) == 1
						&& pc.getTempCharGfx() != 6035) {
					htmlid = "minitos05";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END // 欲望の洞窟側クエスト終了
						|| pc.getInventory().checkItem(41130) // 血痕の指令書
						|| pc.getInventory().checkItem(41131)) { // 血痕の命令書
					htmlid = "minitos01";
				} else if (pc.getInventory().checkItem(41121) // カヘルの指令書
						&& pc.getInventory().checkItem(41122)) { // カヘルの命令書
					htmlid = "minitos06";
				} else if (pc.getInventory().checkItem(41121)) { // カヘルの命令書
					htmlid = "minitos02";
				}
			} else if (npcid == 81208) { // 汚れたブロッブ
				if (pc.getInventory().checkItem(41129) // 血痕の精髄
						|| pc.getInventory().checkItem(41138)) { // カヘルの精髄
					htmlid = "minibrob04";
				} else if (pc.getInventory().checkItem(41126) // 血痕の堕落した精髄
						&& pc.getInventory().checkItem(41127) // 血痕の無力な精髄
						&& pc.getInventory().checkItem(41128) // 血痕の我執な精髄
						|| pc.getInventory().checkItem(41135) // カヘルの堕落した精髄
						&& pc.getInventory().checkItem(41136) // カヘルの我執な精髄
						&& pc.getInventory().checkItem(41137)) { // カヘルの我執な精髄
					htmlid = "minibrob02";
				}
			} else if (npcid == 50113) { // 渓谷の村 レックマン
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orena14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orena0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orena2";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orena3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orena4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orena5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orena6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orena7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orena8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orena9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orena10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orena11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orena12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orena13";
				}
			} else if (npcid == 50112) { // 旧・歌う島 セリアン
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orenb14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenb0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenb2";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenb3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenb4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenb5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenb6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenb7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenb8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenb9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenb10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenb11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenb12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenb13";
				}
			} else if (npcid == 50111) { // 話せる島 リリー
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orenc14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenc1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenc0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenc3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenc4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenc5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenc6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenc7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenc8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenc9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenc10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenc11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenc12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenc13";
				}
			} else if (npcid == 50116) { // グルディオ ギオン
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orend14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orend3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orend1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orend0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orend4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orend5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orend6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orend7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orend8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orend9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orend10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orend11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orend12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orend13";
				}
			} else if (npcid == 50117) { // ケント シリア
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orene14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orene3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orene4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orene1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orene0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orene5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orene6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orene7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orene8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orene9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orene10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orene11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orene12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orene13";
				}
			} else if (npcid == 50119) { // ウッドベック オシーリア
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orenf14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenf3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenf4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenf5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenf1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenf0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenf6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenf7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenf8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenf9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenf10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenf11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenf12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenf13";
				}
			} else if (npcid == 50121) { // 火田村 ホーニン
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "oreng14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "oreng3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "oreng4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "oreng5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "oreng6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "oreng1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "oreng0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "oreng7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "oreng8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "oreng9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "oreng10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "oreng11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "oreng12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "oreng13";
				}
			} else if (npcid == 50114) { // エルフの森 チコ
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orenh14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenh3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenh4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenh5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenh6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenh7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenh1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenh0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenh8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenh9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenh10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenh11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenh12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenh13";
				}
			} else if (npcid == 50120) { // シルバーナイトタウン ホップ
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "oreni14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "oreni3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "oreni4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "oreni5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "oreni6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "oreni7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "oreni8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "oreni1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "oreni0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "oreni9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "oreni10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "oreni11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "oreni12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "oreni13";
				}
			} else if (npcid == 50122) { // ギラン ターク
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orenj14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenj3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenj4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenj5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenj6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenj7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenj8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenj9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenj1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenj0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenj10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenj11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenj12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenj13";
				}
			} else if (npcid == 50123) { // ハイネ ガリオン
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orenk14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenk3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenk4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenk5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenk6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenk7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenk8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenk9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenk10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenk1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenk0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenk11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenk12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenk13";
				}
			} else if (npcid == 50125) { // 象牙の塔 ギルバート
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orenl14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenl3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenl4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenl5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenl6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenl7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenl8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenl9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenl10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenl11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenl1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenl0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenl12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenl13";
				}
			} else if (npcid == 50124) { // ウェルダン フォリカン
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orenm14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenm3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenm4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenm5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenm6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenm7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenm8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenm9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenm10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenm11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenm12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenm1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenm0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenm13";
				}
			} else if (npcid == 50126) { // アデン ジェリック
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "orenn14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "orenn3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "orenn4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "orenn5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "orenn6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "orenn7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "orenn8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "orenn9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "orenn10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "orenn11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "orenn12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "orenn13";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "orenn1";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "orenn0";
				}
			} else if (npcid == 50115) { // 沈黙の洞窟 ザルマン
				if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == L1Quest.QUEST_END) {
					htmlid = "oreno0";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 1) {
					htmlid = "oreno3";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 2) {
					htmlid = "oreno4";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 3) {
					htmlid = "oreno5";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 4) {
					htmlid = "oreno6";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 5) {
					htmlid = "oreno7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 6) {
					htmlid = "oreno8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 7) {
					htmlid = "oreno9";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 8) {
					htmlid = "oreno10";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 9) {
					htmlid = "oreno11";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 10) {
					htmlid = "oreno12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 11) {
					htmlid = "oreno13";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 12) {
					htmlid = "oreno14";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_TOSCROLL) == 13) {
					htmlid = "oreno1";
				}
			} else if (npcid == 71256) { // ロビンフッド
				if (!pc.isElf()) {
					htmlid = "robinhood2";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 255) {
					htmlid = "robinhood12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 8) {
					if (pc.getInventory().checkItem(40491, 30)
							&& pc.getInventory().checkItem(40495, 40)
							&& pc.getInventory().checkItem(100, 1)
							&& pc.getInventory().checkItem(40509, 12)
							&& pc.getInventory().checkItem(40052, 1)
							&& pc.getInventory().checkItem(40053, 1)
							&& pc.getInventory().checkItem(40054, 1)
							&& pc.getInventory().checkItem(40055, 1)
							&& pc.getInventory().checkItem(41347, 1)
							&& pc.getInventory().checkItem(41350, 1)) {
						htmlid = "robinhood11";
					} else if (pc.getInventory().checkItem(40491, 30)
							&& pc.getInventory().checkItem(40495, 40)
							&& pc.getInventory().checkItem(100, 1)
							&& pc.getInventory().checkItem(40509, 12)) {
						htmlid = "robinhood16";
					} else if ((!(pc.getInventory().checkItem(40491, 30)
							&& pc.getInventory().checkItem(40495, 40)
							&& pc.getInventory().checkItem(100, 1) && pc
							.getInventory().checkItem(40509, 12)))) {
						htmlid = "robinhood17";
					}
				} else if (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 7) {
					if (pc.getInventory().checkItem(41352, 4)
							&& pc.getInventory().checkItem(40618, 30)
							&& pc.getInventory().checkItem(40643, 30)
							&& pc.getInventory().checkItem(40645, 30)
							&& pc.getInventory().checkItem(40651, 30)
							&& pc.getInventory().checkItem(40676, 30)
							&& pc.getInventory().checkItem(40514, 20)
							&& pc.getInventory().checkItem(41351, 1)
							&& pc.getInventory().checkItem(41346, 1)) {
						htmlid = "robinhood9";
					} else if (pc.getInventory().checkItem(41351, 1)
							&& pc.getInventory().checkItem(41352, 4)) {
						htmlid = "robinhood14";
					} else if (pc.getInventory().checkItem(41351, 1)
							&& (!(pc.getInventory().checkItem(41352, 4)))) {
						htmlid = "robinhood15";
					} else if (pc.getInventory().checkItem(41351)) {
						htmlid = "robinhood9";
					} else {
						htmlid = "robinhood18";
					}
				} else if ((pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 2)
						|| (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 3)
						|| (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 4)
						|| (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 5)
						|| (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 6)) {
					htmlid = "robinhood13";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 1) {
					htmlid = "robinhood8";
				} else {
					htmlid = "robinhood1";
				}
			} else if (npcid == 71257) { // ジブリル
				if (!pc.isElf()) {
					htmlid = "zybril16";
				} else if ((pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) >= 7)) {
					htmlid = "zybril19";
				} else if (pc.getInventory().checkItem(41349)
						&& (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 7)) {
					htmlid = "zybril19";
				} else if (pc.getInventory().checkItem(41349)
						&& (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 6)) {
					htmlid = "zybril18";
				} else if ((pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 6)
						&& (!(pc.getInventory().checkItem(41354)))) {
					htmlid = "zybril7";
				} else if ((pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 6)
						&& pc.getInventory().checkItem(41354)) {
					htmlid = "zybril17";
				} else if (pc.getInventory().checkItem(41353)
						&& pc.getInventory().checkItem(40514, 10)
						&& pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 5) {
					htmlid = "zybril8";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 5) {
					htmlid = "zybril13";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 4
						&& pc.getInventory().checkItem(40048, 10)
						&& pc.getInventory().checkItem(40049, 10)
						&& pc.getInventory().checkItem(40050, 10)
						&& pc.getInventory().checkItem(40051, 10)) {
					htmlid = "zybril7";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 4) {
					htmlid = "zybril12";
				} else if (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 3) {
					htmlid = "zybril3";
				} else if ((pc.isElf())
						&& ((pc.getQuest()
								.getStep(L1Quest.QUEST_MOONOFLONGBOW) == 2) || (pc
								.getQuest().getStep(
										L1Quest.QUEST_MOONOFLONGBOW) == 1))) {
					htmlid = "zybril1";
				} else {
					htmlid = "zybril1";
				}
			} else if (npcid == 71258) { // マルバ
				if (pc.getLawful() <= -501) {
					htmlid = "marba1";
				} else if (pc.isCrown() || pc.isDarkelf() || pc.isKnight()
						|| pc.isWizard() || pc.isDragonKnight()
						|| pc.isIllusionist()) {
					htmlid = "marba2";
				} else if (pc.getInventory().checkItem(40665)
						&& (pc.getInventory().checkItem(40693)
								|| pc.getInventory().checkItem(40694)
								|| pc.getInventory().checkItem(40695)
								|| pc.getInventory().checkItem(40697)
								|| pc.getInventory().checkItem(40698) || pc
								.getInventory().checkItem(40699))) {
					htmlid = "marba8";
				} else if (pc.getInventory().checkItem(40665)) {
					htmlid = "marba17";
				} else if (pc.getInventory().checkItem(40664)) {
					htmlid = "marba19";
				} else if (pc.getInventory().checkItem(40637)) {
					htmlid = "marba18";
				} else {
					htmlid = "marba3";
				}
			} else if (npcid == 71259) { // アラス
				if (pc.getLawful() <= -501) {
					htmlid = "aras12";
				} else if (pc.isCrown() || pc.isDarkelf() || pc.isKnight()
						|| pc.isWizard() || pc.isDragonKnight()
						|| pc.isIllusionist()) {
					htmlid = "aras11";
				} else if (pc.getInventory().checkItem(40665)
						&& (pc.getInventory().checkItem(40679)
								|| pc.getInventory().checkItem(40680)
								|| pc.getInventory().checkItem(40681)
								|| pc.getInventory().checkItem(40682)
								|| pc.getInventory().checkItem(40683) || pc
								.getInventory().checkItem(40684))) {
					htmlid = "aras3";
				} else if (pc.getInventory().checkItem(40665)) {
					htmlid = "aras8";
				} else if (pc.getInventory().checkItem(40679)
						|| pc.getInventory().checkItem(40680)
						|| pc.getInventory().checkItem(40681)
						|| pc.getInventory().checkItem(40682)
						|| pc.getInventory().checkItem(40683)
						|| pc.getInventory().checkItem(40684)
						|| pc.getInventory().checkItem(40693)
						|| pc.getInventory().checkItem(40694)
						|| pc.getInventory().checkItem(40695)
						|| pc.getInventory().checkItem(40697)
						|| pc.getInventory().checkItem(40698)
						|| pc.getInventory().checkItem(40699)) {
					htmlid = "aras3";
				} else if (pc.getInventory().checkItem(40664)) {
					htmlid = "aras6";
				} else if (pc.getInventory().checkItem(40637)) {
					htmlid = "aras1";
				} else {
					htmlid = "aras7";
				}
			} else if (npcid == 70838) { // ネルファ
				if (pc.isCrown() || pc.isKnight() || pc.isWizard()
						|| pc.isDragonKnight() || pc.isIllusionist()) {
					htmlid = "nerupam1";
				} else if (pc.isDarkelf() && (pc.getLawful() <= -1)) {
					htmlid = "nerupaM2";
				} else if (pc.isDarkelf()) {
					htmlid = "nerupace1";
				} else if (pc.isElf()) {
					htmlid = "nerupae1";
				}
			} else if (npcid == 80094) { // 祭壇
				if (pc.isIllusionist()) {
					htmlid = "altar1";
				} else if (!pc.isIllusionist()) {
					htmlid = "altar2";
				}
			} else if (npcid == 80099) { // 治安団長ラルソン
				if (pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 1) {
					if (pc.getInventory().checkItem(41325, 1)) {
						htmlid = "rarson8";
					} else {
						htmlid = "rarson10";
					}
				} else if (pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 2) {
					if (pc.getInventory().checkItem(41317, 1)
							&& pc.getInventory().checkItem(41315, 1)) {
						htmlid = "rarson13";
					} else {
						htmlid = "rarson19";
					}
				} else if (pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 3) {
					htmlid = "rarson14";
				} else if (pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 4) {
					if (!(pc.getInventory().checkItem(41326, 1))) {
						htmlid = "rarson18";
					} else if (pc.getInventory().checkItem(41326, 1)) {
						htmlid = "rarson11";
					} else {
						htmlid = "rarson17";
					}
				} else if (pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) >= 5) {
					htmlid = "rarson1";
				}
			} else if (npcid == 80101) { // クエン
				if (pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 4) {
					if ((pc.getInventory().checkItem(41315, 1))
							&& pc.getInventory().checkItem(40494, 30)
							&& pc.getInventory().checkItem(41317, 1)) {
						htmlid = "kuen4";
					} else if (pc.getInventory().checkItem(41316, 1)) {
						htmlid = "kuen1";
					} else if (!pc.getInventory().checkItem(41316)) {
						pc.getQuest().setStep(
								L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 1);
					}
				} else if ((pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 2)
						&& (pc.getInventory().checkItem(41317, 1))) {
					htmlid = "kuen3";
				} else {
					htmlid = "kuen1";
				}
			} else if (npcid == 80134) { // タリオン
				if (pc.isDragonKnight()) { // ドラゴンナイト
					int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
					if (pc.getLevel() >= 30 && lv30_step == 2) {
						htmlid = "talrion1";
					} else if (pc.getLevel() >= 45 && lv45_step == 5) {
						htmlid = "talrion9";
					} else if ((pc.getLevel() >= 50) && (lv50_step == 4)) {
						htmlid = "talrion10";
					} else {
						htmlid = "talrion4";
					}
				}
			} else if (npcid == 80135) { // エルラス
				if (pc.isDragonKnight()) { // ドラゴンナイト
					int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
					if (lv30_step == L1Quest.QUEST_END) {
						htmlid = "elas6";
					} else if (pc.getLevel() >= 30 && lv30_step >= 1) {
						htmlid = "elas1";
					}
				}
			} else if (npcid == 80136) { // 長老 プロケル
				int lv15_step = quest.getStep(L1Quest.QUEST_LEVEL15);
				int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
				int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
				int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
				if (pc.isDragonKnight()) { // ドラゴンナイト
					if (pc.getLevel() >= 50 && lv45_step == L1Quest.QUEST_END) {
						if (lv50_step == 0) {
							htmlid = "prokel21";
						} else if (lv50_step > 3) { // クリア済み
							htmlid = "prokel32";
						} else if (lv50_step > 1) {
							htmlid = "prokel25";
						} else {
							htmlid = "prokel24";
						}
					} else if (pc.getLevel() >= 45
							&& lv30_step == L1Quest.QUEST_END) {
						if (lv45_step == 0) {
							htmlid = "prokel15";
						} else if (lv45_step >= 5) { // クリア済み
							htmlid = "prokel20";
						} else {
							htmlid = "prokel17";
						}
					} else if (pc.getLevel() >= 30
							&& lv15_step == L1Quest.QUEST_END) {
						if (lv30_step == 0) {
							htmlid = "prokel8";
						} else if (lv30_step >= 2) { // クリア済み
							htmlid = "prokel14";
						} else {
							htmlid = "prokel10";
						}
					} else if (pc.getLevel() >= 15) {
						if (lv15_step == 0) {
							htmlid = "prokel2";
						} else if (lv15_step == L1Quest.QUEST_END) { // クリア済み)
							htmlid = "prokel7";
						} else {
							htmlid = "prokel4";
						}
					} else { // Lv15未満
						htmlid = "prokel1";
					}
				}
			} else if (npcid == 80145) { // 長老 シルレイン
				int lv15_step = quest.getStep(L1Quest.QUEST_LEVEL15);
				int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
				int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
				int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
				if (pc.isDragonKnight()) { // ドラゴンナイト
					if (pc.getLevel() >= 45 && lv45_step == 1) {
						htmlid = "silrein37";
					} else if (pc.getLevel() >= 45 && lv45_step == 2) {
						htmlid = "silrein38";
					} else if (pc.getLevel() >= 45 && lv45_step == 3) {
						htmlid = "silrein40";
					} else if (pc.getLevel() >= 45 && lv45_step == 4) {
						htmlid = "silrein43";
					}
				} else if (pc.isIllusionist()) { // イリュージョニスト
					if (pc.getLevel() >= 50 && lv45_step == L1Quest.QUEST_END) {
						if (lv50_step == 0) {
							htmlid = "silrein27";
						} else if (lv50_step > 4) {
							htmlid = "silrein36";
						} else if (lv50_step > 3) {
							htmlid = "silrein35";
						} else if (lv50_step > 2) {
							if (pc.getInventory().checkItem(49206)) {
								htmlid = "silrein33";
							}
						} else if (lv50_step > 1) {
							if (pc.getInventory().checkItem(49178)
									&& pc.getInventory().checkItem(49202)) {
								htmlid = "silrein32";
							} else {
								htmlid = "silrein34";
							}
						} else if (lv50_step > 0) {
							htmlid = "silrein29";
						} else {
							htmlid = "silrein26";
						}
					} else if (pc.getLevel() >= 45
							&& lv30_step == L1Quest.QUEST_END) {
						if (lv45_step == 0) {
							htmlid = "silrein18";
						} else if (lv45_step == L1Quest.QUEST_END) {
							htmlid = "silrein26";
						} else if (lv45_step > 4) {
							if (pc.getInventory().checkItem(49202)) {
								htmlid = "silrein23";
							} else {
								htmlid = "silrein24";
							}
						} else if (lv45_step > 0) {
							if (pc.getInventory().checkItem(49194)
									&& pc.getInventory().checkItem(49195)
									&& pc.getInventory().checkItem(49196)) {
								htmlid = "silrein20";
							} else {
								htmlid = "silrein21";
							}
						} else {
							htmlid = "silrein19";
						}
					} else if (pc.getLevel() >= 30
							&& lv15_step == L1Quest.QUEST_END) {
						if (lv30_step == 0) {
							htmlid = "silrein11";
						} else if (lv30_step == L1Quest.QUEST_END) {
							htmlid = "silrein15";
						} else if (lv30_step > 0) {
							htmlid = "silrein14";
						} else {
							htmlid = "silrein10";
						}
					} else if (pc.getLevel() >= 15) {
						if (lv15_step == 0) {
							htmlid = "silrein2";
						} else if (lv15_step == L1Quest.QUEST_END) {
							htmlid = "silrein5";
						} else {
							htmlid = "silrein4";
						}
					} else {
						htmlid = "silrein1";
					}
				}
			} else if (npcid == 81247) { // ホワイトアントの死体(1)
				if (pc.isIllusionist()) {
					int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (pc.getLevel() >= 45
							&& lv30_step == L1Quest.QUEST_END) {
						if (lv45_step == 1) {
							htmlid = "wcorpse2";
						} else {
							htmlid = "wcorpse1";
						}
					}
				}
			} else if (npcid == 81248) { // ホワイトアントの死体(2)
				if (pc.isIllusionist()) {
					int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (pc.getLevel() >= 45
							&& lv30_step == L1Quest.QUEST_END) {
						if (lv45_step == 2) {
							htmlid = "wcorpse5";
						} else {
							htmlid = "wcorpse4";
						}
					}
				}
			} else if (npcid == 81249) { // ホワイトアントの死体(3)
				if (pc.isIllusionist()) {
					int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (pc.getLevel() >= 45
							&& lv30_step == L1Quest.QUEST_END) {
						if (lv45_step == 3) {
							htmlid = "wcorpse8";
						} else {
							htmlid = "wcorpse7";
						}
					}
				}
			} else if (npcid == 81250) { // ホワイトアントの痕跡(土)
				if (pc.isIllusionist()) {
					int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (pc.getLevel() >= 45
							&& lv30_step == L1Quest.QUEST_END) {
						if (lv45_step == 5) {
							htmlid = "wa_earth2";
						} else {
							htmlid = "wa_earth1";
						}
					}
				}
			} else if (npcid == 81251) { // ホワイトアントの痕跡(酸性液)
				if (pc.isIllusionist()) {
					int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (pc.getLevel() >= 45
							&& lv30_step == L1Quest.QUEST_END) {
						if (lv45_step == 6) {
							htmlid = "wa_acidw2";
						} else {
							htmlid = "wa_acidw1";
						}
					}
				}
			} else if (npcid == 81252) { // ホワイトアントの痕跡(卵の殻)
				if (pc.isIllusionist()) {
					int lv30_step = quest.getStep(L1Quest.QUEST_LEVEL30);
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					if (pc.getLevel() >= 45
							&& lv30_step == L1Quest.QUEST_END) {
						if (lv45_step == 7) {
							htmlid = "wa_egg2";
						} else {
							htmlid = "wa_egg1";
						}
					}
				}
			} else if (npcid == 91314) { // 青い魂の炎
				if (pc.isIllusionist()) {
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
					if (pc.getLevel() > 49
							&& lv45_step == L1Quest.QUEST_END) {
						if (lv50_step == 3) {
							htmlid = "bluesoul_f2";
						} else {
							htmlid = "bluesoul_f1";
						}
					}
				}
			} else if (npcid == 91315) { // 紅い魂の炎
				if (pc.isDragonKnight()) {
					int lv45_step = quest.getStep(L1Quest.QUEST_LEVEL45);
					int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
					if (pc.getLevel() > 49 && lv45_step == L1Quest.QUEST_END) {
						if (lv50_step == 3) {
							htmlid = "redsoul_f2";
						} else {
							htmlid = "redsoul_f1";
						}
					}
				}
			} else if (npcid == 81245) { // オーク密使(HC3)
				if (pc.isDragonKnight()) {
					if (pc.getTempCharGfx() == 6984) { // オーク密使変身
						int lv30_step = pc.getQuest().getStep(
								L1Quest.QUEST_LEVEL30);
						if (lv30_step == 1) {
							htmlid = "spy_orc1";
						}
					}
				}
			} else if (npcid == 91050) { // ドルーガ袋屋
				if (pc.getInventory().checkItem(50500, 1)) {
					htmlid = "veil3";
				} else if (pc.getInventory().checkItem(50501)) {
					htmlid = "veil8";
				}
			} else if (npcid == 91327) { // ユリエ
				if (pc.getQuest().getStep(L1Quest.QUEST_YURIE) == L1Quest.QUEST_END
						&& !pc.getInventory().checkItem(50006)) {
					htmlid = "j_html03";
				} else {
					if (pc.getQuest().getStep(L1Quest.QUEST_YURIE) == 0
							&& !pc.getInventory().checkItem(50006)) {
						htmlid = "j_html01";
					}
				}
			} else if (npcid == 70035 || npcid == 70041 || npcid == 70042) { // ギランレース管理人(セシル　パーキン　ポーリー)
				// STATUS_NONE = 0; STATUS_READY = 1; STATUS_PLAYING = 2;
				// STATUS_END = 3;
				if (L1BugBearRace.getInstance().getGameStatus() == 0) {
					htmlid = "maeno5";
				} else if (L1BugBearRace.getInstance().getGameStatus() == 1) {
					htmlid = "maeno1";
				} else if (L1BugBearRace.getInstance().getGameStatus() == 2) {
					htmlid = "maeno3";
				} else if (L1BugBearRace.getInstance().getGameStatus() == 3) {
					htmlid = "maeno5";
				}
			} else if (npcid == 80192) { // 宝石細工職人 デービッド
				if (pc.getInventory().checkItem(49031) // アイスクリスタル
						&& pc.getInventory().checkItem(21088)) { // アイスクイーンのイヤリング　７段階
					htmlid = "gemout8";
				} else if (pc.getInventory().checkItem(49031) // アイスクリスタル
						&& pc.getInventory().checkItem(21087)) { // アイスクイーンのイヤリング　６段階
					htmlid = "gemout7";
				} else if (pc.getInventory().checkItem(49031) // アイスクリスタル
						&& pc.getInventory().checkItem(21086)) { // アイスクイーンのイヤリング　５段階
					htmlid = "gemout6";
				} else if (pc.getInventory().checkItem(49031) // アイスクリスタル
						&& pc.getInventory().checkItem(21085)) { // アイスクイーンのイヤリング　４段階
					htmlid = "gemout5";
				} else if (pc.getInventory().checkItem(49031) // アイスクリスタル
						&& pc.getInventory().checkItem(21084)) { // アイスクイーンのイヤリング　３段階
					htmlid = "gemout4";
				} else if (pc.getInventory().checkItem(49031) // アイスクリスタル
						&& pc.getInventory().checkItem(21083)) { // アイスクイーンのイヤリング　２段階
					htmlid = "gemout3";
				} else if (pc.getInventory().checkItem(49031) // アイスクリスタル
						&& pc.getInventory().checkItem(21082)) { // アイスクイーンのイヤリング　１段階
					htmlid = "gemout2";
				} else if (pc.getInventory().checkItem(49031) // アイスクリスタル
						&& pc.getInventory().checkItem(21081)) { // アイスクイーンのイヤリング　０段階
					htmlid = "gemout1";
				} else if (pc.getInventory().checkItem(49031)) { // アイスクリスタル
					htmlid = "gemout17";
				} else {
					htmlid = "8event3";
				}
			} else if (npcid == 80238) { // ヒレン
				if (pc.getInventory().checkItem(50537, 1) || pc.getInventory().checkItem(50538, 1)) {
					// 真実のティーバッグ、ジェフの契約書
					htmlid = "hiren3";
				} else {
					htmlid = "hiren1";
				}
			} else if (npcid == 80239) { // ジェフ
				if (pc.getInventory().checkItem(50538, 1)) { // ジェフの契約書
					htmlid = "jeff1";
				} else if (pc.getInventory().checkItem(50537, 1)) { // 真実のティーバッグ
					htmlid = "jeff2";
				} else {
					htmlid = "arsia";
				}
			} else if (npcid == 80242) { // 孤独な精霊
				if (L1GameTimeClock.getInstance().currentTime().isNight()) { // 夜
					htmlid = "tearfairy2";
				} else { // 昼
					htmlid = "tearfairy1";
				}
			} else if (npcid == 80171) { // 初心者案内人
				if (pc.getLevel() < 13) {
					talkToBeginnersGuide(pc, npcid);
					if (pc.isDarkelf()) {
						htmlid = "tutord";
					} else if (pc.isDragonKnight()) {
						htmlid = "tutordk";
					} else if (pc.isElf()) {
						htmlid = "tutore";
					} else if (pc.isIllusionist()) {
						htmlid = "tutori";
					} else if (pc.isKnight()) {
						htmlid = "tutork";
					} else if (pc.isWizard()) {
						htmlid = "tutorm";
					} else if (pc.isCrown()) {
						htmlid = "tutorp";
					}
				} else {
					htmlid = "tutorend";
				}
			} else if (npcid == 80172) { // 修練場管理員
				int level = pc.getLevel();
				if (level < 13) {
					talkToBeginnersGuide(pc, npcid);
					if (level > 4) {
						htmlid = "admin3";
					} else {
						htmlid = "admin2";
					}
				} else {
					htmlid = "admin1";
				}
			} else if (npcid == 80179) { // ホバン
				int level = pc.getLevel();
				if (level < 5) {
					if (quest.getStep(L1Quest.QUEST_NEWBIE) == 0) {
						L1ItemInstance item =
								pc.getInventory().storeItem(50546, 1); // お菓子のかご
						if (item != null) {
							pc.sendPackets(new S_ServerMessage(143,
									getNpcTemplate().getName(), item.getLogName())); // \f1%0が%1をくれました。
						}
						quest.setStep(L1Quest.QUEST_NEWBIE, 1);
						htmlid = "j_nb01";
					} else {
						htmlid = "j_nb05";
					}
				} else if (level < 31) {
					htmlid = "j_nb02";
				} else {
					if (quest.getStep(L1Quest.QUEST_NEWBIE) != L1Quest.QUEST_END) {
						if (pc.getInventory().checkItem(50546)) { // お菓子のかご
							pc.getInventory().consumeItem(50546, 1);
						}
						L1ItemInstance item =
								pc.getInventory().storeItem(50548, 1); // ホバンの箱
						if (item != null) {
							pc.sendPackets(new S_ServerMessage(143,
									getNpcTemplate().getName(), item.getLogName())); // \f1%0が%1をくれました。
						}
						quest.setStep(L1Quest.QUEST_NEWBIE, L1Quest.QUEST_END);
						htmlid = "j_nb98";
					} else {
						htmlid = "j_nb99";
					}
				}
			} else if (npcid == 80180) { // 旅人案内人
				int level = pc.getLevel();
				if (level < 13) {
					htmlid = "lowlvS1";
				} else if (level > 12 && level < 46) {
					htmlid = "lowlvS2";
				} else {
					htmlid = "lowlvno";
				}

				/*
				 * レイド　未調整 } else if (npcid >= 81273 && npcid <= 81276) { //
				 * 龍門の扉 switch (npcid) { case 81273: // アンタラス if
				 * (getRBpcCount((short) 1005) < 32) {
				 * L1Teleport.teleport(pc, 32599, 32742, (short) 1005, 5,
				 * true); } else { pc.sendPackets(new
				 * S_ServerMessage(1229)); } break; case 81274: // パブリオン if
				 * (getRBpcCount((short) 1011) < 32) {
				 * L1Teleport.teleport(pc, 32927, 32741, (short) 1011, 5,
				 * true); } else { pc.sendPackets(new
				 * S_ServerMessage(1229)); } break; case 81275: // 未実装 　Lindvior
				 * if (getRBpcCount((short) 1005) < 32) {
				 * L1Teleport.teleport(pc, 32599, 32742, (short) 1005, 5,
				 * true); } else { pc.sendPackets(new
				 * S_ServerMessage(1229)); } break; case 81276: // 未実装　Valakas
				 * if (getRBpcCount((short) 1005) < 32) {
				 * L1Teleport.teleport(pc, 32599, 32742, (short) 1005, 5,
				 * true); } else { pc.sendPackets(new
				 * S_ServerMessage(1229)); } }
				 */
			} else if (npcid == 70762) {
				if (pc.getLevel() >= 50 && pc.isDarkelf()) {
					htmlid = "karif1";
				} else {
					htmlid = "karif9";
				}
			}

			// html表示パケット送信
			if (htmlid != null) { // htmlidが指定されている場合
				if (htmldata != null) { // html指定がある場合は表示
					pc
							.sendPackets(new S_NpcTalkReturn(objid, htmlid,
									htmldata));
				} else {
					pc.sendPackets(new S_NpcTalkReturn(objid, htmlid));
				}
			} else {
				if (pc.getLawful() < -1000) { // プレイヤーがカオティック
					pc.sendPackets(new S_NpcTalkReturn(talking, objid, 2));
				} else {
					pc.sendPackets(new S_NpcTalkReturn(talking, objid, 1));
				}
			}
		}
	}

	private static String talkToTownadviser(L1PcInstance pc, int town_id) {
		String htmlid;
		if (pc.getHomeTownId() == town_id) {
			htmlid = "artisan1";
		} else {
			htmlid = "artisan2";
		}

		return htmlid;
	}

	private static String talkToTownmaster(L1PcInstance pc, int town_id) {
		String htmlid;
		if (pc.getHomeTownId() == town_id) {
			htmlid = "hometown";
		} else {
			htmlid = "othertown";
		}
		return htmlid;
	}

	@Override
	public void onFinalAction(L1PcInstance pc, String action) {
	}

	public void doFinalAction(L1PcInstance pc) {
	}

	private boolean checkHasCastle(L1PcInstance pc, int castle_id) {
		if (pc.getClanId() != 0) { // クラン所属中
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				if (clan.getCastleId() == castle_id) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkClanLeader(L1PcInstance pc) {
		if (pc.isCrown()) { // 君主
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				if (pc.getId() == clan.getLeaderId()) {
					return true;
				}
			}
		}
		return false;
	}

	private int getNecessarySealCount(L1PcInstance pc) {
		int rulerCount = 0;
		int necessarySealCount = 10;
		if (pc.getInventory().checkItem(40917)) { // 地の支配者
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40920)) { // 風の支配者
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40918)) { // 水の支配者
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40919)) { // 火の支配者
			rulerCount++;
		}
		if (rulerCount == 0) {
			necessarySealCount = 10;
		} else if (rulerCount == 1) {
			necessarySealCount = 100;
		} else if (rulerCount == 2) {
			necessarySealCount = 200;
		} else if (rulerCount == 3) {
			necessarySealCount = 500;
		}
		return necessarySealCount;
	}

	private void createRuler(L1PcInstance pc, int attr, int sealCount) {
		// 1.地属性,2.火属性,4.水属性,8.風属性
		int rulerId = 0;
		int protectionId = 0;
		int sealId = 0;
		if (attr == 1) {
			rulerId = 40917;
			protectionId = 40909;
			sealId = 40913;
		} else if (attr == 2) {
			rulerId = 40919;
			protectionId = 40911;
			sealId = 40915;
		} else if (attr == 4) {
			rulerId = 40918;
			protectionId = 40910;
			sealId = 40914;
		} else if (attr == 8) {
			rulerId = 40920;
			protectionId = 40912;
			sealId = 40916;
		}
		pc.getInventory().consumeItem(protectionId, 1);
		pc.getInventory().consumeItem(sealId, sealCount);
		L1ItemInstance item = pc.getInventory().storeItem(rulerId, 1);
		if (item != null) {
			pc.sendPackets(new S_ServerMessage(143,
					getNpcTemplate().getName(), item.getLogName())); // \f1%0が%1をくれました。
		}
	}

	private String talkToDoromond(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getQuest().getStep(L1Quest.QUEST_DOROMOND) == 0) {
			htmlid = "jpe0011";
		} else if (pc.getQuest().getStep(L1Quest.QUEST_DOROMOND) == 1) {
			htmlid = "jpe0015";
		}

		return htmlid;
	}

	private String talkToAlex(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getLevel() < 3) {
			htmlid = "jpe0021";
		} else if (pc.getQuest().getStep(L1Quest.QUEST_DOROMOND) < 2) {
			htmlid = "jpe0022";
		} else if (pc.getQuest().getStep(L1Quest.QUEST_AREX) == L1Quest.QUEST_END) {
			htmlid = "jpe0023";
		} else if (pc.getLevel() >= 10 && pc.getLevel() < 25) {
			if (pc.getInventory().checkItem(41227)) { // アレックスの紹介状
				htmlid = "jpe0023";
			} else if (pc.isCrown()) {
				htmlid = "jpe0024p";
			} else if (pc.isKnight()) {
				htmlid = "jpe0024k";
			} else if (pc.isElf()) {
				htmlid = "jpe0024e";
			} else if (pc.isWizard()) {
				htmlid = "jpe0024w";
			} else if (pc.isDarkelf()) {
				htmlid = "jpe0024d";
			}
		} else if (pc.getLevel() > 25) {
			htmlid = "jpe0023";
		} else {
			htmlid = "jpe0021";
		}
		return htmlid;
	}

	private String talkToAlexInTrainingRoom(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getLevel() < 3) {
			htmlid = "jpe0031";
		} else {
			if (pc.getQuest().getStep(L1Quest.QUEST_DOROMOND) < 2) {
				htmlid = "jpe0035";
			} else {
				htmlid = "jpe0036";
			}
		}

		return htmlid;
	}

	private String cancellation(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getLevel() < 13) {
			htmlid = "jpe0161";
		} else {
			htmlid = "jpe0162";
		}

		return htmlid;
	}

	private String talkToRuba(L1PcInstance pc) {
		String htmlid = "";

		if (pc.isCrown() || pc.isWizard()) {
			htmlid = "en0101";
		} else if (pc.isKnight() || pc.isElf() || pc.isDarkelf()) {
			htmlid = "en0102";
		}

		return htmlid;
	}

	private String talkToSIGuide(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getLevel() < 3) {
			htmlid = "en0301";
		} else if (pc.getLevel() >= 3 && pc.getLevel() < 7) {
			htmlid = "en0302";
		} else if (pc.getLevel() >= 7 && pc.getLevel() < 9) {
			htmlid = "en0303";
		} else if (pc.getLevel() >= 9 && pc.getLevel() < 12) {
			htmlid = "en0304";
		} else if (pc.getLevel() >= 12 && pc.getLevel() < 13) {
			htmlid = "en0305";
		} else if (pc.getLevel() >= 13 && pc.getLevel() < 25) {
			htmlid = "en0306";
		} else {
			htmlid = "en0307";
		}
		return htmlid;
	}

	private String talkToPopirea(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getLevel() < 25) {
			htmlid = "jpe0041";
			if (pc.getInventory().checkItem(41209)
					|| pc.getInventory().checkItem(41210)
					|| pc.getInventory().checkItem(41211)
					|| pc.getInventory().checkItem(41212)) {
				htmlid = "jpe0043";
			}
			if (pc.getInventory().checkItem(41213)) {
				htmlid = "jpe0044";
			}
		} else {
			htmlid = "jpe0045";
		}
		return htmlid;
	}

	private String talkToSecondtbox(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getQuest().getStep(L1Quest.QUEST_TBOX1) == L1Quest.QUEST_END) {
			if (pc.getInventory().checkItem(40701)) {
				htmlid = "maptboxa";
			} else {
				htmlid = "maptbox0";
			}
		} else {
			htmlid = "maptbox0";
		}
		return htmlid;
	}

	private String talkToThirdtbox(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getQuest().getStep(L1Quest.QUEST_TBOX2) == L1Quest.QUEST_END) {
			if (pc.getInventory().checkItem(40701)) {
				htmlid = "maptboxd";
			} else {
				htmlid = "maptbox0";
			}
		} else {
			htmlid = "maptbox0";
		}
		return htmlid;
	}

	private void talkToBeginnersGuide(L1PcInstance pc, int npcid) {
		if (npcid == 80171) { // 初心者案内人
			// ヘイスト(1200秒)、HPとMP全回復
			if (pc.getLevel() < 13) {
				pc.sendPackets(new S_ServerMessage(183)); // \f1体に新たな力がみなぎります。
				pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1200));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
				pc.sendPackets(new S_SkillSound(pc.getId(), 755));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 755));
				pc.setMoveSpeed(1);
				pc.setSkillEffect(L1SkillId.STATUS_HASTE, 1200 * 1000);
				pc.sendPackets(new S_ServerMessage(77)); // \f1気分が良くなりました。
				pc.setCurrentHp(pc.getMaxHp());
				pc.setCurrentMp(pc.getMaxMp());
				pc.sendPackets(new S_SkillSound(pc.getId(), 830));
			}
		} else if (npcid == 80172) { // 修練場管理人
			// ヘイスト(2400秒)
			if (pc.getLevel() < 13) {
				pc.sendPackets(new S_ServerMessage(183)); // \f1体に新たな力がみなぎります。
				pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 2400));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
				pc.sendPackets(new S_SkillSound(pc.getId(), 755));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 755));
				pc.setMoveSpeed(1);
				pc.setSkillEffect(L1SkillId.STATUS_HASTE, 2400 * 1000);
			}
			// ブレスウェポン
			if (pc.getLevel() < 5) {
				if (pc.getWeapon() != null) {
					for (L1ItemInstance item : pc.getInventory().getItems()) {
						if (pc.getWeapon().equals(item)) {
							L1SkillUse l1skilluse = new L1SkillUse();
							l1skilluse.handleCommands(pc, L1SkillId.BLESS_WEAPON,
									pc.getId(), pc.getX(), pc.getY(), null, 0,
									L1SkillUse.TYPE_NPCBUFF);
							break;
						}
					}
				}
			}
		}
	}

	/*
	 * // レイド入場者カウント　　未調整 private int getRBpcCount(short mapId) { int pcCount =
	 * 0; for (Object obj : L1World.getInstance().getVisibleObjects(mapId)
	 * .values()) { if (obj instanceof L1PcInstance) { L1PcInstance pc =
	 * (L1PcInstance) obj; if (pc != null) { pcCount++; } } } return pcCount; }
	 */

	private static final long REST_MILLISEC = 10000;

	private static final Timer _restTimer = new Timer(true);

	private RestMonitor _monitor;

	public class RestMonitor extends TimerTask {
		@Override
		public void run() {
			setRest(false);
		}
	}

}
