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

package jp.l1j.server.datatables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.utils.StreamUtil;

public class BadNamesTable {
	private static Logger _log = Logger.getLogger(BadNamesTable.class.getName());

	private static BadNamesTable _instance;

	private ArrayList<String> _nameList = new ArrayList<String>();

	public static BadNamesTable getInstance() {
		if (_instance == null) {
			_instance = new BadNamesTable();
		}
		return _instance;
	}

	private BadNamesTable() {
		LineNumberReader lnr = null;
		try {
			File mobDataFile = new File("data/badnames.txt");
			lnr = new LineNumberReader(new BufferedReader(new FileReader(mobDataFile)));
			String line = null;
			while ((line = lnr.readLine()) != null) {
				if (line.trim().length() == 0 || line.startsWith("#")) {
					continue;
				}
				StringTokenizer st = new StringTokenizer(line, ";");
				_nameList.add(st.nextToken());
			}
			_log.fine("loaded bad names: " + _nameList.size() + " records");
		} catch (FileNotFoundException e) {
			_log.warning("badnames.txt is missing in data folder");
		} catch (Exception e) {
			_log.warning("error while loading bad names list : " + e);
		} finally {
			StreamUtil.close(lnr);
		}
	}

	public boolean isBadName(String name) {
		for (String badName : _nameList) {
			if (name.toLowerCase().contains(badName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public String[] getAllBadNames() {
		return _nameList.toArray(new String[_nameList.size()]);
	}
}
