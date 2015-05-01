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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.packets.server.S_CharVisualUpdate;
import jp.l1j.server.packets.server.S_OwnCharStatus;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;

public class FishingTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(FishingTimeController.class.getName());

	private static FishingTimeController _instance;
	private final List<L1PcInstance> _fishingList = new ArrayList<L1PcInstance>();
	
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	public static FishingTimeController getInstance() {
		if (_instance == null) {
			_instance = new FishingTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(300);
				fishing();
			}
		} catch (Exception e1) {
		}
	}

	public void addMember(L1PcInstance pc) {
		if (pc == null || _fishingList.contains(pc)) {
			return;
		}
		_fishingList.add(pc);
	}

	public void removeMember(L1PcInstance pc) {
		if (pc == null || !_fishingList.contains(pc)) {
			return;
		}
		_fishingList.remove(pc);
	}

	private void fishing() {
		if (_fishingList.size() > 0) {
			long currentTime = System.currentTimeMillis();
			for (int i = 0; i < _fishingList.size(); i++) {
				L1PcInstance pc = _fishingList.get(i);
				if (pc.isFishing()) {
					long time = pc.getFishingTime();
					if (currentTime <= (time + 500) && currentTime >= (time - 500)
							&& !pc.isFishingReady()) {
						pc.setFishingReady(true);
						finishFishing(pc);
					}
				}
			}
		}
	}

	private void finishFishing(L1PcInstance pc) {
		int chance = _random.nextInt(200) + 1;

		if (chance < 50) {
			successFishing(pc, 41298); // ヤング フィッシュ(25%)
		} else if (chance < 86) {
			successFishing(pc, 50549); // ボックス フィッシュ(18%)
		} else if (chance < 101) {
			successFishing(pc, 41300); // ストロング フィッシュ(7.5%)
		} else if (chance < 116) {
			successFishing(pc, 41299); // スウィフト フィッシュ(7.5%)
		} else if (chance < 126) {
			successFishing(pc, 41296); // フナ(5%)
		} else if (chance < 136) {
			successFishing(pc, 41297); // コイ(5%)
		} else if (chance < 141) {
			successFishing(pc, 41301); // シャイニング レッド フィッシュ(2.5%)
		} else if (chance < 146) {
			successFishing(pc, 41302); // シャイニング グリーン フィッシュ(2.5%)
		} else if (chance < 151) {
			successFishing(pc, 41303); // シャイニング ブルー フィッシュ(2.5%)
		} else if (chance < 156) {
			successFishing(pc, 41304); // シャイニング ホワイト フィッシュ(2.5%)
		} else if (chance < 158) {
			successFishing(pc, 41252); // レア タートル(1.0%)
		} else if (chance < 160) {
			successFishing(pc, 41294); // 輝く鱗(1.0%)
		} else {
			pc.sendPackets(new S_ServerMessage(1136)); // 釣りに失敗しました。
			if (pc.isFishingReady()) {
				restartFishing(pc);
			}
		}
	}

	private void successFishing(L1PcInstance pc, int itemId) {
		L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
		if (item != null) {
			pc.sendPackets(new S_ServerMessage(403, item.getItem().getName()));
			pc.addExp(Config.RATE_FISHING_EXP);
			pc.sendPackets(new S_OwnCharStatus(pc));
			item.setCount(1);
			if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else { // インベントリが一杯の場合
				stopFishing(pc);
				item.startItemOwnerTimer(pc);
				L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
				return;
			}
		} else {
			pc.sendPackets(new S_ServerMessage(1136)); // 釣りに失敗しました。
			stopFishing(pc);
			return;
		}

		if (pc.isFishingReady()) {
			if (itemId == 41294) {
				pc.sendPackets(new S_ServerMessage(1739)); // 輝く鱗を釣り上げたため、魔法の釣りが終了しました。
				stopFishing(pc);
				return;
			}
			restartFishing(pc);
		}
	}

	private void restartFishing(L1PcInstance pc) {
		if (pc.getInventory().consumeItem(41295, 1)) {
			long fishTime = System.currentTimeMillis() + 10000 + _random.nextInt(5) * 1000;
			pc.setFishingTime(fishTime);
			pc.setFishingReady(false);
		} else {
			pc.sendPackets(new S_ServerMessage(1137)); // 釣りをするためにはエサが必要です。
			stopFishing(pc);
		}
	}

	private void stopFishing(L1PcInstance pc) {
		pc.setFishingTime(0);
		pc.setFishingReady(false);
		pc.setFishing(false);
		pc.sendPackets(new S_CharVisualUpdate(pc));
		pc.broadcastPacket(new S_CharVisualUpdate(pc));
		FishingTimeController.getInstance().removeMember(pc);
	}
}
