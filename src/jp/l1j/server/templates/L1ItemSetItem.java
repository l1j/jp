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

public class L1ItemSetItem {
	private final int id;
	private final int amount;
	private final int enchant;

	public L1ItemSetItem(int id, int amount, int enchant) {
		super();
		this.id = id;
		this.amount = amount;
		this.enchant = enchant;
	}

	public int getId() {
		return id;
	}

	public int getAmount() {
		return amount;
	}

	public int getEnchant() {
		return enchant;
	}
}