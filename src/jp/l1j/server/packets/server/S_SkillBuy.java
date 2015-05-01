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

import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1PcInstance;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_SkillBuy extends ServerBasePacket {
	private static Logger _log = Logger.getLogger(S_SkillBuy.class.getName());
	private static final String _S_SKILL_BUY = "[S] S_SkillBuy";

	private byte[] _byte = null;

	public S_SkillBuy(int o, L1PcInstance pc) {
		int count = Scount(pc);
		int inCount = 0;
		for (int k = 0; k < count; k++) {
			if (!pc.isSkillMastery((k + 1))) {
				inCount++;
			}
		}

		try {
			writeC(Opcodes.S_OPCODE_SKILLBUY);
			writeD(100);
			writeH(inCount);
			for (int k = 0; k < count; k++) {
				if (!pc.isSkillMastery((k + 1))) {
					writeD(k);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	public int Scount(L1PcInstance pc) {
		int RC = 0;
		switch (pc.getType()) {
		case 0: // 君主
			if (pc.getLevel() > 20 || pc.isGm()) {
				RC = 16;
			} else if (pc.getLevel() > 10) {
				RC = 8;
			}
			break;

		case 1: // ナイト
			if (pc.getLevel() >= 50 || pc.isGm()) {
				RC = 8;
			}
			break;

		case 2: // エルフ
			if (pc.getLevel() >= 24 || pc.isGm()) {
				RC = 23;
			} else if (pc.getLevel() >= 16) {
				RC = 16;
			} else if (pc.getLevel() >= 8) {
				RC = 8;
			}
			break;

		case 3: // WIZ
			if (pc.getLevel() >= 12 || pc.isGm()) {
				RC = 23;
			} else if (pc.getLevel() >= 8) {
				RC = 16;
			} else if (pc.getLevel() >= 4) {
				RC = 8;
			}
			break;

		case 4: // DE
			if (pc.getLevel() >= 24 || pc.isGm()) {
				RC = 16;
			} else if (pc.getLevel() >= 12) {
				RC = 8;
			}
			break;

		default:
			break;
		}
		return RC;
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return _S_SKILL_BUY;
	}

}
