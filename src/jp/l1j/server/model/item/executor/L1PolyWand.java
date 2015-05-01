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
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.datatables.PolyTable;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1PolyMorph;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.model.skill.L1BuffUtil;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_AttackPacket;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_ShowPolyList;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Skill;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1PolyWand {

	private static Logger _log = Logger.getLogger(L1PolyWand.class.getName());

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1PolyWand> {
		@XmlElement(name = "Item")
		private List<L1PolyWand> _list;

		public Iterator<L1PolyWand> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "PolyId")
		private int _polyId;

		public int getPolyId() {
			return _polyId;
		}

		@XmlAttribute(name = "Chance")
		private int _chance;

		public int getChance() {
			return _chance;
		}
	}

	private static final String _path = "./data/xml/Item/PolyWand.xml";

	private static HashMap<Integer, L1PolyWand> _dataMap = new HashMap<Integer, L1PolyWand>();

	public static L1PolyWand get(int id) {
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

	private int _totalChance;

	private int getTotalChance() {
		return _totalChance;
	}

	private boolean init() {
		if (ItemTable.getInstance().getTemplate(getItemId()) == null) {
			System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, getItemId()));
			// %s はアイテムリストに存在しません。
			return false;
		}
		for (Effect each : getEffects()) {
			_totalChance += each.getChance();
			if (PolyTable.getInstance().getTemplate(each.getPolyId()) == null) {
				System.out.println(String.format(I18N_DOES_NOT_EXIST_POLY_LIST, each.getPolyId()));
				// %s は変身リストに存在しません。
				return false;
			}
		}
//		if (getTotalChance() != 0 && getTotalChance() != 100) {
//			System.out.println(String.format(I18N_PROBABILITIES_ERROR, getItemId()));
//			// %s の確率が100%ではありません。
//			return false;
//		}
		return true;
	}

	private static void loadXml(HashMap<Integer, L1PolyWand> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1PolyWand.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1PolyWand each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading poly wands...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}

	public static void reload() {
		HashMap<Integer, L1PolyWand> dataMap = new HashMap<Integer, L1PolyWand>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item, int objid) {
		// アブソルート バリアの解除
		L1BuffUtil.cancelBarrier(pc);

		if (pc.getMapId() == 63 || pc.getMapId() == 552
					|| pc.getMapId() == 555 || pc.getMapId() == 557
					|| pc.getMapId() == 558 || pc.getMapId() == 779) { // 水中では使用不可
			pc.sendPackets(new S_ServerMessage(563)); // \f1ここでは使えません。
			return false;
		}

		int maxChargeCount = item.getItem().getMaxChargeCount();
		int chargeCount = item.getChargeCount();
		if (maxChargeCount > 0 && chargeCount <= 0
				|| pc.getTempCharGfx() == 6034
				|| pc.getTempCharGfx() == 6035) {
			// \f1何も起きませんでした。
			pc.sendPackets(new S_ServerMessage(79));
			return false;
		}

		L1Object targetPc = L1World.getInstance().findObject(objid);
		if (targetPc == null) {
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
		}

		pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_Wand));
		pc.broadcastPacket(new S_AttackPacket(pc, 0,ActionCodes.ACTION_Wand));

		L1Character cha = (L1Character) targetPc;
		boolean isSameClan = false;
		if (cha instanceof L1PcInstance) {
			L1PcInstance target = (L1PcInstance) cha;
			if (target.getClanId() != 0 && pc.getClanId() == target.getClanId()) {
				isSameClan = true;
			}
		}
		if (pc.getId() != cha.getId() && !isSameClan) { // 自分以外と違うクラン
			int probability = 3 * (pc.getLevel() - cha.getLevel()) + 100 - cha.getMr();
			int rnd = _random.nextInt(100) + 1;
			if (rnd > probability) {
				removeItem(pc, item, chargeCount);
				return true;
			}
		}

		Effect effect = null;
		int chance = 0;
		int r = _random.nextInt(getTotalChance());
		for (Effect each : getEffects()) {
			chance += each.getChance();
			if (r < chance) {
				effect = each;
				break;
			}
		}
		if (effect == null) {
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
		}

		if (cha instanceof L1PcInstance) {
			L1PcInstance target = (L1PcInstance) cha;

			if (target.getInventory().checkEquipped(20281)) {
				target.sendPackets(new S_ShowPolyList(target.getId()));
				if (!target.isShapeChange()) {
					target.setShapeChange(true);
				}
				target.sendPackets(new S_ServerMessage(966));
				// 魔法の力によって保護されます。
				// 変身の際のメッセージは、他人が自分を変身させた時に出るメッセージと、レベルが足りない時に出るメッセージ以外はありません。
			} else {
				L1Skill skillTemp = SkillTable.getInstance().findBySkillId(SHAPE_CHANGE);
				L1PolyMorph.doPoly(target, effect.getPolyId(),
						skillTemp.getBuffDuration(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
				if (pc.getId() != target.getId()) {
					target.sendPackets(new S_ServerMessage(241, pc.getName()));
					// %0があなたを変身させました。
				}
			}
		} else if (cha instanceof L1MonsterInstance) {
			L1MonsterInstance mob = (L1MonsterInstance) cha;
			if (mob.getLevel() < 50) {
				int npcId = mob.getNpcTemplate().getNpcId();
				if (npcId != 45338 && npcId != 45370 && npcId != 45456
						// クロコダイル、バンディットボス、ネクロマンサー
						&& npcId != 45464 && npcId != 45473 && npcId != 45488
						// セマ、バルタザール、カスパー
						&& npcId != 45497 && npcId != 45516 && npcId != 45529
						// メルキオール、イフリート、ドレイク(DV)
						&& npcId != 45458) { // ドレイク(船長)
					L1Skill skillTemp = SkillTable.getInstance().findBySkillId(SHAPE_CHANGE);
					L1PolyMorph.doPoly(mob, effect.getPolyId(),
							skillTemp.getBuffDuration(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
				}
			}
		}

		removeItem(pc, item, chargeCount);
		return true;
	}

	private void removeItem(L1PcInstance pc, L1ItemInstance item, int chargeCount) {
		if (getRemove() > 0) {
			if (chargeCount > 0) {
				item.setChargeCount(chargeCount - getRemove());
				pc.getInventory().updateItem(item,
						L1PcInventory.COL_CHARGE_COUNT);
			} else {
				pc.getInventory().removeItem(item, getRemove());
			}
		}
	}

}
