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

package jp.l1j.server.controller.timer;

import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_Weather;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;

public class WeatherTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(WeatherTimeController.class.getName());
	
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	
	private static WeatherTimeController _instance;

	public static WeatherTimeController getInstance() {
		if (_instance == null) {
			_instance = new WeatherTimeController();
		}
		return _instance;
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep(Config.WEATHER_INTERVAL * 1000);
				Weather();
			}
		} catch (Exception e1) {
			_log.warning(e1.getMessage());
		}
	}

	private void Weather() {
		int rnd = _random.nextInt(20);
		L1World.getInstance().setWeather(rnd);
		L1World.getInstance().broadcastPacketToAll(new S_Weather(rnd));
		String msg = I18N_WEATHER_SYS_CLEAR; // 天候システム: 晴れ
		if (rnd == 0 || (rnd >= 17 && rnd <= 19)) {
			msg = I18N_WEATHER_SYS_RAIN; // 天候システム: 雨
		} else if (rnd >= 1 && rnd <= 3) {
			msg = I18N_WEATHER_SYS_SNOW; // 天候システム: 雪
		}
		System.out.println(msg);
	}
}
