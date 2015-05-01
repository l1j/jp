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
import jp.l1j.server.packets.server.S_MessageYN;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.utils.FaceToFace;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_Fight extends ClientBasePacket {

	private static final String C_FIGHT = "[C] C_Fight";
	private static Logger _log = Logger.getLogger(C_Fight.class.getName());

	public C_Fight(byte abyte0[], ClientThread client)
			throws Exception {
		super(abyte0);

		L1PcInstance pc = client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
		L1PcInstance target = FaceToFace.faceToFace(pc);
		if (target != null) {
			if (!target.isParalyzed()) {
				if (pc.getFightId() != 0) {
					pc.sendPackets(new S_ServerMessage(633)); // \f1あなたはすでにほかの人と決闘中です。
					return;
				} else if (target.getFightId() != 0) {
					target.sendPackets(new S_ServerMessage(634)); // \f1すでにほかの人と決闘中です。
					return;
				}
				pc.setFightId(target.getId());
				target.setFightId(pc.getId());
				target.sendPackets(new S_MessageYN(630, pc.getName())); // %0%sがあなたと決闘を望んでいます。応じますか？（Y/N）
			}
		}
	}

	@Override
	public String getType() {
		return C_FIGHT;
	}

}
