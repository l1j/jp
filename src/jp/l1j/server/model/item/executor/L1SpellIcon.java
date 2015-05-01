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
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.skill.L1BuffUtil;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1Skill;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1SpellIcon {

	private static Logger _log = Logger.getLogger(L1SpellIcon.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1SpellIcon> {
		@XmlElement(name = "Item")
		private List<L1SpellIcon> _list;

		public Iterator<L1SpellIcon> iterator() {
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

	private static final String _path = "./data/xml/Item/SpellIcon.xml";

	private static HashMap<Integer, L1SpellIcon> _dataMap = new HashMap<Integer, L1SpellIcon>();

	public static L1SpellIcon get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
	}

	@XmlAttribute(name = "Consume")
	private boolean _consume;

	private boolean getConsume() {
		return _consume;
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
		// ストームウォークの暫定対応（本来は、skillsテーブルにストームウォークを登録するべき）
		//if (SkillTable.getInstance().findBySkillId(effect.getSkillId()) == null) {
		if (effect.getSkillId() > 0 && SkillTable.getInstance().findBySkillId(effect.getSkillId()) == null) {
			System.out.println(String.format(I18N_DOES_NOT_EXIST_SKILL_LIST, effect.getSkillId()));
			// %s はアイテムリストに存在しません。
			return false;
		}
		return true;
	}
	
	private static void loadXml(HashMap<Integer, L1SpellIcon> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1SpellIcon.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1SpellIcon each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading spell icons...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1SpellIcon> dataMap = new HashMap<Integer, L1SpellIcon>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item, int spellsc_objid,
			int spellsc_x, int spellsc_y) {
	
		L1BuffUtil.cancelBarrier(pc); // アブソルート バリアの解除
		
		Effect effect = getEffect();

		// ストームウォークの暫定対応（本来は、skillsテーブルにストームウォークを登録するべき）
		if (getItemId() == 42501) { // ストームウォーク
			if (getConsume()) {
				if (pc.getCurrentMp() < 10) {
					pc.sendPackets(new S_ServerMessage(278));
					// \f1MPが不足していて魔法を使うことができません。
					return false;
				}
				pc.setCurrentMp(pc.getCurrentMp() - 10);
			}
			L1Teleport.teleport(pc, spellsc_x, spellsc_y, pc.getMapId(),
						pc.getHeading(), true, L1Teleport.CHANGE_POSITION);
		} else {
			L1SkillUse l1skilluse = new L1SkillUse();
			if (getConsume()) {
				l1skilluse.handleCommands(pc, effect.getSkillId(),
						spellsc_objid, spellsc_x, spellsc_y, null, 0, L1SkillUse.TYPE_NORMAL);
			} else {
				l1skilluse.handleCommands(pc, effect.getSkillId(),
						spellsc_objid, spellsc_x, spellsc_y, null, 0, L1SkillUse.TYPE_SPELLSC);
			}
		}
				
		return true;
	}

	public String getAppendName() {
		Effect effect = getEffect();
		if (effect.getSkillId() <= 0) {
			return null;
		}
		L1Skill skill = SkillTable.getInstance().findBySkillId(effect.getSkillId());
		String name = String.valueOf(skill.getConsumeMp()) + "/" + String.valueOf(skill.getConsumeHp());
		if (skill.getConsumeAmount() > 0) {
			name = name + "/" + String.valueOf(skill.getConsumeAmount());
		}
		return " (" + name + ")";
	}
	
}
