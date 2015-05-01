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

package jp.l1j.server.packets.client;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.ClientThread;
import jp.l1j.server.controller.timer.HomeTownTimeController;
import jp.l1j.server.controller.timer.WarTimeController;
import jp.l1j.server.datatables.CastleTable;
import jp.l1j.server.datatables.CharacterTable;
import jp.l1j.server.datatables.DoorTable;
import jp.l1j.server.datatables.ExpTable;
import jp.l1j.server.datatables.HouseTable;
import jp.l1j.server.datatables.InnKeyTable;
import jp.l1j.server.datatables.InnTable;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.datatables.NpcActionTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.PetTable;
import jp.l1j.server.datatables.PolyTable;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.datatables.TownTable;
import jp.l1j.server.datatables.UbTable;
import jp.l1j.server.model.L1BugBearRace;
import jp.l1j.server.model.L1CastleLocation;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1DeathMatch;
import jp.l1j.server.model.L1HardinQuest;
import jp.l1j.server.model.L1HauntedHouse;
import jp.l1j.server.model.L1HouseLocation;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1OrimQuest;
import jp.l1j.server.model.L1PetMatch;
import jp.l1j.server.model.L1PolyMorph;
import jp.l1j.server.model.L1PolyRace;
import jp.l1j.server.model.L1Quest;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1TownLocation;
import jp.l1j.server.model.L1UltimateBattle;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1DoorInstance;
import jp.l1j.server.model.instance.L1HousekeeperInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1MerchantInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import jp.l1j.server.model.instance.L1TownAdvisorInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.model.item.L1ItemId;
import jp.l1j.server.model.item.executor.L1BeginnerItem;
import jp.l1j.server.model.npc.L1NpcHtml;
import jp.l1j.server.model.npc.action.L1NpcAction;
import jp.l1j.server.model.skill.L1BuffUtil;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_ApplyAuction;
import jp.l1j.server.packets.server.S_AuctionBoardRead;
import jp.l1j.server.packets.server.S_CloseList;
import jp.l1j.server.packets.server.S_DelSkill;
import jp.l1j.server.packets.server.S_Deposit;
import jp.l1j.server.packets.server.S_Drawal;
import jp.l1j.server.packets.server.S_HouseMap;
import jp.l1j.server.packets.server.S_HowManyKey;
import jp.l1j.server.packets.server.S_HpUpdate;
import jp.l1j.server.packets.server.S_ItemName;
import jp.l1j.server.packets.server.S_Lawful;
import jp.l1j.server.packets.server.S_MessageYN;
import jp.l1j.server.packets.server.S_MpUpdate;
import jp.l1j.server.packets.server.S_NpcTalkReturn;
import jp.l1j.server.packets.server.S_PetCtrlMenu;
import jp.l1j.server.packets.server.S_PetList;
import jp.l1j.server.packets.server.S_PledgeWarehouseHistory;
import jp.l1j.server.packets.server.S_RetrieveAdditionalList;
import jp.l1j.server.packets.server.S_RetrieveElfList;
import jp.l1j.server.packets.server.S_RetrieveList;
import jp.l1j.server.packets.server.S_RetrievePledgeList;
import jp.l1j.server.packets.server.S_SelectTarget;
import jp.l1j.server.packets.server.S_SellHouse;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_ShopBuyList;
import jp.l1j.server.packets.server.S_ShopSellList;
import jp.l1j.server.packets.server.S_SkillHaste;
import jp.l1j.server.packets.server.S_SkillIconAura;
import jp.l1j.server.packets.server.S_SkillIconGFX;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_TaxRate;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Castle;
import jp.l1j.server.templates.L1House;
import jp.l1j.server.templates.L1Inn;
import jp.l1j.server.templates.L1Item;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.templates.L1Skill;
import jp.l1j.server.templates.L1Town;

public class C_NpcAction extends ClientBasePacket {

	private static final String C_NPC_ACTION = "[C] C_NpcAction";
	private static Logger _log = Logger.getLogger(C_NpcAction.class.getName());
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	public C_NpcAction(byte abyte0[], ClientThread client) throws Exception {
		super(abyte0);
		int objid = readD();
		String s = readS();

		String s2 = null;
		if (s.equalsIgnoreCase("select") // 競売掲示板のリストを選択
				|| s.equalsIgnoreCase("map") // アジトの位置を確かめる
				|| s.equalsIgnoreCase("apply")) { // 競売に参加する
			s2 = readS();
		} else if (s.equalsIgnoreCase("ent")) {
			L1Object obj = L1World.getInstance().findObject(objid);
			if (obj != null && obj instanceof L1NpcInstance) {
				if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80088) {
					s2 = readS();
				}
			}
		}

		int[] materials = null;
		int[] counts = null;
		int[] createitem = null;
		int[] createcount = null;

		String htmlid = null;
		String success_htmlid = null;
		String failure_htmlid = null;
		String[] htmldata = null;

		L1PcInstance pc = client.getActiveChar();
		L1PcInstance target;
		L1Object obj = L1World.getInstance().findObject(objid);
		if (obj != null) {
			if (obj instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) obj;
				pet.onFinalAction(pc, s);
			} else if (obj instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) obj;
				summon.onFinalAction(pc, s);
			} else if (obj instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				int difflocx = Math.abs(pc.getX() - npc.getX());
				int difflocy = Math.abs(pc.getY() - npc.getY());
				int npcid = ((L1NpcInstance) obj).getNpcTemplate().getNpcId();
				// 3マス以上離れた場合アクション無効（強化ＮＰＣは12マス以上離れたら無効）
				if ((difflocx > 12 || difflocy > 12) && (npcid >= 81352 || npcid <= 81362)) {
					return;
				} else if ((difflocx > 3 || difflocy > 3) && (npcid < 81352 || npcid > 81362)) {
					return;
				}
				npc.onFinalAction(pc, s);
			} else if (obj instanceof L1PcInstance) {
				target = (L1PcInstance) obj;
				if (s.matches("[0-9]+")) {
					if (target.isSummonMonster()) {
						summonMonster(target, s);
						target.setSummonMonster(false);
					}
				} else {
					if (target.isShapeChange()) {
						L1PolyMorph.handleCommands(target, s);
						target.setShapeChange(false);
					} else {
						L1PolyMorph poly = PolyTable.getInstance().getTemplate(
								s);
						if (poly != null || s.equals("none")) {
							if (target.getInventory().checkItem(40088)
									&& usePolyScroll(target, 40088, s)) {
							}
							if (target.getInventory().checkItem(40096)
									&& usePolyScroll(target, 40096, s)) {
							}
							if (target.getInventory().checkItem(140088)
									&& usePolyScroll(target, 140088, s)) {
							}
						}
					}
				}
				return;
			}
		} else {
			// _log.warning("object not found, oid " + i);
		}

		// XML化されたアクション
		L1NpcAction action = NpcActionTable.getInstance().get(s, pc, obj);
		if (action != null) {
			L1NpcHtml result = action.execute(s, pc, obj, readByte());
			if (result != null) {
				pc.sendPackets(new S_NpcTalkReturn(obj.getId(), result));
			}
			return;
		}

		/*
		 * アクション個別処理
		 */
		if (s.equalsIgnoreCase("buy")) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			// "sell"のみ表示されるはずのNPCをチェックする。
			if (isNpcSellOnly(npc)) {
				return;
			}

			// 販売リスト表示
			pc.sendPackets(new S_ShopSellList(objid));
		} else if (s.equalsIgnoreCase("sell")) {
			int npcid = ((L1NpcInstance) obj).getNpcTemplate().getNpcId();
			if (npcid == 70523 || npcid == 70805) { // ラダー or ジュリー
				htmlid = "ladar2";
			} else if (npcid == 70537 || npcid == 70807) { // ファーリン or フィン
				htmlid = "farlin2";
			} else if (npcid == 70525 || npcid == 70804) { // ライアン or ジョエル
				htmlid = "lien2";
			} else if (npcid == 50527 || npcid == 50505 || npcid == 50519
					|| npcid == 50545 || npcid == 50531 || npcid == 50529
					|| npcid == 50516 || npcid == 50538 || npcid == 50518
					|| npcid == 50509 || npcid == 50536 || npcid == 50520
					|| npcid == 50543 || npcid == 50526 || npcid == 50512
					|| npcid == 50510 || npcid == 50504 || npcid == 50525
					|| npcid == 50534 || npcid == 50540 || npcid == 50515
					|| npcid == 50513 || npcid == 50528 || npcid == 50533
					|| npcid == 50542 || npcid == 50511 || npcid == 50501
					|| npcid == 50503 || npcid == 50508 || npcid == 50514
					|| npcid == 50532 || npcid == 50544 || npcid == 50524
					|| npcid == 50535 || npcid == 50521 || npcid == 50517
					|| npcid == 50537 || npcid == 50539 || npcid == 50507
					|| npcid == 50530 || npcid == 50502 || npcid == 50506
					|| npcid == 50522 || npcid == 50541 || npcid == 50523
					|| npcid == 50620 || npcid == 50623 || npcid == 50619
					|| npcid == 50621 || npcid == 50622 || npcid == 50624
					|| npcid == 50617 || npcid == 50614 || npcid == 50618
					|| npcid == 50616 || npcid == 50615 || npcid == 50626
					|| npcid == 50627 || npcid == 50628 || npcid == 50629
					|| npcid == 50630 || npcid == 50631) { // アジトのNPC
				String sellHouseMessage = sellHouse(pc, objid, npcid);
				if (sellHouseMessage != null) {
					htmlid = sellHouseMessage;
				}
			} else { // 一般商人

				// 買い取りリスト表示
				pc.sendPackets(new S_ShopBuyList(objid, pc));
			}
			// TODO アライメント回復
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80166) { // 善の使者
			// 　
			// ユリウス
			if (s.equalsIgnoreCase("0")) {
				if (pc.getInventory().checkItem(49303)) { // 贖罪の聖書チェック
					int allLawful = pc.getLawful() + 3000; // アライメント+3000
					if (allLawful >= 32767) {
						allLawful = 32767;
					}
					pc.setLawful(allLawful);
					S_Lawful s_lawful = new S_Lawful(pc.getId(), pc.getLawful());
					pc.sendPackets(s_lawful);
					pc.broadcastPacket(s_lawful);
					pc.getInventory().consumeItem(49303, 1); // 贖罪の聖書削除
					pc.save(); // DB儲存
					htmlid = "yuris2"; //
				} else {
					htmlid = "yuris3"; //
				}
			}
		} else if (s.equalsIgnoreCase("retrieve")) { // 「個人倉庫：アイテムを受け取る」
			if (pc.getLevel() >= 5) {
				int size = pc.getWarehouseInventory().getSize();
				if (size > 0) {
					pc.sendPackets(new S_RetrieveList(objid, pc));
				} else {
					htmlid = "uish999";
					// お客様名義であずかっている荷物はありませんが。もう一度お確かめ下さい。
				}
			}
		} else if (s.equalsIgnoreCase("retrieve-char")) { // 「追加倉庫：アイテムを受け取る」
			if (pc.getLevel() >= 5) {
				int size = pc.getAdditionalWarehouseInventory().getSize();
				if (size > 0) {
					pc.sendPackets(new S_RetrieveAdditionalList(objid, pc));
				} else {
					htmlid = "uish999";
					// お客様名義であずかっている荷物はありませんが。もう一度お確かめ下さい。
				}
			}
		} else if (s.equalsIgnoreCase("retrieve-elven")) { // 「エルフ倉庫：荷物を受け取る」
			if (pc.getLevel() >= 5 && pc.isElf()) {
				int size = pc.getElfWarehouseInventory().getSize();
				if (size > 0) {
					pc.sendPackets(new S_RetrieveElfList(objid, pc));
				} else {
					htmlid = "uish999";
					// お客様名義であずかっている荷物はありませんが。もう一度お確かめ下さい。
				}
			}
		} else if (s.equalsIgnoreCase("retrieve-pledge")) { // 「血盟倉庫：荷物を受け取る」
			if (pc.getLevel() >= 5) {
				if (pc.getClanId() == 0) {
					// \f1血盟倉庫を使用するには血盟に加入していなくてはなりません。
					pc.sendPackets(new S_ServerMessage(208));
					return;
				}
				int rank = pc.getClanRank();
				if (rank == L1Clan.CLAN_RANK_REGULAR) {
					// 一般の血盟員は、血盟倉庫を利用することはできません。
					pc.sendPackets(new S_ServerMessage(728));
					return;
				}
				L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
				int size = clan.getWarehouse().getSize();
				if (size > 0) {
					pc.sendPackets(new S_RetrievePledgeList(objid, pc));
				} else {
					htmlid = "uish999";
					// お客様名義であずかっている荷物はありませんが。もう一度お確かめ下さい。
				}
			}
		} else if(s.equalsIgnoreCase("history")){ // 血盟倉庫の使用履歴を表示
			pc.sendPackets(new S_PledgeWarehouseHistory(pc.getClanId()));
		} else if (s.equalsIgnoreCase("get")) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcId = npc.getNpcTemplate().getNpcId();
			// クーパー or ダンハム
			if (npcId == 70099 || npcId == 70796) {
				L1ItemInstance item = pc.getInventory().storeItem(20081, 1); // オイルスキンマント
				String npcName = npc.getNpcTemplate().getName();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				// f1%0が%1をくれました。
				pc.getQuest().setEnd(L1Quest.QUEST_OILSKINMANT);
				htmlid = ""; // ウィンドウを消す
			// タウンマスター：報酬をもらう
			} else if (npcId == 70528 || npcId == 70546 || npcId == 70567
					|| npcId == 70594 || npcId == 70654 || npcId == 70748
					|| npcId == 70774 || npcId == 70799 || npcId == 70815
					|| npcId == 70860) {
				int townId = pc.getHomeTownId();
				int pay = pc.getPay();
				int cb = pc.getContribution(); // 貢献度
				htmlid = "";
				if (pay < 1) {
					pc.sendPackets(new S_ServerMessage(767));// 村の支援金がありません。また来月お越しください。　
				} else if ((pay > 0) && (cb < 500)) {
					pc.sendPackets(new S_ServerMessage(766));// すでに報酬をもらっているか、貢献度不足で報酬を受けることができません。また来月お越しください。
				} else if (townId > 0) {
					double payBonus = 1.0; // cb > 499 && cb < 1000
					boolean isLeader = TownTable.getInstance().isLeader(pc,
							townId); // 村長
					L1ItemInstance item = pc.getInventory().findItemId(
							L1ItemId.ADENA);
					if ((cb > 999) && (cb < 1500)) {
						payBonus = 1.5;
					} else if ((cb > 1499) && (cb < 2000)) {
						payBonus = 2.0;
					} else if ((cb > 1999) && (cb < 2500)) {
						payBonus = 2.5;
					} else if ((cb > 2499) && (cb < 3000)) {
						payBonus = 3.0;
					} else if (cb > 2999) {
						payBonus = 4.0;
					}
					if (isLeader) {
						payBonus++;
					}
					if ((item != null)
							&& (item.getCount() + pay * payBonus > 2000000000)) {
						pc.sendPackets(new S_ServerMessage(166,
								"所持アデナが2,000,000,000を超えています。"));
						htmlid = "";
					} else if ((item != null)
							&& (item.getCount() + pay * payBonus < 2000000001)) {
						pay = (int) (HomeTownTimeController.getPay(pc.getId()) * payBonus);
						pc.getInventory().storeItem(L1ItemId.ADENA, pay);
						pc.sendPackets(new S_ServerMessage(761, "" + pay));
						pc.setPay(0);
					}
				}
			}
		} else if (s.equalsIgnoreCase("townscore")) {// 貢献度確認
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcId = npc.getNpcTemplate().getNpcId();
			if ((npcId == 70528) || (npcId == 70546) || (npcId == 70567)
					|| (npcId == 70594) || (npcId == 70654)
					|| (npcId == 70748) || (npcId == 70774)
					|| (npcId == 70799) || (npcId == 70815)
					|| (npcId == 70860)) {
				if (pc.getHomeTownId() > 0) {
					pc.sendPackets(new S_ServerMessage(1569, String
							.valueOf(pc.getContribution())));
				}
			}
		} else if (s.equalsIgnoreCase("fix")) { // 武器を修理する
		} else if (s.equalsIgnoreCase("room")) { // 部屋を借りる
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcId = npc.getNpcTemplate().getNpcId();
			boolean canRent = false;
			boolean findRoom = false;
			boolean isRent = false;
			boolean isHall = false;
			int roomNumber = 0;
			byte roomCount = 0;
			for (int i = 0; i < 16; i++) {
				L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
				if (inn != null) {
					Timestamp dueTime = inn.getDueTime();
					Calendar cal = Calendar.getInstance();
					long checkDueTime = (cal.getTimeInMillis() - dueTime.getTime()) / 1000;
					if (dueTime != null) {
						if (inn.getLodgerId() == pc.getId()
								&& checkDueTime < 0) { // 利用者判定
							if (inn.isHall()) {
								isHall = true;
							}
							isRent = true;
							break;
						}
						if ((!findRoom)) {
							if (checkDueTime >= 0) { // 利用時間以内
								canRent = true;
								findRoom = true;
								roomNumber = inn.getRoomNumber();
								break;
							} else {
								if (!inn.isHall()) { // 小部屋
									roomCount++;
								}
							}
						}
					} else {
						if (!findRoom) {
							canRent = true;
							findRoom = true;
							roomNumber = inn.getRoomNumber();
							break;
						}
					}
				}
			}

			if (isRent) {
				if (isHall) {
					htmlid = "inn15";
				} else {
					htmlid = "inn5";
				}
			}
			else if (roomCount >= 12) {
				htmlid = "inn6";
			}
			else if (canRent) {
				pc.setInnRoomNumber(roomNumber);
				pc.setHall(false);
				pc.sendPackets(new S_HowManyKey(npc, 300, 1, 8, "inn2"));
			}
		}
		else if (s.equalsIgnoreCase("hall") && (obj instanceof L1MerchantInstance)) {
			if (pc.isCrown()) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				int npcId = npc.getNpcTemplate().getNpcId();
				boolean canRent = false;
				boolean findRoom = false;
				boolean isRent = false;
				boolean isHall = false;
				int roomNumber = 0;
				byte roomCount = 0;
				for (int i = 0; i < 16; i++) {
					L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
					if (inn != null) {
						Timestamp dueTime = inn.getDueTime();
						Calendar cal = Calendar.getInstance();
						long checkDueTime = (cal.getTimeInMillis() - dueTime.getTime()) / 1000;
						if (dueTime != null) {
							if (inn.getLodgerId() == pc.getId()
									&& checkDueTime < 0) {
								if (inn.isHall()) {
									isHall = true;
								}
								isRent = true;
								break;
							}
							if ((!findRoom)) {
								if (checkDueTime >= 0) {
									canRent = true;
									findRoom = true;
									roomNumber = inn.getRoomNumber();
									break;
								} else {
									if (inn.isHall()) {
										roomCount++;
									}
								}
							}
						} else {
							if (!findRoom) {
								canRent = true;
								findRoom = true;
								roomNumber = inn.getRoomNumber();
								break;
							}
						}
					}
				}

				if (isRent) {
					if (isHall) {
						htmlid = "inn15";
					} else {
						htmlid = "inn5";
					}
				}
				else if (roomCount >= 4) {
					htmlid = "inn16";
				}
				else if (canRent) {
					pc.setInnRoomNumber(roomNumber);
					pc.setHall(true);
					pc.sendPackets(new S_HowManyKey(npc, 300, 1, 8, "inn12"));
				}
			} else {
				htmlid = "inn10";
			}
		}
		else if (s.equalsIgnoreCase("return")) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcId = npc.getNpcTemplate().getNpcId();
			int price = 0;
			boolean isBreak = false;
			for (int i = 0; i < 16; i++) {
				L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
				if (inn != null) {
					if (inn.getLodgerId() == pc.getId()) {
						Timestamp dueTime = inn.getDueTime();
						if (dueTime != null) {
							Calendar cal = Calendar.getInstance();
							if (((cal.getTimeInMillis() - dueTime.getTime()) / 1000) < 0) {
								isBreak = true;
								price += 60;
							}
						}
						Timestamp ts = new Timestamp(System.currentTimeMillis());
						inn.setDueTime(ts);
						inn.setLodgerId(0);
						inn.setKeyId(0);
						inn.setHall(false);
						InnTable.getInstance().updateInn(inn);
						break;
					}
				}
			}
			for (L1ItemInstance item : pc.getInventory().getItems()) {
				if (item.getInnNpcId() == npcId) {
					price += 20 * item.getCount();
					InnKeyTable.deleteKey(item);
					pc.getInventory().removeItem(item);
					isBreak = true;
				}
			}

			if (isBreak) {
				htmldata = new String[]  {npc.getName(), String.valueOf(price)};
				htmlid = "inn20";
				pc.getInventory().storeItem(L1ItemId.ADENA, price);
			} else {
				htmlid = "";
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 91328) {
			// ユキ - 象牙の塔秘密研究室 - ハーディンクエスト
			if (s.equalsIgnoreCase("enter")) { // 過去にテレポートする。
				boolean entrance = false;
				if (pc.isInParty()) { // パーティに所属しているか？
					if (pc.getParty().isLeader(pc)) { // リーダーか？
						int members = 1;
						for (L1PcInstance player : L1World.getInstance().getVisiblePlayer(pc)) {
							if (pc.getParty().isMember(player)) {
								members++;
								if (pc.getParty().getNumOfMembers() == members
										&& pc.getParty().getNumOfMembers()> 1) {
									// 画面内にパーティメンバーが全員いる
									entrance = true;
								}
							}
						}
					}
				}
				if (entrance) {
					if (L1HardinQuest.getInstance().getNumOfActiveMaps() < 50) {
						int instanceMap = L1HardinQuest.getInstance().setActiveMaps(9000);
						for (L1PcInstance ptmember : pc.getParty().getMembers()) {
							L1PolyMorph.undoPoly(ptmember);
							L1Teleport.teleport(ptmember, 32726, 32724,
									(short) (instanceMap), 6, true);
						}
						L1HardinQuest.getInstance().getActiveMaps(instanceMap).setLeader(pc);
						L1HardinQuest.getInstance().getActiveMaps(instanceMap).start();
					}
				}else{
					htmlid = "";
				}
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 91329) {
			// ユキ - 象牙の塔秘密研究室 - オリムクエスト(通常は3名以上)
			if (s.equalsIgnoreCase("enter")) {
				boolean entrance =false;
				if(pc.isInParty()){//パーティに所属しているか？
					if(pc.getParty().isLeader(pc)){//リーダーか？
						int members = 1;
						for(L1PcInstance player:L1World.getInstance().getVisiblePlayer(pc)){
							if(pc.getParty().isMember(player)){
								members++;
								if(pc.getParty().getNumOfMembers()==members &&
										pc.getParty().getNumOfMembers()> 1){
									// 画面内にパーティメンバーが全員いる
									entrance = true;
								}
							}
						}
					}
				}
				if(pc.isGm()){
					int instanceMap = L1OrimQuest.getInstance().setActiveMaps(9101);
					L1Teleport.teleport(pc, 32798, 32805, (short) (instanceMap), 6, true);
					L1OrimQuest.getInstance().getActiveMaps(instanceMap).start();
					return;
				}
				if(entrance){//入場します。
					if (L1OrimQuest.getInstance().getNumOfActiveMaps() < 50) {
						int instanceMap = L1OrimQuest.getInstance().setActiveMaps(9101);
						for(L1PcInstance ptmember:pc.getParty().getMembers()){
							L1PolyMorph.undoPoly(ptmember);// ptmember
							L1Teleport.teleport(ptmember, 32798, 32805, (short) (instanceMap), 6, true);
						}
						L1OrimQuest.getInstance().getActiveMaps(instanceMap).start();
					}
				}else{
					htmlid = "";
				}
			}
		} else if (s.equalsIgnoreCase("enter")) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcId = npc.getNpcTemplate().getNpcId();

			for (L1ItemInstance item : pc.getInventory().getItems()) {
				if (item.getInnNpcId() == npcId) {
					for (int i = 0; i < 16; i++) {
						L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
						if (inn.getKeyId() == item.getKeyId()) {
							Timestamp dueTime = item.getDueTime();
							if (dueTime != null) {
								Calendar cal = Calendar.getInstance();
								if (((cal.getTimeInMillis() - dueTime.getTime()) / 1000) < 0) {
									int[] data = null;
									switch (npcId) {
										case 70012:
											data = new int[] {32745, 32803, 16384, 32743, 32808, 16896};
											break;
										case 70019:
											data = new int[] {32743, 32803, 17408, 32744, 32807, 17920};
											break;
										case 70031:
											data = new int[] {32744, 32803, 18432, 32744, 32807, 18944};
											break;
										case 70065:
											data = new int[] {32744, 32803, 19456, 32744, 32807, 19968};
											break;
										case 70070:
											data = new int[] {32744, 32803, 20480, 32744, 32807, 20992};
											break;
										case 70075:
											data = new int[] {32744, 32803, 21504, 32744, 32807, 22016};
											break;
										case 70084:
											data = new int[] {32744, 32803, 22528, 32744, 32807, 23040};
											break;
										default:
											break;
									}

									pc.setInnKeyId(item.getKeyId());

									if (!item.checkRoomOrHall()) {
										L1Teleport.teleport(pc, data[0], data[1], (short) data[2], 6, true);
									} else {
										L1Teleport.teleport(pc, data[3], data[4], (short) data[5], 6, true);
										break;
									}
								}
							}
						}
					}
				}
			}
		} else if (s.equalsIgnoreCase("openigate")) { // ゲートキーパー / 城門を開ける
			L1NpcInstance npc = (L1NpcInstance) obj;
			openCloseGate(pc, npc.getNpcTemplate().getNpcId(), true);
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("closeigate")) { // ゲートキーパー / 城門を閉める
			L1NpcInstance npc = (L1NpcInstance) obj;
			openCloseGate(pc, npc.getNpcTemplate().getNpcId(), false);
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("askwartime")) { // 近衛兵 / 次の攻城戦いの時間をたずねる
			L1NpcInstance npc = (L1NpcInstance) obj;
			if (npc.getNpcTemplate().getNpcId() == 60514) { // ケント城近衛兵
				htmldata = makeWarTimeStrings(L1CastleLocation.KENT_CASTLE_ID);
				htmlid = "ktguard7";
			} else if (npc.getNpcTemplate().getNpcId() == 60560) { // オーク近衛兵
				htmldata = makeWarTimeStrings(L1CastleLocation.OT_CASTLE_ID);
				htmlid = "orcguard7";
			} else if (npc.getNpcTemplate().getNpcId() == 60552) { // ウィンダウッド城近衛兵
				htmldata = makeWarTimeStrings(L1CastleLocation.WW_CASTLE_ID);
				htmlid = "wdguard7";
			} else if (npc.getNpcTemplate().getNpcId() == 60524 || // ギラン街入り口近衛兵(
					// 弓)
					npc.getNpcTemplate().getNpcId() == 60525 || // ギラン街入り口近衛兵
					npc.getNpcTemplate().getNpcId() == 60529) { // ギラン城近衛兵
				htmldata = makeWarTimeStrings(L1CastleLocation.GIRAN_CASTLE_ID);
				htmlid = "grguard7";
			} else if (npc.getNpcTemplate().getNpcId() == 70857) { // ハイネ城ハイネガード
				htmldata = makeWarTimeStrings(L1CastleLocation.HEINE_CASTLE_ID);
				htmlid = "heguard7";
			} else if (npc.getNpcTemplate().getNpcId() == 60530 || // ドワーフ城ドワーフガード
					npc.getNpcTemplate().getNpcId() == 60531) {
				htmldata = makeWarTimeStrings(L1CastleLocation.DOWA_CASTLE_ID);
				htmlid = "dcguard7";
			} else if (npc.getNpcTemplate().getNpcId() == 60533 || // アデン城 ガード
					npc.getNpcTemplate().getNpcId() == 60534) {
				htmldata = makeWarTimeStrings(L1CastleLocation.ADEN_CASTLE_ID);
				htmlid = "adguard7";
			} else if (npc.getNpcTemplate().getNpcId() == 81156) { // アデン偵察兵（
				// ディアド要塞）
				htmldata = makeWarTimeStrings(L1CastleLocation.DIAD_CASTLE_ID);
				htmlid = "dfguard3";
			}
		} else if (s.equalsIgnoreCase("inex")) { // 収入/支出の報告を受ける
			// 暫定的に公金をチャットウィンドウに表示させる。
			// メッセージは適当。
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				int castle_id = clan.getCastleId();
				if (castle_id != 0) { // 城主クラン
					L1Castle l1castle = CastleTable.getInstance()
							.getCastleTable(castle_id);
					pc.sendPackets(new S_ServerMessage(309, // %0の精算総額は%1アデナです。
							l1castle.getName(), String.valueOf(l1castle
									.getPublicMoney())));
					htmlid = ""; // ウィンドウを消す
				}
			}
		} else if (s.equalsIgnoreCase("tax")) { // 税率を調節する
			pc.sendPackets(new S_TaxRate(pc.getId()));
		} else if (s.equalsIgnoreCase("withdrawal")) { // 資金を引き出す
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				int castle_id = clan.getCastleId();
				if (castle_id != 0) { // 城主クラン
					if (pc.getId() == clan.getLeaderId() && pc.isTax()) { // 君主
						L1Castle l1castle = CastleTable.getInstance()
								.getCastleTable(castle_id);
						pc.sendPackets(new S_Drawal(pc.getId(), l1castle
								.getPublicMoney()));
						pc.setTax(false);
					}
				}
			}
		} else if (s.equalsIgnoreCase("cdeposit")) { // 資金を入金する
			pc.sendPackets(new S_Deposit(pc.getId()));
		} else if (s.equalsIgnoreCase("employ")) { // 傭兵の雇用

		} else if (s.equalsIgnoreCase("arrange")) { // 雇用した傭兵の配置

		} else if (s.equalsIgnoreCase("castlegate")) { // 城門を管理する
			repairGate(pc);
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("encw")) { // 武器専門家 / 武器の強化魔法を受ける
			if (pc.getWeapon() == null) {
				pc.sendPackets(new S_ServerMessage(79));
			} else {
				for (L1ItemInstance item : pc.getInventory().getItems()) {
					if (pc.getWeapon().equals(item)) {
						L1SkillUse l1skilluse = new L1SkillUse();
						l1skilluse.handleCommands(pc, ENCHANT_WEAPON, item
								.getId(), 0, 0, null, 0,
								L1SkillUse.TYPE_SPELLSC);
						break;
					}
				}
			}
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("enca")) { // 防具専門家 / 防具の強化魔法を受ける
			L1ItemInstance item = pc.getInventory().getItemEquipped(2, 2);
			if (item != null) {
				L1SkillUse l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(pc, BLESSED_ARMOR, item.getId(), 0,
						0, null, 0, L1SkillUse.TYPE_SPELLSC);
			} else {
				pc.sendPackets(new S_ServerMessage(79));
			}
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("depositnpc")) { // 「動物を預ける」
			Object[] petList = pc.getPetList().values().toArray();
			for (Object petObject : petList) {
				if (petObject instanceof L1PetInstance) { // ペット
					L1PetInstance pet = (L1PetInstance) petObject;
					pc.sendPackets(new S_PetCtrlMenu(pc, pet, false));// ペットコントロールメニュー
					// ペット空腹度タイマー停止
					pet.stopFoodTimer(pet);
					pet.collect(true, pet);
					pc.getPetList().remove(pet.getId());
					pet.deleteMe();
				}
			}
			/*
			if (pc.getPetList().isEmpty()) {
				pc.sendPackets(new S_PetCtrlMenu(pc, null, false));// ペットコントロールメニュー
			} else {
				for (Object petObject : petList) {
					if (petObject instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) petObject;
						pc.sendPackets(new S_SummonPack(summon, pc));
						pc.sendPackets(new S_ServerMessage(79));
						break;
					}
				}
			}
			*/
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("withdrawnpc")) { // 「動物を受け取る」
			pc.sendPackets(new S_PetList(objid, pc));
		} else if (s.equalsIgnoreCase("aggressive")) { // 攻撃態勢
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(1);
			}
		} else if (s.equalsIgnoreCase("defensive")) { // 防御態勢
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(2);
			}
		} else if (s.equalsIgnoreCase("stay")) { // 休憩
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(3);
			}
		} else if (s.equalsIgnoreCase("extend")) { // 配備
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(4);
			}
		} else if (s.equalsIgnoreCase("alert")) { // 警戒
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(5);
			}
		} else if (s.equalsIgnoreCase("dismiss")) { // 解散
			if (obj instanceof L1PetInstance) {
				L1PetInstance l1pet = (L1PetInstance) obj;
				l1pet.setCurrentPetStatus(6);
			}
		} else if (s.equalsIgnoreCase("changename")) { // 「名前を決める」
			pc.setTempID(objid); // ペットのオブジェクトIDを保存しておく
			pc.sendPackets(new S_MessageYN(325, "")); // 動物の名前を決めてください：
		} else if (s.equalsIgnoreCase("attackchr")) {
			if (obj instanceof L1Character) {
				L1Character cha = (L1Character) obj;
				pc.sendPackets(new S_SelectTarget(cha.getId()));
			}
		} else if (s.equalsIgnoreCase("select")) { // 競売掲示板のリストをクリック
			pc.sendPackets(new S_AuctionBoardRead(objid, s2));
		} else if (s.equalsIgnoreCase("map")) { // アジトの位置を確かめる
			pc.sendPackets(new S_HouseMap(objid, s2));
		} else if (s.equalsIgnoreCase("apply")) { // 競売に参加する
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 君主、かつ、
					// 血盟主
					if (pc.getLevel() >= 15) {
						if (clan.getHouseId() == 0) {
							pc.sendPackets(new S_ApplyAuction(objid, s2));
						} else {
							pc.sendPackets(new S_ServerMessage(521)); // すでに家を所有しています

							htmlid = ""; // ウィンドウを消す
						}
					} else {
						pc.sendPackets(new S_ServerMessage(519)); // レベル15未満の君主は競売に参加できません

						htmlid = ""; // ウィンドウを消す
					}
				} else {
					pc.sendPackets(new S_ServerMessage(518)); // この命令は血盟の君主のみが利用できます

					htmlid = ""; // ウィンドウを消す
				}
			} else {
				pc.sendPackets(new S_ServerMessage(518)); // この命令は血盟の君主のみが利用できます。
				htmlid = ""; // ウィンドウを消す
			}
		} else if (s.equalsIgnoreCase("open") // ドアを開ける
				|| s.equalsIgnoreCase("close")) { // ドアを閉める
			L1NpcInstance npc = (L1NpcInstance) obj;
			openCloseDoor(pc, npc, s);
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("expel")) { // 外部の人間を追い出す
			L1NpcInstance npc = (L1NpcInstance) obj;
			expelOtherClan(pc, npc.getNpcTemplate().getNpcId());
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("pay")) { // 税金を納める
			if (obj instanceof L1HousekeeperInstance) {
				htmldata = ((L1HousekeeperInstance) obj)
						.makeHouseTaxStrings(pc);
			}
			htmlid = "agpay";
		} else if (s.equalsIgnoreCase("payfee")) { // 税金を納める
			if (obj instanceof L1HousekeeperInstance) {
				((L1HousekeeperInstance) obj).payFree(pc);
			}
			htmlid = "";
		} else if (s.equalsIgnoreCase("name")) { // 家の名前を決める
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().getNpcId() == keeperId) {
						pc.setTempID(houseId); // アジトIDを保存しておく
						pc.sendPackets(new S_MessageYN(512, "")); // 家の名前は？
					}
				}
			}
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("rem")) { // 家の中の家具をすべて取り除く
		} else if (s.equalsIgnoreCase("tel0") // テレポートする(倉庫)
				|| s.equalsIgnoreCase("tel1") // テレポートする(ペット保管所)
				|| s.equalsIgnoreCase("tel2") // テレポートする(贖罪の使者)
				|| s.equalsIgnoreCase("tel3")) { // テレポートする(ギラン市場)
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().getNpcId() == keeperId) {
						int[] loc = new int[3];
						if (s.equalsIgnoreCase("tel0")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									0);
						} else if (s.equalsIgnoreCase("tel1")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									1);
						} else if (s.equalsIgnoreCase("tel2")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									2);
						} else if (s.equalsIgnoreCase("tel3")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									3);
						}
						L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2],
								5, true);
					}
				}
			}
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("upgrade")) { // 地下アジトを作る
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().getNpcId() == keeperId) {
						if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
							// 君主、 かつ、血盟主
							if (house.isPurchaseBasement()) {
								// 既に地下アジトを所有しています。
								pc.sendPackets(new S_ServerMessage(1135));
							} else {
								if (pc.getInventory().consumeItem(
										L1ItemId.ADENA, 5000000)) {
									house.setPurchaseBasement(true);
									HouseTable.getInstance().updateHouse(house); // DBに書き込み
									// 地下アジトが生成されました。
									pc.sendPackets(new S_ServerMessage(1099));
								} else {
									// \f1アデナが不足しています。
									pc.sendPackets(new S_ServerMessage(189));
								}
							}
						} else {
							// この命令は血盟の君主のみが利用できます。
							pc.sendPackets(new S_ServerMessage(518));
						}
					}
				}
			}
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("hall")
				&& obj instanceof L1HousekeeperInstance) { // 地下アジトにテレポートする
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().getNpcId() == keeperId) {
						if (house.isPurchaseBasement()) {
							int[] loc = new int[3];
							loc = L1HouseLocation.getBasementLoc(houseId);
							L1Teleport.teleport(pc, loc[0], loc[1],
									(short) (loc[2]), 5, true);
						} else {
							// 地下アジトがないため、テレポートできません。
							pc.sendPackets(new S_ServerMessage(1098));
						}
					}
				}
			}
			htmlid = ""; // ウィンドウを消す
		}

		// ElfAttr:0.無属性,1.地属性,2.火属性,4.水属性,8.風属性
		else if (s.equalsIgnoreCase("fire")) // エルフの属性変更「火の系列を習う」
		{
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				pc.setElfAttr(2);
				pc.save(); // DBにキャラクター情報を書き込む
				pc.sendPackets(new S_SkillIconGFX(15, 1)); // 体の隅々に火の精霊力が染みこんできます。
				htmlid = ""; // ウィンドウを消す
			}
		} else if (s.equalsIgnoreCase("water")) { // エルフの属性変更「水の系列を習う」
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				pc.setElfAttr(4);
				pc.save(); // DBにキャラクター情報を書き込む
				pc.sendPackets(new S_SkillIconGFX(15, 2)); // 体の隅々に水の精霊力が染みこんできます。
				htmlid = ""; // ウィンドウを消す
			}
		} else if (s.equalsIgnoreCase("air")) { // エルフの属性変更「風の系列を習う」
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				pc.setElfAttr(8);
				pc.save(); // DBにキャラクター情報を書き込む
				pc.sendPackets(new S_SkillIconGFX(15, 3)); // 体の隅々に風の精霊力が染みこんできます。
				htmlid = ""; // ウィンドウを消す
			}
		} else if (s.equalsIgnoreCase("earth")) { // エルフの属性変更「地の系列を習う」
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				pc.setElfAttr(1);
				pc.save(); // DBにキャラクター情報を書き込む
				pc.sendPackets(new S_SkillIconGFX(15, 4)); // 体の隅々に地の精霊力が染みこんできます。
				htmlid = ""; // ウィンドウを消す
			}
		} else if (s.equalsIgnoreCase("init")) { // エルフの属性変更「精霊力を除去する」
			if (pc.isElf()) {
				if (pc.getElfAttr() == 0) {
					return;
				}
				if (!Config.LEARN_ALL_ELF_SKILLS) { // 全ての精霊魔法を習得不可
					for (int cnt = 129; cnt <= 176; cnt++) // 全エルフ魔法をチェック
					{
						L1Skill l1skills1 = SkillTable.getInstance().findBySkillId(
								cnt);
						int skill_attr = l1skills1.getAttr();
						if (skill_attr != 0) // 無属性魔法以外のエルフ魔法をDBから削除する
						{
							SkillTable.getInstance().spellLost(pc.getId(),
									l1skills1.getSkillId());
						}
					}
					pc.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 248, 252, 252, 255, 0, 0, 0, 0, 0,
							0)); // 無属性魔法以外のエルフ魔法を魔法ウィンドウから削除する
				}
				// エレメンタルプロテクションによって上昇している属性防御をリセット
				if (pc.hasSkillEffect(ELEMENTAL_PROTECTION)) {
					pc.removeSkillEffect(ELEMENTAL_PROTECTION);
				}
				// 召喚しているエレメンタルを解散
				Object[] petList = pc.getPetList().values().toArray();
				for (Object pet : petList) {
					if (pet instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) pet;
						summon.Death(pc);
					}
				}

				pc.setElfAttr(0);
				pc.save(); // DBにキャラクター情報を書き込む
				pc.sendPackets(new S_ServerMessage(678));
				htmlid = ""; // ウィンドウを消す
			}
			// TODO 善の使者　ミリエル　変身 　start
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80167) { // 善の使者
			// ミリエル

			if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) { // check
				pc.getInventory().consumeItem(L1ItemId.ADENA, 1000); // del

				L1PolyMorph.doPoly(pc, 914, 1800, L1PolyMorph.MORPH_BY_NPC);
			} else {
				pc.sendPackets(new S_ServerMessage(337, "$4")); // アデナが不足しています。
			}
			// TODO 善の使者　ミリエル　変身 end
		} else if (s.equalsIgnoreCase("exp")) { // 「経験値を回復する」
			if (pc.getExpRes() == 1) {
				int level = pc.getLevel();
				int cost = level * level * 100;
				pc.sendPackets(new S_MessageYN(738, String.valueOf(cost)));
				// 経験値を回復するには%0のアデナが必要です。経験値を回復しますか？
			} else {
				pc.sendPackets(new S_ServerMessage(739)); // 今は経験値を回復することができません。
				htmlid = ""; // ウィンドウを消す
			}
		} else if (s.equalsIgnoreCase("pk")) { // 「贖罪する」
			if (pc.getLawful() < 30000) {
				pc.sendPackets(new S_ServerMessage(559));
				// \f1まだ罪晴らしに十分な善行を行っていません。
			} else if (pc.getPkCount() < 5) {
				pc.sendPackets(new S_ServerMessage(560));
				// \f1まだ罪晴らしをする必要はありません。
			} else {
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, 700000)) {
					pc.setPkCount(pc.getPkCount() - 5);
					pc.sendPackets(new S_ServerMessage(561, String.valueOf(pc
							.getPkCount()))); // PK回数が%0になりました。
				} else {
					pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足しています。
				}
			}
			// ウィンドウを消す
			htmlid = "";
		} else if (s.equalsIgnoreCase("ent")) {
			// 「お化け屋敷に入る」
			// 「アルティメット バトルに参加する」または
			// 「観覧モードで闘技場に入る」
			// 「ステータス再分配」
			int npcId = ((L1NpcInstance) obj).getNpcId();
			if (npcId == 80085) { // お化け屋敷の管理人　デュオ
				htmlid = enterHauntedHouse(pc);
			} else if (npcId == 80088) { // ぺットマッチ管理人
				htmlid = enterPetMatch(pc, Integer.valueOf(s2));
			} else if (npcId == 80086) { // 　デスマッチ管理人　クサン
				L1DeathMatch.getInstance().enterGame(pc);
			} else if (npcId == 80087) { // 　デスマッチ管理人　ダトゥ
				L1DeathMatch.getInstance().enterGame(pc);
				// TODO ペットレース管理人 start
			} else if (npcId == 80168) { // 　ペットレース管理人　 ドゥポ
				L1PolyRace.getInstance().enterGame(pc);
				// TODO ペットレース管理人　end
			} else if (npcId == 50038 || npcId == 50042 || npcId == 50029
					|| npcId == 50019 || npcId == 50062) { // 副管理人の場合は観戦
				htmlid = watchUb(pc, npcId);
			} else if (npcId == 71251) { // ロロ
				if (!pc.getInventory().checkItem(49142)) { // 希望のロウソク
					pc.sendPackets(new S_ServerMessage(1290)); // ステータス初期化に必要なアイテムがありません。
					return;
				}
				pc.resetStatus();
			} else {
				htmlid = enterUb(pc, npcId);
			}
		} else if (s.equalsIgnoreCase("par")) { // UB関連「アルティメット バトルに参加する」 副管理人経由
			htmlid = enterUb(pc, ((L1NpcInstance) obj).getNpcId());
		} else if (s.equalsIgnoreCase("info")) { // 「情報を確認する」「競技情報を確認する」
			int npcId = ((L1NpcInstance) obj).getNpcId();
			if (npcId == 80085 || npcId == 80086 || npcId == 80087) {
			} else {
				htmlid = "colos2";
			}
		} else if (s.equalsIgnoreCase("sco")) { // UB関連「高得点者一覧を確認する」
			htmldata = new String[10];
			htmlid = "colos3";
		}

		else if (s.equalsIgnoreCase("haste")) { // ヘイスト師
			L1NpcInstance l1npcinstance = (L1NpcInstance) obj;
			int npcid = l1npcinstance.getNpcTemplate().getNpcId();
			if (npcid == 70514) {
				pc.sendPackets(new S_ServerMessage(183));
				pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1600));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
				pc.sendPackets(new S_SkillSound(pc.getId(), 755));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 755));
				pc.setMoveSpeed(1);
				pc.setSkillEffect(STATUS_HASTE, 1600 * 1000);
				htmlid = ""; // ウィンドウを消す
			}
		}
		// 変身専門家
		else if (s.equalsIgnoreCase("skeleton nbmorph")) {
			poly(client, 2374);
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("lycanthrope nbmorph")) {
			poly(client, 3874);
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("shelob nbmorph")) {
			poly(client, 95);
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("ghoul nbmorph")) {
			poly(client, 3873);
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("ghast nbmorph")) {
			poly(client, 3875);
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("atuba orc nbmorph")) {
			poly(client, 3868);
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("skeleton axeman nbmorph")) {
			poly(client, 2376);
			htmlid = ""; // ウィンドウを消す
		} else if (s.equalsIgnoreCase("troll nbmorph")) {
			poly(client, 3878);
			htmlid = ""; // ウィンドウを消す
		}
		// 長老 ノナメ
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71038) {
			// 「手紙を受け取る」
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41060, 1); // ノナメの推薦書
				String npcName = npc.getNpcTemplate().getName();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				// \f1%0が%1をくれました。
				htmlid = "orcfnoname9";
			}
			// 「調査をやめます」
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41060, 1)) {
					htmlid = "orcfnoname11";
				}
			}
		}
		// テレポーター マエノブ
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46197) {
			htmlid = teleportToi(pc, s);
		}
		// テレポーター マエノブの炎 傲慢の塔6F
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46198) {
			htmlid = teleportToi(pc, s, 0);
		}
		// テレポーター マエノブの炎 傲慢の塔16F
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46199) {
			htmlid = teleportToi(pc, s, 1);
		}
		// テレポーター マエノブの炎 傲慢の塔26F
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46200) {
			htmlid = teleportToi(pc, s, 2);
		}
		// テレポーター マエノブの炎 傲慢の塔36F
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46201) {
			htmlid = teleportToi(pc, s, 3);
		}
		// テレポーター マエノブの炎 傲慢の塔46F
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46202) {
			htmlid = teleportToi(pc, s, 4);
		}
		// テレポーター マエノブの炎 傲慢の塔56F
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46203) {
			htmlid = teleportToi(pc, s, 5);
		}
		// テレポーター マエノブの炎 傲慢の塔66F
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46204) {
			htmlid = teleportToi(pc, s, 6);
		}
		// テレポーター マエノブの炎 傲慢の塔76F
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46205) {
			htmlid = teleportToi(pc, s, 7);
		}
		// テレポーター マエノブの炎 傲慢の塔86F
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46206) {
			htmlid = teleportToi(pc, s, 8);
		}
		// テレポーター マエノブの炎 傲慢の塔96F
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46207) {
			htmlid = teleportToi(pc, s, 9);
		}
		// 佐ノ吉(ギラン → 惣構え)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46293) {
			if (s.equalsIgnoreCase("teleport jp yamato p1")) {
				L1Teleport.teleport(pc, 32814, 32806, (short) 8000, 5, true);
				htmlid = "";
			}
		}
		// 虎助
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46295) {
			if (s.equalsIgnoreCase("a")) {
				if (pc.getLevel() < 45) {
					htmlid = "jp_takesif1";
				} else if (pc.getLevel() > 54) {
					htmlid = "jp_takesif2";
				} else {
					if (pc.getInventory().checkItem(42104, 1)
							|| pc.getAdditionalWarehouseInventory().checkItem(42104, 1)) { // 武士の心得
						htmlid = "jp_takesiff";
					} else {
						if (pc.getInventory().checkItem(42103, 100)) { //封印された妖怪の魂
							pc.getInventory().consumeItem(42103, 100);
							L1BuffUtil.effectBlessOfComa(pc, BLESS_OF_SAMURAI,
							3600, 7612); // 武士の心得
							L1NpcInstance npc = (L1NpcInstance) obj;
							L1ItemInstance item = pc.getInventory().storeItem(42104, 1); // 武士の心得
							String npcName = npc.getNpcTemplate().getName();
							String itemName = item.getItem().getName();
							pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
							htmlid = "jp_takesi2";
						} else {
							htmlid = "jp_takesif3";
						}
					}
				}
			}
		}
		// 小六郎
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46296) {
			if (s.equalsIgnoreCase("a")) {
				if (pc.getLevel() < 49) {
					htmlid = "jp_kazukif1";
				} else if (pc.getLevel() > 54) {
					htmlid = "jp_kazukif2";
				} else {
					if (pc.getInventory().checkItem(42105, 1)
							|| pc.getAdditionalWarehouseInventory().checkItem(42105, 1)) { // 癒毒の巻物
						htmlid = "jp_kazukiff";
					} else {
						if (pc.getInventory().checkItem(42102, 1)) { //牛鬼の毒
							pc.getInventory().consumeItem(42102, 1);
							pc.addExp(ExpTable.getNeedExpNextLevel(pc.getLevel()) / 20); // 経験値5%
							L1NpcInstance npc = (L1NpcInstance) obj;
							L1ItemInstance item = pc.getInventory().storeItem(42105, 1); // 癒毒の巻物
							String npcName = npc.getNpcTemplate().getName();
							String itemName = item.getItem().getName();
							pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
							htmlid = "jp_kazuki2";
						} else {
							htmlid = "jp_kazukif3";
						}
					}
				}
			}
		}
		// 万吉(惣構え → ギラン)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46297) {
			if (s.equalsIgnoreCase("a")) {
				L1Teleport.teleport(pc, 33438, 32796, (short) 4, 5, true);
				htmlid = "";
			}
		}
		// 日ノ本の特典女将
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 46299) {
			if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(42101, 1)) { // 熱い勇士の汗血
					pc.getInventory().consumeItem(42101, 1);
					pc.addExp(ExpTable.getNeedExpNextLevel(Config.GIVE_EXP_LEVEL)
							/ ExpTable.getExpRate(pc.getLevel()));
					htmlid = "jp_hinowms";
				} else {
					htmlid = "jp_hinowmf";
				}
			}
		}
		// 調査団長 アトゥバ ノア
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71040) {
			// 「やってみます」
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41065, 1); // 調査団の証書
				String npcName = npc.getNpcTemplate().getName();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				// \f1%0が%1をくれました。
				htmlid = "orcfnoa4";
			}
			// 「調査をやめます」
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41065, 1)) {
					htmlid = "orcfnoa7";
				}
			}
		}
		// ネルガ フウモ
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71041) {
			// 「調査をします」
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41064, 1); // 調査団の証書
				String npcName = npc.getNpcTemplate().getName();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				// \f1%0が%1をくれました。
				htmlid = "orcfhuwoomo4";
			}
			// 「調査をやめます」
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41064, 1)) {
					htmlid = "orcfhuwoomo6";
				}
			}
		}
		// ネルガ バクモ
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71042) {
			// 「調査をします」
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41062, 1); // 調査団の証書
				String npcName = npc.getNpcTemplate().getName();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				// \f1%0が%1をくれました。
				htmlid = "orcfbakumo4";
			}
			// 「調査をやめます」
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41062, 1)) {
					htmlid = "orcfbakumo6";
				}
			}
		}
		// ドゥダ-マラ ブカ
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71043) {
			// 「調査をします」
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41063, 1); // 調査団の証書
				String npcName = npc.getNpcTemplate().getName();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				// \f1%0が%1をくれました。
				htmlid = "orcfbuka4";
			}
			// 「調査をやめます」
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41063, 1)) {
					htmlid = "orcfbuka6";
				}
			}
		}
		// ドゥダ-マラ カメ
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71044) {
			// 「調査をします」
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41061, 1); // 調査団の証書
				String npcName = npc.getNpcTemplate().getName();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				// \f1%0が%1をくれました。
				htmlid = "orcfkame4";
			}
			// 「調査をやめます」
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41061, 1)) {
					htmlid = "orcfkame6";
				}
			}
		}
		// 空間の歪み
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80048) {
			// 「やめる」
			if (s.equalsIgnoreCase("2")) {
				htmlid = ""; // ウィンドウを消す
			}
		}
		// デジェネレイト ソウル
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71095) {
			if (s.equalsIgnoreCase("teleport evil-dungeon")) { // 思念のダンジョン
				boolean find = false;
				for (Object objs : L1World.getInstance().getVisibleObjects(306).values()) {
					if (objs instanceof L1PcInstance) {
						L1PcInstance _pc = (L1PcInstance) objs;
						if (_pc != null) {  // 思念のダンジョン内に他PCがいる場合
							find = true;
							htmlid = "csoulqn"; // おまえの邪念はまだ満たされてない。
							break;
						}
					}
				}
				if (!find) {
					L1Quest quest = pc.getQuest();
					int lv50_step = quest.getStep(L1Quest.QUEST_LEVEL50);
					if (lv50_step == L1Quest.QUEST_END) {
						htmlid = "csoulq3";
					} else if (lv50_step >= 3) {
						L1Teleport.teleport(pc, 32747, 32799, (short) 306, 6, true);
					} else {
						htmlid = "csoulq2";
					}
				}
			}
		}
		// 揺らぐ者
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80049) {
			// 「バルログの意志を迎え入れる」
			if (s.equalsIgnoreCase("1")) {
				if (pc.getKarma() <= -10000000) {
					pc.setKarma(1000000);
					// バルログの笑い声が脳裏を強打します。
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "betray13";
				}
			}
		}
		// ヤヒの執政官
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80050) {
			// 「私の霊魂はヤヒ様へ…」
			if (s.equalsIgnoreCase("1")) {
				htmlid = "meet105";
			}
			// 「私の霊魂をかけてヤヒ様に忠誠を誓います…」
			else if (s.equalsIgnoreCase("2")) {
				if (pc.getInventory().checkItem(40718)) { // ブラッドクリスタルの欠片
					htmlid = "meet106";
				} else {
					htmlid = "meet110";
				}
			}
			// 「ブラッドクリスタルの欠片を1個捧げます」
			else if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().consumeItem(40718, 1)) {
					pc.addKarma((int) (-100 * Config.RATE_KARMA));
					// ヤヒの姿がだんだん近くに感じられます。
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "meet107";
				} else {
					htmlid = "meet104";
				}
			}
			// 「ブラッドクリスタルの欠片を10個捧げます」
			else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().consumeItem(40718, 10)) {
					pc.addKarma((int) (-1000 * Config.RATE_KARMA));
					// ヤヒの姿がだんだん近くに感じられます。
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "meet108";
				} else {
					htmlid = "meet104";
				}
			}
			// 「ブラッドクリスタルの欠片を100個捧げます」
			else if (s.equalsIgnoreCase("c")) {
				if (pc.getInventory().consumeItem(40718, 100)) {
					pc.addKarma((int) (-10000 * Config.RATE_KARMA));
					// ヤヒの姿がだんだん近くに感じられます。
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "meet109";
				} else {
					htmlid = "meet104";
				}
			}
			// 「ヤヒ様に会わせてください」
			else if (s.equalsIgnoreCase("d")) {
				if (pc.getInventory().checkItem(40615) // 影の神殿2階の鍵
						|| pc.getInventory().checkItem(40616)) { // 影の神殿3階の鍵
					htmlid = "";
				} else {
					L1Teleport.teleport(pc, 32683, 32895, (short) 608, 5, true);
				}
			}
		}
		// ヤヒの軍師
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80052) {
			// 私に力をくださいますよう・・・
			if (s.equalsIgnoreCase("a")) {
				if (pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
					pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
				} else {
					pc.setSkillEffect(STATUS_CURSE_BARLOG, 1020 * 1000);
					pc.sendPackets(new S_SkillIconAura(221, 1020, 2));
					pc.sendPackets(new S_SkillSound(pc.getId(), 750));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
					pc.sendPackets(new S_ServerMessage(1127));
				}
			}
		}
		// ヤヒの鍛冶屋
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80053) {
			// 「材料すべてを用意できました」
			if (s.equalsIgnoreCase("a")) {
				// バルログのツーハンド ソード / ヤヒの鍛冶屋
				int aliceMaterialId = 0;
				int karmaLevel = 0;
				int[] material = null;
				int[] count = null;
				int createItem = 0;
				String successHtmlId = null;
				String htmlId = null;

				int[] aliceMaterialIdList = { 40991, 196, 197, 198, 199, 200,
						201, 202 };
				int[] karmaLevelList = { -1, -2, -3, -4, -5, -6, -7, -8 };
				int[][] materialsList = { { 40995, 40718, 40991 },
						{ 40997, 40718, 196 }, { 40990, 40718, 197 },
						{ 40994, 40718, 198 }, { 40993, 40718, 199 },
						{ 40998, 40718, 200 }, { 40996, 40718, 201 },
						{ 40992, 40718, 202 } };
				int[][] countList = { { 100, 100, 1 }, { 100, 100, 1 },
						{ 100, 100, 1 }, { 50, 100, 1 }, { 50, 100, 1 },
						{ 50, 100, 1 }, { 10, 100, 1 }, { 10, 100, 1 } };
				int[] createItemList = { 196, 197, 198, 199, 200, 201, 202, 203 };
				String[] successHtmlIdList = { "alice_1", "alice_2", "alice_3",
						"alice_4", "alice_5", "alice_6", "alice_7", "alice_8" };
				String[] htmlIdList = { "aliceyet", "alice_1", "alice_2",
						"alice_3", "alice_4", "alice_5", "alice_5", "alice_7" };

				for (int i = 0; i < aliceMaterialIdList.length; i++) {
					if (pc.getInventory().checkItem(aliceMaterialIdList[i])) {
						aliceMaterialId = aliceMaterialIdList[i];
						karmaLevel = karmaLevelList[i];
						material = materialsList[i];
						count = countList[i];
						createItem = createItemList[i];
						successHtmlId = successHtmlIdList[i];
						htmlId = htmlIdList[i];
						break;
					}
				}

				if (aliceMaterialId == 0) {
					htmlid = "alice_no";
				} else if (aliceMaterialId == aliceMaterialId) {
					if (pc.getKarmaLevel() <= karmaLevel) {
						materials = material;
						counts = count;
						createitem = new int[] { createItem };
						createcount = new int[] { 1 };
						success_htmlid = successHtmlId;
						failure_htmlid = "alice_no";
					} else {
						htmlid = htmlId;
					}
				} else if (aliceMaterialId == 203) {
					htmlid = "alice_8";
				}
			}
		}
		// ヤヒの補佐官
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80055) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			htmlid = getYaheeAmulet(pc, npc, s);
		}
		// 業の管理者
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80056) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			if (pc.getKarma() <= -10000000) {
				getBloodCrystalByKarma(pc, npc, s);
			}
			htmlid = "";
		}
		// 次元の扉(バルログの部屋)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80063) {
			// 「中に入る」
			if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(40921)) { // 元素の支配者
					L1Teleport.teleport(pc, 32674, 32832, (short) 603, 2, true);
				} else {
					htmlid = "gpass02";
				}
			}
		}
		// バルログの執政官
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80064) {
			// 「私の永遠の主はバルログ様だけです…」
			if (s.equalsIgnoreCase("1")) {
				htmlid = "meet005";
			}
			// 「私の霊魂をかけてバルログ様に忠誠を誓います…」
			else if (s.equalsIgnoreCase("2")) {
				if (pc.getInventory().checkItem(40678)) { // ソウルクリスタルの欠片
					htmlid = "meet006";
				} else {
					htmlid = "meet010";
				}
			}
			// 「ソウルクリスタルの欠片を1個捧げます」
			else if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().consumeItem(40678, 1)) {
					pc.addKarma((int) (100 * Config.RATE_KARMA));
					// バルログの笑い声が脳裏を強打します。
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "meet007";
				} else {
					htmlid = "meet004";
				}
			}
			// 「ソウルクリスタルの欠片を10個捧げます」
			else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().consumeItem(40678, 10)) {
					pc.addKarma((int) (1000 * Config.RATE_KARMA));
					// バルログの笑い声が脳裏を強打します。
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "meet008";
				} else {
					htmlid = "meet004";
				}
			}
			// 「ソウルクリスタルの欠片を100個捧げます」
			else if (s.equalsIgnoreCase("c")) {
				if (pc.getInventory().consumeItem(40678, 100)) {
					pc.addKarma((int) (10000 * Config.RATE_KARMA));
					// バルログの笑い声が脳裏を強打します。
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "meet009";
				} else {
					htmlid = "meet004";
				}
			}
			// 「バルログ様に会わせてください」
			else if (s.equalsIgnoreCase("d")) {
				if (pc.getInventory().checkItem(40909) // 地の通行証
						|| pc.getInventory().checkItem(40910) // 水の通行証
						|| pc.getInventory().checkItem(40911) // 火の通行証
						|| pc.getInventory().checkItem(40912) // 風の通行証
						|| pc.getInventory().checkItem(40913) // 地の印章
						|| pc.getInventory().checkItem(40914) // 水の印章
						|| pc.getInventory().checkItem(40915) // 火の印章
						|| pc.getInventory().checkItem(40916) // 風の印章
						|| pc.getInventory().checkItem(40917) // 地の支配者
						|| pc.getInventory().checkItem(40918) // 水の支配者
						|| pc.getInventory().checkItem(40919) // 火の支配者
						|| pc.getInventory().checkItem(40920) // 風の支配者
						|| pc.getInventory().checkItem(40921)) { // 元素の支配者
					htmlid = "";
				} else {
					L1Teleport.teleport(pc, 32674, 32832, (short) 602, 2, true);
				}
			}
		}
		// 揺らめく者
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80066) {
			// 「カヘルの意志を受け入れる」
			if (s.equalsIgnoreCase("1")) {
				if (pc.getKarma() >= 10000000) {
					pc.setKarma(-1000000);
					// ヤヒの姿がだんだん近くに感じられます。
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "betray03";
				}
			}
		}
		// バルログの補佐官
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80071) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			htmlid = getBarlogEarring(pc, npc, s);
		}
		// バルログの軍師
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80073) {
			// 私に力をくださいますよう・・・
			if (s.equalsIgnoreCase("a")) {
				if (pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
					pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
				} else {
					pc.setSkillEffect(STATUS_CURSE_YAHEE, 1020 * 1000);
					pc.sendPackets(new S_SkillIconAura(221, 1020, 1));
					pc.sendPackets(new S_SkillSound(pc.getId(), 750));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
					pc.sendPackets(new S_ServerMessage(1127));
				}
			}
		}
		// バルログの鍛冶屋
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80072) {
			String sEquals = null;
			int karmaLevel = 0;
			int[] material = null;
			int[] count = null;
			int createItem = 0;
			String failureHtmlId = null;
			String htmlId = null;

			String[] sEqualsList = { "0", "1", "2", "3", "4", "5", "6", "7",
					"8", "a", "b", "c", "d", "e", "f", "g", "h" };
			String[] htmlIdList = { "lsmitha", "lsmithb", "lsmithc", "lsmithd",
					"lsmithe", "", "lsmithf", "lsmithg", "lsmithh" };
			int[] karmaLevelList = { 1, 2, 3, 4, 5, 6, 7, 8 };
			int[][] materialsList = { { 20158, 40669, 40678 },
					{ 20144, 40672, 40678 }, { 20075, 40671, 40678 },
					{ 20183, 40674, 40678 }, { 20190, 40674, 40678 },
					{ 20078, 40674, 40678 }, { 20078, 40670, 40678 },
					{ 40719, 40673, 40678 } };
			int[][] countList = { { 1, 50, 100 }, { 1, 50, 100 },
					{ 1, 50, 100 }, { 1, 20, 100 }, { 1, 40, 100 },
					{ 1, 5, 100 }, { 1, 1, 100 }, { 1, 1, 100 } };
			int[] createItemList = { 20083, 20131, 20069, 20179, 20209, 20290,
					20261, 20031 };
			String[] failureHtmlIdList = { "lsmithaa", "lsmithbb", "lsmithcc",
					"lsmithdd", "lsmithee", "lsmithff", "lsmithgg", "lsmithhh" };

			for (int i = 0; i < sEqualsList.length; i++) {
				if (s.equalsIgnoreCase(sEqualsList[i])) {
					sEquals = sEqualsList[i];
					if (i <= 8) {
						htmlId = htmlIdList[i];
					} else if (i > 8) {
						karmaLevel = karmaLevelList[i - 9];
						material = materialsList[i - 9];
						count = countList[i - 9];
						createItem = createItemList[i - 9];
						failureHtmlId = failureHtmlIdList[i - 9];
					}
					break;
				}
			}
			if (s.equalsIgnoreCase(sEquals)) {
				if (karmaLevel != 0 && (pc.getKarmaLevel() >= karmaLevel)) {
					materials = material;
					counts = count;
					createitem = new int[] { createItem };
					createcount = new int[] { 1 };
					success_htmlid = "";
					failure_htmlid = failureHtmlId;
				} else {
					htmlid = htmlId;
				}
			}
		}
		// 業の管理者
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80074) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			if (pc.getKarma() >= 10000000) {
				getSoulCrystalByKarma(pc, npc, s);
			}
			htmlid = "";
		}
		// アルフォンス
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80057) {
			htmlid = karmaLevelToHtmlId(pc.getKarmaLevel());
			htmldata = new String[] { String.valueOf(pc.getKarmaPercent()) };
		}
		// 次元の扉(土風水火)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80059
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80060
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80061
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80062) {
			htmlid = talkToDimensionDoor(pc, (L1NpcInstance) obj, s);
		}
		// ジャック オ ランタン
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 81124) {
			if (s.equalsIgnoreCase("1")) {
				poly(client, 4002);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("2")) {
				poly(client, 4004);
				htmlid = ""; // ウィンドウを消す
			} else if (s.equalsIgnoreCase("3")) {
				poly(client, 4950);
				htmlid = ""; // ウィンドウを消す
			}
		}

		// クエスト関連
		// 一般クエスト / ライラ
		else if (s.equalsIgnoreCase("contract1")) {
			pc.getQuest().setStep(L1Quest.QUEST_LYRA, 1);
			htmlid = "lyraev2";
		} else if (s.equalsIgnoreCase("contract1yes") || // ライラ Yes
				s.equalsIgnoreCase("contract1no")) { // ライラ No

			if (s.equalsIgnoreCase("contract1yes")) {
				htmlid = "lyraev5";
			} else if (s.equalsIgnoreCase("contract1no")) {
				pc.getQuest().setStep(L1Quest.QUEST_LYRA, 0);
				htmlid = "lyraev4";
			}
			int totem = 0;
			if (pc.getInventory().checkItem(40131)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40132)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40133)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40134)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40135)) {
				totem++;
			}
			if (totem != 0) {
				materials = new int[totem];
				counts = new int[totem];
				createitem = new int[totem];
				createcount = new int[totem];

				totem = 0;
				if (pc.getInventory().checkItem(40131)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40131);
					int i1 = l1iteminstance.getCount();
					materials[totem] = 40131;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 50;
					totem++;
				}
				if (pc.getInventory().checkItem(40132)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40132);
					int i1 = l1iteminstance.getCount();
					materials[totem] = 40132;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 100;
					totem++;
				}
				if (pc.getInventory().checkItem(40133)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40133);
					int i1 = l1iteminstance.getCount();
					materials[totem] = 40133;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 50;
					totem++;
				}
				if (pc.getInventory().checkItem(40134)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40134);
					int i1 = l1iteminstance.getCount();
					materials[totem] = 40134;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 30;
					totem++;
				}
				if (pc.getInventory().checkItem(40135)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40135);
					int i1 = l1iteminstance.getCount();
					materials[totem] = 40135;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 200;
					totem++;
				}
			}
		}
		// 最近の物価について
		else if (s.equalsIgnoreCase("pandora6")     // 70014: パンドラ
				|| s.equalsIgnoreCase("cold6")      // 70013: コルド
				|| s.equalsIgnoreCase("balsim3")    // 70010: バルシム
				|| s.equalsIgnoreCase("arieh6")     // 70015: アリエ
				|| s.equalsIgnoreCase("andyn3")     // 70016: アンディン
				|| s.equalsIgnoreCase("ysorya3")    // 70018: イソーリア
				|| s.equalsIgnoreCase("luth3")      // 70021: ロッテ
				|| s.equalsIgnoreCase("catty3")     // 70024: ケティ
				|| s.equalsIgnoreCase("mayer3")     // 70030: メイアー
				|| s.equalsIgnoreCase("vergil3")    // 70032: バージル
				|| s.equalsIgnoreCase("stella6")    // 70036: ステラ
				|| s.equalsIgnoreCase("ralf6")      // 70044: ラルフ
				|| s.equalsIgnoreCase("berry6")     // 70045: ベリー
				|| s.equalsIgnoreCase("jin6")       // 70046: ジン
				|| s.equalsIgnoreCase("defman3")    // 70047: デフマン
				|| s.equalsIgnoreCase("mellisa3")   // 70052: メリサ
				|| s.equalsIgnoreCase("mandra3")    // 70061: マンドラ
				|| s.equalsIgnoreCase("bius3")      // 70063: ビウス
				|| s.equalsIgnoreCase("momo6")      // 70069: モモ
				|| s.equalsIgnoreCase("ashurEv7")   // 70071: アシュール
				|| s.equalsIgnoreCase("elmina3")    // 70072: エルミナ
				|| s.equalsIgnoreCase("glen3")      // 70073: グレン
				|| s.equalsIgnoreCase("mellin3")    // 70074: メリン
				|| s.equalsIgnoreCase("orcm6")      // 70078: オキム
				|| s.equalsIgnoreCase("jackson3")   // 70079: ジャクソン
				|| s.equalsIgnoreCase("britt3")     // 70082: ブリット
				|| s.equalsIgnoreCase("old6")       // 70085: オールド
				|| s.equalsIgnoreCase("shivan3")) { // 70083: シバン
			htmlid = s;
			int npcid = ((L1NpcInstance) obj).getNpcTemplate().getNpcId();
			int taxRatesCastle = L1CastleLocation
					.getCastleTaxRateByNpcId(npcid);
			htmldata = new String[] { String.valueOf(taxRatesCastle) };
		}
		// タウンマスター（この村の住民に登録する）
		else if (s.equalsIgnoreCase("set")) {
			if (obj instanceof L1NpcInstance) {
				int npcid = ((L1NpcInstance) obj).getNpcTemplate().getNpcId();
				int town_id = L1TownLocation.getTownIdByNpcid(npcid);

				if (town_id >= 1 && town_id <= 10) {
					if (pc.getHomeTownId() == -1) {
						// \f1新しく住民登録を行なうには時間がかかります。時間を置いてからまた登録してください。
						pc.sendPackets(new S_ServerMessage(759));
						htmlid = "";
					} else if (pc.getHomeTownId() > 0) {
						// 既に登録してる
						if (pc.getHomeTownId() != town_id) {
							L1Town town = TownTable.getInstance().getTownTable(
									pc.getHomeTownId());
							if (town != null) {
								// 現在、あなたが住民登録している場所は%0です。
								pc.sendPackets(new S_ServerMessage(758, town
										.getName()));
							}
							htmlid = "";
						} else {
							// ありえない？
							htmlid = "";
						}
					} else if (pc.getHomeTownId() == 0) {
						// 登録
						if (pc.getLevel() < 10) {
							// \f1住民登録ができるのはレベル10以上のキャラクターです。
							pc.sendPackets(new S_ServerMessage(757));
							htmlid = "";
						} else {
							int level = pc.getLevel();
							int cost = level * level * 10;
							if (pc.getInventory().consumeItem(L1ItemId.ADENA,
									cost)) {
								pc.setHomeTownId(town_id);
								pc.setContribution(0); // 念のため
								pc.save();
							} else {
								// アデナが不足しています。
								pc.sendPackets(new S_ServerMessage(337, "$4"));
							}
							htmlid = "";
						}
					}
				}
			}
		}
		// タウンマスター（住民登録を取り消す）
		else if (s.equalsIgnoreCase("clear")) {
			if (obj instanceof L1NpcInstance) {
				int npcid = ((L1NpcInstance) obj).getNpcTemplate().getNpcId();
				int town_id = L1TownLocation.getTownIdByNpcid(npcid);
				if (town_id > 0) {
					if (pc.getHomeTownId() > 0) {
						if (pc.getHomeTownId() == town_id) {
							pc.setHomeTownId(0);
							pc.setContribution(0); // 貢献度クリア
							pc.save();
						} else {
							// \f1あなたは他の村の住民です。
							pc.sendPackets(new S_ServerMessage(756));
						}
					}
					htmlid = "";
				}
			}
		}
		// タウンマスター（村の村長が誰かを聞く）
		else if (s.equalsIgnoreCase("ask")) {
			if (obj instanceof L1NpcInstance) {
				int npcid = ((L1NpcInstance) obj).getNpcTemplate().getNpcId();
				int town_id = L1TownLocation.getTownIdByNpcid(npcid);

				if (town_id >= 1 && town_id <= 10) {
					L1Town town = TownTable.getInstance().getTownTable(town_id);
					// String leader = town.getLeaderName();
					// TODO
					String leaderName = CharacterTable.getInstance().getCharName(town.getLeaderId());
					if (leaderName != null && leaderName.length() != 0) {
						htmlid = "owner";
						htmldata = new String[] { leaderName };
					} else {
						htmlid = "noowner";
					}
				}
			}
		}
		// タウンアドバイザー
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70534
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70556
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70572
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70631
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70663
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70761
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70788
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70806
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70830
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70876) {
			L1TownAdvisorInstance advisor =
				new L1TownAdvisorInstance(((L1NpcInstance) obj).getNpcTemplate());
			advisor.create(pc, s);
			htmlid = "";
		}
		// ドロモンド
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70997) {
			// ありがとう、旅立ちます
			if (s.equalsIgnoreCase("0")) {
				createitem = new int[] { 41146 }; // ドロモンドの紹介状
				createcount = new int[] { 1 };
				pc.getQuest().setStep(L1Quest.QUEST_DOROMOND, 1);
				htmlid = "jpe0015";
			}
		}
		// アレックス(歌う島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70999) {
			// ドロモンドの紹介状を渡す
			if (s.equalsIgnoreCase("1")) {
				if (pc.getInventory().consumeItem(41146, 1)) {
					final int[] item_ids = { 23, 20219, 20193, };
					final int[] item_amounts = { 1, 1, 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getLogName()));
					}
					pc.getQuest().setStep(L1Quest.QUEST_DOROMOND, 2);
					htmlid = "";
				}
			} else if (s.equalsIgnoreCase("2")) {
				L1ItemInstance item = pc.getInventory().storeItem(41227, 1); // アレックスの紹介状
				pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj)
						.getNpcTemplate().getName(), item.getLogName()));
				pc.getQuest().setStep(L1Quest.QUEST_AREX, L1Quest.QUEST_END);
				htmlid = "";
			}
		}
		// ポピレア(歌う島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71005) {
			// アイテムを受け取る
			if (s.equalsIgnoreCase("0")) {
				if (!pc.getInventory().checkItem(41209)) {
					L1ItemInstance item = pc.getInventory().storeItem(41209, 1);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().getName(),
							item.getItem().getName()));
					htmlid = ""; // ウィンドウを消す
				}
			}
			// アイテムを受け取る
			else if (s.equalsIgnoreCase("1")) {
				if (pc.getInventory().consumeItem(41213, 1)) {
					L1ItemInstance item = pc.getInventory()
							.storeItem(40029, 20);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().getName(),
							item.getItem().getName() + " (" + 20 + ")"));
					htmlid = ""; // ウィンドウを消す
				}
			}
		}
		// ティミー(歌う島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71006) {
			if (s.equalsIgnoreCase("0")) {
				if (pc.getLevel() > 25) {
					htmlid = "jpe0057";
				} else if (pc.getInventory().checkItem(41213)) { // ティミーのバスケット
					htmlid = "jpe0056";
				} else if (pc.getInventory().checkItem(41210)
						|| pc.getInventory().checkItem(41211)) { // 研磨材、ハーブ
					htmlid = "jpe0055";
				} else if (pc.getInventory().checkItem(41209)) { // ポピリアの依頼書
					htmlid = "jpe0054";
				} else if (pc.getInventory().checkItem(41212)) { // 特製キャンディー
					htmlid = "jpe0056";
					materials = new int[] { 41212 }; // 特製キャンディー
					counts = new int[] { 1 };
					createitem = new int[] { 41213 }; // ティミーのバスケット
					createcount = new int[] { 1 };
				} else {
					htmlid = "jpe0057";
				}
			}
		}
		// カリス(歌う島ダンジョン/新隠された渓谷ダンジョン)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71029) {
			if (s.equalsIgnoreCase("teleport town-in")) {
				if (Config.START_MAP_ID == 2005) {
					L1Teleport.teleport(pc, 32691, 32864, (short) 2005, 5, true);
				} else if (Config.START_MAP_ID == 8013){
					L1Teleport.teleport(pc, 32817, 32796, (short) 8011, 5, true);
				}
				htmlid = "";
			}
		}
		// 治療師（歌う島の中：ＨＰのみ回復）
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70512) {
			// 治療を受ける("fullheal"でリクエストが来ることはあるのか？)
			if (s.equalsIgnoreCase("0") || s.equalsIgnoreCase("fullheal")) {
				int hp = _random.nextInt(21) + 70;
				pc.setCurrentHp(pc.getCurrentHp() + hp);
				pc.sendPackets(new S_ServerMessage(77));
				pc.sendPackets(new S_SkillSound(pc.getId(), 830));
				pc
						.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				htmlid = ""; // ウィンドウを消す
			}
		}
		// 治療師（訓練場：HPMP回復）
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71037) {
			if (s.equalsIgnoreCase("0") && pc.getLevel() <= 13) {
				pc.setCurrentHp(pc.getMaxHp());
				pc.setCurrentMp(pc.getMaxMp());
				pc.sendPackets(new S_ServerMessage(77));
				pc.sendPackets(new S_SkillSound(pc.getId(), 830));
				pc
						.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				pc
						.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
			}
		}
		// 治療師（西部）
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71030) {
			if (s.equalsIgnoreCase("fullheal") && pc.getLevel() <= 13) {
				if (pc.getInventory().checkItem(L1ItemId.ADENA, 5)) { // check
					pc.getInventory().consumeItem(L1ItemId.ADENA, 5); // del
					pc.setCurrentHp(pc.getMaxHp());
					pc.setCurrentMp(pc.getMaxMp());
					pc.sendPackets(new S_ServerMessage(77));
					pc.sendPackets(new S_SkillSound(pc.getId(), 830));
					pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc
							.getMaxHp()));
					pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc
							.getMaxMp()));
					if (pc.isInParty()) { // パーティー中
						pc.getParty().updateMiniHP(pc);
					}
				} else {
					pc.sendPackets(new S_ServerMessage(337, "$4"));
					// アデナが不足しています。
				}
			}
		}
		// キャンセレーション師
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71002) {
			// キャンセレーション魔法をかけてもらう
			if (s.equalsIgnoreCase("0")) {
				if (pc.getLevel() <= 13) {
					L1SkillUse skillUse = new L1SkillUse();
					skillUse.handleCommands(pc, CANCELLATION, pc.getId(), pc
							.getX(), pc.getY(), null, 0,
							L1SkillUse.TYPE_NPCBUFF, (L1NpcInstance) obj);
					htmlid = ""; // ウィンドウを消す
				}
			}
		}
		// ケスキン(歌う島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71025) {
			if (s.equalsIgnoreCase("0")) {
				L1ItemInstance item = pc.getInventory().storeItem(41225, 1); // ケスキンの発注書
				pc
						.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
				htmlid = "jpe0083";
			}
		}
		// ルケイン(海賊島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71055) {
			// アイテムを受け取る
			if (s.equalsIgnoreCase("0")) {
				L1ItemInstance item = pc.getInventory().storeItem(40701, 1); // 小さな宝の地図
				pc
						.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
				pc.getQuest().setStep(L1Quest.QUEST_LUKEIN1, 1);
				htmlid = "lukein8";
			} else if (s.equalsIgnoreCase("2")) {
				pc.getInventory().consumeItem(40631, 1);
				L1ItemInstance item = pc.getInventory().storeItem(49277, 1); // レスタのリング（修理済み）
				pc
						.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
				htmlid = "lukein12";
				pc.getQuest().setStep(L1Quest.QUEST_RESTA, 3);
			}
		}
		// 小さな箱-1番目
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71063) {
			if (s.equalsIgnoreCase("0")) {
				materials = new int[] { 40701 }; // 小さな宝の地図
				counts = new int[] { 1 };
				createitem = new int[] { 40702 }; // 小さな袋
				createcount = new int[] { 1 };
				htmlid = "maptbox1";
				pc.getQuest().setEnd(L1Quest.QUEST_TBOX1);
				int[] nextbox = { 1, 2, 3 };
				int pid = _random.nextInt(nextbox.length);
				int nb = nextbox[pid];
				if (nb == 1) { // b地点
					pc.getQuest().setStep(L1Quest.QUEST_LUKEIN1, 2);
				} else if (nb == 2) { // c地点
					pc.getQuest().setStep(L1Quest.QUEST_LUKEIN1, 3);
				} else if (nb == 3) { // d地点
					pc.getQuest().setStep(L1Quest.QUEST_LUKEIN1, 4);
				}
			}
		}
		// 小さな箱-2番目
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71064
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71065
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71066) {
			if (s.equalsIgnoreCase("0")) {
				materials = new int[] { 40701 }; // 小さな宝の地図
				counts = new int[] { 1 };
				createitem = new int[] { 40702 }; // 小さな袋
				createcount = new int[] { 1 };
				htmlid = "maptbox1";
				pc.getQuest().setEnd(L1Quest.QUEST_TBOX2);
				int[] nextbox2 = { 1, 2, 3, 4, 5, 6 };
				int pid = _random.nextInt(nextbox2.length);
				int nb2 = nextbox2[pid];
				if (nb2 == 1) { // e地点
					pc.getQuest().setStep(L1Quest.QUEST_LUKEIN1, 5);
				} else if (nb2 == 2) { // f地点
					pc.getQuest().setStep(L1Quest.QUEST_LUKEIN1, 6);
				} else if (nb2 == 3) { // g地点
					pc.getQuest().setStep(L1Quest.QUEST_LUKEIN1, 7);
				} else if (nb2 == 4) { // h地点
					pc.getQuest().setStep(L1Quest.QUEST_LUKEIN1, 8);
				} else if (nb2 == 5) { // i地点
					pc.getQuest().setStep(L1Quest.QUEST_LUKEIN1, 9);
				} else if (nb2 == 6) { // j地点
					pc.getQuest().setStep(L1Quest.QUEST_LUKEIN1, 10);
				}
			}
		}
		// シミズ(海賊島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71056) {
			// 息子を捜す
			if (s.equalsIgnoreCase("a")) {
				pc.getQuest().setStep(L1Quest.QUEST_SIMIZZ, 1);
				htmlid = "SIMIZZ7";
			} else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().checkItem(40661)
						&& pc.getInventory().checkItem(40662)
						&& pc.getInventory().checkItem(40663)) {
					htmlid = "SIMIZZ8";
					pc.getQuest().setStep(L1Quest.QUEST_SIMIZZ, 2);
					materials = new int[] { 40661, 40662, 40663 };
					counts = new int[] { 1, 1, 1 };
					createitem = new int[] { 20044 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "SIMIZZ9";
				}
			} else if (s.equalsIgnoreCase("d")) {
				htmlid = "SIMIZZ12";
				pc.getQuest().setStep(L1Quest.QUEST_SIMIZZ, L1Quest.QUEST_END);
			}
		}
		// ドイル(海賊島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71057) {
			// ラッシュについて聞く
			if (s.equalsIgnoreCase("3")) {
				htmlid = "doil4";
			} else if (s.equalsIgnoreCase("6")) {
				htmlid = "doil6";
			} else if (s.equalsIgnoreCase("1")) {
				if (pc.getInventory().checkItem(40714)) {
					htmlid = "doil8";
					materials = new int[] { 40714 };
					counts = new int[] { 1 };
					createitem = new int[] { 40647 };
					createcount = new int[] { 1 };
					pc.getQuest().setStep(L1Quest.QUEST_DOIL,
							L1Quest.QUEST_END);
				} else {
					htmlid = "doil7";
				}
			}
		}
		// ルディアン(海賊島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71059) {
			// ルディアンの頼みを受け入れる
			if (s.equalsIgnoreCase("A")) {
				htmlid = "rudian6";
				L1ItemInstance item = pc.getInventory().storeItem(40700, 1);
				pc
						.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
				pc.getQuest().setStep(L1Quest.QUEST_RUDIAN, 1);
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(40710)) {
					htmlid = "rudian8";
					materials = new int[] { 40700, 40710 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 40647 };
					createcount = new int[] { 1 };
					pc.getQuest().setStep(L1Quest.QUEST_RUDIAN,
							L1Quest.QUEST_END);
				} else {
					htmlid = "rudian9";
				}
			}
		}
		// レスタ(海賊島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71060) {
			// 仲間たちについて
			if (s.equalsIgnoreCase("A")) {
				if (pc.getQuest().getStep(L1Quest.QUEST_RUDIAN) == L1Quest.QUEST_END) {
					htmlid = "resta6";
				} else {
					htmlid = "resta4";
				}
			} else if (s.equalsIgnoreCase("B")) {
				htmlid = "resta10";
				pc.getQuest().setStep(L1Quest.QUEST_RESTA, 2);
			}
		}
		// カドムス(海賊島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71061) {
			// 地図を組み合わせてください
			if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(40647, 3)) {
					htmlid = "cadmus6";
					pc.getInventory().consumeItem(40647, 3);
					pc.getQuest().setStep(L1Quest.QUEST_CADMUS, 2);
				} else {
					htmlid = "cadmus5";
					pc.getQuest().setStep(L1Quest.QUEST_CADMUS, 1);
				}
			}
		}
		// カミーラ(海賊島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71036) {
			if (s.equalsIgnoreCase("a")) {
				htmlid = "kamyla7";
				pc.getQuest().setStep(L1Quest.QUEST_KAMYLA, 1);
			} else if (s.equalsIgnoreCase("c")) {
				htmlid = "kamyla10";
				pc.getInventory().consumeItem(40644, 1);
				pc.getQuest().setStep(L1Quest.QUEST_KAMYLA, 3);
			} else if (s.equalsIgnoreCase("e")) {
				htmlid = "kamyla13";
				pc.getInventory().consumeItem(40630, 1);
				pc.getQuest().setStep(L1Quest.QUEST_KAMYLA, 4);
			} else if (s.equalsIgnoreCase("i")) {
				htmlid = "kamyla25";
			} else if (s.equalsIgnoreCase("b")) { // カーミラ（フランコの迷宮）
				if (pc.getQuest().getStep(L1Quest.QUEST_KAMYLA) == 1) {
					L1Teleport.teleport(pc, 32679, 32742, (short) 482, 5, true);
				}
			} else if (s.equalsIgnoreCase("d")) { // カーミラ（ディエゴの閉ざされた牢）
				if (pc.getQuest().getStep(L1Quest.QUEST_KAMYLA) == 3) {
					L1Teleport.teleport(pc, 32736, 32800, (short) 483, 5, true);
				}
			} else if (s.equalsIgnoreCase("f")) { // カーミラ（ホセ地下牢）
				L1Teleport.teleport(pc, 32746, 32807, (short) 484, 5, true);
			}
		}
		// フランコ(海賊島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71089) {
			// カミーラにあなたの潔白を証明しましょう
			if (s.equalsIgnoreCase("a")) {
				htmlid = "francu10";
				L1ItemInstance item = pc.getInventory().storeItem(40644, 1);
				pc
						.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
				pc.getQuest().setStep(L1Quest.QUEST_KAMYLA, 2);
			}
		}
		// 試練のクリスタル2(海賊島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71090) {
			// はい、武器とスクロールをください
			if (s.equalsIgnoreCase("a")) {
				htmlid = "";
				final int[] item_ids = { 246, 247, 248, 249, 40660 };
				final int[] item_amounts = { 1, 1, 1, 1, 5 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().getName(),
							item.getItem().getName()));
					pc.getQuest().setStep(L1Quest.QUEST_CRYSTAL, 1);
				}
			} else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().checkEquipped(246)
						|| pc.getInventory().checkEquipped(247)
						|| pc.getInventory().checkEquipped(248)
						|| pc.getInventory().checkEquipped(249)) {
					htmlid = "jcrystal5";
				} else if (pc.getInventory().checkItem(40660)) {
					htmlid = "jcrystal4";
				} else {
					pc.getInventory().consumeItem(246, 1);
					pc.getInventory().consumeItem(247, 1);
					pc.getInventory().consumeItem(248, 1);
					pc.getInventory().consumeItem(249, 1);
					pc.getInventory().consumeItem(40620, 1);
					pc.getQuest().setStep(L1Quest.QUEST_CRYSTAL, 2);
					L1Teleport.teleport(pc, 32801, 32895, (short) 483, 4, true);
				}
			} else if (s.equalsIgnoreCase("c")) {
				if (pc.getInventory().checkEquipped(246)
						|| pc.getInventory().checkEquipped(247)
						|| pc.getInventory().checkEquipped(248)
						|| pc.getInventory().checkEquipped(249)) {
					htmlid = "jcrystal5";
				} else {
					pc.getInventory().checkItem(40660);
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40660);
					int sc = l1iteminstance.getCount();
					if (sc > 0) {
						pc.getInventory().consumeItem(40660, sc);
					} else {
					}
					pc.getInventory().consumeItem(246, 1);
					pc.getInventory().consumeItem(247, 1);
					pc.getInventory().consumeItem(248, 1);
					pc.getInventory().consumeItem(249, 1);
					pc.getInventory().consumeItem(40620, 1);
					pc.getQuest().setStep(L1Quest.QUEST_CRYSTAL, 0);
					L1Teleport.teleport(pc, 32736, 32800, (short) 483, 4, true);
				}
			}
		}
		// 試練のクリスタル2(海賊島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71091) {
			// さらば！！
			if (s.equalsIgnoreCase("a")) {
				htmlid = "";
				pc.getInventory().consumeItem(40654, 1);
				pc.getQuest()
						.setStep(L1Quest.QUEST_CRYSTAL, L1Quest.QUEST_END);
				L1Teleport.teleport(pc, 32744, 32927, (short) 483, 4, true);
			}
		}
		// リザードマンの長老(海賊島)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71074) {
			// その戦士は今どこらへんにいるんですか？
			if (s.equalsIgnoreCase("A")) {
				htmlid = "lelder5";
				pc.getQuest().setStep(L1Quest.QUEST_LIZARD, 1);
				// 宝を取り戻してきます
			} else if (s.equalsIgnoreCase("B")) {
				htmlid = "lelder10";
				pc.getInventory().consumeItem(40633, 1);
				pc.getQuest().setStep(L1Quest.QUEST_LIZARD, 3);
			} else if (s.equalsIgnoreCase("C")) {
				htmlid = "lelder13";
				if (pc.getQuest().getStep(L1Quest.QUEST_LIZARD) == L1Quest.QUEST_END) {
				}
				materials = new int[] { 40634 };
				counts = new int[] { 1 };
				createitem = new int[] { 20167 }; // リザードマングローブ
				createcount = new int[] { 1 };
				pc.getQuest().setStep(L1Quest.QUEST_LIZARD, L1Quest.QUEST_END);
			}
		}
		// 傭兵団長 ティオン
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71198) {
			if (s.equalsIgnoreCase("A")) {
				if (pc.getQuest().getStep(71198) != 0
						|| pc.getInventory().checkItem(21059, 1)
						|| pc.getAdditionalWarehouseInventory().checkItem(21059, 1)) {
					// ポイズンサーペントクローク
					return;
				}
				if (pc.getInventory().consumeItem(41339, 5)) { // 亡者のメモ
					L1ItemInstance item = ItemTable.getInstance().createItem(
							41340); // 傭兵団長 ティオンの紹介状
					if (item != null) {
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.getName(), item.getItem()
											.getName())); // \f1%0が%1をくれました。
						}
					}
					pc.getQuest().setStep(71198, 1);
					htmlid = "tion4";
				} else {
					htmlid = "tion9";
				}
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getQuest().getStep(71198) != 1
						|| pc.getInventory().checkItem(21059, 1)
						|| pc.getAdditionalWarehouseInventory().checkItem(21059, 1)) {
					// ポイズンサーペントクローク
					return;
				}
				if (pc.getInventory().consumeItem(41341, 1)) { // ジェロンの教本
					pc.getQuest().setStep(71198, 2);
					htmlid = "tion5";
				} else {
					htmlid = "tion10";
				}
			} else if (s.equalsIgnoreCase("C")) {
				if (pc.getQuest().getStep(71198) != 2
						|| pc.getInventory().checkItem(21059, 1)
						|| pc.getAdditionalWarehouseInventory().checkItem(21059, 1)) {
					// ポイズンサーペントクローク
					return;
				}
				if (pc.getInventory().consumeItem(41343, 1)) { // パプリオンの血痕
					L1ItemInstance item = ItemTable.getInstance().createItem(
							21057); // 訓練騎士のマント1
					if (item != null) {
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.getName(), item.getItem()
											.getName())); // \f1%0が%1をくれました。
						}
					}
					pc.getQuest().setStep(71198, 3);
					htmlid = "tion6";
				} else {
					htmlid = "tion12";
				}
			} else if (s.equalsIgnoreCase("D")) {
				if (pc.getQuest().getStep(71198) != 3
						|| pc.getInventory().checkItem(21059, 1)
						|| pc.getAdditionalWarehouseInventory().checkItem(21059, 1)) {
					// ポイズンサーペントクローク
					return;
				}
				if (pc.getInventory().consumeItem(41344, 1)) { // 水の精粋
					L1ItemInstance item = ItemTable.getInstance().createItem(
							21058); // 訓練騎士のマント2
					if (item != null) {
						pc.getInventory().consumeItem(21057, 1); // 訓練騎士のマント1
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.getName(), item.getItem()
											.getName())); // \f1%0が%1をくれました。
						}
					}
					pc.getQuest().setStep(71198, 4);
					htmlid = "tion7";
				} else {
					htmlid = "tion13";
				}
			} else if (s.equalsIgnoreCase("E")) {
				if (pc.getQuest().getStep(71198) != 4
						|| pc.getInventory().checkItem(21059, 1)
						|| pc.getAdditionalWarehouseInventory().checkItem(21059, 1)) {
					// ポイズンサーペントクローク
					return;
				}
				if (pc.getInventory().consumeItem(41345, 1)) { // 酸性の乳液
					L1ItemInstance item = ItemTable.getInstance().createItem(
							21059); // ポイズン サーペント クローク
					if (item != null) {
						pc.getInventory().consumeItem(21058, 1); // 訓練騎士のマント2
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.getName(), item.getItem()
											.getName())); // \f1%0が%1をくれました。
						}
					}
					pc.getQuest().setStep(71198, 0);
					pc.getQuest().setStep(71199, 0);
					htmlid = "tion8";
				} else {
					htmlid = "tion15";
				}
			}
		}
		// ジェロン
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71199) {
			if (s.equalsIgnoreCase("A")) {
				if (pc.getQuest().getStep(71199) != 0
						|| pc.getInventory().checkItem(21059, 1)
						|| pc.getAdditionalWarehouseInventory().checkItem(21059, 1)) {
					// ポイズンサーペントクローク
					return;
				}
				if (pc.getInventory().checkItem(41340, 1)) { // 傭兵団長 ティオンの紹介状
					pc.getQuest().setStep(71199, 1);
					htmlid = "jeron2";
				} else {
					htmlid = "jeron10";
				}
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getQuest().getStep(71199) != 1
						|| pc.getInventory().checkItem(21059, 1)
						|| pc.getAdditionalWarehouseInventory().checkItem(21059, 1)) {
					// ポイズンサーペントクローク
					return;
				}
				if (pc.getInventory().consumeItem(40308, 1000000)) {
					L1ItemInstance item = ItemTable.getInstance().createItem(
							41341); // ジェロンの教本
					if (item != null) {
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.getName(), item.getItem()
											.getName())); // \f1%0が%1をくれました。
						}
					}
					pc.getInventory().consumeItem(41340, 1);
					pc.getQuest().setStep(71199, 255);
					htmlid = "jeron6";
				} else {
					htmlid = "jeron8";
				}
			} else if (s.equalsIgnoreCase("C")) {
				if (pc.getQuest().getStep(71199) != 1
						|| pc.getInventory().checkItem(21059, 1)
						|| pc.getAdditionalWarehouseInventory().checkItem(21059, 1)) {
					// ポイズンサーペントクローク
					return;
				}
				if (pc.getInventory().consumeItem(41342, 1)) { // メデューサの血
					L1ItemInstance item = ItemTable.getInstance().createItem(
							41341); // ジェロンの教本
					if (item != null) {
						if (pc.getInventory().checkAddItem(item, 1) == 0) {
							pc.getInventory().storeItem(item);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate()
											.getName(), item.getItem()
											.getName())); // \f1%0が%1をくれました。
						}
					}
					pc.getInventory().consumeItem(41340, 1);
					pc.getQuest().setStep(71199, 255);
					htmlid = "jeron5";
				} else {
					htmlid = "jeron9";
				}
			}
		}
		// 占星術師ケプリシャ
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80079) {
			// ケプリシャと魂の契約を結ぶ
			if (s.equalsIgnoreCase("0")) {
				if (!pc.getInventory().checkItem(41312)
						&& !pc.getAdditionalWarehouseInventory().checkItem(41312)) { // 占星術師の壺
					L1ItemInstance item = pc.getInventory().storeItem(41312, 1);
					if (item != null) {
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
						// \f1%0が%1をくれました。
						pc.getQuest().setStep(L1Quest.QUEST_KEPLISHA,
								L1Quest.QUEST_END);
					}
					htmlid = "keplisha7";
				}
			}
			// 援助金を出して運勢を見る
			else if (s.equalsIgnoreCase("1")) {
				if (!pc.getInventory().checkItem(41314)
						&& !pc.getAdditionalWarehouseInventory().checkItem(41314)) { // 占星術師のお守り
					if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
						materials = new int[] { L1ItemId.ADENA, 41313 };
						// アデナ、占星術師の玉
						counts = new int[] { 1000, 1 };
						createitem = new int[] { 41314 }; // 占星術師のお守り
						createcount = new int[] { 1 };
						int htmlA = _random.nextInt(3) + 1;
						int htmlB = _random.nextInt(100) + 1;
						switch (htmlA) {
						case 1:
							htmlid = "horosa" + htmlB; // horosa1 ~
							// horosa100
							break;
						case 2:
							htmlid = "horosb" + htmlB; // horosb1 ~
							// horosb100
							break;
						case 3:
							htmlid = "horosc" + htmlB; // horosc1 ~
							// horosc100
							break;
						default:
							break;
						}
					} else {
						htmlid = "keplisha8";
					}
				}
			}
			// ケプリシャから祝福を受ける
			else if (s.equalsIgnoreCase("2")) {
				if (pc.getTempCharGfx() != pc.getClassId()) {
					htmlid = "keplisha9";
				} else {
					if (pc.getInventory().checkItem(41314)) { // 占星術師のお守り
						pc.getInventory().consumeItem(41314, 1); // 占星術師のお守り
						int html = _random.nextInt(9) + 1;
						int PolyId = 6180 + _random.nextInt(64);
						polyByKeplisha(client, PolyId);
						switch (html) {
						case 1:
							htmlid = "horomon11";
							break;
						case 2:
							htmlid = "horomon12";
							break;
						case 3:
							htmlid = "horomon13";
							break;
						case 4:
							htmlid = "horomon21";
							break;
						case 5:
							htmlid = "horomon22";
							break;
						case 6:
							htmlid = "horomon23";
							break;
						case 7:
							htmlid = "horomon31";
							break;
						case 8:
							htmlid = "horomon32";
							break;
						case 9:
							htmlid = "horomon33";
							break;
						default:
							break;
						}
					}
				}
			}
			// 壺を割って契約を破棄する
			else if (s.equalsIgnoreCase("3")) {
				if (pc.getInventory().checkItem(41312)) { // 占星術師の壺
					L1ItemInstance item = pc.getInventory().findItemId(41312);
					item.delete();
				}
				if (pc.getInventory().checkItem(41313)) { // 占星術師の玉
					L1ItemInstance item = pc.getInventory().findItemId(41313);
					item.delete();
				}
				if (pc.getInventory().checkItem(41314)) { // 占星術師のお守り
					L1ItemInstance item = pc.getInventory().findItemId(41314);
					item.delete();
				}
				if (pc.getAdditionalWarehouseInventory().checkItem(41312)) { // 占星術師の壺
					L1ItemInstance item = pc.getAdditionalWarehouseInventory().findItemId(41312);
					item.delete();
				}
				if (pc.getAdditionalWarehouseInventory().checkItem(41313)) { // 占星術師の玉
					L1ItemInstance item = pc.getAdditionalWarehouseInventory().findItemId(41313);
					item.delete();
				}
				if (pc.getAdditionalWarehouseInventory().checkItem(41314)) { // 占星術師のお守り
					L1ItemInstance item = pc.getAdditionalWarehouseInventory().findItemId(41314);
					item.delete();
				}
				htmlid = "";
			}
		}
		// 釣りっ子(IN)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80082) {
			// 「魚釣りをする」
			if (s.equalsIgnoreCase("a")) {
				if (pc.getLevel() < 15) {
					htmlid = "fk_in_lv"; // Lv15未満入場制限
				} else if (pc.getInventory().consumeItem(L1ItemId.ADENA, 1000)) {
					L1PolyMorph.undoPoly(pc);
					L1Teleport.teleport(pc, 32813, 32796, (short) 5124, 4, true);
				} else {
					htmlid = "fk_in_0";
				}
			}
		}
		// 釣りっ子(OUT)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80083) {
			// 「外に出る」
			if (s.equalsIgnoreCase("teleport fishing-out")) {
				L1Teleport.teleport(pc, 32613, 32781, (short) 4, 4, true);
			}
		}
		// 怪しいオーク商人 パルーム
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80084) {
			// 「資源リストをもらう」
			if (s.equalsIgnoreCase("q")) {
				if (pc.getInventory().checkItem(41356, 1)) {
					htmlid = "rparum4";
				} else {
					L1ItemInstance item = pc.getInventory().storeItem(41356, 1);
					if (item != null) {
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
						// \f1%0が%1をくれました。
					}
					htmlid = "rparum3";
				}
			}
		}
		// アデン騎馬団員
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80105) {
			// 「新たな力をくださいる」
			if (s.equalsIgnoreCase("c")) {
				if (pc.isCrown()) {
					if (pc.getInventory().checkItem(20383, 1)) {
						if (pc.getInventory().checkItem(L1ItemId.ADENA, 100000)) {
							L1ItemInstance item = pc.getInventory().findItemId(
									20383);
							if (item != null && item.getChargeCount() != 50) {
								item.setChargeCount(50);
								pc.getInventory().updateItem(item,
										L1PcInventory.COL_CHARGE_COUNT);
								pc.getInventory().consumeItem(L1ItemId.ADENA,
										100000);
								htmlid = "";
							}
						} else {
							pc.sendPackets(new S_ServerMessage(337, "$4"));
							// アデナが不足しています。
						}
					}
				}
			}
		}
		// 補佐官イリス
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71126) {
			// ラスタバド内に送ってください
			if (s.equalsIgnoreCase("0")) {
				L1Teleport.teleport(pc, 32726, 32847, (short) 451, 5, true);
			// 「はい。私がご協力しましょう」
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(41007, 1)) { // イリスの命令書：霊魂の安息
					htmlid = "eris10";
				} else {
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(41007, 1);
					String npcName = npc.getNpcTemplate().getName();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
					htmlid = "eris6";
				}
			} else if (s.equalsIgnoreCase("C")) {
				if (pc.getInventory().checkItem(41009, 1)) { // イリスの命令書：同盟の意思
					htmlid = "eris10";
				} else {
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(41009, 1);
					String npcName = npc.getNpcTemplate().getName();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
					htmlid = "eris8";
				}
			} else if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(41007, 1)) { // イリスの命令書：霊魂の安息
					if (pc.getInventory().checkItem(40969, 20)) { // ダークエルフ魂の結晶体
						htmlid = "eris18";
						materials = new int[] { 40969, 41007 };
						counts = new int[] { 20, 1 };
						createitem = new int[] { 41008 }; // イリスのバック
						createcount = new int[] { 1 };
					} else {
						htmlid = "eris5";
					}
				} else {
					htmlid = "eris2";
				}
			} else if (s.equalsIgnoreCase("E")) {
				if (pc.getInventory().checkItem(41010, 1)) { // イリスの推薦書
					htmlid = "eris19";
				} else {
					htmlid = "eris7";
				}
			} else if (s.equalsIgnoreCase("D")) {
				if (pc.getInventory().checkItem(41010, 1)) { // イリスの推薦書
					htmlid = "eris19";
				} else {
					if (pc.getInventory().checkItem(41009, 1)) { // イリスの命令書：同盟の意思
						if (pc.getInventory().checkItem(40959, 1)) { // 冥法軍王の印章
							htmlid = "eris17";
							materials = new int[] { 40959, 41009 }; // 冥法軍王の印章
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 }; // イリスの推薦書
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40960, 1)) { // 魔霊軍王の印章
							htmlid = "eris16";
							materials = new int[] { 40960, 41009 }; // 魔霊軍王の印章
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 }; // イリスの推薦書
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40961, 1)) { // 魔獣霊軍王の印章
							htmlid = "eris15";
							materials = new int[] { 40961, 41009 }; // 魔獣軍王の印章
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 }; // イリスの推薦書
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40962, 1)) { // 暗殺軍王の印章
							htmlid = "eris14";
							materials = new int[] { 40962, 41009 }; // 暗殺軍王の印章
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 }; // イリスの推薦書
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40635, 10)) { // 魔霊軍のバッジ
							htmlid = "eris12";
							materials = new int[] { 40635, 41009 }; // 魔霊軍のバッジ
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 }; // イリスの推薦書
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40638, 10)) { // 魔獣軍のバッジ
							htmlid = "eris11";
							materials = new int[] { 40638, 41009 }; // 魔霊軍のバッジ
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 }; // イリスの推薦書
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40642, 10)) { // 冥法軍のバッジ
							htmlid = "eris13";
							materials = new int[] { 40642, 41009 }; // 冥法軍のバッジ
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 }; // イリスの推薦書
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40667, 10)) { // 暗殺軍のバッジ
							htmlid = "eris13";
							materials = new int[] { 40667, 41009 }; // 暗殺軍のバッジ
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 }; // イリスの推薦書
							createcount = new int[] { 1 };
						} else {
							htmlid = "eris8";
						}
					} else {
						htmlid = "eris7";
					}
				}
			}
		}
		// 倒れた航海士
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80076) {
			if (s.equalsIgnoreCase("A")) {
				int[] diaryno = { 49082, 49083 };
				int pid = _random.nextInt(diaryno.length);
				int di = diaryno[pid];
				if (di == 49082) { // 奇数ページ抜け
					htmlid = "voyager6a";
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(di, 1);
					String npcName = npc.getNpcTemplate().getName();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				} else if (di == 49083) { // 偶数ページ抜け
					htmlid = "voyager6b";
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(di, 1);
					String npcName = npc.getNpcTemplate().getName();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
				}
			}
		}
		// 錬金術師 ペリター
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71128) {
			if (s.equals("A")) {
				if (pc.getInventory().checkItem(41010, 1)) { // イリスの推薦書
					htmlid = "perita2";
				} else {
					htmlid = "perita3";
				}
			} else if (s.equals("p")) {
				// 呪われたブラックイアリング判別
				if (pc.getInventory().checkItem(40987, 1) // ウィザードクラス
						&& pc.getInventory().checkItem(40988, 1) // ナイトクラス
						&& pc.getInventory().checkItem(40989, 1)) { // ウォーリアクラス
					htmlid = "perita43";
				} else if (pc.getInventory().checkItem(40987, 1) // ウィザードクラス
						&& pc.getInventory().checkItem(40989, 1)) { // ウォーリアクラス
					htmlid = "perita44";
				} else if (pc.getInventory().checkItem(40987, 1) // ウィザードクラス
						&& pc.getInventory().checkItem(40988, 1)) { // ナイトクラス
					htmlid = "perita45";
				} else if (pc.getInventory().checkItem(40988, 1) // ナイトクラス
						&& pc.getInventory().checkItem(40989, 1)) { // ウォーリアクラス
					htmlid = "perita47";
				} else if (pc.getInventory().checkItem(40987, 1)) { // ウィザードクラス
					htmlid = "perita46";
				} else if (pc.getInventory().checkItem(40988, 1)) { // ナイトクラス
					htmlid = "perita49";
				} else if (pc.getInventory().checkItem(40987, 1)) { // ウォーリアクラス
					htmlid = "perita48";
				} else {
					htmlid = "perita50";
				}
			} else if (s.equals("q")) {
				// ブラックイアリング判別
				if (pc.getInventory().checkItem(41173, 1) // ウィザードクラス
						&& pc.getInventory().checkItem(41174, 1) // ナイトクラス
						&& pc.getInventory().checkItem(41175, 1)) { // ウォーリアクラス
					htmlid = "perita54";
				} else if (pc.getInventory().checkItem(41173, 1) // ウィザードクラス
						&& pc.getInventory().checkItem(41175, 1)) { // ウォーリアクラス
					htmlid = "perita55";
				} else if (pc.getInventory().checkItem(41173, 1) // ウィザードクラス
						&& pc.getInventory().checkItem(41174, 1)) { // ナイトクラス
					htmlid = "perita56";
				} else if (pc.getInventory().checkItem(41174, 1) // ナイトクラス
						&& pc.getInventory().checkItem(41175, 1)) { // ウォーリアクラス
					htmlid = "perita58";
				} else if (pc.getInventory().checkItem(41174, 1)) { // ウィザードクラス
					htmlid = "perita57";
				} else if (pc.getInventory().checkItem(41175, 1)) { // ナイトクラス
					htmlid = "perita60";
				} else if (pc.getInventory().checkItem(41176, 1)) { // ウォーリアクラス
					htmlid = "perita59";
				} else {
					htmlid = "perita61";
				}
			} else if (s.equals("s")) {
				// ミステリアス ブラックイアリング判別
				if (pc.getInventory().checkItem(41161, 1) // ウィザードクラス
						&& pc.getInventory().checkItem(41162, 1) // ナイトクラス
						&& pc.getInventory().checkItem(41163, 1)) { // ウォーリアクラス
					htmlid = "perita62";
				} else if (pc.getInventory().checkItem(41161, 1) // ウィザードクラス
						&& pc.getInventory().checkItem(41163, 1)) { // ウォーリアクラス
					htmlid = "perita63";
				} else if (pc.getInventory().checkItem(41161, 1) // ウィザードクラス
						&& pc.getInventory().checkItem(41162, 1)) { // ナイトクラス
					htmlid = "perita64";
				} else if (pc.getInventory().checkItem(41162, 1) // ナイトクラス
						&& pc.getInventory().checkItem(41163, 1)) { // ウォーリアクラス
					htmlid = "perita66";
				} else if (pc.getInventory().checkItem(41161, 1)) { // ウィザードクラス
					htmlid = "perita65";
				} else if (pc.getInventory().checkItem(41162, 1)) { // ナイトクラス
					htmlid = "perita68";
				} else if (pc.getInventory().checkItem(41163, 1)) { // ウォーリアクラス
					htmlid = "perita67";
				} else {
					htmlid = "perita69";
				}
			} else if (s.equals("B")) {
				// 浄化のポーション
				if (pc.getInventory().checkItem(40651, 10) // 火の息吹
						&& pc.getInventory().checkItem(40643, 10) // 水の息吹
						&& pc.getInventory().checkItem(40618, 10) // 大地の息吹
						&& pc.getInventory().checkItem(40645, 10) // 風の息吹
						&& pc.getInventory().checkItem(40676, 10) // 闇の息吹
						&& pc.getInventory().checkItem(40442, 5) // プロッブの胃液
						&& pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
					htmlid = "perita7";
					materials = new int[] { 40651, 40643, 40618, 40645, 40676,
							40442, 40051 };
					counts = new int[] { 10, 10, 10, 10, 20, 5, 1 };
					createitem = new int[] { 40925 }; // 浄化のポーション
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita8";
				}
			} else if (s.equals("G") || s.equals("h") || s.equals("i")) {
				// ミステリアス ポーション：１段階
				if (pc.getInventory().checkItem(40651, 5) // 火の息吹
						&& pc.getInventory().checkItem(40643, 5) // 水の息吹
						&& pc.getInventory().checkItem(40618, 5) // 大地の息吹
						&& pc.getInventory().checkItem(40645, 5) // 風の息吹
						&& pc.getInventory().checkItem(40676, 5) // 闇の息吹
						&& pc.getInventory().checkItem(40675, 5) // 闇の鉱石
						&& pc.getInventory().checkItem(40049, 3) // 高級ルビー
						&& pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
					htmlid = "perita27";
					materials = new int[] { 40651, 40643, 40618, 40645, 40676,
							40675, 40049, 40051 };
					counts = new int[] { 5, 5, 5, 5, 10, 10, 3, 1 };
					createitem = new int[] { 40926 }; // ミステリアスポーション：１段階
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita28";
				}
			} else if (s.equals("H") || s.equals("j") || s.equals("k")) {
				// ミステリアス ポーション：２段階
				if (pc.getInventory().checkItem(40651, 10) // 火の息吹
						&& pc.getInventory().checkItem(40643, 10) // 水の息吹
						&& pc.getInventory().checkItem(40618, 10) // 大地の息吹
						&& pc.getInventory().checkItem(40645, 10) // 風の息吹
						&& pc.getInventory().checkItem(40676, 20) // 闇の息吹
						&& pc.getInventory().checkItem(40675, 10) // 闇の鉱石
						&& pc.getInventory().checkItem(40048, 3) // 高級ダイアモンド
						&& pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
					htmlid = "perita29";
					materials = new int[] { 40651, 40643, 40618, 40645, 40676,
							40675, 40048, 40051 };
					counts = new int[] { 10, 10, 10, 10, 20, 10, 3, 1 };
					createitem = new int[] { 40927 }; // ミステリアスポーション：２段階
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita30";
				}
			} else if (s.equals("I") || s.equals("l") || s.equals("m")) {
				// ミステリアス ポーション：３段階
				if (pc.getInventory().checkItem(40651, 20) // 火の息吹
						&& pc.getInventory().checkItem(40643, 20) // 水の息吹
						&& pc.getInventory().checkItem(40618, 20) // 大地の息吹
						&& pc.getInventory().checkItem(40645, 20) // 風の息吹
						&& pc.getInventory().checkItem(40676, 30) // 闇の息吹
						&& pc.getInventory().checkItem(40675, 10) // 闇の鉱石
						&& pc.getInventory().checkItem(40050, 3) // 高級サファイア
						&& pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
					htmlid = "perita31";
					materials = new int[] { 40651, 40643, 40618, 40645, 40676,
							40675, 40050, 40051 };
					counts = new int[] { 20, 20, 20, 20, 30, 10, 3, 1 };
					createitem = new int[] { 40928 }; // ミステリアスポーション：３段階
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita32";
				}
			} else if (s.equals("J") || s.equals("n") || s.equals("o")) {
				// ミステリアス ポーション：４段階
				if (pc.getInventory().checkItem(40651, 30) // 火の息吹
						&& pc.getInventory().checkItem(40643, 30) // 水の息吹
						&& pc.getInventory().checkItem(40618, 30) // 大地の息吹
						&& pc.getInventory().checkItem(40645, 30) // 風の息吹
						&& pc.getInventory().checkItem(40676, 30) // 闇の息吹
						&& pc.getInventory().checkItem(40675, 20) // 闇の鉱石
						&& pc.getInventory().checkItem(40052, 1) // 最高級ダイアモンド
						&& pc.getInventory().checkItem(40051, 1)) { // 高級エメラルド
					htmlid = "perita33";
					materials = new int[] { 40651, 40643, 40618, 40645, 40676,
							40675, 40052, 40051 };
					counts = new int[] { 30, 30, 30, 30, 30, 20, 1, 1 };
					createitem = new int[] { 40928 }; // ミステリアスポーション：４段階
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita34";
				}
			} else if (s.equals("K")) { // １段階イアリング(霊魂のイアリング)
				int earinga = 0;
				int earingb = 0;
				if (pc.getInventory().checkEquipped(21014)
						|| pc.getInventory().checkEquipped(21006)
						|| pc.getInventory().checkEquipped(21007)) {
					htmlid = "perita36";
				} else if (pc.getInventory().checkItem(21014, 1)) { // ウィザードクラス
					earinga = 21014;
					earingb = 41176;
				} else if (pc.getInventory().checkItem(21006, 1)) { // ナイトクラス
					earinga = 21006;
					earingb = 41177;
				} else if (pc.getInventory().checkItem(21007, 1)) { // ウォーリアクラス
					earinga = 21007;
					earingb = 41178;
				} else {
					htmlid = "perita36";
				}
				if (earinga > 0) {
					materials = new int[] { earinga };
					counts = new int[] { 1 };
					createitem = new int[] { earingb };
					createcount = new int[] { 1 };
				}
			} else if (s.equals("L")) { // ２段階イアリング(知恵のイアリング)
				if (pc.getInventory().checkEquipped(21015)) {
					htmlid = "perita22";
				} else if (pc.getInventory().checkItem(21015, 1)) {
					materials = new int[] { 21015 };
					counts = new int[] { 1 };
					createitem = new int[] { 41179 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita22";
				}
			} else if (s.equals("M")) { // ３段階イアリング(真実のイアリング)
				if (pc.getInventory().checkEquipped(21016)) {
					htmlid = "perita26";
				} else if (pc.getInventory().checkItem(21016, 1)) {
					materials = new int[] { 21016 };
					counts = new int[] { 1 };
					createitem = new int[] { 41182 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita26";
				}
			} else if (s.equals("b")) { // ２段階イアリング(情熱のイアリング)
				if (pc.getInventory().checkEquipped(21009)) {
					htmlid = "perita39";
				} else if (pc.getInventory().checkItem(21009, 1)) {
					materials = new int[] { 21009 };
					counts = new int[] { 1 };
					createitem = new int[] { 41180 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita39";
				}
			} else if (s.equals("d")) { // ３段階イアリング(名誉のイアリング)
				if (pc.getInventory().checkEquipped(21012)) {
					htmlid = "perita41";
				} else if (pc.getInventory().checkItem(21012, 1)) {
					materials = new int[] { 21012 };
					counts = new int[] { 1 };
					createitem = new int[] { 41183 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita41";
				}
			} else if (s.equals("a")) { // ２段階イアリング(憤怒のイアリング)
				if (pc.getInventory().checkEquipped(21008)) {
					htmlid = "perita38";
				} else if (pc.getInventory().checkItem(21008, 1)) {
					materials = new int[] { 21008 };
					counts = new int[] { 1 };
					createitem = new int[] { 41181 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita38";
				}
			} else if (s.equals("c")) { // ３段階イアリング(勇猛のイアリング)
				if (pc.getInventory().checkEquipped(21010)) {
					htmlid = "perita40";
				} else if (pc.getInventory().checkItem(21010, 1)) {
					materials = new int[] { 21010 };
					counts = new int[] { 1 };
					createitem = new int[] { 41184 };
					createcount = new int[] { 1 };
				} else {
					htmlid = "perita40";
				}
			}
		}
		// 宝石細工師 ルームィス
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71129) {
			if (s.equals("Z")) {
				htmlid = "rumtis2";
			} else if (s.equals("Y")) {
				if (pc.getInventory().checkItem(41010, 1)) { // イリスの推薦書
					htmlid = "rumtis3";
				} else {
					htmlid = "rumtis4";
				}
			} else if (s.equals("q")) {
				htmlid = "rumtis92";
			} else if (s.equals("A")) {
				if (pc.getInventory().checkItem(41161, 1)) {
					// ミステリアスブラックイアリング
					htmlid = "rumtis6";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("B")) {
				if (pc.getInventory().checkItem(41164, 1)) {
					// ミステリアスウィザードイアリング
					htmlid = "rumtis7";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("C")) {
				if (pc.getInventory().checkItem(41167, 1)) {
					// ミステリアスグレーウィザードイアリング
					htmlid = "rumtis8";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("T")) {
				if (pc.getInventory().checkItem(41167, 1)) {
					// ミステリアスホワイトウィザードイアリング
					htmlid = "rumtis9";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("w")) {
				if (pc.getInventory().checkItem(41162, 1)) {
					// ミステリアスブラックイアリング
					htmlid = "rumtis14";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("x")) {
				if (pc.getInventory().checkItem(41165, 1)) {
					// ミステリアスナイトイアリング
					htmlid = "rumtis15";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("y")) {
				if (pc.getInventory().checkItem(41168, 1)) {
					// ミステリアスグレーナイトイアリング
					htmlid = "rumtis16";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("z")) {
				if (pc.getInventory().checkItem(41171, 1)) {
					// ミステリアスホワイトナイトイアリング
					htmlid = "rumtis17";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("U")) {
				if (pc.getInventory().checkItem(41163, 1)) {
					// ミステリアスブラックイアリング
					htmlid = "rumtis10";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("V")) {
				if (pc.getInventory().checkItem(41166, 1)) {
					// ミステリアスウォーリアイアリング
					htmlid = "rumtis11";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("W")) {
				if (pc.getInventory().checkItem(41169, 1)) {
					// ミステリアスグレーウォーリアイアリング
					htmlid = "rumtis12";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("X")) {
				if (pc.getInventory().checkItem(41172, 1)) {
					// ミステリアスホワイウォーリアイアリング
					htmlid = "rumtis13";
				} else {
					htmlid = "rumtis101";
				}
			} else if (s.equals("D") || s.equals("E") || s.equals("F")
					|| s.equals("G")) {
				int insn = 0;
				int bacn = 0;
				int me = 0;
				int mr = 0;
				int mj = 0;
				int an = 0;
				int men = 0;
				int mrn = 0;
				int mjn = 0;
				int ann = 0;
				if (pc.getInventory().checkItem(40959, 1) // 冥法軍王の印章
						&& pc.getInventory().checkItem(40960, 1) // 魔霊軍王の印章
						&& pc.getInventory().checkItem(40961, 1) // 魔獣軍王の印章
						&& pc.getInventory().checkItem(40962, 1)) { // 暗殺軍王の印章
					insn = 1;
					me = 40959;
					mr = 40960;
					mj = 40961;
					an = 40962;
					men = 1;
					mrn = 1;
					mjn = 1;
					ann = 1;
				} else if (pc.getInventory().checkItem(40642, 10) // 冥法軍のバッジ
						&& pc.getInventory().checkItem(40635, 10) // 魔霊軍のバッジ
						&& pc.getInventory().checkItem(40638, 10) // 魔獣軍のバッジ
						&& pc.getInventory().checkItem(40667, 10)) { // 暗殺軍のバッジ
					bacn = 1;
					me = 40642;
					mr = 40635;
					mj = 40638;
					an = 40667;
					men = 10;
					mrn = 10;
					mjn = 10;
					ann = 10;
				}
				if (pc.getInventory().checkItem(40046, 1) // サファイア
						&& pc.getInventory().checkItem(40618, 5) // 大地の息吹
						&& pc.getInventory().checkItem(40643, 5) // 水の息吹
						&& pc.getInventory().checkItem(40645, 5) // 風の息吹
						&& pc.getInventory().checkItem(40651, 5) // 火の息吹
						&& pc.getInventory().checkItem(40676, 5)) { // 闇の息吹
					if (insn == 1 || bacn == 1) {
						htmlid = "rumtis60";
						materials = new int[] { me, mr, mj, an, 40046, 40618,
								40643, 40651, 40676 };
						counts = new int[] { men, mrn, mjn, ann, 1, 5, 5, 5, 5,
								5 };
						createitem = new int[] { 40926 }; // 加工されたサファイア：１段階
						createcount = new int[] { 1 };
					} else {
						htmlid = "rumtis18";
					}
				}
			}
		}
		// アタロゼ
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71119) {
			// 「ラスタバドの歴史書1章から8章まで全部渡す」
			if (s.equalsIgnoreCase("request las history book")) {
				materials = new int[] { 41019, 41020, 41021, 41022, 41023,
						41024, 41025, 41026 };
				counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1 };
				createitem = new int[] { 41027 };
				createcount = new int[] { 1 };
				htmlid = "";
			}
		}
		// 長老随行員クロレンス
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71170) {
			// 「ラスタバドの歴史書を渡す」
			if (s.equalsIgnoreCase("request las weapon manual")) {
				materials = new int[] { 41027 };
				counts = new int[] { 1 };
				createitem = new int[] { 40965 };
				createcount = new int[] { 1 };
				htmlid = "";
			}
		}
		// 真冥王 ダンテス
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71168) {
			// 「異界の魔物がいる場所へ送ってください」
			if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(41028, 1)) {
					L1Teleport.teleport(pc, 32648, 32921, (short) 535, 6, true);
					pc.getInventory().consumeItem(41028, 1);
				}
			}
		}
		// 諜報員(欲望の洞窟側)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80067) {
			// 「動揺しつつも承諾する」
			if (s.equalsIgnoreCase("n")) {
				htmlid = "";
				poly(client, 6034);
				final int[] item_ids = { 41132, 41133, 41134 };
				final int[] item_amounts = { 1, 1, 1 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().getName(),
							item.getItem().getName()));
					pc.getQuest().setStep(L1Quest.QUEST_DESIRE, 1);
				}
				// 「そんな任務はやめる」
			} else if (s.equalsIgnoreCase("d")) {
				htmlid = "minicod09";
				pc.getInventory().consumeItem(41130, 1);
				pc.getInventory().consumeItem(41131, 1);
				// 「初期化する」
			} else if (s.equalsIgnoreCase("k")) {
				htmlid = "";
				pc.getInventory().consumeItem(41132, 1); // 血痕の堕落した粉
				pc.getInventory().consumeItem(41133, 1); // 血痕の無力した粉
				pc.getInventory().consumeItem(41134, 1); // 血痕の我執した粉
				pc.getInventory().consumeItem(41135, 1); // カヘルの堕落した精髄
				pc.getInventory().consumeItem(41136, 1); // カヘルの無力した精髄
				pc.getInventory().consumeItem(41137, 1); // カヘルの我執した精髄
				pc.getInventory().consumeItem(41138, 1); // カヘルの精髄
				pc.getQuest().setStep(L1Quest.QUEST_DESIRE, 0);
				// 精髄を渡す
			} else if (s.equalsIgnoreCase("e")) {
				if (pc.getQuest().getStep(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END
						|| pc.getKarmaLevel() >= 1) {
					htmlid = "";
				} else {
					if (pc.getInventory().checkItem(41138)) {
						htmlid = "";
						pc.addKarma((int) (1600 * Config.RATE_KARMA));
						pc.getInventory().consumeItem(41130, 1); // 血痕の契約書
						pc.getInventory().consumeItem(41131, 1); // 血痕の指令書
						pc.getInventory().consumeItem(41138, 1); // カヘルの精髄
						pc.getQuest().setStep(L1Quest.QUEST_DESIRE,
								L1Quest.QUEST_END);
					} else {
						htmlid = "minicod04";
					}
				}
				// プレゼントをもらう
			} else if (s.equalsIgnoreCase("g")) {
				htmlid = "";
				L1ItemInstance item = pc.getInventory().storeItem(41130, 1); // 血痕の契約書
				pc
						.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
			}
		}
		// 諜報員(影の神殿側)
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 81202) {
			// 「頭にくるが承諾する」
			if (s.equalsIgnoreCase("n")) {
				htmlid = "";
				poly(client, 6035);
				final int[] item_ids = { 41123, 41124, 41125 };
				final int[] item_amounts = { 1, 1, 1 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().getName(),
							item.getItem().getName()));
					pc.getQuest().setStep(L1Quest.QUEST_SHADOWS, 1);
				}
				// 「そんな任務はやめる」
			} else if (s.equalsIgnoreCase("d")) {
				htmlid = "minitos09";
				pc.getInventory().consumeItem(41121, 1);
				pc.getInventory().consumeItem(41122, 1);
				// 「初期化する」
			} else if (s.equalsIgnoreCase("k")) {
				htmlid = "";
				pc.getInventory().consumeItem(41123, 1); // カヘルの堕落した粉
				pc.getInventory().consumeItem(41124, 1); // カヘルの無力した粉
				pc.getInventory().consumeItem(41125, 1); // カヘルの我執した粉
				pc.getInventory().consumeItem(41126, 1); // 血痕の堕落した精髄
				pc.getInventory().consumeItem(41127, 1); // 血痕の無力した精髄
				pc.getInventory().consumeItem(41128, 1); // 血痕の我執した精髄
				pc.getInventory().consumeItem(41129, 1); // 血痕の精髄
				pc.getQuest().setStep(L1Quest.QUEST_SHADOWS, 0);
				// 精髄を渡す
			} else if (s.equalsIgnoreCase("e")) {
				if (pc.getQuest().getStep(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END
						|| pc.getKarmaLevel() >= 1) {
					htmlid = "";
				} else {
					if (pc.getInventory().checkItem(41129)) {
						htmlid = "";
						pc.addKarma((int) (-1600 * Config.RATE_KARMA));
						pc.getInventory().consumeItem(41121, 1); // カヘルの契約書
						pc.getInventory().consumeItem(41122, 1); // カヘルの指令書
						pc.getInventory().consumeItem(41129, 1); // 血痕の精髄
						pc.getQuest().setStep(L1Quest.QUEST_SHADOWS,
								L1Quest.QUEST_END);
					} else {
						htmlid = "minitos04";
					}
				}
				// 素早く受取る
			} else if (s.equalsIgnoreCase("g")) {
				htmlid = "";
				L1ItemInstance item = pc.getInventory().storeItem(41121, 1); // カヘルの契約書
				pc
						.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
			}
		}
		// ゾウのストーンゴーレム
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71252) {
			int weapon1 = 0;
			int weapon2 = 0;
			int newWeapon = 0;
			if (s.equalsIgnoreCase("A")) {
				weapon1 = 5; // +7エルヴンダガー
				weapon2 = 6; // +7ラスタバドダガー
				newWeapon = 259; // マナバーラード
				htmlid = "joegolem9";
			} else if (s.equalsIgnoreCase("B")) {
				weapon1 = 145; // +7バーサーカーアックス
				weapon2 = 148; // +7グレートアックス
				newWeapon = 260; // レイジングウィンド
				htmlid = "joegolem10";
			} else if (s.equalsIgnoreCase("C")) {
				weapon1 = 52; // +7ツーハンドソード
				weapon2 = 64; // +7グレートソード
				newWeapon = 262; // ディストラクション
				htmlid = "joegolem11";
			} else if (s.equalsIgnoreCase("D")) {
				weapon1 = 125; // +7ソーサリースタッフ
				weapon2 = 129; // +7メイジスタッフ
				newWeapon = 261; // アークメイジスタッフ
				htmlid = "joegolem12";
			} else if (s.equalsIgnoreCase("E")) {
				weapon1 = 99; // +7エルブンスピアー
				weapon2 = 104; // +7フォチャード
				newWeapon = 263; // フリージングランサー
				htmlid = "joegolem13";
			} else if (s.equalsIgnoreCase("F")) {
				weapon1 = 32; // +7グラディウス
				weapon2 = 42; // +7レイピア
				newWeapon = 264; // ライトニングエッジ
				htmlid = "joegolem14";
			}
			if (pc.getInventory().checkEnchantItem(weapon1, 7, 1)
					&& pc.getInventory().checkEnchantItem(weapon2, 7, 1)
					&& pc.getInventory().checkItem(41246, 1000) // 結晶体
					&& pc.getInventory().checkItem(49143, 10)) { // 勇気の結晶
				pc.getInventory().consumeEnchantItem(weapon1, 7, 1);
				pc.getInventory().consumeEnchantItem(weapon2, 7, 1);
				pc.getInventory().consumeItem(41246, 1000);
				pc.getInventory().consumeItem(49143, 10);
				L1ItemInstance item = pc.getInventory().storeItem(newWeapon, 1);
				pc
						.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
			} else {
				htmlid = "joegolem15";
				if (!pc.getInventory().checkEnchantItem(weapon1, 7, 1)) {
					pc.sendPackets(new S_ServerMessage(337, "+7 "
							+ ItemTable.getInstance().getTemplate(weapon1)
									.getName())); // \f1%0が不足しています。
				}
				if (!pc.getInventory().checkEnchantItem(weapon2, 7, 1)) {
					pc.sendPackets(new S_ServerMessage(337, "+7 "
							+ ItemTable.getInstance().getTemplate(weapon2)
									.getName())); // \f1%0が不足しています。
				}
				if (!pc.getInventory().checkItem(41246, 1000)) {
					int itemCount = 0;
					itemCount = 1000 - pc.getInventory().countItems(41246);
					pc.sendPackets(new S_ServerMessage(337, ItemTable
							.getInstance().getTemplate(41246).getName()
							+ "(" + itemCount + ")")); // \f1%0が不足しています。
				}
				if (!pc.getInventory().checkItem(49143, 10)) {
					int itemCount = 0;
					itemCount = 10 - pc.getInventory().countItems(49143);
					pc.sendPackets(new S_ServerMessage(337, ItemTable
							.getInstance().getTemplate(49143).getName()
							+ "(" + itemCount + ")")); // \f1%0が不足しています。
				}
			}
		}
		// ゾウのストーンゴーレム テーベ砂漠
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71253) {
			// 「歪みのコアを作る」
			if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(49101, 100)) {
					materials = new int[] { 49101 };
					counts = new int[] { 100 };
					createitem = new int[] { 49092 };
					createcount = new int[] { 1 };
					htmlid = "joegolem18";
				} else {
					htmlid = "joegolem19";
				}
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(49101, 1)) {
					pc.getInventory().consumeItem(49101, 1);
					L1Teleport.teleport(pc, 33966, 33253, (short) 4, 5, true);
					htmlid = "";
				} else {
					htmlid = "joegolem20";
				}
			}
		}
		// テーベ オシリス祭壇のキーパー
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71255) {
			// 「テーベオシリス祭壇の鍵を持っているなら、オシリスの祭壇にお送りしましょう。」
			if (s.equalsIgnoreCase("e")) {
				if (pc.getInventory().checkItem(49242, 1)) {
					// 鍵のチェック(20人限定/時の歪みが現れてから2h30は未実装)
					pc.getInventory().consumeItem(49242, 1);
					L1Teleport.teleport(pc, 32735, 32831, (short) 782, 2, true);
					htmlid = "";
				} else {
					htmlid = "tebegate3";
					// 「上限人数に達している場合は」
					// htmlid = "tebegate4";
				}
			}
		}
		// ククルカン祭壇のキーパー
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 90521) {
			// 「ククルカン祭壇の鍵を持っているなら、そこまでお送りしましょう。」
			if (s.equalsIgnoreCase("e")) {
				if (pc.getInventory().checkItem(50005, 1)) {
					pc.getInventory().consumeItem(50005, 1);
					L1Teleport.teleport(pc, 32731, 32863, (short) 784, 2, true);
					htmlid = "";
				} else {
					htmlid = "tikalgate3";
				}
			}
		}
		// ロビンフッド
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71256) {
			if (s.equalsIgnoreCase("E")) {
				if ((pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 8)
						&& pc.getInventory().checkItem(40491, 30)
						&& pc.getInventory().checkItem(40495, 40)
						&& pc.getInventory().checkItem(100, 1)
						&& pc.getInventory().checkItem(40509, 12)
						&& pc.getInventory().checkItem(40052, 1)
						&& pc.getInventory().checkItem(40053, 1)
						&& pc.getInventory().checkItem(40054, 1)
						&& pc.getInventory().checkItem(40055, 1)
						&& pc.getInventory().checkItem(41347, 1)
						&& pc.getInventory().checkItem(41350, 1)) {
					pc.getInventory().consumeItem(40491, 30);
					pc.getInventory().consumeItem(40495, 40);
					pc.getInventory().consumeItem(100, 1);
					pc.getInventory().consumeItem(40509, 12);
					pc.getInventory().consumeItem(40052, 1);
					pc.getInventory().consumeItem(40053, 1);
					pc.getInventory().consumeItem(40054, 1);
					pc.getInventory().consumeItem(40055, 1);
					pc.getInventory().consumeItem(41347, 1);
					pc.getInventory().consumeItem(41350, 1);
					htmlid = "robinhood12";
					pc.getInventory().storeItem(205, 1);
					pc.getQuest().setStep(L1Quest.QUEST_MOONOFLONGBOW,
							L1Quest.QUEST_END);
				}
			} else if (s.equalsIgnoreCase("C")) {
				if (pc.getQuest().getStep(L1Quest.QUEST_MOONOFLONGBOW) == 7) {
					if (pc.getInventory().checkItem(41352, 4)
							&& pc.getInventory().checkItem(40618, 30)
							&& pc.getInventory().checkItem(40643, 30)
							&& pc.getInventory().checkItem(40645, 30)
							&& pc.getInventory().checkItem(40651, 30)
							&& pc.getInventory().checkItem(40676, 30)
							&& pc.getInventory().checkItem(40514, 20)
							&& pc.getInventory().checkItem(41351, 1)
							&& pc.getInventory().checkItem(41346, 1)) {
						pc.getInventory().consumeItem(41352, 4);
						pc.getInventory().consumeItem(40618, 30);
						pc.getInventory().consumeItem(40643, 30);
						pc.getInventory().consumeItem(40645, 30);
						pc.getInventory().consumeItem(40651, 30);
						pc.getInventory().consumeItem(40676, 30);
						pc.getInventory().consumeItem(40514, 20);
						pc.getInventory().consumeItem(41351, 1);
						pc.getInventory().consumeItem(41346, 1);
						pc.getInventory().storeItem(41347, 1);
						pc.getInventory().storeItem(41350, 1);
						htmlid = "robinhood10";
						pc.getQuest().setStep(L1Quest.QUEST_MOONOFLONGBOW, 8);
					}
				}
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(41348)
						&& pc.getInventory().checkItem(41346)) {
					htmlid = "robinhood13";
				} else {
					pc.getInventory().storeItem(41348, 1);
					pc.getInventory().storeItem(41346, 1);
					htmlid = "robinhood13";
					pc.getQuest().setStep(L1Quest.QUEST_MOONOFLONGBOW, 2);
				}
			} else if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(40028)) {
					pc.getInventory().consumeItem(40028, 1);
					htmlid = "robinhood4";
					pc.getQuest().setStep(L1Quest.QUEST_MOONOFLONGBOW, 1);
				} else {
					htmlid = "robinhood19";
				}
			}
		}
		// ジブリル
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71257) {
			if (s.equalsIgnoreCase("D")) {
				if (pc.getInventory().checkItem(41349)) {
					htmlid = "zybril10";
					pc.getInventory().storeItem(41351, 1);
					pc.getInventory().consumeItem(41349, 1);
					pc.getQuest().setStep(L1Quest.QUEST_MOONOFLONGBOW, 7);
				} else {
					htmlid = "zybril14";
				}
			} else if (s.equalsIgnoreCase("C")) {
				if (pc.getInventory().checkItem(40514, 10)
						&& pc.getInventory().checkItem(41353)) {
					pc.getInventory().consumeItem(40514, 10);
					pc.getInventory().consumeItem(41353, 1);
					pc.getInventory().storeItem(41354, 1);
					htmlid = "zybril9";
					pc.getQuest().setStep(L1Quest.QUEST_MOONOFLONGBOW, 6);
				}
			} else if (pc.getInventory().checkItem(41353)
					&& pc.getInventory().checkItem(40514, 10)) {
				htmlid = "zybril8";
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(40048, 10)
						&& pc.getInventory().checkItem(40049, 10)
						&& pc.getInventory().checkItem(40050, 10)
						&& pc.getInventory().checkItem(40051, 10)) {
					pc.getInventory().consumeItem(40048, 10);
					pc.getInventory().consumeItem(40049, 10);
					pc.getInventory().consumeItem(40050, 10);
					pc.getInventory().consumeItem(40051, 10);
					pc.getInventory().storeItem(41353, 1);
					htmlid = "zybril15";
					pc.getQuest().setStep(L1Quest.QUEST_MOONOFLONGBOW, 5);
				} else {
					htmlid = "zybril12";
					pc.getQuest().setStep(L1Quest.QUEST_MOONOFLONGBOW, 4);
				}
			} else if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(41348)
						&& pc.getInventory().checkItem(41346)) {
					htmlid = "zybril3";
					pc.getQuest().setStep(L1Quest.QUEST_MOONOFLONGBOW, 3);
				} else {
					htmlid = "zybril11";
				}
			}
		}
		// マルバ
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71258) {
			if (pc.getInventory().checkItem(40665)) {
				htmlid = "marba17";
				if (s.equalsIgnoreCase("B")) {
					htmlid = "marba7";
					if (pc.getInventory().checkItem(214)
							&& pc.getInventory().checkItem(20389)
							&& pc.getInventory().checkItem(20393)
							&& pc.getInventory().checkItem(20401)
							&& pc.getInventory().checkItem(20406)
							&& pc.getInventory().checkItem(20409)) {
						htmlid = "marba15";
					}
				}
			} else if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(40637)) {
					htmlid = "marba20";
				} else {
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(40637, 1);
					String npcName = npc.getNpcTemplate().getName();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
					htmlid = "marba6";
				}
			}
		}
		// アラス
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71259) {
			if (pc.getInventory().checkItem(40665)) {
				htmlid = "aras8";
			} else if (pc.getInventory().checkItem(40637)) {
				htmlid = "aras1";
				if (s.equalsIgnoreCase("A")) {
					if (pc.getInventory().checkItem(40664)) {
						htmlid = "aras6";
						if (pc.getInventory().checkItem(40679)
								|| pc.getInventory().checkItem(40680)
								|| pc.getInventory().checkItem(40681)
								|| pc.getInventory().checkItem(40682)
								|| pc.getInventory().checkItem(40683)
								|| pc.getInventory().checkItem(40684)
								|| pc.getInventory().checkItem(40693)
								|| pc.getInventory().checkItem(40694)
								|| pc.getInventory().checkItem(40695)
								|| pc.getInventory().checkItem(40697)
								|| pc.getInventory().checkItem(40698)
								|| pc.getInventory().checkItem(40699)) {
							htmlid = "aras3";
						} else {
							htmlid = "aras6";
						}
					} else {
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								40664, 1);
						String npcName = npc.getNpcTemplate().getName();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName));
						htmlid = "aras6";
					}
				} else if (s.equalsIgnoreCase("B")) {
					if (pc.getInventory().checkItem(40664)) {
						pc.getInventory().consumeItem(40664, 1);
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								40665, 1);
						String npcName = npc.getNpcTemplate().getName();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName));
						htmlid = "aras13";
					} else {
						htmlid = "aras14";
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								40665, 1);
						String npcName = npc.getNpcTemplate().getName();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName));
					}
				} else {
					if (s.equalsIgnoreCase("7")) {
						if (pc.getInventory().checkItem(40693)
								&& pc.getInventory().checkItem(40694)
								&& pc.getInventory().checkItem(40695)
								&& pc.getInventory().checkItem(40697)
								&& pc.getInventory().checkItem(40698)
								&& pc.getInventory().checkItem(40699)) {
							htmlid = "aras10";
						} else {
							htmlid = "aras9";
						}
					}
				}
			} else {
				htmlid = "aras7";
			}
		}
		// 治安団長ラルソン
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80099) {
			if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(40308, 300)) {
					pc.getInventory().consumeItem(40308, 300);
					pc.getInventory().storeItem(41315, 1);
					pc.getQuest().setStep(
							L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 1);
					htmlid = "rarson16";
				} else if (!pc.getInventory().checkItem(40308, 300)) {
					htmlid = "rarson7";
				}
			} else if (s.equalsIgnoreCase("B")) {
				if ((pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 1)
						&& (pc.getInventory().checkItem(41325, 1))) {
					pc.getInventory().consumeItem(41325, 1);
					pc.getInventory().storeItem(40308, 2000);
					pc.getInventory().storeItem(41317, 1);
					pc.getQuest().setStep(
							L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 2);
					htmlid = "rarson9";
				} else {
					htmlid = "rarson10";
				}
			} else if (s.equalsIgnoreCase("C")) {
				if ((pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 4)
						&& (pc.getInventory().checkItem(41326, 1))) {
					pc.getInventory().storeItem(40308, 30000);
					pc.getInventory().consumeItem(41326, 1);
					htmlid = "rarson12";
					pc.getQuest().setStep(
							L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 5);
				} else {
					htmlid = "rarson17";
				}
			} else if (s.equalsIgnoreCase("D")) {
				if ((pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) <= 1)
						|| (pc.getQuest().getStep(
								L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 5)) {
					if (pc.getInventory().checkItem(40308, 300)) {
						pc.getInventory().consumeItem(40308, 300);
						pc.getInventory().storeItem(41315, 1);
						pc.getQuest().setStep(
								L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 1);
						htmlid = "rarson16";
					} else if (!pc.getInventory().checkItem(40308, 300)) {
						htmlid = "rarson7";
					}
				} else if ((pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) >= 2)
						&& (pc.getQuest().getStep(
								L1Quest.QUEST_GENERALHAMELOFRESENTMENT) <= 4)) {
					if (pc.getInventory().checkItem(40308, 300)) {
						pc.getInventory().consumeItem(40308, 300);
						pc.getInventory().storeItem(41315, 1);
						htmlid = "rarson16";
					} else if (!pc.getInventory().checkItem(40308, 300)) {
						htmlid = "rarson7";
					}
				}
			}
		}
		// クエン
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80101) {
			if (s.equalsIgnoreCase("request letter of kuen")) {
				if ((pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 2)
						&& (pc.getInventory().checkItem(41317, 1))) {
					pc.getInventory().consumeItem(41317, 1);
					pc.getInventory().storeItem(41318, 1);
					pc.getQuest().setStep(
							L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 3);
					htmlid = "";
				} else {
					htmlid = "";
				}
			} else if (s.equalsIgnoreCase("request holy mithril dust")) {
				if ((pc.getQuest().getStep(
						L1Quest.QUEST_GENERALHAMELOFRESENTMENT) == 3)
						&& (pc.getInventory().checkItem(41315, 1))
						&& pc.getInventory().checkItem(40494, 30)
						&& pc.getInventory().checkItem(41318, 1)) {
					pc.getInventory().consumeItem(41315, 1);
					pc.getInventory().consumeItem(41318, 1);
					pc.getInventory().consumeItem(40494, 30);
					pc.getInventory().storeItem(41316, 1);
					pc.getQuest().setStep(
							L1Quest.QUEST_GENERALHAMELOFRESENTMENT, 4);
					htmlid = "";
				} else {
					htmlid = "";
				}
			}
		}

		// 長老 プロケル
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80136) {
			int lv15_step = pc.getQuest().getStep(L1Quest.QUEST_LEVEL15);
			int lv30_step = pc.getQuest().getStep(L1Quest.QUEST_LEVEL30);
			int lv45_step = pc.getQuest().getStep(L1Quest.QUEST_LEVEL45);
			int lv50_step = pc.getQuest().getStep(L1Quest.QUEST_LEVEL50);
			// int lv50_step = pc.getQuest().getStep(L1Quest.QUEST_LEVEL50);
			if (pc.isDragonKnight()) {
				// 「プロケルの課題を遂行する」
				if (s.equalsIgnoreCase("a") && lv15_step == 0) {
					L1NpcInstance npc = (L1NpcInstance) obj;
					L1ItemInstance item = pc.getInventory().storeItem(49210, 1); // プロケルの1番目の指令書
					String npcName = npc.getNpcTemplate().getName();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
					// \f1%0が%1をくれました。
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL15, 1);
					htmlid = "prokel3";
				// 「プロケルの2番目の課題を遂行する」
				} else if (s.equalsIgnoreCase("c") && lv30_step == 0) {
					final int[] item_ids = { 49211, 49215, }; // プロケルの2番目の指令書,
					// プロケルの鉱物の袋
					final int[] item_amounts = { 1, 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
					}
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL30, 1);
					htmlid = "prokel9";
				// 「鉱物の袋が必要だ」
				} else if (s.equalsIgnoreCase("e")) {
					if (pc.getInventory().checkItem(49215, 1)) {
						htmlid = "prokel35";
					} else {
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								49215, 1); // プロケルの鉱物の袋
						String npcName = npc.getNpcTemplate().getName();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName)); // \f1%0が%1をくれました。
						htmlid = "prokel13";
					}
				// 「プロケルの3番目の課題を遂行する」
				} else if (s.equalsIgnoreCase("f") && lv45_step == 0) {
					final int[] item_ids = { 49209, 49212, 49226, };
					// プロケルの手紙,プロケルの3番目の指令書,タワー
					// ポータル
					// テレポート
					// スクロール
					final int[] item_amounts = { 1, 1, 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
					}
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL45, 1);
					htmlid = "prokel16";
				// 「プロケルの4番目の課題を遂行する」
				} else if (s.equalsIgnoreCase("h") && (lv50_step == 0)) {
					final int[] item_ids = { 49287, };
					final int[] item_amounts = { 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate().getName(),
								item.getItem().getName()));
					}
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 1);
					htmlid = "prokel22";
				} else if (s.equalsIgnoreCase("k") && (lv50_step >= 2)) {
					if (pc.getInventory().checkItem(49202, 1)
							|| pc.getInventory().checkItem(49216, 1)) {
						htmlid = "prokel29";
					} else {
						final int[] item_ids = { 49202, 49216, };
						final int[] item_amounts = { 1, 1, };
						for (int i = 0; i < item_ids.length; i++) {
							L1ItemInstance item = pc.getInventory().storeItem(
									item_ids[i], item_amounts[i]);
							pc.sendPackets(new S_ServerMessage(143,
									((L1NpcInstance) obj).getNpcTemplate().getName(),
									item.getItem().getName()));
						}
						htmlid = "prokel28";
					}
				}
			}
		}

		// 長老 シルレイン
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80145) {
			int lv15_step = pc.getQuest().getStep(L1Quest.QUEST_LEVEL15);
			int lv30_step = pc.getQuest().getStep(L1Quest.QUEST_LEVEL30);
			int lv45_step = pc.getQuest().getStep(L1Quest.QUEST_LEVEL45);
			int lv50_step = pc.getQuest().getStep(L1Quest.QUEST_LEVEL50);
			if (pc.isDragonKnight()) {
				// 「プロケルの手紙を渡す」
				if (s.equalsIgnoreCase("l") && lv45_step == 1) {
					if (pc.getInventory().checkItem(49209, 1)) { // check
						pc.getInventory().consumeItem(49209, 1); // del
						pc.getQuest().setStep(L1Quest.QUEST_LEVEL45, 2);
						htmlid = "silrein38";
					}
				} else if (s.equalsIgnoreCase("m") && lv45_step == 2) {
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL45, 3);
					htmlid = "silrein39";
				}
			} else if (pc.isIllusionist()) {
				// シルレインの1番目の手紙
				if (s.equalsIgnoreCase("a") && (lv15_step == 0)) {
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL15, 1);
					createitem = new int[] { 49172, 49182 };
					createcount = new int[] { 1, 1 };
					htmlid = "silrein3";
				// シルレインの2番目の手紙
				} else if (s.equalsIgnoreCase("c") && (lv30_step == 0)) {
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL30, 1);
					createitem = new int[] { 49173, 49179 };
					createcount = new int[] { 1, 1 };
					htmlid = "silrein2";
				} else if (s.equalsIgnoreCase("o") && (lv30_step == 1)) {
					if (pc.getInventory().checkItem(49186, 1)
							|| pc.getInventory().checkItem(49179, 1)) {
						htmlid = "silrein17";
					} else {
						createitem = new int[] { 49186 };
						createcount = new int[] { 1 };
						htmlid = "silrein16";
					}
				// シルレインの3番目の手紙
				} else if (s.equalsIgnoreCase("e") && (lv45_step == 0)) {
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL45, 1);
					createitem = new int[] { 49174, 49180 };
					createcount = new int[] { 1, 1 };
					htmlid = "silrein19";
				// シルレインの4番目の手紙
				} else if (s.equalsIgnoreCase("h") && (lv50_step == 0)) {
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 1);
					createitem = new int[] { 49176 };
					createcount = new int[] { 1 };
					htmlid = "silrein28";
				} else if (s.equalsIgnoreCase("k") && (lv50_step >= 2)) {
					if (pc.getInventory().checkItem(49202, 1)
							|| pc.getInventory().checkItem(49178, 1)) {
						htmlid = "silrein32";
					} else {
						createitem = new int[] { 49202, 49178 };
						createcount = new int[] { 1, 1 };
						htmlid = "silrein32";
					}
				}
			}
		}

		// エルラス
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80135) {
			if (pc.isDragonKnight()) {
				// 「オーク密使変身スクロールを受け取る」
				if (s.equalsIgnoreCase("a")) {
					if (pc.getInventory().checkItem(49220, 1)) {
						htmlid = "elas5";
					} else {
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								49220, 1); // オーク密使変身スクロール
						String npcName = npc.getNpcTemplate().getName();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName)); // \f1%0が%1をくれました。
						htmlid = "elas4";
					}
				}
			}
		}

		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 81245) {
			// オーク密使(HC3)
			if (pc.isDragonKnight()) {
				if (s.equalsIgnoreCase("request flute of spy")) {
					if (pc.getInventory().checkItem(49223, 1)) { // check
						pc.getInventory().consumeItem(49223, 1); // del
						L1NpcInstance npc = (L1NpcInstance) obj;
						L1ItemInstance item = pc.getInventory().storeItem(
								49222, 1); // オーク密使の笛
						String npcName = npc.getNpcTemplate().getName();
						String itemName = item.getItem().getName();
						pc.sendPackets(new S_ServerMessage(143, npcName,
								itemName)); // \f1%0が%1をくれました。
						htmlid = "";
					} else {
						htmlid = "";
					}
				}
			}
		}

		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 81246) { // シャルナ
			if (s.equalsIgnoreCase("0")) {
				materials = new int[] { 40308 };
				counts = new int[] { 2500 };
				if (pc.getLevel() < 30) {
					htmlid = "sharna4";
				} else if (pc.getLevel() >= 30 && pc.getLevel() <= 39) {
					createitem = new int[] { 49149 }; // シャルナの変身スクロール（レベル30）
					createcount = new int[] { 1 };
				} else if (pc.getLevel() >= 40 && pc.getLevel() <= 51) {
					createitem = new int[] { 49150 }; // シャルナの変身スクロール（レベル40）
					createcount = new int[] { 1 };
				} else if (pc.getLevel() >= 52 && pc.getLevel() <= 54) {
					createitem = new int[] { 49151 }; // シャルナの変身スクロール（レベル52）
					createcount = new int[] { 1 };
				} else if (pc.getLevel() >= 55 && pc.getLevel() <= 59) {
					createitem = new int[] { 49152 }; // シャルナの変身スクロール（レベル55）
					createcount = new int[] { 1 };
				} else if (pc.getLevel() >= 60 && pc.getLevel() <= 64) {
					createitem = new int[] { 49153 }; // シャルナの変身スクロール（レベル60）
					createcount = new int[] { 1 };
				} else if (pc.getLevel() >= 65 && pc.getLevel() <= 69) {
					createitem = new int[] { 49154 }; // シャルナの変身スクロール（レベル65）
					createcount = new int[] { 1 };
				} else if (pc.getLevel() >= 70) {
					createitem = new int[] { 49155 }; // シャルナの変身スクロール（レベル70）
					createcount = new int[] { 1 };
				}
				success_htmlid = "sharna3";
				failure_htmlid = "sharna5";
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70035
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70041
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70042) { // ギランレース管理人
			// 　
			// セシル
			// 　
			// ポーリー
			// 　
			// パーキン
			if (s.equalsIgnoreCase("status")) {// status
				htmldata = new String[15];
				for (int i = 0; i < 5; i++) {
					/* TODO getNameIdだと2文字以上の名前が表示されない。
					htmldata[i * 3] = (NpcTable.getInstance().getTemplate(
							L1BugBearRace.getInstance().getRunner(i).getNpcId()).getNameId());
					*/
					htmldata[i * 3] = (NpcTable.getInstance().getTemplate(
							L1BugBearRace.getInstance().getRunner(i).getNpcId()).getName());
					String condition;// 610 普通
					if (L1BugBearRace.getInstance().getCondition(i) == 0) {
						condition = "$610";
					} else {
						if (L1BugBearRace.getInstance().getCondition(i) > 0) {// 368 良い
							condition = "$368";
						} else {// 370 悪い
							condition = "$370";
						}
					}
					htmldata[i * 3 + 1] = condition;
					htmldata[i * 3 + 2] = String.valueOf(L1BugBearRace.getInstance().getWinningAverage(i));
				}
				htmlid = "maeno4";
			}
		}// 話せる島-ユリエ//XXX
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 91327) {
			if (s.equalsIgnoreCase("c")) {
				if (!pc.getInventory().checkItem(50006)
						&& !pc.getAdditionalWarehouseInventory().checkItem(50006)) { // 時空の瓶
					L1ItemInstance item = pc.getInventory().storeItem(50006, 1);
					if (item != null) {
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.getName(), item.getItem().getName()));
						// \f1%0が%1をくれました。
						pc.getQuest().setStep(L1Quest.QUEST_YURIE,
								L1Quest.QUEST_END);
					}
				}
				htmlid = "";
			} else if (s.equalsIgnoreCase("a")) {// 寄付金10000アデナと時空の玉を渡す
				if (pc.getInventory().checkItem(L1ItemId.ADENA, 10000)
						&& pc.getInventory().checkItem(50007, 1)) {
					int mate = 1;
					if (pc.getInventory().countItems(50007) > 3) {
						mate = pc.getInventory().countItems(50007) - 2;
					}
					pc.getInventory().consumeItem(50007, mate);
					pc.getInventory().consumeItem(L1ItemId.ADENA, 10000);
					htmlid = "";
					L1Teleport
							.teleport(pc, 32745, 32855, (short) 9100, 6, true);
				} else {
					htmlid = "j_html02";
				}
			} else if (s.equalsIgnoreCase("b")) {// 寄付金10000アデナとオリムの水晶を渡す
				if(!pc.isGm()){
					return;
				}
				if (pc.getInventory().checkItem(L1ItemId.ADENA, 10000)
						&& pc.getInventory().checkItem(50025, 1)) {
					int mate = 1;
					if (pc.getInventory().countItems(50025) > 3) {
						mate = pc.getInventory().countItems(50025) - 2;
					}
					if(!pc.isGm()){
						pc.getInventory().consumeItem(50025, mate);
						pc.getInventory().consumeItem(L1ItemId.ADENA, 10000);
					}
					htmlid = "";
					L1Teleport
							.teleport(pc, 32745, 32855, (short) 9202, 6, true);
				} else {
					htmlid = "j_html02";
				}
			} else if (s.equalsIgnoreCase("d")) {// 日記10ページ分をハーディンの日記帳に復元してもらう
				if (pc.getInventory().checkItem(50008, 1)
						&& pc.getInventory().checkItem(50009, 1)
						&& pc.getInventory().checkItem(50010, 1)
						&& pc.getInventory().checkItem(50011, 1)
						&& pc.getInventory().checkItem(50012, 1)
						&& pc.getInventory().checkItem(50013, 1)
						&& pc.getInventory().checkItem(50014, 1)
						&& pc.getInventory().checkItem(50015, 1)
						&& pc.getInventory().checkItem(50016, 1)
						&& pc.getInventory().checkItem(50017, 1)) {
					materials = new int[] { 50008, 50009, 50010, 50011, 50012,
							50013, 50014, 50015, 50016, 50017 }; // アデナ、
					counts = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
					createitem = new int[] { 50018 }; // ケレニスの日記帳
					createcount = new int[] { 1 };
					htmlid = "j_html04";
				} else {
					htmlid = "j_html06";
				}
			} else if (s.equalsIgnoreCase("e")) {// 日記18ページ分をオリムの日記帳に復元してもらう
				if (pc.getInventory().checkItem(50026,1)//オリムの日記 6月14日
						&& pc.getInventory().checkItem(50027,1)//オリムの日記 6月16日
						&& pc.getInventory().checkItem(50028,1)//オリムの日記 6月18日
						&& pc.getInventory().checkItem(50029,1)//オリムの日記 6月21日
						&& pc.getInventory().checkItem(50030,1)//オリムの日記 6月22日
						&& pc.getInventory().checkItem(50031,1)//オリムの日記 6月25日
						&& pc.getInventory().checkItem(50032,1)//オリムの日記 7月5日
						&& pc.getInventory().checkItem(50033,1)//オリムの日記 7月17日
						&& pc.getInventory().checkItem(50034,1)//オリムの日記 7月18日
						&& pc.getInventory().checkItem(50035,1)//オリムの日記 8月5日
						&& pc.getInventory().checkItem(50036,1)//オリムの日記 8月8日
						&& pc.getInventory().checkItem(50037,1)//オリムの日記 8月9日
						&& pc.getInventory().checkItem(50038,1)//オリムの日記 8月10日
						&& pc.getInventory().checkItem(50039,1)//オリムの日記 8月11日
						&& pc.getInventory().checkItem(50040,1)//オリムの日記 8月12日
						&& pc.getInventory().checkItem(50041,1)//オリムの日記 8月13日
						&& pc.getInventory().checkItem(50042,1)//オリムの日記 8月14日
						&& pc.getInventory().checkItem(50043,1)//オリムの日記 8月15日
					) {
					materials = new int[] {50026,50027,50028,50029,50030,50031,50032,50033,
								50034,50035,50036,50037,50038,50039,50040,50041,50042,50043}; // アデナ、
					counts = new int[] {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
					createitem = new int[] { 50044 }; // オリムの日記帳
					createcount = new int[] { 1 };
					htmlid = "j_html04";
				} else {
					htmlid = "j_html06";
				}
			}
		}// ディエツ　「欲心はまた他の欲心を呼んで」
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71179) {
			if (s.equalsIgnoreCase("A")) {// 復元された古代のアミュレット
				if (pc.getInventory().checkItem(41139, 1) && // 崩れた古代のアミュレット
						pc.getInventory().checkItem(49028, 1) && // タロスのルビー
						pc.getInventory().checkItem(49029, 1) && // タロスのサファイア
						pc.getInventory().checkItem(49030, 1)) { // タロスのエメラルド
					if (_random.nextInt(100) < 40) { // 暫定で40%位で設定
						materials = new int[] { 41139, 49028, 49029, 49030 };
						counts = new int[] { 1, 1, 1, 1 };
						createitem = new int[] { 41140 };
						createcount = new int[] { 1 };
						htmlid = "dh8";
					} else {
						materials = new int[] { 41139, 49028, 49029, 49030 };
						counts = new int[] { 1, 1, 1, 1 };
						createitem = new int[] { 49270 }; // ジュエリーパウダー
						createcount = new int[] { 1 };
						htmlid = "dh7";
					}
				} else {
					htmlid = "dh6";
				}
			} else if (s.equalsIgnoreCase("B")) {// 輝く古代のアミュレット
				if (pc.getInventory().checkItem(41140, 1) && // 復元された古代のアミュレット
						pc.getInventory().checkItem(49027, 1)) { // タロスのダイヤモンド
					if (_random.nextInt(100) < 40) { // 暫定で40%位で設定
						materials = new int[] { 41140, 49027 };
						counts = new int[] { 1, 1 };
						createitem = new int[] { 20422 };
						createcount = new int[] { 1 };
						htmlid = "dh9";
					} else {
						materials = new int[] { 41140, 49027 };
						counts = new int[] { 1, 1 };
						createitem = new int[] { 49270 }; // ジュエリーパウダー
						createcount = new int[] { 1 };
						htmlid = "dh7";
					}
				} else {
					htmlid = "dh6";
				}
			}
		}// ロウギ　「欲心はまた他の欲心を呼んで」
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71178) {
			if (s.equalsIgnoreCase("A")) {// 崩れた古代のアミュレット
				if (pc.getInventory().checkItem(41139, 1)) { // 崩れた古代のアミュレット
					pc.getInventory().consumeItem(41139, 1);
					pc.getInventory().storeItem(40308, 500);
					htmlid = "ru8";
				} else {
					htmlid = "ru4";
				}
			} else if (s.equalsIgnoreCase("B")) {// タロスのルビー
				if (pc.getInventory().checkItem(49028, 1)) { // タロスのルビー
					pc.getInventory().consumeItem(49028, 1);
					pc.getInventory().storeItem(40308, 100);
					htmlid = "ru6";
				} else {
					htmlid = "ru4";
				}
			} else if (s.equalsIgnoreCase("C")) {// タロスのエメラルド
				if (pc.getInventory().checkItem(49030, 1)) { // タロスのエメラルド
					pc.getInventory().consumeItem(49030, 1);
					pc.getInventory().storeItem(40308, 100);
					htmlid = "ru6";
				} else {
					htmlid = "ru4";
				}
			} else if (s.equalsIgnoreCase("D")) {// タロスのサファイヤ
				if (pc.getInventory().checkItem(49029, 1)) { // タロスのサファイヤ
					pc.getInventory().consumeItem(49029, 1);
					pc.getInventory().storeItem(40308, 100);
					htmlid = "ru6";
				} else {
					htmlid = "ru4";
				}
			} else if (s.equalsIgnoreCase("E")) {// 復元された古代のアミュレット
				if (pc.getInventory().checkItem(41140, 1)) { // 復元された古代のアミュレット
					pc.getInventory().consumeItem(41140, 1);
					pc.getInventory().storeItem(40308, 10000);
					htmlid = "ru9";
				} else {
					htmlid = "ru4";
				}
			}
		}// 白魔道士 ピエタ 「君主Lv45クエスト」
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 71200) {
			if (s.equalsIgnoreCase("a")) {
				pc.getQuest().setStep(L1Quest.QUEST_LEVEL45, 3);
				materials = new int[] { 41422 };
				counts = new int[] { 1 };
				createitem = new int[] { 40568 };
				createcount = new int[] { 1 };
				htmlid = "pieta5";
			} else if (s.equalsIgnoreCase("b")) {
				materials = new int[] { 41422 };
				counts = new int[] { 1 };
				createitem = new int[] { 40568 };
				createcount = new int[] { 1 };
				htmlid = "pieta9";
			} else if (s.equalsIgnoreCase("0")) {
				htmlid = "";
				L1Teleport.teleport(pc, 32726, 32799, (short) 270, 2, true);
			}

		}// ディカルデン 「君主/ナイト/エルフ/ウィザードLv50クエスト」
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70739) {
			if (pc.isCrown()) {
				if (s.equalsIgnoreCase("e")) {
					if (pc.getInventory().checkItem(49159, 1)) {
						htmlid = "dicardingp5";
						pc.getInventory().consumeItem(49159, 1);
						pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 2);
					} else {
						htmlid = "dicardingp4a";
					}
				} else if (s.equalsIgnoreCase("d")) {
					htmlid = "dicardingp7";
					L1PolyMorph.doPoly(pc, 6035, 900, 1);
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 3);
				} else if (s.equalsIgnoreCase("c")) {
					htmlid = "dicardingp9";
					L1PolyMorph.undoPoly(pc);
					L1PolyMorph.doPoly(pc, 6035, 900, 1);
				} else if (s.equalsIgnoreCase("b")) {
					htmlid = "dicardingp12";
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 4);
					if (pc.getInventory().checkItem(49165)) {
						pc.getInventory().consumeItem(49165, pc.getInventory().countItems(49165));
					}
					if (pc.getInventory().checkItem(49166)) {
						pc.getInventory().consumeItem(49166, pc.getInventory().countItems(49166));
					}
					if (pc.getInventory().checkItem(49167)) {
						pc.getInventory().consumeItem(49167, pc.getInventory().countItems(49167));
					}
					if (pc.getInventory().checkItem(49168)) {
						pc.getInventory().consumeItem(49168, pc.getInventory().countItems(49168));
					}
					if (pc.getInventory().checkItem(49239)) {
						pc.getInventory().consumeItem(49239, pc.getInventory().countItems(49239));
					}
				}
			}
			if (pc.isKnight()) {
				if (s.equalsIgnoreCase("h")) {
					if (pc.getInventory().checkItem(49160, 1)) {
						htmlid = "dicardingk5";
						pc.getInventory().consumeItem(49160, 1);
						pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 2);
					}
				} else if (s.equalsIgnoreCase("j")) {
					htmlid = "dicardingk10";
					pc.getInventory().consumeItem(49161, 10);
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 4);
				} else if (s.equalsIgnoreCase("k")) {
					htmlid = "dicardingk13";
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 4);
					if (pc.getInventory().checkItem(49165)) {
						pc.getInventory().consumeItem(49165, pc.getInventory().countItems(49165));
					}
					if (pc.getInventory().checkItem(49166)) {
						pc.getInventory().consumeItem(49166, pc.getInventory().countItems(49166));
					}
					if (pc.getInventory().checkItem(49167)) {
						pc.getInventory().consumeItem(49167, pc.getInventory().countItems(49167));
					}
					if (pc.getInventory().checkItem(49168)) {
						pc.getInventory().consumeItem(49168, pc.getInventory().countItems(49168));
					}
					if (pc.getInventory().checkItem(49239)) {
						pc.getInventory().consumeItem(49239, pc.getInventory().countItems(49239));
					}
				}
			}
			if (pc.isElf()) {
				if (s.equalsIgnoreCase("n")) {
					if (pc.getInventory().checkItem(49162, 1)) {
						htmlid = "dicardinge5";
						pc.getInventory().consumeItem(49162, 1);
						pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 2);
					}
				} else if (s.equalsIgnoreCase("p")) {
					htmlid = "dicardinge10";
					pc.getInventory().consumeItem(49163, 1);
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 5);
				} else if (s.equalsIgnoreCase("q")) {
					htmlid = "dicardinge14";
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 5);
					if (pc.getInventory().checkItem(49165)) {
						pc.getInventory().consumeItem(49165, pc.getInventory().countItems(49165));
					}
					if (pc.getInventory().checkItem(49166)) {
						pc.getInventory().consumeItem(49166, pc.getInventory().countItems(49166));
					}
					if (pc.getInventory().checkItem(49167)) {
						pc.getInventory().consumeItem(49167, pc.getInventory().countItems(49167));
					}
					if (pc.getInventory().checkItem(49168)) {
						pc.getInventory().consumeItem(49168, pc.getInventory().countItems(49168));
					}
					if (pc.getInventory().checkItem(49239)) {
						pc.getInventory().consumeItem(49239, pc.getInventory().countItems(49239));
					}
				}
			}
			if (pc.isWizard()) {
				if (s.equalsIgnoreCase("u")) {
					if (pc.getInventory().checkItem(49164, 1)) {
						htmlid = "dicardingw6";
						pc.getInventory().consumeItem(49164, 1);
						pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 3);
					}
				} else if (s.equalsIgnoreCase("w")) {
					htmlid = "dicardingw12";
					pc.getQuest().setStep(L1Quest.QUEST_LEVEL50, 4);
					if (pc.getInventory().checkItem(49165)) {
						pc.getInventory().consumeItem(49165, pc.getInventory().countItems(49165));
					}
					if (pc.getInventory().checkItem(49166)) {
						pc.getInventory().consumeItem(49166, pc.getInventory().countItems(49166));
					}
					if (pc.getInventory().checkItem(49167)) {
						pc.getInventory().consumeItem(49167, pc.getInventory().countItems(49167));
					}
					if (pc.getInventory().checkItem(49168)) {
						pc.getInventory().consumeItem(49168, pc.getInventory().countItems(49168));
					}
					if (pc.getInventory().checkItem(49239)) {
						pc.getInventory().consumeItem(49239, pc.getInventory().countItems(49239));
					}
				}
			}
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 91308) { // 捨てられた肉体
			if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(49239, 1)) {
					htmlid = "rtf06";
				} else {
					createitem = new int[] { 49239 };
					createcount = new int[] { 1 };
				}
			}
		}// 宝石細工職人 デービッド　「IQイヤリングクエスト」
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80192) {
			if (s.equalsIgnoreCase("b")) {// アイスクイーンのイヤリング　１段階
				if (pc.getInventory().checkEquipped(21081)) {
					htmlid = "8event6";
				} else if (pc.getInventory().checkItem(49031, 1) && // アイスクリスタル
						pc.getInventory().checkItem(21081, 1)) { // イヤリング０段階
					materials = new int[] { 49031, 21081 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 21082 };
					createcount = new int[] { 1 };
					htmlid = "8event4";
				}
			} else if (s.equalsIgnoreCase("c")) {// アイスクイーンのイヤリング　２段階
				if (pc.getInventory().checkEquipped(21082)) {
					htmlid = "8event6";
				} else if (pc.getInventory().checkItem(49031, 1) && // アイスクリスタル
						pc.getInventory().checkItem(21082, 1)) { // イヤリング１段階
					materials = new int[] { 49031, 21082 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 21083 };
					createcount = new int[] { 1 };
					htmlid = "8event4";
				}
			} else if (s.equalsIgnoreCase("d")) {// アイスクイーンのイヤリング　３段階
				if (pc.getInventory().checkEquipped(21083)) {
					htmlid = "8event6";
				} else if (pc.getInventory().checkItem(49031, 1) && // アイスクリスタル
						pc.getInventory().checkItem(21083, 1)) { // イヤリング２段階
					materials = new int[] { 49031, 21083 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 21084 };
					createcount = new int[] { 1 };
					htmlid = "8event4";
				}
			} else if (s.equalsIgnoreCase("e")) {// アイスクイーンのイヤリング　４段階
				if (pc.getInventory().checkEquipped(21084)) {
					htmlid = "8event6";
				} else if (pc.getInventory().checkItem(49031, 1) && // アイスクリスタル
						pc.getInventory().checkItem(21084, 1)) { // イヤリング３段階
					materials = new int[] { 49031, 21084 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 21085 };
					createcount = new int[] { 1 };
					htmlid = "8event4";
				}
			} else if (s.equalsIgnoreCase("f")) {// アイスクイーンのイヤリング　５段階
				if (pc.getInventory().checkEquipped(21085)) {
					htmlid = "8event6";
				} else if (pc.getInventory().checkItem(49031, 1) && // アイスクリスタル
						pc.getInventory().checkItem(21085, 1)) { // イヤリング４段階
					materials = new int[] { 49031, 21085 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 21086 };
					createcount = new int[] { 1 };
					htmlid = "8event4";
				}
			} else if (s.equalsIgnoreCase("g")) {// アイスクイーンのイヤリング　６段階
				if (pc.getInventory().checkEquipped(21086)) {
					htmlid = "8event6";
				} else if (pc.getInventory().checkItem(49031, 1) && // アイスクリスタル
						pc.getInventory().checkItem(21086, 1)) { // イヤリング５段階
					materials = new int[] { 49031, 21086 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 21087 };
					createcount = new int[] { 1 };
					htmlid = "8event4";
				}
			} else if (s.equalsIgnoreCase("h")) {// アイスクイーンのイヤリング　７段階
				if (pc.getInventory().checkEquipped(21087)) {
					htmlid = "8event6";
				} else if (pc.getInventory().checkItem(49031, 1) && // アイスクリスタル
						pc.getInventory().checkItem(21087, 1)) { // イヤリング６段階
					materials = new int[] { 49031, 21087 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 21088 };
					createcount = new int[] { 1 };
					htmlid = "8event4";
				}
			} else if (s.equalsIgnoreCase("i")) {// アイスクイーンのイヤリング　８段階+STR
				if (pc.getInventory().checkEquipped(21088)) {
					htmlid = "8event6";
				} else if (pc.getInventory().checkItem(49031, 1) && // アイスクリスタル
						pc.getInventory().checkItem(21088, 1)) { // イヤリング７段階
					materials = new int[] { 49031, 21088 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 21089 };
					createcount = new int[] { 1 };
					htmlid = "8event4";
				}
			} else if (s.equalsIgnoreCase("j")) {// アイスクイーンのイヤリング　８段階+DEX
				if (pc.getInventory().checkEquipped(21088)) {
					htmlid = "8event6";
				} else if (pc.getInventory().checkItem(49031, 1) && // アイスクリスタル
						pc.getInventory().checkItem(21088, 1)) { // イヤリング７段階
					materials = new int[] { 49031, 21088 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 21090 };
					createcount = new int[] { 1 };
					htmlid = "8event4";
				}
			} else if (s.equalsIgnoreCase("k")) {// アイスクイーンのイヤリング　８段階+WIS
				if (pc.getInventory().checkEquipped(21088)) {
					htmlid = "8event6";
				} else if (pc.getInventory().checkItem(49031, 1) && // アイスクリスタル
						pc.getInventory().checkItem(21088, 1)) { // イヤリング７段階
					materials = new int[] { 49031, 21088 };
					counts = new int[] { 1, 1 };
					createitem = new int[] { 21091 };
					createcount = new int[] { 1 };
					htmlid = "8event4";
				}
			} else {
				htmlid = "8event3";
			}
		}
		// ペット商人・引き換え
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70077 // ロドニー
				|| ((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 91056) { // バンク
			SellOfPet(s, pc);
			htmlid = "";
		}
		// ヒレン
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80238) {
			if (s.equalsIgnoreCase("a")) {
				createitem = new int[] { 50537 }; // 真実のティーバッグ
				createcount = new int[] { 1 };
				htmlid = "hiren2";
			}
		}
		// ジェフ
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80239) {
			if (s.equalsIgnoreCase("a")) { // 誓約します!
				createitem = new int[] { 50538 }; // ジェフの契約書
				createcount = new int[] { 1 };
				htmlid = "jeff2_2";
			} else if (s.equalsIgnoreCase("b")) { // 強化魔法薬をください
				if (pc.getInventory().checkItem(41252, 1) && // レア タートル
					pc.getInventory().checkItem(41265, 1) && // ハチミツ
					pc.getInventory().checkItem(50533, 1) && // 神秘のキノコパウダー
					pc.getInventory().checkItem(50534, 1) && // 悲しみの涙
					pc.getInventory().checkItem(50535, 1)) { // 樹液
					materials = new int[] { 41252, 41265, 50533, 50534, 50535 };
					counts = new int[] { 1, 1, 1, 1, 1 };
					createitem = new int[] { 50539 }; // ジェフの魔法薬
					createcount = new int[] { 1 };
					htmlid = "";
				} else if (pc.getInventory().checkItem(50536, 1)) { // 魔法薬の材料
					materials = new int[] { 50536 };
					counts = new int[] { 1 };
					createitem = new int[] { 50539 }; // ジェフの魔法薬
					createcount = new int[] { 1 };
					htmlid = "";
				} else {
					htmlid = "jeff4";
				}
			} else if (s.equalsIgnoreCase("c")) { // チャージしたいです
				if (pc.getInventory().checkItem(40308, 1000)) {
					if (pc.getInventory().checkItem(50571, 1)
							|| pc.getAdditionalWarehouseInventory().checkItem(50571, 1)) { // 領収証
						htmlid = "jeff5"; // 前回購入から24時間経過していない
					} else {
						pc.getInventory().storeItem(50560, 1); // 1時間のマジックチャージ
						L1ItemInstance item = pc.getInventory().storeItem(50571, 1);
						item.startExpirationTimer(pc);
						htmlid = "";
					}
				} else {
					htmlid = "jeff6";
				}
			} else if (s.equalsIgnoreCase("d")) {
				htmlid = "jeff3";
			} else {
				htmlid = "jeff1";
			}
		}
		// 孤独な精霊
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80242) {
			if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(50534, 1)) { // 悲しみの涙
					htmlid = "tearfairy3";
				} else {
					pc.getInventory().storeItem(50534, 1);
					htmlid = "";
				}
			}
		// 初心者案内人(新隠された渓谷)
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80171) {
			int level = pc.getLevel();
			if (pc.getLevel() < 2) {
				pc.addExp(ExpTable.getNeedExpNextLevel(pc.getLevel()));
				L1ItemInstance item = pc.getInventory().storeItem(42099, 5);
				pc.sendPackets(new S_ServerMessage(143, item.getItem().getName()));
				htmlid = "";
			} if (s.equalsIgnoreCase("A")) { // 君主
				if ((level >= 2) && (level <= 4)) {
					htmlid = "tutorp1";
				} else if ((level >= 5) && (level <= 7)) {
					htmlid = "tutorp2";
				} else if ((level >= 8) && (level <= 9)) {
					htmlid = "tutorp3";
				} else if ((level >= 10) && (level <= 11)) {
					htmlid = "tutorp4";
				} else if (level == 12) {
					htmlid = "tutorp5";
				} else if (level >= 13) {
					htmlid = "tutorp6";
				}
			} else if (s.equalsIgnoreCase("B")) { // ナイト
				if ((level >= 2) && (level <= 4)) {
					htmlid = "tutork1";
				} else if ((level >= 5) && (level <= 7)) {
					htmlid = "tutork2";
				} else if ((level >= 8) && (level <= 9)) {
					htmlid = "tutork3";
				} else if ((level >= 10) && (level <= 11)) {
					htmlid = "tutork4";
				} else if (level == 12) {
					htmlid = "tutork5";
				} else if (level >= 13) {
					htmlid = "tutorend";
				}
			} else if (s.equalsIgnoreCase("C")) { // エルフ
				if ((level >= 2) && (level <= 4)) {
					htmlid = "tutore1";
				} else if ((level >= 5) && (level <= 7)) {
					htmlid = "tutore2";
				} else if ((level >= 8) && (level <= 9)) {
					htmlid = "tutore3";
				} else if ((level >= 10) && (level <= 11)) {
					htmlid = "tutore4";
				} else if (level == 12) {
					htmlid = "tutore5";
				} else if (level >= 13) {
					htmlid = "tutore6";
				}
			} else if (s.equalsIgnoreCase("D")) { // ウィザード
				if ((level >= 2) && (level <= 4)) {
					htmlid = "tutorm1";
				} else if ((level >= 5) && (level <= 7)) {
					htmlid = "tutorm2";
				} else if ((level >= 8) && (level <= 9)) {
					htmlid = "tutorm3";
				} else if ((level >= 10) && (level <= 11)) {
					htmlid = "tutorm4";
				} else if (level == 12) {
					htmlid = "tutorm5";
				} else if (level >= 13) {
					htmlid = "tutorm6";
				}
			} else if (s.equalsIgnoreCase("E")) { // ダークエルフ
				if ((level >= 2) && (level <= 4)) {
					htmlid = "tutord1";
				} else if ((level >= 5) && (level <= 7)) {
					htmlid = "tutord2";
				} else if ((level >= 8) && (level <= 9)) {
					htmlid = "tutord3";
				} else if ((level >= 10) && (level <= 11)) {
					htmlid = "tutord4";
				} else if (level == 12) {
					htmlid = "tutord5";
				} else if (level >= 13) {
					htmlid = "tutord6";
				}
			} else if (s.equalsIgnoreCase("F")) { // ドラゴンナイト
				if ((level >= 2) && (level <= 4)) {
					htmlid = "tutordk1";
				} else if ((level >= 5) && (level <= 7)) {
					htmlid = "tutordk2";
				} else if ((level >= 8) && (level <= 9)) {
					htmlid = "tutordk3";
				} else if ((level >= 10) && (level <= 11)) {
					htmlid = "tutordk4";
				} else if (level == 12) {
					htmlid = "tutordk5";
				} else if (level >= 13) {
					htmlid = "tutorend";
				}
			} else if (s.equalsIgnoreCase("G")) { // イリュージョニスト
				if ((level >= 2) && (level <= 4)) {
					htmlid = "tutori1";
				} else if ((level >= 5) && (level <= 7)) {
					htmlid = "tutori2";
				} else if ((level >= 8) && (level <= 9)) {
					htmlid = "tutori3";
				} else if ((level >= 10) && (level <= 11)) {
					htmlid = "tutori4";
				} else if (level == 12) {
					htmlid = "tutori5";
				} else if (level >= 13) {
					htmlid = "tutorend";
				}
			} else if (s.equalsIgnoreCase("H")) { // 話せる島の倉庫番
				L1Teleport.teleport(pc, 32575, 32945, (short) 0, 5, true);
				createitem = new int[] { 42099 };
				createcount = new int[] { 1 };
				htmlid = "";
			} else if (s.equalsIgnoreCase("I")) { // 血盟執行員
				L1Teleport.teleport(pc, 32579, 32923, (short) 0, 5, true);
				createitem = new int[] { 42099 };
				createcount = new int[] { 1 };
				htmlid = "";
			} else if (s.equalsIgnoreCase("J")) { // 隠された渓谷のダンジョン
				L1Teleport.teleport(pc, 32676, 32813, (short) 2005, 5, true);
				createitem = new int[] { 42099 };
				createcount = new int[] { 1 };
				htmlid = "";
			} else if (s.equalsIgnoreCase("K")) { // ウィザードゲレン
				L1Teleport.teleport(pc, 32562, 33082, (short) 0, 5, true);
				createitem = new int[] { 42099 };
				createcount = new int[] { 1 };
				htmlid = "";
			} else if (s.equalsIgnoreCase("L")) { // 象牙の塔
				L1Teleport.teleport(pc, 32792, 32820, (short) 75, 5, true);
				createitem = new int[] { 42099 };
				createcount = new int[] { 1 };
				htmlid = "";
			} else if (s.equalsIgnoreCase("M")) { // ダークウィザードセディア
				L1Teleport.teleport(pc, 32877, 32904, (short) 304, 5, true);
				createitem = new int[] { 42099 };
				createcount = new int[] { 1 };
				htmlid = "";
			} else if (s.equalsIgnoreCase("N")) { // イリュージョニストスビエル
				L1Teleport.teleport(pc, 32759, 32884, (short) 1000, 5, true);
				createitem = new int[] { 42099 };
				createcount = new int[] { 1 };
				htmlid = "";
			} else if (s.equalsIgnoreCase("O")) { // 村の西側
				L1Teleport.teleport(pc, 32605, 32837, (short) 2005, 5, true);
				htmlid = "";
			} else if (s.equalsIgnoreCase("P")) { // 村の東側
				L1Teleport.teleport(pc, 32733, 32902, (short) 2005, 5, true);
				htmlid = "";
			} else if (s.equalsIgnoreCase("Q")) { // 村の南西の狩場
				L1Teleport.teleport(pc, 32559, 32843, (short) 2005, 5, true);
				htmlid = "";
			} else if (s.equalsIgnoreCase("R")) { // 村の南東の狩場
				L1Teleport.teleport(pc, 32677, 32982, (short) 2005, 5, true);
				htmlid = "";
			} else if (s.equalsIgnoreCase("S")) { // 村の北東の狩場
				L1Teleport.teleport(pc, 32781, 32854, (short) 2005, 5, true);
				htmlid = "";
			} else if (s.equalsIgnoreCase("T")) { // 村の北西の狩場
				L1Teleport.teleport(pc, 32674, 32739, (short) 2005, 5, true);
				htmlid = "";
			} else if (s.equalsIgnoreCase("U")) { // 村の西の狩場
				L1Teleport.teleport(pc, 32578, 32737, (short) 2005, 5, true);
				htmlid = "";
			} else if (s.equalsIgnoreCase("V")) { // 村の南の狩場
				L1Teleport.teleport(pc, 32542, 32996, (short) 2005, 5, true);
				htmlid = "";
			} else if (s.equalsIgnoreCase("W")) { // 村の東の狩場
				L1Teleport.teleport(pc, 32794, 32973, (short) 2005, 5, true);
				htmlid = "";
			} else if (s.equalsIgnoreCase("X")) { // 村の北の狩場
				L1Teleport.teleport(pc, 32803, 32789, (short) 2005, 5, true);
				htmlid = "";
			} else {
				htmlid = "";
			}
		// 修練場管理員
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80172) {
			if (s.equalsIgnoreCase("A")) {
				giveBeginnerItems(pc);
				htmlid = "";
			}
		// ホバン
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80179) {
			if (s.equalsIgnoreCase("f")) { // おじさんにこの飴をあげます。
				htmlid = "j_nb51";
			} if (s.equalsIgnoreCase("g")) { // はい、おじさん
				int level = pc.getLevel();
				int quest_step = pc.getQuest().getStep(L1Quest.QUEST_NEWBIE);
				if (level < 10 && quest_step <= 1) {
					pc.getQuest().setStep(L1Quest.QUEST_NEWBIE, 2);
					materials = new int[] { 50546 };
					counts = new int[] { 1 };
					createitem = new int[] { 21141, 40029, 50546 };
					// 象牙の塔の防御の紋様, 象牙の塔の体力回復剤, お菓子のかご
					createcount = new int[] { 1, 50, 1 };
					htmlid = "j_nb11";
				} else if (level < 15 && quest_step <= 2) {
					pc.getQuest().setStep(L1Quest.QUEST_NEWBIE, 3);
					materials = new int[] { 50546 };
					counts = new int[] { 1 };
					createitem = new int[] { 21138, 21144, 40029, 50546 };
					// 象牙の塔の魔法の紋様, 象牙の塔の生命の紋様, 象牙の塔の体力回復剤, お菓子のかご
					createcount = new int[] { 1, 1, 50, 1 };
					htmlid = "j_nb11";
				} else if (level < 20 && quest_step <= 3) {
					pc.getQuest().setStep(L1Quest.QUEST_NEWBIE, 4);
					materials = new int[] { 50546 };
					counts = new int[] { 1 };
					createitem = new int[] { 21121, 21124, 21127, 21130, 21133, 40029, 50546 };
					// 象牙の塔の腕力の紋様, 象牙の塔の精神の紋様, 象牙の塔の知力の紋様,
					// 象牙の塔の機敏の紋様, 象牙の塔の体力の紋様, 象牙の塔の体力回復剤, お菓子のかご
					createcount = new int[] { 1, 1, 1, 1, 1, 50, 1 };
					htmlid = "j_nb11";
				} else if (level < 22 && quest_step <= 4) {
					pc.getQuest().setStep(L1Quest.QUEST_NEWBIE, 5);
					materials = new int[] { 50546 };
					counts = new int[] { 1 };
					createitem = new int[] { 21113, 40029, 50546 };
					// 象牙の塔の祈りの紋様, 象牙の塔の体力回復剤, お菓子のかご
					createcount = new int[] { 1, 50, 1 };
					htmlid = "j_nb11";
				} else if (level < 24 && quest_step <= 5) {
					pc.getQuest().setStep(L1Quest.QUEST_NEWBIE, 6);
					materials = new int[] { 50546 };
					counts = new int[] { 1 };
					createitem = new int[] { 40029, 50546, 50630, 50631 };
					// 象牙の塔の体力回復剤, お菓子のかご, 憤怒のポーション, 集中のポーション
					createcount = new int[] { 50, 1, 3, 3 };
					htmlid = "j_nb11";
				} else if (level < 26 && quest_step <= 6) {
					pc.getQuest().setStep(L1Quest.QUEST_NEWBIE, 7);
					materials = new int[] { 50546 };
					counts = new int[] { 1 };
					createitem = new int[] { 40029, 50546, 50625, 50626, 50627, 50628, 50629 };
					// 象牙の塔の体力回復剤, お菓子のかご, 腕力, 機敏, 体力, 知力, 精神のポーション
					createcount = new int[] { 50, 1, 3, 3, 3, 3, 3 };
					htmlid = "j_nb11";
				} else if (level < 28 && quest_step <= 7) {
					pc.getQuest().setStep(L1Quest.QUEST_NEWBIE, 8);
					materials = new int[] { 50546 };
					counts = new int[] { 1 };
					createitem = new int[] { 40029, 50546, 50620, 50621 };
					// 象牙の塔の体力回復剤, お菓子のかご, 治癒のポーション, 瞑想のポーション
					createcount = new int[] { 50, 1, 3, 3 };
					htmlid = "j_nb11";
				} else if (level < 28 && quest_step <= 8) {
					pc.getQuest().setStep(L1Quest.QUEST_NEWBIE, 9);
					materials = new int[] { 50546 };
					counts = new int[] { 1 };
					createitem = new int[] { 40029, 50546, 50622, 50623, 50624 };
					// 象牙の塔の体力回復剤, お菓子のかご, 生命, 魔法, 魔法抵抗のポーション
					createcount = new int[] { 50, 1, 3, 3, 3 };
					htmlid = "j_nb11";
				} else if (level < 31 && quest_step <= 9) {
					pc.getQuest().setStep(L1Quest.QUEST_NEWBIE, 10);
					materials = new int[] { 50546 };
					counts = new int[] { 1 };
					createitem = new int[] { 50546 };
					createcount = new int[] { 1 };
					htmlid = "j_nb11";
				} else {
					htmlid = "";
				}
			}
		// 旅人案内人
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80180) {
			int level = pc.getLevel();
			if (level < 46) {
				if (s.equals("a")) { // ゲレンのところにテレポートする
					L1Teleport.teleport(pc, 32592, 32957, (short) 0, 5, true);
					htmlid = "";
				} else if (s.equals("b")) { // ロウフルテンプルにテレポートする
					L1Teleport.teleport(pc, 33119, 32933, (short) 4, 5, true);
					htmlid = "";
				} else if (s.equals("c")) { // カオティックテンプルにテレポートする
					L1Teleport.teleport(pc, 32887, 32652, (short) 4, 5, true);
					htmlid = "";
				} else if (s.equals("d")) { // リンダのところにテレポートする
					L1Teleport.teleport(pc, 32792, 32820, (short) 75, 5, true);
					htmlid = "";
				} else if (s.equals("e")) { // 精霊魔法修練室にテレポートする
					L1Teleport.teleport(pc, 32789, 32851, (short) 76, 5, true);
					htmlid = "";
				} else if (s.equals("f")) { // エリオンのところにテレポートする
					L1Teleport.teleport(pc, 32750, 32847, (short) 76, 5, true);
					htmlid = "";
				} else if (s.equals("g")) { // セディアのところにテレポートする
					if (pc.isDarkelf()) {
						L1Teleport.teleport(pc, 32877, 32904, (short) 304, 5, true);
						htmlid = "";
					} else {
						htmlid = "lowlv40";
					}
				} else if (s.equals("h")) { // ジェパールのところにテレポートする
					if (pc.isDragonKnight()) {
						L1Teleport.teleport(pc, 32811, 32873, (short) 1001, 5, true);
						htmlid = "";
					} else {
						htmlid = "lowlv41";
					}
				} else if (s.equals("i")) { // スビエルのところにテレポートする
					if (pc.isIllusionist()) {
						L1Teleport.teleport(pc, 32759, 32884, (short) 1000, 5, true);
						htmlid = "";
					} else {
						htmlid = "lowlv42";
					}
				} else if (s.equals("j")) { // グンターのところにテレポートする
					L1Teleport.teleport(pc, 32509, 32867, (short) 0, 5, true);
					htmlid = "";
				} else if (s.equals("k")) { // 象牙の塔のアクセサリー
					if ((level > 34)) {
						createitem = new int[] { 21155, 21169, 21223, 21224, 21225 };
						// 象牙の塔のタリスマン, 象牙の塔のイアリング, 修行者のアミュレット,
						// 修行者のリング, 修行者のベルト
						createcount = new int[] { 1, 1, 1, 2, 1 };
						boolean isOK = false;
						for (int i = 0; i < createitem.length; i++) {
							if (!pc.getInventory().checkItem(createitem[i], 1)) {
								createcount[i] = 1;
								isOK = true;
							}
						}
						if (isOK) {
							success_htmlid = "lowlv43";
						} else {
							htmlid = "lowlv45";
						}
					} else {
						htmlid = "lowlv44";
					}
				} else if (s.equals("0")) { // 戻る
					if (level < 13) {
						htmlid = "lowlvS1";
					} else if ((level > 12) && (level < 46)) {
						htmlid = "lowlvS2";
					} else {
						htmlid = "lowlvno";
					}
				} else if (s.equals("1")) { // 他の助言を聞く
					if (level < 13) {
						htmlid = "lowlv14";
					} else if ((level > 12) && (level < 46)) {
						htmlid = "lowlv15";
					} else {
						htmlid = "lowlvno";
					}
				} else if (s.equals("2")) { // 象牙の塔の装備を再度受け取る
					if (giveBeginnerItems(pc)) {
						success_htmlid = "lowlv16";
					} else {
						htmlid = "lowlv17";
					}
				} else if (s.equals("6")) { // 象牙の塔の魔法の袋を購入する
					if (!pc.getInventory().checkItem(50544, 1)
							&& !pc.getInventory().checkItem(50545, 1)) {
						createitem = new int[] { 50544 };
						createcount = new int[] { 2 };
						materials = new int[] { 40308 };
						counts = new int[] { 2000 };
						success_htmlid = "lowlv22";
						failure_htmlid = "lowlv20";
					} else if (pc.getInventory().checkItem(50544, 1)
							|| pc.getInventory().checkItem(50545, 1)) {
						htmlid = "lowlv23";
					} else {
						htmlid = "lowlvno";
					}
				} else if (s.equals("<")) { // 防具魔法スクロール(7個)
					createitem = new int[] { 50541 };
					createcount = new int[] { 5 };
					materials = new int[] { 40308 };
					counts = new int[] { 5000 };
					success_htmlid = "lowlv18";
					failure_htmlid = "lowlv20";
				} else if (s.equals(">")) { // 防具魔法スクロール(7個)
					createitem = new int[] { 50541 };
					createcount = new int[] { 7 };
					materials = new int[] { 40308 };
					counts = new int[] { 7000 };
					success_htmlid = "lowlv18";
					failure_htmlid = "lowlv20";
				}
			}
		// 鍛冶屋 クス
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70517) {
			if (s.equalsIgnoreCase("fixFree")) { // 壊れた武器を修理する
				boolean fixed = false;
				for (L1ItemInstance item : pc.getInventory().getItems()) {
					if (item.getItem().getType2() != 0
							&& item.getDurability() > 0) {
						item.setDurability(0);
						pc.sendPackets(new S_ServerMessage(464, item.getLogName()));
						// %0%sは新品同様の状態になりました。
						fixed = true;
					}
				}
				if (fixed) {
					htmlid = "cuse2";
				} else {
					htmlid = "";
				}
			}
		// キルトン
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80263) {
			if (s.equalsIgnoreCase("0")) { // タイガーの狩りを依頼する
				if (pc.getInventory().checkItem(50565, 1)
						|| pc.getAdditionalWarehouseInventory().checkItem(50565, 1)) { // キルトンの契約書
					htmlid = "killton2";
				} else if (pc.getInventory().checkItem(50567)
						|| pc.getAdditionalWarehouseInventory().checkItem(50567) // 配送員ミミックの笛：タイガー飼育場
						|| pc.getInventory().checkItem(50569)
						|| pc.getAdditionalWarehouseInventory().checkItem(50569)) { // タイガー飼育場
					htmlid = "killton4";
				} else if (pc.getInventory().checkItem(40308, 500000)) {
					pc.getInventory().consumeItem(40308, 500000);
					L1ItemInstance item = pc.getInventory().storeItem(50565, 1);
					item.startExpirationTimer(pc);
					htmlid = "";
				} else {
					htmlid = "killton3";
				}
			}
		// メーリン
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80264) {
			if (s.equalsIgnoreCase("0")) { // 紀州犬の子犬を引き取る
				if (pc.getInventory().checkItem(50566, 1)
						|| pc.getAdditionalWarehouseInventory().checkItem(50566, 1)) { // メーリンの契約書
					htmlid = "merin2";
				} else if (pc.getInventory().checkItem(50568)
						|| pc.getAdditionalWarehouseInventory().checkItem(50568) // 配送員ミミックの笛：紀州犬のかご
						|| pc.getInventory().checkItem(50570)
						|| pc.getAdditionalWarehouseInventory().checkItem(50570)) { // 紀州犬のかご
					htmlid = "merin4";
				} else if (pc.getInventory().checkItem(40308, 500000)) {
					pc.getInventory().consumeItem(40308, 500000);
					L1ItemInstance item = pc.getInventory().storeItem(50566, 1);
					item.startExpirationTimer(pc);
					htmlid = "";
				} else {
					htmlid = "merin3";
				}
			}
		// 特産物管理人 モポ
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80266) {
			if (s.equalsIgnoreCase("a")) { // ドワーフブドウジュース100個契約
				htmlid = getDwarfPotion(pc, 50579, 3000);
			} else if (s.equalsIgnoreCase("b")) { // ドワーフブドウジュース200個契約
				htmlid = getDwarfPotion(pc, 50580, 6000);
			} else if (s.equalsIgnoreCase("c")) { // ドワーフブドウジュース300個契約
				htmlid = getDwarfPotion(pc, 50581, 9000);
			} else if (s.equalsIgnoreCase("d")) { // モポの契約書：エキス100本契約
				htmlid = getDwarfPotion(pc, 50582, 3000);
			} else if (s.equalsIgnoreCase("e")) { // モポの契約書：エキス200本契約
				htmlid = getDwarfPotion(pc, 50583, 6000);
			} else if (s.equalsIgnoreCase("f")) { // モポの契約書：エキス300本契約
				htmlid = getDwarfPotion(pc, 50584, 9000);
			}
		// 強化ウィザード
		} else if ((((L1NpcInstance) obj).getNpcTemplate().getNpcId() >= 81352)
				&& (((L1NpcInstance) obj).getNpcTemplate().getNpcId() <= 81362)) {
			if (s.equals("a") || s.equals("b") || s.equals("c")){
				int[] skills = new int[10];
				// リムーブカーズ、フルヒール、ヘイスト、アドバンスドスピリッツ、、
				// アクアプロテクター、アイアンスキン、ペイシェンス、インサイト、
				if (s.equals("a")) { // 「強烈な炎の魔法」をもらう
					skills = new int[] {37, 57, 43, 79, 160, 168, 211, 216, 148};
					// ファイアーウェポン
				} else if (s.equals("b")) { // 「強烈な風の魔法」をもらう
					skills = new int[] {37, 57, 43, 79, 160, 168, 211, 216, 149};
					// ウィンドショット
				} else if (s.equals("c")) { // 「強烈な魂の魔法」をもらう
					// コンセントレーション
					skills = new int[] {37, 57, 43, 79, 160, 168, 211, 216, 206};
				}
				if(pc.getInventory().consumeItem(40308, 3000)){ // 3000アデナ
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < skills.length; i++) {
						l1skilluse.handleCommands(pc, skills[i], pc.getId(),
								pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_NPCBUFF);
					}
					htmlid = "bs_done";
				} else {
					htmlid = "bs_adena";
				}
			} else if (s.equals("0")) {
				htmlid = "bs_01";
			// スペルスクロール製作（個数選択）
			} else if (s.equals("1") || s.equals("2") || s.equals("3")
					|| s.equals("4") || s.equals("5")) {
				int[] cost = { 50, 100, 100, 200, 200 };
				int amount = 0;
				if (s.equals("1")) {
					amount = 1;
				} else if (s.equals("2")) {
					amount = 5;
				} else if (s.equals("3")) {
					amount = 10;
				} else if (s.equals("4")) {
					amount = 100;
				} else if (s.equals("5")) {
					amount = 500;
				}
				if (amount > 0) {
					htmlid = "bs_m4";
					htmldata = new String[] {
						String.valueOf(cost[0] * amount), String.valueOf(cost[1] * amount),
						String.valueOf(cost[2] * amount), String.valueOf(cost[3] * amount),
						String.valueOf(cost[4] * amount), String.valueOf(amount)
					};
				}
				pc.setCreateScrollAmount(amount);
			// スペルスクロール製作（ライト）
			} else if (s.equals("A")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40860, 50, 40090, 0);
			// スペルスクロール製作（シールド）
			} else if (s.equals("B")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40861, 50, 40090, 0);
			// スペルスクロール製作（エネルギー ボルト）
			} else if (s.equals("C")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40862, 50, 40090, 0);
			// スペルスクロール製作（ホーリー ウェポン）
			} else if (s.equals("D")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40866, 50, 40090, 0);
			// スペルスクロール製作（ヒール）
			} else if (s.equals("E")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40859, 50, 40090, 0);
			// スペルスクロール製作（ディクリース ウェイト）
			} else if (s.equals("F")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40872, 100, 40091, 0);
			// スペルスクロール製作（ディテクション）
			} else if (s.equals("G")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40871, 100, 40091, 0);
			// スペルスクロール製作（エンチャント ウェポン）
			} else if (s.equals("H")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40870, 100, 40091, 0);
			// スペルスクロール製作（キュア ポイズン）
			} else if (s.equals("I")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40867, 100, 40091, 0);
			// スペルスクロール製作（ファイアー アロー）
			} else if (s.equals("J")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40873, 100, 40091, 0);
			// スペルスクロール製作（ライトニング）
			} else if (s.equals("K")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40875, 100, 40092, 0);
			// スペルスクロール製作（ブレスド アーマー）
			} else if (s.equals("L")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40879, 100, 40092, 0);
			// スペルスクロール製作（エキストラ ヒール）
			} else if (s.equals("M")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40877, 100, 40092, 0);
			// スペルスクロール製作（フローズン クラウド）
			} else if (s.equals("N")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40880, 100, 40092, 0);
			// スペルスクロール製作（ターン アンデッド）
			} else if (s.equals("O")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40876, 100, 40092, 0);
			// スペルスクロール製作（メディテーション）
			} else if (s.equals("P")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40890, 200, 40093, 0);
			// スペルスクロール製作（ファイアー ボール）
			} else if (s.equals("Q")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40883, 200, 40093, 0);
			// スペルスクロール製作（フィジカル エンチャント：DEX）
			} else if (s.equals("R")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40884, 200, 40093, 0);
			// スペルスクロール製作（カウンター マジック）
			} else if (s.equals("S")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40889, 200, 40093, 40318);
			// スペルスクロール製作（スロー）
			} else if (s.equals("T")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40887, 200, 40093, 40318);
			// スペルスクロール製作（グレーター ヒール）
			} else if (s.equals("U")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40893, 200, 40094, 0);
			// スペルスクロール製作（リムーブ カーズ）
			} else if (s.equals("V")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40894, 200, 40094, 0);
			// スペルスクロール製作（マナ ドレイン）
			} else if (s.equals("W")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40897, 200, 40094, 0);
			// スペルスクロール製作（コーン オブ コールド）
			} else if (s.equals("X")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40896, 200, 40094, 0);
			// スペルスクロール製作（コール ライトニング）
			} else if (s.equals("Y")) {
				htmlid = getSpellScroll(pc, (L1NpcInstance) obj, 40892, 200, 40094, 0);
			}
		// ドラゴンポータル(隠された竜の地)
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 91066) {
			int level = pc.getLevel();
			// ドワーフ村(東/西/南/北)　本来は分けるべきだが正確な位置が不明
			if (s.equalsIgnoreCase("a") || s.equalsIgnoreCase("b")
					|| s.equalsIgnoreCase("c") || s.equalsIgnoreCase("d")) {
				L1Teleport.teleport(pc, 32820, 32904, (short) 1002, 5, true);
				htmlid = "";
			// 調和の大地(地)
			} else if (s.equalsIgnoreCase("1")) {
				L1Teleport.teleport(pc, 32993, 32716, (short) 1002, 4, true);
				htmlid = "";
			// 調和の大地(水)
			} else if (s.equalsIgnoreCase("2")) {
				L1Teleport.teleport(pc, 32874, 32785, (short) 1002, 5, true);
				htmlid = "";
			// 調和の大地(火)
			} else if (s.equalsIgnoreCase("3")) {
				L1Teleport.teleport(pc, 32793, 32593, (short) 1002, 5, true);
				htmlid = "";
			// 調和の大地(風)
			} else if (s.equalsIgnoreCase("4")) {
				L1Teleport.teleport(pc, 32904, 32627, (short) 1002, 5, true);
				htmlid = "";
			// 竜の墓(北)
			} else if (s.equalsIgnoreCase("5")) {
				L1Teleport.teleport(pc, 32698, 32664, (short) 1002, 6, true);
				htmlid = "";
			// 竜の墓(南)
			} else if (s.equalsIgnoreCase("6")) {
				L1Teleport.teleport(pc, 32710, 32759, (short) 1002, 6, true);
				htmlid = "";
			// 蒼空の渓谷
			} else if (s.equalsIgnoreCase("7")) {
				L1Teleport.teleport(pc, 32986, 32630, (short) 1002, 4, true);
				htmlid = "";
			} else {
				htmlid = "dsecret3";
			}
		// ネットカフェマップ
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 51000) {
			// 正確なテレポート位置が不明
			if (s.equalsIgnoreCase("a")) {
				L1Teleport.teleport(pc, 32549, 32532, (short) 7002, 5, true);
			} else if (s.equalsIgnoreCase("b")) {
				L1Teleport.teleport(pc, 32549, 32532, (short) 7003, 5, true);
			} else if (s.equalsIgnoreCase("c")) {
				L1Teleport.teleport(pc, 32549, 32532, (short) 7004, 5, true);
			} else if (s.equalsIgnoreCase("d")) {
				L1Teleport.teleport(pc, 32549, 32532, (short) 7005, 5, true);
			} else if (s.equalsIgnoreCase("e")) {
				L1Teleport.teleport(pc, 32549, 32532, (short) 7006, 5, true);
			} else if (s.equalsIgnoreCase("f")) {
				L1Teleport.teleport(pc, 32732, 32928, (short) 7007, 5, true);
			} else if (s.equalsIgnoreCase("g")) {
				L1Teleport.teleport(pc, 32732, 32928, (short) 7008, 5, true);
			} else if (s.equalsIgnoreCase("h")) {
				L1Teleport.teleport(pc, 32732, 32928, (short) 7009, 5, true);
			} else if (s.equalsIgnoreCase("i")) {
				L1Teleport.teleport(pc, 32732, 32928, (short) 7010, 5, true);
			} else if (s.equalsIgnoreCase("j")) {
				L1Teleport.teleport(pc, 32732, 32928, (short) 7011, 5, true);
			}
		// アイテム制作師 シュエルメ
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 91061) {
			if (s.equalsIgnoreCase("e")) { // 誕生の魔眼を作る
				if (pc.getInventory().checkItem(40308, 200000)
						&& pc.getInventory().checkItem(50508, 1)
						&& pc.getInventory().checkItem(50509, 1)) {
					if (_random.nextInt(99) < Config.CREATE_CHANCE_MAGIC_EYE_OF_BIRTH) {
						materials = new int[] { 40308, 50508, 50509 };
						counts = new int[] { 200000, 1, 1 };
						createitem = new int[] { 50512 };
						createcount = new int[] { 1 };
						htmlid = "";
					} else {
						pc.getInventory().consumeItem(40308, 200000);
						pc.getInventory().consumeItem(50508, 1);
						pc.getInventory().consumeItem(50509, 1);
						htmlid = "sherme5";
					}
				} else {
					htmlid = "sherme6";
				}
			} else if (s.equalsIgnoreCase("f")) { // 形象の魔眼を作る
				if (pc.getInventory().checkItem(40308, 200000)
						&& pc.getInventory().checkItem(50510, 1)
						&& pc.getInventory().checkItem(50512, 1)) {
					if (_random.nextInt(99) < Config.CREATE_CHANCE_MAGIC_EYE_OF_SHAPE) {
						materials = new int[] { 40308, 50510, 50512 };
						counts = new int[] { 200000, 1, 1 };
						createitem = new int[] { 50513 };
						createcount = new int[] { 1 };
						htmlid = "";
					} else {
						pc.getInventory().consumeItem(40308, 200000);
						pc.getInventory().consumeItem(50510, 1);
						pc.getInventory().consumeItem(50512, 1);
						htmlid = "sherme5";
					}
				} else {
					htmlid = "sherme6";
				}
			} else if (s.equalsIgnoreCase("g")) { // 生命の魔眼を作る
				if (pc.getInventory().checkItem(40308, 200000)
						&& pc.getInventory().checkItem(50511, 1)
						&& pc.getInventory().checkItem(50513, 1)) {
					if (_random.nextInt(99) < Config.CREATE_CHANCE_MAGIC_EYE_OF_LIFE) {
						materials = new int[] { 40308, 50511, 50513 };
						counts = new int[] { 200000, 1, 1 };
						createitem = new int[] { 50514 };
						createcount = new int[] { 1 };
						htmlid = "";
					} else {
						pc.getInventory().consumeItem(40308, 200000);
						pc.getInventory().consumeItem(50511, 1);
						pc.getInventory().consumeItem(50513, 1);
						htmlid = "sherme5";
					}
				} else {
					htmlid = "sherme6";
				}
			}
		// クレイ
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 91062) {
			if (s.equalsIgnoreCase("a")) {
				L1BuffUtil.effectBlessOfDragonSlayer(pc, BLESS_OF_CRAY,
						2400, 7681); // クレイの祝福
				htmlid = "grayknight2";
			}
		// サエル
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 91500) {
			if (s.equalsIgnoreCase("a")) {
				L1BuffUtil.effectBlessOfDragonSlayer(pc, BLESS_OF_SAEL,
						2400, 7680); // サエルの祝福
				htmlid = "";
			}
		// グンター
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 91600) {
			if (s.equalsIgnoreCase("a")) {
				L1BuffUtil.effectBlessOfDragonSlayer(pc, BLESS_OF_GUNTER,
						2400, 7683); // グンターの助言
				htmlid = "gunterdg2";
			}
		// ヴァラカス(未実装) gfx 7682
		// コマ
		} else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 80194) {
			if (s.equalsIgnoreCase("1")) { // 欠片を3つ持ってきました
				counts = new int[5];
				int checkcount = 0;
				int consumecount = 0;
				for (int i = 0; i < 5; i++) {
					if (pc.getInventory().checkItem(50515 + i, 1)) {
						counts[i] = 1;
						checkcount++;
					} else {
						counts[i] = 0;
					}
				}
				if (checkcount > 2) {
					L1BuffUtil.effectBlessOfComa(pc, BLESS_OF_COMA1, 3600, 7382);
					// コマの祝福A
					for (int i=0; i < 5; i++) {
						if (counts[i] == 1) {
							pc.getInventory().consumeItem(50515 + i, counts[i]);
							consumecount++;
						}
						if (consumecount > 2) {
							break;
						}
					}
					htmlid = "";
				} else {
					htmlid = "coma3";
				}
			} else if (s.equalsIgnoreCase("2")) { // 欠片を5つ持ってきました
				counts = new int[5];
				int checkcount = 0;
				for (int i = 0; i < 5; i++) {
					if (pc.getInventory().checkItem(50515 + i, 1)) {
						counts[i] = 1;
						checkcount++;
					}
				}
				if (checkcount > 4) {
					L1BuffUtil.effectBlessOfComa(pc, BLESS_OF_COMA2, 7200, 7383);
					// コマの祝福B
					for (int i = 0; i < 5; i++) {
						pc.getInventory().consumeItem(50515 + i, 1);
					}
					htmlid = "";
				} else {
					htmlid = "coma3";
				}
			} else if (s.equalsIgnoreCase("3")) { // 最初からもう一度選びます
				pc.resetComaMaterialAmount();
				htmlid = "coma4";
			} else if (s.equalsIgnoreCase("4")) { // すべて選びました
				int count = pc.getTotalComaMaterialAmount();
				if (count < 3) {
					htmlid = "coma3_2";
				} if (count == 3 || count == 5) {
					int amount = 0;
					boolean isError = false;
					for (int i = 0; i < 5; i++) {
						amount = pc.getComaMaterialAmount(i);
						if (amount > 0 && !pc.getInventory().checkItem(50515 + i, amount)) {
							isError = true;
							break;
						}
					}
					if (isError) {
						htmlid = "coma3_3";
					} else {
						if (count == 3) { // コマの祝福A
							L1BuffUtil.effectBlessOfComa(pc, BLESS_OF_COMA1, 3600, 7382);
						} else if (count == 5) { // コマの祝福B
							L1BuffUtil.effectBlessOfComa(pc, BLESS_OF_COMA2, 7200, 7383);
						}
						for (int i = 0; i < 5; i++) {
							amount = pc.getComaMaterialAmount(i);
							if (amount > 0 && pc.getInventory().checkItem(50515 + i, amount)) {
								pc.getInventory().consumeItem(50515 + i, amount);
							}
						}
						pc.resetComaMaterialAmount();
						htmlid = "";
					}
				} else if (count > 5) {
					htmlid = "coma3_1";
				} else {
					htmlid = "coma3_3";
				}
			} else if (s.equalsIgnoreCase("a")) { // デスマッチ 1個
				if (pc.getInventory().checkItem(50515, 1)) {
					pc.setComaMaterialAmount(0, 1);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("b")) { // デスマッチ 2個
				if (pc.getInventory().checkItem(50515, 2)) {
					pc.setComaMaterialAmount(0, 2);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("c")) { // デスマッチ 3個
				if (pc.getInventory().checkItem(50515, 3)) {
					pc.setComaMaterialAmount(0, 3);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("d")) { // デスマッチ 4個
				if (pc.getInventory().checkItem(50515, 4)) {
					pc.setComaMaterialAmount(0, 4);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("e")) { // デスマッチ 5個
				if (pc.getInventory().checkItem(50515, 5)) {
					pc.setComaMaterialAmount(0, 5);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("f")) { // お化け屋敷 1個
				if (pc.getInventory().checkItem(50516, 1)) {
					pc.setComaMaterialAmount(1, 1);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("g")) { // お化け屋敷 2個
				if (pc.getInventory().checkItem(50516, 2)) {
					pc.setComaMaterialAmount(1, 2);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("h")) { // お化け屋敷 3個
				if (pc.getInventory().checkItem(50516, 3)) {
					pc.setComaMaterialAmount(1, 3);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("i")) { // お化け屋敷 4個
				if (pc.getInventory().checkItem(50516, 4)) {
					pc.setComaMaterialAmount(1, 4);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("j")) { // お化け屋敷 5個
				if (pc.getInventory().checkItem(50516, 5)) {
					pc.setComaMaterialAmount(1, 5);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("k")) { // ペットレース 1個
				if (pc.getInventory().checkItem(50517, 1)) {
					pc.setComaMaterialAmount(2, 1);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("l")) { // ペットレース 2個
				if (pc.getInventory().checkItem(50517, 2)) {
					pc.setComaMaterialAmount(2, 2);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("m")) { // ペットレース 3個
				if (pc.getInventory().checkItem(50517, 3)) {
					pc.setComaMaterialAmount(2, 3);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("n")) { // ペットレース 4個
				if (pc.getInventory().checkItem(50517, 4)) {
					pc.setComaMaterialAmount(2, 4);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("o")) { // ペットレース 5個
				if (pc.getInventory().checkItem(50517, 5)) {
					pc.setComaMaterialAmount(2, 5);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("p")) { // ペットマッチ 1個
				if (pc.getInventory().checkItem(50518, 1)) {
					pc.setComaMaterialAmount(3, 1);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("q")) { // ペットマッチ 2個
				if (pc.getInventory().checkItem(50518, 2)) {
					pc.setComaMaterialAmount(3, 2);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("s")) { // ペットマッチ 3個
				if (pc.getInventory().checkItem(50518, 3)) {
					pc.setComaMaterialAmount(3, 3);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("t")) { // ペットマッチ 4個
				if (pc.getInventory().checkItem(50518, 4)) {
					pc.setComaMaterialAmount(3, 4);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("u")) { // ペットマッチ 5個
				if (pc.getInventory().checkItem(50518, 5)) {
					pc.setComaMaterialAmount(3, 5);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("v")) { // アルティメットバトル 1個
				if (pc.getInventory().checkItem(50519, 1)) {
					pc.setComaMaterialAmount(4, 1);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("w")) { // アルティメットバトル 2個
				if (pc.getInventory().checkItem(50519, 2)) {
					pc.setComaMaterialAmount(4, 2);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("x")) { // アルティメットバトル 3個
				if (pc.getInventory().checkItem(50519, 3)) {
					pc.setComaMaterialAmount(4, 3);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("y")) { // アルティメットバトル 4個
				if (pc.getInventory().checkItem(50519, 4)) {
					pc.setComaMaterialAmount(4, 4);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			} else if (s.equalsIgnoreCase("z")) { // アルティメットバトル 5個
				if (pc.getInventory().checkItem(50519, 5)) {
					pc.setComaMaterialAmount(4, 5);
				}
				htmlid = "coma5";
				htmldata = pc.getAllComaMaterialAmount();
			}
		}
/* // 武器交換NPCの前準備
		// ゾウのラヴァゴーレム
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 99267) {

			L1ItemInstance item = null;
			int createJoeItem = 0;
			int createJoeCount = 0;


			if (s.equalsIgnoreCase("A8")) { // +8マナバゼラート
				if ((pc.getInventory().checkEnchantItem(37,9,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,9,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,9,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,9,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,9,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,9,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,9,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,10000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 10000000 };

					pc.getInventory().getEnchantItem(pc, 259, 8, 1); // マナバゼラート
					if (pc.getInventory().consumeEnchantItem(37,9,1)
					||pc.getInventory().consumeEnchantItem(42,9,1)
					||pc.getInventory().consumeEnchantItem(41,9,1)
					||pc.getInventory().consumeEnchantItem(52,9,1)
					||pc.getInventory().consumeEnchantItem(180,9,1)
					||pc.getInventory().consumeEnchantItem(181,9,1)
					||pc.getInventory().consumeEnchantItem(131,9,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 10000000);
					htmlid = "joegolem9";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("A7")) { // +7マナバゼラート
				if ((pc.getInventory().checkEnchantItem(37,8,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,8,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,8,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,8,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,8,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,8,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,8,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,5000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 5000000 };

					pc.getInventory().getEnchantItem(pc, 259, 7, 1); // マナバゼラート
					if (pc.getInventory().consumeEnchantItem(37,8,1)
					||pc.getInventory().consumeEnchantItem(42,8,1)
					||pc.getInventory().consumeEnchantItem(41,8,1)
					||pc.getInventory().consumeEnchantItem(52,8,1)
					||pc.getInventory().consumeEnchantItem(180,8,1)
					||pc.getInventory().consumeEnchantItem(181,8,1)
					||pc.getInventory().consumeEnchantItem(131,8,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 5000000);
					htmlid = "joegolem9";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("B8")) { // +8レイジングウィンド
				if ((pc.getInventory().checkEnchantItem(37,9,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,9,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,9,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,9,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,9,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,9,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,9,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,10000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 10000000 };

					pc.getInventory().getEnchantItem(pc, 260, 8, 1); // レイジングウィンド
					if (pc.getInventory().consumeEnchantItem(37,9,1)
					||pc.getInventory().consumeEnchantItem(42,9,1)
					||pc.getInventory().consumeEnchantItem(41,9,1)
					||pc.getInventory().consumeEnchantItem(52,9,1)
					||pc.getInventory().consumeEnchantItem(180,9,1)
					||pc.getInventory().consumeEnchantItem(181,9,1)
					||pc.getInventory().consumeEnchantItem(131,9,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 10000000);
					htmlid = "joegolem10";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("B7")) { // +7レイジングウィンド
				if ((pc.getInventory().checkEnchantItem(37,8,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,8,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,8,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,8,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,8,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,8,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,8,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,5000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 5000000 };

					pc.getInventory().getEnchantItem(pc, 260, 7, 1); // レイジングウィンド
					if (pc.getInventory().consumeEnchantItem(37,8,1)
					||pc.getInventory().consumeEnchantItem(42,8,1)
					||pc.getInventory().consumeEnchantItem(41,8,1)
					||pc.getInventory().consumeEnchantItem(52,8,1)
					||pc.getInventory().consumeEnchantItem(180,8,1)
					||pc.getInventory().consumeEnchantItem(181,8,1)
					||pc.getInventory().consumeEnchantItem(131,8,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 5000000);
					htmlid = "joegolem10";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("C8")) { // +8ディストラクション
				if ((pc.getInventory().checkEnchantItem(37,9,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,9,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,9,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,9,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,9,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,9,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,9,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,10000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 10000000 };

					pc.getInventory().getEnchantItem(pc, 262, 8, 1); // ディストラクション
					if (pc.getInventory().consumeEnchantItem(37,9,1)
					||pc.getInventory().consumeEnchantItem(42,9,1)
					||pc.getInventory().consumeEnchantItem(41,9,1)
					||pc.getInventory().consumeEnchantItem(52,9,1)
					||pc.getInventory().consumeEnchantItem(180,9,1)
					||pc.getInventory().consumeEnchantItem(181,9,1)
					||pc.getInventory().consumeEnchantItem(131,9,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 10000000);
					htmlid = "joegolem11";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("C7")) { // +7ディストラクション
				if ((pc.getInventory().checkEnchantItem(37,8,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,8,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,8,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,8,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,8,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,8,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,8,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,5000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 5000000 };

					pc.getInventory().getEnchantItem(pc, 262, 7, 1); // ディストラクション
					if (pc.getInventory().consumeEnchantItem(37,8,1)
					||pc.getInventory().consumeEnchantItem(42,8,1)
					||pc.getInventory().consumeEnchantItem(41,8,1)
					||pc.getInventory().consumeEnchantItem(52,8,1)
					||pc.getInventory().consumeEnchantItem(180,8,1)
					||pc.getInventory().consumeEnchantItem(181,8,1)
					||pc.getInventory().consumeEnchantItem(131,8,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 5000000);
					htmlid = "joegolem11";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("D8")) { // +8エンジェルスタッフ
				if ((pc.getInventory().checkEnchantItem(37,9,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,9,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,9,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,9,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,9,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,9,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,9,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,10000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 10000000 };

					pc.getInventory().getEnchantItem(pc, 261, 8, 1); // エンジェルスタッフ
					if (pc.getInventory().consumeEnchantItem(37,9,1)
					||pc.getInventory().consumeEnchantItem(42,9,1)
					||pc.getInventory().consumeEnchantItem(41,9,1)
					||pc.getInventory().consumeEnchantItem(52,9,1)
					||pc.getInventory().consumeEnchantItem(180,9,1)
					||pc.getInventory().consumeEnchantItem(181,9,1)
					||pc.getInventory().consumeEnchantItem(131,9,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 10000000);
					htmlid = "joegolem12";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("D7")) { // +7エンジェルスタッフ
				if ((pc.getInventory().checkEnchantItem(37,8,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,8,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,8,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,8,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,8,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,8,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,8,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,5000000))	{ // アデナ
					if (pc.getInventory().consumeEnchantItem(37,9,1)
					||pc.getInventory().consumeEnchantItem(42,9,1)
					||pc.getInventory().consumeEnchantItem(41,9,1)
					||pc.getInventory().consumeEnchantItem(52,9,1)
					||pc.getInventory().consumeEnchantItem(180,9,1)
					||pc.getInventory().consumeEnchantItem(181,9,1)
					||pc.getInventory().consumeEnchantItem(131,9,1)) {
						;
					}
					counts = new int[] { 1 , 5000000 };

					pc.getInventory().getEnchantItem(pc, 261, 7, 1); // エンジェルスタッフ
					if (pc.getInventory().consumeEnchantItem(37,8,1)
					||pc.getInventory().consumeEnchantItem(42,8,1)
					||pc.getInventory().consumeEnchantItem(41,8,1)
					||pc.getInventory().consumeEnchantItem(52,8,1)
					||pc.getInventory().consumeEnchantItem(180,8,1)
					||pc.getInventory().consumeEnchantItem(181,8,1)
					||pc.getInventory().consumeEnchantItem(131,8,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 5000000);
					htmlid = "joegolem12";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("E8")) { // +8フリージングランサー
				if ((pc.getInventory().checkEnchantItem(37,9,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,9,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,9,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,9,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,9,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,9,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,9,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,10000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 10000000 };

					pc.getInventory().getEnchantItem(pc, 263, 8, 1); // フリージングランサー
					if (pc.getInventory().consumeEnchantItem(37,9,1)
					||pc.getInventory().consumeEnchantItem(42,9,1)
					||pc.getInventory().consumeEnchantItem(41,9,1)
					||pc.getInventory().consumeEnchantItem(52,9,1)
					||pc.getInventory().consumeEnchantItem(180,9,1)
					||pc.getInventory().consumeEnchantItem(181,9,1)
					||pc.getInventory().consumeEnchantItem(131,9,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 10000000);
					htmlid = "joegolem13";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("E7")) { // +7フリージングランサー
				if ((pc.getInventory().checkEnchantItem(37,8,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,8,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,8,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,8,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,8,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,8,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,8,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,5000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 5000000 };

					pc.getInventory().getEnchantItem(pc, 263, 7, 1); // フリージングランサー
					if (pc.getInventory().consumeEnchantItem(37,8,1)
					||pc.getInventory().consumeEnchantItem(42,8,1)
					||pc.getInventory().consumeEnchantItem(41,8,1)
					||pc.getInventory().consumeEnchantItem(52,8,1)
					||pc.getInventory().consumeEnchantItem(180,8,1)
					||pc.getInventory().consumeEnchantItem(181,8,1)
					||pc.getInventory().consumeEnchantItem(131,8,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 5000000);
					htmlid = "joegolem13";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("F8")) { // +8ライトニングエッジ
				if ((pc.getInventory().checkEnchantItem(37,9,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,9,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,9,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,9,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,9,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,9,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,9,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,10000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 10000000 };

					pc.getInventory().getEnchantItem(pc, 264, 8, 1); // ライトニングエッジ
					if (pc.getInventory().consumeEnchantItem(37,9,1)
					||pc.getInventory().consumeEnchantItem(42,9,1)
					||pc.getInventory().consumeEnchantItem(41,9,1)
					||pc.getInventory().consumeEnchantItem(52,9,1)
					||pc.getInventory().consumeEnchantItem(180,9,1)
					||pc.getInventory().consumeEnchantItem(181,9,1)
					||pc.getInventory().consumeEnchantItem(131,9,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 10000000);
					htmlid = "joegolem14";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("F7")) { // +7ライトニングエッジ
				if ((pc.getInventory().checkEnchantItem(37,8,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,8,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,8,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,8,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,8,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,8,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,8,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,5000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 5000000 };

					pc.getInventory().getEnchantItem(pc, 264, 7, 1); // ライトニングエッジ
					if (pc.getInventory().consumeEnchantItem(37,8,1)
					||pc.getInventory().consumeEnchantItem(42,8,1)
					||pc.getInventory().consumeEnchantItem(41,8,1)
					||pc.getInventory().consumeEnchantItem(52,8,1)
					||pc.getInventory().consumeEnchantItem(180,8,1)
					||pc.getInventory().consumeEnchantItem(181,8,1)
					||pc.getInventory().consumeEnchantItem(131,8,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 5000000);
					htmlid = "joegolem14";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("G8")) { // +8エンジェルスレイヤー
				if ((pc.getInventory().checkEnchantItem(37,9,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,9,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,9,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,9,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,9,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,9,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,9,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,10000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 10000000 };

					pc.getInventory().getEnchantItem(pc, 704, 8, 1); // エンジェルスレイヤー
					if (pc.getInventory().consumeEnchantItem(37,9,1)
					||pc.getInventory().consumeEnchantItem(42,9,1)
					||pc.getInventory().consumeEnchantItem(41,9,1)
					||pc.getInventory().consumeEnchantItem(52,9,1)
					||pc.getInventory().consumeEnchantItem(180,9,1)
					||pc.getInventory().consumeEnchantItem(181,9,1)
					||pc.getInventory().consumeEnchantItem(131,9,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 10000000);
					htmlid = "joegolem31";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
			if (s.equalsIgnoreCase("G7")) { // +7エンジェルスレイヤー
				if ((pc.getInventory().checkEnchantItem(37,8,1) // ダマスカス ソード
						|| pc.getInventory().checkEnchantItem(42,8,1) // レイピア
						|| pc.getInventory().checkEnchantItem(41,8,1) // 刀
						|| pc.getInventory().checkEnchantItem(52,8,1) // ツーハンド ソード
						|| pc.getInventory().checkEnchantItem(180,8,1) // クロス ボウ
						|| pc.getInventory().checkEnchantItem(181,8,1) // ロング ボウ
						|| pc.getInventory().checkEnchantItem(131,8,1)) //フォース スタッフ
						&& pc.getInventory().checkItem(40308,5000000))	{ // アデナ
					materials = new int[] { 37|42|41|52|180|181|131 , 40308 };
					counts = new int[] { 1 , 5000000 };

					pc.getInventory().getEnchantItem(pc, 704, 7, 1); // エンジェルスレイヤー
					if (pc.getInventory().consumeEnchantItem(37,8,1)
					||pc.getInventory().consumeEnchantItem(42,8,1)
					||pc.getInventory().consumeEnchantItem(41,8,1)
					||pc.getInventory().consumeEnchantItem(52,8,1)
					||pc.getInventory().consumeEnchantItem(180,8,1)
					||pc.getInventory().consumeEnchantItem(181,8,1)
					||pc.getInventory().consumeEnchantItem(131,8,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 5000000);
					htmlid = "joegolem31";
					}
				return;
			} else {
				htmlid = "joegolem15";
			}
		}

		// ピアス
		else if (((L1NpcInstance) obj).getNpcTemplate().getNpcId() == 70908) {

			if (s.equalsIgnoreCase("A8")) { // +8破壊のデュアルブレード
				if ((pc.getInventory().checkEnchantItem(81,9,1) // ブラインド デュアルブレード
						|| pc.getInventory().checkEnchantItem(162,9,1) // ブラインド クロウ
						|| pc.getInventory().checkEnchantItem(177,9,1) // ブラインド クロスボウ
						|| pc.getInventory().checkEnchantItem(194,9,1) // 真ガントレット
						|| pc.getInventory().checkEnchantItem(13,9,1)) // フィンガー オブ デス
						&& pc.getInventory().checkItem(40308,10000000))	{ // アデナ
					materials = new int[] { 81|162|177|194|13 , 40308 };
					counts = new int[] { 1 , 10000000 };

					pc.getInventory().getEnchantItem(pc, 705, 8, 1); // 破壊のデュアルブレード
					if (pc.getInventory().consumeEnchantItem(81,9,1)
					||pc.getInventory().consumeEnchantItem(162,9,1)
					||pc.getInventory().consumeEnchantItem(177,9,1)
					||pc.getInventory().consumeEnchantItem(194,9,1)
					||pc.getInventory().consumeEnchantItem(13,9,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 10000000);
					}
				htmlid = "pears7";
			} else {
				htmlid = "pears9";
			}
			if (s.equalsIgnoreCase("A7")) { // +7破壊のデュアルブレード
				if ((pc.getInventory().checkEnchantItem(81,8,1) // ブラインド デュアルブレード
						|| pc.getInventory().checkEnchantItem(162,8,1) // ブラインド クロウ
						|| pc.getInventory().checkEnchantItem(177,8,1) // ブラインド クロスボウ
						|| pc.getInventory().checkEnchantItem(194,8,1) // 真ガントレット
						|| pc.getInventory().checkEnchantItem(13,8,1)) // フィンガー オブ デス
						&& pc.getInventory().checkItem(40308,5000000))	{ // アデナ
					materials = new int[] { 81|162|177|194|13 , 40308 };
					counts = new int[] { 1 , 5000000 };

					pc.getInventory().getEnchantItem(pc, 705, 7, 1); // 破壊のデュアルブレード
					if (pc.getInventory().consumeEnchantItem(81,8,1)
					||pc.getInventory().consumeEnchantItem(162,8,1)
					||pc.getInventory().consumeEnchantItem(177,8,1)
					||pc.getInventory().consumeEnchantItem(194,8,1)
					||pc.getInventory().consumeEnchantItem(13,8,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 5000000);
					}
				htmlid = "pears7";
			} else {
				htmlid = "pears9";
			}
			if (s.equalsIgnoreCase("B8")) { // +8破壊のクロウ
				if ((pc.getInventory().checkEnchantItem(81,9,1) // ブラインド デュアルブレード
						|| pc.getInventory().checkEnchantItem(162,9,1) // ブラインド クロウ
						|| pc.getInventory().checkEnchantItem(177,9,1) // ブラインド クロスボウ
						|| pc.getInventory().checkEnchantItem(194,9,1) // 真ガントレット
						|| pc.getInventory().checkEnchantItem(13,9,1)) // フィンガー オブ デス
						&& pc.getInventory().checkItem(40308,10000000))	{ // アデナ
					materials = new int[] { 81|162|177|194|13 , 40308 };
					counts = new int[] { 1 , 10000000 };

					pc.getInventory().getEnchantItem(pc, 706, 8, 1); // 破壊のクロウ
					if (pc.getInventory().consumeEnchantItem(81,9,1)
					||pc.getInventory().consumeEnchantItem(162,9,1)
					||pc.getInventory().consumeEnchantItem(177,9,1)
					||pc.getInventory().consumeEnchantItem(194,9,1)
					||pc.getInventory().consumeEnchantItem(13,9,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 10000000);
					}
				htmlid = "pears7";
			} else {
				htmlid = "pears9";
			}
			if (s.equalsIgnoreCase("B7")) { // +7破壊のクロウ
				if ((pc.getInventory().checkEnchantItem(81,8,1) // ブラインド デュアルブレード
						|| pc.getInventory().checkEnchantItem(162,8,1) // ブラインド クロウ
						|| pc.getInventory().checkEnchantItem(177,8,1) // ブラインド クロスボウ
						|| pc.getInventory().checkEnchantItem(194,8,1) // 真ガントレット
						|| pc.getInventory().checkEnchantItem(13,8,1)) // フィンガー オブ デス
						&& pc.getInventory().checkItem(40308,5000000))	{ // アデナ
					materials = new int[] { 81|162|177|194|13 , 40308 };
					counts = new int[] { 1 , 5000000 };

					pc.getInventory().getEnchantItem(pc, 706, 7, 1); // 破壊のクロウ
					if (pc.getInventory().consumeEnchantItem(81,8,1)
					||pc.getInventory().consumeEnchantItem(162,8,1)
					||pc.getInventory().consumeEnchantItem(177,8,1)
					||pc.getInventory().consumeEnchantItem(194,8,1)
					||pc.getInventory().consumeEnchantItem(13,8,1)) {
						;
					}
					pc.getInventory().consumeItem(40308, 5000000);
					}
				htmlid = "pears7";
			} else {
				htmlid = "pears9";
			}
		}
*/
		// else System.out.println("C_NpcAction: " + s);
		if (htmlid != null && htmlid.equalsIgnoreCase("colos2")) {
			htmldata = makeUbInfoStrings(((L1NpcInstance) obj).getNpcTemplate()
					.getNpcId());
		}
		if (createitem != null) { // アイテム精製
			boolean isCreate = true;
			if (materials != null) {
				for (int j = 0; j < materials.length; j++) {
					if (!pc.getInventory().checkItemNotEquipped(materials[j],
							counts[j])) {
						L1Item temp = ItemTable.getInstance().getTemplate(
								materials[j]);
						pc.sendPackets(new S_ServerMessage(337, temp.getName()));
						// \f1%0が不足しています。
						isCreate = false;
					}
				}
			}

			if (isCreate) {
				// 容量と重量の計算
				int create_count = 0; // アイテムの個数（纏まる物は1個）
				int create_weight = 0;
				for (int k = 0; k < createitem.length; k++) {
					L1Item temp = ItemTable.getInstance().getTemplate(
							createitem[k]);
					if (temp.isStackable()) {
						if (!pc.getInventory().checkItem(createitem[k])) {
							create_count += 1;
						}
					} else {
						create_count += createcount[k];
					}
					create_weight += temp.getWeight() * createcount[k] / 1000;
				}
				// 容量確認
				if (pc.getInventory().getSize() + create_count > 180) {
					pc.sendPackets(new S_ServerMessage(263));
					// \f1一人のキャラクターが持って歩けるアイテムは最大180個までです。
					return;
				}
				// 重量確認
				if (pc.getMaxWeight() < pc.getInventory().getWeight()
						+ create_weight) {
					pc.sendPackets(new S_ServerMessage(82));
					// アイテムが重すぎて、これ以上持てません。
					return;
				}

				if (materials != null) {
					for (int j = 0; j < materials.length; j++) {
						// 材料消費
						pc.getInventory().consumeItem(materials[j], counts[j]);
					}
				}
				for (int k = 0; k < createitem.length; k++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							createitem[k], createcount[k]);
					if (item != null) {
						String itemName = ItemTable.getInstance().getTemplate(
								createitem[k]).getName();
						String createrName = "";
						if (obj instanceof L1NpcInstance) {
							createrName = ((L1NpcInstance) obj)
									.getNpcTemplate().getName();
						}
						if (createcount[k] > 1) {
							pc.sendPackets(new S_ServerMessage(143,
									createrName, itemName + " ("
											+ createcount[k] + ")"));
							// \f1%0が%1をくれました 。
						} else {
							pc.sendPackets(new S_ServerMessage(143,
									createrName, itemName)); // \f1%0が%1をくれました。
						}
					}
				}
				if (success_htmlid != null) { // html指定がある場合は表示
					pc.sendPackets(new S_NpcTalkReturn(objid, success_htmlid,
							htmldata));
				}
			} else { // 精製失敗
				if (failure_htmlid != null) { // html指定がある場合は表示
					pc.sendPackets(new S_NpcTalkReturn(objid, failure_htmlid,
							htmldata));
				}
			}
		}

		if (htmlid != null) { // html指定がある場合は表示
			pc.sendPackets(new S_NpcTalkReturn(objid, htmlid, htmldata));
		}
	}

	private String karmaLevelToHtmlId(int level) {
		if (level == 0 || level < -7 || 7 < level) {
			return "";
		}
		String htmlid = "";
		if (0 < level) {
			htmlid = "vbk" + level;
		} else if (level < 0) {
			htmlid = "vyk" + Math.abs(level);
		}
		return htmlid;
	}

	private String watchUb(L1PcInstance pc, int npcId) {
		L1UltimateBattle ub = UbTable.getInstance().getUbForNpcId(npcId);
		L1Location loc = ub.getLocation();
		if (pc.getInventory().consumeItem(L1ItemId.ADENA, 100)) {
			try {
				pc.save();
				pc.beginGhost(loc.getX(), loc.getY(), (short) loc.getMapId(),
						true);
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		} else {
			pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足しています。
		}
		return "";
	}

	private String enterUb(L1PcInstance pc, int npcId) {
		L1UltimateBattle ub = UbTable.getInstance().getUbForNpcId(npcId);
		if (!ub.isActive() || !ub.canPcEnter(pc)) { // 時間外
			return "colos2";
		}
		if (ub.isNowUb()) { // 競技中
			return "colos1";
		}
		if (ub.getMembersCount() >= ub.getMaxPlayer()) { // 定員オーバー
			return "colos4";
		}

		ub.addMember(pc); // メンバーに追加
		L1Location loc = ub.getLocation().randomLocation(10, false);
		L1Teleport.teleport(pc, loc.getX(), loc.getY(), ub.getMapId(), 5, true);
		return "";
	}

	private String enterHauntedHouse(L1PcInstance pc) {
		if (L1HauntedHouse.getInstance().getHauntedHouseStatus() == L1HauntedHouse.STATUS_PLAYING) { // 競技中
			pc.sendPackets(new S_ServerMessage(1182)); // もうゲームは始まってるよ。
			return "";
		}
		if (L1HauntedHouse.getInstance().getMembersCount() >= 10) { // 定員オーバー
			pc.sendPackets(new S_ServerMessage(1184)); // お化け屋敷は人でいっぱいだよ。
			return "";
		}

		L1HauntedHouse.getInstance().addMember(pc); // メンバーに追加
		L1Teleport.teleport(pc, 32722, 32830, (short) 5140, 2, true);
		return "";
	}

	private String enterPetMatch(L1PcInstance pc, int objid2) {
		Object[] petlist = pc.getPetList().values().toArray();
		if (petlist.length > 0) {
			pc.sendPackets(new S_ServerMessage(1187)); // ペットのアミュレットが使用中です。
			return "";
		}
		if (!L1PetMatch.getInstance().enterPetMatch(pc, objid2)) {
			pc.sendPackets(new S_ServerMessage(1182)); // もうゲームは始まってるよ。
		}
		return "";
	}

	private void summonMonster(L1PcInstance pc, String s) {
		String[] summonstr_list;
		int[] summonid_list;
		int[] summonlvl_list;
		int[] summoncha_list;
		int summonid = 0;
		int levelrange = 0;
		int summoncost = 0;
		/*
		 * summonstr_list = new String[] { "7", "263", "8", "264", "9", "265",
		 * "10", "266", "11", "267", "12", "268", "13", "269", "14", "270",
		 * "526", "15", "271", "527", "17", "18" }; summonid_list = new int[] {
		 * 81083, 81090, 81084, 81091, 81085, 81092, 81086, 81093, 81087, 81094,
		 * 81088, 81095, 81089, 81096, 81097, 81098, 81099, 81100, 81101, 81102,
		 * 81103, 81104 }; summonlvl_list = new int[] { 28, 28, 32, 32, 36, 36,
		 * 40, 40, 44, 44, 48, 48, 52, 52, 56, 56, 56, 60, 60, 60, 68, 72 }; //
		 * ドッペルゲンガーボス、クーガーにはペットボーナスが付かないので+6しておく summoncha_list = new int[] { 6,
		 * 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8, 8, 8, 8, 10, 10, 10, 36, 40 };
		 */
		summonstr_list = new String[] { "7", "263", "519", "8", "264", "520",
				"9", "265", "521", "10", "266", "522", "11", "267", "523",
				"12", "268", "524", "13", "269", "525", "14", "270", "526",
				"15", "271", "527", "16", "17", "18", "274" };
		summonid_list = new int[] { 81210, 81211, 81212, 81213, 81214, 81215,
				81216, 81217, 81218, 81219, 81220, 81221, 81222, 81223, 81224,
				81225, 81226, 81227, 81228, 81229, 81230, 81231, 81232, 81233,
				81234, 81235, 81236, 81237, 81238, 81239, 81240 };
		summonlvl_list = new int[] { 28, 28, 28, 32, 32, 32, 36, 36, 36, 40,
				40, 40, 44, 44, 44, 48, 48, 48, 52, 52, 52, 56, 56, 56, 60, 60,
				60, 64, 68, 72, 72 };
		// ドッペルゲンガーボス、クーガーにはペットボーナスが付かないので+6しておく
		// summoncha_list = new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
		// 8,
		// 8, 8, 8, 8, 8, 8, 8, 10, 10, 10, 12, 12, 12, 20, 42, 42, 50 };
		summoncha_list = new int[] { 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
				8, // 28 ~　44
				8, 8, 8, 8, 8, 8, 10, 10, 10, 12, 12, 12, // 48 ~ 60
				20, 36, 36, 44 }; // 64,68,72,72
		// サモンの種類、必要Lv、ペットコストを得る
		for (int loop = 0; loop < summonstr_list.length; loop++) {
			if (s.equalsIgnoreCase(summonstr_list[loop])) {
				summonid = summonid_list[loop];
				levelrange = summonlvl_list[loop];
				summoncost = summoncha_list[loop];
				break;
			}
		}
		// Lv不足
		if (pc.getLevel() < levelrange) {
			// レベルが低くて該当のモンスターを召還することができません。
			pc.sendPackets(new S_ServerMessage(743));
			return;
		}

		int petcost = 0;
		Object[] petlist = pc.getPetList().values().toArray();
		for (Object pet : petlist) {
			// 現在のペットコスト
			petcost += ((L1NpcInstance) pet).getPetcost();
		}

		/*
		 * // 既にペットがいる場合は、ドッペルゲンガーボス、クーガーは呼び出せない if ((summonid == 81103 ||
		 * summonid == 81104) && petcost != 0) { pc.sendPackets(new
		 * S_CloseList(pc.getId())); return; } int charisma = pc.getCha() + 6 -
		 * petcost; int summoncount = charisma / summoncost;
		 */
		int pcCha = pc.getCha();
		int charisma = 0;
		int summoncount = 0;
		if (levelrange <= 56 // max count = 5
				|| levelrange == 64) { // max count = 2
			if (pcCha > 34) {
				pcCha = 34;
			}
		} else if (levelrange == 60) {
			if (pcCha > 30) { // max count = 3
				pcCha = 30;
			}
		} else if (levelrange > 64) {
			if (pcCha > 44) { // max count = 1
				pcCha = 44;
			}
		}
		charisma = pcCha + 6 - petcost;
		summoncount = charisma / summoncost;

		L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonid);
		for (int cnt = 0; cnt < summoncount; cnt++) {
			L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
			// if (summonid == 81103 || summonid == 81104) {
			// summon.setPetcost(pc.getCha() + 7);
			// } else {
			summon.setPetcost(summoncost);
			// }
		}
		pc.sendPackets(new S_CloseList(pc.getId()));
	}

	private void poly(ClientThread clientthread, int polyId) {
		L1PcInstance pc = clientthread.getActiveChar();

		if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) { // check
			pc.getInventory().consumeItem(L1ItemId.ADENA, 100); // del

			L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_NPC);
		} else {
			pc.sendPackets(new S_ServerMessage(337, "$4")); // アデナが不足しています。
		}
	}

	private void polyByKeplisha(ClientThread clientthread, int polyId) {
		L1PcInstance pc = clientthread.getActiveChar();

		if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) { // check
			pc.getInventory().consumeItem(L1ItemId.ADENA, 100); // del

			L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_KEPLISHA);
		} else {
			pc.sendPackets(new S_ServerMessage(337, "$4")); // アデナが不足しています。
		}
	}

	private String sellHouse(L1PcInstance pc, int objectId, int npcId) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
		if (clan == null) {
			return ""; // ウィンドウを消す
		}
		int houseId = clan.getHouseId();
		if (houseId == 0) {
			return ""; // ウィンドウを消す
		}
		L1House house = HouseTable.getInstance().getHouseTable(houseId);
		int keeperId = house.getKeeperId();
		if (npcId != keeperId) {
			return ""; // ウィンドウを消す
		}
		if (!pc.isCrown()) {
			pc.sendPackets(new S_ServerMessage(518)); // この命令は血盟の君主のみが利用できます。
			return ""; // ウィンドウを消す
		}
		if (pc.getId() != clan.getLeaderId()) {
			pc.sendPackets(new S_ServerMessage(518)); // この命令は血盟の君主のみが利用できます。
			return ""; // ウィンドウを消す
		}
		if (house.isOnSale()) {
			return "agonsale";
		}

		pc.sendPackets(new S_SellHouse(objectId, String.valueOf(houseId)));
		return null;
	}

	private void openCloseDoor(L1PcInstance pc, L1NpcInstance npc, String s) {
		// int doorId = 0;
		L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
		if (clan != null) {
			int houseId = clan.getHouseId();
			if (houseId != 0) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				int keeperId = house.getKeeperId();
				if (npc.getNpcTemplate().getNpcId() == keeperId) {
					L1DoorInstance door1 = null;
					L1DoorInstance door2 = null;
					L1DoorInstance door3 = null;
					L1DoorInstance door4 = null;
					for (L1DoorInstance door : DoorTable.getInstance()
							.getDoorList()) {
						if (door.getKeeperId() == keeperId) {
							if (door1 == null) {
								door1 = door;
								continue;
							}
							if (door2 == null) {
								door2 = door;
								continue;
							}
							if (door3 == null) {
								door3 = door;
								continue;
							}
							if (door4 == null) {
								door4 = door;
								break;
							}
						}
					}
					if (door1 != null) {
						if (s.equalsIgnoreCase("open")) {
							door1.open();
						} else if (s.equalsIgnoreCase("close")) {
							door1.close();
						}
					}
					if (door2 != null) {
						if (s.equalsIgnoreCase("open")) {
							door2.open();
						} else if (s.equalsIgnoreCase("close")) {
							door2.close();
						}
					}
					if (door3 != null) {
						if (s.equalsIgnoreCase("open")) {
							door3.open();
						} else if (s.equalsIgnoreCase("close")) {
							door3.close();
						}
					}
					if (door4 != null) {
						if (s.equalsIgnoreCase("open")) {
							door4.open();
						} else if (s.equalsIgnoreCase("close")) {
							door4.close();
						}
					}
				}
			}
		}
	}

	private void openCloseGate(L1PcInstance pc, int keeperId, boolean isOpen) {
		boolean isNowWar = false;
		int pcCastleId = 0;
		if (pc.getClanId() != 0) {
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				pcCastleId = clan.getCastleId();
			}
		}
		if (keeperId == 70656 || keeperId == 70549 || keeperId == 70985) { // ケント城
			if (isExistDefenseClan(L1CastleLocation.KENT_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.KENT_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.KENT_CASTLE_ID);
		} else if (keeperId == 70600) { // OT
			if (isExistDefenseClan(L1CastleLocation.OT_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.OT_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.OT_CASTLE_ID);
		} else if (keeperId == 70778 || keeperId == 70987 || keeperId == 70687) { // WW城
			if (isExistDefenseClan(L1CastleLocation.WW_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.WW_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.WW_CASTLE_ID);
		} else if (keeperId == 70817 || keeperId == 70800 || keeperId == 70988
				|| keeperId == 70990 || keeperId == 70989 || keeperId == 70991) { // ギラン城
			if (isExistDefenseClan(L1CastleLocation.GIRAN_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.GIRAN_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.GIRAN_CASTLE_ID);
		} else if (keeperId == 70863 || keeperId == 70992 || keeperId == 70862) { // ハイネ城
			if (isExistDefenseClan(L1CastleLocation.HEINE_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.HEINE_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.HEINE_CASTLE_ID);
		} else if (keeperId == 70995 || keeperId == 70994 || keeperId == 70993) { // ドワーフ城
			if (isExistDefenseClan(L1CastleLocation.DOWA_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.DOWA_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.DOWA_CASTLE_ID);
		} else if (keeperId == 70996) { // アデン城
			if (isExistDefenseClan(L1CastleLocation.ADEN_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.ADEN_CASTLE_ID) {
					return;
				}
			}
			isNowWar = WarTimeController.getInstance().isNowWar(
					L1CastleLocation.ADEN_CASTLE_ID);
		}

		for (L1DoorInstance door : DoorTable.getInstance().getDoorList()) {
			if (door.getKeeperId() == keeperId) {
				if (door.isDead()) { // ゲートが破壊されていたら開閉不可
				} else {
					if (isOpen) { // 開
						door.open();
					} else { // 閉
						door.close();
					}
				}
			}
		}
	}

	private boolean isExistDefenseClan(int castleId) {
		boolean isExistDefenseClan = false;
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (castleId == clan.getCastleId()) {
				isExistDefenseClan = true;
				break;
			}
		}
		return isExistDefenseClan;
	}

	private void expelOtherClan(L1PcInstance clanPc, int keeperId) {
		int houseId = 0;
		for (L1House house : HouseTable.getInstance().getHouseTableList()) {
			if (house.getKeeperId() == keeperId) {
				houseId = house.getHouseId();
			}
		}
		if (houseId == 0) {
			return;
		}

		int[] loc = new int[3];
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) object;
				if (L1HouseLocation.isInHouseLoc(houseId, pc.getX(), pc.getY(),
						pc.getMapId())
						&& clanPc.getClanId() != pc.getClanId()) {
					loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
					if (pc != null) {
						L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2],
								5, true);
					}
				}
			}
		}
	}

	private void repairGate(L1PcInstance pc) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
		if (clan != null) {
			int castleId = clan.getCastleId();
			if (castleId != 0) { // 城主クラン
				if (!WarTimeController.getInstance().isNowWar(castleId)) {
					// 城門を元に戻す
					for (L1DoorInstance door : DoorTable.getInstance()
							.getDoorList()) {
						if (L1CastleLocation.checkInWarArea(castleId, door)) {
							door.repairGate();
						}
					}
					pc.sendPackets(new S_ServerMessage(990)); // 城門自動修理を命令しました。
				} else {
					pc.sendPackets(new S_ServerMessage(991)); // 城門自動修理命令を取り消しました。
				}
			}
		}
	}

	private String[] makeWarTimeStrings(int castleId) {
		L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);
		if (castle == null) {
			return null;
		}
		Calendar warTime = castle.getWarTime();
		int year = warTime.get(Calendar.YEAR);
		int month = warTime.get(Calendar.MONTH) + 1;
		int day = warTime.get(Calendar.DATE);
		int hour = warTime.get(Calendar.HOUR_OF_DAY);
		int minute = warTime.get(Calendar.MINUTE);
		String[] result;
		if (castleId == L1CastleLocation.OT_CASTLE_ID) {
			result = new String[] { String.valueOf(year),
					String.valueOf(month), String.valueOf(day),
					String.valueOf(hour), String.valueOf(minute) };
		} else {
			result = new String[] { "", String.valueOf(year),
					String.valueOf(month), String.valueOf(day),
					String.valueOf(hour), String.valueOf(minute) };
		}
		return result;
	}

	private String getYaheeAmulet(L1PcInstance pc, L1NpcInstance npc, String s) {
		int[] amuletIdList = { 20358, 20359, 20360, 20361, 20362, 20363, 20364,
				20365 };
		int amuletId = 0;
		L1ItemInstance item = null;
		String htmlid = null;
		if (s.equalsIgnoreCase("1") && pc.getKarmaLevel() == -1) {
			amuletId = amuletIdList[0];
		} else if (s.equalsIgnoreCase("2") && pc.getKarmaLevel() == -2) {
			amuletId = amuletIdList[1];
		} else if (s.equalsIgnoreCase("3") && pc.getKarmaLevel() == -3) {
			amuletId = amuletIdList[2];
		} else if (s.equalsIgnoreCase("4") && pc.getKarmaLevel() == -4) {
			amuletId = amuletIdList[3];
		} else if (s.equalsIgnoreCase("5") && pc.getKarmaLevel() == -5) {
			amuletId = amuletIdList[4];
		} else if (s.equalsIgnoreCase("6") && pc.getKarmaLevel() == -6) {
			amuletId = amuletIdList[5];
		} else if (s.equalsIgnoreCase("7") && pc.getKarmaLevel() == -7) {
			amuletId = amuletIdList[6];
		} else if (s.equalsIgnoreCase("8") && pc.getKarmaLevel() == -8) {
			amuletId = amuletIdList[7];
		}
		if (amuletId != 0) {
			item = pc.getInventory().storeItem(amuletId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.getName(), item.getLogName())); // \f1%0が%1をくれました。
			}
			for (int id : amuletIdList) {
				if (id == amuletId) {
					break;
				}
				if (pc.getInventory().checkItem(id)) {
					pc.getInventory().consumeItem(id, 1);
				}
				if (pc.getAdditionalWarehouseInventory().checkItem(id)) {
					pc.getAdditionalWarehouseInventory().consumeItem(id, 1);
				}
			}
			htmlid = "";
		}
		return htmlid;
	}

	private String getBarlogEarring(L1PcInstance pc, L1NpcInstance npc, String s) {
		int[] earringIdList = { 21020, 21021, 21022, 21023, 21024, 21025,
				21026, 21027 };
		int earringId = 0;
		L1ItemInstance item = null;
		String htmlid = null;
		if (s.equalsIgnoreCase("1") && pc.getKarmaLevel() == 1) {
			earringId = earringIdList[0];
		} else if (s.equalsIgnoreCase("2") && pc.getKarmaLevel() == 2) {
			earringId = earringIdList[1];
		} else if (s.equalsIgnoreCase("3") && pc.getKarmaLevel() == 3) {
			earringId = earringIdList[2];
		} else if (s.equalsIgnoreCase("4") && pc.getKarmaLevel() == 4) {
			earringId = earringIdList[3];
		} else if (s.equalsIgnoreCase("5") && pc.getKarmaLevel() == 5) {
			earringId = earringIdList[4];
		} else if (s.equalsIgnoreCase("6") && pc.getKarmaLevel() == 6) {
			earringId = earringIdList[5];
		} else if (s.equalsIgnoreCase("7") && pc.getKarmaLevel() == 7) {
			earringId = earringIdList[6];
		} else if (s.equalsIgnoreCase("8") && pc.getKarmaLevel() == 8) {
			earringId = earringIdList[7];
		}
		if (earringId != 0) {
			item = pc.getInventory().storeItem(earringId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.getName(), item.getLogName())); // \f1%0が%1をくれました。
			}
			for (int id : earringIdList) {
				if (id == earringId) {
					break;
				}
				if (pc.getInventory().checkItem(id)) {
					pc.getInventory().consumeItem(id, 1);
				}
				if (pc.getAdditionalWarehouseInventory().checkItem(id)) {
					pc.getAdditionalWarehouseInventory().consumeItem(id, 1);
				}
			}
			htmlid = "";
		}
		return htmlid;
	}

	private String[] makeUbInfoStrings(int npcId) {
		L1UltimateBattle ub = UbTable.getInstance().getUbForNpcId(npcId);
		return ub.makeUbInfoStrings();
	}

	private String talkToDimensionDoor(L1PcInstance pc, L1NpcInstance npc,
			String s) {
		String htmlid = "";
		int protectionId = 0;
		int sealId = 0;
		int locX = 0;
		int locY = 0;
		short mapId = 0;
		if (npc.getNpcTemplate().getNpcId() == 80059) { // 次元の扉(土)
			protectionId = 40909;
			sealId = 40913;
			locX = 32773;
			locY = 32835;
			mapId = 607;
		} else if (npc.getNpcTemplate().getNpcId() == 80060) { // 次元の扉(風)
			protectionId = 40912;
			sealId = 40916;
			locX = 32757;
			locY = 32842;
			mapId = 606;
		} else if (npc.getNpcTemplate().getNpcId() == 80061) { // 次元の扉(水)
			protectionId = 40910;
			sealId = 40914;
			locX = 32830;
			locY = 32822;
			mapId = 604;
		} else if (npc.getNpcTemplate().getNpcId() == 80062) { // 次元の扉(火)
			protectionId = 40911;
			sealId = 40915;
			locX = 32835;
			locY = 32822;
			mapId = 605;
		}

		// 「中に入ってみる」「元素の支配者を近づけてみる」「通行証を使う」「通過する」
		if (s.equalsIgnoreCase("a")) {
			L1Teleport.teleport(pc, locX, locY, mapId, 5, true);
			htmlid = "";
		}
		// 「絵から突出部分を取り除く」
		else if (s.equalsIgnoreCase("b")) {
			L1ItemInstance item = pc.getInventory().storeItem(protectionId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.getName(), item.getLogName())); // \f1%0が%1をくれました。
			}
			htmlid = "";
		}
		// 「通行証を捨てて、この地をあきらめる」
		else if (s.equalsIgnoreCase("c")) {
			htmlid = "wpass07";
		}
		// 「続ける」
		else if (s.equalsIgnoreCase("d")) {
			if (pc.getInventory().checkItem(sealId)) { // 地の印章
				L1ItemInstance item = pc.getInventory().findItemId(sealId);
				pc.getInventory().consumeItem(sealId, item.getCount());
			}
		}
		// 「そのままにする」「慌てて拾う」
		else if (s.equalsIgnoreCase("e")) {
			htmlid = "";
		}
		// 「消えるようにする」
		else if (s.equalsIgnoreCase("f")) {
			if (pc.getInventory().checkItem(protectionId)) { // 地の通行証
				pc.getInventory().consumeItem(protectionId, 1);
			}
			if (pc.getInventory().checkItem(sealId)) { // 地の印章
				L1ItemInstance item = pc.getInventory().findItemId(sealId);
				pc.getInventory().consumeItem(sealId, item.getCount());
			}
			htmlid = "";
		}
		return htmlid;
	}

	private boolean isNpcSellOnly(L1NpcInstance npc) {
		int npcId = npc.getNpcTemplate().getNpcId();
		String npcName = npc.getNpcTemplate().getName();
		if (npcId == 70027 // ディオ
				|| "アデン商団".equals(npcName)) {
			return true;
		}
		return false;
	}

	private void getBloodCrystalByKarma(L1PcInstance pc, L1NpcInstance npc,
			String s) {
		L1ItemInstance item = null;

		// 「ブラッドクリスタルの欠片を1個ください」
		if (s.equalsIgnoreCase("1")) {
			pc.addKarma((int) (500 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.getName(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// ヤヒの姿を記憶するのが難しくなります。
			pc.sendPackets(new S_ServerMessage(1081));
		}
		// 「ブラッドクリスタルの欠片を10個ください」
		else if (s.equalsIgnoreCase("2")) {
			pc.addKarma((int) (5000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 10);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.getName(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// ヤヒの姿を記憶するのが難しくなります。
			pc.sendPackets(new S_ServerMessage(1081));
		}
		// 「ブラッドクリスタルの欠片を100個ください」
		else if (s.equalsIgnoreCase("3")) {
			pc.addKarma((int) (50000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 100);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.getName(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// ヤヒの姿を記憶するのが難しくなります。
			pc.sendPackets(new S_ServerMessage(1081));
		}
	}

	private void getSoulCrystalByKarma(L1PcInstance pc, L1NpcInstance npc,
			String s) {
		L1ItemInstance item = null;

		// 「ソウルクリスタルの欠片を1個ください」
		if (s.equalsIgnoreCase("1")) {
			pc.addKarma((int) (-500 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.getName(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// バルログの冷笑を感じ悪寒が走ります。
			pc.sendPackets(new S_ServerMessage(1080));
		}
		// 「ソウルクリスタルの欠片を10個ください」
		else if (s.equalsIgnoreCase("2")) {
			pc.addKarma((int) (-5000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 10);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.getName(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// バルログの冷笑を感じ悪寒が走ります。
			pc.sendPackets(new S_ServerMessage(1080));
		}
		// 「ソウルクリスタルの欠片を100個ください」
		else if (s.equalsIgnoreCase("3")) {
			pc.addKarma((int) (-50000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 100);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.getName(), item.getLogName())); // \f1%0が%1をくれました。
			}
			// バルログの冷笑を感じ悪寒が走ります。
			pc.sendPackets(new S_ServerMessage(1080));
		}
	}

	private boolean usePolyScroll(L1PcInstance pc, int itemId, String s) {
		int time = 0;
		if (itemId == 40088 || itemId == 40096) { // 変身スクロール、象牙の塔の変身スクロール
			time = 1800;
		} else if (itemId == 140088) { // 祝福された変身スクロール
			time = 2100;
		}

		L1PolyMorph poly = PolyTable.getInstance().getTemplate(s);
		L1ItemInstance item = pc.getInventory().findItemId(itemId);
		boolean isUseItem = false;
		if (poly != null || s.equals("none")) {
			if (s.equals("none")) {
				if (pc.getTempCharGfx() == 6034 || pc.getTempCharGfx() == 6035) {
					isUseItem = true;
				} else {
					pc.removeSkillEffect(SHAPE_CHANGE);
					isUseItem = true;
				}
			} else if (poly.getMinLevel() <= pc.getLevel() || pc.isGm()) {
				L1PolyMorph.doPoly(pc, poly.getPolyId(), time,
						L1PolyMorph.MORPH_BY_ITEMMAGIC);
				isUseItem = true;
			}
		}
		if (isUseItem) {
			pc.getInventory().removeItem(item, 1);
		} else {
			pc.sendPackets(new S_ServerMessage(181)); // \f1そのようなモンスターには変身できません。
		}
		return isUseItem;
	}

	/**
	 * @param s
	 * @param pc
	 */
	private void SellOfPet(String s, L1PcInstance pc) {
		int consumeItem = 0;
		int consumeItemCount = 0;
		int petNpcId = 0;
		int petItemId = 0;
		int upLv = 0;
		int lvExp = 0;
		String msg = "";
		if (s.equalsIgnoreCase("buy 1")) {
			petNpcId = 45042;// ドーベルマン
			consumeItem = 40308;
			consumeItemCount = 50000;
			petItemId = 40314;
			upLv = 10;
			lvExp = ExpTable.getExpByLevel(upLv);
			msg = "アデナ";
		} else if (s.equalsIgnoreCase("buy 2")) {
			petNpcId = 45034;// シェパード
			consumeItem = 40308;
			consumeItemCount = 50000;
			petItemId = 40314;
			upLv = 10;
			lvExp = ExpTable.getExpByLevel(upLv);
			msg = "アデナ";
		} else if (s.equalsIgnoreCase("buy 3")) {
			petNpcId = 45046;// ビーグル
			consumeItem = 40308;
			consumeItemCount = 50000;
			petItemId = 40314;
			upLv = 10;
			lvExp = ExpTable.getExpByLevel(upLv);
			msg = "アデナ";
		} else if (s.equalsIgnoreCase("buy 4")) {
			petNpcId = 45047;// セントバーナード
			consumeItem = 40308;
			consumeItemCount = 50000;
			petItemId = 40314;
			upLv = 10;
			lvExp = ExpTable.getExpByLevel(upLv);
			msg = "アデナ";
		} else if (s.equalsIgnoreCase("buy 8")) {
			petNpcId = 91173;// アズール ハッチリン
			consumeItem = 50502;
			consumeItemCount = 1;
			petItemId = 40314;
			upLv = 5;
			lvExp = ExpTable.getExpByLevel(upLv);
			msg = "グリーンハッチリンの卵";
		} else if (s.equalsIgnoreCase("buy 9")) {
			petNpcId = 91174;// クリムゾン ハッチリン
			consumeItem = 50503;
			consumeItemCount = 1;
			petItemId = 40314;
			upLv = 5;
			lvExp = ExpTable.getExpByLevel(upLv);
			msg = "イエローハッチリンの卵";
		}
		if (petNpcId > 0) {
			if (!pc.getInventory().checkItem(consumeItem, consumeItemCount)) {
				pc.sendPackets(new S_ServerMessage(337, msg));
			} else if (pc.getInventory().getSize() > 180) {
				pc.sendPackets(new S_ServerMessage(263));
				// \f1一人のキャラクターが持って歩けるアイテムは最大180個までです。
			} else if (pc.getInventory().checkItem(consumeItem,
					consumeItemCount)) {
				pc.getInventory().consumeItem(consumeItem, consumeItemCount);
				L1PcInventory inv = pc.getInventory();
				L1ItemInstance petamu = inv.storeItem(petItemId, 1);
				if (petamu != null) {
					PetTable.getInstance().buyNewPet(petNpcId,
							petamu.getId() + 1, petamu.getId(), upLv, lvExp);
					pc.sendPackets(new S_ItemName(petamu));
				}
			}
		}
	}

	private boolean giveBeginnerItems(L1PcInstance pc) {
		boolean result = false;
		L1BeginnerItem items = L1BeginnerItem.get("A");
		result = items.storeToInventory(pc);
		items = L1BeginnerItem.get(pc.getClassFeature().getClassNameInitial());
		result = items.storeToInventory(pc);

		return result;
	}

	private String getDwarfPotion(L1PcInstance pc, int itemId, int price) {
		String htmlid = "";

		if (pc.getInventory().checkItem(50579, 1)
				|| pc.getInventory().checkItem(50580, 1)
				|| pc.getInventory().checkItem(50581, 1)
				|| pc.getInventory().checkItem(50587, 1)
				|| pc.getInventory().checkItem(50588, 1)
				|| pc.getInventory().checkItem(50589, 1)
				|| pc.getAdditionalWarehouseInventory().checkItem(50579, 1)
				|| pc.getAdditionalWarehouseInventory().checkItem(50580, 1)
				|| pc.getAdditionalWarehouseInventory().checkItem(50581, 1)
				|| pc.getAdditionalWarehouseInventory().checkItem(50587, 1)
				|| pc.getAdditionalWarehouseInventory().checkItem(50588, 1)
				|| pc.getAdditionalWarehouseInventory().checkItem(50589, 1)) {
			htmlid = "mopo4";
		} else if (pc.getInventory().checkItem(40308, price)) {
			pc.getInventory().consumeItem(40308, price);
			L1ItemInstance item = pc.getInventory().storeItem(itemId, 1);
			item.startExpirationTimer(pc);
		} else {
			htmlid = "mopo5";
		}

		return htmlid;
	}

	private String getSpellScroll(L1PcInstance pc, L1NpcInstance npc,
			int spellScrollId, int cost, int blankScrollId, int needItemId) {
		String htmlid = "";
		int amount = pc.getCreateScrollAmount();
		pc.setCreateScrollAmount(0);
		int price = cost * amount;
		if (pc.getInventory().checkItem(40308, price)
				&& pc.getInventory().checkItem(blankScrollId, amount)
				&& (needItemId == 0 || (needItemId > 0 && pc.getInventory().checkItem(needItemId, amount)))) {
			L1ItemInstance item = ItemTable.getInstance().createItem(spellScrollId);
			if (pc.getInventory().checkAddItem(item, amount, true) == L1Inventory.OK) {
				if (needItemId > 0) {
					pc.getInventory().consumeItem(needItemId, amount);
				}
				pc.getInventory().consumeItem(40308, price);
				pc.getInventory().consumeItem(blankScrollId, amount);
				item.setCount(amount);
				pc.getInventory().storeItem(item);
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().getName(), item.getLogName()));
				// \f1%0が%1をくれました。
				htmlid = "bs_m1";
			}
		} else {
			htmlid = "bs_m2";
		}
		return htmlid;
	}

	private String teleportToi(L1PcInstance pc, String s) {
		String htmlid = "";

		if (s.equalsIgnoreCase("J")) { // 傲慢の塔6階
			if (pc.getInventory().checkItem(40308, 2400)) {
				pc.getInventory().consumeItem(40308, 2400);
				L1Teleport.teleport(pc, 33805, 32864, (short) 106, 2, true);
			} else {
				htmlid = "maetnob2";
			}
		} else if (s.equalsIgnoreCase("A")) { // 傲慢の塔16階
			if (pc.getInventory().checkItem(40104, 2)
					&& pc.getInventory().checkItem(40308, 300)) {
				pc.getInventory().consumeItem(40104, 2);
				pc.getInventory().consumeItem(40308, 300);
				L1Teleport.teleport(pc, 32781, 32864, (short) 116, 2, true);
			} else {
				htmlid = "maetnob2";
			}
		} else if (s.equalsIgnoreCase("B")) { // 傲慢の塔26階
			if (pc.getInventory().checkItem(40105, 2)
					&& pc.getInventory().checkItem(40308, 300)) {
				pc.getInventory().consumeItem(40105, 2);
				pc.getInventory().consumeItem(40308, 300);
				L1Teleport.teleport(pc, 32781, 32864, (short) 126, 2, true);
			} else {
				htmlid = "maetnob2";
			}
		} else if (s.equalsIgnoreCase("C")) { // 傲慢の塔36階
			if (pc.getInventory().checkItem(40106, 2)
					&& pc.getInventory().checkItem(40308, 300)) {
				pc.getInventory().consumeItem(40106, 2);
				pc.getInventory().consumeItem(40308, 300);
				L1Teleport.teleport(pc, 32781, 32864, (short) 136, 2, true);
			} else {
				htmlid = "maetnob2";
			}
		} else if (s.equalsIgnoreCase("D")) { // 傲慢の塔46階
			if (pc.getInventory().checkItem(40107, 2)
					&& pc.getInventory().checkItem(40308, 300)) {
				pc.getInventory().consumeItem(40107, 2);
				pc.getInventory().consumeItem(40308, 300);
				L1Teleport.teleport(pc, 32781, 32864, (short) 146, 2, true);
			} else {
				htmlid = "maetnob2";
			}
		} else if (s.equalsIgnoreCase("E")) { // 傲慢の塔56階
			if (pc.getInventory().checkItem(40108, 2)
					&& pc.getInventory().checkItem(40308, 300)) {
				pc.getInventory().consumeItem(40108, 2);
				pc.getInventory().consumeItem(40308, 300);
				L1Teleport.teleport(pc, 32783, 32799, (short) 156, 2, true);
			} else {
				htmlid = "maetnob2";
			}
		} else if (s.equalsIgnoreCase("F")) { // 傲慢の塔66階
			if (pc.getInventory().checkItem(40109, 2)
					&& pc.getInventory().checkItem(40308, 300)) {
				pc.getInventory().consumeItem(40109, 2);
				pc.getInventory().consumeItem(40308, 300);
				L1Teleport.teleport(pc, 32783, 32799, (short) 166, 2, true);
			} else {
				htmlid = "maetnob2";
			}
		} else if (s.equalsIgnoreCase("G")) { // 傲慢の塔76階
			if (pc.getInventory().checkItem(40110, 2)
					&& pc.getInventory().checkItem(40308, 300)) {
				pc.getInventory().consumeItem(40110, 2);
				pc.getInventory().consumeItem(40308, 300);
				L1Teleport.teleport(pc, 32783, 32799, (short) 176, 2, true);
			} else {
				htmlid = "maetnob2";
			}
		} else if (s.equalsIgnoreCase("H")) { // 傲慢の塔86階
			if (pc.getInventory().checkItem(40111, 2)
					&& pc.getInventory().checkItem(40308, 300)) {
				pc.getInventory().consumeItem(40111, 2);
				pc.getInventory().consumeItem(40308, 300);
				L1Teleport.teleport(pc, 32783, 32799, (short) 186, 2, true);
			} else {
				htmlid = "maetnob2";
			}
		} else if (s.equalsIgnoreCase("I")) { // 傲慢の塔96階
			if (pc.getInventory().checkItem(40112, 2)
					&& pc.getInventory().checkItem(40308, 300)) {
				pc.getInventory().consumeItem(40112, 2);
				pc.getInventory().consumeItem(40308, 300);
				L1Teleport.teleport(pc, 32783, 32799, (short) 196, 2, true);
			} else {
				htmlid = "maetnob2";
			}
		}

		return htmlid;
	}

	private String teleportToi(L1PcInstance pc, String s, int n) {
		// TOIテレポートスクロール 無し, 11F, 21F, 31F, 41F, 51F, 61F, 71F, 81F, 91F
		int[] itemId = {0, 40104, 40105, 40106, 40107, 40108, 40109, 40110, 40111, 40112};

		// TOI 10F, 20F, 30F, 40F, 50F, 60F, 70F, 80F, 90F, 100F
		int[] x1 = {32800, 32800, 32800, 32800, 32796, 32720, 32720, 32724, 32722, 32731};
		int[] y1 = {32800, 32800, 32800, 32800, 32796, 32821, 32821, 32822, 32827, 32856};
		short[] m1 = {110, 120, 130, 140, 150, 160, 170, 180, 190, 200};

		// TOI 1F, 11F, 21F, 31F, 41F, 51F, 61F, 71F, 81F, 91F
		int[] x2 = {32724, 32631, 32631, 32631, 32631, 32669, 32669, 32669, 32669, 32669};
		int[] y2 = {32799, 32935, 32935, 32935, 32935, 32814, 32814, 32814, 32814, 32814};
		short[] m2 = {101, 111, 121, 131, 141, 151, 161, 171, 181, 191};

		String htmlid = "";

		if (s.equalsIgnoreCase("A")) { // ここの一番上の階に上がる
			if (pc.getInventory().checkItem(40308, 2400) && (itemId[n] == 0
					|| (itemId[n] > 0 && pc.getInventory().checkItem(itemId[n], 2)))) {
				if (itemId[n] > 0) {
					pc.getInventory().consumeItem(itemId[n], 2);
				}
				pc.getInventory().consumeItem(40308, 2400);
				L1Teleport.teleport(pc, x1[n], y1[n], m1[n], 6, true);
			} else {
				htmlid = "maetnob2";
			}
		} else if (s.equalsIgnoreCase("B")) { // ここの一番下の階に降りる
			if (pc.getInventory().checkItem(40308, 300)) {
				pc.getInventory().consumeItem(40308, 300);
				L1Teleport.teleport(pc, x2[n], y2[n], m2[n], 2, true);
			} else {
				htmlid = "maetnob2";
			}
		}

		return htmlid;
	}

	@Override
	public String getType() {
		return C_NPC_ACTION;
	}

}
