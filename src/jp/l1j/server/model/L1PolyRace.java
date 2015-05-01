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
package jp.l1j.server.model;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.DoorTable;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1DoorInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.skill.L1SkillId;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_EffectLocation;
import jp.l1j.server.packets.server.S_MessageYN;
import jp.l1j.server.packets.server.S_Race;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillBrave;
import jp.l1j.server.packets.server.S_SkillHaste;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.types.Base;
import jp.l1j.server.utils.collections.Lists;

public class L1PolyRace {

	private final int[] polyList = { 936, 3134, 1642, 931, 96, 4038, 929, 1540,
			3783, 934, 3918, 3199, 3184, 3132, 3107, 3188, 3211, 3143, 3156,
			3154, 3178, 4133, 5089, 945, 4171, 2541, 2001, 1649, 29, };

	private final int[] startpolyList = { 938, 2145, 3182, 5065 };
	// 　スタート時　ビーグル・ハスキー・ハイセントバーナード・紀州犬の小犬の４種類

	private final int[] fragment = { 50515, 50516, 50518, 50519 };
	// 　デスマッチ・お化け屋敷・ペットマッチ・アルテメットバトル勝利の欠片の４種類

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	private static L1PolyRace instance;

	public static L1PolyRace getInstance() {
		if (instance == null) {
			instance = new L1PolyRace();
		}
		return instance;
	}

	public static final int STATUS_NONE = 0;
	public static final int STATUS_READY = 1;
	public static final int STATUS_PLAYING = 2;
	public static final int STATUS_END = 3;

	private static final int maxLap = Config.PET_RACE_MAX_LAP; // 周回数
	private static final int maxPlayer = 10; // 参加人数 1~20
	private static final int minPlayer = Config.PET_RACE_MIN_PLAYER; // 最小参加人数

	private static int readyTime = 5 * 1000; // 競技開始待機時間　50秒
	private static int limitTime = 300 * 1000; // 競技時間　300秒

	// Race参加者リスト
	private final List<L1PcInstance> playerList = Lists.newArrayList();

	public void addPlayerList(L1PcInstance pc) {
		if (!playerList.contains(pc)) {
			playerList.add(pc);
		}
	}

	public void removePlayerList(L1PcInstance pc) {
		if (playerList.contains(pc)) {
			playerList.remove(pc);
		}
	}

	// winer送信者リスト
	private final List<L1PcInstance> goalList = Lists.newArrayList();

	public void enterGame(L1PcInstance pc) {
		if (pc.getLevel() < 30) {
			pc.sendPackets(new S_ServerMessage(1273, "30", "99"));
			// 選んだマッチはLv%0〜Lv%1が参加できます。
			return;
		}
		if (!pc.getInventory().consumeItem(40308, 1000)) {
			pc.sendPackets(new S_ServerMessage(189)); // \f1%0を買いません。
			return;
		}
		if (playerList.size() + orderList.size() >= maxPlayer) {
			pc.sendPackets(new S_SystemMessage(I18N_POLYRACE_REACHED_TO_MAX_PLAYERS));
			return;
		}
		if (getGameStatus() == STATUS_PLAYING || getGameStatus() == STATUS_END) {
			pc.sendPackets(new S_ServerMessage(1182)); // もうゲームは始まってるよ。
			return;
		}
		if (getGameStatus() == STATUS_NONE) {
			addOrderList(pc);
			return;
		}

		addPlayerList(pc);
		goalList.add(pc);
		L1Teleport.teleport(pc, 32768, 32849, (short) 5143, 6, true);
		pc.getInventory().takeoffEquip(945); // 牛のpolyIdで装備を全部外す。
		removeSkillEffect(pc);
	}

	// Race参加予約者リスト
	private final List<L1PcInstance> orderList = Lists.newArrayList();

	public void removeOrderList(L1PcInstance pc) {
		orderList.remove(pc);
	}

	// 予約受付...会場入場
	public void addOrderList(L1PcInstance pc) {
		if (orderList.contains(pc)) {
			pc.sendPackets(new S_ServerMessage(1254)); // 既に出場予約が受け付けられています。
			return;
		}
		orderList.add(pc);
		pc.setInOrderList(true);
		pc.sendPackets(new S_ServerMessage(1253, String.valueOf(orderList
				.size()))); // エントリーナンバー%0での出場が予約されました。
		if (orderList.size() >= minPlayer) {
			for (L1PcInstance player : orderList) {
				player.sendPackets(new S_MessageYN(1256, null));
				// レースに出場しますか？（Y/N）
			}
			setGameStatus(STATUS_READY);
			startReadyTimer();
		}
	}

	private boolean checkPlayersOK() {
		if (getGameStatus() == STATUS_READY) {
			return playerList.size() >= minPlayer;
		}
		return false;
	}

	private void setGameStart() {
		setGameStatus(STATUS_PLAYING);
		int startpoly = _random.nextInt(startpolyList.length);
		int basepoly = startpolyList[startpoly];

		for (L1PcInstance pc : playerList) {
			L1PolyMorph.doPoly(pc, basepoly, 3600, L1PolyMorph.MORPH_BY_NPC);
			pc.setBasePoly(basepoly); // ペットレース場基本変身poly id
			pc.sendPackets(new S_ServerMessage(1257)); // まもなくレースがスタートします。
			pc.sendPackets(new S_Race(S_Race.GameStart));// 5.4.3.2.1.GO!
			pc.sendPackets(new S_Race(maxLap, pc.getLap()));// 最大周回数
			pc.sendPackets(new S_Race(playerList, pc));// 参加者リスト
		}
		startCompareTimer();
		startClockTimer();
	}

	// 順位送信処理　start
	private void setGameWinner(L1PcInstance pc) {
		if (get1stWinner() == null) {
			set1stWinner(pc);//
			startWaitTimer(); // 10秒間遅延しendタスク実行　10秒以内 2nd 3rd　GOALすれば有効
		} else if (get2ndWinner() == null) {
			set2ndWinner(pc);

		} else if (get3rdWinner() == null) {
			set3rdWinner(pc);
		}
		setWinner(pc);
		L1PcInstance winner = getWinner();
		for (L1PcInstance pc1 : goalList) {
			pc1.sendPackets(new S_Race(winner.getName(), _time * 2));
			// YOU WIN・timeをレース参加者に送信
		}
		goalList.remove(pc);
		// YOU WIN受信回数で画面に表示される順位が変わるため、レース終了者には、YOU WINを送信しない処置
	}

	// 順位送信処理 end

	private static final int END_STATUS_WINNER = 1;
	private static final int END_STATUS_NOWINNER = 2;
	private static final int END_STATUS_NOPLAYER = 3;

	// 競技終了後処理　1:勝利者がいる場合 2:時間切れの場合 3:参加人数不足
	private void setGameEnd(int type) {
		setGameStatus(STATUS_END);
		switch (type) {
		case END_STATUS_WINNER:
			stopCompareTimer();
			stopGameTimeLimitTimer();
			sendEndMessage();
			break;
		case END_STATUS_NOWINNER:
			stopCompareTimer();
			sendEndMessage();
			for (L1PcInstance pc : playerList) {
				// pc.setBasePoly(0); // ペットレース場基本変身poly id　リセット
				continue;
			}
			break;
		case END_STATUS_NOPLAYER:
			for (L1PcInstance pc : playerList) {
				// pc.sendPackets(new S_ServerMessage(1264)); 本鯖用メッセージ
				// 最小参加人数の2名に満たなかったため、レースを強制終了します。 1000アデナをお返ししました。
				pc.sendPackets(new S_SystemMessage(String.format(I18N_POLYRACE_LESS_THAN_MIN_PLAYERS, Config.PET_RACE_MIN_PLAYER)));
				pc.getInventory().storeItem(40308, 1000);
			}
			break;
		}
		startEndTimer();// 5秒後村に移動
	}

	// Race賞品処理　start

	private void giftWinner() {
		L1PcInstance winner1 = get1stWinner();
		L1PcInstance winner2 = get2ndWinner();
		L1PcInstance winner3 = get3rdWinner();
		L1ItemInstance item1 = ItemTable.getInstance().createItem(49278);
		L1ItemInstance item2 = ItemTable.getInstance().createItem(49279);
		L1ItemInstance item3 = ItemTable.getInstance().createItem(49280);

		if (playerList.size() <= 4) {
			if (winner1 == null || item1 == null) {
				return;
			}
			if (winner1.getInventory().checkAddItem(item1, 1) == L1Inventory.OK) {
				item1.setCount(1);
				winner1.getInventory().storeItem(item1);
				winner1
						.sendPackets(new S_ServerMessage(403, item1
								.getLogName()));
				// %0を手に入れました。
			}
		} else if (playerList.size() <= 5 && playerList.size() >= 7) {
			if (winner1 == null || item1 == null) {
				return;
			}
			if (winner1.getInventory().checkAddItem(item1, 1) == L1Inventory.OK) {
				item1.setCount(1);
				winner1.getInventory().storeItem(item1);
				winner1
						.sendPackets(new S_ServerMessage(403, item1
								.getLogName()));
				// %0を手に入れました。
			} else if (winner2.getInventory().checkAddItem(item2, 1) == L1Inventory.OK) {
				item2.setCount(1);
				winner2.getInventory().storeItem(item2);
				winner2
						.sendPackets(new S_ServerMessage(403, item2
								.getLogName())); // %0を手に入れました。
			}
		} else if (playerList.size() >= 8) {
			if (winner1 == null || item1 == null) {
				return;
			}
			if (winner1.getInventory().checkAddItem(item1, 1) == L1Inventory.OK) {
				item1.setCount(1);
				winner1.getInventory().storeItem(item1);
				winner1
						.sendPackets(new S_ServerMessage(403, item1
								.getLogName()));
				// %0を手に入れました。
			} else if (winner2.getInventory().checkAddItem(item2, 1) == L1Inventory.OK) {
				item2.setCount(1);
				winner2.getInventory().storeItem(item2);
				winner2
						.sendPackets(new S_ServerMessage(403, item2
								.getLogName())); // %0を手に入れました。
			} else if (winner3.getInventory().checkAddItem(item3, 1) == L1Inventory.OK) {
				item3.setCount(1);
				winner3.getInventory().storeItem(item3);
				winner3
						.sendPackets(new S_ServerMessage(403, item3
								.getLogName())); // %0を手に入れました。
			}
		}
		// 勝利の欠片　処理
		int rnd1 = _random.nextInt(100) + 1; // 欠片入手可否乱数
		int rnd2 = _random.nextInt(100) + 1; // 欠片入手人数乱数
		int rnd3 = _random.nextInt(100) + 1; // 他ミニゲーム欠片入手可否数乱数
		L1ItemInstance item4 = ItemTable.getInstance().createItem(50517);// ペットレース勝利の欠片
		if (rnd1 >= 1 && rnd1 <= 100) { // 40%の確率で入手
			if (rnd2 >= 1 && rnd2 <= 100) { // 50%の確率で一位のみ入手
				winner2 = null;
				winner3 = null;
				fragment(rnd3, winner1, winner2, winner3, item4);
			} else if (rnd2 >= 51 && rnd2 <= 80) {// 30%の確率で二位まで入手
				winner3 = null;
				fragment(rnd3, winner1, winner2, winner3, item4);
			} else if (rnd2 >= 81 && rnd2 <= 100) {// 20%の確率で三位まで入手
				fragment(rnd3, winner1, winner2, winner3, item4);
			}
		}
	}

	// 　勝利の欠片入手判定
	/**
	 * @param rnd3
	 * @param winner1
	 * @param winner2
	 * @param winner3
	 * @param item4
	 */
	private void fragment(int rnd3, L1PcInstance winner1, L1PcInstance winner2,
			L1PcInstance winner3, L1ItemInstance item4) {
		if (winner1.getInventory().checkAddItem(item4, 1) == L1Inventory.OK) {
			item4.setCount(1);
			winner1.getInventory().storeItem(item4);
			winner1.sendPackets(new S_ServerMessage(403, item4.getLogName()));
			// %0 を手に入れました。
		}
		if (rnd3 >= 1 && rnd3 <= 40) { // 40%の確率でその他ミニゲーム勝利の欠片入手
			int i = _random.nextInt(fragment.length);
			L1ItemInstance item5 = ItemTable.getInstance().createItem(
					fragment[i]);// その他ミニゲーム勝利の欠片１つ
			if (winner1.getInventory().checkAddItem(item5, 1) == L1Inventory.OK) {
				item4.setCount(1);
				winner1.getInventory().storeItem(item5);
				winner1
						.sendPackets(new S_ServerMessage(403, item5
								.getLogName())); // %0を手に入れました。
			}
		}
		if (winner2 == null) {
			return;
		} else {
			if (winner2.getInventory().checkAddItem(item4, 1) == L1Inventory.OK) {
				item4.setCount(1);
				winner2.getInventory().storeItem(item4);
				winner2
						.sendPackets(new S_ServerMessage(403, item4
								.getLogName())); // %0を手に入れました。
			}
			if (rnd3 >= 1 && rnd3 <= 40) { // 40%の確率でその他ミニゲーム勝利の欠片入手
				int i = _random.nextInt(fragment.length);
				L1ItemInstance item5 = ItemTable.getInstance().createItem(
						fragment[i]);// その他ミニゲーム勝利の欠片１つ
				if (winner2.getInventory().checkAddItem(item5, 1) == L1Inventory.OK) {
					item4.setCount(1);
					winner2.getInventory().storeItem(item5);
					winner2.sendPackets(new S_ServerMessage(403, item5
							.getLogName())); // %0を手に入れました。
				}
			}
		}
		if (winner3 == null) {
			return;
		} else {
			if (winner3.getInventory().checkAddItem(item4, 1) == L1Inventory.OK) {
				item4.setCount(1);
				winner3.getInventory().storeItem(item4);
				winner3
						.sendPackets(new S_ServerMessage(403, item4
								.getLogName())); // %0を手に入れました。
			}
			if (rnd3 >= 1 && rnd3 <= 40) { // 40%の確率でその他ミニゲーム勝利の欠片入手
				int i = _random.nextInt(fragment.length);
				L1ItemInstance item5 = ItemTable.getInstance().createItem(
						fragment[i]);// その他ミニゲーム勝利の欠片１つ
				if (winner3.getInventory().checkAddItem(item5, 1) == L1Inventory.OK) {
					item4.setCount(1);
					winner3.getInventory().storeItem(item5);
					winner3.sendPackets(new S_ServerMessage(403, item5
							.getLogName())); // %0を手に入れました。
				}
			}
		}
	}

	// Race賞品処理　end

	private void sendEndMessage() {
		L1PcInstance winner = get1stWinner();
		for (L1PcInstance pc : playerList) {
			if (winner != null) {
				pc.sendPackets(new S_ServerMessage(1259)); // まもなく村へ移動されます。
				// pc.setBasePoly(0); // ペットレース場基本変身poly id　リセット
				continue;
			}
			pc.sendPackets(new S_Race(S_Race.GameOver));
		}
	}

	// 初期化+ 会場準備　処理 start
	private void setGameInit() {
		for (L1PcInstance pc : playerList) {
			pc.sendPackets(new S_Race(S_Race.GameEnd));
			pc.setLap(1);
			pc.setLapCheck(0);
			L1Teleport.teleport(pc, 32616, 32782, (short) 4, 5, true);
			pc.setBasePoly(0); // ペットレース場基本変身poly id　リセット
			removeSkillEffect(pc);
		}
		setDoorClose(true);
		setGameStatus(STATUS_NONE);
		setWinner(null);
		set1stWinner(null);
		set2ndWinner(null);
		set3rdWinner(null);
		playerList.clear();
		goalList.clear();
		clearTime();
	}

	// XXX for ClientThread.java
	public void checkLeaveGame(L1PcInstance pc) {
		if (pc.getMapId() == 5143) {
			removePlayerList(pc);
			L1PolyMorph.undoPoly(pc);
		}
		if (pc.isInOrderList()) {
			removeOrderList(pc);
		}
	}

	// XXX for C_Attr.java
	public void requsetAttr(L1PcInstance pc, int c) {
		if (c == 0) { // NO
			removeOrderList(pc);
			pc.setInOrderList(false);
			pc.sendPackets(new S_ServerMessage(1255)); // 出場予約がキャンセルされました。
		} else { // YES
			addPlayerList(pc);
			goalList.add(pc);
			L1Teleport.teleport(pc, 32768, 32849, (short) 5143, 6, true);
			pc.getInventory().takeoffEquip(945); // 牛のpolyIdで装備を全部外す。
			removeSkillEffect(pc);
			removeOrderList(pc);
			pc.setInOrderList(false);
		}
	}

	// 初期化+ 会場準備　処理 end

	private final List<L1PcInstance> position = Lists.newArrayList();

	// 周回順位判定処理　start
	private void comparePosition() {
		List<L1PcInstance> temp = Lists.newArrayList();
		int size = playerList.size();
		int count = 0;
		while (size > count) {
			int maxLapScore = 0;
			for (L1PcInstance pc : playerList) {
				if (temp.contains(pc)) {
					continue;
				}
				if (pc.getLapScore() >= maxLapScore) {
					maxLapScore = pc.getLapScore();
				}
			}
			for (L1PcInstance player : playerList) {
				if (player.getLapScore() == maxLapScore) {
					temp.add(player);
				}
			}
			count++;
		}
		if (!position.equals(temp)) {
			position.clear();
			position.addAll(temp);
			for (L1PcInstance pc : playerList) {
				pc.sendPackets(new S_Race(position, pc));
			}
		}
	}

	// 周回順位判定処理　end

	private void setDoorClose(boolean isClose) {
		L1DoorInstance[] list = DoorTable.getInstance().getDoorList();
		for (L1DoorInstance door : list) {
			if (door.getMapId() == 5143) {
				if (isClose) {
					door.close();
				} else {
					door.open();
				}
			}
		}
	}

	public void removeSkillEffect(L1PcInstance pc) {
		L1SkillUse skill = new L1SkillUse();
		skill.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(),
				pc.getY(), null, 0, Base.SKILL_TYPE[1]);
	}

	// 床パネル設定
	private void onEffectTrap(L1PcInstance pc) {
		int x = pc.getX();
		int y = pc.getY();
		if (x == 32748 && (y == 32845 || y == 32846)) {
			speedUp(pc, 32748, 32845);
		} else if (x == 32748 && (y == 32847 || y == 32848)) {
			speedUp(pc, 32748, 32847);
		} else if (x == 32748 && (y == 32849 || y == 32850)) {
			speedUp(pc, 32748, 32849);
		} else if (x == 32748 && y == 32851) {
			speedUp(pc, 32748, 32851);
		} else if (x == 32762 && (y == 32811 || y == 32812)) {
			speedUp(pc, 32762, 32811);
		} else if ((x == 32799 || x == 32800) && y == 32830) {
			speedUp(pc, 32800, 32830);
		} else if ((x == 32736 || x == 32737) && y == 32840) {
			randomPoly(pc, 32737, 32840);
			speedUp2(pc, 32737, 32840);
		} else if ((x == 32738 || x == 32739) && y == 32840) {
			randomPoly(pc, 32739, 32840);
			speedUp2(pc, 32739, 32840);
		} else if ((x == 32740 || x == 32741) && y == 32840) {
			randomPoly(pc, 32741, 32840);
			speedUp2(pc, 32741, 32840);
		} else if (x == 32749 && (y == 32818 || y == 32817)) {
			randomPoly(pc, 32749, 32817);
		} else if (x == 32749 && (y == 32816 || y == 32815)) {
			randomPoly(pc, 32749, 32815);
		} else if (x == 32749 && (y == 32814 || y == 32813)) {
			randomPoly(pc, 32749, 32813);
		} else if (x == 32749 && (y == 32812 || y == 32811)) {
			randomPoly(pc, 32749, 32811);
		} else if (x == 32790 && (y == 32812 || y == 32813)) {
			randomPoly(pc, 32790, 32812);
			speedUp2(pc, 32790, 32812);
		} else if ((x == 32793 || x == 32794) && y == 32831) {
			randomPoly(pc, 32794, 32831);
			speedUp2(pc, 32794, 32831);

		}
	}

	private static int POLY_EFFECT = 15566;
	private static int SPEED_EFFECT = 18333;
	private static int SPEED_EFFECT2 = 18333;

	// 変身処理（変身パネル）　start
	private void randomPoly(L1PcInstance pc, int x, int y) {
		if (pc.hasSkillEffect(POLY_EFFECT)) {
			return;
		}
		pc.setSkillEffect(POLY_EFFECT, 4 * 1000);

		int i = _random.nextInt(polyList.length);
		L1PolyMorph.doPoly(pc, polyList[i], 30, L1PolyMorph.MORPH_BY_NPC);

		for (L1PcInstance player : playerList) {
			player.sendPackets(new S_EffectLocation(x, y, 6675));
		}
	}

	// 変身処理（変身パネル）　end

	// 加速処理（加速パネル　ブレイブ速度２倍） start
	private void speedUp(L1PcInstance pc, int x, int y) {
		pc.setSkillEffect(SPEED_EFFECT, 15 * 1000);
		int time = 15;
		int objectId = pc.getId();
		pc.sendPackets(new S_SkillBrave(objectId, 5, time));
		pc.setSkillEffect(L1SkillId.STATUS_BRAVE, time * 1000);
		pc.setSkillEffect(L1SkillId.STATUS_BRAVE2, time * 1000);
		pc.setBraveSpeed(1);
		/**
		 * XXX pc.broadcastPacket(new S_SkillBrave(objectId, 5, time))!!!
		 */
		pc.broadcastPacket(new S_SkillBrave(objectId, 5, time));
		for (L1PcInstance player : playerList) {
			player.sendPackets(new S_EffectLocation(x, y, 6674));
		}
	}

	// 加速処理（加速パネル　ブレイブ速度２倍） end

	// 加速処理（変身パネル用　ヘイスト速度） start
	private void speedUp2(L1PcInstance pc, int x, int y) {
		pc.setSkillEffect(SPEED_EFFECT2, 15 * 1000);
		int time = 30;
		int objectId = pc.getId();
		pc.sendPackets(new S_SkillHaste(objectId, 1, time));
		pc.setSkillEffect(L1SkillId.STATUS_HASTE, time * 1000);
		pc.setMoveSpeed(1);
	}

	// 加速処理（変身パネル用　ヘイスト速度） end

	// 周回数判定処理　start
	public void checkLapFinish(L1PcInstance pc) {
		if (pc.getMapId() != 5143 || getGameStatus() != STATUS_PLAYING) {
			return;
		}

		onEffectTrap(pc);
		int x = pc.getX();
		int y = pc.getY();
		int check = pc.getLapCheck();

		if (x == 32760 && y >= 32845 && check == 0) {
			pc.setLapCheck(check + 1);
		} else if (x == 32754 && y >= 32845 && check == 1) {
			pc.setLapCheck(check + 1);
		} else if (x == 32748 && y >= 32845 && check == 2) {
			pc.setLapCheck(check + 1);
		} else if (x <= 32743 && y == 32844 && check == 3) {
			pc.setLapCheck(check + 1);
		} else if (x <= 32742 && y == 32840 && check == 4) {
			pc.setLapCheck(check + 1);
		} else if (x <= 32742 && y == 32835 && check == 5) {
			pc.setLapCheck(check + 1);
		} else if (x <= 32742 && y == 32830 && check == 6) {
			pc.setLapCheck(check + 1);
		} else if (x <= 32742 && y == 32826 && check == 7) {
			pc.setLapCheck(check + 1);
		} else if (x <= 32742 && y == 32822 && check == 8) {
			pc.setLapCheck(check + 1);
		} else if (x == 32749 && y <= 32818 && check == 9) {
			pc.setLapCheck(check + 1);
		} else if (x == 32755 && y <= 32818 && check == 10) {
			pc.setLapCheck(check + 1);
		} else if (x == 32760 && y <= 32818 && check == 11) {
			pc.setLapCheck(check + 1);
		} else if (x == 32765 && y <= 32818 && check == 12) {
			pc.setLapCheck(check + 1);
		} else if (x == 32770 && y <= 32818 && check == 13) {
			pc.setLapCheck(check + 1);
		} else if (x == 32775 && y <= 32818 && check == 14) {
			pc.setLapCheck(check + 1);
		} else if (x == 32780 && y <= 32818 && check == 15) {
			pc.setLapCheck(check + 1);
		} else if (x == 32785 && y <= 32818 && check == 16) {
			pc.setLapCheck(check + 1);
		} else if (x == 32789 && y <= 32818 && check == 17) {
			pc.setLapCheck(check + 1);
		} else if (x >= 32792 && y == 32821 && check == 18) {
			pc.setLapCheck(check + 1);
		} else if (x >= 32793 && y == 32826 && check == 19) {
			pc.setLapCheck(check + 1);
		} else if (x >= 32793 && y == 32831 && check == 20) {
			pc.setLapCheck(check + 1);
		} else if (x >= 32793 && y == 32836 && check == 21) {
			pc.setLapCheck(check + 1);
		} else if (x >= 32793 && y == 32842 && check == 22) {
			pc.setLapCheck(check + 1);
		} else if (x == 32790 && y >= 32845 && check == 23) {
			pc.setLapCheck(check + 1);
		} else if (x == 32785 && y >= 32845 && check == 24) {
			pc.setLapCheck(check + 1);
		} else if (x == 32780 && y >= 32845 && check == 25) {
			pc.setLapCheck(check + 1);
		} else if (x == 32775 && y >= 32845 && check == 26) {
			pc.setLapCheck(check + 1);
		} else if (x == 32770 && y >= 32845 && check == 27) {
			pc.setLapCheck(check + 1);
		} else if (x == 32764 && y >= 32845 && check == 28) {
			if (pc.getLap() == maxLap) {
				setGameWinner(pc);
			}
			pc.setLapCheck(0);
			pc.setLap(pc.getLap() + 1);
			pc.sendPackets(new S_Race(maxLap, pc.getLap()));// lap

		}
	}

	// 周回数判定処理　end

	private int _status = 0;

	public void setGameStatus(int i) {
		_status = i;
	}

	public int getGameStatus() {
		return _status;
	}

	private int _time = 0;

	private void clearTime() {
		_time = 0;
	}

	private void addTime() {
		_time++;
	}

	private L1PcInstance _winner = null;

	public void setWinner(L1PcInstance pc) {
		_winner = pc;
	}

	public L1PcInstance getWinner() {
		return _winner;
	}

	private L1PcInstance _1stwinner = null;

	public void set1stWinner(L1PcInstance pc) {
		_1stwinner = pc;
	}

	public L1PcInstance get1stWinner() {
		return _1stwinner;
	}

	private L1PcInstance _2ndwinner = null;

	public void set2ndWinner(L1PcInstance pc) {
		_2ndwinner = pc;
	}

	public L1PcInstance get2ndWinner() {
		return _2ndwinner;
	}

	private L1PcInstance _3rdwinner = null;

	public void set3rdWinner(L1PcInstance pc) {
		_3rdwinner = pc;
	}

	public L1PcInstance get3rdWinner() {
		return _3rdwinner;
	}

	private void startReadyTimer() {
		new ReadyTimer().begin();
	}

	private void startCheckTimer() {
		new CheckTimer().begin();
	}

	private void startClockTimer() {
		new ClockTimer().begin();
	}

	private GameTimeLimitTimer limitTimer;

	private void startGameTimeLimitTimer() {
		Timer timer = new Timer();
		limitTimer = new GameTimeLimitTimer();
		timer.schedule(limitTimer, limitTime);
	}

	private void stopGameTimeLimitTimer() {
		limitTimer.stopTimer();
	}

	private void startWaitTimer() {
		new WaitTimer().begin();
	}

	private void startEndTimer() {
		new EndTimer().begin();
	}

	private CompareTimer compareTimer;

	private void startCompareTimer() {
		Timer timer = new Timer();
		compareTimer = new CompareTimer();
		timer.schedule(compareTimer, 2000, 2000);
	}

	private void stopCompareTimer() {
		compareTimer.stopTimer();
	}

	// 会場入場-->人数確認 処理　start
	private class ReadyTimer extends TimerTask {
		@Override
		public void run() {
			for (L1PcInstance pc : playerList) {
				// pc.sendPackets(new S_ServerMessage(1258)); 本鯖用メッセージ
				// 参加者が2名以上いないとレースはスタートしません。
				pc
						.sendPackets(new S_SystemMessage("参加者が"
								+ Config.PET_RACE_MIN_PLAYER
								+ "名以上いないとレースはスタートしません 。"));
			}
			startCheckTimer();
			this.cancel();
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, readyTime);
		}
	}

	// 会場入場-->人数確認 処理　end

	// 確認OK --->競技開始処理　start
	private class CheckTimer extends TimerTask {
		@Override
		public void run() {
			if (checkPlayersOK()) {
				setGameStart();
			} else {
				setGameEnd(END_STATUS_NOPLAYER);
			}
			this.cancel();
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 30 * 1000); // 60s
		}
	}

	// 確認OK --->競技開始処理　end

	// 開始前待機5秒-->競技時間開始処理　start
	private class ClockTimer extends TimerTask {
		@Override
		public void run() {
			// 時計
			for (L1PcInstance pc : playerList) {
				pc.sendPackets(new S_Race(S_Race.CountDown));
			}
			setDoorClose(false);
			startGameTimeLimitTimer();
			this.cancel();
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 5000); // 5s
		}
	}

	// 開始前待機5秒-->競技時間開始処理　end

	// 競技開始--->競技終了処理　start
	private class GameTimeLimitTimer extends TimerTask {
		@Override
		public void run() {
			setGameEnd(END_STATUS_NOWINNER);
			this.cancel();
		}

		public void stopTimer() {
			this.cancel();
		}
	}

	// 競技開始--->競技終了処理　end

	// 1位確定後　10秒間　2,3位判定待ち処理　start
	private class WaitTimer extends TimerTask {
		@Override
		public void run() {
			setGameEnd(END_STATUS_WINNER);
			this.cancel();
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 10000); // 10s- 10000
		}
	}

	// 1位確定後　10秒間　2,3位判定待ち処理　end

	private class EndTimer extends TimerTask {
		@Override
		public void run() {
			giftWinner();
			setGameInit();
			this.cancel();
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 5000); // 5s- 5000
		}
	}

	private class CompareTimer extends TimerTask {
		@Override
		public void run() {
			comparePosition();
			addTime();
		}

		public void stopTimer() {
			this.cancel();
		}
	}
}
