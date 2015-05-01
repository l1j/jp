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
package jp.l1j.server.packets;

import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.ClientThread;
import static jp.l1j.server.codes.Opcodes.*;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.client.C_AddBookmark;
import jp.l1j.server.packets.client.C_AddBuddy;
import jp.l1j.server.packets.client.C_Amount;
import jp.l1j.server.packets.client.C_Attack;
import jp.l1j.server.packets.client.C_Attr;
import jp.l1j.server.packets.client.C_AuthLogin;
import jp.l1j.server.packets.client.C_BanClan;
import jp.l1j.server.packets.client.C_BanParty;
import jp.l1j.server.packets.client.C_Board;
import jp.l1j.server.packets.client.C_BoardBack;
import jp.l1j.server.packets.client.C_BoardDelete;
import jp.l1j.server.packets.client.C_BoardRead;
import jp.l1j.server.packets.client.C_BoardWrite;
import jp.l1j.server.packets.client.C_Buddy;
import jp.l1j.server.packets.client.C_CallPlayer;
import jp.l1j.server.packets.client.C_ChangeHeading;
import jp.l1j.server.packets.client.C_ChangeWarTime;
import jp.l1j.server.packets.client.C_CharReset;
import jp.l1j.server.packets.client.C_CharcterConfig;
import jp.l1j.server.packets.client.C_Chat;
import jp.l1j.server.packets.client.C_ChatParty;
import jp.l1j.server.packets.client.C_ChatWhisper;
import jp.l1j.server.packets.client.C_CheckPK;
import jp.l1j.server.packets.client.C_Clan;
import jp.l1j.server.packets.client.C_CommonClick;
import jp.l1j.server.packets.client.C_CreateChar;
import jp.l1j.server.packets.client.C_CreateClan;
import jp.l1j.server.packets.client.C_CreateParty;
import jp.l1j.server.packets.client.C_DeleteBookmark;
import jp.l1j.server.packets.client.C_DeleteBuddy;
import jp.l1j.server.packets.client.C_DeleteChar;
import jp.l1j.server.packets.client.C_DeleteInventoryItem;
import jp.l1j.server.packets.client.C_Deposit;
import jp.l1j.server.packets.client.C_Door;
import jp.l1j.server.packets.client.C_Drawal;
import jp.l1j.server.packets.client.C_DropItem;
import jp.l1j.server.packets.client.C_Emblem;
import jp.l1j.server.packets.client.C_EnterPortal;
import jp.l1j.server.packets.client.C_Exclude;
import jp.l1j.server.packets.client.C_ExitGhost;
import jp.l1j.server.packets.client.C_ExtraCommand;
import jp.l1j.server.packets.client.C_Fight;
import jp.l1j.server.packets.client.C_FishClick;
import jp.l1j.server.packets.client.C_FixWeaponList;
import jp.l1j.server.packets.client.C_GiveItem;
import jp.l1j.server.packets.client.C_HireSoldier;
import jp.l1j.server.packets.client.C_JoinClan;
import jp.l1j.server.packets.client.C_KeepAlive;
import jp.l1j.server.packets.client.C_LeaveClan;
import jp.l1j.server.packets.client.C_LeaveParty;
import jp.l1j.server.packets.client.C_LoginToServer;
import jp.l1j.server.packets.client.C_LoginToServerOK;
import jp.l1j.server.packets.client.C_Mail;
import jp.l1j.server.packets.client.C_MoveChar;
import jp.l1j.server.packets.client.C_NewCharSelect;
import jp.l1j.server.packets.client.C_NpcAction;
import jp.l1j.server.packets.client.C_NpcTalk;
import jp.l1j.server.packets.client.C_Party;
import jp.l1j.server.packets.client.C_PetMenu;
import jp.l1j.server.packets.client.C_PickUpItem;
import jp.l1j.server.packets.client.C_Pledge;
import jp.l1j.server.packets.client.C_PledgeRecommendation;
import jp.l1j.server.packets.client.C_Propose;
import jp.l1j.server.packets.client.C_Rank;
import jp.l1j.server.packets.client.C_Restart;
import jp.l1j.server.packets.client.C_Result;
import jp.l1j.server.packets.client.C_ReturnToLogin;
import jp.l1j.server.packets.client.C_SelectList;
import jp.l1j.server.packets.client.C_SelectTarget;
import jp.l1j.server.packets.client.C_SendLocation;
import jp.l1j.server.packets.client.C_ServerVersion;
import jp.l1j.server.packets.client.C_Ship;
import jp.l1j.server.packets.client.C_Shop;
import jp.l1j.server.packets.client.C_ShopList;
import jp.l1j.server.packets.client.C_SkillBuy;
import jp.l1j.server.packets.client.C_SkillBuyOK;
import jp.l1j.server.packets.client.C_TaxRate;
import jp.l1j.server.packets.client.C_Teleport;
import jp.l1j.server.packets.client.C_Title;
import jp.l1j.server.packets.client.C_Trade;
import jp.l1j.server.packets.client.C_TradeAddItem;
import jp.l1j.server.packets.client.C_TradeCancel;
import jp.l1j.server.packets.client.C_TradeOK;
import jp.l1j.server.packets.client.C_UseItem;
import jp.l1j.server.packets.client.C_UsePetItem;
import jp.l1j.server.packets.client.C_UseSkill;
import jp.l1j.server.packets.client.C_War;
import jp.l1j.server.packets.client.C_Who;

public class PacketHandler {
	private static Logger _log = Logger.getLogger(PacketHandler.class.getName());

	private final ClientThread _client;
	
	public PacketHandler(ClientThread clientthread) {
		_client = clientthread;
	}

	public void handlePacket(byte abyte0[], L1PcInstance object) throws Exception {
		int i = abyte0[0] & 0xff;
		if (Config.DEBUG_MODE) {
			System.out.println("Client OPCODE: " + i);
		}
		switch (i) {
		case C_OPCODE_SENDLOCATION:
			new C_SendLocation(abyte0, _client);
			break;
		case C_OPCODE_EXCLUDE:
			new C_Exclude(abyte0, _client);
			break;
		case C_OPCODE_CHARACTERCONFIG:
			new C_CharcterConfig(abyte0, _client);
			break;
		case C_OPCODE_DOOR:
			new C_Door(abyte0, _client);
			break;
		case C_OPCODE_TITLE:
			new C_Title(abyte0, _client);
			break;
		case C_OPCODE_BOARDDELETE:
			new C_BoardDelete(abyte0, _client);
			break;
		case C_OPCODE_PLEDGE:
			new C_Pledge(abyte0, _client);
			break;
		case C_OPCODE_CHANGEHEADING:
			new C_ChangeHeading(abyte0, _client);
			break;
		case C_OPCODE_NPCACTION:
			new C_NpcAction(abyte0, _client);
			break;
		case C_OPCODE_USESKILL:
			new C_UseSkill(abyte0, _client);
			break;
		case C_OPCODE_EMBLEM:
			new C_Emblem(abyte0, _client);
			break;
		case C_OPCODE_TRADEADDCANCEL:
			new C_TradeCancel(abyte0, _client);
			break;
		case C_OPCODE_CHANGEWARTIME:
			new C_ChangeWarTime(abyte0, _client);
			break;
		case C_OPCODE_BOOKMARK:
			new C_AddBookmark(abyte0, _client);
			break;
		case C_OPCODE_CREATECLAN:
			new C_CreateClan(abyte0, _client);
			break;
		case C_OPCODE_CLIENTVERSION:
			new C_ServerVersion(abyte0, _client);
			break;
		case C_OPCODE_PROPOSE:
			new C_Propose(abyte0, _client);
			break;
		case C_OPCODE_SKILLBUY:
			new C_SkillBuy(abyte0, _client);
			break;
		case C_OPCODE_BOARDNEXT:
			new C_BoardBack(abyte0, _client);
			break;
		case C_OPCODE_SHOP:
			new C_Shop(abyte0, _client);
			break;
		case C_OPCODE_BOARDREAD:
			new C_BoardRead(abyte0, _client);
			break;
		case C_OPCODE_TRADE:
			new C_Trade(abyte0, _client);
			break;
		case C_OPCODE_DELETECHAR:
			new C_DeleteChar(abyte0, _client);
			break;
		case C_OPCODE_KEEPALIVE:
			new C_KeepAlive(abyte0, _client);
			break;
		case C_OPCODE_ATTR:
			new C_Attr(abyte0, _client);
			break;
		case C_OPCODE_LOGINPACKET:
			new C_AuthLogin(abyte0, _client);
			break;
		case C_OPCODE_RESULT:
			new C_Result(abyte0, _client);
			break;
		case C_OPCODE_DEPOSIT:
			new C_Deposit(abyte0, _client);
			break;
		case C_OPCODE_DRAWAL:
			new C_Drawal(abyte0, _client);
			break;
		case C_OPCODE_LOGINTOSERVEROK:
			new C_LoginToServerOK(abyte0, _client);
			break;
		case C_OPCODE_SKILLBUYOK:
			new C_SkillBuyOK(abyte0, _client);
			break;
		case C_OPCODE_TRADEADDITEM:
			new C_TradeAddItem(abyte0, _client);
			break;
		case C_OPCODE_ADDBUDDY:
			new C_AddBuddy(abyte0, _client);
			break;
		case C_OPCODE_RETURNTOLOGIN:
			new C_ReturnToLogin(abyte0, _client);
			break;
		case C_OPCODE_CHAT:
			new C_Chat(abyte0, _client);
			break;
		case C_OPCODE_TRADEADDOK:
			new C_TradeOK(abyte0, _client);
			break;
		case C_OPCODE_CHECKPK:
			new C_CheckPK(abyte0, _client);
			break;
		case C_OPCODE_TAXRATE:
			new C_TaxRate(abyte0, _client);
			break;
		case C_OPCODE_CHANGECHAR:
			new C_NewCharSelect(abyte0, _client);
			new C_CommonClick(_client);
			break;
		case C_OPCODE_BUDDYLIST:
			new C_Buddy(abyte0, _client);
			break;
		case C_OPCODE_DROPITEM:
			new C_DropItem(abyte0, _client);
			break;
		case C_OPCODE_LEAVEPARTY:
			new C_LeaveParty(abyte0, _client);
			break;
		case C_OPCODE_ATTACK:
		case C_OPCODE_ARROWATTACK:
			new C_Attack(abyte0, _client);
			break;
		// キャラクターのショートカットやインベントリの状態がプレイ中に変動した場合に
		// ショートカットやインベントリの状態を付加してクライアントから送信されてくる
		// 送られてくるタイミングはクライアント終了時
		case C_OPCODE_QUITGAME:
			break;
		case C_OPCODE_BANCLAN:
			new C_BanClan(abyte0, _client);
			break;
		case C_OPCODE_BOARD:
			new C_Board(abyte0, _client);
			break;
		case C_OPCODE_DELETEINVENTORYITEM:
			new C_DeleteInventoryItem(abyte0, _client);
			break;
		case C_OPCODE_CHATWHISPER:
			new C_ChatWhisper(abyte0, _client);
			break;
		case C_OPCODE_PARTYLIST:
			new C_Party(abyte0, _client);
			break;
		case C_OPCODE_PICKUPITEM:
			new C_PickUpItem(abyte0, _client);
			break;
		case C_OPCODE_WHO:
			new C_Who(abyte0, _client);
			break;
		case C_OPCODE_GIVEITEM:
			new C_GiveItem(abyte0, _client);
			break;
		case C_OPCODE_MOVECHAR:
			new C_MoveChar(abyte0, _client);
			break;
		case C_OPCODE_BOOKMARKDELETE:
			new C_DeleteBookmark(abyte0, _client);
			break;
		case C_OPCODE_RESTART:
			new C_Restart(abyte0, _client);
			break;
		case C_OPCODE_LEAVECLANE:
			new C_LeaveClan(abyte0, _client);
			break;
		case C_OPCODE_NPCTALK:
			new C_NpcTalk(abyte0, _client);
			break;
		case C_OPCODE_BANPARTY:
			new C_BanParty(abyte0, _client);
			break;
		case C_OPCODE_DELETEBUDDY:
			new C_DeleteBuddy(abyte0, _client);
			break;
		case C_OPCODE_WAR:
			new C_War(abyte0, _client);
			break;
		case C_OPCODE_LOGINTOSERVER:
			new C_LoginToServer(abyte0, _client);
			break;
		case C_OPCODE_PRIVATESHOPLIST:
			new C_ShopList(abyte0, _client);
			break;
		case C_OPCODE_CHATGLOBAL:
			new C_Chat(abyte0, _client);
			break;
		case C_OPCODE_JOINCLAN:
			new C_JoinClan(abyte0, _client);
			break;
		case C_OPCODE_COMMONCLICK:
			new C_CommonClick(_client);
			break;
		case C_OPCODE_NEWCHAR:
			new C_CreateChar(abyte0, _client);
			break;
		case C_OPCODE_EXTCOMMAND:
			new C_ExtraCommand(abyte0, _client);
			break;
		case C_OPCODE_BOARDWRITE:
			new C_BoardWrite(abyte0, _client);
			break;
		case C_OPCODE_USEITEM:
			new C_UseItem(abyte0, _client);
			break;
		case C_OPCODE_CREATEPARTY:
			new C_CreateParty(abyte0, _client);
			break;
		case C_OPCODE_ENTERPORTAL:
			new C_EnterPortal(abyte0, _client);
			break;
		case C_OPCODE_AMOUNT:
			new C_Amount(abyte0, _client);
			break;
		case C_OPCODE_FIX_WEAPON_LIST:
			new C_FixWeaponList(abyte0, _client);
			break;
		case C_OPCODE_SELECTLIST:
			new C_SelectList(abyte0, _client);
			break;
		case C_OPCODE_EXIT_GHOST:
			new C_ExitGhost(abyte0, _client);
			break;
		case C_OPCODE_CALL:
			new C_CallPlayer(abyte0, _client);
			break;
		case C_OPCODE_HIRESOLDIER:
			new C_HireSoldier(abyte0, _client);
			break;
		case C_OPCODE_FISHCLICK:
			new C_FishClick(abyte0, _client);
			break;
		case C_OPCODE_SELECTTARGET:
			new C_SelectTarget(abyte0, _client);
			break;
		case C_OPCODE_PETMENU:
			new C_PetMenu(abyte0, _client);
			break;
		case C_OPCODE_USEPETITEM:
			new C_UsePetItem(abyte0, _client);
			break;
		case C_OPCODE_TELEPORT:
			new C_Teleport(abyte0, _client);
			break;
		case C_OPCODE_RANK:
			new C_Rank(abyte0, _client);
			break;
		case C_OPCODE_CAHTPARTY:
			new C_ChatParty(abyte0, _client);
			break;
		case C_OPCODE_FIGHT:
			new C_Fight(abyte0, _client);
			break;
		case C_OPCODE_SHIP:
			new C_Ship(abyte0, _client);
 			break;
		case C_OPCODE_MAIL:
			new C_Mail(abyte0, _client);
			break;
		case C_OPCODE_CHARRESET:
			new C_CharReset(abyte0, _client);
			break;
		case C_OPCODE_CLAN:
			new C_Clan(abyte0, _client);
			break;
		case C_OPCODE_PLEDGE_RECOMMENDATION:
			new C_PledgeRecommendation(abyte0, _client);
			break;
		}
	}
}
