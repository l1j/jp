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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_MAP_LIST;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.skill.L1BuffUtil;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class RandomDungeonTable {

	private static Logger _log = Logger.getLogger(RandomDungeonTable.class.getName());

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	private static RandomDungeonTable _instance = null;

	private static Map<String, NewDungeonRandom> _dungeons = new HashMap<String, NewDungeonRandom>();

	public static RandomDungeonTable getInstance() {
		if (_instance == null) {
			_instance = new RandomDungeonTable();
		}
		return _instance;
	}

	private RandomDungeonTable() {
		loadRandomDungeons(_dungeons);
	}
	
	private void loadRandomDungeons(Map<String, NewDungeonRandom> dungeons) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM random_dungeons");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int srcMapId = rs.getInt("src_map_id");
				short newMapId1 = rs.getShort("new_map_id1");
				short newMapId2 = rs.getShort("new_map_id2");
				short newMapId3 = rs.getShort("new_map_id3");
				short newMapId4 = rs.getShort("new_map_id4");
				short newMapId5 = rs.getShort("new_map_id5");
				boolean isErr = false;
				if (MapTable.getInstance().locationname(srcMapId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, srcMapId));
					// %s はマップリストに存在しません。
					isErr = true;
				}
				if (MapTable.getInstance().locationname(newMapId1) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, newMapId1));
					// %s はマップリストに存在しません。
					isErr = true;
				}
				if (MapTable.getInstance().locationname(newMapId2) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, newMapId2));
					// %s はマップリストに存在しません。
					isErr = true;
				}
				if (MapTable.getInstance().locationname(newMapId3) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, newMapId3));
					// %s はマップリストに存在しません。
					isErr = true;
				}
				if (MapTable.getInstance().locationname(newMapId4) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, newMapId4));
					// %s はマップリストに存在しません。
					isErr = true;
				}
				if (MapTable.getInstance().locationname(newMapId5) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, newMapId5));
					// %s はマップリストに存在しません。
					isErr = true;
				}
				if (isErr) {
					continue;
				}
				int srcX = rs.getInt("src_x");
				int srcY = rs.getInt("src_y");
				String key = new StringBuilder().append(srcMapId).append(srcX).append(srcY).toString();
				int[] newX = new int[5];
				int[] newY = new int[5];
				short[] newMapId = new short[5];
				newX[0] = rs.getInt("new_x1");
				newY[0] = rs.getInt("new_y1");
				newMapId[0] = newMapId1;
				newX[1] = rs.getInt("new_x2");
				newY[1] = rs.getInt("new_y2");
				newMapId[1] = newMapId2;
				newX[2] = rs.getInt("new_x3");
				newY[2] = rs.getInt("new_y3");
				newMapId[2] = newMapId3;
				newX[3] = rs.getInt("new_x4");
				newY[3] = rs.getInt("new_y4");
				newMapId[3] = newMapId4;
				newX[4] = rs.getInt("new_x5");
				newY[4] = rs.getInt("new_y5");
				newMapId[4] = newMapId5;
				int heading = rs.getInt("new_heading");
				NewDungeonRandom newDungeonRandom = new NewDungeonRandom(newX,
						newY, newMapId, heading);
				if (dungeons.containsKey(key)) {
					_log.log(Level.WARNING, "同じキーのdungeonデータがあります。key=" + key);
				}
				dungeons.put(key, newDungeonRandom);
			}
			System.out.println("loading random dungeons...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	public void reload() {
		Map<String, NewDungeonRandom> dungeons = new HashMap<String, NewDungeonRandom>();
		loadRandomDungeons(dungeons);
		_dungeons = dungeons;
	}
	
	private static class NewDungeonRandom {
		int[] _newX = new int[5];
		int[] _newY = new int[5];
		short[] _newMapId = new short[5];
		int _heading;
		private NewDungeonRandom(int[] newX, int[] newY, short[] newMapId,
				int heading) {
			for (int i = 0; i < 5; i++) {
				_newX[i] = newX[i];
				_newY[i] = newY[i];
				_newMapId[i] = newMapId[i];
			}
			_heading = heading;
		}
	}

	public boolean dg(int locX, int locY, int mapId, L1PcInstance pc) {
		String key = new StringBuilder().append(mapId).append(locX).append(locY).toString();
		if (_dungeons.containsKey(key)) {
			int rnd = _random.nextInt(5);
			NewDungeonRandom newDungeonRandom = _dungeons.get(key);
			short newMap = newDungeonRandom._newMapId[rnd];
			int newX = newDungeonRandom._newX[rnd];
			int newY = newDungeonRandom._newY[rnd];
			int heading = newDungeonRandom._heading;
			// 3秒間は無敵（アブソルートバリア状態）にする。
			L1BuffUtil.barrier(pc, 3000);
			L1Teleport.teleport(pc, newX, newY, newMap, heading, false);
			return true;
		}
		return false;
	}
}
