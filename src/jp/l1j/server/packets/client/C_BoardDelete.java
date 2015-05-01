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

package jp.l1j.server.packets.client;

import java.util.logging.Logger;
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.templates.L1BoardPost;

public class C_BoardDelete extends ClientBasePacket {
	private static final String C_BOARD_DELETE = "[C] C_BoardDelete";
	
	private static Logger _log = Logger.getLogger(C_BoardDelete.class.getName());

	public C_BoardDelete(byte decrypt[], ClientThread client) {
		super(decrypt);
		int objId = readD();
		int topicId = readD();
		L1Object obj = L1World.getInstance().findObject(objId);
		if (obj == null) {
			_log.warning("Invalid NPC ID: " + objId);
			return;
		}
		L1BoardPost topic = L1BoardPost.findById(topicId);
		if (topic == null) {
			logNotExist(topicId);
			return;
		}
		String name = client.getActiveChar().getName();
		if (!name.equals(topic.getName())) {
			logIllegalDeletion(topic, name);
			return;
		}
		topic.delete();
	}

	private void logNotExist(int topicId) {
		_log.warning(String.format("Illegal board deletion request: Topic id <%d> does not exist.", topicId));
	}

	private void logIllegalDeletion(L1BoardPost topic, String name) {
		_log.warning(String.format("Illegal board deletion request: Name <%s> expected but was <%s>.",
				topic.getName(), name));
	}

	@Override
	public String getType() {
		return C_BOARD_DELETE;
	}
}
