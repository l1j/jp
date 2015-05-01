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
import jp.l1j.server.packets.server.S_Party;

public class L1PartyRefresh extends TimerTask {
	private static Logger _log = Logger.getLogger(L1PartyRefresh.class
			.getName());

	private final L1PcInstance _pc;

	public L1PartyRefresh(L1PcInstance pc) {
		_pc = pc;
	}

	/**
	 * 3.3C
	 */
	public void fresh() {
		_pc.sendPackets(new S_Party(110, _pc));
	}

	@Override
	public void run() {
		try {
			if (_pc.isDead() || _pc.getParty() == null) {
				_pc.stopRefreshParty();
				return;
			}
			fresh();
		} catch (Throwable e) {
			_pc.stopRefreshParty();
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}
}
