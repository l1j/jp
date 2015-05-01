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

package jp.l1j.server.model.map;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.datatables.MapTable;

/**
 * テキストマップ(maps/\d*.txt)を読み込む.
 */
public class TextMapReader extends MapReader {

	/** メッセージログ用. */
	private static Logger _log = Logger
			.getLogger(TextMapReader.class.getName());

	/** マップホルダー. */
	private static final String MAP_DIR = "./maps/";

	/** MAP_INFO用 マップ番号位置. */
	public static final int MAPINFO_MAP_NO = 0;

	/** MAP_INFO用 開始X座標の位置. */
	public static final int MAPINFO_START_X = 1;

	/** MAP_INFO用 最終X座標の位置. */
	public static final int MAPINFO_END_X = 2;

	/** MAP_INFO用 開始Y座標の位置. */
	public static final int MAPINFO_START_Y = 3;

	/** MAP_INFO用 開始Y座標の位置. */
	public static final int MAPINFO_END_Y = 4;

	/**
	 * 指定のマップ番号のテキストマップを読み込む.
	 * 
	 * @param mapId
	 *            マップ番号
	 * @param xSize
	 *            X座標のサイズ
	 * @param ySize
	 *            Y座標のサイズ
	 * @return byte[][]
	 * @throws IOException
	 */
	public byte[][] read(final int mapId, final int xSize, final int ySize)
			throws IOException {
		byte[][] map = new byte[xSize][ySize];
		LineNumberReader in = new LineNumberReader(new BufferedReader(
				new FileReader(MAP_DIR + mapId + ".txt")));

		int y = 0;
		String line;
		while ((line = in.readLine()) != null) {
			if (line.trim().length() == 0 || line.startsWith("#")) {
				continue; // 空行とコメントをスキップ
			}

			int x = 0;
			StringTokenizer tok = new StringTokenizer(line, ",");
			while (tok.hasMoreTokens()) {
				byte tile = Byte.parseByte(tok.nextToken());
				map[x][y] = tile;

				x++;
			}
			y++;
		}
		in.close();
		return map;
	}

	/**
	 * 指定のマップ番号のテキストマップを読み込む.
	 * 
	 * @param id
	 *            マップ番号
	 * @return L1Map
	 * @throws IOException
	 */
	@Override
	public L1Map read(final int id) throws IOException {
		for (ArrayList<Integer> info : MAP_INFO) {
			int mapId = info.get(MAPINFO_MAP_NO);
			int xSize = info.get(MAPINFO_END_X) - info.get(MAPINFO_START_X) + 1;
			int ySize = info.get(MAPINFO_END_Y) - info.get(MAPINFO_START_Y) + 1;

			if (mapId == id) {
				L1V1Map map = new L1V1Map((short) mapId,
						this.read(mapId, xSize, ySize),
						info.get(MAPINFO_START_X),
						info.get(MAPINFO_START_Y),
						MapTable.getInstance().locationname(mapId),// TODO
						// マップ名称検索用
						MapTable.getInstance().isUnderwater(mapId), MapTable
								.getInstance().isMarkable(mapId), MapTable
								.getInstance().isTeleportable(mapId), MapTable
								.getInstance().isEscapable(mapId), MapTable
								.getInstance().isUseResurrection(mapId),
						MapTable.getInstance().isUsePainwand(mapId), MapTable
								.getInstance().isEnabledDeathPenalty(mapId),
						MapTable.getInstance().isTakePets(mapId), MapTable
								.getInstance().isRecallPets(mapId), MapTable
								.getInstance().isUsableItem(mapId), MapTable
								.getInstance().isUsableSkill(mapId));
				return map;
			}
		}
		throw new FileNotFoundException("MapId: " + id);
	}

	/**
	 * 全てのテキストマップを読み込む.
	 * 
	 * @return Map
	 * @throws IOException
	 */
	@Override
	public Map<Integer, L1Map> read() throws IOException {
		Map<Integer, L1Map> maps = new HashMap<Integer, L1Map>();

		for (ArrayList<Integer> info : MAP_INFO) {
			int mapId = info.get(MAPINFO_MAP_NO);
			int xSize = info.get(MAPINFO_END_X) - info.get(MAPINFO_START_X) + 1;
			int ySize = info.get(MAPINFO_END_Y) - info.get(MAPINFO_START_Y) + 1;

			try {
				L1V1Map map = new L1V1Map((short) mapId,
						this.read(mapId, xSize, ySize),
						info.get(MAPINFO_START_X),
						info.get(MAPINFO_START_Y),
						MapTable.getInstance().locationname(mapId),// TODO
						// マップ名称検索用
						MapTable.getInstance().isUnderwater(mapId), MapTable
								.getInstance().isMarkable(mapId), MapTable
								.getInstance().isTeleportable(mapId), MapTable
								.getInstance().isEscapable(mapId), MapTable
								.getInstance().isUseResurrection(mapId),
						MapTable.getInstance().isUsePainwand(mapId), MapTable
								.getInstance().isEnabledDeathPenalty(mapId),
						MapTable.getInstance().isTakePets(mapId), MapTable
								.getInstance().isRecallPets(mapId), MapTable
								.getInstance().isUsableItem(mapId), MapTable
								.getInstance().isUsableSkill(mapId));

				maps.put(mapId, map);
			} catch (IOException e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}

		return maps;
	}

	/**
	 * 全マップIDのリストを返す.
	 * 
	 * @return List<Integer> 全マップIDのリスト
	 */
	public static List<Integer> listMapIds() {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (ArrayList<Integer> info : MAP_INFO) {
			ids.add(info.get(MAPINFO_MAP_NO));
		}
		return ids;
	}

	/**
	 * mapInfo：マップNo、マップサイズを保持している.
	 * 1レコードが{mapNo,StartX,EndX,StartY,EndY}で構成されている.
	 */
private static final ArrayList<ArrayList<Integer>> MAP_INFO =
				MapTable.getInstance().getMapInfo();

}
