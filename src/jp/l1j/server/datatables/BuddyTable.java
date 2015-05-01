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

package jp.l1j.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.L1Buddy;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class BuddyTable {
	private static Logger _log = Logger.getLogger(BuddyTable.class.getName());

	private static BuddyTable _instance;

	private final Map<Integer, L1Buddy> _buddys = new HashMap<Integer, L1Buddy>();

	public static BuddyTable getInstance() {
		if (_instance == null) {
			_instance = new BuddyTable();
		}
		return _instance;
	}

	private BuddyTable() {

		Connection con = null;
		PreparedStatement charIdPS = null;
		ResultSet charIdRS = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			charIdPS = con.prepareStatement("SELECT distinct(char_id) as char_id FROM character_buddys");
			charIdRS = charIdPS.executeQuery();
			while (charIdRS.next()) {
				PreparedStatement buddysPS = null;
				ResultSet buddysRS = null;
				try {
					buddysPS = con.prepareStatement("SELECT buddy_id FROM character_buddys WHERE char_id = ?");
					int charId = charIdRS.getInt("char_id");
					buddysPS.setInt(1, charId);
					L1Buddy buddy = new L1Buddy(charId);
					buddysRS = buddysPS.executeQuery();
					while (buddysRS.next()) {
						int id = buddysRS.getInt("buddy_id");
						String name = CharacterTable.getInstance().getCharName(id);
						buddy.add(id, name);
					}
					_buddys.put(buddy.getCharId(), buddy);
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				} finally {
					SqlUtil.close(buddysRS);
					SqlUtil.close(buddysPS);
				}
			}
			_log.fine("loaded buddy: " + _buddys.size() + " records");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(charIdRS);
			SqlUtil.close(charIdPS);
			SqlUtil.close(con);
		}
	}

	public L1Buddy getBuddyTable(int charId) {
		L1Buddy buddy = _buddys.get(charId);
		if (buddy == null) {
			buddy = new L1Buddy(charId);
			_buddys.put(charId, buddy);
		}
		return buddy;
	}

	public void addBuddy(int charId, int buddyId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO character_buddys SET char_id=?, buddy_id=?");
			pstm.setInt(1, charId);
			pstm.setInt(2, buddyId);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void removeBuddy(int charId, String buddyName) {
		Connection con = null;
		PreparedStatement pstm = null;
		L1Buddy buddy = getBuddyTable(charId);
		if (!buddy.containsName(buddyName)) {
			return;
		}
		try {
			L1PcInstance pc = (L1PcInstance) L1World.getInstance().getPlayer(buddyName);
			int buddyId = pc != null ? pc.getId() : null;
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_buddys WHERE char_id=? AND buddy_id=?");
			pstm.setInt(1, charId);
			pstm.setInt(2, buddyId);
			pstm.execute();
			buddy.remove(buddyName);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
}
