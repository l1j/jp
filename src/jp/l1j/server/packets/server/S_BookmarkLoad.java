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
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.templates.L1BookMark;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class S_BookmarkLoad extends ServerBasePacket {
	private static Logger _log = Logger.getLogger(S_AuctionBoardRead.class.getName());
	
	private static final String S_BookMarkLoad = "[S] S_BookmarkLoad";
	
	private byte[] _byte = null;

	public S_BookmarkLoad(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_bookmarks WHERE char_id=? ORDER BY name ASC");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();
			rs.last();
			int count = rs.getRow();
			rs.beforeFirst();
			writeC(Opcodes.S_OPCODE_CHARRESET);
			writeC(42); // type
			writeC(105);
			writeC(0x00); 
			writeC(0x02);
			int b = 0;
			for (int i=0; i<104; i++) {
				if (i < count) {
					writeC(b++);
				} else {
					writeC(0xff);
				}
			}
			writeC(100);
			writeC(0);
			writeC(count);
			writeC(0);
			L1BookMark bookmark = null;
			while (rs.next()) {
				bookmark = new L1BookMark();
				bookmark.setId(rs.getInt("id"));
				bookmark.setCharId(rs.getInt("char_id"));
				bookmark.setName(rs.getString("name"));
				bookmark.setLocX(rs.getInt("loc_x"));
				bookmark.setLocY(rs.getInt("loc_y"));
				bookmark.setMapId(rs.getShort("map_id"));
				//bookmark.setRandomX(rs.getShort("random_x"));
				//bookmark.setRandomY(rs.getShort("random_y"));
				writeH(bookmark.getLocX());
				writeH(bookmark.getLocY());
				writeS(bookmark.getName());
				writeH(bookmark.getMapId());  // mapid
				writeD(bookmark.getId()); // objid
				pc.addBookMark(bookmark);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_BookMarkLoad;
	}
}
