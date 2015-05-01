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
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.l1j.server.model.L1Clan;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class ClanRecommendTable {
	private static Logger _log = Logger.getLogger(ClanRecommendTable.class.getName());

	private static ClanRecommendTable _instance;
	
	public static ClanRecommendTable getInstance() {
		if (_instance == null) {
			_instance = new ClanRecommendTable();
		}
		return _instance;
	}
	
	/**
	 * 血盟推薦 登陸
	 * @param clan_id 血盟 id
	 * @param clan_type 血盟類型 友好/打怪/戰鬥
	 * @param message 類型說明文字
	 */
	public void addRecommendRecord(int clan_id, int clan_type, String message){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO clan_recommends SET clan_id=?, clan_name=?, char_name=?, clan_type=?, message=?");
			L1Clan clan = ClanTable.getInstance().getTemplate(clan_id);
			pstm.setInt(1, clan_id);
			pstm.setString(2, clan.getClanName());
			pstm.setString(3, clan.getLeaderName());
			pstm.setInt(4, clan_type);
			pstm.setString(5, message);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
	
	/**
	 * 血盟推薦 增加一筆申請
	 * @param clan_id 申請的血盟ID
	 * @param char_name 申請玩家名稱
	 */
	public void addRecommendApply(int clan_id, String char_name){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO clan_applies SET clan_id=?, clan_name=?, char_name=?");
			L1Clan clan = ClanTable.getInstance().getTemplate(clan_id);
			pstm.setInt(1, clan_id);
			pstm.setString(2, clan.getClanName());
			pstm.setString(3, char_name);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
	
	/**
	 * 更新登錄資料
	 */
	public void updateRecommendRecord(int clan_id, int clan_type, String message){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE clan_recommends SET clan_name=?, char_name=?, clan_type=?, message=? WHERE clan_id=?");
			L1Clan clan = ClanTable.getInstance().getTemplate(clan_id);
			pstm.setString(1, clan.getClanName());
			pstm.setString(2, clan.getLeaderName());
			pstm.setInt(3, clan_type);
			pstm.setString(4, message);
			pstm.setInt(5, clan_id);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
	
	/**
	 * 刪除血盟推薦申請
	 * @param id 申請ID
	 */
	public void removeRecommendApply(int id){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM clan_applies WHERE id=?");
			pstm.setInt(1, id);
			pstm.execute();
		}
		catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		
	}
	
	/**
	 * 刪除血盟推薦 登錄
	 * @param clan_id 血盟 id
	 */
	public void removeRecommendRecord(int clan_id){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM clan_recommends WHERE clan_id=?");
			pstm.setInt(1, clan_id);
			pstm.execute();
		}
		catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		
	}
	
	/**
	 * 取得申請的玩家名稱
	 * @param index_id
	 * @return
	 */
	public String getApplyPlayerName(int index_id){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String charName = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM clan_applies WHERE id=?");
			pstm.setInt(1, index_id);
			rs = pstm.executeQuery();
			
			if(rs.first()){
				charName = rs.getString("char_name");
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return charName;
	}
	
	/**
	 * 該血盟是否登錄
	 * @param clan_id
	 * @return
	 */
	public boolean isRecorded(int clan_id){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM clan_recommends WHERE clan_id=?");
			pstm.setInt(1, clan_id);
			rs = pstm.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return false;
	}
	
	/**
	 * 該玩家是否提出申請
	 * @param char_name
	 * @return
	 */
	public boolean isApplied(String char_name){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM clan_applies WHERE char_name=?");
			pstm.setString(1, char_name);
			rs = pstm.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return false;
	}
	
	/**
	 * 該血盟是否有人申請加入
	 */
	public boolean isClanApplyByPlayer(int clan_id){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM clan_applies WHERE clan_id=?");
			pstm.setInt(1, clan_id);
			rs = pstm.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return false;
	}
	
	/**
	 * 是否對該血盟提出申請
	 * @param clan_id 血盟Id
	 * @return True:False
	 */
	public boolean isApplyForTheClan(int clan_id, String char_name){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM clan_applies WHERE clan_id=? AND char_name=?");
			pstm.setInt(1, clan_id);
			pstm.setString(2, char_name);
			rs = pstm.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return false;
	}

}
