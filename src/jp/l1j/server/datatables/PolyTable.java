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
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.L1PolyMorph;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class PolyTable {
	private static Logger _log = Logger.getLogger(PolyTable.class.getName());

	private static PolyTable _instance;

	private static HashMap<String, L1PolyMorph> _polymorphs = new HashMap<String, L1PolyMorph>();
	
	private static HashMap<Integer, L1PolyMorph> _polyIds = new HashMap<Integer, L1PolyMorph>();

	public static PolyTable getInstance() {
		if (_instance == null) {
			_instance = new PolyTable();
		}
		return _instance;
	}

	private PolyTable() {
		loadPolymorphs(_polymorphs, _polyIds);
	}

	private void loadPolymorphs(HashMap<String, L1PolyMorph> polymorphs,
			HashMap<Integer, L1PolyMorph> polyIds) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM polymorphs");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int gfxId = rs.getInt("gfx_id");
				int minLevel = rs.getInt("min_level");
				int weaponEquipFlg = rs.getInt("weapon_equip");
				int armorEquipFlg = rs.getInt("armor_equip");
				boolean canUseSkill = rs.getBoolean("can_use_skill");
				int causeFlg = rs.getInt("cause");
				L1PolyMorph poly = new L1PolyMorph(id, name, gfxId, minLevel,
						weaponEquipFlg, armorEquipFlg, canUseSkill, causeFlg);
				polymorphs.put(name, poly);
				polyIds.put(gfxId, poly);
			}
			_log.fine("loaded poly: " + polymorphs.size() + " records");
			System.out.println("loading polymorphs...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "error while creating polymorph table", e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	public void reload() {
		HashMap<String, L1PolyMorph> polymorphs = new HashMap<String, L1PolyMorph>();
		HashMap<Integer, L1PolyMorph> polyIds = new HashMap<Integer, L1PolyMorph>();
		loadPolymorphs(polymorphs, polyIds);
		_polymorphs = polymorphs;
		_polyIds = polyIds;
	}
	
	public L1PolyMorph getTemplate(String name) {
		return _polymorphs.get(name);
	}

	public L1PolyMorph getTemplate(int polyId) {
		return _polyIds.get(polyId);
	}
}
