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
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_SelectTarget extends ClientBasePacket {

	private static final String C_SELECT_TARGET = "[C] C_SelectTarget";
	private static Logger _log = Logger.getLogger(C_SelectTarget.class
			.getName());

	public C_SelectTarget(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);

		int petId = readD();
		int type = readC();
		int targetId = readD();

		L1PetInstance pet = (L1PetInstance) L1World.getInstance().findObject(
				petId);
		L1Character target = (L1Character) L1World.getInstance().findObject(
				targetId);

		if (pet != null && target != null) {
			if (target instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) target;
				// 目標在安區、攻擊者在安區、NOPVP
				if ((pc.getZoneType() == 1) || (pet.getZoneType() == 1)
						|| (pc.checkNonPvP(pc, pet))) {
					// ペットオーナー
					if (pet.getMaster() instanceof L1PcInstance) {
						L1PcInstance petMaster = (L1PcInstance) pet.getMaster();
						petMaster.sendPackets(new S_ServerMessage(328)); // 正しい対象を選択してください。
					}
					return;
				}
			}
			// ペット攻撃目標
			else if (target instanceof L1PetInstance) {
				L1PetInstance targetPet = (L1PetInstance) target;
				// 攻撃目標指定時ゾーン判定
				if ((targetPet.getZoneType() == 1) || (pet.getZoneType() == 1)) {
					// ペットオーナー
					if (pet.getMaster() instanceof L1PcInstance) {
						L1PcInstance petMaster = (L1PcInstance) pet.getMaster();
						petMaster.sendPackets(new S_ServerMessage(328));
						// 正しい対象を選択してください。
					}
					return;
				}
			}
			// サモン攻撃目標
			else if (target instanceof L1SummonInstance) {
				L1SummonInstance targetSummon = (L1SummonInstance) target;
				// 攻撃目標指定自ゾーン判定
				if ((targetSummon.getZoneType() == 1)
						|| (pet.getZoneType() == 1)) {
					// サモンオーナー
					if (pet.getMaster() instanceof L1PcInstance) {
						L1PcInstance petMaster = (L1PcInstance) pet.getMaster();
						petMaster.sendPackets(new S_ServerMessage(328)); // 正しい対象を選択してください。
					}
					return;
				}
			}
			// ティムモンスター攻撃目標
			else if (target instanceof L1MonsterInstance) {
				L1MonsterInstance mob = (L1MonsterInstance) target;
				if (pet.getMaster().isAttackMiss(pet.getMaster(),
						mob.getNpcId())) {
					if (pet.getMaster() instanceof L1PcInstance) {
						L1PcInstance petMaster = (L1PcInstance) pet.getMaster();
						petMaster.sendPackets(new S_ServerMessage(328)); // 正しい対象を選択してください。
					}
					return;
				}
			}
			pet.setMasterTarget(target);
		}
	}

	@Override
	public String getType() {
		return C_SELECT_TARGET;
	}
}