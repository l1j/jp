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

package jp.l1j.server.random;

public class RandomGeneratorFactory {
	private static RandomGenerator _rnd = null;

	/**
	 * サーバー共用の乱数ジェネレータを返します。少量の乱数生成であればこちらを推奨します。
	 * このメソッド及びメソッドが返す乱数ジェネレータはスレッドセーフであることが保障されており、外部同期の必要はありません。
	 * 但し、複数のスレッドから多量の乱数を同時に生成した場合のパフォーマンスは乱数ジェネレータの実装に依存します。
	 * 
	 * @return サーバー共用の乱数ジェネレータ
	 */
	public static RandomGenerator getSharedRandom() {
		if (_rnd == null) {
			_rnd = newRandom();
		}
		return _rnd;
	}

	/**
	 * 新しい乱数ジェネレータを返します。 このメソッド及びメソッドが返す乱数ジェネレータはスレッドセーフです。
	 * 
	 * @return 新しい乱数ジェネレータ
	 */
	public static RandomGenerator newRandom() {
		return new StandardRandom();
	}

}
