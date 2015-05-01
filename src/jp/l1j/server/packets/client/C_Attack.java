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
import jp.l1j.server.model.AcceleratorChecker;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import static jp.l1j.server.model.instance.L1PcInstance.REGENSTATE_ATTACK;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_AttackPacket;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_UseArrowSkill;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_Attack extends ClientBasePacket {

	private static Logger _log = Logger.getLogger(C_Attack.class.getName());

	private int _targetX = 0;

	private int _targetY = 0;

	public C_Attack(byte[] decrypt, ClientThread client) {
		super(decrypt);
		int targetId = readD();
		int x = readH();
		int y = readH();
		_targetX = x;
		_targetY = y;

		L1PcInstance pc = client.getActiveChar();

		if (pc.isGhost() || pc.isDead() || pc.isTeleport()
				|| pc.isParalyzed() || pc.isSleeped()) {
			return;
		}

		L1Object target = L1World.getInstance().findObject(targetId);

		// 攻撃アクションをとれる状態か確認
		if (pc.getInventory().getWeight240() >= 197) { // 重量オーバー
			pc.sendPackets(new S_ServerMessage(110)); // \f1アイテムが重すぎて戦闘することができません。
			return;
		}

		if (pc.isInvisble()) { // インビジビリティ、ブラインドハイディング中
			return;
		}

		if (pc.isInvisDelay()) { // インビジビリティディレイ中
			return;
		}

		if (target instanceof L1Character) {
			if (target.getMapId() != pc.getMapId()
					|| pc.getLocation().getLineDistance(target.getLocation()) > 20D) { // ターゲットが異常な場所にいたら終了
				return;
			}
		}

		if (target instanceof L1NpcInstance) {
			int hiddenStatus = ((L1NpcInstance) target).getHiddenStatus();
			if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK
					|| hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY) { // 地中に潜っているか、飛んでいる
				return;
			}
		}

		// 攻撃要求間隔をチェックする
		if (Config.CHECK_ATTACK_INTERVAL) {
			int result;
			result = pc.getAcceleratorChecker().checkInterval(
					AcceleratorChecker.ACT_TYPE.ATTACK);
			if (result == AcceleratorChecker.R_DISPOSED) {
				return;
			}
		}

		// 攻撃アクションがとれる場合の処理
		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // アブソルート バリアの解除
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
			pc.startHpRegenerationByDoll();
			pc.startMpRegenerationByDoll();
		}
		pc.killSkillEffectTimer(MEDITATION);

		pc.delInvis(); // 透明状態の解除

		pc.setRegenState(REGENSTATE_ATTACK);

		if (target != null && !((L1Character) target).isDead()) {
			target.onAction(pc);
		} else { // 空攻撃
			L1ItemInstance weapon = pc.getWeapon();
			int weaponId = 0;
			int weaponType = 0;
			L1ItemInstance arrow = null;
			L1ItemInstance sting = null;
			if (weapon != null) {
				weaponId = weapon.getItem().getItemId();
				weaponType = weapon.getItem().getType1();
				if (weaponType == 20) { // 弓
					arrow = pc.getInventory().getArrow();
				}
				if (weaponType == 62) { // ガントレット
					sting = pc.getInventory().getSting();
				}
			}
			pc.setHeading(pc.targetDirection(x, y));
			if (weaponType == 20 && (weaponId == 190 || arrow != null)) {
				calcOrbit(pc.getX(), pc.getY(), pc.getHeading()); // 軌道計算
				if (arrow != null) { // 矢がある場合
					pc.sendPackets(new S_UseArrowSkill(pc, 0, 66, _targetX,
							_targetY, true));
					pc.broadcastPacket(new S_UseArrowSkill(pc, 0, 66, _targetX,
							_targetY, true));
					pc.getInventory().removeItem(arrow, 1);
				} else if (weaponId == 190) { // /L1J2/src/l1j/server/server/clientpackets/C_Attack.java
					pc.sendPackets(new S_UseArrowSkill(pc, 0, 2349, _targetX,
							_targetY, true));
					pc.broadcastPacket(new S_UseArrowSkill(pc, 0, 2349,
							_targetX, _targetY, true));
				}
			} else if (weaponType == 62 && sting != null) {
				calcOrbit(pc.getX(), pc.getY(), pc.getHeading()); // 軌道計算
				pc.sendPackets(new S_UseArrowSkill(pc, 0, 2989, _targetX,
						_targetY, true));
				pc.broadcastPacket(new S_UseArrowSkill(pc, 0, 2989, _targetX,
						_targetY, true));
				pc.getInventory().removeItem(sting, 1);
			} else {
				pc.sendPackets(new S_AttackPacket(pc, 0,
						ActionCodes.ACTION_Attack));
				pc.broadcastPacket(new S_AttackPacket(pc, 0,
						ActionCodes.ACTION_Attack));
			}
		}
	}

	private void calcOrbit(int cX, int cY, int head) {
		float disX = Math.abs(cX - _targetX);
		float disY = Math.abs(cY - _targetY);
		float dis = Math.max(disX, disY);
		float avgX = 0;
		float avgY = 0;
		if (dis == 0) {
			if (head == 1) {
				avgX = 1;
				avgY = -1;
			} else if (head == 2) {
				avgX = 1;
				avgY = 0;
			} else if (head == 3) {
				avgX = 1;
				avgY = 1;
			} else if (head == 4) {
				avgX = 0;
				avgY = 1;
			} else if (head == 5) {
				avgX = -1;
				avgY = 1;
			} else if (head == 6) {
				avgX = -1;
				avgY = 0;
			} else if (head == 7) {
				avgX = -1;
				avgY = -1;
			} else if (head == 0) {
				avgX = 0;
				avgY = -1;
			}
		} else {
			avgX = disX / dis;
			avgY = disY / dis;
		}

		int addX = (int) Math.floor((avgX * 15) + 0.59f);
		int addY = (int) Math.floor((avgY * 15) + 0.59f);

		if (cX > _targetX) {
			addX *= -1;
		}
		if (cY > _targetY) {
			addY *= -1;
		}

		_targetX = _targetX + addX;
		_targetY = _targetY + addY;
	}
}
