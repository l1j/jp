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

package jp.l1j.server.packets.server;

import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.L1Character;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_MoveCharPacket extends ServerBasePacket {

	private static final String _S__1F_MOVECHARPACKET = "[S] S_MoveCharPacket";

	private static Logger _log = Logger.getLogger(S_MoveCharPacket.class
			.getName());

	private byte[] _byte = null;

	public S_MoveCharPacket(L1Character cha) {
		int x = cha.getX();
		int y = cha.getY();
		// if(cha instanceof L1PcInstance)
		// {

		switch (cha.getHeading()) {
		case 1: // '\001'
			x--;
			y++;
			break;

		case 2: // '\002'
			x--;
			break;

		case 3: // '\003'
			x--;
			y--;
			break;

		case 4: // '\004'
			y--;
			break;

		case 5: // '\005'
			x++;
			y--;
			break;

		case 6: // '\006'
			x++;
			break;

		case 7: // '\007'
			x++;
			y++;
			break;

		case 0: // '\0'
			y++;
			break;
		}

		writeC(Opcodes.S_OPCODE_MOVEOBJECT);
		writeD(cha.getId());
		writeH(x);
		writeH(y);
		writeC(cha.getHeading());
		writeC(129);
		writeD(0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return _S__1F_MOVECHARPACKET;
	}
}