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

package jp.l1j.server.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import static jp.l1j.locale.I18N.*;
import jp.l1j.configure.Config;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.DoorTable;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1DoorInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.skill.L1SkillId;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.packets.server.S_DeathMatch;
import jp.l1j.server.packets.server.S_MessageYN;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1DoorGfx;
import jp.l1j.server.types.Base;
import jp.l1j.server.utils.collections.Lists;

/**
 * デスマッチ専用アイテムについては媒介アイテム処理が煩雑になるので導入しない・・・ 待機時間の仕様は組み込んでいない。 //キャンセル5回で入場不可能
 *
 *
 *
 */
public class L1DeathMatch {
	private final int[] fragment = { 50515, 50516, 50518, 50519 };
	private final int ISSUE_ITEM = 50499;// デスマッチ記念品の箱
	// 　デスマッチ・お化け屋敷・ペットマッチ・アルテメットバトル勝利の欠片の４種類

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	private static L1DeathMatch instance;

	public static L1DeathMatch getInstance() {
		if (instance == null) {
			instance = new L1DeathMatch();
		}
		return instance;
	}

	private final L1DoorInstance _doorLeft;

	private L1DeathMatch() {
		if (Config.DEATH_MATCH_MIN_PLAYER < 2) {// JPデフォルト設定
			gameStartMinPlayer = 6;
			orderEntMinPlayer = 10;
		}
		setMapId((short) 5153);
		L1DoorGfx leftGfx = L1DoorGfx.findByGfxId(6692);
		_doorLeft = DoorTable.getInstance().createDoor(0, leftGfx,
				new L1Location(32638, 32884, 5153), 0, 0, false);
		// 右側ゲート
		// L1DoorGfx rightGfx = L1DoorGfx.findByGfxId(6694);
		// _doorRight = DoorTable.getInstance().createDoor(0,
		// rightGfx, new L1Location(32653, 32899, 5153), 0, 0);
	}

	public static final int STATUS_NONE = 0;
	public static final int STATUS_READY = 1;
	public static final int STATUS_PLAYING = 2;
	public static final int STATUS_END = 3;

	private static final int maxPlayer = 20;// 最大競技可能人数
	private static int gameStartMinPlayer = Config.DEATH_MATCH_MIN_PLAYER; // 最小ゲーム開始人数
	private static int orderEntMinPlayer = Config.DEATH_MATCH_MIN_PLAYER; // 入場受付開始人数

	private static int limitTime = 60 * 30 * 1000; // 競技時間最大　30分
	private static int limitPenaltyTime = 60 * 10 * 1000;// 競技開始から10分
	// 競技参加中のプレイヤーリスト
	private final List<L1PcInstance> playerList = Lists.newArrayList();
	// 最終利用時間
	private final HashMap<String, Date> lastPlayTime = new HashMap<String, Date>();

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

	private short mapId;
	long startTime;// = System.currentTimeMillis();

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * 1270 最小参加人数の5名に満たなかったため、決闘が強制終了されました。 1000アデナをお返ししました。 1273
	 * 選んだマッチはLv%0?Lv%1が参加できます。 1521 一日参加回数%0回を超えました。
	 */
	// 参加予約者リスト
	private final List<L1PcInstance> orderList = Lists.newArrayList();
	//
	// 予約キャンセルリスト
	private final HashMap<String, Integer> orderCancelList = new HashMap<String, Integer>();

	//
	public void enterGame(L1PcInstance pc) {
		/*
		if (!(pc.getLevel() > 29 && pc.getLevel() < 52)) {
			pc.sendPackets(new S_ServerMessage(1273, "30", "51"));
			// 選んだマッチはLv%0～Lv%1が参加できます。
			return;
		}
		*/
		if (getOrderCancelList(pc) > 4) {
			if (lastPlayTime.containsKey(pc.getName())) {
				Calendar lasttime = Calendar.getInstance(TimeZone
						.getTimeZone(Config.TIME_ZONE));
				Calendar now = Calendar.getInstance(TimeZone
						.getTimeZone(Config.TIME_ZONE));
				lasttime.setTime(lastPlayTime.get(pc.getName()));
				now.setTime(new Date(System.currentTimeMillis()));
				if (lasttime.get(Calendar.MONTH) < now.get(Calendar.MONTH)
						|| lasttime.get(Calendar.YEAR) < now.get(Calendar.YEAR)
						|| lasttime.get(Calendar.DATE) < now.get(Calendar.DATE)) {
					orderCancelList.remove(pc.getName());
				} else {
					pc.sendPackets(new S_ServerMessage(1387));// 1387
					// 今日はもうデスマッチの利用ができません。
					return;
				}
			}
		}
		if (getGameStatus() == STATUS_END) {
			pc.sendPackets(new S_ServerMessage(1182)); // もうゲームは始まってるよ。
			return;
		}
		if (getGameStatus() == STATUS_PLAYING) {
			if ((System.currentTimeMillis() - getStartTime()) > 60 * 3000) {
				pc.sendPackets(new S_ServerMessage(1182)); // もうゲームは始まってるよ。
				return;
			}
		}
		if (getGameStatus() == STATUS_NONE) {
			addOrderList(pc);
			return;
		}
		pc.sendPackets(new S_MessageYN(1268, null));
		// 1268 デスマッチに出場しますか？（Y/N）

	}

	// XXX for C_Attr.java
	public void requsetAttr(L1PcInstance pc, int c) {
		if (c == 0) { // NO
			if (orderList.contains(pc)) {
				removeOrderList(pc);
				addOrderCancelList(pc);
				if (getOrderCancelList(pc) == 4) {
					pc.sendPackets(new S_ServerMessage(1390));// 1390
					// デスマッチの入場の予約が4回キャンセルされ、最後の1回残っています。
				} else {
					pc.sendPackets(new S_ServerMessage(1267, ""
							+ orderCancelList.get(pc.getName()))); // 1267
					// デスマッチ出場の予約が%0回キャンセルされました。
				}
			}
		} else { // YES
			int duplicate = 0;
			if (orderList.contains(pc)) {
				duplicate = 1;
			}
			if (playerList.size() + orderList.size() - duplicate >= maxPlayer) {
				pc.sendPackets(new S_ServerMessage(1536));// 1536
				// 定員に達したため、入場できません。
				return;
			}
			if (!pc.getInventory().consumeItem(40308, 1000)) {
				pc.sendPackets(new S_ServerMessage(189)); // 189 \f1アデナが不足しています。
				return;
			}

			if (getStartTime() == 0) {
				setStartTime(System.currentTimeMillis());
				new CheckTimer().begin();
			}

			// パーティに加入している場合は解散もしくは脱退
			if (pc.isInParty()) {
				pc.getParty().leaveMember(pc);
			}

			if (getGameStatus() == STATUS_PLAYING) {
				L1Location loc = new L1Location(32625 + _random.nextInt(28),
						32885 + _random.nextInt(28), getMapId());
				while (!loc.getMap().isPassable(loc.getX(), loc.getY())) {
					loc.set(32625 + _random.nextInt(28), 32885 + _random
							.nextInt(28), getMapId());
				}
				L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) loc
						.getMapId(), 5, true);
			} else {
				if (_random.nextInt(2) > 0) {
					L1Teleport.teleport(pc, 32637 + _random.nextInt(3),
							32877 + _random.nextInt(6), (short) 5153, 6, true);
				} else {
					L1Teleport.teleport(pc, 32655 + _random.nextInt(6),
							32897 + _random.nextInt(4), (short) 5153, 6, true);
				}
			}
			removeSkillEffect(pc);
			addPlayerList(pc);
			removeOrderList(pc);

			// 未実装　待ち時間仕様
			Calendar now = Calendar.getInstance(TimeZone
					.getTimeZone(Config.TIME_ZONE));
			now.setTime(new Date(System.currentTimeMillis()));
			addLastPlayTime(pc, now.getTime());
		}
	}

	// 初期化+ 会場準備　処理 end

	private void addLastPlayTime(L1PcInstance pc, Date date) {
		if (!lastPlayTime.containsKey(pc.getName())) {
			lastPlayTime.put(pc.getName(), date);
		}
	}

	/*
	 * private void removeLastPlayTime(L1PcInstance pc) {
	 * if(lastPlayTime.containsKey(pc.getName())){
	 * lastPlayTime.remove(pc.getName()); } }
	 */
	public void addOrderCancelList(L1PcInstance pc) {
		if (orderCancelList.containsKey(pc.getName())) {
			int cnt = orderCancelList.get(pc.getName()) + 1;
			orderCancelList.remove(pc.getName());
			orderCancelList.put(pc.getName(), cnt);
		} else {// しない
			orderCancelList.put(pc.getName(), 1);
		}
		if (getOrderCancelList(pc) > 4) {// 日付を保存
			Calendar now = Calendar.getInstance(TimeZone
					.getTimeZone(Config.TIME_ZONE));
			now.setTime(new Date(System.currentTimeMillis()));
			addLastPlayTime(pc, now.getTime());
		}
	}

	private void removeOrderList(L1PcInstance pc) {
		if (orderList.contains(pc)) {
			orderList.remove(pc);
		}
	}

	private int getOrderCancelList(L1PcInstance pc) {
		int cnt = 0;
		if (orderCancelList.containsKey(pc.getName())) {
			cnt = orderCancelList.get("" + pc.getName());
		}
		return cnt;
	}

	// 予約受付
	private void addOrderList(L1PcInstance pc) {
		if (orderList.contains(pc)) {
			pc.sendPackets(new S_ServerMessage(1266)); // 1266
			// すでにデスマッチへの出場予約が受け付けられています。
			return;
		}
		orderList.add(pc);
		pc.sendPackets(new S_ServerMessage(1265, String.valueOf(orderList
				.size()))); // 1265 エントリーナンバー%0でデスマッチへの出場が予約されました。

		if (orderList.size() >= orderEntMinPlayer) {
			setGameStatus(STATUS_READY);
			for (L1PcInstance player : orderList) {
				player.sendPackets(new S_MessageYN(1268, null)); // 1268
				// デスマッチに出場しますか？（Y/N）
			}

		}
	}

	private boolean checkPlayersOK() {
		if (getGameStatus() == STATUS_READY) {
			return playerList.size() >= gameStartMinPlayer;
		}
		return false;
	}

	private static final int END_STATUS_WINNER = 1;
	private static final int END_STATUS_NOWINNER = 2;
	private static final int END_STATUS_NOPLAYER = 3;

	// 競技終了後処理　1:勝利者がいる場合 2:時間切れの場合 3:参加人数不足
	private void setGameEnd(int type) {
		setGameStatus(STATUS_END);
		switch (type) {
		case END_STATUS_WINNER:
			stopGameTimeLimitTimer();
			if (_penalty != null) {
				_penalty.cancel();
			}
			if (_issue != null) {
				_issue.cancel();
			}
			sendEndMessage();
			break;
		case END_STATUS_NOWINNER:
			if (_penalty != null) {
				_penalty.cancel();
			}
			sendEndMessage();
			break;
		case END_STATUS_NOPLAYER:
			for (L1PcInstance pc : playerList) {
				// pc.sendPackets(new S_ServerMessage(1264)); 本鯖用メッセージ
				// 最小参加人数の2名に満たなかったため、レースを強制終了します。 1000アデナをお返ししました。
				pc.sendPackets(new S_SystemMessage(String.format(I18N_DEATHMATCH_LESS_THAN_MIN_PLAYERS, gameStartMinPlayer)));
				pc.getInventory().storeItem(40308, 1000);
			}
			break;
		}
		for (L1PcInstance pc : playerList) {
			/*
			 * L1ItemInstance
			 * IssuedItem=pc.getInventory().findItemId(ISSUE_ITEM);
			 * if(IssuedItem!=null){ pc.getInventory().deleteItem(IssuedItem); }
			 */
			if (pc.isGhost()) {
				pc.makeReadyEndGhost(false);
			}
			pc.startHpRegeneration();
			pc.startMpRegeneration();
		}
		startEndTimer();// 5秒後村に移動
	}

	// 賞品処理　start

	private void giftWinner() {
		L1PcInstance winner1 = get1stWinner();
		L1PcInstance winner2 = get2ndWinner();
		L1PcInstance winner3 = get3rdWinner();
		L1PcInstance winner4 = get4thWinner();
		L1PcInstance winner5 = get5thWinner();
		L1PcInstance winner6 = get6thWinner();
		int BraveCount[] = new int[6];
		/**
		 * 参加人数 1～5人 : 1位(1)支給 参加人数 6～8人 : 1位(2)、2位(1)支給 参加人数 9～11人 :
		 * 1位(3)、2位(2)、3位(1)支給 参加人数 12～14人 : 1位(4)、2位(3)、3位(2)、4位(1)支給 参加人数
		 * 15～17人 : 1位(5)、2位(4)、3位(3)、4位(2)、5位(1)支給 参加人数 18～20人 :
		 * 1位(6)、2位(5)、3位(4)、4位(3)、5位(2)、6位(1)支給 ※参加人数の報酬"順位(勇者の証)"
		 */
		if (_startPlayerNum >= 1 && 5 >= _startPlayerNum) {
			BraveCount[0] = 1;
		} else if (_startPlayerNum >= 6 && 8 >= _startPlayerNum) {
			BraveCount[0] = 2;
			BraveCount[1] = 1;
		} else if (_startPlayerNum >= 9 && 11 >= _startPlayerNum) {
			BraveCount[0] = 3;
			BraveCount[1] = 2;
			BraveCount[2] = 1;
		} else if (_startPlayerNum >= 12 && 14 >= _startPlayerNum) {
			BraveCount[0] = 4;
			BraveCount[1] = 3;
			BraveCount[2] = 2;
			BraveCount[3] = 1;
		} else if (_startPlayerNum >= 15 && 17 >= _startPlayerNum) {
			BraveCount[0] = 5;
			BraveCount[1] = 4;
			BraveCount[2] = 3;
			BraveCount[3] = 2;
			BraveCount[4] = 1;
		} else if (_startPlayerNum >= 18 && 20 >= _startPlayerNum) {
			BraveCount[0] = 6;
			BraveCount[1] = 5;
			BraveCount[2] = 4;
			BraveCount[3] = 3;
			BraveCount[4] = 2;
			BraveCount[5] = 1;
		}
		L1ItemInstance item1 = ItemTable.getInstance().createItem(41402);// 勇者の証

		if (winner1 == null) {
			return;
		}
		if (winner1.getInventory().checkAddItem(item1, BraveCount[0]) == L1Inventory.OK) {
			item1.setCount(BraveCount[0]);
			winner1.getInventory().storeItem(41402, BraveCount[0]);
			winner1.sendPackets(new S_ServerMessage(403, item1.getLogName()));
			// %0を手に入れました。
		}
		if (BraveCount[1] != 0) {
			if (winner2.getInventory().checkAddItem(item1, BraveCount[1]) == L1Inventory.OK) {
				item1.setCount(BraveCount[1]);
				winner2.getInventory().storeItem(41402, BraveCount[1]);
				winner2
						.sendPackets(new S_ServerMessage(403, item1
								.getLogName()));
				// %0を手に入れました。
			}
		}
		if (BraveCount[2] != 0) {
			if (winner3.getInventory().checkAddItem(item1, BraveCount[2]) == L1Inventory.OK) {
				item1.setCount(BraveCount[2]);
				winner3.getInventory().storeItem(41402, BraveCount[2]);
				winner3
						.sendPackets(new S_ServerMessage(403, item1
								.getLogName()));
				// %0を手に入れました。
			}
		}
		if (BraveCount[3] != 0) {
			if (winner4.getInventory().checkAddItem(item1, BraveCount[3]) == L1Inventory.OK) {
				item1.setCount(BraveCount[3]);
				winner4.getInventory().storeItem(41402, BraveCount[3]);
				winner4
						.sendPackets(new S_ServerMessage(403, item1
								.getLogName()));
				// %0を手に入れました。
			}
		}
		if (BraveCount[4] != 0) {
			if (winner5.getInventory().checkAddItem(item1, BraveCount[4]) == L1Inventory.OK) {
				item1.setCount(BraveCount[4]);
				winner5.getInventory().storeItem(41402, BraveCount[4]);
				winner5
						.sendPackets(new S_ServerMessage(403, item1
								.getLogName()));
				// %0を手に入れました。
			}
		}
		if (BraveCount[5] != 0) {
			if (winner6.getInventory().checkAddItem(item1, BraveCount[5]) == L1Inventory.OK) {
				item1.setCount(BraveCount[5]);
				winner6.getInventory().storeItem(41402, BraveCount[5]);
				winner6
						.sendPackets(new S_ServerMessage(403, item1
								.getLogName()));
				// %0を手に入れました。
			}
		}

		// 勝利の欠片　処理 4位まではランダムで渡す

		fragment(winner1, winner2, winner3, winner4);

	}

	/**
	 *
	 * // 　勝利の欠片入手判定
	 *
	 * @param winner1
	 * @param winner2
	 * @param winner3
	 * @param winner4
	 */
	private void fragment(L1PcInstance winner1, L1PcInstance winner2,
			L1PcInstance winner3, L1PcInstance winner4) {
		L1ItemInstance item = ItemTable.getInstance().createItem(50515);// デスマッチ勝利の欠片　1位には支給
		if (winner1.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
			item.setCount(1);
			winner1.getInventory().storeItem(item);
			winner1.sendPackets(new S_ServerMessage(403, item.getLogName()));
			// %0 を手に入れました。
		}

		if ((_random.nextInt(100) + 1) <= 40) { // 40%の確率で各種ミニゲーム勝利の欠片入手
			int i = _random.nextInt(fragment.length);
			item = ItemTable.getInstance().createItem(fragment[i]);// その他ミニゲーム勝利の欠片１つ
			if (winner1.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
				item.setCount(1);
				winner1.getInventory().storeItem(item);
				winner1
						.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0を手に入れました。
			}
		}

		/**********/
		if (winner2 != null) {
			if ((_random.nextInt(100) + 1) <= 30 * 2) {
				item = ItemTable.getInstance().createItem(50515);// デスマッチ勝利の欠片
				if (winner2.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
					winner2.getInventory().storeItem(50515, 1);
					winner2.sendPackets(new S_ServerMessage(403, item
							.getLogName())); // %0を手に入れました。
				}
			}

			if ((_random.nextInt(100) + 1) <= 30) { // 30%の確率でその他ミニゲーム勝利の欠片入手
				int i = _random.nextInt(fragment.length);
				item = ItemTable.getInstance().createItem(fragment[i]);// その他ミニゲーム勝利の欠片１つ
				if (winner2.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
					item.setCount(1);
					winner2.getInventory().storeItem(item);
					winner2.sendPackets(new S_ServerMessage(403, item
							.getLogName())); // %0を手に入れました。
				}
			}
		}
		/**********/
		if (winner3 != null) {
			if ((_random.nextInt(100) + 1) <= 20 * 2) {
				item = ItemTable.getInstance().createItem(50515);// デスマッチ勝利の欠片
				if (winner3.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
					winner3.getInventory().storeItem(50515, 1);
					winner3.sendPackets(new S_ServerMessage(403, item
							.getLogName())); // %0を手に入れました。
				}
			}

			if ((_random.nextInt(100) + 1) <= 20) { // 20%の確率でその他ミニゲーム勝利の欠片入手
				int i = _random.nextInt(fragment.length);
				item = ItemTable.getInstance().createItem(fragment[i]);// その他ミニゲーム勝利の欠片１つ
				if (winner3.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
					item.setCount(1);
					winner3.getInventory().storeItem(item);
					winner3.sendPackets(new S_ServerMessage(403, item
							.getLogName())); // %0を手に入れました。
				}
			}
		}
		/**********/
		if (winner4 != null) {
			if ((_random.nextInt(10) + 1) <= 10 * 2) {
				item = ItemTable.getInstance().createItem(50515);// デスマッチ勝利の欠片
				if (winner4.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
					winner4.getInventory().storeItem(50515, 1);
					winner4.sendPackets(new S_ServerMessage(403, item
							.getLogName())); // %0を手に入れました。
				}
			}

			if ((_random.nextInt(100) + 1) <= 10) { // 10%の確率でその他ミニゲーム勝利の欠片入手
				int i = _random.nextInt(fragment.length);
				item = ItemTable.getInstance().createItem(fragment[i]);// その他ミニゲーム勝利の欠片１つ
				if (winner4.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
					item.setCount(1);
					winner4.getInventory().storeItem(item);
					winner4.sendPackets(new S_ServerMessage(403, item
							.getLogName())); // %0を手に入れました。
				}
			}
		}
	}

	private void sendEndMessage() {
		L1PcInstance winner = get1stWinner();
		for (L1PcInstance pc : playerList) {
			pc.sendPackets(new S_DeathMatch(S_DeathMatch.CountDownOff));
			if (winner != null) {
				pc.sendPackets(new S_ServerMessage(1272, winner.getName()));// 1272
				// %0%s
				// デスマッチで勝利を収めました。

				/*
				 * if(winner.getId()==pc.getId()){ pc.sendPackets(new
				 * S_DeathMatch(S_DeathMatch.Winner)); }else{ pc.sendPackets(new
				 * S_DeathMatch(S_DeathMatch.GameOver)); }
				 */
			} else {
				pc.sendPackets(new S_ServerMessage(1275));// 1275
				// デスマッチ終了の時間となりました。優勝者はいませんでした。ゲームを終了します。
				// pc.sendPackets(new S_DeathMatch(S_DeathMatch.GameOver));
			}
			pc.sendPackets(new S_ServerMessage(1259)); // まもなく村へ移動されます。
		}
	}

	// 初期化
	private void setGameInit() {
		for (L1PcInstance pc : playerList) {
			L1Teleport.teleport(pc, 32616, 32782, (short) 4, 5, true);
		}
		setStartTime(0);
		setGameStatus(STATUS_NONE);
		setWinner(null);
		set1stWinner(null);
		set2ndWinner(null);
		set3rdWinner(null);
		set4thWinner(null);
		set5thWinner(null);
		set6thWinner(null);
		_startPlayerNum = 0;
		_penalty = null;
		_issue = null;
		playerList.clear();
		for (L1PcInstance pc : orderList) {// 出場していないプレイヤーをキャンセルリストへ
			addOrderCancelList(pc);
		}
		orderList.clear();
	}

	// XXX for ClientThread.java
	public void checkLeaveGame(L1PcInstance pc) {
		if (pc.getMapId() == this.getMapId()) {
			removePlayerList(pc);
			// addOrderCancelList(pc);//切断した場合はキャンセルと同じ扱いにする
			if (getGameStatus() == STATUS_PLAYING) {
				sendRemainder(null);
			}
			// L1PolyMorph.undoPoly(pc);
			L1Teleport.teleport(pc, 32624, 32813, (short) 4, 5, false);
		}
	}

	public void removeSkillEffect(L1PcInstance pc) {
		L1SkillUse skill = new L1SkillUse();
		skill.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(),
				pc.getY(), null, 0, Base.SKILL_TYPE[1]);
	}

	private int _status = 0;

	public void setGameStatus(int i) {
		_status = i;
	}

	public int getGameStatus() {
		return _status;
	}

	private L1PcInstance _winner = null;

	public void setWinner(L1PcInstance pc) {
		_winner = pc;
	}

	public L1PcInstance getWinner() {
		return _winner;
	}

	private GameTimeLimitTimer limitTimer;

	private void startGameTimeLimitTimer() {
		Timer timer = new Timer();
		limitTimer = new GameTimeLimitTimer();
		timer.schedule(limitTimer, limitTime);
	}

	private void stopGameTimeLimitTimer() {
		if (limitTimer != null) {
			limitTimer.stopTimer();
		}
	}

	private void startEndTimer() {
		new EndTimer().begin();
	}

	private LimitPenaltyTimer _penalty;
	private IssueItem _issue;
	private int _startPlayerNum;

	private class CheckTimer extends TimerTask {
		@Override
		public void run() {
			for (L1PcInstance pc : playerList) {
				pc.sendPackets(new S_ServerMessage(1269)); // 1269
				// まもなくデスマッチがスタートします。
			}

			try {
				Thread.sleep(6 * 1000);
				_doorLeft.open();
				CloseTimer temp = new CloseTimer(_doorLeft);
				temp.begin();
				Thread.sleep(4 * 1000);// 50+10=60s
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (checkPlayersOK()) {
				setGameStatus(STATUS_PLAYING);
				for (L1PcInstance pc : playerList) {
					pc.sendPackets(new S_DeathMatch(S_DeathMatch.CountDown));
				}
				startGameTimeLimitTimer();
				_issue = new IssueItem();
				_issue.begin();
				_penalty = new LimitPenaltyTimer();
				_penalty.begin();
				_startPlayerNum = playerList.size();
				setRemainder(_startPlayerNum);

				for (L1Object obj : L1World.getInstance().getVisiblePoint(
						new L1Location(32638, 32880, getMapId()), 3)) {
					if (obj instanceof L1PcInstance) {
						L1Location loc = new L1Location(32625 + _random
								.nextInt(28), 32885 + _random.nextInt(28),
								getMapId());
						while (!loc.getMap().isPassable(loc.getX(), loc.getY())) {
							loc.set(32625 + _random.nextInt(28),
									32885 + _random.nextInt(28), getMapId());
						}
						L1Teleport.teleport((L1PcInstance) obj, loc.getX(), loc
								.getY(), (short) loc.getMapId(), 5, false);
					}
				}
				for (L1Object obj : L1World.getInstance().getVisiblePoint(
						new L1Location(32658, 32899, getMapId()), 3)) {
					if (obj instanceof L1PcInstance) {
						L1Location loc = new L1Location(32625 + _random
								.nextInt(28), 32885 + _random.nextInt(28),
								getMapId());
						while (!loc.getMap().isPassable(loc.getX(), loc.getY())) {
							loc.set(32625 + _random.nextInt(28),
									32885 + _random.nextInt(28), getMapId());
						}
						L1Teleport.teleport((L1PcInstance) obj, loc.getX(), loc
								.getY(), (short) loc.getMapId(), 5, false);
					}
				}

			} else {
				setGameEnd(END_STATUS_NOPLAYER);
			}
			this.cancel();
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 50 * 1000); // 50s
		}
	}

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

	// ペナルティで死亡するか不明、とりあえず１まで減らす
	// hp/mp減少・周期の値　不明
	private class LimitPenaltyTimer extends TimerTask {
		@Override
		public void run() {
			if (getGameStatus() == STATUS_PLAYING) {
				for (L1PcInstance pc : playerList) {
					if (!pc.isDead() && pc.getMapId() == getMapId()) {
						if (_random.nextInt(2) > 0) {
							int newHp = pc.getCurrentHp() - 10;
							if (newHp < 1) {
								newHp = 1;
							}
							pc.setCurrentHp(newHp);
						} else {
							int newMp = pc.getCurrentMp() - 3;
							if (newMp < 1) {
								newMp = 1;
							}
							pc.setCurrentMp(newMp);
						}
					}
				}
			} else {
				this.cancel();
			}
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, limitPenaltyTime, 10 * 1000); // 10秒周期
		}
	}

	// 支給品配布
	private class IssueItem extends TimerTask {
		@Override
		public void run() {
			if (getGameStatus() == STATUS_PLAYING) {
				for (L1PcInstance pc : playerList) {
					if (pc.getMapId() == getMapId()) {// 生死に関わらず支給
						// XXX
						L1ItemInstance item = ItemTable.getInstance()
								.createItem(ISSUE_ITEM);//
						if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
							item.setCount(1);
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(403, item
									.getLogName())); // %0を手に入れました。
						}
					}
				}
			} else {
				this.cancel();
			}
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 60 * 3 * 1000); //
		}
	}

	private class EndTimer extends TimerTask {
		@Override
		public void run() {
			giftWinner();
			setGameInit();
			this.cancel();
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 10000);
		}
	}

	private L1PcInstance _1stwinner = null;
	private L1PcInstance _4thwinner = null;

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

	public void setMapId(short mapId) {
		this.mapId = mapId;
	}

	public short getMapId() {
		return mapId;
	}

	public void set4thWinner(L1PcInstance _4thwinner) {
		this._4thwinner = _4thwinner;
	}

	public L1PcInstance get4thWinner() {
		return _4thwinner;
	}

	private L1PcInstance _5thwinner;
	private L1PcInstance _6thwinner;

	public L1PcInstance get5thWinner() {
		return _5thwinner;
	}

	public void set5thWinner(L1PcInstance _5thwinner) {
		this._5thwinner = _5thwinner;
	}

	public L1PcInstance get6thWinner() {
		return _6thwinner;
	}

	public void set6thWinner(L1PcInstance _6thwinner) {
		this._6thwinner = _6thwinner;
	}

	private int _remainder = 0;

	public void sendRemainder(L1PcInstance deathPlayer) {
		int players = getRemainder() - 1;
		L1PcInstance winner = null;
		setRemainder(players);
		if (players <= 1) {
			for (L1PcInstance pc : playerList) {
				// if(!pc.isGhost()&&!pc.isDead()&&pc.getMap().getBaseMapId()==5153){
				if (!pc.isGhost() && !pc.isDead() && pc.getMapId() == 5153) {
					winner = pc;
					break;
				}
			}
			setWinner(winner);
			set1stWinner(winner);
			set2ndWinner(deathPlayer);
			setGameEnd(END_STATUS_WINNER);
		} else if (players == 2) {
			set3rdWinner(deathPlayer);
			for (L1PcInstance pc : playerList) {
				if (pc.getMapId() == getMapId()) {
					pc.sendPackets(new S_ServerMessage(1274, "" + players));// 1274
					// 生存者は残り%0名です。
				}
			}
		} else if (players == 3) {
			set4thWinner(deathPlayer);
			for (L1PcInstance pc : playerList) {
				if (pc.getMapId() == getMapId()) {
					pc.sendPackets(new S_ServerMessage(1274, "" + players));// 1274
					// 生存者は残り%0名です。
				}
			}
		} else if (players == 4) {
			set5thWinner(deathPlayer);
			for (L1PcInstance pc : playerList) {
				if (pc.getMapId() == getMapId()) {
					pc.sendPackets(new S_ServerMessage(1274, "" + players));// 1274
					// 生存者は残り%0名です。
				}
			}
		} else if (players == 5) {
			set6thWinner(deathPlayer);
			for (L1PcInstance pc : playerList) {
				if (pc.getMapId() == getMapId()) {
					pc.sendPackets(new S_ServerMessage(1274, "" + players));// 1274
					// 生存者は残り%0名です。
				}
			}
		} else {
			for (L1PcInstance pc : playerList) {
				if (pc.getMapId() == getMapId()) {
					pc.sendPackets(new S_ServerMessage(1274, "" + players));// 1274
					// 生存者は残り%0名です。
				}
			}
		}
	}

	public void setRemainder(int remainder) {
		this._remainder = remainder;
	}

	public int getRemainder() {
		return _remainder;
	}

	public class CloseTimer extends TimerTask {

		private final L1DoorInstance _door;

		public CloseTimer(L1DoorInstance door) {
			_door = door;
		}

		@Override
		public void run() {
			if (_door.getOpenStatus() == ActionCodes.ACTION_Open) {
				for (L1Object obj : L1World.getInstance().getVisiblePoint(
						new L1Location(_door.getX(), _door.getY(), _door
								.getMapId()), 0)) {
					if (obj instanceof L1PcInstance) {
						L1Location loc = new L1Location(32625 + _random
								.nextInt(28), 32885 + _random.nextInt(28),
								getMapId());
						while (!loc.getMap().isPassable(loc.getX(), loc.getY())) {
							loc.set(32625 + _random.nextInt(28),
									32885 + _random.nextInt(28), getMapId());
						}
						L1Teleport.teleport((L1PcInstance) obj, loc.getX(), loc
								.getY(), (short) loc.getMapId(), 5, false);
					}
				}
				for (L1Object obj : L1World.getInstance().getVisiblePoint(
						new L1Location(_door.getX() + 1, _door.getY(), _door
								.getMapId()), 0)) {
					if (obj instanceof L1PcInstance) {
						L1Location loc = new L1Location(32625 + _random
								.nextInt(28), 32885 + _random.nextInt(28),
								getMapId());
						while (!loc.getMap().isPassable(loc.getX(), loc.getY())) {
							loc.set(32625 + _random.nextInt(28),
									32885 + _random.nextInt(28), getMapId());
						}
						L1Teleport.teleport((L1PcInstance) obj, loc.getX(), loc
								.getY(), (short) loc.getMapId(), 5, false);
					}
				}

				_door.close();
			}
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 4 * 1000);
		}
	}
}
