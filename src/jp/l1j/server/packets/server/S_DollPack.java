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
import jp.l1j.server.model.instance.L1DollInstance;
import jp.l1j.server.model.instance.L1PcInstance;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket , S_DollPack

public class S_DollPack extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_DollPack.class.getName());
	private static final String S_DOLLPACK = "[S] S_DollPack";
	private byte[] _byte = null;

	public S_DollPack(L1DollInstance pet, L1PcInstance player) {
		/*
		 * int addbyte = 0; int addbyte1 = 1; int addbyte2 = 13; int setting =
		 * 4;
		 */
		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(pet.getX());
		writeH(pet.getY());
		writeD(pet.getId());
		writeH(pet.getGfxId()); // SpriteID in List.spr
		writeC(pet.getStatus()); // Modes in List.spr
		writeC(pet.getHeading());
		writeC(0); // (Bright) - 0~15
		writeC(pet.getMoveSpeed()); // スピード - 0:normal,1:fast,2:slow
		writeD(0);
		writeH(0);
		writeS(pet.getNameId());
		writeS(pet.getTitle());
		writeC(0); // 獣獣艦砺 - 0:mob, item(atk pointer) , 1:poisoned() ,
		// 2:invisable() , 4:pc, 8:cursed() , 16:brave() ,
		// 32:??, 64:??(??) , 128:invisable but name
		writeD(0); // ??
		writeS(null); // ??
		writeS(pet.getMaster() != null ? pet.getMaster().getName() : "");
		writeC(0); // ??
		writeC(0xFF);
		writeC(0);
		writeC(pet.getLevel()); // PC = 0, Mon = Lv
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
		return S_DOLLPACK;
	}

}
