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

package jp.l1j.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.command.GMCommandConfigs;
import jp.l1j.server.controller.Announcements;
import jp.l1j.server.controller.AnnouncementsCycle;
import jp.l1j.server.controller.LoginController;
import jp.l1j.server.controller.ShutdownController;
import jp.l1j.server.controller.timer.AuctionTimeController;
import jp.l1j.server.controller.timer.DeleteItemController;
import jp.l1j.server.controller.timer.FishingTimeController;
import jp.l1j.server.controller.timer.HomeTownTimeController;
import jp.l1j.server.controller.timer.HouseTaxTimeController;
import jp.l1j.server.controller.timer.LightTimeController;
import jp.l1j.server.controller.timer.MapTimeController;
import jp.l1j.server.controller.timer.NpcChatTimeController;
import jp.l1j.server.controller.timer.ShutdownTimeController;
import jp.l1j.server.controller.timer.UbTimeController;
import jp.l1j.server.controller.timer.WarTimeController;
import jp.l1j.server.controller.timer.WeatherTimeController;
import jp.l1j.server.datatables.ArmorSetTable;
import jp.l1j.server.datatables.CastleTable;
import jp.l1j.server.datatables.CharacterTable;
import jp.l1j.server.datatables.ChatLogTable;
import jp.l1j.server.datatables.ClanTable;
import jp.l1j.server.datatables.CookingRecipeTable;
import jp.l1j.server.datatables.DoorTable;
import jp.l1j.server.datatables.DropRateTable;
import jp.l1j.server.datatables.DropTable;
import jp.l1j.server.datatables.DungeonTable;
import jp.l1j.server.datatables.InnTable;
import jp.l1j.server.datatables.IpTable;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.datatables.MagicDollTable;
import jp.l1j.server.datatables.MailTable;
import jp.l1j.server.datatables.MapTable;
import jp.l1j.server.datatables.MobGroupTable;
import jp.l1j.server.datatables.MobSkillTable;
import jp.l1j.server.datatables.NpcActionTable;
import jp.l1j.server.datatables.NpcChatTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.NpcTalkDataTable;
import jp.l1j.server.datatables.PetTable;
import jp.l1j.server.datatables.PetTypeTable;
import jp.l1j.server.datatables.PolyTable;
import jp.l1j.server.datatables.RaceTicketTable;
import jp.l1j.server.datatables.RandomDungeonTable;
import jp.l1j.server.datatables.ResolventTable;
import jp.l1j.server.datatables.RestartLocationTable;
import jp.l1j.server.datatables.ReturnLocationTable;
import jp.l1j.server.datatables.ShopTable;
import jp.l1j.server.datatables.ShutdownRequestTable;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.datatables.SpawnFurnitureTable;
import jp.l1j.server.datatables.SpawnNpcTable;
import jp.l1j.server.datatables.SpawnTable;
import jp.l1j.server.datatables.SpawnUbMobTable;
import jp.l1j.server.datatables.SprListTable;
import jp.l1j.server.datatables.SprTable;
import jp.l1j.server.datatables.WeaponSkillTable;
import jp.l1j.server.model.ElementalStoneGenerator;
import jp.l1j.server.model.L1BossCycle;
import jp.l1j.server.model.L1BugBearRace;
import jp.l1j.server.model.L1CastleLocation;
import jp.l1j.server.model.L1NpcRegenerationTimer;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.gametime.L1GameTimeClock;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.item.executor.L1BeginnerItem;
import jp.l1j.server.model.item.executor.L1BlankScroll;
import jp.l1j.server.model.item.executor.L1BlessOfEva;
import jp.l1j.server.model.item.executor.L1BluePotion;
import jp.l1j.server.model.item.executor.L1BravePotion;
import jp.l1j.server.model.item.executor.L1CurePotion;
import jp.l1j.server.model.item.executor.L1Elixir;
import jp.l1j.server.model.item.executor.L1EnchantBonus;
import jp.l1j.server.model.item.executor.L1EnchantProtectScroll;
import jp.l1j.server.model.item.executor.L1ExtraPotion;
import jp.l1j.server.model.item.executor.L1FireCracker;
import jp.l1j.server.model.item.executor.L1FloraPotion;
import jp.l1j.server.model.item.executor.L1Furniture;
import jp.l1j.server.model.item.executor.L1GreenPotion;
import jp.l1j.server.model.item.executor.L1HealingPotion;
import jp.l1j.server.model.item.executor.L1MagicEye;
import jp.l1j.server.model.item.executor.L1MagicPotion;
import jp.l1j.server.model.item.executor.L1Material;
import jp.l1j.server.model.item.executor.L1MaterialChoice;
import jp.l1j.server.model.item.executor.L1PolyPotion;
import jp.l1j.server.model.item.executor.L1PolyScroll;
import jp.l1j.server.model.item.executor.L1PolyWand;
import jp.l1j.server.model.item.executor.L1Roulette;
import jp.l1j.server.model.item.executor.L1ShowMessage;
import jp.l1j.server.model.item.executor.L1SpawnWand;
import jp.l1j.server.model.item.executor.L1SpeedUpClock;
import jp.l1j.server.model.item.executor.L1SpellIcon;
import jp.l1j.server.model.item.executor.L1SpellItem;
import jp.l1j.server.model.item.executor.L1TeleportAmulet;
import jp.l1j.server.model.item.executor.L1ThirdSpeedPotion;
import jp.l1j.server.model.item.executor.L1TreasureBox;
import jp.l1j.server.model.item.executor.L1UniqueEnchantScroll;
import jp.l1j.server.model.item.executor.L1UnknownMaliceWeapon;
import jp.l1j.server.model.item.executor.L1WisdomPotion;
import jp.l1j.server.model.map.L1WorldMap;
import jp.l1j.server.model.map.executor.L1MapLimiter;
import jp.l1j.server.model.trap.L1WorldTraps;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.SystemUtil;

public class GameServer extends Thread {
	private ServerSocket _serverSocket;
	private static Logger _log = Logger.getLogger(GameServer.class.getName());
	private LoginController _loginController;
	private static int YesNoCount = 0;

	@Override
	public void run() {
		System.out.println(String.format(I18N_MEMORY_USEAGE, SystemUtil.getUsedMemoryMB()));
		System.out.println(I18N_WAITING_FOR_CLIENT);
		while (true) {
			try {
				Socket socket = _serverSocket.accept();
				System.out.println(String.format(I18N_TRYING_TO_CONNECTION, socket.getInetAddress()));
				String host = socket.getInetAddress().getHostAddress();
				if (IpTable.getInstance().isBannedIp(host)) {
					_log.info(String.format(I18N_BANNED_IP, host));
				} else {
					ClientThread client = new ClientThread(socket);
					GeneralThreadPool.getInstance().execute(client);
				}
			} catch (IOException ioexception) {
			}
		}
	}

	private static GameServer _instance;

	private GameServer() {
		super("GameServer");
	}

	public static GameServer getInstance() {
		if (_instance == null) {
			_instance = new GameServer();
		}
		return _instance;
	}

	private void puts(String message, Object... args) {
		String msg = (args.length == 0) ? message : String.format(message, args);
		System.out.println(msg);
	}

	private void printStartupMessage() {
			puts(I18N_GENERATE_SERVER_SOCKET);
			puts("/***********************************************************");
			puts("*                L1J-JP 3.63C  For All User                *");
			puts("***********************************************************/");
			puts("");
			puts(I18N_SERVER_SETTINGS);
			puts("");
			puts(I18N_EXP, Config.RATE_XP);
			puts(I18N_LAWFUL, Config.RATE_LA);
			puts(I18N_KARMA, Config.RATE_KARMA);
			puts(I18N_ITEM_DROP, Config.RATE_DROP_ITEMS);
			puts(I18N_ADENA_DROP, Config.RATE_DROP_ADENA);
			puts(I18N_ENCHANT_WEAPON, Config.ENCHANT_CHANCE_WEAPON);
			puts(I18N_ENCHANT_ARMOR, Config.ENCHANT_CHANCE_ARMOR);
			puts(I18N_ENCHANT_ATTRIBUTE, Config.ATTR_ENCHANT_CHANCE);
			puts(I18N_ENCHANT_ACCESSORY, Config.ENCHANT_CHANCE_ACCESSORY);
			puts(I18N_ENCHANT_DOLL, Config.ENCHANT_CHANCE_DOLL);
			puts(I18N_WEIGHT_REDUCTION, Config.RATE_WEIGHT_LIMIT);
			puts(I18N_GLOBAL_CHAT, Config.GLOBAL_CHAT_LEVEL);
			puts(Config.ALT_NONPVP ? I18N_PVP : I18N_NON_PVP);
			puts(I18N_MAX_USERS, Config.MAX_ONLINE_USERS);
			puts("");
			puts("/***********************************************************");
			puts("*    L1J-JP MMORPG Lineage Server Emulator Java Project    *");
			puts("***********************************************************/");
			puts("");
		}

	public void initialize() throws Exception {
		printStartupMessage();

		String host = Config.GAME_SERVER_HOST_NAME;
		int port = Config.GAME_SERVER_PORT;
		if (!"*".equals(host)) {
			InetAddress inetaddress = InetAddress.getByName(host);
			inetaddress.getHostAddress();
			_serverSocket = new ServerSocket(port, 50, inetaddress);
		} else {
			_serverSocket = new ServerSocket(port);
		}

		IdFactory.getInstance();
		
		// テキストマップデータをロード
		L1WorldMap.getInstance();
		
		// ログインコントローラー
		_loginController = LoginController.getInstance();
		_loginController.setMaxAllowedOnlinePlayers(Config.MAX_ONLINE_USERS);

		// 全キャラクターネームロード
		CharacterTable.getInstance().loadAllCharName();

		// オンライン状態リセット
		CharacterTable.clearOnlineStatus();

		// ゲーム時間時計
		L1GameTimeClock.init();

		// UBタイムコントローラー
		UbTimeController ubTimeContoroller = UbTimeController.getInstance();
		GeneralThreadPool.getInstance().execute(ubTimeContoroller);

		// 戦争タイムコントローラー
		WarTimeController warTimeController = WarTimeController.getInstance();
		GeneralThreadPool.getInstance().execute(warTimeController);

		// 天候タイムコントローラー
		if (Config.WEATHER_SYSTEM) {
			WeatherTimeController weatherTimeController = WeatherTimeController.getInstance();
			GeneralThreadPool.getInstance().execute(weatherTimeController);
		}
		
		// 精霊の石生成
		if (Config.ELEMENTAL_STONE_AMOUNT > 0) {
			ElementalStoneGenerator elementalStoneGenerator = ElementalStoneGenerator.getInstance();
			GeneralThreadPool.getInstance().execute(elementalStoneGenerator);
		}

		// ホームタウンシステムコントローラー
		HomeTownTimeController.getInstance();

		// アジト競売タイムコントローラー
		AuctionTimeController auctionTimeController = AuctionTimeController.getInstance();
		GeneralThreadPool.getInstance().execute(auctionTimeController);

		// アジト税金タイムコントローラー
		HouseTaxTimeController houseTaxTimeController = HouseTaxTimeController.getInstance();
		GeneralThreadPool.getInstance().execute(houseTaxTimeController);

		// 釣りタイムコントローラー
		FishingTimeController fishingTimeController = FishingTimeController.getInstance();
		GeneralThreadPool.getInstance().execute(fishingTimeController);

		// NPCチャットタイムコントローラー
		NpcChatTimeController npcChatTimeController = NpcChatTimeController.getInstance();
		GeneralThreadPool.getInstance().execute(npcChatTimeController);

		// ライトタイムコントローラー
		LightTimeController lightTimeController = LightTimeController.getInstance();
		GeneralThreadPool.getInstance().execute(lightTimeController);

		// アイテム削除コントローラー
		new DeleteItemController().initialize();
		
		// マップタイムコントローラー
		MapTimeController mapTimeController = MapTimeController.getInstance();
		mapTimeController.load();
		GeneralThreadPool.getInstance().execute(mapTimeController);

		// 自動シャットダウンコントローラー
		if (Config.AUTO_SHUTDOWN) {
			ShutdownTimeController shutdownTimeController = ShutdownTimeController.getInstance();
			shutdownTimeController.load();
			GeneralThreadPool.getInstance().execute(shutdownTimeController);
		}

		// 定期アナウンスコントローラー
		Announcements.getInstance();
		AnnouncementsCycle.getInstance();

		MobSkillTable.getInstance();
		NpcTable.getInstance();
		DoorTable.getInstance();
		SpawnTable.getInstance();
		MobGroupTable.getInstance();
		SkillTable.getInstance();
		PolyTable.getInstance();
		ItemTable.getInstance();
		ArmorSetTable.getInstance();
		DropTable.getInstance();
		DropRateTable.getInstance();
		ShopTable.getInstance();
		NpcTalkDataTable.getInstance();
		L1World.getInstance();
		L1WorldTraps.getInstance();
		DungeonTable.getInstance();
		RandomDungeonTable.getInstance();
		SpawnNpcTable.getInstance();
		IpTable.getInstance();
		MapTable.getInstance();
		SpawnUbMobTable.getInstance();
		PetTable.getInstance();
		ClanTable.getInstance();
		CastleTable.getInstance();
		L1CastleLocation.setCastleTaxRate(); // これはCastleTable初期化後でなければいけない
		RestartLocationTable.getInstance();
		GeneralThreadPool.getInstance();
		L1NpcRegenerationTimer.getInstance();
		ChatLogTable.getInstance();
		WeaponSkillTable.getInstance();
		NpcActionTable.getInstance();
		ReturnLocationTable.load();
		GMCommandConfigs.getInstance();
		PetTypeTable.getInstance();
		SprTable.getInstance();
		ResolventTable.getInstance();
		SpawnFurnitureTable.getInstance();
		NpcChatTable.getInstance();
		MailTable.getInstance();
		SprListTable.getInstance();
		RaceTicketTable.getInstance();
		L1BugBearRace.getInstance();
		InnTable.getInstance();
		MagicDollTable.getInstance();
		CookingRecipeTable.getInstance();
		ShutdownRequestTable.removeAll();
		
		// Loading the XML files
		L1BossCycle.load();
		L1BeginnerItem.load();
		L1BlankScroll.load();
		L1BlessOfEva.load();
		L1BluePotion.load();
		L1BravePotion.load();
		L1CurePotion.load();
		L1Elixir.load();
		L1EnchantBonus.load();
		L1EnchantProtectScroll.load();
		L1ExtraPotion.load();
		L1FireCracker.load();
		L1FloraPotion.load();
		L1Furniture.load();
		L1GreenPotion.load();
		L1HealingPotion.load();
		L1MagicEye.load();
		L1MagicPotion.load();
		L1MapLimiter.load();
		L1Material.load();
		L1MaterialChoice.load();
		L1PolyPotion.load();
		L1PolyScroll.load();
		L1PolyWand.load();
		L1Roulette.load();
		L1ShowMessage.load();
		L1SpawnWand.load();
		L1SpeedUpClock.load();
		L1SpellIcon.load();
		L1SpellItem.load();
		L1TeleportAmulet.load();
		L1ThirdSpeedPotion.load();
		L1TreasureBox.load();
		L1UniqueEnchantScroll.load();
		L1UnknownMaliceWeapon.load();
		L1WisdomPotion.load();
		
		System.out.println(I18N_LOADING_COMPLETE);
		Runtime.getRuntime().addShutdownHook(ShutdownController.getInstance());
		this.start();
	}

	/**
	 * オンライン中のプレイヤー全てに対してkick、キャラクター情報の保存をする。
	 */
	public void disconnectAllCharacters() {
		Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();
		for (L1PcInstance pc : players) {
			pc.getNetConnection().setActiveChar(null);
			pc.getNetConnection().kick();
		}
		// 全員Kickした後に保存処理をする
		for (L1PcInstance pc : players) {
			ClientThread.quitGame(pc);
			L1World.getInstance().removeObject(pc);
		}
	}

	private class ServerShutdownThread extends Thread {
		private final int _secondsCount;

		public ServerShutdownThread(int secondsCount) {
			_secondsCount = secondsCount;
		}

		@Override
		public void run() {
			L1World world = L1World.getInstance();
			try {
				int secondsCount = _secondsCount;
				world.broadcastServerMessage(I18N_SHUTDOWN_THE_SERVER);
				world.broadcastServerMessage(I18N_PLEASE_LOGOUT);
				while (0 < secondsCount) {
					if (secondsCount <= 30) {
						world.broadcastServerMessage(String.format(I18N_SHUTDOWN_AFTER_FEW_SECONDS, secondsCount));
					} else {
						if (secondsCount % 60 == 0) {
							world.broadcastServerMessage(String.format(I18N_SHUTDOWN_AFTER_FEW_MINUTES, secondsCount / 60));
						}
					}
					Thread.sleep(1000);
					secondsCount--;
				}
				shutdown();
			} catch (InterruptedException e) {
				world.broadcastServerMessage(I18N_SHUTDOWN_ABORT);
				return;
			}
		}
	}

	private ServerShutdownThread _shutdownThread = null;

	public synchronized void shutdownWithCountdown(int secondsCount) {
		if (_shutdownThread != null) {
			// 既にシャットダウン要求が行われている
			// TODO エラー通知が必要かもしれない
			return;
		}
		_shutdownThread = new ServerShutdownThread(secondsCount);
		GeneralThreadPool.getInstance().execute(_shutdownThread);
	}

	public void shutdown() {
		disconnectAllCharacters();
		System.exit(0);
	}

	public synchronized void abortShutdown() {
		if (_shutdownThread == null) {
			// シャットダウン要求が行われていない
			// TODO エラー通知が必要かもしれない
			return;
		}
		_shutdownThread.interrupt();
		_shutdownThread = null;
	}

	public static int getYesNoCount() {
		YesNoCount += 1;
		return YesNoCount;
	}
}
