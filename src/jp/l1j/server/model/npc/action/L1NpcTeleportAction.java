/**
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
package jp.l1j.server.model.npc.action;

import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1CastleLocation;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.item.L1ItemId;
import jp.l1j.server.model.map.L1Map;
import jp.l1j.server.model.npc.L1NpcHtml;
import jp.l1j.server.packets.server.S_ServerMessage;
import org.w3c.dom.Element;

public class L1NpcTeleportAction extends L1NpcXmlAction {
	private final L1Location _loc;
	private final int _x;
	private final int _y;
	private final int _heading;
	private final int _price;
	private final int _itemId;
	private final boolean _effect;

	public L1NpcTeleportAction(Element element) {
		super(element);

		int x = L1NpcXmlParser.getIntAttribute(element, "X", -1);
		int y = L1NpcXmlParser.getIntAttribute(element, "Y", -1);
		_x = L1NpcXmlParser.getIntAttribute(element, "RandomX", 0);
		_y = L1NpcXmlParser.getIntAttribute(element, "RandomY", 0);
		int mapId = L1NpcXmlParser.getIntAttribute(element, "Map", -1);
		_loc = new L1Location(x, y, mapId);

		_heading = L1NpcXmlParser.getIntAttribute(element, "Heading", 5);

		_price = L1NpcXmlParser.getIntAttribute(element, "Price", 0);
		_itemId = L1NpcXmlParser.getIntAttribute(element, "ItemId", 40308);
		_effect = L1NpcXmlParser.getBoolAttribute(element, "Effect", true);
	}

	private boolean collectFee(L1PcInstance customer, int npcid) {
		double rate = getFeeRateByNpcId(npcid);
		int price = (int) ((double) _price * rate); // 物価(税率 - 10%)を加算;
		if (price <= 0) {
			return true;
		}

		if (!customer.getInventory().checkItem(_itemId, price)) {
			if (_itemId == L1ItemId.ADENA) {
			customer.sendPackets(new S_ServerMessage(189)); // アデナが不足しています。
			} else {
				int itemCount = price - customer.getInventory()
						.countItems(_itemId);
				customer.sendPackets(new S_ServerMessage(337,ItemTable
						.getInstance().getTemplate(_itemId).getName()
						+ "(" + itemCount + ")")); // \f1%0が不足しています。
			}
			return false;
		}

		customer.getInventory().consumeItem(_itemId, price);
		return true;
	}

	private double getFeeRateByNpcId(int npcid) {
		double rate = 1.0;
		int tax = L1CastleLocation.getCastleTaxRateByNpcId(npcid);
		if (tax > 0) {
			rate = rate + (tax - 10) / 100;
		}
		return rate;
	}
	
	@Override
	public L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj,
			byte[] args) {
		L1NpcInstance npc = (L1NpcInstance) obj;
		if (!collectFee(pc, npc.getNpcId())) {
			return L1NpcHtml.HTML_CLOSE;
		}

		L1Map map = _loc.getMap();
		int x = (_loc.getX() + ((int) (Math.random() * _x) - (int) (Math.random() * _x)));
		int y = (_loc.getY() + ((int) (Math.random() * _y) - (int) (Math.random() * _y)));
		
		if (!(map.isInMap(x, y) && map.isPassable(x, y))) {
			x = _loc.getX() - 1;
			y = _loc.getY() - 1;
		}
		
		L1Teleport.teleport(pc, x, y, (short) _loc.getMapId(), _heading, _effect);
		return null;
	}

}
