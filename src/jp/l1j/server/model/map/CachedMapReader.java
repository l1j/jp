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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jp.l1j.server.datatables.MapTable;

/**
 * テキストマップをキャッシングして読み込み時間を短縮する.
 */
public class CachedMapReader extends MapReader {

	/** テキストマップホルダー. */
	private static final String MAP_DIR = "./maps/";

	/** キャッシングするマップホルダー. */
	private static final String CACHE_DIR = "./data/mapcache/";

	/**
	 * 指定のマップ番号のテキストマップをキャッシュマップに変更する.
	 * 
	 * @param mapId
	 *            マップ番号
	 * @return L1V1Map
	 * @throws IOException
	 */
	private L1V1Map cacheMap(final int mapId) throws IOException {
		File file = new File(CACHE_DIR);
		if (!file.exists()) {
			file.mkdir();
		}

		L1V1Map map = (L1V1Map) new TextMapReader().read(mapId);

		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(CACHE_DIR + mapId + ".map")));

		out.writeInt(map.getId());
		out.writeInt(map.getX());
		out.writeInt(map.getY());
		out.writeInt(map.getWidth());
		out.writeInt(map.getHeight());

		for (byte[] line : map.getRawTiles()) {
			for (byte tile : line) {
				out.writeByte(tile);
			}
		}
		out.flush();
		out.close();

		return map;
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
		File file = new File(CACHE_DIR + mapId + ".map");
		if (!file.exists()) {
			return cacheMap(mapId);
		}

		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(CACHE_DIR + mapId + ".map")));

		int id = in.readInt();
		if (mapId != id) {
			throw new FileNotFoundException();
		}

		int xLoc = in.readInt();
		int yLoc = in.readInt();
		int width = in.readInt();
		int height = in.readInt();

		byte[][] tiles = new byte[width][height];
		for (byte[] line : tiles) {
			in.read(line);
		}

		in.close();
		L1V1Map map = new L1V1Map(id, tiles, xLoc, yLoc, MapTable
				.getInstance().locationname(mapId),// TODO マップ名称検索用
				MapTable.getInstance().isUnderwater(mapId), MapTable
						.getInstance().isMarkable(mapId), MapTable
						.getInstance().isTeleportable(mapId), MapTable
						.getInstance().isEscapable(mapId), MapTable
						.getInstance().isUseResurrection(mapId), MapTable
						.getInstance().isUsePainwand(mapId), MapTable
						.getInstance().isEnabledDeathPenalty(mapId), MapTable
						.getInstance().isTakePets(mapId), MapTable
						.getInstance().isRecallPets(mapId), MapTable
						.getInstance().isUsableItem(mapId), MapTable
						.getInstance().isUsableSkill(mapId));
		return map;
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
		for (int id : TextMapReader.listMapIds()) {
			maps.put(id, read(id));
		}
		return maps;
	}
}
