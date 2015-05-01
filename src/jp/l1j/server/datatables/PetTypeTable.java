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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_ITEM_LIST;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_NPC_LIST;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.templates.L1PetType;
import jp.l1j.server.utils.IntRange;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class PetTypeTable {
	private static PetTypeTable _instance;
	
	private static Logger _log = Logger.getLogger(PetTypeTable.class.getName());
	
	private Map<Integer, L1PetType> _types = new HashMap<Integer, L1PetType>();
	
	private Set<String> _defaultNames = new HashSet<String>();

	public static PetTypeTable getInstance() {
		if (_instance == null) {
			_instance = new PetTypeTable();
		}
		return _instance;
	}

	private PetTypeTable() {
		loadPetTypes(_types, _defaultNames);
	}

	private void loadPetTypes(Map<Integer, L1PetType> types, Set<String> defaultNames) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM pet_types");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int npcId = rs.getInt("npc_id");
				int tameItemId = rs.getInt("tame_item_id");
				int transformItemId = rs.getInt("transform_item_id");
				int transformNpcId = rs.getInt("transform_npc_id");
				boolean isErr = false;
				if (NpcTable.getInstance().getTemplate(npcId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_NPC_LIST, npcId));
					// %s はNPCリストに存在しません。
					isErr = true;
				}
				if (transformNpcId != 0 && NpcTable.getInstance().getTemplate(transformNpcId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_NPC_LIST, transformNpcId));
					// %s はNPCリストに存在しません。
					isErr = true;
				}
				if (tameItemId != 0 && ItemTable.getInstance().getTemplate(tameItemId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, tameItemId));
					// %s はアイテムリストに存在しません。
					isErr = true;
				}
				if (transformItemId != 0 && ItemTable.getInstance().getTemplate(transformItemId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, transformItemId));
					// %s はアイテムリストに存在しません。
					isErr = true;
				}
				if (isErr) {
					continue;
				}
				L1Npc npc = NpcTable.getInstance().getTemplate(npcId);
				String name = npc.getName();
				int minHpUp = rs.getInt("min_hpup");
				int maxHpUp = rs.getInt("max_hpup");
				int minMpUp = rs.getInt("min_mpup");
				int maxMpUp = rs.getInt("max_mpup");
				int msgIds[] = new int[5];
				for (int i = 0; i < 5; i++) {
					msgIds[i] = rs.getInt("message_id" + (i + 1));
				}
				int defyMsgId = rs.getInt("defy_message_id");
				boolean useEquipment = rs.getBoolean("use_equipment");
				IntRange hpUpRange = new IntRange(minHpUp, maxHpUp);
				IntRange mpUpRange = new IntRange(minMpUp, maxMpUp);
				types.put(npcId, new L1PetType(npcId, name,
						tameItemId, hpUpRange, mpUpRange, transformItemId,
						transformNpcId, msgIds, defyMsgId, useEquipment));
				defaultNames.add(name.toLowerCase());
			}
			System.out.println("loading pet types...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	public void reload() {
		Map<Integer, L1PetType> types = new HashMap<Integer, L1PetType>();
		Set<String> defaultNames = new HashSet<String>();
		loadPetTypes(types, defaultNames);
		_types = types;
		_defaultNames = defaultNames;
	}

	public L1PetType get(int baseNpcId) {
		return _types.get(baseNpcId);
	}

	public boolean isNameDefault(String name) {
		return _defaultNames.contains(name.toLowerCase());
	}
}