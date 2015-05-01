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
import jp.l1j.server.utils.FaceToFace;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_Trade extends ClientBasePacket {

	private static final String C_TRADE = "[C] C_Trade";
	private static Logger _log = Logger.getLogger(C_Trade.class.getName());

	public C_Trade(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);

		L1PcInstance player = clientthread.getActiveChar();
		if (player.isGhost()) {
			return;
		}
		L1PcInstance target = FaceToFace.faceToFace(player);
		if (target != null) {
			if (!target.isParalyzed()) {
				player.setTradeID(target.getId()); // 相手のオブジェクトIDを保存しておく
				target.setTradeID(player.getId());
				target.sendPackets(new S_MessageYN(252, player.getName())); // %0%sがあなたとアイテムの取引を望んでいます。取引しますか？（Y/N）
			}
		}
	}

	@Override
	public String getType() {
		return C_TRADE;
	}
}
