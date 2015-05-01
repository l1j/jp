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

package jp.l1j.server.model.item;

public class L1ItemRate {
	private final int _itemId;
	private final double _sellingPrice;
	private final double _purchasingPrice;

	public L1ItemRate(int itemId, double sellingPrice, double purchasingPrice) {
		_itemId = itemId;
		_sellingPrice = sellingPrice;
		_purchasingPrice = purchasingPrice;
	}

	public int getItemId() {
		return _itemId;
	}

	public double getSellingPrice() {
		return _sellingPrice;
	}

	public double getPurchasingPrice() {
		return _purchasingPrice;
	}

}
