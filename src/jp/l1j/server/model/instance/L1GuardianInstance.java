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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.DropTable;
import jp.l1j.server.datatables.NpcTalkDataTable;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1NpcTalkData;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_ChangeHeading;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_NpcChatPacket;
import jp.l1j.server.packets.server.S_NpcTalkReturn;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.CalcExp;

public class L1GuardianInstance extends L1NpcInstance {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1GuardianInstance.class
			.getName());

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();
	private L1GuardianInstance _npc = this;

	/**
	 * @param template
	 */
	public L1GuardianInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void searchTarget() {
		// ターゲット検索
		L1PcInstance targetPlayer = null;

		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()
					|| pc.isGhost()) {
				continue;
			}
			if (!pc.isInvisble() || getNpcTemplate().isAgroCoi()) { // インビジチェック
				if (!pc.isElf()) { // エルフ以外
					targetPlayer = pc;
					wideBroadcastPacket(new S_NpcChatPacket(this, "$804", 2)); // エルフ以外の者よ、命が惜しければ早くここから去れ。ここは神聖な場所だ。
					break;
				} else if (pc.isElf() && pc.isWantedForElf()) {
					targetPlayer = pc;
					wideBroadcastPacket(new S_NpcChatPacket(this, "$815", 1)); // 同族を殺したものは、己の血でその罪をあがなうことになるだろう。
					break;
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

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onAction(L1PcInstance player) {
		onAction(player, 0);
	}

	@Override
	public void onAction(L1PcInstance player, int skillId) {
		if (player.getType() == 2 && player.getCurrentWeapon() == 0
				&& player.isElf()) {
			L1Attack attack = new L1Attack(player, this, skillId);

			if (attack.calcHit()) {
				if (getNpcTemplate().getNpcId() == 70848) { // エント
					int chance = _random.nextInt(100) + 1;
					if (chance <= 10) {
						player.getInventory().storeItem(40506, 1);
						player.sendPackets(new S_ServerMessage(143, "$755",
								"$794")); // \f1%0が%1をくれました。
					} else if (chance <= 60 && chance > 10) {
						player.getInventory().storeItem(40507, 1);
						player.sendPackets(new S_ServerMessage(143, "$755",
								"$763")); // \f1%0が%1をくれました。
					} else if (chance <= 70 && chance > 60) {
						player.getInventory().storeItem(40505, 1);
						player.sendPackets(new S_ServerMessage(143, "$755",
								"$770")); // \f1%0が%1をくれました。
					}
				}
				if (getNpcTemplate().getNpcId() == 70850) { // パン
					int chance = _random.nextInt(100) + 1;
					if (chance <= 30) {
						player.getInventory().storeItem(40519, 5);
						player.sendPackets(new S_ServerMessage(143, "$753",
								"$760" + " (" + 5 + ")")); // \f1%0が%1をくれました。
					}
				}
				if (getNpcTemplate().getNpcId() == 70846) { // アラクネ
					int chance = _random.nextInt(100) + 1;
					if (chance <= 30) {
						player.getInventory().storeItem(40503, 1);
						player.sendPackets(new S_ServerMessage(143, "$752",
								"$769")); // \f1%0が%1をくれました。
					}
				}
				attack.calcDamage();
				attack.calcStaffOfMana();
				attack.addPcPoisonAttack(player, this);
				attack.addChaserAttack();
				attack.addEvilAttack();
			}
			attack.action();
			attack.commit();
		} else if (getCurrentHp() > 0 && !isDead()) {
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
	public void onTalkAction(L1PcInstance player) {
		int objid = getId();
		L1NpcTalkData talking = NpcTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().getNpcId());
		L1Object object = L1World.getInstance().findObject(getId());
		L1NpcInstance target = (L1NpcInstance) object;
		String htmlid = null;
		String[] htmldata = null;

		if (talking != null) {
			int pcx = player.getX(); // PCのX座標
			int pcy = player.getY(); // PCのY座標
			int npcx = target.getX(); // NPCのX座標
			int npcy = target.getY(); // NPCのY座標

			if (pcx == npcx && pcy < npcy) {
				setHeading(0);
			} else if (pcx > npcx && pcy < npcy) {
				setHeading(1);
			} else if (pcx > npcx && pcy == npcy) {
				setHeading(2);
			} else if (pcx > npcx && pcy > npcy) {
				setHeading(3);
			} else if (pcx == npcx && pcy > npcy) {
				setHeading(4);
			} else if (pcx < npcx && pcy > npcy) {
				setHeading(5);
			} else if (pcx < npcx && pcy == npcy) {
				setHeading(6);
			} else if (pcx < npcx && pcy < npcy) {
				setHeading(7);
			}
			broadcastPacket(new S_ChangeHeading(this));

			// html表示パケット送信
			if (htmlid != null) { // htmlidが指定されている場合
				if (htmldata != null) { // html指定がある場合は表示
					player.sendPackets(new S_NpcTalkReturn(objid, htmlid,
							htmldata));
				} else {
					player.sendPackets(new S_NpcTalkReturn(objid, htmlid));
				}
			} else {
				if (player.getLawful() < -1000) { // プレイヤーがカオティック
					player.sendPackets(new S_NpcTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NpcTalkReturn(talking, objid, 1));
				}
			}
			// 動かないようにする
			synchronized (this) {
				if (_monitor != null) {
					_monitor.cancel();
				}
				setRest(true);
				_monitor = new RestMonitor();
				_restTimer.schedule(_monitor, REST_MILLISEC);
			}
		}
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) { // 攻撃でＨＰを減らすときはここを使用
		if (attacker instanceof L1PcInstance && damage > 0) {
			L1PcInstance pc = (L1PcInstance) attacker;
			if (pc.getType() == 2 && // 素手ならダメージなし
					pc.getCurrentWeapon() == 0) {
			} else {
				if (getCurrentHp() > 0 && !isDead()) {
					if (damage >= 0) {
						setHate(attacker, damage);
					}
					if (damage > 0) {
						removeSkillEffect(FOG_OF_SLEEPING);
						removeSkillEffect(PHANTASM);
					}
					onNpcAI();
					// 仲間意識をもつモンスターのターゲットに設定
					serchLink(pc, getNpcTemplate().getFamily());
					if (damage > 0) {
						pc.setPetTarget(this);
					}

					int newHp = getCurrentHp() - damage;
					if (newHp <= 0 && !isDead()) {
						setCurrentHpDirect(0);
						setDead(true);
						setStatus(ActionCodes.ACTION_Die);
						_lastattacker = attacker;
						Death death = new Death();
						GeneralThreadPool.getInstance().execute(death);
					}
					if (newHp > 0) {
						setCurrentHp(newHp);
					}
				} else if (!isDead()) { // 念のため
					setDead(true);
					setStatus(ActionCodes.ACTION_Die);
					_lastattacker = attacker;
					Death death = new Death();
					GeneralThreadPool.getInstance().execute(death);
				}
			}
		}
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

	private L1Character _lastattacker;

	class Death implements Runnable {
		L1Character lastAttacker = _lastattacker;

		public void run() {
			setDeathProcessing(true);
			setCurrentHpDirect(0);
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			int targetobjid = getId();
			getMap().setPassable(getLocation(), true);
			broadcastPacket(new S_DoActionGFX(targetobjid,
					ActionCodes.ACTION_Die));

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
				ArrayList<L1Character> targetList = _hateList
						.toTargetArrayList();
				ArrayList<Integer> hateList = _hateList.toHateArrayList();
				int exp = getExp();
				CalcExp.calcExp(player, targetobjid, targetList, hateList, exp);

				ArrayList<L1Character> dropTargetList = _dropHateList
						.toTargetArrayList();
				ArrayList<Integer> dropHateList = _dropHateList
						.toHateArrayList();
				try {
					if (getNpcTemplate().isEqualityDrop()) {
						// 平等にドロップ
						DropTable.getInstance().equalityDrop(_npc, dropTargetList);
					} else {
						// ヘイトに応じてドロップ
						DropTable.getInstance().dropShare(_npc, dropTargetList, dropHateList);
					}
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
				// カルマは止めを刺したプレイヤーに設定。ペットorサモンで倒した場合も入る。
				player.addKarma((int) (getKarma() * Config.RATE_KARMA));
			}
			setDeathProcessing(false);

			setKarma(0);
			setExp(0);
			allTargetClear();

			startDeleteTimer();
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
	}

	public void doFinalAction(L1PcInstance player) {
	}

	private static final long REST_MILLISEC = 10000;

	private static final Timer _restTimer = new Timer(true);

	private RestMonitor _monitor;

	public class RestMonitor extends TimerTask {
		@Override
		public void run() {
			setRest(false);
		}
	}
}
