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

package jp.l1j.server.templates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;
import jp.l1j.server.utils.collections.Lists;

public class L1CharacterSkill {
	private static Logger _log = Logger.getLogger(L1CharacterSkill.class.getName());

	private int _id;
	private int _charcterId;
	private int _skillId;
	private String _skillName;
	private boolean _isActive;
	private int _activeTimeLeft;

	public L1CharacterSkill(int id, int charcterId, int skillId,
			String skillName, boolean isActive, int activeTimeLeft) {
		_id = id;
		_charcterId = charcterId;
		_skillId = skillId;
		_skillName = skillName;
		_isActive = isActive;
		_activeTimeLeft = activeTimeLeft;
	}

	private static L1CharacterSkill fromResultSet(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int charcterId = rs.getInt("char_id");
		int skillId = rs.getInt("skill_id");
		String skillName = rs.getString("skill_name");
		boolean isActive = rs.getBoolean("is_active");
		int activeTimeLeft = rs.getInt("active_time_left");
		return new L1CharacterSkill(id, charcterId, skillId, skillName,
				isActive, activeTimeLeft);
	}

	public static List<L1CharacterSkill> findByCharcterId(int id) {
		List<L1CharacterSkill> result = Lists.newArrayList();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_skills WHERE char_id = ?");
			pstm.setInt(1, id);
			rs = pstm.executeQuery();
			while (rs.next()) {
				result.add(fromResultSet(rs));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return result;
	}

	public static Logger getLog() {
		return _log;
	}

	public int getId() {
		return _id;
	}

	public int getCharcterId() {
		return _charcterId;
	}

	public int getSkillId() {
		return _skillId;
	}

	public String getSkillName() {
		return _skillName;
	}

	public boolean isActive() {
		return _isActive;
	}

	public int getActiveTimeLeft() {
		return _activeTimeLeft;
	}
}
