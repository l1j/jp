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

import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.AcceleratorChecker;
import jp.l1j.server.datatables.DungeonTable;
import jp.l1j.server.datatables.RandomDungeonTable;
import jp.l1j.server.model.instance.L1PcInstance;
import static jp.l1j.server.model.instance.L1PcInstance.REGENSTATE_MOVE;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.trap.L1WorldTraps;
import jp.l1j.server.packets.server.S_MoveCharPacket;
import jp.l1j.server.packets.server.S_SystemMessage;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_MoveChar extends ClientBasePacket {

	private static Logger _log = Logger.getLogger(C_MoveChar.class.getName());

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	private static final int CLIENT_LANGUAGE = Config.CLIENT_LANGUAGE;

	// マップタイル調査用
	private void sendMapTileLog(L1PcInstance pc) {
		pc.sendPackets(new S_SystemMessage(pc.getMap().toString(
				pc.getLocation())));
	}

	// 移動
	public C_MoveChar(byte decrypt[], ClientThread client)
			throws Exception {
		super(decrypt);
		int locx = readH();
		int locy = readH();
		int heading = readC();

		L1PcInstance pc = client.getActiveChar();

		if (pc == null || pc.isTeleport()) { // テレポート処理中
			return;
		}

		// 移動要求間隔をチェックする
		if (Config.CHECK_MOVE_INTERVAL) {
			int result;
			result = pc.getAcceleratorChecker()
					.checkInterval(AcceleratorChecker.ACT_TYPE.MOVE);
			if (result == AcceleratorChecker.R_DISPOSED) {
				return;
			}
		}

		pc.killSkillEffectTimer(MEDITATION);
		pc.setCallClanId(0); // コールクランを唱えた後に移動すると召喚無効

		if (!pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // アブソルートバリア中ではない
			pc.setRegenState(REGENSTATE_MOVE);
		}
		pc.getMap().setPassable(pc.getLocation(), true);

		if (CLIENT_LANGUAGE == 3) { // Taiwan Only
			heading ^= 0x49;
			locx = pc.getX();
			locy = pc.getY();
		}

		locx += HEADING_TABLE_X[heading];
		locy += HEADING_TABLE_Y[heading];

		if (DungeonTable.getInstance().dg(locx, locy, pc.getMap().getId(), pc)) { // ダンジョンにテレポートした場合
			return;
		}
		if (RandomDungeonTable.getInstance().dg(locx, locy, pc.getMap().getId(),
				pc)) { // テレポート先がランダムなテレポート地点
			return;
		}

		pc.getLocation().set(locx, locy);
		pc.setHeading(heading);
		if (pc.isGmInvis() || pc.isGhost()) {
		} else if (pc.isInvisble()) {
			pc.broadcastPacketForFindInvis(new S_MoveCharPacket(pc), true);
		} else {
			pc.broadcastPacket(new S_MoveCharPacket(pc));
		}

		// sendMapTileLog(pc); // 移動先タイルの情報を送る(マップ調査用)

		// TODO ペットレース用　start
				jp.l1j.server.model.L1PolyRace.getInstance().checkLapFinish(pc);
		// TODO ペットレース用　end
		L1WorldTraps.getInstance().onPlayerMoved(pc);

		pc.getMap().setPassable(pc.getLocation(), false);
		// user.UpdateObject(); // 可視範囲内の全オブジェクト更新
	}
}