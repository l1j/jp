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

public class S_OutputRawString extends ServerBasePacket {
	private static final String _S_OUTPUT_RAW_STRING = "[S] S_OutputRawString";
	private static final String _HTMLID = "deposit";
	
	private byte[] _byte = null;
	
	public S_OutputRawString(int objId, String name, String text) {
		if(text.length() > 0){
			buildPacket(objId,_HTMLID, name, text);
		}else{
			close(objId);
		}	
	}
	
	public void close(int objId) {
		buildPacket(objId, null, null, null);
	}

	private void buildPacket(int objId, String html, String name, String text) {
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(objId);
		writeS(html);
		writeH(0x02);
		writeH(0x02);
		writeS(name);
		writeS(text);
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
		return _S_OUTPUT_RAW_STRING;
	}
}
