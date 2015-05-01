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

package jp.l1j.server.controller.timer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.CharacterTable;
import jp.l1j.server.datatables.TownTable;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.gametime.L1GameTime;
import jp.l1j.server.model.gametime.L1GameTimeAdapter;
import jp.l1j.server.model.gametime.L1GameTimeClock;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_PacketBox;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class HomeTownTimeController {
	private static Logger _log = Logger.getLogger(HomeTownTimeController.class.getName());

	private static HomeTownTimeController _instance;

	public static HomeTownTimeController getInstance() {
		if (_instance == null) {
			_instance = new HomeTownTimeController();
		}
		return _instance;
	}

	private HomeTownTimeController() {
		startListener();
	}

	private static L1TownFixedProcListener _listener;

	private void startListener() {
		if (_listener == null) {
			_listener = new L1TownFixedProcListener();
			L1GameTimeClock.getInstance().addListener(_listener);
		}
	}

	private class L1TownFixedProcListener extends L1GameTimeAdapter {
		@Override
		public void onDayChanged(L1GameTime time) {
			fixedProc(time);
		}
	}

	private void fixedProc(L1GameTime time) {
		Calendar cal = time.getCalendar();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if (day == 25) {
			monthlyProc();
		} else {
			dailyProc();
		}
	}

	public void dailyProc() {
		_log.info(I18N_HOME_TOWN_SYS_DAILY_PROC);
		// ホームタウンシステム: 日時処理
		TownTable.getInstance().updateTaxRate();
		TownTable.getInstance().updateSalesMoneyYesterday();
		TownTable.getInstance().load();
	}

	public void monthlyProc() {
		_log.info(I18N_HOME_TOWN_SYS_MONTHLY_PROC);
		// ホームタウンシステム: 月次処理
		L1World.getInstance().setProcessingContributionTotal(true);
		Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();
		for (L1PcInstance pc : players) {
			try {
				// DBにキャラクター情報を書き込む
				pc.save();
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
		
		for (int townId = 1; townId <= 10; townId++) {
			int leaderId = totalContribution(townId);
			if (leaderId > 0) {
				String leaderName = CharacterTable.getInstance().getCharName(leaderId);
				S_PacketBox packet = new S_PacketBox(S_PacketBox.MSG_TOWN_LEADER, leaderName);
				for (L1PcInstance pc : players) {
					if (pc.getHomeTownId() == townId) {
						pc.setContribution(0);
						pc.sendPackets(packet);
					}
				}
			}
		}
		TownTable.getInstance().load();
		for (L1PcInstance pc : players) {
			if (pc.getHomeTownId() == -1) {
				pc.setHomeTownId(0);
			}
			pc.setContribution(0);
			try {
				// DBにキャラクター情報を書き込む
				pc.save();
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
		clearHomeTownID();
		L1World.getInstance().setProcessingContributionTotal(false);
	}

	private static int totalContribution(int townId) {
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs1 = null;
		PreparedStatement pstm2 = null;
		ResultSet rs2 = null;
		PreparedStatement pstm3 = null;
		ResultSet rs3 = null;
		PreparedStatement pstm4 = null;
		PreparedStatement pstm5 = null;
		int leaderId = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT id, name FROM characters WHERE hometown_id = ? ORDER BY contribution DESC");
			pstm1.setInt(1, townId);
			rs1 = pstm1.executeQuery();
			if (rs1.next()) {
				leaderId = rs1.getInt("id");
			}
			double totalContribution = 0;
			pstm2 = con.prepareStatement("SELECT SUM(contribution) AS total_contribution FROM characters WHERE hometown_id = ?");
			pstm2.setInt(1, townId);
			rs2 = pstm2.executeQuery();
			if (rs2.next()) {
				totalContribution = rs2.getInt("total_contribution");
			}
			double townFixTax = 0;
			pstm3 = con.prepareStatement("SELECT town_fix_tax FROM towns WHERE id = ?");
			pstm3.setInt(1, townId);
			rs3 = pstm3.executeQuery();
			if (rs3.next()) {
				townFixTax = rs3.getInt("town_fix_tax");
			}
			double contributionUnit = 0;
			if (totalContribution != 0) {
				contributionUnit = Math.floor(townFixTax / totalContribution * 100) / 100;
			}
			pstm4 = con.prepareStatement("UPDATE characters SET contribution = 0, pay = contribution * ? WHERE hometown_id = ?");
			pstm4.setDouble(1, contributionUnit);
			pstm4.setInt(2, townId);
			pstm4.execute();
			pstm5 = con.prepareStatement("UPDATE towns SET leader_id = ?, tax_rate = 0, tax_rate_reserved = 0, sales_money = 0, sales_money_yesterday = sales_money, town_tax = 0, town_fix_tax = 0 WHERE id = ?");
			pstm5.setInt(1, leaderId);
			pstm5.setInt(2, townId);
			pstm5.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs1);
			SqlUtil.close(rs2);
			SqlUtil.close(rs3);
			SqlUtil.close(pstm1);
			SqlUtil.close(pstm2);
			SqlUtil.close(pstm3);
			SqlUtil.close(pstm4);
			SqlUtil.close(pstm5);
			SqlUtil.close(con);
		}
		return leaderId;
	}

	private static void clearHomeTownID() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE characters SET hometown_id = 0 WHERE hometown_id = -1");
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	/**
	 * 報酬を取得しクリアする
	 * 
	 * @return 報酬
	 */
	public static int getPay(int objid) {
		Connection con = null;
		PreparedStatement pstm1 = null;
		PreparedStatement pstm2 = null;
		ResultSet rs1 = null;
		int pay = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con.prepareStatement("SELECT pay FROM characters WHERE id = ? FOR UPDATE");
			pstm1.setInt(1, objid);
			rs1 = pstm1.executeQuery();
			if (rs1.next()) {
				pay = rs1.getInt("pay");
			}
			pstm2 = con.prepareStatement("UPDATE characters SET pay = 0 WHERE id = ?");
			pstm2.setInt(1, objid);
			pstm2.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs1);
			SqlUtil.close(pstm1);
			SqlUtil.close(pstm2);
			SqlUtil.close(con);
		}
		return pay;
	}
}
