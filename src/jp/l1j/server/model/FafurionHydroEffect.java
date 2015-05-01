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

import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SkillSound;

public class FafurionHydroEffect extends TimerTask {
	private static Logger _log = Logger.getLogger(FafurionHydroEffect.class
			.getName());

	private final L1PcInstance _pc;

	public FafurionHydroEffect(L1PcInstance pc) {
		_pc = pc;
	}

	@Override
	public void run() {
		try {
			if (_pc.isDead()) {
				return;
			}
			effect();
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void effect() {
		_pc.sendPackets(new S_SkillSound(_pc.getId(), 2245));
		_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 2245));
	}
}