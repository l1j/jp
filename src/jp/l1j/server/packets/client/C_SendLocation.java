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
import java.util.logging.Logger;
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.L1DragonSlayer;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_PacketBox;
import jp.l1j.server.packets.server.S_SendLocation;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.utils.L1SpawnUtil;

public class C_SendLocation extends ClientBasePacket {

	private static final String C_SEND_LOCATION = "[C] C_SendLocation";
	private static Logger _log = Logger.getLogger(C_SendLocation.class.getName());

	public C_SendLocation(byte abyte0[], ClientThread client) {
		super(abyte0);
		int type = readC();

		// クライアントがアクティブ,ノンアクティブ転換時に
		// オペコード 0x57 0x0dパケットを送ってくるが詳細不明の為無視
		// マップ座標転送時は0x0bパケット
		if (type == 0x0d) {
			return;
		}

		if (type == 0x0b) {
			String name = readS();
			int mapId = readH();
			int x = readH();
			int y = readH();
			int msgId = readC();

			if (name.isEmpty()) {
				return;
			}
			L1PcInstance target = L1World.getInstance().getPlayer(name);
			if (target != null) {
				L1PcInstance pc = client.getActiveChar();
				String sender = pc.getName();
				target.sendPackets(new S_SendLocation(type, sender, mapId, x, y, msgId));
				//将来的にtypeを使う可能性があるので送る
			}
		} else if (type == 0x06) {
			int objectId = readD();
			int gate = readD();
			int dragonGate[] = {91051, 91052, 91053, 91054 };
			L1PcInstance pc = client.getActiveChar();
			if (gate >= 0 && gate <= 3) {
				Calendar nowTime = Calendar.getInstance();
				if (nowTime.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY
						&& nowTime.get(Calendar.HOUR_OF_DAY) >= 8
						&& nowTime.get(Calendar.HOUR_OF_DAY) < 12) {
					pc.sendPackets(new S_ServerMessage(1643));
					// 火曜日午前7時から10時まではドラゴンキーを使用できません。
				} else {
					boolean limit = true;
					switch (gate) {
					case 0: // アンタラス
						for (int i = 0; i < 6; i++) {
							if (!L1DragonSlayer.getInstance().getPortalNumber()[i]) {
								limit = false;
								break;
							}
						}
						break;
					case 1: // パプリオン
						for (int i = 6; i < 12; i++) {
							if (!L1DragonSlayer.getInstance().getPortalNumber()[i]) {
								limit = false;
								break;
							}
						}
						break;
					case 2: // リンドビオル
						for (int i = 12; i < 18; i++) {
							if (!L1DragonSlayer.getInstance().getPortalNumber()[i]) {
								limit = false;
								break;
							}
						}
						break;
					case 3: // ヴァラカス
						for (int i = 18; i < 24; i++) {
							if (!L1DragonSlayer.getInstance().getPortalNumber()[i]) {
								limit = false;
								break;
							}
						}
						break;
					}
					if (!limit) { // インスタンスダンジョンの空きがある場合
						if (!pc.getInventory().consumeItem(50501, 1)) {
							pc.sendPackets(new S_ServerMessage(1566));
							// ドラゴンキーが必要です。
							return;
						}
						L1SpawnUtil.spawn(pc, dragonGate[gate], 0, 60 * 60 * 2 * 1000); // 2時間
						for (L1PcInstance listner : L1World.getInstance().getAllPlayers()) {
							if (!listner.getExcludingList().contains(pc.getName())) {
								if (listner.isShowTradeChat() || listner.isShowWorldChat()) {
									listner.sendPackets(new S_ServerMessage(2921));
									// 鋼鉄ギルド ドワーフ：うむ…ドラゴンの鳴き声がここまで聞こえてくる。
									// きっと誰かがドラゴンポータルを開いたに違いない！
									// 準備ができたドラゴン スレイヤーに栄光と祝福を！
								}
							}
						}
					}
				}
			}
		}
		// TODO 3.53C start
		else if (type == 0x09){ // マップタイマーの残り時間を表示
			L1PcInstance pc = client.getActiveChar();
			pc.sendPackets(new S_PacketBox(S_PacketBox.DISPLAY_MAP_TIME ,
					pc.getEnterTime(53),   // ギラン監獄
					pc.getEnterTime(78),   // 象牙の塔
					pc.getEnterTime(451),  // ラスタバド ダンジョン
					pc.getEnterTime(30))); // ドラゴンバレー ダンジョン
		} else if (type == 0x13) { // web center
			// not yet
		} else if (type == 0x2C) { // モンスター討伐数をリセット
			L1PcInstance pc = client.getActiveChar();
			pc.setMonsterKill(0);
		}
		// TODO 3.53C end
	}

	@Override
	public String getType() {
		return C_SEND_LOCATION;
	}
}

