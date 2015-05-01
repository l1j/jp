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

import jp.l1j.configure.Config;
import jp.l1j.server.ClientThread;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.templates.L1Account;

public class S_CharAmount extends ServerBasePacket {

	private byte[] _byte = null;

	public S_CharAmount(int value, ClientThread client) {
		buildPacket(value, client);
	}

	private void buildPacket(int value, ClientThread client) {
		L1Account account = L1Account.findByName(client.getAccountName());
		int characterSlot = account.getCharacterSlot();
		int maxAmount = Config.DEFAULT_CHARACTER_SLOT + characterSlot;

		writeC(Opcodes.S_OPCODE_CHARAMOUNT);
		writeC(value);
// writeD(0x00000000);
// writeD(0x0000);
		writeC(maxAmount); // max amount
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}
}
