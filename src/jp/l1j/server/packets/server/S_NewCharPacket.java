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

import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1PcInstance;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_NewCharPacket extends ServerBasePacket {
	private static final String _S__25_NEWCHARPACK = "[S] New Char Packet";
	private byte[] _byte = null;

	public S_NewCharPacket(L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_NEWCHARPACK);
		writeS(pc.getName());
		writeS("");
		writeC(pc.getType());
		writeC(pc.getSex());
		writeH(pc.getLawful());
		writeH(pc.getMaxHp());
		writeH(pc.getMaxMp());
		writeC(pc.getAc());
		writeC(pc.getLevel());
		writeC(pc.getStr());
		writeC(pc.getDex());
		writeC(pc.getCon());
		writeC(pc.getWis());
		writeC(pc.getCha());
		writeC(pc.getInt());

		// is Administrator
		// 0 = false
		// 1 = true , can't attack
		// > 1 true , can't attack
		// can use Public GameMaster Command
		// if (pc.isGm()) {
		// writeC(1);
		// } else {
		writeC(0);
		// }
		
		writeD(pc.getSimpleBirthday());
		
		// 3.53c start
		int code = pc.getLevel() ^ pc.getStr() ^ pc.getDex() ^ pc.getCon() ^ pc.getWis() ^ pc.getCha() ^ pc.getInt();
		writeC(code & 0xFF);
		// 3.53c end
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
		return _S__25_NEWCHARPACK;
	}

}
