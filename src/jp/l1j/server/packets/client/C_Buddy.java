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

import java.util.logging.Logger;
import jp.l1j.server.ClientThread;
import jp.l1j.server.datatables.BuddyTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Buddy;
import jp.l1j.server.packets.server.S_Buddy;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_Buddy extends ClientBasePacket {

	private static final String C_BUDDY = "[C] C_Buddy";
	private static Logger _log = Logger.getLogger(C_Buddy.class.getName());

	public C_Buddy(byte abyte0[], ClientThread clientthread) {
		super(abyte0);
		L1PcInstance pc = clientthread.getActiveChar();
		L1Buddy buddy = BuddyTable.getInstance().getBuddyTable(
				pc.getId());
		pc.sendPackets(new S_Buddy(pc.getId(), buddy));
	}

	@Override
	public String getType() {
		return C_BUDDY;
	}

}
