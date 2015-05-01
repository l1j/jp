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

import java.util.List;

public class StringUtil {
	public static String[] newArray(Object... obj) {
		String[] result = new String[obj.length];
		for (int i = 0; i < obj.length; i++) {
			result[i] = obj[i].toString();
		}
		return result;
	}

	public static String complementClassName(String className,
			String defaultPackage) {
		// .が含まれていればフルパスと見なしてそのまま返す
		if (className.contains(".")) {
			return className;
		}

		// デフォルトパッケージ名を補完
		return defaultPackage + "." + className;
	}

	public static <T> String join(List<T> list, String with) {
		StringBuffer buf = new StringBuffer();
		for (T s : list) {
			if (buf.length() > 0) {
				buf.append(with);
			}
			buf.append(s);
		}
		return buf.toString();
	}
}
