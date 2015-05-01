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

package jp.l1j.server.model.trap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.datatables.TrapTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1TrapInstance;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1World;
import jp.l1j.server.types.Point;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class L1WorldTraps {
	private static Logger _log = Logger.getLogger(L1WorldTraps.class.getName());

	private List<L1TrapInstance> _allTraps = new ArrayList<L1TrapInstance>();
	private List<L1TrapInstance> _allBases = new ArrayList<L1TrapInstance>();

	private Timer _timer = new Timer();

	private static L1WorldTraps _instance;

	private L1WorldTraps() {
		initialize();
	}

	public static L1WorldTraps getInstance() {
		if (_instance == null) {
			_instance = new L1WorldTraps();
		}
		return _instance;
	}

	private void initialize() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			pstm = con.prepareStatement("SELECT * FROM spawn_traps");

			rs = pstm.executeQuery();

			while (rs.next()) {
				int trapId = rs.getInt("trap_id");
				L1Trap trapTemp = TrapTable.getInstance().getTemplate(trapId);
				L1Location loc = new L1Location();
				loc.setMap(rs.getInt("map_id"));
				loc.setX(rs.getInt("loc_x"));
				loc.setY(rs.getInt("loc_y"));
				Point rndPt = new Point();
				rndPt.setX(rs.getInt("loc_rnd_x"));
				rndPt.setY(rs.getInt("loc_rnd_y"));
				int count = rs.getInt("count");
				int span = rs.getInt("span");

				for (int i = 0; i < count; i++) {
					L1TrapInstance trap = new L1TrapInstance(IdFactory
							.getInstance().nextId(), trapTemp, loc, rndPt, span);
					L1World.getInstance().addVisibleObject(trap);
					_allTraps.add(trap);
				}
				L1TrapInstance base = new L1TrapInstance(IdFactory
						.getInstance().nextId(), loc);
				L1World.getInstance().addVisibleObject(base);
				_allBases.add(base);
			}

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public static void reloadTraps() {
		TrapTable.getInstance().reload();
		L1WorldTraps oldInstance = _instance;
		_instance = new L1WorldTraps();
		oldInstance.resetTimer();
		removeTraps(oldInstance._allTraps);
		removeTraps(oldInstance._allBases);
	}

	private static void removeTraps(List<L1TrapInstance> traps) {
		for (L1TrapInstance trap : traps) {
			trap.disableTrap();
			L1World.getInstance().removeVisibleObject(trap);
		}
	}

	private void resetTimer() {
		synchronized (this) {
			_timer.cancel();
			_timer = new Timer();
		}
	}

	private void disableTrap(L1TrapInstance trap) {
		trap.disableTrap();

		synchronized (this) {
			_timer.schedule(new TrapSpawnTimer(trap), trap.getSpan());
		}
	}

	public void resetAllTraps() {
		for (L1TrapInstance trap : _allTraps) {
			trap.resetLocation();
			trap.enableTrap();
		}
	}

	public void onPlayerMoved(L1PcInstance player) {
		L1Location loc = player.getLocation();

		for (L1TrapInstance trap : _allTraps) {
			if (trap.isEnable() && loc.equals(trap.getLocation()) && !player.isGmInvis()) {
				trap.onTrod(player);
				disableTrap(trap);
			}
		}
	}

	public void onDetection(L1PcInstance caster) {
		L1Location loc = caster.getLocation();

		for (L1TrapInstance trap : _allTraps) {
			if (trap.isEnable() && loc.isInScreen(trap.getLocation())) {
				trap.onDetection(caster);
				disableTrap(trap);
			}
		}
	}

	private class TrapSpawnTimer extends TimerTask {
		private final L1TrapInstance _targetTrap;

		public TrapSpawnTimer(L1TrapInstance trap) {
			_targetTrap = trap;
		}

		@Override
		public void run() {
			_targetTrap.resetLocation();
			_targetTrap.enableTrap();
		}
	}


	/*************外部からトラップ情報を操作する**********************/
	public synchronized void addTrap(L1TrapInstance trap) {
		_allTraps.add(trap);
		//_allBases.add(trap);
	}

	 public synchronized void removeTrap(L1TrapInstance trap) {
		//_allBases.remove(trap);
		 for (Iterator<L1TrapInstance> i =_allTraps.listIterator(); i.hasNext();) {
			  L1TrapInstance traps=i.next();  //次の要素の呼び出し
			  if(traps.getId()==trap.getId()){
					  i.remove();  //要素の削除
				  break;
			  }
		 }
		List<L1TrapInstance>traps =new ArrayList<L1TrapInstance>();
		traps.add(trap);
		removeTraps(traps);
	}
	 /****************************************************************/

}
