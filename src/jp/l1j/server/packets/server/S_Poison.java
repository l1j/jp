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

import static jp.l1j.locale.I18N.*;
import jp.l1j.server.codes.Opcodes;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_Poison extends ServerBasePacket {

	/**
	 * キャラクターの外見を毒状態へ変更する際に送信するパケットを構築する
	 * 
	 * @param objId
	 *            外見を変えるキャラクターのID
	 * @param type
	 *            外見のタイプ 0 = 通常色, 1 = 緑色, 2 = 灰色
	 */
	public S_Poison(int objId, int type) {
		writeC(Opcodes.S_OPCODE_POISON);
		writeD(objId);

		if (type == 0) { // 通常
			writeC(0);
			writeC(0);
		} else if (type == 1) { // 緑色
			writeC(1);
			writeC(0);
		} else if (type == 2) { // 灰色
			writeC(0);
			writeC(1);
		} else {
			throw new IllegalArgumentException(String.format(I18N_IS_UNKNOWN_PARAM, type));
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_POISON;
	}

	private static final String S_POISON = "[S] S_Poison";
}
