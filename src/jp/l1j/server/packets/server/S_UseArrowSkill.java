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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.L1Character;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_UseArrowSkill extends ServerBasePacket {

	private static final String S_USE_ARROW_SKILL = "[S] S_UseArrowSkill";
	private static Logger _log = Logger.getLogger(S_UseArrowSkill.class
			.getName());

	private static AtomicInteger _sequentialNumber = new AtomicInteger(0);

	private byte[] _byte = null;

	public S_UseArrowSkill(L1Character cha, int targetobj, int spellgfx,
			int x, int y, boolean isHit) {

		int aid = 1;
		// オークアーチャー、蒼天のみ変更
		if (cha.getTempCharGfx() == 3860 || cha.getTempCharGfx() == 7959) {
			aid = 21;
		}
		writeC(Opcodes.S_OPCODE_ATTACKPACKET);
		writeC(aid);
		writeD(cha.getId());
		writeD(targetobj);
		// writeC(isHit ? 6 : 0); // 3.0c
		writeH(isHit ? 6 : 0);
		writeC(cha.getHeading());
		// writeD(0x12000000);
		// writeD(246);
		writeD(_sequentialNumber.incrementAndGet());
		writeH(spellgfx);
		writeC(127); // スキル使用時の光源の広さ？
		writeH(cha.getX());
		writeH(cha.getY());
		writeH(x);
		writeH(y);
		// writeC(228);
		// writeC(231);
		// writeC(95);
		// writeC(82);
		// writeC(170);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
		writeC(0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		} else {
			int seq = 0;
			synchronized (this){
				seq = _sequentialNumber.incrementAndGet();
			}
			_byte[13] = (byte) (seq & 0xff);
			_byte[14] = (byte) (seq >> 8 & 0xff);
			_byte[15] = (byte) (seq >> 16 & 0xff);
			_byte[16] = (byte) (seq >> 24 & 0xff);
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_USE_ARROW_SKILL;
	}

}