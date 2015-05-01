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

import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_T;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_BELT;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_BOOTS;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_CLOAK;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_EARRING;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_GLOVE;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_HELM;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_AMULET;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_RING1;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_RING2;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_RING3;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_RING4;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_RUNE1;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_RUNE2;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_RUNE3;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_RUNE4;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_RUNE5;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_ARMOR;
import static jp.l1j.server.packets.server.S_EquipmentWindow.EQUIPMENT_INDEX_WEAPON;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.ClientThread;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.command.executor.L1HpBar;
import jp.l1j.server.controller.timer.WarTimeController;
import jp.l1j.server.datatables.CharacterTable;
import jp.l1j.server.datatables.DeathPenaltyTable;
import jp.l1j.server.datatables.ExpTable;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.datatables.MapTimerTable;
import jp.l1j.server.model.AcceleratorChecker;
import jp.l1j.server.model.FafurionHydroEffect;
import jp.l1j.server.model.HpRegeneration;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1CastleLocation;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1ChatParty;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1DeathMatch;
import jp.l1j.server.model.L1EquipmentSlot;
import jp.l1j.server.model.L1ExcludingList;
import jp.l1j.server.model.L1Karma;
import jp.l1j.server.model.L1Magic;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1Party;
import jp.l1j.server.model.L1PartyRefresh;
import jp.l1j.server.model.L1PcDeleteTimer;
import jp.l1j.server.model.L1PinkName;
import jp.l1j.server.model.L1Quest;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1TownLocation;
import jp.l1j.server.model.L1War;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.MpRegeneration;
import jp.l1j.server.model.classes.L1ClassFeature;
import jp.l1j.server.model.gametime.L1RealTimeCarrier;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.model.inventory.L1WarehouseInventory;
import jp.l1j.server.model.map.executor.L1MapLimiter;
import jp.l1j.server.model.monitor.L1PcAutoUpdate;
import jp.l1j.server.model.monitor.L1PcExpMonitor;
import jp.l1j.server.model.monitor.L1PcGhostMonitor;
import jp.l1j.server.model.monitor.L1PcHellMonitor;
import jp.l1j.server.model.monitor.L1PcInvisDelay;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.packets.PacketOutput;
import jp.l1j.server.packets.server.S_BlueMessage;
import jp.l1j.server.packets.server.S_BonusStats;
import jp.l1j.server.packets.server.S_CastleMaster;
import jp.l1j.server.packets.server.S_ChangeShape;
import jp.l1j.server.packets.server.S_CharReset;
import jp.l1j.server.packets.server.S_CharVisualUpdate;
import jp.l1j.server.packets.server.S_Disconnect;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_DoActionShop;
import jp.l1j.server.packets.server.S_Emblem;
import jp.l1j.server.packets.server.S_EquipmentWindow;
import jp.l1j.server.packets.server.S_Exp;
import jp.l1j.server.packets.server.S_Fishing;
import jp.l1j.server.packets.server.S_HpMeter;
import jp.l1j.server.packets.server.S_HpUpdate;
import jp.l1j.server.packets.server.S_Invis;
import jp.l1j.server.packets.server.S_Lawful;
import jp.l1j.server.packets.server.S_Liquor;
import jp.l1j.server.packets.server.S_MpUpdate;
import jp.l1j.server.packets.server.S_OtherCharPacks;
import jp.l1j.server.packets.server.S_OwnCharStatus;
import jp.l1j.server.packets.server.S_PacketBox;
import jp.l1j.server.packets.server.S_Poison;
import jp.l1j.server.packets.server.S_RemoveObject;
import jp.l1j.server.packets.server.S_Resurrection;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillIconGFX;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.packets.server.ServerBasePacket;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Account;
import jp.l1j.server.templates.L1BookMark;
import jp.l1j.server.templates.L1InventoryItem;
import jp.l1j.server.templates.L1Item;
import jp.l1j.server.templates.L1MagicDoll;
import jp.l1j.server.templates.L1PrivateShopBuyList;
import jp.l1j.server.templates.L1PrivateShopSellList;
import jp.l1j.server.utils.CalcStat;

public class L1PcInstance extends L1Character {
	private static final long serialVersionUID = 1L;

	public static final int CLASSID_KNIGHT_MALE = 61;
	public static final int CLASSID_KNIGHT_FEMALE = 48;
	public static final int CLASSID_ELF_MALE = 138;
	public static final int CLASSID_ELF_FEMALE = 37;
	public static final int CLASSID_WIZARD_MALE = 734;
	public static final int CLASSID_WIZARD_FEMALE = 1186;
	public static final int CLASSID_DARK_ELF_MALE = 2786;
	public static final int CLASSID_DARK_ELF_FEMALE = 2796;
	public static final int CLASSID_PRINCE = 0;
	public static final int CLASSID_PRINCESS = 1;
	public static final int CLASSID_DRAGON_KNIGHT_MALE = 6658;
	public static final int CLASSID_DRAGON_KNIGHT_FEMALE = 6661;
	public static final int CLASSID_ILLUSIONIST_MALE = 6671;
	public static final int CLASSID_ILLUSIONIST_FEMALE = 6650;

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	private short _hpr = 0;
	private short _trueHpr = 0;

	public short getHprByDoll() {
		short hprByDoll = 0;
		for (Object _doll : getDollList().values().toArray()) {
			L1DollInstance doll = (L1DollInstance) _doll;
			if (L1MagicDoll.enableHpr(doll)) {
				hprByDoll += L1MagicDoll.getHprByDoll(doll);
			}
		}
		return hprByDoll;
	}

	public short getHpr() {
		return (short) _hpr;
	}

	// 3.3C start
	boolean _rpActive = false;
	private L1PartyRefresh _rp;
	private int _partyType;

	// 3.3C end

	public void addHpr(int i) {
		_trueHpr += i;
		_hpr = (short) Math.max(0, _trueHpr);
	}

	private short _mpr = 0;
	private short _trueMpr = 0;

	public short getMprByDoll() {
		short mprByDoll = 0;
		for (Object _doll : getDollList().values().toArray()) {
			L1DollInstance doll = (L1DollInstance) _doll;
			if (L1MagicDoll.enableMpr(doll)) {
				mprByDoll += L1MagicDoll.getMprByDoll(doll);
			}
		}
		return mprByDoll;
	}

	public short getMpr() {
		return (short) _mpr;
	}

	public void addMpr(int i) {
		_trueMpr += i;
		_mpr = (short) Math.max(0, _trueMpr);
	}

	public short _originalHpr = 0; // ● オリジナルCON HPR

	public short getOriginalHpr() {

		return _originalHpr;
	}

	public short _originalMpr = 0; // ● オリジナルWIS MPR

	public short getOriginalMpr() {

		return _originalMpr;
	}

	public void startHpRegeneration() {
		final int INTERVAL = 1000;

		if (!_hpRegenActive) {
			_hpRegen = new HpRegeneration(this);
			_timer.scheduleAtFixedRate(_hpRegen, INTERVAL, INTERVAL);
			_hpRegenActive = true;
		}
	}

	public void stopHpRegeneration() {
		if (_hpRegenActive) {
			_hpRegen.cancel();
			_hpRegen = null;
			_hpRegenActive = false;
		}
	}

	public void startMpRegeneration() {
		final int INTERVAL = 1000;

		if (!_mpRegenActive) {
			_mpRegen = new MpRegeneration(this);
			_timer.scheduleAtFixedRate(_mpRegen, INTERVAL, INTERVAL);
			_mpRegenActive = true;
		}
	}

	public void stopMpRegeneration() {
		if (_mpRegenActive) {
			_mpRegen.cancel();
			_mpRegen = null;
			_mpRegenActive = false;
		}
	}

	public void startObjectAutoUpdate() {
		removeAllKnownObjects();
		_autoUpdateFuture = GeneralThreadPool.getInstance()
				.pcScheduleAtFixedRate(new L1PcAutoUpdate(getId()), 0L,
						INTERVAL_AUTO_UPDATE);
	}

	// TODO マジックドールによるHP回復タイマーを開始
	public void startHpRegenerationByDoll() {
		for (Object _doll : getDollList().values().toArray()) {
			L1DollInstance doll = (L1DollInstance) _doll;
			if (L1MagicDoll.enableHpr(doll)) {
				doll.startHprTimer();
			}
		}
	}

	// TODO マジックドールによるHP回復タイマーを停止
	public void stopHpRegenerationByDoll() {
		for (Object _doll : getDollList().values().toArray()) {
			L1DollInstance doll = (L1DollInstance) _doll;
			if (L1MagicDoll.enableHpr(doll)) {
				doll.stopHprTimer();
			}
		}
	}

	// TODO マジックドールによるMP回復タイマーを開始
	public void startMpRegenerationByDoll() {
		for (Object _doll : getDollList().values().toArray()) {
			L1DollInstance doll = (L1DollInstance) _doll;
			if (L1MagicDoll.enableMpr(doll)) {
				doll.startMprTimer();
			}
		}
	}

	// TODO マジックドールによるMP回復タイマーを停止
	public void stopMpRegenerationByDoll() {
		for (Object _doll : getDollList().values().toArray()) {
			L1DollInstance doll = (L1DollInstance) _doll;
			if (L1MagicDoll.enableMpr(doll)) {
				doll.stopMprTimer();
			}
		}
	}

	// TODO マジックドールのアイテム生成タイマーを開始
	public void startMakeItemByDoll() {
		for (Object _doll : getDollList().values().toArray()) {
			L1DollInstance doll = (L1DollInstance) _doll;
			if (L1MagicDoll.enableMakeItem(doll)) {
				doll.startMakeTimer();
			}
		}
	}

	// TODO マジックドールのアイテム生成タイマーを停止
	public void stopMakeItemByDoll() {
		for (Object _doll : getDollList().values().toArray()) {
			L1DollInstance doll = (L1DollInstance) _doll;
			if (L1MagicDoll.enableMakeItem(doll)) {
				doll.stopMakeTimer();
			}
		}
	}

	// TODO パプリオンハイドロエフェクト開始
	public void startFafurionHydroEffect() {
		if (!_fafurionHydroActiveEffect) {
			_fafurionHydroEffect = new FafurionHydroEffect(this);
			_timer.scheduleAtFixedRate(_fafurionHydroEffect, 60000,60000);
			_fafurionHydroActiveEffect = true;
		}
	}

	// TODO パプリオンハイドロエフェクト停止
	public void stopFafurionHydroEffect() {
		if (_fafurionHydroActiveEffect) {
			_fafurionHydroEffect.cancel();
			_fafurionHydroEffect = null;
			_fafurionHydroActiveEffect = false;
		}
	}

	/**
	 * 各種モニタータスクを停止する。
	 */
	public void stopEtcMonitor() {
		if (_autoUpdateFuture != null) {
			_autoUpdateFuture.cancel(true);
			_autoUpdateFuture = null;
		}
		if (_expMonitorFuture != null) {
			_expMonitorFuture.cancel(true);
			_expMonitorFuture = null;
		}
		if (_ghostFuture != null) {
			_ghostFuture.cancel(true);
			_ghostFuture = null;
		}

		if (_hellFuture != null) {
			_hellFuture.cancel(true);
			_hellFuture = null;
		}

	}

	// 有効期限付きアイテムのタイマーを開始
	public void startExpirationTimer() {
		for (L1ItemInstance item : getInventory().getItems()) {
			if (item.getExpirationTime() != null) {
				item.startExpirationTimer(this);
			}
		}
	}

	// 時間制限付きマップのタイマー
	private static ScheduledFuture<?> _mapLimiterFuture;
	private static L1MapLimiter _mapLimiter = null;
	public L1MapLimiter getMapLimiter() {
		return _mapLimiter;
	}

	public void setMapLimiter(L1MapLimiter mapLimiter) {
		_mapLimiter = mapLimiter;
	}

	public void startMapLimiter() {
		if (getMapLimiter() != null) {
			stopMapLimiter();
		}

		setMapLimiter(L1MapLimiter.get(getMapId()));
		if (isGm() == false && getMapLimiter() != null) {
			getMapLimiter().execute(this);
			ScheduledExecutorService schedule =
					Executors.newSingleThreadScheduledExecutor();
			_mapLimiterFuture = schedule.scheduleAtFixedRate(getMapLimiter(), 0,
					1000, TimeUnit.MILLISECONDS);
		}
	}

	public void stopMapLimiter() {
		if (getMapLimiter() != null) {
			getMapLimiter().save();
			setMapLimiter(null);
			if (_mapLimiterFuture != null) {
				_mapLimiterFuture.cancel(true);
				_mapLimiterFuture = null;
			}
		}
	}

	// 時間制限付きマップの残り時間を取得
	public int getEnterTime(int areaId) {
		int time = 0;
		L1MapLimiter limiter = getMapLimiter();
		if (limiter != null && limiter.getAreaId() == areaId) {
			time = limiter.getEnterTime() / 60;
		} else {
			limiter = L1MapLimiter.get(areaId);
			if (limiter != null) {
				MapTimerTable timer = MapTimerTable.find(getId(), limiter.getAreaId());
				if (timer != null) {
					time = timer.getEnterTime() / 60;
				} else {
					time = limiter.getEffect().getTime() / 60;
				}
			}
		}
		return time;
	}

	private static final long INTERVAL_AUTO_UPDATE = 300;
	private ScheduledFuture<?> _autoUpdateFuture;

	private static final long INTERVAL_EXP_MONITOR = 500;
	private ScheduledFuture<?> _expMonitorFuture;

	public void onChangeExp() {
		int level = ExpTable.getLevelByExp(getExp());
		int char_level = getLevel();
		int gap = level - char_level;
		if (gap == 0) {
			// sendPackets(new S_OwnCharStatus(this));
			sendPackets(new S_Exp(this));
			return;
		}

		// レベルが変化した場合
		if (gap > 0) {
			levelUp(gap);
		} else if (gap < 0) {
			levelDown(gap);
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		// 宿屋使用判定
		if (perceivedFrom.getMapId() > 10000
				&& perceivedFrom.getInnKeyId() != getInnKeyId()) {
			return;
		}
		if (isGmInvis() || isGhost()) {
			return;
		}
		if (isInvisble() && !perceivedFrom.hasSkillEffect(GMSTATUS_FINDINVIS)) {
			return;
		}

		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_OtherCharPacks(this, perceivedFrom
				.hasSkillEffect(GMSTATUS_FINDINVIS))); // 自分の情報を送る
		if (isInParty() && getParty().isMember(perceivedFrom)) { // PTメンバーならHPメーターも送る
			perceivedFrom.sendPackets(new S_HpMeter(this));
		}

		if (isPrivateShop()) {
			perceivedFrom.sendPackets(new S_DoActionShop(getId(),
					ActionCodes.ACTION_Shop, getShopChat()));
		} else if (isFishing()) { // 釣り中
			perceivedFrom.sendPackets(new S_Fishing(getId(),
					ActionCodes.ACTION_Fishing, getFishX(), getFishY()));
		}

		if (isCrown()) { // 君主
			L1Clan clan = L1World.getInstance().getClan(getClanName());
			if (clan != null) {
				if (getId() == clan.getLeaderId() // 血盟主で城主クラン
						&& clan.getCastleId() != 0) {
					perceivedFrom.sendPackets(new S_CastleMaster(clan
							.getCastleId(), getId()));
				}
			}
		}
	}

	// 範囲外になった認識済みオブジェクトを除去
	private void removeOutOfRangeObjects() {
		for (L1Object known : getKnownObjects()) {
			if (known == null) {
				continue;
			}

			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (!getLocation().isInScreen(known.getLocation())) { // 画面外
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			} else {
				if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			}
		}
	}

	// オブジェクト認識処理
	public void updateObject() {
		removeOutOfRangeObjects();

		if (getMapId() <= 10000) {
			// 認識範囲内のオブジェクトリストを作成
			for (L1Object visible : L1World.getInstance().getVisibleObjects(
					this, Config.PC_RECOGNIZE_RANGE)) {
				if (!knownsObject(visible)) {
					visible.onPerceive(this);
				} else {
					if (visible instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) visible;
						if (getLocation().isInScreen(npc.getLocation())
								&& npc.getHiddenStatus() != 0) {
							npc.approachPlayer(this);
						}
					}
				}
				if (hasSkillEffect(GMSTATUS_HPBAR)
						&& L1HpBar.isHpBarTarget(visible)) {
					sendPackets(new S_HpMeter((L1Character) visible));
				}
			}
		} else { // 宿屋内判定
			for (L1Object visible : L1World.getInstance()
					.getVisiblePlayer(this)) {
				if (!knownsObject(visible)) {
					visible.onPerceive(this);
				}
				if (hasSkillEffect(GMSTATUS_HPBAR)
						&& L1HpBar.isHpBarTarget(visible)) {
					if (getInnKeyId() == ((L1Character) visible).getInnKeyId()) {
						sendPackets(new S_HpMeter((L1Character) visible));
					}
				}
			}
		}
	}

	private void sendVisualEffect() {
		int poisonId = 0;
		if (getPoison() != null) { // 毒状態
			poisonId = getPoison().getEffectId();
		}
		if (getParalysis() != null) { // 麻痺状態
			// 麻痺エフェクトを優先して送りたい為、poisonIdを上書き。
			poisonId = getParalysis().getEffectId();
		}
		if (poisonId != 0) { // このifはいらないかもしれない
			sendPackets(new S_Poison(getId(), poisonId));
			broadcastPacket(new S_Poison(getId(), poisonId));
		}
	}

	public void sendVisualEffectAtLogin() {
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			sendPackets(new S_Emblem(clan.getClanId()));
		}

		if (getClanId() != 0) { // クラン所属
			L1Clan clan = L1World.getInstance().getClan(getClanName());
			if (clan != null) {
				if (isCrown() && getId() == clan.getLeaderId() && // プリンスまたはプリンセス、
						// かつ、血盟主で自クランが城主
						clan.getCastleId() != 0) {
					sendPackets(new S_CastleMaster(clan.getCastleId(), getId()));
				}
			}
		}

		sendVisualEffect();
	}

	public void sendVisualEffectAtTeleport() {
		if (isDrink()) { // liquorで酔っている
			sendPackets(new S_Liquor(getId(), 1));
		}

		sendVisualEffect();
	}

	private ArrayList<Integer> skillList = new ArrayList<Integer>();

	public void setSkillMastery(int skillid) {
		if (!skillList.contains(skillid)) {
			skillList.add(skillid);
		}
	}

	public void removeSkillMastery(int skillid) {
		if (skillList.contains(skillid)) {
			skillList.remove((Object) skillid);
		}
	}

	public boolean isSkillMastery(int skillid) {
		return skillList.contains(skillid);
	}

	public void clearSkillMastery() {
		skillList.clear();
	}

	public L1PcInstance(L1Account account) {
		_account = account;
		_accessLevel = 0;
		_currentWeapon = 0;
		_inventory = new L1PcInventory(this);
		_warehouse = new L1WarehouseInventory(getAccountId(),
				L1InventoryItem.LOC_WAREHOUSE);
		_additionalWarehouse = new L1WarehouseInventory(getId(),
				L1InventoryItem.LOC_ADDITIONAL_WAREHOUSE);
		_elfWarehouse = new L1WarehouseInventory(getAccountId(),
				L1InventoryItem.LOC_ELF_WAREHOUSE);
		_tradewindow = new L1Inventory();
		_bookmarks = new ArrayList<L1BookMark>();
		_quest = new L1Quest(this);
		_equipSlot = new L1EquipmentSlot(this);
	}

	@Override
	public void setCurrentHp(int i) {
		if (getCurrentHp() == i) {
			return;
		}
		int currentHp = i;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
		setCurrentHpDirect(currentHp);
		sendPackets(new S_HpUpdate(currentHp, getMaxHp()));
		if (isInParty()) { // パーティー中
			getParty().updateMiniHP(this);
		}
	}

	@Override
	public void setCurrentMp(int i) {
		if (getCurrentMp() == i) {
			return;
		}
		int currentMp = i;
		if (currentMp >= getMaxMp() || isGm()) {
			currentMp = getMaxMp();
		}
		setCurrentMpDirect(currentMp);
		sendPackets(new S_MpUpdate(currentMp, getMaxMp()));
	}

	@Override
	public L1PcInventory getInventory() {
		return _inventory;
	}

	public L1WarehouseInventory getWarehouseInventory() {
		return _warehouse;
	}

	public L1WarehouseInventory getAdditionalWarehouseInventory() {
		return _additionalWarehouse;
	}

	public L1WarehouseInventory getElfWarehouseInventory() {
		return _elfWarehouse;
	}

	public L1Inventory getTradeWindowInventory() {
		return _tradewindow;
	}

	public boolean isGmInvis() {
		return _gmInvis;
	}

	public void setGmInvis(boolean flag) {
		_gmInvis = flag;
	}

	public int getCurrentWeapon() {
		return _currentWeapon;
	}

	public void setCurrentWeapon(int i) {
		_currentWeapon = i;
	}

	public int getType() {
		return _type;
	}

	public void setType(int i) {
		_type = i;
	}

	public short getAccessLevel() {
		return _accessLevel;
	}

	public void setAccessLevel(short i) {
		_accessLevel = i;
	}

	public int getClassId() {
		return _classId;
	}

	public void setClassId(int i) {
		_classId = i;
		_classFeature = L1ClassFeature.newClassFeature(i);
	}

	private L1ClassFeature _classFeature = null;

	public L1ClassFeature getClassFeature() {
		return _classFeature;
	}

	@Override
	public synchronized int getExp() {
		return _exp;
	}

	@Override
	public synchronized void setExp(int i) {
		_exp = i;
	}

	private int _PkCount; // ● PKカウント

	public int getPkCount() {
		return _PkCount;
	}

	public void setPkCount(int i) {
		_PkCount = i;
	}

	private int _PkCountForElf; // ● PKカウント(エルフ用)

	public int getPkCountForElf() {
		return _PkCountForElf;
	}

	public void setPkCountForElf(int i) {
		_PkCountForElf = i;
	}

	private int _clanid; // ● クランＩＤ

	public int getClanId() {
		return _clanid;
	}

	public void setClanid(int i) {
		_clanid = i;
	}

	private String clanname; // ● クラン名

	public String getClanName() {
		return clanname;
	}

	public void setClanname(String s) {
		clanname = s;
	}

	// 参照を持つようにしたほうがいいかもしれない
	public L1Clan getClan() {
		return L1World.getInstance().getClan(getClanName());
	}

	private int _clanRank; // ● クラン内のランク(君主、副君主、ガーディアン、一般、修練)

	public int getClanRank() {
		return _clanRank;
	}

	public void setClanRank(int i) {
		_clanRank = i;
	}

	// 誕生日
	private Timestamp _birthday;

	public Timestamp getBirthday() {
		return _birthday;
	}

	public int getSimpleBirthday() {
		if (_birthday !=null) {
			SimpleDateFormat SimpleDate = new SimpleDateFormat("yyyyMMdd");
			int BornTime = Integer.parseInt(SimpleDate.format(_birthday.getTime()));
			return BornTime;
		} else {
			return 0;
		}
	}

	public void setBirthday(Timestamp time) {
		_birthday = time;
	}

	public void setBirthday(){
		_birthday = new Timestamp(System.currentTimeMillis());
	}

	private byte _sex; // ● 性別

	public byte getSex() {
		return _sex;
	}

	public void setSex(int i) {
		_sex = (byte) i;
	}

	public boolean isGm() {
		return _gm;
	}

	public void setGm(boolean flag) {
		_gm = flag;
	}

	public boolean isMonitor() {
		return _monitor;
	}

	public void setMonitor(boolean flag) {
		_monitor = flag;
	}

	private L1PcInstance getStat() {
		return null;
	}

	public void reduceCurrentHp(double d, L1Character l1character) {
		getStat().reduceCurrentHp(d, l1character);
	}

	/**
	 * 指定されたプレイヤー群にログアウトしたことを通知する
	 *
	 * @param playersList
	 *            通知するプレイヤーの配列
	 */
	private void notifyPlayersLogout(List<L1PcInstance> playersArray) {
		for (L1PcInstance player : playersArray) {
			if (player.knownsObject(this)) {
				player.removeKnownObject(this);
				player.sendPackets(new S_RemoveObject(this));
			}
		}
	}

	public void logout() {
		L1World world = L1World.getInstance();
		if (getClanId() != 0) // クラン所属
		{
			L1Clan clan = world.getClan(getClanName());
			if (clan != null) {
				if (clan.getWarehouseUsingChar() == getId()) // 自キャラがクラン倉庫使用中
				{
					clan.setWarehouseUsingChar(0); // クラン倉庫のロックを解除
				}
			}
		}
		notifyPlayersLogout(getKnownPlayers());
		world.removeVisibleObject(this);
		world.removeObject(this);
		notifyPlayersLogout(world.getRecognizePlayer(this));
		_inventory.clearItems();
		_warehouse.clearItems();
		removeAllKnownObjects();
		stopHpRegeneration();
		stopMpRegeneration();
		setDead(true); // 使い方おかしいかもしれないけど、ＮＰＣに消滅したことをわからせるため
		setNetConnection(null);
		setPacketOutput(null);
	}

	public ClientThread getNetConnection() {
		return _netConnection;
	}

	public void setNetConnection(ClientThread clientthread) {
		_netConnection = clientthread;
	}

	public boolean isInParty() {
		return getParty() != null;
	}

	public L1Party getParty() {
		return _party;
	}

	public void setParty(L1Party p) {
		_party = p;
	}

	public boolean isInChatParty() {
		return getChatParty() != null;
	}

	public L1ChatParty getChatParty() {
		return _chatParty;
	}

	public void setChatParty(L1ChatParty cp) {
		_chatParty = cp;
	}

	public int getPartyID() {
		return _partyID;
	}

	public void setPartyID(int partyID) {
		_partyID = partyID;
	}

	public int getTradeID() {
		return _tradeID;
	}

	public void setTradeID(int tradeID) {
		_tradeID = tradeID;
	}

	public void setTradeOk(boolean tradeOk) {
		_tradeOk = tradeOk;
	}

	public boolean getTradeOk() {
		return _tradeOk;
	}

	public int getTempID() {
		return _tempID;
	}

	public void setTempID(int tempID) {
		_tempID = tempID;
	}

	public boolean isTeleport() {
		return _isTeleport;
	}

	public void setTeleport(boolean flag) {
		_isTeleport = flag;
	}

	public boolean isDrink() {
		return _isDrink;
	}

	public void setDrink(boolean flag) {
		_isDrink = flag;
	}

	public boolean isGres() {
		return _isGres;
	}

	public void setGres(boolean flag) {
		_isGres = flag;
	}

	public boolean isPinkName() {
		return _isPinkName;
	}

	public void setPinkName(boolean flag) {
		_isPinkName = flag;
	}

	private ArrayList<L1PrivateShopSellList> _sellList = new ArrayList<L1PrivateShopSellList>();

	public ArrayList<L1PrivateShopSellList> getSellList() {
		return _sellList;
	}

	private ArrayList<L1PrivateShopBuyList> _buyList = new ArrayList<L1PrivateShopBuyList>();

	public ArrayList<L1PrivateShopBuyList> getBuyList() {
		return _buyList;
	}

	private byte[] _shopChat;

	public void setShopChat(byte[] chat) {
		_shopChat = chat;
	}

	public byte[] getShopChat() {
		return _shopChat;
	}

	private boolean _isPrivateShop = false;

	public boolean isPrivateShop() {
		return _isPrivateShop;
	}

	public void setPrivateShop(boolean flag) {
		_isPrivateShop = flag;
	}

	private boolean _isTradingInPrivateShop = false;

	public boolean isTradingInPrivateShop() {
		return _isTradingInPrivateShop;
	}

	public void setTradingInPrivateShop(boolean flag) {
		_isTradingInPrivateShop = flag;
	}

	private int _partnersPrivateShopItemCount = 0; // 閲覧中の個人商店のアイテム数

	public int getPartnersPrivateShopItemCount() {
		return _partnersPrivateShopItemCount;
	}

	public void setPartnersPrivateShopItemCount(int i) {
		_partnersPrivateShopItemCount = i;
	}

	private PacketOutput _out;

	public void setPacketOutput(PacketOutput out) {
		_out = out;
	}

	public void sendPackets(ServerBasePacket serverbasepacket) {
		if (_out == null) {
			return;
		}

		try {
			_out.sendPacket(serverbasepacket);
		} catch (Exception e) {
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
		// テレポート処理中
		if (isTeleport()) {
			return;
		}
		// 攻撃される側または攻撃する側がセーフティーゾーン
		if (getZoneType() == 1 || attacker.getZoneType() == 1) {
			// 攻撃モーション送信
			L1Attack attack_mortion = new L1Attack(attacker, this, skillId);
			attack_mortion.action();
			return;
		}

		if (checkNonPvP(this, attacker) == true) {
			// 攻撃モーション送信
			L1Attack attack_mortion = new L1Attack(attacker, this, skillId);
			attack_mortion.action();
			return;
		}

		if (getCurrentHp() > 0 && !isDead()) {
			attacker.delInvis();

			boolean isCounterBarrier = false;
			boolean isStormBarrier = false;
			int rnd = _random.nextInt(100) + 1;
			L1Attack attack = new L1Attack(attacker, this);
			if (this.getInventory().checkEquipped(21180) // リンドビオルストームシリーズ
					|| this.getInventory().checkEquipped(21181)
					|| this.getInventory().checkEquipped(21182)
					|| this.getInventory().checkEquipped(21183)) {
				boolean isStormProbability = (rnd <= 8); // 8%の確率
				boolean isLongDistance = attack.isLongDistance();
				if (isStormProbability && isLongDistance) {
					isStormBarrier = true;
				}
			}
			if (attack.calcHit()) {
				if (hasSkillEffect(COUNTER_BARRIER)) {
					L1Magic magic = new L1Magic(this, attacker);
					boolean isProbability = magic
							.calcProbabilityMagic(COUNTER_BARRIER);
					boolean isShortDistance = attack.isShortDistance();
					if (isProbability && isShortDistance) {
						isCounterBarrier = true;
					}
				}
				if (!isCounterBarrier || !isStormBarrier) {
					attacker.setPetTarget(this);
					attack.calcDamage();
					attack.calcStaffOfMana();
					attack.addPcPoisonAttack(attacker, this);
					attack.addChaserAttack();
					attack.addEvilAttack();
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
		}
	}

	public boolean checkNonPvP(L1PcInstance pc, L1Character target) {
		L1PcInstance targetpc = null;
		if (target instanceof L1PcInstance) {
			targetpc = (L1PcInstance) target;
		} else if (target instanceof L1PetInstance) {
			targetpc = (L1PcInstance) ((L1PetInstance) target).getMaster();
		} else if (target instanceof L1SummonInstance) {
			targetpc = (L1PcInstance) ((L1SummonInstance) target).getMaster();
		}
		if (targetpc == null) {
			return false; // 相手がPC、サモン、ペット以外
		}
		if (!Config.ALT_NONPVP) { // Non-PvP設定
			if (getMap().isCombatZone(getLocation())) {
				return false;
			}

			// 全戦争リストを取得
			for (L1War war : L1World.getInstance().getWarList()) {
				if (pc.getClanId() != 0 && targetpc.getClanId() != 0) { // 共にクラン所属中
					boolean same_war = war.CheckClanInSameWar(pc.getClanName(),
							targetpc.getClanName());
					if (same_war == true) { // 同じ戦争に参加中
						return false;
					}
				}
			}
			// Non-PvP設定でも戦争中は布告なしで攻撃可能
			if (target instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) target;
				if (isInWarAreaAndWarTime(pc, targetPc)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean isInWarAreaAndWarTime(L1PcInstance pc, L1PcInstance target) {
		// pcとtargetが戦争中に戦争エリアに居るか
		int castleId = L1CastleLocation.getCastleIdByArea(pc);
		int targetCastleId = L1CastleLocation.getCastleIdByArea(target);
		if (castleId != 0 && targetCastleId != 0 && castleId == targetCastleId) {
			if (WarTimeController.getInstance().isNowWar(castleId)) {
				return true;
			}
		}
		return false;
	}

	public void setPetTarget(L1Character target) {
		Object[] petList = getPetList().values().toArray();
		for (Object pet : petList) {
			if (pet instanceof L1PetInstance) {
				L1PetInstance pets = (L1PetInstance) pet;
				pets.setMasterTarget(target);
			} else if (pet instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) pet;
				summon.setMasterTarget(target);
			}
		}
	}

	public void delInvis() {
		// 魔法接続時間内はこちらを利用
		if (hasSkillEffect(INVISIBILITY)) { // インビジビリティ
			killSkillEffectTimer(INVISIBILITY);
			sendPackets(new S_Invis(getId(), 0));
			broadcastPacket(new S_OtherCharPacks(this));
		}
		if (hasSkillEffect(BLIND_HIDING)) { // ブラインド ハイディング
			killSkillEffectTimer(BLIND_HIDING);
			sendPackets(new S_Invis(getId(), 0));
			broadcastPacket(new S_OtherCharPacks(this));
		}
	}

	public void delBlindHiding() {
		// 魔法接続時間終了はこちら
		killSkillEffectTimer(BLIND_HIDING);
		sendPackets(new S_Invis(getId(), 0));
		broadcastPacket(new S_OtherCharPacks(this));
	}

	// 魔法のダメージの場合はここを使用 (ここで魔法ダメージ軽減処理) attr:0.無属性魔法,1.地魔法,2.火魔法,3.水魔法,4.風魔法
	public void receiveDamage(L1Character attacker, int damage, int attr) {
		int player_mr = getMr();
		int rnd = _random.nextInt(100) + 1;
		if (player_mr >= rnd) {
			damage /= 2;
		}
		receiveDamage(attacker, damage, false);
	}

	public void receiveManaDamage(L1Character attacker, int mpDamage) { // 攻撃でＭＰを減らすときはここを使用
		if (mpDamage > 0 && !isDead()) {
			delInvis();
			if (attacker instanceof L1PcInstance) {
				L1PinkName.onAction(this, attacker);
			}
			if (attacker instanceof L1PcInstance
					&& ((L1PcInstance) attacker).isPinkName()) {
				// ガードが画面内にいれば、攻撃者をガードのターゲットに設定する
				for (L1Object object : L1World.getInstance().getVisibleObjects(
						attacker)) {
					if (object instanceof L1GuardInstance) {
						L1GuardInstance guard = (L1GuardInstance) object;
						guard.setTarget(((L1PcInstance) attacker));
					}
				}
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp > getMaxMp()) {
				newMp = getMaxMp();
			}

			if (newMp <= 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}


	/**
	 * 3.63裝備顯示裝備類
	 * @param pc
	 * @param isEq
	 */
	public void setEquipped(L1PcInstance pc, boolean isEq) {
		for (L1ItemInstance item : pc.getInventory().getItems()) {
			if ((item.getItem().getType2() == 2) && (item.isEquipped())) {
				int items = 0;
				if ((item.getItem().getType() == 1)) {
					items = EQUIPMENT_INDEX_HELM;
				} else if ((item.getItem().getType() == 2)) {
					items = EQUIPMENT_INDEX_T;
				} else if ((item.getItem().getType() == 3)) {
					items = EQUIPMENT_INDEX_ARMOR;
				} else if ((item.getItem().getType() == 4)) {
					items = EQUIPMENT_INDEX_CLOAK;
				} else if ((item.getItem().getType() == 5)) {
					items = EQUIPMENT_INDEX_GLOVE;
				} else if ((item.getItem().getType() == 6)) {
					items = EQUIPMENT_INDEX_BOOTS;
				} else if ((item.getItem().getType() == 7)) {
          // shield
					items = EQUIPMENT_INDEX_SHIELD;
				} else if ((item.getItem().getType() == 8)) {
          // guarder
					items = EQUIPMENT_INDEX_SHIELD;
				} else if ((item.getItem().getType() == 10)) {
					items = EQUIPMENT_INDEX_AMULET;
				} else if ((item.getItem().getType() == 11) && item.getRingID() == 18) {
					items = EQUIPMENT_INDEX_RING1;
				} else if ((item.getItem().getType() == 11) && item.getRingID() == 19) {
					items = EQUIPMENT_INDEX_RING2;	
				} else if ((item.getItem().getType() == 11) && item.getRingID() == 20) {
					items = EQUIPMENT_INDEX_RING3;
				} else if ((item.getItem().getType() == 11) && item.getRingID() == 21) {
					items = EQUIPMENT_INDEX_RING4;
				} else if ((item.getItem().getType() == 12)) {
					items = EQUIPMENT_INDEX_EARRING;
				} else if ((item.getItem().getType() == 13)) {
					items = EQUIPMENT_INDEX_BELT;	
				} else if ((item.getItem().getType() == 14)) {
          // pattern_back
					items = EQUIPMENT_INDEX_RUNE1;
				} else if ((item.getItem().getType() == 15)) {
          // pattern_left
					items = EQUIPMENT_INDEX_RUNE2;
				} else if ((item.getItem().getType() == 16)) {
          // pattern_right
					items = EQUIPMENT_INDEX_RUNE3;
				} else if ((item.getItem().getType() == 17)) {
          // talisman_left
					items = EQUIPMENT_INDEX_RUNE4;
				} else if ((item.getItem().getType() == 18)) {
          // talisman_right
					items = EQUIPMENT_INDEX_RUNE5;
				}
				pc.sendPackets(new S_EquipmentWindow(pc, item.getId(),items,isEq)); 
			}
			if ((item.getItem().getType2() == 1) && (item.isEquipped())) {
				int items = EQUIPMENT_INDEX_WEAPON;
				pc.sendPackets(new S_EquipmentWindow(pc, item.getId(),items,isEq)); 
			}
		}
	}

	private long _oldTime = 0; // ● 連続魔法ダメージの軽減に使用する

	public long getOldTime() {
		return _oldTime;
	}

	public void setOldTime(long i) {
		_oldTime = i;
	}

	private int _numberOfDamaged = 0; // ● 連続魔法ダメージ中に攻撃を受けた回数

	public int getNumberOfDamaged() {
		return _numberOfDamaged;
	}

	public void setNumberOfDamaged(int i) {
		_numberOfDamaged = i;
	}

	public void addNumberOfDamaged(int i) {
		_numberOfDamaged += i;
	}

	public void receiveDamage(L1Character attacker, double damage,
			boolean isMagicDamage) { // 攻撃でＨＰを減らすときはここを使用
		if (getCurrentHp() > 0 && !isDead()) {
			if (attacker != this) {
				if (!(attacker instanceof L1EffectInstance)
						&& !knownsObject(attacker)
						&& attacker.getMapId() == this.getMapId()) {
					attacker.onPerceive(this);
				}
			}

			if (damage > 0) {
				delInvis();
				if (attacker instanceof L1PcInstance) {
					L1PinkName.onAction(this, attacker);
				}
				if (attacker instanceof L1PcInstance
						&& ((L1PcInstance) attacker).isPinkName()) {
					// ガードが画面内にいれば、攻撃者をガードのターゲットに設定する
					for (L1Object object : L1World.getInstance()
							.getVisibleObjects(attacker)) {
						if (object instanceof L1GuardInstance) {
							L1GuardInstance guard = (L1GuardInstance) object;
							guard.setTarget(((L1PcInstance) attacker));
						}
					}
				}
				removeSkillEffect(FOG_OF_SLEEPING);
				removeSkillEffect(PHANTASM);
			}

			if (hasSkillEffect(MORTAL_BODY) && getId() != attacker.getId()) {
				int rnd = _random.nextInt(100) + 1;
				if (damage > 0 && rnd <= 20) { // リニューアル前は10%
					if (attacker instanceof L1PcInstance) {
						L1PcInstance attackPc = (L1PcInstance) attacker;
						attackPc.sendPackets(new S_DoActionGFX(
								attackPc.getId(), ActionCodes.ACTION_Damage));
						attackPc.broadcastPacket(new S_DoActionGFX(attackPc
								.getId(), ActionCodes.ACTION_Damage));
						attackPc.receiveDamage(this, 30, false);
					} else if (attacker instanceof L1NpcInstance) {
						L1NpcInstance attackNpc = (L1NpcInstance) attacker;
						attackNpc.broadcastPacket(new S_DoActionGFX(attackNpc
								.getId(), ActionCodes.ACTION_Damage));
						attackNpc.receiveDamage(this, 30);
					}
				}
			}
			/*
			 * 試験実装後削除予定 if (attacker.hasSkillEffect(JOY_OF_PAIN) && getId() !=
			 * attacker.getId()) { int nowDamage = getMaxHp() - getCurrentHp();
			 * if (nowDamage > 0) { if (attacker instanceof L1PcInstance) {
			 * L1PcInstance attackPc = (L1PcInstance) attacker;
			 * attackPc.sendPackets(new S_DoActionGFX( attackPc.getId(),
			 * ActionCodes.ACTION_Damage)); attackPc.broadcastPacket(new
			 * S_DoActionGFX(attackPc .getId(), ActionCodes.ACTION_Damage));
			 * attackPc.receiveDamage(this, (nowDamage / 5), false); } else if
			 * (attacker instanceof L1NpcInstance) { L1NpcInstance attackNpc =
			 * (L1NpcInstance) attacker; attackNpc.broadcastPacket(new
			 * S_DoActionGFX(attackNpc .getId(), ActionCodes.ACTION_Damage));
			 * attackNpc.receiveDamage(this, (nowDamage / 5)); } } }
			 */
			if (getInventory().checkEquipped(145) // バーサーカーアックス
					|| getInventory().checkEquipped(149)) { // ミノタウルスアックス
				damage *= 1.5; // 被ダメ1.5倍
			}
			if (hasSkillEffect(ILLUSION_AVATAR)) {
				damage *= 1.2; // 被ダメ1.2倍
			}
			if (attacker instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) attacker;
				// 攻撃目標指定、NOPVP
				if ((getZoneType() == 1) || (pet.getZoneType() == 1)
						|| (checkNonPvP(this, pet))) {
					damage = 0;
				}
			} else if (attacker instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) attacker;
				// 攻撃目標指定、NOPVP
				if ((getZoneType() == 1) || (summon.getZoneType() == 1)
						|| (checkNonPvP(this, summon))) {
					damage = 0;
				}
			}
			int newHp = getCurrentHp() - (int) (damage);
			if (newHp > getMaxHp()) {
				newHp = getMaxHp();
			}
			if (newHp <= 0) {
				if (isGm()) {
					setCurrentHp(getMaxHp());
				} else {
					death(attacker);
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
			}
			int healHp = 0;
			if (getInventory().checkEquipped(21176)	// パブリオンハイドロシリーズ
					|| getInventory().checkEquipped(21177)
					|| getInventory().checkEquipped(21178)
					|| getInventory().checkEquipped(21179)) {
				int rnd = _random.nextInt(100) + 1;
				if (damage > 0 && rnd <= 5) { // 5%の確率で回復
					sendPackets(new S_SkillSound(getId(), 2187));
					broadcastPacket(new S_SkillSound(getId(), 2187));
					healHp = _random.nextInt(60) + 60;
					newHp += healHp;
					if(newHp > getMaxHp()) {
						newHp = getMaxHp();
					}
					setCurrentHp(newHp);
				}
			}
		} else if (!isDead()) { // 念のため
			System.out.println("警告：プレイヤーのＨＰ減少処理が正しく行われていない箇所があります。※もしくは最初からＨＰ０");
			death(attacker);
		}
	}

	public void death(L1Character lastAttacker) {
		synchronized (this) {
			if (isDead()) {
				return;
			}
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
		}
		GeneralThreadPool.getInstance().execute(new Death(lastAttacker));

	}

	// TODO Death関連メソッド
	private class Death implements Runnable {
		L1Character _lastAttacker;

		Death(L1Character cha) {
			_lastAttacker = cha;
		}

		public void run() {
			L1Character lastAttacker = _lastAttacker;
			_lastAttacker = null;
			setCurrentHp(0);
			setGresValid(false); // EXPロストするまでG-RES無効

			while (isTeleport()) { // テレポート中なら終わるまで待つ
				try {
					Thread.sleep(300);
				} catch (Exception e) {
				}
			}

			stopHpRegeneration();
			stopMpRegeneration();

			int targetobjid = getId();
			getMap().setPassable(getLocation(), true);

			// エンチャントを解除する
			// 変身状態も解除されるため、キャンセレーションをかけてから変身状態に戻す
			int tempchargfx = 0;
			if (hasSkillEffect(SHAPE_CHANGE)) {
				tempchargfx = getTempCharGfx();
				setTempCharGfxAtDead(tempchargfx);
			} else {
				setTempCharGfxAtDead(getClassId());
			}

			// キャンセレーションをエフェクトなしでかける
			L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(L1PcInstance.this, CANCELLATION, getId(),
					getX(), getY(), null, 0, L1SkillUse.TYPE_LOGIN);

			// シャドウ系変身中に死亡するとクライアントが落ちるため暫定対応
			if (tempchargfx == 5727 || tempchargfx == 5730
					|| tempchargfx == 5733 || tempchargfx == 5736) {
				tempchargfx = 0;
			}
			if (tempchargfx != 0) {
				sendPackets(new S_ChangeShape(getId(), tempchargfx));
				broadcastPacket(new S_ChangeShape(getId(), tempchargfx));
			} else {
				// シャドウ系変身中に攻撃しながら死亡するとクライアントが落ちるためディレイを入れる
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}

			sendPackets(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
			broadcastPacket(new S_DoActionGFX(targetobjid,
					ActionCodes.ACTION_Die));

			if (// getMap().getBaseMapId()==5153
					getMapId() == 5153) {// デスマッチ
				for (Object doll : getDollList().values().toArray()) {
					if (((L1DollInstance) doll).isChargeDoll()) { // 課金マジックドールのタイマーを停止
						L1ItemInstance item = getInventory().getItem(((L1DollInstance) doll).getItemObjId());
						item.stopChargeTimer();
					}
					((L1DollInstance) doll).deleteDoll();
				}
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
				}

				broadcastPacket(new S_SkillSound(getId(), '\346'));

				resurrect(1);
				setCurrentHp(1);

				sendPackets(new S_Resurrection(L1PcInstance.this,
						L1PcInstance.this, 0));
				broadcastPacket(new S_Resurrection(L1PcInstance.this,
						L1PcInstance.this, 0));
				broadcastPacket(new S_RemoveObject(L1PcInstance.this));

				try {
					save();
					beginGhost(getX(), getY(), getMapId(), false);
				} catch (Exception e) {
					e.printStackTrace();
				}

				sendPackets(new S_ServerMessage(1271));// 1271
				// 戦闘に敗れたため観戦者となりました。
				sendPackets(new S_CharVisualUpdate(L1PcInstance.this));
				broadcastPacket(new S_CharVisualUpdate(L1PcInstance.this));
				if (getMapId() == 5153) {// ノーマル
					L1DeathMatch.getInstance().sendRemainder(L1PcInstance.this);
				} else {// エキスパー
					// L1DeathMatchExpert.getInstance().sendRemainder(L1PcInstance.this);
				}
			}

			if (lastAttacker != L1PcInstance.this) {
				// セーフティーゾーン、コンバットゾーンで最後に殺したキャラが
				// プレイヤーorペットだったら、ペナルティなし
				if (getZoneType() != 0) {
					L1PcInstance player = null;
					if (lastAttacker instanceof L1PcInstance) {
						player = (L1PcInstance) lastAttacker;
					} else if (lastAttacker instanceof L1PetInstance) {
						player = (L1PcInstance) ((L1PetInstance) lastAttacker)
								.getMaster();
					} else if (lastAttacker instanceof L1SummonInstance) {
						player = (L1PcInstance) ((L1SummonInstance) lastAttacker)
								.getMaster();
					}
					if (player != null) {
						// 戦争中に戦争エリアに居る場合は例外
						if (!isInWarAreaAndWarTime(L1PcInstance.this, player)) {
							return;
						}
					}
				}

				boolean sim_ret = simWarResult(lastAttacker); // 模擬戦
				if (sim_ret == true) { // 模擬戦中ならペナルティなし
					return;
				}
			}

			if (!getMap().isEnabledDeathPenalty()) {
				return;
			}

			// 決闘中ならペナルティなし
			L1PcInstance fightPc = null;
			if (lastAttacker instanceof L1PcInstance) {
				fightPc = (L1PcInstance) lastAttacker;
			}
			if (fightPc != null) {
				if (getFightId() == fightPc.getId()
						&& fightPc.getFightId() == getId()) { // 決闘中
					setFightId(0);
					sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
					fightPc.setFightId(0);
					fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL,
							0, 0));
					return;
				}
			}

			deathPenalty(); // EXPロスト

			setGresValid(true); // EXPロストしたらG-RES有効

			if (getExpRes() == 0) {
				setExpRes(1);
			}

			// ガードに殺された場合のみ、PKカウントを減らしガードに攻撃されなくなる
			if (lastAttacker instanceof L1GuardInstance) {
				if (getPkCount() > 0) {
					setPkCount(getPkCount() - 1);
				}
				setLastPk(null);
			}
			if (lastAttacker instanceof L1GuardianInstance) {
				if (getPkCountForElf() > 0) {
					setPkCountForElf(getPkCountForElf() - 1);
				}
				setLastPkForElf(null);
			}

			// 一定の確率でアイテムをDROP
			// アライメント32000以上で0%、以降-1000毎に0.4%
			// アライメントが0未満の場合は-1000毎に0.8%
			// アライメント-32000以下で最高51.2%のDROP率
			int lostRate = (int) (((getLawful() + 32768D) / 1000D - 65D) * 4D);
			if (lostRate < 0) {
				lostRate *= -1;
				if (getLawful() < 0) {
					lostRate *= 2;
				}
				int rnd = _random.nextInt(1000) + 1;
				if (rnd <= lostRate) {
					int count = 1;
					if (getLawful() <= -30000) {
						count = _random.nextInt(4) + 1;
					} else if (getLawful() <= -20000) {
						count = _random.nextInt(3) + 1;
					} else if (getLawful() <= -10000) {
						count = _random.nextInt(2) + 1;
					} else if (getLawful() < 0) {
						count = _random.nextInt(1) + 1;
					}
					caoPenaltyResult(count);
				}
			}

			boolean castle_ret = castleWarResult(); // 攻城戦
			if (castle_ret == true) { // 攻城戦中で旗内なら赤ネームペナルティなし
				return;
			}

			// 最後に殺したキャラがプレイヤーだったら、赤ネームにする
			L1PcInstance player = null;
			if (lastAttacker instanceof L1PcInstance) {
				player = (L1PcInstance) lastAttacker;
			}
			if (player != null) {
				if (getLawful() >= 0 && isPinkName() == false) {
					boolean isChangePkCount = false;
					boolean isChangePkCountForElf = false;
					// アライメントが30000未満の場合はPKカウント増加
					if (player.getLawful() < 30000) {
						player.setPkCount(player.getPkCount() + 1);
						isChangePkCount = true;
						if (player.isElf() && isElf()) {
							player
							.setPkCountForElf(player.getPkCountForElf() + 1);
							isChangePkCountForElf = true;
						}
					}
					player.setLastPk();
					if (player.isElf() && isElf()) {
						player.setLastPkForElf();
					}

					// アライメント処理
					// 公式の発表および各LVでのPKからつじつまの合うように変更
					// （PK側のLVに依存し、高LVほどリスクも高い）
					// 48あたりで-8kほど DKの時点で10k強
					// 60で約20k強 65で30k弱
					int lawful;

					if (player.getLevel() < 50) {
						lawful = -1
								* (int) ((Math.pow(player.getLevel(), 2) * 4));
					} else {
						lawful = -1
								* (int) ((Math.pow(player.getLevel(), 3) * 0.08));
					}
					// もし(元々のアライメント-1000)が計算後より低い場合
					// 元々のアライメント-1000をアライメント値とする
					// （連続でPKしたときにほとんど値が変わらなかった記憶より）
					// これは上の式よりも自信度が低いうろ覚えですので
					// 明らかにこうならない！という場合は修正お願いします
					if ((player.getLawful() - 1000) < lawful) {
						lawful = player.getLawful() - 1000;
					}

					if (lawful <= -32768) {
						lawful = -32768;
					}
					player.setLawful(lawful);

					S_Lawful s_lawful = new S_Lawful(player.getId(), player
							.getLawful());
					player.sendPackets(s_lawful);
					player.broadcastPacket(s_lawful);
					// TODO 地獄切り替え＆ＰＫカウント変更　start
					if (Config.HELL == true) {
						int PkCountLimitMax = Config.PK_COUNT_LIMIT;
						int PkCountLimitMin = Config.PK_COUNT_LIMIT - 5;

						if (isChangePkCount
								&& player.getPkCount() >= PkCountLimitMin
								&& player.getPkCount() < PkCountLimitMax) {
							// あなたのPK回数が%0になりました。回数が%1になると地獄行きです。
							player.sendPackets(new S_BlueMessage(551, String
									.valueOf(player.getPkCount()), String
									.valueOf(PkCountLimitMax)));
						} else if (isChangePkCount
								&& player.getPkCount() >= PkCountLimitMax) {
							player.beginHell(true);
						}
					}
					// TODO 地獄切り替え＆ＰＫカウント切り替え　end
				} else {
					setPinkName(false);
				}
			}

			// TODO PK情報 start
			if (Config.PK_LOG == false) { // pklog on/off判定
			} else {

				if (lastAttacker instanceof L1PcInstance) {
					L1World world = L1World.getInstance();
					String areaname = "";
					String Clanname1 = "";
					String Clanname2 = "";
					int PKcount1 = 0;
					int PKcount2 = 0;

					if (player.getClanId() == 0) {
						Clanname1 = "未所属";
					} else {
						Clanname1 = player.getClanName();
					}
					if (getClanId() == 0) {
						Clanname2 = "未所属";
					} else {
						Clanname2 = getClanName();
					}
					areaname = lastAttacker.getMap().locationname();
					PKcount1 = player.getPkCount();
					PKcount2 = getPkCount();
					world.broadcastServerMessage("Kill:"
							+ lastAttacker.getName() + "[" + Clanname1
							+ "] Kill/Death(" + PKcount1 + ")");
					world.broadcastServerMessage("Death:" + getName() + "["
							+ Clanname2 + "] Kill/Death(" + PKcount2 + ")");
					world.broadcastServerMessage("Location:" + areaname);
				}
			}
			// TODO PK情報 end
			_pcDeleteTimer = new L1PcDeleteTimer(L1PcInstance.this);
			_pcDeleteTimer.begin();
		}
	}

	public void stopPcDeleteTimer() {
		if (_pcDeleteTimer != null) {
			_pcDeleteTimer.cancel();
			_pcDeleteTimer = null;
		}
	}

	private void caoPenaltyResult(int count) {
		for (int i = 0; i < count; i++) {
			L1ItemInstance item = getInventory().caoPenalty();

			if (item != null) {
				getInventory().tradeItem(
						item,
						item.isStackable() ? item.getCount() : 1,
								L1World.getInstance().getInventory(getX(), getY(),
										getMapId()));
				sendPackets(new S_ServerMessage(638, item.getLogName()));
				// %0を失いました
			} else {
			}
		}
	}

	public boolean castleWarResult() {
		if (getClanId() != 0 && isCrown()) { // クラン所属中プリのチェック
			L1Clan clan = L1World.getInstance().getClan(getClanName());
			// 全戦争リストを取得
			for (L1War war : L1World.getInstance().getWarList()) {
				int warType = war.GetWarType();
				boolean isInWar = war.CheckClanInWar(getClanName());
				boolean isAttackClan = war.CheckAttackClan(getClanName());
				if (getId() == clan.getLeaderId() && // 血盟主で攻撃側で攻城戦中
						warType == 1 && isInWar && isAttackClan) {
					String enemyClanName = war.GetEnemyClanName(getClanName());
					if (enemyClanName != null) {
						war.CeaseWar(getClanName(), enemyClanName); // 終結
					}
					break;
				}
			}
		}

		int castleId = 0;
		boolean isNowWar = false;
		castleId = L1CastleLocation.getCastleIdByArea(this);
		if (castleId != 0) { // 旗内に居る
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		return isNowWar;
	}

	public boolean simWarResult(L1Character lastAttacker) {
		if (getClanId() == 0) { // クラン所属していない
			return false;
		}
		if (Config.SIM_WAR_PENALTY) { // 模擬戦ペナルティありの場合はfalse
			return false;
		}
		L1PcInstance attacker = null;
		String enemyClanName = null;
		boolean sameWar = false;

		if (lastAttacker instanceof L1PcInstance) {
			attacker = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			attacker = (L1PcInstance) ((L1PetInstance) lastAttacker)
					.getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			attacker = (L1PcInstance) ((L1SummonInstance) lastAttacker)
					.getMaster();
		} else {
			return false;
		}

		// 全戦争リストを取得
		for (L1War war : L1World.getInstance().getWarList()) {
			L1Clan clan = L1World.getInstance().getClan(getClanName());

			int warType = war.GetWarType();
			boolean isInWar = war.CheckClanInWar(getClanName());
			if (attacker != null && attacker.getClanId() != 0) {
				// lastAttackerがPC、サモン、ペットでクラン所属中
				sameWar = war.CheckClanInSameWar(getClanName(), attacker
						.getClanName());
			}

			if (getId() == clan.getLeaderId() && // 血盟主で模擬戦中
					warType == 2 && isInWar == true) {
				enemyClanName = war.GetEnemyClanName(getClanName());
				if (enemyClanName != null) {
					war.CeaseWar(getClanName(), enemyClanName); // 終結
				}
			}

			if (warType == 2 && sameWar) {// 模擬戦で同じ戦争に参加中の場合、ペナルティなし
				return true;
			}
		}
		return false;
	}

	public void resExp() {
		int level = getLevel();
		double exp = calcDeathPenalty(level + 1);
		if (level <= 50) {
			exp *= 0.7;
		} else if (level == 51) {
			exp *= 0.71;
		} else if (level == 52) {
			exp *= 0.72;
		} else if (level == 53) {
			exp *= 0.73;
		} else if (level == 54) {
			exp *= 0.74;
		} else if (level == 55) {
			exp *= 0.75;
		} else if (level == 56) {
			exp *= 0.76;
		} else if (level == 57) {
			exp *= 0.77;
		} else if (level == 58) {
			exp *= 0.78;
		} else if (level == 59) {
			exp *= 0.79;
		} else if (level == 60) {
			exp *= 0.8;
		} else if (level == 61) {
			exp *= 0.81;
		} else if (level == 62) {
			exp *= 0.82;
		} else if (level == 63) {
			exp *= 0.83;
		} else if (level == 64) {
			exp *= 0.84;
		} else if (level == 65) {
			exp *= 0.85;
		} else if (level == 66) {
			exp *= 0.86;
		} else if (level == 67) {
			exp *= 0.87;
		} else if (level == 68) {
			exp *= 0.88;
		} else if (level == 69) {
			exp *= 0.89;
		} else if (level == 70) {
			exp *= 0.9;
		} else if (level == 71) {
			exp *= 0.91;
		} else if (level == 72) {
			exp *= 0.92;
		} else if (level == 73) {
			exp *= 0.93;
		} else if (level == 74) {
			exp *= 0.94;
		} else if (level == 75) {
			exp *= 0.95;
		} else if (level == 76) {
			exp *= 0.96;
		} else if (level == 77) {
			exp *= 0.97;
		} else if (level == 78) {
			exp *= 0.98;
		} else {
			exp *= 0.99;
		}

		if (exp == 0) {
			return;
		}
		addExp((int) exp);
	}

	private double calcDeathPenalty(int level) {
		int exp = ExpTable.getNeedExpNextLevel(level);
		if (level <= 10) {
			exp = 0;
		} else if (level <= 44) {
			exp *= 0.1;
		} else if (level == 45) {
			exp *= 0.09;
		} else if (level == 46) {
			exp *= 0.08;
		} else if (level == 47) {
			exp *= 0.07;
		} else if (level == 48) {
			exp *= 0.06;
		} else if (level == 49) {
			exp *= 0.05;
		} else if (level <= 64) {
			exp *= 0.045;
		} else if (level <= 69) {
			exp *= 0.04;
		} else if (level <= 74) {
			exp *= 0.035;
		} else if (level <= 78) {
			exp *= 0.03;
		} else if (level == 79) {
			exp *= 0.025;
		} else if (level <= 81) {
			exp *= 0.02;
		} else {
			exp *= 0.015;
		}

		int penaltyRate = DeathPenaltyTable.getDeathPenaltyRate(level);
		if (penaltyRate < 0) {
			penaltyRate = 0;
		}

		exp = exp * penaltyRate / 100;

		return exp;
	}

	public void deathPenalty() {
		int level = getLevel();
		int exp = (int) calcDeathPenalty(level);
		if (exp == 0) {
			return;
		}
		addExp(-exp);
	}

	private int _originalEr = 0; // ● オリジナルDEX ER補正

	public int getOriginalEr() {

		return _originalEr;
	}

	public int getEr() {
		if (hasSkillEffect(STRIKER_GALE)) {
			return 0;
		}

		int er = 0;
		if (isKnight()) {
			er = getLevel() / 4; // ナイト
		} else if (isCrown() || isElf()) {
			er = getLevel() / 6; // 君主、エルフ
		} else if (isDarkelf()) {
			er = getLevel() / 6; // ダークエルフ
		} else if (isWizard()) {
			er = getLevel() / 10; // ウィザード
		} else if (isDragonKnight()) {
			er = getLevel() / 5; // ドラゴンナイト
		} else if (isIllusionist()) {
			er = getLevel() / 9; // イリュージョニスト
		}

		er += (getDex() - 8) / 2;

		er += getOriginalEr();

		if (hasSkillEffect(DRESS_EVASION)) {
			er += 12;
		}
		if (hasSkillEffect(SOLID_CARRIAGE)) {
			er += 15;
		}
		return er;
	}

	public L1BookMark getBookMark(String name) {
		for (int i = 0; i < _bookmarks.size(); i++) {
			L1BookMark element = _bookmarks.get(i);
			if (element.getName().equalsIgnoreCase(name)) {
				return element;
			}

		}
		return null;
	}

	public L1BookMark getBookMark(int id) {
		for (int i = 0; i < _bookmarks.size(); i++) {
			L1BookMark element = _bookmarks.get(i);
			if (element.getId() == id) {
				return element;
			}

		}
		return null;
	}

	public int getBookMarkSize() {
		return _bookmarks.size();
	}

	public void addBookMark(L1BookMark book) {
		_bookmarks.add(book);
	}

	public void removeBookMark(L1BookMark book) {
		_bookmarks.remove(book);
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	public void setWeapon(L1ItemInstance weapon) {
		_weapon = weapon;
	}

	public L1Quest getQuest() {
		return _quest;
	}

	public boolean isCrown() {
		return (getClassId() == CLASSID_PRINCE || getClassId() == CLASSID_PRINCESS);
	}

	public boolean isKnight() {
		return (getClassId() == CLASSID_KNIGHT_MALE || getClassId() == CLASSID_KNIGHT_FEMALE);
	}

	public boolean isElf() {
		return (getClassId() == CLASSID_ELF_MALE || getClassId() == CLASSID_ELF_FEMALE);
	}

	public boolean isWizard() {
		return (getClassId() == CLASSID_WIZARD_MALE || getClassId() == CLASSID_WIZARD_FEMALE);
	}

	public boolean isDarkelf() {
		return (getClassId() == CLASSID_DARK_ELF_MALE || getClassId() == CLASSID_DARK_ELF_FEMALE);
	}

	public boolean isDragonKnight() {
		return (getClassId() == CLASSID_DRAGON_KNIGHT_MALE || getClassId() == CLASSID_DRAGON_KNIGHT_FEMALE);
	}

	public boolean isIllusionist() {
		return (getClassId() == CLASSID_ILLUSIONIST_MALE || getClassId() == CLASSID_ILLUSIONIST_FEMALE);
	}

	private static Logger _log = Logger.getLogger(L1PcInstance.class.getName());
	private ClientThread _netConnection;
	private int _classId;
	private int _type;
	private int _exp;
	private final L1Karma _karma = new L1Karma();
	private boolean _gm;
	private boolean _monitor;
	private boolean _gmInvis;
	private short _accessLevel;
	private int _currentWeapon;
	private final L1PcInventory _inventory;
	private final L1WarehouseInventory _warehouse;
	private final L1WarehouseInventory _additionalWarehouse;
	private final L1WarehouseInventory _elfWarehouse;
	private final L1Inventory _tradewindow;
	private L1ItemInstance _weapon;
	private L1Party _party;
	private L1ChatParty _chatParty;
	private int _partyID;
	private int _tradeID;
	private boolean _tradeOk;
	private int _tempID;
	private boolean _isTeleport = false;
	private boolean _isDrink = false;
	private boolean _isGres = false;
	private boolean _isPinkName = false;
	private final ArrayList<L1BookMark> _bookmarks;
	private L1Quest _quest;
	private MpRegeneration _mpRegen;
	private HpRegeneration _hpRegen;
	private static Timer _timer = new Timer(true);
	private boolean _mpRegenActive;
	private boolean _hpRegenActive;
	private L1EquipmentSlot _equipSlot;
	private L1PcDeleteTimer _pcDeleteTimer;
	private final L1Account _account;
	private FafurionHydroEffect _fafurionHydroEffect;
	private boolean _fafurionHydroActiveEffect;

	public int getAccountId() {
		return _account.getId();
	}

	public String getAccountName() {
		return _account.getName();
	}

	private short _baseMaxHp = 0; // ● ＭＡＸＨＰベース（1〜32767）

	public short getBaseMaxHp() {
		return _baseMaxHp;
	}

	public void addBaseMaxHp(short i) {
		i += _baseMaxHp;
		if (i >= 32767) {
			i = 32767;
		} else if (i < 1) {
			i = 1;
		}
		addMaxHp(i - _baseMaxHp);
		_baseMaxHp = i;
	}

	private short _baseMaxMp = 0; // ● ＭＡＸＭＰベース（0〜32767）

	public short getBaseMaxMp() {
		return _baseMaxMp;
	}

	public void addBaseMaxMp(short i) {
		i += _baseMaxMp;
		if (i >= 32767) {
			i = 32767;
		} else if (i < 0) {
			i = 0;
		}
		addMaxMp(i - _baseMaxMp);
		_baseMaxMp = i;
	}

	private int _baseAc = 0; // ● ＡＣベース（-128〜127）

	public int getBaseAc() {
		return _baseAc;
	}

	private int _originalAc = 0; // ● オリジナルDEX ＡＣ補正

	public int getOriginalAc() {

		return _originalAc;
	}

	private byte _baseStr = 0; // ● ＳＴＲベース（1〜127）

	public byte getBaseStr() {
		return _baseStr;
	}

	public void addBaseStr(byte i) {
		i += _baseStr;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addStr((byte) (i - _baseStr));
		_baseStr = i;
	}

	private byte _baseCon = 0; // ● ＣＯＮベース（1〜127）

	public byte getBaseCon() {
		return _baseCon;
	}

	public void addBaseCon(byte i) {
		i += _baseCon;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addCon((byte) (i - _baseCon));
		_baseCon = i;
	}

	private byte _baseDex = 0; // ● ＤＥＸベース（1〜127）

	public byte getBaseDex() {
		return _baseDex;
	}

	public void addBaseDex(byte i) {
		i += _baseDex;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addDex((byte) (i - _baseDex));
		_baseDex = i;
	}

	private byte _baseCha = 0; // ● ＣＨＡベース（1〜127）

	public byte getBaseCha() {
		return _baseCha;
	}

	public void addBaseCha(byte i) {
		i += _baseCha;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addCha((byte) (i - _baseCha));
		_baseCha = i;
	}

	private byte _baseInt = 0; // ● ＩＮＴベース（1〜127）

	public byte getBaseInt() {
		return _baseInt;
	}

	public void addBaseInt(byte i) {
		i += _baseInt;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addInt((byte) (i - _baseInt));
		_baseInt = i;
	}

	private byte _baseWis = 0; // ● ＷＩＳベース（1〜127）

	public byte getBaseWis() {
		return _baseWis;
	}

	public void addBaseWis(byte i) {
		i += _baseWis;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addWis((byte) (i - _baseWis));
		_baseWis = i;
	}

	private int _originalStr = 0; // ● オリジナル STR

	public int getOriginalStr() {
		return _originalStr;
	}

	public void setOriginalStr(int i) {
		_originalStr = i;
	}

	private int _originalCon = 0; // ● オリジナル CON

	public int getOriginalCon() {
		return _originalCon;
	}

	public void setOriginalCon(int i) {
		_originalCon = i;
	}

	private int _originalDex = 0; // ● オリジナル DEX

	public int getOriginalDex() {
		return _originalDex;
	}

	public void setOriginalDex(int i) {
		_originalDex = i;
	}

	private int _originalCha = 0; // ● オリジナル CHA

	public int getOriginalCha() {
		return _originalCha;
	}

	public void setOriginalCha(int i) {
		_originalCha = i;
	}

	private int _originalInt = 0; // ● オリジナル INT

	public int getOriginalInt() {
		return _originalInt;
	}

	public void setOriginalInt(int i) {
		_originalInt = i;
	}

	private int _originalWis = 0; // ● オリジナル WIS

	public int getOriginalWis() {
		return _originalWis;
	}

	public void setOriginalWis(int i) {
		_originalWis = i;
	}

	private int _originalDmgup = 0; // ● オリジナルSTR ダメージ補正

	public int getOriginalDmgup() {

		return _originalDmgup;
	}

	private int _originalBowDmgup = 0; // ● オリジナルDEX 弓ダメージ補正

	public int getOriginalBowDmgup() {

		return _originalBowDmgup;
	}

	private int _originalHitup = 0; // ● オリジナルSTR 命中補正

	public int getOriginalHitup() {

		return _originalHitup;
	}

	private int _originalBowHitup = 0; // ● オリジナルDEX 命中補正

	public int getOriginalBowHitup() {

		return _originalBowHitup;
	}

	private int _originalMr = 0; // ● オリジナルWIS 魔法防御

	public int getOriginalMr() {

		return _originalMr;
	}

	private int _originalMagicHit = 0; // ● オリジナルINT 魔法命中

	public int getOriginalMagicHit() {

		return _originalMagicHit;
	}

	private int _originalMagicCritical = 0; // ● オリジナルINT 魔法クリティカル

	public int getOriginalMagicCritical() {

		return _originalMagicCritical;
	}

	private int _originalMagicConsumeReduction = 0; // ● オリジナルINT 消費MP軽減

	public int getOriginalMagicConsumeReduction() {

		return _originalMagicConsumeReduction;
	}

	private int _originalMagicDamage = 0; // ● オリジナルINT 魔法ダメージ

	public int getOriginalMagicDamage() {

		return _originalMagicDamage;
	}

	private int _originalHpup = 0; // ● オリジナルCON HP上昇値補正

	public int getOriginalHpup() {

		return _originalHpup;
	}

	private int _originalMpup = 0; // ● オリジナルWIS MP上昇値補正

	public int getOriginalMpup() {

		return _originalMpup;
	}

	private int _baseDmgup = 0; // ● ダメージ補正ベース（-128〜127）

	public int getBaseDmgup() {
		return _baseDmgup;
	}

	private int _baseBowDmgup = 0; // ● 弓ダメージ補正ベース（-128〜127）

	public int getBaseBowDmgup() {
		return _baseBowDmgup;
	}

	private int _baseHitup = 0; // ● 命中補正ベース（-128〜127）

	public int getBaseHitup() {
		return _baseHitup;
	}

	private int _baseBowHitup = 0; // ● 弓命中補正ベース（-128〜127）

	public int getBaseBowHitup() {
		return _baseBowHitup;
	}

	private int _baseMr = 0; // ● 魔法防御ベース（0〜）

	public int getBaseMr() {
		return _baseMr;
	}

	private int _advenHp; // ● // アドバンスド スピリッツで増加しているＨＰ

	public int getAdvenHp() {
		return _advenHp;
	}

	public void setAdvenHp(int i) {
		_advenHp = i;
	}

	private int _advenMp; // ● // アドバンスド スピリッツで増加しているＭＰ

	public int getAdvenMp() {
		return _advenMp;
	}

	public void setAdvenMp(int i) {
		_advenMp = i;
	}

	private int _highLevel; // ● 過去最高レベル

	public int getHighLevel() {
		return _highLevel;
	}

	public void setHighLevel(int i) {
		_highLevel = i;
	}

	private int _bonusStats; // ● 割り振ったボーナスステータス

	public int getBonusStats() {
		return _bonusStats;
	}

	public void setBonusStats(int i) {
		_bonusStats = i;
	}

	private int _elixirStats; // ● エリクサーで上がったステータス

	public int getElixirStats() {
		return _elixirStats;
	}

	public void setElixirStats(int i) {
		_elixirStats = i;
	}

	private int _elfAttr; // ● エルフの属性

	public int getElfAttr() {
		return _elfAttr;
	}

	public void setElfAttr(int i) {
		_elfAttr = i;
	}

	private int _expRes; // ● EXP復旧

	public int getExpRes() {
		return _expRes;
	}

	public void setExpRes(int i) {
		_expRes = i;
	}

	private int _partnerId; // ● 結婚相手

	public int getPartnerId() {
		return _partnerId;
	}

	public void setPartnerId(int i) {
		_partnerId = i;
	}

	private int _onlineStatus; // ● オンライン状態

	public int getOnlineStatus() {
		return _onlineStatus;
	}

	public void setOnlineStatus(int i) {
		_onlineStatus = i;
	}

	private int _homeTownId; // ● ホームタウン

	public int getHomeTownId() {
		return _homeTownId;
	}

	public void setHomeTownId(int i) {
		_homeTownId = i;
	}

	private int _contribution; // ● 貢献度

	public int getContribution() {
		return _contribution;
	}

	public void setContribution(int i) {
		_contribution = i;
	}

	private int _pay; // HomeTownTimeController update

	public int getPay() {
		return _pay;
	}

	public void setPay(int i) {
		_pay = i;
	}

	// 地獄に滞在する時間（秒）
	private int _hellTime;

	public int getHellTime() {
		return _hellTime;
	}

	public void setHellTime(int i) {
		_hellTime = i;
	}

	// 血盟再加入時間
	private Timestamp _rejoinClanTime;

	public Timestamp getRejoinClanTime() {
		return _rejoinClanTime;
	}

	public void setRejoinClanTime(Timestamp time) {
		_rejoinClanTime = time;
	}

	private boolean _active; // ● 有効/無効

	public boolean isActive() {
		return _active;
	}

	public void setActive(boolean flag) {
		_active = flag;
	}

	private int _food; // ● 満腹度

	@Override
	public int getFood() {
		return _food;
	}

	@Override
	public void setFood(int i) {
		_food = i;
		setServivalScream(); // TODO 生存の叫び
	}

	public L1EquipmentSlot getEquipSlot() {
		return _equipSlot;
	}

	public static L1PcInstance load(String charName) {
		L1PcInstance result = null;
		try {
			result = CharacterTable.getInstance().loadCharacter(charName);
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return result;
	}

	/**
	 * このプレイヤーの状態をストレージへ書き込む。
	 *
	 * @throws Exception
	 */
	public void save() throws Exception {
		if (isGhost()) {
			return;
		}
		if (isInCharReset()) {
			return;
		}

		CharacterTable.getInstance().storeCharacter(this);
	}

	/**
	 * このプレイヤーのインベントリアイテムの状態をストレージへ書き込む。
	 */
	public void saveInventory() {
		for (L1ItemInstance item : getInventory().getItems()) {
			getInventory().saveItem(item);
		}
	}

	public static final int REGENSTATE_NONE = 4;
	public static final int REGENSTATE_MOVE = 2;
	public static final int REGENSTATE_ATTACK = 1;

	public void setRegenState(int state) {
		_mpRegen.setState(state);
		_hpRegen.setState(state);
	}

	public double getMaxWeight() {
		int str = getStr();
		int con = getCon();
		double maxWeight = 150 * (Math.floor(0.6 * str + 0.4 * con + 1));

		double weightReductionByArmor = getWeightReduction(); // 防具による重量軽減
		weightReductionByArmor /= 100;

		double weightReductionByDoll = 0; // マジックドールによる重量軽減
		weightReductionByDoll += L1MagicDoll.getWeightReductionByDoll(this);
		weightReductionByDoll /= 100;

		int weightReductionByMagic = 0;
		if (hasSkillEffect(DECREASE_WEIGHT)) { // ディクリースウェイト
			weightReductionByMagic = 180;
		}

		double originalWeightReduction = 0; // オリジナルステータスによる重量軽減
		originalWeightReduction += 0.04 * (getOriginalStrWeightReduction() + getOriginalConWeightReduction());

		double weightReduction = 1 + weightReductionByArmor
				+ weightReductionByDoll + originalWeightReduction;

		maxWeight *= weightReduction;

		maxWeight += weightReductionByMagic;

		maxWeight *= Config.RATE_WEIGHT_LIMIT; // ウェイトレートを掛ける

		return maxWeight;
	}

	// TODO ユグドラの実　テレポートバグ対応
	public boolean isFastMovable() {
		return (hasSkillEffect(HOLY_WALK)
				|| hasSkillEffect(MOVING_ACCELERATION) || hasSkillEffect(WIND_WALK));
	}

	public boolean isRIBRAVE() {
		return (hasSkillEffect(STATUS_RIBRAVE));
	}

	// TODO ユグドラのみテレポートバグ対応

	public boolean isFastAttackable() {
		return hasSkillEffect(BLOODLUST);
	}

	public boolean isWindShackle() { // ウィンドシャックル　攻撃速度1/2
		return hasSkillEffect(WIND_SHACKLE)
				||hasSkillEffect(AREA_WIND_SHACKLE);
	}

	public boolean isBrave() {
		return hasSkillEffect(STATUS_BRAVE);
	}

	public boolean isElfBrave() {
		return hasSkillEffect(STATUS_ELFBRAVE);
	}

	public boolean isHaste() {
		return (hasSkillEffect(STATUS_HASTE) || hasSkillEffect(HASTE)
				|| hasSkillEffect(GREATER_HASTE) || getMoveSpeed() == 1);
	}

	public boolean isSlow() {
		return (hasSkillEffect(SLOW) || hasSkillEffect(MASS_SLOW)
				|| getMoveSpeed() == 2);
	}

	public boolean isThirdSpeed() { // 三段加速
		return hasSkillEffect(STATUS_THIRD_SPEED);
	}

	private int invisDelayCounter = 0;

	public boolean isInvisDelay() {
		return (invisDelayCounter > 0);
	}

	private Object _invisTimerMonitor = new Object();

	public void addInvisDelayCounter(int counter) {
		synchronized (_invisTimerMonitor) {
			invisDelayCounter += counter;
		}
	}

	private static final long DELAY_INVIS = 3000L;

	public void beginInvisTimer() {
		addInvisDelayCounter(1);
		GeneralThreadPool.getInstance().pcSchedule(new L1PcInvisDelay(getId()),
				DELAY_INVIS);
	}

	public synchronized void addExp(int exp) {
		_exp = ExpTable.EXP_RANGE.ensure(_exp + exp);
	}

	public synchronized void addContribution(int contribution) {
		_contribution += contribution;
	}

	public void beginExpMonitor() {
		_expMonitorFuture = GeneralThreadPool.getInstance()
				.pcScheduleAtFixedRate(new L1PcExpMonitor(getId()), 0L,
						INTERVAL_EXP_MONITOR);
	}

	private void levelUp(int gap) {
		resetLevel();

		// 復活のポーション
		if (getLevel() == 99 && Config.ALT_REVIVAL_POTION) {
			try {
				L1Item l1item = ItemTable.getInstance().getTemplate(43000);
				if (l1item != null) {
					getInventory().storeItem(43000, 1);
					sendPackets(new S_ServerMessage(403, l1item.getName()));
				} else {
					sendPackets(new S_SystemMessage(I18N_FAILED_TO_OBTAIN_THE_REVIVAL_POTION));
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				sendPackets(new S_SystemMessage(I18N_FAILED_TO_OBTAIN_THE_REVIVAL_POTION));
			}
		}

		for (int i = 0; i < gap; i++) {
			short randomHp = CalcStat.calcStatHp(getType(), getBaseMaxHp(),
					getBaseCon(), getOriginalHpup());
			short randomMp = CalcStat.calcStatMp(getType(), getBaseMaxMp(),
					getBaseWis(), getOriginalMpup());
			addBaseMaxHp(randomHp);
			addBaseMaxMp(randomMp);
		}
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();
		if (getLevel() > getHighLevel()) {
			setHighLevel(getLevel());
		}

		try {
			// DBにキャラクター情報を書き込む
			save();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		// ボーナスステータス
		if (getLevel() >= 51 && getLevel() - 50 > getBonusStats()) {
			if ((getBaseStr() + getBaseDex() + getBaseCon() + getBaseInt()
					+ getBaseWis() + getBaseCha()) < 210) {
				sendPackets(new S_BonusStats(getId(), 1));
			}
		}
		sendPackets(new S_OwnCharStatus(this));
	}

	private void levelDown(int gap) {
		resetLevel();

		for (int i = 0; i > gap; i--) {
			// レベルダウン時はランダム値をそのままマイナスする為に、base値に0を設定
			short randomHp = CalcStat.calcStatHp(getType(), 0, getBaseCon(),
					getOriginalHpup());
			short randomMp = CalcStat.calcStatMp(getType(), 0, getBaseWis(),
					getOriginalMpup());
			addBaseMaxHp((short) -randomHp);
			addBaseMaxMp((short) -randomMp);
		}
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();
		if (Config.LEVEL_DOWN_RANGE != 0) {
			if (getHighLevel() - getLevel() >= Config.LEVEL_DOWN_RANGE) {
				sendPackets(new S_ServerMessage(64)); // ワールドとの接続が切断されました。
				sendPackets(new S_Disconnect());
				_log.info(String.format(String.format(I18N_BEYOND_THE_TOLERANCE_OF_THE_LEVEL_DOWN,
						this.getName(), this.getAccountName())));
				// レベルダウンの許容範囲を超えています。char=%s account=% host=%
			}
		}

		try {
			// DBにキャラクター情報を書き込む
			save();
			saveInventory();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		sendPackets(new S_OwnCharStatus(this));
	}

	public void beginGameTimeCarrier() {
		//new L1GameTimeCarrier(this).start();
		new L1RealTimeCarrier(this).start();
	}

	private boolean _ghost = false; // ゴースト

	public boolean isGhost() {
		return _ghost;
	}

	private void setGhost(boolean flag) {
		_ghost = flag;
	}

	private boolean _ghostCanTalk = true; // NPCに話しかけられるか

	public boolean isGhostCanTalk() {
		return _ghostCanTalk;
	}

	private void setGhostCanTalk(boolean flag) {
		_ghostCanTalk = flag;
	}

	private boolean _isReserveGhost = false; // ゴースト解除準備

	public boolean isReserveGhost() {
		return _isReserveGhost;
	}

	private void setReserveGhost(boolean flag) {
		_isReserveGhost = flag;
	}

	public void beginGhost(int locx, int locy, short mapid, boolean canTalk) {
		beginGhost(locx, locy, mapid, canTalk, 0);
	}

	public void beginGhost(int locx, int locy, short mapid, boolean canTalk,
			int sec) {
		if (isGhost()) {
			return;
		}
		setGhost(true);
		_ghostSaveLocX = getX();
		_ghostSaveLocY = getY();
		_ghostSaveMapId = getMapId();
		_ghostSaveHeading = getHeading();
		setGhostCanTalk(canTalk);
		L1Teleport.teleport(this, locx, locy, mapid, 5, true);
		if (sec > 0) {
			_ghostFuture = GeneralThreadPool.getInstance().pcSchedule(
					new L1PcGhostMonitor(getId()), sec * 1000);
		}
	}

	public void makeReadyEndGhost() {
		setReserveGhost(true);
		L1Teleport.teleport(this, _ghostSaveLocX, _ghostSaveLocY,
				_ghostSaveMapId, _ghostSaveHeading, true);
	}

	public void makeReadyEndGhost(boolean effectble) {
		setReserveGhost(true);
		L1Teleport.teleport(this, _ghostSaveLocX, _ghostSaveLocY,
				_ghostSaveMapId, _ghostSaveHeading, effectble);
	}

	public void endGhost() {
		setGhost(false);
		setGhostCanTalk(true);
		setReserveGhost(false);
	}

	private ScheduledFuture<?> _ghostFuture;

	private int _ghostSaveLocX = 0;
	private int _ghostSaveLocY = 0;
	private short _ghostSaveMapId = 0;
	private int _ghostSaveHeading = 0;

	private ScheduledFuture<?> _hellFuture;

	public void beginHell(boolean isFirst) {
		// 地獄以外に居るときは地獄へ強制移動
		if (getMapId() != 666) {
			int locx = 32701;
			int locy = 32777;
			short mapid = 666;
			L1Teleport.teleport(this, locx, locy, mapid, 5, false);
		}

		if (isFirst) {
			if (getPkCount() <= 10) {
				setHellTime(300);
			} else {
				setHellTime(300 * (getPkCount() - 10) + 300);
			}
			// あなたのPK回数が%0になり、地獄に落とされました。あなたはここで%1分間反省しなければなりません。
			sendPackets(new S_BlueMessage(552, String.valueOf(getPkCount()),
					String.valueOf(getHellTime() / 60)));
		} else {
			// あなたは%0秒間ここにとどまらなければなりません。
			sendPackets(new S_BlueMessage(637, String.valueOf(getHellTime())));
		}
		if (_hellFuture == null) {
			_hellFuture = GeneralThreadPool.getInstance()
					.pcScheduleAtFixedRate(new L1PcHellMonitor(getId()), 0L,
							1000L);
		}
	}

	public void endHell() {
		if (_hellFuture != null) {
			_hellFuture.cancel(false);
			_hellFuture = null;
		}
		// 地獄から脱出したら火田村へ帰還させる。
		int[] loc = L1TownLocation
				.getGetBackLoc(L1TownLocation.TOWNID_ORCISH_FOREST);
		L1Teleport.teleport(this, loc[0], loc[1], (short) loc[2], 5, true);
		try {
			save();
		} catch (Exception ignore) {
			// ignore
		}
	}

	@Override
	public void setPoisonEffect(int effectId) {
		sendPackets(new S_Poison(getId(), effectId));

		if (!isGmInvis() && !isGhost() && !isInvisble()) {
			broadcastPacket(new S_Poison(getId(), effectId));
		}
		if (isGmInvis() || isGhost()) {
		} else if (isInvisble()) {
			broadcastPacketForFindInvis(new S_Poison(getId(), effectId), true);
		} else {
			broadcastPacket(new S_Poison(getId(), effectId));
		}
	}

	@Override
	public void healHp(int pt) {
		super.healHp(pt);

		sendPackets(new S_HpUpdate(this));
	}

	@Override
	public int getKarma() {
		return _karma.get();
	}

	@Override
	public void setKarma(int i) {
		_karma.set(i);
	}

	public void addKarma(int i) {
		synchronized (_karma) {
			_karma.add(i);
		}
	}

	public int getKarmaLevel() {
		return _karma.getLevel();
	}

	public int getKarmaPercent() {
		return _karma.getPercent();
	}

	private Timestamp _lastPk;

	/**
	 * プレイヤーの最終PK時間を返す。
	 *
	 * @return _lastPk
	 *
	 */
	public Timestamp getLastPk() {
		return _lastPk;
	}

	/**
	 * プレイヤーの最終PK時間を設定する。
	 *
	 * @param time
	 *            最終PK時間（Timestamp型） 解除する場合はnullを代入
	 */
	public void setLastPk(Timestamp time) {
		_lastPk = time;
	}

	/**
	 * プレイヤーの最終PK時間を現在の時刻に設定する。
	 */
	public void setLastPk() {
		_lastPk = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * プレイヤーが手配中であるかを返す。
	 *
	 * @return 手配中であれば、true
	 */
	public boolean isWanted() {
		if (_lastPk == null) {
			return false;
		} else if (System.currentTimeMillis() - _lastPk.getTime() > 24 * 3600 * 1000) {
			setLastPk(null);
			return false;
		}
		return true;
	}

	private Timestamp _lastPkForElf;

	public Timestamp getLastPkForElf() {
		return _lastPkForElf;
	}

	public void setLastPkForElf(Timestamp time) {
		_lastPkForElf = time;
	}

	public void setLastPkForElf() {
		_lastPkForElf = new Timestamp(System.currentTimeMillis());
	}

	public boolean isWantedForElf() {
		if (_lastPkForElf == null) {
			return false;
		} else if (System.currentTimeMillis() - _lastPkForElf.getTime() > 24 * 3600 * 1000) {
			setLastPkForElf(null);
			return false;
		}
		return true;
	}

	private Timestamp _deleteTime; // キャラクター削除までの時間

	public Timestamp getDeleteTime() {
		return _deleteTime;
	}

	public void setDeleteTime(Timestamp time) {
		_deleteTime = time;
	}

	private boolean _useAdditionalWarehouse; // 追加倉庫の使用有無

	public boolean getUseAdditionalWarehouse() {
		return _useAdditionalWarehouse;
	}

	public void setUseAdditionalWarehouse(boolean flag) {
		_useAdditionalWarehouse = flag;
	}

	private Timestamp _logoutTime;

	public void setLogoutTime(Timestamp time) {
		_logoutTime = time;
	}

	public void setLogoutTime() {
		_logoutTime = new Timestamp(System.currentTimeMillis());
	}

	public Timestamp getLogoutTime() {
		return _logoutTime;
	}

	private int _safecount = 0;

	public int getSafeCount() {
		return _safecount;
	}

	public void setSafeCount(int i) {
		_safecount = i;
	}

	// アインハザードの祝福
	private int _blessOfAin;

	public void setBlessOfAin(int i) {
		_blessOfAin = i;
	}

	public void calcBlessOfAin(int i) {
		int calc = _blessOfAin + i;
		if (calc >= 2000000) {
			calc = 2000000;
		}
		_blessOfAin = calc;
	}

	public int getBlessOfAin() {
		return _blessOfAin;
	}

	// TODO 生存の叫び start
	private long _servivalScream;

	public long getServivalScream() {
		return _servivalScream;
	}

	public void setServivalScream() {
		if (getFood() >= 225) { // 満腹
			final Calendar cal = Calendar.getInstance();
			long time = cal.getTimeInMillis() / 1000;
			setServivalScream(time);
		}
	}

	public void setServivalScream(long time) {
		_servivalScream = time;
	}
	// TODO 生存の叫び end

	@Override
	public int getMagicLevel() {
		return getClassFeature().getMagicLevel(getLevel());
	}

	private int _weightReduction = 0;

	public int getWeightReduction() {
		return _weightReduction;
	}

	public void addWeightReduction(int i) {
		_weightReduction += i;
	}

	private int _originalStrWeightReduction = 0; // ● オリジナルSTR 重量軽減

	public int getOriginalStrWeightReduction() {

		return _originalStrWeightReduction;
	}

	private int _originalConWeightReduction = 0; // ● オリジナルCON 重量軽減

	public int getOriginalConWeightReduction() {

		return _originalConWeightReduction;
	}

	private int _hasteItemEquipped = 0;

	public int getHasteItemEquipped() {
		return _hasteItemEquipped;
	}

	public void addHasteItemEquipped(int i) {
		_hasteItemEquipped += i;
	}

	public void removeHasteSkillEffect() {
		if (hasSkillEffect(SLOW)) {
			removeSkillEffect(SLOW);
		}
		if (hasSkillEffect(MASS_SLOW)) {
			removeSkillEffect(MASS_SLOW);
		}
		if (hasSkillEffect(ENTANGLE)) {
			removeSkillEffect(ENTANGLE);
		}
		if (hasSkillEffect(HASTE)) {
			removeSkillEffect(HASTE);
		}
		if (hasSkillEffect(GREATER_HASTE)) {
			removeSkillEffect(GREATER_HASTE);
		}
		if (hasSkillEffect(STATUS_HASTE)) {
			removeSkillEffect(STATUS_HASTE);
		}
	}

	private int _damageReductionByArmor = 0; // 防具によるダメージ軽減

	public int getDamageReductionByArmor() {
		return _damageReductionByArmor;
	}

	public void addDamageReductionByArmor(int i) {
		_damageReductionByArmor += i;
	}

	private int _hitModifierByArmor = 0; // 防具による命中率補正

	public int getHitModifierByArmor() {
		return _hitModifierByArmor;
	}

	public void addHitModifierByArmor(int i) {
		_hitModifierByArmor += i;
	}

	private int _dmgModifierByArmor = 0; // 防具によるダメージ補正

	public int getDmgModifierByArmor() {
		return _dmgModifierByArmor;
	}

	public void addDmgModifierByArmor(int i) {
		_dmgModifierByArmor += i;
	}

	private int _bowHitModifierByArmor = 0; // 防具による弓の命中率補正

	public int getBowHitModifierByArmor() {
		return _bowHitModifierByArmor;
	}

	public void addBowHitModifierByArmor(int i) {
		_bowHitModifierByArmor += i;
	}

	private int _bowDmgModifierByArmor = 0; // 防具による弓のダメージ補正

	public int getBowDmgModifierByArmor() {
		return _bowDmgModifierByArmor;
	}

	public void addBowDmgModifierByArmor(int i) {
		_bowDmgModifierByArmor += i;
	}

	private boolean _gresValid; // G-RESが有効か

	private void setGresValid(boolean valid) {
		_gresValid = valid;
	}

	public boolean isGresValid() {
		return _gresValid;
	}

	private long _fishingTime = 0;

	public long getFishingTime() {
		return _fishingTime;
	}

	public void setFishingTime(long i) {
		_fishingTime = i;
	}

	private boolean _isFishing = false;

	public boolean isFishing() {
		return _isFishing;
	}

	public void setFishing(boolean flag) {
		_isFishing = flag;
	}

	private boolean _isFishingReady = false;

	public boolean isFishingReady() {
		return _isFishingReady;
	}

	public void setFishingReady(boolean flag) {
		_isFishingReady = flag;
	}

	private int _cookingId = 0;

	public int getCookingId() {
		return _cookingId;
	}

	public void setCookingId(int i) {
		_cookingId = i;
	}

	private int _dessertId = 0;

	public int getDessertId() {
		return _dessertId;
	}

	public void setDessertId(int i) {
		_dessertId = i;
	}

	/**
	 * LVによる命中ボーナスを設定する LVが変動した場合などに呼び出せば再計算される
	 *
	 * @return
	 */
	public void resetBaseDmgup() {
		int newBaseDmgup = 0;
		int newBaseBowDmgup = 0;
		if (isKnight() || isDarkelf() || isDragonKnight()) { // ナイト、ダークエルフ、ドラゴンナイト
			newBaseDmgup = getLevel() / 10;
			newBaseBowDmgup = 0;
		} else if (isElf()) { // エルフ
			newBaseDmgup = 0;
			newBaseBowDmgup = getLevel() / 10;
		}
		addDmgup(newBaseDmgup - _baseDmgup);
		addBowDmgup(newBaseBowDmgup - _baseBowDmgup);
		_baseDmgup = newBaseDmgup;
		_baseBowDmgup = newBaseBowDmgup;
	}

	/**
	 * LVによる命中ボーナスを設定する LVが変動した場合などに呼び出せば再計算される
	 *
	 * @return
	 */
	public void resetBaseHitup() {
		int newBaseHitup = 0;
		int newBaseBowHitup = 0;
		if (isCrown()) { // プリ
			newBaseHitup = getLevel() / 4;
			newBaseBowHitup = getLevel() / 4;
		} else if (isKnight()) { // ナイト
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isElf()) { // エルフ
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		} else if (isWizard()) { // ウィザード
			newBaseHitup = getLevel() / 8;
			newBaseBowHitup = getLevel() / 8;
		} else if (isDarkelf()) { // ダークエルフ
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isDragonKnight()) { // ドラゴンナイト
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isIllusionist()) { // イリュージョニスト
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		}
		addHitup(newBaseHitup - _baseHitup);
		addBowHitup(newBaseBowHitup - _baseBowHitup);
		_baseHitup = newBaseHitup;
		_baseBowHitup = newBaseBowHitup;
	}

	/**
	 * キャラクターステータスからACを再計算して設定する 初期設定時、LVUP,LVDown時などに呼び出す
	 */
	public void resetBaseAc() {
		int newAc = CalcStat.calcAc(getLevel(), getBaseDex());
		addAc(newAc - _baseAc);
		_baseAc = newAc;
	}

	/**
	 * キャラクターステータスから素のMRを再計算して設定する 初期設定時、スキル使用時やLVUP,LVDown時に呼び出す
	 */
	public void resetBaseMr() {
		int newMr = 0;
		if (isCrown()) { // プリ
			newMr = 10;
		} else if (isElf()) { // エルフ
			newMr = 25;
		} else if (isWizard()) { // ウィザード
			newMr = 15;
		} else if (isDarkelf()) { // ダークエルフ
			newMr = 10;
		} else if (isDragonKnight()) { // ドラゴンナイト
			newMr = 18;
		} else if (isIllusionist()) { // イリュージョニスト
			newMr = 20;
		}
		newMr += CalcStat.calcStatMr(getWis()); // WIS分のMRボーナス
		newMr += getLevel() / 2; // LVの半分だけ追加
		addMr(newMr - _baseMr);
		_baseMr = newMr;
	}

	/**
	 * EXPから現在のLvを再計算して設定する 初期設定時、死亡時やLVUP時に呼び出す
	 */
	public void resetLevel() {
		setLevel(ExpTable.getLevelByExp(_exp));

		if (_hpRegen != null) {
			_hpRegen.updateLevel();
		}
	}

	/**
	 * ステータスを初期化（希望のロウソク）
	 */
	public void resetStatus() {
		// マジックドールを召喚解除
		L1DollInstance doll = null;
		Object[] dollList = getDollList().values().toArray();
		for (Object dollObject : dollList) {
			doll = (L1DollInstance) dollObject;
			sendPackets(new S_SkillSound(doll.getId(), 5936));
			broadcastPacket(new S_SkillSound(doll.getId(), 5936));
			if (doll.isChargeDoll()) { // 課金マジックドールのタイマーを停止
				L1ItemInstance item = getInventory().getItem(doll.getItemObjId());
				item.stopChargeTimer();
			}
			doll.deleteDoll();
			sendPackets(new S_SkillIconGFX(56, 0));
			sendPackets(new S_OwnCharStatus(this));
			break;
		}

		L1SkillUse l1skilluse = new L1SkillUse();
		l1skilluse.handleCommands(this, CANCELLATION, getId(), getX(), getY(),
				null, 0, L1SkillUse.TYPE_LOGIN);
		getInventory().takeoffEquip(945); // 牛のpolyIdで装備を全部外す。
		L1Teleport.teleport(this, 32737, 32789, (short) 997, 4, false);
		int initStatusPoint = 75 + getElixirStats();
		int pcStatusPoint = getBaseStr() + getBaseInt() + getBaseWis()
				+ getBaseDex() + getBaseCon() + getBaseCha();
		if (getLevel() > 50) {
			pcStatusPoint += (getLevel() - 50 - getBonusStats());
		}
		int diff = pcStatusPoint - initStatusPoint;
		/**
		 * [50級以上]
		 *
		 * 目前點數 - 初始點數 = 人物應有等級 - 50 -> 人物應有等級 = 50 + (目前點數 - 初始點數)
		 */
		int maxLevel = 1;

		if (diff > 0) {
			// 最高到99級:也就是?不支援轉生
			maxLevel = Math.min(50 + diff, 99);
		} else {
			maxLevel = getLevel();
		}

		setTempMaxLevel(maxLevel);
		setTempLevel(1);
		setInCharReset(true);
		sendPackets(new S_CharReset(this));
	}

	/**
	 * 初期ステータスから現在のボーナスを再計算して設定する 初期設定時、再配分時に呼び出す
	 */
	public void resetOriginalHpup() {
		int originalCon = getOriginalCon();
		if (isCrown()) {
			if (originalCon == 12 || originalCon == 13) {
				_originalHpup = 1;
			} else if (originalCon == 14 || originalCon == 15) {
				_originalHpup = 2;
			} else if (originalCon >= 16) {
				_originalHpup = 3;
			} else {
				_originalHpup = 0;
			}
		} else if (isKnight()) {
			if (originalCon == 15 || originalCon == 16) {
				_originalHpup = 1;
			} else if (originalCon >= 17) {
				_originalHpup = 3;
			} else {
				_originalHpup = 0;
			}
		} else if (isElf()) {
			if (originalCon >= 13 && originalCon <= 17) {
				_originalHpup = 1;
			} else if (originalCon == 18) {
				_originalHpup = 2;
			} else {
				_originalHpup = 0;
			}
		} else if (isDarkelf()) {
			if (originalCon == 10 || originalCon == 11) {
				_originalHpup = 1;
			} else if (originalCon >= 12) {
				_originalHpup = 2;
			} else {
				_originalHpup = 0;
			}
		} else if (isWizard()) {
			if (originalCon == 14 || originalCon == 15) {
				_originalHpup = 1;
			} else if (originalCon >= 16) {
				_originalHpup = 2;
			} else {
				_originalHpup = 0;
			}
		} else if (isDragonKnight()) {
			if (originalCon == 15 || originalCon == 16) {
				_originalHpup = 1;
			} else if (originalCon >= 17) {
				_originalHpup = 3;
			} else {
				_originalHpup = 0;
			}
		} else if (isIllusionist()) {
			if (originalCon == 13 || originalCon == 14) {
				_originalHpup = 1;
			} else if (originalCon >= 15) {
				_originalHpup = 2;
			} else {
				_originalHpup = 0;
			}
		}
	}

	public void resetOriginalMpup() {
		int originalWis = getOriginalWis();
		{
			if (isCrown()) {
				if (originalWis >= 16) {
					_originalMpup = 1;
				} else {
					_originalMpup = 0;
				}
			} else if (isKnight()) {
				_originalMpup = 0;
			} else if (isElf()) {
				if (originalWis >= 14 && originalWis <= 16) {
					_originalMpup = 1;
				} else if (originalWis >= 17) {
					_originalMpup = 2;
				} else {
					_originalMpup = 0;
				}
			} else if (isDarkelf()) {
				if (originalWis >= 12) {
					_originalMpup = 1;
				} else {
					_originalMpup = 0;
				}
			} else if (isWizard()) {
				if (originalWis >= 13 && originalWis <= 16) {
					_originalMpup = 1;
				} else if (originalWis >= 17) {
					_originalMpup = 2;
				} else {
					_originalMpup = 0;
				}
			} else if (isDragonKnight()) {
				if (originalWis >= 13 && originalWis <= 15) {
					_originalMpup = 1;
				} else if (originalWis >= 16) {
					_originalMpup = 2;
				} else {
					_originalMpup = 0;
				}
			} else if (isIllusionist()) {
				if (originalWis >= 13 && originalWis <= 15) {
					_originalMpup = 1;
				} else if (originalWis >= 16) {
					_originalMpup = 2;
				} else {
					_originalMpup = 0;
				}
			}
		}
	}

	public void resetOriginalStrWeightReduction() {
		int originalStr = getOriginalStr();
		if (isCrown()) {
			if (originalStr >= 14 && originalStr <= 16) {
				_originalStrWeightReduction = 1;
			} else if (originalStr >= 17 && originalStr <= 19) {
				_originalStrWeightReduction = 2;
			} else if (originalStr == 20) {
				_originalStrWeightReduction = 3;
			} else {
				_originalStrWeightReduction = 0;
			}
		} else if (isKnight()) {
			_originalStrWeightReduction = 0;
		} else if (isElf()) {
			if (originalStr >= 16) {
				_originalStrWeightReduction = 2;
			} else {
				_originalStrWeightReduction = 0;
			}
		} else if (isDarkelf()) {
			if (originalStr >= 13 && originalStr <= 15) {
				_originalStrWeightReduction = 2;
			} else if (originalStr >= 16) {
				_originalStrWeightReduction = 3;
			} else {
				_originalStrWeightReduction = 0;
			}
		} else if (isWizard()) {
			if (originalStr >= 9) {
				_originalStrWeightReduction = 1;
			} else {
				_originalStrWeightReduction = 0;
			}
		} else if (isDragonKnight()) {
			if (originalStr >= 16) {
				_originalStrWeightReduction = 1;
			} else {
				_originalStrWeightReduction = 0;
			}
		} else if (isIllusionist()) {
			if (originalStr == 18) {
				_originalStrWeightReduction = 1;
			} else {
				_originalStrWeightReduction = 0;
			}
		}
	}

	public void resetOriginalDmgup() {
		int originalStr = getOriginalStr();
		if (isCrown()) {
			if (originalStr >= 15 && originalStr <= 17) {
				_originalDmgup = 1;
			} else if (originalStr >= 18) {
				_originalDmgup = 2;
			} else {
				_originalDmgup = 0;
			}
		} else if (isKnight()) {
			if (originalStr == 18 || originalStr == 19) {
				_originalDmgup = 2;
			} else if (originalStr == 20) {
				_originalDmgup = 4;
			} else {
				_originalDmgup = 0;
			}
		} else if (isElf()) {
			if (originalStr == 12 || originalStr == 13) {
				_originalDmgup = 1;
			} else if (originalStr >= 14) {
				_originalDmgup = 2;
			} else {
				_originalDmgup = 0;
			}
		} else if (isDarkelf()) {
			if (originalStr >= 14 && originalStr <= 17) {
				_originalDmgup = 1;
			} else if (originalStr == 18) {
				_originalDmgup = 2;
			} else {
				_originalDmgup = 0;
			}
		} else if (isWizard()) {
			if (originalStr == 10 || originalStr == 11) {
				_originalDmgup = 1;
			} else if (originalStr >= 12) {
				_originalDmgup = 2;
			} else {
				_originalDmgup = 0;
			}
		} else if (isDragonKnight()) {
			if (originalStr >= 15 && originalStr <= 17) {
				_originalDmgup = 1;
			} else if (originalStr >= 18) {
				_originalDmgup = 3;
			} else {
				_originalDmgup = 0;
			}
		} else if (isIllusionist()) {
			if (originalStr == 13 || originalStr == 14) {
				_originalDmgup = 1;
			} else if (originalStr >= 15) {
				_originalDmgup = 2;
			} else {
				_originalDmgup = 0;
			}
		}
	}

	public void resetOriginalConWeightReduction() {
		int originalCon = getOriginalCon();
		if (isCrown()) {
			if (originalCon >= 11) {
				_originalConWeightReduction = 1;
			} else {
				_originalConWeightReduction = 0;
			}
		} else if (isKnight()) {
			if (originalCon >= 15) {
				_originalConWeightReduction = 1;
			} else {
				_originalConWeightReduction = 0;
			}
		} else if (isElf()) {
			if (originalCon >= 15) {
				_originalConWeightReduction = 2;
			} else {
				_originalConWeightReduction = 0;
			}
		} else if (isDarkelf()) {
			if (originalCon >= 9) {
				_originalConWeightReduction = 1;
			} else {
				_originalConWeightReduction = 0;
			}
		} else if (isWizard()) {
			if (originalCon == 13 || originalCon == 14) {
				_originalConWeightReduction = 1;
			} else if (originalCon >= 15) {
				_originalConWeightReduction = 2;
			} else {
				_originalConWeightReduction = 0;
			}
		} else if (isDragonKnight()) {
			_originalConWeightReduction = 0;
		} else if (isIllusionist()) {
			if (originalCon == 17) {
				_originalConWeightReduction = 1;
			} else if (originalCon == 18) {
				_originalConWeightReduction = 2;
			} else {
				_originalConWeightReduction = 0;
			}
		}
	}

	public void resetOriginalBowDmgup() {
		int originalDex = getOriginalDex();
		if (isCrown()) {
			if (originalDex >= 13) {
				_originalBowDmgup = 1;
			} else {
				_originalBowDmgup = 0;
			}
		} else if (isKnight()) {
			_originalBowDmgup = 0;
		} else if (isElf()) {
			if (originalDex >= 14 && originalDex <= 16) {
				_originalBowDmgup = 2;
			} else if (originalDex >= 17) {
				_originalBowDmgup = 3;
			} else {
				_originalBowDmgup = 0;
			}
		} else if (isDarkelf()) {
			if (originalDex == 18) {
				_originalBowDmgup = 2;
			} else {
				_originalBowDmgup = 0;
			}
		} else if (isWizard()) {
			_originalBowDmgup = 0;
		} else if (isDragonKnight()) {
			_originalBowDmgup = 0;
		} else if (isIllusionist()) {
			_originalBowDmgup = 0;
		}
	}

	public void resetOriginalHitup() {
		int originalStr = getOriginalStr();
		if (isCrown()) {
			if (originalStr >= 16 && originalStr <= 18) {
				_originalHitup = 1;
			} else if (originalStr >= 19) {
				_originalHitup = 2;
			} else {
				_originalHitup = 0;
			}
		} else if (isKnight()) {
			if (originalStr == 17 || originalStr == 18) {
				_originalHitup = 2;
			} else if (originalStr >= 19) {
				_originalHitup = 4;
			} else {
				_originalHitup = 0;
			}
		} else if (isElf()) {
			if (originalStr == 13 || originalStr == 14) {
				_originalHitup = 1;
			} else if (originalStr >= 15) {
				_originalHitup = 2;
			} else {
				_originalHitup = 0;
			}
		} else if (isDarkelf()) {
			if (originalStr >= 15 && originalStr <= 17) {
				_originalHitup = 1;
			} else if (originalStr == 18) {
				_originalHitup = 2;
			} else {
				_originalHitup = 0;
			}
		} else if (isWizard()) {
			if (originalStr == 11 || originalStr == 12) {
				_originalHitup = 1;
			} else if (originalStr >= 13) {
				_originalHitup = 2;
			} else {
				_originalHitup = 0;
			}
		} else if (isDragonKnight()) {
			if (originalStr >= 14 && originalStr <= 16) {
				_originalHitup = 1;
			} else if (originalStr >= 17) {
				_originalHitup = 3;
			} else {
				_originalHitup = 0;
			}
		} else if (isIllusionist()) {
			if (originalStr == 12 || originalStr == 13) {
				_originalHitup = 1;
			} else if (originalStr == 14 || originalStr == 15) {
				_originalHitup = 2;
			} else if (originalStr == 16) {
				_originalHitup = 3;
			} else if (originalStr >= 17) {
				_originalHitup = 4;
			} else {
				_originalHitup = 0;
			}
		}
	}

	public void resetOriginalBowHitup() {
		int originalDex = getOriginalDex();
		if (isCrown()) {
			_originalBowHitup = 0;
		} else if (isKnight()) {
			_originalBowHitup = 0;
		} else if (isElf()) {
			if (originalDex >= 13 && originalDex <= 15) {
				_originalBowHitup = 2;
			} else if (originalDex >= 16) {
				_originalBowHitup = 3;
			} else {
				_originalBowHitup = 0;
			}
		} else if (isDarkelf()) {
			if (originalDex == 17) {
				_originalBowHitup = 1;
			} else if (originalDex == 18) {
				_originalBowHitup = 2;
			} else {
				_originalBowHitup = 0;
			}
		} else if (isWizard()) {
			_originalBowHitup = 0;
		} else if (isDragonKnight()) {
			_originalBowHitup = 0;
		} else if (isIllusionist()) {
			_originalBowHitup = 0;
		}
	}

	public void resetOriginalMr() {
		int originalWis = getOriginalWis();
		if (isCrown()) {
			if (originalWis == 12 || originalWis == 13) {
				_originalMr = 1;
			} else if (originalWis >= 14) {
				_originalMr = 2;
			} else {
				_originalMr = 0;
			}
		} else if (isKnight()) {
			if (originalWis == 10 || originalWis == 11) {
				_originalMr = 1;
			} else if (originalWis >= 12) {
				_originalMr = 2;
			} else {
				_originalMr = 0;
			}
		} else if (isElf()) {
			if (originalWis >= 13 && originalWis <= 15) {
				_originalMr = 1;
			} else if (originalWis >= 16) {
				_originalMr = 2;
			} else {
				_originalMr = 0;
			}
		} else if (isDarkelf()) {
			if (originalWis >= 11 && originalWis <= 13) {
				_originalMr = 1;
			} else if (originalWis == 14) {
				_originalMr = 2;
			} else if (originalWis == 15) {
				_originalMr = 3;
			} else if (originalWis >= 16) {
				_originalMr = 4;
			} else {
				_originalMr = 0;
			}
		} else if (isWizard()) {
			if (originalWis >= 15) {
				_originalMr = 1;
			} else {
				_originalMr = 0;
			}
		} else if (isDragonKnight()) {
			if (originalWis >= 14) {
				_originalMr = 2;
			} else {
				_originalMr = 0;
			}
		} else if (isIllusionist()) {
			if (originalWis >= 15 && originalWis <= 17) {
				_originalMr = 2;
			} else if (originalWis == 18) {
				_originalMr = 4;
			} else {
				_originalMr = 0;
			}
		}

		addMr(_originalMr);
	}

	public void resetOriginalMagicHit() {
		int originalInt = getOriginalInt();
		if (isCrown()) {
			if (originalInt == 12 || originalInt == 13) {
				_originalMagicHit = 1;
			} else if (originalInt >= 14) {
				_originalMagicHit = 2;
			} else {
				_originalMagicHit = 0;
			}
		} else if (isKnight()) {
			if (originalInt == 10 || originalInt == 11) {
				_originalMagicHit = 1;
			} else if (originalInt == 12) {
				_originalMagicHit = 2;
			} else {
				_originalMagicHit = 0;
			}
		} else if (isElf()) {
			if (originalInt == 13 || originalInt == 14) {
				_originalMagicHit = 1;
			} else if (originalInt >= 15) {
				_originalMagicHit = 2;
			} else {
				_originalMagicHit = 0;
			}
		} else if (isDarkelf()) {
			if (originalInt == 12 || originalInt == 13) {
				_originalMagicHit = 1;
			} else if (originalInt >= 14) {
				_originalMagicHit = 2;
			} else {
				_originalMagicHit = 0;
			}
		} else if (isWizard()) {
			if (originalInt >= 14) {
				_originalMagicHit = 1;
			} else {
				_originalMagicHit = 0;
			}
		} else if (isDragonKnight()) {
			if (originalInt == 12 || originalInt == 13) {
				_originalMagicHit = 2;
			} else if (originalInt == 14 || originalInt == 15) {
				_originalMagicHit = 3;
			} else if (originalInt >= 16) {
				_originalMagicHit = 4;
			} else {
				_originalMagicHit = 0;
			}
		} else if (isIllusionist()) {
			if (originalInt >= 13) {
				_originalMagicHit = 1;
			} else {
				_originalMagicHit = 0;
			}
		}
	}

	public void resetOriginalMagicCritical() {
		int originalInt = getOriginalInt();
		if (isCrown()) {
			_originalMagicCritical = 0;
		} else if (isKnight()) {
			_originalMagicCritical = 0;
		} else if (isElf()) {
			if (originalInt == 14 || originalInt == 15) {
				_originalMagicCritical = 2;
			} else if (originalInt >= 16) {
				_originalMagicCritical = 4;
			} else {
				_originalMagicCritical = 0;
			}
		} else if (isDarkelf()) {
			_originalMagicCritical = 0;
		} else if (isWizard()) {
			if (originalInt == 15) {
				_originalMagicCritical = 2;
			} else if (originalInt == 16) {
				_originalMagicCritical = 4;
			} else if (originalInt == 17) {
				_originalMagicCritical = 6;
			} else if (originalInt == 18) {
				_originalMagicCritical = 8;
			} else {
				_originalMagicCritical = 0;
			}
		} else if (isDragonKnight()) {
			_originalMagicCritical = 0;
		} else if (isIllusionist()) {
			_originalMagicCritical = 0;
		}
	}

	public void resetOriginalMagicConsumeReduction() {
		int originalInt = getOriginalInt();
		if (isCrown()) {
			if (originalInt == 11 || originalInt == 12) {
				_originalMagicConsumeReduction = 1;
			} else if (originalInt >= 13) {
				_originalMagicConsumeReduction = 2;
			} else {
				_originalMagicConsumeReduction = 0;
			}
		} else if (isKnight()) {
			if (originalInt == 9 || originalInt == 10) {
				_originalMagicConsumeReduction = 1;
			} else if (originalInt >= 11) {
				_originalMagicConsumeReduction = 2;
			} else {
				_originalMagicConsumeReduction = 0;
			}
		} else if (isElf()) {
			_originalMagicConsumeReduction = 0;
		} else if (isDarkelf()) {
			if (originalInt == 13 || originalInt == 14) {
				_originalMagicConsumeReduction = 1;
			} else if (originalInt >= 15) {
				_originalMagicConsumeReduction = 2;
			} else {
				_originalMagicConsumeReduction = 0;
			}
		} else if (isWizard()) {
			_originalMagicConsumeReduction = 0;
		} else if (isDragonKnight()) {
			_originalMagicConsumeReduction = 0;
		} else if (isIllusionist()) {
			if (originalInt == 14) {
				_originalMagicConsumeReduction = 1;
			} else if (originalInt >= 15) {
				_originalMagicConsumeReduction = 2;
			} else {
				_originalMagicConsumeReduction = 0;
			}
		}
	}

	public void resetOriginalMagicDamage() {
		int originalInt = getOriginalInt();
		if (isCrown()) {
			_originalMagicDamage = 0;
		} else if (isKnight()) {
			_originalMagicDamage = 0;
		} else if (isElf()) {
			_originalMagicDamage = 0;
		} else if (isDarkelf()) {
			_originalMagicDamage = 0;
		} else if (isWizard()) {
			if (originalInt >= 13) {
				_originalMagicDamage = 1;
			} else {
				_originalMagicDamage = 0;
			}
		} else if (isDragonKnight()) {
			if (originalInt == 13 || originalInt == 14) {
				_originalMagicDamage = 1;
			} else if (originalInt == 15 || originalInt == 16) {
				_originalMagicDamage = 2;
			} else if (originalInt == 17) {
				_originalMagicDamage = 3;
			} else {
				_originalMagicDamage = 0;
			}
		} else if (isIllusionist()) {
			if (originalInt == 16) {
				_originalMagicDamage = 1;
			} else if (originalInt == 17) {
				_originalMagicDamage = 2;
			} else {
				_originalMagicDamage = 0;
			}
		}
	}

	public void resetOriginalAc() {
		int originalDex = getOriginalDex();
		if (isCrown()) {
			if (originalDex >= 12 && originalDex <= 14) {
				_originalAc = 1;
			} else if (originalDex == 15 || originalDex == 16) {
				_originalAc = 2;
			} else if (originalDex >= 17) {
				_originalAc = 3;
			} else {
				_originalAc = 0;
			}
		} else if (isKnight()) {
			if (originalDex == 13 || originalDex == 14) {
				_originalAc = 1;
			} else if (originalDex >= 15) {
				_originalAc = 3;
			} else {
				_originalAc = 0;
			}
		} else if (isElf()) {
			if (originalDex >= 15 && originalDex <= 17) {
				_originalAc = 1;
			} else if (originalDex == 18) {
				_originalAc = 2;
			} else {
				_originalAc = 0;
			}
		} else if (isDarkelf()) {
			if (originalDex >= 17) {
				_originalAc = 1;
			} else {
				_originalAc = 0;
			}
		} else if (isWizard()) {
			if (originalDex == 8 || originalDex == 9) {
				_originalAc = 1;
			} else if (originalDex >= 10) {
				_originalAc = 2;
			} else {
				_originalAc = 0;
			}
		} else if (isDragonKnight()) {
			if (originalDex == 12 || originalDex == 13) {
				_originalAc = 1;
			} else if (originalDex >= 14) {
				_originalAc = 2;
			} else {
				_originalAc = 0;
			}
		} else if (isIllusionist()) {
			if (originalDex == 11 || originalDex == 12) {
				_originalAc = 1;
			} else if (originalDex >= 13) {
				_originalAc = 2;
			} else {
				_originalAc = 0;
			}
		}

		addAc(0 - _originalAc);
	}

	public void resetOriginalEr() {
		int originalDex = getOriginalDex();
		if (isCrown()) {
			if (originalDex == 14 || originalDex == 15) {
				_originalEr = 1;
			} else if (originalDex == 16 || originalDex == 17) {
				_originalEr = 2;
			} else if (originalDex == 18) {
				_originalEr = 3;
			} else {
				_originalEr = 0;
			}
		} else if (isKnight()) {
			if (originalDex == 14 || originalDex == 15) {
				_originalEr = 1;
			} else if (originalDex == 16) {
				_originalEr = 3;
			} else {
				_originalEr = 0;
			}
		} else if (isElf()) {
			_originalEr = 0;
		} else if (isDarkelf()) {
			if (originalDex >= 16) {
				_originalEr = 2;
			} else {
				_originalEr = 0;
			}
		} else if (isWizard()) {
			if (originalDex == 9 || originalDex == 10) {
				_originalEr = 1;
			} else if (originalDex == 11) {
				_originalEr = 2;
			} else {
				_originalEr = 0;
			}
		} else if (isDragonKnight()) {
			if (originalDex == 13 || originalDex == 14) {
				_originalEr = 1;
			} else if (originalDex >= 15) {
				_originalEr = 2;
			} else {
				_originalEr = 0;
			}
		} else if (isIllusionist()) {
			if (originalDex == 12 || originalDex == 13) {
				_originalEr = 1;
			} else if (originalDex >= 14) {
				_originalEr = 2;
			} else {
				_originalEr = 0;
			}
		}
	}

	public void resetOriginalHpr() {
		int originalCon = getOriginalCon();
		if (isCrown()) {
			if (originalCon == 13 || originalCon == 14) {
				_originalHpr = 1;
			} else if (originalCon == 15 || originalCon == 16) {
				_originalHpr = 2;
			} else if (originalCon == 17) {
				_originalHpr = 3;
			} else if (originalCon == 18) {
				_originalHpr = 4;
			} else {
				_originalHpr = 0;
			}
		} else if (isKnight()) {
			if (originalCon == 16 || originalCon == 17) {
				_originalHpr = 2;
			} else if (originalCon == 18) {
				_originalHpr = 4;
			} else {
				_originalHpr = 0;
			}
		} else if (isElf()) {
			if (originalCon == 14 || originalCon == 15) {
				_originalHpr = 1;
			} else if (originalCon == 16) {
				_originalHpr = 2;
			} else if (originalCon >= 17) {
				_originalHpr = 3;
			} else {
				_originalHpr = 0;
			}
		} else if (isDarkelf()) {
			if (originalCon == 11 || originalCon == 12) {
				_originalHpr = 1;
			} else if (originalCon >= 13) {
				_originalHpr = 2;
			} else {
				_originalHpr = 0;
			}
		} else if (isWizard()) {
			if (originalCon == 17) {
				_originalHpr = 1;
			} else if (originalCon == 18) {
				_originalHpr = 2;
			} else {
				_originalHpr = 0;
			}
		} else if (isDragonKnight()) {
			if (originalCon == 16 || originalCon == 17) {
				_originalHpr = 1;
			} else if (originalCon == 18) {
				_originalHpr = 3;
			} else {
				_originalHpr = 0;
			}
		} else if (isIllusionist()) {
			if (originalCon == 14 || originalCon == 15) {
				_originalHpr = 1;
			} else if (originalCon >= 16) {
				_originalHpr = 2;
			} else {
				_originalHpr = 0;
			}
		}
	}

	public void resetOriginalMpr() {
		int originalWis = getOriginalWis();
		if (isCrown()) {
			if (originalWis == 13 || originalWis == 14) {
				_originalMpr = 1;
			} else if (originalWis >= 15) {
				_originalMpr = 2;
			} else {
				_originalMpr = 0;
			}
		} else if (isKnight()) {
			if (originalWis == 11 || originalWis == 12) {
				_originalMpr = 1;
			} else if (originalWis == 13) {
				_originalMpr = 2;
			} else {
				_originalMpr = 0;
			}
		} else if (isElf()) {
			if (originalWis >= 15 && originalWis <= 17) {
				_originalMpr = 1;
			} else if (originalWis == 18) {
				_originalMpr = 2;
			} else {
				_originalMpr = 0;
			}
		} else if (isDarkelf()) {
			if (originalWis >= 13) {
				_originalMpr = 1;
			} else {
				_originalMpr = 0;
			}
		} else if (isWizard()) {
			if (originalWis == 14 || originalWis == 15) {
				_originalMpr = 1;
			} else if (originalWis == 16 || originalWis == 17) {
				_originalMpr = 2;
			} else if (originalWis == 18) {
				_originalMpr = 3;
			} else {
				_originalMpr = 0;
			}
		} else if (isDragonKnight()) {
			if (originalWis == 15 || originalWis == 16) {
				_originalMpr = 1;
			} else if (originalWis >= 17) {
				_originalMpr = 2;
			} else {
				_originalMpr = 0;
			}
		} else if (isIllusionist()) {
			if (originalWis >= 14 && originalWis <= 16) {
				_originalMpr = 1;
			} else if (originalWis >= 17) {
				_originalMpr = 2;
			} else {
				_originalMpr = 0;
			}
		}
	}

	public void refresh() {
		resetLevel();
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseMr();
		resetBaseAc();
		resetOriginalHpup();
		resetOriginalMpup();
		resetOriginalDmgup();
		resetOriginalBowDmgup();
		resetOriginalHitup();
		resetOriginalBowHitup();
		resetOriginalMr();
		resetOriginalMagicHit();
		resetOriginalMagicCritical();
		resetOriginalMagicConsumeReduction();
		resetOriginalMagicDamage();
		resetOriginalAc();
		resetOriginalEr();
		resetOriginalHpr();
		resetOriginalMpr();
		resetOriginalStrWeightReduction();
		resetOriginalConWeightReduction();
	}

	// 3.3C start
	public void startRefreshParty() {// 3.3C追加パーティー開始

		final int INTERVAL = 25000;

		if (!_rpActive) {

			_rp = new L1PartyRefresh(this);

			_timer.scheduleAtFixedRate(_rp, INTERVAL, INTERVAL);

			_rpActive = true;

		}

	}

	public void stopRefreshParty() {// 3.3C追加　暫定パーティー更新

		if (_rpActive) {

			_rp.cancel();

			_rp = null;

			_rpActive = false;

		}
	}

	// 3.3C end

	private final L1ExcludingList _excludingList = new L1ExcludingList();

	public L1ExcludingList getExcludingList() {
		return _excludingList;
	}

	// -- 加速器検知機能 --
	private final AcceleratorChecker _acceleratorChecker = new AcceleratorChecker(
			this);

	public AcceleratorChecker getAcceleratorChecker() {
		return _acceleratorChecker;
	}

	/**
	 * テレポート先の座標
	 */
	private int _teleportX = 0;

	public int getTeleportX() {
		return _teleportX;
	}

	public void setTeleportX(int i) {
		_teleportX = i;
	}

	private int _teleportY = 0;

	public int getTeleportY() {
		return _teleportY;
	}

	public void setTeleportY(int i) {
		_teleportY = i;
	}

	private short _teleportMapId = 0;

	public short getTeleportMapId() {
		return _teleportMapId;
	}

	public void setTeleportMapId(short i) {
		_teleportMapId = i;
	}

	private int _teleportHeading = 0;

	public int getTeleportHeading() {
		return _teleportHeading;
	}

	public void setTeleportHeading(int i) {
		_teleportHeading = i;
	}

	private int _tempCharGfxAtDead;

	public int getTempCharGfxAtDead() {
		return _tempCharGfxAtDead;
	}

	public void setTempCharGfxAtDead(int i) {
		_tempCharGfxAtDead = i;
	}

	private boolean _isCanWhisper = true;

	public boolean isCanWhisper() {
		return _isCanWhisper;
	}

	public void setCanWhisper(boolean flag) {
		_isCanWhisper = flag;
	}

	// 血盟
	private boolean _isShowClanChat = true;

	public boolean isShowClanChat() {
		return _isShowClanChat;
	}

	public void setShowClanChat(boolean flag) {
		_isShowClanChat = flag;
	}

	// パーティー
	private boolean _isShowPartyChat = true;

	public boolean isShowPartyChat() {
		return _isShowPartyChat;
	}

	public void setShowPartyChat(boolean flag) {
		_isShowPartyChat = flag;
	}

	private boolean _isShowTradeChat = true;

	public boolean isShowTradeChat() {
		return _isShowTradeChat;
	}

	public void setShowTradeChat(boolean flag) {
		_isShowTradeChat = flag;
	}

	private boolean _isShowWorldChat = true;

	public boolean isShowWorldChat() {
		return _isShowWorldChat;
	}

	public void setShowWorldChat(boolean flag) {
		_isShowWorldChat = flag;
	}

	private int _fightId;

	public int getFightId() {
		return _fightId;
	}

	public void setFightId(int i) {
		_fightId = i;
	}

	// 釣り可能座標
	private int _fishX;

	public int getFishX() {
		return _fishX;
	}

	public void setFishX(int i) {
		_fishX = i;
	}

	private int _fishY;

	public int getFishY() {
		return _fishY;
	}

	public void setFishY(int i) {
		_fishY = i;
	}

	private byte _chatCount = 0;

	private long _oldChatTimeInMillis = 0L;

	public void checkChatInterval() {
		long nowChatTimeInMillis = System.currentTimeMillis();
		if (_chatCount == 0) {
			_chatCount++;
			_oldChatTimeInMillis = nowChatTimeInMillis;
			return;
		}

		long chatInterval = nowChatTimeInMillis - _oldChatTimeInMillis;
		if (chatInterval > 2000) {
			_chatCount = 0;
			_oldChatTimeInMillis = 0;
		} else {
			if (_chatCount >= 3) {
				setSkillEffect(STATUS_CHAT_PROHIBITED, 120 * 1000);
				sendPackets(new S_SkillIconGFX(36, 120));
				sendPackets(new S_ServerMessage(153));
				// \f3迷惑なチャット流しをしたので、今後2分間チャットを行うことはできません。
				_chatCount = 0;
				_oldChatTimeInMillis = 0;
			}
			_chatCount++;
		}
	}

	private long _weaponDelayTime = 0L; // モーション溜め防止の武器持ち替えディレイ

	public void setWeaponDelayTime(long weaponDelayTime) {
		_weaponDelayTime = weaponDelayTime;
	}

	public long getWeaponDelayTime() {
		return _weaponDelayTime;
	}

	private int _callClanId;

	public int getCallClanId() {
		return _callClanId;
	}

	public void setCallClanId(int i) {
		_callClanId = i;
	}

	private int _callClanHeading;

	public int getCallClanHeading() {
		return _callClanHeading;
	}

	public void setCallClanHeading(int i) {
		_callClanHeading = i;
	}

	private boolean _isInCharReset = false;

	public boolean isInCharReset() {
		return _isInCharReset;
	}

	public void setInCharReset(boolean flag) {
		_isInCharReset = flag;
	}

	private int _tempLevel = 1;

	public int getTempLevel() {
		return _tempLevel;
	}

	public void setTempLevel(int i) {
		_tempLevel = i;
	}

	private int _tempMaxLevel = 1;

	public int getTempMaxLevel() {
		return _tempMaxLevel;
	}

	public void setTempMaxLevel(int i) {
		_tempMaxLevel = i;
	}
	/*
	private int _awakeSkillId = 0;

	public int getAwakeSkillId() {
		return _awakeSkillId;
	}

	public void setAwakeSkillId(int i) {
		_awakeSkillId = i;
	}
	 */
	// 　TODO ペットレース処理　start
	private int _lap = 1;

	public void setLap(int i) {
		_lap = i;
	}

	public int getLap() {
		return _lap;
	}

	private int _lapCheck = 0;

	public void setLapCheck(int i) {
		_lapCheck = i;
	}

	public int getLapCheck() {
		return _lapCheck;
	}

	public int getLapScore() {
		return _lap * 29 + _lapCheck;
	}

	// 補充
	private boolean _order_list = false;

	public boolean isInOrderList() {
		return _order_list;
	}

	public void setInOrderList(boolean bool) {
		_order_list = bool;
	}

	// TODO　ペットレーススタート時変身poly id
	private int _basepoly = 0;

	public void setBasePoly(int i) {
		_basepoly = i;
	}

	public int getBasePoly() {
		return _basepoly;
	}

	private int _minigameplaying = 0;

	public void setMiniGamePlaying(int i) {
		_minigameplaying = i;
	}

	public int getMiniGamePlaying() {
		return _minigameplaying;
	}

	// TODO　ペットレース処理　end

	private boolean _isSummonMonster = false;

	public void setSummonMonster(boolean SummonMonster) {
		_isSummonMonster = SummonMonster;
	}

	public boolean isSummonMonster() {
		return _isSummonMonster;
	}

	private boolean _isShapeChange = false;

	public void setShapeChange(boolean isShapeChange) {
		_isShapeChange = isShapeChange;
	}

	public boolean isShapeChange() {
		return _isShapeChange;
	}

	private boolean _Attacklog = false;

	public void setAttackLog(boolean i) {
		_Attacklog = i;
	}

	public boolean getAttackLog() {
		return _Attacklog;
	}

	private boolean _Potlog = true;

	public void setPotLog(boolean i) {
		_Potlog = i;
	}

	public boolean getPotLog() {
		return _Potlog;
	}

	private boolean _Droplog = true;

	public void setDropLog(boolean i) {
		_Droplog = i;
	}

	public boolean getDropLog() {
		return _Droplog;
	}

	private int _exposureTargetId = 0; // 弱点露出時の相手のID

	public int getExposureTargetId() {
		return _exposureTargetId;
	}

	public void setExposureTargetId(int i) {
		_exposureTargetId = i;
	}

	private boolean _FoeSlayer = false;

	public void setFoeSlayer(boolean FoeSlayer) {
		_FoeSlayer = FoeSlayer;
	}

	public boolean isFoeSlayer() {
		return _FoeSlayer;
	}

	private boolean _isTax = false;

	public void setTax(boolean b) {
		_isTax = b;
	}

	public boolean isTax() {
		return _isTax;
	}

	public void setPartyType(int type) {
		_partyType = type;
	}

	public int getPartyType() {
		return _partyType;
	}

	private int _expBonus = 0; // Expボーナス

	/**
	 * このプレイヤーが現在受けている経験値ボーナスをパーセンテージで返します。この値は加算分の値です。例えば本来の経験値が200で、
	 * このメソッドが45を返した場合、プレイヤーは200*0.45の90がボーナス経験値として受け取れ、合計経験値は290になります。
	 *
	 * @return 経験値ボーナス(%)。例えば45%であれば45を返します。
	 */
	public int getExpBonusPct() {
		return _expBonus;
	}

	/**
	 * このプレイヤーが受ける経験値ボーナスをパーセンテージで加算します。
	 *
	 * @param i
	 *            加算する経験値ボーナス(%)。例えば45%を加算する場合は45を指定します。この値にマイナスが指定された場合、
	 *            その分の経験値ボーナスが減算されます。
	 * @see #getExpBonusPct()
	 */
	public void addExpBonusPct(int i) {
		_expBonus += i;
	}

	private int _potionRecoveryRate = 0; // ポーション回復率

	public int getPotionRecoveryRatePct() {
		return _potionRecoveryRate;
	}

	public void addPotionRecoveryRatePct(int i) {
		_potionRecoveryRate += i;
	}

	private int _monsterKill = 0; // モンスター討伐数

	public int getMonsterKill() {
		return _monsterKill;
	}

	public void setMonsterKill(int i) {
		_monsterKill = i;
		sendPackets(new S_OwnCharStatus(this));
	}

	public void addMonsterKill(int i) {
		_monsterKill += i;
		sendPackets(new S_OwnCharStatus(this));
	}

	private int _createScrollAmount = 0; // スペルスクロール製作個数

	public int getCreateScrollAmount() {
		return _createScrollAmount;
	}

	public void setCreateScrollAmount(int i) {
		_createScrollAmount = i;
	}
	
	private int[] _comaMaterialAmount = new int[5]; // 勝利の欠片個数
	
	public int getComaMaterialAmount(int i) {
		return _comaMaterialAmount[i];
	}
	
	public void setComaMaterialAmount(int i, int amount) {
		_comaMaterialAmount[i] = amount;
	}
	
	public String[] getAllComaMaterialAmount() {
		String amounts[] = new String[5];
		for (int i = 0; i < _comaMaterialAmount.length; i++) {
			amounts[i] = String.valueOf(_comaMaterialAmount[i]);
		}
		return amounts;
	}
	
	public int getTotalComaMaterialAmount() {
		int total = 0;
		for (int amount : _comaMaterialAmount) {
			total += amount;
		}
		return total;
	}
	
	public void resetComaMaterialAmount() {
		_comaMaterialAmount = new int[5];
	}
}