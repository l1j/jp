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
import jp.l1j.server.datatables.DungeonTable;
import jp.l1j.server.model.instance.L1PcInstance;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_EnterPortal extends ClientBasePacket {

	private static final String C_ENTER_PORTAL = "[C] C_EnterPortal";
	private static Logger _log = Logger.getLogger(C_EnterPortal.class
			.getName());

	public C_EnterPortal(byte abyte0[], ClientThread client)
			throws Exception {
		super(abyte0);
		int locx = readH();
		int locy = readH();
		L1PcInstance pc = client.getActiveChar();
		if (pc.isTeleport()) { // テレポート処理中
			return;
		}
		// ダンジョンにテレポート
		DungeonTable.getInstance().dg(locx, locy, pc.getMap().getId(), pc);
	}

	@Override
	public String getType() {
		return C_ENTER_PORTAL;
	}
}
