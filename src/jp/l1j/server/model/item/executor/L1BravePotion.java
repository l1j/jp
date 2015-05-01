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
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.model.skill.L1BuffUtil;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillBrave;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1BravePotion {

	private static Logger _log = Logger.getLogger(L1BravePotion.class.getName());
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1BravePotion> {
		@XmlElement(name = "Item")
		private List<L1BravePotion> _list;

		public Iterator<L1BravePotion> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "Time")
		private int _time;
		
		private int getTime() {
			return _time;
		}
		
		@XmlAttribute(name = "GfxId")
		private int _gfxid;
		
		private int getGfxId() {
			return _gfxid;
		}
		
		@XmlAttribute(name = "GfxId2")
		private int _gfxid2;
		
		private int getGfxId2() {
			return _gfxid2;
		}

		@XmlAttribute(name = "ClassInitial")
		private String _classInitial;

		private String getClassInitial() {
			return _classInitial;
		}

	}

	private static final String _path = "./data/xml/Item/BravePotion.xml";

	private static HashMap<Integer, L1BravePotion> _dataMap = new HashMap<Integer, L1BravePotion>();

	public static L1BravePotion get(int id) {
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
	
	private static void loadXml(HashMap<Integer, L1BravePotion> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1BravePotion.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1BravePotion each : list) {
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
		System.out.println("loading brave potions...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1BravePotion> dataMap = new HashMap<Integer, L1BravePotion>();
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
			pc.sendPackets(new S_ServerMessage(264)); // \f1あなたのクラスではこのアイテムは使用できません。
			return false;
		}

		if (getItemId() == 49158) { // ユグドラの実
			if (pc.hasSkillEffect(STATUS_BRAVE)) { // 名誉のコインとは重複しない
				pc.killSkillEffectTimer(STATUS_BRAVE);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
		} else if (getItemId() == 40068) { // エルヴン ワッフル
			if (pc.hasSkillEffect(STATUS_BRAVE)) { // 名誉のコインとは重複しない
				pc.killSkillEffectTimer(STATUS_BRAVE);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			if (pc.hasSkillEffect(WIND_WALK)) { // ウィンドウォークとは重複しない
				pc.killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
		} else if (getItemId() == 140068) { // 祝福されたエルヴン ワッフル
			if (pc.hasSkillEffect(STATUS_BRAVE)) { // 名誉のコインとは重複しない
				pc.killSkillEffectTimer(STATUS_BRAVE);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			if (pc.hasSkillEffect(WIND_WALK)) { // ウィンドウォークとは重複しない
				pc.killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
		} else if (getItemId() == 40733) { // 名誉のコイン
			if (pc.hasSkillEffect(STATUS_ELFBRAVE)) { // エルヴンワッフルとは重複しない
				pc.killSkillEffectTimer(STATUS_ELFBRAVE);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			if (pc.hasSkillEffect(HOLY_WALK)) { // ホーリーウォークとは重複しない
				pc.killSkillEffectTimer(HOLY_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			if (pc.hasSkillEffect(MOVING_ACCELERATION)) { // ムービングアクセレーションとは重複しない
				pc.killSkillEffectTimer(MOVING_ACCELERATION);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			if (pc.hasSkillEffect(WIND_WALK)) { // ウィンドウォークとは重複しない
				pc.killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			if (pc.hasSkillEffect(STATUS_RIBRAVE)) { // ユグドラの実とは重複しない
				pc.killSkillEffectTimer(STATUS_RIBRAVE);
				// XXX ユグドラの実のアイコンを消す方法が不明
				pc.setBraveSpeed(0);
			}
			if (pc.hasSkillEffect(BLOODLUST)) { // ブラッドラストとは重複しない
				pc.killSkillEffectTimer(BLOODLUST);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
		}
		
		if (getItemId() == 40068 || getItemId() == 140068) { // エルヴンワッフル
            pc.sendPackets(new S_SkillBrave(pc.getId(), effect.getGfxId2(), effect.getTime()));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), effect.getGfxId2(), 0));
            pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
            pc.setSkillEffect(STATUS_ELFBRAVE, effect.getTime() * 1000);
        } else if (getItemId() == 49158) { // ユグドラの実
            pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
            pc.setSkillEffect(STATUS_RIBRAVE, effect.getTime() * 1000);
        } else {
            pc.sendPackets(new S_SkillBrave(pc.getId(), effect.getGfxId2(), effect.getTime()));
            pc.broadcastPacket(new S_SkillBrave(pc.getId(), effect.getGfxId2(), 0));
            pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
            pc.setSkillEffect(STATUS_BRAVE, effect.getTime() * 1000);
        }
		pc.setBraveSpeed(1);
		
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
