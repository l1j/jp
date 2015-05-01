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
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Pet;
import jp.l1j.server.templates.L1PetType;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.L1QueryUtil;
import jp.l1j.server.utils.SqlUtil;

public class PetTable {
	private static Logger _log = Logger.getLogger(PetTable.class.getName());

	private static PetTable _instance;

	private final HashMap<Integer, L1Pet> _pets = new HashMap<Integer, L1Pet>();

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	public static PetTable getInstance() {
		if (_instance == null) {
			_instance = new PetTable();
		}
		return _instance;
	}

	private PetTable() {
		load();
	}

	private void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM pets");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1Pet pet = new L1Pet();
				int itemobjid = rs.getInt(1);
				pet.setItemObjId(itemobjid);
				pet.setObjId(rs.getInt(2));
				pet.setName(rs.getString(3));
				pet.setNpcId(rs.getInt(4));
				pet.setLevel(rs.getInt(5));
				pet.setHp(rs.getInt(6));
				pet.setMp(rs.getInt(7));
				pet.setExp(rs.getInt(8));
				pet.setLawful(rs.getInt(9));
				pet.setFood(rs.getInt(10));
				_pets.put(new Integer(itemobjid), pet);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void storeNewPet(L1NpcInstance pet, int objid, int itemobjid) {
		// XXX 呼ばれる前と処理の重複
		L1Pet l1pet = new L1Pet();
		l1pet.setItemObjId(itemobjid);
		l1pet.setObjId(objid);
		l1pet.setName(pet.getNpcTemplate().getName());
		l1pet.setNpcId(pet.getNpcTemplate().getNpcId());
		l1pet.setLevel(pet.getNpcTemplate().getLevel());
		l1pet.setHp(pet.getMaxHp());
		l1pet.setMp(pet.getMaxMp());
		l1pet.setExp(750); // Lv.5のEXP
		l1pet.setLawful(0);
		l1pet.setFood(50);
		_pets.put(new Integer(itemobjid), l1pet);
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO pets SET item_obj_id=?,id=?,name=?,npc_id=?,level=?,hp=?,mp=?,exp=?,lawful=?,food=?");
			pstm.setInt(1, l1pet.getItemObjId());
			pstm.setInt(2, l1pet.getObjId());
			pstm.setString(3, l1pet.getName());
			pstm.setInt(4, l1pet.getNpcId());
			pstm.setInt(5, l1pet.getLevel());
			pstm.setInt(6, l1pet.getHp());
			pstm.setInt(7, l1pet.getMp());
			pstm.setInt(8, l1pet.getExp());
			pstm.setInt(9, l1pet.getLawful());
			pstm.setInt(10, l1pet.getFood());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public void storePet(L1Pet pet) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE pets SET id=?,name=?,npc_id=?,level=?,hp=?,mp=?,exp=?,lawful=?,food=? WHERE item_obj_id=?");
			pstm.setInt(1, pet.getObjId());
			pstm.setString(2, pet.getName());
			pstm.setInt(3, pet.getNpcId());
			pstm.setInt(4, pet.getLevel());
			pstm.setInt(5, pet.getHp());
			pstm.setInt(6, pet.getMp());
			pstm.setInt(7, pet.getExp());
			pstm.setInt(8, pet.getLawful());
			pstm.setInt(9, pet.getFood());
			pstm.setInt(10, pet.getItemObjId());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	/** ペット空腹度更新 */
	public void storePetFood(int itemObjId, int food) {
		L1QueryUtil.execute("UPDATE pets SET food = ? WHERE item_obj_id = ?", food, itemObjId);
	}

	public void deletePet(int itemobjid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?");
			pstm.setInt(1, itemobjid);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		_pets.remove(itemobjid);
	}

	/**
	 * Petsテーブルに既に名前が存在するかを返す。
	 * 
	 * @param nameCaseInsensitive
	 *            調べるペットの名前。大文字小文字の差異は無視される。
	 * @return 既に名前が存在すればtrue
	 */
	public static boolean isNameExists(String nameCaseInsensitive) {
		String nameLower = nameCaseInsensitive.toLowerCase();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			/*
			 * 同じ名前を探す。MySQLはデフォルトでcase insensitiveなため
			 * 本来LOWERは必要ないが、binaryに変更された場合に備えて。
			 */
			pstm = con.prepareStatement("SELECT item_obj_id FROM pets WHERE LOWER(name)=?");
			pstm.setString(1, nameLower);
			rs = pstm.executeQuery();
			if (!rs.next()) { // 同じ名前が無かった
				return false;
			}
			if (PetTypeTable.getInstance().isNameDefault(nameLower)) { // デフォルトの名前なら重複していないとみなす
				return false;
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return true;
	}

	/**
	 * ペット購入
	 */
	public void buyNewPet(int petNpcId, int objid, int itemobjid, int upLv, int lvExp) {
		L1PetType petType = PetTypeTable.getInstance().get(petNpcId);
		L1Pet l1pet = new L1Pet();
		l1pet.setItemObjId(itemobjid);
		l1pet.setObjId(objid);
		l1pet.setNpcId(petNpcId);
		l1pet.setName(petType.getName());
		l1pet.setLevel(upLv);
		int minHpUp = petType.getHpUpRange().getLow();
		int maxHpUp = petType.getHpUpRange().getHigh();
		int minMpUp = petType.getMpUpRange().getLow();
		int maxMpUp = petType.getMpUpRange().getHigh();
		short randomhp = (short) ((minHpUp + maxHpUp) / 2);
		short randommp = (short) ((minMpUp + maxMpUp) / 2);
		for (int i = 1; i < upLv; i++) {
			randomhp += (_random.nextInt(maxHpUp - minHpUp) + minHpUp + 1);
			randommp += (_random.nextInt(maxMpUp - minMpUp) + minMpUp + 1);
		}
		l1pet.setHp(randomhp);
		l1pet.setMp(randommp);
		l1pet.setExp(lvExp); // upLv EXP
		l1pet.setLawful(0);
		_pets.put(new Integer(itemobjid), l1pet);
		l1pet.setFood(50);
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO pets SET item_obj_id=?,id=?,name=?,npc_id=?,level=?,hp=?,mp=?,exp=?,lawful=?,food=?");
			pstm.setInt(1, l1pet.getItemObjId());
			pstm.setInt(2, l1pet.getObjId());
			pstm.setString(3, l1pet.getName());
			pstm.setInt(4, l1pet.getNpcId());
			pstm.setInt(5, l1pet.getLevel());
			pstm.setInt(6, l1pet.getHp());
			pstm.setInt(7, l1pet.getMp());
			pstm.setInt(8, l1pet.getExp());
			pstm.setInt(9, l1pet.getLawful());
			pstm.setInt(10, l1pet.getFood());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public L1Pet getTemplate(int itemObjId) {
		return _pets.get(itemObjId);
	}
}