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
import jp.l1j.server.model.item.L1ItemRate;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class ItemRateTable {
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(ItemRateTable.class.getName());

	private static ItemRateTable _instance;

	private static Map<Integer, L1ItemRate> _itemRates = new HashMap<Integer, L1ItemRate>();

	public static ItemRateTable getInstance() {
		if (_instance == null) {
			_instance = new ItemRateTable();
		}
		return _instance;
	}

	private ItemRateTable() {
		loadItemRates(_itemRates);
	}

	private void loadItemRates(Map<Integer, L1ItemRate> itemRates) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT item_id, selling_price, purchasing_price FROM item_rates ORDER BY item_id");		
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
				L1ItemRate rate = new L1ItemRate(
					itemId,
					rs.getDouble("selling_price"),
					rs.getDouble("purchasing_price")	
				);
				itemRates.put(rate.getItemId(), rate);
			}
			System.out.println("loading item rates...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	public void reload() {
		Map<Integer, L1ItemRate> itemRates = new HashMap<Integer, L1ItemRate>();
		loadItemRates(itemRates);
		_itemRates = itemRates;
	}
	
	public L1ItemRate get(int itemId) {
		return _itemRates.get(itemId);
	}
}
