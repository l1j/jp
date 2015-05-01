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

public class S_SellHouse extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_SellHouse.class.
			getName());
	private static final String S_SELLHOUSE = "[S] S_SellHouse";
	private byte[] _byte = null;

	public S_SellHouse(int objectId, String houseNumber) {
		buildPacket(objectId, houseNumber);
	}

	private void buildPacket(int objectId, String houseNumber) {
		writeC(Opcodes.S_OPCODE_INPUTAMOUNT);
		writeD(objectId);
		writeD(0); // ?
		writeD(100000); // スピンコントロールの初期価格
		writeD(100000); // 価格の下限
		writeD(2000000000); // 価格の上限
		writeH(0); // ?
		writeS("agsell");
		writeS("agsell " + houseNumber);
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
		return S_SELLHOUSE;
	}
}
