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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.map.executor.L1MapLimiter;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public final class MapTable {
	private class MapData {
		public String locationname = null;// TODO　マップ名称検索用
		public int startX = 0;
		public int endX = 0;
		public int startY = 0;
		public int endY = 0;
		public double monster_amount = 1;
		public double dropRate = 1;
		public double uniqueRate = 1;
		public boolean isUnderwater = false;
		public boolean markable = false;
		public boolean teleportable = false;
		public boolean escapable = false;
		public boolean isUseResurrection = false;
		public boolean isUsePainwand = false;
		public boolean isEnabledDeathPenalty = false;
		public boolean isTakePets = false;
		public boolean isRecallPets = false;
		public boolean isUsableItem = false;
		public boolean isUsableSkill = false;
	}

	private static Logger _log = Logger.getLogger(MapTable.class.getName());

	private static MapTable _instance;

	/**
	 * KeyにマップID、Valueにテレポート可否フラグが格納されるHashMap
	 */
	private static Map<Integer, MapData> _maps = new HashMap<Integer, MapData>();

	/**
	 * 新しくMapsTableオブジェクトを生成し、マップのテレポート可否フラグを読み込む。
	 */
	private MapTable() {
		loadMaps(_maps);
	}

	/**
	 * マップのテレポート可否フラグをデータベースから読み込み、HashMap _mapsに格納する。
	 */
	private void loadMaps(Map<Integer, MapData> maps) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM map_ids");
			for (rs = pstm.executeQuery(); rs.next();) {
				MapData data = new MapData();
				int mapId = rs.getInt("id");
				// TODO マップ名称検索用
				data.locationname = rs.getString("name");
				data.startX = rs.getInt("start_x");
				data.endX = rs.getInt("end_x");
				data.startY = rs.getInt("start_y");
				data.endY = rs.getInt("end_y");
				data.monster_amount = rs.getDouble("monster_amount");
				data.dropRate = rs.getDouble("drop_rate");
				data.uniqueRate = rs.getDouble("unique_rate");
				data.isUnderwater = rs.getBoolean("underwater");
				data.markable = rs.getBoolean("markable");
				data.teleportable = rs.getBoolean("teleportable");
				data.escapable = rs.getBoolean("escapable");
				data.isUseResurrection = rs.getBoolean("resurrection");
				data.isUsePainwand = rs.getBoolean("painwand");
				data.isEnabledDeathPenalty = rs.getBoolean("penalty");
				data.isTakePets = rs.getBoolean("take_pets");
				data.isRecallPets = rs.getBoolean("recall_pets");
				data.isUsableItem = rs.getBoolean("usable_item");
				data.isUsableSkill = rs.getBoolean("usable_skill");
				maps.put(new Integer(mapId), data);
			}
			_log.config("Maps " + maps.size());
			System.out.println("loading map ids...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	public void reload() {
		Map<Integer, MapData> maps = new HashMap<Integer, MapData>();
		loadMaps(maps);
		_maps = maps;
	}

	/**
	 * MapsTableのインスタンスを返す。
	 *
	 * @return MapsTableのインスタンス
	 */
	public static MapTable getInstance() {
		if (_instance == null) {
			_instance = new MapTable();
		}
		return _instance;
	}

	/**
	 * マップの全座標情報を返す。
	 *
	 * @return マップid,X開始座標,X終了座標,Y開始座標,Y終了座標
	 */
	public ArrayList<ArrayList<Integer>> getMapInfo() {
		ArrayList<ArrayList<Integer>> maps = new ArrayList<ArrayList<Integer>>();
    for (Iterator<Map.Entry<Integer, MapData>> it = _maps.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<Integer, MapData> entry = it.next();
			Integer key = entry.getKey();
			MapData val = entry.getValue();
			ArrayList<Integer> map = new ArrayList<Integer>();
			map.add(key);
			map.add(val.startX);
			map.add(val.endX);
			map.add(val.startY);
			map.add(val.endY);
			maps.add(map);
		}
		return maps;
	}
	
	// TODO マップ名称検索　start
	/**
	 * マップ名称を返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 * @return locationname
	 */
	public String locationname(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return null;
		}
		return _maps.get(mapId).locationname;
	}
	// TODO マップ名称検索 end
	
	/**
	 * マップのX開始座標を返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 * @return X開始座標
	 */
	public int getStartX(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startX;
	}

	/**
	 * マップのX終了座標を返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 * @return X終了座標
	 */
	public int getEndX(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endX;
	}

	/**
	 * マップのY開始座標を返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 * @return Y開始座標
	 */
	public int getStartY(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startY;
	}

	/**
	 * マップのY終了座標を返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 * @return Y終了座標
	 */
	public int getEndY(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endY;
	}

	/**
	 * マップのモンスター量倍率を返す
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 * @return モンスター量の倍率
	 */
	public double getMonsterAmount(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.monster_amount;
	}

	/**
	 * マップのドロップ倍率を返す
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 * @return ドロップ倍率
	 */
	public double getDropRate(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.dropRate;
	}

	/**
	 * マップのユニークドロップ倍率を返す
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 * @return ユニークドロップ倍率
	 */
	public double getUniqueRate(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.uniqueRate;
	}

	/**
	 * マップが、水中であるかを返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 *
	 * @return 水中であればtrue
	 */
	public boolean isUnderwater(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUnderwater;
	}

	/**
	 * マップが、ブックマーク可能であるかを返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 * @return ブックマーク可能であればtrue
	 */
	public boolean isMarkable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).markable;
	}

	/**
	 * マップが、ランダムテレポート可能であるかを返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 * @return 可能であればtrue
	 */
	public boolean isTeleportable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).teleportable;
	}

	/**
	 * マップが、MAPを超えたテレポート可能であるかを返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 * @return 可能であればtrue
	 */
	public boolean isEscapable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).escapable;
	}

	/**
	 * マップが、復活可能であるかを返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 *
	 * @return 復活可能であればtrue
	 */
	public boolean isUseResurrection(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUseResurrection;
	}

	/**
	 * マップが、パインワンド使用可能であるかを返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 *
	 * @return パインワンド使用可能であればtrue
	 */
	public boolean isUsePainwand(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsePainwand;
	}

	/**
	 * マップが、デスペナルティがあるかを返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 *
	 * @return デスペナルティであればtrue
	 */
	public boolean isEnabledDeathPenalty(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isEnabledDeathPenalty;
	}

	/**
	 * マップが、ペット・サモンを連れて行けるかを返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 *
	 * @return ペット・サモンを連れて行けるならばtrue
	 */
	public boolean isTakePets(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isTakePets;
	}

	/**
	 * マップが、ペット・サモンを呼び出せるかを返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 *
	 * @return ペット・サモンを呼び出せるならばtrue
	 */
	public boolean isRecallPets(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isRecallPets;
	}

	/**
	 * マップが、アイテムを使用できるかを返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 *
	 * @return アイテムを使用できるならばtrue
	 */
	public boolean isUsableItem(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableItem;
	}

	/**
	 * マップが、スキルを使用できるかを返す。
	 *
	 * @param mapId
	 *            調べるマップのマップID
	 *
	 * @return スキルを使用できるならばtrue
	 */
	public boolean isUsableSkill(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableSkill;
	}

	public L1MapLimiter newMapTimeExecutor() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}
