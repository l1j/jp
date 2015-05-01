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

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_CharPacks extends ServerBasePacket {
	private static final String S_CHAR_PACKS = "[S] S_CharPacks";

	private static Logger _log = Logger.getLogger(S_CharPacks.class.getName());

	public S_CharPacks(String name, String clanName, int type, int sex,
			int lawful, int hp, int mp, int ac, int lv, int str, int dex,
			int con, int wis, int cha, int intel, int accessLevel, int birthday) {
		writeC(Opcodes.S_OPCODE_CHARLIST);
		writeS(name);
		writeS(clanName);
		writeC(type);
		writeC(sex);
		writeH(lawful);
		writeH(hp);
		writeH(mp);
		writeC(ac);
		writeC(lv);
		writeC(str);
		writeC(dex);
		writeC(con);
		writeC(wis);
		writeC(cha);
		writeC(intel);

		// is Administrator
		// 0 = false
		// 1 = true , can't attack
		// > 1 true , can't attack
		// can use Public GameMaster Command
		// if (accessLevel == 200) {
		// writeC(1);
		// } else {
		writeC(0);
		// }
		
		writeD(birthday);
		
		// 3.53c start
		int code = lv ^ str ^ dex ^ con ^ wis ^ cha ^ intel;
		writeC(code & 0xFF);
		// 3.53c end
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_CHAR_PACKS;
	}
}
