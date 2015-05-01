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

package jp.l1j.server.packets.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static jp.l1j.server.codes.Opcodes.S_OPCODE_PLEDGE_RECOMMENDATION;
import jp.l1j.server.datatables.CharacterTable;
import jp.l1j.server.datatables.ClanRecommendTable;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

/**
 * 推薦血盟 
 */
public class S_PledgeRecommendation extends ServerBasePacket{

	private static final String S_PledgeRecommendation = "[S] S_PledgeRecommendation";
	
	private byte[] _byte = null;
	
	
	/**
	 * 打開推薦血盟 邀請目錄
	 * @param type
	 * @param clan_id
	 */
	public S_PledgeRecommendation(int type, int clan_id){
		buildPacket(type, clan_id, null, 0, null);
	}
	
	/**
	 * 打開推薦血盟 血盟目錄 / 申請目錄
	 * @param type
	 * @param char_name
	 */
	public S_PledgeRecommendation(int type, String char_name){
		buildPacket(type, 0, null, 0, char_name);
	}
	
	/**
	 * 推薦血盟  申請/處理申請
	 * @param type
	 * @param id 申請:血盟 id 處理申請: 流水號
	 * @param acceptType 0:申請  1:接受  2:拒絕  3:刪除
	 */
	public S_PledgeRecommendation(int type, int record_id, int acceptType){
		buildPacket(type, record_id, null, acceptType, null);
	}
	
	/**
	 * 登錄結果
	 */
	public S_PledgeRecommendation(boolean postStatus, int clan_id){
		buildPacket(postStatus, clan_id);
	}

	/**
	 * 血盟推薦登錄狀態
	 * @param postStatus 登錄成功:True, 取消登陸:False
	 */
	private void buildPacket(boolean postStatus, int clan_id){
		writeC(S_OPCODE_PLEDGE_RECOMMENDATION);
		writeC(postStatus ? 0 : 1);
		if(!ClanRecommendTable.getInstance().isRecorded(clan_id)){
			writeC(0x82);
		} else{
			writeC(0);
		}
		writeD(0);
		writeC(0);
	}
	
	private void buildPacket(int type, int record_id, String typeMessage, int acceptType, String char_name){
		writeC(S_OPCODE_PLEDGE_RECOMMENDATION);
		writeC(type);
		
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		switch(type){
		case 2: // 查詢
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("SELECT * FROM clan_recommends ORDER BY RAND() LIMIT 10");
				rs = pstm.executeQuery();
				
				int rows = 0;
				
				while(rs.next()){
					if(ClanRecommendTable.getInstance().isApplyForTheClan(rs.getInt("clan_id"), char_name)){ 
						continue;
					} else{
						rows++;
					}
				}
				
				rs.beforeFirst();
				
				writeC(0x00);
				writeC(rows); 
				
				while(rs.next()){
					if(ClanRecommendTable.getInstance().isApplyForTheClan(rs.getInt("clan_id"), char_name)){ 
						continue;
					}
					writeD(rs.getInt("clan_id"));       // 血盟id
					writeS(rs.getString("clan_name"));  // 血盟名稱
					writeS(rs.getString("char_name")); // 王族名稱
					writeD(0);                          // 一周最大上線人數
					writeC(rs.getInt("clan_type"));     // 血盟登錄類型
					L1Clan clan = L1World.getInstance().getClan(rs.getString("clan_name"));
					writeC(clan.getHouseId() > 0 ? 1 : 0); // 是否有盟屋
					writeC(0);  // 戰爭狀態
					writeC(0);  // 尚未使用
					writeS(typeMessage); // 血盟類型說明
					//writeD(clan.getEmblemId()); // 盟徽編號
					writeD(clan.getClanId());
				}
			}  catch (SQLException e) {
				System.out.println(e.getLocalizedMessage());
			} finally {
				SqlUtil.close(rs);
				SqlUtil.close(pstm);
				SqlUtil.close(con);
			}
			break;
		case 3: // 申請目錄
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("SELECT * FROM clan_applies WHERE char_name=?");
				pstm.setString(1, char_name);
				rs = pstm.executeQuery();
				
				rs.last();
				int rows = rs.getRow();
				rs.beforeFirst();
				writeC(0x00);
				writeC(rows); 
				
				while(rs.next()){
					PreparedStatement pstm2 = con.prepareStatement("SELECT * FROM clan_recommends WHERE clan_id=?");
					pstm2.setInt(1, rs.getInt("clan_id"));
					ResultSet rs2 = pstm2.executeQuery();
					
					if(rs2.first()){
						writeD(rs.getInt("id")); // id
						writeC(0);
						writeD(rs2.getInt("clan_id"));  // 血盟id
						writeS(rs2.getString("clan_name")); // 血盟名稱
						L1Clan clan = L1World.getInstance().getClan(rs.getString("clan_name"));
						writeS(clan.getLeaderName());   // 王族名稱
						writeD(0);                      // 一周最大上線人數
						writeC(rs2.getInt("clan_type")); // 血盟登錄類型
						writeC(clan.getHouseId() > 0 ? 1 : 0); // 是否有盟屋
						writeC(0);                             // 戰爭狀態
						writeC(0);                             // 尚未使用
						writeS(rs2.getString("message")); // 血盟類型說明
						//writeD(clan.getEmblemId()); // 盟徽編號
						writeD(clan.getClanId());
					}
				}
			}  catch (SQLException e) {
				System.out.println(e.getLocalizedMessage());
			} finally {
				SqlUtil.close(rs);
				SqlUtil.close(pstm);
				SqlUtil.close(con);
			}
			break;
		case 4: // 邀請名單
			if(!ClanRecommendTable.getInstance().isRecorded(record_id)){
				writeC(0x82);
			} else {
				try {
					con = L1DatabaseFactory.getInstance().getConnection();
					pstm = con.prepareStatement("SELECT * FROM clan_recommends WHERE clan_id=?");
					pstm.setInt(1, record_id);
					rs = pstm.executeQuery();
					
					writeC(0x00);
					
					if(rs.first()){
						writeC(rs.getInt("clan_type")); // 血盟類型
						writeS(rs.getString("message"));
					}
					
					PreparedStatement pstm2 = con.prepareStatement("SELECT * FROM clan_applies WHERE clan_id=?");
					pstm2.setInt(1, record_id);
					ResultSet rs2 = pstm2.executeQuery();
					rs2.last();
					int rows = rs2.getRow();
					rs2.beforeFirst();
					
					writeC(rows); 
					
					while(rs2.next()){
						writeD(rs2.getInt("id"));
						L1PcInstance pc = L1World.getInstance().getPlayer(rs2.getString("char_name"));
						if(pc == null){
							pc = CharacterTable.getInstance().restoreCharacter(rs2.getString("char_name"));
						}
						writeC(0);
						writeC(pc.getOnlineStatus());       // 上線狀態
						writeS(pc.getName());               // 角色明稱
						writeC(pc.getType());               // 職業
						writeH(pc.getLawful());             // 角色 正義值
						writeH(pc.getLevel());              // 角色 等級
					}
				} catch (SQLException e) {
					System.out.println(e.getLocalizedMessage());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					SqlUtil.close(rs);
					SqlUtil.close(pstm);
					SqlUtil.close(con);
				}
			}
			break;
		case 5: // 申請加入
		case 6: // 刪除申請
			writeC(0x00);
			writeD(record_id);
			writeC(acceptType);
			break;
		}
		writeD(0);
		writeC(0);
	}
	
	
	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_PledgeRecommendation;
	}
}
