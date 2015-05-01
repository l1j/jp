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
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.item.L1ItemId;
import jp.l1j.server.templates.L1BoardPost;

public class C_BoardWrite extends ClientBasePacket {
	private static final String C_BOARD_WRITE = "[C] C_BoardWrite";
	
	private static Logger _log = Logger.getLogger(C_BoardWrite.class.getName());

	public C_BoardWrite(byte decrypt[], ClientThread client) {
		super(decrypt);
		int id = readD();
		String title = readS();
		String content = readS();
		L1Object tg = L1World.getInstance().findObject(id);
		if (tg == null) {
			_log.warning("Invalid NPC ID: " + id);
			return;
		}
		L1PcInstance pc = client.getActiveChar();
		L1BoardPost.create(pc.getName(), title, content);
		pc.getInventory().consumeItem(L1ItemId.ADENA, 300);
	}

	@Override
	public String getType() {
		return C_BOARD_WRITE;
	}
}
