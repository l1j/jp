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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.templates.L1Town;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class TownTable {
	private static Logger _log = Logger.getLogger(TownTable.class.getName());

	private static TownTable _instance;

	private final Map<Integer, L1Town> _towns = new ConcurrentHashMap<Integer, L1Town>();

	public static TownTable getInstance() {
		if (_instance == null) {
			_instance = new TownTable();
		}
		return _instance;
	}

	private TownTable() {
		load();
	}

	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;	
		_towns.clear();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM towns");
			int townid;
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1Town town = new L1Town();
				townid = rs.getInt("id");
				town.setTownId(townid);
				town.setName(rs.getString("name"));
				town.setLeaderId(rs.getInt("leader_id"));
				town.setLeaderName(CharacterTable.getInstance().getCharName(town.getLeaderId()));
				town.setTaxRate(rs.getInt("tax_rate"));
				town.setTaxRateReserved(rs.getInt("tax_rate_reserved"));
				town.setSalesMoney(rs.getInt("sales_money"));
				town.setSalesMoneyYesterday(rs.getInt("sales_money_yesterday"));
				town.setTownTax(rs.getInt("town_tax"));
				town.setTownFixTax(rs.getInt("town_fix_tax"));
				_towns.put(new Integer(townid), town);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public L1Town[] getTownTableList() {
		return _towns.values().toArray(new L1Town[_towns.size()]);
	}

	public L1Town getTownTable(int id) {
		return _towns.get(id);
	}

	public boolean isLeader(L1PcInstance pc, int town_id) {
		L1Town town = getTownTable(town_id);
		return (town.getLeaderId() == pc.getId());
	}

	public synchronized void addSalesMoney(int town_id, int salesMoney) {
		Connection con = null;
		PreparedStatement pstm = null;
		L1Town town = TownTable.getInstance().getTownTable(town_id);
		int townTaxRate = town.getTaxRate();
		int townTax = salesMoney / 100 * townTaxRate;
		int townFixTax = salesMoney / 100 * 2;
		if (townTax <= 0 && townTaxRate > 0) {
			townTax = 1;
		}
		if (townFixTax <= 0 && townTaxRate > 0) {
			townFixTax = 1;
		}
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE towns SET sales_money = sales_money + ?, town_tax = town_tax + ?, town_fix_tax = town_fix_tax + ? WHERE id = ?");
			pstm.setInt(1, salesMoney);
			pstm.setInt(2, townTax);
			pstm.setInt(3, townFixTax);
			pstm.setInt(4, town_id);
			pstm.execute();
			town.setSalesMoney(town.getSalesMoney() + salesMoney);
			town.setTownTax(town.getTownTax() + townTax);
			town.setTownFixTax(town.getTownFixTax() + townFixTax);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void updateTaxRate() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE towns SET tax_rate = tax_rate_reserved");
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void updateSalesMoneyYesterday() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE towns SET sales_money_yesterday = sales_money, sales_money = 0");
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
}
