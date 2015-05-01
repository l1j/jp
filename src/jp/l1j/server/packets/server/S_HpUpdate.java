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
package jp.l1j.server.packets.server;

import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.utils.IntRange;

public class S_HpUpdate extends ServerBasePacket {
	private static final IntRange hpRange = new IntRange(1, 32767);

	public S_HpUpdate(int currentHp, int maxHp) {
		buildPacket(currentHp, maxHp);
	}

	public S_HpUpdate(L1PcInstance pc) {
		buildPacket(pc.getCurrentHp(), pc.getMaxHp());
	}

	public void buildPacket(int currentHp, int maxHp) {
		writeC(Opcodes.S_OPCODE_HPUPDATE);
		writeH(hpRange.ensure(currentHp));
		writeH(hpRange.ensure(maxHp));
		// writeC(0);
		// writeD(GameTimeController.getInstance().getGameTime());
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
