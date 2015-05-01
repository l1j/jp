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

package jp.l1j.server.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;

public class Lists {
	private static RandomGenerator _rnd = RandomGeneratorFactory.newRandom();

	public static <E> ArrayList<E> newArrayList() {
		return new ArrayList<E>();
	}

	public static <E> ArrayList<E> newArrayList(Collection<? extends E> c) {
		return new ArrayList<E>(c);
	}

	public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
		return new CopyOnWriteArrayList<E>();
	}

	public static <E> E getRandomElement(List<E> list) {
		if (list.isEmpty()) {
			throw new IllegalArgumentException("list is empty");
		}
		return list.get(_rnd.nextInt(list.size()));
	}
}
