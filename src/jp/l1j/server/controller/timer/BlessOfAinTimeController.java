package jp.l1j.server.controller.timer;

import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;

public class BlessOfAinTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(FishingTimeController.class.getName());
	
	private static BlessOfAinTimeController _instance;
	
	public static BlessOfAinTimeController getInstance() {
		if (_instance == null) {
			_instance = new BlessOfAinTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		while (true) {
			try {
				for (L1PcInstance _pc : L1World.getInstance().getAllPlayers()) {
					if(_pc.getLevel() >= 49){
						int sc = _pc.getSafeCount();
						if(_pc.getZoneType() == 1 && !_pc.isPrivateShop()) {
							if(sc >= 14){
								if(_pc.getBlessOfAin() <= 1999999)
									_pc.calcBlessOfAin(10000);
								_pc.setSafeCount(0);
							} else {
								_pc.setSafeCount(sc + 1);
							}
						} else {
							if(sc > 0)
								_pc.setSafeCount(0);
						}
					}
				}
				Thread.sleep(60000);
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}
}