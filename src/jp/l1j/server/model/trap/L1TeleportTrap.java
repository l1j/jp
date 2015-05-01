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
package jp.l1j.server.model.trap;

import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.storage.TrapStorage;

public class L1TeleportTrap extends L1Trap {
	private final L1Location _loc;

	public L1TeleportTrap(TrapStorage storage) {
		super(storage);

		int x = storage.getInt("teleport_x");
		int y = storage.getInt("teleport_y");
		int mapId = storage.getInt("teleport_map_id");
		_loc = new L1Location(x, y, mapId);
	}

	@Override
	public void onTrod(L1PcInstance trodFrom, L1Object trapObj) {
		sendEffect(trapObj);

		L1Teleport.teleport(trodFrom, _loc.getX(), _loc.getY(), (short) _loc
				.getMapId(), 5, true);
	}
}
