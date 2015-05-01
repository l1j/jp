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

package jp.l1j.server.model.instance;

import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.ScheduledFuture;
import jp.l1j.server.GeneralThreadPool;
import static jp.l1j.server.codes.ActionCodes.*;
import jp.l1j.server.model.HpRegenerationByDoll;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.MakeItemByDoll;
import jp.l1j.server.model.MpRegenerationByDoll;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_DollPack;
import jp.l1j.server.packets.server.S_OwnCharStatus;
import jp.l1j.server.packets.server.S_SkillHaste;
import jp.l1j.server.packets.server.S_SkillIconGFX;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1MagicDoll;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.Teleportation;

public class L1DollInstance extends L1NpcInstance {	
	private static final int[] DollAction = { ACTION_Think, ACTION_Aggress,
			ACTION_Salute, ACTION_Cheer };

	private static final long serialVersionUID = 1L;

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	
	private static Timer _timer = new Timer();
	
	private ScheduledFuture<?> _summonFuture;

	private HpRegenerationByDoll _hprTask;
	private boolean _hpRegenActive;
	
	private MpRegenerationByDoll _mprTask;
	private boolean _mpRegenActive;

	private MakeItemByDoll _makeTask;
	private boolean _makeActive;

	private int sleeptime_PT = 10;
	
	private int _itemId;
	
	private int _itemObjId;
	
	// ターゲットがいない場合の距離
	@Override
	public boolean noTarget() {
		if (_master.isDead()) {
			if (isChargeDoll()) { // 課金マジックドールのタイマーを停止
				L1ItemInstance item = _master.getInventory().getItem(getItemObjId());
				item.stopChargeTimer();
			}
			deleteDoll();
			return true;
		} else if (_master != null && _master.getMapId() == getMapId()) {
			int dir = moveDirection(_master.getX(), _master.getY());
			int distance = getLocation().getTileLineDistance(_master.getLocation());
			if (distance > 15) {
				// 本家仕様とは異なるが、15セル以上離れた場合は主人のもとへテレポート（引っ掛かり防止対策）
				Teleportation.teleport(this, _master.getX(), _master.getY(),
						_master.getMapId(), 5);
			} else if (distance > 3) {
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
			} else {
				if (sleeptime_PT == 0) {
					broadcastPacket(new S_DoActionGFX(getId(),
							DollAction[_random.nextInt(2)]));
					sleeptime_PT = _random.nextInt(20) + 10;
				} else {
					--sleeptime_PT;
					setSleepTime(500);
				}
			}
		} else {
			if (isChargeDoll()) { // 課金マジックドールのタイマーを停止
				L1ItemInstance item = _master.getInventory().getItem(getItemObjId());
				item.stopChargeTimer();
			}
			deleteDoll();
			return true;
		}
		return false;
	}

	// 目標までの距離に応じて最適と思われるルーチンで進む方向を返す
	@Override
	public int moveDirection(int x, int y, double d) { // 目標点Ｘ 目標点Ｙ 目標までの距離
		int dir = 0;
		if (d > courceRange) { // 距離が遠い場合は単純計算
			dir = targetDirection(x, y);
			dir = checkObject(getX(), getY(), getMapId(), dir);
		} else { // 目標までの最短経路を探索
			dir = _serchCource(x, y);
			if (dir == -1) { // 目標までの経路がなっかた場合はとりあえず近づいておく
				dir = targetDirection(x, y);
				if (!isExsistCharacterBetweenTarget(dir)) {
					dir = checkObject(getX(), getY(), getMapId(), dir);
				}
			}
		}
		return dir;
	}

	// 時間計測用
	class DollTimer implements Runnable {
		@Override
		public void run() {
			if (_destroyed) { // 既に破棄されていないかチェック
				return;
			}
			if (isChargeDoll()) { // 課金マジックドールのタイマーを停止
				L1ItemInstance item = _master.getInventory().getItem(getItemObjId());
				item.stopChargeTimer();
			}
			deleteDoll();
		}
	}

	public L1DollInstance(L1Npc template, L1PcInstance master, int itemId,
			int itemObjId) {
		super(template);
		setId(IdFactory.getInstance().nextId());
		setItemId(itemId);
		setItemObjId(itemObjId);
		int summonTime = L1MagicDoll.getSummonTime(this) * 1000;
		_summonFuture = GeneralThreadPool.getInstance().schedule(new DollTimer(), summonTime);
		setMaster(master);
		setX((_random.nextInt(5) + master.getX() - 2));
		setY((_random.nextInt(5) + master.getY() - 2));
		setMap(master.getMapId());
		setHeading(_random.nextInt(8));
		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addDoll(this);
		if (!isAiRunning()) {
			startAI();
		}
		if (L1MagicDoll.enableHpr(this)) {
			startHprTimer();
		}
		if (L1MagicDoll.enableMpr(this)) {
			startMprTimer();
		}
		if (L1MagicDoll.enableMakeItem(this)) {
			startMakeTimer();
		}
	}

	public void deleteDoll() {
		L1PcInstance pc = (L1PcInstance) _master;
		L1ItemInstance item = _master.getInventory().findItemId(_itemId);
		broadcastPacket(new S_SkillSound(getId(), 5936));
		if (_master != null) {
			pc.sendPackets(new S_SkillIconGFX(56, 0));
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
		if (L1MagicDoll.enableHpr(this)) {
			stopHprTimer();
		}
		if (L1MagicDoll.enableMpr(this)) {
			stopMprTimer();
		}
		if (L1MagicDoll.enableMakeItem(this)) {
			stopMakeTimer();
		}
		if (L1MagicDoll.isHaste(pc)) {
			pc.addHasteItemEquipped(-1);
			if (pc.getHasteItemEquipped() == 0) {
				pc.setMoveSpeed(0);
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
		}
		_master.getDollList().remove(getId());
		deleteMe();
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		// 宿屋内判定
		if (perceivedFrom.getMapId() > 10000
				&& perceivedFrom.getInnKeyId() != _master.getInnKeyId()) {
			return;
		}
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_DollPack(this, perceivedFrom));
	}

	@Override
	public void onItemUse() {
		if (!isActived()) {
			// １００％の確率でヘイストポーション使用
			useItem(USEITEM_HASTE, 100);
		}
	}

	@Override
	public void onGetItem(L1ItemInstance item, int count) {
		if (getNpcTemplate().getDigestItem() > 0) {
			setDigestItem(item);
		}
		if (Arrays.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
			useItem(USEITEM_HASTE, 100);
		}
	}

	// TODO マジックドール　アイテム製作開始
	public void startMakeTimer() {
		int interval = L1MagicDoll.getMakeTimeByDoll(this) * 1000;
		if (!_makeActive) {
			_makeTask = new MakeItemByDoll((L1PcInstance) _master, this);
			_timer.scheduleAtFixedRate(_makeTask, interval, interval);
			_makeActive = true;
		}
	}

	// TODO マジックドール　アイテム製作停止
	public void stopMakeTimer() {
		if (_makeActive) {
			_makeTask.cancel();
			_makeTask = null;
			_makeActive = false;
		}
	}

	// TODO マジックドール　HPR開始
	public void startHprTimer() {
		int interval = L1MagicDoll.getHprTimeByDoll(this) * 1000;
		if (!_hpRegenActive) {
			_hprTask = new HpRegenerationByDoll((L1PcInstance) _master, this);
			_timer.scheduleAtFixedRate(_hprTask, interval, interval);
			_hpRegenActive = true;
		}
	}

	// TODO マジックドール　HPR停止
	public void stopHprTimer() {
		if (_hpRegenActive) {
			_hprTask.cancel();
			_hprTask = null;
			_hpRegenActive = false;
		}
	}

	// TODO マジックドール　MPR開始
	public void startMprTimer() {
		int interval = L1MagicDoll.getMprTimeByDoll(this) * 1000;
		if (!_mpRegenActive) {
			_mprTask = new MpRegenerationByDoll((L1PcInstance) _master, this);
			_timer.scheduleAtFixedRate(_mprTask, interval, interval);
			_mpRegenActive = true;
		}
	}

	// TODO マジックドール　MPR停止
	public void stopMprTimer() {
		if (_mpRegenActive) {
			_mprTask.cancel();
			_mprTask = null;
			_mpRegenActive = false;
		}
	}

	public int getItemObjId() {
		return _itemObjId;
	}

	public void setItemObjId(int i) {
		_itemObjId = i;
	}

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(int i) {
		_itemId = i;
	}

	public boolean isChargeDoll() { // 課金マジックドール
		L1ItemInstance item = _master.getInventory().findItemId(_itemId);
		return item.isChargeDoll();
	}
}