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

import jp.l1j.configure.Config;
import jp.l1j.server.codes.Opcodes;

public class S_ServerVersion extends ServerBasePacket {
	private static final String S_SERVER_VERSION = "[S] ServerVersion";

	private static final int CLIENT_LANGUAGE = Config.CLIENT_LANGUAGE;

	/** 系統時間驗證 */
	private static final int UPTIME = 1327204035;// (int) (System.currentTimeMillis() / 1000);

	/** 服務器版本. */
	private static final int SERVER_VERSION = 120913203;

	/** 緩存版本. */
	private static final int CACHE_VERSION = 120913200;

	/** 認證(身份驗證)版本. */
	private static final int AUTH_VERSION = 2010083002;

	/** NPC版本. */
	private static final int NPC_VERSION = 120913201;

	/** 伺服器驗證. */
	private static final int SERVER_TYPE = 490882;

	public S_ServerVersion() {
		writeC(Opcodes.S_OPCODE_SERVERVERSION);
		writeC(0x00);
		writeC(0x01);//TODO 第幾個伺服器
		writeD(SERVER_VERSION);//TODO 3.63Cserver version
		writeD(CACHE_VERSION);//TODO 3.63Ccache version
		writeD(AUTH_VERSION);//TODO 3.63Cauth version
		writeD(NPC_VERSION);//TODO 3.63npc version
		writeD(0x0);//TODO server start time
		writeC(0x00);//TODO 未知封包
		writeC(0x00);//TODO 未知封包
		writeC(CLIENT_LANGUAGE);
		writeD(SERVER_TYPE);
		writeD(UPTIME);
		writeH(0x01);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_SERVER_VERSION;
	}
}
