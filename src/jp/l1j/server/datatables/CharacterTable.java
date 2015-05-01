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
import jp.l1j.server.model.map.L1Map;
import jp.l1j.server.model.map.L1WorldMap;
import jp.l1j.server.storage.CharacterStorage;
import jp.l1j.server.storage.mysql.MySqlCharacterStorage;
import jp.l1j.server.templates.L1CharName;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class CharacterTable {
	private CharacterStorage _charStorage;

	private static CharacterTable _instance;

	private static Logger _log = Logger.getLogger(CharacterTable.class.getName());

	private final Map<String, L1CharName> _charNameList = new ConcurrentHashMap<String, L1CharName>();

	private CharacterTable() {
		_charStorage = new MySqlCharacterStorage();
	}

	public static CharacterTable getInstance() {
		if (_instance == null) {
			_instance = new CharacterTable();
		}
		return _instance;
	}

	public void storeNewCharacter(L1PcInstance pc) throws Exception {
		synchronized (pc) {
			_charStorage.createCharacter(pc);
			String name = pc.getName();
			if (!_charNameList.containsKey(name)) {
				L1CharName cn = new L1CharName();
				cn.setName(name);
				cn.setId(pc.getId());
				_charNameList.put(name, cn);
			}
			_log.finest("storeNewCharacter");
		}
	}

	public void storeCharacter(L1PcInstance pc) throws Exception {
		synchronized (pc) {
			_charStorage.storeCharacter(pc);
			_log.finest("storeCharacter: " + pc.getName());
		}
	}

	public void deleteCharacter(int accountId, String charName) throws Exception {
		// 多分、同期は必要ない
		_charStorage.deleteCharacter(accountId, charName);
		if (_charNameList.containsKey(charName)) {
			_charNameList.remove(charName);
		}
		_log.finest("deleteCharacter");
	}

	public L1PcInstance restoreCharacter(String charName) throws Exception {
		L1PcInstance pc = _charStorage.loadCharacter(charName);
		return pc;
	}

	public L1PcInstance loadCharacter(String charName) throws Exception {
		L1PcInstance pc = null;
		try {
			pc = restoreCharacter(charName);
			// マップの範囲外ならSKTに移動させる
			L1Map map = L1WorldMap.getInstance().getMap(pc.getMapId());
			if (!map.isInMap(pc.getX(), pc.getY())) {
				pc.setX(33087);
				pc.setY(33396);
				pc.setMap((short) 4);
			}
			/*
			 * if(l1pcinstance.getClanId() != 0) { L1Clan clan = new L1Clan();
			 * ClanTable clantable = new ClanTable(); clan =
			 * clantable.getClan(l1pcinstance.getClanName());
			 * l1pcinstance.setClanname(clan.GetClanName()); }
			 */
			_log.finest("loadCharacter: " + pc.getName());
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return pc;
	}

	public static void clearOnlineStatus() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE characters SET online_status=0");
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public static void updateOnlineStatus(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE characters SET online_status=? WHERE id=?");
			pstm.setInt(1, pc.getOnlineStatus());
			pstm.setInt(2, pc.getId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public static void updatePartnerId(int targetId) {
		updatePartnerId(targetId, 0);
	}

	public static void updatePartnerId(int targetId, int partnerId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE characters SET PartnerID=? WHERE id=?");
			pstm.setInt(1, partnerId);
			pstm.setInt(2, targetId);
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

        public static void saveCharStatus(L1PcInstance pc) {
            
		Connection con = null;

		PreparedStatement pstm = null;

		try {

			con = L1DatabaseFactory.getInstance().getConnection();

			pstm = con.prepareStatement("UPDATE characters SET original_str= ?"

					+ ", original_con= ?, original_dex= ?, original_cha= ?"

					+ ", original_int= ?, original_wis= ?" + " WHERE id=?");

			pstm.setInt(1, pc.getBaseStr());

			pstm.setInt(2, pc.getBaseCon());

			pstm.setInt(3, pc.getBaseDex());

			pstm.setInt(4, pc.getBaseCha());

			pstm.setInt(5, pc.getBaseInt());

			pstm.setInt(6, pc.getBaseWis());

			pstm.setInt(7, pc.getId());

			pstm.execute();

		} catch (Exception e) {

			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);

		} finally {

			SqlUtil.close(pstm);

			SqlUtil.close(con);

		}

	}
        
	public void restoreInventory(L1PcInstance pc) {
		pc.getInventory().loadItems();
		pc.getWarehouseInventory().loadItems();
		pc.getElfWarehouseInventory().loadItems();
	}

	public static boolean doesCharNameExist(String name) {
		boolean result = true;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT account_id FROM characters WHERE name=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			result = rs.next();
		} catch (SQLException e) {
			_log.warning("could not check existing charname:" + e.getMessage());
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return result;
	}

	public void loadAllCharName() {
		L1CharName cn = null;
		String name = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM characters");
			rs = pstm.executeQuery();
			while (rs.next()) {
				cn = new L1CharName();
				name = rs.getString("name");
				cn.setName(name);
				cn.setId(rs.getInt("id"));
				_charNameList.put(name, cn);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public L1CharName[] getCharNameList() {
		return _charNameList.values().toArray(new L1CharName[_charNameList.size()]);
	}
	
	public String getCharName(int charId) {
		for (L1CharName cn : getCharNameList()) {
			if (charId == cn.getId()) {
				return cn.getName();
			}
		}
		return null;
	}
}
