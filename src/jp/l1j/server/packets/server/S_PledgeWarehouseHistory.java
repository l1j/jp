/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package jp.l1j.server.packets.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import jp.l1j.configure.Config;
import static jp.l1j.server.codes.Opcodes.S_OPCODE_PACKETBOX;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.templates.L1Item;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

/**
 * 血盟倉庫の使用履歴を表示
 */
public class S_PledgeWarehouseHistory extends ServerBasePacket {
	
	private static final String S_PledgeWarehouseHistory = "[S] S_PledgeWarehouseHistory";
	
	private byte[] _byte = null;
	
	
	public S_PledgeWarehouseHistory(int clanId){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try{
			con = L1DatabaseFactory.getInstance().getConnection();
			
			// 指定した日数を経過した使用履歴を削除
			int time = Config.MAX_CLAN_WAREHOUSE_HISTORY_DAYS * 24 * 60 * 60 * 1000;
			pstm = con.prepareStatement("DELETE FROM clan_warehouse_histories WHERE clan_id=? AND record_time < ?");
			pstm.setInt(1, clanId);
			pstm.setTimestamp(2, new Timestamp(System.currentTimeMillis() - time));
			pstm.execute();
			pstm.close();
			
			// 使用履歴を取得(記録の新しいものから順に表示する)
			pstm = con.prepareStatement("SELECT * FROM clan_warehouse_histories WHERE clan_id=? ORDER BY id DESC");
			pstm.setInt(1, clanId);
			rs = pstm.executeQuery();
			rs.last();
			int rowcount = rs.getRow();
      rs.beforeFirst();
			/** パケット部分 */
			writeC(S_OPCODE_PACKETBOX);
			writeC(S_PacketBox.HTML_CLAN_WARHOUSE_RECORD);
			writeD(rowcount);  // 記録総数
			while (rs.next()) {
				writeS(rs.getString("char_name")); // キャラクター名
				writeC(rs.getInt("type"));         // 受領区分(1:受取, 0:預入)
				writeS(rs.getString("item_name")); // アイテム名
				writeD(rs.getInt("item_count"));   // アイテム数量
				writeD((int)((System.currentTimeMillis() - rs.getTimestamp("record_time").getTime()) / 60000)); // 経過時間
			}
		} catch (SQLException e) {
			System.out.println(e.getLocalizedMessage());
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
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
		return S_PledgeWarehouseHistory;
	}
}
