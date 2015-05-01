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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class XMLResourceBundleControl extends ResourceBundle.Control {
	
	public List<String> getFormats(String baseName) {
		if (baseName == null) {
			throw new NullPointerException();
		}
		return Arrays.asList("xml");
	}
	
	public ResourceBundle newBundle(String baseName, Locale locale,
			String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
		if (baseName == null || locale == null || format == null || loader == null) {
			throw new NullPointerException();
		}
		ResourceBundle bundle = null;
		if (format.equals("xml")) {
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, format);
			
			/*
			InputStream stream = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection connection = url.openConnection();
					if (connection != null) {
						// キャッシュを無効にすることで、再ロード用の最新データを
						// 取得できるようにします。
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			*/
					
			// TODO start
			File file = new File(resourceName.replace("//", "./"));
			InputStream stream = new FileInputStream(file);
			// TODO end
			
			if (stream != null) {
				BufferedInputStream bis = new BufferedInputStream(stream);
				bundle = new XMLResourceBundle(bis);
				bis.close();
			}
		}
		return bundle;
	}

}
