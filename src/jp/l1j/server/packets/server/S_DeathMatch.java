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

//73 sound only
public class S_DeathMatch extends ServerBasePacket {
	private static final String S_DEATHMACTH= "[S] S_DeathMatch";

	private byte[] _byte = null;
	public static final int CountDown = 0x47;
	public static final int CountDownOff = 0x48;
	//public static final int Winner = 0x44;
	//public static final int GameOver = 0x45;
	//public static final int GameEnd = 0x46;



	public S_DeathMatch(int type) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(type);
		if(type==CountDown){
			writeH(600);
		}
	}
	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_DEATHMACTH;
	}
}
