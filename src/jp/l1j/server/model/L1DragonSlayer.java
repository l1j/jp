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

import jp.l1j.server.datatables.ReturnLocationTable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.types.Point;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.model.instance.L1DoorInstance;
import jp.l1j.server.model.instance.L1EffectInstance;
import jp.l1j.server.model.instance.L1FieldObjectInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1TrapInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.trap.L1WorldTraps;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.packets.server.S_CharVisualUpdate;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_MapID;
import jp.l1j.server.packets.server.S_NpcPack;
import jp.l1j.server.packets.server.S_OtherCharPacks;
import jp.l1j.server.packets.server.S_OwnCharPack;
import jp.l1j.server.packets.server.S_RemoveObject;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_Weather;
import jp.l1j.server.utils.collections.Maps;

public class L1DragonSlayer {
	private static Logger _log = Logger.getLogger(L1DragonSlayer.class.getName());

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	private static L1DragonSlayer _instance;

	public static final int STATUS_DRAGONSLAYER_NONE = 0;
	public static final int STATUS_DRAGONSLAYER_READY_1RD = 1;
	public static final int STATUS_DRAGONSLAYER_READY_2RD = 2;
	public static final int STATUS_DRAGONSLAYER_READY_3RD = 3;
	public static final int STATUS_DRAGONSLAYER_READY_4RD = 4;
	public static final int STATUS_DRAGONSLAYER_START_1RD = 5;
	public static final int STATUS_DRAGONSLAYER_START_2RD = 6;
	public static final int STATUS_DRAGONSLAYER_START_2RD_1 = 7;
	public static final int STATUS_DRAGONSLAYER_START_2RD_2 = 8;
	public static final int STATUS_DRAGONSLAYER_START_2RD_3 = 9;
	public static final int STATUS_DRAGONSLAYER_START_2RD_4 = 10;
	public static final int STATUS_DRAGONSLAYER_START_3RD = 11;
	public static final int STATUS_DRAGONSLAYER_START_3RD_1 = 12;
	public static final int STATUS_DRAGONSLAYER_START_3RD_2 = 13;
	public static final int STATUS_DRAGONSLAYER_START_3RD_3 = 14;
	public static final int STATUS_DRAGONSLAYER_END_1 = 15;
	public static final int STATUS_DRAGONSLAYER_END_2 = 16;
	public static final int STATUS_DRAGONSLAYER_END_3 = 17;
	public static final int STATUS_DRAGONSLAYER_END_4 = 18;
	public static final int STATUS_DRAGONSLAYER_END_5 = 19;
	public static final int STATUS_DRAGONSLAYER_END = 20;

	public static final int STATUS_NONE = 0;
	public static final int STATUS_READY_SPAWN = 1;
	public static final int STATUS_SPAWN = 2;

	private static class DragonSlayer {
		private ArrayList<L1PcInstance> _members = new ArrayList<L1PcInstance>();
	}

	private static final Map<Integer, DragonSlayer> _dataMap = Maps.newHashMap();

	public static L1DragonSlayer getInstance() {
		if (_instance == null) {
			_instance = new L1DragonSlayer();
		}
		return _instance;
	}

	private boolean[] _portalNumber = new boolean[24];

	public boolean[] getPortalNumber() {
		return _portalNumber;
	}

	public void setPortalNumber(int number, boolean i) {
		_portalNumber[number] = i;
	}

	private L1Location _location;
	L1Location loc = new L1Location();

	L1Location getLocation() {
		return _location;
	}

	private boolean[] _checkDragonPortal = new boolean[4];

	public boolean[] checkDragonPortal() {
		_checkDragonPortal[0] = false; // アンタラス
		_checkDragonPortal[1] = false; // パプリオン
		_checkDragonPortal[2] = false; // リンドビオル
		_checkDragonPortal[3] = false; // ヴァラカス(未実装)

		for (int i = 0; i < 18; i++) {
			if (!getPortalNumber()[i]) {
				if (i < 6) { // アンタラス
					_checkDragonPortal[0] = true;
				} else if (i < 12) { // パプリオン
					_checkDragonPortal[1] = true;
				} else if (i < 18) { // リンドビオル
					_checkDragonPortal[2] = true;
				} else if (i < 24) { // ヴァラカス(未実装)
					_checkDragonPortal[3] = true;
				}
			}
		}
		return _checkDragonPortal;
	}

	private L1NpcInstance[] _portal = new L1NpcInstance[24];

	public L1NpcInstance[] portalPack() {
		return _portal;
	}

	public void setPortalPack(int number, L1NpcInstance portal) {
		_portal[number] = portal;
	}

	private int[] _DragonSlayerStatus = new int[24];

	public int[] getDragonSlayerStatus() {
		return _DragonSlayerStatus;
	}

	public void setDragonSlayerStatus(int portalNum, int i) {
		_DragonSlayerStatus[portalNum] = i;
	}

	private int _hiddenDragonValleyStatus = 0;

	public int checkHiddenDragonValleyStatus() {
		return _hiddenDragonValleyStatus;
	}

	public void setHiddenDragonValleyStatus(int i) {
		_hiddenDragonValleyStatus = i;
	}

	public void addPlayerList(L1PcInstance pc, int portalNum) {
		if (_dataMap.containsKey(portalNum)) {
			if (!_dataMap.get(portalNum)._members.contains(pc)) {
				_dataMap.get(portalNum)._members.add(pc);
			}
		}
	}

	public void removePlayer(L1PcInstance pc, int portalNum) {
		if (_dataMap.containsKey(portalNum)) {
			if (_dataMap.get(portalNum)._members.contains(pc)) {
				_dataMap.get(portalNum)._members.remove(pc);
			}
		}
	}

	private void clearPlayerList(int portalNum) {
		if (_dataMap.containsKey(portalNum)) {
			_dataMap.get(portalNum)._members.clear();
		}
	}

	public int getPlayersCount(int num) {
		DragonSlayer _DragonSlayer = null;
		if (!_dataMap.containsKey(num)) {
			_DragonSlayer = new DragonSlayer();
			_dataMap.put(num, _DragonSlayer);
		}
		return _dataMap.get(num)._members.size();
	}

	private L1PcInstance[] getPlayersArray(int num) {
		return _dataMap.get(num)._members.toArray(new L1PcInstance[_dataMap.get(num)._members.size()]);
	}

	public void startDragonSlayer(int portalNum) {
		if (getDragonSlayerStatus()[portalNum] == STATUS_DRAGONSLAYER_NONE) {
			setDragonSlayerStatus(portalNum, STATUS_DRAGONSLAYER_READY_1RD);
			DragonSlayerTimer timer = new DragonSlayerTimer(portalNum, STATUS_DRAGONSLAYER_READY_1RD, 150000);
			timer.begin();
		}
	}

	public void startDragonSlayer2rd(int portalNum) {
		if (getDragonSlayerStatus()[portalNum] == STATUS_DRAGONSLAYER_START_1RD) {
			if (portalNum < 6) {
				sendMessage(portalNum, 1573, null);
				// アンタラス：愚か者めが！俺を怒らせるのか。
			} else if (portalNum < 12) {
				sendMessage(portalNum, 1661, null);
				// パプリオン：笑わせるな！あいつらがお前と共にあの世をさまようことになる、俺の生贄か！
			} else if (portalNum < 18) {
				sendMessage(portalNum, 1759, null);
				// リンドビオル：笑わせるな！自分たちの愚かさを骨身にしみるまで思い知らせてやる！
			// } else if (portalNum < 24) {
			// ヴァラカス(未実装)
			}
			setDragonSlayerStatus(portalNum, STATUS_DRAGONSLAYER_START_2RD);
			DragonSlayerTimer timer = new DragonSlayerTimer(portalNum, STATUS_DRAGONSLAYER_START_2RD, 10000);
			timer.begin();
		}
	}

	public void startDragonSlayer3rd(int portalNum) {
		if (getDragonSlayerStatus()[portalNum] == STATUS_DRAGONSLAYER_START_2RD_4) {
			if (portalNum < 6) {
				sendMessage(portalNum, 1577, null);
				// クレイ：うぉーっ！血塗られた魂の叫びが聞こえないのか！死ね！
			} else if (portalNum < 12) {
				sendMessage(portalNum, 1665, null);
				// 巫女サエル：パプリオンの力がかなり落ちたようです！勇士たちよ！もうちょっとです！
			} else if (portalNum < 18) {
				sendMessage(portalNum, 1769, null);
				// 雲の大精霊：雲の精霊よ！僕に力を！
			// } else if (portalNum < 24) {
			// ヴァラカス(未実装)
			}
			setDragonSlayerStatus(portalNum, STATUS_DRAGONSLAYER_START_3RD);
			DragonSlayerTimer timer = new DragonSlayerTimer(portalNum, STATUS_DRAGONSLAYER_START_3RD, 10000);
			timer.begin();
		}
	}

	public void endDragonSlayer(int portalNum) {
		if (getDragonSlayerStatus()[portalNum] == STATUS_DRAGONSLAYER_START_3RD_3) {
			setDragonSlayerStatus(portalNum, STATUS_DRAGONSLAYER_END_1);
			DragonSlayerTimer timer = new DragonSlayerTimer(portalNum, STATUS_DRAGONSLAYER_END_1, 10000);
			timer.begin();
		}
	}

	public void endDragonPortal(int portalNum) {
		if (getDragonSlayerStatus()[portalNum] != STATUS_DRAGONSLAYER_END_5) {
			setDragonSlayerStatus(portalNum, STATUS_DRAGONSLAYER_END_5);
			DragonSlayerTimer timer = new DragonSlayerTimer(portalNum, STATUS_DRAGONSLAYER_END_5, 5000);
			timer.begin();
		}
	}

	public class DragonSlayerTimer extends TimerTask {

		private final int _num;
		private final int _status;
		private final int _time;

		public DragonSlayerTimer(int num, int status, int time) {
			_num = num;
			_status = status;
			_time = time;
		}

		@Override
		public void run() {
			short mapId = (short) (1005 + _num);
			int[] msg = new int[10];
			if (_num < 6) {
				msg = new int[] { 1570, 1571, 1572, 1574, 1575, 1576, 1578, 1579, 1581, 1593};
				// アンタラス：誰だ、私を眠りから覚ましたのは。
				// クレイ：アンタラス！お前を追ってこの漆黒の闇までやって来た！
				// アンタラス：笑止千万！クレイ！もう一度あの世へ送ってやる！
				// クレイ：勇士たちよ！君たちの剣にアデンの運命がかかっている。アンタラスの黒い息遣いを止めることができるのは君たちだけだ！
				// アンタラス：そんなガラクタで俺に勝てるとでも思ってるのか！グハハハ！
				// アンタラス：そろそろうまいめしにありつけそうだな。おまえらの血のにおい、うーんたまらん！
				// アンタラス：生意気なやつらめ！死にたいのか！
				// アンタラス：許さん！すぐに父上がお出ましになるだろう。
				// クレイ：おお…最強の勇士であることを示してみせた最高のナイトよ！試練に打ち勝ってアンタラスをしとめたのだな。恨みは解けるだろう。ウハハハ！
				// ドワーフの叫び：アンタラスの黒い息遣いを止めた勇者が誕生しました！
			} else if (_num < 12) {
				msg = new int[] { 1657, 1658, 1659, 1662, 1663, 1664, 1666, 1667, 1669, 1682};
				// パプリオン：俺の領域を侵すとは…その勇気は高く買おう。
				// 巫女サエル：この卑劣なパプリオンめ！私を騙した代償を払わせてやる！
				// パプリオン：封印を解く時にはお前が助けになったが…俺に二回目の慈悲はない…
				// 巫女サエル：勇士たちよ！あの邪悪なパプリオンを倒して、エヴァ王国にかけられた血の呪いを解いてください！
				// パプリオン：手頃なおもちゃだな！ハハハ！
				// パプリオン：骨の髄まで凍りつくほどの恐怖を味わせてやろう！
				// 巫女サエル：パプリオンの力がかなり落ちたようです！勇士たちよ！もうちょっとです！
				// パプリオン：おまえの言うところの希望なるものが、虚構であることを教えてやろう！
				// パプリオン：サエルの味方をしたことを後悔させてやろう！この愚か者めが！
				// 巫女サエル：ありがとうございます。あなたがたはアデン最高の勇士です。これでエヴァ王国にかけられた長年の呪いも解けるでしょう。
				// カイムサムの叫び：パプリオンの黒い息遣いを止めた勇者が誕生しました！
			} else if (_num < 18) {
				msg = new int[] { 1755, 1760, 1758, 1763, 1767, 1768, 1770, 1771, 1772, 1754};
				// リンドビオル：俺の安眠を邪魔するのは誰だ！
				// 雲の大精霊：リンドビオル様の聖所に忍び込んだクセモノは誰だ！
				// リンドビオル：このリンドビオルを怒らせた代償を払ってもらおうか。
				// 雲の大精霊：ウウウ…リンドビオル様のご機嫌を損ねるとは！
				// リンドビオル：このこざかしいやつらめ！俺を試そうというのか！
				// リンドビオル：あがけ！もっとあがいてみろ！
				// リンドビオル：自分たちの愚かさを嘆いても手遅れだ！
				// リンドビオル：もう許さん！もう一度かかってこい！
				// リンドビオル：グアアアッ！こんなはずが！お前たちをナメてかかった俺がバカだった…
				// ドワーフの叫び：リンドビオルの翼を折った勇者が誕生しました！
			// } else if (_num < 24) {
			// ヴァラカス(未実装)
			}
			switch (_status) {
				// 第1ステージ
				case STATUS_DRAGONSLAYER_READY_1RD:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_READY_2RD);
					sendMessage(_num, msg[0], null);
					// アンタラス：誰だ、私を眠りから覚ましたのは。
					// パプリオン：俺の領域を侵すとは…その勇気は高く買おう。
					// リンドビオル：俺の安眠を邪魔するのは誰だ！
					DragonSlayerTimer timer_1rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_READY_2RD, 10000);
					timer_1rd.begin();
					break;
				case STATUS_DRAGONSLAYER_READY_2RD:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_READY_3RD);
					sendMessage(_num, msg[1], null);
					// クレイ：アンタラス！お前を追ってこの漆黒の闇までやって来た！
					// 巫女サエル：この卑劣なパプリオンめ！私を騙した代償を払わせてやる！
					// 雲の大精霊：リンドビオル様の聖所に忍び込んだクセモノは誰だ！
					DragonSlayerTimer timer_2rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_READY_3RD, 10000);
					timer_2rd.begin();
					break;
				case STATUS_DRAGONSLAYER_READY_3RD:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_READY_4RD);
					sendMessage(_num, msg[2], null);
					// アンタラス：笑止千万！クレイ！もう一度あの世へ送ってやる！
					// パプリオン：封印を解く時にはお前が助けになったが…俺に二回目の慈悲はない…
					// リンドビオル：このリンドビオルを怒らせた代償を払ってもらおうか。
					DragonSlayerTimer timer_3rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_READY_4RD, 10000);
					timer_3rd.begin();
					break;
				case STATUS_DRAGONSLAYER_READY_4RD:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_START_1RD);
					if (_num < 6) {
						spawn(91200, _num, 32783, 32693, mapId, 10, 0); // アンタラスLv1
					} else if (_num < 12) {
						spawn(91514, _num, 32955, 32839, mapId, 10, 0); // パプリオンLv1
					} else if (_num < 18) {
						spawn(91603, _num, 32848, 32879, mapId, 10, 0); // リンドビオルLv1
					//} else if (_num < 24) {
					// ヴァラカスLv1(未実装)
					}
					break;
				// 第2ステージ
				case STATUS_DRAGONSLAYER_START_2RD:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_START_2RD_1);
					sendMessage(_num, msg[3], null);
					// クレイ：勇士たちよ！君たちの剣にアデンの運命がかかっている。アンタラスの黒い息遣いを止めることができるのは君たちだけだ！
					// 巫女サエル：勇士たちよ！あの邪悪なパプリオンを倒して、エヴァ王国にかけられた血の呪いを解いてください！
					// 雲の大精霊：ウウウ…リンドビオル様のご機嫌を損ねるとは！
					DragonSlayerTimer timer_4rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_START_2RD_1, 10000);
					timer_4rd.begin();
					break;
				case STATUS_DRAGONSLAYER_START_2RD_1:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_START_2RD_2);
					sendMessage(_num, msg[4], null);
					// アンタラス：そんなガラクタで俺に勝てるとでも思ってるのか！グハハハ！
					// パプリオン：手頃なおもちゃだな！ハハハ！
					// リンドビオル：このこざかしいやつらめ！俺を試そうというのか！
					DragonSlayerTimer timer_5rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_START_2RD_2, 30000);
					timer_5rd.begin();
					break;
				case STATUS_DRAGONSLAYER_START_2RD_2:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_START_2RD_3);
					sendMessage(_num, msg[5], null);
					// アンタラス：そろそろうまいめしにありつけそうだな。おまえらの血のにおい、うーんたまらん！
					// パプリオン：骨の髄まで凍りつくほどの恐怖を味わせてやろう！
					// リンドビオル：あがけ！もっとあがいてみろ！
					DragonSlayerTimer timer_6rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_START_2RD_3, 10000);
					timer_6rd.begin();
					break;
				case STATUS_DRAGONSLAYER_START_2RD_3:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_START_2RD_4);
					if (_num < 6) {
						spawn(91201, _num, 32783, 32693, mapId, 10, 0); // アンタラスLv2
					} else if (_num < 12) {
						spawn(91515, _num, 32955, 32839, mapId, 10, 0); // パプリオンLv2
					} else if (_num < 18) {
						spawn(91604, _num, 32863, 32861, mapId, 0, 0); // リンドビオルLv2
					//} else if (_num < 24) {
					// ヴァラカスLv2(未実装)
					}
					break;
				// 第3ステージ
				case STATUS_DRAGONSLAYER_START_3RD:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_START_3RD_1);
					sendMessage(_num, msg[6], null);
					// アンタラス：生意気なやつらめ！死にたいのか！
					// パプリオン：おまえの言うところの希望なるものが、虚構であることを教えてやろう！
					// リンドビオル：自分たちの愚かさを嘆いても手遅れだ！
					DragonSlayerTimer timer_7rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_START_3RD_1, 40000);
					timer_7rd.begin();
					break;
				case STATUS_DRAGONSLAYER_START_3RD_1:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_START_3RD_2);
					sendMessage(_num, msg[7], null);
					// アンタラス：許さん！すぐに父上がお出ましになるだろう。
					// パプリオン：サエルの味方をしたことを後悔させてやろう！この愚か者めが！
					// リンドビオル：もう許さん！もう一度かかってこい！
					DragonSlayerTimer timer_8rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_START_3RD_2, 10000);
					timer_8rd.begin();
					break;
				case STATUS_DRAGONSLAYER_START_3RD_2:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_START_3RD_3);
					if (_num < 6) {
						spawn(91202, _num, 32783, 32693, mapId, 10, 0); // アンタラスLv3
					} else if (_num < 12) {
						spawn(91516, _num, 32955, 32839, mapId, 10, 0); // パプリオンLv3
					} else if (_num < 18) {
						spawn(91605, _num, 32848, 32879, mapId, 10, 0); // リンドビオルLv3
					//} else if (_num < 24) {
					// ヴァラカスLv3(未実装)
					}
					break;
				case STATUS_DRAGONSLAYER_END_1:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_END_2);
					sendMessage(_num, msg[8], null);
					// クレイ：おお…最強の勇士であることを示してみせた最高のナイトよ！試練に打ち勝ってアンタラスをしとめたのだな。恨みは解けるだろう。ウハハハ！
					// 巫女サエル：ありがとうございます。あなたがたはアデン最高の勇士です。これでエヴァ王国にかけられた長年の呪いも解けるでしょう。
					// リンドビオル：グアアアッ！こんなはずが！お前たちをナメてかかった俺がバカだった…
					if (checkHiddenDragonValleyStatus() == STATUS_NONE) {
						setHiddenDragonValleyStatus(STATUS_READY_SPAWN);
						DragonSlayerTimer timer_9rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_END_2, 10000);
						timer_9rd.begin();
					} else {
						if (getDragonSlayerStatus()[_num] != STATUS_DRAGONSLAYER_END_5) {
							setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_END_5);
							DragonSlayerTimer timer = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_END_5, 5000);
							timer.begin();
						}
					}
						sendMessage(_num, msg[9], null);
						// ドワーフの叫び：アンタラスの黒い息遣いを止めた勇者が誕生しました！
						// カイムサムの叫び：パプリオンの黒い息遣いを止めた勇者が誕生しました！
						// ドワーフの叫び：リンドビオルの翼を折った勇者が誕生しました！
					break;
				case STATUS_DRAGONSLAYER_END_2:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_END_3);
					sendMessage(_num, 1582, null); // ドワーフの叫び：ウェルダン村で隠された竜の地の扉が開きました。
					if (checkHiddenDragonValleyStatus() == STATUS_READY_SPAWN) {
						setHiddenDragonValleyStatus(STATUS_SPAWN);
						spawn(91066, -1, 33726, 32506, (short) 4, 0, 86400000);
					}
					DragonSlayerTimer timer_10rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_END_3, 5000);
					timer_10rd.begin();
					break;
				case STATUS_DRAGONSLAYER_END_3:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_END_4);
					sendMessage(_num, 1583, null); // ドワーフの叫び：ウェルダン村で隠された竜の地の扉が開かれています。
					DragonSlayerTimer timer_11rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_END_4, 5000);
					timer_11rd.begin();
					break;
				case STATUS_DRAGONSLAYER_END_4:
					setDragonSlayerStatus(_num, STATUS_DRAGONSLAYER_END_5);
					sendMessage(_num, 1584, null); // ドワーフの叫び：早くこの地を去りなさい。そろそろ扉が閉じます。
					DragonSlayerTimer timer_12rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_END_5, 5000);
					timer_12rd.begin();
					break;
				case STATUS_DRAGONSLAYER_END_5:
					if (portalPack()[_num] != null) {
						portalPack()[_num].setStatus(ActionCodes.ACTION_Die);
						portalPack()[_num].broadcastPacket(new S_DoActionGFX(portalPack()[_num].getId(), ActionCodes.ACTION_Die));
						portalPack()[_num].deleteMe();
					}
					resetDragonSlayer(_num);
					DragonSlayerTimer timer_13rd = new DragonSlayerTimer(_num, STATUS_DRAGONSLAYER_END, 300000);
					timer_13rd.begin();
					break;
				case STATUS_DRAGONSLAYER_END:
					setPortalNumber(_num, false);
					break;
			}
			cancel();
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, _time);
		}
	}

	public void sendMessage(int portalNum, int type, String msg) {
		L1PcInstance[] players = getPlayersArray(portalNum);
		for (L1PcInstance pc : players) {
			if (isDragonSlayer(portalNum, pc)) {
				pc.sendPackets(new S_ServerMessage(type, msg));
			}
		}
	}

	private boolean isDragonSlayer(int portalNum, L1PcInstance pc) {
		boolean isDragonSlayer = false;
		short mapId = (short) (1005 + portalNum);
		if (pc.getMapId() == mapId
				&& portalNum >= 0 && portalNum <= 5 // アンタラスレア
				&& pc.getX() >= 32744 && pc.getX() <= 32812
				&& pc.getY() >= 32659 && pc.getY() <= 32723) {
			isDragonSlayer = true;
		} else if (pc.getMapId() == mapId
				&& portalNum >= 6 && portalNum <= 11 // パプリオンレア
				&& pc.getX() >= 32928 && pc.getX() <= 32993
				&& pc.getY() >= 32812 && pc.getY() <= 32860) {
			isDragonSlayer = true;
		} else if (pc.getMapId() == mapId
				&& portalNum >= 12 && portalNum <= 17 // リンドビオルレア
				&& pc.getX() >= 32828 && pc.getX() <= 32880
				&& pc.getY() >= 32845 && pc.getY() <= 32897) {
			isDragonSlayer = true;
		//} else if (pc.getMapId() == mapId
		//		&& portalNum >= 18 && portalNum <= 23 // ヴァラカスレア(未実装)
		//		&& pc.getX() >= 32808 && pc.getX() <= 32863
		//		&& pc.getY() >= 32748 && pc.getY() <= 32792) {
		//	isDragonSlayer = true;
		}
		return isDragonSlayer;
	}
	
	private String getDragonName(int npcId) {
		L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
		return npc.getNpcTemplate().getName();
	}
	
	public void dropRewardItem(int portalNum, int npcId) {
		short mapId = (short) (1005 + portalNum);
		int dragon[] = new int[] {
			91200, 91201, 91202, // アンタラス
			91514, 91515, 91516, // パプリオン
			91603, 91604, 91605  // リンドビオル
			// ヴァラカス(未実装)
		};
		int reward[] = new int[] {
			50553, 50553, 50556, // 逃げたドラゴンの痕跡、逃げたドラゴンの痕跡、地竜の証
			50553, 50553, 50557, // 逃げたドラゴンの痕跡、逃げたドラゴンの痕跡、地竜の証
			50553, 50553, 50558  // 逃げたドラゴンの痕跡、逃げたドラゴンの痕跡、風竜の証
			// 50553, 50553, 50559 // 逃げたドラゴンの痕跡、逃げたドラゴンの痕跡、火竜の証(未実装)
		};
		int i = Arrays.binarySearch(dragon, npcId);
		L1PcInstance[] players = getPlayersArray(portalNum);
		for (L1PcInstance pc : players) {
			if (isDragonSlayer(portalNum, pc)) {
				L1ItemInstance item = pc.getInventory().storeItem(reward[i], 1);
				String npcName = getDragonName(npcId);
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				// \F2%0%sが%1%oをくれました。
			}
		}
	}

	/**
	 *  レイド終了後のリセット
	 *  
	 */
	public void resetDragonSlayer(int portalNum) {
		short mapId = (short) (1005 + portalNum);

		for (Object obj : L1World.getInstance().getVisibleObjects(mapId).values()) {
			if (obj instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) obj;
				if (pc != null) {
					if (pc.isDead()) {
						reStartPlayer(pc);
					} else {
						pc.setPortalNumber(-1);
						L1Teleport.teleport(pc, 33710, 32521, (short) 4, pc.getHeading(), true);
					}
				}
			} else if (obj instanceof L1DoorInstance) {
				L1DoorInstance door = (L1DoorInstance) obj;
				door.close();
			} else if (obj instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				if ((npc.getMaster() == null)
						&& (npc.getNpcTemplate().getNpcId() < 81301
						&& npc.getNpcTemplate().getNpcId() > 81306)) {
					// TODO 3.52 81301～81306に該当するNPCが存在しない（unknown npc）
					npc.deleteMe();
				}
			} else if (obj instanceof L1Inventory) {
				L1Inventory inventory = (L1Inventory) obj;
				inventory.clearItems();
			}
		}
		for (L1Object obj : L1World.getInstance().getObject()) {
			if (obj.getMapId() == mapId) {
				if (obj instanceof L1NpcInstance) {
					L1NpcInstance npc = (L1NpcInstance) obj;
					if ((npc.getNpcTemplate().getNpcId() < 91506 // 五色のパール、神秘の五色のパール
						&& npc.getNpcTemplate().getNpcId() > 91508) // トルナール
					|| (npc.getNpcTemplate().getNpcId() < 91615 // 雲の大精霊
							&& npc.getNpcTemplate().getNpcId() > 91616)) { // 怪しい暗雲
					((L1NpcInstance) obj).deleteMe();
					}
				}
			}
		}
		setPortalPack(portalNum, null);
		setDragonSlayerStatus(portalNum, STATUS_DRAGONSLAYER_NONE);
		clearPlayerList(portalNum);
	}

	private void reStartPlayer(L1PcInstance pc) {
		pc.stopPcDeleteTimer();

		int[] loc = ReturnLocationTable.getReturnLocation(pc, true);

		pc.removeAllKnownObjects();
		pc.broadcastPacket(new S_RemoveObject(pc));

		pc.setCurrentHp(pc.getLevel());
		pc.setFood(40);
		pc.setDead(false);
		pc.setStatus(0);
		L1World.getInstance().moveVisibleObject(pc, loc[2]);
		pc.setX(loc[0]);
		pc.setY(loc[1]);
		pc.setMap((short) loc[2]);
		pc.sendPackets(new S_MapID(pc.getMapId(), pc.getMap().isUnderwater()));
		pc.broadcastPacket(new S_OtherCharPacks(pc));
		pc.sendPackets(new S_OwnCharPack(pc));
		pc.sendPackets(new S_CharVisualUpdate(pc));
		pc.startHpRegeneration();
		pc.startMpRegeneration();
		pc.sendPackets(new S_Weather(L1World.getInstance().getWeather()));
		if (pc.getHellTime() > 0) {
			pc.beginHell(false);
		}
	}

	public void spawn(int npcId, int portalNumber, int X, int Y, short mapId, int randomRange,
			int timeMillisToDelete) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(IdFactory.getInstance().nextId());
			npc.setMap(mapId);

			if (randomRange == 0) {
				npc.setX(X);
				npc.setY(Y);
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(X + _random.nextInt(randomRange) - _random.nextInt(randomRange));
					npc.setY(Y + _random.nextInt(randomRange) - _random.nextInt(randomRange));
					if (npc.getMap().isInMap(npc.getLocation())
							&& npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.setX(X);
					npc.setY(Y);
				}
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(_random.nextInt(8));
			npc.setPortalNumber(portalNumber);

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			if (npc.getGfxId() == 7585 // ドラゴンポータル(隠された竜の地)
					|| npc.getGfxId() == 7554 // ドラゴンポータル(地)
					|| npc.getGfxId() == 7548 // ドラゴンポータル(水)
					|| npc.getGfxId() == 7552 // ドラゴンポータル(風)
					|| npc.getGfxId() == 7550 // ドラゴンポータル(火)
					|| npc.getGfxId() == 7557 // アンタラスLv1
					|| npc.getGfxId() == 7539 // アンタラスLv2
					|| npc.getGfxId() == 7558 // アンタラスLv3
					|| npc.getGfxId() == 7864 // パプリオンLv1
					|| npc.getGfxId() == 7869 // パプリオンLv2
					|| npc.getGfxId() == 7870 // パプリオンLv3
					|| npc.getGfxId() == 8036 // リンドビオルLv1
					|| npc.getGfxId() == 8056 // リンドビオルLv2
					|| npc.getGfxId() == 8055 // リンドビオルLv3
					// ヴァラカスLv1(未実装)
					// ヴァラカスLv2(未実装)
					// ヴァラカスLv3(未実装)
					) {
				npc.setDelayTime(ActionCodes.ACTION_AxeWalk, L1NpcInstance.ATTACK_SPEED);
				for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(npc)) {
					npc.onPerceive(pc);
					S_DoActionGFX gfx = new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_AxeWalk);
					pc.sendPackets(gfx);
				}
			}

			npc.updateLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
			
			if (0 < timeMillisToDelete) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc,
						timeMillisToDelete);
				timer.begin();
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

/*	*//**
	 * 指定されたロケーションに任意のモンスターを指定数生成する。
	 * （将来、エンチャントルートのMOBをインスタンスマップに自動Spawnさせる準備）
	 * @param loc
	 *            出現位置
	 * @param npcid
	 *            任意のNpcId
	 * @param mobCount
	 *            出現数
	 * @return L1MonsterInstance 戻り値 : 成功=生成したインスタンス 失敗=null
	 *//*
	@SuppressWarnings("unused")
	private L1MonsterInstance spawnMob(L1Location loc, int npcid, int mobCount) {
		final L1MonsterInstance mob = new L1MonsterInstance(NpcTable.getInstance().getTemplate(npcid));
		if (mob == null) {
			_log.warning("mob == null");
			return mob;
		}

		mob.setId(IdFactory.getInstance().nextId());
		mob.setHeading(_random.nextInt(8));
		mob.setX(loc.getX());
		mob.setHomeX(loc.getX());
		mob.setY(loc.getY());
		mob.setHomeY(loc.getY());
		mob.setMap((short) loc.getMapId());
		mob.setStoreDroped(false);
		mob.setUbSealCount(mobCount);

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

	*//**
	 * モンスターをカウントする
	 * （将来、エンチャントルートのMOBをインスタンスマップに自動Spawnさせる準備）
	 * （一般MOB既定カウントでゲートMOBをSpawnさせる為）
	 * @param mobCnt 討伐MOB数
	 *
	 * @return 討伐MOB数
	 *//*
	private int countMonster() {
		int mobCnt = 0;
		for (final L1Object obj : L1World.getInstance().getVisiblePoint(
				getLocation(), 15)) {
			if (obj instanceof L1MonsterInstance) {
				if (!((L1MonsterInstance) obj).isDead()) {
					mobCnt++;
				}
			}
		}
		return mobCnt;
	}*/
}
