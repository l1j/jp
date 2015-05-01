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
import jp.l1j.server.packets.server.S_ChangeHeading;

public class C_ChangeHeading extends ClientBasePacket {
	
	private static final String C_CHANGE_HEADING = "[C] C_ChangeHeading";
	
	private static Logger _log = Logger.getLogger(C_ChangeHeading.class.getName());

	public C_ChangeHeading(byte[] decrypt, ClientThread client) {
		super(decrypt);
		int heading = readC();
		L1PcInstance pc = client.getActiveChar();
		pc.setHeading(heading);
		_log.finest("Change Heading: " + pc.getHeading());
		if (pc.isGmInvis() || pc.isGhost()) {
		} else if (pc.isInvisble()) {
			pc.broadcastPacketForFindInvis(new S_ChangeHeading(pc), true);
		} else {
			pc.broadcastPacket(new S_ChangeHeading(pc));
		}
	}

	@Override
	public String getType() {
		return C_CHANGE_HEADING;
	}
}