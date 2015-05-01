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

package jp.l1j.server.packets.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class S_CharacterConfig extends ServerBasePacket {
	private static Logger _log = Logger.getLogger(S_CharacterConfig.class.getName());
	
	private static final String S_CHARACTER_CONFIG = "[S] S_CharacterConfig";
	
	private byte[] _byte = null;

	public S_CharacterConfig(int objectId) {
		buildPacket(objectId);
	}

	private void buildPacket(int objectId) {
		int length = 0;
		byte data[] = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_configs WHERE char_id=?");
			pstm.setInt(1, objectId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				length = rs.getInt("length");
				data = rs.getBytes("data");
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		if (length != 0) {
			writeC(Opcodes.S_OPCODE_SKILLICONGFX);
			writeC(41);
			writeD(length);
			writeBytes(data);
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
		return S_CHARACTER_CONFIG;
	}
}
