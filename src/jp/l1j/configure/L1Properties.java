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

package jp.l1j.configure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class L1Properties {
	private static Logger _log = Logger.getLogger(L1Properties.class.getName());
	private final Properties _props = new Properties();

	private L1Properties() {
	}

	private L1Properties(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		if (file.getPath().endsWith(".xml")) {
			_props.loadFromXML(is);
		} else {
			_props.load(is);
		}
		is.close();
	}

	public static L1Properties load(File file) {
		_log.log(Level.FINE, "Loading properties file: {0}", file.getName());
		try {
			return new L1Properties(file);
		} catch (IOException e) {
			return new NullProperties(e);
		}
	}

	private void notifyLoadingDefault(String key, boolean allowDefaultValue) {
		if (!allowDefaultValue) {
			// デフォルト値が許可されていない。エラー
			throw new RuntimeException(key
					+ " does not exists. It has not default value.");
		}
		// デフォルト値をロードすることを通知
		_log.log(Level.INFO, String.format("%s does not exists. Server use default value.", key));
	}

	public String getProperty(String key, boolean allowDefaultValue) {
		if (!_props.containsKey(key)) {
			notifyLoadingDefault(key, allowDefaultValue);
			return null;
		}
		return _props.getProperty(key);
	}

	public boolean isNull() {
		return false;
	}

	public IOException getException() {
		throw new UnsupportedOperationException();
	}

	private static class NullProperties extends L1Properties {
		private IOException _e;

		public NullProperties(IOException e) {
			_e = e;
		}

		@Override
		public String getProperty(String key, boolean allowDefaultValue) {
			return null;
		}

		@Override
		public boolean isNull() {
			return true;
		}

		@Override
		public IOException getException() {
			return _e;
		}
	}
}
