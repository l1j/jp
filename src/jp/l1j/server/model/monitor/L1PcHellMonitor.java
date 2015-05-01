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

import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.model.instance.L1PcInstance;

public class L1PcHellMonitor extends L1PcMonitor {

	public L1PcHellMonitor(int oId) {
		super(oId);
	}

	@Override
	public void execTask(L1PcInstance pc) {
		if (pc.isDead()) { // 死んでいたらカウントダウンしない
			return;
		}
		pc.setHellTime(pc.getHellTime() - 1);
		if (pc.getHellTime() <= 0) {
			// endHellの実行時間が影響ないように
			Runnable r = new L1PcMonitor(pc.getId()) {
				@Override
				public void execTask(L1PcInstance pc) {
					pc.endHell();
				}
			};
			GeneralThreadPool.getInstance().execute(r);
		}
	}
}
