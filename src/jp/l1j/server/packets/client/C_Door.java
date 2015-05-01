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

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import jp.l1j.server.ClientThread;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.HouseTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.model.instance.L1DoorInstance;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.templates.L1House;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket, C_Door

public class C_Door extends ClientBasePacket {

	private static Logger _log = Logger.getLogger(C_Door.class.getName());
	private static final String C_DOOR = "[C] C_Door";

	@SuppressWarnings("unused")
	public C_Door(byte abyte0[], ClientThread client)
			throws Exception {
		super(abyte0);
		int locX = readH();
		int locY = readH();
		int objectId = readD();

		L1PcInstance pc = client.getActiveChar();
		L1Object object = L1World.getInstance().findObject(objectId);
		int npcId = ((L1NpcInstance) object).getNpcTemplate().getNpcId();

		if(!(object instanceof L1DoorInstance)) {
			return;
		}
		
		L1DoorInstance door = (L1DoorInstance)object;
		if (door == null) {
			return;
		}

		L1NpcInstance mob = (L1NpcInstance)object;
		if (mob == null) {
			return;
		}

		// 水晶の洞窟
		if ((door.getDoorId() >= 5001 && door.getDoorId() <= 5010)) {
			return;
		// TIC2F
		} else if (door.getDoorId() == 6006) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				return;
			}
			if (pc.getInventory().consumeItem(40163,1)) { // ゴールデンキー
				door.open();
				CloseTimer closetimer = new CloseTimer(door);
				closetimer.begin();
			}
		// TIC2F
		} else if (door.getDoorId() == 6007) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				return;
			}
			if (pc.getInventory().consumeItem(40313,1)) { // パールシルバーキー
				door.open();
				CloseTimer closetimer = new CloseTimer(door);
				closetimer.begin();
			}
		// アンタラス洞窟
		} else if (door.getDoorId() == 7001 || door.getDoorId() == 7004
				|| door.getDoorId() == 7007 || door.getDoorId() == 7010
				|| door.getDoorId() == 7013 || door.getDoorId() == 7016
				|| door.getDoorId() == 7019 || door.getDoorId() == 7022
				|| door.getDoorId() == 7025 || door.getDoorId() == 7028
				|| door.getDoorId() == 7031 || door.getDoorId() == 7034
				|| door.getDoorId() == 7037 || door.getDoorId() == 7040
				|| door.getDoorId() == 7043 || door.getDoorId() == 7046
				|| door.getDoorId() == 7049 || door.getDoorId() == 7052
				|| door.getDoorId() == 7055 || door.getDoorId() == 7058
				|| door.getDoorId() == 7061 || door.getDoorId() == 7064
				|| door.getDoorId() == 7067 || door.getDoorId() == 7070) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				return;
			}
			if (pc.getInventory().consumeItem(50528,1)) { // コマ クームの息吹A
				door.open();
				CloseTimer closetimer = new CloseTimer(door);
				closetimer.begin();
			}
		// アンタラス洞窟
		} else if (door.getDoorId() == 7002 || door.getDoorId() == 7005
				|| door.getDoorId() == 7008 || door.getDoorId() == 7011
				|| door.getDoorId() == 7014 || door.getDoorId() == 7017
				|| door.getDoorId() == 7020 || door.getDoorId() == 7023
				|| door.getDoorId() == 7026 || door.getDoorId() == 7029
				|| door.getDoorId() == 7032 || door.getDoorId() == 7035
				|| door.getDoorId() == 7038 || door.getDoorId() == 7041
				|| door.getDoorId() == 7044 || door.getDoorId() == 7047
				|| door.getDoorId() == 7050 || door.getDoorId() == 7053
				|| door.getDoorId() == 7056 || door.getDoorId() == 7059
				|| door.getDoorId() == 7062 || door.getDoorId() == 7065
				|| door.getDoorId() == 7068 || door.getDoorId() == 7071) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				return;
			}
			if (pc.getInventory().consumeItem(50529,1)) { // コマ クームの息吹B
				door.open();
				CloseTimer closetimer = new CloseTimer(door);
				closetimer.begin();
			}
		// アンタラス洞窟
		} else if (door.getDoorId() == 7003 || door.getDoorId() == 7006
				|| door.getDoorId() == 7009 || door.getDoorId() == 7012
				|| door.getDoorId() == 7015 || door.getDoorId() == 7018
				|| door.getDoorId() == 7021 || door.getDoorId() == 7024
				|| door.getDoorId() == 7027 || door.getDoorId() == 7030
				|| door.getDoorId() == 7033 || door.getDoorId() == 7036
				|| door.getDoorId() == 7039 || door.getDoorId() == 7042
				|| door.getDoorId() == 7045 || door.getDoorId() == 7048
				|| door.getDoorId() == 7051 || door.getDoorId() == 7054
				|| door.getDoorId() == 7057 || door.getDoorId() == 7060
				|| door.getDoorId() == 7063 || door.getDoorId() == 7066
				|| door.getDoorId() == 7069 || door.getDoorId() == 7072) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				return;
			}
			if (pc.getInventory().consumeItem(50530,1)) { // コマ クームの息吹C
				door.open();
				CloseTimer closetimer = new CloseTimer(door);
				closetimer.begin();
			}
		// パプリオン洞窟
		} else if (door.getDoorId() == 7073 || door.getDoorId() == 7076
				|| door.getDoorId() == 7079 || door.getDoorId() == 7082
				|| door.getDoorId() == 7085 || door.getDoorId() == 7088
				|| door.getDoorId() == 7091 || door.getDoorId() == 7094
				|| door.getDoorId() == 7097 || door.getDoorId() == 7100
				|| door.getDoorId() == 7103 || door.getDoorId() == 7106
				|| door.getDoorId() == 7109 || door.getDoorId() == 7112
				|| door.getDoorId() == 7115 || door.getDoorId() == 7118
				|| door.getDoorId() == 7121 || door.getDoorId() == 7124
				|| door.getDoorId() == 7127 || door.getDoorId() == 7130
				|| door.getDoorId() == 7133 || door.getDoorId() == 7136
				|| door.getDoorId() == 7139 || door.getDoorId() == 7142) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				return;
			}
			if (pc.getInventory().consumeItem(50550,1)) { // 海の秘密A
				door.open();
				CloseTimer closetimer = new CloseTimer(door);
				closetimer.begin();
			}
		// パプリオン洞窟
		} else if (door.getDoorId() == 7074 || door.getDoorId() == 7077
				|| door.getDoorId() == 7080 || door.getDoorId() == 7083
				|| door.getDoorId() == 7086 || door.getDoorId() == 7089
				|| door.getDoorId() == 7092 || door.getDoorId() == 7095
				|| door.getDoorId() == 7098 || door.getDoorId() == 7101
				|| door.getDoorId() == 7104 || door.getDoorId() == 7107
				|| door.getDoorId() == 7110 || door.getDoorId() == 7113
				|| door.getDoorId() == 7116 || door.getDoorId() == 7119
				|| door.getDoorId() == 7122 || door.getDoorId() == 7125
				|| door.getDoorId() == 7128 || door.getDoorId() == 7131
				|| door.getDoorId() == 7134 || door.getDoorId() == 7137
				|| door.getDoorId() == 7140 || door.getDoorId() == 7143) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				return;
			}
			if (pc.getInventory().consumeItem(50551,1)) { // 海の秘密B
				door.open();
				CloseTimer closetimer = new CloseTimer(door);
				closetimer.begin();
			}
		// パプリオン洞窟
		} else if (door.getDoorId() == 7001 || door.getDoorId() == 7004
				|| door.getDoorId() == 7007 || door.getDoorId() == 7010
				|| door.getDoorId() == 7013 || door.getDoorId() == 7016
				|| door.getDoorId() == 7019 || door.getDoorId() == 7022
				|| door.getDoorId() == 7025 || door.getDoorId() == 7028
				|| door.getDoorId() == 7031 || door.getDoorId() == 7034
				|| door.getDoorId() == 7037 || door.getDoorId() == 7040
				|| door.getDoorId() == 7043 || door.getDoorId() == 7046
				|| door.getDoorId() == 7049 || door.getDoorId() == 7052
				|| door.getDoorId() == 7055 || door.getDoorId() == 7058
				|| door.getDoorId() == 7061 || door.getDoorId() == 7064
				|| door.getDoorId() == 7067 || door.getDoorId() == 7070) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				return;
			}
			if (pc.getInventory().consumeItem(50552,1)) { // 海の秘密C
				door.open();
				CloseTimer closetimer = new CloseTimer(door);
				closetimer.begin();
			}
		// リンドビオル洞窟
			} else if (door.getDoorId() == 7145 || door.getDoorId() == 7148
					|| door.getDoorId() == 7151 || door.getDoorId() == 7154
					|| door.getDoorId() == 7157 || door.getDoorId() == 7160
					|| door.getDoorId() == 7163 || door.getDoorId() == 7166
					|| door.getDoorId() == 7169 || door.getDoorId() == 7172
					|| door.getDoorId() == 7175 || door.getDoorId() == 7178
					|| door.getDoorId() == 7181 || door.getDoorId() == 7184
					|| door.getDoorId() == 7187 || door.getDoorId() == 7190
					|| door.getDoorId() == 7193 || door.getDoorId() == 7196
					|| door.getDoorId() == 7199 || door.getDoorId() == 7202
					|| door.getDoorId() == 7205 || door.getDoorId() == 7208
					|| door.getDoorId() == 7211 || door.getDoorId() == 7214) {
				if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
					return;
				}
				if (pc.getInventory().consumeItem(50635,1)) { // 疾風の羽毛A
						door.open();
						CloseTimer closetimer = new CloseTimer(door);
						closetimer.begin();
				}
				// リンドビオル洞窟
			} else if (door.getDoorId() == 7146 || door.getDoorId() == 7149
					|| door.getDoorId() == 7152 || door.getDoorId() == 7155
					|| door.getDoorId() == 7158 || door.getDoorId() == 7161
					|| door.getDoorId() == 7164 || door.getDoorId() == 7167
					|| door.getDoorId() == 7170 || door.getDoorId() == 7173
					|| door.getDoorId() == 7176 || door.getDoorId() == 7179
					|| door.getDoorId() == 7182 || door.getDoorId() == 7185
					|| door.getDoorId() == 7188 || door.getDoorId() == 7191
					|| door.getDoorId() == 7194 || door.getDoorId() == 7197
					|| door.getDoorId() == 7200 || door.getDoorId() == 7203
					|| door.getDoorId() == 7206 || door.getDoorId() == 7209
					|| door.getDoorId() == 7212 || door.getDoorId() == 7215) {
				if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
					return;
				}
				if (pc.getInventory().consumeItem(50636,1)) { // 疾風の羽毛B
					door.open();
					CloseTimer closetimer = new CloseTimer(door);
					closetimer.begin();
			}
				// リンドビオル洞窟
			} else if (door.getDoorId() == 7147 || door.getDoorId() == 7150
					|| door.getDoorId() == 7153 || door.getDoorId() == 7156
					|| door.getDoorId() == 7159 || door.getDoorId() == 7162
					|| door.getDoorId() == 7165 || door.getDoorId() == 7168
					|| door.getDoorId() == 7171 || door.getDoorId() == 7174
					|| door.getDoorId() == 7177 || door.getDoorId() == 7180
					|| door.getDoorId() == 7183 || door.getDoorId() == 7186
					|| door.getDoorId() == 7189 || door.getDoorId() == 7192
					|| door.getDoorId() == 7195 || door.getDoorId() == 7198
					|| door.getDoorId() == 7201 || door.getDoorId() == 7204
					|| door.getDoorId() == 7207 || door.getDoorId() == 7210
					|| door.getDoorId() == 7213 || door.getDoorId() == 7216) {
				if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
					return;
				}
				if (pc.getInventory().consumeItem(50637,1)) { // 疾風の羽毛C
					door.open();
					CloseTimer closetimer = new CloseTimer(door);
					closetimer.begin();
			}
		// ヴァラカス洞窟(未実装)
				} else if (!isExistKeeper(pc, door.getKeeperId())) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				door.close();
			} else if (door.getOpenStatus() == ActionCodes.ACTION_Close) {
				door.open();
			}
		}
	}

	private boolean isExistKeeper(L1PcInstance pc, int keeperId) {
		if (keeperId == 0) {
			return false;
		}

		L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
		if (clan != null) {
			int houseId = clan.getHouseId();
			if (houseId != 0) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				if (keeperId == house.getKeeperId()) {
					return false;
				}
			}
		}
		return true;
	}

	public class CloseTimer extends TimerTask {

		private L1DoorInstance _door;

		public CloseTimer(L1DoorInstance door) {
			_door = door;
		}

		@Override
		public void run() {
			if (_door.getOpenStatus() == ActionCodes.ACTION_Open) {
				_door.close();
			}
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 5 * 1000);
		}
	}

	@Override
	public String getType() {
		return C_DOOR;
	}
}
