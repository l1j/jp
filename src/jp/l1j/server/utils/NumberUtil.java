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
package jp.l1j.server.utils;

import jp.l1j.server.random.RandomGeneratorFactory;

public class NumberUtil {

	/**
	 * 少数を小数点第二位までの確率で上か下に丸めた整数を返す。 例えば1.3は30%の確率で切り捨て、70%の確率で切り上げられる。
	 * 
	 * @param number
	 *            - もとの少数
	 * @return 丸められた整数
	 */
	public static int randomRound(double number) {
		double percentage = (number - Math.floor(number)) * 100;

		if (percentage == 0) {
			return ((int) number);
		} else {
			int r = RandomGeneratorFactory.getSharedRandom().nextInt(100);
			if (r < percentage) {
				return ((int) number + 1);
			} else {
				return ((int) number);
			}
		}
	}
}
