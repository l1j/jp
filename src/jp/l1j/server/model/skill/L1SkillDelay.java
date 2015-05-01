/*
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
package jp.l1j.server.model.skill;

import java.util.logging.Logger;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.model.L1Character;

// Referenced classes of package jp.l1j.server.model:
// L1SkillDelay

public class L1SkillDelay {

	private static Logger _log = Logger.getLogger(L1SkillDelay.class
			.getName());

	private L1SkillDelay() {
	}

	static class SkillDelayTimer implements Runnable {
		private int _delayTime;
		private L1Character _cha;

		public SkillDelayTimer(L1Character cha, int time) {
			_cha = cha;
			_delayTime = time;
		}

		@Override
		public void run() {
			stopDelayTimer();
		}

		public void stopDelayTimer() {
			_cha.setSkillDelay(false);
		}
	}

	public static void onSkillUse(L1Character cha, int time) {
		cha.setSkillDelay(true);
		GeneralThreadPool.getInstance().schedule(
				new SkillDelayTimer(cha, time), time);
	}

}
