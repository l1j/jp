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

import java.io.FileNotFoundException;
import java.util.logging.Logger;
import jp.l1j.server.ClientThread;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;

public class C_NpcTalkAction extends ClientBasePacket {
	private static final String C_NPC_TALK_ACTION = "[C] C_NpcTalkAction";
	
	private static Logger _log = Logger.getLogger(C_NpcTalkAction.class.getName());

	public C_NpcTalkAction(byte decrypt[], ClientThread client)
			throws FileNotFoundException, Exception {
		super(decrypt);
		int objectId = readD();
		String action = readS();
		L1PcInstance activeChar = client.getActiveChar();
		L1Object obj = L1World.getInstance().findObject(objectId);
		if (obj == null) {
			_log.warning("object not found, oid " + objectId);
			return;
		}
		try {
			L1NpcInstance npc = (L1NpcInstance) obj;
			npc.onFinalAction(activeChar, action);
		} catch (ClassCastException e) {
		}
	}

	@Override
	public String getType() {
		return C_NPC_TALK_ACTION;
	}
}
