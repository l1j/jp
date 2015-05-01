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

import java.util.logging.Logger;
import java.util.Timer;
import java.util.TimerTask;

import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_Disconnect;

public class L1PcDeleteTimer extends TimerTask {
	private static Logger _log = Logger.getLogger(L1PcDeleteTimer.class
			.getName());

	public L1PcDeleteTimer(L1PcInstance pc) {
		_pc = pc;
	}

	@Override
	public void run() {
		_pc.saveInventory();
		_pc.sendPackets(new S_Disconnect());
		this.cancel();
	}

	public void begin() {
		Timer timer = new Timer();
		timer.schedule(this, 10 * 60 * 1000);
	}

	private final L1PcInstance _pc;

}
