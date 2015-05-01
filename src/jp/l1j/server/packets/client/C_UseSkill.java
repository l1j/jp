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
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.AcceleratorChecker;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1World;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_UseSkill extends ClientBasePacket {

	private static Logger _log = Logger.getLogger(C_UseSkill.class.getName());

	public C_UseSkill(byte abyte0[], ClientThread client) throws Exception {
		super(abyte0);
		int row = readC();
		int column = readC();
		int skillId = (row * 8) + column + 1;
		String charName = null;
		String message = null;
		int targetId = 0;
		int targetX = 0;
		int targetY = 0;
		L1PcInstance pc = client.getActiveChar();

		if (pc.isTeleport() || pc.isDead()) {
			return;
		}
		if (!pc.getMap().isUsableSkill()) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1ここでは使えません。
			return;
		}
		if (!pc.isSkillMastery(skillId)) {
			return;
		}

		// 要求間隔をチェックする
		if (Config.CHECK_SPELL_INTERVAL) {
			int result;
			// FIXME どのスキルがdir/no dirであるかの判断が適当
			if (SkillTable.getInstance().findBySkillId(skillId).getActionId() ==
						ActionCodes.ACTION_SkillAttack) {
				result = pc.getAcceleratorChecker().checkInterval(
						AcceleratorChecker.ACT_TYPE.SPELL_DIR);
			} else {
				result = pc.getAcceleratorChecker().checkInterval(
						AcceleratorChecker.ACT_TYPE.SPELL_NODIR);
			}
			if (result == AcceleratorChecker.R_DISPOSED) {
				return;
			}
		}

		if (abyte0.length > 4) {
			try {
				if (skillId == CALL_CLAN || skillId == RUN_CLAN) { // コールクラン、ランクラン
					String tempName[] = readS().split("\\[");
					charName = tempName[0];
				} else if (skillId == TRUE_TARGET) { // トゥルーターゲット
					targetId = readD();
					targetX = readH();
					targetY = readH();
					message = readS();
				} else if (skillId == TELEPORT || skillId == MASS_TELEPORT) { // テレポート、マステレポート
					readH(); // MapID
					targetId = readD(); // Bookmark ID
				} else if (skillId == FIRE_WALL || skillId == LIFE_STREAM) { // ファイアーウォール、ライフストリーム
					targetX = readH();
					targetY = readH();
				} else {
					targetId = readD();
					targetX = readH();
					targetY = readH();
				}
			} catch (Exception e) {
				// _log.log(Level.SEVERE, "", e);
			}
		}

		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // アブソルート バリアの解除
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
			pc.startHpRegenerationByDoll();
			pc.startMpRegenerationByDoll();
		}
		pc.killSkillEffectTimer(MEDITATION);

		try {
			if (skillId == CALL_CLAN || skillId == RUN_CLAN) { // コールクラン、ランクラン
				if (charName.isEmpty()) {
					// 名前が空の場合クライアントで弾かれるはず
					return;
				}

				L1PcInstance target = L1World.getInstance().getPlayer(charName);

				if (target == null) {
					// メッセージが正確であるか未調査
					pc.sendPackets(new S_ServerMessage(73, charName)); // \f1%0はゲームをしていません。
					return;
				}
				if (pc.getClanId() != target.getClanId()) {
					pc.sendPackets(new S_ServerMessage(414)); // 同じ血盟員ではありません。
					return;
				}
				targetId = target.getId();
				if (skillId == CALL_CLAN) {
					// 移動せずに連続して同じクラン員にコールクランした場合、向きは前回の向きになる
					int callClanId = pc.getCallClanId();
					if (callClanId == 0 || callClanId != targetId) {
						pc.setCallClanId(targetId);
						pc.setCallClanHeading(pc.getHeading());
					}
				}
			}
			L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(pc, skillId, targetId, targetX, targetY,
					message, 0, L1SkillUse.TYPE_NORMAL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
