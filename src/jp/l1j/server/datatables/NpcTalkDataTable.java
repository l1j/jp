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

import java.sql.*;
import java.util.HashMap;
import java.util.logging.Logger;
import jp.l1j.server.model.L1NpcTalkData;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class NpcTalkDataTable {
	private static Logger _log = Logger.getLogger(NpcTalkDataTable.class.getName());

	private static NpcTalkDataTable _instance;

	private HashMap<Integer, L1NpcTalkData> _datatable = new HashMap<Integer, L1NpcTalkData>();

	public static NpcTalkDataTable getInstance() {
		if (_instance == null) {
			_instance = new NpcTalkDataTable();
		}
		return _instance;
	}

	private NpcTalkDataTable() {
		parseList();
	}

	private void parseList() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM npc_actions");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1NpcTalkData l1npctalkdata = new L1NpcTalkData();
				l1npctalkdata.setNpcID(rs.getInt("npc_id"));
				l1npctalkdata.setNormalAction(rs.getString("normal_action"));
				l1npctalkdata.setChaoticAction(rs.getString("chaotic_action"));
				l1npctalkdata.setTeleportURL(rs.getString("teleport_url"));
				l1npctalkdata.setTeleportURLA(rs.getString("teleport_urla"));
				_datatable.put(new Integer(l1npctalkdata.getNpcID()), l1npctalkdata);
			}
			_log.fine("loaded npc action: " + _datatable.size() + " records");
		} catch (SQLException e) {
			_log.warning("error while creating npc action table " + e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public L1NpcTalkData getTemplate(int i) {
		return _datatable.get(new Integer(i));
	}
}
