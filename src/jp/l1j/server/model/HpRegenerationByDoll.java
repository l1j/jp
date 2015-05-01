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

package jp.l1j.server.model;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.instance.L1DollInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.templates.L1MagicDoll;

public class HpRegenerationByDoll extends TimerTask {
	private static Logger _log = Logger.getLogger(HpRegenerationByDoll.class.getName());

	private final L1PcInstance _pc;
	private final L1DollInstance _doll;

	public HpRegenerationByDoll(L1PcInstance pc, L1DollInstance doll) {
		_pc = pc;
		_doll = doll;
	}

	@Override
	public void run() {
		try {
			if (_pc.isDead()) {
				return;
			}
			regenHp();
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void regenHp() {
		int newHp = _pc.getCurrentHp() + L1MagicDoll.getHprByDoll(_doll);
		if (newHp < 0) {
			newHp = 0;
		}
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 6321));
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 6321));
		_pc.setCurrentHp(newHp);
	}
}