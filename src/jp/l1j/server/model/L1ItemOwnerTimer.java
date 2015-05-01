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

import jp.l1j.server.model.instance.L1ItemInstance;

public class L1ItemOwnerTimer extends TimerTask {
	private static Logger _log = Logger.getLogger(L1ItemOwnerTimer.class
			.getName());

	public L1ItemOwnerTimer(L1ItemInstance item, int timeMillis) {
		_item = item;
		_timeMillis = timeMillis;
	}

	@Override
	public void run() {
		_item.setItemOwnerId(0);
		this.cancel();
	}

	public void begin() {
		Timer timer = new Timer();
		timer.schedule(this, _timeMillis);
	}

	private final L1ItemInstance _item;
	private final int _timeMillis;
}
