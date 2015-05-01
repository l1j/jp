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

package jp.l1j.server.templates;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import jp.l1j.server.utils.L1QueryUtil;
import jp.l1j.server.utils.L1QueryUtil.EntityFactory;

public class L1CookingIngredient {
	private final int _id;
	private final int _cookingRecipeId;
	private final int _itemId;
	private final int _amount;

	private L1CookingIngredient(int id, int cookingRecipeId, int itemId,
			int amount) {
		super();
		this._id = id;
		this._cookingRecipeId = cookingRecipeId;
		this._itemId = itemId;
		this._amount = amount;
	}

	private static class Factory implements EntityFactory<L1CookingIngredient> {
		@Override
		public L1CookingIngredient fromResultSet(ResultSet rs)
				throws SQLException {
			int id = rs.getInt("id");
			int cookingRecipeId = rs.getInt("cooking_recipe_id");
			int itemId = rs.getInt("item_id");
			int amount = rs.getInt("amount");
			return new L1CookingIngredient(id, cookingRecipeId, itemId, amount);
		}
	}

	public int getId() {
		return _id;
	}

	public int getCookingRecipeId() {
		return _cookingRecipeId;
	}

	public int getItemId() {
		return _itemId;
	}

	public int getAmount() {
		return _amount;
	}

	public static List<L1CookingIngredient> findByCookingRecipeId(
			int cookingRecipeId) {
		return L1QueryUtil
				.selectAll(
						new Factory(),
						"SELECT * FROM cooking_ingredients WHERE cooking_recipe_id = ?",
						cookingRecipeId);
	}
}
