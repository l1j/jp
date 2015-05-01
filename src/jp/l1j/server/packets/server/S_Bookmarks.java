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

public class S_Bookmarks extends ServerBasePacket {
	private static final String _S__1F_S_Bookmarks = "[S] S_Bookmarks";

	private static Logger _log = Logger.getLogger(S_Bookmarks.class.getName());

	private byte[] _byte = null;

	public S_Bookmarks(String name, int map, int id, int x, int y) {
		buildPacket(name, map, id, x, y);
	}

	private void buildPacket(String name, int map, int id, int x, int y) {
		writeC(Opcodes.S_OPCODE_BOOKMARKS);
		writeS(name);
		writeH(map);
		writeD(id);
		writeH(x);
		writeH(y);
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
		return _S__1F_S_Bookmarks;
	}
}