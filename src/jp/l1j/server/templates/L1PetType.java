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
package jp.l1j.server.templates;

import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.utils.IntRange;

public class L1PetType {
	private final int _baseNpcId;

	private final L1Npc _baseNpcTemplate;

	private final String _name;

	private final int _tameItemId;

	private final IntRange _hpUpRange;

	private final IntRange _mpUpRange;

	private final int _transformNpcId;

	private final int _msgIds[];

	private final int _defyMsgId;

	private final int _transformItemId;

	private final boolean _useEquipment;

	public L1PetType(int baseNpcId, String name, int tameItemId,
			IntRange hpUpRange, IntRange mpUpRange, int transformItemId,
			int transformNpcId, int msgIds[], int defyMsgId,
			boolean useEquipment) {
		_baseNpcId = baseNpcId;
		_baseNpcTemplate = NpcTable.getInstance().getTemplate(baseNpcId);
		_name = name;
		_tameItemId = tameItemId;
		_hpUpRange = hpUpRange;
		_mpUpRange = mpUpRange;
		_transformItemId = transformItemId;
		_transformNpcId = transformNpcId;
		_msgIds = msgIds;
		_defyMsgId = defyMsgId;
		_useEquipment = useEquipment;
	}

	public int getBaseNpcId() {
		return _baseNpcId;
	}

	public L1Npc getBaseNpcTemplate() {
		return _baseNpcTemplate;
	}

	public String getName() {
		return _name;
	}

	public int getTameItemId() {
		return _tameItemId;
	}

	public boolean canTame() {
		return _tameItemId != 0;
	}

	public IntRange getHpUpRange() {
		return _hpUpRange;
	}

	public IntRange getMpUpRange() {
		return _mpUpRange;
	}

	public int getTransformNpcId() {
		return _transformNpcId;
	}

	public boolean canEvolve() {
		return _transformNpcId != 0;
	}

	public int getMessageId(int num) {
		if (num == 0) {
			return 0;
		}
		return _msgIds[num - 1];
	}

	public static int getMessageNumber(int level) {
		if (50 <= level) {
			return 5;
		}
		if (48 <= level) {
			return 4;
		}
		if (36 <= level) {
			return 3;
		}
		if (24 <= level) {
			return 2;
		}
		if (12 <= level) {
			return 1;
		}
		return 0;
	}

	public int getDefyMessageId() {
		return _defyMsgId;
	}

	// 進化アイテム
	public int getTransformItemId() {
		return _transformItemId;
	}

	public boolean useEvolvItem() {
		return _transformItemId != 0;
	}

	// ペット使用可能装備
	public boolean useEquipment() {
		return _useEquipment;
	}
}