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

package jp.l1j.locale;

import java.util.ResourceBundle;

public class I18N {
	
	private static XMLResourceBundleControl control = new XMLResourceBundleControl();
	private static ResourceBundle bundle = ResourceBundle.getBundle("./locale/lang", control);

	/*
	 * jp.l1j.server.GameServer.java
	 */
	public static final String I18N_MEMORY_USEAGE = bundle.getString("MemoryUsage");
	public static final String I18N_WAITING_FOR_CLIENT = bundle.getString("WaittingForClient");
	public static final String I18N_TRYING_TO_CONNECTION = bundle.getString("TryingToConnection");
	public static final String I18N_BANNED_IP = bundle.getString("BannedIP");
	public static final String I18N_GENERATE_SERVER_SOCKET = bundle.getString("GenerateServerSocket");
	public static final String I18N_SERVER_SETTINGS = bundle.getString("ServerSettings");
	public static final String I18N_EXP = bundle.getString("Exp");
	public static final String I18N_LAWFUL = bundle.getString("Lawful");
	public static final String I18N_KARMA = bundle.getString("Karma");
	public static final String I18N_ITEM_DROP = bundle.getString("ItemDrop");
	public static final String I18N_ADENA_DROP = bundle.getString("AdenaDrop");
	public static final String I18N_ENCHANT_WEAPON = bundle.getString("EnchantWeapon");
	public static final String I18N_ENCHANT_ARMOR = bundle.getString("EnchantArmor");
	public static final String I18N_ENCHANT_ATTRIBUTE = bundle.getString("EnchantAttribute");
	public static final String I18N_ENCHANT_ACCESSORY = bundle.getString("EnchantAccessory");
	public static final String I18N_ENCHANT_DOLL = bundle.getString("EnchantDoll");
	public static final String I18N_WEIGHT_REDUCTION = bundle.getString("WeightReduction");
	public static final String I18N_GLOBAL_CHAT = bundle.getString("GlobalChat");
	public static final String I18N_PVP = bundle.getString("PvP");
	public static final String I18N_NON_PVP = bundle.getString("NonPvP");
	public static final String I18N_MAX_USERS = bundle.getString("MaxUsers");
	public static final String I18N_LOADING_COMPLETE = bundle.getString("LoadingComplete");
	public static final String I18N_SHUTDOWN_THE_SERVER = bundle.getString("ShutdownTheServer");
	public static final String I18N_PLEASE_LOGOUT = bundle.getString("PleaseLogout");
	public static final String I18N_SHUTDOWN_AFTER_FEW_MINUTES = bundle.getString("ShutdownAfterFewMinutes");
	public static final String I18N_SHUTDOWN_AFTER_FEW_SECONDS = bundle.getString("ShutdownAfterFewSeconds");
	public static final String I18N_SHUTDOWN_ABORT = bundle.getString("ShutdownAbort");

	/*
	 * jp.l1j.server.ClientThread.java
	 */
	public static final String I18N_CONNECTED_TO_THE_SERVER = bundle.getString("ConnectedToTheServer");
	public static final String I18N_TERMINATED_THE_CONNECTION = bundle.getString("TerminatedTheConnection");
	public static final String I18N_KILLED_THE_CONNECTION = bundle.getString("KilledTheConnection");
	
	/*
	 * jp.l1j.server.command.GMCommands.java
	 */
	public static final String I18N_CANNOT_USE_THE_COMMAND = bundle.getString("CanNotUseTheCommand");
	public static final String I18N_USED_THE_COMMAND = bundle.getString("UsedTheCommand");
	public static final String I18N_DOES_NOT_EXIST_COMMAND = bundle.getString("DoesNotExistCommand");
	public static final String I18N_EXECUTE_THE_COMMAND = bundle.getString("ExecuteTheCommand");
	public static final String I18N_REEXECUTE_THE_COMMAND = bundle.getString("ReExecuteTheCommand");
	public static final String I18N_REEXECUTE_THE_COMMAND_ERROR = bundle.getString("ReExecuteTheCommandError");
	
	/*
	 * jp.l1j.server.command.GMCommandsConfig.java
	 */
	public static final String I18N_LOAD_FAILED = bundle.getString("LoadFailed");
	
	/*
	 * jp.l1j.server.command.L1Commands.java
	 */
	public static final String I18N_LOAD_FROM_THE_TABLE_FAILED = bundle.getString("LoadFromTheTableFailed");
		
	/*
	 * jp.l1j.server.command.executor.*
	 */
	public static final String I18N_COMMAND_FORMAT_1 = bundle.getString("CommandFormat1");
	public static final String I18N_COMMAND_FORMAT_2 = bundle.getString("CommandFormat2");
	public static final String I18N_COMMAND_FORMAT_3 = bundle.getString("CommandFormat3");
	public static final String I18N_COMMAND_FORMAT_4 = bundle.getString("CommandFormat4");
	public static final String I18N_COMMAND_FORMAT_5 = bundle.getString("CommandFormat5");
	public static final String I18N_COMMAND_FORMAT_ON = bundle.getString("CommandFormatOn");
	public static final String I18N_COMMAND_FORMAT_OFF = bundle.getString("CommandFormatOff");
	public static final String I18N_COMMAND_ERROR = bundle.getString("CommandError");
	public static final String I18N_ACT_ID = bundle.getString("ActID");
	public static final String I18N_AMOUNT = bundle.getString("Amount");
	public static final String I18N_AXIS_X = bundle.getString("AxisX");
	public static final String I18N_AXIS_Y = bundle.getString("AxisY");
	public static final String I18N_CHAR_NAME = bundle.getString("CharName");
	public static final String I18N_COMMAND_NAME = bundle.getString("CommandName");
	public static final String I18N_ENCHANT = bundle.getString("Enchant");
	public static final String I18N_GFX_ID = bundle.getString("GfxID");
	public static final String I18N_IDENTIFY = bundle.getString("Identify");
	public static final String I18N_INTERVAL = bundle.getString("Interval");
	public static final String I18N_ITEM_ID = bundle.getString("ItemID");
	public static final String I18N_ITEM_NAME = bundle.getString("ItemName");
	public static final String I18N_ITEM_SET_NAME = bundle.getString("ItemSetName");
	public static final String I18N_LEVEL = bundle.getString("Level");
	public static final String I18N_MAX_LEVEL = bundle.getString("MaxLevel");
	public static final String I18N_MIN_LEVEL = bundle.getString("MinLevel");
	public static final String I18N_MAP_ID = bundle.getString("MapID");
	public static final String I18N_MINUTES = bundle.getString("Minutes");
	public static final String I18N_NPC_ID = bundle.getString("NpcID");
	public static final String I18N_NPC_NAME = bundle.getString("NpcName");
	public static final String I18N_PARAM = bundle.getString("Param");
	public static final String I18N_RANGE = bundle.getString("Range");
	public static final String I18N_ROOM_NAME = bundle.getString("RoomName");
	public static final String I18N_SECONDS = bundle.getString("Seconds");
	public static final String I18N_SKILL_ID = bundle.getString("SkillID");
	public static final String I18N_SPAWN_ID = bundle.getString("SpawnID");
	public static final String I18N_SPR_ID = bundle.getString("SprID");
	public static final String I18N_STATUS = bundle.getString("Status");
	public static final String I18N_VALUE = bundle.getString("Value");
	public static final String I18N_PRINCE = bundle.getString("Prince");
	public static final String I18N_KNIGHT = bundle.getString("Knight");
	public static final String I18N_WIZARD = bundle.getString("Wizard");
	public static final String I18N_ELF = bundle.getString("Elf");
	public static final String I18N_DARK_ELF = bundle.getString("DarkElf");
	public static final String I18N_DRAGON_KNIGHT = bundle.getString("DragonKnight");
	public static final String I18N_ILLUSIONIST = bundle.getString("Illusionist");
	public static final String I18N_POSSIBLE = bundle.getString("Possible");
	public static final String I18N_IMPOSSIBLE = bundle.getString("Impossible");
	public static final String I18N_MALE = bundle.getString("Male");
	public static final String I18N_FEMALE = bundle.getString("Female");
	
	/*
	 * jp.l1j.server.command.executor.L1AccountBanKick.java
	 */
	public static final String I18N_ACCOUNT_BAN = bundle.getString("AccountBan");
	public static final String I18N_DOES_NOT_EXIST_CHAR = bundle.getString("DoesNotExistChar");
	
	/*
	 * jp.l1j.server.command.executor.L1AddInstanceMap.java
	 */
	public static final String I18N_GENERATED_THE_INSTANCE_MAP = bundle.getString("GeneratedTheInstanceMap");
	
	/*
	 * jp.l1j.server.command.executor.L1Adena.java
	 */
	public static final String I18N_GENERATED_THE_ADENA = bundle.getString("GeneratedTheAdena");
	
	/*
	 * jp.l1j.server.command.executor.L1AttackLog.java
	 */
	public static final String I18N_CHANGED_TO_DISPLAY_THE_DAMAGE = bundle.getString("ChangedToDisplayTheDamage");
	public static final String I18N_CHANGED_TO_HIDE_THE_DAMAGE = bundle.getString("ChangedToHideTheDamage");
	
	/*
	 * jp.l1j.server.command.executor.L1BanIp.java
	 */
	public static final String I18N_CONNECTED_PLAYER = bundle.getString("ConnectedPlayer");
	public static final String I18N_ADDED_TO_THE_BAN_LIST = bundle.getString("AddedToTheBanList");
	public static final String I18N_REMOVED_FROM_THE_BAN_LIST = bundle.getString("RemovedFromTheBanList");
	public static final String I18N_EXIST_IN_THE_BAN_LIST = bundle.getString("ExistInTheBanList");
	public static final String I18N_DOES_NOT_EXIST_IN_THE_BAN_LIST = bundle.getString("DoesNotExistInTheBanList");
	
	/*
	 * jp.l1j.server.command.executor.L1Buff.java
	 */
	public static final String I18N_IS_NOT_BUFF_SKILL = bundle.getString("IsNotBuffSkill");
	
	/*
	 * jp.l1j.server.command.executor.L1ChangeWeather.java
	 */
	public static final String I18N_WEATHER_OPTIONS = bundle.getString("WeatherOptions");
	
	/*
	 * jp.l1j.server.command.executor.L1AttackLog.java
	 */
	public static final String I18N_ENABLED_THE_GLOBAL_CHAT = bundle.getString("EnabledTheGlobalChat");
	public static final String I18N_DISABLED_THE_GLOBAL_CHAT = bundle.getString("DisabledTheGlobalChat");
	public static final String I18N_GLOBAL_CHAT_IS_POSSIBLE = bundle.getString("GlobalChatIsPossible");
	public static final String I18N_GLOBAL_CHAT_IS_IMPOSSIBLE = bundle.getString("GlobalChatIsImpossible");
	
	/*
	 * jp.l1j.server.command.executor.L1CreateItem.java
	 */
	public static final String I18N_DOES_NOT_EXIST_ITEM = bundle.getString("DoesNotExistItem");
	
	/*
	 * jp.l1j.server.command.executor.L1CreateItemSet.java
	 */
	public static final String I18N_DOES_NOT_EXIST_ITEM_SET = bundle.getString("DoesNotExistItemSet");
	
	/*
	 * jp.l1j.server.command.executor.L1DeleteGroundItem.java
	 */
	public static final String I18N_REMOVED_THE_ITEMS_ON_THE_WORLD_MAP = bundle.getString("RemovedTheItemsOnTheWorldMap");
	
	/*
	 * jp.l1j.server.command.executor.L1Describe.java
	 */
	public static final String I18N_DESC_DMG = bundle.getString("DescDmg");
	public static final String I18N_DESC_HIT = bundle.getString("DescHit");
	public static final String I18N_DESC_BOW_DMG = bundle.getString("DescBowDmg");
	public static final String I18N_DESC_BOW_HIT = bundle.getString("DescBowHit");
	public static final String I18N_DESC_MR = bundle.getString("DescMR");
	public static final String I18N_DESC_HPR = bundle.getString("DescHPR");
	public static final String I18N_DESC_MPR = bundle.getString("DescMPR");
	public static final String I18N_DESC_RESIST_FREEZE = bundle.getString("DescResistFreeze");
	public static final String I18N_DESC_RESIST_STUN = bundle.getString("DescResistStun");
	public static final String I18N_DESC_RESIST_STONE = bundle.getString("DescResistStone");
	public static final String I18N_DESC_RESIST_SLEEP = bundle.getString("DescResistSleep");
	public static final String I18N_DESC_RESIST_HOLD = bundle.getString("DescResistHold");
	public static final String I18N_DESC_RESIST_BLIND = bundle.getString("DescResistBlind");
	public static final String I18N_DESC_KARMA = bundle.getString("DescKarma");
	public static final String I18N_DESC_INVENTORY_SIZE = bundle.getString("DescInventorySize");
	public static final String I18N_DESC_EXP_BONUS = bundle.getString("DescExpBonus");
		
	/*
	 * jp.l1j.server.command.executor.L1DropLog.java
	 */
	public static final String I18N_CHANGED_TO_DISPLAY_THE_DROPS = bundle.getString("ChangedToDisplayTheDrops");
	public static final String I18N_CHANGED_TO_HIDE_THE_DROPS = bundle.getString("ChangedToHideTheDrops");
	
	/*
	 * jp.l1j.server.command.executor.L1Favorite.java
	 */
	public static final String I18N_COMMAND_IS_EMPTY = bundle.getString("CommandIsEmpty");
	public static final String I18N_CAN_NOT_BE_REGISTERD = bundle.getString("CanNotBeRegisterd");
	public static final String I18N_REGISTERD_COMMAND = bundle.getString("RegisterdCommand");
	public static final String I18N_COMMAND_THAT_ARE_REGISTERD = bundle.getString("CommandThatAreRegisterd");
	public static final String I18N_DOES_NOT_EXIST_COMMAND_THAT_ARE_REGISTERD = bundle.getString("CommandThatAreRegisterd");
	
	/*
	 * jp.l1j.server.command.executor.L1GMRoom.java
	 */
	public static final String I18N_DOES_NOT_EXIST_THE_ROOM = bundle.getString("DoesNotExistTheRoom");
	
	/*
	 * jp.l1j.server.command.executor.L1InsertSpawn.java
	 */
	public static final String I18N_DOES_NOT_EXIST_NPC = bundle.getString("DoesNotExistNPC");
	public static final String I18N_IS_NOT_MOBS = bundle.getString("IsNotMobs");
	public static final String I18N_ADDED_TO_THE_SPAWN_LIST = bundle.getString("AddedToTheSpawnList");
	
	/*
	 * jp.l1j.server.command.executor.L1Invisible.java
	 */
	public static final String I18N_BECAME_INVISIBLE = bundle.getString("BecameInvisible");
	
	/*
	 * jp.l1j.server.command.executor.L1AccountKick.java
	 */
	public static final String I18N_ACCOUNT_KICK = bundle.getString("AccountKick");
	
	/*
	 * jp.l1j.server.command.executor.L1LevelPresent.java
	 */
	public static final String I18N_GAVE_THE_ITEM_1 = bundle.getString("GaveTheItem1");
	
	/*
	 * jp.l1j.server.command.executor.L1Loc.java
	 */
	public static final String I18N_LOCATION = bundle.getString("Location");
	
	/*
	 * jp.l1j.server.command.executor.L1Move.java
	 */
	public static final String I18N_MOVED_TO_LOC = bundle.getString("MovedToLoc");
	
	/*
	 * jp.l1j.server.command.executor.L1PartyRecall.java
	 */
	public static final String I18N_RECALLED_BY_GM = bundle.getString("RecalledByGM");
	public static final String I18N_IS_NOT_PARTY_MEMBER = bundle.getString("IsNotPartyMember");
	
	/*
	 * jp.l1j.server.command.executor.L1PcBuff.java
	 */
	public static final String I18N_BUFF_SKILLS_BY_GM = bundle.getString("BuffSkillsByGM");
	
	/*
	 * jp.l1j.server.command.executor.L1PotLog.java
	 */
	public static final String I18N_CHANGED_TO_DISPLAY_USE_OF_POTIONS = bundle.getString("ChangedToDisplayUseOfThePotions");
	public static final String I18N_CHANGED_TO_HIDE_USE_OF_POTIONS = bundle.getString("ChangedToHideUseOfThePotions");

	/*
	 * jp.l1j.server.command.executor.L1Present.java
	 */
	public static final String I18N_GAVE_THE_ITEM_2 = bundle.getString("GaveTheItem2");

	/*
	 * jp.l1j.server.command.executor.L1Recalled.java
	 */
	public static final String I18N_RECALLED = bundle.getString("Recalled");
	
	/*
	 * jp.l1j.server.command.executor.L1Reload.java
	 */
	public static final String I18N_RELOADED_THE_DISPLAY_OBJECTS = bundle.getString("ReloadedTheDisplayObjects");
	
	/*
	 * jp.l1j.server.command.executor.L1ReloadTrap.java
	 */
	public static final String I18N_RELOADED_THE_TRAPS = bundle.getString("ReloadedTheTraps");
	
	/*
	 * jp.l1j.server.command.executor.L1RemoveInstanceMap.java
	 */
	public static final String I18N_REMOVED_THE_INSTANCE_MAP = bundle.getString("RemovedTheInstanceMap");
	public static final String I18N_DOES_NOT_EXIST_THE_INSTANCE_MAP = bundle.getString("DoesNotExistInstanceMap");
	
	/*
	 * jp.l1j.server.command.executor.L1Ress.java
	 */
	public static final String I18N_RESUSCITATED_BY_GM = bundle.getString("ResuscitatedByGM");
	public static final String I18N_RECOVERED_BY_GM = bundle.getString("RecoveredByGM");
	
	/*
	 * jp.l1j.server.command.executor.L1ShutdownRequest.java
	 */
	public static final String I18N_ACCEPTED_SHUTDOWN_REQUEST = bundle.getString("AcceptedShutdownRequest");
	public static final String I18N_SHUTDOWN_REQUEST_MAX = bundle.getString("ShutdownRequestMax");
	
	/*
	 * jp.l1j.server.command.executor.L1SKick.java
	 */
	public static final String I18N_DISCONNECTED_THE_CONNECTION = bundle.getString("DisconnectedTheConnection");
	
	/*
	 * jp.l1j.server.command.executor.L1SpawnCmd.java
	 */
	public static final String I18N_SUMMON_MONSTER_1 = bundle.getString("SummonMonster1");
	
	/*
	 * jp.l1j.server.command.executor.L1Satus.java
	 */
	public static final String I18N_PROMOTED_TO_GM = bundle.getString("PromotedToGM");
	public static final String I18N_IS_UNKNOWN_PARAM = bundle.getString("IsUnknownParam");
	public static final String I18N_CHANGED_THE_STATUS = bundle.getString("ChangedTheStatus");
	
	/*
	 * jp.l1j.server.command.executor.L1Summon.java
	 */
	public static final String I18N_SUMMON_MONSTER_2 = bundle.getString("SummonMonster2");
	
	/*
	 * jp.l1j.server.command.executor.L1ToPC.java
	 */
	public static final String I18N_MOVED_TO_THE_CHAR = bundle.getString("MovedToTheChar");
	
	/*
	 * jp.l1j.server.command.executor.L1Visible.java
	 */
	public static final String I18N_BECAME_VISIBLE = bundle.getString("BecameVisible");
	
	/*
	 * jp.l1j.server.controller.LoginController.java
	 */
	public static final String I18N_IS_NOT_AUTHENTICATED = bundle.getString("IsNotAuthenticated");
	
	/*
	 * jp.l1j.server.controller.HomeTownTimeController.java
	 */
	public static final String I18N_HOME_TOWN_SYS_DAILY_PROC = bundle.getString("HomeTownSystemDailyProcess");
	public static final String I18N_HOME_TOWN_SYS_MONTHLY_PROC = bundle.getString("HomeTownSystemMonthlyProcess");
	
	/*
	 * jp.l1j.server.controller.WeatherTimeController.java
	 */
	public static final String I18N_WEATHER_SYS_CLEAR = bundle.getString("WeatherSystemClear");
	public static final String I18N_WEATHER_SYS_RAIN = bundle.getString("WeatherSystemRain");
	public static final String I18N_WEATHER_SYS_SNOW = bundle.getString("WeatherSystemSnow");
	
	/*
	 * jp.l1j.server.datatables.ItemTable.java
	 */
	public static final String I18N_LOAD_ITEM_FAILED = bundle.getString("LoadItemFailed");
	
	/*
	 * jp.l1j.server.model.AccelerratorChecker.java
	 */
	public static final String I18N_ACCELERRATOR_DISCONNECT_THE_CONNECTION = bundle.getString("AccelerratorDisconnectTheConnection");
	public static final String I18N_ACCELERRATOR_STOP_THE_ACTION = bundle.getString("AccelerratorStopTheAction");
	public static final String I18N_ACCELERRATOR_MOVE_TO_THE_ISOLATION_MAP = bundle.getString("AccelerratorMoveToTheIsolationMap");
	public static final String I18N_ACCELERRATOR_OVERSPEED_DETECTED = bundle.getString("AccelerratorOverspeedDetected");
	
	/*
	 * jp.l1j.server.model.L1Attack.java
	 */
	public static final String I18N_ATTACK_GAVE_TEXT_COLOR = bundle.getString("AttackGaveTextColor");
	public static final String I18N_ATTACK_RECEIVED_TEXT_COLOR = bundle.getString("AttackReceivedTextColor");
	public static final String I18N_ATTACK_FORMAT = bundle.getString("AttackFormat");
	public static final String I18N_ATTACK_TO = bundle.getString("AttackTo");
	public static final String I18N_ATTACK_DMG = bundle.getString("AttackDmg");
	public static final String I18N_ATTACK_MISS = bundle.getString("AttackMiss");
	
	/*
	 * jp.l1j.server.model.L1BugBearRace.java
	 */
	public static final String I18N_RACE_ROUND = bundle.getString("RaceRound");
	
	/*
	 * jp.l1j.server.model.L1Clan.java
	 */
	public static final String I18N_CLAN_LEADER = bundle.getString("ClanLeader");
	public static final String I18N_CLAN_SUBLEADER = bundle.getString("ClanSubLeader");
	public static final String I18N_CLAN_GUARDIAN = bundle.getString("ClanGuardian");
	public static final String I18N_CLAN_ELITE = bundle.getString("ClanElite");
	public static final String I18N_CLAN_REGULAR = bundle.getString("ClanRegular");
	
	/*
	 * jp.l1j.server.model.L1Clan.java
	 */
	public static final String I18N_REMOVE_ITEMS_AFTER_FEW_SECONDS = bundle.getString("RemoveItemsAfterFewSeconds");
	public static final String I18N_REMOVED_ITEMS_ON_WORLD_MAP = bundle.getString("RemovedItemsOnWorldMap");
	
	/*
	 * jp.l1j.server.model.L1Magic.java
	 */
	public static final String I18N_SKILL_GAVE_TEXT_COLOR = bundle.getString("SkillGaveTextColor");
	public static final String I18N_SKILL_RECEIVED_TEXT_COLOR = bundle.getString("SkillReceivedTextColor");
	public static final String I18N_SKILL_SUCCESS = bundle.getString("SkillSuccess");
	public static final String I18N_SKILL_FAILED = bundle.getString("SkillFailed");
	public static final String I18N_SKILL_DMG = bundle.getString("SkillDmg");
	
	/*
	 * jp.l1j.server.model.L1DeathMatch.java
	 */
	public static final String I18N_DEATHMATCH_LESS_THAN_MIN_PLAYERS = bundle.getString("DeathMatchLessThanMinPlayers");
	
	/*
	 * jp.l1j.server.model.L1PolyRace.java
	 */
	public static final String I18N_POLYRACE_LESS_THAN_MIN_PLAYERS = bundle.getString("PolyRaceLessThanMinPlayers");
	public static final String I18N_POLYRACE_REACHED_TO_MAX_PLAYERS = bundle.getString("PolyRaceReachedToMaxPlayers");

	/*
	 * jp.l1j.server.model.L1SpellBook.java
	 */
	public static final String I18N_SPELL_BOOK_NAME = bundle.getString("SpellBookName");
	public static final String I18N_SPIRIT_CRYSTAL_NAME = bundle.getString("SpiritCrystalName");
	public static final String I18N_DARK_SPIRIT_CRYSTAL_NAME = bundle.getString("DarkSpiritCrystalName");
	public static final String I18N_TECHNICAL_DOCUMENT_NAME = bundle.getString("TechnicalDocumentName");
	public static final String I18N_DRAGON_TABLET_NAME = bundle.getString("DragonTabletName");
	public static final String I18N_MEMORY_CRYSTAL_NAME = bundle.getString("MemoryCrystalName");
	
	/*
	 * jp.l1j.server.model.instance.L1HousekeeperInstance.java
	 */
	public static final String I18N_TAX_HAS_ALLREADY_PAID = bundle.getString("TaxHasAllreadyPaid");
	
	/*
	 * jp.l1j.server.model.instance.L1PcInstance.java
	 */
	public static final String I18N_FAILED_TO_OBTAIN_THE_REVIVAL_POTION = bundle.getString("FailedToObtainTheRevivalPotion");
	
	/*
	 * jp.l1j.server.model.item.executor.*
	 */
	public static final String I18N_DOES_NOT_EXIST_ITEM_LIST = bundle.getString("DoesNotExistItemList");
	public static final String I18N_DOES_NOT_EXIST_SKILL_LIST = bundle.getString("DoesNotExistSkillList");
	public static final String I18N_DOES_NOT_EXIST_POLY_LIST = bundle.getString("DoesNotExistPolyList");
	public static final String I18N_DOES_NOT_EXIST_MAP_LIST = bundle.getString("DoesNotExistMapList");
	public static final String I18N_DOES_NOT_EXIST_NPC_LIST = bundle.getString("DoesNotExistNpcList");
	public static final String I18N_PROBABILITIES_ERROR = bundle.getString("ProbabilitiesError");
	public static final String I18N_OVER_ENCHANT_SUCCESSFUL = bundle.getString("OverEnchantSuccessful");
	
	/*
	 * jp.l1j.server.packets.client.C_AuthLogin.java
	 */
	public static final String I18N_DENY_TO_MULTIPLE_LOGINS = bundle.getString("DenyToMultipleLogins");
	public static final String I18N_DENY_TO_CONNECT_FROM_SPECIFIC_IP = bundle.getString("DenyToConnectFromSpecificIP");
	public static final String I18N_DENY_TO_LOGIN_BAN_ACCOUNT = bundle.getString("DenyToLoginBanAccount");
	public static final String I18N_DENY_TO_LOGIN_BECAUSE_REACHED_MAX_CONNECTIONS = bundle.getString("DenyToLoginBecauseReachedMaxConnections");
	
	/*
	 * jp.l1j.server.packets.client.C_LoginToServer.java
	 */
	public static final String I18N_MULTIPLE_LOGINS_DETECTED = bundle.getString("MultipleLoginsDetected");
	public static final String I18N_LOGIN_REQUEST_IS_INVALID = bundle.getString("LoginRequestIsInvalid");
	public static final String I18N_BEYOND_THE_TOLERANCE_OF_THE_LEVEL_DOWN = bundle.getString("BeyondTheToleranceOfTheLevelDown");
	public static final String I18N_LOGGED_CHARACTER = bundle.getString("LoggedCharacter");
	
	/*
	 * jp.l1j.server.packets.client.C_PickUpItem.java
	 */
	public static final String I18N_CAN_NOT_PICK_UP_ADENA = bundle.getString("CanNotPickUpAdena");
	
	/*
	 * jp.l1j.server.packets.client.C_Rank.java
	 */
	public static final String I18N_CLAN_LIST = bundle.getString("ClanList");
	public static final String I18N_JOIN_TO_CLAN = bundle.getString("JoinToClan");
	public static final String I18N_LEAVE_THE_CLAN = bundle.getString("LeaveTheClan");

	/*
	 * jp.l1j.server.packets.client.C_Shop.java
	 */
	public static final String I18N_TRADING_IS_IMPOSSIBLE = bundle.getString("TradingIsImpossible");
	public static final String I18N_CAN_NOT_TO_STACK = bundle.getString("CanNotToStack");
	
	/*
	 * jp.l1j.server.packets.client.C_UseItem.java
	 */
	public static final String I18N_CAN_BE_USED_BELOW_THE_MAX_LEVEL = bundle.getString("CanBeUsedBelowTheMaxLevel");
}
