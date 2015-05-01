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
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1FollowerInstance;
import jp.l1j.server.model.instance.L1PcInstance;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket, S_NpcPack

public class S_FollowerPack extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_FollowerPack.class
			.getName());
	private static final String S_FOLLOWER_PACK = "[S] S_FollowerPack";

	private static final int STATUS_POISON = 1;
	private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
	private static final int STATUS_FREEZE = 8;
	private static final int STATUS_BRAVE = 16;
	private static final int STATUS_ELFBRAVE = 32;
	private static final int STATUS_FASTMOVABLE = 64;
	private static final int STATUS_GHOST = 128;

	private byte[] _byte = null;

	public S_FollowerPack(L1FollowerInstance follower, L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(follower.getX());
		writeH(follower.getY());
		writeD(follower.getId());
		writeH(follower.getGfxId());
		writeC(follower.getStatus());
		writeC(follower.getHeading());
		writeC(follower.getLightSize());
		writeC(follower.getMoveSpeed());
		writeD(0);
		writeH(0);
		writeS(follower.getNameId());
		writeS(follower.getTitle());
		int status = 0;
		if (follower.getPoison() != null) { // 毒状態
			if (follower.getPoison().getEffectId() == 1) {
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
		writeC(follower.getLevel());
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
		return S_FOLLOWER_PACK;
	}

}
