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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_ITEM_LIST;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public final class ResolventTable {
	private static Logger _log = Logger.getLogger(ResolventTable.class.getName());

	private static ResolventTable _instance;

	private static Map<Integer, Integer> _resolvents = new HashMap<Integer, Integer>();

	public static ResolventTable getInstance() {
		if (_instance == null) {
			_instance = new ResolventTable();
		}
		return _instance;
	}

	private ResolventTable() {
		loadResolvents(_resolvents);
	}

	private void loadResolvents(Map<Integer, Integer> resolvents) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM resolvents");
			for (rs = pstm.executeQuery(); rs.next();) {
				int itemId = rs.getInt("item_id");
				boolean isErr = false;
				if (ItemTable.getInstance().getTemplate(itemId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, itemId));
					// %s はアイテムリストに存在しません。
					isErr = true;
				}
				if (isErr) {
					continue;
				}
				int crystalCount = rs.getInt("crystal_count");
				resolvents.put(new Integer(itemId), crystalCount);
			}
			_log.config("resolvent " + resolvents.size());
			System.out.println("loading resolvents...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	public void reload() {
		Map<Integer, Integer> resolvents = new HashMap<Integer, Integer>();
		loadResolvents(resolvents);
		_resolvents = resolvents;
	}
	
	public int getCrystalCount(int itemId) {
		int crystalCount = 0;
		if (_resolvents.containsKey(itemId)) {
			crystalCount = _resolvents.get(itemId);
		}
		return crystalCount;
	}
}
