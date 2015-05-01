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
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_MessageYN;
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_CreateParty extends ClientBasePacket {

	private static final String C_CREATE_PARTY = "[C] C_CreateParty";
	private static Logger _log = Logger.getLogger(C_CreateParty.class
			.getName());

	public C_CreateParty(byte decrypt[], ClientThread client) throws Exception {
		super(decrypt);

		L1PcInstance pc = client.getActiveChar();

		int type = readC();
		if (type == 0 || type == 1) { // パーティー(パーティー自動分配ON/OFFで異なる)
			int targetId = readD();
			L1Object temp = L1World.getInstance().findObject(targetId);
			if (temp instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) temp;
				if (pc.getId() == targetPc.getId()) {
					return;
				}
				if (targetPc.isInParty()) {
					// すでに他のパーティーに所属しているため招待できません
					pc.sendPackets(new S_ServerMessage(415));
					return;
				}

				if (pc.isInParty()) {
					if (pc.getParty().isLeader(pc)) {
						targetPc.setPartyType(type);
						targetPc.setPartyID(pc.getId());
						switch(type) {
						case 0:
							// \f2%0\f>%sから \fUパーティー\f> に招待されました。応じますか？（Y/N）
							targetPc.sendPackets(new S_MessageYN(953, pc.getName()));
							break;
						case 1:
							// \f2%0\f>%sから \fU自動分配のパーティー\f> に招待されました。 応じますか？(Y/N)
							targetPc.sendPackets(new S_MessageYN(954, pc.getName()));
							break;
						}		
					} else {
						// パーティーのリーダーのみが招待できます。
						pc.sendPackets(new S_ServerMessage(416));
					}
				} else {
					pc.setPartyType(type);
					targetPc.setPartyID(pc.getId());
					// \f2%0\f>%sから \fUパーティー\f> に招待されました。応じますか？（Y/N）
					switch (type) {
					case 0:
						targetPc.sendPackets(new S_MessageYN(953, pc.getName()));
						break;
					case 1:
						targetPc.sendPackets(new S_MessageYN(954, pc.getName()));
						break;
					}
				}
			}
		} else if (type == 2) { // チャットパーティー
			String name = readS();
			L1PcInstance targetPc = L1World.getInstance().getPlayer(name);
			if (targetPc == null) {
				// %0という名前の人はいません。
				pc.sendPackets(new S_ServerMessage(109));
				return;
			}
			if (pc.getId() == targetPc.getId()) {
				return;
			}
			if (targetPc.isInChatParty()) {
				// すでに他のパーティーに所属しているため招待できません
				pc.sendPackets(new S_ServerMessage(415));
				return;
			}

			if (pc.isInChatParty()) {
				if (pc.getChatParty().isLeader(pc)) {
					targetPc.setPartyID(pc.getId());
					// \f2%0\f>%sから\fUチャットパーティー\f>に招待されました。応じますか？（Y/N）
					targetPc.sendPackets(new S_MessageYN(951, pc.getName()));
				} else {
					// パーティーのリーダーのみが招待できます。
					pc.sendPackets(new S_ServerMessage(416));
				}
			} else {
				targetPc.setPartyID(pc.getId());
				// \f2%0\f>%sから\fUチャットパーティー\f>に招待されました。応じますか？（Y/N）
				targetPc.sendPackets(new S_MessageYN(951, pc.getName()));
			}
		}
		// パーティーリーダー委任
		else if (type == 3) {
			// パーティーリーダーではないため、権限を行使できません。
			if ((pc.getParty() == null) || !pc.getParty().isLeader(pc)) {
				pc.sendPackets(new S_ServerMessage(1697));
				return;
			}

			//
			int targetId = readD();

			//
			L1Object obj = L1World.getInstance().findObject(targetId);

			//
			if ((obj == null) || (pc.getId() == obj.getId())
					|| !(obj instanceof L1PcInstance)) {
				return;
			}

			//
			L1PcInstance targetPc = (L1PcInstance) obj;

			// 現在、パーティーに所属しているメンバーではありません。
			if (!targetPc.isInParty()) {
				pc.sendPackets(new S_ServerMessage(1696));
				return;
			}

			// 新しいリーダーを指定
			pc.getParty().passLeader(targetPc);
		}

	}

	@Override
	public String getType() {
		return C_CREATE_PARTY;
	}
}