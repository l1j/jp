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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_ITEM_LIST;
import static jp.l1j.locale.I18N.I18N_DOES_NOT_EXIST_NPC_LIST;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1Quest;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.item.L1ItemId;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Drop;
import jp.l1j.server.types.Point;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class DropTable {
	private static Logger _log = Logger.getLogger(DropTable.class.getName());

	private static DropTable _instance;

	private static HashMap<Integer, ArrayList<L1Drop>> _droplists; // モンスター毎のドロップリスト

	public static DropTable getInstance() {
		if (_instance == null) {
			_instance = new DropTable();
		}
		return _instance;
	}

	private DropTable() {
		load();
	}

	private HashMap<Integer, ArrayList<L1Drop>> allDropList() {
		HashMap<Integer, ArrayList<L1Drop>> droplistMap = new HashMap<Integer, ArrayList<L1Drop>>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from drop_items");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int mobId = rs.getInt("npc_id");
				int itemId = rs.getInt("item_id");
				int min = rs.getInt("min");
				int max = rs.getInt("max");
				int chance = rs.getInt("chance");
				boolean isErr = false;
				if (NpcTable.getInstance().getTemplate(mobId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_NPC_LIST, mobId));
					// %s はNPCリストに存在しません。
					isErr = true;
				}
				if (ItemTable.getInstance().getTemplate(itemId) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, itemId));
					// %s はアイテムリストに存在しません。
					isErr = true;
				}
				if (isErr) {
					continue;
				}
				L1Drop drop = new L1Drop(mobId, itemId, min, max, chance);
				ArrayList<L1Drop> dropList = droplistMap.get(drop.getMobid());
				if (dropList == null) {
					dropList = new ArrayList<L1Drop>();
					droplistMap.put(new Integer(drop.getMobid()), dropList);
				}
				dropList.add(drop);
			}
			System.out.println("loading drop items...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
		return droplistMap;
	}

	private void load() {
		_droplists = allDropList();
	}
	
	public void reload() {
		load();
	}

	// インベントリにドロップを設定
	public void setDrop(L1NpcInstance npc, L1Inventory inventory) {
		// ドロップリストの取得
		int mobId = npc.getNpcTemplate().getNpcId();
		ArrayList<L1Drop> dropList = _droplists.get(mobId);
		if (dropList == null) {
			return;
		}
		// レート取得
		double droprate = Config.RATE_DROP_ITEMS;
		if (droprate <= 0) {
			droprate = 0;
		}
		double adenarate = Config.RATE_DROP_ADENA;
		if (adenarate <= 0) {
			adenarate = 0;
		}
		if (droprate <= 0 && adenarate <= 0) {
			return;
		}
		// ユニークドロップレートを取得
		double uniqueDropRate = Config.RATE_DROP_UNIQUE_ITEMS;
		if (uniqueDropRate <= 0) {
			uniqueDropRate = 0;
		}
		double uniqueRateOfMapId = MapTable.getInstance().getUniqueRate(npc.getMap().getBaseMapId());
		if (uniqueRateOfMapId <= 0) {
			uniqueRateOfMapId = 0;
		}
		int itemId;
		int itemCount;
		int addCount;
		int randomChance;
		L1ItemInstance item;
		RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
		for (L1Drop drop : dropList) {
			// ドロップアイテムの取得
			itemId = drop.getItemid();
			if (adenarate == 0 && itemId == L1ItemId.ADENA) {
				continue; // アデナレート０でドロップがアデナの場合はスルー
			}
			// ドロップチャンス判定
			randomChance = random.nextInt(0xf4240) + 1;
			double rateOfMapId = MapTable.getInstance().getDropRate(npc.getMap().getBaseMapId());
			double rateOfItem = DropRateTable.getInstance().getDropRate(itemId);
			if (droprate == 0 || drop.getChance() * droprate * rateOfMapId * rateOfItem < randomChance) {
				continue;
			}
			// ドロップ個数を設定
			double amount = DropRateTable.getInstance().getDropAmount(itemId);
			int min = (int) (drop.getMin() * amount);
			int max = (int) (drop.getMax() * amount);
			itemCount = min;
			addCount = max - min + 1;
			if (addCount > 1) {
				itemCount += random.nextInt(addCount);
			}
			if (itemId == L1ItemId.ADENA) { // ドロップがアデナの場合はアデナレートを掛ける
				itemCount *= adenarate;
			}
			if (itemCount <= 0) {
				continue;	// 個数が0以下の場合は、アイテムを生成しない
			}
			if (itemCount > 2000000000) {
				itemCount = 2000000000;
			}
			// アイテムの生成
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(itemCount);
			// アイテム格納
			item = inventory.storeItem(item);	
			// ユニークオプションを付加
			double uniqueRateOfItem = DropRateTable.getInstance().getUniqueRate(itemId);
			double uniqueRate = uniqueDropRate * uniqueRateOfMapId * uniqueRateOfItem;
			item.setUniqueOptions((int) uniqueRate);
		}
	}

	// 平等にドロップ(ルーティング設定に関わらず、キャラクターのインベントリ内に格納)
	public void equalityDrop(L1NpcInstance npc, ArrayList recipientList) {
		// ドロップアイテムが無い場合
		L1Inventory inventory = npc.getInventory();
		if (inventory.getSize() == 0) {
			return;
		}
		// ドロップ非対象者を除外(サモン、ペット、モンスターを中心に画面範囲外のキャラクター)
		L1Character recipient;
		Point pt = npc.getLocation();
		for (int i= recipientList.size() - 1; i >= 0; i--) {
			recipient = (L1Character) recipientList.get(i);
			if (recipient instanceof L1SummonInstance || recipient instanceof L1PetInstance) {
				recipientList.remove(i);
			} else if(recipient == null) {
				recipientList.remove(i);
			} else if(npc.getMapId() != recipient.getMapId()) {
				recipientList.remove(i);
			} else if(!pt.isInScreen(recipient.getLocation())) {
				recipientList.remove(i);
			}
		}
		// ドロップアイテムを分配
		for (L1ItemInstance drop : inventory.getItems()) {
			for (int i= recipientList.size() - 1; i >= 0; i--) {
				L1PcInstance pc = (L1PcInstance) recipientList.get(i);
				if (pc.getInventory().checkAddItem(drop, drop.getCount()) == L1Inventory.OK) {
					pc.getInventory().storeItem(drop.getItemId(), drop.getCount());
					if (pc.isInParty()) {
						pc.sendPackets(new S_ServerMessage(813, npc.getName(), drop.getLogName(), pc.getName()));
					} else {
						pc.sendPackets(new S_ServerMessage(143, npc.getName(), drop.getLogName()));
					}
				} else {
					// インベントリが一杯の場合は足元にドロップ
					L1Inventory ground = L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId());
					ground.storeItem(drop.getItemId(), drop.getCount());
				}
			}
		}
	}
	
	// ドロップをヘイトに応じて分配
	public void dropShare(L1NpcInstance npc, ArrayList acquisitorList, ArrayList hateList) {
		L1Inventory inventory = npc.getInventory();
		if (inventory.getSize() == 0) {
			return;
		}
		if (acquisitorList.size() != hateList.size()) {
			return;
		}
		// ヘイトの合計を取得
		int totalHate = 0;
		L1Character acquisitor;
		for (int i = hateList.size() - 1; i >= 0; i--) {
			acquisitor = (L1Character) acquisitorList.get(i);
			if ((Config.AUTO_LOOT == 2) // オートルーティング２の場合はサモン及びペットは省く
					&& (acquisitor instanceof L1SummonInstance || acquisitor instanceof L1PetInstance)) {
				acquisitorList.remove(i);
				hateList.remove(i);
			} else if (acquisitor != null
					&& acquisitor.getMapId() == npc.getMapId()
					&& acquisitor.getLocation().getTileLineDistance(npc.getLocation()) <= Config.LOOTING_RANGE) {
				totalHate += (Integer) hateList.get(i);
			} else { // nullだったり死んでたり遠かったら排除
				acquisitorList.remove(i);
				hateList.remove(i);
			}
		}
		// ドロップの分配
		L1ItemInstance item;
		L1Inventory targetInventory = null;
		L1PcInstance player;
		L1PcInstance[] partyMember;
		RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
		int randomInt;
		int chanceHate;
		int itemId;
		for (int i = inventory.getSize(); i > 0; i--) {
			item = inventory.getItems().get(0);
			itemId = item.getItemId();
			boolean isGround = false;
			boolean isPartyShare = false;
			if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) { // light系アイテム
				item.setNowLighting(false);
			}
			if (((Config.AUTO_LOOT != 0) || itemId == L1ItemId.ADENA)
					&& totalHate > 0) { // オートルーティングかアデナで取得者がいる場合
				randomInt = random.nextInt(totalHate);
				chanceHate = 0;
				for (int j = hateList.size() - 1; j >= 0; j--) {
					chanceHate += (Integer) hateList.get(j);
					if (chanceHate > randomInt) {
						acquisitor = (L1Character) acquisitorList.get(j);
						if (itemId >= 40131 && itemId <= 40135) {
							if (!(acquisitor instanceof L1PcInstance) || hateList.size() > 1) {
								targetInventory = null;
								break;
							}
							player = (L1PcInstance) acquisitor;
							if (player.getQuest().getStep(L1Quest.QUEST_LYRA) != 1) {
								targetInventory = null;
								break;
							}
						}
						if (acquisitor.getInventory().checkAddItem(item,
								item.getCount()) == L1Inventory.OK) {
							targetInventory = acquisitor.getInventory();
							if (acquisitor instanceof L1PcInstance) {
								player = (L1PcInstance) acquisitor;
								L1ItemInstance l1iteminstance =
									player.getInventory().findItemId(L1ItemId.ADENA); // 所持アデナをチェック
								if (l1iteminstance != null
										&& l1iteminstance.getCount() > 2000000000) {
									targetInventory =
										L1World.getInstance().getInventory(acquisitor.getX(),
											acquisitor.getY(), acquisitor.getMapId()); // 持てないので足元に落とす
									isGround = true;
									player.sendPackets(new S_ServerMessage(166,
											"所持しているアデナ",
											"2,000,000,000を超過しています。"));
									// \f1%0が%4%1%3%2
								} else {
									if (player.isInParty()) { // パーティの場合
										partyMember = player.getParty().getMembers();
										if (player.getPartyType() == 1) {
											int partySize = 0;
											int memberItemCount = 0;
											for (L1PcInstance member : partyMember) {
												if (member !=null && member.getMapId() == npc.getMapId()
														&& member.getCurrentHp() > 0 && !member.isDead()) {
													partySize++;
												}
											}
											if (partySize > 1 && item.getCount() >= partySize) {
												memberItemCount = item.getCount() / partySize;
												int otherCount = item.getCount() - memberItemCount * partySize;
												if (otherCount > 0) {
													item.setCount(memberItemCount + otherCount);
												}
												for (L1PcInstance member : partyMember) {
													if (member !=null && member.getMapId() == npc.getMapId()
															&& member.getCurrentHp() > 0 && !member.isDead()) {
														member.getInventory().storeItem(itemId, memberItemCount);
														for (L1PcInstance pc : player.getParty().getMembers()) {
															pc.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(), member.getName()));
														}
														if (otherCount > 0) {
															item.setCount(memberItemCount);
															otherCount = 0;
														}
													}
												}
												inventory.removeItem(item, item.getCount());
												isPartyShare = true;
											} else {
												for (L1PcInstance pc : player.getParty().getMembers()) {
													pc.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(), player.getName()));
												}
											}
										} else {
											for (L1PcInstance element : partyMember) {
												element.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(), player.getName()));
											}
										}
									} else {
										if (player.getDropLog() == true) {// TODO
											// DROPLOG判定
											// ソロの場合
											player.sendPackets(new S_ServerMessage(143, npc.getName(), item.getLogName()));
											// f1%0が%1をくれました。
										}
									}
								}
							}
						} else {
							targetInventory =
								L1World.getInstance().getInventory(acquisitor.getX(), acquisitor.getY(), acquisitor.getMapId()); // 持てないので足元に落とす
							isGround = true;
						}
						break;
					}
				}
			} else { // ノンオートルーティング
				List<Integer> dirList = new ArrayList<Integer>();
				for (int j = 0; j < 8; j++) {
					dirList.add(j);
				}
				int x = 0;
				int y = 0;
				int dir = 0;
				do {
					if (dirList.size() == 0) {
						x = 0;
						y = 0;
						break;
					}
					randomInt = random.nextInt(dirList.size());
					dir = dirList.get(randomInt);
					dirList.remove(randomInt);
					switch (dir) {
					case 0:
						x = 0;
						y = -1;
						break;
					case 1:
						x = 1;
						y = -1;
						break;
					case 2:
						x = 1;
						y = 0;
						break;
					case 3:
						x = 1;
						y = 1;
						break;
					case 4:
						x = 0;
						y = 1;
						break;
					case 5:
						x = -1;
						y = 1;
						break;
					case 6:
						x = -1;
						y = 0;
						break;
					case 7:
						x = -1;
						y = -1;
						break;
					}
				} while (!npc.getMap().isPassable(npc.getX(), npc.getY(), dir));
				targetInventory = L1World.getInstance().getInventory(npc.getX() + x, npc.getY() + y, npc.getMapId());
				isGround = true;
			}
			if (itemId >= 40131 && itemId <= 40135) {
				if (isGround || targetInventory == null) {
					inventory.removeItem(item, item.getCount());
					continue;
				}
			}
			inventory.tradeItem(item, item.getCount(), targetInventory);
		}
		npc.updateLight();
	}
}