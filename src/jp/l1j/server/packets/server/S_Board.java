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

import java.util.List;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.templates.L1BoardPost;

public class S_Board extends ServerBasePacket {

	private static final String S_BOARD = "[S] S_Board";

	private static final int TOPIC_LIMIT = 8;

	private byte[] _byte = null;

	public S_Board(int boardObjId) {
		buildPacket(boardObjId, 0);
	}

	public S_Board(int boardObjId, int number) {
		buildPacket(boardObjId, number);
	}

	private void buildPacket(int boardObjId, int number) {
		List<L1BoardPost> topics = L1BoardPost.index(number, TOPIC_LIMIT);
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0); // DragonKeybbs = 1
		writeD(boardObjId);
		if (number == 0) {
			writeD(0x7FFFFFFF);
		} else {
			writeD(number);
		}
		writeC(topics.size());
		if (number == 0) {
			writeC(0);
			writeH(300);
		}
		for (L1BoardPost topic : topics) {
			writeD(topic.getId());
			writeS(topic.getName());
			writeS(topic.getDate());
			writeS(topic.getTitle());
		}
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
		return S_BOARD;
	}
}
