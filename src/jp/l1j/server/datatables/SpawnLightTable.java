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
import jp.l1j.server.model.instance.L1FieldObjectInstance;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class SpawnLightTable {
	private static Logger _log = Logger.getLogger(SpawnLightTable.class.getName());

	private static SpawnLightTable _instance;

	public static SpawnLightTable getInstance() {
		if (_instance == null) {
			_instance = new SpawnLightTable();
		}
		return _instance;
	}

	private SpawnLightTable() {
		FillLightSpawnTable();
	}

	private void FillLightSpawnTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawn_lights");
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
					L1FieldObjectInstance field =
							(L1FieldObjectInstance) constructor.newInstance(parameters);
					field.setId(IdFactory.getInstance().nextId());
					field.setX(rs.getInt("loc_x"));
					field.setY(rs.getInt("loc_y"));
					field.setMap((short) rs.getInt("map_id"));
					field.setHomeX(field.getX());
					field.setHomeY(field.getY());
					field.setHeading(0);
					L1World.getInstance().storeObject(field);
					L1World.getInstance().addVisibleObject(field);
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
}
