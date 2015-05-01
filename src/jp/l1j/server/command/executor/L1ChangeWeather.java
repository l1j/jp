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

package jp.l1j.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.packets.server.S_Weather;

public class L1ChangeWeather implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1ChangeWeather.class.getName());

	private L1ChangeWeather() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ChangeWeather();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			int weather = Integer.parseInt(tok.nextToken());
			L1World.getInstance().setWeather(weather);
			L1World.getInstance().broadcastPacketToAll(new S_Weather(weather));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_1,
					cmdName, I18N_WEATHER_OPTIONS)));
			// .%s %s の形式で入力してください。
		}
	}
}
