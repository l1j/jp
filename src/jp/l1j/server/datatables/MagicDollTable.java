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
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_ITEM_LIST;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_NPC_LIST;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_SKILL_LIST;
import jp.l1j.server.templates.L1MagicDoll;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class MagicDollTable {
	private static Logger _log = Logger.getLogger(MagicDollTable.class.getName());

	private static MagicDollTable _instance;

	private static HashMap<Integer, L1MagicDoll> _dolls = new HashMap<Integer, L1MagicDoll>();

	public static MagicDollTable getInstance() {
		if (_instance == null) {
			_instance = new MagicDollTable();
		}
		return _instance;
	}

	private MagicDollTable() {
		loadDolls(_dolls);
	}

	private void loadDolls(HashMap<Integer, L1MagicDoll> dolls) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM magic_dolls");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int itemId = rs.getInt("item_id");
				int npcId = rs.getInt("npc_id");
				int makeItemId = rs.getInt("make_item_id");
				int skillId = rs.getByte("skill_id");
				boolean isErr = false;
				if (ItemTable.getInstance().getTemplate(itemId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, itemId));
					// %s はアイテムリストに存在しません。
					isErr = true;
				}
				if (makeItemId != 0 && ItemTable.getInstance().getTemplate(makeItemId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, makeItemId));
					// %s はアイテムリストに存在しません。
					isErr = true;
				}
				if (NpcTable.getInstance().getTemplate(npcId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_NPC_LIST, npcId));
					// %s はNPCリストに存在しません。
					isErr = true;
				}
				if (skillId != 0 && SkillTable.getInstance().findBySkillId(skillId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_SKILL_LIST, skillId));
					// %s はスキルリストに存在しません。
					isErr = true;
				}
				if (isErr) {
					continue;
				}
				L1MagicDoll doll = new L1MagicDoll();
				doll.setItemId(itemId);
				doll.setDollId(npcId);
				doll.setAc(rs.getInt("ac"));
				doll.setStr(rs.getInt("str"));
				doll.setCon(rs.getInt("con"));
				doll.setDex(rs.getInt("dex"));
				doll.setInt(rs.getInt("int"));
				doll.setWis(rs.getInt("wis"));
				doll.setCha(rs.getInt("cha"));
				doll.setHp(rs.getInt("hp"));
				doll.setHpr(rs.getInt("hpr"));
				doll.setHprTime(rs.getInt("hpr_time"));
				doll.setMp(rs.getInt("mp"));
				doll.setMpr(rs.getInt("mpr"));
				doll.setMprTime(rs.getInt("mpr_time"));
				doll.setMr(rs.getInt("mr"));
				doll.setHit(rs.getInt("hit"));
				doll.setDmg(rs.getInt("dmg"));
				doll.setDmgChance(rs.getInt("dmg_chance"));
				doll.setBowHit(rs.getInt("bow_hit"));
				doll.setBowDmg(rs.getInt("bow_dmg"));
				doll.setDmgReduction(rs.getInt("dmg_reduction"));
				doll.setDmgReductionChance(rs.getInt("dmg_reduction_chance"));
				doll.setDmgEvasionChance(rs.getInt("dmg_evasion_chance"));
				doll.setWeightReduction(rs.getInt("weight_reduction"));
				doll.setResistStun(rs.getInt("resist_stun"));
				doll.setResistStone(rs.getInt("resist_stone"));
				doll.setResistSleep(rs.getInt("resist_sleep"));
				doll.setResistFreeze(rs.getInt("resist_freeze"));
				doll.setResistHold(rs.getInt("resist_hold"));
				doll.setResistBlind(rs.getInt("resist_blind"));
				doll.setExpBonus(rs.getInt("exp_bonus"));
				doll.setMakeItemId(makeItemId);
				doll.setMakeTime(rs.getInt("make_time"));
				doll.setSkillId(skillId);
				doll.setSkillChance(rs.getByte("skill_chance"));
				doll.setSummonTime(rs.getInt("summon_time"));
				dolls.put(new Integer(itemId), doll);
			}
			System.out.println("loading magic dolls...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void reload() {
		HashMap<Integer, L1MagicDoll> dolls = new HashMap<Integer, L1MagicDoll>();
		loadDolls(dolls);
		_dolls = dolls;
	}
	
	public L1MagicDoll getTemplate(int itemId) {
		if (_dolls.containsKey(itemId)) {
			return _dolls.get(itemId);
		}
		return null;
	}
}
