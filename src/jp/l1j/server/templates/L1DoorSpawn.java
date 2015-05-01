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

package jp.l1j.server.templates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;
import jp.l1j.server.utils.collections.Lists;

public class L1DoorSpawn {
	private static Logger _log = Logger.getLogger(L1DoorSpawn.class.getName());
	private final int _id;
	private final L1DoorGfx _gfx;
	private final int _x;
	private final int _y;
	private final int _mapId;
	private final L1Location _loc;
	private final int _hp;
	private final int _keeper;
	private final boolean _Open; //ドアの開閉状態：0（False) = 閉, 1（True) = 開

	public L1DoorSpawn(int id, L1DoorGfx gfx, int x, int y, int mapId, int hp,
			int keeper, boolean DoorOpen) {
		super();
		_id = id;
		_gfx = gfx;
		_x = x;
		_y = y;
		_mapId = mapId;
		_loc = new L1Location(_x, _y, _mapId);
		_hp = hp;
		_keeper = keeper;
		_Open = DoorOpen;
	}

	public int getId() {
		return _id;
	}

	public L1DoorGfx getGfx() {
		return _gfx;
	}

	public int getX() {
		return _x;
	}

	public int getY() {
		return _y;
	}

	public int getMapId() {
		return _mapId;
	}

	public L1Location getLocation() {
		return _loc;
	}

	public int getHp() {
		return _hp;
	}

	public int getKeeper() {
		return _keeper;
	}

	public boolean DoorOpen() {
		return _Open;
	}


	public static List<L1DoorSpawn> all() {
		List<L1DoorSpawn> result = Lists.newArrayList();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawn_doors");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				int gfxId = rs.getInt("gfx_id");
				int x = rs.getInt("loc_x");
				int y = rs.getInt("loc_y");
				int mapId = rs.getInt("map_id");
				int hp = rs.getInt("hp");
				int keeper = rs.getInt("npc_id");
				boolean isOpen = rs.getBoolean("is_open");
				L1DoorGfx gfx = L1DoorGfx.findByGfxId(gfxId);
				L1DoorSpawn spawn = new L1DoorSpawn(id, gfx, x, y, mapId, hp, keeper, isOpen);
				result.add(spawn);
			}

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return result;
	}
}
