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
package jp.l1j.server.model.skill;

public class L1SkillId {
	public static final int SKILLS_BEGIN = 1;

	/*
	 * Regular Magic Lv1-10
	 */
	public static final int HEAL = 1; // E: LESSER_HEAL

	public static final int LIGHT = 2;

	public static final int SHIELD = 3;

	public static final int ENERGY_BOLT = 4;

	public static final int TELEPORT = 5;

	public static final int ICE_DAGGER = 6;

	public static final int WIND_CUTTER = 7; // E: WIND_SHURIKEN

	public static final int HOLY_WEAPON = 8;

	public static final int CURE_POISON = 9;

	public static final int CHILL_TOUCH = 10;

	public static final int CURSE_POISON = 11;

	public static final int ENCHANT_WEAPON = 12;

	public static final int DETECTION = 13;

	public static final int DECREASE_WEIGHT = 14;

	public static final int FIRE_ARROW = 15;

	public static final int STALAC = 16;

	public static final int LIGHTNING = 17;

	public static final int TURN_UNDEAD = 18;

	public static final int EXTRA_HEAL = 19; // E: HEAL

	public static final int CURSE_BLIND = 20;

	public static final int BLESSED_ARMOR = 21;

	public static final int FROZEN_CLOUD = 22;

	public static final int WEAK_ELEMENTAL = 23; // E: REVEAL_WEAKNESS

	// none = 24
	public static final int FIREBALL = 25;

	public static final int PHYSICAL_ENCHANT_DEX = 26; // E: ENCHANT_DEXTERITY

	public static final int WEAPON_BREAK = 27;

	public static final int VAMPIRIC_TOUCH = 28;

	public static final int SLOW = 29;

	public static final int EARTH_JAIL = 30;

	public static final int COUNTER_MAGIC = 31;

	public static final int MEDITATION = 32;

	public static final int CURSE_PARALYZE = 33;

	public static final int CALL_LIGHTNING = 34;

	public static final int GREATER_HEAL = 35;

	public static final int TAMING_MONSTER = 36; // E: TAME_MONSTER

	public static final int REMOVE_CURSE = 37;

	public static final int CONE_OF_COLD = 38;

	public static final int MANA_DRAIN = 39;

	public static final int DARKNESS = 40;

	public static final int CREATE_ZOMBIE = 41;

	public static final int PHYSICAL_ENCHANT_STR = 42; // E: ENCHANT_MIGHTY

	public static final int HASTE = 43;

	public static final int CANCELLATION = 44; // E: CANCEL MAGIC

	public static final int ERUPTION = 45;

	public static final int SUNBURST = 46;

	public static final int WEAKNESS = 47;

	public static final int BLESS_WEAPON = 48;

	public static final int HEAL_ALL = 49; // E: HEAL_PLEDGE

	public static final int ICE_LANCE = 50;

	public static final int SUMMON_MONSTER = 51;

	public static final int HOLY_WALK = 52;

	public static final int TORNADO = 53;

	public static final int GREATER_HASTE = 54;

	public static final int BERSERKERS = 55;

	public static final int DISEASE = 56;

	public static final int FULL_HEAL = 57;

	public static final int FIRE_WALL = 58;

	public static final int BLIZZARD = 59;

	public static final int INVISIBILITY = 60;

	public static final int RESURRECTION = 61;

	public static final int EARTHQUAKE = 62;

	public static final int LIFE_STREAM = 63;

	public static final int SILENCE = 64;

	public static final int LIGHTNING_STORM = 65;

	public static final int FOG_OF_SLEEPING = 66;

	public static final int SHAPE_CHANGE = 67; // E: POLYMORPH

	public static final int IMMUNE_TO_HARM = 68;

	public static final int MASS_TELEPORT = 69;

	public static final int FIRE_STORM = 70;

	public static final int DECAY_POTION = 71;

	public static final int COUNTER_DETECTION = 72;

	public static final int CREATE_MAGICAL_WEAPON = 73;

	public static final int METEOR_STRIKE = 74;

	public static final int GREATER_RESURRECTION = 75;

	public static final int MASS_SLOW = 76;

	public static final int DISINTEGRATE = 77; // E: DESTROY

	public static final int ABSOLUTE_BARRIER = 78;

	public static final int ADVANCE_SPIRIT = 79;

	public static final int FREEZING_BLIZZARD = 80;

	// none = 81 - 86
	/*
	 * Knight skills
	 */
	public static final int SHOCK_STUN = 87; // E: STUN_SHOCK

	public static final int REDUCTION_ARMOR = 88;

	public static final int BOUNCE_ATTACK = 89;

	public static final int SOLID_CARRIAGE = 90;

	public static final int COUNTER_BARRIER = 91;

	// none = 92-96
	/*
	 * Dark Spirit Magic
	 */
	public static final int BLIND_HIDING = 97;

	public static final int ENCHANT_VENOM = 98;

	public static final int SHADOW_ARMOR = 99;

	public static final int BRING_STONE = 100;

	public static final int MOVING_ACCELERATION = 101; // E: PURIFY_STONE

	public static final int BURNING_SPIRIT = 102;

	public static final int SHADOW_SLEEP = 103; // 旧 DARK_BLIND

	public static final int VENOM_RESIST = 104;

	public static final int DOUBLE_BRAKE = 105;

	public static final int UNCANNY_DODGE = 106;

	public static final int SHADOW_FANG = 107;

	public static final int FINAL_BURN = 108;

	public static final int DRESS_MIGHTY = 109;

	public static final int DRESS_DEXTERITY = 110;

	public static final int DRESS_EVASION = 111;
	
	public static final int ARMOR_BREAK = 112;

	// none = 112
	/*
	 * Royal Magic
	 */
	public static final int TRUE_TARGET = 113;

	public static final int GLOWING_AURA = 114;

	public static final int SHINING_SHIELD = 115;

	public static final int CALL_CLAN = 116; // E: CALL_PLEDGE_MEMBER

	public static final int BRAVE_AURA = 117;

	public static final int RUN_CLAN = 118;

	public static final int DIVINE_SACRIFICE = 119; // 未実装

	public static final int AVATAR = 120; // 未実装

	// none = 121 - 128
	/*
	 * Spirit Magic
	 */
	public static final int RESIST_MAGIC = 129;

	public static final int BODY_TO_MIND = 130;

	public static final int TELEPORT_TO_MATHER = 131;

	public static final int TRIPLE_ARROW = 132;

	public static final int ELEMENTAL_FALL_DOWN = 133;

	public static final int COUNTER_MIRROR = 134;

	// none = 135 - 136
	public static final int CLEAR_MIND = 137;

	public static final int RESIST_ELEMENTAL = 138;

	// none = 139 - 144
	public static final int RETURN_TO_NATURE = 145;

	public static final int BLOODY_SOUL = 146; // E: BLOOD_TO_SOUL

	public static final int ELEMENTAL_PROTECTION = 147; // E:PROTECTION_FROM_ELEMENTAL

	public static final int FIRE_WEAPON = 148;

	public static final int WIND_SHOT = 149;

	public static final int WIND_WALK = 150;

	public static final int EARTH_SKIN = 151;

	public static final int ENTANGLE = 152;

	public static final int ERASE_MAGIC = 153;

	public static final int LESSER_ELEMENTAL = 154; // E:SUMMON_LESSER_ELEMENTAL

	public static final int FIRE_BLESS = 155; // E: BLESS_OF_FIRE

	public static final int STORM_EYE = 156; // E: EYE_OF_STORM

	public static final int EARTH_BIND = 157;

	public static final int NATURES_TOUCH = 158;

	public static final int EARTH_BLESS = 159; // E: BLESS_OF_EARTH

	public static final int AQUA_PROTECTER = 160;

	public static final int AREA_OF_SILENCE = 161;

	public static final int GREATER_ELEMENTAL = 162; // E:SUMMON_GREATER_ELEMENTAL

	public static final int BURNING_WEAPON = 163;

	public static final int NATURES_BLESSING = 164;

	public static final int CALL_OF_NATURE = 165; // E: NATURES_MIRACLE

	public static final int STORM_SHOT = 166;

	public static final int WIND_SHACKLE = 167;

	public static final int IRON_SKIN = 168;

	public static final int EXOTIC_VITALIZE = 169;

	public static final int WATER_LIFE = 170;

	public static final int ELEMENTAL_FIRE = 171;

	public static final int STORM_WALK = 172;

	public static final int POLLUTE_WATER = 173;

	public static final int STRIKER_GALE = 174;

	public static final int SOUL_OF_FLAME = 175;

	public static final int ADDITIONAL_FIRE = 176;

	// none = 177-180
	/*
	 * Dragon Knight skills
	 */
	public static final int DRAGON_SKIN = 181;

	public static final int BURNING_SLASH = 182;

	public static final int GUARD_BRAKE = 183;

	public static final int MAGMA_BREATH = 184;

	public static final int AWAKEN_ANTHARAS = 185;

	public static final int BLOODLUST = 186;

	public static final int FOE_SLAYER = 187;

	public static final int RESIST_FEAR = 188;

	public static final int MAGMA_ARROW = 189; // 旧 SHOCK_SKIN

	public static final int AWAKEN_FAFURION = 190;

	public static final int MORTAL_BODY = 191;

	public static final int THUNDER_GRAB = 192;

	public static final int HORROR_OF_DEATH = 193;

	public static final int EYE_OF_DRAGON = 194; // 旧 FREEZING_BREATH

	public static final int AWAKEN_VALAKAS = 195;

	// none = 196-200
	/*
	 * Illusionist Magic
	 */
	public static final int MIRROR_IMAGE = 201;

	public static final int CONFUSION = 202;

	public static final int SMASH_ENERGY = 203; // 旧 SMASH

	public static final int ILLUSION_OGRE = 204;

	public static final int CUBE_IGNITION = 205;

	public static final int CONCENTRATION = 206;

	public static final int MIND_BREAK = 207;

	public static final int BONE_BREAK = 208;

	public static final int ILLUSION_LICH = 209;

	public static final int CUBE_QUAKE = 210;

	public static final int PATIENCE = 211;

	public static final int PHANTASM = 212;

	public static final int EYES_BREAKER = 213; // 旧 ARM_BREAKER

	public static final int ILLUSION_DIA_GOLEM = 214;

	public static final int CUBE_SHOCK = 215;

	public static final int INSIGHT = 216;

	public static final int PANIC = 217;

	public static final int JOY_OF_PAIN = 218;

	public static final int ILLUSION_AVATAR = 219;

	public static final int CUBE_BALANCE = 220;

	public static final int SKILLS_END = 220;

	public static final int MASS_SHOCK_STUN = 500; // 範囲スタン

	public static final int MASS_CANCELLATION = 501; // 範囲キャンセレーション

	public static final int AREA_POISON = 502; // 範囲毒攻撃（汎用）

	public static final int AREA_WIND_SHACKLE = 503; // 範囲ウィンドシャックル

	/*
	 * Status
	 */
	public static final int STATUS_BEGIN = 1000;

	public static final int STATUS_BRAVE = 1000;

	public static final int STATUS_HASTE = 1001;

	public static final int STATUS_BLUE_POTION = 1002;

	public static final int STATUS_UNDERWATER_BREATH = 1003;

	public static final int STATUS_WISDOM_POTION = 1004;

	public static final int STATUS_CHAT_PROHIBITED = 1005;

	public static final int STATUS_POISON = 1006;

	public static final int STATUS_POISON_SILENCE = 1007;

	public static final int STATUS_POISON_PARALYZING = 1008;

	public static final int STATUS_POISON_PARALYZED = 1009;

	public static final int STATUS_CURSE_PARALYZING = 1010;

	public static final int STATUS_CURSE_PARALYZED = 1011;

	public static final int STATUS_FLOATING_EYE = 1012;

	public static final int STATUS_HOLY_WATER = 1013;

	public static final int STATUS_HOLY_MITHRIL_POWDER = 1014;

	public static final int STATUS_HOLY_WATER_OF_EVA = 1015;

	public static final int STATUS_ELFBRAVE = 1016;

	public static final int STATUS_RIBRAVE = 1017;

	public static final int STATUS_CUBE_IGNITION_TO_ALLY = 1018;

	public static final int STATUS_CUBE_IGNITION_TO_ENEMY = 1019;

	public static final int STATUS_CUBE_QUAKE_TO_ALLY = 1020;

	public static final int STATUS_CUBE_QUAKE_TO_ENEMY = 1021;

	public static final int STATUS_CUBE_SHOCK_TO_ALLY = 1022;

	public static final int STATUS_CUBE_SHOCK_TO_ENEMY = 1023;

	public static final int STATUS_MR_REDUCTION_BY_CUBE_SHOCK = 1024;

	public static final int STATUS_CUBE_BALANCE = 1025;

	public static final int STATUS_THIRD_SPEED = 1026;

	public static final int STATUS_FLORA_POTION_STR = 1027;

	public static final int STATUS_FLORA_POTION_DEX = 1028;

	public static final int STATUS_FREEZE = 1029;

	public static final int STATUS_CURSE_BARLOG = 1030;

	public static final int STATUS_CURSE_YAHEE = 1031;

	public static final int STATUS_WEAKNESS_EXPOSURE_LV1 = 1032;

	public static final int STATUS_WEAKNESS_EXPOSURE_LV2 = 1033;

	public static final int STATUS_WEAKNESS_EXPOSURE_LV3 = 1034;

	public static final int STATUS_DESTRUCTION_NOSTRUM = 1035;

	public static final int STATUS_HOLD = 1036;

	public static final int STATUS_END = 1037;

	public static final int STATUS_EXP_UP =1100; // 祈りのポーション

	public static final int STATUS_EXP_UP_II =1101; // 祈りのポーションII

	public static final int POTION_OF_SWORDMAN = 1102; // 剣士のポーション

	public static final int POTION_OF_MAGICIAN = 1103; // 術士のポーション

	public static final int POTION_OF_RECOVERY = 1104; // 治癒のポーション

	public static final int POTION_OF_MEDITATION = 1105; // 瞑想のポーション

	public static final int POTION_OF_LIFE = 1106; // 生命のポーション

	public static final int POTION_OF_MAGIC = 1107; // 魔法のポーション

	public static final int POTION_OF_MAGIC_RESIST = 1108; // 魔法抵抗のポーション

	public static final int POTION_OF_STR = 1109; // 腕力のポーション

	public static final int POTION_OF_DEX = 1110; // 機敏のポーション

	public static final int POTION_OF_CON = 1111; // 体力のポーション

	public static final int POTION_OF_INT = 1112; // 知力のポーション

	public static final int POTION_OF_WIS = 1113; // 精神のポーション

	public static final int POTION_OF_RAGE = 1114; // 憤怒のポーション

	public static final int POTION_OF_CONCENTRATION = 1115; // 集中のポーション

	public static final int GMSTATUS_BEGIN = 2000;

	public static final int GMSTATUS_INVISIBLE = 2000;

	public static final int GMSTATUS_HPBAR = 2001;

	public static final int GMSTATUS_SHOWTRAPS = 2002;

	public static final int GMSTATUS_FINDINVIS = 2003;

	public static final int GMSTATUS_END = 2003;

	public static final int COOKING_NOW = 2999;

	public static final int COOKING_BEGIN = 3000;

	public static final int COOKING_1_0_N = 3000;

	public static final int COOKING_1_1_N = 3001;

	public static final int COOKING_1_2_N = 3002;

	public static final int COOKING_1_3_N = 3003;

	public static final int COOKING_1_4_N = 3004;

	public static final int COOKING_1_5_N = 3005;

	public static final int COOKING_1_6_N = 3006;

	public static final int COOKING_1_7_N = 3007;

	public static final int COOKING_1_0_S = 3008;

	public static final int COOKING_1_1_S = 3009;

	public static final int COOKING_1_2_S = 3010;

	public static final int COOKING_1_3_S = 3011;

	public static final int COOKING_1_4_S = 3012;

	public static final int COOKING_1_5_S = 3013;

	public static final int COOKING_1_6_S = 3014;

	public static final int COOKING_1_7_S = 3015;

	public static final int COOKING_2_0_N = 3016;

	public static final int COOKING_2_1_N = 3017;

	public static final int COOKING_2_2_N = 3018;

	public static final int COOKING_2_3_N = 3019;

	public static final int COOKING_2_4_N = 3020;

	public static final int COOKING_2_5_N = 3021;

	public static final int COOKING_2_6_N = 3022;

	public static final int COOKING_2_7_N = 3023;

	public static final int COOKING_2_0_S = 3024;

	public static final int COOKING_2_1_S = 3025;

	public static final int COOKING_2_2_S = 3026;

	public static final int COOKING_2_3_S = 3027;

	public static final int COOKING_2_4_S = 3028;

	public static final int COOKING_2_5_S = 3029;

	public static final int COOKING_2_6_S = 3030;

	public static final int COOKING_2_7_S = 3031;

	public static final int COOKING_3_0_N = 3032;

	public static final int COOKING_3_1_N = 3033;

	public static final int COOKING_3_2_N = 3034;

	public static final int COOKING_3_3_N = 3035;

	public static final int COOKING_3_4_N = 3036;

	public static final int COOKING_3_5_N = 3037;

	public static final int COOKING_3_6_N = 3038;

	public static final int COOKING_3_7_N = 3039;

	public static final int COOKING_3_0_S = 3040;

	public static final int COOKING_3_1_S = 3041;

	public static final int COOKING_3_2_S = 3042;

	public static final int COOKING_3_3_S = 3043;

	public static final int COOKING_3_4_S = 3044;

	public static final int COOKING_3_5_S = 3045;

	public static final int COOKING_3_6_S = 3046;

	public static final int COOKING_3_7_S = 3047;

	public static final int ELIXIR_OF_IVORY_TOWER = 3048; // 象牙の塔の妙薬
	
	public static final int COOKING_4_1 = 3049; // 力強い和牛ステーキ
	
	public static final int COOKING_4_2 = 3050; // 素早い鮭の煮付
	
	public static final int COOKING_4_3 = 3051; // 賢い七面鳥焼き
	
	public static final int COOKING_4_4 = 3052; // 修練の鶏スープ

	public static final int COOKING_END = 3052;
 
	public static final int BLOODSTAIN_OF_ANTHARAS = 4011; // アンタラスの血痕

	public static final int BLOODSTAIN_OF_FAFURION = 4012; // パプリオンの血痕

	public static final int BLOODSTAIN_OF_LINDVIOR = 4013; // リンドビオルの血痕

	public static final int BLOODSTAIN_OF_VALAKAS = 4014; // ヴァラカスの血痕

	public static final int FAFURION_DEATH_HEAL = 4015; // パプリオンデスヒール

	public static final int FAFURION_REDUCE_HEAL = 4016; // パプリオンリデュースヒール

	public static final int FAFURION_DEATH_PORTION = 4017; // パプリオンデスポーション

	public static final int BLESS_OF_CRAY = 4056; // クレイの祝福

	public static final int BLESS_OF_SAEL = 4057; // サエルの祝福

	public static final int BLESS_OF_GUNTER = 4058; // グンターの助言

	public static final int MAGIC_EYE_OF_ANTHARAS = 4060; // 地竜の魔眼

	public static final int MAGIC_EYE_OF_FAFURION = 4061; // 水竜の魔眼

	public static final int MAGIC_EYE_OF_LINDVIOR = 4062; // 風竜の魔眼

	public static final int MAGIC_EYE_OF_VALAKAS = 4063; // 火竜の魔眼

	public static final int MAGIC_EYE_OF_BIRTH = 4064; // 誕生の魔眼

	public static final int MAGIC_EYE_OF_SHAPE = 4065; // 形象の魔眼

	public static final int MAGIC_EYE_OF_LIFE = 4066; // 生命の魔眼

	public static final int STONE_OF_DRAGON = 4071; // ドラゴンの石

	public static final int CHASER_OF_THEBES = 10146; // テーベチェイサー

	public static final int CHASER_OF_KUKULCAN = 10146; // ククルカンチェイサー

	public static final int CURSE_PARALYZE2 = 11011;

	public static final int STATUS_BRAVE2 = 11017; //TODO ペットレース用ステータス

	public static final int BLESS_OF_COMA1 = 11018; // コマの祝福Ａ

	public static final int BLESS_OF_COMA2 = 11019; // コマの祝福Ｂ

	public static final int BLESS_OF_SAMURAI = 11020; // 武士の心得

	public static final int ICE_ERUPTION = 702; // アイスイラプション

	public static final int SAND_ERUPTION = 704; // サンドイラプション

	public static final int ELZABE_AREA_SILENCE = 707; // エルジャベ エリアサイレンス

	public static final int ELIZABE_SAND_ERUPTION  = 711; // エリジャベ サンドイラプション

	public static final int ELIZABE_AREA_POISON  = 712; // エリジャベ エリアポイズン

	public static final int ELIZABE_TELEPORT  = 713; // エリジャベ テレポート
}
