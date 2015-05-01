/**
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

package jp.l1j.server.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.templates.L1ItemSetItem;
import jp.l1j.server.utils.IterableElementList;
import jp.l1j.server.utils.PerformanceTimer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GMCommandConfigs {
	private static Logger _log = Logger.getLogger(GMCommandConfigs.class.getName());

	private static GMCommandConfigs _instance;

	private static HashMap<String, ConfigLoader> _loaders = new HashMap<String, ConfigLoader>();

	public static HashMap<String, L1Location> _rooms = new HashMap<String, L1Location>();
	
	public static HashMap<String, List<L1ItemSetItem>> _itemSets = new HashMap<String, List<L1ItemSetItem>>();

	public static GMCommandConfigs getInstance() {
		if (_instance == null) {
			_instance = new GMCommandConfigs();
		}
		return _instance;
	}

	private GMCommandConfigs() {	
		_loaders.put("roomlist", new RoomLoader());
		_loaders.put("itemsetlist", new ItemSetLoader());
		loadCommands(_loaders);
	}

	private interface ConfigLoader {
		public void load(Element element);
	}

	private abstract class ListLoaderAdapter implements ConfigLoader {
		private final String _listName;

		public ListLoaderAdapter(String listName) {
			_listName = listName;
		}

		@Override
		public final void load(Element element) {
			NodeList nodes = element.getChildNodes();
			for (Element elem : new IterableElementList(nodes)) {
				if (elem.getNodeName().equalsIgnoreCase(_listName)) {
					loadElement(elem);
				}
			}
		}

		public abstract void loadElement(Element element);
	}

	private class RoomLoader extends ListLoaderAdapter {
		public RoomLoader() {
			super("Room");
		}

		@Override
		public void loadElement(Element element) {
			String name = element.getAttribute("Name");
			int locX = Integer.valueOf(element.getAttribute("LocX"));
			int locY = Integer.valueOf(element.getAttribute("LocY"));
			int mapId = Integer.valueOf(element.getAttribute("MapId"));
			_rooms.put(name.toLowerCase(), new L1Location(locX, locY, mapId));
		}
	}

	private class ItemSetLoader extends ListLoaderAdapter {
		public ItemSetLoader() {
			super("ItemSet");
		}

		public L1ItemSetItem loadItem(Element element) {
			int id = Integer.valueOf(element.getAttribute("Id"));
			int amount = Integer.valueOf(element.getAttribute("Amount"));
			int enchant = 0;
			String enchantValue = element.getAttribute("Enchant");
			if (!enchantValue.isEmpty()) {
				enchant = Integer.valueOf(enchantValue);
			}
			return new L1ItemSetItem(id, amount, enchant);
		}

		@Override
		public void loadElement(Element element) {
			List<L1ItemSetItem> list = new ArrayList<L1ItemSetItem>();
			NodeList nodes = element.getChildNodes();
			for (Element elem : new IterableElementList(nodes)) {
				if (elem.getNodeName().equalsIgnoreCase("Item")) {
					list.add(loadItem(elem));
				}
			}
			String name = element.getAttribute("Name");
			_itemSets.put(name.toLowerCase(), list);
		}
	}

	private static Document loadXml(String file)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return builder.parse(file);
	}

	public static void loadCommands(HashMap<String, ConfigLoader> loaders) {
		try {
			PerformanceTimer timer = new PerformanceTimer();
			Document doc = loadXml("./data/xml/GmCommands/GMCommands.xml");
			NodeList nodes = doc.getDocumentElement().getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				ConfigLoader loader = loaders.get(nodes.item(i).getNodeName().toLowerCase());
				if (loader != null) {
					loader.load((Element) nodes.item(i));
				}
			}
			System.out.println("loading gm commands...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (Exception e) {
			_log.log(Level.SEVERE, String.format(I18N_LOAD_FAILED, "GMCommands.xml"), e);
			// %s の読み込みに失敗しました。
			System.out.println("loading gm commands...NG!");
		}
	}

	public void reload() {
		HashMap<String, ConfigLoader> loaders = new HashMap<String, ConfigLoader>();
		_rooms.clear();
		_itemSets.clear();
		loaders.put("roomlist", _instance.new RoomLoader());
		loaders.put("itemsetlist", _instance.new ItemSetLoader());
		loadCommands(loaders);
		_loaders = loaders;
	}
	
	public HashMap<String, List<L1ItemSetItem>> getItemSets() {
		return _itemSets;
	}
	
	public HashMap<String, L1Location> getRooms() {
		return _rooms;
	}
}
