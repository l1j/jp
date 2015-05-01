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
import jp.l1j.server.templates.L1PetItem;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class PetItemTable {
	private static Logger _log = Logger.getLogger(PetItemTable.class.getName());

	private static PetItemTable _instance;

	private static HashMap<Integer, L1PetItem> _petItems = new HashMap<Integer, L1PetItem>();

	public static PetItemTable getInstance() {
		if (_instance == null) {
			_instance = new PetItemTable();
		}
		return _instance;
	}

	private PetItemTable() {
		loadPetItems(_petItems);
	}

	private void loadPetItems(HashMap<Integer, L1PetItem> petItems) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM pet_items");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int itemId = rs.getInt("item_id");
				boolean isErr = false;
				if (ItemTable.getInstance().getTemplate(itemId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, itemId));
					// %s はアイテムリストに存在しません。
					isErr = true;
				}
				if (isErr) {
					continue;
				}
				L1PetItem petItem = new L1PetItem();
				petItem.setItemId(itemId);
				petItem.setHitModifier(rs.getInt("hit_modifier"));
				petItem.setDamageModifier(rs.getInt("dmg_modifier"));
				petItem.setAddAc(rs.getInt("ac"));
				petItem.setAddStr(rs.getInt("str"));
				petItem.setAddCon(rs.getInt("con"));
				petItem.setAddDex(rs.getInt("dex"));
				petItem.setAddInt(rs.getInt("int"));
				petItem.setAddWis(rs.getInt("wis"));
				petItem.setAddHp(rs.getInt("hp"));
				petItem.setAddMp(rs.getInt("mp"));
				petItem.setAddSp(rs.getInt("sp"));
				petItem.setAddMr(rs.getInt("mr"));
				petItem.setUseType(rs.getInt("use_type"));
				petItems.put(petItem.getItemId(), petItem);
			}
			System.out.println("loading pet items...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "error while creating pet_items table", e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}

	public void reload() {
		HashMap<Integer, L1PetItem> petItems = new HashMap<Integer, L1PetItem>();
		loadPetItems(petItems);
		_petItems = petItems;
	}

	public L1PetItem getTemplate(int itemId) {
		return _petItems.get(itemId);
	}
}
