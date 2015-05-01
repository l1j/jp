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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.NpcChatTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.SprTable;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1HardinQuest;
import jp.l1j.server.model.L1HateList;
import jp.l1j.server.model.L1Magic;
import jp.l1j.server.model.L1MobGroupInfo;
import jp.l1j.server.model.L1MobSkillUse;
import jp.l1j.server.model.L1MobSkillUse.L1AvailableSkill;
import jp.l1j.server.model.L1NpcChatTimer;
import jp.l1j.server.model.L1NpcRegenerationTimer;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1OrimQuest;
import jp.l1j.server.model.L1Spawn;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.inventory.L1GroundInventory;
import jp.l1j.server.model.inventory.L1Inventory;
import static jp.l1j.server.model.item.L1ItemId.*;
import jp.l1j.server.model.map.L1Map;
import jp.l1j.server.model.map.L1WorldMap;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.server.S_ChangeHeading;
import jp.l1j.server.packets.server.S_ChangeShape;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_MoveCharPacket;
import jp.l1j.server.packets.server.S_NpcPack;
import jp.l1j.server.packets.server.S_RemoveObject;
import jp.l1j.server.packets.server.S_SkillHaste;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.templates.L1NpcChat;
import jp.l1j.server.types.Point;
import jp.l1j.server.utils.TimerPool;
import jp.l1j.server.utils.collections.Lists;

public class L1NpcInstance extends L1Character {
	private static final long serialVersionUID = 1L;

	public static final int MOVE_SPEED = 0;
	public static final int ATTACK_SPEED = 1;
	public static final int MAGIC_SPEED = 2;

	public static final int HIDDEN_STATUS_NONE = 0;
	public static final int HIDDEN_STATUS_SINK = 1;
	public static final int HIDDEN_STATUS_FLY = 2;
	public static final int HIDDEN_STATUS_ICE = 3;

	public static final int CHAT_TIMING_APPEARANCE = 0;
	public static final int CHAT_TIMING_DEAD = 1;
	public static final int CHAT_TIMING_HIDE = 2;
	public static final int CHAT_TIMING_GAME_TIME = 3;

	private static Logger _log = Logger
			.getLogger(L1NpcInstance.class.getName());
	private L1Npc _npcTemplate;
	private L1PcInstance _pc;
	private L1Spawn _spawn;
	private int _spawnNumber; // L1Spawnで管理されているナンバー

	private int _petcost; // ペットになったときのコスト
	public L1Inventory _inventory = new L1Inventory();
	private L1MobSkillUse _mobSkill;
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	// 対象を初めて発見したとき。（テレポート用）
	private boolean firstFound = true;

	// 経路探索範囲（半径） ※上げすぎ注意！！
	public static int courceRange = 15;

	// 吸われたMP
	private int _drainedMana = 0;

	// 休憩
	private boolean _rest = false;

	// ランダム移動時の距離と方向
	private int _randomMoveDistance = 0;

	private int _randomMoveDirection = 0;

	// ■■■■■■■■■■■■■ ＡＩ関連 ■■■■■■■■■■■

	interface NpcAI {
		public void start();
	}

	protected void startAI() {
		if (Config.NPCAI_IMPLTYPE == 1) {
			new NpcAITimerImpl().start();
		} else if (Config.NPCAI_IMPLTYPE == 2) {
			new NpcAIThreadImpl().start();
		} else {
			new NpcAITimerImpl().start();
		}
	}

	/**
	 * マルチ(コア)プロセッサをサポートする為のタイマープール。 AIの実装タイプがタイマーの場合に使用される。
	 */
	private static final TimerPool _timerPool = new TimerPool(4);

	class NpcAITimerImpl extends TimerTask implements NpcAI {
		/**
		 * 死亡処理の終了を待つタイマー
		 */
		private class DeathSyncTimer extends TimerTask {
			private void schedule(int delay) {
				_timerPool.getTimer().schedule(new DeathSyncTimer(), delay);
			}

			@Override
			public void run() {
				if (isDeathProcessing()) {
					schedule(getSleepTime());
					return;
				}
				allTargetClear();
				setAiRunning(false);
			}
		}

		@Override
		public void start() {
			setAiRunning(true);
			_timerPool.getTimer().schedule(NpcAITimerImpl.this, 0);
		}

		private void stop() {
			_mobSkill.resetAllSkillUsedCount();
			_timerPool.getTimer().schedule(new DeathSyncTimer(), 0); // 死亡同期を開始
		}

		// 同じインスタンスをTimerへ登録できない為、苦肉の策。
		private void schedule(int delay) {
			_timerPool.getTimer().schedule(new NpcAITimerImpl(), delay);
		}

		@Override
		public void run() {
			try {
				if (notContinued()) {
					stop();
					return;
				}

				// XXX 同期がとても怪しげな麻痺判定
				if (0 < _paralysisTime) {
					schedule(_paralysisTime);
					_paralysisTime = 0;
					setParalyzed(false);
					return;
				} else if (isParalyzed() || isSleeped()) {
					schedule(200);
					return;
				}

				if (!AIProcess()) { // AIを続けるべきであれば、次の実行をスケジュールし、終了
					schedule(getSleepTime());
					return;
				}
				stop();
			} catch (Exception e) {
				_log.log(Level.WARNING, "NpcAIで例外が発生しました。", e);
			}
		}

		private boolean notContinued() {
			return _destroyed || isDead() || getCurrentHp() <= 0
					|| getHiddenStatus() != HIDDEN_STATUS_NONE;
		}
	}

	class NpcAIThreadImpl implements Runnable, NpcAI {
		@Override
		public void start() {
			GeneralThreadPool.getInstance().execute(NpcAIThreadImpl.this);
		}

		@Override
		public void run() {
			try {
				setAiRunning(true);
				while (!_destroyed && !isDead() && getCurrentHp() > 0
						&& getHiddenStatus() == HIDDEN_STATUS_NONE) {
					/*
					 * if (_paralysisTime > 0) { try {
					 * Thread.sleep(_paralysisTime); } catch (Exception
					 * exception) { break; } finally { setParalyzed(false);
					 * _paralysisTime = 0; } }
					 */
					while (isParalyzed() || isSleeped()) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							setParalyzed(false);
						}
					}

					if (AIProcess()) {
						break;
					}
					try {
						// 指定時間分スレッド停止
						Thread.sleep(getSleepTime());
					} catch (Exception e) {
						break;
					}
				}
				_mobSkill.resetAllSkillUsedCount();
				do {
					try {
						Thread.sleep(getSleepTime());
					} catch (Exception e) {
						break;
					}
				} while (isDeathProcessing());
				allTargetClear();
				setAiRunning(false);
			} catch (Exception e) {
				_log.log(Level.WARNING, "NpcAIで例外が発生しました。", e);
			}
		}
	}

	// ＡＩの処理 (返り値はＡＩ処理を終了するかどうか)
	private boolean AIProcess() {
		setSleepTime(300);

		checkTarget();
		if (_target == null && _master == null) {
			// 空っぽの場合はターゲットを探してみる
			// （主人がいる場合は自分でターゲットを探さない）
			searchTarget();
		}

		onItemUse();

		if (_target == null) {
			// ターゲットがいない場合
			checkTargetItem();
			if (isPickUpItem() && _targetItem == null) {
				// アイテム拾う子の場合はアイテムを探してみる
				searchTargetItem();
			}

			if (_targetItem == null) {
				if (noTarget()) {
					return true;
				}
			} else {
				// onTargetItem();
				L1Inventory groundInventory = L1World.getInstance()
						.getInventory(_targetItem.getX(), _targetItem.getY(),
								_targetItem.getMapId());
				if (groundInventory.checkItem(_targetItem.getItemId())) {
					onTargetItem();
				} else {
					_targetItemList.remove(_targetItem);
					_targetItem = null;
					setSleepTime(1000);
					return false;
				}
			}
		} else { // ターゲットがいる場合
			if (getHiddenStatus() == HIDDEN_STATUS_NONE) {
				onTarget();
			} else {
				return true;
			}
		}

		return false; // ＡＩ処理続行
	}

	// アイテム使用処理（Ｔｙｐｅによって結構違うのでオーバライドで実装）
	public void onItemUse() {
	}

	// ターゲットを探す（Ｔｙｐｅによって結構違うのでオーバライドで実装）
	public void searchTarget() {
	}

	// 有効なターゲットか確認及び次のターゲットを設定
	public void checkTarget() {
		if (_target == null
				|| _target.getMapId() != getMapId()
				|| _target.getCurrentHp() <= 0
				|| _target.isDead()
				|| (_target.isInvisble() && !getNpcTemplate().isAgroCoi() && !_hateList
						.containsKey(_target))) {
			if (_target != null) {
				tagertClear();
			}
			if (!_hateList.isEmpty()) {
				_target = _hateList.getMaxHateCharacter();
				checkTarget();
			}
		}
	}

	// ヘイトの設定
	public void setHate(L1Character cha, int hate) {
		if (cha != null && cha.getId() != getId()) {
			if (!isFirstAttack() && hate != 0) {
				// hate += 20; // ＦＡヘイト
				hate += getMaxHp() / 10; // ＦＡヘイト
				setFirstAttack(true);
			}

			_hateList.add(cha, hate);
			_dropHateList.add(cha, hate);
			_target = _hateList.getMaxHateCharacter();
			checkTarget();
		}
	}

	// リンクの設定
	public void setLink(L1Character cha) {
	}

	// 仲間意識によりアクティブになるＮＰＣの検索（攻撃者がプレイヤーのみ有効）
	public void serchLink(L1PcInstance targetPlayer, int family) {
		List<L1Object> targetKnownObjects = targetPlayer.getKnownObjects();
		for (Object knownObject : targetKnownObjects) {
			if (knownObject instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) knownObject;
				if (npc.getNpcTemplate().getAgroFamily() > 0) {
					// 仲間に対してアクティブになる場合
					if (npc.getNpcTemplate().getAgroFamily() == 1) {
						// 同種族に対してのみ仲間意識
						if (npc.getNpcTemplate().getFamily() == family) {
							npc.setLink(targetPlayer);
						}
					} else {
						// 全てのＮＰＣに対して仲間意識
						npc.setLink(targetPlayer);
					}
				}
				L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
				if (mobGroupInfo != null) {
					if (getMobGroupId() != 0
							&& getMobGroupId() == npc.getMobGroupId()) { // 同じグループ
						npc.setLink(targetPlayer);
					}
				}
			}
		}
	}

	private boolean useSkill(L1Character target) {
		List<L1AvailableSkill> skills = _mobSkill.availableSkills(target, true);
		if (skills.isEmpty()) { // トリガの条件に合うスキルがない
			return false;
		}
		if (_random.nextInt(2) >= 1) { // 一定の確率で物理攻撃
			return false;
		}
		return _mobSkill.useSkill(Lists.getRandomElement(skills)); // 使えるスキルの中から適当に選ぶ
	}

	// ターゲットがいる場合の処理
	public void onTarget() {
		setActived(true);
		_targetItemList.clear();
		_targetItem = null;
		L1Character target = _target; // ここから先は_targetが変わると影響出るので別領域に参照確保
		if (getAtkSpeed() == 0) { // 逃げるキャラ
			if (getPassiSpeed() > 0 && getSkillEffectTimeSec(STATUS_HOLD) <= 0) { // 移動できるキャラ
				int escapeDistance = 15;
				if (hasSkillEffect(40) == true) {
					escapeDistance = 1;
				}
				if (getLocation().getTileLineDistance(target.getLocation()) > escapeDistance) { // ターゲットから逃げるの終了
					tagertClear();
				} else { // ターゲットから逃げる
					int dir = targetReverseDirection(target.getX(), target
							.getY());
					dir = checkObject(getX(), getY(), getMapId(), dir);
					setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
				}
			}
		} else { // 逃げないキャラ
			if (isAttackPosition(target.getX(), target.getY(), getNpcTemplate()
					.getRanged())) { // 攻撃可能位置
				if (useSkill(target)) {
					setSleepTime(calcSleepTime(_mobSkill.getSleepTime(),
							MAGIC_SPEED));
				} else { // スキル使用が失敗したら物理攻撃
					setHeading(targetDirection(target.getX(), target.getY()));
					attackTarget(target);
				}
			} else { // 攻撃不可能位置
				if (useSkill(target)) { // ランダムにスキル使用
					setSleepTime(calcSleepTime(_mobSkill.getSleepTime(),
							MAGIC_SPEED));
					return;
				}

				if (getPassiSpeed() > 0 && getSkillEffectTimeSec(STATUS_HOLD) <= 0) {
					// 移動できるキャラ
					int distance = getLocation().getTileDistance(
							target.getLocation());
					if (firstFound == true && getNpcTemplate().isTeleport()
							&& distance > 3 && distance < 15) {
						if (nearTeleport(target.getX(), target.getY()) == true) {
							firstFound = false;
							return;
						}
					}

					if (getNpcTemplate().isTeleport()
							&& 20 > _random.nextInt(100)
							&& getCurrentMp() >= 10 && distance > 6
							&& distance < 15) { // テレポート移動
						if (nearTeleport(target.getX(), target.getY()) == true) {
							return;
						}
					}
					int dir = moveDirection(target.getX(), target.getY());
					if (dir == -1) {
						tagertClear();
					} else {
						setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
					}
				} else if (getPassiSpeed() <= 0) {
					// 移動できないキャラ（ターゲットから排除、ＰＴのときドロップチャンスがリセットされるけどまぁ自業自得）
					tagertClear();
				}
			}
		}
	}

	// 目標を指定のスキルで攻撃
	public void attackTarget(L1Character target) {

		if (target instanceof L1PcInstance) {
			L1PcInstance player = (L1PcInstance) target;
			if (player.isTeleport()) { // テレポート処理中
				return;
			}
		} else if (target instanceof L1PetInstance) {
			L1PetInstance pet = (L1PetInstance) target;
			L1Character cha = pet.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // テレポート処理中
					return;
				}
			}
		} else if (target instanceof L1SummonInstance) {
			L1SummonInstance summon = (L1SummonInstance) target;
			L1Character cha = summon.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // テレポート処理中
					return;
				}
			}
		}
		if (this instanceof L1PetInstance) {
			L1PetInstance pet = (L1PetInstance) this;
			L1Character cha = pet.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // テレポート処理中
					return;
				}
			}
		} else if (this instanceof L1SummonInstance) {
			L1SummonInstance summon = (L1SummonInstance) this;
			L1Character cha = summon.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // テレポート処理中
					return;
				}
			}
		}

		if (target instanceof L1NpcInstance) {
			L1NpcInstance npc = (L1NpcInstance) target;
			if (npc.getHiddenStatus() != HIDDEN_STATUS_NONE) { // 地中に潜っているか、飛んでいる
				allTargetClear();
				return;
			}
		}

		boolean isCounterBarrier = false;
		boolean isStormBarrier = false;
		L1PcInstance pc = _pc;
		L1Attack attack = new L1Attack(this, target);
		if (target instanceof L1PcInstance) {
			int rnd = _random.nextInt(100) + 1;
			if (target.getInventory().checkEquipped(21180)	// リンドビオルストームシリーズ
				|| target.getInventory().checkEquipped(21181)
				|| target.getInventory().checkEquipped(21182)
				|| target.getInventory().checkEquipped(21183)) {
				boolean isStormProbability = (rnd <= 8); // 8%の確率
				boolean isLongDistance = attack.isLongDistance();
				if (isStormProbability && isLongDistance) {
					isStormBarrier = true;
				}
			}
		}
		if (attack.calcHit()) {
			if (target.hasSkillEffect(COUNTER_BARRIER)) {
				L1Magic magic = new L1Magic(target, this);
				boolean isProbability = magic
						.calcProbabilityMagic(COUNTER_BARRIER);
				boolean isShortDistance = attack.isShortDistance();
				if (isProbability && isShortDistance) {
					isCounterBarrier = true;
				}
			}
			if (!isCounterBarrier || !isStormBarrier) {
				attack.calcDamage();
			} else {
					attack.calcDamage();
			}
			// 目標-案山子
			if (target instanceof L1ScarecrowInstance) {
				if (target.getHeading() < 7) {
					target.setHeading(target.getHeading() + 1);
				} else {
					target.setHeading(0);
				}
				target.broadcastPacket(new S_ChangeHeading(target));
			}
		}
		if (isCounterBarrier) {
			attack.actionCounterBarrier();
			attack.commitCounterBarrier();
		} else if (isStormBarrier) {
			attack.actionStormBarrier();
			attack.commitStormBarrier();
		} else {
			attack.action();
			attack.commit();
		}
		setSleepTime(calcSleepTime(getAtkSpeed(), ATTACK_SPEED));
	}

	// ターゲットアイテムを探す
	public void searchTargetItem() {
		ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

		for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
			if (obj != null && obj instanceof L1GroundInventory) {
				gInventorys.add((L1GroundInventory) obj);
			}
		}
		if (gInventorys.size() == 0) {
			return;
		}

		// 拾うアイテム(のインベントリ)をランダムで選定
		int pickupIndex = (int) (Math.random() * gInventorys.size());
		L1GroundInventory inventory = gInventorys.get(pickupIndex);
		for (L1ItemInstance item : inventory.getItems()) {
			if (getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) { // 持てるならターゲットアイテムに加える
				_targetItem = item;
				_targetItemList.add(_targetItem);
			}
		}
	}

	// 飛んでいる状態からアイテムを探し、あれば降りて拾う
	public void searchItemFromAir() {
		ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

		for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
			if (obj != null && obj instanceof L1GroundInventory) {
				gInventorys.add((L1GroundInventory) obj);
			}
		}
		if (gInventorys.size() == 0) {
			return;
		}

		// 拾うアイテム(のインベントリ)をランダムで選定
		int pickupIndex = (int) (Math.random() * gInventorys.size());
		L1GroundInventory inventory = gInventorys.get(pickupIndex);
		for (L1ItemInstance item : inventory.getItems()) {
			if (item.getItem().getType() == 6 // potion
					|| item.getItem().getType() == 7) { // food
				if (getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
					if (getHiddenStatus() == HIDDEN_STATUS_FLY
							&& getNpcTemplate().getNpcId() != 46291) { // シェルマン以外
						setHiddenStatus(HIDDEN_STATUS_NONE);
						broadcastPacket(new S_DoActionGFX(getId(),
								ActionCodes.ACTION_Movedown));
						setStatus(0);
						broadcastPacket(new S_NpcPack(this));
						onNpcAI();
						startChat(CHAT_TIMING_HIDE);
						_targetItem = item;
						_targetItemList.add(_targetItem);
					}
				}
			}
		}
	}

	// シェルマンのHide解除
	public void appearSwelmaen() {
		if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
			setHiddenStatus(HIDDEN_STATUS_NONE);
			broadcastPacket(new S_DoActionGFX(getId(),
			ActionCodes.ACTION_Appear));
			setStatus(0);
			broadcastPacket(new S_NpcPack(this));
			onNpcAI();
		}
	}

	public static void shuffle(L1Object[] arr) {
		for (int i = arr.length - 1; i > 0; i--) {
			int t = (int) (Math.random() * i);

			// 選ばれた値と交換する
			L1Object tmp = arr[i];
			arr[i] = arr[t];
			arr[t] = tmp;
		}
	}

	// 有効なターゲットアイテムか確認及び次のターゲットアイテムを設定
	public void checkTargetItem() {
		if (_targetItem == null
				|| _targetItem.getMapId() != getMapId()
				|| getLocation().getTileDistance(_targetItem.getLocation()) > 15) {
			if (!_targetItemList.isEmpty()) {
				_targetItem = _targetItemList.get(0);
				_targetItemList.remove(0);
				checkTargetItem();
			} else {
				_targetItem = null;
			}
		}
	}

	// ターゲットアイテムがある場合の処理
	public void onTargetItem() {
		if (getLocation().getTileLineDistance(_targetItem.getLocation()) == 0) { // ピックアップ可能位置
			pickupTargetItem(_targetItem);
		} else { // ピックアップ不可能位置
			int dir = moveDirection(_targetItem.getX(), _targetItem.getY());
			if (dir == -1) { // 拾うの諦め
				_targetItemList.remove(_targetItem);
				_targetItem = null;
			} else { // ターゲットアイテムへ移動
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
			}
		}
	}

	// アイテムを拾う
	public void pickupTargetItem(L1ItemInstance targetItem) {
		int count = targetItem.getCount();
		L1Inventory groundInventory = L1World.getInstance().getInventory(
				targetItem.getX(), targetItem.getY(), targetItem.getMapId());
		L1ItemInstance item = groundInventory.tradeItem(targetItem, targetItem
				.getCount(), getInventory());
		updateLight();
		onGetItem(item, count);
		_targetItemList.remove(_targetItem);
		_targetItem = null;
		setSleepTime(1000);
	}

	// ターゲットがいない場合の処理 (返り値はＡＩ処理を終了するかどうか)
	public boolean noTarget() {
		if (_master != null && _master.getMapId() == getMapId()
				&& getLocation().getTileLineDistance(_master.getLocation()) > 2) { // 主人が同じマップにいて離れてる場合は追尾
			int dir = moveDirection(_master.getX(), _master.getY());
			if (dir != -1) {
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
			} else {
				return true;
			}
		} else {
			if (L1World.getInstance().getRecognizePlayer(this).size() == 0) {
				return true; // 周りにプレイヤーがいなくなったらＡＩ処理終了
			}
			// 移動できるキャラはランダムに動いておく
			if (_master == null && getPassiSpeed() > 0 && !isRest() && getSkillEffectTimeSec(STATUS_HOLD) <= 0) {
				// グループに属していないorグループに属していてリーダーの場合、ランダムに動いておく
				L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
				if (mobGroupInfo == null || mobGroupInfo != null
						&& mobGroupInfo.isLeader(this)) {
					// 移動する予定の距離を移動し終えたら、新たに距離と方向を決める
					// そうでないなら、移動する予定の距離をデクリメント
					if (_randomMoveDistance == 0) {
						_randomMoveDistance = _random.nextInt(5) + 1;
						_randomMoveDirection = _random.nextInt(20);
						// ホームポイントから離れすぎないように、一定の確率でホームポイントの方向に補正
						if (getHomeX() != 0 && getHomeY() != 0
								&& _randomMoveDirection < 8
								&& _random.nextInt(3) == 0) {
							_randomMoveDirection = moveDirection(getHomeX(),
									getHomeY());
						}
					} else {
						_randomMoveDistance--;
					}
					int dir = checkObject(getX(), getY(), getMapId(),
							_randomMoveDirection);
					if (dir != -1) {
						setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
					}
				} else { // リーダーを追尾
					L1NpcInstance leader = mobGroupInfo.getLeader();
					if (getLocation().getTileLineDistance(leader.getLocation()) > 2) {
						int dir = moveDirection(leader.getX(), leader.getY());
						if (dir == -1) {
							return true;
						} else {
							setDirectionMove(dir);
							setSleepTime(calcSleepTime(getPassiSpeed(),
									MOVE_SPEED));
						}
					}
				}
			}
		}
		return false;
	}

	public void onFinalAction(L1PcInstance pc, String s) {
	}

	// 現在のターゲットを削除
	public void tagertClear() {
		if (_target == null) {
			return;
		}
		_hateList.remove(_target);
		_target = null;
	}

	// 指定されたターゲットを削除
	public void targetRemove(L1Character target) {
		_hateList.remove(target);
		if (_target != null && _target.equals(target)) {
			_target = null;
		}
	}

	// 全てのターゲットを削除
	public void allTargetClear() {
		_hateList.clear();
		_dropHateList.clear();
		_target = null;
		_targetItemList.clear();
		_targetItem = null;
	}

	// マスターの設定
	public void setMaster(L1Character cha) {
		_master = cha;
	}

	// マスターの取得
	public L1Character getMaster() {
		return _master;
	}

	// ＡＩトリガ
	public void onNpcAI() {
	}

	// アイテム精製
	public void refineItem() {

		int[] materials = null;
		int[] counts = null;
		int[] createitem = null;
		int[] createcount = null;

		if (_npcTemplate.getNpcId() == 45032) { // ブロッブ
			// オリハルコンソードの刀身
			if (getExp() != 0 && !_inventory.checkItem(20)) {
				materials = new int[] { 40508, 40521, 40045 };
				counts = new int[] { 150, 3, 3 };
				createitem = new int[] { 20 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// ロングソードの刀身
			if (getExp() != 0 && !_inventory.checkItem(19)) {
				materials = new int[] { 40494, 40521 };
				counts = new int[] { 150, 3 };
				createitem = new int[] { 19 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// ショートソードの刀身
			if (getExp() != 0 && !_inventory.checkItem(3)) {
				materials = new int[] { 40494, 40521 };
				counts = new int[] { 50, 1 };
				createitem = new int[] { 3 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// オリハルコンホーン
			if (getExp() != 0 && !_inventory.checkItem(100)) {
				materials = new int[] { 88, 40508, 40045 };
				counts = new int[] { 4, 80, 3 };
				createitem = new int[] { 100 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// ミスリルホーン
			if (getExp() != 0 && !_inventory.checkItem(89)) {
				materials = new int[] { 88, 40494 };
				counts = new int[] { 2, 80 };
				createitem = new int[] { 89 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						L1ItemInstance item = _inventory.storeItem(
								createitem[j], createcount[j]);
						if (getNpcTemplate().getDigestItem() > 0) {
							setDigestItem(item);
						}
					}
				}
			}
		} else if (_npcTemplate.getNpcId() == 81069) { // ドッペルゲンガー（クエスト）
			// ドッペルゲンガーの体液
			if (getExp() != 0 && !_inventory.checkItem(40542)) {
				materials = new int[] { 40032 };
				counts = new int[] { 1 };
				createitem = new int[] { 40542 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
		} else if (_npcTemplate.getNpcId() == 45166 // ジャックオーランタン
				|| _npcTemplate.getNpcId() == 45167) {
			// パンプキンの種
			if (getExp() != 0 && !_inventory.checkItem(40726)) {
				materials = new int[] { 40725 };
				counts = new int[] { 1 };
				createitem = new int[] { 40726 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
		}
	}

	private boolean _aiRunning = false; // ＡＩが実行中か
	// ※ＡＩをスタートさせる時にすでに実行されてないか確認する時に使用
	private boolean _actived = false; // ＮＰＣがアクティブか
	// ※この値がfalseで_targetがいる場合、アクティブになって初行動とみなしヘイストポーション等を使わせる判定で使用
	private boolean _firstAttack = false; // ファーストアッタクされたか
	private int _sleep_time; // ＡＩを停止する時間(ms) ※行動を起こした場合に所要する時間をセット
	protected L1HateList _hateList = new L1HateList();
	protected L1HateList _dropHateList = new L1HateList();
	// ※攻撃するターゲットの判定とＰＴ時のドロップ判定で使用
	protected List<L1ItemInstance> _targetItemList = new ArrayList<L1ItemInstance>(); // ダーゲットアイテム一覧
	protected L1Character _target = null; // 現在のターゲット
	protected L1ItemInstance _targetItem = null; // 現在のターゲットアイテム
	protected L1Character _master = null; // 主人orグループリーダー
	private boolean _deathProcessing = false; // 死亡処理中か
	// EXP、Drop分配中はターゲットリスト、ヘイトリストをクリアしない

	private int _paralysisTime = 0; // Paralysis RestTime

	public void setParalysisTime(int ptime) {
		_paralysisTime = ptime;
	}

	public L1HateList getHateList() {
		return _hateList;
	}

	public int getParalysisTime() {
		return _paralysisTime;
	}

	// HP自然回復
	public final void startHpRegeneration() {
		int hprInterval = getNpcTemplate().getHprInterval();
		int hpr = getNpcTemplate().getHpr();
		if (!_hprRunning && hprInterval > 0 && hpr > 0) {
			_hprTimer = new HprTimer(hpr);
			L1NpcRegenerationTimer.getInstance().scheduleAtFixedRate(_hprTimer,
					hprInterval, hprInterval);
			_hprRunning = true;
		}
	}

	public final void stopHpRegeneration() {
		if (_hprRunning) {
			_hprTimer.cancel();
			_hprRunning = false;
		}
	}

	// MP自然回復
	public final void startMpRegeneration() {
		int mprInterval = getNpcTemplate().getMprInterval();
		int mpr = getNpcTemplate().getMpr();
		if (!_mprRunning && mprInterval > 0 && mpr > 0) {
			_mprTimer = new MprTimer(mpr);
			L1NpcRegenerationTimer.getInstance().scheduleAtFixedRate(_mprTimer,
					mprInterval, mprInterval);
			_mprRunning = true;
		}
	}

	public final void stopMpRegeneration() {
		if (_mprRunning) {
			_mprTimer.cancel();
			_mprRunning = false;
		}
	}

	// ■■■■■■■■■■■■ タイマー関連 ■■■■■■■■■■

	// ＨＰ自然回復
	private boolean _hprRunning = false;

	private HprTimer _hprTimer;

	class HprTimer extends TimerTask {
		@Override
		public void run() {
			try {
				if ((!_destroyed && !isDead())
						&& (getCurrentHp() > 0 && getCurrentHp() < getMaxHp())) {
					setCurrentHp(getCurrentHp() + _point);
				} else {
					cancel();
					_hprRunning = false;
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}

		public HprTimer(int point) {
			if (point < 1) {
				point = 1;
			}
			_point = point;
		}

		private final int _point;
	}

	// ＭＰ自然回復
	private boolean _mprRunning = false;

	private MprTimer _mprTimer;

	class MprTimer extends TimerTask {
		@Override
		public void run() {
			try {
				if ((!_destroyed && !isDead())
						&& (getCurrentHp() > 0 && getCurrentMp() < getMaxMp())) {
					setCurrentMp(getCurrentMp() + _point);
				} else {
					cancel();
					_mprRunning = false;
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}

		public MprTimer(int point) {
			if (point < 1) {
				point = 1;
			}
			_point = point;
		}

		private final int _point;
	}

	// アイテム消化
	private Map<Integer, Integer> _digestItems;
	public boolean _digestItemRunning = false;

	class DigestItemTimer implements Runnable {
		@Override
		public void run() {
			_digestItemRunning = true;
			while (!_destroyed && _digestItems.size() > 0) {
				try {
					Thread.sleep(1000);
				} catch (Exception exception) {
					break;
				}

				Object[] keys = _digestItems.keySet().toArray();
				for (int i = 0; i < keys.length; i++) {
					Integer key = (Integer) keys[i];
					Integer digestCounter = _digestItems.get(key);
					digestCounter -= 1;
					if (digestCounter <= 0) {
						_digestItems.remove(key);
						L1ItemInstance digestItem = getInventory().getItem(key);
						if (digestItem != null) {
							getInventory().removeItem(digestItem,
									digestItem.getCount());
						}
					} else {
						_digestItems.put(key, digestCounter);
					}
				}
			}
			_digestItemRunning = false;
		}
	}

	// ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■

	public L1NpcInstance(L1Npc template) {
		setStatus(0);
		setMoveSpeed(0);
		setDead(false);
		setStatus(0);
		setreSpawn(false);

		if (template != null) {
			setting_template(template);
		}
	}

	// 指定のテンプレートで各種値を初期化
	public void setting_template(L1Npc template) {
		_npcTemplate = template;
		int randomlevel = 0;
		double rate = 0;
		double diff = 0;
		setName(template.getName());
		setNameId(template.getNameId());
		if (template.getRandomLevel() == 0) { // ランダムLv指定なし
			setLevel(template.getLevel());
		} else { // ランダムLv指定あり（最小値:getLevel(),最大値:getRandomLevel()）
			randomlevel = _random.nextInt(template.getRandomLevel()
					- template.getLevel() + 1);
			diff = template.getRandomLevel() - template.getLevel();
			rate = randomlevel / diff;
			randomlevel += template.getLevel();
			setLevel(randomlevel);
		}
		if (template.getRandomHp() == 0) {
			setMaxHp(template.getHp());
			setCurrentHpDirect(template.getHp());
		} else {
			double randomhp = rate
					* (template.getRandomHp() - template.getHp());
			int hp = (int) (template.getHp() + randomhp);
			setMaxHp(hp);
			setCurrentHpDirect(hp);
		}
		if (template.getRandomMp() == 0) {
			setMaxMp(template.getMp());
			setCurrentMpDirect(template.getMp());
		} else {
			double randommp = rate
					* (template.getRandomMp() - template.getMp());
			int mp = (int) (template.getMp() + randommp);
			setMaxMp(mp);
			setCurrentMpDirect(mp);
		}
		if (template.getRandomAc() == 0) {
			setAc(template.getAc());
		} else {
			double randomac = rate
					* (template.getRandomAc() - template.getAc());
			int ac = (int) (template.getAc() + randomac);
			setAc(ac);
		}
		if (template.getRandomLevel() == 0) {
			setStr(template.getStr());
			setCon(template.getCon());
			setDex(template.getDex());
			setInt(template.getInt());
			setWis(template.getWis());
			setMr(template.getMr());
		} else {
			setStr((byte) Math.min(template.getStr() + diff, 127));
			setCon((byte) Math.min(template.getCon() + diff, 127));
			setDex((byte) Math.min(template.getDex() + diff, 127));
			setInt((byte) Math.min(template.getInt() + diff, 127));
			setWis((byte) Math.min(template.getWis() + diff, 127));
			setMr((byte) Math.min(template.getMr() + diff, 127));

			addHitup((int) diff * 2);
			addDmgup((int) diff * 2);
		}
		setPassiSpeed(template.getPassiSpeed());
		setAtkSpeed(template.getAtkSpeed());
		setAgro(template.isAgro());
		setAgroCoi(template.isAgroCoi());
		setAgroSosc(template.isAgroSosc());
		setTempCharGfx(template.getGfxId());
		setGfxId(template.getGfxId());
		if (template.getRandomExp() == 0) {
			setExp(template.getExp());
		} else {
			int level = getLevel();
			int exp = level * level;
			exp += 1;
			setExp(exp);
		}
		if (template.getRandomLawful() == 0) {
			setLawful(template.getLawful());
			setTempLawful(template.getLawful());
		} else {
			double randomlawful = rate
					* (template.getRandomLawful() - template.getLawful());
			int lawful = (int) (template.getLawful() + randomlawful);
			setLawful(lawful);
			setTempLawful(lawful);
		}
		setPickUpItem(template.isPickUpItem());
		if (template.isBraveSpeed()) {
			setBraveSpeed(1);
		} else {
			setBraveSpeed(0);
		}
		if (template.getDigestItem() > 0) {
			_digestItems = new HashMap<Integer, Integer>();
		}
		setKarma(template.getKarma());
		_mobSkill = new L1MobSkillUse(this);
	}

	// 遅延時間
	public void setDelayTime(int i, int type) {
		setSleepTime(calcSleepTime(SprTable.getInstance()
				.getMoveSpeed(getTempCharGfx(), i), type));
	}

	private int _passiSpeed;

	public int getPassiSpeed() {
		return _passiSpeed;
	}

	public void setPassiSpeed(int i) {
		_passiSpeed = i;
	}

	private int _atkSpeed;

	public int getAtkSpeed() {
		return _atkSpeed;
	}

	public void setAtkSpeed(int i) {
		_atkSpeed = i;
	}

	private boolean _pickupItem;

	public boolean isPickUpItem() {
		return _pickupItem;
	}

	public void setPickUpItem(boolean flag) {
		_pickupItem = flag;
	}

	@Override
	public L1Inventory getInventory() {
		return _inventory;
	}

	public void setInventory(L1Inventory inventory) {
		_inventory = inventory;
	}

	public L1Npc getNpcTemplate() {
		return _npcTemplate;
	}

	public int getNpcId() {
		return _npcTemplate.getNpcId();
	}

	public void setPetcost(int i) {
		_petcost = i;
	}

	public int getPetcost() {
		return _petcost;
	}

	public void setSpawn(L1Spawn spawn) {
		_spawn = spawn;
	}

	public L1Spawn getSpawn() {
		return _spawn;
	}

	public void setSpawnNumber(int number) {
		_spawnNumber = number;
	}

	public int getSpawnNumber() {
		return _spawnNumber;
	}

	// オブジェクトIDをSpawnTaskに渡し再利用する
	// グループモンスターは複雑になるので再利用しない
	public void onDecay(boolean isReuseId) {
		int id = 0;
		if (isReuseId) {
			id = getId();
		} else {
			id = 0;
		}
		_spawn.executeSpawnTask(_spawnNumber, id);
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_NpcPack(this));
		onNpcAI();
	}

	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		List<L1PcInstance> players = L1World.getInstance().getRecognizePlayer(
				this);
		if (players.size() > 0) {
			S_RemoveObject s_deleteNewObject = new S_RemoveObject(this);
			for (L1PcInstance pc : players) {
				if (pc != null) {
					pc.removeKnownObject(this);
					// if(!L1Character.distancepc(user, this))
					pc.sendPackets(s_deleteNewObject);
				}
			}
		}
		removeAllKnownObjects();
		// XXX
		if (getMap().isInMap(getX(), getY())) {
			getMap().setPassable(getX(), getY(), true);
		}
		// リスパウン設定
		L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
		if (mobGroupInfo == null) {
			if (isReSpawn()) {
				onDecay(true);
			}
		} else {
			if (mobGroupInfo.removeMember(this) == 0) { // グループメンバー全滅
				doExecutionWhenNpcDied(this);
				setMobGroupInfo(null);
				if (isReSpawn()) {
					onDecay(false);
				}
			}
		}

	}

	public void ReceiveManaDamage(L1Character attacker, int damageMp) {
	}

	public void receiveDamage(L1Character attacker, int damage) {
	}

	public void setDigestItem(L1ItemInstance item) {
		_digestItems.put(new Integer(item.getId()), new Integer(
				getNpcTemplate().getDigestItem()));
		if (!_digestItemRunning) {
			DigestItemTimer digestItemTimer = new DigestItemTimer();
			GeneralThreadPool.getInstance().execute(digestItemTimer);
		}
	}

	public void onGetItem(L1ItemInstance item, int count) {
		refineItem();
		getInventory().shuffle();
		if (getNpcTemplate().getDigestItem() > 0) {
			setDigestItem(item);
		}
	}

	public void approachPlayer(L1PcInstance pc) {
		if (pc.hasSkillEffect(60) || pc.hasSkillEffect(97)) { // インビジビリティ、ブラインドハイディング中
			return;
		}
		if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
			if (getCurrentHp() == getMaxHp()) {
				if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 2) {
					appearOnGround(pc);
				}
			}
		} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
			if (getCurrentHp() == getMaxHp()) {
				if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 1) {
					appearOnGround(pc);
				}
			} else {
				// if (getNpcTemplate().getNpcId() != 45681) { // リンドビオル以外
				searchItemFromAir();
				// }
			}
		} else if (getHiddenStatus() == HIDDEN_STATUS_ICE) {
			if (getCurrentHp() < getMaxHp()) {
				appearOnGround(pc);
			}
		}
	}

	public void appearOnGround(L1PcInstance pc) {
		if (getHiddenStatus() == HIDDEN_STATUS_SINK
				|| getHiddenStatus() == HIDDEN_STATUS_ICE) {
			setHiddenStatus(HIDDEN_STATUS_NONE);
			broadcastPacket(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_Appear));
			setStatus(0);
			broadcastPacket(new S_NpcPack(this));
			if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) // インビジビリティ、ブラインドハイディング中以外、GM以外
					&& !pc.isGm()) {
				_hateList.add(pc, 0);
				_target = pc;
			}
			onNpcAI(); // モンスターのＡＩを開始
		} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
			setHiddenStatus(HIDDEN_STATUS_NONE);
			broadcastPacket(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_Movedown));
			setStatus(0);
			broadcastPacket(new S_NpcPack(this));
			if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) // インビジビリティ、ブラインドハイディング中以外、GM以外
					&& !pc.isGm()) {
				_hateList.add(pc, 0);
				_target = pc;
			}
			onNpcAI(); // モンスターのＡＩを開始
			startChat(CHAT_TIMING_HIDE);
		}
	}

	// ■■■■■■■■■■■■■ 移動関連 ■■■■■■■■■■■

	// 指定された方向に移動させる
	public void setDirectionMove(int dir) {
		if (dir >= 0) {
			int nx = 0;
			int ny = 0;

			switch (dir) {
			case 1:
				nx = 1;
				ny = -1;
				setHeading(1);
				break;

			case 2:
				nx = 1;
				ny = 0;
				setHeading(2);
				break;

			case 3:
				nx = 1;
				ny = 1;
				setHeading(3);
				break;

			case 4:
				nx = 0;
				ny = 1;
				setHeading(4);
				break;

			case 5:
				nx = -1;
				ny = 1;
				setHeading(5);
				break;

			case 6:
				nx = -1;
				ny = 0;
				setHeading(6);
				break;

			case 7:
				nx = -1;
				ny = -1;
				setHeading(7);
				break;

			case 0:
				nx = 0;
				ny = -1;
				setHeading(0);
				break;

			default:
				break;

			}

			getMap().setPassable(getLocation(), true);

			int nnx = getX() + nx;
			int nny = getY() + ny;
			setX(nnx);
			setY(nny);

			getMap().setPassable(getLocation(), false);

			broadcastPacket(new S_MoveCharPacket(this));

			// movement_distanceマス以上離れたらホームポイントへテレポート
			if (getMovementDistance() > 0) {
				if (this instanceof L1GuardInstance
						|| this instanceof L1MerchantInstance
						|| this instanceof L1MonsterInstance) {
					if (getLocation().getLineDistance(
							new Point(getHomeX(), getHomeY())) > getMovementDistance()) {
						teleport(getHomeX(), getHomeY(), getHeading());
					}
				}
			}
			// 恨みに満ちたソルジャーゴースト、恨みに満ちたゴースト、恨みに満ちたハメル将軍
			if (getNpcTemplate().getNpcId() >= 45912
					&& getNpcTemplate().getNpcId() <= 45916) {
				if (getX() >= 32638 && getX() <= 32596 && getY() >= 32697
						&& getY() <= 32642 && getMapId() == 4) {
					teleport(getHomeX(), getHomeY(), getHeading());
				}
			}
		}
	}

	public int moveDirection(int x, int y) { // 目標点Ｘ 目標点Ｙ
		return moveDirection(x, y, getLocation().getLineDistance(
				new Point(x, y)));
	}

	// 目標までの距離に応じて最適と思われるルーチンで進む方向を返す
	public int moveDirection(int x, int y, double d) { // 目標点Ｘ 目標点Ｙ 目標までの距離
		int dir = 0;
		if (hasSkillEffect(40) == true && d >= 2D) { // ダークネスが掛かっていて、距離が2以上の場合追跡終了
			return -1;
		} else if (d > 30D) { // 距離が激しく遠い場合は追跡終了
			return -1;
		} else if (d > courceRange) { // 距離が遠い場合は単純計算
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

	protected boolean isExsistCharacterBetweenTarget(int dir) {
		if (!(this instanceof L1MonsterInstance)) { // モンスター以外は対象外
			return false;
		}
		if (_target == null) { // ターゲットがいない場合
			return false;
		}

		int locX = getX();
		int locY = getY();
		int targetX = locX;
		int targetY = locY;

		if (dir == 1) {
			targetX = locX + 1;
			targetY = locY - 1;
		} else if (dir == 2) {
			targetX = locX + 1;
		} else if (dir == 3) {
			targetX = locX + 1;
			targetY = locY + 1;
		} else if (dir == 4) {
			targetY = locY + 1;
		} else if (dir == 5) {
			targetX = locX - 1;
			targetY = locY + 1;
		} else if (dir == 6) {
			targetX = locX - 1;
		} else if (dir == 7) {
			targetX = locX - 1;
			targetY = locY - 1;
		} else if (dir == 0) {
			targetY = locY - 1;
		}

		for (L1Object object : L1World.getInstance().getVisibleObjects(this, 1)) {
			// PC, Summon, Petがいる場合
			if (object instanceof L1PcInstance
					|| object instanceof L1SummonInstance
					|| object instanceof L1PetInstance) {
				L1Character cha = (L1Character) object;
				// 進行方向に立ちふさがっている場合、ターゲットリストに加える
				if (cha.getX() == targetX && cha.getY() == targetY
						&& cha.getMapId() == getMapId()) {
					if (object instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) object;
						if (pc.isGhost()) { // UB観戦中のPCは除く
							continue;
						}
					}
					_hateList.add(cha, 0);
					_target = cha;
					return true;
				}
			}
		}
		return false;
	}

	// 目標の逆方向を返す
	public int targetReverseDirection(int tx, int ty) { // 目標点Ｘ 目標点Ｙ
		int dir = targetDirection(tx, ty);
		dir += 4;
		if (dir > 7) {
			dir -= 8;
		}
		return dir;
	}

	// 進みたい方向に障害物がないか確認、ある場合は前方斜め左右も確認後進める方向を返す
	// ※従来あった処理に、バックできない仕様を省いて、目標の反対（左右含む）には進まないようにしたもの
	public static int checkObject(int x, int y, short m, int d) { // 起点Ｘ 起点Ｙ
		// マップＩＤ
		// 進行方向
		L1Map map = L1WorldMap.getInstance().getMap(m);
		if (d == 1) {
			if (map.isPassable(x, y, 1)) {
				return 1;
			} else if (map.isPassable(x, y, 0)) {
				return 0;
			} else if (map.isPassable(x, y, 2)) {
				return 2;
			}
		} else if (d == 2) {
			if (map.isPassable(x, y, 2)) {
				return 2;
			} else if (map.isPassable(x, y, 1)) {
				return 1;
			} else if (map.isPassable(x, y, 3)) {
				return 3;
			}
		} else if (d == 3) {
			if (map.isPassable(x, y, 3)) {
				return 3;
			} else if (map.isPassable(x, y, 2)) {
				return 2;
			} else if (map.isPassable(x, y, 4)) {
				return 4;
			}
		} else if (d == 4) {
			if (map.isPassable(x, y, 4)) {
				return 4;
			} else if (map.isPassable(x, y, 3)) {
				return 3;
			} else if (map.isPassable(x, y, 5)) {
				return 5;
			}
		} else if (d == 5) {
			if (map.isPassable(x, y, 5)) {
				return 5;
			} else if (map.isPassable(x, y, 4)) {
				return 4;
			} else if (map.isPassable(x, y, 6)) {
				return 6;
			}
		} else if (d == 6) {
			if (map.isPassable(x, y, 6)) {
				return 6;
			} else if (map.isPassable(x, y, 5)) {
				return 5;
			} else if (map.isPassable(x, y, 7)) {
				return 7;
			}
		} else if (d == 7) {
			if (map.isPassable(x, y, 7)) {
				return 7;
			} else if (map.isPassable(x, y, 6)) {
				return 6;
			} else if (map.isPassable(x, y, 0)) {
				return 0;
			}
		} else if (d == 0) {
			if (map.isPassable(x, y, 0)) {
				return 0;
			} else if (map.isPassable(x, y, 7)) {
				return 7;
			} else if (map.isPassable(x, y, 1)) {
				return 1;
			}
		}
		return -1;
	}

	// 目標までの最短経路の方向を返す
	// ※目標を中心とした探索範囲のマップで探索
	protected int _serchCource(int x, int y) // 目標点Ｘ 目標点Ｙ
	{
		int i;
		int locCenter = courceRange + 1;
		int diff_x = x - locCenter; // Ｘの実際のロケーションとの差
		int diff_y = y - locCenter; // Ｙの実際のロケーションとの差
		int[] locBace = { getX() - diff_x, getY() - diff_y, 0, 0 }; // Ｘ Ｙ
		// 方向
		// 初期方向
		int[] locNext = new int[4];
		int[] locCopy;
		int[] dirFront = new int[5];
		boolean serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
		LinkedList<int[]> queueSerch = new LinkedList<int[]>();

		// 探索用マップの設定
		for (int j = courceRange * 2 + 1; j > 0; j--) {
			for (i = courceRange - Math.abs(locCenter - j); i >= 0; i--) {
				serchMap[j][locCenter + i] = true;
				serchMap[j][locCenter - i] = true;
			}
		}

		// 初期方向の設置
		int[] firstCource = { 2, 4, 6, 0, 1, 3, 5, 7 };
		for (i = 0; i < 8; i++) {
			System.arraycopy(locBace, 0, locNext, 0, 4);
			_moveLocation(locNext, firstCource[i]);
			if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
				// 最短経路が見つかった場合:隣
				return firstCource[i];
			}
			if (serchMap[locNext[0]][locNext[1]]) {
				int tmpX = locNext[0] + diff_x;
				int tmpY = locNext[1] + diff_y;
				boolean found = false;
				if (i == 0) {
					found = getMap().isPassable(tmpX, tmpY + 1, i);
				} else if (i == 1) {
					found = getMap().isPassable(tmpX - 1, tmpY + 1, i);
				} else if (i == 2) {
					found = getMap().isPassable(tmpX - 1, tmpY, i);
				} else if (i == 3) {
					found = getMap().isPassable(tmpX - 1, tmpY - 1, i);
				} else if (i == 4) {
					found = getMap().isPassable(tmpX, tmpY - 1, i);
				} else if (i == 5) {
					found = getMap().isPassable(tmpX + 1, tmpY - 1, i);
				} else if (i == 6) {
					found = getMap().isPassable(tmpX + 1, tmpY, i);
				} else if (i == 7) {
					found = getMap().isPassable(tmpX + 1, tmpY + 1, i);
				}
				if (found)// 移動経路があった場合
				{
					locCopy = new int[4];
					System.arraycopy(locNext, 0, locCopy, 0, 4);
					locCopy[2] = firstCource[i];
					locCopy[3] = firstCource[i];
					queueSerch.add(locCopy);
				}
				serchMap[locNext[0]][locNext[1]] = false;
			}
		}
		locBace = null;

		// 最短経路を探索
		while (queueSerch.size() > 0) {
			locBace = queueSerch.removeFirst();
			_getFront(dirFront, locBace[2]);
			for (i = 4; i >= 0; i--) {
				System.arraycopy(locBace, 0, locNext, 0, 4);
				_moveLocation(locNext, dirFront[i]);
				if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
					return locNext[3];
				}
				if (serchMap[locNext[0]][locNext[1]]) {
					int tmpX = locNext[0] + diff_x;
					int tmpY = locNext[1] + diff_y;
					boolean found = false;
					if (i == 0) {
						found = getMap().isPassable(tmpX, tmpY + 1, i);
					} else if (i == 1) {
						found = getMap().isPassable(tmpX - 1, tmpY + 1, i);
					} else if (i == 2) {
						found = getMap().isPassable(tmpX - 1, tmpY, i);
					} else if (i == 3) {
						found = getMap().isPassable(tmpX - 1, tmpY - 1, i);
					} else if (i == 4) {
						found = getMap().isPassable(tmpX, tmpY - 1, i);
					}
					if (found) // 移動経路があった場合
					{
						locCopy = new int[4];
						System.arraycopy(locNext, 0, locCopy, 0, 4);
						locCopy[2] = dirFront[i];
						queueSerch.add(locCopy);
					}
					serchMap[locNext[0]][locNext[1]] = false;
				}
			}
			locBace = null;
		}
		return -1; // 目標までの経路がない場合
	}

	private void _moveLocation(int[] ary, int d) {
		if (d == 1) {
			ary[0] = ary[0] + 1;
			ary[1] = ary[1] - 1;
		} else if (d == 2) {
			ary[0] = ary[0] + 1;
		} else if (d == 3) {
			ary[0] = ary[0] + 1;
			ary[1] = ary[1] + 1;
		} else if (d == 4) {
			ary[1] = ary[1] + 1;
		} else if (d == 5) {
			ary[0] = ary[0] - 1;
			ary[1] = ary[1] + 1;
		} else if (d == 6) {
			ary[0] = ary[0] - 1;
		} else if (d == 7) {
			ary[0] = ary[0] - 1;
			ary[1] = ary[1] - 1;
		} else if (d == 0) {
			ary[1] = ary[1] - 1;
		}
		ary[2] = d;
	}

	private void _getFront(int[] ary, int d) {
		if (d == 1) {
			ary[4] = 2;
			ary[3] = 0;
			ary[2] = 1;
			ary[1] = 3;
			ary[0] = 7;
		} else if (d == 2) {
			ary[4] = 2;
			ary[3] = 4;
			ary[2] = 0;
			ary[1] = 1;
			ary[0] = 3;
		} else if (d == 3) {
			ary[4] = 2;
			ary[3] = 4;
			ary[2] = 1;
			ary[1] = 3;
			ary[0] = 5;
		} else if (d == 4) {
			ary[4] = 2;
			ary[3] = 4;
			ary[2] = 6;
			ary[1] = 3;
			ary[0] = 5;
		} else if (d == 5) {
			ary[4] = 4;
			ary[3] = 6;
			ary[2] = 3;
			ary[1] = 5;
			ary[0] = 7;
		} else if (d == 6) {
			ary[4] = 4;
			ary[3] = 6;
			ary[2] = 0;
			ary[1] = 5;
			ary[0] = 7;
		} else if (d == 7) {
			ary[4] = 6;
			ary[3] = 0;
			ary[2] = 1;
			ary[1] = 5;
			ary[0] = 7;
		} else if (d == 0) {
			ary[4] = 2;
			ary[3] = 6;
			ary[2] = 0;
			ary[1] = 1;
			ary[0] = 7;
		}
	}

	// ■■■■■■■■■■■■ アイテム関連 ■■■■■■■■■■

	private void useHealPotion(int healHp, int effectId) {
		broadcastPacket(new S_SkillSound(getId(), effectId));
		if (this.hasSkillEffect(POLLUTE_WATER)) { // ポルートウォーター中は回復量1/2倍
			healHp /= 2;
		}
		if (this instanceof L1PetInstance) {
			((L1PetInstance) this).setCurrentHp(getCurrentHp() + healHp);
		} else if (this instanceof L1SummonInstance) {
			((L1SummonInstance) this).setCurrentHp(getCurrentHp() + healHp);
		} else {
			setCurrentHpDirect(getCurrentHp() + healHp);
		}
	}

	private void useHastePotion(int time) {
		broadcastPacket(new S_SkillHaste(getId(), 1, time));
		broadcastPacket(new S_SkillSound(getId(), 191));
		setMoveSpeed(1);
		setSkillEffect(STATUS_HASTE, time * 1000);
	}

	// アイテムの使用判定及び使用
	public static final int USEITEM_HEAL = 0;
	public static final int USEITEM_HASTE = 1;
	public static int[] healPotions = { POTION_OF_GREATER_HEALING,
			POTION_OF_EXTRA_HEALING, POTION_OF_HEALING };
	public static int[] haestPotions = { B_POTION_OF_GREATER_HASTE_SELF,
			POTION_OF_GREATER_HASTE_SELF, B_POTION_OF_HASTE_SELF,
			POTION_OF_HASTE_SELF };

	public void useItem(int type, int chance) { // 使用する種類 使用する可能性(％)
		if (hasSkillEffect(71)) {
			return; // ディケイ ポーション状態かチェック
		}

		RandomGenerator random = RandomGeneratorFactory.getSharedRandom();
		if (random.nextInt(100) > chance) {
			return; // 使用する可能性
		}

		if (type == USEITEM_HEAL) { // 回復系ポーション
			// 回復量の大きい順
			if (getInventory().consumeItem(POTION_OF_GREATER_HEALING, 1)) {
				useHealPotion(75, 197);
			} else if (getInventory().consumeItem(POTION_OF_EXTRA_HEALING, 1)) {
				useHealPotion(45, 194);
			} else if (getInventory().consumeItem(POTION_OF_HEALING, 1)) {
				useHealPotion(15, 189);
			}
		} else if (type == USEITEM_HASTE) { // ヘイスト系ポーション
			if (hasSkillEffect(1001)) {
				return; // ヘイスト状態チェック
			}

			// 効果の長い順
			if (getInventory().consumeItem(B_POTION_OF_GREATER_HASTE_SELF, 1)) {
				useHastePotion(2100);
			} else if (getInventory().consumeItem(POTION_OF_GREATER_HASTE_SELF,
					1)) {
				useHastePotion(1800);
			} else if (getInventory().consumeItem(B_POTION_OF_HASTE_SELF, 1)) {
				useHastePotion(350);
			} else if (getInventory().consumeItem(POTION_OF_HASTE_SELF, 1)) {
				useHastePotion(300);
			}
		}
	}

	// ■■■■■■■■■■■■■ スキル関連(npcskillsテーブル実装されたら消すかも) ■■■■■■■■■■■

	// 目標の隣へテレポート
	public boolean nearTeleport(int nx, int ny) {
		int rdir = _random.nextInt(8);
		int dir;
		for (int i = 0; i < 8; i++) {
			dir = rdir + i;
			if (dir > 7) {
				dir -= 8;
			}
			if (dir == 1) {
				nx++;
				ny--;
			} else if (dir == 2) {
				nx++;
			} else if (dir == 3) {
				nx++;
				ny++;
			} else if (dir == 4) {
				ny++;
			} else if (dir == 5) {
				nx--;
				ny++;
			} else if (dir == 6) {
				nx--;
			} else if (dir == 7) {
				nx--;
				ny--;
			} else if (dir == 0) {
				ny--;
			}
			if (getMap().isPassable(nx, ny)) {
				dir += 4;
				if (dir > 7) {
					dir -= 8;
				}
				teleport(nx, ny, dir);
				setCurrentMp(getCurrentMp() - 10);
				return true;
			}
		}
		return false;
	}

	// 目標へテレポート
	public void teleport(int nx, int ny, int dir) {
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.sendPackets(new S_SkillSound(getId(), 169));
			pc.sendPackets(new S_RemoveObject(this));
			pc.removeKnownObject(this);
		}
		setX(nx);
		setY(ny);
		setHeading(dir);
	}

	// ----------From L1Character-------------
	private String _nameId; // ● ネームＩＤ

	public String getNameId() {
		return _nameId;
	}

	public void setNameId(String s) {
		_nameId = s;
	}

	private boolean _Agro; // ● アクティブか

	public boolean isAgro() {
		return _Agro;
	}

	public void setAgro(boolean flag) {
		_Agro = flag;
	}

	private boolean _Agrocoi; // ● インビジアクティブか

	public boolean isAgroCoi() {
		return _Agrocoi;
	}

	public void setAgroCoi(boolean flag) {
		_Agrocoi = flag;
	}

	private boolean _Agrososc; // ● 変身アクティブか

	public boolean isAgroSosc() {
		return _Agrososc;
	}

	public void setAgroSosc(boolean flag) {
		_Agrososc = flag;
	}

	private int _homeX; // ● ホームポイントＸ（モンスターの戻る位置とかペットの警戒位置）

	public int getHomeX() {
		return _homeX;
	}

	public void setHomeX(int i) {
		_homeX = i;
	}

	private int _homeY; // ● ホームポイントＹ（モンスターの戻る位置とかペットの警戒位置）

	public int getHomeY() {
		return _homeY;
	}

	public void setHomeY(int i) {
		_homeY = i;
	}

	private boolean _reSpawn; // ● 再ポップするかどうか

	public boolean isReSpawn() {
		return _reSpawn;
	}

	public void setreSpawn(boolean flag) {
		_reSpawn = flag;
	}

	private boolean _weaponBreaked; // ● ウェポンブレイク中かどうか

	public boolean isWeaponBreaked() {
		return _weaponBreaked;
	}

	public void setWeaponBreaked(boolean flag) {
		_weaponBreaked = flag;
	}

	private int _hiddenStatus; // ● 地中に潜ったり、空を飛んでいる状態

	public int getHiddenStatus() {
		return _hiddenStatus;
	}

	public void setHiddenStatus(int i) {
		_hiddenStatus = i;
	}

	// 行動距離
	private int _movementDistance = 0;

	public int getMovementDistance() {
		return _movementDistance;
	}

	public void setMovementDistance(int i) {
		_movementDistance = i;
	}

	// 表示用ロウフル
	private int _tempLawful = 0;

	public int getTempLawful() {
		return _tempLawful;
	}

	public void setTempLawful(int i) {
		_tempLawful = i;
	}

	protected int calcSleepTime(int sleepTime, int type) {
		switch (getMoveSpeed()) {
		case 0: // 通常
			break;
		case 1: // ヘイスト
			sleepTime -= (sleepTime * 0.25);
			break;
		case 2: // スロー
			sleepTime *= 2;
			break;
		}
		if (getBraveSpeed() == 1) {
			sleepTime -= (sleepTime * 0.25);
		}
		if (hasSkillEffect(WIND_SHACKLE)) {
			if (type == ATTACK_SPEED || type == MAGIC_SPEED) {
				sleepTime += (sleepTime * 2);// 0.25
			}
		}
		if (hasSkillEffect(AREA_WIND_SHACKLE)) {
			if (type == ATTACK_SPEED || type == MAGIC_SPEED) {
				sleepTime += (sleepTime * 2);// 0.25
			}
		}
		return sleepTime;		
	}

	protected void setAiRunning(boolean aiRunning) {
		_aiRunning = aiRunning;
	}

	protected boolean isAiRunning() {
		return _aiRunning;
	}

	protected void setActived(boolean actived) {
		_actived = actived;
	}

	protected boolean isActived() {
		return _actived;
	}

	protected void setFirstAttack(boolean firstAttack) {
		_firstAttack = firstAttack;
	}

	protected boolean isFirstAttack() {
		return _firstAttack;
	}

	protected void setSleepTime(int sleep_time) {
		_sleep_time = sleep_time;
	}

	protected int getSleepTime() {
		return _sleep_time;
	}

	protected void setDeathProcessing(boolean deathProcessing) {
		_deathProcessing = deathProcessing;
	}

	protected boolean isDeathProcessing() {
		return _deathProcessing;
	}

	public int drainMana(int drain) {
		if (_drainedMana >= Config.MANA_DRAIN_LIMIT_PER_NPC) {
			return 0;
		}
		int result = Math.min(drain, getCurrentMp());
		if (_drainedMana + result > Config.MANA_DRAIN_LIMIT_PER_NPC) {
			result = Config.MANA_DRAIN_LIMIT_PER_NPC - _drainedMana;
		}
		_drainedMana += result;
		return result;
	}

	public boolean _destroyed = false; // このインスタンスが破棄されているか

	// ※破棄後に動かないよう強制的にＡＩ等のスレッド処理中止（念のため）

	// NPCが別のNPCに変わる場合の処理
	protected void transform(int transformId) {
		stopHpRegeneration();
		stopMpRegeneration();
		int transformGfxId = getNpcTemplate().getTransformGfxId();
		if (transformGfxId != 0) {
			broadcastPacket(new S_SkillSound(getId(), transformGfxId));
		}
		L1Npc npcTemplate = NpcTable.getInstance().getTemplate(transformId);
		setting_template(npcTemplate);

		broadcastPacket(new S_ChangeShape(getId(), getTempCharGfx()));
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}

	}

	public void setRest(boolean _rest) {
		this._rest = _rest;
	}

	public boolean isRest() {
		return _rest;
	}

	private boolean _isResurrect;

	public boolean isResurrect() {
		return _isResurrect;
	}

	public void setResurrect(boolean flag) {
		_isResurrect = flag;
	}

	/** 妖精森林 物品掉落 */
	private boolean _isDropitems = false;

	public boolean isDropitems() {
		return _isDropitems;
	}

	public void setDropItems(boolean i) {
		_isDropitems = i;
	}

	private boolean _forDropitems = false;

	public boolean forDropitems() {
		return _forDropitems;
	}

	public void giveDropItems(boolean i) {
		_forDropitems = i;
	}

	@Override
	public synchronized void resurrect(int hp) {
		if (_destroyed) {
			return;
		}
		if (_deleteTask != null) {
			if (!_future.cancel(false)) { // キャンセルできない
				return;
			}
			_deleteTask = null;
			_future = null;
		}
		super.resurrect(hp);

		// キャンセレーションをエフェクトなしでかける
		// 本来は死亡時に行うべきだが、負荷が大きくなるため復活時に行う
		L1SkillUse skill = new L1SkillUse();
		skill.handleCommands(null, CANCELLATION, getId(), getX(), getY(), null,
				0, L1SkillUse.TYPE_LOGIN, this);
	}

	// 死んでから消えるまでの時間計測用
	private DeleteTimer _deleteTask;
	private ScheduledFuture<?> _future = null;

	protected synchronized void startDeleteTimer() {
		if (_deleteTask != null) {
			return;
		}
		_deleteTask = new DeleteTimer(getId());
		_future = GeneralThreadPool.getInstance().schedule(_deleteTask,
				Config.NPC_DELETION_TIME * 1000);
	}

	protected static class DeleteTimer extends TimerTask {
		private int _id;

		protected DeleteTimer(int oId) {
			_id = oId;
			if (!(L1World.getInstance().findObject(_id) instanceof L1NpcInstance)) {
				throw new IllegalArgumentException("allowed only L1NpcInstance");
			}
		}

		@Override
		public void run() {
			L1NpcInstance npc = (L1NpcInstance) L1World.getInstance()
					.findObject(_id);
			if (npc == null || !npc.isDead() || npc._destroyed) {
				return; // 復活してるか、既に破棄済みだったら抜け
			}
			try {
				npc.deleteMe();
			} catch (Exception e) { // 絶対例外を投げないように
				e.printStackTrace();
			}
		}
	}

	private L1MobGroupInfo _mobGroupInfo = null;

	public boolean isInMobGroup() {
		return getMobGroupInfo() != null;
	}

	public L1MobGroupInfo getMobGroupInfo() {
		return _mobGroupInfo;
	}

	public void setMobGroupInfo(L1MobGroupInfo m) {
		_mobGroupInfo = m;
	}

	private int _mobGroupId = 0;

	public int getMobGroupId() {
		return _mobGroupId;
	}

	public void setMobGroupId(int i) {
		_mobGroupId = i;
	}

	public void startChat(int chatTiming) {
		// 出現時のチャットにも関わらず死亡中、死亡時のチャットにも関わらず生存中
		if (chatTiming == CHAT_TIMING_APPEARANCE && this.isDead()) {
			return;
		}
		if (chatTiming == CHAT_TIMING_DEAD && !this.isDead()) {
			return;
		}
		if (chatTiming == CHAT_TIMING_HIDE && this.isDead()) {
			return;
		}
		if (chatTiming == CHAT_TIMING_GAME_TIME && this.isDead()) {
			return;
		}

		int npcId = this.getNpcTemplate().getNpcId();
		L1NpcChat npcChat = null;
		if (chatTiming == CHAT_TIMING_APPEARANCE) {
			npcChat = NpcChatTable.getInstance().getTemplateAppearance(npcId);
		} else if (chatTiming == CHAT_TIMING_DEAD) {
			npcChat = NpcChatTable.getInstance().getTemplateDead(npcId);
		} else if (chatTiming == CHAT_TIMING_HIDE) {
			npcChat = NpcChatTable.getInstance().getTemplateHide(npcId);
		} else if (chatTiming == CHAT_TIMING_GAME_TIME) {
			npcChat = NpcChatTable.getInstance().getTemplateGameTime(npcId);
		}
		if (npcChat == null) {
			return;
		}

		Timer timer = new Timer(true);
		L1NpcChatTimer npcChatTimer = new L1NpcChatTimer(this, npcChat);
		if (!npcChat.isRepeat()) {
			timer.schedule(npcChatTimer, npcChat.getStartDelayTime());
		} else {
			timer.scheduleAtFixedRate(npcChatTimer,
					npcChat.getStartDelayTime(), npcChat.getRepeatInterval());
		}
	}

	/*
	 * ソーシャルアクションが送信された場合に呼び出される
	 *
	 * @param actioId ソーシャルアクションID
	 */
	public void receiveSocialAction(L1PcInstance pc, int actionId) {
		// TODO Auto-generated method stub
		if (getNpcId() == 91330) {// GFX不可 ハーディン
			if (L1HardinQuest.getInstance().getActiveMaps(getMapId()).isActive()) {
				L1HardinQuest.getInstance().getActiveMaps(getMapId()).setActionHardin(pc, actionId);
			}
		} else if (getNpcId() == 91297) {// ケレニス
			if (L1HardinQuest.getInstance().getActiveMaps(getMapId()).isActive()) {
				L1HardinQuest.getInstance().getActiveMaps(getMapId()).setActionKerenis(pc, actionId);
			}
		} else if (getNpcId() == 91449) {// 船の外枠
			if (L1OrimQuest.getInstance().getActiveMaps(getMapId())!=null) {
				L1OrimQuest.getInstance().getActiveMaps(getMapId()).setAction(pc,actionId);
			}
		}
	}

	/**
	 * グループに属するモンスターが死亡した際に判定する。
	 *
	 * @param npc
	 */
	private void doExecutionWhenNpcDied(L1NpcInstance npc) {
// 下記コードをL1MonstterInstanceに移設
//		try {
//			// 秘譚
//			if (91265 <= npc.getNpcId() && 91268 >= npc.getNpcId()) {
//				if (!L1HardinQuest.getInstance().getActiveMaps(npc.getMapId())
//						.isDeleteTransactionNow()) {
//					L1HardinQuest.getInstance().getActiveMaps(npc.getMapId())
//							.guardmanDeath();
//				}
//			}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/**
	 * ターゲットを設定する
	 *
	 * @param cha
	 *            新しく追加するターゲット
	 */
	public void setTarget(L1Character cha) {
		_hateList.add(cha, 0);
		_target = cha;
	}
}