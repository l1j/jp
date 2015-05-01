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
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1Skill;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1BlankScroll {

	private static Logger _log = Logger.getLogger(L1BlankScroll.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1BlankScroll> {
		@XmlElement(name = "Item")
		private List<L1BlankScroll> _list;

		public Iterator<L1BlankScroll> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "ItemId")
		private int _itemId;

		public int getItemId() {
			return _itemId;
		}

		@XmlAttribute(name = "SkillId")
		private int _skillId;

		public int getSkillId() {
			return _skillId;
		}
	}

	private static final String _path = "./data/xml/Item/BlankScroll.xml";

	private static HashMap<Integer, L1BlankScroll> _dataMap = new HashMap<Integer, L1BlankScroll>();

	public static L1BlankScroll get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
	}

	@XmlAttribute(name = "ClassInitial")
	private String _classInitial;

	private String getClassInitial() {
		return _classInitial;
	}

	@XmlAttribute(name = "Remove")
	private int _remove;

	private int getRemove() {
		return _remove;
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
			if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
				System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, each.getItemId()));
				// %s はアイテムリストに存在しません。
				return false;
			}
			if (SkillTable.getInstance().findBySkillId(each.getSkillId()) == null) {
				System.out.println(String.format(I18N_DOES_NOT_EXIST_SKILL_LIST, each.getSkillId()));
				// %s はスキルリストに存在しません。
				return false;
			}
		}
		return true;
	}
	
	private static void loadXml(HashMap<Integer, L1BlankScroll> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1BlankScroll.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1BlankScroll each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading blank scrolls...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1BlankScroll> dataMap = new HashMap<Integer, L1BlankScroll>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item, int skillId) {
		
		int maxChargeCount = item.getItem().getMaxChargeCount();
		int chargeCount = item.getChargeCount();
		if (maxChargeCount > 0 && chargeCount <= 0) {
			// \f1何も起きませんでした。
			pc.sendPackets(new S_ServerMessage(79));
			return false;
		}
		
		// 使用可能クラスの判定
		if (getClassInitial().equalsIgnoreCase("P") && !pc.isCrown()) {
			pc.sendPackets(new S_ServerMessage(264));
			// \f1あなたのクラスではこのアイテムは使用できません。
			return false;
		} else if (getClassInitial().equalsIgnoreCase("K") && !pc.isKnight()) {
			pc.sendPackets(new S_ServerMessage(264));
			// \f1あなたのクラスではこのアイテムは使用できません。
			return false;
		} else if (getClassInitial().equalsIgnoreCase("E") && !pc.isElf()) {
			pc.sendPackets(new S_ServerMessage(264));
			// \f1あなたのクラスではこのアイテムは使用できません。
			return false;
		} else if (getClassInitial().equalsIgnoreCase("W") && !pc.isWizard()) {
			pc.sendPackets(new S_ServerMessage(264));
			// \f1あなたのクラスではこのアイテムは使用できません。
			return false;
		} else if (getClassInitial().equalsIgnoreCase("D") && !pc.isDarkelf()) {
			pc.sendPackets(new S_ServerMessage(264));
			// \f1あなたのクラスではこのアイテムは使用できません。
			return false;
		} else if (getClassInitial().equalsIgnoreCase("R") && !pc.isDragonKnight()) {
			pc.sendPackets(new S_ServerMessage(264));
			// \f1あなたのクラスではこのアイテムは使用できません。
			return false;
		} else if (getClassInitial().equalsIgnoreCase("I") && !pc.isIllusionist()) {
			pc.sendPackets(new S_ServerMessage(264));
			// \f1あなたのクラスではこのアイテムは使用できません。
			return false;
		}
		
		Effect effect = null;
		for (Effect each : getEffects()) {
			if (skillId > each.getSkillId() - 1) {
				continue;
			}
			effect = each;
			break;
		}
		
		if (effect == null) {
			pc.sendPackets(new S_ServerMessage(591));
			// \f1スクロールがそんな強い魔法を記録するにはあまりに弱いです。
			return false;
		}
		
		// スクロール(Lv5)でレベル5以下の魔法
		L1ItemInstance spellsc = ItemTable.getInstance().createItem(effect.getItemId());
		if (spellsc != null) {
			if (pc.getInventory().checkAddItem(spellsc, 1) == L1Inventory.OK) {
				L1Skill skill = SkillTable.getInstance().findBySkillId(effect.getSkillId());
				if (pc.getCurrentHp() + 1 < skill.getConsumeHp() + 1) {
					pc.sendPackets(new S_ServerMessage(279));
					// \f1HPが不足していて魔法を使うことができません。
					return false;
				}
				if (pc.getCurrentMp() < skill.getConsumeMp()) {
					pc.sendPackets(new S_ServerMessage(278));
					// \f1MPが不足していて魔法を使うことができません。
					return false;
				}
				if (skill.getConsumeItemId() != 0) { // 材料が必要
					if (!pc.getInventory().checkItem(skill.getConsumeItemId(),
							skill.getConsumeAmount())) { // 必要材料をチェック
						pc.sendPackets(new S_ServerMessage(299));
						// \f1魔法を詠唱するための材料が足りません。
						return false;
					}
				}
				pc.setCurrentHp(pc.getCurrentHp() - skill.getConsumeHp());
				pc.setCurrentMp(pc.getCurrentMp() - skill.getConsumeMp());
				int lawful = pc.getLawful() + skill.getLawful();
				if (lawful > 32767) {
					lawful = 32767;
				}
				if (lawful < -32767) {
					lawful = -32767;
				}
				pc.setLawful(lawful);
				if (skill.getConsumeItemId() != 0) { // 材料が必要
					pc.getInventory().consumeItem(skill.getConsumeItemId(),
							skill.getConsumeAmount());
				}
				pc.getInventory().storeItem(spellsc);
			}
		} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
		}
		
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
