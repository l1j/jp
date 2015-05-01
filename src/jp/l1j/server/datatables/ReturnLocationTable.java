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

package jp.l1j.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_MAP_LIST;
import jp.l1j.server.model.L1TownLocation;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class ReturnLocationTable {
	private static Logger _log = Logger.getLogger(ReturnLocationTable.class.getName());

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	private static HashMap<Integer, ArrayList<ReturnLocationTable>> _returnLocations =
			new HashMap<Integer, ArrayList<ReturnLocationTable>>();

	private int _areaX1;
	private int _areaY1;
	private int _areaX2;
	private int _areaY2;
	private int _areaMapId;
	private int _getbackX1;
	private int _getbackY1;
	private int _getbackX2;
	private int _getbackY2;
	private int _getbackX3;
	private int _getbackY3;
	private int _getbackMapId;
	private int _getbackTownId;
	private int _getbackTownIdForElf;
	private int _getbackTownIdForDarkelf;
	private boolean _escapable; // 未使用(map_idsに持っているし、ここに来る前にチェックされている)

	private boolean isSpecifyArea() {
		return (_areaX1 != 0 && _areaY1 != 0 && _areaX2 != 0 && _areaY2 != 0);
	}

	public static void loadReturnLocations(HashMap<Integer, ArrayList<ReturnLocationTable>> returnLocations) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			// 同マップでエリア指定と無指定が混在していたら、エリア指定を先に読み込む為にarea_x1 DESC
			String sSQL = "SELECT * FROM return_locations ORDER BY area_map_id,area_x1 DESC ";
			pstm = con.prepareStatement(sSQL);
			rs = pstm.executeQuery();
			while (rs.next()) {
				int areaMapId = rs.getInt("area_map_id");
				int getbackMapId = rs.getInt("getback_map_id");
				boolean isErr = false;
				if (MapTable.getInstance().locationname(areaMapId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, areaMapId));
					// %s はマップリストに存在しません。
					isErr = true;
				}
				if (MapTable.getInstance().locationname(getbackMapId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, getbackMapId));
					// %s はマップリストに存在しません。
					isErr = true;
				}
				if (isErr) {
					continue;
				}
				ReturnLocationTable getback = new ReturnLocationTable();
				getback._areaX1 = rs.getInt("area_x1");
				getback._areaY1 = rs.getInt("area_y1");
				getback._areaX2 = rs.getInt("area_x2");
				getback._areaY2 = rs.getInt("area_y2");
				getback._areaMapId = rs.getInt("area_map_id");
				getback._getbackX1 = rs.getInt("getback_x1");
				getback._getbackY1 = rs.getInt("getback_y1");
				getback._getbackX2 = rs.getInt("getback_x2");
				getback._getbackY2 = rs.getInt("getback_y2");
				getback._getbackX3 = rs.getInt("getback_x3");
				getback._getbackY3 = rs.getInt("getback_y3");
				getback._getbackMapId = rs.getInt("getback_map_id");
				getback._getbackTownId = rs.getInt("getback_town_id");
				getback._getbackTownIdForElf = rs.getInt("getback_town_id_elf");
				getback._getbackTownIdForDarkelf = rs.getInt("getback_town_id_darkelf");
				getback._escapable = rs.getBoolean("scroll_escape");
				ArrayList<ReturnLocationTable> getbackList = returnLocations.get(getback._areaMapId);
				if (getbackList == null) {
					getbackList = new ArrayList<ReturnLocationTable>();
					returnLocations.put(getback._areaMapId, getbackList);
				}
				getbackList.add(getback);
			}
			System.out.println("loading return locations...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (Exception e) {
			_log.log(Level.SEVERE, "could not Get Getback data", e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	public static void load() {
		loadReturnLocations(_returnLocations);
	}
	
	public static void reload() {
		HashMap<Integer, ArrayList<ReturnLocationTable>> returnLocations =
				new HashMap<Integer, ArrayList<ReturnLocationTable>>();
		loadReturnLocations(returnLocations);
		_returnLocations = returnLocations;
	}

	/**
	 * pcの現在地から帰還ポイントを取得する。
	 * 
	 * @param pc
	 * @param bScroll_Escape
	 *            (未使用)
	 * @return locx,locy,mapidの順に格納されている配列
	 */
	public static int[] getReturnLocation(L1PcInstance pc, boolean bScroll_Escape) {
		int[] loc = new int[3];
		int nPosition = _random.nextInt(3);
		int pcLocX = pc.getX();
		int pcLocY = pc.getY();
		int pcMapId = pc.getMapId();
		ArrayList<ReturnLocationTable> getbackList = _returnLocations.get(pcMapId);
		if (getbackList != null) {
			ReturnLocationTable getback = null;
			for (ReturnLocationTable gb : getbackList) {
				if (gb.isSpecifyArea()) {
					if (gb._areaX1 <= pcLocX && pcLocX <= gb._areaX2
							&& gb._areaY1 <= pcLocY && pcLocY <= gb._areaY2) {
						getback = gb;
						break;
					}
				} else {
					getback = gb;
					break;
				}
			}
			loc = ReadGetbackInfo(getback, nPosition);
			// town_idが指定されている場合はそこへ帰還させる
			if (pc.isElf() && getback._getbackTownIdForElf > 0) {
				loc = L1TownLocation.getGetBackLoc(getback._getbackTownIdForElf);
			} else if (pc.isDarkelf() && getback._getbackTownIdForDarkelf > 0) {
				loc = L1TownLocation.getGetBackLoc(getback._getbackTownIdForDarkelf);
			} else if (getback._getbackTownId > 0) {
				loc = L1TownLocation.getGetBackLoc(getback._getbackTownId);
			}
		}
		// getbackテーブルにデータがない場合、SKTに帰還
		else {
			loc[0] = 33089;
			loc[1] = 33397;
			loc[2] = 4;
		}
		return loc;
	}

	private static int[] ReadGetbackInfo(ReturnLocationTable getback, int nPosition) {
		int[] loc = new int[3];
		switch (nPosition) {
		case 0:
			loc[0] = getback._getbackX1;
			loc[1] = getback._getbackY1;
			break;
		case 1:
			loc[0] = getback._getbackX2;
			loc[1] = getback._getbackY2;
			break;
		case 2:
			loc[0] = getback._getbackX3;
			loc[1] = getback._getbackY3;
			break;
		}
		loc[2] = getback._getbackMapId;
		return loc;
	}
}
