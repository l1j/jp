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

package jp.l1j.server.model.shop;

import jp.l1j.server.datatables.RaceTicketTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.L1World;
import jp.l1j.server.templates.L1RaceTicket;

public class L1AssessedItem {
	private final int _targetId;
	private final double _assessedPrice;

	L1AssessedItem(int targetId, double assessedPrice) {
		_targetId = targetId;
		//XXX
		L1ItemInstance item = (L1ItemInstance) L1World.getInstance().findObject(getTargetId());
		if(item.getItemId()==40309){//レースチケット
			L1RaceTicket ticket=RaceTicketTable.getInstance().getTemplate(_targetId);
			int price=0;
			if(ticket!=null){
				price=(int) (assessedPrice*ticket.getAllotmentPercentage()*ticket.getVictory());
			}
			_assessedPrice = price;
		}else{
			_assessedPrice = assessedPrice;
		}
	}

	public int getTargetId() {
		return _targetId;
	}

	public double getAssessedPrice() {
		return _assessedPrice;
	}
}
