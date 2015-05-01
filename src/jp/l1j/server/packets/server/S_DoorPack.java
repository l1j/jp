/**
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
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1DoorInstance;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket, S_DoorPack

public class S_DoorPack extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_DoorPack.class.getName());
	private static final String S_DOOR_PACK = "[S] S_DoorPack";

	private static final int STATUS_POISON = 1;
	private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
	private static final int STATUS_FREEZE = 8;
	private static final int STATUS_BRAVE = 16;
	private static final int STATUS_ELFBRAVE = 32;
	private static final int STATUS_FASTMOVABLE = 64;
	private static final int STATUS_GHOST = 128;

	private byte[] _byte = null;

	public S_DoorPack(L1DoorInstance door) {
		buildPacket(door);
	}

	private void buildPacket(L1DoorInstance door) {
		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(door.getX());
		writeH(door.getY());
		writeD(door.getId());
		writeH(door.getGfxId());
		int doorStatus = door.getStatus();
		int openStatus = door.getOpenStatus();
		if (door.isDead()) {
			writeC(doorStatus);
		} else if (openStatus == ActionCodes.ACTION_Open) {
			writeC(openStatus);
		} else if (door.getMaxHp() > 1 && doorStatus != 0) {
			writeC(doorStatus);
		} else {
			writeC(openStatus);
		}
		writeC(0);
		writeC(0);
		writeC(0);
		writeD(1);
		writeH(0);
		writeS(null);
		writeS(null);
		int status = 0;
		if (door.getPoison() != null) { // 毒状態
			if (door.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}
		writeC(status);
		writeD(0);
		writeS(null);
		writeS(null);
		writeC(0);
		writeC(0xFF);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0xFF);
		writeC(0xFF);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}

		return _byte;
	}

	@Override
	public String getType() {
		return S_DOOR_PACK;
	}

}
