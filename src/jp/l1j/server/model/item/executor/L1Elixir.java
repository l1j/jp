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
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.packets.server.S_OwnCharStatus2;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1Elixir {

	private static Logger _log = Logger.getLogger(L1Elixir.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1Elixir> {
		@XmlElement(name = "Item")
		private List<L1Elixir> _list;

		public Iterator<L1Elixir> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Effect {
		@XmlAttribute(name = "Str")
		private int _str = 0;
		
		private int getStr() {
			return _str;
		}		
		
		@XmlAttribute(name = "Con")
		private int _con = 0;
		
		private int getCon() {
			return _con;
		}
		
		@XmlAttribute(name = "Dex")
		private int _dex = 0;
		
		private int getDex() {
			return _dex;
		}
		
		@XmlAttribute(name = "Int")
		private int _int = 0;
		
		private int getInt() {
			return _int;
		}		
	
		@XmlAttribute(name = "Wis")
		private int _wis = 0;
		
		private int getWis() {
			return _wis;
		}		
		
		@XmlAttribute(name = "Cha")
		private int _cha = 0;
		
		private int getCha() {
			return _cha;
		}		
	}

	private static final String _path = "./data/xml/Item/Elixir.xml";

	private static HashMap<Integer, L1Elixir> _dataMap = new HashMap<Integer, L1Elixir>();

	public static L1Elixir get(int id) {
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

	private static void loadXml(HashMap<Integer, L1Elixir> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1Elixir.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1Elixir each : list) {
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
		System.out.println("loading elixirs...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1Elixir> dataMap = new HashMap<Integer, L1Elixir>();
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

		if (pc.getElixirStats() >= Config.ELIXIR_MAX_USE) {
			pc.sendPackets(new S_ServerMessage(481)); // \f1一つの能力値の最大値は35です。他の能力値を選択してください。
			return false;
		}
		
		Effect effect = getEffect();
		if (effect.getStr() > 0) {
			if (pc.getBaseStr() >= Config.ELIXIR_MAX_STR) {
				pc.sendPackets(new S_ServerMessage(481)); // \f1一つの能力値の最大値は35です。他の能力値を選択してください。
				return false;
			}
			pc.addBaseStr((byte) effect.getStr()); // 素のSTR値
			pc.setElixirStats(pc.getElixirStats() + effect.getStr());
		} else if (effect.getCon() > 0) {
			if (pc.getBaseCon() >= Config.ELIXIR_MAX_CON) {
				pc.sendPackets(new S_ServerMessage(481)); // \f1一つの能力値の最大値は35です。他の能力値を選択してください。
				return false;
			}
			pc.addBaseCon((byte) effect.getCon()); // 素のCON値
			pc.setElixirStats(pc.getElixirStats() + effect.getCon());
		} else if (effect.getDex() > 0) {
			if (pc.getBaseDex() >= Config.ELIXIR_MAX_DEX) {
				pc.sendPackets(new S_ServerMessage(481)); // \f1一つの能力値の最大値は35です。他の能力値を選択してください。
				return false;
			}
			pc.addBaseDex((byte) effect.getDex()); // 素のDex値
			pc.setElixirStats(pc.getElixirStats() + effect.getDex());
		} else if (effect.getInt() > 0) {
			if (pc.getBaseInt() >= Config.ELIXIR_MAX_INT) {
				pc.sendPackets(new S_ServerMessage(481)); // \f1一つの能力値の最大値は35です。他の能力値を選択してください。
				return false;
			}
			pc.addBaseInt((byte) effect.getInt()); // 素のINT値
			pc.setElixirStats(pc.getElixirStats() + effect.getInt());
		} else if (effect.getWis() > 0) {
			if (pc.getBaseWis() >= Config.ELIXIR_MAX_WIS) {
				pc.sendPackets(new S_ServerMessage(481)); // \f1一つの能力値の最大値は35です。他の能力値を選択してください。
				return false;
			}
			pc.addBaseWis((byte) effect.getWis()); // 素のWIS値
			pc.setElixirStats(pc.getElixirStats() + effect.getWis());
		} else if (effect.getCha() > 0) {
			if (pc.getBaseCha() >= Config.ELIXIR_MAX_CHA) {
				pc.sendPackets(new S_ServerMessage(481)); // \f1一つの能力値の最大値は35です。他の能力値を選択してください。
				return false;
			}
			pc.addBaseCha((byte) effect.getCha()); // 素のCHA値
			pc.setElixirStats(pc.getElixirStats() + effect.getCha());
		}
		pc.sendPackets(new S_OwnCharStatus2(pc));
		try {
			pc.save(); // DBにキャラクター情報を書き込む
		} catch (Exception e) {
			// save失敗時でもキャラステータスは上がっているのでエリクサーを削除する
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
