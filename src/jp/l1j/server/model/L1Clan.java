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

package jp.l1j.server.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.CharacterTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1WarehouseInventory;
import jp.l1j.server.templates.L1InventoryItem;

public class L1Clan {
	public static final int CLAN_RANK_LEADER = 4;	// 君主
	public static final int CLAN_RANK_SUBLEADER = 3; // 副君主
	public static final int CLAN_RANK_GUARDIAN = 6; // ガーディアン
	public static final int CLAN_RANK_ELITE = 5;	// 一般
	public static final int CLAN_RANK_REGULAR = 2;  // 修練

	private static Logger _log = Logger.getLogger(L1Clan.class.getName());

	private int _clanId;

	private String _clanName;

	private int _leaderId;

	private String _leaderName;

	private int _castleId;

	private int _houseId;

	private int _warehouse = 0;
	
	private Timestamp _createdAt;

	private final L1WarehouseInventory _clanWarehouse;

	private final ArrayList<String> membersNameList = new ArrayList<String>();

	public L1Clan(int clanId) {
		_clanId = clanId;
		_clanWarehouse = new L1WarehouseInventory(clanId,
				L1InventoryItem.LOC_CLAN_WAREHOUSE);
	}

	public int getClanId() {
		return _clanId;
	}

	public String getClanName() {
		return _clanName;
	}

	public void setClanName(String clanName) {
		_clanName = clanName;
	}

	public int getLeaderId() {
		return _leaderId;
	}

	public void setLeaderId(int leaderId) {
		_leaderId = leaderId;
	}

	public String getLeaderName() {
		return _leaderName;
	}

	public void setLeaderName(String leaderName) {
		_leaderName = leaderName;
	}
	
	public int getCastleId() {
		return _castleId;
	}

	public void setCastleId(int castleId) {
		_castleId = castleId;
	}

	public int getHouseId() {
		return _houseId;
	}

	public void setHouseId(int houseId) {
		_houseId = houseId;
	}

	public Timestamp getCreatedAt() {
		return _createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		_createdAt = createdAt;
	}

	public void addMemberName(String memberName) {
		if (!membersNameList.contains(memberName)) {
			membersNameList.add(memberName);
		}
	}

	public void delMemberName(String memberName) {
		if (membersNameList.contains(memberName)) {
			membersNameList.remove(memberName);
		}
	}

	public L1PcInstance[] getOnlineClanMember() // オンライン中のクラン員のみ
	{
		ArrayList<L1PcInstance> onlineMembers = new ArrayList<L1PcInstance>();
		for (String name : membersNameList) {
			L1PcInstance pc = L1World.getInstance().getPlayer(name);
			if (pc != null && !onlineMembers.contains(pc)) {
				onlineMembers.add(pc);
			}
		}
		return onlineMembers.toArray(new L1PcInstance[onlineMembers.size()]);
	}

	public String getOnlineMembersFP() { // FP means "For Pledge"
		String result = "";
		for (String name : membersNameList) {
			L1PcInstance pc = L1World.getInstance().getPlayer(name);
			if (pc != null) {
				result = result + name + " ";
			}
		}
		return result;
	}

	public String getAllMembersFP() {
		String result = "";
		for (String name : membersNameList) {
			result = result + name + " ";
		}
		return result;
	}

	public String getOnlineMembersFPWithRank() {
		String result = "";
		for (String name : membersNameList) {
			L1PcInstance pc = L1World.getInstance().getPlayer(name);
			if (pc != null) {
				result = result + name + getRankString(pc) + " ";
			}
		}
		return result;
	}

	public String getAllMembersFPWithRank() {
		String result = "";
		try {
			for (String name : membersNameList) {
				L1PcInstance pc = CharacterTable.getInstance().restoreCharacter(name);
				if (pc != null) {
					result = result + name + getRankString(pc) + " ";
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return result;
	}

	public String getRankString(L1PcInstance pc) {
		String rank = "";
		if (pc != null) {
			if (pc.getClanRank() == CLAN_RANK_REGULAR) {
				rank = I18N_CLAN_REGULAR; // [修練]
			} else if (pc.getClanRank() == CLAN_RANK_ELITE) {
				rank = I18N_CLAN_ELITE; // [一般]
			} else if (pc.getClanRank() == CLAN_RANK_GUARDIAN) {
				rank = I18N_CLAN_GUARDIAN; // [ガーディアン]
			} else if (pc.getClanRank() == CLAN_RANK_SUBLEADER) {
				rank = I18N_CLAN_SUBLEADER; // [副君主]
			} else if (pc.getClanRank() == CLAN_RANK_LEADER) {
				rank = I18N_CLAN_LEADER; // [君主]
			}
		}
		return rank;
	}

	public String[] getAllMembers() {
		return membersNameList.toArray(new String[membersNameList.size()]);
	}

	public L1WarehouseInventory getWarehouse() {
		return _clanWarehouse;
	}

	public int getWarehouseUsingChar() {
		return _warehouse;
	}

	public void setWarehouseUsingChar(int objid) {
		_warehouse = objid;
	}
}
