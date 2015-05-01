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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.ClientThread;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.controller.timer.FishingTimeController;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.datatables.MagicDollTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.PetTable;
import jp.l1j.server.datatables.ResolventTable;
import jp.l1j.server.datatables.ReturnLocationTable;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.L1CastleLocation;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1Cooking;
import jp.l1j.server.model.L1DragonSlayer;
import jp.l1j.server.model.L1HouseLocation;
import jp.l1j.server.model.L1ItemDelay;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1PolyMorph;
import jp.l1j.server.model.L1Quest;
import jp.l1j.server.model.L1SpellBook;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1TownLocation;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1DollInstance;
import jp.l1j.server.model.instance.L1GuardianInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1TowerInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.model.item.L1ItemId;
import jp.l1j.server.model.item.executor.L1BlankScroll;
import jp.l1j.server.model.item.executor.L1BlessOfEva;
import jp.l1j.server.model.item.executor.L1BluePotion;
import jp.l1j.server.model.item.executor.L1BravePotion;
import jp.l1j.server.model.item.executor.L1CurePotion;
import jp.l1j.server.model.item.executor.L1Elixir;
import jp.l1j.server.model.item.executor.L1EnchantProtectScroll;
import jp.l1j.server.model.item.executor.L1EnchantScroll;
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
import jp.l1j.server.model.poison.L1DamagePoison;
import jp.l1j.server.model.skill.L1BuffUtil;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_CurseBlind;
import jp.l1j.server.packets.server.S_DragonGate;
import jp.l1j.server.packets.server.S_Fishing;
import jp.l1j.server.packets.server.S_IdentifyDesc;
import jp.l1j.server.packets.server.S_ItemName;
import jp.l1j.server.packets.server.S_Liquor;
import jp.l1j.server.packets.server.S_MessageYN;
import jp.l1j.server.packets.server.S_OwnCharAttrDef;
import jp.l1j.server.packets.server.S_OwnCharStatus;
import jp.l1j.server.packets.server.S_PacketBox;
import jp.l1j.server.packets.server.S_Paralysis;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillHaste;
import jp.l1j.server.packets.server.S_SkillIconAura;
import jp.l1j.server.packets.server.S_SkillIconGFX;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_Sound;
import jp.l1j.server.packets.server.S_SpMr;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Account;
import jp.l1j.server.templates.L1Armor;
import jp.l1j.server.templates.L1BookMark;
import jp.l1j.server.templates.L1EtcItem;
import jp.l1j.server.templates.L1Item;
import jp.l1j.server.templates.L1MagicDoll;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.templates.L1Pet;
import jp.l1j.server.templates.L1Skill;
import jp.l1j.server.utils.L1ItemUtil;

public class C_UseItem extends ClientBasePacket {

	private static final String C_ITEM_USE = "[C] C_ItemUse";
	private static Logger _log = Logger.getLogger(C_UseItem.class.getName());

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	private static Random _randomForGaussian = new Random();

	public C_UseItem(byte abyte0[], ClientThread client) throws Exception {
		super(abyte0);
		int itemObjid = readD();

		L1PcInstance pc = client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
		L1ItemInstance item = pc.getInventory().getItem(itemObjid);

		if (item.getItem().getUseType() == -1) { // none:使用できないアイテム
			pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
			// \f1%0は使用できません。
			return;
		}
		int pcObjid = pc.getId();
		if (pc.isTeleport()) { // テレポート処理中
			return;
		}
		if (item == null && pc.isDead()) {
			return;
		}
		if (!pc.getMap().isUsableItem()) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1ここでは使えません。
			return;
		}
		int itemId;
		try {
			itemId = item.getItem().getItemId();
		} catch (Exception e) {
			return;
		}

		String s = "";
		int objid = 0;
		int skillid = 0;
		int mapid = 0;
		int locx = 0;
		int locy = 0;
		int cookStatus = 0;
		int cookNo = 0;

		int use_type = item.getItem().getUseType();
		if (use_type == 16) { // 変身スクロール(sosc)
			s = readS();
		} else if (use_type == 7 || use_type == 14 || use_type == 26
				|| use_type == 27) {
			// 確認スクロール(identify)、choice、武器強化スクロール(dai)、
			// 防具強化スクロール(zel)
			objid = readD();
		} else if (use_type == 6 || use_type == 29) {
			// テレポートスクロール(ntele)、祝福されたテレポートスクロール(btele)
			mapid = readH();
			objid = readD();
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		} else if (use_type == 28 ) { // ブランクスクロール(blank)
			skillid = readC();
		} else if (use_type == 30) { // スペルスクロール(spell_buff)
			objid = readD();
		} else if (use_type == 5 || use_type == 17 || use_type == 39) {
			// spell_long、spell_short、spell_point
			objid = readD();
			locx = readH();
			locy = readH();
		} else if (use_type == 8) { // 復活スクロール、祝福された復活スクロール(res)
			objid = readD();
		} else if (use_type == 42) {
			// スペルスクロール(spell_point)、釣り竿(fishing_rod)
			locx = readH();
			locy = readH();
		} else {
			objid = readC();
		}

		if (pc.getCurrentHp() <= 0) {
			return;
		}

		int delay_id = 0;
		if (item.getItem().getType2() == 0) { // 種別：その他のアイテム
			delay_id = ((L1EtcItem) item.getItem()).getDelayId();
		}
		if (delay_id != 0) { // ディレイ設定あり
			if (pc.hasItemDelay(delay_id) == true) {
				return;
			}
		}

		// 再使用チェック
		boolean isDelayEffect = false;
		if (item.getItem().getType2() == 0) {
			int delayEffect = ((L1EtcItem) item.getItem()).getDelayEffect();
			if (delayEffect > 0) {
				isDelayEffect = true;
				Timestamp lastUsed = item.getLastUsed();
				if (lastUsed != null) {
					Calendar cal = Calendar.getInstance();
					if ((cal.getTimeInMillis() - lastUsed.getTime()) / 1000 <= delayEffect) {
						// \f1何も起きませんでした。
						pc.sendPackets(new S_ServerMessage(79));
						return;
					}
				}
			}
		}

		_log.finest("request item use (obj) = " + itemObjid + " action = "
				+ objid + " value = " + s);


		if (item.getItem().getType2() == 0) { // 種別：その他のアイテム
			int item_minlvl = ((L1EtcItem) item.getItem()).getMinLevel();
			int item_maxlvl = ((L1EtcItem) item.getItem()).getMaxLevel();
			if (item_minlvl != 0 && item_minlvl > pc.getLevel() && !pc.isGm()) {
				pc.sendPackets(new S_ServerMessage(318, String.valueOf(item_minlvl)));
				// このアイテムは%0レベル以上にならなければ使用できません。
				return;
			} else if (item_maxlvl != 0 && item_maxlvl < pc.getLevel() && !pc.isGm()) {
				// S_ServerMessageでは引数が%dの場合表示されない。
				// pc.sendPackets(new S_ServerMessage(673, String.valueOf(max)));

				// pc.sendPackets(new S_SystemMessage("このアイテムは" + max
				//		+ "レベル以下のみ使用できます。"));

				// TODO
				pc.sendPackets(new S_SystemMessage(String.format(I18N_CAN_BE_USED_BELOW_THE_MAX_LEVEL, item_maxlvl)));
				// このアイテムは%dレベル以下のみ使用できます。
				return;
			}

			if (itemId == 40003) { // ランタン オイル
				for (L1ItemInstance lightItem : pc.getInventory().getItems()) {
					if (lightItem.getItem().getItemId() == 40002) {
						lightItem.setChargeTime(item.getItem().getChargeTime());
						pc.sendPackets(new S_ItemName(lightItem));
						pc.sendPackets(new S_ServerMessage(230));
						// ランタンにオイルを注ぎました。
						pc.getInventory().removeItem(item, 1);
						break;
					}
				}
			} else if (itemId == 40008 || itemId == 40410 || itemId == 140008) {
				// メイプルワンド、ブラックエントの表皮
				L1PolyWand wand = L1PolyWand.get(itemId);
				if (wand != null) {
					wand.use(pc, item, objid);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (itemId >= 40033 && itemId <= 40038) { // エリクサー
				L1Elixir elixir = L1Elixir.get(itemId);
				if (elixir != null) {
					elixir.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (itemId == 40070) { // 進化の実
				pc.sendPackets(new S_ServerMessage(76, item.getLogName()));
				pc.getInventory().removeItem(item, 1);
			} else if (itemId == 40079 || itemId == 40095) { // 帰還スクロール
				if (pc.getMap().isEscapable() || pc.isGm()) {
					int[] loc = ReturnLocationTable.getReturnLocation(pc, true);
					L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
					pc.getInventory().removeItem(item, 1);
				} else {
					pc.sendPackets(new S_ServerMessage(647));
				}
				L1BuffUtil.cancelBarrier(pc); // アブソルート バリアの解除
			} else if (itemId == 40124) { // 血盟帰還スクロール
				if(this.usePledgeScroll(pc, item)) {
					pc.getInventory().removeItem(item, 1);
				}
			} else if (itemId == 40097 || itemId == 40119
					|| itemId == 140119 || itemId == 140329) {
				// 解呪スクロール、原住民のトーテム
				for (L1ItemInstance eachItem : pc.getInventory().getItems()) {
					if (eachItem.getItem().getBless() != 2
							&& eachItem.getItem().getBless() != 130) {
						continue;
					}
					if (!eachItem.isEquipped()
							&& (itemId == 40119 || itemId == 40097)) {
						// n解呪は装備しているものしか解呪しない
						continue;
					}
					int id_normal = eachItem.getItemId() - 200000;
					L1Item template = ItemTable.getInstance().getTemplate(id_normal);
					if (template == null) {
						continue;
					}
					if (pc.getInventory().checkItem(id_normal) && template.isStackable()) {
						pc.getInventory().storeItem(id_normal, eachItem.getCount());
						pc.getInventory().removeItem(eachItem, eachItem.getCount());
					} else {
						eachItem.setItem(template);
						pc.getInventory().updateItem(eachItem, L1PcInventory.COL_ITEMID);
						pc.getInventory().saveItem(eachItem);
					}
				}
				pc.getInventory().removeItem(item, 1);
				pc.sendPackets(new S_ServerMessage(155)); // \f1誰かが助けてくれたようです。
			} else if (itemId > 40169 && itemId < 40226 || itemId >= 45000
					&& itemId <= 45022) { // 魔法書
				L1SpellBook.useSpellBook(pc, item);
			} else if (itemId > 40225 && itemId < 40232 || itemId >= 45023
					&& itemId <= 45024) {
				if (pc.isCrown() || pc.isGm()) {
					if (itemId == 40226 && pc.getLevel() >= 15) {
						L1SpellBook.SpellBook4(pc, item, client);
					} else if (itemId == 40228 && pc.getLevel() >= 30) {
						L1SpellBook.SpellBook4(pc, item, client);
					} else if (itemId == 40227 && pc.getLevel() >= 40) {
						L1SpellBook.SpellBook4(pc, item, client);
					} else if ((itemId == 40231 || itemId == 40232)
							&& pc.getLevel() >= 45) {
						L1SpellBook.SpellBook4(pc, item, client);
					} else if (itemId == 40230 && pc.getLevel() >= 50) {
						L1SpellBook.SpellBook4(pc, item, client);
					} else if (itemId == 40229 && pc.getLevel() >= 55) {
						L1SpellBook.SpellBook4(pc, item, client);
					} else if (itemId == 45023 && pc.getLevel() >= 30) {
						// ディバイン サクリファイス(仮)
						L1SpellBook.SpellBook4(pc, item, client);
					} else if (itemId == 45024 && pc.getLevel() >= 30) {
						// アヴァター(仮)
						L1SpellBook.SpellBook4(pc, item, client);
					} else {
						pc.sendPackets(new S_ServerMessage(312)); // LVが低くて
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			} else if (itemId >= 40232 && itemId <= 40264 // 精霊の水晶
					|| itemId >= 41149 && itemId <= 41153) {
				L1SpellBook.useElfSpellBook(pc, item, itemId);
			} else if ((itemId > 40264 && itemId < 40280) || itemId == 42106) { // 闇精霊の水晶
				if (pc.isDarkelf() || pc.isGm()) {
					if (itemId >= 40265 && itemId <= 40269
							&& pc.getLevel() >= 15) {
						L1SpellBook.SpellBook1(pc, item, client);
					} else if (itemId >= 40270 && itemId <= 40274
							&& pc.getLevel() >= 30) {
						L1SpellBook.SpellBook1(pc, item, client);
					} else if (itemId >= 40275 && itemId <= 40279
							&& pc.getLevel() >= 45) {
						L1SpellBook.SpellBook1(pc, item, client);
					} else if (itemId == 42106 && pc.getLevel() >= 60) {
						L1SpellBook.SpellBook1(pc, item, client);
					} else {
						pc.sendPackets(new S_ServerMessage(312));
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					// (原文:闇精霊の水晶はダークエルフのみが習得できます。)
				}
			} else if (itemId >= 40164 && itemId <= 40166 // 技術書
					|| itemId >= 41147 && itemId <= 41148) {
				if (pc.isKnight() || pc.isGm()) {
					if (itemId >= 40164 && itemId <= 40165 // スタン、リダクションアーマー
							&& pc.getLevel() >= 50) {
						L1SpellBook.SpellBook3(pc, item, client);
					} else if (itemId >= 41147 && itemId <= 41148 // ソリッドキャリッジ、
							// カウンターバリア
							&& pc.getLevel() >= 50) {
						L1SpellBook.SpellBook3(pc, item, client);
					} else if (itemId == 40166 && pc.getLevel() >= 60) { // バウンスアタック
						L1SpellBook.SpellBook3(pc, item, client);
					} else {
						pc.sendPackets(new S_ServerMessage(312));
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			} else if (itemId >= 49102 && itemId <= 49116) { // ドラゴンナイトの書板
				if (pc.isDragonKnight() || pc.isGm()) {
					if (itemId >= 49102 && itemId <= 49106 // ドラゴンナイト秘技LV1
							&& pc.getLevel() >= 15) {
						L1SpellBook.SpellBook5(pc, item, client);
					} else if (itemId >= 49107 && itemId <= 49111 // ドラゴンナイト秘技LV2
							&& pc.getLevel() >= 30) {
						L1SpellBook.SpellBook5(pc, item, client);
					} else if (itemId >= 49112 && itemId <= 49116 // ドラゴンナイト秘技LV3
							&& pc.getLevel() >= 45) {
						L1SpellBook.SpellBook5(pc, item, client);
					} else {
						pc.sendPackets(new S_ServerMessage(312));
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			} else if (itemId >= 49117 && itemId <= 49136) { // 記憶の水晶
				if (pc.isIllusionist() || pc.isGm()) {
					if (itemId >= 49117 && itemId <= 49121 // イリュージョニスト魔法LV1
							&& pc.getLevel() >= 10) {
						L1SpellBook.SpellBook6(pc, item, client);
					} else if (itemId >= 49122 && itemId <= 49126 // イリュージョニスト魔法LV2
							&& pc.getLevel() >= 20) {
						L1SpellBook.SpellBook6(pc, item, client);
					} else if (itemId >= 49127 && itemId <= 49131 // イリュージョニスト魔法LV3
							&& pc.getLevel() >= 30) {
						L1SpellBook.SpellBook6(pc, item, client);
					} else if (itemId >= 49132 && itemId <= 49136 // イリュージョニスト魔法LV4
							&& pc.getLevel() >= 40) {
						L1SpellBook.SpellBook6(pc, item, client);
					} else {
						pc.sendPackets(new S_ServerMessage(312));
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			} else if (itemId == 40314 || itemId == 40316) { // ペットのアミュレット
				if (pc.getInventory().checkItem(41160)) { // 召喚の笛
					if (withdrawPet(pc, itemObjid)) {
						pc.getInventory().consumeItem(41160, 1);
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					// \f1何も起きませんでした。
				}
			} else if (itemId == 40315) { // ペットの笛
				pc.sendPackets(new S_Sound(437));
				pc.broadcastPacket(new S_Sound(437));
				Object[] petList = pc.getPetList().values().toArray();
				for (Object petObject : petList) {
					if (petObject instanceof L1PetInstance) { // ペット
						L1PetInstance pet = (L1PetInstance) petObject;
						pet.call();
					}
				}
			} else if (itemId == 40317) { // 砥石
				L1ItemInstance target = pc.getInventory().getItem(objid);
				if (target.getItem().getType2() != 0
						&& target.getDurability() > 0) { // 武器か防具の場合のみ
					String msg0;
					pc.getInventory().recoveryDamage(target);
					msg0 = target.getLogName();
					if (target.getDurability() == 0) {
						pc.sendPackets(new S_ServerMessage(464, msg0));
						// %0%sは新品同様の状態になりました。
					} else {
						pc.sendPackets(new S_ServerMessage(463, msg0));
						// %0の状態が良くなりました。
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					// \f1何も起きませんでした。
				}
				pc.getInventory().removeItem(item, 1);
			} else if (itemId == 40493) { // マジックフルート
				pc.sendPackets(new S_Sound(165));
				pc.broadcastPacket(new S_Sound(165));
				for (L1Object visible : pc.getKnownObjects()) {
					if (visible instanceof L1GuardianInstance) {
						L1GuardianInstance guardian = (L1GuardianInstance) visible;
						if (guardian.getNpcTemplate().getNpcId() == 70850) { // パン
							L1ItemUtil.createNewItem(pc, 88, 1);
							pc.getInventory().removeItem(item, 1);
						}
					}
				}
			} else if (itemId == 40858) { // liquor（酒）
				pc.setDrink(true);
				pc.sendPackets(new S_Liquor(pc.getId(), 1));
				pc.getInventory().removeItem(item, 1);
			} else if (itemId >= 40901 && itemId <= 40908) { // 各種エンゲージリング
				useEngagementRing(pc);
			} else if (itemId == 41245) { // 溶解剤
				L1ItemInstance target = pc.getInventory().getItem(objid);
				useResolvent(pc, target, item);
			} else if (itemId >= 41277 && itemId <= 41292 // Lv1料理
					|| itemId >= 49049 && itemId <= 49064 // Lv2料理
					|| itemId >= 49244 && itemId <= 49259 // Lv3料理
					|| itemId >= 50641 && itemId <= 50644 // 新料理
					|| itemId == 50543) { // 象牙の塔の妙薬
				L1Cooking.useCookingItem(pc, item);
			} else if (itemId == 41315) { // 聖水
				if (pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
					pc.sendPackets(new S_ServerMessage(79));
					// \f1何も起きませんでした。
					return;
				}
				if (pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
					pc.removeSkillEffect(STATUS_HOLY_MITHRIL_POWDER);
				}
				pc.setSkillEffect(STATUS_HOLY_WATER, 912 * 1000);
				pc.sendPackets(new S_SkillSound(pc.getId(), 190));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
				pc.sendPackets(new S_ServerMessage(1141));
				pc.getInventory().removeItem(item, 1);
			} else if (itemId == 41316) { // 神聖なミスリル パウダー
				if (pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
					pc.sendPackets(new S_ServerMessage(79));
					// \f1何も起きませんでした。
					return;
				}
				if (pc.hasSkillEffect(STATUS_HOLY_WATER)) {
					pc.removeSkillEffect(STATUS_HOLY_WATER);
				}
				pc.setSkillEffect(STATUS_HOLY_MITHRIL_POWDER, 912 * 1000);
				pc.sendPackets(new S_SkillSound(pc.getId(), 190));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
				pc.sendPackets(new S_ServerMessage(1142));
				pc.getInventory().removeItem(item, 1);
			} else if (itemId == 41354) { // 神聖なエヴァの水
				if (pc.hasSkillEffect(STATUS_HOLY_WATER)
						|| pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
					pc.sendPackets(new S_ServerMessage(79));
					// \f1何も起きませんでした。
					return;
				}
				pc.setSkillEffect(STATUS_HOLY_WATER_OF_EVA, 912 * 1000);
				pc.sendPackets(new S_SkillIconAura(221, 912, 5));
				pc.sendPackets(new S_SkillSound(pc.getId(), 190));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
				pc.sendPackets(new S_ServerMessage(1140));
				pc.getInventory().removeItem(item, 1);
			} else if (itemId == 41345) { // 酸性の乳液
				L1DamagePoison.doInfection(pc, pc, 3000, 5);
				pc.getInventory().removeItem(item, 1);
			} else if (itemId == 41401) { // 家具除去ワンド
				if (L1Furniture.getInstance().remove(pc, itemId, item)) {
					if (item.getChargeCount() > 1) {
						item.setChargeCount(item.getChargeCount() - 1);
						pc.getInventory().updateItem(item,
								L1PcInventory.COL_CHARGE_COUNT);
					} else {
						pc.getInventory().removeItem(item, 1);
					}
				}
			} else if (itemId == 41426) { // 封印スクロール
				L1ItemInstance lockItem = pc.getInventory().getItem(objid);
				if (lockItem != null && lockItem.getItem().getType2() == 1
						|| lockItem.getItem().getType2() == 2
						|| lockItem.getItem().getType2() == 0
						&& lockItem.getItem().isSealable()) {
					if (!lockItem.isSealed()) {
						lockItem.setSealed(true);
						pc.getInventory().updateItem(lockItem,
								L1PcInventory.COL_BLESS);
						pc.getInventory().saveItem(lockItem);
						pc.getInventory().removeItem(item, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
						// \f1何も起きませんでした。
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					// \f1何も起きませんでした。
				}
			} else if (itemId == 41427) { // 封印解除スクロール
				L1ItemInstance lockItem = pc.getInventory().getItem(objid);
				if (lockItem != null && lockItem.getItem().getType2() == 1
						|| lockItem.getItem().getType2() == 2
						|| lockItem.getItem().getType2() == 0
						&& lockItem.getItem().isSealable()) {
					if (lockItem.isSealed()) {
						lockItem.setSealed(false);
						pc.getInventory().updateItem(lockItem,
								L1PcInventory.COL_BLESS);
						pc.getInventory().saveItem(lockItem);
						pc.getInventory().removeItem(item, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
						// \f1何も起きませんでした。
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					// \f1何も起きませんでした。
				}
			} else if (itemId == 41428) { // 太古の玉爾
				if (pc != null && item != null) {
					L1Account account = L1Account.findByName(pc.getAccountName());
					if (account == null) {
						pc.sendPackets(new S_ServerMessage(79));
						// \f1何も起きませんでした。
						return;
					}
					int characterSlot = account.getCharacterSlot();
					int maxAmount = Config.DEFAULT_CHARACTER_SLOT + characterSlot;
					if (maxAmount >= 8) {
						pc.sendPackets(new S_ServerMessage(79));
						// \f1何も起きませんでした。
						return;
					}
					if (characterSlot < 0) {
						characterSlot = 0;
					} else {
						characterSlot += 1;
					}
					account.setCharacterSlot(characterSlot);
					account.updateCharacterSlot();
					pc.getInventory().removeItem(item, 1);
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			} else if (itemId == 42100) { // 倉庫番のキー
				if (!pc.getUseAdditionalWarehouse()) {
					pc.getInventory().removeItem(item, 1);
					pc.setUseAdditionalWarehouse(true);
					pc.save();
					pc.sendPackets(new S_ServerMessage(1624,
							Config.MAX_ADDITIONAL_WAREHOUSE_ITEM + ""));
					// 追加倉庫が%0セル拡張されました。
				} else {
					pc.sendPackets(new S_ServerMessage(79));
					// \f1何も起きませんでした。
				}
			} else if (itemId == 43001) { // 性別変更スクロール(オリジナルアイテム)
				HashMap<Integer, Integer> maleToFemale = new HashMap<Integer, Integer>();
				maleToFemale.put(0, 1);
				maleToFemale.put(61, 48);
				maleToFemale.put(138, 37);
				maleToFemale.put(734, 1186);
				maleToFemale.put(2786, 2796);
				maleToFemale.put(6658, 6661);
				maleToFemale.put(6671, 6650);

				HashMap<Integer, Integer> femaleToMale = new HashMap<Integer, Integer>();
				femaleToMale.put(1, 0);
				femaleToMale.put(48, 61);
				femaleToMale.put(37, 138);
				femaleToMale.put(1186, 734);
				femaleToMale.put(2796, 2786);
				femaleToMale.put(6661, 6658);
				femaleToMale.put(6650, 6671);

				if (pc.getSex() == 0) {
					pc.setClassId(maleToFemale.get(pc.getClassId()));
					pc.setSex(1);
				} else {
					pc.setClassId(femaleToMale.get(pc.getClassId()));
					pc.setSex(0);
				}
				pc.save();
				L1PolyMorph.doPoly(pc, pc.getClassId(), 0, L1PolyMorph.MORPH_BY_ITEMMAGIC);
				pc.getInventory().removeItem(item, 1);
			} else if (itemId == 49168) { // 破壊の秘薬　
				pc.setSkillEffect(STATUS_DESTRUCTION_NOSTRUM, 600 * 1000);
				pc.sendPackets(new S_SkillIconAura(221, 600, 6));
				pc.sendPackets(new S_SkillSound(pc.getId(), 190));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
				pc.sendPackets(new S_ServerMessage(1382));
				pc.getInventory().removeItem(item, 1);
			} else if (itemId >= 49304 && itemId <= 49309) {
				// 不確かなマリスエレメント武器
				L1UnknownMaliceWeapon malice = L1UnknownMaliceWeapon.get(itemId);
				if (malice != null) {
					malice.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (itemId == 50501) { // ドラゴンキー
				if (!L1CastleLocation.checkInAllWarArea(pc.getLocation())) {
					pc.sendPackets(new S_DragonGate(pc,
							L1DragonSlayer.getInstance().checkDragonPortal()));
				} else { // 戦争中のエリア内の場合
					pc.sendPackets(new S_ServerMessage(79));
					// \f1何も起きませんでした。
				}
			} else if (itemId == 50539) { // ジェフの魔法薬
				L1ItemInstance target = pc.getInventory().getItem(objid);
				if (enchantMagicDoll(pc, target)) {
					pc.getInventory().removeItem(item, 1);
				}
			} else if (itemId == 50547) { // おいしい飴
				useMagicCandy(pc, itemId);
			} else if (itemId == 50560 || itemId == 50561 || itemId == 50562
					|| itemId == 50563) { // マジックチャージ
				L1ItemInstance target = pc.getInventory().getItem(objid);
				if (target != null) {
					if (chargeMagicDoll(pc, item, target)) {
						pc.getInventory().removeItem(item, 1);
					}
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (itemId == 50585 || itemId == 50586) {
				// くるった時計：2時間、くるった時計：4時間
				L1ItemInstance target = pc.getInventory().getItem(objid);
				L1SpeedUpClock clock = L1SpeedUpClock.get(itemId);
				if (clock != null) {
					clock.use(pc, item, target);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}

				// L1J-JP2プロジェクトのオリジナル仕様？なのでコメントアウト
				//			} else if (itemId == 43000) { // 復活のポーション（Lv99キャラのみが使用可能/Lv1に戻る効果）
				//				pc.setExp(1);
				//				pc.resetLevel();
				//				pc.setBonusStats(0);
				//				pc.sendPackets(new S_SkillSound(pcObjid, 191));
				//				pc.broadcastPacket(new S_SkillSound(pcObjid, 191));
				//				pc.sendPackets(new S_OwnCharStatus(pc));
				//				pc.getInventory().removeItem(item, 1);
				//				pc.sendPackets(new S_ServerMessage(822)); // 独自アイテムですので、メッセージは適当です。
				//				pc.save(); // DBにキャラクター情報を書き込む
				//			} else if (itemId == 240100) { // 呪われたテレポートスクロール(オリジナルアイテム)
				//				L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), true);
				//				pc.getInventory().removeItem(item, 1);
				//				L1BuffUtil.cancelBarrier(pc); // アブソルート バリアの解除

			} else if (item.getItem().getType() == 0) { // アロー
				pc.getInventory().setArrow(item.getItem().getItemId());
				pc.sendPackets(new S_ServerMessage(452, item.getLogName()));
				// %0が選択されました。
			} else if (item.getItem().getType() == 15) { // スティング
				pc.getInventory().setSting(item.getItem().getItemId());
				pc.sendPackets(new S_ServerMessage(452, item.getLogName()));
				// %0が選択されました。
			} else if (item.getItem().getType() == 20) { // スペルアイコン
				L1SpellIcon spell = L1SpellIcon.get(itemId);
				if (spell != null) {
					spell.use(pc, item, objid, locx, locy);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (item.getItem().getType() == 18 // スペルスクロール
					|| item.getItem().getType() == 19 // エボニーワンド
					) {
				L1SpellItem spell = L1SpellItem.get(itemId);
				if (spell != null) {
					spell.use(pc, item, objid, locx, locy);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (item.getItem().getType() == 2) { // light系アイテム
				useLight(pc, item);
			} else if (item.getItem().getType() == 7) { // 食べ物(food)
				eatFood(pc, item);
				pc.getInventory().consumeItem(itemId, 1);
			} else if (item.getItem().getType() == 16) { // treasure_box
				L1TreasureBox box = L1TreasureBox.get(itemId);
				if (box != null) {
					if (box.open(pc)) {
						L1EtcItem temp = (L1EtcItem) item.getItem();
						if (temp.getDelayEffect() > 0) {
							isDelayEffect = true;
						} else {
							pc.getInventory().removeItem(item, 1);
						}
						if (item.getChargeCount() > 1) {
							item.setChargeCount(item.getChargeCount() - 1);
							pc.getInventory().updateItem(item,
									L1PcInventory.COL_CHARGE_COUNT);
						} else if (item.getChargeCount() == 1) {
							pc.getInventory().removeItem(item, 1);
						}
					}
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (item.getItem().getType() == 17) { // マジックドール類
				useMagicDoll(pc, item);
			} else if (item.getItem().getType() == 5) { // 花火(firecracker)
				L1FireCracker fireCracker = L1FireCracker.get(itemId);
				if (fireCracker != null) {
					fireCracker.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (item.getItem().getType() == 21) { // 蒸発保護スクロール
				L1ItemInstance target = pc.getInventory().getItem(objid);
				L1EnchantProtectScroll scroll = L1EnchantProtectScroll.get(item.getItemId());
				if (scroll != null) {
					scroll.use(pc, item, target);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (item.getItem().getType() == 22) { // ユニーク強化スクロール
				L1ItemInstance target = pc.getInventory().getItem(objid);
				L1UniqueEnchantScroll scroll = L1UniqueEnchantScroll.get(item.getItemId());
				if (scroll != null) {
					scroll.use(pc, item, target);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 6 || use_type == 29) {
				// テレポートスクロール(ntele)、祝福されたテレポートスクロール(btele)
				if(useTeleportScroll(pc, item, objid)) {
					pc.getInventory().removeItem(item, 1);
				}
			} else if (use_type == 7) { // 確認スクロール(identify)
				L1ItemInstance target = pc.getInventory().getItem(objid);
				if (!target.isIdentified()) {
					target.setIdentified(true);
					pc.getInventory().updateItem(target, L1PcInventory.COL_IS_ID);
				}
				pc.sendPackets(new S_IdentifyDesc(target));
				pc.getInventory().removeItem(item, 1);
			} else if (use_type == 8) { // 復活スクロール(res)
				if (useResurrectionScroll(pc, item, objid)) {
					pc.getInventory().removeItem(item, 1);
				}
			} else if (use_type == 14) { // choice
				L1ItemInstance target = pc.getInventory().getItem(objid);
				L1MaterialChoice material = L1MaterialChoice.get(itemId);
				if (material != null) {
					material.use(pc, item, target);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 16) { // 変身スクロール(sosc)
				L1PolyScroll polyScroll = L1PolyScroll.get(itemId);
				if (polyScroll != null) {
					polyScroll.use(pc, item, s);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 26 || use_type == 27) {
				// 武器強化スクロール(dai)、防具強化スクロール(zel)
				L1ItemInstance target = pc.getInventory().getItem(objid);
				if (L1EnchantScroll.getInstance().use(pc, item, target)) {
					pc.getInventory().removeItem(item, 1);
				}
			} else if (use_type == 28) { // ブランクスクロール(blank)
				L1BlankScroll blanksc = L1BlankScroll.get(itemId);
				if (blanksc != null) {
					blanksc.use(pc, item, skillid);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 42) { // 魔法の釣り竿(fishing_rod)
				startFishing(pc, itemId, locx, locy);
			} else if (use_type == 59) { // 回復ポーション(healing)
				L1HealingPotion healingPotion = L1HealingPotion.get(itemId);
				if (healingPotion != null) {
					healingPotion.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 60) { // シアンポーション、エントの枝(cure)
				L1CurePotion curePotion = L1CurePotion.get(itemId);
				if (curePotion != null) {
					curePotion.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 61) { // グリーンポーション系(haste)
				L1GreenPotion greenPotion = L1GreenPotion.get(itemId);
				if (greenPotion != null) {
					greenPotion.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 62) { // ブレイブポーション系(brave)
				L1BravePotion bravePotion = L1BravePotion.get(itemId);
				if (bravePotion != null) {
					bravePotion.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 63) { // チーズケーキ、ドラゴンブラッド(third_speed)
				L1ThirdSpeedPotion thirdSpeedPotion = L1ThirdSpeedPotion.get(itemId);
				if (thirdSpeedPotion != null) {
					thirdSpeedPotion.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 64) { // 魔眼(magic_eye)
				L1MagicEye magicEye = L1MagicEye.get(itemId);
				if (magicEye != null) {
					magicEye.use(pc);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 65) { // 魔力回復ポーション系(magic_healing)
				L1MagicPotion magicPotion = L1MagicPotion.get(itemId);
				if (magicPotion != null) {
					magicPotion.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 66) { // エヴァの祝福、マーメイドの鱗、水の精粋(bless_eva)
				L1BlessOfEva blessOfEva = L1BlessOfEva.get(itemId);
				if (blessOfEva != null) {
					blessOfEva.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 67) { // ブルーポーション系(magic_regeneration)
				L1BluePotion bluePotion = L1BluePotion.get(itemId);
				if (bluePotion != null) {
					bluePotion.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 68) { // ウィズダムポーション(wisdom)
				L1WisdomPotion wisdomPotion = L1WisdomPotion.get(itemId);
				if (wisdomPotion != null) {
					wisdomPotion.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 69) { // 激励・才能・フローラのポーション(flora)
				L1FloraPotion floraPotion = L1FloraPotion.get(itemId);
				if (floraPotion != null) {
					floraPotion.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 78) { // 祈りのポーションなど課金ポーション系
				L1ExtraPotion extraPotion = L1ExtraPotion.get(itemId);
				if (extraPotion != null) {
					extraPotion.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (itemId == L1ItemId.POTION_OF_BLINDNESS) { // オペイクポーション
				if(useBlindPotion(pc)) {
					pc.getInventory().removeItem(item, 1);
				}
			} else if (use_type == 70) { // シャルナ変身スクロール、変身ポーション(poly)
				L1PolyPotion polyPotion = L1PolyPotion.get(itemId);
				if (polyPotion != null) {
					polyPotion.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 71) { // HTML表示系アイテム(npc_talk)
				L1ShowMessage message = L1ShowMessage.get(itemId);
				if (message != null) {
					if (!message.use(pc)) {
						pc.sendPackets(new S_ServerMessage(79));
						// \f1何も起きませんでした。
					}
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 72) { // ルーレット(roulette)
				L1Roulette roulette = L1Roulette.get(itemId);
				if (roulette != null) {
					roulette.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 73) { // テレポートスクロール以外(teleport)
				L1TeleportAmulet teleport = L1TeleportAmulet.get(itemId);
				if (teleport != null) {
					if (!teleport.use(pc, item)) {
						pc.sendPackets(new S_ServerMessage(79));
						// \f1何も起きませんでした。
					}
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 74) { // モンスター召喚(spawn)
				L1SpawnWand spawn = L1SpawnWand.get(itemId);
				if (spawn != null) {
					if (!spawn.use(pc, item)) {
						pc.sendPackets(new S_ServerMessage(79));
						// \f1何も起きませんでした。
					}
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 75) { // 家具
				L1Furniture furniture = L1Furniture.get(itemId);
				if (furniture != null) {
					furniture.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else if (use_type == 77) { // material
				L1Material material = L1Material.get(itemId);
				if (material != null) {
					material.use(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					// \f1%0は使用できません。
				}
			} else {
				int locX = ((L1EtcItem) item.getItem()).getLocX();
				int locY = ((L1EtcItem) item.getItem()).getLocY();
				short mapId = ((L1EtcItem) item.getItem()).getMapId();
				if (locX != 0 && locY != 0) { // 各種テレポートスクロール
					L1BuffUtil.cancelBarrier(pc); // アブソルート バリアの解除
					if (pc.getMap().isEscapable() || pc.isGm()) {
						L1Teleport.teleport(pc, locX, locY, mapId, pc.getHeading(), true);
						pc.getInventory().removeItem(item, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(647));
					}
					L1BuffUtil.barrier(pc, 3000); // 3秒間は無敵（アブソルートバリア状態）にする。
				} else {
					if (item.getCount() < 1) { // あり得ない？
						pc.sendPackets(new S_ServerMessage(329, item.getLogName()));
						// \f1%0を持っていません。
					} else {
						pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
						// \f1%0は使用できません。
					}
				}
			}

		} else if (item.getItem().getType2() == 1) {
			// 種別：武器
			int min = item.getItem().getMinLevel();
			int max = item.getItem().getMaxLevel();
			if (min != 0 && min > pc.getLevel()) {
				pc.sendPackets(new S_ServerMessage(318, String.valueOf(min)));
				// このアイテムは%0レベル以上にならなければ使用できません。
			} else if (max != 0 && max < pc.getLevel()) {
				if (max < 50) {
					pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_LEVEL_OVER, max));
				} else {
					// S_ServerMessageでは引数が%dの場合表示されない。
					// pc.sendPackets(new S_ServerMessage(673, String.valueOf(max)));

					// pc.sendPackets(new S_SystemMessage("このアイテムは" + max
					//		+ "レベル以下のみ使用できます。"));

					// TODO
					pc.sendPackets(new S_SystemMessage(String.format(I18N_CAN_BE_USED_BELOW_THE_MAX_LEVEL, max)));
					// このアイテムは%dレベル以下のみ使用できます。
				}
			} else {
				if (pc.isCrown() && item.getItem().isUseRoyal()
						|| pc.isKnight() && item.getItem().isUseKnight()
						|| pc.isElf() && item.getItem().isUseElf()
						|| pc.isWizard() && item.getItem().isUseWizard()
						|| pc.isDarkelf() && item.getItem().isUseDarkelf()
						|| pc.isDragonKnight() && item.getItem().isUseDragonknight()
						|| pc.isIllusionist() && item.getItem().isUseIllusionist()) {
					UseWeapon(pc, item);
				} else {
					pc.sendPackets(new S_ServerMessage(264));
					// \f1あなたのクラスではこのアイテムは使用できません。
				}
			}
		} else if (item.getItem().getType2() == 2) { // 種別：防具
			if (pc.isCrown() && item.getItem().isUseRoyal()
					|| pc.isKnight() && item.getItem().isUseKnight()
					|| pc.isElf() && item.getItem().isUseElf()
					|| pc.isWizard() && item.getItem().isUseWizard()
					|| pc.isDarkelf() && item.getItem().isUseDarkelf()
					|| pc.isDragonKnight() && item.getItem().isUseDragonknight()
					|| pc.isIllusionist() && item.getItem().isUseIllusionist()) {

				int min = ((L1Armor) item.getItem()).getMinLevel();
				int max = ((L1Armor) item.getItem()).getMaxLevel();
				if (min != 0 && min > pc.getLevel()) {
					pc.sendPackets(new S_ServerMessage(318, String.valueOf(min)));
					// このアイテムは%0レベル以上にならなければ使用できません。
				} else if (max != 0 && max < pc.getLevel()) {
					if (max < 50) {
						pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_LEVEL_OVER, max));
					} else {
						// S_ServerMessageでは引数が%dの場合表示されない。
						// pc.sendPackets(new S_ServerMessage(673, String.valueOf(max)));

						// pc.sendPackets(new S_SystemMessage("このアイテムは" + max
						//		+ "レベル以下のみ使用できます。"));

						// TODO
						pc.sendPackets(new S_SystemMessage(String.format(I18N_CAN_BE_USED_BELOW_THE_MAX_LEVEL, max)));
						// このアイテムは%dレベル以下のみ使用できます。
					}
				} else {
					UseArmor(pc, item);
				}
			} else {
				pc.sendPackets(new S_ServerMessage(264));
				// \f1あなたのクラスではこのアイテムは使用できません。
			}
		}

		// 効果ディレイがある場合は現在時間をセット
		if (isDelayEffect) {
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			item.setLastUsed(ts);
			pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
			pc.getInventory().saveItem(item);
		}

		L1ItemDelay.onItemUse(client, item); // アイテムディレイ開始

	}

	private void UseArmor(L1PcInstance activeChar, L1ItemInstance armor) {
		int itemid = armor.getItem().getItemId();
		int type = armor.getItem().getType();
		L1PcInventory pcInventory = activeChar.getInventory();
		boolean equipeSpace; // 装備する箇所が空いているか
		if (type == 11) { // リングの場合
			equipeSpace = pcInventory.getTypeEquipped(2, type) <= 3;//TODO 特定のタイプのアイテムを装備している数
		} else {
			equipeSpace = pcInventory.getTypeEquipped(2, type) <= 0;
		}

		if (equipeSpace && !armor.isEquipped()) {
			// 使用した防具を装備していなくて、その装備箇所が空いている場合（装着を試みる）
			int polyid = activeChar.getTempCharGfx();

			if (!L1PolyMorph.isEquipableArmor(polyid, type)) { // その変身では装備不可
				return;
			}

			//TODO 修正判斷76等戒指&81等戒指等級判斷
			if (type == 11 && pcInventory.getTypeEquipped(2, 11) == 2) {
				L1ItemInstance ring[] = new L1ItemInstance[2];
				ring = pcInventory.getRingEquipped();
				if (activeChar.getLevel() < 76){
					activeChar.sendPackets(new S_ServerMessage(124));
          // \f1すでに何かを装備しています。
					return;
				} else if ((ring[0].getItem().getItemId() == itemid
						&& ring[1].getItem().getItemId() == itemid)) {
					activeChar.sendPackets(new S_ServerMessage(124));
          // \f1すでに何かを装備しています。
					return;
				}
			}

			if (type == 11 && pcInventory.getTypeEquipped(2, 11) == 3) {
				L1ItemInstance ring[] = new L1ItemInstance[2];
				ring = pcInventory.getRingEquipped();
				if (activeChar.getLevel() < 81){
					activeChar.sendPackets(new S_ServerMessage(124));
          // \f1すでに何かを装備しています。
					return;
				} else if ((ring[0].getItem().getItemId() == itemid
						&& ring[1].getItem().getItemId() == itemid) 
						|| (ring[0].getItem().getItemId() == itemid
						&& ring[2].getItem().getItemId() == itemid)
						|| (ring[1].getItem().getItemId() == itemid
						&& ring[2].getItem().getItemId() == itemid)) {
					activeChar.sendPackets(new S_ServerMessage(124));
          // \f1すでに何かを装備しています。
					return;
				}
			}
			//TODO 修正判斷76等戒指&81等戒指等級判斷

			if (type == 7 && pcInventory.getTypeEquipped(2, 8) >= 1
					|| type == 8 && pcInventory.getTypeEquipped(2, 7) >= 1) {
				// シールド、ガーダー同時裝備不可
				activeChar.sendPackets(new S_ServerMessage(124));
				// \f1すでに何かを装備しています。
				return;
			}
			if (type == 7 && activeChar.getWeapon() != null) {
				// シールドの場合、武器を装備していたら両手武器チェック
				if (activeChar.getWeapon().getItem().isTwohanded()) {
					// 両手武器判定
					activeChar.sendPackets(new S_ServerMessage(129));
					// \f1両手の武器を武装したままシールドを着用することはできません。
					return;
				}
			}
			/*
      if (type == 3 && pcInventory.getTypeEquipped(2, 4) >= 1) {
				// シャツの場合、マントを着てないか確認
				activeChar.sendPackets(new S_ServerMessage(126, "$224", "$225"));
				// \f1%1上に%0を着ることはできません。
				return;
			} else if ((type == 3) && pcInventory.getTypeEquipped(2, 2) >= 1) {
				// シャツの場合、メイルを着てないか確認
				activeChar.sendPackets(new S_ServerMessage(126, "$224", "$226"));
				// \f1%1上に%0を着ることはできません。
				return;
			} else if ((type == 2) && pcInventory.getTypeEquipped(2, 4) >= 1) {
				// メイルの場合、マントを着てないか確認
				activeChar.sendPackets(new S_ServerMessage(126, "$226", "$225"));
				// \f1%1上に%0を着ることはできません。
				return;
			}
			*/
			L1BuffUtil.cancelBarrier(activeChar); // アブソルート バリアの解除

			pcInventory.setEquipped(armor, true);
		} else if (armor.isEquipped()) {
			// 使用した防具を装備していた場合（脱着を試みる）
			if (armor.getItem().getBless() == 2) { // 呪われていた場合
				activeChar.sendPackets(new S_ServerMessage(150));
				// \f1はずすことができません。呪いをかけられているようです。
				return;
			}
			/*
      if (type == 3 && pcInventory.getTypeEquipped(2, 2) >= 1) {
				// シャツの場合、メイルを着てないか確認
				activeChar.sendPackets(new S_ServerMessage(127));
				// \f1それは脱ぐことができません
				return;
			} else if ((type == 2 || type == 3)
					&& pcInventory.getTypeEquipped(2, 4) >= 1) {
				// シャツとメイルの場合、マントを着てないか確認
				activeChar.sendPackets(new S_ServerMessage(127));
				// \f1それは脱ぐことができません
				return;
			}
			*/
      if (type == 7) { // シールドの場合、ソリッドキャリッジの効果消失
				 if (activeChar.hasSkillEffect(SOLID_CARRIAGE)) {
					 activeChar.removeSkillEffect(SOLID_CARRIAGE);
				 }
			 }
			 pcInventory.setEquipped(armor, false);
		} else {
			activeChar.sendPackets(new S_ServerMessage(124));
			// \f1すでに何かを装備しています。
		}
		// セット装備用HP、MP、MR更新
		activeChar.setCurrentHp(activeChar.getCurrentHp());
		activeChar.setCurrentMp(activeChar.getCurrentMp());
		activeChar.sendPackets(new S_OwnCharAttrDef(activeChar));
		activeChar.sendPackets(new S_OwnCharStatus(activeChar));
		activeChar.sendPackets(new S_SpMr(activeChar));
	}

	private void UseWeapon(L1PcInstance activeChar, L1ItemInstance weapon) {
		// 武器持ち替えによるモーション溜め防止策
		if (activeChar.getWeaponDelayTime() > System.currentTimeMillis()) {
			return;
		}
		activeChar.setWeaponDelayTime(System.currentTimeMillis() + 1000);

		L1PcInventory pcInventory = activeChar.getInventory();
		if (activeChar.getWeapon() == null
				|| !activeChar.getWeapon().equals(weapon)) {
			// 指定された武器が装備している武器と違う場合、装備できるか確認
			int weapon_type = weapon.getItem().getType();
			int polyid = activeChar.getTempCharGfx();

			if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type)) {
				// その変身では装備不可
				return;
			}
			if (weapon.getItem().isTwohanded()
					&& pcInventory.getTypeEquipped(2, 7) >= 1) {
				// 両手武器の場合、シールド装備の確認
				activeChar.sendPackets(new S_ServerMessage(128));
				// \f1シールドを装備している時は両手で持つ武器を使うことはできません。
				return;
			}
		}

		L1BuffUtil.cancelBarrier(activeChar); // アブソルート バリアの解除

		if (activeChar.getWeapon() != null) {
			// 既に何かを装備している場合、前の装備をはずす
			if (activeChar.getWeapon().getItem().getBless() == 2) {
				// 呪われていた場合
				activeChar.sendPackets(new S_ServerMessage(150));
				// \f1はずすことができません。呪いをかけられているようです。
				return;
			}
			if (activeChar.getWeapon().equals(weapon)) {
				// 装備交換ではなく外すだけ
				pcInventory.setEquipped(activeChar.getWeapon(), false,
						false, false);
				return;
			} else {
				pcInventory.setEquipped(activeChar.getWeapon(), false,
						false, true);
			}
		}

		if (weapon.getItemId() == 200002) { // 呪われたダイスダガー
			activeChar.sendPackets(new S_ServerMessage(149, weapon.getLogName()));
			// \f1%0が手にくっつきました。
		}
		pcInventory.setEquipped(weapon, true, false, false);
	}

	private void eatFood(L1PcInstance pc, L1ItemInstance item) {
		// XXX 食べ物毎の満腹度(100単位で変動)
		short foodvolume1 = (short) (item.getItem().getFoodVolume() / 10);
		short foodvolume2 = 0;
		if (foodvolume1 <= 0) {
			foodvolume1 = 5;
		}
		if (pc.getFood() >= 225) {
			pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, (short) pc.getFood()));
		} else {
			foodvolume2 = (short) (pc.getFood() + foodvolume1);
			if (foodvolume2 <= 225) {
				pc.setFood(foodvolume2);
				pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, (short) pc.getFood()));
			} else {
				pc.setFood((short) 225);
				pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, (short) pc.getFood()));
			}
		}
		if (item.getItemId() == 40057) { // フローティングアイ肉
			pc.setSkillEffect(STATUS_FLOATING_EYE, 0);
		}
		pc.sendPackets(new S_ServerMessage(76, item.getItem().getIdentifiedNameId()));
	}

	private void startFishing(L1PcInstance pc, int itemId, int fishX, int fishY) {
		if (pc.getMapId() != 5124 || fishX <= 32789 || fishX >= 32813
				|| fishY <= 32786 || fishY >= 32812) {
			// ここに釣り竿を投げることはできません。
			pc.sendPackets(new S_ServerMessage(1138));
			return;
		}

		int rodLength = 0;
		if (itemId == 41293) {
			rodLength = 5;
		} else if (itemId == 41294) {
			rodLength = 3;
		}
		if (pc.getMap().isFishingZone(fishX, fishY)) {
			if (pc.getMap().isFishingZone(fishX + 1, fishY)
					&& pc.getMap().isFishingZone(fishX - 1, fishY)
					&& pc.getMap().isFishingZone(fishX, fishY + 1)
					&& pc.getMap().isFishingZone(fishX, fishY - 1)) {
				if (fishX > pc.getX() + rodLength
						|| fishX < pc.getX() - rodLength) {
					// ここに釣り竿を投げることはできません。
					pc.sendPackets(new S_ServerMessage(1138));
				} else if (fishY > pc.getY() + rodLength
						|| fishY < pc.getY() - rodLength) {
					// ここに釣り竿を投げることはできません。
					pc.sendPackets(new S_ServerMessage(1138));
				} else if (pc.getInventory().consumeItem(41295, 1)) { // エサ
					pc.sendPackets(new S_Fishing(pc.getId(),
							ActionCodes.ACTION_Fishing, fishX, fishY));
					pc.broadcastPacket(new S_Fishing(pc.getId(),
							ActionCodes.ACTION_Fishing, fishX, fishY));
					pc.setFishing(true);
					long time = System.currentTimeMillis() + 10000
							+ _random.nextInt(5) * 1000;
					pc.setFishingTime(time);
					FishingTimeController.getInstance().addMember(pc);
				} else {
					// 釣りをするためにはエサが必要です。
					pc.sendPackets(new S_ServerMessage(1137));
				}
			} else {
				// ここに釣り竿を投げることはできません。
				pc.sendPackets(new S_ServerMessage(1138));
			}
		} else {
			// ここに釣り竿を投げることはできません。
			pc.sendPackets(new S_ServerMessage(1138));
		}
	}

	private boolean useBlindPotion(L1PcInstance pc) {
		if (pc.hasSkillEffect(DECAY_POTION)) {
			pc.sendPackets(new S_ServerMessage(698));
			// \f1魔力によって何も飲むことができません。
			return false;
		}

		// アブソルート バリアの解除
		L1BuffUtil.cancelBarrier(pc);

		int time = 16;
		if (pc.hasSkillEffect(CURSE_BLIND)) {
			pc.killSkillEffectTimer(CURSE_BLIND);
		} else if (pc.hasSkillEffect(DARKNESS)) {
			pc.killSkillEffectTimer(DARKNESS);
		}

		if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) {
			pc.sendPackets(new S_CurseBlind(2));
		} else {
			pc.sendPackets(new S_CurseBlind(1));
		}

		pc.setSkillEffect(CURSE_BLIND, time * 1000);

		return true;
	}

	private void useEngagementRing(L1PcInstance pc) {
		L1PcInstance partner = null;
		boolean partner_stat = false;
		if (pc.getPartnerId() != 0) { // 結婚中
			partner = (L1PcInstance) L1World.getInstance().findObject(pc.getPartnerId());
			if (partner != null && partner.getPartnerId() != 0
					&& pc.getPartnerId() == partner.getId()
					&& partner.getPartnerId() == pc.getId()) {
				partner_stat = true;
			}
		} else {
			pc.sendPackets(new S_ServerMessage(662)); // \f1あなたは結婚していません。
			return;
		}

		if (partner_stat) {
			boolean castle_area = L1CastleLocation.checkInAllWarArea(
					partner.getX(), partner.getY(), partner.getMapId());
			if ((partner.getMapId() == 0 || partner.getMapId() == 4
					|| partner.getMapId() == 304)
					&& castle_area == false) {
				L1Teleport.teleport(pc, partner.getX(), partner.getY(), partner.getMapId(), 5, true);
			} else {
				pc.sendPackets(new S_ServerMessage(547));
				// \f1あなたのパートナーは今あなたが行けない所でプレイ中です。
			}
		} else {
			pc.sendPackets(new S_ServerMessage(546));
			// \f1あなたのパートナーは今プレイをしていません。
		}
	}

	private void useLight(L1PcInstance pc, L1ItemInstance item) {
		if (item.getChargeTime() <= 0 && item.getItemId() != 40004) {
			return;
		}
		if (item.isNowLighting()) {
			item.setNowLighting(false);
			item.stopChargeTimer();
			pc.updateLight();
		} else {
			item.setNowLighting(true);
			item.startChargeTimer(pc);
			pc.updateLight();
		}
		pc.sendPackets(new S_ItemName(item));
	}

	private void useMagicCandy(L1PcInstance pc, int itemId) {
		if (pc.hasSkillEffect(71) == true) { // ディケイポーションの状態
			pc.sendPackets(new S_ServerMessage(698));
			// \f1魔力によって何も飲むことができません。
			return;
		}
		// アブソルート バリアの解除
		L1BuffUtil.cancelBarrier(pc);
		// ヘイスト
		L1BuffUtil.haste(pc, 3600 * 1000);
		// フィジカルエンチャントDEX、フィジカルエンチャントSTR、アイアンスキン
		int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, IRON_SKIN };
		for (int i = 0; i < allBuffSkill.length; i++) {
			L1Skill skill = SkillTable.getInstance().findBySkillId(allBuffSkill[i]);
			new L1SkillUse().handleCommands(pc, allBuffSkill[i], pc.getId(),
					pc.getX(), pc.getY(), null, 3600, L1SkillUse.TYPE_GMBUFF);
		}

		if (pc.getQuest().getStep(L1Quest.QUEST_NEWBIE) == L1Quest.QUEST_END) {
			L1ItemInstance item = pc.getInventory().findItemId(itemId);
			pc.getInventory().removeItem(item);
		} else {
			pc.getInventory().consumeItem(itemId, 1);
		}
	}

	private void useMagicDoll(L1PcInstance pc, L1ItemInstance item) {
		int itemId = item.getItemId();
		int itemObjectId = item.getId();
		L1MagicDoll magic_doll = MagicDollTable.getInstance().getTemplate(
				(itemId));
		if (magic_doll != null) {

			boolean isAppear = true;
			L1DollInstance doll = null;
			Object[] dollList = pc.getDollList().values().toArray();
			for (Object dollObject : dollList) {
				doll = (L1DollInstance) dollObject;
				if (doll.getItemObjId() == itemObjectId) {
					// 既に引き出しているマジックドール
					isAppear = false;
					break;
				}
			}

			if (isAppear) {
				if (!pc.getInventory().checkItem(41246, 50)) {
					pc.sendPackets(new S_ServerMessage(337, "$5240"));
					// \f1%0が不足しています。
					return;
				}
				if (dollList.length >= Config.MAX_DOLL_COUNT) {
					// \f1これ以上のモンスターを操ることはできません。
					pc.sendPackets(new S_ServerMessage(319));
					return;
				}
				if (item.isChargeDoll() && item.getChargeTime() <= 0) {
					// キャラクター マジックドールの使用時間を使い切ったため、召還ができません。
					pc.sendPackets(new S_ServerMessage(2788));
					return;
				}

				int npcId = magic_doll.getDollId();
				L1Npc template = NpcTable.getInstance().getTemplate(npcId);
				doll = new L1DollInstance(template, pc, itemId, itemObjectId);
				pc.sendPackets(new S_SkillSound(doll.getId(), 5935));
				pc.broadcastPacket(new S_SkillSound(doll.getId(), 5935));
				pc.sendPackets(new S_SkillIconGFX(56, 1800));
				pc.sendPackets(new S_OwnCharStatus(pc));
				pc.sendPackets(new S_SpMr(pc));
				pc.getInventory().consumeItem(41246, 50);

				if (L1MagicDoll.isHaste(pc)) {
					pc.addHasteItemEquipped(1);
					pc.removeHasteSkillEffect();
					if (pc.getMoveSpeed() != 1) {
						pc.setMoveSpeed(1);
						pc.sendPackets(new S_SkillHaste(pc.getId(), 1, -1));
						pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
					}
				}

				if (item.isChargeDoll()) { // 課金マジックドール
					item.startChargeTimer(pc);
				}
			} else {
				pc.sendPackets(new S_SkillSound(doll.getId(), 5936));
				pc.broadcastPacket(new S_SkillSound(doll.getId(), 5936));
				if (doll.isChargeDoll()) { // 課金マジックドールのタイマーを停止
					item.stopChargeTimer();
				}
				doll.deleteDoll();
				pc.sendPackets(new S_SkillIconGFX(56, 0));
				pc.sendPackets(new S_OwnCharStatus(pc));
				pc.sendPackets(new S_SpMr(pc));
			}

		}
	}

	private boolean chargeMagicDoll(L1PcInstance pc, L1ItemInstance item, L1ItemInstance target) {
		if (target == null || !target.isChargeDoll()) {
			// 課金マジックドールではない
			pc.sendPackets(new S_ServerMessage(2478));
			// 残り30分以内のマジックドールにのみ、チャージが可能です。
			return false;
		}

		int chargeTime = target.getChargeTime();
		if (chargeTime > 1800) {
			pc.sendPackets(new S_ServerMessage(2478));
			// 残り30分以内のマジックドールにのみ、チャージが可能です。
			return false;
		}

		int itemObjectId = target.getId();
		L1DollInstance doll = null;
		Object[] dollList = pc.getDollList().values().toArray();

		for (Object dollObject : dollList) {
			doll = (L1DollInstance) dollObject;
			if (doll.getItemObjId() == itemObjectId) {
				// 既に引き出しているマジックドール
				pc.sendPackets(new S_SkillSound(doll.getId(), 5936));
				pc.broadcastPacket(new S_SkillSound(doll.getId(), 5936));
				if (doll.isChargeDoll()) { // 課金マジックドールのタイマーを停止
					target.stopChargeTimer();
				}
				doll.deleteDoll();
				pc.sendPackets(new S_SkillIconGFX(56, 0));
				pc.sendPackets(new S_OwnCharStatus(pc));
				pc.sendPackets(new S_SpMr(pc));
				break;
			}
		}
		target.setChargeTime(chargeTime + item.getItem().getChargeTime());
		target.save();

		return true;
	}

	private boolean enchantMagicDoll(L1PcInstance pc, L1ItemInstance target) {
		int[] curDollId = new int[] { // 強化前のマジックドール
				49320, 49321, 49322, 49323, 49324, // ジャイアント
				49326, 49327, 49328, 49329, 49330, // サイクロプス
				49332, 49333, 49334, 49335, 49336, // マーメイド
				49338, 49339, 49340, 49341, 49342, // ブルート
				49365, 49366, 49367, 49368, 49369, // ペンギン(兄)
				49371, 49372, 49373, 49374, 49375  // ペンギン(妹)
		};

		int[] newDollId = new int[] { // 強化後のマジックドール
				49321, 49322, 49323, 49324, 49325, // ジャイアント
				49327, 49328, 49329, 49330, 49331, // サイクロプス
				49333, 49334, 49335, 49336, 49337, // マーメイド
				49339, 49340, 49341, 49342, 49343, // ブルート
				49366, 49367, 49368, 49369, 49370, // ペンギン(兄)
				49372, 49373, 49374, 49375, 49376  // ペンギン(妹)
		};

		int i = Arrays.binarySearch(curDollId, target.getItemId());

		if (target == null || i < 0) {
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
		}

		int itemObjectId = target.getId();
		L1DollInstance doll = null;
		Object[] dollList = pc.getDollList().values().toArray();

		for (Object dollObject : dollList) {
			doll = (L1DollInstance) dollObject;
			if (doll.getItemObjId() == itemObjectId) {
				// 既に引き出しているマジックドール
				pc.sendPackets(new S_SkillSound(doll.getId(), 5936));
				pc.broadcastPacket(new S_SkillSound(doll.getId(), 5936));
				if (doll.isChargeDoll()) { // 課金マジックドールのタイマーを停止
					target.stopChargeTimer();
				}
				doll.deleteDoll();
				pc.sendPackets(new S_SkillIconGFX(56, 0));
				break;
			}
		}

		int rnd = _random.nextInt(100) + 1;
		int enchant_chance_doll = (100 + 3 * Config.ENCHANT_CHANCE_DOLL) / 3;
		int enchant_level = target.getEnchantLevel();
		boolean is_identified = target.isIdentified();
		String item_name = (new StringBuilder()).append("+"
				+ enchant_level).append(" ").append(target.getLogName()).toString();

		if (rnd < enchant_chance_doll) {
			// \f1%0が%2%1光ります。
			pc.sendPackets(new S_ServerMessage(161, item_name, "$245", "$248"));
			pc.getInventory().removeItem(target, 1);
			target = pc.getInventory().storeItem(newDollId[i], 1);
			target.setEnchantLevel(enchant_level + 1);
			pc.getInventory().updateItem(target, L1PcInventory.COL_ENCHANTLVL);
			pc.getInventory().saveItem(target);
		} else if (rnd < (enchant_chance_doll * 2)) {
			// \f1%0が%2と強烈に%1光りましたが、幸い無事にすみました。
			pc.sendPackets(new S_ServerMessage(160, item_name, "$245", "$248"));
		} else {
			// TODO 本鯖のメッセージが不明
			if (enchant_level > 0) {
				pc.sendPackets(new S_ServerMessage(161, item_name, "$246", "$247"));
				pc.getInventory().removeItem(target, 1);
				L1ItemInstance item = pc.getInventory().storeItem(curDollId[i - 1], 1);
				item.setEnchantLevel(enchant_level - 1);
				pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
				pc.getInventory().saveItem(item);
			}
		}
		pc.sendPackets(new S_OwnCharStatus(pc));
		pc.sendPackets(new S_SpMr(pc));
		return true;
	}

	private boolean usePledgeScroll(L1PcInstance pc, L1ItemInstance item) {
		if (pc.getMap().isEscapable() || pc.isGm()) {
			int castle_id = 0;
			int house_id = 0;
			if (pc.getClanId() != 0) { // クラン所属
				L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
				if (clan != null) {
					castle_id = clan.getCastleId();
					house_id = clan.getHouseId();
				}
			}
			if (castle_id != 0) { // 城主クラン員
				if (pc.getMap().isEscapable() || pc.isGm()) {
					int[] loc = L1CastleLocation.getCastleLoc(castle_id);
					L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
				} else {
					pc.sendPackets(new S_ServerMessage(647));
					return false;
				}
			} else if (house_id != 0) { // アジト所有クラン員
				if (pc.getMap().isEscapable() || pc.isGm()) {
					int[] loc =  L1HouseLocation.getHouseLoc(house_id);
					L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
				} else {
					pc.sendPackets(new S_ServerMessage(647));
					return false;
				}
			} else {
				if (pc.getHomeTownId() > 0) {
					int[] loc = L1TownLocation.getGetBackLoc(pc.getHomeTownId());
					L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
				} else {
					int[] loc = ReturnLocationTable.getReturnLocation(pc, true);
					L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
				}
			}
		} else {
			pc.sendPackets(new S_ServerMessage(647));
			return false;
		}
		L1BuffUtil.cancelBarrier(pc); // アブソルート バリアの解除
		return true;
	}

	private void useResolvent(L1PcInstance pc, L1ItemInstance item,
			L1ItemInstance resolvent) {
		if (item == null || resolvent == null) {
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return;
		}
		if (item.getItem().getType2() == 1 || item.getItem().getType2() == 2) {
			// 武器・防具
			if (item.getEnchantLevel() != 0) { // 強化済み
				pc.sendPackets(new S_ServerMessage(1161)); // 溶解できません。
				return;
			}
			if (item.isEquipped()) { // 装備中
				pc.sendPackets(new S_ServerMessage(1161)); // 溶解できません。
				return;
			}
		}
		int crystalCount = ResolventTable.getInstance().getCrystalCount(
				item.getItem().getItemId());
		if (crystalCount == 0) {
			pc.sendPackets(new S_ServerMessage(1161)); // 溶解できません。
			return;
		}

		int rnd = _random.nextInt(100) + 1;
		if (rnd >= 1 && rnd <= 50) {
			crystalCount = 0;
			pc.sendPackets(new S_ServerMessage(158, item.getName()));
			// \f1%0が蒸発してなくなりました。
		} else if (rnd >= 51 && rnd <= 90) {
			crystalCount *= 1;
		} else if (rnd >= 91 && rnd <= 100) {
			crystalCount *= 1.5;
			pc.getInventory().storeItem(41246, (int) (crystalCount * 1.5));
		}
		if (crystalCount != 0) {
			L1ItemInstance crystal = ItemTable.getInstance().createItem(41246);
			crystal.setCount(crystalCount);
			if (pc.getInventory().checkAddItem(crystal, 1) == L1Inventory.OK) {
				pc.getInventory().storeItem(crystal);
				pc.sendPackets(new S_ServerMessage(403, crystal.getLogName()));
				// %0を手に入れました。
			} else { // 持てない場合は地面に落とす 處理のキャンセルはしない（不正防止）
				L1World.getInstance().getInventory(pc.getX(), pc.getY(),
						pc.getMapId()).storeItem(crystal);
			}
		}
		pc.getInventory().removeItem(item, 1);
		pc.getInventory().removeItem(resolvent, 1);
	}

	private boolean useResurrectionScroll(L1PcInstance pc, L1ItemInstance item, int objid) {
		int itemId = item.getItemId();
		L1Character resobject = (L1Character) L1World.getInstance().findObject(objid);
		if (resobject != null) {
			if (resobject instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) resobject;
				if (pc.getId() == targetPc.getId()) {
					return false;
				}
				if (L1World.getInstance().getVisiblePlayer(targetPc,0).size() > 0) {
					for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(
							targetPc, 0)) {
						if (!visiblePc.isDead()) {
							// \f1その場所に他の人が立っているので復活させることができません。
							pc.sendPackets(new S_ServerMessage(592));
							return false;
						}
					}
				}
				if (targetPc.getCurrentHp() == 0
						&& targetPc.isDead() == true) {
					if (pc.getMap().isUseResurrection()) {
						targetPc.setTempID(pc.getId());
						if (itemId == 40089) { // また復活したいですか？（Y/N）
							targetPc.sendPackets(new S_MessageYN(321, ""));
						} else if (itemId == 140089) { // また復活したいですか？（Y/N）
							targetPc.sendPackets(new S_MessageYN(322, ""));
						}
					} else {
						return false;
					}
				}
			} else if (resobject instanceof L1NpcInstance) {
				if (!(resobject instanceof L1TowerInstance)) {
					L1NpcInstance npc = (L1NpcInstance) resobject;
					if (npc.getNpcTemplate().isCantResurrect()
							&& !(npc instanceof L1PetInstance)) {
						pc.getInventory().removeItem(item, 1);
						return false;
					}
					if (npc instanceof L1PetInstance
							&& L1World.getInstance().getVisiblePlayer(npc, 0).size() > 0) {
						for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc, 0)) {
							if (!visiblePc.isDead()) {
								// \f1その場所に他の人が立っているので復活させることができません。
								pc.sendPackets(new S_ServerMessage(592));
								return false;
							}
						}
					}
					if (npc.getCurrentHp() == 0 && npc.isDead()) {
						npc.resurrect(npc.getMaxHp() / 4);
						npc.setResurrect(true);
						if ((npc instanceof L1PetInstance)) {
							L1PetInstance pet = (L1PetInstance) npc;
							// 空腹度タイマー開始
							pet.startFoodTimer(pet);
							// HP&MP回復開始
							pet.startHpRegeneration();
							pet.startMpRegeneration();
						}
					}
				}
			}
		}
		return true;
	}

	private boolean useTeleportScroll(L1PcInstance pc, L1ItemInstance item, int objid) {
		int itemId = item.getItemId();
		pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		L1BuffUtil.cancelBarrier(pc); // アブソルート バリアの解除
		L1BookMark bookm = pc.getBookMark(objid);
		if (bookm != null) { // ブックマークを取得出来たらテレポート
			if (pc.getMap().isEscapable() || pc.isGm()) {
				int newX = bookm.getLocX();
				int newY = bookm.getLocY();
				short mapId = bookm.getMapId();

				if (itemId == 40086) { // マステレポートスクロール
					for (L1PcInstance member : L1World.getInstance().getVisiblePlayer(pc)) {
						if (pc.getLocation().getTileLineDistance(
								member.getLocation()) <= 3
								&& member.getClanId() == pc.getClanId()
								&& pc.getClanId() != 0
								&& member.getId() != pc.getId()) {
							L1Teleport.teleport(member, newX, newY, mapId, 5, true);
						}
					}
				}
				L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
			} else {
				L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
				pc.sendPackets(new S_ServerMessage(79));
			}
		} else {
			if (pc.getMap().isTeleportable() || pc.isGm()) {
				L1Location newLocation = pc.getLocation().randomLocation(200, true);
				int newX = newLocation.getX();
				int newY = newLocation.getY();
				short mapId = (short) newLocation.getMapId();

				if (itemId == 40086) { // マステレポートスクロール
					for (L1PcInstance member : L1World.getInstance().getVisiblePlayer(pc)) {
						if (pc.getLocation().getTileLineDistance(
								member.getLocation()) <= 3
								&& member.getClanId() == pc.getClanId()
								&& pc.getClanId() != 0
								&& member.getId() != pc.getId()) {
							L1Teleport.teleport(member, newX, newY, mapId, 5, true);
						}
					}
				}
				L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
			} else {
				L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
				pc.sendPackets(new S_ServerMessage(276));
			}
		}
		L1BuffUtil.barrier(pc, 3000); // 3秒間は無敵（アブソルートバリア状態）にする。

		return true;
	}

	private boolean withdrawPet(L1PcInstance pc, int itemObjectId) {
		int petCost = 0;
		int petCount = 0;
		int divisor = 6;

		if (!pc.getMap().isTakePets()) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1ここでは使えません。
			return false;
		}

		Object[] petList = pc.getPetList().values().toArray();
		for (Object pet : petList) {
			if (pet instanceof L1PetInstance) {
				if (((L1PetInstance) pet).getItemObjId() == itemObjectId) {
					// 既に引き出しているペット
					return false;
				}
			}
			petCost += ((L1NpcInstance) pet).getPetcost();
		}
		int charisma = pc.getCha();
		if (pc.isCrown()) { // 君主
			charisma += 6;
		} else if (pc.isElf()) { // エルフ
			charisma += 12;
		} else if (pc.isWizard()) { // WIZ
			charisma += 6;
		} else if (pc.isDarkelf()) { // DE
			charisma += 6;
		} else if (pc.isDragonKnight()) { // ドラゴンナイト
			charisma += 6;
		} else if (pc.isIllusionist()) { // イリュージョニスト
			charisma += 6;
		}

		L1Pet l1pet = PetTable.getInstance().getTemplate(itemObjectId);
		if (l1pet != null) {
			int npcId = l1pet.getNpcId();
			charisma -= petCost;
			if (npcId == 45313 || npcId == 45710 // タイガー、バトルタイガー
					|| npcId == 45711 || npcId == 45712) { // 紀州犬の子犬、紀州犬
				divisor = 12;
			} else {
				divisor = 6;
			}
			petCount = charisma / divisor;
			if (petCount <= 0) {
				pc.sendPackets(new S_ServerMessage(489));
				// 引き取ろうとするペットが多すぎます。
				return false;
			}
			L1Npc npcTemp = NpcTable.getInstance().getTemplate(npcId);
			L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
			pet.setPetcost(divisor);
		}
		return true;
	}

	@Override
	public String getType() {
		return C_ITEM_USE;
	}
}
