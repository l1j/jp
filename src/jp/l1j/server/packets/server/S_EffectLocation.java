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
import jp.l1j.server.model.L1Location;
import jp.l1j.server.types.Point;

public class S_EffectLocation extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 指定された位置へエフェクトを表示するパケットを構築する。
	 * 
	 * @param pt - エフェクトを表示する位置を格納したPointオブジェクト
	 * @param gfxId - 表示するエフェクトのID
	 */
	public S_EffectLocation(Point pt, int gfxId) {
		this(pt.getX(), pt.getY(), gfxId);
	}

	/**
	 * 指定された位置へエフェクトを表示するパケットを構築する。
	 * 
	 * @param loc - エフェクトを表示する位置を格納したL1Locationオブジェクト
	 * @param gfxId - 表示するエフェクトのID
	 */
	public S_EffectLocation(L1Location loc, int gfxId) {
		this(loc.getX(), loc.getY(), gfxId);
	}

	/**
	 * 指定された位置へエフェクトを表示するパケットを構築する。
	 * 
	 * @param x - エフェクトを表示する位置のX座標
	 * @param y - エフェクトを表示する位置のY座標
	 * @param gfxId - 表示するエフェクトのID
	 */
	public S_EffectLocation(int x, int y, int gfxId) {
		writeC(Opcodes.S_OPCODE_EFFECTLOCATION);
		writeH(x);
		writeH(y);
		writeH(gfxId);
		writeC(0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}

		return _byte;
	}
}
