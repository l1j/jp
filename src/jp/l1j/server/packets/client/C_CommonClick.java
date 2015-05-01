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

package jp.l1j.server.packets.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.ClientThread;
import jp.l1j.server.datatables.CharacterTable;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_CharAmount;
import jp.l1j.server.packets.server.S_CharPacks;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class C_CommonClick {
	private static final String C_COMMON_CLICK = "[C] C_CommonClick";

	private static Logger _log = Logger
			.getLogger(C_CommonClick.class.getName());

	public C_CommonClick(ClientThread client) {
		deleteCharacter(client); // 削除期限に達したキャラクターを削除する
		int amountOfChars = client.getAccount().countCharacters();
		client.sendPacket(new S_CharAmount(amountOfChars, client));
		if (amountOfChars > 0) {
			sendCharPacks(client);
		}
	}

	private void deleteCharacter(ClientThread client) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn
					.prepareStatement("SELECT * FROM characters WHERE account_id=? ORDER BY id");
			pstm.setInt(1, client.getAccount().getId());
			rs = pstm.executeQuery();

			while (rs.next()) {
				String name = rs.getString("name");
				String clanname = rs.getString("clan_name");

				Timestamp deleteTime = rs.getTimestamp("delete_time");
				if (deleteTime != null) {
					Calendar cal = Calendar.getInstance();
					long checkDeleteTime = ((cal.getTimeInMillis() - deleteTime
							.getTime()) / 1000) / 3600;
					if (checkDeleteTime >= 0) {
						L1Clan clan = L1World.getInstance().getClan(clanname);
						if (clan != null) {
							clan.delMemberName(name);
						}
						CharacterTable.getInstance().deleteCharacter(
								client.getAccount().getId(), name);
					}
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(conn);
		}
	}

	private void sendCharPacks(ClientThread client) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn
					.prepareStatement("SELECT * FROM characters WHERE account_id=? ORDER BY id");
			pstm.setInt(1, client.getAccount().getId());
			rs = pstm.executeQuery();

			while (rs.next()) {
				String name = rs.getString("name");
				String clanname = rs.getString("clan_name");
				int type = rs.getInt("type");
				byte sex = rs.getByte("sex");
				int lawful = rs.getInt("lawful");

				int currenthp = rs.getInt("cur_hp");
				if (currenthp < 1) {
					currenthp = 1;
				} else if (currenthp > 32767) {
					currenthp = 32767;
				}

				int currentmp = rs.getInt("cur_mp");
				if (currentmp < 1) {
					currentmp = 1;
				} else if (currentmp > 32767) {
					currentmp = 32767;
				}

				int lvl;
				if (Config.CHARACTER_CONFIG_IN_SERVER_SIDE) {
					lvl = rs.getInt("level");
					if (lvl < 1) {
						lvl = 1;
					} else if (lvl > 127) {
						lvl = 127;
					}
				} else {
					lvl = 1;
				}

				int ac = rs.getByte("ac");
				int str = rs.getByte("str");
				int dex = rs.getByte("dex");
				int con = rs.getByte("con");
				int wis = rs.getByte("wis");
				int cha = rs.getByte("cha");
				int intel = rs.getByte("int");
				int accessLevel = rs.getShort("access_level");
				Timestamp _birthday = (Timestamp) rs.getTimestamp("birthday");
				SimpleDateFormat SimpleDate = new SimpleDateFormat("yyyyMMdd");
				int birthday = Integer.parseInt(SimpleDate.format(_birthday.getTime()));
				
				S_CharPacks cpk = new S_CharPacks(name, clanname, type, sex,
						lawful, currenthp, currentmp, ac, lvl, str, dex, con,
						wis, cha, intel, accessLevel, birthday);

				client.sendPacket(cpk);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(conn);
		}
	}

	public String getType() {
		return C_COMMON_CLICK;
	}
}