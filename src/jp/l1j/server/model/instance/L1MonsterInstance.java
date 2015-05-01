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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.DropTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.NpcTalkDataTable;
import jp.l1j.server.datatables.UbTable;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1DragonSlayer;
import jp.l1j.server.model.L1HardinQuest;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1MobGroupInfo;
import jp.l1j.server.model.L1NpcTalkData;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1OrimQuest;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1UltimateBattle;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.skill.L1BuffUtil;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_NpcPack;
import jp.l1j.server.packets.server.S_NpcTalkReturn;
import jp.l1j.server.packets.server.S_RemoveObject;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillBrave;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.CalcExp;
import jp.l1j.server.utils.IdFactory;

public class L1MonsterInstance extends L1NpcInstance {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1MonsterInstance.class
			.getName());

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	private boolean _storeDroped; // ドロップアイテムの読込が完了したか

	// アイテム使用処理
	@Override
	public void onItemUse() {
		if (!isActived() && _target != null) {
			useItem(USEITEM_HASTE, 40); // ４０％の確率でヘイストポーション使用

			// アイテムじゃないけどドッペル処理
			if (getNpcTemplate().isDoppel() && _target instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) _target;
				setName(_target.getName());
				setNameId(_target.getName());
				setTitle(_target.getTitle());
				setTempLawful(_target.getLawful());
				setTempCharGfx(targetPc.getClassId());
				setGfxId(targetPc.getClassId());
				setPassiSpeed(640);
				setAtkSpeed(900); // 正確な値がわからん
				for (L1PcInstance pc : L1World.getInstance()
						.getRecognizePlayer(this)) {
					pc.sendPackets(new S_RemoveObject(this));
					pc.removeKnownObject(this);
					pc.updateObject();
				}
			}
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) { // ＨＰが４０％きったら
			useItem(USEITEM_HEAL, 50); // ５０％の確率で回復ポーション使用
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		if (0 < getCurrentHp()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK
					|| getHiddenStatus() == HIDDEN_STATUS_ICE) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Hide));
			} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Moveup));
			}
			perceivedFrom.sendPackets(new S_NpcPack(this));
			onNpcAI(); // モンスターのＡＩを開始
			if (getBraveSpeed() == 1) { // ちゃんとした方法がわからない
				perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
			}
		} else {
			perceivedFrom.sendPackets(new S_NpcPack(this));
		}
	}

	// ターゲットを探す
	public static int[][] _classGfxId = { { 0, 1 }, { 48, 61 }, { 37, 138 },
			{ 734, 1186 }, { 2786, 2796 }, { 6658, 6661 }, { 6671, 6650 } };

	@Override
	public void searchTarget() {
		// ターゲット捜索
		L1PcInstance targetPlayer = null;
		int minDistance = Short.MAX_VALUE;
		int distance = 0;

		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()
					|| pc.isMonitor() || pc.isGhost()) {
				continue;
			}

			// 闘技場内は変身／未変身に限らず全てアクティブ
			int mapId = getMapId();
			if (mapId == 88 || mapId == 98 || mapId == 92 || mapId == 91
					|| mapId == 95) {
				if (!pc.isInvisble() || !pc.isGmInvis() || getNpcTemplate().isAgroCoi()) { // インビジチェック
					distance = getLocation().getTileLineDistance(pc.getLocation());
					if (minDistance > distance) {
						minDistance = distance;
						targetPlayer = pc;
						continue;
					}
				}
			}

			if (getNpcId() == 45600) { // カーツ
				if (pc.isCrown() || pc.isDarkelf()
						|| pc.getTempCharGfx() != pc.getClassId()) { // 未変身の君主、DEにはアクティブ
					distance = getLocation().getTileLineDistance(pc.getLocation());
					if (minDistance > distance) {
						minDistance = distance;
						targetPlayer = pc;
						continue;
					}
				}
			}

			// どちらかの条件を満たす場合、友好と見なされ先制攻撃されない。
			// ・モンスターのカルマがマイナス値（バルログ側モンスター）でPCのカルマレベルが1以上（バルログ友好）
			// ・モンスターのカルマがプラス値（ヤヒ側モンスター）でPCのカルマレベルが-1以下（ヤヒ友好）
			if ((getNpcTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1)
					|| (getNpcTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1)) {
				continue;
			}
			// 見棄てられた者たちの地 カルマクエストの変身中は、各陣営のモンスターから先制攻撃されない
			if (pc.getTempCharGfx() == 6034 && getNpcTemplate().getKarma() < 0
					|| pc.getTempCharGfx() == 6035
					&& getNpcTemplate().getKarma() > 0
					|| pc.getTempCharGfx() == 6035
					&& getNpcTemplate().getNpcId() == 46070
					|| pc.getTempCharGfx() == 6035
					&& getNpcTemplate().getNpcId() == 46072) {
				continue;
			}

			if (!getNpcTemplate().isAgro() && !getNpcTemplate().isAgroSosc()
					&& getNpcTemplate().isAgroGfxId1() < 0
					&& getNpcTemplate().isAgroGfxId2() < 0) { // 完全なノンアクティブモンスター
				if (pc.getLawful() < -1000) { // プレイヤーがカオティック
					distance = getLocation().getTileLineDistance(pc.getLocation());
					if (minDistance > distance) {
						minDistance = distance;
						targetPlayer = pc;
					}
				}
				continue;
			}

			if (!pc.isInvisble() || !pc.isGmInvis() || getNpcTemplate().isAgroCoi()) { // インビジチェック
				if (pc.hasSkillEffect(67)) { // 変身してる
					if (getNpcTemplate().isAgroSosc()) { // 変身に対してアクティブ
						distance = getLocation().getTileLineDistance(pc.getLocation());
						if (minDistance > distance) {
							minDistance = distance;
							targetPlayer = pc;
							continue;
						}
					}
				} else if (getNpcTemplate().isAgro()) { // アクティブモンスター
					distance = getLocation().getTileLineDistance(pc.getLocation());
					if (minDistance > distance) {
						minDistance = distance;
						targetPlayer = pc;
						continue;
					}
				}

				// 特定のクラスorグラフィックＩＤにアクティブ
				if (getNpcTemplate().isAgroGfxId1() >= 0
						&& getNpcTemplate().isAgroGfxId1() <= 6) { // クラス指定
					if (_classGfxId[getNpcTemplate().isAgroGfxId1()][0] == pc
							.getTempCharGfx()
							|| _classGfxId[getNpcTemplate().isAgroGfxId1()][1] == pc
									.getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate().isAgroGfxId1()) { // グラフィックＩＤ指定
					distance = getLocation().getTileLineDistance(pc.getLocation());
					if (minDistance > distance) {
						minDistance = distance;
						targetPlayer = pc;
						continue;
					}
				}

				if (getNpcTemplate().isAgroGfxId2() >= 0
						&& getNpcTemplate().isAgroGfxId2() <= 6) { // クラス指定
					if (_classGfxId[getNpcTemplate().isAgroGfxId2()][0] == pc.getTempCharGfx()
							|| _classGfxId[getNpcTemplate().isAgroGfxId2()][1] == pc.getTempCharGfx()) {
						distance = getLocation().getTileLineDistance(pc.getLocation());
						if (minDistance > distance) {
							minDistance = distance;
							targetPlayer = pc;
							continue;
						}
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate().isAgroGfxId2()) { // グラフィックＩＤ指定
					distance = getLocation().getTileLineDistance(pc.getLocation());
					if (minDistance > distance) {
						minDistance = distance;
						targetPlayer = pc;
						continue;
					}
				}
			}
		}
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
	}

	// リンクの設定
	@Override
	public void setLink(L1Character cha) {
		if (cha != null && _hateList.isEmpty()) { // ターゲットがいない場合のみ追加
			_hateList.add(cha, 0);
			checkTarget();
		}
	}

	public L1MonsterInstance(L1Npc template) {
		super(template);
		_storeDroped = false;
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		if (!_storeDroped) // 無駄なオブジェクトＩＤを発行しないようにここでセット
		{
			DropTable.getInstance().setDrop(this, getInventory());
			getInventory().shuffle();
			_storeDroped = true;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int objid = getId();
		L1NpcTalkData talking = NpcTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().getNpcId());
		String htmlid = null;
		String[] htmldata = null;

		// html表示パケット送信
		if (htmlid != null) { // htmlidが指定されている場合
			if (htmldata != null) { // html指定がある場合は表示
				pc.sendPackets(new S_NpcTalkReturn(objid, htmlid, htmldata));
			} else {
				pc.sendPackets(new S_NpcTalkReturn(objid, htmlid));
			}
		} else {
			if (pc.getLawful() < -1000) { // プレイヤーがカオティック
				pc.sendPackets(new S_NpcTalkReturn(talking, objid, 2));
			} else {
				pc.sendPackets(new S_NpcTalkReturn(talking, objid, 1));
			}
		}
	}

	@Override
	public void onAction(L1PcInstance player) {
		onAction(player, 0);
	}

	@Override
	public void onAction(L1PcInstance player, int skillId) {
		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(player, this, skillId);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.calcStaffOfMana();
				attack.addPcPoisonAttack(player, this);
				attack.addChaserAttack();
				attack.addEvilAttack();
			}
			attack.action();
			attack.commit();
		}
	}

	@Override
	public void ReceiveManaDamage(L1Character attacker, int mpDamage) { // 攻撃でＭＰを減らすときはここを使用
		if (mpDamage > 0 && !isDead()) {
			// int Hate = mpDamage / 10 + 10; // 注意！計算適当 ダメージの１０分の１＋ヒットヘイト１０
			// setHate(attacker, Hate);
			setHate(attacker, mpDamage);

			onNpcAI();

			if (attacker instanceof L1PcInstance) { // 仲間意識をもつモンスターのターゲットに設定
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.getFamily());
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) { // 攻撃でＨＰを減らすときはここを使用
		if (getCurrentHp() > 0 && !isDead()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK
					|| getHiddenStatus() == HIDDEN_STATUS_FLY) {
				return;
			}
			if (damage >= 0) {
				if (!(attacker instanceof L1EffectInstance)) { // FWはヘイトなし
					setHate(attacker, damage);
				}
			}
			if (damage > 0) {
				removeSkillEffect(FOG_OF_SLEEPING);
				removeSkillEffect(PHANTASM);
			}

			onNpcAI();

			if (attacker instanceof L1PcInstance) { // 仲間意識をもつモンスターのターゲットに設定
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.getFamily());
			}

			// 血痕によるダメージ増加
			int npcid = getNpcTemplate().getNpcId();
			if ((npcid == 91514 || npcid == 91515 || npcid == 91516) // パプリオン
					&& (attacker.hasSkillEffect(BLOODSTAIN_OF_ANTHARAS))) { //  アンタラスの血痕
				damage *= 1.5;
			} else if ((npcid == 91603 || npcid == 91604 || npcid == 91605) // リンドビオル
					&& (attacker.hasSkillEffect(BLOODSTAIN_OF_FAFURION))) { // パプリオンの血痕
				damage *= 1.5;
			}
			// ヴァラカス(未実装)
			// アンタラス(未実装)

			if ((attacker instanceof L1PcInstance) && (damage > 0)) {
				L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);
			}

			int newHp = getCurrentHp() - damage;
			if (newHp <= 0 && !isDead()) {
				// XXX
				try {
					doExecutionWhenNpcDied();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int transformId = getNpcTemplate().getTransformId();
				// 変身しないモンスター
				if (transformId == -1) {
					int npcId = getNpcTemplate().getNpcId();
					if (getPortalNumber() != -1) {
						L1DragonSlayer.getInstance().dropRewardItem(
								getPortalNumber(), npcId);
						if (npcId == 91200 // アンタラスLv1
								|| npcId == 91514 // パプリオンLv1
								|| npcId == 91603 // リンドビオルLv1
								// ヴァラカスLv1(未実装)
								) {
							L1DragonSlayer.getInstance().startDragonSlayer2rd(
									getPortalNumber());
						} else if (npcId == 91201 // アンタラスLv2
								|| npcId == 91515 // パプリオンLv2
								|| npcId == 91604 // リンドビオルLv2
								// ヴァラカスLv2(未実装)
								) {
							L1DragonSlayer.getInstance().startDragonSlayer3rd(
									getPortalNumber());
						} else if (npcId == 91202 // アンタラスLv3
								|| npcId == 91516 // パプリオンLv3
								|| npcId == 91605 // リンドビオルLv3
								// ヴァラカスLv3(未実装)
								) {
							bloodstain();
							L1DragonSlayer.getInstance().endDragonSlayer(
									getPortalNumber());
						}
					}

					if (getPortalNumber() == -1
						&& (getNpcTemplate().getNpcId() == 91200 // アンタラスLv1
						|| getNpcTemplate().getNpcId() == 91201 // アンタラスLv2
						|| getNpcTemplate().getNpcId() == 91514 // パプリオンLv1
						|| getNpcTemplate().getNpcId() == 91515 // パプリオンLv2
						|| getNpcTemplate().getNpcId() == 91603 // リンドビオルLv1
						|| getNpcTemplate().getNpcId() == 91604 // リンドビオルLv2
						// ヴァラカスLv1(未実装)
						// ヴァラカスLv2(未実装)
						)) {
						doNextDragonStep(attacker, getNpcTemplate().getNpcId());
					}

					dead(attacker);
					// TODO BOSS_END情報関連STRAT
					bossendlog();
					// TODO BOSS_END情報関連END
				} else { // 変身するモンスター
					// distributeExpDropKarma(attacker);
					boolean transform = true;
					int npcId = getNpcTemplate().getNpcId();
					if (npcId >= 46210 && npcId <= 46219) { // 監視者リーパー
						int chance = _random.nextInt(100) + 1;
						if (chance <= 0) { // ボスへの変身確率は50%
							transform = false;
						}
					}

					if (transform) {
						transform(transformId);
						setCurrentHp(getMaxHp());
						setCurrentMp(getMaxMp());
						startChat(0);
					} else {
						dead(attacker);
						bossendlog();
					}
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
				hide();
			}
		} else if (!isDead()) { // 念のため
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			Death death = new Death(attacker);
			GeneralThreadPool.getInstance().execute(death);
			// Death(attacker);
		}
	}

	private void bossendlog() {
		if (Config.BOSS_END_LOG == false) { // boss_end_log on/off判定
		} else {
			boolean boss = getNpcTemplate().getBoss();
			if (boss == true) {
				L1World world = L1World.getInstance();
				String bossname = getNpcTemplate().getName();
				world.broadcastServerMessage(bossname + " が 討伐されました。");
			} else {
			}
		}
	}
	/**
	 * 指定されたMOBが死亡した場合に開くドアforクリスタルケイブ
	 * 
	 * @param npc
	 */
	private static void openDoorWhenNpcDied(L1NpcInstance npc) {
		int[] npcId = { 46143, 46144, 46145, 46146, 46147, 46148, 46149, 46150,
				46151, 46152};
		int[] doorId = { 5001, 5002, 5003, 5004, 5005, 5006, 5007, 5008, 5009,
				5010};

		for (int i = 0; i < npcId.length; i++) {
			if (npc.getNpcTemplate().getNpcId() == npcId[i]) {
				openDoorInCrystalCave(doorId[i]);
			}
		}
	}

	private static void openDoorInCrystalCave(int doorId) {
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1DoorInstance) {
				L1DoorInstance door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					door.open();
				}
			}
		}
	}

	/**
	 * 距離が5以上離れているpcを距離3〜4の位置に引き寄せる。
	 *
	 * @param pc
	 */
	private void recall(L1PcInstance pc) {
		if (getMapId() != pc.getMapId()) {
			return;
		}
		if (getLocation().getTileLineDistance(pc.getLocation()) > 4) {
			for (int count = 0; count < 10; count++) {
				L1Location newLoc = getLocation().randomLocation(3, 4, false);
				if (glanceCheck(getX(), getY(), newLoc.getX(), newLoc.getY())
								|| glanceCheck(newLoc.getX(), newLoc.getY(), getX(), getY())) {
					L1Teleport.teleport(pc, newLoc.getX(), newLoc.getY(),
							getMapId(), 5, true);
					break;
				}
			}
		}
	}

	/**
	 * PCを最大で10セル ランダムに飛ばす
	 * @id ELIZABE_TELEPORT
	 * @param cha
	 * @param effectable
	 */
	public static void randomTeleportByElizabe(L1PcInstance pc, boolean effectable) {
		L1Location newLocation = pc.getLocation().randomLocation(10, true);
		int newX = newLocation.getX();
		int newY = newLocation.getY();
		short mapId = (short) newLocation.getMapId();

		L1Teleport.teleport(pc, newX, newY, mapId, 5, effectable);
		S_SkillSound packet = new S_SkillSound(pc.getId(),2235);
		pc.sendPackets(packet);
		pc.broadcastPacket(packet);
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

	class Death implements Runnable {
		L1Character _lastAttacker;

		public Death(L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			setDeathProcessing(true);
			setCurrentHpDirect(0);
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);

			getMap().setPassable(getLocation(), true);

			broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));

			startChat(CHAT_TIMING_DEAD);

			distributeExpDropKarma(_lastAttacker);
			giveUbSeal();

			setDeathProcessing(false);

			setExp(0);
			setKarma(0);
			allTargetClear();

			startDeleteTimer();
		}
	}

	private void distributeExpDropKarma(L1Character lastAttacker) {
		if (lastAttacker == null) {
			return;
		}
		L1PcInstance pc = null;
		if (lastAttacker instanceof L1PcInstance) {
			pc = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			pc = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			pc = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
		}

		if (pc != null) {
			ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			ArrayList<Integer> hateList = _hateList.toHateArrayList();
			int exp = getExp();
			CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
			// 死亡した場合はドロップとカルマも分配、死亡せず変身した場合はEXPのみ
			if (isDead()) {
				distributeDrop();
				giveKarma(pc);
			}
		} else if (lastAttacker instanceof L1EffectInstance) { // FWが倒した場合
			ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			ArrayList<Integer> hateList = _hateList.toHateArrayList();
			// ヘイトリストにキャラクターが存在する
			if (hateList.size() != 0) {
				// 最大ヘイトを持つキャラクターが倒したものとする
				int maxHate = 0;
				for (int i = hateList.size() - 1; i >= 0; i--) {
					if (maxHate < ((Integer) hateList.get(i))) {
						maxHate = (hateList.get(i));
						lastAttacker = targetList.get(i);
					}
				}
				if (lastAttacker instanceof L1PcInstance) {
					pc = (L1PcInstance) lastAttacker;
				} else if (lastAttacker instanceof L1PetInstance) {
					pc = (L1PcInstance) ((L1PetInstance) lastAttacker)
							.getMaster();
				} else if (lastAttacker instanceof L1SummonInstance) {
					pc = (L1PcInstance) ((L1SummonInstance) lastAttacker)
							.getMaster();
				}
				if (pc != null) {
					int exp = getExp();
					CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
					// 死亡した場合はドロップとカルマも分配、死亡せず変身した場合はEXPのみ
					if (isDead()) {
						distributeDrop();
						giveKarma(pc);
					}
				}
			}
		}
	}

	private void distributeDrop() {
		ArrayList<L1Character> dropTargetList = _dropHateList
				.toTargetArrayList();
		ArrayList<Integer> dropHateList = _dropHateList.toHateArrayList();
		try {
			int npcId = getNpcTemplate().getNpcId();
			if (isResurrect() == false) { // 復活したMOBにはドロップなし
				if (npcId != 45640 || (npcId == 45640 && getTempCharGfx() == 2332)) {
					if (getNpcTemplate().isEqualityDrop()) {
						// 平等にドロップ
						DropTable.getInstance().equalityDrop(L1MonsterInstance.this,
								dropTargetList);
					} else {
						// ヘイトに応じてドロップ
						DropTable.getInstance().dropShare(L1MonsterInstance.this,
								dropTargetList, dropHateList);
					}
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private void giveKarma(L1PcInstance pc) {
		int karma = getKarma();
		if (karma != 0) {
			int karmaSign = Integer.signum(karma);
			int pcKarmaLevel = pc.getKarmaLevel();
			int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
			// カルマ背信行為は5倍
			if (pcKarmaLevelSign != 0 && karmaSign != pcKarmaLevelSign) {
				karma *= 5;
			}
			// カルマは止めを刺したプレイヤーに設定。ペットorサモンで倒した場合も入る。
			pc.addKarma((int) (karma * Config.RATE_KARMA));
		}
	}

	private void giveUbSeal() {
		if (getUbSealCount() != 0) { // UBの勇者の証
			L1UltimateBattle ub = UbTable.getInstance().getUb(getUbId());
			if (ub != null) {
				for (L1PcInstance pc : ub.getMembersArray()) {
					if (pc != null && !pc.isDead() && !pc.isGhost()) {
						L1ItemInstance item = pc.getInventory().storeItem(
								41402, getUbSealCount());
						pc.sendPackets(new S_ServerMessage(403, item
								.getLogName())); // %0を手に入れました。
					}
				}
			}
		}
	}

	public boolean is_storeDroped() {
		return _storeDroped;
	}

	public void setStoreDroped(boolean flag) {
		_storeDroped = flag;
	}

	private int _ubSealCount = 0; // UBで倒された時、参加者に与えられる勇者の証の個数

	public int getUbSealCount() {
		return _ubSealCount;
	}

	public void setUbSealCount(int i) {
		_ubSealCount = i;
	}

	private int _ubId = 0; // UBID

	public int getUbId() {
		return _ubId;
	}

	public void setUbId(int i) {
		_ubId = i;
	}

	private void hide() {
		int npcid = getNpcTemplate().getNpcId();
		if (npcid == 45061 // カーズドスパルトイ
				|| npcid == 45161 // スパルトイ
				|| npcid == 45181 // スパルトイ
				|| npcid == 45455) { // デッドリースパルトイ
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Hide));
					setStatus(13);
					broadcastPacket(new S_NpcPack(this));
				}
			}
		} else if (npcid == 45682) { // アンタラス
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_AntharasHide));
					setStatus(20);
					broadcastPacket(new S_NpcPack(this));
				}
			}
		} else if (npcid == 45067 // バレーハーピー
				|| npcid == 45264 // ハーピー
				|| npcid == 45452 // ハーピー
				|| npcid == 45090 // バレーグリフォン
				|| npcid == 45321 // グリフォン
				|| npcid == 45445) { // グリフォン
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Moveup));
					setStatus(4);
					broadcastPacket(new S_NpcPack(this));
				}
			}
		} else if (npcid == 45681) { // リンドビオル
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Moveup));
					setStatus(11);
					broadcastPacket(new S_NpcPack(this));
				}
			}
		} else if (npcid == 46107 // テーベ マンドラゴラ(白)
				|| npcid == 46108) { // テーベ マンドラゴラ(黒)
			if (getMaxHp() / 4 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Hide));
					setStatus(13);
					broadcastPacket(new S_NpcPack(this));
				}
			}
		} else if (npcid == 46291) { // シェルマン
			if (getMaxHp() / 3 > getCurrentHp() && getMaxHp() / 4 < getCurrentHp()) {
				int rnd = _random.nextInt(20);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Hide));
					setStatus(6);
					broadcastPacket(new S_NpcPack(this));
				}
			}
/*		} else if (npcid == 91603 || npcid == 91604 || npcid == 91605) { // リンドビオルレイド
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacket(new S_DoActionGFX(getId(),
							ActionCodes.ACTION_Moveup));
					setStatus(13);
					broadcastPacket(new S_NpcPack(this));
				}
			}*/
		}
	}

	public void initHide() {
		// 出現直後の隠れる動作
		// 潜るMOBは一定の確率で地中に潜った状態に、
		// 飛ぶMOBは飛んだ状態にしておく
		int npcid = getNpcTemplate().getNpcId();
		if (npcid == 45061 // カーズドスパルトイ
				|| npcid == 45161 // スパルトイ
				|| npcid == 45181 // スパルトイ
				|| npcid == 45455) { // デッドリースパルトイ
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (npcid == 45045 // クレイゴーレム
				|| npcid == 45126 // ストーンゴーレム
				|| npcid == 45134 // ストーンゴーレム
				|| npcid == 45281) { // ギランストーンゴーレム
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(4);
			}
		} else if (npcid == 45067 // バレーハーピー
				|| npcid == 45264 // ハーピー
				|| npcid == 45452 // ハーピー
				|| npcid == 45090 // バレーグリフォン
				|| npcid == 45321 // グリフォン
				|| npcid == 45445) { // グリフォン
			setHiddenStatus(HIDDEN_STATUS_FLY);
			setStatus(4);
		} else if (npcid == 45681) { // リンドビオル
			setHiddenStatus(HIDDEN_STATUS_FLY);
			setStatus(11);
		} else if (npcid == 46107 // テーベ マンドラゴラ(白)
				|| npcid == 46108) { // テーベ マンドラゴラ(黒)
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (npcid >= 46125 && npcid <= 46128) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
			setStatus(4);
		}
	}

	public void initHideForMinion(L1NpcInstance leader) {
		// グループに属するモンスターの出現直後の隠れる動作（リーダーと同じ動作にする）
		int npcid = getNpcTemplate().getNpcId();
		if (leader.getHiddenStatus() == HIDDEN_STATUS_SINK) {
			if (npcid == 45061 // カーズドスパルトイ
					|| npcid == 45161 // スパルトイ
					|| npcid == 45181 // スパルトイ
					|| npcid == 45455) { // デッドリースパルトイ
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			} else if (npcid == 45045 // クレイゴーレム
					|| npcid == 45126 // ストーンゴーレム
					|| npcid == 45134 // ストーンゴーレム
					|| npcid == 45281) { // ギランストーンゴーレム
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(4);
			} else if (npcid == 46107 // テーベ マンドラゴラ(白)
					|| npcid == 46108) { // テーベ マンドラゴラ(黒)
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (leader.getHiddenStatus() == HIDDEN_STATUS_FLY) {
			if (npcid == 45067 // バレーハーピー
					|| npcid == 45264 // ハーピー
					|| npcid == 45452 // ハーピー
					|| npcid == 45090 // バレーグリフォン
					|| npcid == 45321 // グリフォン
					|| npcid == 45445) { // グリフォン
				setHiddenStatus(HIDDEN_STATUS_FLY);
				setStatus(4);
			} else if (npcid == 45681) { // リンドビオル
				setHiddenStatus(HIDDEN_STATUS_FLY);
				setStatus(11);
			}
		} else if (npcid >= 46125 && npcid <= 46128) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
			setStatus(4);
		}
	}

	@Override
	protected void transform(int transformId) {
		super.transform(transformId);

		// DROPの再設定
		getInventory().clearItems();
		DropTable.getInstance().setDrop(this, getInventory());
		getInventory().shuffle();
	}

	private void dead(L1Character attacker) {
		setCurrentHpDirect(0);
		setDead(true);
		setStatus(ActionCodes.ACTION_Die);
		openDoorWhenNpcDied(this);
		Death death = new Death(attacker);
		GeneralThreadPool.getInstance().execute(death);
	}

	// スレッドにアクセス
	private void doExecutionWhenNpcDied() throws InterruptedException {
		if (91265 <= getNpcId() && 91268 >= getNpcId()) { // ハーディンクエスト
			L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
			if (mobGroupInfo != null) {
				boolean flag=false;
				for (L1NpcInstance mob : mobGroupInfo.getMembers()) {
					if (!mob.isDead()) {
						flag = true;
						break;
					}
				}
				if(!flag){
					if (!L1HardinQuest.getInstance().getActiveMaps(getMapId()).isDeleteTransactionNow()) {
						L1HardinQuest.getInstance().getActiveMaps(getMapId()).guardmanDeath();
					}
				}
			}
		} else if (getNpcId() == 91295) {// ブラックウィングケレニス
			if (L1HardinQuest.getInstance().getActiveMaps(getMapId()) != null) {
				if (!(L1HardinQuest.getInstance().getActiveMaps(getMapId())
						.isDeleteTransactionNow())) {
					L1HardinQuest.getInstance().getActiveMaps(getMapId())
							.lunkerDie(this);
				}
			}
		} else if (getNpcId() == 91296) {// ケレニス
			if (L1HardinQuest.getInstance().getActiveMaps(getMapId()) != null) {
				if (!(L1HardinQuest.getInstance().getActiveMaps(getMapId())
						.isDeleteTransactionNow())) {
					L1HardinQuest.getInstance().getActiveMaps(getMapId())
							.lunkerDie(this);
				}
			}
		} else if (getNpcId() == 91290) {// リーパー
			if (L1HardinQuest.getInstance().getActiveMaps(getMapId()) != null) {
				if (!(L1HardinQuest.getInstance().getActiveMaps(getMapId())
						.isDeleteTransactionNow())) {
					L1HardinQuest.getInstance().getActiveMaps(getMapId())
							.lunkerDie(this);
				}
			}
		} else if (getNpcId() == 91294) {// バフォメット
			if (L1HardinQuest.getInstance().getActiveMaps(getMapId()) != null) {
				if (!(L1HardinQuest.getInstance().getActiveMaps(getMapId())
						.isDeleteTransactionNow())) {
					L1HardinQuest.getInstance().getActiveMaps(getMapId())
							.lunkerDie(this);
				}
			}
		} else if (getNpcId() == 91455) {// 呪術ミミック
			if (L1OrimQuest.getInstance().getActiveMaps(getMapId()) != null) {
				L1OrimQuest.getInstance().getActiveMaps(getMapId()).mimicDie(this);
			}
		} else if (getNpcId() == 91481) {// クラーケン
			if (L1OrimQuest.getInstance().getActiveMaps(getMapId()) != null) {
				L1OrimQuest.getInstance().getActiveMaps(getMapId()).crakenDead();
			}
		} else if (getNpcId() == 91492) {// イカ足右
			if (L1OrimQuest.getInstance().getActiveMaps(getMapId()) != null) {
				L1OrimQuest.getInstance().getActiveMaps(getMapId()).crakenTailDead_Right();
			}
		} else if (getNpcId() == 91491) {// イカ足左
			if (L1OrimQuest.getInstance().getActiveMaps(getMapId()) != null) {
				L1OrimQuest.getInstance().getActiveMaps(getMapId()).crakenTailDead_Left();
			}
		}
	}

	private boolean _nextDragonStepRunning = false;

	protected void setNextDragonStepRunning(boolean nextDragonStepRunning) {
		_nextDragonStepRunning = nextDragonStepRunning;
	}

	protected boolean isNextDragonStepRunning() {
		return _nextDragonStepRunning;
	}

	private void bloodstain() {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this, 50)) {
			if (getNpcTemplate().getNpcId() == 91202) {
				pc.sendPackets(new S_ServerMessage(1580));
				//アンタラス：おまえらは黄昏の呪いがあるだろう！シーレンよ！母上よ！お救いください…
				L1BuffUtil.bloodstain(pc, (byte) 0, 4320, true); // アンタラスの血痕
			} else if (getNpcTemplate().getNpcId() == 91516) {
				pc.sendPackets(new S_ServerMessage(1668));
				// パプリオン：サエルめ…シーレンよ！母上よ！お救いください…
				L1BuffUtil.bloodstain(pc, (byte) 1, 4320, true); // パプリオンの血痕
			} else if (getNpcTemplate().getNpcId() == 91605) {
				pc.sendPackets(new S_ServerMessage(1773));
				// リンドビオル：ああっ！母なるシーレンよ！お助けください！
				L1BuffUtil.bloodstain(pc, (byte) 2, 4320, true); // リンドビオルの血痕
			}
			// ヴァラカスの血痕(未実装)
		}
	}

	private void doNextDragonStep(L1Character attacker, int npcid) {
		if (!isNextDragonStepRunning()) {
			int[] dragonId = {
				91200, // アンタラスLv1
				91201, // アンタラスLv2
				91514, // パプリオンLv1
				91515, // パプリオンLV2
				91603, // リンドビオルLv1
				91604 // リンドビオルLv2
				// ヴァラカスLv1(未実装)
				// ヴァラカスLv2(未実装)
			};
			int[] nextStepId = {
				91201, // アンタラスLv2
				91202, // アンタラスLv3
				91515, // パプリオンLv2
				91516, // パプリオンLv3
				91604, // リンドビオルLv2
				91605 // リンドビオルLv3
				// ヴァラカスLv2(未実装)
				// ヴァラカスLv3(未実装)
			};
			int nextSpawnId = 0;
			for (int i = 0; i < dragonId.length; i++) {
				if (npcid == dragonId[i]) {
					nextSpawnId = nextStepId[i];
					break;
				}
			}
			if (attacker != null && nextSpawnId > 0) {
				L1PcInstance _pc = null;
				if (attacker instanceof L1PcInstance) {
					_pc = (L1PcInstance) attacker;
				} else if (attacker instanceof L1PetInstance) {
					L1PetInstance pet = (L1PetInstance) attacker;
					L1Character cha = pet.getMaster();
					if (cha instanceof L1PcInstance) {
						_pc = (L1PcInstance) cha;
					}
				} else if (attacker instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) attacker;
					L1Character cha = summon.getMaster();
					if (cha instanceof L1PcInstance) {
						_pc = (L1PcInstance) cha;
					}
				}
				if (_pc != null) {
					NextDragonStep nextDragonStep = new NextDragonStep(_pc,
							this, nextSpawnId);
					GeneralThreadPool.getInstance().execute(nextDragonStep);
				}
			}
		}
	}

	class NextDragonStep implements Runnable {
		L1PcInstance _pc;
		L1MonsterInstance _mob;
		int _npcid;
		int _transformId;
		int _x;
		int _y;
		int _h;
		short _m;
		L1Location _loc = new L1Location();

		public NextDragonStep(L1PcInstance pc, L1MonsterInstance mob,
				int transformId) {
			_pc = pc;
			_mob = mob;
			_transformId = transformId;
			_x = mob.getX();
			_y = mob.getY();
			_h = mob.getHeading();
			_m = mob.getMapId();
			_loc = mob.getLocation();
		}

		@Override
		public void run() {
			setNextDragonStepRunning(true);
			try {
				Thread.sleep(10500);
				L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(
						_transformId);
				npc.setId(IdFactory.getInstance().nextId());
				npc.setMap((short) _m);
				npc.setHomeX(_x);
				npc.setHomeY(_y);
				npc.setHeading(_h);
				npc.getLocation().set(_loc);
				npc.getLocation().forward(_h);
				npc.setPortalNumber(getPortalNumber());

				broadcastPacket(new S_NpcPack(npc));
				broadcastPacket(new S_DoActionGFX(npc.getId(),
						ActionCodes.ACTION_Hide));

				L1World.getInstance().storeObject(npc);
				L1World.getInstance().addVisibleObject(npc);
				npc.updateLight();
				npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
				setNextDragonStepRunning(false);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private boolean _isCurseMimic ;

	public void setCurseMimic(boolean curseMimic) {
		_isCurseMimic = curseMimic;
	}
	
	public boolean isCurseMimic(){
		return _isCurseMimic;
	}
}
