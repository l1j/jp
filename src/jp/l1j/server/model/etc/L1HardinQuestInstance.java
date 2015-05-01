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
package jp.l1j.server.model.etc;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.datatables.DoorTable;
import jp.l1j.server.datatables.MobGroupTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.TrapTable;
import jp.l1j.server.model.instance.L1DoorInstance;
import jp.l1j.server.model.instance.L1EffectInstance;
import jp.l1j.server.model.instance.L1FieldObjectInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1TrapInstance;
import jp.l1j.server.model.L1HardinQuest;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1MobGroupSpawn;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.skill.L1SkillId;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.model.trap.L1Trap;
import jp.l1j.server.model.trap.L1WorldTraps;
import jp.l1j.server.packets.server.S_GreenMessage;
import jp.l1j.server.packets.server.S_NpcChatPacket;
import jp.l1j.server.packets.server.S_NpcPack;
import jp.l1j.server.packets.server.S_NpcTalkReturn;
import jp.l1j.server.packets.server.S_ShockWave;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1DoorGfx;
import jp.l1j.server.types.Point;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.collections.Lists;

public class L1HardinQuestInstance {

	class assailantSpawn extends TimerTask {

		public void begin() {
			final Timer timer = new Timer();
			timer.scheduleAtFixedRate(this, 10 * 1000, 160 * 1000);
		}

		@Override
		public void run() {
			if (!isDeleteTransactionNow()) {
				if (getBoneLaidStatus() == -1 && _leader.getMapId() == getMapId()) {
					final RandomGenerator rd = RandomGeneratorFactory.getSharedRandom();
					final int i = rd.nextInt(3);
					final int mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(85 + i).getLeaderId();
					doSpawnGroup(_leader.getLocation(), mobGroupLeaderId, 85 + i);
					if (i == 1) {
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7596", 0));
						// 7596 キャ?！オーガよぉ?！
					}
				} else {
					this.cancel();
				}
			} else {
				this.cancel();
			}
		}
	}

	/**
	 * @author
	 *
	 */
	class attackKerenis extends TimerTask {
		int randomtime = 20;

		public void begin() {
			final Timer timer = new Timer();
			timer.scheduleAtFixedRate(this, 30 * 1000, randomtime * 1000);
		}

		@Override
		public void run() {
			if (!isDeleteTransactionNow()) {
				if (!isAlreadyGuardManDeath()) {
					final RandomGenerator rd = RandomGeneratorFactory.getSharedRandom();
					randomtime = 20 + rd.nextInt(10);
					for (final L1Object obj : L1World.getInstance().getVisibleObjects(_Kerenis, 6)) {
						if (obj instanceof L1MonsterInstance) {
							if (!((L1MonsterInstance) obj).isDead()) {
								final L1SkillUse l1skilluse = new L1SkillUse();
								l1skilluse.handleCommands(_leader,
										L1SkillId.FROZEN_CLOUD, obj.getId(),
										obj.getX(), obj.getY(), null, 0,
										L1SkillUse.TYPE_GMBUFF, _Kerenis);
								break;
							}
						}
					}
				} else {
					this.cancel();
				}
			} else {
				this.cancel();
			}

		}
	}

	class HardinQuestMonitor extends TimerTask {
		public void begin() {
			final Timer timer = new Timer();
			timer.scheduleAtFixedRate(this, 30 * 1000, 300 * 1000);
		}

		@Override
		public void run() {
			if (!isDeleteTransactionNow()) {// 念のため
				for (final L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					if (getMapId() == pc.getMapId()) {
						return;
					}
				}
				if (!isDeleteTransactionNow())
					end();
				this.cancel();
			} else {
				this.cancel();
			}
		}
	}

	class HqThread extends Thread {
		@Override
		public void run() {
			try {
				sendCountDown();
				boolean hardMode = false;
				int pattern = 1;

				if (isHardMode()) {// ハード
					hardMode = true;
				}
				for (int round = 1; round <= 12; round++) {
					if (!isDeleteTransactionNow()) {// ラウンド開始時に続行可能か確認する
						setCurRound(round);
						sendRoundMessage(round);

						if (hardMode) {// ハード
							if (round > 1) {
								if (getBoneLaidStatus() == 2) {
									pattern = 3;// スーパーハード
								} else {
									pattern = 2;// ハード
								}
							} else if (getBoneLaidStatus() == 2) {
								pattern = 3;//スーパーハード
							}
						}
						if (round % 4 == 0) {// ボスラウンドのみボス出現時とモンスターの出現をずらす
							Thread.sleep(5 * 1000);// 5秒のみ
						}
						final RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
						if (99 < (random.nextInt(100) + 1)) {
							setBonusStage(true);
						}
						for (int subRound = 1; subRound <= 4; subRound++) {
							if (isDeleteTransactionNow())
								return;
							sendSubRoundMessage(subRound);
							selectSpawn(pattern, round);
							// Thread.sleep(9000);
						}
						if (round % 4 == 0) {
							Thread.sleep(109 * 1000);
						} else {
							Thread.sleep(75 * 1000);
						}
						int cnt = 0;
						for (final L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if (pc.getMapId() == getMapId()) {
								cnt++;
							}
						}
						/*
						 * if (cnt < 5) {//5人以上いないとタリスマンが受け取れないため開始できない
						 * outPushPlayer(); clearColosseum(); setActive(false);
						 * return; }
						 */
						if (searchCountMonster() > 6 || isBonusStage()) {
							if (!isBonusStage() && getCurRound() != 12) {
								sendMessage("$7653", 0);
								// 悪鬼羅刹どもが湧いてきたわい！覚悟はいいかの！
							}
							Thread.sleep(25 * 1000);
							if (searchCountMonster() > 6 || isBonusStage()) {
								Thread.sleep(35 * 1000);
								sendMessage("$7811", 0);
								_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7811", 0));
								// このままでは…お前たち、がんばっておくれ！
								boolean remain = true;
								for (int i = 0; ((i < 3) && remain)
										|| isBonusStage(); i++) {
									if (isDeleteTransactionNow())
										return;
									Thread.sleep(30000);
									remain = searchCountMonster() > 6;
								}

								if (remain || isBonusStage()) {
									sendMessage("$7681", 0);
									_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7681", 0));
									// どうやらお前たちに過度の期待を寄せていたようじゃ…
									if (!isDeleteTransactionNow())
										outPushPlayer();
										// サブラウンド毎に終了の有無を確認
									return;
								} else {
									if (getCurRound() != 12) {
										sendMessage("$8703", 0);
										_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$8703", 0));
										// ハーディン：ふぅ…かろうじて防げたようだな。もっとがんばるのだ！！
									}									
								}
							}
							setRoundStatus(round - 1, 2);// 遅い
						} else {
							if (searchCountMonster() < 4) {
								setRoundStatus(round - 1, 1);// 早い spawnPartを進める
							} else {
								setRoundStatus(round - 1, 3);// 普通 spawnPartを停止
							}
							if (getCurRound() != 12) {
								sendMessage("$7652", 0);
								// 7652 思ったより進行が早いぞ！気を抜くな！
							}
						}
					} else {
						return;
					}
				}
			} catch (final Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}

	class KerenisPart1 extends Thread {
		@Override
		public void run() {
			try {
				final L1Location loc = new L1Location(32742, 32930, getMapId());
				spawnOne(loc, 91320, 0);
				if (_leader.getMapId() == getMapId()) {
					_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_ep003"));
					// オリムはどう思う？ハーディン様は私のことを疎ましく思ってるかもしれないわ。
				}
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7616", 0));
				// 7616 ハーディンがわたしのことをどう思っているのか、オリムは知ってる？
				setActionKerenisDirect(0);
				Thread.sleep(3000);
				if (getActionKerenis() == 1) {
					_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7562", 0));
					// 7562 まあ、こんなところかしら。
				} else {
					if (getActionKerenis() == 2) {
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7570", 0));
						// 7570 へぇ?、そんなにわたしと戦いたいってわけ。
					} else {// 0
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7617", 0));
						// 7617 え、知らないって？
					}
				}

				Thread.sleep(102 * 1000);
				if (getSwitchStatus(0) == 0) {
					setSwitchStatus(0, -1);
				}
				if (isAlreadyFirstSwitch()) {// 0＝ギリギリ 1早い 2遅い
					_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7622", 0));
					// 7622 スタートは順調のようね。その調子よ。
					return;
				} else {
					if (_leader.getMapId() == getMapId()) {
						_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_ep004"));
					}
					_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7618", 0));
					sendMessage("$7560 : $7618", 0);
					// 7618 みんなに何かあったのかな？いくらなんでも遅い…
					setActionKerenisDirect(0);//
					Thread.sleep(3000);
					if (isAlreadyFirstSwitch()) {
						setSwitchStatus(0, 2);
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7620", 0));
						// あら、ちょっと急ぎすぎたかもね。ごめんなさい。
					}

					if (getActionKerenis() == 1) {
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7571", 0));
						// 7620 7571 こっちのことなんてどうでもいいんでしょ！
					} else {
						if (getActionKerenis() == 2) {
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7563", 0));
							// 7563 ふふ、認める気になった？
						} else {
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7619", 0));
							// 分かったのなら、何か反応してよ！
						}

					}
				}
				Thread.sleep(15 * 1000);// 15秒後強制帰還
				// -1のままならクエスト失敗へ
				// System.out.print(getSwitchStatus(0));
				if (getSwitchStatus(0) == -1) {
					if (isAlreadyFirstSwitch()) {
						setSwitchStatus(0, 0);
					} else {
						if (!isDeleteTransactionNow())
							outPushPlayer();// end();
					}
				}

			} catch (final InterruptedException ignore) {
			}
		}
	}

	class KerenisPart2 extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(310 * 1000);
				if (getSwitchStatus(1) == 0) {
					setSwitchStatus(1, -1);
				}
				if (isAlreadySecondSwitch()) {
					return;
				} else {
					if (_leader.getMapId() == getMapId()) {
						_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_ep004"));
					}
					_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7625", 0));
					sendMessage("$7560 : $7625", 0);
					// 7625 みんな何してるの？遅すぎる…
					setActionKerenisDirect(0);
					Thread.sleep(3000);
					if (isAlreadySecondSwitch()) {
						setSwitchStatus(1, 2);
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7627", 0));
						// お願い、急いで！もしかして、いじわるじゃないよね？
					}
					if (getActionKerenis() == 1) {
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7571", 0));
						// 7571 こっちのことなんてどうでもいいんでしょ！
					} else {
						if (getActionKerenis() == 2) {
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7563", 0));
							// 7563 ふふ、認める気になった？
						} else {
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7626", 0));
							// 怒ってるのに、何も言わないの？
						}
					}
					setActionKerenisDirect(0);

				}
				Thread.sleep(15 * 1000);// 15秒後強制帰還
				// -1のままならクエスト失敗へ
				if (getSwitchStatus(1) == -1) {
					if (isAlreadySecondSwitch()) {
						setSwitchStatus(1, 0);
					} else {
						if (!isDeleteTransactionNow())
							outPushPlayer();// end();
					}
				}
			} catch (final InterruptedException ignore) {
			}
		}
	}

	class KerenisPart3 extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(230 * 1000);
				if (getSwitchStatus(2) == 0) {
					setSwitchStatus(2, -1);
				}
				if (isAlreadyPortal()) {
					return;
				} else {
					if (_leader.getMapId() == getMapId()) {
						_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_ep004"));
					}
					_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7632", 0));
					sendMessage("$7560 : $7632", 0);
					// 7632 もう最低！
					setActionKerenisDirect(0);
					Thread.sleep(3000);
					if (isAlreadyPortal()) {
						setSwitchStatus(2, 2);
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7634", 0));
						// これじゃあわたしが横暴みたいじゃない！
					}

					if (getActionKerenis() == 1) {
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7563", 0));
						// 7563 ふふ、認める気になった？
					} else {
						if (getActionKerenis() == 2) {
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7571", 0));
							// こっちのことなんてどうでもいいんでしょ！
						} else {
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7633", 0));
							// 7633 オリム！聞いてよ！
						}
					}
					setActionKerenisDirect(0);
				}
				Thread.sleep(15 * 1000);// 15秒後強制帰還
				// -1のままならクエスト失敗へ
				if (getSwitchStatus(2) == -1) {
					if (isAlreadyPortal()) {
						setSwitchStatus(2, 0);
					} else {
						if (!isDeleteTransactionNow())
							outPushPlayer();// end();
					}
				}
			} catch (final InterruptedException ignore) {
			}
		}
	}

	class KerenisPart4 extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(150 * 1000);
				if (getSwitchStatus(3) == 0) {
					setSwitchStatus(3, -1);
				}
				if (isAlreadyGuardManDeath()) {
					return;
				} else {
					if (_leader.getMapId() == getMapId()) {
						_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_ep004"));
					}
					sendMessage("$7560 : $7640", 0);// 7640 救いようがないわ。
					setActionKerenisDirect(0);
					Thread.sleep(3000);
					if (isAlreadyGuardManDeath()) {
						setSwitchStatus(3, 2);
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7642", 0));
						// 7642 あったまきた！わたしが切れるまで何もしない気ね！
					}

					if (getActionKerenis() == 1) {
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7563", 0));
						// 7563 ふふ、認める気になった？
					} else {
						if (getActionKerenis() == 2) {
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7571", 0));
							// こっちのことなんてどうでもいいんでしょ！
						}
					}
					Thread.sleep(3000);
					_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7641", 0));
					// 7641 はぁ…まあいいいでしょ。これで大丈夫ってことよね。
					setActionKerenisDirect(0);
				}
				Thread.sleep(15 * 1000);// 15秒後強制帰還
				// -1のままならクエスト失敗へ
				if (getSwitchStatus(3) == -1) {
					if (isAlreadyGuardManDeath()) {
						setSwitchStatus(3, 0);
					} else {
						if (!isDeleteTransactionNow())
							outPushPlayer();// end();
					}
				}
			} catch (final InterruptedException ignore) {
			}
		}
	}

	class KerenisRandomMessage extends TimerTask {
		private final int MSGID = 7576;

		public void begin() {
			final Timer timer = new Timer();
			timer.scheduleAtFixedRate(this, 10 * 1000, 60 * 1000);
		}

		/*
		 * 7576 何か意味があるのかしら？これ。 7577 キュア ポイズンを使えばオークを白く染められるわよ。 7578
		 * お母さんの名前はエヴァ。えっ…お父さんの話？ 7579 一番好きな人？…ひ、み、つ。 7580
		 * 出でよ！魔法の玉！…あら？なんかダメみたいね。 7581 生を司る命の水よ！次元を司る時の風よ！わたしに力を！えい！ 7582
		 * わたしは甘いものが苦手なの。やっぱピリッと辛いものじゃなきゃね。 7583 わたしはエヴァの娘、渇きなんて感じたことがないわ。 7584
		 * わたしは炎なんかには屈しないわ、絶対にね！ 7585 何か面白いことないかな？ 7586
		 * はぁ…今日が過ぎても、また退屈な日常が続くんでしょうね…。 7587 オークなんて全部やっつけちゃえ！
		 */
		@Override
		public void run() {
			if (!isDeleteTransactionNow()) {
				if (!isAlreadyGuardManDeath()) {
					final RandomGenerator rd = RandomGeneratorFactory.getSharedRandom();
					final int i = rd.nextInt(12);
					_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$" + (MSGID + i), 0));
				} else {
					this.cancel();
				}
			} else {
				this.cancel();
			}
		}
	}

	class moveWall extends TimerTask {// 壁が開き数秒で戻る
		private final RandomGenerator _rnd = RandomGeneratorFactory.newRandom();

		private void waitForLocation(L1Location loc)
				throws InterruptedException {
			boolean find = true;
			while (find) {
				find = false;
				for (final L1Object obj : L1World.getInstance()
						.getVisiblePoint(loc, 0)) {
					if (!(obj instanceof L1ItemInstance)) {
						find = true;
						Thread.sleep(500);
						break;
					}
				}
			}
		}

		private L1Location findLocation(L1Location base) {
			L1Location newLoc = new L1Location(base);
			boolean find = true;
			while (find) {
				find = false;
				int moveX = 2 + _rnd.nextInt(3);// 前方2セル～4セルへ
				int moveY = _rnd.nextInt(7);// 左端を基準に7セルを想定
				newLoc.set(newLoc.getX() + moveX, newLoc.getY() - moveY);
				for (final L1Object obj : L1World.getInstance()
						.getVisiblePoint(newLoc, 0)) {
					if (!(obj instanceof L1ItemInstance)) {
						find = true;
						break;
					}
				}
			}
			return newLoc;
		}

		public void begin() {
			final Timer timer = new Timer();
			timer.schedule(this, 6000);
		}

		@Override
		public void run() {
			try {
				int cnt = 3;
				for (L1DoorInstance wall : Lists.newArrayList(getAllDoors())) {
					if (cnt % 4 == 0) {
						sendShockWave();
					}
					if (wall.getGfxId() != WALL_GFXID) {
						continue;
					}
					L1Location newLoc = findLocation(wall.getLocation());
					removeDoor(wall);
					addDoor(newLoc);
					Thread.sleep(600);
					cnt++;
				}
				Thread.sleep(5000);
				// 扉の閉止を開始　パターン１　左から　２　右から
				int val;
				int startX1;// 32703,32702
				int startX2;
				// int frontY;32866 10
				// int backY;872 7
				if (_rnd.nextInt(2) == 0) {// 1
					val = 1;
					startX1 = 32703;
					startX2 = startX1;
				} else {
					val = -1;
					startX1 = 32711;
					startX2 = 32709;
				}
				cnt = 0;
				for (L1DoorInstance wall : Lists.newArrayList(getAllDoors())) {
					L1Location newLoc = new L1Location();
					if (cnt % 4 == 0) {
						sendShockWave();
					}
					if (cnt % 2 == 0) {
						newLoc.set(startX1, 32866, getMapId());
						startX1 += val;
					} else {
						newLoc.set(startX2, 32872, getMapId());
						startX2 += val;
					}
					waitForLocation(newLoc);
					removeDoor(wall);
					addDoor(newLoc);
					Thread.sleep(600);
					cnt++;
				}
				Thread.sleep(20000);
				sendMessage("$8718", 0);
				// 8718 中央の足場は一度だけしか作動しませんのでご注意ください。
			} catch (final InterruptedException e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}

		}

	}

	class orcVocabulary extends TimerTask {
		public void begin() {
			final Timer timer = new Timer();
			timer.schedule(this, 2 * 1000);
		}

		@Override
		public void run() {
			try {
				final L1Location loc = new L1Location();
				loc.set(32746, 32932, getMapId());
				final L1NpcInstance orc = spawnOne(loc, 91333, 7);// オーク
				loc.set(32748, 32931, getMapId());
				final L1NpcInstance orcFighter = spawnOne(loc, 91336, 7);// オークファイター
				loc.set(32745, 32935, getMapId());
				final L1NpcInstance orcArcher = spawnOne(loc, 91334, 7);// オークアーチャー
				loc.set(32747, 32934, getMapId());
				final L1NpcInstance barusim = spawnOne(loc, 91335, 7);// バルシム
				Thread.sleep(2000);
				barusim.broadcastPacket(new S_NpcChatPacket(barusim, "$7842", 0));
				// あのま、ま、魔女…お、お、俺は… 7842 バルシム
				Thread.sleep(2000);
				orc.broadcastPacket(new S_NpcChatPacket(orc, "$7848", 0));
				// 俺たち、魔女、殴った。 7848 オーク
				Thread.sleep(2000);
				orcFighter.broadcastPacket(new S_NpcChatPacket(orcFighter, "$7854", 0));
				// お前、出るな！ 7854 オークファイター
				Thread.sleep(2000);
				orc.broadcastPacket(new S_NpcChatPacket(orc, "$7849", 0));
				// すまない… 7849
				Thread.sleep(2000);
				orcFighter.broadcastPacket(new S_NpcChatPacket(orcFighter, "$7855", 0));
				// 俺たち、魔女、罰する！ 7855
				Thread.sleep(2000);
				barusim.broadcastPacket(new S_NpcChatPacket(barusim, "$7843", 0));
				// お、お、俺は…魔女が…こ、こ、怖い… 7843
				Thread.sleep(2000);
				orcFighter.broadcastPacket(new S_NpcChatPacket(orcFighter, "$7856", 0));
				// バルシム、ばか！お前、ここで、待つ！ 7856
				Thread.sleep(2000);
				barusim.broadcastPacket(new S_NpcChatPacket(barusim, "$7844", 0));
				// バルシム、す、す、すまない！ 7844
				Thread.sleep(2000);
				orcFighter.broadcastPacket(new S_NpcChatPacket(orcFighter, "$7857", 0));
				// やつらに連絡したか。 7857
				Thread.sleep(2000);
				orcArcher.broadcastPacket(new S_NpcChatPacket(orcArcher, "$7851", 0));
				// 連絡した。すぐに来る。 7851
				Thread.sleep(2000);
				orcFighter.broadcastPacket(new S_NpcChatPacket(orcFighter, "$7858", 0));
				// やつらが来るまで、俺たちここで状況見る。 7858
				Thread.sleep(2000);
				orcArcher.broadcastPacket(new S_NpcChatPacket(orcArcher, "$7852", 0));
				// 隊長も恐ろしいか。 7852
				Thread.sleep(2000);
				orcFighter.broadcastPacket(new S_NpcChatPacket(orcFighter, "$7859", 0));
				// うるさい！賢い俺、戦略立てた！ 7859
				Thread.sleep(2000);
				// 出口748 935
				while (orcFighter.moveDirection(32748, 32935) != -1) {
					if (orcFighter.getLocation().getLineDistance(
							new Point(32748, 32935)) != 0) {
						orcFighter.setDirectionMove(orcFighter.moveDirection(32748, 32935));
						Thread.sleep(orcFighter.getPassiSpeed());
					} else {
						break;
					}
				}
				Thread.sleep(2000);
				orcFighter.deleteMe();

				orcArcher.broadcastPacket(new S_NpcChatPacket(orcArcher, "$7853", 0));
				// まただ…すまない… 7853
				Thread.sleep(2000);
				while (orcArcher.moveDirection(32748, 32935) != -1) {
					if (orcArcher.getLocation().getLineDistance(
							new Point(32748, 32935)) != 0) {
						orcArcher.setDirectionMove(orcArcher.moveDirection(32748, 32935));
						Thread.sleep(orcArcher.getPassiSpeed());
					} else {
						break;
					}
				}
				Thread.sleep(2000);
				orcArcher.deleteMe();

				orc.broadcastPacket(new S_NpcChatPacket(orc, "$7850", 0));
				// 俺、出ていない。 7850
				Thread.sleep(2000);
				while (orc.moveDirection(32748, 32935) != -1) {
					if (orc.getLocation().getLineDistance(
							new Point(32748, 32935)) != 0) {
						orc.setDirectionMove(orc.moveDirection(32748, 32935));
						Thread.sleep(orc.getPassiSpeed());
					} else {
						break;
					}
				}
				Thread.sleep(2000);
				orc.deleteMe();

				barusim.broadcastPacket(new S_NpcChatPacket(barusim, "$7845", 0));
				// 魔女が…こ、こ、怖い… 7845
				Thread.sleep(4000);
				barusim.broadcastPacket(new S_NpcChatPacket(barusim, "$7846", 0));
				// と、と、ところで…い、い、いつ行くんだ？ 7846
				Thread.sleep(4000);
				barusim.broadcastPacket(new S_NpcChatPacket(barusim, "$7847", 0));
				// 俺は怖い。も、も、もう…ま、ま、待てない… 7847
				Thread.sleep(2000);
				while (barusim.moveDirection(32748, 32935) != -1) {
					if (barusim.getLocation().getLineDistance(
							new Point(32748, 32935)) != 0) {
						barusim.setDirectionMove(barusim.moveDirection(32748, 32935));
						Thread.sleep(barusim.getPassiSpeed());
					} else {
						break;
					}
				}
				Thread.sleep(2000);
				barusim.deleteMe();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 競技開始までをカウントダウンする。
	 *
	 * @throws InterruptedException
	 */
	class Preliminary extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7598", 0));
				// 7598 来たか…少し待っておれ。今準備をすます。4
				Thread.sleep(4000);
				sendMessage(_Hardin.getName() + " : $8693", 0);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$8693", 0));
				// 8693 最終確認を始めるとするか。8
				Thread.sleep(8000);
				setActionHardinDirect(0);
				sendMessage(_Hardin.getName() + " : $8694", 0);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$8694", 0));
				// 8694 最終確認が必要ないなら[alt + 2]を押せばいい。8
				Thread.sleep(8000);
				if (getActionHardin() != 1) {
					sendMessage(_Hardin.getName() + " : $8695", 0);
					_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$8695", 0));
					// 8695 ダンジョンに入ると、おまえたちの最小限の資格が試されるだろう。
					Thread.sleep(8000);
					sendMessage(_Hardin.getName() + " : $8696", 0);
					_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$8696", 0));
					// 8696 そう難しくない相手だ。簡単にパスできると信じているぞ。
					Thread.sleep(8000);
					sendMessage(_Hardin.getName() + " : $8697", 0);
					_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$8697", 0));
					// 8697 それと中にはトラップを解除したり、設置したりするための足場が用意されている。
					Thread.sleep(8000);
					sendMessage(_Hardin.getName() + " : $8698", 0);
					_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$8698", 0));
					// 8698 足場はできるだけわかりやすいようにしてあるつもりだが。
					Thread.sleep(8000);
					sendMessage(_Hardin.getName() + " : $8699", 0);
					_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$8699", 0));
					// 8699 私の美的感覚については文句を言わないでくれ。
					Thread.sleep(8000);
					sendMessage(_Hardin.getName() + " : $8700", 0);
					_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$8700", 0));
					// 8700 それとまぁ興味深いものも目にするだろう
					// ｪ、時間に遅れないように急いだほうがいい。
					Thread.sleep(8000);
					sendMessage(_Hardin.getName() + " : $8701", 0);
					_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$8701", 0));
					// 8701 それではくれぐれも気をつけて。悪いが安全は保証してやれないんだ。
					Thread.sleep(8000);
				}
				setActionHardinDirect(0);
				sendMessage(_Hardin.getName() + " : $8702", 0);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$8702", 0));
				// 8702* では始めるとするか？
				Thread.sleep(4000);
				if (getActionHardin() != 1) {
					if (_leader.getMapId() == getMapId()) {
						_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_ep002"));
						// 10秒だけ時間をやろう
					}
					Thread.sleep(10000);
				}

				setActionHardinDirect(0);// 初期化
				if (_leader.getMapId() == getMapId()) {
					_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_ep001"));
				}
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7599", 0));
				// 7599 オリム！これでお別れじゃな。[Alt + 2]を押してみよ。
				Thread.sleep(8000);
				if (getActionHardin() != 1) {
					sendMessage(_Hardin.getName() + " : $7601", 0);
					_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7601", 0));
					// 7601 オリム。しばし別れの時間をやろう。
					setActionHardinDirect(0);// 初期化
					Thread.sleep(10000);
					for (int i = 0; i < 5; i++) {// 5回まで待つ
						if (getActionHardin() != 1) {
							setActionHardinDirect(0);// 初期化
							sendMessage(_Hardin.getName() + " : $7602", 0);
							_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7602", 0));
							// 7602 急ぐのじゃ。オリム。
							Thread.sleep(8000);
						} else {
							break;
						}
					}
				}
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7600", 0));
				// 7600 支度が終わったようだな。オリムよ。
				Thread.sleep(8000);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7603", 0));
				// 7603 オリム。それではケレニスのところに送ってやろう。
				Thread.sleep(2000);
				// 転送 0 ,47 s
				if (_leader.getMapId() == getMapId()) {
					L1Teleport.teleport(_leader, 32738, 32930, getMapId(), 5, true);
				}
				final L1Location loc = new L1Location();
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7604", 0));
				// 7604 いいだろう。残った者も早く準備なさい。
				/* 1 */
				Thread.sleep(1000);
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7607", 0));
				// 7607 やっぱり、来たのね。オリム。
				/* 5 */
				Thread.sleep(4000);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7605", 0));
				// 7605 早く魔法陣に入るのじゃ！そう長くは持たんぞ。
				/* 10 */
				Thread.sleep(2000);
				// 陣出現

				loc.set(32725, 32724, getMapId());
				setSwitch(loc, 65);
				spawnOne(loc, 91319, 0);
				/* 12 */Thread.sleep(5000);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7606", 0));
				// 7606 儂は一足先に目的地に行ってるからの。
				Thread.sleep(4000);
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7608", 0));
				// 7608 なにかあったらわたしを守って。…なにもおきないと思うけど。
				Thread.sleep(4000);
				/* 20 */
				// ハーディンテレポート
				_Hardin.teleport(32716, 32846, 6);
				loc.set(32716, 32846, getMapId());
				spawnOne(loc, 91319, 0);
				spawnOne(loc, 91317, 0);
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7609", 0));
				// 7609 ねぇ、退屈でしょ、なんなら話でもしてみる？
				Thread.sleep(5000);
				setActionKerenisDirect(0);
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7610", 0));
				// 7610 わたしの話をきいてそうだと思うなら[Alt + 2]、違うと思うのなら[Alt + 4]を押すのよ。
				Thread.sleep(5000);
				/* 30 */// ここは危険
				sendMessage(_Hardin.getName() + " : $7611", 0);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7611", 0));
				// 7611 ここは危険じゃ。よそ者が立ち入らぬよう装置を止めてくれ。
				Thread.sleep(2000);
				if (getActionKerenis() == 1) {
					_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7561", 0));
					// 7561 そうそう、そんな感じ。
				} else {
					if (getActionKerenis() == 2) {
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7569", 0));
						// 7569 何？どういうこと？
					} else {
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7612", 0));
						// 7612 ほらやり方が分かったのなら、なんか言ってみなさいよ。
					}
				}
				setActionKerenisDirect(0);
				Thread.sleep(6000);
				/* 37 */// 装置は～
				sendMessage(_Hardin.getName() + " : $7613", 0);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7613", 0));
				// 7613 装置はお前たちだけが見られるようにしておいた。頼んだぞ。
				Thread.sleep(5000);
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7614", 0));
				// 7614 準備はいいようね。じゃあ、始めましょうか。
				sendMessage(_Hardin.getName() + " : $7615", 0);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7615", 0));
				// 7615 早くすませてこっちにきてくれんか。
				sendShockWave();
				Thread.sleep(2000);
				/* 41 */// 扉開放--ここまでに初期処理を済ませておく
				/* タイマーおよびスレッド開始 */
				if (!isDeleteTransactionNow()) {
					_kp1 = new KerenisPart1();
					GeneralThreadPool.getInstance().execute(_kp1);
					_krm = new KerenisRandomMessage();
					_krm.begin();
				}
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @author
	 *
	 */
	class spawnBoss extends TimerTask {
		public void begin() {
			final Timer timer = new Timer();
			timer.schedule(this, 0);
		}

		/*
		 * 7655 バフォメット 7656 俺を呼んだのは誰だ！ 7657 ふふふ…ハーディン！おまえか！くはははは！ 7658
		 * あは！よりどりみどりね！ 7659 ついに来たか、ケレニス！ 7660 何！ケレニス、どうしたんだ！オリム、一体何があったんだ！
		 * //俺を呼んだのは・・・
		 */
		@Override
		public void run() {
			try {
				if (getCurRound() == 4) {
					spawnOneMob(getLocation(), 91291);// デスナイト
				} else if (getCurRound() == 8) {
					spawnOneMob(getLocation(), 91293);// フェニックス
				} else if (getCurRound() == 12) {
					int cnt = 0;
					for (int i = 0; i < _roundStatus.length; i++) {
						if (getRoundStatus(i) == 1) {
							cnt++;
						}
					}
					//L1MonsterInstance Kerenis = null;
					if (8 < cnt) {// 9回以上早いモードならバフォメット出現、それ以外はリーパー
						_lunker = spawnOneMob(getLocation(), 91294);// バフォメット
					} else {
						_lunker = spawnOneMob(getLocation(), 91290);// リーパー
					}
					final L1Location loc = new L1Location(32711, 32845,
							getMapId());
					boolean kerenisSpawn = true;

					/*
					 * 7656 俺を呼んだのは誰だ！ 7657 ふふふ…ハーディン！おまえか！くはははは！
					 */
					_lunker.broadcastPacket(new S_NpcChatPacket(_lunker, "$7656", 0));
					Thread.sleep(1500);
					_lunker.broadcastPacket(new S_NpcChatPacket(_lunker, "$7657", 0));
					if (!isKerenisAngry()) {// ケレニスが怒っていなければ出てくれる
						_Kerenis = spawnOneMob(loc, 91296);
					} else {// 怒っている場合で全て遅延処理の場合、ブラックケレニスへ
						if (isHardMode()) {
							_Kerenis = spawnOneMob(loc, 91295);
						} else {
							kerenisSpawn = false;
						}
					}
					if (kerenisSpawn) {
						_Kerenis.tagertClear();
						_Kerenis.setTarget(_lunker);
						_lunker.tagertClear();
						_lunker.setTarget(_Kerenis);
						if (_Kerenis.getNpcId() == 91296) {// ケレニス
							/*
							 * ちょっと遅れたかしらね。 7820 ケレニス！モンスターの意識をそらしてくれ！ 7821
							 * 分かりました！でもすこし手間取りそう… 7822 ハーディン、力を解き放ったら何が起こるか…
							 * 7823 あとはわしに任せるんじゃ、ケレニス。 7824
							 */
							Thread.sleep(1500);
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7820", 0));
							Thread.sleep(1500);
							_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7821", 0));
							Thread.sleep(1500);
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7822", 0));
							Thread.sleep(1500);
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7823", 0));
							Thread.sleep(1500);
							_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7824", 0));
						} else {// ブラックウィングケレニス
							/*
							 * オリム！覚悟は出来たかしら？ 7815 わたしのエモノを横取りしたのはだれ！？ 7816
							 * オリム、何があったんじゃ！？ 7817 まずはあなたが相手よ！ 7818 この生意気な女め！
							 * 7819
							 */
							Thread.sleep(1500);
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7815", 0));
							Thread.sleep(1500);
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7816", 0));
							Thread.sleep(1500);
							_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7817", 0));
							Thread.sleep(1500);
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7818", 0));
							Thread.sleep(1500);
							_lunker.broadcastPacket(new S_NpcChatPacket(_lunker, "$7819", 0));
							Thread.sleep(1500);
						}
					}
				}
			} catch (final InterruptedException e) {

				e.printStackTrace();
			}
		}
	}

	/* インスタンスマップを生成するため、作成したスイッチおよびトラップは削除できるようにインスタンスを保持しておく */

	private L1Location _location; // 中心点

	private short _mapId;

	private L1PcInstance _leader;

	public void setLeader(L1PcInstance leader) {
		this._leader = leader;
	}

	private int[] _randomRuneEffect;

	private int[] _roundStatus;

	private int[] _switchStatus;

	private L1NpcInstance _Hardin;

	private L1NpcInstance _Kerenis;

	private boolean _active;

	private boolean _receiveAllAction;

	private boolean _kerenisAngry;

	private static final Logger _log = Logger.getLogger(L1HardinQuestInstance.class.getName());

	private boolean _alreadyFirstSwitch;

	private boolean _alreadySecondSwitch;

	private boolean _alreadyPortal;

	//private boolean _hintAlready;

	private int _curRound;

	spawnBoss _sb;

	HqThread _hq;

	Preliminary _preliminary;

	HardinQuestMonitor _hqm;

	KerenisRandomMessage _krm;

	KerenisPart1 _kp1;

	KerenisPart2 _kp2;

	KerenisPart3 _kp3;

	KerenisPart4 _kp4;

	assailantSpawn _as;

	attackKerenis _ak;

	private boolean _BlackRuneTrodAlready;

	orcVocabulary _ov;

	private boolean _blackRune;

	private int _actionKerenis;

	private int _actionHardin;

	private int[] _onPlayerList;

	L1NpcInstance _lunker;

	private boolean _alreadyGuardManDeath;

	private boolean _deleteTransactionNow;

	private final ArrayList<L1TrapInstance> _trapList = new ArrayList<L1TrapInstance>();

	private ArrayList<L1DoorInstance> _doors;

	moveWall _mw;

	/**
	 * @param mapId
	 */
	public L1HardinQuestInstance(short mapId) {
		setMapId(mapId);
		init();
	}

	private static final int WALL_GFXID = 7536;
	private final L1DoorGfx _wallGfx = L1DoorGfx.findByGfxId(WALL_GFXID);

	private void addDoor(L1Location loc) {
		// XXX keeperId = 1とすることで開閉を防ぐ。
		L1DoorInstance door = DoorTable.getInstance().createDoor(0, _wallGfx, loc, 0, 1, false);
		getAllDoors().add(door);
	}

	/**
	 * @param trap
	 */
	private void addTrapList(L1TrapInstance trap) {
		_trapList.add(trap);
	}

	/**
	 * @param loc
	 * @param npcId
	 * @param groupId
	 */
	private void doSpawnGroup(L1Location loc, int npcId, int groupId) {
		final L1NpcInstance mob = new L1NpcInstance(NpcTable.getInstance().getTemplate(npcId));

		mob.setId(IdFactory.getInstance().nextId());
		mob.setHeading(5);
		mob.setX(loc.getX());
		mob.setHomeX(loc.getX());
		mob.setY(loc.getY());
		mob.setHomeY(loc.getY());
		mob.setMap((short) loc.getMapId());

		L1MobGroupSpawn.getInstance().doSpawn(mob, groupId, true, false);

		mob.deleteMe();
	}

	public void end() {
		// this.outPushPlayer();// プレイヤーを強制的にマップから追い出す。
		setDeleteTransactionNow(true);// 削除開始フラグ
		killTimer();// スレッドおよびタイマーが全て終了するのを待つ
		reset();// 保持しているMapのオブジェクトをクリア
		setDeleteTransactionNow(false);// 削除終了フラグ
		L1HardinQuest.getInstance().resetActiveMaps(getMapId());// 該当マップを未使用に
	}

	private int getActionHardin() {
		return _actionHardin;
	}

	private int getActionKerenis() {
		return _actionKerenis;
	}

	private ArrayList<L1DoorInstance> getAllDoors() {
		return _doors;
	}

	private int getBoneLaidStatus() {// 1なら早い、２なら遅い
		return getSwitchStatus(_switchStatus.length - 1);
	}

	private int getCurRound() {
		return _curRound;
	}

	L1Location getLocation() {
		return _location;
	}

	public short getMapId() {
		return _mapId;
	}

	private int[] getOnPlayerList() {
		return _onPlayerList;
	}

	private int getRandomRuneEffect(int i) {
		return _randomRuneEffect[i];
	}

	private boolean getReceiveAllAction() {
		return _receiveAllAction;
	}

	private int getRoundStatus(int i) {
		return _roundStatus[i];
	}

	private int getSwitchStatus(int x) {
		return _switchStatus[x];
	}

	private ArrayList<L1TrapInstance> getTrapList() {
		return _trapList;
	}

	/**
	 * @throws InterruptedException
	 */
	public void guardmanDeath() throws InterruptedException {
		GuardmanDeath temp = new GuardmanDeath();
		temp.begin();
	}
	class GuardmanDeath extends TimerTask {
		public void begin() {
			final Timer timer = new Timer();
			timer.schedule(this, 0);
		}

		@Override
		public void run() {
			if (!isDeleteTransactionNow()) {// 念のため
				try {
					if (getSwitchStatus(3) == 0) {
						setSwitchStatus(3, 1);
					}
					setAlreadyGuardManDeath(true);
					if (_leader.getMapId() == getMapId()) {
						_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_ep008"));
					}
					// まずはオリムだけ先に送ってあげるわ、いってらっしゃい。
					_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7643", 0));// 7643
					// オリム、準備がよければハーディンのところに送っちゃうよ。
					if (getReceiveAllAction()) {
						setActionKerenisDirect(0);
						Thread.sleep(8000);
						if (getActionKerenis() == 1) {
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7567", 0));
							// 7567 わたしもすぐ行くわ。
						} else {
							if (getActionKerenis() == 2) {
								_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7575", 0));
								// 7575 オリムは言うとおりにしててよ。
							} else {// 0
								_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7646", 0));
								// 7646 なにぼーっとしてんのよ！さっさと送っちゃうわよ！
							}
						}
					} else {// 全て無視
						_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7644", 0));// 7644
						// オリムのくせに生意気！
						setActionKerenisDirect(0);
						Thread.sleep(3000);
						if (getActionKerenis() == 1) {
							setKerenisAngry(true);
							_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7568", 0));
							// yes 7568 もうやめてよ…
						} else {
							// musi 7645 バイバイ！
							if (getActionKerenis() == 0) {
								_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7645", 0));
							}
						}
					}
					Thread.sleep(2500);
					if(_leader != null){
						if(_leader.getMapId()==getMapId()){
							L1Teleport.teleport(_leader, 32718, 32849, getMapId(), 5, true);
						}
					}
					/*
					 * 7647 オリムも来たようじゃな。それでは儀式が始まるぞ。こっちじゃ。 7648 早くせんと扉が閉まってしまうぞ！急ぐのじゃ。 7649
					 * ああ、時間切れじゃ。悪い気が地上に流れないように防がなくては。
					 */
					sendMessage("$7647", 0);
					Thread.sleep(4000);
					sendMessage("$7648", 0);
					Thread.sleep(4000);
					sendMessage("$7649", 0);
					sendShockWave();
					Thread.sleep(5000);
					if (!isDeleteTransactionNow()) {
						_hq = new HqThread();
						GeneralThreadPool.getInstance().execute(_hq);
					}
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
			this.cancel();
		}
	}
	
	GuestSpawn _gs;

	class GuestSpawn extends Thread {
		@Override
		public void run() {
			try {
				sendShockWave();
				final L1Location loc = new L1Location();
				loc.set(32707, 32858, getMapId());
				if (isHardMode()) {
					L1NpcInstance Mob = null;
					if (isKerenisAngry()) {// バルログ
						Mob = spawnOne(loc, 91344, 0);
						Mob.broadcastPacket(new S_NpcChatPacket(Mob, "$7694", 0));
						// 7694 全ては計画通り。ふっふっふ…
						Thread.sleep(1000);
						Mob.broadcastPacket(new S_NpcChatPacket(Mob, "$7695", 0));// 7695
						// これもすべてあのお方の計画に含まれているからな…
						Thread.sleep(1000);
						Mob.broadcastPacket(new S_NpcChatPacket(Mob, "$7709", 0));// 7709
						// 疑念は消えないと思うが、あのウィザードがお前たちに猶予を与えるとは思えん。ククク。
						Thread.sleep(1000);
						_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7677", 0));// 7677
						// さあ、早く！脱出するんじゃ！熱気で肺が焼けてしまう前に！
					} else {
						Mob = spawnOne(loc, 91343, 0);
						Mob.broadcastPacket(new S_NpcChatPacket(Mob, "$7663", 0));// 7663
						// 諸君、ご苦労！期待以上の成果だ。クククク…
						Thread.sleep(1000);
						Mob.broadcastPacket(new S_NpcChatPacket(Mob, "$7664", 0));// 7664
						// ケレニスまで気絶させるとは…こりゃおどろいたな！ククク。
						Thread.sleep(1000);
						Mob.broadcastPacket(new S_NpcChatPacket(Mob, "$7665", 0));// 7665
						// 気になることはいっぱいあるだろうが、あのウィザードはお前たちに時間をくれないだろう！ククク。
						Thread.sleep(1000);
						_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7837", 0));
						// お前たち早く逃げるのじゃ！この領域を封印する！ 7837
					}

					Thread.sleep(1000);
					_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7838", 0));
					// 南口を塞ぐ！急ぐのじゃ！ 7838
					Thread.sleep(1000);
					_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7839", 0));
					// 脱出口は東の方角じゃ！急げ！ 7839
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 初期設定をする
	 */
	private void init() {
		setActive(true);
		// 1400000 過去話せる島-フィールド 1 91297 32741 32930 0 0 6 0 9000 0
		_location = new L1Location(32707, 32846, getMapId());
		_switchStatus = new int[4];
		_roundStatus = new int[12];
		_randomRuneEffect = new int[9];
		_doors = new ArrayList<L1DoorInstance>();
		final RandomGenerator rd = RandomGeneratorFactory.getSharedRandom();
		int value = 0;
		int[] tmp = new int[4];
		boolean find = false;
		setRandomRuneEffect(0, value);// 0番目を0に設定
		for (int i = 0; i < 4; i++) {
			while (!find) {
				value = rd.nextInt(4) + 1;
				find = searchRandomRuneEffect(value, tmp);
			}
			tmp[i] = value;
			find = false;
		}
		for (int i = 0; i < 4; i++) {
			if (rd.nextInt(2) == 1) {
				setRandomRuneEffect(i * 2 + 1, (tmp[i] - 1) * 2 + 1);
				setRandomRuneEffect(i * 2 + 2, (tmp[i] - 1) * 2 + 2);
			} else {
				setRandomRuneEffect(i * 2 + 1, (tmp[i] - 1) * 2 + 2);
				setRandomRuneEffect(i * 2 + 2, (tmp[i] - 1) * 2 + 1);
			}
		}
		final L1Location loc = new L1Location();
		loc.set(32742, 32930, getMapId());

		_Kerenis = spawnOne(loc, 91297, 6);
		loc.set(32733, 32724, getMapId());
		// 魔法陣91320
		_Hardin = spawnOne(loc, 91330, 6);// テレポート後 32716 32846
		loc.set(32684, 32817, getMapId());
		spawnOne(loc, 81167, 0);
		loc.set(32732, 32789, getMapId());
		spawnOne(loc, 81167, 0);
		loc.set(32760, 32791, getMapId());
		spawnOne(loc, 81167, 0);
		loc.set(32667, 32874, getMapId());
		spawnOne(loc, 81167, 0);
		loc.set(32729, 32854, getMapId());
		spawnOne(loc, 81167, 0);
		// 玉
		// 91325 magic-boll 青
		// 91326 magic-boll 赤
		loc.set(32666, 32817, getMapId());
		spawnOne(loc, 91326, 0);
		loc.set(32668, 32817, getMapId());
		spawnOne(loc, 91326, 0);
		loc.set(32668, 32819, getMapId());
		spawnOne(loc, 91326, 0);
		loc.set(32666, 32819, getMapId());
		spawnOne(loc, 91326, 0);

		loc.set(32809, 32837, getMapId());
		spawnOne(loc, 91326, 0);
		loc.set(32809, 32839, getMapId());
		spawnOne(loc, 91326, 0);
		loc.set(32807, 32837, getMapId());
		spawnOne(loc, 91326, 0);
		loc.set(32807, 32839, getMapId());
		spawnOne(loc, 91326, 0);
		//
		loc.set(32806, 32863, getMapId());
		spawnOne(loc, 91339, 0);
		loc.set(32808, 32864, getMapId());
		spawnOne(loc, 91339, 0);
		loc.set(32800, 32864, getMapId());
		spawnOne(loc, 91339, 0);
		loc.set(32799, 32866, getMapId());
		spawnOne(loc, 91339, 0);
		loc.set(32807, 32870, getMapId());
		spawnOne(loc, 91339, 0);
		loc.set(32806, 32872, getMapId());
		spawnOne(loc, 91339, 0);
		loc.set(32798, 32872, getMapId());
		spawnOne(loc, 91339, 0);
		loc.set(32800, 32873, getMapId());
		spawnOne(loc, 91339, 0);

		//loc.set(32802, 32868, getMapId());
		//spawnOne(loc, 91338, 0);

		/* トラップ追加 */

		loc.set(32666, 32817, getMapId());
		setSwitch(loc, 54);
		loc.set(32668, 32817, getMapId());
		setSwitch(loc, 54);
		loc.set(32668, 32819, getMapId());
		setSwitch(loc, 54);
		loc.set(32666, 32819, getMapId());
		setSwitch(loc, 54);

		loc.set(32712, 32793, getMapId());
		setSwitch(loc, 55);
		loc.set(32703, 32791, getMapId());
		setSwitch(loc, 55);
		loc.set(32710, 32803, getMapId());
		setSwitch(loc, 55);
		loc.set(32703, 32800, getMapId());
		setSwitch(loc, 55);

		loc.set(32807, 32839, getMapId());
		setSwitch(loc, 56);
		loc.set(32809, 32837, getMapId());
		setSwitch(loc, 56);
		loc.set(32807, 32837, getMapId());
		setSwitch(loc, 56);
		loc.set(32809, 32839, getMapId());
		setSwitch(loc, 56);

		loc.set(32807, 32870, getMapId());
		setSwitch(loc, 57);
		loc.set(32806, 32872, getMapId());
		setSwitch(loc, 57);
		loc.set(32799, 32866, getMapId());
		setSwitch(loc, 57);
		loc.set(32800, 32864, getMapId());
		setSwitch(loc, 57);
		loc.set(32808, 32864, getMapId());
		setSwitch(loc, 57);
		loc.set(32806, 32863, getMapId());
		setSwitch(loc, 57);
		loc.set(32798, 32872, getMapId());
		setSwitch(loc, 57);
		loc.set(32800, 32873, getMapId());
		setSwitch(loc, 57);

		loc.set(32684, 32816, getMapId());
		setSwitch(loc, 60);
		loc.set(32732, 32789, getMapId());
		setSwitch(loc, 61);
		loc.set(32760, 32791, getMapId());
		setSwitch(loc, 62);
		loc.set(32729, 32854, getMapId());
		setSwitch(loc, 63);
		loc.set(32667, 32874, getMapId());
		setSwitch(loc, 64);

		final int mobGroupIdFirst = 78;
		int mobGroupLeaderId;
		int temp;

		loc.set(32785, 32871, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32725, 32789, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32745, 32813, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32686, 32790, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32664, 32813, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32669, 32850, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32683, 32813, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32715, 32810, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32745, 32789, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32784, 32795, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32805, 32797, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32792, 32828, getMapId());
		temp = rd.nextInt(7) + mobGroupIdFirst;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		loc.set(32775, 32846, getMapId());
		temp = 77;
		mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(temp).getLeaderId();
		doSpawnGroup(loc, mobGroupLeaderId, temp);
		/* ハーディンの日記1~10 */
		final int diaryFirstId = 50008;
		final int[] x = { 32763, 32758, 32663, 32667, 32722 };
		final int[] y = { 32800, 32801, 32876, 32867, 32866 };

		for (int i = 0; i < x.length; i++) {
			if (rd.nextInt(100) <= 50) {// 暫定
				loc.set(x[i], y[i], getMapId());
				L1World.getInstance().getInventory(loc).storeItem(
						diaryFirstId + rd.nextInt(10), 1);
			}
		}

		if (getMapId() != 9000) {
			// ドア
			for (L1DoorInstance door : DoorTable.getInstance().getDoorList()) {
				if (door.getMapId() == 2) {// TIC2Fのドア
					if (door.getX() == 32684 && door.getY() == 32850) {
						continue;
					}
					L1DoorGfx gfx = L1DoorGfx.findByGfxId(door.getGfxId());
					L1DoorInstance createDoor = DoorTable.getInstance().createDoor(0, gfx,
							new L1Location(door.getX(), door.getY(), getMapId()), 0, 0, false);
				}
			}
			// 炎
			for (L1Object obj : L1World.getInstance().getObject()) {
				if (obj.getMapId() == 2) {
					if (obj instanceof L1FieldObjectInstance) {
						if (((L1FieldObjectInstance) obj).getNpcId() == 91269) {
							continue;
						}
						spawnOne(new L1Location(obj.getX(), obj.getY(), getMapId()),
								((L1FieldObjectInstance) obj).getNpcId(), 0);
					}
				}
			}
		}
		createWalls();
		// 91316 red
		// 91317　red魔法陣　点滅
		// 91318 白
		// 91319　白魔法陣　点滅
	}

	private void createWalls() {
		for (int i = 0; i < 10; i++) {
			// 前方
			addDoor(new L1Location(32702 + i, 32866, getMapId()));
			// 後方
			addDoor(new L1Location(32703 + i, 32872, getMapId()));
		}
	}

	/**
	 * @return 入場可能～競技終了まではtrue,それ以外はfalseを返す。
	 */
	public boolean isActive() {
		return _active;
	}

	public boolean isAlreadyFirstSwitch() {
		return _alreadyFirstSwitch;
	}

	private boolean isAlreadyGuardManDeath() {
		return _alreadyGuardManDeath;
	}

	public boolean isAlreadyPortal() {
		return _alreadyPortal;
	}

	public boolean isAlreadySecondSwitch() {
		return _alreadySecondSwitch;
	}

	private boolean isBlackRuneCreated() {

		return _blackRune;
	}

	private boolean isBlackRuneTrodAlready() {

		return _BlackRuneTrodAlready;
	}

	public boolean isDeleteTransactionNow() {
		return _deleteTransactionNow;
	}

	private boolean isHardMode() {
		boolean flag = true;
		for (int n = 0; n < _switchStatus.length - 1; n++) {
			if (getSwitchStatus(n) != 2) {// 2=遅い
				flag = false;
				break;
			}
		}
		return flag;
	}

	private boolean isKerenisAngry() {
		return _kerenisAngry;
	}

	private void killTimer() {
		try {

			/* ケレニス　ランダムメッセージ　タイマー */
			if (_krm != null) {
				_krm.cancel();
				_krm = null;
			}
			/* ケレニス ランダム攻撃 タイマー */
			if (_ak != null) {
				_ak.cancel();
				_ak = null;
			}
			/* オーク・オーガ・KBBの襲撃　タイマー */
			if (_as != null) {
				_as.cancel();
				_as = null;
			}
			/* ケレニス、バフォメット出現 */
			if (_sb != null) {
				_sb.cancel();
				_sb = null;
			}
			/* クエスト開始のメッセージ */
			if (_preliminary != null) {
				_preliminary.join();
				_preliminary = null;
			}
			/* 動く壁 */
			if (_mw != null) {
				_mw.cancel();
				_mw = null;
			}
			/*
			 * モニター if (_hqm != null) { _hqm.cancel(); _hqm = null; }
			 */

			/* オーク出現 タイマー */
			if (_ov != null) {
				_ov.cancel();
				_ov = null;
			}

			/* UB(儀式)スレッド */
			if (_hq != null) {
				_hq.join();// 終了まで待つ
				_hq = null;
			}

			/* ケレニス進行パート Start スレッド1~4 */
			if (_kp1 != null) {
				_kp1.join();// 終了まで待つ
				_kp1 = null;
			}
			if (_kp2 != null) {
				_kp2.join();// 終了まで待つ
				_kp2 = null;
			}
			if (_kp3 != null) {
				_kp3.join();// 終了まで待つ
				_kp3 = null;
			}
			if (_kp4 != null) {
				_kp4.join();// 終了まで待つ
				_kp4 = null;
			}
			/* ゲスト出現 */
			if (_gs != null) {
				_gs.join();// 終了まで待つ
				_gs = null;
			}
			/* ケレニス進行パート　End */
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @param npc
	 * @throws InterruptedException
	 */
	public void lunkerDie(L1NpcInstance npc) throws InterruptedException {
		boolean kerenisSpawn = false;
		boolean guestSpawn = false;
		if (!isKerenisAngry()) {
			kerenisSpawn = true;// リトルケレニス
		} else {
			if (isHardMode()) {// ブラックウィングケレニス
				kerenisSpawn = true;
			}
		}
		if (isHardMode()) {
			guestSpawn = true;
		}

		if (kerenisSpawn) {
			if (npc.getNpcId() == 91295 || npc.getNpcId() == 91296) {// ケレニス
				boolean find = false;
				for (L1Object obj : L1World.getInstance().getVisiblePoint(
						getLocation(), 15)) {
					if (obj instanceof L1MonsterInstance) {
						if (((L1MonsterInstance) obj).getNpcId() == 91290
								|| ((L1MonsterInstance) obj).getNpcId() == 91294) {
							if (!((L1MonsterInstance) obj).isDead()) {
								find = true;
							}
							break;
						}
					}
				}
				if (find) {// 死んでいない
					BossEndTalks bet = new BossEndTalks(1, _Hardin, _Kerenis);
					bet.begin();
				} else {// 死亡済み
					if (guestSpawn) {
						_gs = new GuestSpawn();
						GeneralThreadPool.getInstance().execute(_gs);
					}
					_mw = new moveWall();
					_mw.begin();
				}
			} else {// バフォ　リーパー
				boolean find = false;
				for (L1Object obj : L1World.getInstance().getVisiblePoint(
						getLocation(), 15)) {
					if (obj instanceof L1MonsterInstance) {
						if (((L1MonsterInstance) obj).getNpcId() == 91295
								|| ((L1MonsterInstance) obj).getNpcId() == 91296) {
							if (!((L1MonsterInstance) obj).isDead()) {
								find = true;
							}
							break;
						}
					}
				}
				if (find) {// 死んでいない
					BossEndTalks bet = new BossEndTalks(2, _Hardin, _Kerenis);
					bet.begin();
				} else {// 死亡済み
					if (guestSpawn) {
						_gs = new GuestSpawn();
						GeneralThreadPool.getInstance().execute(_gs);
					}
					_mw = new moveWall();
					_mw.begin();
				}
			}
		} else {// 1匹の場合は検索の必要無し
			if (guestSpawn) {
				_gs = new GuestSpawn();
				GeneralThreadPool.getInstance().execute(_gs);
			}
			_mw = new moveWall();
			_mw.begin();
		}
	}
	
	private void setItemEffect(L1ItemInstance item, int type){
		if(type == 0){
			item.setIsHaste(true) ;
		}else if(type == 1){
			item.setMr(5) ;
		}else if(type == 2){
			item.setHitModifier(1) ;
		}else if(type == 3){
			item.setBowHitModifier(1) ;
		}else if(type == 4){
			item.setDmgModifier(1) ;
			item.setBowDmgModifier(1);
		}else if(type == 5){
			item.setSp(1) ;
		}else if(type == 6){
			item.setHpr(1) ;
		}else if(type == 7){
			item.setMpr(1) ; ;
		}
	}

	/**
	 * @throws InterruptedException
	 */
	public void onBlackRune() throws InterruptedException {
		// 魔法陣 32802 32868
		// 上
		// 左
		// →
		// 下
		/*
		 * 西側の上の足場 南側の左の足場はを持っている。 東側の下の足場はを持っている。 北側の右の足場はを持っている
		 * getRandomRuneEffect(type-1)*2+((type+getRandomRuneEffect(type))%2)
		 * 8681 [ヘイスト] 8682 [魔法抵抗] 8685 [命中] 8686 [遠距離命中]
		 *
		 * 8683 [追加打撃]
		 *
		 * 8684 [魔力] 8687 [HP自然回復] 8688 [MP自然回復]
		 */
		if (isBlackRuneTrodAlready()) {
			return;
		}
		sendShockWave();
		setBlackRuneTrodAlready(true);
		final int mapId = getMapId();
		int starItem = 0;// オプション。星を付加する　未実装
		int player_cnt = 0;
		for (int i = 0; i < getOnPlayerList().length; i++) {
			if (getOnPlayerList()[i] != 0) {
				player_cnt++;
			}
		}
		
		if (player_cnt != 0) {
			if(player_cnt <= 4){//4以下は四つ星
				starItem = 21156;
			}else if(player_cnt == 5){
				starItem = 21157;
			}else if(player_cnt == 6){
				starItem = 21158;
			}else if(player_cnt >= 7){
				starItem = 21159;
			}
		}
		
		final L1Location loc = new L1Location(32802, 32868, mapId);// 黒のルーン
		for (final L1Object obj : L1World.getInstance().getVisiblePoint(loc, 0)) {
			if (obj instanceof L1PcInstance) {
				L1ItemInstance item = L1World.getInstance().getInventory(loc).storeItem(starItem,1);//取得条件不明
				final RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
				final int rnd = random.nextInt(8);
				setItemEffect(item, rnd);//最後に踏む人が得るタリスマンの条件不明のためランダム
			}
		}
		
		loc.set(32806, 32863, mapId);
		for (final L1Object obj : L1World.getInstance().getVisiblePoint(loc, 0)) {
			if (obj instanceof L1PcInstance) {
				for (int i = 0; i < getOnPlayerList().length; i++) {
					if (obj.getId() == getOnPlayerList()[i]) {
						L1ItemInstance item = L1World.getInstance().getInventory(loc).storeItem(starItem, 1);
						setItemEffect(item,getRandomRuneEffect(i + 1) - 1);
					}
				}
			}
		}

		loc.set(32808, 32864, mapId);
		for (final L1Object obj : L1World.getInstance().getVisiblePoint(loc, 0)) {
			if (obj instanceof L1PcInstance) {
				for (int i = 0; i < getOnPlayerList().length; i++) {
					if (obj.getId() == getOnPlayerList()[i]) {
						L1ItemInstance item =  L1World.getInstance().getInventory(loc).storeItem(starItem, 1);
						setItemEffect(item,getRandomRuneEffect(i + 1) - 1);
					}

				}
			}
		}

		loc.set(32800, 32864, mapId);
		for (final L1Object obj : L1World.getInstance().getVisiblePoint(loc, 0)) {
			if (obj instanceof L1PcInstance) {
				for (int i = 0; i < getOnPlayerList().length; i++) {
					if (obj.getId() == getOnPlayerList()[i]) {
						L1ItemInstance item =  L1World.getInstance().getInventory(loc).storeItem(starItem, 1);
						setItemEffect(item,getRandomRuneEffect(i + 1) - 1);
					}
				}
			}
		}

		loc.set(32799, 32866, mapId);
		for (final L1Object obj : L1World.getInstance().getVisiblePoint(loc, 0)) {
			if (obj instanceof L1PcInstance) {
				for (int i = 0; i < getOnPlayerList().length; i++) {
					if (obj.getId() == getOnPlayerList()[i]) {
						L1ItemInstance item =  L1World.getInstance().getInventory(loc).storeItem(starItem, 1);
						setItemEffect(item,getRandomRuneEffect(i + 1) - 1);
					}
				}
			}
		}

		loc.set(32807, 32870, mapId);
		for (final L1Object obj : L1World.getInstance().getVisiblePoint(loc, 0)) {
			if (obj instanceof L1PcInstance) {
				for (int i = 0; i < getOnPlayerList().length; i++) {
					if (obj.getId() == getOnPlayerList()[i]) {
						L1ItemInstance item =  L1World.getInstance().getInventory(loc).storeItem(starItem, 1);
						setItemEffect(item,getRandomRuneEffect(i + 1) - 1);
					}
				}
			}
		}

		loc.set(32806, 32872, mapId);
		for (final L1Object obj : L1World.getInstance().getVisiblePoint(loc, 0)) {
			if (obj instanceof L1PcInstance) {
				for (int i = 0; i < getOnPlayerList().length; i++) {
					if (obj.getId() == getOnPlayerList()[i]) {
						L1ItemInstance item =  L1World.getInstance().getInventory(loc).storeItem(starItem, 1);
						setItemEffect(item,getRandomRuneEffect(i + 1) - 1);
					}
				}
			}
		}

		loc.set(32798, 32872, mapId);
		for (final L1Object obj : L1World.getInstance().getVisiblePoint(loc, 0)) {
			if (obj instanceof L1PcInstance) {
				for (int i = 0; i < getOnPlayerList().length; i++) {
					if (obj.getId() == getOnPlayerList()[i]) {
						L1ItemInstance item =  L1World.getInstance().getInventory(loc).storeItem(starItem, 1);
						setItemEffect(item,getRandomRuneEffect(i + 1) - 1);
					}
				}
			}
		}

		loc.set(32800, 32873, mapId);
		for (final L1Object obj : L1World.getInstance().getVisiblePoint(loc, 0)) {
			if (obj instanceof L1PcInstance) {
				for (int i = 0; i < getOnPlayerList().length; i++) {
					if (obj.getId() == getOnPlayerList()[i]) {
						L1ItemInstance item =  L1World.getInstance().getInventory(loc).storeItem(starItem, 1);
						setItemEffect(item,getRandomRuneEffect(i + 1) - 1);
					}
				}
			}
		}

		/*
		 * 8719 中央の足場が作動して報酬が現われました。
		 */
		sendMessage("$8719", 0);
		Thread.sleep(30 * 1000);// 30秒後強制帰還
		if (!isDeleteTransactionNow())
			outPushPlayer();// end();
	}

	/**
	 * @throws InterruptedException
	 */
	public void onFirstSwitch() throws InterruptedException {// 32741 32930
		if (!isDeleteTransactionNow()) {
			sendShockWave();
			_kp2 = new KerenisPart2();
			GeneralThreadPool.getInstance().execute(_kp2);
		}

		if (getSwitchStatus(0) == 0) {
			setSwitchStatus(0, 1);
		}
		setAlreadyFirstSwitch(true);
		/* グラフィックを表示させる */
		final L1Location loc = new L1Location(32744, 32932, getMapId());
		spawnOne(loc, 91326, 0);// 赤玉
		loc.set(32667, 32818, getMapId());
		spawnOne(loc, 91318, 0);//

		sendMessage(_Hardin.getName() + " : $7621", 0);
		_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7621", 0));// 7621
		// 早くアロートラップを設置するんじゃ。簡単なことじゃろ？
		// オークタイマー
		/* タイマー開始 */
		_ov = new orcVocabulary();
		_ov.begin();

		// ああっ！あのオークたちは…困ったわねぇ。オリム～お願い～ね？ またオークが来たわ！前に白く染めたやつ！オリム、頼んだわよ！
		if (_leader.getMapId() == getMapId()) {
			_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_ep005"));
		}
		_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7623", 0));// //7623
		// またオークが来たわ！前に白く染めたやつ！オリム、頼んだわよ！
		Thread.sleep(8000);
		if (getActionKerenis() == 1) {
			_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7564", 0));// 7564
			// オリムのことだから、注意ぐらいは引き付けてくれるかもね。
		} else {
			if (getActionKerenis() == 2) {
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7572", 0));
				// 7572 ちゃんとやってよ。オリム。
			} else {// 0
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7624", 0));
				// 7624 オリム?！なにびびってんのよ！
			}
		}

		// オーク出現
		final int mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(85).getLeaderId();
		doSpawnGroup(_leader.getLocation(), mobGroupLeaderId, 85);
		// タイマーセット
		// ランダムにMobGroupを出現させる
		_as = new assailantSpawn();
		_as.begin();
		_ak = new attackKerenis();
		_ak.begin();
		// 身近に敵がいた場合魔法攻撃を行う。後にランダム秒待機させる
	}

	/**
	 * @throws InterruptedException
	 */
	public void onSecondSwitch() throws InterruptedException {
		if (!isDeleteTransactionNow()) {
			sendShockWave();
			_kp3 = new KerenisPart3();
			GeneralThreadPool.getInstance().execute(_kp3);
		}
		if (getSwitchStatus(1) == 0) {
			setSwitchStatus(1, 1);
		}
		setAlreadySecondSwitch(true);
		final L1Location loc = new L1Location(32740, 32928, getMapId());
		spawnOne(loc, 91326, 0);
		loc.set(32712, 32793, getMapId());
		spawnOne(loc, 81167, 0);
		loc.set(32703, 32791, getMapId());
		spawnOne(loc, 81167, 0);
		loc.set(32710, 32803, getMapId());
		spawnOne(loc, 81167, 0);
		loc.set(32703, 32800, getMapId());
		spawnOne(loc, 81167, 0);

		_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7629", 0));// 7629
		// ふぅ…まだまだ余裕だけれど、急いでくれると助かるわね。
		sendMessage(_Hardin.getName() + " : $7628", 0);
		_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7628", 0));// 7628
		// ご苦労！次はこちらに来ることができる魔法陣を作ってくれ。さぁ急ぐんじゃ！
		// 鬱陶しいわね、このオークたち！ハーディン様はここがオークの巣窟と分かってたのかしら。
		// ハーディンはここがオークのなわばりって分かってたのでしょうね。
		if (_leader.getMapId() == getMapId()) {
			_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_ep006"));
		}
		_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7630", 0));// 7630
		// ハーディンはここがオークのなわばりって分かってたのでしょうね。
		setActionKerenisDirect(0);
		Thread.sleep(8000);
		final int mobGroupLeaderId = MobGroupTable.getInstance().getTemplate(85).getLeaderId();
		doSpawnGroup(_leader.getLocation(), mobGroupLeaderId, 85);
		if (getActionKerenis() == 1) {
			_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7565", 0));// 7565
			// こんの?！

		} else {
			if (getActionKerenis() == 2) {
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7573", 0));
				// 7573 いまさら何を言ってるの？
			} else {// 0
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7631", 0));
				// 7631 オリム?！しゃきっとしなさい！
			}
		}
	}

	/**
	 * @throws InterruptedException
	 */
	public void onThirdSwitch() throws InterruptedException {
		if (!isDeleteTransactionNow()) {
			_kp4 = new KerenisPart4();
			GeneralThreadPool.getInstance().execute(_kp4);
		}

		if (getSwitchStatus(2) == 0) {
			setSwitchStatus(2, 1);
		}
		setAlreadyPortal(true);
		final L1Location loc = new L1Location(32743, 32929, getMapId());
		spawnOne(loc, 91326, 0);

		/* テレポート先にグラフィックを表示させておく */
		loc.set(32787, 32821, getMapId());
		spawnOne(loc, 91318, 0);
		/* テレポートグラフィック付きポータルを表示させる */
		loc.set(32808, 32838, getMapId());
		spawnOne(loc, 91319, 0);
		/* ワールドに新しいトラップを作成して追加する */
		setSwitch(loc, 53);

		sendMessage(_Hardin.getName() + " : $7635", 0);
		_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7635", 0));// 7635
		// うむ！では門番を倒してこちらに来てくれ！
		_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7636", 0));// 7636
		// や、ばいな…ちょっと集中力が切れちゃったみたい…
		// これで終わりよ。がんばってね、オリム！ これで最後よ！頑張りなさい、オリム！
		if (_leader.getMapId() == getMapId()) {
			_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_ep007"));
		}
		_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7637", 0));// 7637
		// これで最後よ！頑張りなさい、オリム！

		Thread.sleep(8000);

		if (getActionKerenis() == 1) {
			_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7566", 0));// 7566
			// へえ、そうなの。

		} else {
			if (getActionKerenis() == 2) {
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7574", 0));
				// 7574 もう嫌…。
			} else {// 0
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7638", 0));
				// 7638 オ?リ?ム?！
			}
		}
		// 敵出現
		_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7639", 0));// 7639
		// なんなのよこれは！
	}

	/**
	 * MAP中のプレイヤーすべてを追い出す
	 */

	private void outPushPlayer() {
		for (final L1PcInstance pc : L1World.getInstance().getAllPlayers()) // ダンジョン内に居るPCを外へ出す
		{
			if (pc.getMapId() == getMapId()) {
				final RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
				final int rndx = random.nextInt(4);
				final int rndy = random.nextInt(4);
				final int locx = 32587 + rndx;
				final int locy = 32941 + rndy;
				final short mapid = 0;
				L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
			}
		}
	}

	/**
	 * @param door
	 */
	private void removeDoor(L1DoorInstance door) {
		getAllDoors().remove(door);
		DoorTable.getInstance().deleteDoorByLocation(door.getLocation());
	}

	private void reset() {
		try {

			for (final L1Object obj : L1World.getInstance().getObject()) {
				if (obj.getMapId() == getMapId()) {
					if (obj instanceof L1FieldObjectInstance) {
						L1World.getInstance().removeVisibleObject(obj);
						L1World.getInstance().removeObject(obj);
					} else if (obj instanceof L1EffectInstance) {
						L1World.getInstance().removeVisibleObject(obj);
						L1World.getInstance().removeObject(obj);
					} else if (obj instanceof L1ItemInstance) {
						final L1Inventory groundInventory = L1World.getInstance().getInventory(
								obj.getX(), obj.getY(), obj.getMapId());
						groundInventory.deleteItem((L1ItemInstance) obj);
						L1World.getInstance().removeVisibleObject(obj);
						L1World.getInstance().removeObject(obj);
					} else if (obj instanceof L1DoorInstance) {
						removeDoor((L1DoorInstance) obj);
					} else if (obj instanceof L1NpcInstance) {
						((L1NpcInstance) obj).deleteMe();
					}
				}
			}
			for (final L1TrapInstance obj : this.getTrapList()) {
				if (obj.getMapId() == getMapId()) {
					// System.out.println("通過");
					L1WorldTraps.getInstance().removeTrap(obj);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param pattern
	 * @param curRound
	 * @throws InterruptedException
	 */
	private void selectSpawn(int pattern, int curRound) throws InterruptedException {
		final int npcid[] = { 91274, 91275, 91276, 91277, 91278, 91279, 91280,
				91281, 91282, 91283, 91284, 91285, 91286, 91287, 91288, 91289,
				91292 };
		/*
		 * int lv[]={ 27 ,// 魔界の スケルトン ガード 27 ,// 魔界の スケルトン マークスマン 29 ,// 魔界の
		 * スケルトン ファイター 30 ,// 魔界の ダーク エレメンタル 32 ,// 魔界の サラマンダー 33 ,// 魔界の ゲイザー
		 * 37 ,// 魔界の サキュバス 42 ,// 魔界の オーク ゾンビ 44 ,// 魔界の ライカンスロープ 44 ,// 魔界の
		 * レッサー デーモン
		 *
		 * 46 ,// 魔界の ホーン ケルベロス 46 ,// 魔界の イフリート
		 *
		 * 47 ,// 魔界の ボーンドラゴン 48 ,// 魔界の バーニング アーチャー 50 ,// 魔界の バーニング ウォリアー 54
		 * ,// 魔界の シアー 55 ,// 魔界の グレート ミノタウルス
		 *
		 * };
		 */
		final RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
		int startPoint = 0;
		int endPoint = 16;
		int delay = 0;
		final int X[] = { 32706, 32695, 32702, 32717, 32706 };
		final int Y[] = { 32836, 32847, 32855, 32847, 32846 };
		final L1Location loc = new L1Location();
		if (pattern == 2) {
			// if(pattern==3){
			// startPoint=12;
			// }else{
			startPoint = 9;
			endPoint = 14;
			// }
		} else if (pattern == 3) {
			// if(pattern==3){
			// startPoint=12;
			// }else{
			startPoint = 12;
			endPoint = 14;
			// }
		} else {
			endPoint = 6;
		}
		for (int i = 0; i < curRound - 1; i++) {
			if (getRoundStatus(i) == 1) {
				if (endPoint < 16) {
					startPoint++;
					endPoint++;
				} else {
					break;
				}
			}else if (getRoundStatus(i) == 2) {//slow
				if (startPoint > 0) {
					startPoint--;
					endPoint--;
				} else {
					break;
				}
			}else{//stop case : 3

			}

		}
		if (isBonusStage()) {
			int s = random.nextInt(4);
			for (L1PcInstance pc  :  L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == getMapId()) {
					pc.sendPackets(new S_SkillSound(pc.getId(), 2028 + s));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 2028 + s));
				}
			}
		}
		for (int i = 0; startPoint + i <= endPoint; i++) {
			final int n = random.nextInt(5);
			loc.set(X[n] - 3 + random.nextInt(6), Y[n] - 3 + random.nextInt(6), getMapId());
			if (isBonusStage()) {
				// 揺れ＆意味不明な表示 WAHT THE!!
				spawnOneMob(loc, 91337);

			} else {
				spawnOneMob(loc, npcid[startPoint + random.nextInt(endPoint + 1 - startPoint)]);
			}
			delay += 500;
			Thread.sleep(500);
		}
		Thread.sleep(9000 - delay);
	}

	private void setBonusStage(boolean flag) {
		_bonusStage = flag;
	}

	private boolean isBonusStage() {
		return _bonusStage;
	}

	private boolean _bonusStage;

	/**
	 * ボスラウンド開始時のメッセージを送信する。
	 *
	 * @param curRound
	 *            開始するラウンド
	 * @throws InterruptedException
	 */
	/*
	 * 8704 4番目の封印が破壊されました。[4/12] 8705 デスナイト：誰だ…眠りの邪魔をするのは…私は…疲れているのだ… 8706
	 * 8番目の封印が破壊されました。[8/12] 8707 フェニックス：扉が開いたのか！？
	 */
	/**
	 * @param curRound
	 * @throws InterruptedException
	 */
	private void sendBossRoundMessage(int curRound) throws InterruptedException {
		if (curRound / 4 > 1) {
			sendMessage("$8706", 0);// blue
			Thread.sleep(3000);
			sendMessage("$8707", 0);// blue
		} else {
			sendMessage("$8704", 0);// blue
			Thread.sleep(3000);
			sendMessage("$8705", 0);// blue
		}
		_sb = new spawnBoss();
		_sb.begin();

	}

	/**
	 * UB開始時のメッセージを送信する。
	 *
	 * @throws InterruptedException
	 *
	 */
	/**
	 * @throws InterruptedException
	 */
	private void sendCountDown() throws InterruptedException {
		/*
		 * 7650 次元の境界が不安定になった隙をついて、魑魅魍魎どもが押し寄せてきたぞい！ 7651 用意はいいか！
		 */
		Thread.sleep(4000);
		sendMessage("$7650", 0);
		Thread.sleep(4000);
		sendMessage("$7651", 0);
		Thread.sleep(4000);
	}

	/**
	 * クエストに参加しているプレイヤーへメッセージ(S_GreenMessage)を送信する。
	 *
	 * @param s
	 *            送信する文字列
	 * @param cases
	 *            送信する範囲 0=ALL 1=リーダー 2=MAP内にいるリーダー以外の全てプレイヤー
	 */
	private void sendMessage(String s, int cases) {
		if(cases==0){
			for (final L1PcInstance pc  :  L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == this.getMapId()) {
					pc.sendPackets(new S_GreenMessage(s));
				}
			}
		}else if(cases==1){
			if (_leader.getMapId() == this.getMapId()) {
				_leader.sendPackets(new S_GreenMessage(s));
			}
		}else if(cases==2){
			for (final L1PcInstance pc  :  L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == this.getMapId() && _leader.getId() != pc.getId()) {
					pc.sendPackets(new S_GreenMessage(s));
				}
			}
		}

	}

	/**
	 * クエストに参加しているプレイヤーへ画面ゆれアクションを送信する。
	 */
	private void sendShockWave() {
		for (final L1PcInstance pc  :  L1World.getInstance().getAllPlayers()) {
			if (pc.getMapId() == this.getMapId()) {
				pc.sendPackets(new S_ShockWave());
			}
		}
	}

	/**
	 * ラウンド開始時のメッセージを送信する。
	 *
	 * @param curRound
	 *            　現在のラウンド
	 *
	 * @throws InterruptedException
	 */
	private void sendRoundMessage(int curRound) throws InterruptedException {
		final int MSGID = 8708;
		if ((curRound % 4 == 0) && (curRound != 12)) {
			Thread.sleep(5000);
			sendBossRoundMessage(curRound);
		} else {
			if (curRound != 12) {
				Thread.sleep(7000);
				sendMessage("$" + (MSGID + curRound
						- (int) (1 + Math.floor(curRound / 4))), 0);
				Thread.sleep(2000);
			} else {
				sendMessage("$7654", 0); // そろそろ大物が出てくる頃合じゃわい！
				Thread.sleep(15000);
				sendMessage("$8717", 0); // 8717 最後の封印が破壊されました。[12/12]
				sendShockWave();
				Thread.sleep(2000);
				_sb = new spawnBoss();
				_sb.begin();
			}
		}
		/*
		 * 8708 1番目の封印の解除が始まります。[1/12] 8709 2番目の封印の解除が始まります。[2/12] 8710
		 * 3番目の封印の解除が始まります。[3/12] 8711 5番目の封印の解除が始まります。[5/12] 8712
		 * 6番目の封印の解除が始まります。[6/12] 8713 7番目の封印の解除が始まります。[7/12] 8714
		 * 9番目の封印の解除が始まります。[9/12] 8715 10番目の封印の解除が始まります。[10/12] 8716
		 * 11番目の封印の解除が始まります。[11/12] 最後の封印が破壊されました。[12/12] 8717
		 */
	}

	/**
	 * サブラウンド開始時のメッセージを送信する。
	 *
	 * @param curSubRound
	 *            現在のサブラウンド
	 */
	private void sendSubRoundMessage(int curSubRound) {
		final int MSGID = 8689;
		if (getCurRound() != 12) {
			sendMessage("$" + (MSGID + curSubRound - 1), 0);
		}
	}

	/*
	 * 8689 封印が少しだけ解かれました。[1/4] 8690 封印が半分ほど解かれました。[2/4] 8691
	 * 封印が半分以上解かれました。[3/4] 8692 封印が完全に解かれました。[4/4]
	 */

	/**
	 * マップ内の全員に緑メッセージを送信 8720 ささやく風がオリムの耳の中に流れて行きます。
	 *
	 * オリム役のリーダーにはHTMLを表示させる
	 *
	 * @param type
	 *            踏まれた刻印番号 0=説明 1=北 2=西 3=東 4=南
	 */
	public void sendWisperWindow(int type) {
		// 8720 ささやく風がオリムの耳の中に流れて行きます。
		sendMessage("$8720",0);
		/*
		 * 西側の上の足場 南側の左の足場はを持っている。 東側の下の足場はを持っている。 北側の右の足場はを持っている
		 * getRandomRuneEffect(type-1)*2+((type+getRandomRuneEffect(type))%2)
		 * 8681 [ヘイスト] 8682 [魔法抵抗] 8685 [命中] 8686 [遠距離命中]
		 *
		 * 8683 [追加打撃]
		 *
		 * 8684 [魔力] 8687 [HP自然回復] 8688 [MP自然回復]
		 */
		if (getMapId() == _leader.getMapId()) {
			if (type == 0) {
				_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_int00"));// 説明
			} else {
				final String[] EffectBase = { "$8681", "$8682", "$8685",
						"$8686", "$8683", "$8684", "$8687", "$8688" };
						
				if (type == 1) {
					final String[] EffectType = { EffectBase[(getRandomRuneEffect(1) - 1)]};
					_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_int01", EffectType));// 北
				} else if (type == 2) {
					final String[] EffectType = { EffectBase[(getRandomRuneEffect(3) - 1)]};
					_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_int02", EffectType));// 西
				} else if (type == 3) {
					final String[] EffectType = { EffectBase[(getRandomRuneEffect(6) - 1)]};
					_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_int03", EffectType));// 東
				} else if (type == 4) {
					final String[] EffectType = { EffectBase[(getRandomRuneEffect(7) - 1)]};
					_leader.sendPackets(new S_NpcTalkReturn(_leader.getId(), "j_int04", EffectType));// 南
				}
			}
		}
	}

	/*
	 * コロシアム内のモンスターをカウントする
	 *
	 * @param curRound 開始するラウンド
	 *
	 * @return モンスター数
	 */
	private int searchCountMonster() {
		int mobCnt = 0;
		for (final L1Object obj : L1World.getInstance().getVisiblePoint(
				getLocation(), 13)) {
			if (obj instanceof L1MonsterInstance) {
				if (!((L1MonsterInstance) obj).isDead()) {
					mobCnt++;
				}
			}
		}
		return mobCnt;
	}

	/**
	 * 配列中にvalueと同じ値がないかチェックする
	 *
	 * @param value
	 *            検査値
	 * @return true=重複無し false=重複あり
	 */
	private boolean searchRandomRuneEffect(int value, int[] tmp) {
		boolean result = true;
		for (int i = 0; i < tmp.length; i++) {
			if (value == tmp[i]) {
				result = false;
				break;
			}
		}
		return result;
	}

	/**
	 * ハーディン応答フラグを設定
	 *
	 * @param pc
	 * @param actionCode
	 */
	public void setActionHardin(L1PcInstance pc, int actionCode) {
		if(pc.getId() == _leader.getId()){
			if (actionCode == 66) {// Alt+4
				actionCode = 2;// 否定
			} else {
				if (actionCode == 69) {// Alt+2
					actionCode = 1;// 肯定
				} else {
					return;// それ以外は無視
				}
			}
			_actionHardin = actionCode;
		}
	}

	/**
	 * ケレニス応答フラグを設定
	 *
	 * @param pc
	 * @param actionCode
	 */
	public void setActionKerenis(L1PcInstance pc,int actionCode) {
		if(pc.getId() == _leader.getId()){
			if (actionCode == 66) {// Alt+4
				actionCode = 2;// 否定
			} else {
				if (actionCode == 69) {// Alt+2
					actionCode = 1;// 肯定
				} else {
					return;// それ以外は無視
				}
			}
			setReceiveAllAction(getReceiveAllAction() | true);

			_actionKerenis = actionCode;
		}

	}

	/**
	 * ハーディン応答フラグを直接設定
	 *
	 * @param actionCode
	 */
	public void setActionHardinDirect(int actionCode) {
		_actionHardin = actionCode;
	}

	/**
	 * ケレニス応答フラグを直接設定
	 *
	 * @param actionCode
	 */
	public void setActionKerenisDirect(int actionCode) {
		_actionKerenis = actionCode;
	}

	/**
	 * インスタンスの状態を設定
	 *
	 * @param active
	 *            true=Active false=Non-Active
	 */
	private void setActive(boolean active) {
		_active = active;
	}

	/**
	 * @param firstSwitch
	 *            　常にTrueが指定
	 */
	private void setAlreadyFirstSwitch(boolean firstSwitch) {
		_alreadyFirstSwitch = firstSwitch;
	}

	/**
	 * @param guradManDeath
	 *            　常にTrueが指定
	 */
	private void setAlreadyGuardManDeath(boolean guradManDeath) {
		_alreadyGuardManDeath = guradManDeath;
	}

	/**********************************************/

	/**
	 * ポータルフラグを設定する
	 *
	 * @param flag
	 *            常にTrueが指定
	 */
	private void setAlreadyPortal(boolean flag) {
		_alreadyPortal = flag;
	}

	/**
	 * @param secondSwitch
	 */
	private void setAlreadySecondSwitch(boolean secondSwitch) {
		_alreadySecondSwitch = secondSwitch;
	}

	/**
	 * 黒の魔法陣を表示し、プレイヤーリストを更新する。 ※プレイヤーリストは踏み替え可能
	 *
	 * @param onPlayerList
	 */
	public void setBlackRune(int[] onPlayerList) {

		setOnPlayerList(onPlayerList);
		/* グラフィックを黒のルーンを表示 */
		if (!isBlackRuneCreated()) {
			final L1Location loc = new L1Location();
			loc.set(32802, 32868, _mapId);
			spawnOne(loc, 91338, 0);
			setSwitch(loc, 59);
			_blackRune = true;
		}
	}

	/**
	 * 黒い魔法陣を踏んだ際に実行される。
	 *
	 * @param trod
	 *            　常にTrueが指定
	 */
	private void setBlackRuneTrodAlready(boolean trod) {
		_BlackRuneTrodAlready = trod;
	}

	/**
	 * 現在のラウンドを更新
	 *
	 * @param curRound
	 *            　ラウンド
	 */
	private void setCurRound(int curRound) {
		_curRound = curRound;
	}

	/**
	 * 削除中フラグの有無を設定する
	 *
	 * @param transactionNow
	 */
	private void setDeleteTransactionNow(boolean transactionNow) {
		_deleteTransactionNow = transactionNow;

	}

	/**
	 * ケレニスが怒らせた場合に呼ばれる
	 *
	 * @param angry
	 *            　常にTrueが指定
	 */
	private void setKerenisAngry(boolean angry) {
		_kerenisAngry = angry;
	}

	/**
	 * フィールドにインスタンスマップIDを格納
	 *
	 * @param mapId
	 *            　インスタンスマップID
	 */
	private void setMapId(short mapId) {
		_mapId = mapId;
	}

	/**
	 * タイルに載っているプレイヤーリストを格納する
	 *
	 * @param onPlayerList
	 */
	private void setOnPlayerList(int[] onPlayerList) {
		_onPlayerList = onPlayerList;
	}

	/**
	 * setRandomRuneEffectのi番目にvalueを格納する
	 *
	 * @param i
	 *            :　0=説明　1=北　2=西　3=東　4=南
	 * @param value
	 *            任意の値
	 */
	private void setRandomRuneEffect(int i, int value) {
		_randomRuneEffect[i] = value;
	}

	/**
	 * 一度でもケレニスの問いかけに応答したかどうかを保存する。
	 *
	 * @param action
	 *            　常にTrueが指定
	 */
	private void setReceiveAllAction(boolean action) {
		_receiveAllAction = action;
	}

	/**
	 * 各ラウンドの進捗状態を保存する。
	 *
	 * @param round
	 *            　任意のラウンド
	 * @param value
	 *            　0=遅い　1=早い
	 */
	private void setRoundStatus(int round, int value) {
		_roundStatus[round] = value;
	}

	/**
	 * ワールドに新しいトラップを作成して追加する ※L1ReloadTrapが実行されると破棄される点に注意
	 *
	 * @param loc
	 *            　任意の位置
	 * @param id
	 *            　トラップID
	 */
	private void setSwitch(L1Location loc, int id) {
		final int trapId = id;
		final L1Trap trapTemp = TrapTable.getInstance().getTemplate(trapId);
		final Point rndPt = new Point();
		rndPt.setX(0);
		rndPt.setY(0);
		final int span = 0;
		final L1TrapInstance trap = new L1TrapInstance(IdFactory.getInstance().nextId(),
				trapTemp, loc, rndPt, span);
		L1World.getInstance().addVisibleObject(trap);

		L1WorldTraps.getInstance().addTrap(trap);

		this.addTrapList(trap);
	}

	/**
	 * SwitchStatus配列のX番目の要素へスイッチの状態を書き込む
	 *
	 * @param x
	 *            　添え字
	 * @param status
	 *            　1=早い 2=遅い 0=遅すぎるもしくは踏まれていない
	 */
	private void setSwitchStatus(int x, int status) {
		_switchStatus[x] = status;
	}

	/**
	 * 指定されたロケーションに任意のNpcを一匹生成する。
	 *
	 * @param loc
	 *            出現位置
	 * @param npcid
	 *            任意のNpcId
	 * @param heading
	 *            向き
	 * @return L1NpcInstance 戻り値 : 成功=生成したインスタンス 失敗=null
	 */
	private L1NpcInstance spawnOne(L1Location loc, int npcid, int heading) {
		final L1NpcInstance mob = new L1NpcInstance(NpcTable.getInstance().getTemplate(npcid));
		if (mob == null) {
			_log.warning("mob == null");
			return mob;
		}

		mob.setId(IdFactory.getInstance().nextId());
		mob.setHeading(heading);
		mob.setX(loc.getX());
		mob.setHomeX(loc.getX());
		mob.setY(loc.getY());
		mob.setHomeY(loc.getY());
		mob.setMap((short) loc.getMapId());

		L1World.getInstance().storeObject(mob);
		L1World.getInstance().addVisibleObject(mob);

		final S_NpcPack S_NpcPack = new S_NpcPack(mob);
		for (final L1PcInstance pc : L1World.getInstance().getRecognizePlayer(mob)) {
			pc.addKnownObject(mob);
			mob.addKnownObject(pc);
			pc.sendPackets(S_NpcPack);
		}
		// モンスターのＡＩを開始
		mob.onNpcAI();
		mob.updateLight();
		mob.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
		return mob;
	}

	/**
	 * 指定されたロケーションに任意のモンスターを一匹生成する。
	 *
	 * @param loc
	 *            出現位置
	 * @param npcid
	 *            任意のNpcId
	 * @return L1MonsterInstance 戻り値 : 成功=生成したインスタンス 失敗=null
	 */
	private L1MonsterInstance spawnOneMob(L1Location loc, int npcid) {
		final L1MonsterInstance mob = new L1MonsterInstance(NpcTable.getInstance().getTemplate(npcid));
		if (mob == null) {
			_log.warning("mob == null");
			return mob;
		}

		mob.setId(IdFactory.getInstance().nextId());
		mob.setHeading(5);
		mob.setX(loc.getX());
		mob.setHomeX(loc.getX());
		mob.setY(loc.getY());
		mob.setHomeY(loc.getY());
		mob.setMap((short) loc.getMapId());
		mob.setStoreDroped(false);
		mob.setUbSealCount(0);

		L1World.getInstance().storeObject(mob);
		L1World.getInstance().addVisibleObject(mob);

		final S_NpcPack S_NpcPack = new S_NpcPack(mob);
		for (final L1PcInstance pc : L1World.getInstance().getRecognizePlayer(mob)) {
			pc.addKnownObject(mob);
			mob.addKnownObject(pc);
			pc.sendPackets(S_NpcPack);
		}
		// モンスターのＡＩを開始
		mob.onNpcAI();
		mob.updateLight();
		// mob.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
		return mob;
	}

	/**
	 * 指定されたロケーションへPCをテレポートさせる
	 *
	 * @param pc
	 * @param loc
	 * @param heading
	 */
	public void tereportEntrance(L1PcInstance pc, L1Location loc, int heading) {
		L1Teleport.teleport(pc, loc, heading, true);
	}

	/**
	 * クエストを開始する。
	 */
	public void start() {// 初期化する
		_preliminary = new Preliminary();
		_hqm = new HardinQuestMonitor();
		GeneralThreadPool.getInstance().execute(_preliminary);
		_hqm.begin();
	}

	// XXX for ClientThread.java
	public void checkLeaveGame(L1PcInstance pc) {
		// 帰還処理
		if (pc.getMapId() == getMapId()) {
			final RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
			final int rndx = random.nextInt(4);
			final int rndy = random.nextInt(4);
			final int locx = 32587 + rndx;
			final int locy = 32941 + rndy;
			final short mapid = 0;
			L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
		}
	}
}

class BossEndTalks extends TimerTask {// ボスのうち片方が死んだ場合に出力されるメッセージ
	int _type;
	L1NpcInstance _Hardin;
	L1NpcInstance _Kerenis;
	BossEndTalks(int type,L1NpcInstance Hardin,L1NpcInstance Kerenis){//
		_type=type;
		_Hardin=Hardin;
		_Kerenis=Kerenis;
	}
	public void begin() {
		final Timer timer = new Timer();
		timer.schedule(this,0);
	}

	@Override
	public void run() {
		try {
			if(_type==1){//バフォ生存
				// まさか、本当にケレニスを倒せるとは！ 7833
				// 蝶のように舞い、蜂のように刺すのじゃ！そうすればダメージも減る！ 7834
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7833", 0));
				Thread.sleep(2000);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7834", 0));
			}else if(_type==2){
				// ご苦労だった。礼を言う。 7827
				//
				// これは…甘い血の香り… 7828
				// ケレニス！どうしたんじゃ！？ 7829
				// この音は…運命の車輪の…回る音… 7830
				// ケレニスを倒すんじゃ！ 7831
				// 後は任せろと言ったくせに… 7832
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7827", 0));
				Thread.sleep(1000);
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7828", 0));
				Thread.sleep(1800);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7829", 0));
				Thread.sleep(1800);
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7830", 0));
				Thread.sleep(1000);
				_Hardin.broadcastPacket(new S_NpcChatPacket(_Hardin, "$7831", 0));
				Thread.sleep(1500);
				_Kerenis.broadcastPacket(new S_NpcChatPacket(_Kerenis, "$7832", 0));
			}
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}

/********************************** 出力タイミング不明メッセージ *********************************************/

// 7588 消えちゃえ！みんな消えちゃえ！
// 7589 なんなの、この動き…
// 7590 もう！みんなしっかりして！
// 7591 この甘い香りは…血のにおい？
// 7592 誰かわたしのとなりにいてくれる人はいないかな？
// 7593 あんたたち！許さないわよ?！
// 7594 ねぇ、大層なお勉強をしてるわりにはそんなもん？
// 7595 お父さんかぁ…大きいといいな。
//
//
//
//
// ケレニスは一体いつになったら来るんじゃ！ 7812
// オリム、何があった！？ 7813
// ケレニスは無事なのか！ 7814
//
// くぅ…ケレニスの力なしではダメなのか… 7825
//
// お前たち、猶予はそう多くない。がんばってくれ！ 7826
//
// 一体何が起こっているんじゃ！？ 7835
// 洞窟が崩壊しているじゃと！？あと少しもってくれ！ 7836
//
// ripper
//
//
// 生きている限り、やり直しはきく！ここは逃げるのじゃ！ 7840
//
//
//
//
// 7661 ヤヒ！お前の…陰謀だったのか…
// 7662 ヤヒ
//
// 7667 もう始めたのか。
// 7668 ついに来たか、ケレニス！
// 7669 ケレニス、何してんだ！やめろ！
// 7670 ううう…体が…いうことを聞かない…
// 7671 ケレニス！
// 7672 ケレニスを隔離する！
//
// 7673 バルログ
//
//
// 7678 なんだこりゃ！
// 7679 危ない！逃げろ！
// 7680 今のケレニスはコントロールできない。逃げろ！
//
// 7682 早く脱出するんじゃ！このままでは無駄死にじゃぞ！
// 7683 今回は失敗じゃ！お前たち早く脱出するんじゃ！この地に封印を施す！
// 7684 急いで！魔法陣が消えてしまうわ！
// 7685 みんな脱出しろ！ここを封印する！
// 7686 オリムの奴め！この大切な時に一体なにをやってるんんじゃ！
// 7687 お前たち早く逃げるのじゃ！この領域を封印する！
// 7688 まだなのか…ケレニス、もうちょっとこらえてくれ！
// 7689 ハーディン…あのヤギみたいなやつ、強すぎる！
// 7690 もう始めたようだな。
// 7691 ケレニスなしでは不可能だ！さっさと脱出しろ！
// 7692 ケレニスはまだなのか！オリム、何が起きたんだ！
//
//
//
// 7696 ケレニスはまだのようだな。
// 7697 ククク…ハーディン、また会おう！
// 7698 バフォメット、もう消えろ！
// 7699 ケレニス、ついに来たか。あのモンスターの視線を引き付けろ！
// 7700 なんでこんなに早く始めるんだ？
// 7701 誰だ、俺をよんだのは。ククク！
// 7702 なんだこりゃ！肉屋かよっ！
// 7703 ついに来たか、ケレニス！
// 7704 何！ケレニス、どうしたんだ！オリム、一体何があったんだ！
// 7705 バフォメット、もう主人のところへ帰れ！
// 7706 うるせえ、この骨野郎！ハハハ！ハーディン、また会おう！ハハハ！
//
// 7707 これは驚いた。まさかケレニスを倒すとはな…
// 7708 脱出じゃ！
//
// 7710 計画は失敗だ！逃げろ！
// 7711 ケレニス！ここを封印する時間を稼いでくれ！
//
//
// 7666 こ、これは！さっさと逃げろ！封印するぞ！
// 謎の重複7693 さあ、早く！脱出するんじゃ！熱気で肺が焼けてしまう前に！
// 7674 これはお前らがやったのか。
// 7675 うん、これもすべてあの方のご意志だからな…
// 7676 気になることはいっぱいあるだろうが、あのウィザードはトイレが近いようだな！ククク。
