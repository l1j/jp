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
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1World;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_CallPlayer extends ClientBasePacket {

	private static final String C_CALL = "[C] C_Call";

	private static Logger _log = Logger.getLogger(C_CallPlayer.class.getName());

	public C_CallPlayer(byte[] decrypt, ClientThread client) {
		super(decrypt);
		L1PcInstance pc = client.getActiveChar();

		if (!pc.isGm()) {
			return;
		}

		String name = readS();
		if (name.isEmpty()) {
			return;
		}

		L1PcInstance target = L1World.getInstance().getPlayer(name);

		if (target == null) {
			return;
		}

		L1Location loc =
				L1Location.randomLocation(target.getLocation(), 1, 2, false);
		L1Teleport.teleport(pc, loc.getX(), loc.getY(), target.getMapId(), pc
				.getHeading(), false);
	}

	@Override
	public String getType() {
		return C_CALL;
	}
}
