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

package jp.l1j.server.model.skill.executor;

import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Character;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_HpUpdate;
import jp.l1j.server.packets.server.S_MpUpdate;
import jp.l1j.server.packets.server.S_OwnCharAttrDef;
import jp.l1j.server.packets.server.S_OwnCharStatus;
import jp.l1j.server.packets.server.S_PacketBox;
import jp.l1j.server.packets.server.S_SpMr;

// XXX とりあえず料理抽出。まだ改善の余地あり。
public class L1DishBuff extends L1BuffSkillExecutorImpl {

	@Override
	public void addEffect(L1Character user, L1Character target,
			int durationSeconds) {
		if (!(target instanceof L1PcInstance)) {
			return;
		}
		L1PcInstance pc = (L1PcInstance) target;
		int cookingId = _skill.getSkillId();
		int cookingType = 0;
		if (cookingId == COOKING_1_0_N || cookingId == COOKING_1_0_S) { // フローティングアイステーキ
			cookingType = 0;
			pc.addWind(10);
			pc.addWater(10);
			pc.addFire(10);
			pc.addEarth(10);
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		} else if (cookingId == COOKING_1_1_N || cookingId == COOKING_1_1_S) { // ベアーステーキ
			cookingType = 1;
			pc.addMaxHp(30);
			pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // パーティー中
				pc.getParty().updateMiniHP(pc);
			}
		} else if (cookingId == COOKING_1_2_N || cookingId == COOKING_1_2_S) { // ナッツ餅
			cookingType = 2;
		} else if (cookingId == COOKING_1_3_N || cookingId == COOKING_1_3_S) { // 蟻脚のチーズ焼き
			cookingType = 3;
			pc.addAc(-1);
			pc.sendPackets(new S_OwnCharStatus(pc));
		} else if (cookingId == COOKING_1_4_N || cookingId == COOKING_1_4_S) { // フルーツサラダ
			cookingType = 4;
			pc.addMaxMp(20);
			pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		} else if (cookingId == COOKING_1_5_N || cookingId == COOKING_1_5_S) { // フルーツ甘酢あんかけ
			cookingType = 5;
		} else if (cookingId == COOKING_1_6_N || cookingId == COOKING_1_6_S) { // 猪肉の串焼き
			cookingType = 6;
			pc.addMr(5);
			pc.sendPackets(new S_SpMr(pc));
		} else if (cookingId == COOKING_1_7_N || cookingId == COOKING_1_7_S) { // キノコスープ
			cookingType = 7;
		} else if (cookingId == COOKING_2_0_N || cookingId == COOKING_2_0_S) { // キャビアカナッペ
			cookingType = 16;
		} else if (cookingId == COOKING_2_1_N || cookingId == COOKING_2_1_S) { // アリゲーターステーキ
			cookingType = 17;
			pc.addMaxHp(30);
			pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // パーティー中
				pc.getParty().updateMiniHP(pc);
			}
			pc.addMaxMp(30);
			pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		} else if (cookingId == COOKING_2_2_N || cookingId == COOKING_2_2_S) { // タートルドラゴンの菓子
			cookingType = 18;
			pc.addAc(-2);
			pc.sendPackets(new S_OwnCharStatus(pc));
		} else if (cookingId == COOKING_2_3_N || cookingId == COOKING_2_3_S) { // キウィパロット焼き
			cookingType = 19;
		} else if (cookingId == COOKING_2_4_N || cookingId == COOKING_2_4_S) { // スコーピオン焼き
			cookingType = 20;
		} else if (cookingId == COOKING_2_5_N || cookingId == COOKING_2_5_S) { // イレッカドムシチュー
			cookingType = 21;
			pc.addMr(10);
			pc.sendPackets(new S_SpMr(pc));
		} else if (cookingId == COOKING_2_6_N || cookingId == COOKING_2_6_S) { // クモ脚の串焼き
			cookingType = 22;
			pc.addSp(1);
			pc.sendPackets(new S_SpMr(pc));
		} else if (cookingId == COOKING_2_7_N || cookingId == COOKING_2_7_S) { // クラブスープ
			cookingType = 32;
		} else if (cookingId == COOKING_3_0_N || cookingId == COOKING_3_0_S) { // クラスタシアンのハサミ焼き
			cookingType = 37;
		} else if (cookingId == COOKING_3_1_N || cookingId == COOKING_3_1_S) { // グリフォン焼き
			cookingType = 38;
			pc.addMaxHp(50);
			pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // パーティー中
				pc.getParty().updateMiniHP(pc);
			}
			pc.addMaxMp(50);
			pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		} else if (cookingId == COOKING_3_2_N || cookingId == COOKING_3_2_S) { // コカトリスステーキ
			cookingType = 39;
		} else if (cookingId == COOKING_3_3_N || cookingId == COOKING_3_3_S) { // タートルドラゴン焼き
			cookingType = 40;
			pc.addAc(-3);
			pc.sendPackets(new S_OwnCharStatus(pc));
		} else if (cookingId == COOKING_3_4_N || cookingId == COOKING_3_4_S) { // レッサードラゴンの手羽先
			cookingType = 41;
			pc.addMr(15);
			pc.sendPackets(new S_SpMr(pc));
			pc.addWind(10);
			pc.addWater(10);
			pc.addFire(10);
			pc.addEarth(10);
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		} else if (cookingId == COOKING_3_5_N || cookingId == COOKING_3_5_S) { // ドレイク焼き
			cookingType = 42;
			pc.addSp(2);
			pc.sendPackets(new S_SpMr(pc));
		} else if (cookingId == COOKING_3_6_N || cookingId == COOKING_3_6_S) { // 深海魚のシチュー
			cookingType = 43;
			pc.addMaxHp(30);
			pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // パーティー中
				pc.getParty().updateMiniHP(pc);
			}
		} else if (cookingId == COOKING_3_7_N || cookingId == COOKING_3_7_S) { // バシリスクの卵スープ
			cookingType = 44;
		} else if (cookingId == ELIXIR_OF_IVORY_TOWER) { // 象牙の塔の妙薬
			cookingType = 54;
			pc.addHpr(10);
			pc.addMpr(2);
		} else if (cookingId == COOKING_4_1) { // 力強い和牛ステーキ
			cookingType = 54; // TODO アイコンが不明
			pc.addMr(10);
			pc.addWind(10);
			pc.addWater(10);
			pc.addFire(10);
			pc.addEarth(10);
			pc.sendPackets(new S_SpMr(pc));
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		} else if (cookingId == COOKING_4_2) { // 素早い鮭の煮付
			cookingType = 54; // TODO アイコンが不明
			pc.addMr(10);
			pc.addWind(10);
			pc.addWater(10);
			pc.addFire(10);
			pc.addEarth(10);
			pc.sendPackets(new S_SpMr(pc));
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		} else if (cookingId == COOKING_4_3) { // 賢い七面鳥焼き
			cookingType = 54; // TODO アイコンが不明
			pc.addSp(2);
			pc.addWind(10);
			pc.addWater(10);
			pc.addFire(10);
			pc.addEarth(10);
			pc.sendPackets(new S_SpMr(pc));
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		} else if (cookingId == COOKING_4_4) { // 修練の鶏スープ
			cookingType = 54; // TODO アイコンが不明
		}
		pc.sendPackets(new S_PacketBox(53, cookingType, durationSeconds));
		if (cookingId >= COOKING_1_0_N && cookingId <= COOKING_1_6_N
				|| cookingId >= COOKING_1_0_S && cookingId <= COOKING_1_6_S
				|| cookingId >= COOKING_2_0_N && cookingId <= COOKING_2_6_N
				|| cookingId >= COOKING_2_0_S && cookingId <= COOKING_2_6_S
				|| cookingId >= COOKING_3_0_N && cookingId <= COOKING_3_6_N
				|| cookingId >= COOKING_3_0_S && cookingId <= COOKING_3_6_S
				|| cookingId == COOKING_4_1 || cookingId == COOKING_4_2
				|| cookingId == COOKING_4_3 || cookingId == COOKING_4_4) {
			pc.setCookingId(cookingId);
		} else if (cookingId == COOKING_1_7_N || cookingId == COOKING_1_7_S
				|| cookingId == COOKING_2_7_N || cookingId == COOKING_2_7_S
				|| cookingId == COOKING_3_7_N || cookingId == COOKING_3_7_S
				|| cookingId == ELIXIR_OF_IVORY_TOWER) {
			pc.setDessertId(cookingId);
		}

		// XXX 空腹ゲージが17%になるため再送信。S_PacketBoxに空腹ゲージ更新のコードが含まれている？
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

	@Override
	public void removeEffect(L1Character target) {
		if (!(target instanceof L1PcInstance)) {
			return;
		}
		L1PcInstance pc = (L1PcInstance) target;
		int skillId = _skill.getSkillId();
		if (skillId == COOKING_1_0_N || skillId == COOKING_1_0_S) { // フローティングアイステーキ
			pc.addWind(-10);
			pc.addWater(-10);
			pc.addFire(-10);
			pc.addEarth(-10);
			pc.sendPackets(new S_OwnCharAttrDef(pc));
			pc.sendPackets(new S_PacketBox(53, 0, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_1_1_N || skillId == COOKING_1_1_S) { // ベアーステーキ
			pc.addMaxHp(-30);
			pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // パーティー中
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_PacketBox(53, 1, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_1_2_N || skillId == COOKING_1_2_S) { // ナッツ餅
			pc.sendPackets(new S_PacketBox(53, 2, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_1_3_N || skillId == COOKING_1_3_S) { // 蟻脚のチーズ焼き
			pc.addAc(1);
			pc.sendPackets(new S_PacketBox(53, 3, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_1_4_N || skillId == COOKING_1_4_S) { // フルーツサラダ
			pc.addMaxMp(-20);
			pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_PacketBox(53, 4, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_1_5_N || skillId == COOKING_1_5_S) { // フルーツ甘酢あんかけ
			pc.sendPackets(new S_PacketBox(53, 5, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_1_6_N || skillId == COOKING_1_6_S) { // 猪肉の串焼き
			pc.addMr(-5);
			pc.sendPackets(new S_SpMr(pc));
			pc.sendPackets(new S_PacketBox(53, 6, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_1_7_N || skillId == COOKING_1_7_S) { // キノコスープ
			pc.sendPackets(new S_PacketBox(53, 7, 0));
			pc.setDessertId(0);
		} else if (skillId == COOKING_2_0_N || skillId == COOKING_2_0_S) { // キャビアカナッペ
			pc.sendPackets(new S_PacketBox(53, 8, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_2_1_N || skillId == COOKING_2_1_S) { // アリゲーターステーキ
			pc.addMaxHp(-30);
			pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // パーティー中
				pc.getParty().updateMiniHP(pc);
			}
			pc.addMaxMp(-30);
			pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_PacketBox(53, 9, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_2_2_N || skillId == COOKING_2_2_S) { // タートルドラゴンの菓子
			pc.addAc(2);
			pc.sendPackets(new S_PacketBox(53, 10, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_2_3_N || skillId == COOKING_2_3_S) { // キウィパロット焼き
			pc.sendPackets(new S_PacketBox(53, 11, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_2_4_N || skillId == COOKING_2_4_S) { // スコーピオン焼き
			pc.sendPackets(new S_PacketBox(53, 12, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_2_5_N || skillId == COOKING_2_5_S) { // イレッカドムシチュー
			pc.addMr(-10);
			pc.sendPackets(new S_SpMr(pc));
			pc.sendPackets(new S_PacketBox(53, 13, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_2_6_N || skillId == COOKING_2_6_S) { // クモ脚の串焼き
			pc.addSp(-1);
			pc.sendPackets(new S_SpMr(pc));
			pc.sendPackets(new S_PacketBox(53, 14, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_2_7_N || skillId == COOKING_2_7_S) { // クラブスープ
			pc.sendPackets(new S_PacketBox(53, 15, 0));
			pc.setDessertId(0);
		} else if (skillId == COOKING_3_0_N || skillId == COOKING_3_0_S) { // クラスタシアンのハサミ焼き
			pc.sendPackets(new S_PacketBox(53, 16, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_3_1_N || skillId == COOKING_3_1_S) { // グリフォン焼き
			pc.addMaxHp(-50);
			pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // パーティー中
				pc.getParty().updateMiniHP(pc);
			}
			pc.addMaxMp(-50);
			pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_PacketBox(53, 17, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_3_2_N || skillId == COOKING_3_2_S) { // コカトリスステーキ
			pc.sendPackets(new S_PacketBox(53, 18, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_3_3_N || skillId == COOKING_3_3_S) { // タートルドラゴン焼き
			pc.addAc(3);
			pc.sendPackets(new S_PacketBox(53, 19, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_3_4_N || skillId == COOKING_3_4_S) { // レッサードラゴンの手羽先
			pc.addMr(-15);
			pc.sendPackets(new S_SpMr(pc));
			pc.addWind(-10);
			pc.addWater(-10);
			pc.addFire(-10);
			pc.addEarth(-10);
			pc.sendPackets(new S_OwnCharAttrDef(pc));
			pc.sendPackets(new S_PacketBox(53, 20, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_3_5_N || skillId == COOKING_3_5_S) { // ドレイク焼き
			pc.addSp(-2);
			pc.sendPackets(new S_SpMr(pc));
			pc.sendPackets(new S_PacketBox(53, 21, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_3_6_N || skillId == COOKING_3_6_S) { // 深海魚のシチュー
			pc.addMaxHp(-30);
			pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // パーティー中
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_PacketBox(53, 22, 0));
			pc.setCookingId(0);
		} else if (skillId == COOKING_3_7_N || skillId == COOKING_3_7_S) { // バシリスクの卵スープ
			pc.sendPackets(new S_PacketBox(53, 23, 0));
			pc.setDessertId(0);
		} else if (skillId == ELIXIR_OF_IVORY_TOWER) { // 象牙の塔の妙薬
			pc.addHpr(-10);
			pc.addMpr(-2);
			pc.sendPackets(new S_PacketBox(53, 54, 0));
			pc.setDessertId(0);
		} else if (skillId == COOKING_4_1) { // 力強い和牛ステーキ
			pc.addMr(-10);
			pc.addWind(-10);
			pc.addWater(-10);
			pc.addFire(-10);
			pc.addEarth(-10);
			pc.sendPackets(new S_SpMr(pc));
			pc.sendPackets(new S_OwnCharAttrDef(pc));
			pc.sendPackets(new S_PacketBox(53, 54, 0)); // TODO アイコンが不明
			pc.setCookingId(0);
		} else if (skillId == COOKING_4_2) { // 素早い鮭の煮付
			pc.addMr(-10);
			pc.addWind(-10);
			pc.addWater(-10);
			pc.addFire(-10);
			pc.addEarth(-10);
			pc.sendPackets(new S_SpMr(pc));
			pc.sendPackets(new S_OwnCharAttrDef(pc));
			pc.sendPackets(new S_PacketBox(53, 54, 0)); // TODO アイコンが不明
			pc.setCookingId(0);
		} else if (skillId == COOKING_4_3) { // 賢い七面鳥焼き
			pc.addSp(-2);
			pc.addWind(-10);
			pc.addWater(-10);
			pc.addFire(-10);
			pc.addEarth(-10);
			pc.sendPackets(new S_SpMr(pc));
			pc.sendPackets(new S_OwnCharAttrDef(pc));
			pc.sendPackets(new S_PacketBox(53, 54, 0)); // TODO アイコンが不明
			pc.setCookingId(0);
		} else if (skillId == COOKING_4_4) { // 修練の鶏スープ
			pc.sendPackets(new S_PacketBox(53, 54, 0)); // TODO アイコンが不明
			pc.setCookingId(0);
		}
	}
}
