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
import jp.l1j.server.datatables.MapTable;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1TeleportAmulet {

	private static Logger _log = Logger.getLogger(L1TeleportAmulet.class.getName());
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1TeleportAmulet> {
		@XmlElement(name = "Item")
		private List<L1TeleportAmulet> _list;

		public Iterator<L1TeleportAmulet> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "X")
		private int _x;
		
		private int getX() {
			return _x;
		}
		
		@XmlAttribute(name = "Y")
		private int _y;
		
		private int getY() {
			return _y;
		}
		
		@XmlAttribute(name = "MapId")
		private int _mapId;
		
		private int getMapId() {
			return _mapId;
		}
		
		@XmlAttribute(name = "Head")
		private int _head;
		
		private int getHead() {
			return _head;
		}
		
		@XmlAttribute(name = "X1")
		private int _x1;
		
		private int getX1() {
			return _x1;
		}
		
		@XmlAttribute(name = "Y1")
		private int _y1;
		
		private int getY1() {
			return _y1;
		}
		
		@XmlAttribute(name = "X2")
		private int _x2;
		
		private int getX2() {
			return _x2;
		}
		
		@XmlAttribute(name = "Y2")
		private int _y2;
		
		private int getY2() {
			return _y2;
		}
		
		@XmlAttribute(name = "CurMapId")
		private int _curMapId;
		
		private int getCurMapId() {
			return _curMapId;
		}
		
		@XmlAttribute(name = "ClassInitial")
		private String _classInitial;
		
		private String getClassInitial() {
			return _classInitial;
		}
				
	}

	private static final String _path = "./data/xml/Item/TeleportAmulet.xml";

	private static HashMap<Integer, L1TeleportAmulet> _dataMap = new HashMap<Integer, L1TeleportAmulet>();

	public static L1TeleportAmulet get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
	}

	@XmlAttribute(name = "X1")
	private int _x1;

	private int getX1() {
		return _x1;
	}

	@XmlAttribute(name = "X2")
	private int _x2;

	private int getX2() {
		return _x2;
	}

	@XmlAttribute(name = "Y1")
	private int _y1;

	private int getY1() {
		return _y1;
	}

	@XmlAttribute(name = "Y2")
	private int _y2;

	private int getY2() {
		return _y2;
	}

	@XmlAttribute(name = "CurMapId")
	private int _curMapId;

	private int getCurMapId() {
		return _curMapId;
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

	@XmlAttribute(name = "ClassInitial")
	private String _classInitial;

	private String getClassInitial() {
		return _classInitial;
	}

	@XmlAttribute(name = "NeedItemId")
	private int _needItemId;

	private int getNeedItemId() {
		return _needItemId;
	}

	@XmlElement(name = "Effect")
	private CopyOnWriteArrayList<Effect> _effects;

	private List<Effect> getEffects() {
		return _effects;
	}
	
	private boolean init() {
		if (ItemTable.getInstance().getTemplate(getItemId()) == null) {
			System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, getItemId()));
			// %s はアイテムリストに存在しません。
			return false;
		}
		for (Effect each : getEffects()) {
			if (MapTable.getInstance().locationname(each.getMapId()) == null) {
				System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, each.getMapId()));
				// %s はマップリストに存在しません。
				return false;
			}
		}
		return true;
	}
	
	private static void loadXml(HashMap<Integer, L1TeleportAmulet> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1TeleportAmulet.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1TeleportAmulet each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading teleport amulets...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1TeleportAmulet> dataMap = new HashMap<Integer, L1TeleportAmulet>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item) {
		if (getCurMapId() > 0 && pc.getMapId() != getCurMapId()) {
			return false;
		}
		if (getX1() > 0 && getX2() > 0
				&& (pc.getX() < getX1() || pc.getX() > getX2())) {
			return false;
		}
		if (getY1() > 0 && getY2() > 0
				&& (pc.getY() < getY1() || pc.getY() > getY2())) {
			return false;
		}
		if (getQuestId() > 0) {
			if (pc.getQuest().getStep(getQuestId()) < getQuestStep()) {
				return false;
			}
		}
		if (getClassInitial() != null) {
			if (getClassInitial().equalsIgnoreCase("P") && !pc.isCrown()) {
				return false;
			} else if (getClassInitial().equalsIgnoreCase("K") && !pc.isKnight()) {
				return false;
			} else if (getClassInitial().equalsIgnoreCase("E") && !pc.isElf()) {
				return false;
			} else if (getClassInitial().equalsIgnoreCase("W") && !pc.isWizard()) {
				return false;
			} else if (getClassInitial().equalsIgnoreCase("D") && !pc.isDarkelf()) {
				return false;
			} else if (getClassInitial().equalsIgnoreCase("R") && !pc.isDragonKnight()) {
				return false;
			} else if (getClassInitial().equalsIgnoreCase("I") && !pc.isIllusionist()) {
				return false;
			}
		}
		if (getNeedItemId() > 0
				&& !pc.getInventory().checkItem(getNeedItemId(), 1)) {
			return false;
		}
		
		Effect effect = null;
		for (Effect each : getEffects()) {
			if (each.getX1() > 0 && each.getX2() > 0
					&& pc.getX() < each.getX1()
					&& pc.getX() > each.getX2()) {
				continue;
			}
			if (each.getY1() > 0 && each.getY2() > 0
					&& pc.getY() < each.getY1()
					&& pc.getY() > each.getY2()) {
				continue;
			}
			if (each.getCurMapId() > 0
					&& pc.getMapId() != each.getCurMapId()) {
				continue;
			}
			if (each.getClassInitial() != null) {
				if (each.getClassInitial().equalsIgnoreCase("P") && !pc.isCrown()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("K") && !pc.isKnight()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("E") && !pc.isElf()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("W") && !pc.isWizard()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("D") && !pc.isDarkelf()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("R") && !pc.isDragonKnight()) {
					continue;
				} else if (each.getClassInitial().equalsIgnoreCase("I") && !pc.isIllusionist()) {
					continue;
				}
			}
			effect = each;
			break;
		}
		
		if (effect == null) {
			return false;
		}

		L1Teleport.teleport(pc, effect.getX(), effect.getY(),
				(short) effect.getMapId(), effect.getHead(), true);
		
		return true;
	}

}
