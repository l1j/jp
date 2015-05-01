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

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class NpcTable {
	static final Logger _log = Logger.getLogger(NpcTable.class.getName());

	private static NpcTable _instance;

	private static HashMap<Integer, L1Npc> _npcs = new HashMap<Integer, L1Npc>();
	
	private static HashMap<String, Constructor<?>> _constructorCache = new HashMap<String, Constructor<?>>();

	private final Map<String, Integer> _familyTypes = buildFamily();

	synchronized public static NpcTable getInstance() {
		if (_instance == null) {
				_instance = new NpcTable();
		}
		return _instance;
	}

	private NpcTable() {
		loadNpcs(_npcs, _constructorCache);
	}

	private Constructor<?> getConstructor(String implName) {
		try {
			String implFullName = "jp.l1j.server.model.instance." + implName + "Instance";
			Constructor<?> con = Class.forName(implFullName).getConstructors()[0];
			return con;
		} catch (ClassNotFoundException e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
		return null;
	}

	private void registerConstructorCache(HashMap<String, Constructor<?>> constructorCache, String implName) {
		if (implName.isEmpty() || constructorCache.containsKey(implName)) {
			return;
		}
		constructorCache.put(implName, getConstructor(implName));
	}

	private void loadNpcs(HashMap<Integer, L1Npc> npcs, HashMap<String, Constructor<?>> constructorCache) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM npcs");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1Npc npc = new L1Npc();
				int npcId = rs.getInt("id");
				npc.setNpcId(npcId);
				npc.setName(rs.getString("name"));
				npc.setNameId(rs.getString("name_id"));
				npc.setImpl(rs.getString("impl"));
				npc.setGfxId(rs.getInt("gfx_id"));
				npc.setLevel(rs.getInt("level"));
				npc.setHp(rs.getInt("hp"));
				npc.setMp(rs.getInt("mp"));
				npc.setAc(rs.getInt("ac"));
				npc.setStr(rs.getByte("str"));
				npc.setCon(rs.getByte("con"));
				npc.setDex(rs.getByte("dex"));
				npc.setWis(rs.getByte("wis"));
				npc.setInt(rs.getByte("int"));
				npc.setMr(rs.getInt("mr"));
				npc.setExp(rs.getInt("exp"));
				npc.setLawful(rs.getInt("lawful"));
				npc.setSize(rs.getString("size"));
				npc.setWeakAttr(rs.getInt("weak_attr"));
				npc.setRanged(rs.getInt("ranged"));
				npc.setTamable(rs.getBoolean("tamable"));
				npc.setPassiSpeed(rs.getInt("move_speed"));
				npc.setAtkSpeed(rs.getInt("atk_speed"));
				npc.setAltAtkSpeed(rs.getInt("alt_atk_speed"));
				npc.setAtkMagicSpeed(rs.getInt("atk_magic_speed"));
				npc.setSubMagicSpeed(rs.getInt("sub_magic_speed"));
				npc.setUndead(rs.getInt("undead"));
				npc.setPoisonAtk(rs.getInt("poison_atk"));
				npc.setParalysIsAtk(rs.getInt("paralysis_atk"));
				npc.setAgro(rs.getBoolean("agro"));
				npc.setAgroSosc(rs.getBoolean("agro_sosc"));
				npc.setAgroCoi(rs.getBoolean("agro_coi"));
				Integer family = _familyTypes.get(rs.getString("family"));
				if (family == null) {
					npc.setFamily(0);
				} else {
					npc.setFamily(family.intValue());
				}
				int agrofamily = rs.getInt("agro_family");
				if (npc.getFamily() == 0 && agrofamily == 1) {
					npc.setAgroFamily(0);
				} else {
					npc.setAgroFamily(agrofamily);
				}
				npc.setAgroGfxId1(rs.getInt("agro_gfx_id1"));
				npc.setAgroGfxId2(rs.getInt("agro_gfx_id2"));
				npc.setPickUpItem(rs.getBoolean("pickup_item"));
				npc.setDigestItem(rs.getInt("digest_item"));
				npc.setBraveSpeed(rs.getBoolean("brave_speed"));
				npc.setHprInterval(rs.getInt("hpr_interval"));
				npc.setHpr(rs.getInt("hpr"));
				npc.setMprInterval(rs.getInt("mpr_interval"));
				npc.setMpr(rs.getInt("mpr"));
				npc.setTeleport(rs.getBoolean("teleport"));
				npc.setRandomLevel(rs.getInt("random_level"));
				npc.setRandomHp(rs.getInt("random_hp"));
				npc.setRandomMp(rs.getInt("random_mp"));
				npc.setRandomAc(rs.getInt("random_ac"));
				npc.setRandomExp(rs.getInt("random_exp"));
				npc.setRandomLawful(rs.getInt("random_lawful"));
				npc.setDamageReduction(rs.getInt("damage_reduction"));
				npc.setHard(rs.getBoolean("hard"));
				npc.setDoppel(rs.getBoolean("doppel"));
				npc.setEnableTU(rs.getBoolean("enable_tu"));
				npc.setEnableErase(rs.getBoolean("enable_erase"));
				npc.setBowActId(rs.getInt("bow_act_id"));
				npc.setKarma(rs.getInt("karma"));
				npc.setTransformId(rs.getInt("transform_id"));
				npc.setTransformGfxId(rs.getInt("transform_gfx_id"));
				npc.setLightSize(rs.getInt("light_size"));
				npc.setAmountFixed(rs.getBoolean("amount_fixed"));
				npc.setChangeHead(rs.getBoolean("change_head"));
				npc.setCantResurrect(rs.getBoolean("cant_resurrect"));
				npc.setEqualityDrop(rs.getBoolean("is_equality_drop"));
				npc.setBoss(rs.getBoolean("boss")); // TODO boss_endlog用
				registerConstructorCache(constructorCache, npc.getImpl());
				npcs.put(npcId, npc);
			}
			System.out.println("loading npcs...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			throw new RuntimeException("Unable to load NpcTable", e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
	}
	
	synchronized public void reload() {
		HashMap<Integer, L1Npc> npcs = new HashMap<Integer, L1Npc>();
		HashMap<String, Constructor<?>> constructorCache = new HashMap<String, Constructor<?>>();
		loadNpcs(npcs, constructorCache);
		_npcs = npcs;
		_constructorCache = constructorCache;
	}

	public L1Npc getTemplate(int id) {
		return _npcs.get(id);
	}

	public L1NpcInstance newNpcInstance(int id) {
		L1Npc npcTemp = getTemplate(id);
		if (npcTemp == null) {
			throw new IllegalArgumentException(String.format("NpcTemplate: %d not found", id));
		}
		return newNpcInstance(npcTemp);
	}

	public L1NpcInstance newNpcInstance(L1Npc template) {
		try {
			Constructor<?> con = _constructorCache.get(template.getImpl());
			return (L1NpcInstance) con.newInstance(new Object[] { template });
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return null;
	}

	private Map<String, Integer> buildFamily() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select distinct(family) as family from npcs WHERE NOT trim(family) =''");
			rs = pstm.executeQuery();
			int id = 1;
			while (rs.next()) {
				String family = rs.getString("family");
				result.put(family, id++);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Unable to load NpcTable", e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
		return result;
	}

	public int findNpcIdByName(String name) {
		for (L1Npc npc : _npcs.values()) {
			if (npc.getName().equals(name)) {
				return npc.getNpcId();
			}
		}
		return 0;
	}

	public int findNpcIdByNameWithoutSpace(String name) {
		for (L1Npc npc : _npcs.values()) {
			if (npc.getName().replace(" ", "").equals(name)) {
				return npc.getNpcId();
			}
		}
		return 0;
	}
}
