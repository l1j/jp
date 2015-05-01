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

package jp.l1j.server.packets.server;

import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1PetInstance;

public class S_PetEquipment extends ServerBasePacket {
	private static final String S_PET_EQUIPMENT = "[S] S_PetEquipment";

	/**
	 * 【Client】 id:60 size:8 time:1302335819781 0000 3c 00 04 bd 54 00 00 00
	 * <...T...
	 * 
	 * 【Server】 id:82 size:16 time:1302335819812 0000 52 25 00 04 bd 54 00 00 0a
	 * 37 80 08 7e ec d0 46 R%...T...7..~..F
	 */

	public S_PetEquipment(int i, L1PetInstance pet, int j) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(0x25);
		writeC(i);
		writeD(pet.getId());
		writeC(j);
		writeC(pet.getAc()); // ペットAC
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_PET_EQUIPMENT;
	}
}