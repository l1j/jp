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
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_PetInventory;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_PetMenu extends ClientBasePacket {

	private static final String C_PET_MENU = "[C] C_PetMenu";
	private static Logger _log = Logger.getLogger(C_PetMenu.class.getName());

	public C_PetMenu(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);

		int petId = readD();

		L1NpcInstance pet = (L1NpcInstance) L1World.getInstance().findObject(petId);
		L1PcInstance pc = clientthread.getActiveChar();

		if (pet != null && pc != null) {
			pc.sendPackets(new S_PetInventory(pet));
		}
	}

	@Override
	public String getType() {
		return C_PET_MENU;
	}
}
