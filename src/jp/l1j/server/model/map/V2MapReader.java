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

package jp.l1j.server.model.map;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;
import jp.l1j.server.datatables.MapTable;
import jp.l1j.server.utils.BinaryInputStream;
import jp.l1j.server.utils.FileUtil;

/**
 * テキストマップ(v2maps/\d*.txt)を読み込む（テスト用).
 */
public class V2MapReader extends MapReader {

	/** マップホルダー. */
	private static final String MAP_DIR = "./v2maps/";

	/**
	 * 全マップIDのリストを返す.
	 * 
	 * @return ArraryList
	 */
	private ArrayList<Integer> listMapIds() {
		ArrayList<Integer> ids = new ArrayList<Integer>();

		File mapDir = new File(MAP_DIR);
		for (String name : mapDir.list()) {
			File mapFile = new File(mapDir, name);
			if (!mapFile.exists()) {
				continue;
			}
			if (!FileUtil.getExtension(mapFile).toLowerCase().equals("md")) {
				continue;
			}
			int id = 0;
			try {
				String idStr = FileUtil.getNameWithoutExtension(mapFile);
				id = Integer.parseInt(idStr);
			} catch (NumberFormatException e) {
				continue;
			}
			ids.add(id);
		}
		return ids;
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
		for (int id : listMapIds()) {
			maps.put(id, read(id));
		}
		return maps;
	}

	/**
	 * 指定のマップ番号のキャッシュマップを読み込む.
	 * 
	 * @param mapId
	 *            マップ番号
	 * @return L1Map
	 * @throws IOException
	 */
	@Override
	public L1Map read(final int mapId) throws IOException {
		File file = new File(MAP_DIR + mapId + ".md");
		if (!file.exists()) {
			throw new FileNotFoundException("MapId: " + mapId);
		}

		BinaryInputStream in = new BinaryInputStream(new BufferedInputStream(
				new InflaterInputStream(new FileInputStream(file))));

		int id = in.readInt();
		if (mapId != id) {
			throw new FileNotFoundException("MapId: " + mapId);
		}

		int xLoc = in.readInt();
		int yLoc = in.readInt();
		int width = in.readInt();
		int height = in.readInt();

		byte[] tiles = new byte[width * height * 2];
		for (int i = 0; i < width * height * 2; i++) {
			tiles[i] = (byte) in.readByte();
		}
		in.close();

		L1V2Map map = new L1V2Map(id, tiles, xLoc, yLoc, width, height,
				MapTable.getInstance().isUnderwater(mapId),
				MapTable.getInstance().isMarkable(mapId),
				MapTable.getInstance().isTeleportable(mapId),
				MapTable.getInstance().isEscapable(mapId),
				MapTable.getInstance().isUseResurrection(mapId),
				MapTable.getInstance().isUsePainwand(mapId),
				MapTable.getInstance().isEnabledDeathPenalty(mapId),
				MapTable.getInstance().isTakePets(mapId),
				MapTable.getInstance().isRecallPets(mapId),
				MapTable.getInstance().isUsableItem(mapId),
				MapTable.getInstance().isUsableSkill(mapId));
		return map;
	}
}
