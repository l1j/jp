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

import java.io.*;
import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_Emblem extends ServerBasePacket {
	private static Logger _log = Logger.getLogger(S_Emblem.class.getName());

	private static final String S_EMBLEM = "[S] S_Emblem";

	public S_Emblem(int clanid) {
		BufferedInputStream bis = null;
		try {
			String emblem_file = String.valueOf(clanid);
			File file = new File("emblem/" + emblem_file);
			if (file.exists()) {
				int data = 0;
				bis = new BufferedInputStream(new FileInputStream(file));
				writeC(Opcodes.S_OPCODE_EMBLEM);
				writeD(clanid);
				while ((data = bis.read()) != -1) {
					writeP(data);
				}
				writeC(0x00);
				writeH(0x0000);
			}
		} catch (Exception e) {
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException ignore) {
					// ignore
				}
			}
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_EMBLEM;
	}
}
