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
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.packets.server.S_AttackPacket;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.utils.L1SpawnUtil;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1SpawnWand {

	private static Logger _log = Logger.getLogger(L1SpawnWand.class.getName());
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1SpawnWand> {
		@XmlElement(name = "Item")
		private List<L1SpawnWand> _list;

		public Iterator<L1SpawnWand> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {	
		@XmlAttribute(name = "NpcId")
		private int _npcId;
		
		private int getNpcId() {
			return _npcId;
		}

		@XmlAttribute(name = "Time")
		private int _time;
		
		private int getTime() {
			return _time;
		}

		@XmlAttribute(name = "Chance")
		private int _chance;
		
		private int getChance() {
			return _chance;
		}

		@XmlAttribute(name = "Radius")
		private int _radius;
		
		private int getRadius() {
			return _radius;
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

	private static final String _path = "./data/xml/Item/SpawnWand.xml";

	private static HashMap<Integer, L1SpawnWand> _dataMap = new HashMap<Integer, L1SpawnWand>();

	public static L1SpawnWand get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
	}

	@XmlAttribute(name = "Type")
	private String _type;

	private String getType() {
		return _type;
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
			if (getType() != null && getType().equalsIgnoreCase("RANDOM")) {
				_totalChance += each.getChance();
			}
			if (NpcTable.getInstance().getTemplate(each.getNpcId()) == null) {
				System.out.println(String.format(I18N_DOES_NOT_EXIST_NPC_LIST, each.getNpcId()));
				// %s はNPCリストに存在しません。
				return false;
			}
		}
		if (getType() != null && getType().equalsIgnoreCase("RANDOM")) {
			if (getTotalChance() != 0 && getTotalChance() != 100) {
				System.out.println(String.format(I18N_PROBABILITIES_ERROR, getItemId()));
				// %s の確率は100%ではありません。
				return false;
			}
		}
		return true;
	}

	private static void loadXml(HashMap<Integer, L1SpawnWand> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1SpawnWand.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1SpawnWand each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading spawn wands...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1SpawnWand> dataMap = new HashMap<Integer, L1SpawnWand>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item) {

		int maxChargeCount = item.getItem().getMaxChargeCount();
		int chargeCount = item.getChargeCount();
		if (maxChargeCount > 0 && chargeCount <= 0) {
			// \f1何も起きませんでした。
			pc.sendPackets(new S_ServerMessage(79));
			return false;
		}

		Effect effect = null;
		if (getType() != null && getType().equalsIgnoreCase("RANDOM")) {
			// 出現するモンスターがランダム
			RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
			int chance = 0;
			int r = random.nextInt(getTotalChance());
			for (Effect each : getEffects()) {
				chance += each.getChance();
				if (r < chance) {
					effect = each;
					break;
				}
			}
		} else {
			// 出現するモンスターが固定
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
		}
		
		if (effect == null) {
			return false;
		}
		
		int radius = effect.getRadius() > 0 ? effect.getRadius() : -1;
		for (L1Object object : L1World.getInstance().getVisibleObjects(pc, radius)) {
			if (object instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) object;
				if (npc.getNpcTemplate().getNpcId() == effect.getNpcId()) {
					return false;
				}
			}
		}
		
		L1SpawnUtil.spawn(pc, effect.getNpcId(), 0, effect.getTime() * 1000);
		
		if (getItemId() == 40006 || getItemId() == 40412 || getItemId() == 14006) {
			// パインワンド、ブラックエントの枝、祝福されたパインワンド
			S_AttackPacket s_attackPacket = new S_AttackPacket(pc,0, ActionCodes.ACTION_Wand);
			pc.sendPackets(s_attackPacket);
			pc.broadcastPacket(s_attackPacket);
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
