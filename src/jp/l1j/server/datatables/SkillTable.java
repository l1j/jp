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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.templates.L1Skill;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;
import jp.l1j.server.utils.collections.Lists;
import jp.l1j.server.utils.collections.Maps;

public class SkillTable {
	private static Logger _log = Logger.getLogger(SkillTable.class.getName());

	private static SkillTable _instance;

	private static Map<Integer, L1Skill> _skills = Maps.newHashMap();

	private static List<Integer> _buffSkillIds;

	public static SkillTable getInstance() {
		if (_instance == null) {
			_instance = new SkillTable();
		}
		return _instance;
	}

	private SkillTable() {
		load();
	}

	private void loadSkills(Map<Integer, L1Skill> skills) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM skills");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1Skill skill = L1Skill.fromResultSet(rs);
				skills.put(skill.getSkillId(), skill);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Unable to load SkillTable.", e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}
	
	private List<Integer> loadBuffSkillIds(Map<Integer, L1Skill> skills) {
		List<Integer> result = Lists.newArrayList();
		for (L1Skill skill : skills.values()) {
			if (skill.isBuff()) {
				result.add(skill.getSkillId());
			}
		}
		return result;
	}

	private void load() {
		PerformanceTimer timer = new PerformanceTimer();
		loadSkills(_skills);
		_buffSkillIds = loadBuffSkillIds(_skills);
		System.out.println("loading skills...OK! " + timer.elapsedTimeMillis() + "ms");
	}
	
	public void reload() {
		PerformanceTimer timer = new PerformanceTimer();
		Map<Integer, L1Skill> skills = Maps.newHashMap();
		List<Integer> buffSkillIds;
		loadSkills(skills);
		buffSkillIds = loadBuffSkillIds(skills);
		_skills = skills;
		_buffSkillIds = buffSkillIds;
		System.out.println("loading skills...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public void spellMastery(int playerobjid, int skillid, String skillname, int active, int time) {
		if (spellCheck(playerobjid, skillid)) {
			return;
		}
		L1PcInstance pc = (L1PcInstance) L1World.getInstance().findObject(playerobjid);
		if (pc != null) {
			pc.setSkillMastery(skillid);
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO character_skills SET char_id=?, skill_id=?, skill_name=?, is_active=?, active_time_left=?");
			pstm.setInt(1, playerobjid);
			pstm.setInt(2, skillid);
			pstm.setString(3, skillname);
			pstm.setInt(4, active);
			pstm.setInt(5, time);
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void spellLost(int playerobjid, int skillid) {
		L1PcInstance pc = (L1PcInstance) L1World.getInstance().findObject(playerobjid);
		if (pc != null) {
			pc.removeSkillMastery(skillid);
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_skills WHERE char_id=? AND skill_id=?");
			pstm.setInt(1, playerobjid);
			pstm.setInt(2, skillid);
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public boolean spellCheck(int playerobjid, int skillid) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_skills WHERE char_id=? AND skill_id=?");
			pstm.setInt(1, playerobjid);
			pstm.setInt(2, skillid);
			rs = pstm.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return false;
	}

	public L1Skill findBySkillId(int skillId) {
		return _skills.get(skillId);
	}

	public L1Skill findByItemName(String itemName) {
		String skillName = itemName.replaceFirst("^.*\\((.+)\\).*$", "$1");
		for (L1Skill skill : _skills.values()) {
			if (skill.getName().equalsIgnoreCase(skillName)) {
				return skill;
			}
		}
		return null;
	}

	public List<Integer> findBuffSkillIds() {
		return Lists.newArrayList(_buffSkillIds);
	}

	public L1Skill getTemplate(int i) {
		return _skills.get(new Integer(i));
	}
}
