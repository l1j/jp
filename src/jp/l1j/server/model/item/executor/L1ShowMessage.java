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
package jp.l1j.server.model.item.executor;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_NpcTalkReturn;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1ShowMessage {

	private static Logger _log = Logger.getLogger(L1ShowMessage.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1ShowMessage> {
		@XmlElement(name = "Item")
		private List<L1ShowMessage> _list;

		public Iterator<L1ShowMessage> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "HtmlId")
		private String _htmlId;

		public String getHtmlId() {
			return _htmlId;
		}

		@XmlAttribute(name = "QuestId")
		private int _questId = 0;

		public int getQuestId() {
			return _questId;
		}

		@XmlAttribute(name = "QuestStep")
		private int _questStep = 0;

		public int getQuestStep() {
			return _questStep;
		}
	}

	private static final String _path = "./data/xml/Item/ShowMessage.xml";

	private static HashMap<Integer, L1ShowMessage> _dataMap = new HashMap<Integer, L1ShowMessage>();

	public static L1ShowMessage get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
	}

	@XmlElement(name = "Effect")
	private CopyOnWriteArrayList<Effect> _effects;

	private List<Effect> getEffects() {
		return _effects;
	}

	private static void loadXml(HashMap<Integer, L1ShowMessage> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1ShowMessage.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1ShowMessage each : list) {
				if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, each.getItemId()));
					// %s はアイテムリストに存在しません。
				} else {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading show messages...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1ShowMessage> dataMap = new HashMap<Integer, L1ShowMessage>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc) {

		Effect effect = null;
		for (Effect each : getEffects()) {
			if (each.getQuestId() > 0) {
				if (pc.getQuest().getStep(each.getQuestId()) != each.getQuestStep()) {
					continue;
				}
			}
			effect = each;
			break;
		}
		
		if (effect == null) {
			return false;
		}
		pc.sendPackets(new S_NpcTalkReturn(pc.getId(), effect.getHtmlId()));
		
		return true;
	}

}
