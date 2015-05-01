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

import java.util.logging.Logger;
import jp.l1j.server.packets.server.S_Board;
import jp.l1j.server.packets.server.S_BoardRead;
import jp.l1j.server.templates.L1Npc;

public class L1BoardInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger _log = Logger.getLogger(L1BoardInstance.class.getName());

	public L1BoardInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		player.sendPackets(new S_Board(getId()));
	}

	@Override
	public void onAction(L1PcInstance player, int number) {
		player.sendPackets(new S_Board(getId(), number));
	}

	public void onActionRead(L1PcInstance player, int number) {
		player.sendPackets(new S_BoardRead(number));
	}
}
