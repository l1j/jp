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

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_War extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_War.class.getName());
	private static final String S_WAR = "[S] S_War";
	private byte[] _byte = null;

	public S_War(final int type, final String clan_name1, final String clan_name2) {
		buildPacket(type, clan_name1, clan_name2);
	}

	private void buildPacket(final int type, final String clan_name1, final String clan_name2) {
		// 1 : _血盟が_血盟に宣戦布告しました。
		// 2 : _血盟が_血盟に降伏しました。
		// 3 : _血盟と_血盟との戦争が終結しました。
		// 4 : _血盟が_血盟との戦争で勝利しました。
		// 6 : _血盟と_血盟が同盟を結びました。
		// 7 : _血盟と_血盟との同盟関係が解除されました。
		// 8 : あなたの血盟が現在_血盟と交戦中です。

		writeC(Opcodes.S_OPCODE_WAR);
		writeC(type);
		writeS(clan_name1);
		writeS(clan_name2);
	}

	@Override
	public final byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public final String getType() {
		return S_WAR;
	}
}
