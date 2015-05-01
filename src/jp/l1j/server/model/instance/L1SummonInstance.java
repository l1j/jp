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
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.DropTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.PetTypeTable;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.inventory.L1Inventory;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_HpMeter;
import jp.l1j.server.packets.server.S_NpcChatPacket;
import jp.l1j.server.packets.server.S_PetCtrlMenu;
import jp.l1j.server.packets.server.S_PetMenuPacket;
import jp.l1j.server.packets.server.S_PetPack;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_SummonPack;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.templates.L1PetType;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.Teleportation;

public class L1SummonInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	private ScheduledFuture<?> _summonFuture;
	
	private static final long SUMMON_TIME = 3600000L;
	
	private int _currentPetStatus;
	
	private boolean _tamed;
	
	private boolean _isReturnToNature = false;
	
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	
	private int _dir;

	// ターゲットがいない場合の処理
	@Override
	public boolean noTarget() {
		switch (_currentPetStatus) {
		case 3: // ● 休憩の場合
			return true;
		case 4:// ● 配備の場合
			if ((_master != null) && (_master.getMapId() == getMapId())
					&& (getLocation().getTileLineDistance(_master.getLocation()) < 5)) {
				_dir = targetReverseDirection(_master.getX(), _master.getY());
				_dir = checkObject(getX(), getY(), getMapId(), _dir);
				setDirectionMove(_dir);
				setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
			} else {
				// 主人を見失うか５マス以上はなれたら休憩状態に
				_currentPetStatus = 3;
				return true;
			}
			return false;
		case 5:// ● 警戒の場合はホームへ
			if ((Math.abs(getHomeX() - getX()) > 1) || (Math.abs(getHomeY() - getY()) > 1)) {
				_dir = moveDirection(getHomeX(), getHomeY());
				if (_dir == -1) {
					// ホームが離れすぎてたら現在地がホーム
					setHomeX(getX());
					setHomeY(getY());
				} else {
					setDirectionMove(_dir);
					setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
				}
			}
			return false;
		default:
			if ((_master != null) && (_master.getMapId() == getMapId())) {// ●主人を追尾
				int distance = getLocation().getTileLineDistance(_master.getLocation());
				if (distance > 15) {
					// 本家仕様とは異なるが、15セル以上離れた場合は主人のもとへテレポート（引っ掛かり防止対策）
					Teleportation.teleport(this, _master.getX(), _master.getY(), _master.getMapId(), 5);
				} else if (distance > 3) {
					_dir = moveDirection(_master.getX(), _master.getY());
					setDirectionMove(_dir);
					setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
				}
			} else {
				// 主人が離れすぎたら休憩状態に
				_currentPetStatus = 3;
				return true;
			}
			return false;
		}
	}

	// １時間計測用
	class SummonTimer implements Runnable {
		@Override
		public void run() {
			if (_destroyed) { // 既に破棄されていないかチェック
				return;
			}
			if (_tamed) {
				// テイミングモンスター、クリエイトゾンビの解放
				liberate();
			} else {
				// サモンの解散
				Death(null);
			}
		}
	}

	// サモンモンスター用
	public L1SummonInstance(L1Npc template, L1Character master) {
		super(template);
		setId(IdFactory.getInstance().nextId());
		_summonFuture = GeneralThreadPool.getInstance().schedule(new SummonTimer(), SUMMON_TIME);
		setMaster(master);
		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		setHeading(5);
		_currentPetStatus = 3;
		_tamed = false;
		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addPet(this);
	}

	// テイミングモンスター、クリエイトゾンビ用
	public L1SummonInstance(L1NpcInstance target, L1Character master, boolean isCreateZombie) {
		super(null);
		setId(IdFactory.getInstance().nextId());
		if (isCreateZombie) { // クリエイトゾンビ
			int npcId = 45065;
			L1PcInstance pc = (L1PcInstance) master;
			int level = pc.getLevel();
			if (pc.isWizard()) {
				if (level >= 24 && level <= 31) {
					npcId = 81183;
				} else if (level >= 32 && level <= 39) {
					npcId = 81184;
				} else if (level >= 40 && level <= 43) {
					npcId = 81185;
				} else if (level >= 44 && level <= 47) {
					npcId = 81186;
				} else if (level >= 48 && level <= 51) {
					npcId = 81187;
				} else if (level >= 52) {
					npcId = 81188;
				}
			} else if (pc.isElf()) {
				if (level >= 48) {
					npcId = 81183;
				}
			}
			L1Npc template = NpcTable.getInstance().getTemplate(npcId).clone();
			setting_template(template);
		} else { // テイミングモンスター
			setting_template(target.getNpcTemplate());
			setCurrentHpDirect(target.getCurrentHp());
			setCurrentMpDirect(target.getCurrentMp());
		}
		_summonFuture = GeneralThreadPool.getInstance().schedule(new SummonTimer(), SUMMON_TIME);
		setMaster(master);
		setX(target.getX());
		setY(target.getY());
		setMap(target.getMapId());
		setHeading(target.getHeading());
		setPetcost(6);
		if (target instanceof L1MonsterInstance
				&& !((L1MonsterInstance) target).is_storeDroped()) {
			DropTable.getInstance().setDrop(target, target.getInventory());
		}
		setInventory(target.getInventory());
		target.setInventory(null);
		_currentPetStatus = 3;
		_tamed = true;
		// ペットが攻撃中だった場合止めさせる
		for (L1NpcInstance each : master.getPetList().values()) {
			each.targetRemove(target);
		}
		target.deleteMe();
		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addPet(this);
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) { // 攻撃でＨＰを減らすときはここを使用
		if (getCurrentHp() > 0) {
			if (damage > 0) {
				setHate(attacker, 0); // サモンはヘイト無し
				removeSkillEffect(FOG_OF_SLEEPING);
				removeSkillEffect(PHANTASM);
				if (!isExsistMaster()) {
					_currentPetStatus = 1;
					setTarget(attacker);
				}
			}
			if (attacker instanceof L1PcInstance && damage > 0) {
				L1PcInstance pc = (L1PcInstance) attacker;
				pc.setPetTarget(this);
			}
			if (attacker instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) attacker;
				// 攻撃目標指定、NOPVP
				if ((getZoneType() == 1) || (pet.getZoneType() == 1)) {
					damage = 0;
				}
			} else if (attacker instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) attacker;
				// 攻撃目標指定、NOPVP
				if ((getZoneType() == 1) || (summon.getZoneType() == 1)) {
					damage = 0;
				}
			}
			int newHp = getCurrentHp() - damage;
			if (newHp <= 0) {
				Death(attacker);
			} else {
				setCurrentHp(newHp);
			}
		} else if (!isDead()) // 念のため
		{
			System.out.println("警告：サモンのＨＰ減少処理が正しく行われていない箇所があります。※もしくは最初からＨＰ０");
			Death(attacker);
		}
	}

	public synchronized void Death(L1Character lastAttacker) {
		if (!isDead()) {
			setDead(true);
			setCurrentHp(0);
			setStatus(ActionCodes.ACTION_Die);
			getMap().setPassable(getLocation(), true);
			// アイテム解放処理
			L1Inventory targetInventory = _master.getInventory();
			List<L1ItemInstance> items = _inventory.getItems();
			for (L1ItemInstance item : items) {
				if (_master.getInventory().checkAddItem( // 容量重量確認及びメッセージ送信
						item, item.getCount()) == L1Inventory.OK) {
					_inventory.tradeItem(item, item.getCount(), targetInventory);
					// \f1%0が%1をくれました。
					((L1PcInstance) _master).sendPackets(new S_ServerMessage(143, getName(), item.getLogName()));
				} else { // 持てないので足元に落とす
					targetInventory = L1World.getInstance().getInventory(getX(), getY(), getMapId());
					_inventory.tradeItem(item, item.getCount(), targetInventory);
				}
			}
			if (_tamed) {
				broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
				startDeleteTimer();
			} else {
				deleteMe();
			}
		}
	}

	public synchronized void returnToNature() {
		_isReturnToNature = true;
		if (!_tamed) {
			getMap().setPassable(getLocation(), true);
			// アイテム解放処理
			L1Inventory targetInventory = _master.getInventory();
			List<L1ItemInstance> items = _inventory.getItems();
			for (L1ItemInstance item : items) {
				if (_master.getInventory().checkAddItem( // 容量重量確認及びメッセージ送信
						item, item.getCount()) == L1Inventory.OK) {
					_inventory.tradeItem(item, item.getCount(), targetInventory);
					// \f1%0が%1をくれました。
					((L1PcInstance) _master).sendPackets(new S_ServerMessage(143, getName(), item.getLogName()));
				} else { // 持てないので足元に落とす
					targetInventory = L1World.getInstance().getInventory(getX(), getY(), getMapId());
					_inventory.tradeItem(item, item.getCount(), targetInventory);
				}
			}
			deleteMe();
		} else {
			liberate();
		}
	}

	// オブジェクト消去処理
	@Override
	public synchronized void deleteMe() {
		if (_destroyed) {
			return;
		}
		if (!_tamed && !_isReturnToNature) {
			broadcastPacket(new S_SkillSound(getId(), 169));
		}
		//if (_master.getPetList().isEmpty()) {
			L1PcInstance pc = (L1PcInstance) _master;
			if (pc instanceof L1PcInstance) {
				pc.sendPackets(new S_PetCtrlMenu(_master, this, false));
				// ペットコントロールメニュー
			}
		//}
		_master.getPetList().remove(getId());
		super.deleteMe();
		if (_summonFuture != null) {
			_summonFuture.cancel(false);
			_summonFuture = null;
		}
	}

	// テイミングモンスター、クリエイトゾンビの時の解放処理
	public void liberate() {
		L1MonsterInstance monster = new L1MonsterInstance(getNpcTemplate());
		monster.setId(IdFactory.getInstance().nextId());
		monster.setX(getX());
		monster.setY(getY());
		monster.setMap(getMapId());
		monster.setHeading(getHeading());
		monster.setStoreDroped(true);
		monster.setInventory(getInventory());
		setInventory(null);
		monster.setCurrentHpDirect(getCurrentHp());
		monster.setCurrentMpDirect(getCurrentMp());
		monster.setExp(0);
		deleteMe();
		L1World.getInstance().storeObject(monster);
		L1World.getInstance().addVisibleObject(monster);
	}

	@Override
	public void setTarget(L1Character target) {
		if (target != null
				&& (_currentPetStatus == 1 || _currentPetStatus == 2 || _currentPetStatus == 5)) {
			setHate(target, 0);
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	public void setMasterTarget(L1Character target) {
		if (target != null
				&& (_currentPetStatus == 1 || _currentPetStatus == 5)) {
			setHate(target, 0);
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	@Override
	public void onAction(L1PcInstance attacker) {
		onAction(attacker, 0);
	}

	@Override
	public void onAction(L1PcInstance attacker, int skillId) {
		// XXX:NullPointerException回避。onActionの引数の型はL1Characterのほうが良い？
		if (attacker == null) {
			return;
		}
		L1Character cha = this.getMaster();
		if (cha == null) {
			return;
		}
		L1PcInstance master = (L1PcInstance) cha;
		if (master.isTeleport()) {
			// テレポート処理中
			return;
		}
		if ((getZoneType() == 1 || attacker.getZoneType() == 1)
				&& isExsistMaster()) {
			// 攻撃される側がセーフティーゾーン
			// 攻撃モーション送信
			L1Attack attack_mortion = new L1Attack(attacker, this, skillId);
			attack_mortion.action();
			return;
		}
		if (attacker.checkNonPvP(attacker, this)) {
			return;
		}
		L1Attack attack = new L1Attack(attacker, this, skillId);
		if (attack.calcHit()) {
			attack.calcDamage();
		}
		attack.action();
		attack.commit();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		if (isDead()) {
			return;
		}
		if (_master.equals(pc)) {
			pc.sendPackets(new S_PetMenuPacket(this, 0));
		}
	}

	@Override
	public void onFinalAction(L1PcInstance pc, String action) {
		int status = ActionType(action);
		if (status == 0) {
			return;
		}
		if (status == 6) {
			L1PcInstance petMaster = (L1PcInstance) _master;
			if (_tamed) {
				// テイミングモンスター、クリエイトゾンビの解放
				liberate();
			} else {
				// サモンの解散
				Death(null);
			}
			// ペットコントロールメニュー
			Object[] petList = petMaster.getPetList().values().toArray();
			for (Object petObject : petList) {
				if (petObject instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) petObject;
					petMaster.sendPackets(new S_SummonPack(summon, petMaster));
					return;
				} else if (petObject instanceof L1PetInstance) {
					L1PetInstance pet = (L1PetInstance) petObject;
					petMaster.sendPackets(new S_PetPack(pet, petMaster));
					return;
				}
			}
		} else {
			// 同じ主人のペットの状態をすべて更新
			Object[] petList = _master.getPetList().values().toArray();
			for (Object petObject : petList) {
				if (petObject instanceof L1SummonInstance) {
					// サモンモンスター
					L1SummonInstance summon = (L1SummonInstance) petObject;
					summon.setCurrentPetStatus(status);
				} else if (petObject instanceof L1PetInstance) { // ペット
					L1PetInstance pet = (L1PetInstance) petObject;
					if ((pc != null) && (pc.getLevel() >= pet.getLevel())
							&& pet.getFood() > 0) {
						pet.setCurrentPetStatus(status);
					} else {
						if (!pet.isDead()) {
							L1PetType type = PetTypeTable.getInstance().get(
									pet.getNpcTemplate().getNpcId());
							int id = type.getDefyMessageId();
							if (id != 0) {
								pet.broadcastPacket(new S_NpcChatPacket(pet,
										"$" + id, 0));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_SummonPack(this, perceivedFrom));
	}

	@Override
	public void onItemUse() {
		if (!isActived()) {
			// １００％の確率でヘイストポーション使用
			useItem(USEITEM_HASTE, 100);
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) {
			// ＨＰが４０％きったら
			// １００％の確率で回復ポーション使用
			useItem(USEITEM_HEAL, 100);
		}
	}

	@Override
	public void onGetItem(L1ItemInstance item, int count) {
		if (getNpcTemplate().getDigestItem() > 0) {
			setDigestItem(item);
		}
		Arrays.sort(healPotions);
		Arrays.sort(haestPotions);
		if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
			if (getCurrentHp() != getMaxHp()) {
				useItem(USEITEM_HEAL, 100);
			}
		} else if (Arrays.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
			useItem(USEITEM_HASTE, 100);
		}
	}

	private int ActionType(String action) {
		int status = 0;
		if (action.equalsIgnoreCase("aggressive")) { // 攻撃態勢
			status = 1;
		} else if (action.equalsIgnoreCase("defensive")) { // 防御態勢
			status = 2;
		} else if (action.equalsIgnoreCase("stay")) { // 休憩
			status = 3;
		} else if (action.equalsIgnoreCase("extend")) { // 配備
			status = 4;
		} else if (action.equalsIgnoreCase("alert")) { // 警戒
			status = 5;
		} else if (action.equalsIgnoreCase("dismiss")) { // 解散
			status = 6;
		}
		return status;
	}

	@Override
	public void setCurrentHp(int i) {
		int currentHp = i;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
		setCurrentHpDirect(currentHp);
		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
		if (_master instanceof L1PcInstance) {
			int HpRatio = 100 * currentHp / getMaxHp();
			L1PcInstance Master = (L1PcInstance) _master;
			Master.sendPackets(new S_HpMeter(getId(), HpRatio));
		}
	}

	@Override
	public void setCurrentMp(int i) {
		int currentMp = i;
		if (currentMp >= getMaxMp()) {
			currentMp = getMaxMp();
		}
		setCurrentMpDirect(currentMp);

		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}

	public void setCurrentPetStatus(int i) {
		_currentPetStatus = i;
		if (_currentPetStatus == 5) {
			setHomeX(getX());
			setHomeY(getY());
		}
		if (_currentPetStatus == 3) {
			allTargetClear();
		} else {
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	public int getCurrentPetStatus() {
		return _currentPetStatus;
	}

	public boolean isExsistMaster() {
		boolean isExsistMaster = true;
		if (this.getMaster() != null) {
			String masterName = this.getMaster().getName();
			if (L1World.getInstance().getPlayer(masterName) == null) {
				isExsistMaster = false;
			}
		}
		return isExsistMaster;
	}
}