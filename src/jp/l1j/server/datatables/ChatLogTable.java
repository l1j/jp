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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class ChatLogTable {
	private static Logger _log = Logger.getLogger(ChatLogTable.class.getName());

	/*
	 * コード的にはHashMapを利用すべきだが、パフォーマンス上の問題があるかもしれない為、配列で妥協。
	 * HashMapへの変更を検討する場合は、パフォーマンス上問題が無いか十分注意すること。
	 */
	private final boolean[] loggingConfig = new boolean[15];

	private ChatLogTable() {
		loadConfig();
	}

	private void loadConfig() {
		loggingConfig[0] = Config.LOGGING_CHAT_NORMAL;
		loggingConfig[1] = Config.LOGGING_CHAT_WHISPER;
		loggingConfig[2] = Config.LOGGING_CHAT_SHOUT;
		loggingConfig[3] = Config.LOGGING_CHAT_WORLD;
		loggingConfig[4] = Config.LOGGING_CHAT_CLAN;
		loggingConfig[11] = Config.LOGGING_CHAT_PARTY;
		loggingConfig[13] = Config.LOGGING_CHAT_COMBINED;
		loggingConfig[14] = Config.LOGGING_CHAT_CHAT_PARTY;
	}

	private static ChatLogTable _instance;

	public static ChatLogTable getInstance() {
		if (_instance == null) {
			_instance = new ChatLogTable();
		}
		return _instance;
	}

	private boolean isLoggingTarget(int type) {
		return loggingConfig[type];
	}

	public void storeChat(L1PcInstance pc, L1PcInstance target, String text, int type) {
		if (!isLoggingTarget(type)) {
			return;
		}

		// type
		// 0:通常チャット
		// 1:Whisper
		// 2:叫び
		// 3:全体チャット
		// 4:血盟チャット
		// 11:パーティチャット
		// 13:連合チャット
		// 14:チャットパーティ
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			if (target != null) {
				pstm = con.prepareStatement(String.format("INSERT INTO chat_logs SET %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
					"account_id=?", "char_id=?", "clan_id=?", "map_id=?", "loc_x=?", "loc_y=?", "type=?",
					"target_account_id=?", "target_char_id=?", "target_clan_id=?", "target_map_id=?", "target_loc_x=?", "target_loc_y=?",
					"content=?", "datetime=SYSDATE()"));
				pstm.setInt(1, pc.getAccountId());
				pstm.setInt(2, pc.getId());
				pstm.setInt(3, pc.getClanId());
				pstm.setInt(4, pc.getMapId());
				pstm.setInt(5, pc.getX());
				pstm.setInt(6, pc.getY());
				pstm.setInt(7, type);
				pstm.setInt(8, target.getAccountId());
				pstm.setInt(9, target.getId());
				pstm.setInt(10, target.getClanId());
				pstm.setInt(11, target.getMapId());
				pstm.setInt(12, target.getX());
				pstm.setInt(13, target.getY());
				pstm.setString(14, text);
			} else {
				pstm = con.prepareStatement(String.format("INSERT INTO chat_logs SET %s, %s, %s, %s, %s, %s, %s, %s, %s",
					"account_id=?", "char_id=?", "clan_id=?", "map_id=?", "loc_x=?", "loc_y=?", "type=?", "content=?", "datetime=SYSDATE()"));
				pstm.setInt(1, pc.getAccountId());
				pstm.setInt(2, pc.getId());
				pstm.setInt(3, pc.getClanId());
				pstm.setInt(4, pc.getMapId());
				pstm.setInt(5, pc.getX());
				pstm.setInt(6, pc.getY());
				pstm.setInt(7, type);
				pstm.setString(8, text);
			}
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
}