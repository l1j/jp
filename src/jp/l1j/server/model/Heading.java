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

package jp.l1j.server.model;

import jp.l1j.server.utils.IntRange;

/**
 * heading値の定数クラス 上下左右は座標を基準として表現したもので、ゲーム画面を基準としたものではない。
 * 
 */
public class Heading {
	/**
	 * 座標上で上方向を表す。X, Y-1
	 */
	public static final int UP = 0;
	/**
	 * 座標上で右上方向を表す。X+1, Y-1
	 */
	public static final int UP_RIGHT = 1;
	/**
	 * 座標上で右方向を表す。X+1, Y
	 */
	public static final int RIGHT = 2;
	/**
	 * 座標上で右下方向を表す。X+1, Y+1
	 */
	public static final int DOWN_RIGHT = 3;
	/**
	 * 座標上で下方向を表す。X+1, Y+1
	 */
	public static final int DOWN = 4;
	/**
	 * 座標上で左下方向を表す。X-1, Y+1
	 */
	public static final int DOWN_LEFT = 5;
	/**
	 * 座標上で左方向を表す。X-1, Y
	 */
	public static final int LEFT = 6;
	/**
	 * 座標上で左上方向を表す。X-1, Y-1
	 */
	public static final int UP_LEFT = 7;

	private static void ensureHeading(int heading) {
		if (!IntRange.includes(heading, 0, 7)) {
			throw new IllegalArgumentException();
		}
	}

	public static Integer rotateLeft(int heading) {
		ensureHeading(heading);
		heading--;
		return heading == -1 ? 7 : heading;

	}

	public static Integer rotateRight(int heading) {
		ensureHeading(heading);
		heading++;
		return heading == 8 ? 0 : heading;
	}
}
