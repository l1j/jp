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

package jp.l1j.server.model.monitor;

import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_Karma;
import jp.l1j.server.packets.server.S_Lawful;
// import jp.l1j.server.utils.IntRange;

public class L1PcExpMonitor extends L1PcMonitor {

	private int _old_lawful;

	private int _old_exp;

	private int _old_karma;

	public L1PcExpMonitor(int oId) {
		super(oId);
	}

	@Override
	public void execTask(L1PcInstance pc) {

		// ロウフルが変わった場合はS_Lawfulを送信
// // ただし色が変わらない場合は送信しない
// if (_old_lawful != pc.getLawful()
// && !((IntRange.includes(_old_lawful, 9000, 32767) && IntRange
// .includes(pc.getLawful(), 9000, 32767)) || (IntRange
// .includes(_old_lawful, -32768, -2000) && IntRange
// .includes(pc.getLawful(), -32768, -2000)))) {
		if (_old_lawful != pc.getLawful()) {
			_old_lawful = pc.getLawful();
			S_Lawful s_lawful = new S_Lawful(pc.getId(), _old_lawful);
			pc.sendPackets(s_lawful);
			pc.broadcastPacket(s_lawful);
		}

		if (_old_karma != pc.getKarma()) {
			_old_karma = pc.getKarma();
			pc.sendPackets(new S_Karma(pc));
		}

		if (_old_exp != pc.getExp()) {
			_old_exp = pc.getExp();
			pc.onChangeExp();
		}
	}
}
