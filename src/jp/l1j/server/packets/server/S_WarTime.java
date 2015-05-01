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

import java.util.Calendar;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.codes.Opcodes;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_WarTime extends ServerBasePacket {
	private static Logger _log = Logger.getLogger(S_WarTime.class.getName());
	private static final String S_WAR_TIME = "[S] S_WarTime";

	public S_WarTime(Calendar cal) {
		// 1997/01/01 17:00を基点としている
		Calendar base_cal = Calendar.getInstance();
		base_cal.set(1997, 0, 1, 17, 0);
		long base_millis = base_cal.getTimeInMillis();
		long millis = cal.getTimeInMillis();
		long diff = millis - base_millis;
		diff -= 1200 * 60 * 1000; // 誤差修正
		diff = diff / 60000; // 分以下切捨て
		// timeは1加算すると3:02（182分）進む
		int time = (int) (diff / 182);

		// writeDの直前のwriteCで時間の調節ができる
		// 0.7倍した時間だけ縮まるが
		// 1つ調整するとその次の時間が広がる？
		writeC(Opcodes.S_OPCODE_WARTIME);
		writeH(6); // リストの数（6以上は無効）
		writeS(Config.TIME_ZONE); // 時間の後ろの（）内に表示される文字列
		writeC(0); // ?
		writeC(0); // ?
		writeC(0);
		writeD(time);
		writeC(0);
		writeD(time + 4);
		writeC(0);
		writeD(time + 8);
		writeC(0);
		writeD(time + 12);
		writeC(0);
		writeD(time + 16);
		writeC(0);
		writeD(time + 20);
		writeC(0);
	}

	@Override
	public final byte[] getContent() {
		return getBytes();
	}

	@Override
	public final String getType() {
		return S_WAR_TIME;
	}
}
