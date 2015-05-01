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

package jp.l1j.locale;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class XMLResourceBundle extends ResourceBundle {
	private Properties properties;
	
	public XMLResourceBundle(InputStream stream) throws IOException {
		// Propertiesクラスを使用してXMLファイルを読み込む
		properties = new Properties();
		properties.loadFromXML(stream);
	}
	
	public Object handleGetObject(String key) {
		if (key == null) {
			throw new NullPointerException();
		}
		return properties.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public Enumeration<String> getKeys() {
		return (Enumeration<String>)properties.propertyNames();
	}
}
