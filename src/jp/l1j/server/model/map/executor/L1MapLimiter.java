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
package jp.l1j.server.model.map.executor;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.MapTimerTable;
import jp.l1j.server.datatables.MapTable;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1MapLimiter implements Runnable  {

	private static Logger _log = Logger.getLogger(L1MapLimiter.class.getName());

	private static L1PcInstance _pc = null;
	
	private static MapTimerTable _mapTimerTable = null;
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "MapLimiterList")
	private static class MapLimiterList implements Iterable<L1MapLimiter> {
		@XmlElement(name = "MapLimiter")
		private List<L1MapLimiter> _list;

		public Iterator<L1MapLimiter> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Effect {
		@XmlAttribute(name = "Time")
		private int _time;

		public int getTime() {
			return _time;
		}

		@XmlAttribute(name = "MinLevel")
		private int _minLevel;
		
		public int getMinLevel() {
			return _minLevel;
		}

		@XmlAttribute(name = "MaxLevel")
		private int _maxLevel;
		
		public int getMaxLevel() {
			return _maxLevel;
		}
		
		@XmlAttribute(name = "Delay")
		private int _delay;

		public int getDelay() {
			return _delay;
		}

		@XmlAttribute(name = "X")
		private int _x;

		public int getX() {
			return _x;
		}

		@XmlAttribute(name = "Y")
		private int _y;

		public int getY() {
			return _y;
		}

		@XmlAttribute(name = "BackMapId")
		private short _backmapid;

		public short getBackMapId() {
			return _backmapid;
		}
	}

	private static final String PATH = "./data/xml/etc/MapLimiter.xml";

	private static HashMap<Integer, L1MapLimiter> _dataMap = new HashMap<Integer, L1MapLimiter>();

	public static L1MapLimiter get(int _mapId) {
		return _dataMap.get(_mapId);
	}

	@XmlAttribute(name = "MapId")
	private Integer _mapId;

	public Integer getMapId() {
		return _mapId;
	}

	@XmlAttribute(name = "AreaId")
	private Integer _areaId;

	public Integer getAreaId() {
		return _areaId;
	}

	@XmlElement(name = "Effect")
	private Effect _effect;

	public Effect getEffect() {
		return _effect;
	}

	public int getEnterTime() {
		return _mapTimerTable == null ? 0 : _mapTimerTable.getEnterTime();
	}
	
	public static void loadXml(HashMap<Integer, L1MapLimiter> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1MapLimiter.MapLimiterList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(PATH);
			MapLimiterList list = (MapLimiterList) um.unmarshal(file);

			for (L1MapLimiter each : list) {
				if (MapTable.getInstance().locationname(each.getMapId()) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_MAP_LIST, each.getMapId()));
				} else {
					dataMap.put(each.getMapId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, PATH + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading map limiter...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1MapLimiter> dataMap = new HashMap<Integer, L1MapLimiter>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public void execute(L1PcInstance pc) {
		_pc = pc;
		_mapTimerTable = MapTimerTable.find(pc.getId(), getAreaId());
		if (_mapTimerTable == null) {
			int time = pc.getMapLimiter().getEffect().getTime();
			_mapTimerTable = new MapTimerTable(pc.getId(), getMapId(), getAreaId(), time);
		}
	}
	
	@Override
	public void run() {
		try {
			Effect effect = getEffect();
			int enterTime = _mapTimerTable.getEnterTime();
			if ((effect.getTime() > 0 && (enterTime - 1) >= 0) || effect.getTime() <= 0){
				if (effect.getMinLevel() > 0 && _pc.getLevel() < effect.getMinLevel()) {
					_pc.sendPackets(new S_ServerMessage(1287));
					// 今のレベルでは入れません。
					teleport();
				}
				
				if (effect.getMaxLevel() > 0 && _pc.getLevel() > effect.getMaxLevel()) {
					_pc.sendPackets(new S_ServerMessage(1287));
					// 今のレベルでは入れません。
					teleport();
				}
				
				if (effect.getTime() > 0) {
					int h = enterTime / 3600;
					int m = (enterTime % 3600) / 60;
					if (enterTime <= effect.getDelay()) { // ディレイ秒以下は毎秒
						_pc.sendPackets(new S_ServerMessage(1528, String.valueOf(enterTime)));
						// ダンジョン滞在期限の残りは%0秒です。
					} else if ((enterTime == effect.getTime()) // 初回入場時
						|| (enterTime % 1800 == 0) // 30分毎
						|| ((enterTime <= 600) && (enterTime % 60 == 0))) { // 残り10分以下は1分毎
						if (h > 0) {
							if (m > 0) {
								_pc.sendPackets(new S_ServerMessage(1525, String.valueOf(h), String.valueOf(m)));
								// ダンジョン滞在期限の残りは%0時間%1分です。
							} else {
								_pc.sendPackets(new S_ServerMessage(1526, String.valueOf(h)));
								// ダンジョン滞在期限の残りは%0時間です。
							}
						} else {
							_pc.sendPackets(new S_ServerMessage(1527, String.valueOf(m)));
							// ダンジョン滞在期限の残りは%0分です。
						}
					}
					_mapTimerTable.setEnterTime(enterTime - 1);
				}
			} else {
				int h = effect.getTime() / 3600;
				int m = (effect.getTime() % 3600) / 60;
				if (h > 0) {
					if (m > 0) {
						_pc.sendPackets(new S_ServerMessage(1524, String.valueOf(h), String.valueOf(m)));
						// ダンジョン滞在期限%0時間%1分を過ぎました。
					} else {
						_pc.sendPackets(new S_ServerMessage(1522, String.valueOf(h)));
						// ダンジョン滞在期限%0時間を過ぎました。
					}
				} else {
					_pc.sendPackets(new S_ServerMessage(1523, String.valueOf(m)));
					// ダンジョン滞在期限%0分を過ぎました。
				}
				teleport();
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private void teleport() {
		Effect effect = getEffect();
		RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
		int rndx = random.nextInt(3);
		int rndy = random.nextInt(3);
		int locx = effect.getX() + rndx;
		int locy = effect.getY() + rndy;
		short mapid = effect.getBackMapId();
		L1Teleport.teleport(_pc, locx, locy, mapid, 5, true);
	}
	
	public void save() {
		if (_mapTimerTable != null) {
			_mapTimerTable.save();
		}
	}
	
}
