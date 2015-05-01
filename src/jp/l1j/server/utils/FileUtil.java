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

import java.io.File;

public class FileUtil {
	public static String getExtension(File file) {
		String fileName = file.getName();
		int index = fileName.lastIndexOf('.');
		if (index != -1) {
			return fileName.substring(index + 1, fileName.length());
		}
		return "";
	}

	public static String getNameWithoutExtension(File file) {
		String fileName = file.getName();
		int index = fileName.lastIndexOf('.');
		if (index != -1) {
			return fileName.substring(0, index);
		}
		return "";
	}
}
