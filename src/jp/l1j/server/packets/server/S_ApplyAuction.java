/**
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

package jp.l1j.server.packets.server;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_ApplyAuction extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_ApplyAuction.class.
			getName());
	private static final String S_APPLYAUCTION = "[S] S_ApplyAuction";
	private byte[] _byte = null;

	public S_ApplyAuction(int objectId, String houseNumber) {
		buildPacket(objectId, houseNumber);
	}

	private void buildPacket(int objectId, String houseNumber) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM auction_houses WHERE house_id=?");
			int number = Integer.valueOf(houseNumber);
			pstm.setInt(1, number);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int nowPrice = rs.getInt("price");
				int bidderId = rs.getInt("bidder_id");
				writeC(Opcodes.S_OPCODE_INPUTAMOUNT);
				writeD(objectId);
				writeD(0); // ?
				if (bidderId == 0) { // 入札者なし
					writeD(nowPrice); // スピンコントロールの初期価格
					writeD(nowPrice); // 価格の下限
				} else { // 入札者あり
					writeD(nowPrice + 1); // スピンコントロールの初期価格
					writeD(nowPrice + 1); // 価格の下限
				}
				writeD(2000000000); // 価格の上限
				writeH(0); // ?
				writeS("agapply");
				writeS("agapply " + houseNumber);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_APPLYAUCTION;
	}
}
