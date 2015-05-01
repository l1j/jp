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
import jp.l1j.server.model.instance.L1PcInstance;

public class S_Exp extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_Exp.class.getName());

	private static final String S_EXP = "[S] S_Exp";

	/**
	 * レベルと経験値データを送る。
	 * @param pc - PC
	 */
	public S_Exp(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_EXP);
		writeC(pc.getLevel());
		writeD(pc.getExp());

//		writeC(Opcodes.S_OPCODE_EXP);
//		writeC(0x39);// level
//		writeD(_objid);// ??
//		writeC(0x0A);// ??
//		writeH(getexp);// min exp
//		writeH(getexpreward);// max exp
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_EXP;
	}
}
