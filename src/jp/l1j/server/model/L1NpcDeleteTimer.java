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

import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.packets.server.S_DoActionGFX;

public class L1NpcDeleteTimer extends TimerTask {
	private static Logger _log = Logger.getLogger(L1NpcDeleteTimer.class
			.getName());

	public L1NpcDeleteTimer(L1NpcInstance npc, int timeMillis) {
		_npc = npc;
		_timeMillis = timeMillis;
	}

	@Override
	public void run() {
		if (_npc != null) {
			if (_npc.getNpcId() == 91051 // ドラゴンポータル(地)
				|| _npc.getNpcId() == 91052 // ドラゴンポータル(水)
				|| _npc.getNpcId() == 91053 // ドラゴンポータル(風)
				|| _npc.getNpcId() == 91054 // ドラゴンポータル(火)
				//|| _npc.getNpcId() == 81277 // TODO 3.52C unknown npc
			) {
				if (_npc.getNpcId() == 91066) { // ドラゴンポータル(隠された竜の地)
					L1DragonSlayer.getInstance().setHiddenDragonValleyStatus(0);
				}
				L1DragonSlayer.getInstance().setPortalPack(_npc.getPortalNumber(), null);
				L1DragonSlayer.getInstance().endDragonPortal(_npc.getPortalNumber());
				_npc.setStatus(ActionCodes.ACTION_Die);
				_npc.broadcastPacket(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Die));
			}
			_npc.deleteMe();
			cancel();
		}
	}

	public void begin() {
		Timer timer = new Timer();
		timer.schedule(this, _timeMillis);
	}

	private final L1NpcInstance _npc;
	private final int _timeMillis;
}
