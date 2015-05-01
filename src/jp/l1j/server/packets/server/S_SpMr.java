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

import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1PcInstance;
import static jp.l1j.server.model.skill.L1SkillId.*;

public class S_SpMr extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_SpMr.class.getName());
	private static final String S_SpMr = "[S] S_SpMr";

	private byte[] _byte = null;

	public S_SpMr(L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_SPMR);
		// ウィズダムポーションのSPはS_SkillBrave送信時に更新されるため差し引いておく
		if (pc.hasSkillEffect(STATUS_WISDOM_POTION)) {
			writeC(pc.getSp() - pc.getTrueSp() - 2); // 装備増加したSP
		} else {
			writeC(pc.getSp() - pc.getTrueSp()); // 装備増加したSP
		}
		writeC(pc.getTrueMr() - pc.getBaseMr()); // 装備や魔法で増加したMR
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
		return S_SpMr;
	}
}
