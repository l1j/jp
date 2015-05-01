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
package jp.l1j.server.model;

import static jp.l1j.server.model.skill.L1SkillId.*;

import java.util.List;
import java.util.Map;

import jp.l1j.server.datatables.CookingRecipeTable;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.instance.L1EffectInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.skill.executor.L1BuffSkillExecutor;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.templates.L1CookingIngredient;
import jp.l1j.server.templates.L1CookingRecipe;
import jp.l1j.server.templates.L1Skill;
import jp.l1j.server.utils.IntRange;
import jp.l1j.server.utils.L1ItemUtil;
import jp.l1j.server.utils.collections.Maps;

// Referenced classes of package jp.l1j.server.model:
// L1Cooking

public class L1Cooking {
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	private static Map<Integer, Integer> _itemIdToCookingIdMap = Maps
			.newHashMap();

	private static void putToMap(int itemId, int CookingId) {
		_itemIdToCookingIdMap.put(itemId, CookingId);
	}

	static {
		// フローティングアイステーキ
		putToMap(41277, COOKING_1_0_N);
		putToMap(41285, COOKING_1_0_S);
		// ベアーステーキ
		putToMap(41278, COOKING_1_1_N);
		putToMap(41286, COOKING_1_1_S);
		// ナッツ餅
		putToMap(41279, COOKING_1_2_N);
		putToMap(41287, COOKING_1_2_S);
		// 蟻脚のチーズ焼き
		putToMap(41280, COOKING_1_3_N);
		putToMap(41288, COOKING_1_3_S);
		// フルーツサラダ
		putToMap(41281, COOKING_1_4_N);
		putToMap(41289, COOKING_1_4_S);
		// フルーツ甘酢あんかけ
		putToMap(41282, COOKING_1_5_N);
		putToMap(41290, COOKING_1_5_S);
		// 猪肉の串焼き
		putToMap(41283, COOKING_1_6_N);
		putToMap(41291, COOKING_1_6_S);
		// キノコスープ
		putToMap(41284, COOKING_1_7_N);
		putToMap(41292, COOKING_1_7_S);
		// キャビアカナッペ
		putToMap(49049, COOKING_2_0_N);
		putToMap(49057, COOKING_2_0_S);
		// アリゲーターステーキ
		putToMap(49050, COOKING_2_1_N);
		putToMap(49058, COOKING_2_1_S);
		// タートルドラゴンの菓子
		putToMap(49051, COOKING_2_2_N);
		putToMap(49059, COOKING_2_2_S);
		// キウィパロット焼き
		putToMap(49052, COOKING_2_3_N);
		putToMap(49060, COOKING_2_3_S);
		// スコーピオン焼き
		putToMap(49053, COOKING_2_4_N);
		putToMap(49061, COOKING_2_4_S);
		// イレッカドムシチュー
		putToMap(49054, COOKING_2_5_N);
		putToMap(49062, COOKING_2_5_S);
		// クモ脚の串焼き
		putToMap(49055, COOKING_2_6_N);
		putToMap(49063, COOKING_2_6_S);
		// クラブスープ
		putToMap(49056, COOKING_2_7_N);
		putToMap(49064, COOKING_2_7_S);
		// クラスタシアンのハサミ焼き
		putToMap(49244, COOKING_3_0_N);
		putToMap(49252, COOKING_3_0_S);
		// グリフォン焼き
		putToMap(49245, COOKING_3_1_N);
		putToMap(49253, COOKING_3_1_S);
		// コカトリスステーキ
		putToMap(49246, COOKING_3_2_N);
		putToMap(49254, COOKING_3_2_S);
		// タートルドラゴン焼き
		putToMap(49247, COOKING_3_3_N);
		putToMap(49255, COOKING_3_3_S);
		// レッサードラゴンの手羽先
		putToMap(49248, COOKING_3_4_N);
		putToMap(49256, COOKING_3_4_S);
		// ドレイク焼き
		putToMap(49249, COOKING_3_5_N);
		putToMap(49257, COOKING_3_5_S);
		// 深海魚のシチュー
		putToMap(49250, COOKING_3_6_N);
		putToMap(49258, COOKING_3_6_S);
		// バシリスクの卵スープ
		putToMap(49251, COOKING_3_7_N);
		putToMap(49259, COOKING_3_7_S);
		// 象牙の塔の妙薬
		putToMap(50543, ELIXIR_OF_IVORY_TOWER);
		// 力強い和牛ステーキ
		putToMap(50641, COOKING_4_1);
		// 素早い鮭の煮付
		putToMap(50642, COOKING_4_2);
		// 賢い七面鳥焼き
		putToMap(50643, COOKING_4_3);
		// 修練の鶏スープ
		putToMap(50644, COOKING_4_4);
	}

	private L1Cooking() {
	}

	public static int itemIdToCookingId(int itemId) {
		Integer result = _itemIdToCookingIdMap.get(itemId);
		if (result == null) {
			return 0;
		}
		return result;
	}

	public static void useCookingItem(L1PcInstance pc, L1ItemInstance item) {
		int itemId = item.getItem().getItemId();
		if (isDessertItem(itemId) && pc.getFood() != 225) {
			pc.sendPackets(new S_ServerMessage(L1MessageId.CANNOT_BE_USED, item
					.getNumberedName(1)));
			return;
		}

		if (itemId >= 41277 && itemId <= 41283 // Lv1料理
				|| itemId >= 41285 && itemId <= 41291 // Lv1幻想の料理
				|| itemId >= 49049 && itemId <= 49055 // Lv2料理
				|| itemId >= 49057 && itemId <= 49063 // Lv2幻想の料理
				|| itemId >= 49244 && itemId <= 49250 // Lv3料理
				|| itemId >= 49252 && itemId <= 49258 // Lv3幻想の料理
				|| itemId >= 50641 && itemId <= 50644 // 新料理
				|| itemId == 50543) { // 象牙の塔の妙薬
			int cookingId = pc.getCookingId();
			if (cookingId != 0) {
				pc.removeSkillEffect(cookingId);
			}
		}

		if (isDessertItem(itemId)) {
			int dessertId = pc.getDessertId();
			if (dessertId != 0) {
				pc.removeSkillEffect(dessertId);
			}
		}

		int cookingId = itemIdToCookingId(itemId);
		if (cookingId != 0) {
			eatCooking(pc, cookingId);
		}
		pc.sendPackets(new S_ServerMessage(L1MessageId.YOU_ATE, item
				.getNumberedName(1)));
		pc.getInventory().removeItem(item, 1);
	}

	private static boolean isDessertItem(int itemId) {
		return itemId == 41284 || itemId == 41292 || itemId == 49056
				|| itemId == 49064 || itemId == 49251 || itemId == 49259
				|| itemId == 50543;
	}

	public static void eatCooking(L1PcInstance pc, int cookingId) {
		L1Skill skill = SkillTable.getInstance().findBySkillId(cookingId);
		if (skill == null) {
			return;
		}
		L1BuffSkillExecutor exe = skill.newBuffSkillExecutor();
		if (exe == null) {
			return;
		}

		int durationSeconds = skill.getBuffDuration();
		exe.addEffect(null, pc, durationSeconds);
		pc.setSkillEffect(cookingId, durationSeconds * 1000);
	}

	/**
	 * <code>pc</code>のそばに焚き火があるか。
	 * 
	 * @return true - 焚き火があった。<br>
	 *         false - 焚き火が無かった。
	 */
	private static boolean isNearFire(L1PcInstance pc) {
		for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 3)) {
			if (obj instanceof L1EffectInstance) {
				L1EffectInstance effect = (L1EffectInstance) obj;
				if (effect.getGfxId() == 5943) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 必要な材料を所有しているか調べる。
	 * 
	 * @return true - 必要な量の材料を所有している<br>
	 *         false - 必要な材料が不足している
	 */
	private static boolean hasEnoughIngredients(L1PcInstance pc,
			List<L1CookingIngredient> ingredients) {
		for (L1CookingIngredient ing : ingredients) {
			if (!pc.getInventory().checkItem(ing.getItemId(), ing.getAmount())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 材料を消費する。
	 */
	private static void consumeIngredients(L1PcInstance pc,
			List<L1CookingIngredient> ingredients) {
		for (L1CookingIngredient ing : ingredients) {
			pc.getInventory().consumeItem(ing.getItemId(), ing.getAmount());
		}
	}

	public static void makeCooking(L1PcInstance pc, int cookNo) {
		if (!isNearFire(pc)) {
			pc.sendPackets(new S_ServerMessage(L1MessageId.NEEDS_FIRE_TO_COOK));
			return;
		}
		if (pc.getMaxWeight() <= pc.getInventory().getWeight()) {
			pc.sendPackets(new S_ServerMessage(
					L1MessageId.ITEMS_TOO_HEAVY_TO_COOK));
			return;
		}
		if (pc.hasSkillEffect(COOKING_NOW)) {
			return;
		}
		pc.setSkillEffect(COOKING_NOW, 3 * 1000);

		L1CookingRecipe recipe = CookingRecipeTable.findById(cookNo);
		List<L1CookingIngredient> ingredients = recipe.getIngredients();
		if (!hasEnoughIngredients(pc, ingredients)) {
			pc.sendPackets(new S_ServerMessage(
					L1MessageId.NOT_ENOUGH_INGREDIENTS));
			return;
		}
		consumeIngredients(pc, ingredients);

		int chance = _random.nextInt(100) + 1;

		IntRange successRange = new IntRange(1, 90);
		if (successRange.includes(chance)) { // 成功
			succeed(pc, recipe);
			return;
		}

		IntRange fantasyRange = new IntRange(91, 95);
		if (fantasyRange.includes(chance)) { // 大成功(幻想料理)
			succeedFantastic(pc, recipe);
			return;
		}

		IntRange failureRange = new IntRange(96, 100);
		if (failureRange.includes(chance)) { // 失敗
			fail(pc);
			return;
		}

		throw new RuntimeException("Logic error."); // ありえない
	}

	private static void succeed(L1PcInstance pc, L1CookingRecipe recipe) {
		L1ItemUtil
				.createNewItem(pc, recipe.getDishId(), recipe.getDishAmount());
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
	}

	private static void succeedFantastic(L1PcInstance pc, L1CookingRecipe recipe) {
		L1ItemUtil.createNewItem(pc, recipe.getFantasyDishId(), recipe
				.getFantasyDishAmount());
		pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
	}

	private static void fail(L1PcInstance pc) {
		pc.sendPackets(new S_ServerMessage(L1MessageId.YOU_FAILED_COOKING));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
	}
}
