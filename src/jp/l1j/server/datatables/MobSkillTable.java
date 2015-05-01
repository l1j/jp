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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import jp.l1j.server.templates.L1MobSkill;
import jp.l1j.server.utils.L1QueryUtil;
import jp.l1j.server.utils.L1QueryUtil.EntityFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.collections.Lists;
import jp.l1j.server.utils.collections.Maps;

public class MobSkillTable {
	private static MobSkillTable _instance;

	private static Map<Integer, List<L1MobSkill>> _mobSkills = Maps.newHashMap();

	synchronized public static MobSkillTable getInstance() {
		if (_instance == null) {
			_instance = new MobSkillTable();
		}
		return _instance;
	}
	
	private MobSkillTable() {
		loadMobSkills(_mobSkills);
	}

	private static class NpcIdFactory implements EntityFactory<Integer> {
		@Override
		public Integer fromResultSet(ResultSet rs) throws SQLException {
			return rs.getInt("npc_id");
		}
	}

	private void loadMobSkills(Map<Integer, List<L1MobSkill>> mobSkills) {
		PerformanceTimer timer = new PerformanceTimer();
		List<Integer> npcIds = L1QueryUtil.selectAll(new NpcIdFactory(),
			"SELECT DISTINCT npc_id FROM mob_skills");
		for (int npcId : npcIds) {
			List<L1MobSkill> skills = L1QueryUtil.selectAll(new L1MobSkill.Factory(),
				"SELECT * FROM mob_skills where npc_id = ? order by act_no", npcId);
			mobSkills.put(npcId, skills);
		}
		System.out.println("loading mob skills...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	synchronized public void reload() {
		Map<Integer, List<L1MobSkill>> mobSkills = Maps.newHashMap();
		loadMobSkills(mobSkills);
		_mobSkills = mobSkills;
	}
	
	public List<L1MobSkill> get(int id) {
		List<L1MobSkill> result = _mobSkills.get(id);
		if (result == null) {
			result = Lists.newArrayList();
		}
		return result;
	}
}
