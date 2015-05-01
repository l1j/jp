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
package jp.l1j.server.model;

import java.util.TimerTask;

import jp.l1j.configure.Config;
import jp.l1j.server.datatables.PetTable;
import jp.l1j.server.datatables.PetTypeTable;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.packets.server.S_NpcChatPacket;
import jp.l1j.server.templates.L1PetType;
import jp.l1j.server.utils.IntRange;

public class L1PetFoodTimer extends TimerTask {
	/**
	 * ペット空腹度タイマー
	 * 
	 * @throws IllegalArgumentException
	 *             petにnullが指定された場合
	 */
	public L1PetFoodTimer(L1PetInstance pet) throws IllegalArgumentException {
		if (pet == null) {
			throw new IllegalArgumentException("pet is null");
		}
		_pet = pet;
	}

	@Override
	public void run() {
		if (_pet.isDead()) {
			cancel();
		}

		int food = _petFoodRange.ensure(_pet.getFood() - 2);
		_pet.setFood(food);
		if (Config.DEBUG_MODE) {
			PetTable.getInstance().storePetFood(_pet.getItemObjId(), food);
		}
		if (food == 0) {
			_pet.setCurrentPetStatus(3);

			// ペット空腹時メッセージ
			L1PetType type = PetTypeTable.getInstance().get(
					_pet.getNpcTemplate().getNpcId());
			int id = type.getDefyMessageId();
			if (id != 0) {
				_pet.broadcastPacket(new S_NpcChatPacket(_pet, "$" + id, 0));
			}
		}
	}

	private static final IntRange _petFoodRange = new IntRange(0, 100);
	private final L1PetInstance _pet;
}