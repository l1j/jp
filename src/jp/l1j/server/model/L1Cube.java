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
package jp.l1j.server.model;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.skill.L1SkillId;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_Paralysis;
import jp.l1j.server.packets.server.S_SpMr;
import static jp.l1j.server.model.skill.L1SkillId.*;

public class L1Cube extends TimerTask {
	private static Logger _log = Logger.getLogger(L1Cube.class.getName());

	private ScheduledFuture<?> _future = null;
	private int _timeCounter = 0;
	private final L1Character _effect;
	private final L1Character _cha;
	private final int _skillId;

	public L1Cube(L1Character effect, L1Character cha, int skillId) {
		_effect = effect;
		_cha = cha;
		_skillId = skillId;
	}

	@Override
	public void run() {
		try {
			if (_cha.isDead()) {
				stop();
				return;
			}
			if (!_cha.hasSkillEffect(_skillId)) {
				stop();
				return;
			}
			_timeCounter++;
			giveEffect();
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void begin() {
		// 効果時間が8秒のため、4秒毎のスキルの場合処理時間を考慮すると実際には1回しか効果が現れない
		// よって開始時間を0.9秒後に設定しておく
		_future = GeneralThreadPool.getInstance().scheduleAtFixedRate(this,
				900, 1000);
	}

	public void stop() {
		if (_future != null) {
			_future.cancel(false);
		}
	}

	public void giveEffect() {
		if (_skillId == STATUS_CUBE_IGNITION_TO_ENEMY) {
			if (_timeCounter % 4 != 0) {
				return;
			}
			if (_cha.hasSkillEffect(STATUS_FREEZE)) {
				return;
			}
			if (_cha.hasSkillEffect(ABSOLUTE_BARRIER)) {
				return;
			}
			if (_cha.hasSkillEffect(ICE_LANCE)) {
				return;
			}
			if (_cha.hasSkillEffect(FREEZING_BLIZZARD)) {
				return;
			}
			if (_cha.hasSkillEffect(EARTH_BIND)) {
				return;
			}

			if (_cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) _cha;
				pc.sendPackets(new S_DoActionGFX(pc.getId(),
						ActionCodes.ACTION_Damage));
				pc.broadcastPacket(new S_DoActionGFX(pc.getId(),
						ActionCodes.ACTION_Damage));
				pc.receiveDamage(_effect, 10, false);
			} else if (_cha instanceof L1MonsterInstance) {
				L1MonsterInstance mob = (L1MonsterInstance) _cha;
				mob.broadcastPacket(new S_DoActionGFX(mob.getId(),
						ActionCodes.ACTION_Damage));
				mob.receiveDamage(_effect, 10);
			}
		} else if (_skillId == STATUS_CUBE_QUAKE_TO_ENEMY) {
			if (_timeCounter % 4 != 0) {
				return;
			}
			if (_cha.hasSkillEffect(STATUS_FREEZE)) {
				return;
			}
			if (_cha.hasSkillEffect(ABSOLUTE_BARRIER)) {
				return;
			}
			if (_cha.hasSkillEffect(ICE_LANCE)) {
				return;
			}
			if (_cha.hasSkillEffect(FREEZING_BLIZZARD)) {
				return;
			}
			if (_cha.hasSkillEffect(EARTH_BIND)) {
				return;
			}

			if (_cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) _cha;
				pc.setSkillEffect(STATUS_FREEZE, 1000);
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
			} else if (_cha instanceof L1MonsterInstance) {
				L1MonsterInstance mob = (L1MonsterInstance) _cha;
				mob.setSkillEffect(STATUS_FREEZE, 1000);
				mob.setParalyzed(true);
			}
		} else if (_skillId == STATUS_CUBE_SHOCK_TO_ENEMY) {
// if (_timeCounter % 5 != 0) {
// return;
// }
// _cha.addMr(-10);
// if (_cha instanceof L1PcInstance) {
// L1PcInstance pc = (L1PcInstance) _cha;
// pc.sendPackets(new S_SpMr(pc));
// }
			_cha.setSkillEffect(STATUS_MR_REDUCTION_BY_CUBE_SHOCK, 4000);
		} else if (_skillId == STATUS_CUBE_BALANCE) {
			if (_timeCounter % 4 == 0) {
				if (_cha.getCurrentHp() > 50) { // HP50以下の場合は適用しない
					int newMp = _cha.getCurrentMp() + 5;
					if (newMp < 0) {
						newMp = 0;
					}
					_cha.setCurrentMp(newMp);
				}
			}
			if (_timeCounter % 5 == 0) {
				if (_cha instanceof L1PcInstance) {
					if (_cha.getCurrentHp() > 50) { // HP50以下の場合は適用しない
						L1PcInstance pc = (L1PcInstance) _cha;
						pc.receiveDamage(_effect, 25, false);
					}
				} else if (_cha instanceof L1MonsterInstance) {
					L1MonsterInstance mob = (L1MonsterInstance) _cha;
					mob.receiveDamage(_effect, 25);
				}
			}
		}
	}

}
