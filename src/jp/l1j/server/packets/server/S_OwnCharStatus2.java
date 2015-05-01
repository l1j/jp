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
import jp.l1j.server.model.instance.L1PcInstance;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket, S_SendInvOnLogin

public class S_OwnCharStatus2 extends ServerBasePacket {

	public S_OwnCharStatus2(L1PcInstance l1pcinstance) {
		if (l1pcinstance == null) {
			return;
		}

		cha = l1pcinstance;

		writeC(Opcodes.S_OPCODE_OWNCHARSTATUS2);
		writeC(cha.getStr());
		writeC(cha.getInt());
		writeC(cha.getWis());
		writeC(cha.getDex());
		writeC(cha.getCon());
		writeC(cha.getCha());
		writeC(cha.getInventory().getWeight240());
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return "[C] S_OwnCharStatus2";
	}

	private static final String _S__4F_S_OwnChraStatus2 = "[C] S_OwnCharStatus2";
	private static Logger _log = Logger.getLogger(S_OwnCharStatus2.class
			.getName());
	private L1PcInstance cha = null;
}
