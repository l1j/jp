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
package jp.l1j.server.utils;

import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;

public class Dice {
	private static RandomGenerator _rnd = RandomGeneratorFactory.newRandom();
	private final int _faces;

	public Dice(int faces) {
		_faces = faces;
	}

	public int getFaces() {
		return _faces;
	}

	public int roll() {
		return _rnd.nextInt(_faces) + 1;
	}

	public int roll(int count) {
		int n = 0;
		for (int i = 0; i < count; i++) {
			n += roll();
		}
		return n;
	}
}
