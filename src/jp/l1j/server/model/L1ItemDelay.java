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
package jp.l1j.server.model;

import java.util.logging.Logger;

import jp.l1j.server.ClientThread;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.templates.L1EtcItem;

// Referenced classes of package jp.l1j.server.model:
// L1ItemDelay

public class L1ItemDelay {

	private static Logger _log = Logger.getLogger(L1ItemDelay.class
			.getName());

	private L1ItemDelay() {
	}

	static class ItemDelayTimer implements Runnable {
		private int _delayId;
		private int _delayTime;
		private L1Character _cha;

		public ItemDelayTimer(L1Character cha, int id, int time) {
			_cha = cha;
			_delayId = id;
			_delayTime = time;
		}

		@Override
		public void run() {
			stopDelayTimer(_delayId);
		}

		public void stopDelayTimer(int delayId) {
			_cha.removeItemDelay(delayId);
		}
	}

	public static void onItemUse(ClientThread client, L1ItemInstance item) {
		int delayId = 0;
		int delayTime = 0;

		L1PcInstance pc = client.getActiveChar();

		if (item.getItem().getType2() == 0) {
			// 種別：その他のアイテム
			delayId = ((L1EtcItem) item.getItem()).getDelayId();
			delayTime = ((L1EtcItem) item.getItem()).getDelayTime();
		} else if (item.getItem().getType2() == 1) {
			// 種別：武器
			return;
		} else if (item.getItem().getType2() == 2) {
			// 種別：防具

			if (item.getItem().getItemId() == 20077
					|| item.getItem().getItemId() == 20062
					|| item.getItem().getItemId() == 120077) {
				// インビジビリティクローク、バルログブラッディクローク
				if (item.isEquipped() && !pc.isInvisble()) {
					pc.beginInvisTimer();
				}
			} else {
				return;
			}
		}

		ItemDelayTimer timer = new ItemDelayTimer(pc, delayId, delayTime);

		pc.addItemDelay(delayId, timer);
		GeneralThreadPool.getInstance().schedule(timer, delayTime);
	}

}
