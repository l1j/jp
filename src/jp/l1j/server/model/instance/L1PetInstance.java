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

package jp.l1j.server.model.instance;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.ExpTable;
import jp.l1j.server.datatables.PetItemTable;
import jp.l1j.server.datatables.PetTable;
import jp.l1j.server.datatables.PetTypeTable;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1PetFoodTimer;
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
import jp.l1j.server.templates.L1Item;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.templates.L1Pet;
import jp.l1j.server.templates.L1PetItem;
import jp.l1j.server.templates.L1PetType;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.Teleportation;

public class L1PetInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;
	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	private int _dir;

	// ターゲットがいない場合の処理
	@Override
	public boolean noTarget() {
		switch (_currentPetStatus) {
		case 3: // ● 休憩の場合
			return true;
		case 4: // ● 配備の場合
			if ((_petMaster != null)
					&& (_petMaster.getMapId() == getMapId())
					&& (getLocation().getTileLineDistance(
							_petMaster.getLocation()) < 5)) {
				_dir = targetReverseDirection(_petMaster.getX(), _petMaster
						.getY());
				_dir = checkObject(getX(), getY(), getMapId(), _dir);
				setDirectionMove(_dir);
				setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
			} else { // 主人を見失うか５マス以上はなれたら休憩状態に
				_currentPetStatus = 3;
				return true;
			}
			return false;
		case 5: // ● 警戒の場合はホームへ
			if ((Math.abs(getHomeX() - getX()) > 1)
					|| (Math.abs(getHomeY() - getY()) > 1)) {
				int dir = moveDirection(getHomeX(), getHomeY());
				if (dir == -1) { // ホームが離れすぎてたら現在地がホーム
					setHomeX(getX());
					setHomeY(getY());
				} else {
					setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
				}
			}
			return false;
		case 7: // ● ペットの笛で主人の元へ
			if ((_petMaster != null)
					&& (_petMaster.getMapId() == getMapId())
					&& (getLocation().getTileLineDistance(
							_petMaster.getLocation()) <= 1)) {
				_currentPetStatus = 3;
				return true;
			}
			int locx = _petMaster.getX() + _random.nextInt(1);
			int locy = _petMaster.getY() + _random.nextInt(1);
			_dir = moveDirection(locx, locy);
			if (_dir == -1) {
				_currentPetStatus = 3;
				return true;
			}
			setDirectionMove(_dir);
			setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
			return false;
		default:
			if ((_petMaster != null) && (_petMaster.getMapId() == getMapId())) { // 主人を追尾
				int distance = getLocation().getTileLineDistance(_master.getLocation());
				if (distance > 15) {
					// 本家仕様とは異なるが、15セル以上離れた場合は主人のもとへテレポート（引っ掛かり防止対策）
					Teleportation.teleport(this, _master.getX(), _master.getY(),
							_master.getMapId(), 5);
				} else if (distance > 3) {
					// 主人が離れすぎたら休憩状態に
					_dir = moveDirection(_petMaster.getX(), _petMaster.getY());
					setDirectionMove(_dir);
					setSleepTime(calcSleepTime(getPassiSpeed(), MOVE_SPEED));
				}
			} else { // ● 主人を見失ったら休憩状態に
				_currentPetStatus = 3;
				return true;
			}
			return false;
		}
	}

	// ペットを引き出した場合
	public L1PetInstance(L1Npc template, L1PcInstance master, L1Pet l1pet) {
		super(template);

		_petMaster = master;
		_itemObjId = l1pet.getItemObjId();
		_type = PetTypeTable.getInstance().get(template.getNpcId());

		// ステータスを上書き
		setId(l1pet.getObjId());
		setName(l1pet.getName());
		setLevel(l1pet.getLevel());
		// HPMPはMAXとする
		setMaxHp(l1pet.getHp());
		setCurrentHpDirect(l1pet.getHp());
		setMaxMp(l1pet.getMp());
		setCurrentMpDirect(l1pet.getMp());
		setExp(l1pet.getExp());
		setExpPercent(ExpTable.getExpPercentage(l1pet.getLevel(), l1pet
				.getExp()));
		setLawful(l1pet.getLawful());
		setTempLawful(l1pet.getLawful());
		setFood(l1pet.getFood());
		// ペット空腹度タイマースタート
		startFoodTimer(this);

		setMaster(master);
		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		setHeading(5);
		_currentPetStatus = 3;

		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addPet(this);
	}

	// ペットをテイムした場合
	public L1PetInstance(L1NpcInstance target, L1PcInstance master, int itemid) {
		super(null);

		_petMaster = master;
		_itemObjId = itemid;
		_type = PetTypeTable.getInstance().get(
				target.getNpcTemplate().getNpcId());

		// ステータスを上書き
		setId(IdFactory.getInstance().nextId());
		setting_template(target.getNpcTemplate());
		setCurrentHpDirect(target.getCurrentHp());
		setCurrentMpDirect(target.getCurrentMp());
		setExp(750); // Lv.5のEXP
		setExpPercent(0);
		setLawful(0);
		setTempLawful(0);
		setFood(50); // 空腹度：普通
		startFoodTimer(this); // ペット空腹度タイマースタート

		setMaster(master);
		setX(target.getX());
		setY(target.getY());
		setMap(target.getMapId());
		setHeading(target.getHeading());
		setPetcost(6);
		setInventory(target.getInventory());
		target.setInventory(null);

		_currentPetStatus = 3;
		/* ペット　自然回復修正 */
		stopHpRegeneration();
		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
		stopMpRegeneration();
		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
		target.deleteMe();
		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}

		master.addPet(this);
		PetTable.getInstance().storeNewPet(target, getId(), itemid);
	}

	// 攻撃でＨＰを減らすときはここを使用
	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		if (getCurrentHp() > 0) {
			if (damage > 0) { // 回復の場合は攻撃しない。
				setHate(attacker, 0); // ペットはヘイト無し
				removeSkillEffect(FOG_OF_SLEEPING);
				removeSkillEffect(PHANTASM);
			}

			if ((attacker instanceof L1PcInstance) && (damage > 0)) {
				L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);
			}

			if (attacker instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) attacker;
				// 目標、攻者ともに、NOPVPゾーンの場合
				if ((getZoneType() == 1) || (pet.getZoneType() == 1)) {
					damage = 0;
				}
			} else if (attacker instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) attacker;
				// 目標、攻者ともに、NOPVPゾーンの場合
				if ((getZoneType() == 1) || (summon.getZoneType() == 1)) {
					damage = 0;
				}
			}

			int newHp = getCurrentHp() - damage;
			if (newHp <= 0) {
				death(attacker);
			} else {
				setCurrentHp(newHp);
			}
		} else if (!isDead()) { // 念のため
			death(attacker);
		}
	}

	public synchronized void death(L1Character lastAttacker) {
		if (!isDead()) {
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			// ペット空腹度タイマー停止
			stopFoodTimer(this);
			setCurrentHp(0);

			getMap().setPassable(getLocation(), true);
			broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
		}
	}

	/** ペット進化 */
	public void evolvePet(int new_itemobjid) {

		L1Pet l1pet = PetTable.getInstance().getTemplate(_itemObjId);
		if (l1pet == null) {
			return;
		}

		int newNpcId = _type.getTransformNpcId();
		int evolvItem = _type.getTransformItemId();
		// 進化前のmaxHp,maxMpを退避
		int tmpMaxHp = getMaxHp();
		int tmpMaxMp = getMaxMp();

		transform(newNpcId);
		_type = PetTypeTable.getInstance().get(newNpcId);

		setLevel(1);
		// HPMPを元の半分にする
		setMaxHp(tmpMaxHp / 2);
		setMaxMp(tmpMaxMp / 2);
		setCurrentHpDirect(getMaxHp());
		setCurrentMpDirect(getMaxMp());
		setExp(0);
		setExpPercent(0);
		getInventory().consumeItem(evolvItem, 1); // 進化アイテムを消去

		// 進化後の装備アイテム処理
		L1Object obj = L1World.getInstance().findObject(l1pet.getObjId());
		if ((obj != null) && (obj instanceof L1NpcInstance)) {
			L1PetInstance new_pet = (L1PetInstance) obj;
			L1Inventory new_petInventory = new_pet.getInventory();
			List<L1ItemInstance> itemList = getInventory().getItems();
			for (Object itemObject : itemList) {
				L1ItemInstance item = (L1ItemInstance) itemObject;
				if (item == null) {
					continue;
				}
				if (item.isEquipped()) { // 裝備中
					item.setEquipped(false);
					L1PetItem petItem = PetItemTable.getInstance().getTemplate(
							item.getItemId());
					if (petItem.getUseType() == 1) { // 牙
						setWeapon(null);
						new_pet.usePetWeapon(this, item);
					} else if (petItem.getUseType() == 0) { // アーマー
						setArmor(null);
						new_pet.usePetArmor(this, item);
					}
				}
				if (new_pet.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
					getInventory().tradeItem(item, item.getCount(),
							new_petInventory);
				} else { // 地面に落とす
					new_petInventory = L1World.getInstance().getInventory(
							getX(), getY(), getMapId());
					getInventory().tradeItem(item, item.getCount(),
							new_petInventory);
				}
			}
			new_pet.broadcastPacket(new S_SkillSound(new_pet.getId(), 2127)); // 進化時光の柱表示
		}

		// 古いペットをDBから消す
		PetTable.getInstance().deletePet(_itemObjId);

		// 新しいペットをDBに書き込む
		l1pet.setItemObjId(new_itemobjid);
		l1pet.setNpcId(newNpcId);
		l1pet.setName(getName());
		l1pet.setLevel(getLevel());
		l1pet.setHp(getMaxHp());
		l1pet.setMp(getMaxMp());
		l1pet.setExp(getExp());
		l1pet.setFood(getFood());

		PetTable.getInstance().storeNewPet(this, getId(), new_itemobjid);

		_itemObjId = new_itemobjid;
		// ペット空腹度タイマースタート
		if ((obj != null) && (obj instanceof L1NpcInstance)) {
			L1PetInstance new_pet = (L1PetInstance) obj;
			startFoodTimer(new_pet);
		}
	}

	// 解放処理
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
		monster.setLevel(getLevel());
		monster.setMaxHp(getMaxHp());
		monster.setCurrentHpDirect(getCurrentHp());
		monster.setMaxMp(getMaxMp());
		monster.setCurrentMpDirect(getCurrentMp());

		_petMaster.getPetList().remove(getId());
		if (_petMaster.getPetList().isEmpty()) {
			_petMaster.sendPackets(new S_PetCtrlMenu(_master, monster, false));
			// ペットコントロールメニュー
		}
		deleteMe();

		// DBとPetTableから削除し、ペットアミュも破棄
		_petMaster.getInventory().removeItem(_itemObjId, 1);
		PetTable.getInstance().deletePet(_itemObjId);

		L1World.getInstance().storeObject(monster);
		L1World.getInstance().addVisibleObject(monster);
		for (L1PcInstance pc : L1World.getInstance()
				.getRecognizePlayer(monster)) {
			onPerceive(pc);
		}
	}

	// ペットの持ち物を収集
	public void collect(boolean isDepositnpc, L1PetInstance pet) {
		L1Inventory targetInventory = _petMaster.getInventory();
		List<L1ItemInstance> items = _inventory.getItems();
		int size = _inventory.getSize();
		for (int i = 0; i < size; i++) {
			L1ItemInstance item = items.get(0);
			if (item.isEquipped()) { // 裝備中
				if (!isDepositnpc) { // 装備品を収集しない場合
					continue;
				} else {
					L1PetItem petItem = PetItemTable.getInstance().getTemplate(
							item.getItemId());
					if (petItem.getUseType() == 1) { // 牙
						removePetWeapon(pet, item);
					} else if (petItem.getUseType() == 0) { // アーマー
						removePetArmor(pet, item);
					}
					L1Pet l1pet = PetTable.getInstance().getTemplate(_itemObjId);
					if (l1pet != null) {
						l1pet.setHp(getMaxHp());
						l1pet.setMp(getMaxMp());
						PetTable.getInstance().storePet(l1pet); // DBに書き込み
					}
					item.setEquipped(false);
				}
			}
			if (_petMaster.getInventory().checkAddItem( // オーナーのアイテム数確認
					item, item.getCount()) == L1Inventory.OK) {
				getInventory()
						.tradeItem(item, item.getCount(), targetInventory);
				_petMaster.sendPackets(new S_ServerMessage(143, getName(), item
						.getLogName()));
			} else { // 地面に落とす
				targetInventory = L1World.getInstance().getInventory(getX(),
						getY(), getMapId());
				getInventory()
						.tradeItem(item, item.getCount(), targetInventory);
			}
		}
	}

	// リスタート時にDROPを地面に落とす
	public void dropItem(L1PetInstance pet) {
		L1Inventory targetInventory = L1World.getInstance().getInventory(
				getX(), getY(), getMapId());
		List<L1ItemInstance> items = _inventory.getItems();
		int size = _inventory.getSize();
		for (int i = 0; i < size; i++) {
			L1ItemInstance item = items.get(0);
			if (item.isEquipped()) { // 裝備中
				L1PetItem petItem = PetItemTable.getInstance().getTemplate(
						item.getItemId());
				if (petItem.getUseType() == 1) { // 牙
					removePetWeapon(pet, item);
				} else if (petItem.getUseType() == 0) { // アーマー
					removePetArmor(pet, item);
				}
				L1Pet l1pet = PetTable.getInstance().getTemplate(_itemObjId);
				if (l1pet != null) {
					l1pet.setHp(getMaxHp());
					l1pet.setMp(getMaxMp());
					PetTable.getInstance().storePet(l1pet); // DBに書き込み
				}
				item.setEquipped(false);
			}
			_inventory.tradeItem(item, item.getCount(), targetInventory);
		}
	}

	// ペットの笛を使った
	public void call() {
		int id = _type.getMessageId(L1PetType.getMessageNumber(getLevel()));
		if (id != 0 && !isDead()) {
			if (getFood() == 0) {
				id = _type.getDefyMessageId();
			}
			broadcastPacket(new S_NpcChatPacket(this, "$" + id, 0));
		}

		if (getFood() > 0) {
			setCurrentPetStatus(7); // 主人の近くで休憩状態
		} else {
			setCurrentPetStatus(3); // 空腹になったら休息
		}
	}

	@Override
	public void setTarget(L1Character target) {
		if ((target != null)
				&& ((_currentPetStatus == 1) || (_currentPetStatus == 2) || (_currentPetStatus == 5))
				&& (getFood() > 0)) {
			setHate(target, 0);
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	public void setMasterTarget(L1Character target) {
		if ((target != null)
				&& ((_currentPetStatus == 1) || (_currentPetStatus == 5))
				&& (getFood() > 0)) {
			setHate(target, 0);
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_PetPack(this, perceivedFrom)); // ペット系オブジェクト認識
		if (isDead()) {
			perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_Die));
		}
	}

	@Override
	public void onAction(L1PcInstance pc) {
		onAction(pc, 0);
	}

	@Override
	public void onAction(L1PcInstance pc, int skillId) {
		L1Character cha = this.getMaster();
		L1PcInstance master = (L1PcInstance) cha;
		if (master.isTeleport()) { // テレポート処理中
			return;
		}
		if (getZoneType() == 1) { // 攻撃される側がセーフティーゾーン
			L1Attack attack_mortion = new L1Attack(pc, this, skillId); // 攻撃モーション送信
			attack_mortion.action();
			return;
		}

		if (pc.checkNonPvP(pc, this)) {
			return;
		}

		L1Attack attack = new L1Attack(pc, this, skillId);
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
		if (_petMaster.equals(pc)) {
			pc.sendPackets(new S_PetMenuPacket(this, getExpPercent()));
			L1Pet l1pet = PetTable.getInstance().getTemplate(_itemObjId);
			// XXX ペットに話しかけるたびにDBに書き込む必要はない
			if (l1pet != null) {
				l1pet.setExp(getExp());
				l1pet.setLevel(getLevel());
				l1pet.setHp(getMaxHp());
				l1pet.setMp(getMaxMp());
				PetTable.getInstance().storePet(l1pet); // DBに書き込み
			}
		}
	}

	@Override
	public void onFinalAction(L1PcInstance pc, String action) {
		int status = actionType(action);
		if (status == 0) {
			return;
		}
		if (status == 6) {
			L1PcInstance petMaster = (L1PcInstance) _master;
			liberate(); // ペットの解放
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
			Object[] petList = _petMaster.getPetList().values().toArray();
			for (Object petObject : petList) {
				if (petObject instanceof L1PetInstance) { // ペット
					L1PetInstance pet = (L1PetInstance) petObject;
					if ((_petMaster != null)
							&& (_petMaster.getLevel() >= pet.getLevel())
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
				} else if (petObject instanceof L1SummonInstance) { // サモン
					L1SummonInstance summon = (L1SummonInstance) petObject;
					summon.setCurrentPetStatus(status);
				}
			}
		}
	}

	@Override
	public void onItemUse() {
		if (!isActived()) {
			useItem(USEITEM_HASTE, 100); // １００％の確率でヘイストポーション使用
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) { // ＨＰが４０％きったら
			useItem(USEITEM_HEAL, 100); // １００％の確率で回復ポーション使用
		}
	}

	@Override
	public void onGetItem(L1ItemInstance item, int count) {
		if (getNpcTemplate().getDigestItem() > 0) {
			setDigestItem(item);
		}
		if (isFood(item.getItem())) {
			eatFood(item, count);
		}

		Arrays.sort(healPotions);
		Arrays.sort(haestPotions);
		if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
			if (getCurrentHp() != getMaxHp()) {
				useItem(USEITEM_HEAL, 100);
			}
		} else if (Arrays
				.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
			useItem(USEITEM_HASTE, 100);
		}
	}

	private int actionType(String action) {
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
		} else if (action.equalsIgnoreCase("getitem")) { // 収集
			collect(false, null);
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

		if (_petMaster != null) {
			int HpRatio = 100 * currentHp / getMaxHp();
			L1PcInstance Master = _petMaster;
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
		if (_currentPetStatus == 7) {
			allTargetClear();
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

	public int getItemObjId() {
		return _itemObjId;
	}

	public void setExpPercent(int expPercent) {
		_expPercent = expPercent;
	}

	public int getExpPercent() {
		return _expPercent;
	}

	private L1ItemInstance _weapon;

	public void setWeapon(L1ItemInstance weapon) {
		_weapon = weapon;
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	private L1ItemInstance _armor;

	public void setArmor(L1ItemInstance armor) {
		_armor = armor;
	}

	public L1ItemInstance getArmor() {
		return _armor;
	}

	private int _hitByWeapon;

	public void setHitByWeapon(int i) {
		_hitByWeapon = i;
	}

	public int getHitByWeapon() {
		return _hitByWeapon;
	}

	private int _damageByWeapon;

	public void setDamageByWeapon(int i) {
		_damageByWeapon = i;
	}

	public int getDamageByWeapon() {
		return _damageByWeapon;
	}

	private int _currentPetStatus;
	private L1PcInstance _petMaster;
	private int _itemObjId;
	private L1PetType _type;
	private int _expPercent;

	public L1PetType getPetType() {
		return _type;
	}

	// ペット空腹度タイマー
	private L1PetFoodTimer _petFoodTimer;

	public void startFoodTimer(L1PetInstance pet) {
		_petFoodTimer = new L1PetFoodTimer(pet);
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(_petFoodTimer, 1000, 200000); //  X秒減少
	}

	public void stopFoodTimer(L1PetInstance pet) {
		if (_petFoodTimer != null) {
			_petFoodTimer.cancel();
			_petFoodTimer = null;
		}
	}

	// ペット装備アイテム
	public void usePetWeapon(L1PetInstance pet, L1ItemInstance weapon) {
		if (pet.getWeapon() == null) {
			setPetWeapon(pet, weapon);
		} else { // 既に何かを装備している場合、前の装備をはずす
			if (pet.getWeapon().equals(weapon)) {
				removePetWeapon(pet, pet.getWeapon());
			} else {
				removePetWeapon(pet, pet.getWeapon());
				setPetWeapon(pet, weapon);
			}
		}
	}

	public void usePetArmor(L1PetInstance pet, L1ItemInstance armor) {
		if (pet.getArmor() == null) {
			setPetArmor(pet, armor);
		} else { // 既に何かを装備している場合、前の装備をはずす
			if (pet.getArmor().equals(armor)) {
				removePetArmor(pet, pet.getArmor());
			} else {
				removePetArmor(pet, pet.getArmor());
				setPetArmor(pet, armor);
			}
		}
	}

	private void setPetWeapon(L1PetInstance pet, L1ItemInstance weapon) {
		int itemId = weapon.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.setHitByWeapon(petItem.getHitModifier());
		pet.setDamageByWeapon(petItem.getDamageModifier());
		pet.addStr(petItem.getAddStr());
		pet.addCon(petItem.getAddCon());
		pet.addDex(petItem.getAddDex());
		pet.addInt(petItem.getAddInt());
		pet.addWis(petItem.getAddWis());
		pet.addMaxHp(petItem.getAddHp());
		pet.addMaxMp(petItem.getAddMp());
		pet.addSp(petItem.getAddSp());
		pet.addMr(petItem.getAddMr());

		pet.setWeapon(weapon);
		weapon.setEquipped(true);
	}

	private void removePetWeapon(L1PetInstance pet, L1ItemInstance weapon) {
		int itemId = weapon.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.setHitByWeapon(0);
		pet.setDamageByWeapon(0);
		pet.addStr(-petItem.getAddStr());
		pet.addCon(-petItem.getAddCon());
		pet.addDex(-petItem.getAddDex());
		pet.addInt(-petItem.getAddInt());
		pet.addWis(-petItem.getAddWis());
		pet.addMaxHp(-petItem.getAddHp());
		pet.addMaxMp(-petItem.getAddMp());
		pet.addSp(-petItem.getAddSp());
		pet.addMr(-petItem.getAddMr());

		pet.setWeapon(null);
		weapon.setEquipped(false);
	}

	private void setPetArmor(L1PetInstance pet, L1ItemInstance armor) {
		int itemId = armor.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.addAc(petItem.getAddAc());
		pet.addStr(petItem.getAddStr());
		pet.addCon(petItem.getAddCon());
		pet.addDex(petItem.getAddDex());
		pet.addInt(petItem.getAddInt());
		pet.addWis(petItem.getAddWis());
		pet.addMaxHp(petItem.getAddHp());
		pet.addMaxMp(petItem.getAddMp());
		pet.addSp(petItem.getAddSp());
		pet.addMr(petItem.getAddMr());

		pet.setArmor(armor);
		armor.setEquipped(true);
	}

	private void removePetArmor(L1PetInstance pet, L1ItemInstance armor) {
		int itemId = armor.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.addAc(-petItem.getAddAc());
		pet.addStr(-petItem.getAddStr());
		pet.addCon(-petItem.getAddCon());
		pet.addDex(-petItem.getAddDex());
		pet.addInt(-petItem.getAddInt());
		pet.addWis(-petItem.getAddWis());
		pet.addMaxHp(-petItem.getAddHp());
		pet.addMaxMp(-petItem.getAddMp());
		pet.addSp(-petItem.getAddSp());
		pet.addMr(-petItem.getAddMr());

		pet.setArmor(null);
		armor.setEquipped(false);
	}

	private void eatFood(L1ItemInstance item, int count) {
		if (item.getItem().getFoodVolume() == 0) {
			return;
		}

		int newFood = getFood();
		int eatenCount = 0;
		for (int i = 0; i < count; i++) {
			if (newFood >= 100) { // 満腹になった
				break;
			}
			newFood += item.getItem().getFoodVolume() / 10;
			eatenCount++;
		}
		if (eatenCount == 0) {
			return;
		}

		getInventory().removeItem(item, eatenCount);
		setFood(Math.min(100, newFood));
		PetTable.getInstance().storePetFood(_itemObjId, getFood());
	}

	private boolean isFood(L1Item item) {
		int petId = _type.getBaseNpcId();
		int itemId = item.getItemId();
		boolean result = false;
		if (petId == 45313 || petId == 45710) { // タイガー、バトルタイガー
			if (itemId == 50572 || itemId == 50574) { // タイガーのエサ、特別なタイガーのエサ
				result = true;
			}
		} else if (petId == 45711 || petId == 45712) { // 紀州犬の子犬、紀州犬
			if (itemId == 50573 || itemId == 50575) { // 紀州犬のエサ、特別な紀州犬のエサ
				result = true;
			}
		} else { // その他のペット
			if (item.getType2() == 0 && item.getType() == 7) {
				result = true;
			}
		}
		return result;
	}
}