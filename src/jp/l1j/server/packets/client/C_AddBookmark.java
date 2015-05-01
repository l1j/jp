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
import jp.l1j.server.model.L1CastleLocation;
import jp.l1j.server.model.L1HouseLocation;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1BookMark;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_AddBookmark extends ClientBasePacket {

	private static final String C_ADD_BOOKMARK = "[C] C_AddBookmark";
	private static Logger _log = Logger.getLogger(C_AddBookmark.class
			.getName());

	public C_AddBookmark(byte[] decrypt, ClientThread client) {
		super(decrypt);
		String s = readS();

		L1PcInstance pc = client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}

		if (pc.getMap().isMarkable() || pc.isGm()) {
			if ((L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc
					.getMapId()) || L1HouseLocation.isInHouse(pc.getX(), pc
					.getY(), pc.getMapId()) || (L1Location.noMarkableLocation(pc.getX(), pc.getY(), pc
					.getMapId())) && !pc.isGm())) {
				// \f1ここを記憶することができません。
				pc.sendPackets(new S_ServerMessage(214));
			} else {
				L1BookMark.addBookmark(pc, s);
			}
		} else {
			// \f1ここを記憶することができません。
			pc.sendPackets(new S_ServerMessage(214));
		}
	}

	@Override
	public String getType() {
		return C_ADD_BOOKMARK;
	}
}
