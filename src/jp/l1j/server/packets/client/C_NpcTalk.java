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
import jp.l1j.server.datatables.NpcActionTable;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.npc.L1NpcHtml;
import jp.l1j.server.model.npc.action.L1NpcAction;
import jp.l1j.server.packets.server.S_NpcTalkReturn;

public class C_NpcTalk extends ClientBasePacket {
	private static final String C_NPC_TALK = "[C] C_NpcTalk";
	
	private static Logger _log = Logger.getLogger(C_NpcTalk.class.getName());

	public C_NpcTalk(byte abyte0[], ClientThread client) throws Exception {
		super(abyte0);
		int objid = readD();
		L1Object obj = L1World.getInstance().findObject(objid);
		L1PcInstance pc = client.getActiveChar();
		if (obj != null && pc != null) {
			L1NpcAction action = NpcActionTable.getInstance().get(pc, obj);
			if (action != null) {
				L1NpcHtml html = action.execute("", pc, obj, new byte[0]);
				if (html != null) {
					pc.sendPackets(new S_NpcTalkReturn(obj.getId(), html));
				}
				return;
			}
			obj.onTalkAction(pc);
		} else {
			_log.severe("Does not exist objid=" + objid);
		}
	}

	@Override
	public String getType() {
		return C_NPC_TALK;
	}
}
