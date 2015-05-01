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

public class L1Alignment {
	private final int _value;

	private L1Alignment(int value) {
		_value = value;
	}

	public static L1Alignment NEUTRAL = new L1Alignment(0);
	public static L1Alignment LAWFUL = new L1Alignment(1);
	public static L1Alignment CHAOTIC = new L1Alignment(2);

	public static L1Alignment fromValue(int value) {
		if (value == 0) {
			return NEUTRAL;
		}
		if (value == 1) {
			return LAWFUL;
		}
		if (value == 2) {
			return CHAOTIC;
		}
		throw new IllegalArgumentException("value is invalid");
	}

	public int value() {
		return _value;
	}
}