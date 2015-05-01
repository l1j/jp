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

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_Paralysis extends ServerBasePacket {

	public S_Paralysis(int type, boolean flag) {
		writeC(Opcodes.S_OPCODE_PARALYSIS);
		if (type == TYPE_PARALYSIS) // 体が完全に麻痺しました。
		{
			if (flag == true) {
				writeC(2);
			} else {
				writeC(3);
			}
		} else if (type == TYPE_PARALYSIS2) // 体が完全に麻痺しました。
		{
			if (flag == true) {
				writeC(4);
			} else {
				writeC(5);
			}
		} else if (type == TYPE_TELEPORT_UNLOCK) // テレポート待ち状態の解除
		{
			writeC(7);
		} else if (type == TYPE_SLEEP) // 強力な睡魔が襲ってきて、寝てしまいました。
		{
			if (flag == true) {
				writeC(10);
			} else {
				writeC(11);
			}
		} else if (type == TYPE_FREEZE) // 体が凍りました。
		{
			if (flag == true) {
				writeC(12);
			} else {
				writeC(13);
			}
		} else if (type == TYPE_STUN) // スタン状態です。
		{
			if (flag == true) {
				writeC(22);
			} else {
				writeC(23);
			}
		} else if (type == TYPE_BIND) // 足が縛られたように動けません。
		{
			if (flag == true) {
				writeC(24);
			} else {
				writeC(25);
			}
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return "[S] S_Paralysis";
	}

	public static final int TYPE_PARALYSIS = 1;

	public static final int TYPE_PARALYSIS2 = 2;

	public static final int TYPE_SLEEP = 3;

	public static final int TYPE_FREEZE = 4;

	public static final int TYPE_STUN = 5;

	public static final int TYPE_BIND = 6;

	public static final int TYPE_TELEPORT_UNLOCK = 7;

	private static final String _S__2F_PARALYSIS = "[S] S_Paralysis";
}
