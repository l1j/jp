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
package jp.l1j.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.ClientThread;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.datatables.SkillTable;
import static jp.l1j.server.model.L1MessageId.*;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.packets.server.S_AddSkill;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.templates.L1Skill;
import jp.l1j.server.types.Point;
import jp.l1j.server.utils.ArrayUtil;
import jp.l1j.server.utils.IntRange;

public class L1SpellBook {
	private static L1Alignment getSpellAlignment(int itemId) {
		int lawfuls[] = { 45000, 45008, 45018, 45021, 40171, 40179, 40180,
				40182, 40194, 40197, 40202, 40206, 40213, 40220, 40222 };
		int chaotics[] = { 45009, 45010, 45019, 40172, 40173, 40178, 40185,
				40186, 40192, 40196, 40201, 40204, 40211, 40221, 40225 };
		if (ArrayUtil.contains(lawfuls, itemId)) {
			return L1Alignment.LAWFUL;
		}
		if (ArrayUtil.contains(chaotics, itemId)) {
			return L1Alignment.CHAOTIC;
		}
		return L1Alignment.NEUTRAL;
	}

	private static L1MapArea LAWFUL_TEMPLE1 = new L1MapArea(33117, 32931,
			33127, 32941, 4);
	
	private static L1MapArea LAWFUL_TEMPLE2 = new L1MapArea(33136, 32236,
			33146, 32246, 4);
	
	private static L1MapArea LAWFUL_TEMPLE3 = new L1MapArea(32783, 32831,
			32803, 32851, 77);

	private static L1MapArea CHAOTIC_TEMPLE1 = new L1MapArea(32881, 32647,
			32891, 32657, 4);
	private static L1MapArea CHAOTIC_TEMPLE2 = new L1MapArea(32663, 32298,
			32673, 32308, 4);

	private static L1Alignment getTempleAlignment(L1Location loc) {
		L1MapArea lawfuls[] = { LAWFUL_TEMPLE1, LAWFUL_TEMPLE2, LAWFUL_TEMPLE3 };
		L1MapArea chaotics[] = { CHAOTIC_TEMPLE1, CHAOTIC_TEMPLE2 };
		for (L1MapArea area : lawfuls) {
			if (area.contains(loc)) {
				return L1Alignment.LAWFUL;
			}
		}
		for (L1MapArea area : chaotics) {
			if (area.contains(loc)) {
				return L1Alignment.CHAOTIC;
			}
		}
		return null;
	}

	private static final ArrayList<IntRange> ITEMID_LEVEL_RANGES = new ArrayList<IntRange>();

	static {
		ITEMID_LEVEL_RANGES.add(new IntRange(45000, 45007)); // Lv1
		ITEMID_LEVEL_RANGES.add(new IntRange(45008, 45015)); // Lv2
		ITEMID_LEVEL_RANGES.add(new IntRange(45016, 45022)); // Lv3
		ITEMID_LEVEL_RANGES.add(new IntRange(40170, 40177)); // Lv4
		ITEMID_LEVEL_RANGES.add(new IntRange(40178, 40185)); // Lv5
		ITEMID_LEVEL_RANGES.add(new IntRange(40186, 40193)); // Lv6
		ITEMID_LEVEL_RANGES.add(new IntRange(40194, 40201)); // Lv7
		ITEMID_LEVEL_RANGES.add(new IntRange(40202, 40209)); // Lv8
		ITEMID_LEVEL_RANGES.add(new IntRange(40210, 40217)); // Lv9
		ITEMID_LEVEL_RANGES.add(new IntRange(40218, 40225)); // Lv10
	}

	private static final HashMap<Integer, Integer> _iconMap = new HashMap<Integer, Integer>() {{
		put(45000, 47001); put(45001, 47002); put(45002, 47003); put(45003, 47004);
		put(45004, 47005); put(45005, 47006); put(45006, 47007); put(45007, 47008);
		put(45008, 47009); put(45009, 47010); put(45010, 47011); put(45011, 47012);
		put(45012, 47013); put(45013, 47014); put(45014, 47015); put(45015, 47016);
		put(45016, 47017); put(45017, 47018); put(45018, 47019); put(45019, 47020);
		put(45020, 47021); put(45021, 47022); put(45022, 47023);
		put(40170, 47025); put(40171, 47026); put(40172, 47027); put(40173, 47028);
		put(40174, 47029); put(40175, 47030); put(40176, 47031); put(40177, 47032);
		put(40178, 47033); put(40179, 47034); put(40180, 47035); put(40181, 47036);
		put(40182, 47037); put(40183, 47038); put(40184, 47039); put(40185, 47040);
		put(40186, 47041); put(40187, 47042); put(40188, 47043); put(40189, 47044);
		put(40190, 47045); put(40191, 47046); put(40192, 47047); put(40193, 47048);
		put(40194, 47049); put(40195, 47050); put(40196, 47051); put(40197, 47052);
		put(40198, 47053); put(40199, 47054); put(40200, 47055); put(40201, 47056);
		put(40202, 47057); put(40203, 47058); put(40204, 47059); put(40205, 47060);
		put(40206, 47061); put(40207, 47062); put(40208, 47063); put(40209, 47064);
		put(40210, 47065); put(40211, 47066); put(40212, 47067); put(40213, 47068);
		put(40214, 47069); put(40215, 47070); put(40216, 47071); put(40217, 47072);
		put(40218, 47073); put(40219, 47074); put(40220, 47075); put(40221, 47076);
		put(40222, 47077); put(40223, 47078); put(40224, 47079); put(40225, 47080);
	}};

	private static int getSpellLevel(int itemId) {
		for (int i = 0; i < ITEMID_LEVEL_RANGES.size(); i++) {
			if (ITEMID_LEVEL_RANGES.get(i).includes(itemId)) {
				return i + 1;
			}
		}
		throw new IllegalArgumentException("itemId is not spell's one.");
	}

	private static boolean isValidAlignment(L1Alignment spell, L1Alignment temple) {
		return spell == temple || spell == L1Alignment.NEUTRAL;
	}

	private static void sendLearningEffect(L1PcInstance pc, L1Alignment temple) {
		if (temple == null || temple == L1Alignment.NEUTRAL) {
			throw new IllegalArgumentException("Invalid alignment");
		}
		int effectId = (temple == L1Alignment.LAWFUL) ? 224 : 231;
		S_SkillSound effect = new S_SkillSound(pc.getId(), effectId);
		pc.sendPackets(effect);
		pc.broadcastPacket(effect);
	}

	private static boolean tryToLearnSpell(L1PcInstance pc, L1ItemInstance item) {
		int itemId = item.getItem().getItemId();
		int spellLevel = getSpellLevel(itemId);
		if (pc.getClassFeature().getMaxSpellLevel() < spellLevel) {
			pc.sendPackets(new S_ServerMessage(NOTHING_HAPPENED));
			return false;
		}
		if (pc.getLevel() < spellLevel * pc.getClassFeature().getSpellLearningInterval()) {
			pc.sendPackets(new S_ServerMessage(MAGIC_LEVEL_TOO_LOW));
			return false;
		}
		learnSpell(pc, item);
		return true;
	}

	private static void failInLearningSpell(L1PcInstance pc, L1ItemInstance item) {
		pc.getInventory().removeItem(item, 1);
		// 間違ったテンプルで読んだ場合雷が落ちる
		pc.sendPackets(new S_ServerMessage(NOTHING_HAPPENED));
		S_SkillSound effect = new S_SkillSound(pc.getId(), 10);
		pc.sendPackets(effect);
		pc.broadcastPacket(effect);
		// XXX ダメージは適当
		pc.setCurrentHp(Math.max(pc.getCurrentHp() - 45, 0));
		if (pc.getCurrentHp() <= 0) {
			pc.death(null);
		}
	}

	public static void useSpellBook(L1PcInstance pc, L1ItemInstance item) {
		int itemId = item.getItem().getItemId();
		L1Alignment spellAlignment = getSpellAlignment(itemId);
		L1Alignment templeAlignment = getTempleAlignment(pc.getLocation());
		if (pc.isGm()) {
			learnSpell(pc, item);
			sendLearningEffect(pc, templeAlignment);
			return;
		}
		if (templeAlignment == null) { // テンプル範囲外
			pc.sendPackets(new S_ServerMessage(NOTHING_HAPPENED));
			return;
		}
		if (isValidAlignment(spellAlignment, templeAlignment)) {
			if (tryToLearnSpell(pc, item)) {
				sendLearningEffect(pc, templeAlignment);
			}
		} else {
			failInLearningSpell(pc, item);
		}
	}

	private static void learnSpell(L1PcInstance pc, L1ItemInstance item) {	
		int itemId = item.getItem().getItemId();
		int spellLevel = getSpellLevel(itemId);	
		if ((pc.isCrown() || pc.isElf()) && spellLevel > 6) {
			createSpellIcon(pc, item);
			return;
		}	
		if (pc.isKnight() && spellLevel > 1) {
			createSpellIcon(pc, item);
			return;
		}	
		if (pc.isDarkelf() && spellLevel > 2) {
			createSpellIcon(pc, item);
			return;
		}	
		if (pc.isDragonKnight() || pc.isIllusionist()) {
			createSpellIcon(pc, item);
			return;
		}	
		pc.getInventory().removeItem(item, 1);
		L1Skill skill = SkillTable.getInstance().findByItemName(item.getItem().getName());
		pc.sendPackets(new S_AddSkill(skill));
		SkillTable.getInstance().spellMastery(pc.getId(), skill.getSkillId(),
				skill.getName(), 0, 0);
	}

	private static void createSpellIcon(L1PcInstance pc, L1ItemInstance item) {
		int itemId = _iconMap.get(item.getItem().getItemId());
		L1ItemInstance icon = ItemTable.getInstance().createItem(itemId);
		L1Inventory inventory = pc.getInventory();
		if(inventory.checkAddItem(icon, 1) == L1Inventory.OK) {
			icon.setIdentified(true);
			inventory.storeItem(icon);
			inventory.removeItem(item, 1);
		}
	}
	
	/*------------------------------ ここから未整理 ------------------------------*/
	public static void useElfSpellBook(L1PcInstance pc, L1ItemInstance item, int itemId) {
		int level = pc.getLevel();
		if ((pc.isElf() || pc.isGm()) && isLearnElfMagic(pc)) {
			if (itemId >= 40232 && itemId <= 40234 && level >= 10) {
				SpellBook2(pc, item);
			} else if (itemId >= 40235 && itemId <= 40236 && level >= 20) {
				SpellBook2(pc, item);
			}
			if (itemId >= 40237 && itemId <= 40240 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId >= 40241 && itemId <= 40243 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40244 && itemId <= 40246 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId >= 40247 && itemId <= 40248 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId >= 40249 && itemId <= 40250 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40251 && itemId <= 40252 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId == 40253 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId == 40254 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId == 40255 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId == 40256 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId == 40257 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40258 && itemId <= 40259 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId >= 40260 && itemId <= 40261 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId == 40262 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40263 && itemId <= 40264 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId >= 41149 && itemId <= 41150 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId == 41151 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 41152 && itemId <= 41153 && level >= 50) {
				SpellBook2(pc, item);
			}
		} else {
			pc.sendPackets(new S_ServerMessage(79)); // (原文:精霊の水晶はエルフのみが習得できます。)
		}
	}

	public static boolean isLearnElfMagic(L1PcInstance pc) {
		int pcX = pc.getX();
		int pcY = pc.getY();
		int pcMapId = pc.getMapId();
		if (pcX >= 32786 && pcX <= 32797 && pcY >= 32842 && pcY <= 32859 && pcMapId == 75 // 象牙の塔
				|| pc.getLocation().isInScreen(new Point(33055, 32336)) && pcMapId == 4) { // マザーツリー
			return true;
		}
		return false;
	}

	public static void SpellBook1(L1PcInstance pc, L1ItemInstance l1iteminstance,
			ClientThread clientthread) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		for (int j6 = 97; j6 < 113; j6++) {
			L1Skill l1skills = SkillTable.getInstance().findBySkillId(j6);
			String s1 = String.format(I18N_DARK_SPIRIT_CRYSTAL_NAME, l1skills.getName());
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;
				case 2: // '\002'
					k = i7;
					break;
				case 3: // '\003'
					l = i7;
					break;
				case 4: // '\004'
					i1 = i7;
					break;
				case 5: // '\005'
					j1 = i7;
					break;
				case 6: // '\006'
					k1 = i7;
					break;
				case 7: // '\007'
					l1 = i7;
					break;
				case 8: // '\b'
					i2 = i7;
					break;
				case 9: // '\t'
					j2 = i7;
					break;
				case 10: // '\n'
					k2 = i7;
					break;
				case 11: // '\013'
					l2 = i7;
					break;
				case 12: // '\f'
					i3 = i7;
					break;
				case 13: // '\r'
					j3 = i7;
					break;
				case 14: // '\016'
					k3 = i7;
					break;
				case 15: // '\017'
					l3 = i7;
					break;
				case 16: // '\020'
					i4 = i7;
					break;
				case 17: // '\021'
					j4 = i7;
					break;
				case 18: // '\022'
					k4 = i7;
					break;
				case 19: // '\023'
					l4 = i7;
					break;
				case 20: // '\024'
					i5 = i7;
					break;
				case 21: // '\025'
					j5 = i7;
					break;
				case 22: // '\026'
					k5 = i7;
					break;
				case 23: // '\027'
					l5 = i7;
					break;
				case 24: // '\030'
					i6 = i7;
					break;
				}
			}
		}
		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, 0, 0, 0, 0));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 231);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	public static void SpellBook2(L1PcInstance pc, L1ItemInstance l1iteminstance) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		for (int j6 = 129; j6 <= 176; j6++) {
			L1Skill l1skills = SkillTable.getInstance().findBySkillId(j6);
			String s1 = String.format(I18N_SPIRIT_CRYSTAL_NAME, l1skills.getName());
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				if (!pc.isGm() && !Config.LEARN_ALL_ELF_SKILLS
						&& l1skills.getAttr() != 0 && pc.getElfAttr() != l1skills.getAttr()) {
					if (pc.getElfAttr() == 0
							|| pc.getElfAttr() == 1
							|| pc.getElfAttr() == 2
							|| pc.getElfAttr() == 4
							|| pc.getElfAttr() == 8) { // 属性値が異常な場合は全属性を覚えられるようにしておく
						pc.sendPackets(new S_ServerMessage(79));
						return;
					}
				}
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;
				case 2: // '\002'
					k = i7;
					break;
				case 3: // '\003'
					l = i7;
					break;
				case 4: // '\004'
					i1 = i7;
					break;
				case 5: // '\005'
					j1 = i7;
					break;
				case 6: // '\006'
					k1 = i7;
					break;
				case 7: // '\007'
					l1 = i7;
					break;
				case 8: // '\b'
					i2 = i7;
					break;
				case 9: // '\t'
					j2 = i7;
					break;
				case 10: // '\n'
					k2 = i7;
					break;
				case 11: // '\013'
					l2 = i7;
					break;
				case 12: // '\f'
					i3 = i7;
					break;
				case 13: // '\r'
					j3 = i7;
					break;
				case 14: // '\016'
					k3 = i7;
					break;
				case 15: // '\017'
					l3 = i7;
					break;
				case 16: // '\020'
					i4 = i7;
					break;
				case 17: // '\021'
					j4 = i7;
					break;
				case 18: // '\022'
					k4 = i7;
					break;
				case 19: // '\023'
					l4 = i7;
					break;
				case 20: // '\024'
					i5 = i7;
					break;
				case 21: // '\025'
					j5 = i7;
					break;
				case 22: // '\026'
					k5 = i7;
					break;
				case 23: // '\027'
					l5 = i7;
					break;
				case 24: // '\030'
					i6 = i7;
					break;
				}
			}
		}
		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, 0, 0, 0, 0));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	public static void SpellBook3(L1PcInstance pc, L1ItemInstance l1iteminstance,
			ClientThread clientthread) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		for (int j6 = 87; j6 <= 91; j6++) {
			L1Skill l1skills = SkillTable.getInstance().findBySkillId(j6);
			String s1 = String.format(I18N_TECHNICAL_DOCUMENT_NAME, l1skills.getName());
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;
				case 2: // '\002'
					k = i7;
					break;
				case 3: // '\003'
					l = i7;
					break;
				case 4: // '\004'
					i1 = i7;
					break;
				case 5: // '\005'
					j1 = i7;
					break;
				case 6: // '\006'
					k1 = i7;
					break;
				case 7: // '\007'
					l1 = i7;
					break;
				case 8: // '\b'
					i2 = i7;
					break;
				case 9: // '\t'
					j2 = i7;
					break;
				case 10: // '\n'
					k2 = i7;
					break;
				case 11: // '\013'
					l2 = i7;
					break;
				case 12: // '\f'
					i3 = i7;
					break;
				case 13: // '\r'
					j3 = i7;
					break;
				case 14: // '\016'
					k3 = i7;
					break;
				case 15: // '\017'
					l3 = i7;
					break;
				case 16: // '\020'
					i4 = i7;
					break;
				case 17: // '\021'
					j4 = i7;
					break;
				case 18: // '\022'
					k4 = i7;
					break;
				case 19: // '\023'
					l4 = i7;
					break;
				case 20: // '\024'
					i5 = i7;
					break;
				case 21: // '\025'
					j5 = i7;
					break;
				case 22: // '\026'
					k5 = i7;
					break;
				case 23: // '\027'
					l5 = i7;
					break;
				case 24: // '\030'
					i6 = i7;
					break;
				}
			}
		}
		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, 0, 0, 0, 0));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	public static void SpellBook4(L1PcInstance pc, L1ItemInstance l1iteminstance,
			ClientThread clientthread) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		for (int j6 = 113; j6 < 121; j6++) {
			L1Skill l1skills = SkillTable.getInstance().findBySkillId(j6);
			String s1 = String.format(I18N_SPELL_BOOK_NAME, l1skills.getName());
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;
				case 2: // '\002'
					k = i7;
					break;
				case 3: // '\003'
					l = i7;
					break;
				case 4: // '\004'
					i1 = i7;
					break;
				case 5: // '\005'
					j1 = i7;
					break;
				case 6: // '\006'
					k1 = i7;
					break;
				case 7: // '\007'
					l1 = i7;
					break;
				case 8: // '\b'
					i2 = i7;
					break;
				case 9: // '\t'
					j2 = i7;
					break;
				case 10: // '\n'
					k2 = i7;
					break;
				case 11: // '\013'
					l2 = i7;
					break;
				case 12: // '\f'
					i3 = i7;
					break;
				case 13: // '\r'
					j3 = i7;
					break;
				case 14: // '\016'
					k3 = i7;
					break;
				case 15: // '\017'
					l3 = i7;
					break;
				case 16: // '\020'
					i4 = i7;
					break;
				case 17: // '\021'
					j4 = i7;
					break;
				case 18: // '\022'
					k4 = i7;
					break;
				case 19: // '\023'
					l4 = i7;
					break;
				case 20: // '\024'
					i5 = i7;
					break;
				case 21: // '\025'
					j5 = i7;
					break;
				case 22: // '\026'
					k5 = i7;
					break;
				case 23: // '\027'
					l5 = i7;
					break;
				case 24: // '\030'
					i6 = i7;
					break;
				}
			}
		}
		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, 0, 0, 0, 0));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	public static void SpellBook5(L1PcInstance pc, L1ItemInstance l1iteminstance,
			ClientThread clientthread) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int i8 = 0;
		int j8 = 0;
		int k8 = 0;
		int l8 = 0;
		for (int j6 = 181; j6 <= 195; j6++) {
			L1Skill l1skills = SkillTable.getInstance().findBySkillId(j6);
			String s1 = String.format(I18N_DRAGON_TABLET_NAME, l1skills.getName());
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;
				case 2: // '\002'
					k = i7;
					break;
				case 3: // '\003'
					l = i7;
					break;
				case 4: // '\004'
					i1 = i7;
					break;
				case 5: // '\005'
					j1 = i7;
					break;
				case 6: // '\006'
					k1 = i7;
					break;
				case 7: // '\007'
					l1 = i7;
					break;
				case 8: // '\b'
					i2 = i7;
					break;
				case 9: // '\t'
					j2 = i7;
					break;
				case 10: // '\n'
					k2 = i7;
					break;
				case 11: // '\013'
					l2 = i7;
					break;
				case 12: // '\f'
					i3 = i7;
					break;
				case 13: // '\r'
					j3 = i7;
					break;
				case 14: // '\016'
					k3 = i7;
					break;
				case 15: // '\017'
					l3 = i7;
					break;
				case 16: // '\020'
					i4 = i7;
					break;
				case 17: // '\021'
					j4 = i7;
					break;
				case 18: // '\022'
					k4 = i7;
					break;
				case 19: // '\023'
					l4 = i7;
					break;
				case 20: // '\024'
					i5 = i7;
					break;
				case 21: // '\025'
					j5 = i7;
					break;
				case 22: // '\026'
					k5 = i7;
					break;
				case 23: // '\027'
					l5 = i7;
					break;
				case 24: // '\030'
					i6 = i7;
					break;
				case 25: // '\031'
					j8 = i7;
					break;
				case 26: // '\032'
					k8 = i7;
					break;
				case 27: // '\033'
					l8 = i7;
					break;
				case 28: // '\034'
					i8 = i7;
					break;
				}
			}
		}
		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, j8, k8, l8, i8));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	public static void SpellBook6(L1PcInstance pc, L1ItemInstance l1iteminstance,
			ClientThread clientthread) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int i8 = 0;
		int j8 = 0;
		int k8 = 0;
		int l8 = 0;
		for (int j6 = 201; j6 <= 220; j6++) {
			L1Skill l1skills = SkillTable.getInstance().findBySkillId(j6);
			String s1 = String.format(I18N_MEMORY_CRYSTAL_NAME, l1skills.getName());
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;
				case 2: // '\002'
					k = i7;
					break;
				case 3: // '\003'
					l = i7;
					break;
				case 4: // '\004'
					i1 = i7;
					break;
				case 5: // '\005'
					j1 = i7;
					break;
				case 6: // '\006'
					k1 = i7;
					break;
				case 7: // '\007'
					l1 = i7;
					break;
				case 8: // '\b'
					i2 = i7;
					break;
				case 9: // '\t'
					j2 = i7;
					break;
				case 10: // '\n'
					k2 = i7;
					break;
				case 11: // '\013'
					l2 = i7;
					break;
				case 12: // '\f'
					i3 = i7;
					break;
				case 13: // '\r'
					j3 = i7;
					break;
				case 14: // '\016'
					k3 = i7;
					break;
				case 15: // '\017'
					l3 = i7;
					break;
				case 16: // '\020'
					i4 = i7;
					break;
				case 17: // '\021'
					j4 = i7;
					break;
				case 18: // '\022'
					k4 = i7;
					break;
				case 19: // '\023'
					l4 = i7;
					break;
				case 20: // '\024'
					i5 = i7;
					break;
				case 21: // '\025'
					j5 = i7;
					break;
				case 22: // '\026'
					k5 = i7;
					break;
				case 23: // '\027'
					l5 = i7;
					break;
				case 24: // '\030'
					i6 = i7;
					break;
				case 25: // '\031'
					j8 = i7;
					break;
				case 26: // '\032'
					k8 = i7;
					break;
				case 27: // '\033'
					l8 = i7;
					break;
				case 28: // '\034'
					i8 = i7;
					break;
				}
			}
		}
		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, j8, k8, l8, i8));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}
}
