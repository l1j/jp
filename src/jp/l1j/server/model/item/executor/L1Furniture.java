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
import java.lang.reflect.Constructor;
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
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.SpawnFurnitureTable;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.model.L1HouseLocation;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1FurnitureInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_AttackPacket;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1Furniture {

	private static Logger _log = Logger.getLogger(L1Furniture.class.getName());

	private static L1Furniture _instance;

	public static L1Furniture getInstance() {
		if (_instance == null) {
			_instance = new L1Furniture();
		}
		return _instance;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1Furniture> {
		@XmlElement(name = "Item")
		private List<L1Furniture> _list;

		public Iterator<L1Furniture> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "NpcId")
		private int _npcId;

		public int getNpcId() {
			return _npcId;
		}
	}

	private static final String _path = "./data/xml/Item/Furniture.xml";

	private static HashMap<Integer, L1Furniture> _dataMap = new HashMap<Integer, L1Furniture>();

	public static L1Furniture get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
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
		if (NpcTable.getInstance().getTemplate(effect.getNpcId()) == null) {
			System.out.println(String.format(I18N_DOES_NOT_EXIST_NPC_LIST, effect.getNpcId()));
			// %s はNPCリストに存在しません。
			return false;
		}
		return true;
	}

	private static void loadXml(HashMap<Integer, L1Furniture> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1Furniture.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1Furniture each : list) {
				if (each.init()) {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading furnitures...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1Furniture> dataMap = new HashMap<Integer, L1Furniture>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item) {

		if (!L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1ここでは使えません。
			return false;
		}

		boolean isAppear = true;
		L1FurnitureInstance furniture = null;
		for (L1Object l1object : L1World.getInstance().getObject()) {
			if (l1object instanceof L1FurnitureInstance) {
				furniture = (L1FurnitureInstance) l1object;
				if (furniture.getItemObjId() == item.getId()) { // 既に引き出している家具
					isAppear = false;
					break;
				}
			}
		}

		Effect effect = getEffect();
		
		if (isAppear) {
			if (pc.getHeading() != 0 && pc.getHeading() != 2) {
				return false;
			}
			
			try {
				L1Npc l1npc = NpcTable.getInstance().getTemplate(effect.getNpcId());
				if (l1npc != null) {
					try {
						String s = l1npc.getImpl();
						Constructor constructor = Class.forName(
								"jp.l1j.server.model.instance." + s
										+ "Instance").getConstructors()[0];
						Object aobj[] = { l1npc };
						furniture = (L1FurnitureInstance) constructor.newInstance(aobj);
						furniture.setId(IdFactory.getInstance().nextId());
						furniture.setMap(pc.getMapId());
						if (pc.getHeading() == 0) {
							furniture.setX(pc.getX());
							furniture.setY(pc.getY() - 1);
						} else if (pc.getHeading() == 2) {
							furniture.setX(pc.getX() + 1);
							furniture.setY(pc.getY());
						}
						furniture.setHomeX(furniture.getX());
						furniture.setHomeY(furniture.getY());
						furniture.setHeading(0);
						furniture.setItemObjId(item.getId());

						L1World.getInstance().storeObject(furniture);
						L1World.getInstance().addVisibleObject(furniture);
						SpawnFurnitureTable.getInstance().insertFurniture(furniture);
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				}
			} catch (Exception exception) {
			}
		} else {
			furniture.deleteMe();
			SpawnFurnitureTable.getInstance().deleteFurniture(furniture);
		}
		
		return true;
	}

	public boolean remove(L1PcInstance pc, int targetId, L1ItemInstance item) {

		if (item.getChargeCount() <= 0) {
			pc.sendPackets(new S_ServerMessage(79)); // \f1何も起きませんでした。
			return false;
		}

		L1Object target = L1World.getInstance().findObject(targetId);
		if (target != null && target instanceof L1FurnitureInstance) {
			L1FurnitureInstance furniture = (L1FurnitureInstance) target;
			furniture.deleteMe();
			SpawnFurnitureTable.getInstance().deleteFurniture(furniture);
		}
		
		S_AttackPacket s_attackPacket = new S_AttackPacket(pc, 0, ActionCodes.ACTION_Wand);
		pc.sendPackets(s_attackPacket);
		pc.broadcastPacket(s_attackPacket);
		
		return true;
	}

}
