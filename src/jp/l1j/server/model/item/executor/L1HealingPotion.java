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
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.model.skill.L1BuffUtil;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1HealingPotion {

	private static Logger _log = Logger.getLogger(L1HealingPotion.class.getName());
	
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1HealingPotion> {
		@XmlElement(name = "Item")
		private List<L1HealingPotion> _list;

		public Iterator<L1HealingPotion> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "Min")
		private int _min;
		
		private int getMin() {
			return _min;
		}		
		
		@XmlAttribute(name = "Max")
		private int _max;
		
		private int getMax() {
			return _max;
		}
				
		@XmlAttribute(name = "GfxId")
		private int _gfxid;
		
		private int getGfxId() {
			return _gfxid;
		}
				
		@XmlAttribute(name = "MapId")
		private int _mapid;
		
		private int getMapId() {
			return _mapid;
		}
	}

	private static final String _path = "./data/xml/Item/HealingPotion.xml";

	private static HashMap<Integer, L1HealingPotion> _dataMap = new HashMap<Integer, L1HealingPotion>();

	public static L1HealingPotion get(int id) {
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
	private CopyOnWriteArrayList<Effect> _effects;

	private List<Effect> getEffects() {
		return _effects;
	}

	private static void loadXml(HashMap<Integer, L1HealingPotion> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1HealingPotion.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1HealingPotion each : list) {
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
		System.out.println("loading healing potions...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1HealingPotion> dataMap = new HashMap<Integer, L1HealingPotion>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item) {
		if (pc.hasSkillEffect(71) == true) { // ディケイ ポーションの状態
			pc.sendPackets(new S_ServerMessage(698)); // 魔力によって何も飲むことができません。
			return false;
		}

		// アブソルート バリアの解除
		L1BuffUtil.cancelBarrier(pc);

		int maxChargeCount = item.getItem().getMaxChargeCount();
		int chargeCount = item.getChargeCount();
		if (maxChargeCount > 0 && chargeCount <= 0) {
			// \f1何も起きませんでした。
			pc.sendPackets(new S_ServerMessage(79));
			return false;
		}

		Effect effect = null;
		for (Effect each : getEffects()) {
			if (each.getMapId() != 0 && pc.getMapId() != each.getMapId()) {
				continue;
			}
			effect = each;
			break;
		}
		
		if (effect == null) {
			// \f1何も起きませんでした。
			pc.sendPackets(new S_ServerMessage(79));
			return false;
		}
		
		pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
		if (pc.getPotLog() == true) {// TODO POTLOG判定　
			pc.sendPackets(new S_ServerMessage(77)); // \f1気分が良くなりました。
		}
		int chance = effect.getMax() - effect.getMin();
		double healHp = effect.getMin();
		if (chance > 0) {
			healHp += _random.nextInt(chance) + 1;
		}

		healHp *= (double)pc.getPotionRecoveryRatePct() / 100 + 1;

		if (pc.hasSkillEffect(POLLUTE_WATER)) { // ポルートウォーター中は回復量1/2倍
			healHp /= 2;
		}
		if (pc.hasSkillEffect(FAFURION_DEATH_PORTION)) { // パプリオンデスポーション中はダメージに変換
			L1Character cha = null;
			pc.sendPackets(new S_ServerMessage(167)); // \f1肌がムズムズします。（仮で適当なものです）
			pc.setCurrentHp(pc.getCurrentHp() - (int)healHp);
			if (pc != null && pc.isInvisble()) {
				pc.delInvis();
			}
			if (cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_SkillSound(cha.getId(), 7781));
			}
		}
		pc.setCurrentHp(pc.getCurrentHp() + (int)healHp);
		
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
