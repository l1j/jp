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
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_NPC_LIST;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.templates.L1NpcChat;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class NpcChatTable {
	private static Logger _log = Logger.getLogger(NpcChatTable.class.getName());

	private static NpcChatTable _instance;

	private HashMap<Integer, L1NpcChat> _npcChatAppearance = new HashMap<Integer, L1NpcChat>();
	
	private HashMap<Integer, L1NpcChat> _npcChatDead = new HashMap<Integer, L1NpcChat>();
	
	private HashMap<Integer, L1NpcChat> _npcChatHide = new HashMap<Integer, L1NpcChat>();
	
	private HashMap<Integer, L1NpcChat> _npcChatGameTime = new HashMap<Integer, L1NpcChat>();

	public static NpcChatTable getInstance() {
		if (_instance == null) {
			_instance = new NpcChatTable();
		}
		return _instance;
	}

	private NpcChatTable() {
		loadNpcChats(_npcChatAppearance, _npcChatDead, _npcChatHide, _npcChatGameTime);
	}

	private void loadNpcChats(HashMap<Integer, L1NpcChat> npcChatAppearance,
			HashMap<Integer, L1NpcChat> npcChatDead,
			HashMap<Integer, L1NpcChat> npcChatHide,
			HashMap<Integer, L1NpcChat> npcChatGameTime) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM npc_chats");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int npcId = rs.getInt("npc_id");
				boolean isErr = false;
				if (NpcTable.getInstance().getTemplate(npcId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_NPC_LIST, npcId));
					// %s はNPCリストに存在しません。
					isErr = true;
				}
				if (isErr) {
					continue;
				}
				L1NpcChat npcChat = new L1NpcChat();
				npcChat.setNpcId(npcId);
				npcChat.setChatTiming(rs.getInt("chat_timing"));
				npcChat.setStartDelayTime(rs.getInt("start_delay_time"));
				npcChat.setChatId1(rs.getString("chat_id1"));
				npcChat.setChatId2(rs.getString("chat_id2"));
				npcChat.setChatId3(rs.getString("chat_id3"));
				npcChat.setChatId4(rs.getString("chat_id4"));
				npcChat.setChatId5(rs.getString("chat_id5"));
				npcChat.setChatInterval(rs.getInt("chat_interval"));
				npcChat.setShout(rs.getBoolean("is_shout"));
				npcChat.setWorldChat(rs.getBoolean("is_world_chat"));
				npcChat.setRepeat(rs.getBoolean("is_repeat"));
				npcChat.setRepeatInterval(rs.getInt("repeat_interval"));
				npcChat.setGameTime(rs.getInt("game_time"));
				if (npcChat.getChatTiming() == L1NpcInstance.CHAT_TIMING_APPEARANCE) {
					npcChatAppearance.put(new Integer(npcChat.getNpcId()), npcChat);
				} else if (npcChat.getChatTiming() == L1NpcInstance.CHAT_TIMING_DEAD) {
					npcChatDead.put(new Integer(npcChat.getNpcId()), npcChat);
				} else if (npcChat.getChatTiming() == L1NpcInstance.CHAT_TIMING_HIDE) {
					npcChatHide.put(new Integer(npcChat.getNpcId()), npcChat);
				} else if (npcChat.getChatTiming() == L1NpcInstance.CHAT_TIMING_GAME_TIME) {
					npcChatGameTime.put(new Integer(npcChat.getNpcId()), npcChat);
				}
			}
			System.out.println("loading npc chats...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	public void reload() {
		HashMap<Integer, L1NpcChat> npcChatAppearance = new HashMap<Integer, L1NpcChat>();
		HashMap<Integer, L1NpcChat> npcChatDead = new HashMap<Integer, L1NpcChat>();
		HashMap<Integer, L1NpcChat> npcChatHide = new HashMap<Integer, L1NpcChat>();
		HashMap<Integer, L1NpcChat> npcChatGameTime = new HashMap<Integer, L1NpcChat>();
		loadNpcChats(npcChatAppearance, npcChatDead, npcChatHide, npcChatGameTime);
		_npcChatAppearance = npcChatAppearance;
		_npcChatDead = npcChatDead;
		_npcChatHide = npcChatHide;
		_npcChatGameTime = npcChatGameTime;
	}
	
	public L1NpcChat getTemplateAppearance(int i) {
		return _npcChatAppearance.get(new Integer(i));
	}

	public L1NpcChat getTemplateDead(int i) {
		return _npcChatDead.get(new Integer(i));
	}

	public L1NpcChat getTemplateHide(int i) {
		return _npcChatHide.get(new Integer(i));
	}

	public L1NpcChat getTemplateGameTime(int i) {
		return _npcChatGameTime.get(new Integer(i));
	}

	public L1NpcChat[] getAllGameTime() {
		return _npcChatGameTime.values().toArray(new L1NpcChat[_npcChatGameTime.size()]);
	}
}
