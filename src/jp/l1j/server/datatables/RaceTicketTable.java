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
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.templates.L1RaceTicket;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class RaceTicketTable {
	private static Logger _log = Logger.getLogger(PetTable.class.getName());

	private static RaceTicketTable _instance;

	private final HashMap<Integer, L1RaceTicket> _tickets = new HashMap<Integer, L1RaceTicket>();
	
	private int _maxRoundNumber;

	public static RaceTicketTable getInstance() {
		if (_instance == null) {
			_instance = new RaceTicketTable();
		}
		return _instance;
	}

	private RaceTicketTable() {
		load();
	}

	private void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM race_tickets");
			int temp=0;
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1RaceTicket ticket = new L1RaceTicket();
				int itemobjid = rs.getInt(1);
				ticket.setItemObjId(itemobjid);
				ticket.setRound(rs.getInt(2));
				ticket.setAllotmentPercentage(rs.getInt(3));
				ticket.setVictory(rs.getInt(4));
				ticket.setRunnerNum(rs.getInt(5));
				
				if(ticket.getRound()>temp){
					temp=ticket.getRound();
				}
				_tickets.put(new Integer(itemobjid), ticket);
			}
			_maxRoundNumber=temp;
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void storeNewTiket(L1RaceTicket ticket) {
		//PCのインベントリーが増える場合に実行
		// XXX 呼ばれる前と処理の重複
		if(ticket.getItemObjId()!=0){
			_tickets.put(new Integer(ticket.getItemObjId()), ticket);
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO race_tickets SET item_obj_id=?,round=?,allotment_percentage=?,victory=?,runner_num=?");
			pstm.setInt(1, ticket.getItemObjId());
			pstm.setInt(2, ticket.getRound());
			pstm.setDouble(3, ticket.getAllotmentPercentage());
			pstm.setInt(4, ticket.getVictory());
			pstm.setInt(5, ticket.getRunnerNum());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
	public void deleteTicket(int itemobjid) {
		//PCのインベントリーが減少する再に使用
		if(_tickets.containsKey(itemobjid)){
			_tickets.remove(itemobjid);
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("delete from race_tickets WHERE item_obj_id=?");
			pstm.setInt(1, itemobjid);
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
	public void oldTicketDelete(int round){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("delete from race_tickets WHERE item_obj_id=0 and round!=?");
			pstm.setInt(1,round);
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
	
	public void updateTicket(int round,int num,double allotment_percentage ) {
		for(L1RaceTicket ticket:getRaceTicketTableList()){
			if(ticket.getRound()==round && ticket.getRunnerNum()==num){
				ticket.setVictory(1);
				ticket.setAllotmentPercentage(allotment_percentage);
			}
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE race_tickets SET victory=? ,allotment_percentage=? WHERE round=? and runner_num=?");	
			pstm.setInt(1,1);
			pstm.setDouble(2,allotment_percentage);
			pstm.setInt(3,round);
			pstm.setInt(4,num);
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public L1RaceTicket getTemplate(int itemobjid) {
		if(_tickets.containsKey(itemobjid)){
			return _tickets.get(itemobjid);
		}
		return null;
	}

	public L1RaceTicket[] getRaceTicketTableList() {
		return _tickets.values().toArray(new L1RaceTicket[_tickets.size()]);
	}

	public int getRoundNumOfMax() {
		return _maxRoundNumber;
	}
}