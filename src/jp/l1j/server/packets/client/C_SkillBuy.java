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
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SkillBuy;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_SkillBuy extends ClientBasePacket {

	private static final String C_SKILL_BUY = "[C] C_SkillBuy";
	private static Logger _log = Logger.getLogger(C_SkillBuy.class.getName());

	public C_SkillBuy(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);

		int i = readD();

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
		pc.sendPackets(new S_SkillBuy(i, pc));
		/*
		 * int type = player.getType(); int lvl = player.getLevel();
		 * 
		 * switch(type) { case 0: // 君主 if(lvl >= 10) { player.sendPackets(new
		 * S_SkillBuy(i, player)); } break;
		 * 
		 * case 1: // ナイト if(lvl >= 50) { player.sendPackets(new S_SkillBuy(i,
		 * player)); } break;
		 * 
		 * case 2: // エルフ if(lvl >= 8) { player.sendPackets(new S_SkillBuy(i,
		 * player)); } break;
		 * 
		 * case 3: // WIZ if(lvl >= 4) { player.sendPackets(new S_SkillBuy(i,
		 * player)); } break;
		 * 
		 * case 4: //DE if(lvl >= 12) { player.sendPackets(new S_SkillBuy(i,
		 * player)); } break;
		 * 
		 * default: break; }
		 */
	}

	@Override
	public String getType() {
		return C_SKILL_BUY;
	}

}
