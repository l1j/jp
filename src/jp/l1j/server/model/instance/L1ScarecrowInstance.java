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
package jp.l1j.server.model.instance;

import java.util.ArrayList;
import java.util.logging.Logger;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.packets.server.S_ChangeHeading;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.CalcExp;

public class L1ScarecrowInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1ScarecrowInstance.class
			.getName());

	public L1ScarecrowInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance pc) {
		onAction(pc, 0);
	}

	@Override
	public void onAction(L1PcInstance pc, int skillId) {
		L1Attack attack = new L1Attack(pc, this, skillId);

		if (attack.calcHit()) {
			attack.calcDamage();
			attack.calcStaffOfMana();
			attack.addPcPoisonAttack(pc, this);
			attack.addChaserAttack();
			attack.addEvilAttack();

			if (pc.getLevel() < 5) { // ＬＶ制限もうける場合はここを変更
				ArrayList<L1Character> targetList = new ArrayList<L1Character>();
				targetList.add(pc);
				ArrayList<Integer> hateList = new ArrayList<Integer>();
				hateList.add(1);
				CalcExp.calcExp(pc, getId(), targetList, hateList, getExp());
			}
			if (getHeading() < 7) { // 今の向きを取得
				setHeading(getHeading() + 1); // 今の向きを設定
			} else {
				setHeading(0); // 今の向きが7 以上になると今の向きを0に戻す
			}
			broadcastPacket(new S_ChangeHeading(this)); // 向きの変更
		}
		attack.action();
		attack.commit();
	}

	@Override
	public void onTalkAction(L1PcInstance l1pcinstance) {

	}

	public void onFinalAction() {

	}

	public void doFinalAction() {
	}
}