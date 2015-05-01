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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_MAP_LIST;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_NPC_LIST;
import jp.l1j.server.model.L1UltimateBattle;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class UbTable {
	private static Logger _log = Logger.getLogger(UbTable.class.getName());

	private static UbTable _instance;

	private HashMap<Integer, L1UltimateBattle> _ubs = new HashMap<Integer, L1UltimateBattle>();

	public static UbTable getInstance() {
		if (_instance == null) {
			_instance = new UbTable();
		}
		return _instance;
	}

	private UbTable() {
		loadUbs(_ubs);
	}

	private void loadUbs(HashMap<Integer, L1UltimateBattle> ubs) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM ubs");
			rs = pstm.executeQuery();
			while (rs.next()) {
				short mapId = rs.getShort("map_id");
				boolean isErr = false;
				if (MapTable.getInstance().locationname(mapId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, mapId));
					// %s はマップリストに存在しません。
					isErr = true;
				}
				if (isErr) {
					continue;
				}
				L1UltimateBattle ub = new L1UltimateBattle();
				ub.setUbId(rs.getInt("id"));
				ub.setMapId(mapId);
				ub.setLocX1(rs.getInt("area_x1"));
				ub.setLocY1(rs.getInt("area_y1"));
				ub.setLocX2(rs.getInt("area_x2"));
				ub.setLocY2(rs.getInt("area_y2"));
				ub.setMinLevel(rs.getInt("min_level"));
				ub.setMaxLevel(rs.getInt("max_level"));
				ub.setMaxPlayer(rs.getInt("max_player"));
				ub.setEnterRoyal(rs.getBoolean("enter_royal"));
				ub.setEnterKnight(rs.getBoolean("enter_knight"));
				ub.setEnterMage(rs.getBoolean("enter_wizard"));
				ub.setEnterElf(rs.getBoolean("enter_elf"));
				ub.setEnterDarkelf(rs.getBoolean("enter_darkelf"));
				ub.setEnterDragonKnight(rs.getBoolean("enter_dragonknight"));
				ub.setEnterIllusionist(rs.getBoolean("enter_illusionist"));
				ub.setEnterMale(rs.getBoolean("enter_male"));
				ub.setEnterFemale(rs.getBoolean("enter_female"));
				ub.setUsePot(rs.getBoolean("use_pot"));
				ub.setHpr(rs.getInt("hpr_bonus"));
				ub.setMpr(rs.getInt("mpr_bonus"));
				ub.resetLoc();
				ubs.put(ub.getUbId(), ub);
			}
			System.out.println("loading ubs...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.warning("ubsettings couldnt be initialized:" + e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
		}
		// ub_managers load
		try {
			PerformanceTimer timer = new PerformanceTimer();
			pstm = con.prepareStatement("SELECT * FROM ub_managers");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1UltimateBattle ub = getUb(rs.getInt("ub_id"));
				if (ub != null) {
					int npcId = rs.getInt("npc_id");
					boolean isErr = false;
					if (NpcTable.getInstance().getTemplate(npcId) == null) {
						System.out.println(String.format(I18N_DOES_NOT_EXIST_NPC_LIST, npcId));
						// %s はNPCリストに存在しません。
						isErr = true;
					}
					if (isErr) {
						continue;
					}
					ub.addManager(npcId);
				}
			}
			System.out.println("loading ub managers...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.warning("ub_managers couldnt be initialized:" + e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
		}
		// ub_times load
		try {
			pstm = con.prepareStatement("SELECT * FROM ub_times");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1UltimateBattle ub = getUb(rs.getInt("ub_id"));
				if (ub != null) {
					ub.addUbTime(rs.getInt("ub_time"));
				}
			}
		} catch (SQLException e) {
			_log.warning("ub_times couldnt be initialized:" + e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
		_log.fine("loaded ub: " + ubs.size() + " records");
	}

	public void reload() {
		HashMap<Integer, L1UltimateBattle> ubs = new HashMap<Integer, L1UltimateBattle>();
		loadUbs(ubs);
		_ubs = ubs;
	}
	
	public L1UltimateBattle getUb(int ubId) {
		return _ubs.get(ubId);
	}

	public Collection<L1UltimateBattle> getAllUb() {
		return Collections.unmodifiableCollection(_ubs.values());
	}

	public L1UltimateBattle getUbForNpcId(int npcId) {
		for (L1UltimateBattle ub : _ubs.values()) {
			if (ub.containsManager(npcId)) {
				return ub;
			}
		}
		return null;
	}

	/**
	 * 指定されたUBIDに対するパターンの最大数を返す。
	 * 
	 * @param ubId
	 *            調べるUBID。
	 * @return パターンの最大数。
	 */
	public int getMaxPattern(int ubId) {
		int n = 0;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT MAX(pattern) FROM spawn_ub_mobs WHERE ub_id=?");
			pstm.setInt(1, ubId);
			rs = pstm.executeQuery();
			if (rs.next()) {
				n = rs.getInt("ub_id");
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return n;
	}
}
