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

import static jp.l1j.server.model.skill.L1SkillId.*;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_EffectLocation;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;

public class L1Evil extends TimerTask {
	private static Logger _log = Logger.getLogger(L1Evil.class.getName());

	private ScheduledFuture<?> _future = null;
	private int _timeCounter = 0;
	private final L1PcInstance _pc;
	private final L1Character _cha;
	private final int _attr;
	private final int _gfxid;
	private L1NpcInstance _targetNpc = null;
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	public L1Evil(L1PcInstance pc, L1Character cha, int attr, int gfxid) {
		_cha = cha;
		_pc = pc;
		_attr = attr;
		_gfxid = gfxid;
	}

	@Override
	public void run() {
		try {
			if (_cha == null || _cha.isDead()) {
				stop();
				return;
			}
			if (_cha.hasSkillEffect(COUNTER_MAGIC)) {
				_cha.removeSkillEffect(COUNTER_MAGIC);
				int castgfx = 10702;
				_cha.broadcastPacket(new S_SkillSound(_cha.getId(), castgfx));
				if (_cha instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) _cha;
					pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
				}
				_cha.setEvilHitting(false);
				return;
			}
			_cha.setEvilHitting(true);
			attack();
			_timeCounter++;
			if (_timeCounter >= 3) {
				_cha.setEvilHitting(false);
				stop();
				return;
			}
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void begin() {
		// 効果時間が8秒のため、4秒毎のスキルの場合処理時間を考慮すると実際には1回しか効果が現れない
		// よって開始時間を0.9秒後に設定しておく
		_future = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, 0,
				1000);
	}

	public void stop() {
		if (_future != null) {
			_future.cancel(false);
		}
	}

	public void attack() {
		double damage = getDamage(_pc, _cha);
		if (_gfxid != 8152) {
			if (_cha.getCurrentHp() - (int) damage <= 0 && _cha.getCurrentHp() != 1) {
				damage = _cha.getCurrentHp();
			} else if (_cha.getCurrentHp() == 1) {
				damage = 1;
			}
			if (_gfxid == 8150) { // イビルリバース時、ＨＰを吸収する
				_pc.setCurrentHp(_pc.getCurrentHp() + (int) damage);
			}
		} else {
			if (_cha.getCurrentMp() - (int) damage <= 0 && _cha.getCurrentHp() != 1) {
				damage = _cha.getCurrentMp();
			} else if (_cha.getCurrentMp() == 1) {
				damage = 1;
			}
			if (_cha instanceof L1NpcInstance) { // 対象がＮＰＣの場合はＭＰ吸収限界を設定（マナスタッフと同様？）
				_targetNpc = (L1NpcInstance)_cha;
				if(_targetNpc.drainMana((int) damage) > 0) {
					_cha.setCurrentMp(_cha.getCurrentMp() - (int) damage);
					_pc.setCurrentMp(_pc.getCurrentMp() + (int) damage);
				}
			} else {
				_cha.setCurrentMp(_cha.getCurrentMp() - (int) damage);
				_pc.setCurrentMp(_pc.getCurrentMp() + (int) damage);
			}
		}
		S_EffectLocation packet = new S_EffectLocation(_cha.getX(),
				_cha.getY(), _gfxid);
		_pc.sendPackets(packet);
		_pc.broadcastPacket(packet);
		if (_cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) _cha;
			pc.sendPackets(new S_DoActionGFX(pc.getId(),
					ActionCodes.ACTION_Damage));
			pc.broadcastPacket(new S_DoActionGFX(pc.getId(),
					ActionCodes.ACTION_Damage));
			pc.receiveDamage(_pc, damage, false);
		} else if (_cha instanceof L1NpcInstance) {
			L1NpcInstance npc = (L1NpcInstance) _cha;
			npc.broadcastPacket(new S_DoActionGFX(npc.getId(),
					ActionCodes.ACTION_Damage));
			npc.receiveDamage(_pc, (int) damage);
		}
	}

	public double getDamage(L1PcInstance pc, L1Character cha) {
		double dmg = 0;
		L1Magic _magic = new L1Magic(pc, cha);
		if (_gfxid == 8150) {
			dmg = _magic.calcMagicDamage(10161);
		} else if (_gfxid == 8152) { // イビリトリックの場合、MP吸収率は１～４
			dmg = (double)_random.nextInt(3) + 1;
		}

		cha.removeSkillEffect(ERASE_MAGIC);

		return dmg;
	}

}
