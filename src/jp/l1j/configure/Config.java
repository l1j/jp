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

package jp.l1j.configure;

import java.util.Calendar;
import jp.l1j.configure.Annotations.Configure;
import jp.l1j.configure.CustomLoaders.WarTimeLoader;
import jp.l1j.configure.CustomLoaders.WarTimeUnitLoader;
import jp.l1j.server.utils.IntRange;
import jp.l1j.server.utils.PerformanceTimer;

public final class Config {
	private static final String SERVER = "./config/server.properties.xml";
	private static final String RATE = "./config/rates.properties.xml";
	private static final String ALT = "./config/altsettings.properties.xml";
	private static final String EVENT = "./config/events.properties.xml";
	private static final String CHARACTER = "./config/charsettings.properties.xml";
	private static final String PACK = "./config/pack.properties";

	/** Debug/release mode */
	@Configure(file = SERVER, key = "DebugMode")
	public static boolean DEBUG_MODE = false;

	/** Thread pools size */
	@Configure(file = SERVER, key = "GeneralThreadPoolType")
	public static int THREAD_P_TYPE_GENERAL = 0;

	@Configure(file = SERVER, key = "GeneralThreadPoolSize")
	public static int THREAD_P_SIZE_GENERAL = 0;

	/** Server control */
	@Configure(file = SERVER, key = "GameserverHostname")
	public static String GAME_SERVER_HOST_NAME = "*";

	@Configure(file = SERVER, key = "GameserverPort")
	public static int GAME_SERVER_PORT = 2000;

	@Configure(file = SERVER, key = "Driver")
	public static String DB_DRIVER = "com.mysql.jdbc.Driver";

	@Configure(file = SERVER, key = "URL")
	public static String DB_URL = "jdbc:mysql://localhost/l1jdb?useUnicode=true&characterEncoding=utf8";

	@Configure(file = SERVER, key = "Login")
	public static String DB_LOGIN = "root";

	@Configure(file = SERVER, key = "Password")
	public static String DB_PASSWORD = "";

	@Configure(file = SERVER, key = "TimeZone")
	public static String TIME_ZONE = "JST";

	@Configure(file = SERVER, key = "ClientLanguage")
	public static int CLIENT_LANGUAGE = 4;

	public static String CLIENT_LANGUAGE_CODE;

	public static String[] LANGUAGE_CODE_ARRAY = { "UTF8", "EUCKR", "UTF8",
			"BIG5", "SJIS", "GBK" };

	@Configure(file = SERVER, key = "HostnameLookups")
	public static boolean HOSTNAME_LOOKUPS = false;

	@Configure(file = SERVER, key = "AutomaticKick")
	public static int AUTOMATIC_KICK = 10;

	@Configure(file = SERVER, key = "AutoCreateAccounts")
	public static boolean AUTO_CREATE_ACCOUNTS = true;

	@Configure(file = SERVER, key = "MaximumOnlineUsers")
	public static int MAX_ONLINE_USERS = 30;

	@Configure(file = SERVER, key = "CacheMapFiles")
	public static boolean CACHE_MAP_FILES = false;

	@Configure(file = SERVER, key = "LoadV2MapFiles")
	public static boolean LOAD_V2_MAP_FILES = false;

	@Configure(file = SERVER, key = "CheckMoveInterval")
	public static boolean CHECK_MOVE_INTERVAL = false;

	@Configure(file = SERVER, key = "CheckAttackInterval")
	public static boolean CHECK_ATTACK_INTERVAL = false;

	@Configure(file = SERVER, key = "CheckSpellInterval")
	public static boolean CHECK_SPELL_INTERVAL = false;

	@Configure(file = SERVER, key = "InjusticeCount")
	public static int INJUSTICE_COUNT = 10;

	@Configure(file = SERVER, key = "JusticeCount")
	public static int JUSTICE_COUNT = 4;

	@Configure(file = SERVER, key = "CheckStrictness")
	public static int CHECK_STRICTNESS = 102;

	@Configure(file = SERVER, key = "PunishmentType")
	public static int PUNISHMENT_TYPE = 0;

	@Configure(file = SERVER, key = "PunishmentTime")
	public static int PUNISHMENT_TIME = 0;

	@Configure(file = SERVER, key = "PunishmentMap")
	public static int PUNISHMENT_MAP_ID = 0;

	@Configure(file = SERVER, key = "LoggingAccelerator")
	public static boolean LOGGING_ACCELERATOR = false;

	@Configure(file = SERVER, key = "LoggingWeaponEnchant")
	public static int LOGGING_WEAPON_ENCHANT = 0;

	@Configure(file = SERVER, key = "LoggingArmorEnchant")
	public static int LOGGING_ARMOR_ENCHANT = 0;

	@Configure(file = SERVER, key = "LoggingAccessoryEnchant")
	public static int LOGGING_ACCESSORY_ENCHANT = 0;

	@Configure(file = SERVER, key = "AnnounceWeaponEnchant")
	public static int ANNOUNCE_WEAPON_ENCHANT = 0;

	@Configure(file = SERVER, key = "AnnounceArmorEnchant")
	public static int ANNOUNCE_ARMOR_ENCHANT = 0;

	@Configure(file = SERVER, key = "AnnounceAccessoryEnchant")
	public static int ANNOUNCE_ACCESSORY_ENCHANT = 0;

	@Configure(file = SERVER, key = "LoggingChatNormal")
	public static boolean LOGGING_CHAT_NORMAL = false;

	@Configure(file = SERVER, key = "LoggingChatWhisper")
	public static boolean LOGGING_CHAT_WHISPER = false;

	@Configure(file = SERVER, key = "LoggingChatShout")
	public static boolean LOGGING_CHAT_SHOUT = false;

	@Configure(file = SERVER, key = "LoggingChatWorld")
	public static boolean LOGGING_CHAT_WORLD = false;

	@Configure(file = SERVER, key = "LoggingChatClan")
	public static boolean LOGGING_CHAT_CLAN = false;

	@Configure(file = SERVER, key = "LoggingChatParty")
	public static boolean LOGGING_CHAT_PARTY = false;

	@Configure(file = SERVER, key = "LoggingChatCombined")
	public static boolean LOGGING_CHAT_COMBINED = false;

	@Configure(file = SERVER, key = "LoggingChatChatParty")
	public static boolean LOGGING_CHAT_CHAT_PARTY = false;

	@Configure(file = SERVER, key = "Autosave")
	public static boolean AUTOSAVE = false;
	
	@Configure(file = SERVER, key = "AutosaveInterval")
	public static int AUTOSAVE_INTERVAL = 1200;

	@Configure(file = SERVER, key = "AutosaveIntervalOfInventory")
	public static int AUTOSAVE_INTERVAL_INVENTORY = 3000;

	@Configure(file = SERVER, key = "SkillTimerImplType")
	public static int SKILLTIMER_IMPLTYPE = 1;

	@Configure(file = SERVER, key = "NpcAIImplType")
	public static int NPCAI_IMPLTYPE = 1;

	@Configure(file = SERVER, key = "TelnetServer")
	public static boolean TELNET_SERVER = false;

	@Configure(file = SERVER, key = "TelnetServerPort")
	public static int TELNET_SERVER_PORT = 23;

	@Configure(file = SERVER, key = "PcRecognizeRange")
	public static int PC_RECOGNIZE_RANGE = 20;

	@Configure(file = SERVER, key = "CharacterConfigInServerSide")
	public static boolean CHARACTER_CONFIG_IN_SERVER_SIDE = true;

	@Configure(file = SERVER, key = "Allow2PC")
	public static boolean ALLOW_2PC = true;

	@Configure(file = SERVER, key = "LevelDownRange")
	public static int LEVEL_DOWN_RANGE = 0;

	@Configure(file = SERVER, key = "SendPacketBeforeTeleport")
	public static boolean SEND_PACKET_BEFORE_TELEPORT = false;
	
	@Configure(file = SERVER, key = "AnnouncementsCycleTime")
	public static int ANNOUNCEMENTS_CYCLE_TIME = 10;
	
	@Configure(file = SERVER, key = "AnnouncmenntsTimeDisplay")
	public static boolean ANNOUNCEMENTS_TIME_DISPALY = true;
	
	@Configure(file = SERVER, key = "AnnouncmenntsTimeFormat")
	public static String ANNOUNCEMENTS_TIME_FORMAT = "YYYY.MM.DD";
	
	@Configure(file = SERVER, key = "AutoShutdown")
	public static boolean AUTO_SHUTDOWN = false;

	@Configure(file = SERVER, key = "ShutdownRequestMax")
	public static int SHUTDOWN_REQUEST_MAX = 0;
	
	@Configure(file = SERVER, key = "ShutdownDelay")
	public static int SHUTDOWN_DELAY = 300;
	
	/** Rate control */
	@Configure(file = RATE, key = "RateXp")
	public static double RATE_XP = 1.0;

	@Configure(file = RATE, key = "RateLawful")
	public static double RATE_LA = 1.0;

	@Configure(file = RATE, key = "RateKarma")
	public static double RATE_KARMA = 1.0;

	@Configure(file = RATE, key = "RateDropAdena")
	public static double RATE_DROP_ADENA = 1.0;

	@Configure(file = RATE, key = "RateDropItems")
	public static double RATE_DROP_ITEMS = 1.0;

	@Configure(file = RATE, key = "RateFishingExp")
	public static int RATE_FISHING_EXP = 200;

	@Configure(file = RATE, key = "EnchantChanceWeapon")
	public static int ENCHANT_CHANCE_WEAPON = 68;

	@Configure(file = RATE, key = "EnchantChanceArmor")
	public static int ENCHANT_CHANCE_ARMOR = 52;

	@Configure(file = RATE, key = "EnchantChanceAccessory")
	public static int ENCHANT_CHANCE_ACCESSORY = 52;

	@Configure(file = RATE, key = "AttrEnchantChance")
	public static int ATTR_ENCHANT_CHANCE = 10;

	@Configure(file = RATE, key = "EnchantChanceDoll")
	public static int ENCHANT_CHANCE_DOLL = 52;

	@Configure(file = RATE, key = "RateWeightLimit")
	public static double RATE_WEIGHT_LIMIT = 1;

	@Configure(file = RATE, key = "RateWeightLimitforPet")
	public static double RATE_WEIGHT_LIMIT_PET = 1;

	@Configure(file = RATE, key = "RateShopSellingPrice")
	public static double RATE_SHOP_SELLING_PRICE = 1.0;

	@Configure(file = RATE, key = "RateShopPurchasingPrice")
	public static double RATE_SHOP_PURCHASING_PRICE = 1.0;

	@Configure(file = RATE, key = "CreateChanceAncientAmulet")
	public static int CREATE_CHANCE_ANCIENT_AMULET = 90;

	@Configure(file = RATE, key = "CreateChanceHistoryBook")
	public static int CREATE_CHANCE_HISTORY_BOOK = 50;

	@Configure(file = RATE, key = "CreateChanceMagicEyeOfBirth")
	public static int CREATE_CHANCE_MAGIC_EYE_OF_BIRTH = 82;

	@Configure(file = RATE, key = "CreateChanceMagicEyeOfShape")
	public static int CREATE_CHANCE_MAGIC_EYE_OF_SHAPE = 82;

	@Configure(file = RATE, key = "CreateChanceMagicEyeOfLife")
	public static int CREATE_CHANCE_MAGIC_EYE_OF_LIFE = 82;

	@Configure(file = RATE, key = "RateDropUniqueItems")
	public static int RATE_DROP_UNIQUE_ITEMS = 0;

	@Configure(file = RATE, key = "RateMakeUniqueItems")
	public static int RATE_MAKE_UNIQUE_ITEMS = 0;

	@Configure(file = RATE, key = "UniqueMaxOptions")
	public static int UNIQUE_MAX_OPTIONS = 3;
	
	@Configure(file = RATE, key = "UniqueMaxAc")
	public static int UNIQUE_MAX_AC = 3;

	@Configure(file = RATE, key = "UniqueMaxStr")
	public static int UNIQUE_MAX_STR = 3;

	@Configure(file = RATE, key = "UniqueMaxCon")
	public static int UNIQUE_MAX_CON = 3;

	@Configure(file = RATE, key = "UniqueMaxDex")
	public static int UNIQUE_MAX_DEX = 3;

	@Configure(file = RATE, key = "UniqueMaxWis")
	public static int UNIQUE_MAX_WIS = 3;

	@Configure(file = RATE, key = "UniqueMaxInt")
	public static int UNIQUE_MAX_INT = 3;

	@Configure(file = RATE, key = "UniqueMaxCha")
	public static int UNIQUE_MAX_CHA = 3;

	@Configure(file = RATE, key = "UniqueMaxHp")
	public static int UNIQUE_MAX_HP = 50;

	@Configure(file = RATE, key = "UniqueMaxHpr")
	public static int UNIQUE_MAX_HPR = 3;

	@Configure(file = RATE, key = "UniqueMaxMp")
	public static int UNIQUE_MAX_MP = 50;

	@Configure(file = RATE, key = "UniqueMaxMpr")
	public static int UNIQUE_MAX_MPR = 3;

	@Configure(file = RATE, key = "UniqueMaxSp")
	public static int UNIQUE_MAX_SP = 3;

	@Configure(file = RATE, key = "UniqueMaxMr")
	public static int UNIQUE_MAX_MR = 10;

	@Configure(file = RATE, key = "UniqueMaxHitModifier")
	public static int UNIQUE_MAX_HIT_MODIFIER = 5;

	@Configure(file = RATE, key = "UniqueMaxDmgModifier")
	public static int UNIQUE_MAX_DMG_MODIFIER = 5;

	@Configure(file = RATE, key = "UniqueMaxBowHitModifier")
	public static int UNIQUE_MAX_BOW_HIT_MODIFIER = 5;

	@Configure(file = RATE, key = "UniqueMaxBowDmgModifier")
	public static int UNIQUE_MAX_BOW_DMG_MODIFIER = 5;

	@Configure(file = RATE, key = "UniqueMaxDefenseEarth")
	public static int UNIQUE_MAX_DEFENSE_EARTH = 10;

	@Configure(file = RATE, key = "UniqueMaxDefenseWater")
	public static int UNIQUE_MAX_DEFENSE_WATER = 10;

	@Configure(file = RATE, key = "UniqueMaxDefenseFire")
	public static int UNIQUE_MAX_DEFENSE_FIRE = 10;

	@Configure(file = RATE, key = "UniqueMaxDefenseWind")
	public static int UNIQUE_MAX_DEFENSE_WIND = 10;

	@Configure(file = RATE, key = "UniqueMaxResistStun")
	public static int UNIQUE_MAX_RESIST_STUN = 10;

	@Configure(file = RATE, key = "UniqueMaxResistStone")
	public static int UNIQUE_MAX_RESIST_STONE = 10;

	@Configure(file = RATE, key = "UniqueMaxResistSleep")
	public static int UNIQUE_MAX_RESIST_SLEEP = 10;

	@Configure(file = RATE, key = "UniqueMaxResistFreeze")
	public static int UNIQUE_MAX_RESIST_FREEZE = 10;

	@Configure(file = RATE, key = "UniqueMaxResistHold")
	public static int UNIQUE_MAX_RESIST_HOLD = 10;

	@Configure(file = RATE, key = "UniqueMaxResistBlind")
	public static int UNIQUE_MAX_RESIST_BLIND = 10;

	@Configure(file = RATE, key = "UniqueMaxExpBonus")
	public static int UNIQUE_MAX_EXP_BONUS = 10;

	@Configure(file = RATE, key = "UniqueHaste")
	public static boolean UNIQUE_HASTE = true;
	
	@Configure(file = RATE, key = "UniqueCanDmg")
	public static boolean UNIQUE_CAN_DMG = true;
	
	@Configure(file = RATE, key = "UniquePrefix")
	public static String UNIQUE_PREFIX = "[HQ]";
	
	/** AltSettings control */
	@Configure(file = ALT, key = "GlobalChatLevel")
	public static int GLOBAL_CHAT_LEVEL = 30;

	@Configure(file = ALT, key = "WhisperChatLevel")
	public static int WHISPER_CHAT_LEVEL = 5;

	@Configure(file = ALT, key = "AutoLoot")
	public static int AUTO_LOOT = 2;

	@Configure(file = ALT, key = "LootingRange")
	public static int LOOTING_RANGE = 3;

	@Configure(file = ALT, key = "NonPvP")
	public static boolean ALT_NONPVP = true;

	@Configure(file = ALT, key = "ChangeTitleByOneself")
	public static boolean CHANGE_TITLE_BY_ONESELF = false;

	@Configure(file = ALT, key = "MaxClanMember")
	public static int MAX_CLAN_MEMBER = 0;

	@Configure(file = ALT, key = "ClanAlliance")
	public static boolean CLAN_ALLIANCE = true;

	@Configure(file = ALT, key = "MaxPT")
	public static int MAX_PT = 8;

	@Configure(file = ALT, key = "MaxChatPT")
	public static int MAX_CHAT_PT = 8;

	@Configure(file = ALT, key = "SimWarPenalty")
	public static boolean SIM_WAR_PENALTY = true;

	@Configure(file = ALT, key = "StartMapId")
	public static int START_MAP_ID = 2005;
	
	@Configure(file = ALT, key = "GetBack")
	public static boolean GET_BACK = false;

	@Configure(file = ALT, key = "ItemDeletionType")
	public static String ALT_ITEM_DELETION_TYPE = "auto";

	@Configure(file = ALT, key = "ItemDeletionTime")
	public static int ALT_ITEM_DELETION_TIME = 10;

	@Configure(file = ALT, key = "ItemDeletionRange")
	public static int ALT_ITEM_DELETION_RANGE = 5;

	@Configure(file = ALT, key = "GMshop")
	public static boolean ALT_GMSHOP = false;

	@Configure(file = ALT, key = "GMshopMinID")
	public static int ALT_GMSHOP_MIN_ID = 0;

	@Configure(file = ALT, key = "GMshopMaxID")
	public static int ALT_GMSHOP_MAX_ID = 0;

	@Configure(file = ALT, key = "WhoCommand")
	public static boolean ALT_WHO_COMMAND = false;

	@Configure(file = ALT, key = "RevivalPotion")
	public static boolean ALT_REVIVAL_POTION = false;

	@Configure(file = ALT, key = "WarTime", loader = WarTimeLoader.class)
	public static int ALT_WAR_TIME = 2;

	@Configure(file = ALT, key = "WarTime", loader = WarTimeUnitLoader.class)
	public static int ALT_WAR_TIME_UNIT = Calendar.HOUR_OF_DAY;

	@Configure(file = ALT, key = "WarInterval", loader = WarTimeLoader.class)
	public static int ALT_WAR_INTERVAL = 4;

	@Configure(file = ALT, key = "WarInterval", loader = WarTimeUnitLoader.class)
	public static int ALT_WAR_INTERVAL_UNIT = Calendar.DATE;

	@Configure(file = ALT, key = "WeatherSystem")
	public static boolean WEATHER_SYSTEM = false;
	
	@Configure(file = ALT, key = "WeatherInterval")
	public static int WEATHER_INTERVAL = 3600;
	
	@Configure(file = ALT, key = "SpawnHomePoint")
	public static boolean SPAWN_HOME_POINT = true;

	@Configure(file = ALT, key = "SpawnHomePointRange")
	public static int SPAWN_HOME_POINT_RANGE = 8;

	@Configure(file = ALT, key = "SpawnHomePointCount")
	public static int SPAWN_HOME_POINT_COUNT = 2;

	@Configure(file = ALT, key = "SpawnHomePointDelay")
	public static int SPAWN_HOME_POINT_DELAY = 100;

	@Configure(file = ALT, key = "InitBossSpawn")
	public static boolean INIT_BOSS_SPAWN = true;

	@Configure(file = ALT, key = "ElementalStoneAmount")
	public static int ELEMENTAL_STONE_AMOUNT = 300;

	@Configure(file = ALT, key = "HouseTaxInterval")
	public static int HOUSE_TAX_INTERVAL = 10;

	@Configure(file = ALT, key = "MaxDollCount")
	public static int MAX_DOLL_COUNT = 1;

	@Configure(file = ALT, key = "ReturnToNature")
	public static boolean RETURN_TO_NATURE = false;

	@Configure(file = ALT, key = "MaxNpcItem")
	public static int MAX_NPC_ITEM = 8;

	@Configure(file = ALT, key = "MaxPersonalWarehouseItem")
	public static int MAX_PERSONAL_WAREHOUSE_ITEM = 100;

	@Configure(file = ALT, key = "MaxAdditionalWarehouseItem")
	public static int MAX_ADDITIONAL_WAREHOUSE_ITEM = 20;

	@Configure(file = ALT, key = "MaxClanWarehouseItem")
	public static int MAX_CLAN_WAREHOUSE_ITEM = 200;

	@Configure(file = ALT, key = "MaxClanWarehouseHistoryDays")
	public static int MAX_CLAN_WAREHOUSE_HISTORY_DAYS = 3;

	@Configure(file = ALT, key = "DeleteCharacterAfter7Days")
	public static boolean DELETE_CHARACTER_AFTER_7DAYS = true;

	@Configure(file = ALT, key = "NpcDeletionTime")
	public static int NPC_DELETION_TIME = 10;

	@Configure(file = ALT, key = "DefaultCharacterSlot")
	public static int DEFAULT_CHARACTER_SLOT = 6;

	@Configure(file = ALT, key = "PkLog")
	public static boolean PK_LOG = false;

	@Configure(file = ALT, key = "BossSpawnLog")
	public static boolean BOSS_SPAWN_LOG = false;

	@Configure(file = ALT, key = "BossEndLog")
	public static boolean BOSS_END_LOG = false;

	@Configure(file = ALT, key = "Hell")
	public static boolean HELL = false;

	@Configure(file = ALT, key = "PkCountLimit")
	public static int PK_COUNT_LIMIT = 10;

	@Configure(file = ALT, key = "RenamePetName")
	public static boolean RENAME_PET_NAME = false;
	
	@Configure(file = ALT, key = "PetRaceMinPlayer")
	public static int PET_RACE_MIN_PLAYER = 1;

	@Configure(file = ALT, key = "PetRaceMaxLap")
	public static int PET_RACE_MAX_LAP = 3;

	@Configure(file = ALT, key = "DeathMatchMinPlayer")
	public static int DEATH_MATCH_MIN_PLAYER = 1;

	@Configure(file = ALT, key = "AccessoryEnchantBonus")
	public static String ACCESSORY_ENCHANT_BONUS = "std";
	
	@Configure(file = ALT, key = "RecycleSystem")
	public static boolean RECYCLE_SYSTEM = false;

	/** Events control */
	@Configure(file = EVENT, key = "HinomotoUpdate1")
	public static boolean HINOMOTO_UPDATE_1 = true;

	@Configure(file = EVENT, key = "GiveExpLevel")
	public static int GIVE_EXP_LEVEL = 0;

	@Configure(file = EVENT, key = "RisingSunEvent")
	public static boolean RISING_SUN_EVENT = true;

	@Configure(file = EVENT, key = "HalloweenEvent")
	public static boolean HALLOWEEN_EVENT = true;

	@Configure(file = EVENT, key = "JpPrivileged")
	public static boolean JP_PRIVILEGED = false;

	@Configure(file = EVENT, key = "TalkingScrollQuest")
	public static boolean TALKING_SCROLL_QUEST = false;

	/** CharSettings control */
	@Configure(file = CHARACTER, key = "PrinceMaxHP")
	public static int PRINCE_MAX_HP = 1000;

	@Configure(file = CHARACTER, key = "PrinceMaxMP")
	public static int PRINCE_MAX_MP = 800;

	@Configure(file = CHARACTER, key = "KnightMaxHP")
	public static int KNIGHT_MAX_HP = 1400;

	@Configure(file = CHARACTER, key = "KnightMaxMP")
	public static int KNIGHT_MAX_MP = 600;

	@Configure(file = CHARACTER, key = "ElfMaxHP")
	public static int ELF_MAX_HP = 1000;

	@Configure(file = CHARACTER, key = "ElfMaxMP")
	public static int ELF_MAX_MP = 900;

	@Configure(file = CHARACTER, key = "WizardMaxHP")
	public static int WIZARD_MAX_HP = 800;

	@Configure(file = CHARACTER, key = "WizardMaxMP")
	public static int WIZARD_MAX_MP = 1200;

	@Configure(file = CHARACTER, key = "DarkelfMaxHP")
	public static int DARKELF_MAX_HP = 1000;

	@Configure(file = CHARACTER, key = "DarkelfMaxMP")
	public static int DARKELF_MAX_MP = 900;

	@Configure(file = CHARACTER, key = "DragonKnightMaxHP")
	public static int DRAGONKNIGHT_MAX_HP = 1400;

	@Configure(file = CHARACTER, key = "DragonKnightMaxMP")
	public static int DRAGONKNIGHT_MAX_MP = 600;

	@Configure(file = CHARACTER, key = "IllusionistMaxHP")
	public static int ILLUSIONIST_MAX_HP = 900;

	@Configure(file = CHARACTER, key = "IllusionistMaxMP")
	public static int ILLUSIONIST_MAX_MP = 1100;
	
	@Configure(file = CHARACTER, key = "PrinceLearnMagicMaxClass")
	public static int PRINCE_LEARN_MAGIC_MAX_CLASS = 2;

	@Configure(file = CHARACTER, key = "PrinceLearnMagicPerLevel")
	public static int PRINCE_LEARN_MAGIC_PER_LEVEL = 10;
	
	@Configure(file = CHARACTER, key = "KnightLearnMagicMaxClass")
	public static int KNIGHT_LEARN_MAGIC_MAX_CLASS = 1;

	@Configure(file = CHARACTER, key = "KnightLearnMagicPerLevel")
	public static int KNIGHT_LEARN_MAGIC_PER_LEVEL = 50;

	@Configure(file = CHARACTER, key = "ElfLearnMagicMaxClass")
	public static int ELF_LEARN_MAGIC_MAX_CLASS = 6;

	@Configure(file = CHARACTER, key = "ElfLearnMagicPerLevel")
	public static int ELF_LEARN_MAGIC_PER_LEVEL = 8;
	
	@Configure(file = CHARACTER, key = "WizardLearnMagicMaxClass")
	public static int WIZARD_LEARN_MAGIC_MAX_CLASS = 10;

	@Configure(file = CHARACTER, key = "WizardLearnMagicPerLevel")
	public static int WIZARD_LEARN_MAGIC_PER_LEVEL = 4;
	
	@Configure(file = CHARACTER, key = "DarkElfLearnMagicMaxClass")
	public static int DARKELF_LEARN_MAGIC_MAX_CLASS = 2;

	@Configure(file = CHARACTER, key = "DarkElfLearnMagicPerLevel")
	public static int DARKELF_LEARN_MAGIC_PER_LEVEL = 12;
	
	@Configure(file = CHARACTER, key = "DragonKnightLearnMagicMaxClass")
	public static int DRAGONKNIGHT_LEARN_MAGIC_MAX_CLASS = 0;

	@Configure(file = CHARACTER, key = "DragonKnightLearnMagicPerLevel")
	public static int DRAGONKNIGHT_LEARN_MAGIC_PER_LEVEL = 0;
	
	@Configure(file = CHARACTER, key = "IllusionistLearnMagicMaxClass")
	public static int ILLUSIONIST_LEARN_MAGIC_MAX_CLASS = 0;

	@Configure(file = CHARACTER, key = "IllusionistLearnMagicPerLevel")
	public static int ILLUSIONIST_LEARN_MAGIC_PER_LEVEL = 0;

	@Configure(file = CHARACTER, key = "LearnAllElfSkills")
	public static boolean LEARN_ALL_ELF_SKILLS = false;

	@Configure(file = CHARACTER, key = "ElixirMaxUse")
	public static int ELIXIR_MAX_USE = 5;

	@Configure(file = CHARACTER, key = "ElixirMaxStr")
	public static int ELIXIR_MAX_STR = 35;

	@Configure(file = CHARACTER, key = "ElixirMaxCon")
	public static int ELIXIR_MAX_CON = 35;

	@Configure(file = CHARACTER, key = "ElixirMaxDex")
	public static int ELIXIR_MAX_DEX = 35;

	@Configure(file = CHARACTER, key = "ElixirMaxInt")
	public static int ELIXIR_MAX_INT = 35;

	@Configure(file = CHARACTER, key = "ElixirMaxWis")
	public static int ELIXIR_MAX_WIS = 35;

	@Configure(file = CHARACTER, key = "ElixirMaxCha")
	public static int ELIXIR_MAX_CHA = 35;

	@Configure(file = CHARACTER, key = "Lv50Exp")
	public static int LV50_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv51Exp")
	public static int LV51_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv52Exp")
	public static int LV52_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv53Exp")
	public static int LV53_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv54Exp")
	public static int LV54_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv55Exp")
	public static int LV55_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv56Exp")
	public static int LV56_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv57Exp")
	public static int LV57_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv58Exp")
	public static int LV58_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv59Exp")
	public static int LV59_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv60Exp")
	public static int LV60_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv61Exp")
	public static int LV61_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv62Exp")
	public static int LV62_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv63Exp")
	public static int LV63_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv64Exp")
	public static int LV64_EXP = 1;

	@Configure(file = CHARACTER, key = "Lv65Exp")
	public static int LV65_EXP = 2;

	@Configure(file = CHARACTER, key = "Lv66Exp")
	public static int LV66_EXP = 2;

	@Configure(file = CHARACTER, key = "Lv67Exp")
	public static int LV67_EXP = 2;

	@Configure(file = CHARACTER, key = "Lv68Exp")
	public static int LV68_EXP = 2;

	@Configure(file = CHARACTER, key = "Lv69Exp")
	public static int LV69_EXP = 2;

	@Configure(file = CHARACTER, key = "Lv70Exp")
	public static int LV70_EXP = 4;

	@Configure(file = CHARACTER, key = "Lv71Exp")
	public static int LV71_EXP = 4;

	@Configure(file = CHARACTER, key = "Lv72Exp")
	public static int LV72_EXP = 4;

	@Configure(file = CHARACTER, key = "Lv73Exp")
	public static int LV73_EXP = 4;

	@Configure(file = CHARACTER, key = "Lv74Exp")
	public static int LV74_EXP = 4;

	@Configure(file = CHARACTER, key = "Lv75Exp")
	public static int LV75_EXP = 8;

	@Configure(file = CHARACTER, key = "Lv76Exp")
	public static int LV76_EXP = 8;

	@Configure(file = CHARACTER, key = "Lv77Exp")
	public static int LV77_EXP = 8;

	@Configure(file = CHARACTER, key = "Lv78Exp")
	public static int LV78_EXP = 8;

	@Configure(file = CHARACTER, key = "Lv79Exp")
	public static int LV79_EXP = 16;

	@Configure(file = CHARACTER, key = "Lv80Exp")
	public static int LV80_EXP = 32;

	@Configure(file = CHARACTER, key = "Lv81Exp")
	public static int LV81_EXP = 64;

	@Configure(file = CHARACTER, key = "Lv82Exp")
	public static int LV82_EXP = 128;

	@Configure(file = CHARACTER, key = "Lv83Exp")
	public static int LV83_EXP = 256;

	@Configure(file = CHARACTER, key = "Lv84Exp")
	public static int LV84_EXP = 512;

	@Configure(file = CHARACTER, key = "Lv85Exp")
	public static int LV85_EXP = 1024;

	@Configure(file = CHARACTER, key = "Lv86Exp")
	public static int LV86_EXP = 2048;

	@Configure(file = CHARACTER, key = "Lv87Exp")
	public static int LV87_EXP = 4096;

	@Configure(file = CHARACTER, key = "Lv88Exp")
	public static int LV88_EXP = 8192;

	@Configure(file = CHARACTER, key = "Lv89Exp")
	public static int LV89_EXP = 16384;

	@Configure(file = CHARACTER, key = "Lv90Exp")
	public static int LV90_EXP = 32768;

	@Configure(file = CHARACTER, key = "Lv91Exp")
	public static int LV91_EXP = 65536;

	@Configure(file = CHARACTER, key = "Lv92Exp")
	public static int LV92_EXP = 131072;

	@Configure(file = CHARACTER, key = "Lv93Exp")
	public static int LV93_EXP = 262144;

	@Configure(file = CHARACTER, key = "Lv94Exp")
	public static int LV94_EXP = 524288;

	@Configure(file = CHARACTER, key = "Lv95Exp")
	public static int LV95_EXP = 1048576;

	@Configure(file = CHARACTER, key = "Lv96Exp")
	public static int LV96_EXP = 2097152;

	@Configure(file = CHARACTER, key = "Lv97Exp")
	public static int LV97_EXP = 4194304;

	@Configure(file = CHARACTER, key = "Lv98Exp")
	public static int LV98_EXP = 8388608;

	@Configure(file = CHARACTER, key = "Lv99Exp")
	public static int LV99_EXP = 16777216;

	@Configure(file = CHARACTER, key = "BlessOfAinExpBonus")
	public static int BLESS_OF_AIN_EXP_BONUS = 77;
	
	@Configure(file = CHARACTER, key = "Lv1ExpBonus")
	public static int LV1_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv2ExpBonus")
	public static int LV2_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv3ExpBonus")
	public static int LV3_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv4ExpBonus")
	public static int LV4_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv5ExpBonus")
	public static int LV5_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv6ExpBonus")
	public static int LV6_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv7ExpBonus")
	public static int LV7_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv8ExpBonus")
	public static int LV8_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv9ExpBonus")
	public static int LV9_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv10ExpBonus")
	public static int LV10_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv11ExpBonus")
	public static int LV11_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv12ExpBonus")
	public static int LV12_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv13ExpBonus")
	public static int LV13_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv14ExpBonus")
	public static int LV14_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv15ExpBonus")
	public static int LV15_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv16ExpBonus")
	public static int LV16_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv17ExpBonus")
	public static int LV17_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv18ExpBonus")
	public static int LV18_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv19ExpBonus")
	public static int LV19_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv20ExpBonus")
	public static int LV20_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv21ExpBonus")
	public static int LV21_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv22ExpBonus")
	public static int LV22_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv23ExpBonus")
	public static int LV23_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv24ExpBonus")
	public static int LV24_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv25ExpBonus")
	public static int LV25_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv26ExpBonus")
	public static int LV26_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv27ExpBonus")
	public static int LV27_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv28ExpBonus")
	public static int LV28_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv29ExpBonus")
	public static int LV29_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv30ExpBonus")
	public static int LV30_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv31ExpBonus")
	public static int LV31_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv32ExpBonus")
	public static int LV32_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv33ExpBonus")
	public static int LV33_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv34ExpBonus")
	public static int LV34_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv35ExpBonus")
	public static int LV35_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv36ExpBonus")
	public static int LV36_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv37ExpBonus")
	public static int LV37_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv38ExpBonus")
	public static int LV38_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv39ExpBonus")
	public static int LV39_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv40ExpBonus")
	public static int LV40_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv41ExpBonus")
	public static int LV41_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv42ExpBonus")
	public static int LV42_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv43ExpBonus")
	public static int LV43_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv44ExpBonus")
	public static int LV44_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv45ExpBonus")
	public static int LV45_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv46ExpBonus")
	public static int LV46_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv47ExpBonus")
	public static int LV47_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv48ExpBonus")
	public static int LV48_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv49ExpBonus")
	public static int LV49_EXP_BONUS = 15;

	@Configure(file = CHARACTER, key = "Lv50ExpBonus")
	public static int LV50_EXP_BONUS = 14;

	@Configure(file = CHARACTER, key = "Lv51ExpBonus")
	public static int LV51_EXP_BONUS = 13;

	@Configure(file = CHARACTER, key = "Lv52ExpBonus")
	public static int LV52_EXP_BONUS = 12;

	@Configure(file = CHARACTER, key = "Lv53ExpBonus")
	public static int LV53_EXP_BONUS = 11;

	@Configure(file = CHARACTER, key = "Lv54ExpBonus")
	public static int LV54_EXP_BONUS = 10;

	@Configure(file = CHARACTER, key = "Lv55ExpBonus")
	public static int LV55_EXP_BONUS = 9;

	@Configure(file = CHARACTER, key = "Lv56ExpBonus")
	public static int LV56_EXP_BONUS = 8;

	@Configure(file = CHARACTER, key = "Lv57ExpBonus")
	public static int LV57_EXP_BONUS = 7;

	@Configure(file = CHARACTER, key = "Lv58ExpBonus")
	public static int LV58_EXP_BONUS = 6;

	@Configure(file = CHARACTER, key = "Lv59ExpBonus")
	public static int LV59_EXP_BONUS = 5;

	@Configure(file = CHARACTER, key = "Lv60ExpBonus")
	public static int LV60_EXP_BONUS = 4;

	@Configure(file = CHARACTER, key = "Lv61ExpBonus")
	public static int LV61_EXP_BONUS = 3;

	@Configure(file = CHARACTER, key = "Lv62ExpBonus")
	public static int LV62_EXP_BONUS = 2;

	@Configure(file = CHARACTER, key = "Lv63ExpBonus")
	public static int LV63_EXP_BONUS = 1;

	@Configure(file = CHARACTER, key = "Lv64ExpBonus")
	public static int LV64_EXP_BONUS = 1;

	@Configure(file = CHARACTER, key = "Lv65ExpBonus")
	public static int LV65_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv66ExpBonus")
	public static int LV66_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv67ExpBonus")
	public static int LV67_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv68ExpBonus")
	public static int LV68_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv69ExpBonus")
	public static int LV69_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv70ExpBonus")
	public static int LV70_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv71ExpBonus")
	public static int LV71_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv72ExpBonus")
	public static int LV72_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv73ExpBonus")
	public static int LV73_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv74ExpBonus")
	public static int LV74_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv75ExpBonus")
	public static int LV75_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv76ExpBonus")
	public static int LV76_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv77ExpBonus")
	public static int LV77_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv78ExpBonus")
	public static int LV78_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv79ExpBonus")
	public static int LV79_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv80ExpBonus")
	public static int LV80_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv81ExpBonus")
	public static int LV81_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv82ExpBonus")
	public static int LV82_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv83ExpBonus")
	public static int LV83_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv84ExpBonus")
	public static int LV84_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv85ExpBonus")
	public static int LV85_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv86ExpBonus")
	public static int LV86_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv87ExpBonus")
	public static int LV87_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv88ExpBonus")
	public static int LV88_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv89ExpBonus")
	public static int LV89_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv90ExpBonus")
	public static int LV90_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv91ExpBonus")
	public static int LV91_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv92ExpBonus")
	public static int LV92_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv93ExpBonus")
	public static int LV93_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv94ExpBonus")
	public static int LV94_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv95ExpBonus")
	public static int LV95_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv96ExpBonus")
	public static int LV96_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv97ExpBonus")
	public static int LV97_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv98ExpBonus")
	public static int LV98_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv99ExpBonus")
	public static int LV99_EXP_BONUS = 0;

	@Configure(file = CHARACTER, key = "Lv1DeathPenalty")
	public static int LV1_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv2DeathPenalty")
	public static int LV2_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv3DeathPenalty")
	public static int LV3_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv4DeathPenalty")
	public static int LV4_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv5DeathPenalty")
	public static int LV5_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv6DeathPenalty")
	public static int LV6_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv7DeathPenalty")
	public static int LV7_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv8DeathPenalty")
	public static int LV8_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv9DeathPenalty")
	public static int LV9_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv10DeathPenalty")
	public static int LV10_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv11DeathPenalty")
	public static int LV11_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv12DeathPenalty")
	public static int LV12_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv13DeathPenalty")
	public static int LV13_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv14DeathPenalty")
	public static int LV14_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv15DeathPenalty")
	public static int LV15_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv16DeathPenalty")
	public static int LV16_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv17DeathPenalty")
	public static int LV17_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv18DeathPenalty")
	public static int LV18_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv19DeathPenalty")
	public static int LV19_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv20DeathPenalty")
	public static int LV20_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv21DeathPenalty")
	public static int LV21_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv22DeathPenalty")
	public static int LV22_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv23DeathPenalty")
	public static int LV23_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv24DeathPenalty")
	public static int LV24_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv25DeathPenalty")
	public static int LV25_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv26DeathPenalty")
	public static int LV26_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv27DeathPenalty")
	public static int LV27_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv28DeathPenalty")
	public static int LV28_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv29DeathPenalty")
	public static int LV29_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv30DeathPenalty")
	public static int LV30_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv31DeathPenalty")
	public static int LV31_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv32DeathPenalty")
	public static int LV32_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv33DeathPenalty")
	public static int LV33_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv34DeathPenalty")
	public static int LV34_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv35DeathPenalty")
	public static int LV35_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv36DeathPenalty")
	public static int LV36_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv37DeathPenalty")
	public static int LV37_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv38DeathPenalty")
	public static int LV38_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv39DeathPenalty")
	public static int LV39_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv40DeathPenalty")
	public static int LV40_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv41DeathPenalty")
	public static int LV41_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv42DeathPenalty")
	public static int LV42_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv43DeathPenalty")
	public static int LV43_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv44DeathPenalty")
	public static int LV44_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv45DeathPenalty")
	public static int LV45_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv46DeathPenalty")
	public static int LV46_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv47DeathPenalty")
	public static int LV47_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv48DeathPenalty")
	public static int LV48_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv49DeathPenalty")
	public static int LV49_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv50DeathPenalty")
	public static int LV50_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv51DeathPenalty")
	public static int LV51_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv52DeathPenalty")
	public static int LV52_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv53DeathPenalty")
	public static int LV53_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv54DeathPenalty")
	public static int LV54_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv55DeathPenalty")
	public static int LV55_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv56DeathPenalty")
	public static int LV56_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv57DeathPenalty")
	public static int LV57_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv58DeathPenalty")
	public static int LV58_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv59DeathPenalty")
	public static int LV59_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv60DeathPenalty")
	public static int LV60_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv61DeathPenalty")
	public static int LV61_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv62DeathPenalty")
	public static int LV62_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv63DeathPenalty")
	public static int LV63_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv64DeathPenalty")
	public static int LV64_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv65DeathPenalty")
	public static int LV65_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv66DeathPenalty")
	public static int LV66_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv67DeathPenalty")
	public static int LV67_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv68DeathPenalty")
	public static int LV68_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv69DeathPenalty")
	public static int LV69_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv70DeathPenalty")
	public static int LV70_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv71DeathPenalty")
	public static int LV71_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv72DeathPenalty")
	public static int LV72_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv73DeathPenalty")
	public static int LV73_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv74DeathPenalty")
	public static int LV74_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv75DeathPenalty")
	public static int LV75_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv76DeathPenalty")
	public static int LV76_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv77DeathPenalty")
	public static int LV77_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv78DeathPenalty")
	public static int LV78_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv79DeathPenalty")
	public static int LV79_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv80DeathPenalty")
	public static int LV80_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv81DeathPenalty")
	public static int LV81_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv82DeathPenalty")
	public static int LV82_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv83DeathPenalty")
	public static int LV83_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv84DeathPenalty")
	public static int LV84_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv85DeathPenalty")
	public static int LV85_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv86DeathPenalty")
	public static int LV86_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv87DeathPenalty")
	public static int LV87_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv88DeathPenalty")
	public static int LV88_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv89DeathPenalty")
	public static int LV89_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv90DeathPenalty")
	public static int LV90_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv91DeathPenalty")
	public static int LV91_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv92DeathPenalty")
	public static int LV92_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv93DeathPenalty")
	public static int LV93_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv94DeathPenalty")
	public static int LV94_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv95DeathPenalty")
	public static int LV95_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv96DeathPenalty")
	public static int LV96_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv97DeathPenalty")
	public static int LV97_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv98DeathPenalty")
	public static int LV98_DEATH_PENALTY = 100;

	@Configure(file = CHARACTER, key = "Lv99DeathPenalty")
	public static int LV99_DEATH_PENALTY = 100;

	@Configure(file = PACK, key = "Autoentication", isOptional = true)
	public static boolean LOGINS_TO_AUTOENTICATION = false;

	@Configure(file = PACK, key = "RSA_KEY_E", isOptional = true)
	public static String RSA_KEY_E = null;

	@Configure(file = PACK, key = "RSA_KEY_N", isOptional = true)
	public static String RSA_KEY_D = null;
	//

	/** その他の設定 */

	// NPCから吸えるMP限界
	public static final int MANA_DRAIN_LIMIT_PER_NPC = 40;

	// 一回の攻撃で吸えるMP限界(SOM、鋼鉄SOM）
	public static final int MANA_DRAIN_LIMIT_PER_SOM_ATTACK = 9;

	private static void validate() {
		if (!IntRange.includes(Config.ALT_ITEM_DELETION_RANGE, 0, 5)) {
			throw new IllegalStateException("ItemDeletionRangeの値が設定可能範囲外です。");
		}

		if (!IntRange.includes(Config.ALT_ITEM_DELETION_TIME, 1, 35791)) {
			throw new IllegalStateException("ItemDeletionTimeの値が設定可能範囲外です。");
		}
	}

	static void afterLoad() {
		CLIENT_LANGUAGE_CODE = LANGUAGE_CODE_ARRAY[CLIENT_LANGUAGE];
	}

	public static void load() {
		PerformanceTimer timer = new PerformanceTimer();
		new ConfigLoader().load(Config.class);
		afterLoad();
		validate();
		System.out.println("loading configs...OK! " + timer.elapsedTimeMillis() + "ms");
	}
		
	private Config() {

	}
}