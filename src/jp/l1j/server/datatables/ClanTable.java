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
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class ClanTable {
	private static Logger _log = Logger.getLogger(ClanTable.class.getName());

	private final HashMap<Integer, L1Clan> _clans = new HashMap<Integer, L1Clan>();

	private static ClanTable _instance;

	public static ClanTable getInstance() {
		if (_instance == null) {
			_instance = new ClanTable();
		}
		return _instance;
	}

	private ClanTable() {
		{
			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("SELECT * FROM clans ORDER BY id");
				rs = pstm.executeQuery();			
				while (rs.next()) {
					int clan_id = rs.getInt("id");
					L1Clan clan = new L1Clan(clan_id);
					clan.setClanName(rs.getString("name"));
					clan.setLeaderId(rs.getInt("leader_id"));
					clan.setLeaderName(CharacterTable.getInstance().getCharName(clan.getLeaderId()));
					clan.setCastleId(rs.getInt("castle_id"));
					clan.setHouseId(rs.getInt("house_id"));
					clan.setCreatedAt(rs.getTimestamp("created_at"));
					L1World.getInstance().storeClan(clan);
					_clans.put(clan_id, clan);
				}
			} catch (SQLException e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} finally {
				SqlUtil.close(rs);
				SqlUtil.close(pstm);
				SqlUtil.close(con);
			}
		}

		Collection<L1Clan> AllClan = L1World.getInstance().getAllClans();
		for (L1Clan clan : AllClan) {
			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con.prepareStatement("SELECT name FROM characters WHERE clan_id = ?");
				pstm.setInt(1, clan.getClanId());
				rs = pstm.executeQuery();
				while (rs.next()) {
					clan.addMemberName(rs.getString("name"));
				}
			} catch (SQLException e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} finally {
				SqlUtil.close(rs);
				SqlUtil.close(pstm);
				SqlUtil.close(con);
			}
		}
		// クラン倉庫のロード
		for (L1Clan clan : AllClan) {
			clan.getWarehouse().loadItems();
		}
	}

	public L1Clan createClan(L1PcInstance player, String clanName) {
		for (L1Clan oldClans : L1World.getInstance().getAllClans()) {
			if (oldClans.getClanName().equalsIgnoreCase(clanName)) {
				return null;
			}
		}
		L1Clan clan = new L1Clan(IdFactory.getInstance().nextId());
		clan.setClanName(clanName);
		clan.setLeaderId(player.getId());
		clan.setLeaderName(player.getName());
		clan.setCastleId(0);
		clan.setHouseId(0);
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO clans SET id=?, name=?, leader_id=?, castle_id=?, house_id=?, created_at=?");
			pstm.setInt(1, clan.getClanId());
			pstm.setString(2, clan.getClanName());
			pstm.setInt(3, clan.getLeaderId());
			pstm.setInt(4, clan.getCastleId());
			pstm.setInt(5, clan.getHouseId());
			pstm.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		L1World.getInstance().storeClan(clan);
		_clans.put(clan.getClanId(), clan);
		player.setClanid(clan.getClanId());
		player.setClanname(clan.getClanName());
		player.setClanRank(L1Clan.CLAN_RANK_LEADER);
		clan.addMemberName(player.getName());
		try {
			// DBにキャラクター情報を書き込む
			player.save();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return clan;
	}

	public void updateClan(L1Clan clan) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE clans SET id=?, leader_id=?, castle_id=?, house_id=?, created_at=? WHERE name=?");
			pstm.setInt(1, clan.getClanId());
			pstm.setInt(2, clan.getLeaderId());
			pstm.setInt(3, clan.getCastleId());
			pstm.setInt(4, clan.getHouseId());
			pstm.setString(5, clan.getClanName());
			pstm.setTimestamp(6, clan.getCreatedAt());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void deleteClan(String clan_name) {
		L1Clan clan = L1World.getInstance().getClan(clan_name);
		if (clan == null) {
			return;
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM clans WHERE name=?");
			pstm.setString(1, clan_name);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		clan.getWarehouse().clearItems();
		clan.getWarehouse().deleteAllItems();
		L1World.getInstance().removeClan(clan);
		_clans.remove(clan.getClanId());
	}

	public L1Clan getTemplate(int clan_id) {
		return _clans.get(clan_id);
	}
}
