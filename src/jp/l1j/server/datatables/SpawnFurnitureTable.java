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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1FurnitureInstance;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class SpawnFurnitureTable {
	private static Logger _log = Logger.getLogger(SpawnFurnitureTable.class.getName());

	private static SpawnFurnitureTable _instance;

	public static SpawnFurnitureTable getInstance() {
		if (_instance == null) {
			_instance = new SpawnFurnitureTable();
		}
		return _instance;
	}

	private SpawnFurnitureTable() {
		FillFurnitureSpawnTable();
	}

	private void FillFurnitureSpawnTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawn_furnitures");
			rs = pstm.executeQuery();
			do {
				if (!rs.next()) {
					break;
				}
				L1Npc l1npc = NpcTable.getInstance().getTemplate(rs.getInt(2));
				if (l1npc != null) {
					String s = l1npc.getImpl();
					Constructor constructor =
						Class.forName("jp.l1j.server.model.instance." + s + "Instance").getConstructors()[0];
					Object parameters[] = { l1npc };
					L1FurnitureInstance furniture =
							(L1FurnitureInstance) constructor.newInstance(parameters);
					furniture = (L1FurnitureInstance) constructor.newInstance(parameters);
					furniture.setId(IdFactory.getInstance().nextId());
					furniture.setItemObjId(rs.getInt(1));
					furniture.setX(rs.getInt(3));
					furniture.setY(rs.getInt(4));
					furniture.setMap((short) rs.getInt(5));
					furniture.setHomeX(furniture.getX());
					furniture.setHomeY(furniture.getY());
					furniture.setHeading(0);
					L1World.getInstance().storeObject(furniture);
					L1World.getInstance().addVisibleObject(furniture);
				}
			} while (true);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (SecurityException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (ClassNotFoundException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (IllegalArgumentException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (InstantiationException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (IllegalAccessException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (InvocationTargetException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void insertFurniture(L1FurnitureInstance furniture) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO spawn_furnitures SET item_obj_id=?, npc_id=?, loc_x=?, loc_y=?, map_id=?");
			pstm.setInt(1, furniture.getItemObjId());
			pstm.setInt(2, furniture.getNpcTemplate().getNpcId());
			pstm.setInt(3, furniture.getX());
			pstm.setInt(4, furniture.getY());
			pstm.setInt(5, furniture.getMapId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void deleteFurniture(L1FurnitureInstance furniture) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM spawn_furnitures WHERE item_obj_id=?");
			pstm.setInt(1, furniture.getItemObjId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
}
