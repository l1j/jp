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
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.DoorTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.TrapTable;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1OrimQuest;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1DoorInstance;
import jp.l1j.server.model.instance.L1EffectInstance;
import jp.l1j.server.model.instance.L1FieldObjectInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1TrapInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.trap.L1Trap;
import jp.l1j.server.model.trap.L1WorldTraps;
import jp.l1j.server.packets.server.S_BlackWindow;
import jp.l1j.server.packets.server.S_DisplayClack;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_EffectLocation;
import jp.l1j.server.packets.server.S_GreenMessage;
import jp.l1j.server.packets.server.S_NpcPack;
import jp.l1j.server.packets.server.S_RedMessage;
import jp.l1j.server.packets.server.S_ShockWave;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_Sound;
import jp.l1j.server.packets.server.S_YellowMessage;
import jp.l1j.server.packets.server.ServerBasePacket;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1DoorGfx;
import jp.l1j.server.types.Point;
import jp.l1j.server.utils.IdFactory;

public class L1OrimQuestInstance {
	private short _mapId;		//生成されたインスタンスマップIDを保存
	private int _OrimQuestStatus;//クエストの進行状態を保存
	private boolean _isDeleteTransactionNow;//船の設備が現在使用中
	private boolean _acceptOrder;//ルーン作成要求を受け付け可否
	private int _comeShipCount ;//幽霊船の襲撃があったかどうかを保存
	private boolean _extraShipCome;
	private boolean _isRankSelected;
	private boolean _isCrakenDead;
	private boolean _isLastBossDead;

	private L1NpcInstance _ship;
	private L1NpcInstance _diffence_rune_1;
	private L1NpcInstance _diffence_rune_2;
	private L1NpcInstance _attack_rune;
	private L1NpcInstance _portal_rune;
	private L1NpcInstance _cannon_1;
	private L1NpcInstance _cannon_2;
	private L1NpcInstance _shipWall;
	private L1NpcInstance _craken_tail_left;
	private L1NpcInstance _craken_tail_right;
	private L1MonsterInstance _mimic_A;
	private L1MonsterInstance _mimic_B;
	private L1MonsterInstance _mimic_C;
	private L1DoorInstance _createDoor;
	private L1TrapInstance _moveTrap;
	private int _startPoint;
	private int _endPoint;

	private int[] _roundDiffenceStatus= new int[12];
	private int[] _roundHitDamage= new int[12];//HITが赤まで行かなかった場合、次のラウンドにHIT数が持ち越されます。
	private int[] _roundStatus = new int[12];//各ラウンドの殲滅速度を保存 -1遅い 0変化無し 1早い
	private int[] _bonusRoundStatus = new int[12];//海洋生物の襲撃を保存
	private int _currentRound;
	private int _point;//現在のポイントを保存
	private int _ship_burning;//出火箇所が9箇所になると、船が沈没寸前ですとのメッセージが出て、出火箇所が10箇所になると沈没します。
	private int _tempNpcNumber;
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	void init(){//初期設定を行う
		_cannon_1 = spawnOne(new L1Location(32803,32809,_mapId),91450 ,0);
		_cannon_2 = spawnOne(new L1Location(32792,32809,_mapId),91451 ,0);
		_cannon_1.getMap().setPassable(32803, 32809, false);
		_cannon_2.getMap().setPassable(32792, 32809, false);
		_shipWall = spawnOne(new L1Location(32799,32806,_mapId),91449 ,0);
		_shipWall.setStatus(32);
		L1DoorGfx gfx = new L1DoorGfx(2510, 0, 10, -10);//透明
		_createDoor = DoorTable.getInstance().createDoor(0, gfx,
				new L1Location(32798, 32795, _mapId), 0, 0, false);
		spawnOne(new L1Location(32677,32800,_mapId), 91452, 0);//転送ポータル
		spawnOne(new L1Location(32677,32864,_mapId), 91452, 0);//転送ポータル
		spawnOne(new L1Location(32741,32860,_mapId), 91452, 0);//転送ポータル
		spawnOne(new L1Location(32805,32862,_mapId), 91452, 0);//転送ポータル
		setSwitch(new L1Location(32677,32800,_mapId), 74);//転送ポータル
		setSwitch(new L1Location(32677,32864,_mapId), 74);//転送ポータル
		setSwitch(new L1Location(32741,32860,_mapId), 74);//転送ポータル
		setSwitch(new L1Location(32805,32862,_mapId), 74);//転送ポータル
	}

	public static final int _STATUS_ORIMQUEAT_NONE = 0;
	public static final int _STATUS_ORIMQUEAT_READY_1 = 1;
	public static final int _STATUS_ORIMQUEAT_READY_2= 2;
	public static final int _STATUS_ORIMQUEAT_READY_3 = 3;
	public static final int _STATUS_ORIMQUEAT_READY_4 = 4;
	public static final int _STATUS_ORIMQUEAT_READY_5 = 30;
	public static final int _STATUS_ORIMQUEAT_READY_6 = 31;
	public static final int _STATUS_ORIMQUEAT_READY_7 = 32;
	public static final int _STATUS_ORIMQUEAT_READY_8 = 33;
	public static final int _STATUS_ORIMQUEAT_READY_8_1 = 50;
	public static final int _STATUS_ORIMQUEAT_READY_8_2 = 51;
	public static final int _STATUS_ORIMQUEAT_READY_8_3 = 52;
	public static final int _STATUS_ORIMQUEAT_READY_9 = 34;
	public static final int _STATUS_ORIMQUEAT_READY_10 = 35;
	public static final int _STATUS_ORIMQUEAT_READY_11 = 36;
	public static final int _STATUS_ORIMQUEAT_READY_11_1 = 37;
	public static final int _STATUS_ORIMQUEAT_READY_11_2 = 38;

	public static final int _STATUS_ORIMQUEAT_START = 5;
	public static final int _STATUS_ORIMQUEAT_START_2 = 6;
	public static final int _STATUS_ORIMQUEAT_START_2_1 = 7;
	public static final int _STATUS_ORIMQUEAT_START_2_2 = 8;
	public static final int _STATUS_ORIMQUEAT_START_2_3 = 9;
	public static final int _STATUS_ORIMQUEAT_START_2_4 = 10;
	public static final int _STATUS_ORIMQUEAT_START_3 = 11;
	public static final int _STATUS_ORIMQUEAT_START_3_1 = 12;
	public static final int _STATUS_ORIMQUEAT_START_3_2 = 13;
	public static final int _STATUS_ORIMQUEAT_START_3_3 = 14;
	public static final int _STATUS_ORIMQUEAT_END_1 = 15;
	public static final int _STATUS_ORIMQUEAT_END_2 = 16;
	public static final int _STATUS_ORIMQUEAT_END_3 = 17;
	public static final int _STATUS_ORIMQUEAT_END_4 = 18;
	public static final int _STATUS_ORIMQUEAT_END_5 = 19;
	public static final int _STATUS_ORIMQUEAT_END_6 = 21;
	public static final int _STATUS_ORIMQUEAT_END = 20;

	public static final int _STATUS_MOVE_SHIP_1 = 99;
	public static final int _STATUS_MOVE_SHIP_2 = 98;
	public static final int _STATUS_MOVE_SEA_MONSTER = 100;
	public static final int _STATUS_ORIMQUEST_MONITOR = 1000;
	public static final int _STATUS_SPAWN_MONSTERS = 10001;
	public static final int _STATUS_CREATE_RUNES = 10002;
	public static final int _STATUS_ENEMY_CANON = 10003;
	public static final int _STATUS_FIRE_CANON_1 = 10004;
	public static final int _STATUS_FIRE_CANON_2 = 10005;

	static final int STATUS_ORIMQUEAT_LIMIT = 25;
	static final int STATUS_ORIMQUEAT_LIMIT2 = 26;
	static final int STATUS_ORIMQUEAT_LIMIT3 = 27;

	public boolean _IS_SPAWN_SHIP_TYPEA ;
	public boolean _IS_SPAWN_SHIP_TYPEB ;
	public boolean _IS_SPAWN_SHIP_TYPEC ;

	public static final int STATUS_NONE = 0;
	public static final int STATUS_READY_SPAWN = 1;
	public static final int STATUS_SPAWN = 2;

	private int _action;//ソーシャルアクションの値を設定する。

	private int[] _pointMessages = {10638,10639,10640,10641,10642,
			10643,10644,10645,10646,10647,10648,10649,10666,10667,10668,10669,10670,10671,10672,
			10673,10674,10675,10676,10677,10678,10679,10680,10681,10682,10683,10684,10685,10686};

	private int[] _monstersA = {	91278,	//魔界の サラマンダー
									91273,	//魔界の ケルベロス
									91459,	//魔界の アシタジオ
									91457,	//魔界の ヘルバウンド
									91456,	//魔界の ホーン ケルベロス
									91458,	//魔界の ラヴァ ゴーレム
									91460,	//魔界の ミノタウルス
									91464,	//魔界の ミノタウルス
									91461,	//魔界の マイノ
									91280,	//魔界の サキュバス
									91462,	//魔界の ガーゴイル
									91463,	//魔界の スピリッド
									91495,	//魔界の レッサードラゴン
									91292,	//魔界の グレート ミノタウルス
									91465,	//魔界の コラプト プリースト
									91466,	//魔界の コラプト プリースト
									91467,	//魔界の コラプト プリースト
									91468,	//魔界の コラプト プリースト
									91469,	//魔界の コラプト プリースト
									91470	//魔界の カオス プリースト
								};
	private int[] _monstersB = {	45374,	//アビス リザードマン
									45825,	//ヴァリアント リザードマン
									45827,	//ヴァリアント リザードマン
									45874,	//ヴァリアント リザードマン
								};
	private int[] _monstersC = {	46057,	//ラバーボーン アーチャー
									46056,	//ラバーボーン ソルジャー
									46059,	//ラバーボーン ナイフ
									46058,	//ラバーボーン ヘッド
									45502	//アビス アーチャー
								};

	public L1OrimQuestInstance(short mapId) {
		setMapId(mapId);
		init();//初期化
	}

	/**
	 * オリムクエストを開始する
	 */
	public void start() {
		if(_STATUS_ORIMQUEAT_NONE  == _OrimQuestStatus){
			OrimQuestTimer timer_start = new OrimQuestTimer(
					_STATUS_ORIMQUEAT_NONE, 0);
			timer_start.begin();
		}
	}

	/**
	 * スレッドプロシージャ
	 */
	class OrimQuestTimer extends TimerTask {
		int _time;//開始までの時間
		int _order;//処理区分
		OrimQuestTimer(int order,int time){
			_time= time;
			_order= order;
		}

		public void begin() {
			final Timer timer = new Timer();
			timer.schedule(this, _time);
		}

		@Override
		public void run() {
			if(!_isDeleteTransactionNow){//削除処理が開始されているならば全てのオーダーを受け付けない
				switch (_order) {
					/**********			開始前の準備段階	START		**************/
					case _STATUS_ORIMQUEAT_NONE:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_1);//クエスト状態を準備に変更
						OrimQuestTimer timer_1 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_READY_1, 12000);
						timer_1.begin();
						//マップ監視用のモニターを起動
						OrimQuestTimer monitor = new OrimQuestTimer(
									_STATUS_ORIMQUEST_MONITOR,30000);
						monitor.begin();
					break;
					case _STATUS_ORIMQUEAT_READY_1:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_2);
						sendMessage("$9529", 0); // GreenMessage オリム：皆さん落ち着いて聞いて
						OrimQuestTimer timer_2 = new OrimQuestTimer(
								_STATUS_ORIMQUEAT_READY_2, 8000);
						timer_2.begin();
					break;
					case _STATUS_ORIMQUEAT_READY_2:
						_action = 0;//ソーシャルアクションを確実にクリアにする。
						setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_3);
						sendMessage("$9530", 0); // GreenMessage オリム：説明がいらないなら
						OrimQuestTimer timer_3 = new OrimQuestTimer(
								_STATUS_ORIMQUEAT_READY_3, 8000);
						timer_3.begin();
					break;
					case _STATUS_ORIMQUEAT_READY_3:
						int action = _action ;
						_action = 0;//ソーシャルアクションを確実にクリアにする。

						if(action == 68){//ステップを一気に引き上げる
							setOrimQuestStatus(_STATUS_ORIMQUEAT_START);
							OrimQuestTimer timer_start1 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_START, 8000);
							timer_start1.begin();
						}else{
							//説明処理を開始
							setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_4);
							sendMessage("$9531", 0);  // GreenMessage われわれの船を攻撃しているのはオールディンというものです
							OrimQuestTimer timer_4 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_READY_4, 8000);
							timer_4.begin();
						}
					break;
					case _STATUS_ORIMQUEAT_READY_4:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_5);
						sendMessage("$9532", 0); // GreenMessageオリム：詳しい話は生き残ってからにしましょう。
						OrimQuestTimer timer_5 = new OrimQuestTimer(
								_STATUS_ORIMQUEAT_READY_5, 8000);
						timer_5.begin();
					break;
					case _STATUS_ORIMQUEAT_READY_5:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_6);
						sendMessage("$9533", 0); // GreenMessage まずは生き残ることです。大砲の使い方から
						OrimQuestTimer timer_6 = new OrimQuestTimer(
								_STATUS_ORIMQUEAT_READY_6, 8000);
						timer_6.begin();
					break;
					case _STATUS_ORIMQUEAT_READY_6:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_7);
						sendMessage("$9534", 0); // GreenMessage 戦いが始まると床に赤い魔法陣が現れます。
						_attack_rune = spawnOne(L1Location.randomLocation(new L1Location(32799,32803,_mapId), 1, 6, false), 91454, 5);
						OrimQuestTimer timer_7 = new OrimQuestTimer(
								_STATUS_ORIMQUEAT_READY_7, 8000);
						timer_7.begin();
					break;
					case _STATUS_ORIMQUEAT_READY_7:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_8);
						sendMessage("$9535", 0); // GreenMessage そしてパーティーリーダーは大砲の間にある同じ模様の足場の上へ立ってください
						OrimQuestTimer timer_8 = new OrimQuestTimer(
								_STATUS_ORIMQUEAT_READY_8, 8000);
						timer_8.begin();
					break;
					case _STATUS_ORIMQUEAT_READY_8:
						_action = 0;//ソーシャルアクションを確実にクリアにする。
						setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_8_1);
						sendMessage("$9536", 0); // GreenMessage その状態で[alt+4]を押すと、大砲から弾が発射されます。
						OrimQuestTimer timer_8_1 = new OrimQuestTimer(
								_STATUS_ORIMQUEAT_READY_8_1, 8000);
						timer_8_1.begin();
					break;
					case _STATUS_ORIMQUEAT_READY_8_1:
						if(_attack_rune == null){//砲弾が発射されて初期化済み
							setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_9);
							OrimQuestTimer timer_9 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_READY_9, 8000);
							timer_9.begin();
						}else{
							setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_8_2);
							sendMessage("$9545", 0); // GreenMessage 時間がありません急いでください。
							OrimQuestTimer timer_8_2 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_READY_8_2, 8000);
							timer_8_2.begin();
						}

					break;
					case _STATUS_ORIMQUEAT_READY_8_2:
						if(_attack_rune == null){//砲弾が発射されて初期化済み
							setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_9);
							OrimQuestTimer timer_9 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_READY_9, 8000);
							timer_9.begin();
						}else{
							setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_9);
							sendMessage("$9546", 0); // GreenMessage 的を逃してしまったようです・・・
							OrimQuestTimer timer_9 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_READY_9, 8000);
							timer_9.begin();
						}

					break;
					case _STATUS_ORIMQUEAT_READY_9:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_10);
						sendMessage("$9537", 0); // GreenMessage オリム：しかし、敵も攻撃を仕掛けてくるため防御も必要です。
						OrimQuestTimer timer_10 = new OrimQuestTimer(
								_STATUS_ORIMQUEAT_READY_10, 8000);
						timer_10.begin();
					break;
					case _STATUS_ORIMQUEAT_READY_10:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_11);
						sendMessage("$9538", 0); // GreenMessage オリム：防御のためには、黄色い魔法陣の中に入ってください。
						_diffence_rune_1 = spawnOne(L1Location.randomLocation(new L1Location(32799,32803,_mapId), 1, 6, false), 91453, 5);
						_diffence_rune_2 = spawnOne(L1Location.randomLocation(new L1Location(32799,32803,_mapId), 1, 6, false), 91453, 5);
						OrimQuestTimer timer_11 = new OrimQuestTimer(
								_STATUS_ORIMQUEAT_READY_11, 8000);
						timer_11.begin();
					break;
					case _STATUS_ORIMQUEAT_READY_11:
						_action = 0;//ソーシャルアクションを確実にクリアにする。
						setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_11_1);
						sendMessage("$9539", 0); // GreenMessage オリム：その状態で[alt+2]を押すと、私が防御の呪文を詠唱します。
						OrimQuestTimer timer_11_1 = new OrimQuestTimer(
								_STATUS_ORIMQUEAT_READY_11_1, 8000);
						timer_11_1.begin();
					break;

					case _STATUS_ORIMQUEAT_READY_11_1:
						if(_diffence_rune_1 == null && _diffence_rune_2 == null ){
							setOrimQuestStatus(_STATUS_ORIMQUEAT_START);
							OrimQuestTimer timer_start = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_START, 8000);
							timer_start.begin();
						}else{
							setOrimQuestStatus(_STATUS_ORIMQUEAT_READY_11_2);
							sendMessage("$9545", 0); // GreenMessage 時間がありません急いでください。
							OrimQuestTimer timer_11_2 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_READY_11_2, 8000);
							timer_11_2.begin();
						}

					break;
					case _STATUS_ORIMQUEAT_READY_11_2:
						if(_diffence_rune_1 == null && _diffence_rune_2 == null ){
							setOrimQuestStatus(_STATUS_ORIMQUEAT_START);
							OrimQuestTimer timer_start = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_START, 8000);
							timer_start.begin();
						}else{
							setOrimQuestStatus(_STATUS_ORIMQUEAT_START);
							sendMessage("$9546", 0); // GreenMessage 的を逃してしまったようです・・・
							OrimQuestTimer timer_start = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_START, 8000);
							timer_start.begin();
						}

					break;
					/**********			開始前の準備段階	END		**************/

					case _STATUS_ORIMQUEAT_START://船 vs 船 通常ラウンドでは3回に分けてMob出現
						clearAllRune();//RUNEを初期化
					try {
						sendMessage("$9540", 0);// GreenMessage さあ敵の攻撃に備えましょう。急いで！
						for(int round=0;round<12;round++){//4.8.12ラウンドでは1回のみボス出現、MOBも一緒に出現。
							_currentRound = round;//
							Thread.sleep(3000);
							if(round==0){
								sendMessage("$9603", 0); //9603	オリム：船のどこかに隠れているモンスターがいます！倒してください！
								sendPackets(new S_DisplayClack());
								sendPackets(new S_Sound(82));
								OrimQuestTimer spawn = new OrimQuestTimer(
										_STATUS_SPAWN_MONSTERS, 0);
								spawn.begin();
								Thread.sleep(35000);
								int monsterCount = checkAliveMonster();
								if(monsterCount >= 5 && monsterCount <= 7){//EASYMODE
									_startPoint = 0;
									_endPoint = 5;
								}else if(monsterCount >= 3 && monsterCount <= 4){//A LITTLE EASYMODE
									_startPoint = 2;
									_endPoint = 7;
								}else if(monsterCount == 2){//スーパーハード
									_startPoint = 10;
									_endPoint = 19;
								}else if(monsterCount == 1){//ハードモード
									_startPoint = 8;
									_endPoint = 12;
								}else if(monsterCount == 0){//ノーマルモード
									_startPoint = 5;
									_endPoint = 9;
								}

							}
							//9588	オリム：前より強い気が感じられます！気をつけてください！
							//9589	オリム：前より弱い気が感じられます。だが、気をつけてください！
							Thread.sleep(5000);
							sendMessage("$9548", 0);//9548	オリム：このあたりに何かが漂っているようです。気を付けてください。
							int i = _random.nextInt(4);
							_bonusRoundStatus[round]=i;//海洋生物の襲来を保存
							Thread.sleep(5000);
							if(i==0){//サメ [座標固定]
								L1NpcInstance temp = spawnOne(new L1Location(32803,32788,_mapId),91445,4);
								temp.broadcastPacket(new S_DoActionGFX(temp.getId(), 0));
								sendMessage("$", 0);//9541	オリム：周囲にサメの群れがいます。気をつけて！
								deleteNpc(temp);
							}else{
								L1NpcInstance temp = null;
								if(i==1){//シーハーピー [ランダム移動]
									int rd = _random.nextInt(4)+3;
									_tempNpcNumber = 91483;
									for(int z = 0;z<rd;z++){
										OrimQuestTimer timer = new OrimQuestTimer(
												_STATUS_MOVE_SEA_MONSTER, 0);
										timer.begin();
									}
									sendMessage("$9542", 0);//9542	オリム：すぐそばにシーハーピーの棲み処があるようです。気をつけて！
								}else if(i==2){//シードレイク [ランダム移動]
									int rd = _random.nextInt(4)+3;
									_tempNpcNumber = 91482;
									for(int z = 0;z<rd;z++){
										OrimQuestTimer timer = new OrimQuestTimer(
												_STATUS_MOVE_SEA_MONSTER, 0);
										timer.begin();
									}
									sendMessage("$9543", 0);//9543	オリム：シードレイクですね。珍しいからってあまり近寄ってはいけませんよ！
								}else if(i==3){//クラーケン [座標固定]
									temp = spawnOne(new L1Location(32800,32794,_mapId),91494,4);
									temp.broadcastPacket(new S_DoActionGFX(temp.getId(), 0));
									deleteNpc(temp);
									sendMessage("$9544", 0);//9544	オリム：あの気味悪いやつは、どこか危なそうです。
								}
							}
							Thread.sleep(10000);
							//点数出力
							if(i!=0){
								if(checkSeaMonsterAttack(i)){//trueが帰ってくれば海洋生物の襲撃と認識
									if(i==3){//クラーケン 他のMOBとの共闘を許可しない。
										if(_createDoor != null){
											_createDoor.open();
										}
										Thread.sleep(10000);
										sendMessage("$10720", 1);//10720	一分以内に倒せれば･･･
										long startTime = System.currentTimeMillis();
										while(!_isCrakenDead && !_isDeleteTransactionNow){//削除処理中ではなく
											Thread.sleep(3000);
										}
										if(System.currentTimeMillis()-startTime <= 63000){//60秒以内の討伐
											//報酬の出現
											spawnOneMob(new L1Location(32798,32807,_mapId), 91440);
										}
										if(_createDoor != null){
											_createDoor.close();
										}
										Thread.sleep(10000);
									}
								}
							}
							sendMessage("$"+_pointMessages[_point/100],1);

							Thread.sleep(10000);
							if(round != 11){//最終ラウンド
								if(round > 0 && (round+1)%4 == 0){//ラウンドが4の倍数
									sendMessage("$9554", 0);//	9554	オリム：気をつけてください！今度の船は他よりも強い気が感じられます。
								}else{
									//敵船接近につき警告
									sendMessage("$9550",0);//9550	オリム：敵の船が近づいています！攻撃に備えてください！
								}
								//ポータルらしきもの出現 両方
								_acceptOrder = true;
								createRune();
							}else{
								sendMessage("$9556",0);//9556	オリム：ウウウ…今度の船からはオールディンの気が感じられます！
							}

							Thread.sleep(20000);
							//20秒経過
							if(round != 11){
								sendMessage("$9551",0);//9551	オリム：船同士がまもなく衝突します！海へ落ちないように気をつけてください！
							}
							//さらに20秒経過=衝突※ここまでデッキでの操作が可能

							OrimQuestTimer move_ship = new OrimQuestTimer(
									_STATUS_MOVE_SHIP_1, 0);
							move_ship.begin();

							Thread.sleep(20000);
							//10秒後
							sendMessage("$"+(9609+round),1);//9609	戦闘が始まります。(?/12)
							Thread.sleep(3000);
							sendPackets(new S_DisplayClack());
							sendPackets(new S_Sound(82));
							Thread.sleep(3000);
							//8~10秒後
							if(!(round == 11)){//最終ラウンドではない
								for(int subRound = 0;subRound<4;subRound++){
									if(subRound == 3){//最終サブラウンドならばオールディンに捕まる
										if(_mimic_A == null && _mimic_B == null &&_mimic_C == null ){//呪術ミミックを設置
											sendMessage("$9608",2);//9608	オールディン：あいつを捕まえろ！
											sendPackets(new S_BlackWindow());
											int[][] templocation = {{32741,32800},
													{32732,32798},
													{32733,32807}
													};
											int rndval = _random.nextInt(3);
											_mimic_A = spawnOneMob(new L1Location(templocation[0][0],templocation[0][1],_mapId),91455);
											_mimic_B = spawnOneMob(new L1Location(templocation[1][0],templocation[1][1],_mapId),91455);
											_mimic_C = spawnOneMob(new L1Location(templocation[2][0],templocation[2][1],_mapId),91455);
											_mimic_A.getInventory().storeItem(50061, 1);
											_mimic_B.getInventory().storeItem(50061, 1);
											_mimic_C.getInventory().storeItem(50061, 1);

											if(rndval==0){
												_mimic_A.setCurseMimic(true);
											}else if(rndval==1){
												_mimic_B.setCurseMimic(true);
											}else if(rndval==2){
												_mimic_C.setCurseMimic(true);
											}
											//誰かテレポートで連れて行かれる
											ArrayList<L1PcInstance> pclist = L1World.getInstance().getVisiblePlayer(_shipWall, 15);
											Collections.shuffle(pclist);
											for(L1PcInstance pc : pclist){
												Thread.sleep(3000);
												L1Teleport.teleport(pc, 32735,32802,_mapId, 1, false);
												break;
											}
											sendMessage("$9606",1);//9606	オリム：早く脱出してください。足止めしているモンスターがどこかにいるはずです。
										}
										OrimQuestTimer moveShip = new OrimQuestTimer(_STATUS_MOVE_SHIP_2, 0);
										moveShip.begin();
									}else{
										if(subRound==1){
											int countMonster = checkAliveMonster();
											if(countMonster >= 5){
												_roundStatus[round]=-1;//遅い
											}else if(countMonster >= 2){//
												_roundStatus[round]=0;//通常スピード
											}else if(countMonster >= 0){//４以上
												_roundStatus[round]=1;//早い
											}
										}
										spawnMonsters();
										 if(subRound==0){
											 if((_currentRound == 3 || _currentRound == 7) && _portal_rune == null){//通常敵船：堕落 ＜ リッチ ＜ ゼニス クイーン
												int npcId = 0;
												if(_point <= 300){//堕落
													npcId = 91480;
												}else if(_point <= 600){//リッチ
													npcId = 91476;
												}else if(_point > 600){//ゼニスクイーン
													npcId = 91478;
												}
												spawnOneMob(L1Location.randomLocation
														(new L1Location(32799,32803,_mapId),
																1, 6, false), npcId);
											 }
										 }
										if(_roundStatus[round] == 1){
											sendMessage("$9560",2);//9560	オリム：戦いに合流してください。敵部隊を全滅させないと、生き残れません！
										}else{
											sendMessage("$"+(9563+subRound),1);//9563	?つ目の部隊が乱入しました。
										}
									}
									Thread.sleep(30000);
									//sleep 30秒(食い残しで35秒延長)
								}
								while(checkAliveMonster() >= 4){//4以上
									_ship_burning++;
									sendPackets(new S_ShockWave());
									L1NpcInstance fire = spawnOne(L1Location.randomLocation(new L1Location(32799,32803,_mapId), 1, 6, false), 91487, 5);
									fire.broadcastPacket(new S_SkillSound(fire.getId(),762));
									if(_ship_burning >= 10){
										sendMessage("$9562",1);//9562	オリム：船が沈没します！急いで脱出してください！この状態では危険です！
										Thread.sleep(35000);
										outPushPlayer();
										return;
									}else if(_ship_burning == 9){
										sendMessage("$9587",1);//9587	オリム：船が沈没する直前です。なんとかして船を死守しなければなりません！
									}else{
										sendMessage("$9561",1);//9561	オリム：乱入した敵が我々の船を壊しにかかっています！急いで倒して！
									}

									Thread.sleep(35000);
								}
							}else{//最終ラウンド
								spawnMonsters();
								spawnOneMob(L1Location.randomLocation(new L1Location(32799,32803,_mapId), 1, 6, false),91471 );//オールディン
								long time = System.currentTimeMillis();
								int opt = 0;
								Thread.sleep(3000);
								while(!_isLastBossDead && !_isDeleteTransactionNow){
									opt += 3000;
									if(opt >= 30000){
										opt = 0;
										//船の破壊を開始
										_ship_burning++;
										sendPackets(new S_ShockWave());
										L1NpcInstance fire = spawnOne(L1Location.randomLocation(new L1Location(32799,32803,_mapId), 1, 6, false), 91487, 5);
										fire.broadcastPacket(new S_SkillSound(fire.getId(),762));
										if(_ship_burning >= 10){
											sendMessage("$9562",1);//9562	オリム：船が沈没します！急いで脱出してください！この状態では危険です！
											Thread.sleep(35000);
											outPushPlayer();
											return;
										}else if(_ship_burning == 9){
											sendMessage("$9587",1);//9587	オリム：船が沈没する直前です。なんとかして船を死守しなければなりません！
										}else{
											sendMessage("$9561",1);//9561	オリム：乱入した敵が我々の船を壊しにかかっています！急いで倒して！
										}
									}
									Thread.sleep(3000);
								}
								OrimQuestTimer end_1 = new OrimQuestTimer(
								_STATUS_ORIMQUEAT_END_1, 0);
								end_1.begin();
							}
						}
					} catch (InterruptedException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
					break;
					/*9557	オリム：敵が乱入してきます！攻撃に備えてください！
					9547	オリム：この戦力ではどうしようもありません。全員逃げてください。




					9585	オリム：残りわずかです。もう少しがんばってください！
					9586	オールディン：その程度か… この船は私のものだな。

					9604	オリム：我々の船が攻撃されています。魔法陣に入ってください。
						 */
	/**
		 * すべてのボスは、その時点での敵のレベル(こちらの点数)によって変化します。

		海賊船：ホセ ＜ ディエゴ ＜ ドレイク船長
		幽霊船：デジェネレイト ソウル（モンスター） ＜ ゾンビ ロード ＜ エルモア ゾンビ マーシャル
		味方船に乗り込んでくるボス：リッチ ＜ ゼニス クイーン


		12ラウンドのボス：バフォメット ＜ オールディン＜ 4賢者(セマ・カスパー・バルタザール・メルキオール) ＜ フード未着用セマ+3賢者
		※．海賊船、幽霊船の場合、出現ボスの違いでそのラウンドに出現する敵モンスターの種類も変わります。
		11ラウンド終了時点で1900点以上でフード未着用セマ+3賢者出現（未確認）
	 */



					//**********			襲撃の段階	END		**************//
					/**********			終了の準備段階	START		**************/
					case _STATUS_ORIMQUEAT_END_1:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_END_2);//クエスト状態を準備に変更
						sendMessage("$9579", 0);//9579	オリム：もはやあれこれ説明する必要はないでしょう…
						spawnOne(new L1Location(1,1,_mapId),91455,0);//呪術ミミック 全員に?段階の箱をくれる
						OrimQuestTimer end_2 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_END_2, 12000);
						end_2.begin();
					break;
					case _STATUS_ORIMQUEAT_END_2:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_END_3);//クエスト状態を準備に変更
						sendMessage("$9580", 0);//9580	オリム：今頃、話せる島はやつらの手で廃虚になっていることでしょう。
						OrimQuestTimer end_3 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_END_3, 12000);
						end_3.begin();
					break;
					case _STATUS_ORIMQUEAT_END_3:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_END_4);//クエスト状態を準備に変更
						sendMessage("$9581", 0);//9581	オリム：戻ったところで、思い出の場所はすべて跡形もなく消えてしまったはず…。
						OrimQuestTimer end_4 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_END_4, 12000);
						end_4.begin();
					break;
					case _STATUS_ORIMQUEAT_END_4:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_END_5);//クエスト状態を準備に変更
						sendMessage("$9582", 0);//9582	オリム：戦いは始まったばかりです。私は師にお会いしてこの戦いを終わらせる方法を探ってみます。
						OrimQuestTimer end_5 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_END_5, 12000);
						end_5.begin();
					break;
					case _STATUS_ORIMQUEAT_END_5:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_END_6);//クエスト状態を準備に変更
						sendMessage("$9583", 0);//9583	オリム：皆さん、どうか生き残ってください。この戦いが終わるまで…
						OrimQuestTimer end_6 = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_END_6, 12000);
						end_6.begin();
					break;
					case _STATUS_ORIMQUEAT_END_6:
						setOrimQuestStatus(_STATUS_ORIMQUEAT_END);//クエスト状態を準備に変更
						sendMessage("$9584", 0);//9584	オリム：もうちょっとでグルーディン領地です！早くこの危機を人々に知らせなければなりません！
						OrimQuestTimer end = new OrimQuestTimer(
									_STATUS_ORIMQUEAT_END, 12000);
						end.begin();
					break;
					case _STATUS_ORIMQUEAT_END://本来ならばランキング画面が表示される
						//全てのプレイヤーを排出
						outPushPlayer();
					break;
					/**********			終了の準備段階	END		**************/
					case _STATUS_MOVE_SHIP_1:
					try {
						int totalShipDamage = 0;
						int shipId = 0;
						L1Location loc = new L1Location(32794+_random.nextInt(8),32831,_mapId);

						int typeA = 0;//ホセ		海賊船
						int typeB = 0;//ディエゴ	海賊船
						int typeC = 0;//ドレイク	幽霊船

						for(int i=0;i < 12;i++){
							if(i < 4){//4ラウンドまでの統計を取る
								typeA+= _roundHitDamage[i];
							}else if(i >= 4 && 7 >= i){
								typeB+= _roundHitDamage[i];
							}else if(i >= 8 && 10 >= i){
								typeC+= _roundHitDamage[i];
							}
							totalShipDamage += _roundHitDamage[i];
						}

						if((_currentRound == 2 || _currentRound == 3)&& !_IS_SPAWN_SHIP_TYPEA
								&& typeA >= 9){//幽霊船A
							_IS_SPAWN_SHIP_TYPEA = true;
							_ship = spawnOne(loc,91441,0);
						}else if((_currentRound == 6 || _currentRound == 7)&& !_IS_SPAWN_SHIP_TYPEB
								&& typeB >= 9){//幽霊船B
							_IS_SPAWN_SHIP_TYPEB = true;
							_ship = spawnOne(loc,91441,0);
						}else if((_currentRound == 10)&& !_IS_SPAWN_SHIP_TYPEC
								&& typeC >= 9){//幽霊船C
							_IS_SPAWN_SHIP_TYPEC = true;
							_ship = spawnOne(loc,91442,0);
						}else{
							int shipDamage = 0;
							if(_currentRound >= 0 && _currentRound <= 3){
								shipDamage = typeA;
							}else if(_currentRound >= 4 && _currentRound <= 7){
								shipDamage = typeB;
							}else if(_currentRound >= 8 && _currentRound <= 11){
								shipDamage = typeC;
							}
							if(shipDamage>=9){//24以上
								shipId = 91448;
							}else if(shipDamage>=6){//12以上
								shipId = 91447;
							}else{
								shipId = 91446;
							}
							_ship = spawnOne(loc,shipId,0);
						}
						if(_ship!=null){
							while (_ship.moveDirection(loc.getX(),loc.getY()-17) != -1) {
								if (_ship.getLocation().getLineDistance(
										new Point(loc.getX(),loc.getY()-17)) != 0) {
									_ship.setDirectionMove(_ship.moveDirection(
											loc.getX(),loc.getY()-17));
									Thread.sleep(_ship.getMoveSpeed());
								}else{
									break;
								}
							}
							sendShipDamage();
							sendPackets(new S_ShockWave());
							_acceptOrder = false;
							clearAllRune();
							if(_ship.getNpcId()==91441){
								sendMessage("$9553",0);//9553	オリム：今度は幽霊船！息つく暇もないのか！
							}else if(_ship.getNpcId()==91442){
								sendMessage("$9552",0);//9552	オリム：ええっ！ドレイク船長の船だって！気をつけてください！手強いですよ！
							}else if(_currentRound == 11){

							}else{
								sendMessage("$9555",0);//9555	オリム：船同士が衝突すれば、敵が飛び込んで来ますよ！気をつけてください！
							}

							Thread.sleep(2000);//2秒待機
							if(_ship.getNpcId()==91441 || _ship.getNpcId()==91442){//海賊船ならば乗り込み発生
								sendMessage("$9559",1);	//オリム：敵船の内部に通じるポータルです！気をつけてください。中から強い気が感じられます。
								if(_portal_rune == null){
									_portal_rune = spawnOne(new L1Location(32799,32809,_mapId),91452,0);
								}
								if(_moveTrap == null){
									if(_ship.getNpcId()==91442){
										_moveTrap = setSwitch(new L1Location(32799,32809,_mapId), 73);
									}else{
										_moveTrap = setSwitch(new L1Location(32799,32809,_mapId), 72);
										for(L1Object obj:L1World.getInstance().getVisiblePoint( new L1Location(32741,32860,_mapId), 15)){
											if(obj instanceof L1ItemInstance){
												L1Inventory groundInventory = L1World
														.getInstance().getInventory(obj.getX(),
																obj.getY(), obj.getMapId());
												groundInventory.deleteItem((L1ItemInstance) obj);
												L1World.getInstance().removeVisibleObject(obj);
												L1World.getInstance().removeObject(obj);
											}else if(obj instanceof L1MonsterInstance){
												obj.getMap().setPassable(obj.getX(), obj.getY(), true);
												((L1MonsterInstance) obj).deleteMe();
											}
										}

									}
								}
							}else if(_currentRound==3){//通常敵船：堕落 ＜ リッチ ＜ ゼニス クイーン
								int npcId = 0;
								if(_point <= 300){//堕落
									npcId = 91480;
								}else if(_point <= 600){//リッチ
									npcId = 91476;
								}else if(_point > 600){//ゼニスクイーン
									npcId = 91478;
								}
								if(typeA >= 12){
									spawnOneMob(new L1Location(32671,32802,_mapId), npcId);
									if(_portal_rune == null){
										_portal_rune = spawnOne(new L1Location(32799,32809,_mapId),91452,0);
									}
									if(_moveTrap == null){
										_moveTrap = setSwitch(new L1Location(32799,32809,_mapId), 70);
									}
								}
							}else if(_currentRound==7){//通常敵船：堕落 ＜ リッチ ＜ ゼニス クイーン
								int npcId = 0;
								if(_point <= 300){
									npcId = 91480;
								}else if(_point <= 600){
									npcId = 91476;
								}else if(_point > 600){
									npcId = 91478;
								}
								if(typeB >= 12){
									spawnOneMob(new L1Location(32671,32866,_mapId), npcId);
									if(_portal_rune == null){
										_portal_rune = spawnOne(new L1Location(32799,32809,_mapId),91452,0);
									}
									if(_moveTrap == null){
										_moveTrap = setSwitch(new L1Location(32799,32809,_mapId), 71);
									}
								}
							}
							//xyz
							//abc
							//x=boss 32671 32802	32677 32800
							//a=32671 32866			32677 32864
							//b=docro 32735 32862	32741 32860
							//c=kaizoku 32799 32863	32805 32862

							while (_ship.moveDirection(loc.getX(),loc.getY()-13) != -1) {
								if (_ship.getLocation().getLineDistance(
										new Point(loc.getX(),loc.getY()-13)) != 0) {
									_ship.setDirectionMove(_ship.moveDirection(
											loc.getX(),loc.getY()-13));
									Thread.sleep(_ship.getMoveSpeed());
								} else {
									break;
								}
							}
						}
					} catch (InterruptedException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
					break;
					case _STATUS_MOVE_SHIP_2:
						if(_ship != null){
							try {
								if(_portal_rune != null){
									_portal_rune.deleteMe();
									_portal_rune = null;
								}
								if(_moveTrap != null){
									L1WorldTraps.getInstance().removeTrap(_moveTrap);
									_moveTrap = null;
								}
								while (_ship.moveDirection(_ship.getX(),_ship.getY()+13) != -1) {
									if (_ship.getLocation().getLineDistance(
											new Point(_ship.getX(),_ship.getY()+13)) != 0) {
										_ship.setDirectionMove(_ship.moveDirection(
												_ship.getX(),_ship.getY()+13));
										Thread.sleep(_ship.getMoveSpeed());
									} else {
										break;
									}
								}
							} catch (InterruptedException e1) {
								// TODO 自動生成された catch ブロック
								e1.printStackTrace();
							}
							deleteNpc(_ship);
							_ship = null;
						}
					break;
					case _STATUS_MOVE_SEA_MONSTER:
					try {
						L1NpcInstance npc =  spawnOne(L1Location.randomLocation(new L1Location(32799,32803,_mapId), 1, 6, false),_tempNpcNumber,_random.nextInt(8));
						Thread.sleep(1000);
						L1Location temploc = new L1Location(npc.getX(),npc.getY(),npc.getMapId());
						temploc.forward(npc.getHeading());
						for(int i = 0;i<40;i++){
							while (npc.moveDirection(temploc.getX(),temploc.getY()) != -1) {
								if (npc.getLocation().getLineDistance(
										new Point(temploc.getX(),temploc.getY())) != 0) {
									npc.setDirectionMove(npc.moveDirection(
											temploc.getX(),temploc.getY()));
									Thread.sleep(npc.getMoveSpeed());
								} else {
									break;
								}
							}
							temploc.forward(npc.getHeading());
						}
						deleteNpc(npc);
					} catch (InterruptedException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
					break;
					case _STATUS_ORIMQUEST_MONITOR :
						boolean flag = false;
						for (L1PcInstance pc  :  L1World.getInstance().getAllPlayers()) {
							if (getMapId() == pc.getMapId()) {
								flag = true;
								break;
							}
						}
						if(flag){
							OrimQuestTimer monitorRepeat = new OrimQuestTimer(
									_STATUS_ORIMQUEST_MONITOR,600000);
							monitorRepeat.begin();
						}else{
							_isDeleteTransactionNow = true;//削除処理へと以降
							reset();
							L1OrimQuest.getInstance().resetActiveMaps(_mapId);//インスタンスマップを解放する。
						}
					break;
					case _STATUS_SPAWN_MONSTERS :
						if(_extraShipCome){
							if(_comeShipCount==0){
								for(int i=0;i<7;i++){
									spawnOneMob(L1Location.randomLocation
											(new L1Location(32799,32803,_mapId), 1, 6, false),_monstersC[_random.nextInt(_monstersC.length)]);
								}
							}else if(_comeShipCount==1){
								for(int i=0;i<7;i++){
									spawnOneMob(L1Location.randomLocation
											(new L1Location(32799,32803,_mapId), 1, 6, false),_monstersC[_random.nextInt(_monstersC.length)]);
								}
							}else if(_comeShipCount==2){
								for(int i=0;i<7;i++){
									spawnOneMob(L1Location.randomLocation
											(new L1Location(32799,32803,_mapId), 1, 6, false),_monstersB[_random.nextInt(_monstersB.length)]);
								}
							}
							_comeShipCount++;
						}else{
							if(_currentRound == 0){
								if(!_isRankSelected){//チュートリアル後に出現するランク決めMOB
									for(int i=0;i<7;i++){
										spawnOneMob(L1Location.randomLocation
												(new L1Location(32799,32803,_mapId), 1, 6, false),_monstersA[_random.nextInt(5)]);
									}
								}else{

								}
							}
						}
					break;
					case _STATUS_CREATE_RUNES :
						createRune();
					break;
					case _STATUS_ENEMY_CANON :
						int[][] table = {{32780,32818,35},{32793,32788,16}};
						int random = _random.nextInt(2);
						S_EffectLocation se = new S_EffectLocation(table[random][0]+_random.nextInt(table[random][2]), table[random][1], 8233);
						sendPackets(se);
					break;
					case _STATUS_FIRE_CANON_1 :
						if( _cannon_1 != null ){
							_cannon_1.broadcastPacket(new S_DoActionGFX(_cannon_1.getId(), 2));
						}
					break;
					case _STATUS_FIRE_CANON_2 :
						if( _cannon_2 != null ){
							_cannon_2.broadcastPacket(new S_DoActionGFX(_cannon_2.getId(), 2));
						}
					break;
				}//isDeleteTrancaction End
			}//swicth End
			cancel();//如何なる理由があっても必ずここを通過する。
		}
	}

	/**
	 * クエストに参加しているプレイヤーへメッセージを送信する。
	 *
	 * @param s
	 *            送信する文字列
	 * @param cases
	 *            送信するパケットの種類 0=Green 1=Yellow 2=Red
	 */
	private void sendMessage(String s, int cases) {
		if(cases==0){
			for (L1PcInstance pc  :  L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == _mapId) {
					pc.sendPackets(new S_GreenMessage(s));
				}
			}
		}else if(cases==1){
			for (L1PcInstance pc  :  L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == _mapId) {
					pc.sendPackets(new S_YellowMessage(s));
				}
			}
		}else if(cases==2){
			for (L1PcInstance pc  :  L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == _mapId) {
					pc.sendPackets(new S_RedMessage(s));
				}
			}
		}

	}

	/**
	 * クエストに参加しているプレイヤーへ引数で渡されたパケットを送信する。
	 *
	 * @param serverbasepacket 送信するパケット
	 */
	private void sendPackets(ServerBasePacket serverbasepacket) {
		for (L1PcInstance pc  :  L1World.getInstance().getAllPlayers()) {
			if (pc.getMapId() == _mapId) {
				pc.sendPackets(serverbasepacket);
			}
		}
	}

	public short getMapId() {
		return _mapId;
	}

	public void setMapId(short _mapId) {
		this._mapId = _mapId;
	}

	public void setOrimQuestStatus(int step) {
		_OrimQuestStatus = step;
	}

	/**
	 * ソーシャルアクション用の応答フラグを設定
	 * @param pc
	 * @param actionCode
	 */
	public void setAction(L1PcInstance pc,int actionCode) {
		if(pc!=null){
			if(pc.isInParty()){
				if(pc.getParty().isLeader(pc) && pc.getMapId()==_mapId){
					if(_OrimQuestStatus == _STATUS_ORIMQUEAT_READY_3){
						_action = actionCode;
					}else if(_OrimQuestStatus > _STATUS_ORIMQUEAT_READY_3){
						if(pc.getX() == 32799 && pc.getY() == 32808){//デッキ上か？
							if(actionCode==66){//Alt+4 攻撃
								fireGun();
							}else if(actionCode==69){//Alt+2 防御
								diffenceShip();
							}
						}
					}
				}
			}else if(pc.isGm()){//GM用
				if(pc.getMapId()==_mapId){
					if(_OrimQuestStatus == _STATUS_ORIMQUEAT_READY_3){
						_action = actionCode;
					}else if(_OrimQuestStatus > _STATUS_ORIMQUEAT_READY_3){
						if(pc.getX() == 32799 && pc.getY() == 32808){//デッキ上か？
							if(actionCode==66){//Alt+4 攻撃
								fireGun();
							}else if(actionCode==69){//Alt+2 防御
								diffenceShip();
							}
						}
					}
				}
			}
		}
	}
	/**
	 * ルーンが全て消えたか調査する。
	 * 戻り値 :
	 * true ならば全て消えている
	 */

	private boolean checkRune(){
		return _diffence_rune_2 == null && _diffence_rune_1 == null && _attack_rune == null;
	}

	/**
	 * オリムの船に防御の魔法を掛けて船を強化する
	 */
	public void diffenceShip() {
		if(_diffence_rune_1!=null && _diffence_rune_2!=null){
			_diffence_rune_1.deleteMe();
			_diffence_rune_2.deleteMe();
			_diffence_rune_1=null;
			_diffence_rune_2=null;
			for (L1PcInstance pc  :  L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == _mapId) {
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 2030));
					pc.sendPackets(new S_SkillSound(pc.getId(), 2030));
				}
			}
			if(_OrimQuestStatus >= _STATUS_ORIMQUEAT_START){
				if(_random.nextInt(4) > 0){
					_roundDiffenceStatus[_currentRound]++;
				}else{
					enemyCanon();
				}
			}
			if(checkRune()){
				OrimQuestTimer createRuneTimer = new OrimQuestTimer(
						_STATUS_CREATE_RUNES, 3000);
				createRuneTimer.begin();
			}
		}
	}
	/**
	 * 砲撃を開始する。
	 */
	public void fireGun() {
		if(_attack_rune!=null){
			_attack_rune.deleteMe();
			_attack_rune=null;
			//花火を表示
			for (L1PcInstance pc  :  L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == _mapId) {
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 2029));
					pc.sendPackets(new S_SkillSound(pc.getId(), 2029));
				}
			}

			if(!(_OrimQuestStatus >= _STATUS_ORIMQUEAT_START)){
				sendMessage("HIT!", 1);
			}else{
				if(_random.nextInt(4) > 0){
					if( _roundHitDamage[_currentRound]==0){
						_roundHitDamage[_currentRound]++;
						sendMessage("HIT!", 1);
					}else if(_roundHitDamage[_currentRound]==1){
						_roundHitDamage[_currentRound]++;
						sendMessage("HIT!", 0);
					}else if(_roundHitDamage[_currentRound]==2){
						_roundHitDamage[_currentRound]++;
						sendMessage("HIT!", 2);
					}else{
						sendMessage("HIT!", 2);
					}
				}else{
					enemyCanon();
				}
			}

			fireCanon();

			if(checkRune()){
				OrimQuestTimer createRuneTimer = new OrimQuestTimer(
						_STATUS_CREATE_RUNES, 3000);
				createRuneTimer.begin();
			}
		}

	}

	private L1NpcInstance spawnOne(L1Location loc, int npcid, int heading) {
		L1NpcInstance mob = new L1NpcInstance(NpcTable.getInstance()
				.getTemplate(npcid));
		if (mob == null) {
			return mob;
		}

		mob.setId(IdFactory.getInstance().nextId());
		mob.setHeading(heading);
		mob.setX(loc.getX());
		mob.setHomeX(loc.getX());
		mob.setY(loc.getY());
		mob.setHomeY(loc.getY());
		mob.setMap((short) loc.getMapId());
		if(mob.getNpcId() == 91487){
			mob.setTempCharGfx(8511+_random.nextInt(5));
		}

		L1World.getInstance().storeObject(mob);
		L1World.getInstance().addVisibleObject(mob);

		S_NpcPack s_npcPack = new S_NpcPack(mob);
		for (L1PcInstance pc  :  L1World.getInstance().getRecognizePlayer(
				mob)) {
			pc.addKnownObject(mob);
			mob.addKnownObject(pc);
			pc.sendPackets(s_npcPack);
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
	 * @return L1MonsterInstance 戻り値  :  成功=生成したインスタンス 失敗=null
	 */
	private L1MonsterInstance spawnOneMob(L1Location loc, int npcid) {
		L1MonsterInstance mob = new L1MonsterInstance(NpcTable
				.getInstance().getTemplate(npcid));
		if (mob == null) {
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

		S_NpcPack s_npcPack = new S_NpcPack(mob);
		for (L1PcInstance pc  :  L1World.getInstance().getRecognizePlayer(
				mob)) {
			pc.addKnownObject(mob);
			mob.addKnownObject(pc);
			pc.sendPackets(s_npcPack);
		}
		// モンスターのＡＩを開始
		mob.onNpcAI();
		mob.updateLight();
		mob.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
		return mob;
	}

	private void clearAllRune(){
		//初期化
		clearAttackRune();
		clearDiffenceRune();
	}
	private void clearAttackRune(){
		//初期化
		if(_attack_rune != null){
			_attack_rune.deleteMe();
			_attack_rune = null;
		}
	}
	private void clearDiffenceRune(){
		//初期化
		if(_diffence_rune_1 != null){
			_diffence_rune_1.deleteMe();
			_diffence_rune_1 = null;
		}
		if(_diffence_rune_2 != null){
			_diffence_rune_2.deleteMe();
			_diffence_rune_2 = null;
		}
	}
	private void createRune(){
		if(_acceptOrder){
			if(_attack_rune==null){
				_attack_rune = spawnOne(L1Location.randomLocation(new L1Location(32799,32803,_mapId), 1, 6, false), 91454, 5);
			}
			if(_diffence_rune_1==null){
				_diffence_rune_1 = spawnOne(L1Location.randomLocation(new L1Location(32799,32803,_mapId), 1, 6, false), 91453, 5);
			}
			if(_diffence_rune_2==null){
				_diffence_rune_2 = spawnOne(L1Location.randomLocation(new L1Location(32799,32803,_mapId), 1, 6, false), 91453, 5);
			}
		}
	}

	private void deleteNpc(L1NpcInstance npc){
		npc.getMap().setPassable(npc.getX(), npc.getY(), true);
		npc.deleteMe();
	}

	public void mimicDie(L1MonsterInstance npc) {
		if(npc.isCurseMimic()){
			for(L1PcInstance pc:L1World.getInstance().getVisiblePlayer(npc, 15)){
				L1Teleport.teleport(pc, 32792,32802,_mapId, 2, true);
			}
			if(_mimic_A != null){
				if(!_mimic_A.isDead()){
					_mimic_A.deleteMe();
				}
				_mimic_A = null;
			}
			if(_mimic_B != null){
				if(!_mimic_B.isDead()){
					_mimic_B.deleteMe();
				}
				_mimic_B = null;
			}
			if(_mimic_C != null){
				if(!_mimic_C.isDead()){
					_mimic_C.deleteMe();
				}
				_mimic_C = null;
			}
		}
	}
	private boolean checkSeaMonsterAttack(int type){
		boolean flag = false;
		int count = 0;
		for(int i =0;i<12;i++){
			if(_bonusRoundStatus[i]==type){
				count++;
			}
		}
		if(count==3){
			flag = true;
		}
		if(flag){
			if(type == 1){
				for(int i=0;i<3;i++){
					spawnOneMob(L1Location.randomLocation
							(new L1Location(32799,32803,_mapId), 1, 6, false),91483);
				}
				sendMessage("$9549",1);//9549	オリム：ウウッ！海洋生物まで攻撃をしかけてきています。なんでこんな時に！
			}else if(type == 2){
				for(int i=0;i<3;i++){
					spawnOneMob(L1Location.randomLocation
							(new L1Location(32799,32803,_mapId), 1, 6, false),91482);
				}
				sendMessage("$9549",1);//9549	オリム：ウウッ！海洋生物まで攻撃をしかけてきています。なんでこんな時に！
			}else if(type == 3){
				spawnOneMob(new L1Location(32800,32794,_mapId),91481);//クラーケン
				spawnOneMob(new L1Location(32793,32795,_mapId),91491);//足左
				spawnOneMob(new L1Location(32804,32796,_mapId),91492);//足右
				sendMessage("$9558",1);//9558	オリム：ウウッ！怒り狂った海洋生物が押し寄せてきています！
			}
		}
		return flag;
	}
	/**
	 * ワールドに新しいトラップを作成して追加する ※L1ReloadTrapが実行されると破棄される点に注意
	 *
	 * @param loc
	 *            　任意の位置
	 * @param id
	 *            　トラップID
	 */
	private static L1TrapInstance setSwitch(L1Location loc, int id) {
		final int trapId = id;
		final L1Trap trapTemp = TrapTable.getInstance().getTemplate(trapId);
		final Point rndPt = new Point();
		rndPt.setX(0);
		rndPt.setY(0);
		final int span = 0;
		final L1TrapInstance trap = new L1TrapInstance(IdFactory.getInstance()
				.nextId(), trapTemp, loc, rndPt, span);
		L1World.getInstance().addVisibleObject(trap);
		L1WorldTraps.getInstance().addTrap(trap);
		return trap;
	}
	private int checkAliveMonster(){
		int count = 0;
		for(Object obj : L1World.getInstance().getVisiblePoint(new L1Location(32799,32803,_mapId), 15)){
			if(obj instanceof L1MonsterInstance){
				if(!((L1MonsterInstance) obj).isDead()){
					count++;
				}
			}
		}
		return count;
	}
	private void spawnMonsters(){
		int startPoint = _startPoint;
		int endPoint = _endPoint;
		for(int i = 0;i<_currentRound;i++){
			if(_roundStatus[i]==1){
				if(endPoint+1 >= _monstersA.length){
					continue;
				}
				startPoint++;
				endPoint++;
			}else if(_roundStatus[i]==-1){
				if(startPoint-1 < 0){
					continue;
				}
				startPoint--;
				endPoint--;
			}
		}
		for(int i = 0;i<7;i++){
			spawnOneMob(L1Location.randomLocation
					(new L1Location(32799,32803,_mapId),
							1, 6, false),_monstersA[startPoint+_random.nextInt(endPoint-startPoint)]);
		}
	}
	private void outPushPlayer(){
		for (Object obj  :  L1World.getInstance().getVisibleObjects(_mapId).values()) {
			if(obj instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance) obj;
				final int rndx = _random.nextInt(4);
				final int rndy = _random.nextInt(4);
				final int locx = 32587 + rndx;
				final int locy = 32941 + rndy;
				final short mapid = 0;//話せる島
				L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
			}else{
				if(obj instanceof L1NpcInstance ){
					deleteNpc((L1NpcInstance) obj);;
				}
			}
		}
	}

	private void sendShipDamage(){

		if(_roundDiffenceStatus[_currentRound] < 2){//２回以上防御陣を張っていなければ連絡船へダイレクトダメージ
			if(_shipWall.getStatus()==35){
				_shipWall.setStatus(36);//沈没
			}else{
				_shipWall.setStatus(_shipWall.getStatus()+1);
			}
			_shipWall.broadcastPacket(new S_DoActionGFX(_shipWall.getId(),_shipWall.getStatus()));
			if(_shipWall.getStatus() > 35){
				sendMessage("$9562",1);//9562	オリム：船が沈没します！急いで脱出してください！この状態では危険です！
				sendPackets(new S_ShockWave());
				try {
					Thread.sleep(35000);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				outPushPlayer();
			}
		}
	}
	private void enemyCanon(){
		int i = _random.nextInt(4);
		for(int z = 0;z<i;z++){
			OrimQuestTimer timer = new OrimQuestTimer(
					_STATUS_ENEMY_CANON, 2000 + 2000 * z);
			timer.begin();
		}
	}

	private void fireCanon(){
		int i = _random.nextInt(2)+2;//2~3回
		int n = _random.nextInt(2);
		for(int z = 0;z<i;z++){
			if((n+z)%2==1){
				OrimQuestTimer timer = new OrimQuestTimer(
						_STATUS_FIRE_CANON_1, 2000 * z);
				timer.begin();
			}else{
				OrimQuestTimer timer = new OrimQuestTimer(
						_STATUS_FIRE_CANON_2, 2000 * z);
				timer.begin();
			}
		}
	}

	private void reset() {
		try {
			for (L1Object obj  :  L1World.getInstance().getVisibleObjects(_mapId).values()) {
				if (obj.getMapId() == getMapId()) {
					if (obj instanceof L1FieldObjectInstance) {
						L1World.getInstance().removeVisibleObject(obj);
						L1World.getInstance().removeObject(obj);
					} else if (obj instanceof L1EffectInstance) {
						L1World.getInstance().removeVisibleObject(obj);
						L1World.getInstance().removeObject(obj);
					} else if (obj instanceof L1ItemInstance) {
						final L1Inventory groundInventory = L1World
								.getInstance().getInventory(obj.getX(),
										obj.getY(), obj.getMapId());
						groundInventory.deleteItem((L1ItemInstance) obj);
						L1World.getInstance().removeVisibleObject(obj);
						L1World.getInstance().removeObject(obj);
					} else if (obj instanceof L1DoorInstance) {
						DoorTable.getInstance().deleteDoorByLocation(obj.getLocation());
					} else if (obj instanceof L1NpcInstance) {
						((L1NpcInstance) obj).deleteMe();
					} else if (obj instanceof L1TrapInstance){
						L1WorldTraps.getInstance().removeTrap((L1TrapInstance) obj);
					}
				}
			}
		} catch (final Exception e) {

			e.printStackTrace();
		}
	}
	/**
	 * イカちゃんの死亡フラグを立てる
	 *
	 */
	public void crakenDead() {
		this._isCrakenDead = true;
		if(_craken_tail_left != null){
			_craken_tail_left.broadcastPacket
				(new S_DoActionGFX(_craken_tail_left.getId(),ActionCodes.ACTION_Die));
			_craken_tail_left.deleteMe();
		}

		if(_craken_tail_right != null){
			_craken_tail_right.broadcastPacket
			(new S_DoActionGFX(_craken_tail_right.getId(),ActionCodes.ACTION_Die));
			_craken_tail_right.deleteMe();
		}
	}
	public void crakenTailDead_Left() {
		_craken_tail_left = null;
	}
	public void crakenTailDead_Right() {
		_craken_tail_right = null;
	}

}
