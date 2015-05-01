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
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.model.skill.L1BuffUtil;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1SpellItem {

	private static Logger _log = Logger.getLogger(L1SpellItem.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1SpellItem> {
		@XmlElement(name = "Item")
		private List<L1SpellItem> _list;

		public Iterator<L1SpellItem> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "SkillId")
		private int _skillId;

		public int getSkillId() {
			return _skillId;
		}
	}

	private static final String _path = "./data/xml/Item/SpellItem.xml";

	private static HashMap<Integer, L1SpellItem> _dataMap = new HashMap<Integer, L1SpellItem>();

	public static L1SpellItem get(int id) {
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
	
	private boolean init() {
		if (ItemTable.getInstance().getTemplate(getItemId()) == null) {
			System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, getItemId()));
			// %s はアイテムリストに存在しません。
			return false;
		}
		Effect effect = getEffect();
		if (SkillTable.getInstance().findBySkillId(effect.getSkillId()) == null) {
			System.out.println(String.format(I18N_DOES_NOT_EXIST_SKILL_LIST, effect.getSkillId()));
			// %s はスキルリストに存在しません。
			return false;
		}
		return true;
	}
	
	private static void loadXml(HashMap<Integer, L1SpellItem> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1SpellItem.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1SpellItem each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading spell items...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1SpellItem> dataMap = new HashMap<Integer, L1SpellItem>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item, int spellsc_objid,
			int spellsc_x, int spellsc_y) {

		int maxChargeCount = item.getItem().getMaxChargeCount();
		int chargeCount = item.getChargeCount();
		if (maxChargeCount > 0 && chargeCount <= 0) {
			// \f1何も起きませんでした。
			pc.sendPackets(new S_ServerMessage(79));
			return false;
		}
		
		if (spellsc_objid == pc.getId()
				&& item.getItem().getUseType() != 30) { // spell_buff
			pc.sendPackets(new S_ServerMessage(281));
			// \f1魔法が無効になりました。
			return false;
		}

		if (spellsc_objid == 0
				&& item.getItem().getUseType() != 0
				&& item.getItem().getUseType() != 26
				&& item.getItem().getUseType() != 27
				&& item.getItem().getUseType() != 39) {
			pc.sendPackets(new S_ServerMessage(281));
			return false;
			// ターゲットがいない場合にhandleCommandsを送るとぬるぽになるためここでreturn
			// handleCommandsのほうで判断＆処理すべき部分かもしれない
		}
		
		L1BuffUtil.cancelBarrier(pc); // アブソルート バリアの解除
		
		Effect effect = getEffect();

		L1SkillUse l1skilluse = new L1SkillUse();
		l1skilluse.handleCommands(pc, effect.getSkillId(), spellsc_objid,
				spellsc_x, spellsc_y, null, 0, L1SkillUse.TYPE_SPELLSC);
		
		if (getRemove() > 0) {
			if (chargeCount > 0) {
				item.setChargeCount(chargeCount - getRemove());
				pc.getInventory().updateItem(item,
						L1PcInventory.COL_CHARGE_COUNT);
			} else {
				pc.getInventory().removeItem(item, getRemove());
			}
		}	
		
		return true;
	}

}
