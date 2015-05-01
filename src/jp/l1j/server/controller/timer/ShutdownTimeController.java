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

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import jp.l1j.server.GameServer;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class ShutdownTimeController implements Runnable {

	private static Logger _log = Logger.getLogger(ShutdownTimeController.class.getName());

	private static final String _path = "./data/xml/Cycle/ShutdownCycle.xml";

	private static HashMap<String, ShutdownTimeController> _dataMap =
			new HashMap<String, ShutdownTimeController>();

	private static ShutdownTimeController _instance;

	public static ShutdownTimeController getInstance() {
		if (_instance == null) {
			_instance = new ShutdownTimeController();
		}
		return _instance;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ShutdownCycleList")
	private static class ShutdownCycleList implements Iterable<ShutdownTimeController> {
		@XmlElement(name = "ShutdownCycle")
		private List<ShutdownTimeController> _list;

		public Iterator<ShutdownTimeController> iterator() {
			return _list.iterator();
		}
	}

	@XmlAttribute(name = "Time")
	private String _time;

	public String getTime() {
		return _time;
	}

	@XmlAttribute(name = "Delay")
	private int _delay;

	public int getDelay() {
		return _delay;
	}

	public static ShutdownTimeController get(String time) {
		return _dataMap.get(time);
	}

	private static void loadShutdownCycles(HashMap<String, ShutdownTimeController> dataMap) {
		try {
			PerformanceTimer timer = new PerformanceTimer();
			JAXBContext context = JAXBContext.newInstance(ShutdownTimeController.ShutdownCycleList.class);
			Unmarshaller um = context.createUnmarshaller();
			File file = new File(_path);
			ShutdownTimeController.ShutdownCycleList list = (ShutdownTimeController.ShutdownCycleList) um.unmarshal(file);
			for (ShutdownTimeController each : list) {
				dataMap.put(each.getTime(), each);
			}
			System.out.println("loading shutdown cycles...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (Exception e) {
			_log.log(Level.SEVERE, "Load " + _path + "failed!", e);
			System.exit(0);
		}
	}

	public void load() {
		loadShutdownCycles(_dataMap);
	}
	
	public void reload() {
		HashMap<String, ShutdownTimeController> dataMap = new HashMap<String, ShutdownTimeController>();
		loadShutdownCycles(dataMap);
		_dataMap = dataMap;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				checkShutdownTime(); // Shutdown時刻をチェック
				Thread.sleep(60000);
			}
		} catch (Exception e1) {
			_log.warning(e1.getMessage());
		}
	}

	private void checkShutdownTime() {
		Calendar cal = Calendar.getInstance();
		ShutdownTimeController timer = get(String.format("%1$TH:%1$TM",cal));
		if (timer != null) {
			GameServer.getInstance().shutdownWithCountdown(timer.getDelay());
		}
	}
}
