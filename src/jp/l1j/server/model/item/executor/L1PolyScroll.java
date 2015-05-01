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
import jp.l1j.server.datatables.PolyTable;
import jp.l1j.server.model.L1PolyMorph;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1PolyScroll {

	private static Logger _log = Logger.getLogger(L1PolyScroll.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1PolyScroll> {
		@XmlElement(name = "Item")
		private List<L1PolyScroll> _list;

		public Iterator<L1PolyScroll> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "Time")
		private int _time;

		public int getTime() {
			return _time;
		}
	}

	private static final String _path = "./data/xml/Item/PolyScroll.xml";

	private static HashMap<Integer, L1PolyScroll> _dataMap = new HashMap<Integer, L1PolyScroll>();

	public static L1PolyScroll get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
	}

	@XmlAttribute(name = "Remove")
	private int _remove;

	private int getRemove() {
		return _remove;
	}

	@XmlElement(name = "Effect")
	private Effect _effect;

	private Effect getEffect() {
		return _effect;
	}

	private static void loadXml(HashMap<Integer, L1PolyScroll> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1PolyScroll.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1PolyScroll each : list) {
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
		System.out.println("loading poly scrolls...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}

	public static void reload() {
		HashMap<Integer, L1PolyScroll> dataMap = new HashMap<Integer, L1PolyScroll>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item, String s) {
		boolean result = false;

		int maxChargeCount = item.getItem().getMaxChargeCount();
		int chargeCount = item.getChargeCount();
		if (maxChargeCount > 0 && chargeCount <= 0) {
			// \f1何も起きませんでした。
			pc.sendPackets(new S_ServerMessage(79));
			return result;
		}

		Effect effect = getEffect();

		L1PolyMorph poly = PolyTable.getInstance().getTemplate(s);
		if (poly != null || s.equals("")) {
			if (s.equals("")) {
				if (pc.getTempCharGfx() == 6034 || pc.getTempCharGfx() == 6035) {
					result = true;
				} else {
					pc.removeSkillEffect(SHAPE_CHANGE);
					result = true;
				}
			} else if (poly.getMinLevel() <= pc.getLevel() || pc.isGm()) {
				L1PolyMorph.doPoly(pc, poly.getPolyId(), effect.getTime(),
						L1PolyMorph.MORPH_BY_ITEMMAGIC);
				result = true;
			}
		}

		if (result) {
			if (getRemove() > 0) {
				if (chargeCount > 0) {
					item.setChargeCount(chargeCount - getRemove());
					pc.getInventory().updateItem(item,
							L1PcInventory.COL_CHARGE_COUNT);
				} else {
					pc.getInventory().removeItem(item, getRemove());
				}
			}
		} else {
			pc.sendPackets(new S_ServerMessage(181));
			// \f1そのようなモンスターには変身できません。
		}

		return result;
	}

}
