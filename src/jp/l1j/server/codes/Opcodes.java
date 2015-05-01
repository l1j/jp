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

package jp.l1j.server.codes;

public class Opcodes {

	public Opcodes() {
	}
	/** 3.63C測試 Client Packet */

	/** 要求下一頁 ( 公佈欄 )*/
	public static final int C_OPCODE_BOARDNEXT = 221;//98;//49;

	/** 要求回到輸入帳號密碼畫面 */
	public static final int C_OPCODE_RETURNTOLOGIN = 218;//68;//1;

	/** 要求船票數量 */
	public static final int C_OPCODE_SHIP = 117;//97;//-101; 

	/** 要求選擇 變更攻城時間 */
	public static final int S_OPCODE_WARTIME = 150;//44;//103;

	/** 要求領出資金 */
	public static final int C_OPCODE_DRAWAL = 192;//232;//108;

	/** 要求存入資金 */
	public static final int C_OPCODE_DEPOSIT = 35;//206;//109;

	/** 要求稅收設定封包 */
	public static final int C_OPCODE_TAXRATE = 200;//165;//1010;
	/** 要求召喚到身邊(gm) */
	public static final int C_OPCODE_CALL = 144;//90;//179;

	/** 要求使用寵物裝備 */
	public static final int C_OPCODE_USEPETITEM = 213;//49;//-1014;

	/** 要求重置人物點數 */
	public static final int C_OPCODE_CHARRESET = 236;//125;//-1016;

	/** 要求設置治安管理 */
	public static final int C_OPCODE_CASTLESECURITY = 125;//76;//-1011;

	/** 要求變更倉庫密碼 && 送出倉庫密碼 */
	public static final int C_OPCODE_WAREHOUSELOCK = 81;//147;//137;

	/** 要求賦予封號 */
	public static final int C_OPCODE_TITLE = 96;//79;//195;

	/** 要求查詢PK次數 */
	public static final int C_OPCODE_CHECKPK = 137;//83;//230;

	/** 要求血盟數據(例如盟標) */
	public static final int C_OPCODE_CLAN = 246;//156;//197;

	/** 要求紀錄快速鍵 */
	public static final int C_OPCODE_CHARACTERCONFIG = 129;//129;//42;

	/** 要求切換角色 (到選人畫面) */
	public static final int C_OPCODE_CHANGECHAR = 237;//112;//227;

	/** 要求使用遠距武器 */
	public static final int C_OPCODE_ARROWATTACK = 247;//243;

	/** 要求使用物品 */
	public static final int C_OPCODE_USEITEM = 44;//0;//106;

	/** 要求使用技能 */
	public static final int C_OPCODE_USESKILL = 115;//94;//206;

	/** 要求物件對話視窗 */
	public static final int C_OPCODE_NPCTALK = 58;//184;//21;

	/** 要求使用廣播聊天頻道 */
	public static final int C_OPCODE_CHATGLOBAL = 62;//176;//210;

	/** 要求登入測試 ( 接收伺服器版本 ) */
	public static final int C_OPCODE_CLIENTVERSION = 127;//211;//160;

	/** 要求自動登錄伺服器 與 師徒系統*/
	public static final int C_OPCODE_AUTOLOGIN = 162;//62;//204;

	/** 要求進入遊戲 */
	public static final int C_OPCODE_LOGINTOSERVER = 131;//20;//63;

	/** 要求更新時間 */
	public static final int C_OPCODE_KEEPALIVE = 182;//245;//176;

	/** 要求使用密語聊天頻道 */
	public static final int C_OPCODE_CHATWHISPER = 122;//172;//171;

	/** 要求物件對話視窗結果 */
	public static final int C_OPCODE_NPCACTION = 37;//33;//178;

	/** 要求丟棄物品 */
	public static final int C_OPCODE_DROPITEM = 54;//4;//52;

	/** 要求撿取物品 */
	public static final int C_OPCODE_PICKUPITEM = 188;//131;//92;

	/** 要求開關門  */
	public static final int C_OPCODE_DOOR = 199;//77;//143;

	/** 要求列表物品取得 */
	public static final int C_OPCODE_RESULT = 40;//115;//115;

	/** 要求刪除物品 */
	public static final int C_OPCODE_DELETEINVENTORYITEM = 209;//107;//39;

	/** 要求創造角色 */
	public static final int C_OPCODE_NEWCHAR = 253;//93;//245;

	/** 要求刪除角色 */
	public static final int C_OPCODE_DELETECHAR = 10;//50;//188;

	/** 要求查詢血盟成員 */
	public static final int C_OPCODE_PLEDGE = 225;//18;//252;

	/** 玩家傳送鎖定*/
	public static final int C_OPCODE_TELEPORTLOCK = 226;//7;//134;

	/** 要求維修物品清單 */
	public static final int C_OPCODE_FIX_WEAPON_LIST = 106;//151;//183;
	
	/** 要求傳送位置 */
	public static final int C_OPCODE_SENDLOCATION = 41;

	/** 要求學習魔法(金幣) */
	public static final int C_OPCODE_SKILLBUY = 173;//9;//140;

	/** 要求傳送 */
	public static final int C_OPCODE_TELEPORT = 242;//25;//225;

	/** 要求開個人商店 */
	public static final int C_OPCODE_SHOP = 16;//198;//69;

	/** 要求改變角色面向 */
	public static final int C_OPCODE_CHANGEHEADING = 65;//205;//234;

	/** 要求死亡後重新開始 */
	public static final int C_OPCODE_RESTART = 71;//55;//103;

	/** 要求讀取公佈欄 */
	public static final int C_OPCODE_BOARD = 73;//247;//13;

	/** 要求閱讀佈告單個欄訊息 */
	public static final int C_OPCODE_BOARDREAD = 59;//13;//55;

	/** 要求寫入公佈欄訊息 */
	public static final int C_OPCODE_BOARDWRITE = 14;//8;//28;

	/** 3.3新地图系统 */
	public static final int C_OPCODE_MAPSYSTEM = 41;//59;//254;

	/** 登入伺服器OK */
	public static final int C_OPCODE_LOGINTOSERVEROK = 75;//118;//196;

	/** 要求打開郵箱 */
	public static final int C_OPCODE_MAIL = 22;//73;//46;

	/** 要求給予角色血盟階級*/
	public static final int C_OPCODE_RANK = 88;//244;//113;

	/** 要求刪除公佈欄內容 */
	public static final int C_OPCODE_BOARDDELETE = 12;//63;//29;

	/** 要求查詢遊戲人數 */
	public static final int C_OPCODE_WHO = 49;//157;//8;

	/** 要求攻擊指定物件(寵物&召喚) */
	public static final int C_OPCODE_SELECTTARGET = 155;//38;//96;

	/** 要求新增好友 */
	public static final int C_OPCODE_ADDBUDDY = 99;//135;//175;

	/** 要求查詢朋友名單 */
	public static final int C_OPCODE_BUDDYLIST = 60;//132;//222;

	/** 要求刪除好友 */
	public static final int C_OPCODE_DELETEBUDDY = 211;//72;//233;

	/** 要求增加記憶座標 */
	public static final int C_OPCODE_BOOKMARK = 134;//29;//255;

	/** 要求刪除記憶座標 */
	public static final int C_OPCODE_BOOKMARKDELETE = 223;//23;//149;

	/** 要求給予物品 */
	public static final int C_OPCODE_GIVEITEM = 244;//250;//98;

	/** 要求寵物回報選單(顯示寵物背包物品窗口) */
	public static final int C_OPCODE_PETMENU = 217;//160;//127;

	/** 要求退出觀看模式 */
	public static final int C_OPCODE_EXIT_GHOST = 210;//154;//59;

	/** 要求角色表情動作 */
	public static final int C_OPCODE_EXTCOMMAND = 157;//182;//108;

	/** 要求加入血盟 */
	public static final int C_OPCODE_JOINCLAN = 30;;//224;//217;

	/** 要求創立血盟 */
	public static final int C_OPCODE_CREATECLAN = 154;//148;//166;

	/** 要求驅逐人物離開血盟 */
	public static final int C_OPCODE_BANCLAN = 222;//200;//118;

	/** 要求確定數量選取 */
	public static final int C_OPCODE_AMOUNT = 109;//67;//62;

	/** 要求使用拒絕名單(開啟指定人物訊息)/exclude 名字*/
	public static final int C_OPCODE_EXCLUDE = 101;//37;//72;

	/** 要求點選項目的結果 */
	public static final int C_OPCODE_ATTR = 61;//10;//5;

	/** 要求交易(個人) */
	public static final int C_OPCODE_TRADE = 103;//181;//19;

	/** 要求交易(添加物品) */
	public static final int C_OPCODE_TRADEADDITEM = 241;//188;//211;

	/** 要求完成交易(個人) */
	public static final int C_OPCODE_TRADEADDOK = 110;//170;//50;

	/** 要求取消交易(個人) */
	public static final int C_OPCODE_TRADEADDCANCEL = 167;//88;//94;

	/** 要求個人商店 （物品列表） */
	public static final int C_OPCODE_PRIVATESHOPLIST = 193;//123;//105;

	/** 要求決鬥 */
	public static final int C_OPCODE_FIGHT = 47;//41;//38;

	/** 要求邀請加入隊伍(要求創立隊伍) 3.3新增委任隊長功能*/
	public static final int C_OPCODE_CREATEPARTY = 166;//16;//145;

	/** 要求隊伍對話控制(命令/chatparty) */
	public static final int C_OPCODE_CAHTPARTY = 113;//61;//2;

	/** 要求脫離隊伍 */
	public static final int C_OPCODE_LEAVEPARTY = 204;//43;//84;

	/** 要求查看隊伍 */
	public static final int C_OPCODE_PARTYLIST = 42;//190;//174;

	/** 要求踢出隊伍 */
	public static final int C_OPCODE_BANPARTY = 70;//213;//170;

	/** 要求結婚 (指令 /求婚)*/
	public static final int C_OPCODE_PROPOSE = 185;//201;//33;

	/**要求鼠標右鍵傳入洞穴 */
	public static final int C_OPCODE_ENTERPORTAL = 249;//239;//229;

	/** 要求釣魚收桿 */
	public static final int C_OPCODE_FISHCLICK = 26;//161;//23;

	/** 要求離開遊戲 */
	public static final int C_OPCODE_QUITGAME = 104;//66;//30;

	/** 要求使用一般聊天頻道 */
	public static final int C_OPCODE_CHAT = 190;//193;//146;

	/** 要求角色移動 */
	public static final int C_OPCODE_MOVECHAR = 95;//163;//190;

	/** 要求角色攻擊 */
	public static final int C_OPCODE_ATTACK = 68;//120;//165;

	/** 要求上傳盟標 */
	public static final int C_OPCODE_EMBLEM = 107;//253;//9;

	/** 要求物品維修/取出寵物 */
	public static final int C_OPCODE_SELECTLIST = 238;//166;//205;

	/** 要求宣戰 */
	public static final int C_OPCODE_WAR = 235;//;//100;//246;

	/** 要求脫離血盟 */
	public static final int C_OPCODE_LEAVECLANE = 121;//102;//220;

	/** 要求學習魔法完成 */
	public static final int C_OPCODE_SKILLBUYOK = 207;//177;

	/** 要求下一步 ( 公告資訊 ) */
	public static final int C_OPCODE_COMMONCLICK = 53;//;//178;//1013;

	/** 要求登入伺服器 */
	public static final int C_OPCODE_LOGINPACKET = 57;//199;//191;

	/** 要求寄送簡訊 未用 */
	public static final int C_OPCODE_SMS = 45;//122;//82;

	/** 血盟推薦 */
	public static final int C_OPCODE_PLEDGE_RECOMMENDATION = 228;
	
	// 未知
	/** 請求 配置已僱用的士兵. */
	public static final int C_OPCODE_PUTSOLDIER = 3; // XXX
	/** 未使用 - 請求 配置已僱用的士兵OK. */
	public static final int C_OPCODE_PUTHIRESOLDIER = 5; // XXX
	/** 請求 配置城牆上的弓箭手OK. */
	public static final int C_OPCODE_PUTBOWSOLDIER = 7; // XXX
	/** 未使用 - 請求 進入遊戲(確定服務器登入訊息). */
	public static final int C_OPCODE_COMMONINFO = 9; // XXX
	/** 請求 決定下次圍城時間(官方已取消使用)-->修正城堡總管全部功能. */
	public static final int C_OPCODE_CHANGEWARTIME = 4;

	/** 3.63C測試 Server Packet （服務端代碼） */

	/** 效果圖示 { 水底呼吸 } */
	public static final int S_OPCODE_BLESSOFEVA = 12;//52;//0;

	/** 產生對話視窗 */
	public static final int S_OPCODE_SHOWHTML = 119;//135;//105;

	/** 物品購買 */
	public static final int S_OPCODE_SHOWSHOPBUYLIST = 254;//4;//206;

	/** 物品增加封包 */
	public static final int S_OPCODE_ADDITEM = 63;//37;//149;

	/** 物品刪除 */
	public static final int S_OPCODE_DELETEINVENTORYITEM = 148;//38;//156;

	/** 丟棄物品封包 */
	public static final int S_OPCODE_DROPITEM = 3;//39;//176;

	/** 遊戲天氣 */
	public static final int S_OPCODE_WEATHER = 193;//239;//42;

	/** 角色皇冠 */
	public static final int S_OPCODE_CASTLEMASTER = 66;//48;//85;

	/** 角色狀態 (2) */
	public static final int S_OPCODE_OWNCHARSTATUS2 = 216;//203;//41;

	/** 角色列表 */
	public static final int S_OPCODE_CHARAMOUNT = 126;//103;//62;

	/** 重置設定 */
	public static final int S_OPCODE_CHARRESET = 33;//57;//130;

	/** 角色資訊 */
	public static final int S_OPCODE_CHARLIST = 184;//167;//15;

	/** 更新角色所在的地圖 */
	public static final int S_OPCODE_MAPID = 150;//82;//113;

	/** 物件封包 */
	public static final int S_OPCODE_CHARPACK = 3;//39;//176;

	/** 更新目前遊戲時間 ( 遊戲時間 ) */
	public static final int S_OPCODE_GAMETIME = 194;//17;//38;

	/** 物件刪除 */
	public static final int S_OPCODE_REMOVE_OBJECT = 185;//75;//169;

	/** 初始化OpCode */
	public static final int S_OPCODE_INITPACKET = 161;//206;//180;

	/** 伺服器版本 */
	public static final int S_OPCODE_SERVERVERSION = 151;//106;//221;

	/** 登入狀態 */
	public static final int S_OPCODE_LOGINRESULT = 51;//109;//5;

	/** 廣播聊天頻道  */
	public static final int S_OPCODE_GLOBALCHAT = 10;//124;//137;

	/** 廣播聊天頻道 / 伺服器訊息 ( 字串 ) */
	public static final int S_OPCODE_SYSMSG = 10;//124;//137;

	/** 伺服器訊息 ( 行數 ) / ( 行數, 附加字串 ) */
	public static final int S_OPCODE_SERVERMSG = 14;//242;//212;

	/** 一般聊天頻道 */
	public static final int S_OPCODE_NORMALCHAT = 76;//81;//216;

	/** 非玩家聊天頻道 { 一般 & 大喊 } NPC */
	public static final int S_OPCODE_NPCSHOUT = 133;//251;//119;

	/** 郵件封包 */
	public static final int S_OPCODE_MAIL = 1;//113;//231;

	/** 物件移動 */
	public static final int S_OPCODE_MOVEOBJECT = 122;//137;//80;

	/** 物件屬性 (門 開關)*/
	public static final int S_OPCODE_ATTRIBUTE = 35;//138;//60;

	/** 物件攻擊 */
	public static final int S_OPCODE_ATTACKPACKET = 142;//67;//240;

	/** 正義值更新 */
	public static final int S_OPCODE_LAWFUL = 140;//56;//36;

	/** 進入遊戲 */
	public static final int S_OPCODE_LOGINTOGAME = 131;//27;//163;

	/** 物品名單 */
	public static final int S_OPCODE_INVLIST = 180;//51;//204;

	/** 物品顯示名稱 */
	public static final int S_OPCODE_ITEMNAME = 195;//230;//225;

	
	/** 封包盒子(多功能封包). */
	public static final int S_OPCODE_PACKETBOX = 40;
	
	/** 封包盒子(多功能封包). */
	public static final int S_OPCODE_ACTIVESPELLS = 40;
	
	/** 封包盒子(多功能封包). */
	public static final int S_OPCODE_SKILLICONGFX = 40;
	
	/** 魔法攻擊力與魔法防禦力 */
	public static final int S_OPCODE_SPMR = 174;//85;//210;

	/** 增加魔法進魔法名單 */
	public static final int S_OPCODE_ADDSKILL = 48;//61;//108;

	/** 角色屬性與能力值 */
	public static final int S_OPCODE_OWNCHARSTATUS = 145;//238;//255;

	/** 物品可用次數 */
	public static final int S_OPCODE_ITEMAMOUNT = 127;//227;//97;

	/** 物品狀態更新 */
	public static final int S_OPCODE_ITEMSTATUS = 127;//227;//97;

	/** 物件動作種類 ( 短時間 ) */
	public static final int S_OPCODE_DOACTIONGFX = 218;//189;//72;

	/** 體力更新 */
	public static final int S_OPCODE_HPUPDATE = 42;//245;//47;

	/** 魔力更新 */
	public static final int S_OPCODE_MPUPDATE = 73;//195;//126;

	/** 產生動畫 [ 物件 ] */
	public static final int S_OPCODE_SKILLSOUNDGFX = 232;//95;//254;

	/** 物件面向 */
	public static final int S_OPCODE_CHANGEHEADING = 199;//77;//124;

	/** 佈告欄 ( 訊息列表 ) */
	public static final int S_OPCODE_BOARD = 64;//45;//189;

	/** 佈告欄( 訊息閱讀 ) */
	public static final int S_OPCODE_BOARDREAD = 56;//194;//195;

	/** 物件隱形 & 現形 */
	public static final int S_OPCODE_INVIS = 57;//3;//186;

	/** 角色創造失敗 */
	public static final int S_OPCODE_NEWCHARWRONG = 153;//19;//74;

	/** 創造角色封包 */
	public static final int S_OPCODE_NEWCHARPACK = 212;//197;//141;

	/** 角色移除 [ 非立即 ] */
	public static final int S_OPCODE_DETELECHAROK = 5;//231;//222;

	/** 角色盟徽 */
	public static final int S_OPCODE_EMBLEM = 50;//140;//202;

	/** 產生動畫 [ 地點 ] */
	public static final int S_OPCODE_EFFECTLOCATION = 112;//132;//159;

	/** 魔法 | 物品效果 { 加速纇 } */
	public static final int S_OPCODE_SKILLHASTE = 149;//185;//24;

	/** NPC物品販賣 */
	public static final int S_OPCODE_SHOWSHOPSELLLIST = 170;//9;//53;

	/** NPC外型改變 */
	public static final int S_OPCODE_POLY = 164;//100;//182;

	/** 物件動作種類 ( 長時間 ) */
	public static final int S_OPCODE_CHARVISUALUPDATE = 113;//118;//220;

	/** 損壞武器名單 */
	public static final int S_OPCODE_SELECTLIST = 208;//129;//147;

	/** 人物回碩檢測  OR 傳送鎖定 ( 無動畫 ) */
	public static final int S_OPCODE_TELEPORTLOCK = 135;//102;//8;

	/** 要求傳送 ( NPC傳送反手 ) */
	public static final int S_OPCODE_TELEPORT = 4;//50;//170;

	/** 魔法效果 - 暗盲咒術 { 編號 } */
	public static final int S_OPCODE_CURSEBLIND = 238;//144;//178;

	/** 角色防禦 & 屬性防禦 更新 */
	public static final int S_OPCODE_OWNCHARATTRDEF = 15;//26;//226;

	/** 倉庫物品名單 */
	public static final int S_OPCODE_SHOWRETRIEVELIST = 250;//70;//248;

	/** 角色封號 */
	public static final int S_OPCODE_CHARTITLE = 202;//127;//233;

	/** 角色記憶座標名單 */
	public static final int S_OPCODE_BOOKMARKS = 11;//97;//70;

	/** 魔法購買 (金幣) */
	public static final int S_OPCODE_SKILLBUY = 222;//25;//90;

	/** 選項封包 { Yes | No } */
	public static final int S_OPCODE_YES_NO = 155;//24;//160;

	/** 交易封包 */
	public static final int S_OPCODE_TRADE = 77;//2;//146;

	/** 增加交易物品封包 */
	public static final int S_OPCODE_TRADEADDITEM = 86;//40;//131;

	/** 交易狀態 */
	public static final int S_OPCODE_TRADESTATUS = 239;//46;//181;

	/** 物件復活 */
	public static final int S_OPCODE_RESURRECTION = 227;//7;//13;

	/** 物件血條 */
	public static final int S_OPCODE_HPMETER = 128;//33;//150;

	/** 改變物件名稱 */
	public static final int S_OPCODE_CHANGENAME = 81;//175;//104;

	/** 選擇一個目標 */
	public static final int S_OPCODE_SELECTTARGET = 177;//5;//239;

	/** 魔法效果 : 中毒 { 編號 } */
	public static final int S_OPCODE_POISON = 93;//91;//183;

	/** 更新新加入或退出的血盟數據*/
	public static final int S_OPCODE_CLAN = 29;//160;//17;

	/** 魔法動畫 { 精準目標 } */
	public static final int S_OPCODE_TRUETARGET = 110;//6;//138;

	/** 魔法效果 : 防禦纇 */
	public static final int S_OPCODE_SKILLICONSHIELD = 69;//44;//81;

	/** 物件亮度 */
	public static final int S_OPCODE_LIGHT = 53;//177;//1;

	/** 撥放音效 */
	public static final int S_OPCODE_SOUND = 84;//161;//172;

	/** 物品資訊訊息 { 使用String-h.tbl } */
	public static final int S_OPCODE_IDENTIFYDESC = 43;//224;//76;

	/** 海底波紋 */
	public static final int S_OPCODE_LIQUOR = 31;//23;//120;

	/** 魔法 | 物品效果圖示 { 勇敢藥水纇 } */
	public static final int S_OPCODE_SKILLBRAVE = 200;//198;//3;

	/** 力量提升封包 */
	public static final int S_OPCODE_STRUP = 120;//31;//25;

	/** 敏捷提升封包 */
	public static final int S_OPCODE_DEXUP = 28;//253;//103;

	/** 血盟小屋名單 */
	public static final int S_OPCODE_HOUSELIST = 24;//252;//51;

	/** 血盟小屋地圖 [ 地點 ] */
	public static final int S_OPCODE_HOUSEMAP = 44;//101;//188;

	/** 拍賣公告欄選取金幣數量     選取物品數量 */
	public static final int S_OPCODE_INPUTAMOUNT = 253;//141;//136;

	/** 要求使用密語聊天頻道 */
	public static final int S_OPCODE_WHISPERCHAT = 255;//157;//75;

	/** 角色名稱變紫色 */
	public static final int S_OPCODE_PINKNAME = 252;//162;//174;

	/** 魔法效果 : 詛咒類 { 編號 } 麻痺,癱瘓 */
	public static final int S_OPCODE_PARALYSIS = 165;//226;//253;

	/** 角色個人商店 { 購買 } */
	public static final int S_OPCODE_PRIVATESHOPLIST = 190;//28;

	/** 夜視功能 */
	public static final int S_OPCODE_ABILITY = 116;//114;//144;

	/** 範圍魔法 */
	public static final int S_OPCODE_RANGESKILLS = 16;//174;//201;

	/** 移除魔法出魔法名單 */
	public static final int S_OPCODE_DELSKILL = 18;//146;//30;

	/** 物品狀態 (祝福 & 詛咒)*/
	public static final int S_OPCODE_ITEMCOLOR = 144;//98;//236;

	/** 公告視窗 */
	public static final int S_OPCODE_COMMONNEWS = 30;//249;//145;

	/** 經驗值更新封包 */
	public static final int S_OPCODE_EXP = 121;//229;//59;

	/** 畫面正中出現紅色字(Account ? has just logged in form)*/
	public static final int S_OPCODE_REDMESSAGE = 90;//53;//237;

	/** 取出城堡寶庫金幣 (1) */
	public static final int S_OPCODE_DRAWAL = 224;//16;//96;

	/** 存入資金城堡寶庫 (2) */
	public static final int S_OPCODE_DEPOSIT = 203;//84;//58;

	/** 稅收設定封包 */
	public static final int S_OPCODE_TAXRATE = 72;//128;//43;

	/** 立即中斷連線 */
	public static final int S_OPCODE_DISCONNECT = 95;//15;//175;

	/** 血盟戰爭訊息 { 編號, 血盟名稱, 目標血盟名稱 } */
	public static final int S_OPCODE_WAR = 123;//55;//123;

	/** 藍色訊息 { 使用String-h.tbl } 紅色字(地獄顯示字)*/
	public static final int S_OPCODE_BLUEMESSAGE = 59;//171;//177;

	/** 更新角色所在的地圖 （水下）*/
	public static final int S_OPCODE_UNDERWATER = 42;//74;//101;//103;//74;//49;//73;
	
	/** 寵物控制條 */
	public static final int S_OPCODE_PETCTRL = 33;

	/** 魔法購買 (材料製作)*/
	public static final int S_OPCODE_SKILLMAKE = 68;

	/** 血盟推薦 */
	public static final int S_OPCODE_PLEDGE_RECOMMENDATION = 192;
	
	// 未知
	/** 未使用 - 修理武器清單. */
	public static final int S_OPCODE_FIX_WEAPON_MENU = 10;
	/** 未使用 - 配置城牆上的弓箭手列表(傭兵購買視窗). */
	public static final int S_OPCODE_PUTBOWSOLDIERLIST = 11;
	/** 可配置排列傭兵數(HTML)(EX:僱用的總傭兵數:XX 可排列的傭兵數:XX ). */
	public static final int S_OPCODE_PUTHIRESOLDIER = 13;
	/** 請求 僱請傭兵列表(購買傭兵完成). */
	public static final int C_OPCODE_HIRESOLDIER = 97;
	/** 未使用 - 強制登出人物. */
	public static final int S_OPCODE_CHAROUT = 17;
	/** 僱請傭兵(傭兵購買視窗). */
	public static final int S_OPCODE_HIRESOLDIER = 126;
	/** 使用地圖道具.官方已棄用 */
	public static final int S_OPCODE_USEMAP = 100;
}
