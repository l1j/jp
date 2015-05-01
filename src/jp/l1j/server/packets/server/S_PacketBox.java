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
package jp.l1j.server.packets.server;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.templates.L1Account;

/**
 * スキルアイコンや遮断リストの表示など複数の用途に使われるパケットのクラス
 */
public class S_PacketBox extends ServerBasePacket {
	private static final String S_PACKETBOX = "[S] S_PacketBox";

	private static Logger _log = Logger.getLogger(S_PacketBox.class.getName());

	private byte[] _byte = null;

	// *** S_107 sub code list ***
	/** Updating */
	public static final int UPDATE_OLD_PART_MEMBER = 104;

	/** 3.3C (パーティーメンバー更新) */
	public static final int PATRY_UPDATE_MEMBER = 105;

	/** 3.3C (パーティーリーダー委任) */
	public static final int PATRY_SET_MASTER = 106;

	/** 3.3C (パーティー情報更新) */
	public static final int PATRY_MEMBERS = 110;

	// 1:Kent 2:Orc 3:WW 4:Giran 5:Heine 6:Dwarf 7:Aden 8:Diad 9:城名9 ...
	/** C(id) H(?): %sの攻城戦が始まりました。 */
	public static final int MSG_WAR_BEGIN = 0;

	/** C(id) H(?): %sの攻城戦が終了しました。 */
	public static final int MSG_WAR_END = 1;

	/** C(id) H(?): %sの攻城戦が進行中です。 */
	public static final int MSG_WAR_GOING = 2;

	/** -: 城の主導権を握りました。 (音楽が変わる) */
	public static final int MSG_WAR_INITIATIVE = 3;

	/** -: 城を占拠しました。 */
	public static final int MSG_WAR_OCCUPY = 4;

	/** ?: 決闘が終りました。 (音楽が変わる) */
	public static final int MSG_DUEL = 5;

	/** C(count): SMSの送信に失敗しました。 / 全部で%d件送信されました。 */
	public static final int MSG_SMS_SENT = 6;

	/** -: 祝福の中、2人は夫婦として結ばれました。 (音楽が変わる) */
	public static final int MSG_MARRIED = 9;

	/** C(weight): 重量(30段階) */
	public static final int WEIGHT = 10;

	/** C(food): 満腹度(30段階) */
	public static final int FOOD = 11;

	/** C(0) C(level): このアイテムは%dレベル以下のみ使用できます。 (0~49以外は表示されない) */
	public static final int MSG_LEVEL_OVER = 12;

	/** UB情報HTML */
	public static final int HTML_UB = 14;

	/**
	 * C(id)<br>
	 * 1:身に込められていた精霊の力が空気の中に溶けて行くのを感じました。<br>
	 * 2:体の隅々に火の精霊力が染みこんできます。<br>
	 * 3:体の隅々に水の精霊力が染みこんできます。<br>
	 * 4:体の隅々に風の精霊力が染みこんできます。<br>
	 * 5:体の隅々に地の精霊力が染みこんできます。<br>
	 */
	public static final int MSG_ELF = 15;

	/** C(count) S(name)...: 遮断リスト複数追加 */
	public static final int ADD_EXCLUDE2 = 17;

	/** S(name): 遮断リスト追加 */
	public static final int ADD_EXCLUDE = 18;

	/** S(name): 遮断解除 */
	public static final int REM_EXCLUDE = 19;

	/** スキルアイコン */
	public static final int ICONS1 = 20;

	/** スキルアイコン */
	public static final int ICONS2 = 21;

	/** オーラ系のスキルアイコン */
	public static final int ICON_AURA = 22;

	/** S(name): タウンリーダーに%sが選ばれました。 */
	public static final int MSG_TOWN_LEADER = 23;

	/**
	 * C(id): あなたのランクが%sに変更されました。<br>
	 * id - 1:見習い 2:一般 3:ガーディアン
	 */
	public static final int MSG_RANK_CHANGED = 27;

	/** D(?) S(name) S(clanname): %s血盟の%sがラスタバド軍を退けました。 */
	public static final int MSG_WIN_LASTAVARD = 30;

	/** -: \f1気分が良くなりました。 */
	public static final int MSG_FEEL_GOOD = 31;

	/** 不明。C_30パケットが飛ぶ */
	public static final int SOMETHING1 = 33;

	/** H(time): ブルーポーションのアイコンが表示される。 */
	public static final int ICON_BLUEPOTION = 34;

	/** H(time): 変身のアイコンが表示される。 */
	public static final int ICON_POLYMORPH = 35;

	/** H(time): チャット禁止のアイコンが表示される。 */
	public static final int ICON_CHATBAN = 36;

	/** 不明。C_7パケットが飛ぶ。C_7はペットのメニューを開いたときにも飛ぶ。 */
	public static final int SOMETHING2 = 37;

	/** 血盟情報のHTMLが表示される */
	public static final int HTML_CLAN1 = 38;

	/** H(time): イミュのアイコンが表示される */
	public static final int ICON_I2H = 40;

	/** キャラクターのゲームオプション、ショートカット情報などを送る */
	public static final int CHARACTER_CONFIG = 41;

	/** キャラクター選択画面に戻る */
	public static final int LOGOUT = 42;

	/** 戦闘中に再始動することはできません。 */
	public static final int MSG_CANT_LOGOUT = 43;

	/**
	 * C(count) D(time) S(name) S(info):<br>
	 * [CALL] ボタンのついたウィンドウが表示される。これはBOTなどの不正者チェックに
	 * 使われる機能らしい。名前をダブルクリックするとC_RequestWhoが飛び、クライアントの
	 * フォルダにbot_list.txtが生成される。名前を選択して+キーを押すと新しいウィンドウが開く。
	 */
	public static final int CALL_SOMETHING = 45;

	/**
	 * C(id): バトル コロシアム、カオス大戦がー<br>
	 * id - 1:開始します 2:取り消されました 3:終了します
	 */
	public static final int MSG_COLOSSEUM = 49;

	/** 血盟情報のHTML */
	public static final int HTML_CLAN2 = 51;

	/** 料理ウィンドウを開く */
	public static final int COOK_WINDOW = 52;

	/** C(type) H(time): 料理アイコンが表示される */
	public static final int ICON_COOKING = 53;

	/** 魚がかかったグラフィックが表示される */
	public static final int FISHING = 55;

	/** アインハザードの祝福 **/
	public static final int BLESS_OF_AIN = 82;
		
	/** 回避率 正*/
	public static final int DODGE_RATE_PLUS = 88;
	
	/** 回避率 負*/
	public static final int DODGE_RATE_MINUS = 101;
	
	/** 血盟倉庫使用記録 */
	public static final int HTML_CLAN_WARHOUSE_RECORD = 117;
	
	/** マップタイマーの残り時間を表示 **/
	public static final int MAP_TIMER = 153;
	public static final int DISPLAY_MAP_TIME = 159;
	
	public S_PacketBox(int subCode) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case MSG_WAR_INITIATIVE:
		case MSG_WAR_OCCUPY:
		case MSG_MARRIED:
		case MSG_FEEL_GOOD:
		case MSG_CANT_LOGOUT:
		case LOGOUT:
		case FISHING:
			break;
		case CALL_SOMETHING:
			callSomething();
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int value) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ICON_BLUEPOTION:
		case ICON_CHATBAN:
		case ICON_I2H:
		case ICON_POLYMORPH:
		case MAP_TIMER: // TODO 3.53C マップタイマー
			writeH(value); // time
			break;
		case MSG_WAR_BEGIN:
		case MSG_WAR_END:
		case MSG_WAR_GOING:
			writeC(value); // castle id
			writeH(0); // ?
			break;
		case MSG_SMS_SENT:
		case WEIGHT:
		case FOOD:
			writeC(value);
			break;
		case MSG_ELF:
		case MSG_RANK_CHANGED:
		case MSG_COLOSSEUM:
			writeC(value); // msg id
			break;
		case MSG_LEVEL_OVER:
			writeC(0); // ?
			writeC(value); // 0-49以外は表示されない
			break;
		case COOK_WINDOW:
			writeC(0xdb); // ?
			writeC(0x31);
			writeC(0xdf);
			writeC(0x02);
			writeC(0x01);
			writeC(value); // level
			break;
		case BLESS_OF_AIN:
			value /= 10000;
			writeD(value); //1%〜200%
			break;
		case DODGE_RATE_PLUS: // + 近距離回避率
			writeC(value);
			writeC(0x00);
			break;
		case DODGE_RATE_MINUS: // - 近距離回避率
			writeC(value);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int type, int time) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ICON_COOKING:
			if (type != 7) {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0x00);
				writeC(0x00);
				writeC(type);
				writeC(0x24);
				writeH(time);
				writeH(0x00);
			} else {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0xc8);
				writeC(0x00);
				writeC(type);
				writeC(0x26);
				writeH(time);
				writeC(0x3e);
				writeC(0x87);
			}
			break;
		case MSG_DUEL:
			writeD(type); // 相手のオブジェクトID
			writeD(time); // 自分のオブジェクトID
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, String name) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ADD_EXCLUDE:
		case REM_EXCLUDE:
		case MSG_TOWN_LEADER:
			writeS(name);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int id, String name, String clanName) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case MSG_WIN_LASTAVARD:
			writeD(id); // クランIDか何か？
			writeS(name);
			writeS(clanName);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, Object[] names) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ADD_EXCLUDE2:
			writeC(names.length);
			for (Object name : names) {
				writeS(name.toString());
			}
			break;
		default:
			break;
		}
	}
	
	// TODO マップタイマーの残り時間を表示 start
	public S_PacketBox(int subCode, int time1, int time2, int time3, int time4) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);
		switch (subCode) {
		case DISPLAY_MAP_TIME :
			writeD(4);
			writeD(1);
			writeS("$12125"); // ギラン監獄
			writeD(time1);
			writeD(2);
			writeS("$6081"); // 象牙の塔
			writeD(time2);
			writeD(3);
			writeS("$12126"); // ラスタバド ダンジョン
			writeD(time3);
			writeD(4);
			writeS("$14250"); // ドラゴンバレー ダンジョン
			writeD(time4);
			break;
		default:
			break;
		}
	}
	// TODO マップタイマーの残り時間を表示 end
	
	private void callSomething() {
		Iterator<L1PcInstance> itr = L1World.getInstance().getAllPlayers().iterator();

		writeC(L1World.getInstance().getAllPlayers().size());

		while (itr.hasNext()) {
			L1PcInstance pc = itr.next();
			L1Account acc = L1Account.findByName(pc.getAccountName());

			// 時間情報 とりあえずログイン時間を入れてみる
			if (acc == null) {
				writeD(0);
			} else {
				Calendar cal = Calendar
						.getInstance(TimeZone.getTimeZone(Config.TIME_ZONE));
				long lastactive = acc.getLastActivatedAt().getTime();
				cal.setTimeInMillis(lastactive);
				cal.set(Calendar.YEAR, 1970);
				int time = (int) (cal.getTimeInMillis() / 1000);
				writeD(time); // JST 1970 1/1 09:00 が基準
			}

			// キャラ情報
			writeS(pc.getName()); // 半角12字まで
			writeS(pc.getClanName()); // []内に表示される文字列。半角12字まで
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}

		return _byte;
	}

	@Override
	public String getType() {
		return S_PACKETBOX;
	}
}