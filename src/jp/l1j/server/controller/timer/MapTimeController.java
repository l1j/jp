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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import jp.l1j.server.datatables.MapTimerTable;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class MapTimeController implements Runnable {

	private static Logger _log = Logger.getLogger(MapTimeController.class.getName());

	private static final String _path = "./data/xml/Cycle/ResetMapTimeCycle.xml";

	private static HashMap<String, MapTimeController> _dataMap = new HashMap<String, MapTimeController>();

	private static MapTimeController _instance;

	public static MapTimeController getInstance() {
		if (_instance == null) {
			_instance = new MapTimeController();
		}
		return _instance;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ResetCycleList")
	private static class ResetCycleList implements Iterable<MapTimeController> {
		@XmlElement(name = "ResetCycle")
		private List<MapTimeController> _list;

		public Iterator<MapTimeController> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Area {
		@XmlAttribute(name = "AreaId")
		private int _areaId;
		
		private int getAreaId() {
			return _areaId;
		}

		@XmlAttribute(name = "Week")
		private int _week;
		
		private int getWeek() {
			return _week;
		}
	}

	@XmlAttribute(name = "Time")
	private String _time;

	public String getTime() {
		return _time;
	}

	@XmlElement(name = "Area")
	private CopyOnWriteArrayList<Area> _areas;

	private List<Area> getAreas() {
		return _areas;
	}
	
	public static MapTimeController get(String time) {
		return _dataMap.get(time);
	}

	private static void loadMapTimers(HashMap<String, MapTimeController> dataMap) {
		try {
			PerformanceTimer timer = new PerformanceTimer();
			JAXBContext context = JAXBContext.newInstance(MapTimeController.ResetCycleList.class);
			Unmarshaller um = context.createUnmarshaller();
			File file = new File(_path);
			MapTimeController.ResetCycleList list = (MapTimeController.ResetCycleList) um.unmarshal(file);
			for (MapTimeController each : list) {
				dataMap.put(each.getTime(), each);
			}
			System.out.println("loading map timers...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (Exception e) {
			_log.log(Level.SEVERE, "Load " + _path + "failed!", e);
			System.exit(0);
		}
	}

	public void load() {
		loadMapTimers(_dataMap);
	}
	
	public void reload() {
		HashMap<String, MapTimeController> dataMap = new HashMap<String, MapTimeController>();
		loadMapTimers(dataMap);
		_dataMap = dataMap;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				checkResetTime();
				Thread.sleep(60000);
			}
		} catch (Exception e1) {
			_log.warning(e1.getMessage());
		}
	}

	private void checkResetTime() {
		Calendar cal = Calendar.getInstance();
		MapTimeController timer = get(String.format("%1$TH:%1$TM",cal));
		if (timer != null) {
			for(Area each : timer.getAreas()) {
				if (each.getWeek() > 0 & each.getWeek() != cal.get(Calendar.DAY_OF_WEEK)) {
					continue;
				}
				MapTimerTable.reset(each.getAreaId());
			}
		}
	}
}
