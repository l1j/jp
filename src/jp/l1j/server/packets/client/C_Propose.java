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

public class C_Propose extends ClientBasePacket {

	private static final String C_PROPOSE = "[C] C_Propose";
	private static Logger _log = Logger.getLogger(C_Propose.class.getName());

	public C_Propose(byte abyte0[], ClientThread clientthread) {
		super(abyte0);
		int c = readC();

		L1PcInstance pc = clientthread.getActiveChar();
		if (c == 0) { // /propose（/プロポーズ）
			if (pc.isGhost()) {
				return;
			}
			L1PcInstance target = FaceToFace.faceToFace(pc);
			if (target != null) {
				if (pc.getPartnerId() != 0) {
					pc.sendPackets(new S_ServerMessage(657)); // \f1あなたはすでに結婚しています。
					return;
				}
				if (target.getPartnerId() != 0) {
					pc.sendPackets(new S_ServerMessage(658)); // \f1その相手はすでに結婚しています。
					return;
				}
				if (pc.getSex() == target.getSex()) {
					pc.sendPackets(new S_ServerMessage(661)); // \f1結婚相手は異性でなければなりません。
					return;
				}
				if (pc.getX() >= 33974 && pc.getX() <= 33976
						&& pc.getY() >= 33362 && pc.getY() <= 33365
						&& pc.getMapId() == 4 && target.getX() >= 33974
						&& target.getX() <= 33976 && target.getY() >= 33362
						&& target.getY() <= 33365 && target.getMapId() == 4) {
					target.setTempID(pc.getId()); // 相手のオブジェクトIDを保存しておく
					target.sendPackets(new S_MessageYN(654, pc.getName())); // %0%sあなたと結婚したがっています。%0と結婚しますか？（Y/N）
				}
			}
		} else if (c == 1) { // /divorce（/離婚）
			if (pc.getPartnerId() == 0) {
				pc.sendPackets(new S_ServerMessage(662)); // \f1あなたは結婚していません。
				return;
			}
			pc.sendPackets(new S_MessageYN(653, "")); // 離婚をするとリングは消えてしまいます。離婚を望みますか？（Y/N）
		}
	}

	@Override
	public String getType() {
		return C_PROPOSE;
	}
}
