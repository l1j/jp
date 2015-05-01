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

package jp.l1j.server.packets.client;

import jp.l1j.server.ClientThread;
import jp.l1j.server.controller.timer.FishingTimeController;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_CharVisualUpdate;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_FishClick extends ClientBasePacket {

	private static final String C_FISHCLICK = "[C] C_FishClick";

	public C_FishClick(byte abyte0[], ClientThread clientthread) throws Exception {
		super(abyte0);
		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null || pc.isDead()) {
			return;
		}
		pc.setFishingTime(0);
		pc.setFishingReady(false);
		pc.setFishing(false);
		pc.sendPackets(new S_CharVisualUpdate(pc));
		pc.broadcastPacket(new S_CharVisualUpdate(pc));
		FishingTimeController.getInstance().removeMember(pc);
	}

	@Override
	public String getType() {
		return C_FISHCLICK;
	}
}
