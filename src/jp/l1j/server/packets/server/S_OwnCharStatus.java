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
import jp.l1j.server.model.gametime.L1GameTimeClock;
import jp.l1j.server.model.instance.L1PcInstance;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_OwnCharStatus extends ServerBasePacket {
	private static Logger _log = Logger.getLogger(S_OwnCharStatus.class
			.getName());

	private static final String S_OWB_CHAR_STATUS = "[S] S_OwnCharStatus";

	private byte[] _byte = null;

	public S_OwnCharStatus(L1PcInstance pc) {
		int time = L1GameTimeClock.getInstance().currentTime().getSeconds();
		time = time - (time % 300);
		// _log.warning((new
		// StringBuilder()).append("送信時間:").append(i).toString());
		writeC(Opcodes.S_OPCODE_OWNCHARSTATUS);
		writeD(pc.getId());

		if (pc.getLevel() < 1) {
			writeC(1);
		} else if (pc.getLevel() > 127) {
			writeC(127);
		} else {
			writeC(pc.getLevel());
		}
		writeD(pc.getExp());

		writeC(pc.getStr());
		writeC(pc.getInt());
		writeC(pc.getWis());
		writeC(pc.getDex());
		writeC(pc.getCon());
		writeC(pc.getCha());
		writeH(pc.getCurrentHp());
		writeH(pc.getMaxHp());
		writeH(pc.getCurrentMp());
		writeH(pc.getMaxMp());
		writeC(pc.getAc());
		writeD(time);
		writeC(pc.getFood());
		writeC(pc.getInventory().getWeight240());
		writeH(pc.getLawful());
		writeC(pc.getFire());
		writeC(pc.getWater());
		writeC(pc.getWind());
		writeC(pc.getEarth());
		writeD(pc.getMonsterKill()); // TODO 3.53C モンスター討伐数
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
		return S_OWB_CHAR_STATUS;
	}
}