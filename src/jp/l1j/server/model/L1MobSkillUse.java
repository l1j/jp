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
package jp.l1j.server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.MobSkillTable;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import jp.l1j.server.model.skill.L1SkillUse;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_NpcChatPacket;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.templates.L1MobSkill;
import jp.l1j.server.templates.L1Skill;
import jp.l1j.server.utils.IntRange;
import jp.l1j.server.utils.L1SpawnUtil;
import jp.l1j.server.utils.collections.Lists;
import jp.l1j.server.utils.collections.Maps;

public class L1MobSkillUse {
	private final List<L1MobSkill> _skills;

	private final L1NpcInstance _attacker;

	private static RandomGenerator _rnd = RandomGeneratorFactory.newRandom();

	private int _sleepTime = 0;

	private Map<L1MobSkill, Integer> _skillUsedCount = null;

	public L1MobSkillUse(L1NpcInstance npc) {
		_attacker = npc;
		_skills = MobSkillTable.getInstance().get(
				npc.getNpcTemplate().getNpcId());
		resetAllSkillUsedCount();
	}

	public static class L1AvailableSkill {
		private final L1MobSkill _skill;
		private final L1Character _target;

		private L1AvailableSkill(L1MobSkill skill, L1Character target) {
			_skill = skill;
			_target = target;
		}

		public L1MobSkill getSkill() {
			return _skill;
		}

		public L1Character getTarget() {
			return _target;
		}
	}

	private int getSkillUsedCount(L1MobSkill skill) {
		return _skillUsedCount.get(skill);
	}

	private void incrementSkillUsedCount(L1MobSkill skill) {
		_skillUsedCount.put(skill, _skillUsedCount.get(skill) + 1);
	}

	public void resetAllSkillUsedCount() {
		_skillUsedCount = Maps.newHashMap();
		for (L1MobSkill skill : _skills) {
			_skillUsedCount.put(skill, 0);
		}
	}

	public int getSleepTime() {
		return _sleepTime;
	}

	public List<L1AvailableSkill> availableSkills(L1Character primaryTarget,
			boolean withTriggerRandom) {
		List<L1AvailableSkill> usableSkills = Lists.newArrayList();
		for (L1MobSkill skill : _skills) {
			if (skill.getType() == L1MobSkill.TYPE_NONE) {
				continue;
			}

			L1Character target = primaryTarget;
			if (skill.getChangeTarget() != 0) {
				target = chooseNewTarget(skill);
			}
			if (!isSkillUseable(skill, target, withTriggerRandom)) {
				continue;
			}

			usableSkills.add(new L1AvailableSkill(skill, target));
		}
		return usableSkills;
	}

	/*
	 * スキル攻撃 スキル攻撃に成功したらtrueを返す。 失敗したらfalseを返す。
	 */
	public boolean useSkill(L1AvailableSkill availableSkill) {
		L1MobSkill skill = availableSkill.getSkill();
		if (!useSkill(skill, availableSkill.getTarget())) {
			return false;
		}
		int chatId = skill.getChatId();
		if (chatId > 0) {
			_attacker.broadcastPacket(new S_NpcChatPacket(_attacker,"$" + chatId, 0));
		}
		incrementSkillUsedCount(skill);
		return true;
	}

	private boolean useSkill(L1MobSkill skill, L1Character target) {
		int type = skill.getType();
		if (type == L1MobSkill.TYPE_PHYSICAL_ATTACK) { // 物理攻撃
			return physicalAttack(skill, target);
		}
		if (type == L1MobSkill.TYPE_MAGIC_ATTACK) { // 魔法攻撃
			return magicAttack(skill, target);
		}
		if (type == L1MobSkill.TYPE_SUMMON) { // サモンする
			return summon(skill, target);
		}
		if (type == L1MobSkill.TYPE_POLY) { // 強制変身させる
			return poly(skill.getPolyId());
		}
		return false;
	}

	private boolean summon(L1MobSkill skill, L1Character target) {
		int summonId = skill.getSummonId();
		IntRange countRange = skill.getSummonCountRange();
		summonMonsters(summonId, countRange.randomValue());

		// 魔方陣の表示
		_attacker.broadcastPacket(new S_SkillSound(_attacker.getId(), 761));

		// 魔法を使う動作のエフェクト
		S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(),
				ActionCodes.ACTION_SkillBuff);
		_attacker.broadcastPacket(gfx);

		_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
		return true;
	}

	/*
	 * 15セル以内で射線が通るPCを指定したモンスターに強制変身させる。 対PCしか使えない。
	 */
	private boolean poly(int polyId) {
		boolean success = false;

		for (L1PcInstance pc : L1World.getInstance()
				.getVisiblePlayer(_attacker)) {
			if (pc.isDead()) { // 死亡している
				continue;
			}
			if (pc.isGhost()) {
				continue;
			}
			if (pc.isGmInvis()) {
				continue;
			}
			if (!(_attacker.glanceCheck(_attacker.getX(), _attacker.getY(), pc.getX(), pc.getY())
							|| _attacker.glanceCheck(pc.getX(), pc.getY(), _attacker.getX(), _attacker.getY()))) {
				continue; // 射線が通らない
			}

			if (_attacker.getNpcTemplate().getNpcId() == 81082) {// ヤヒの場合
				pc.getInventory().takeoffEquip(945); // 牛のpolyIdで装備を全部外す。
			}
			L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_NPC);

			success = true;
		}
		if (success) {
			// 変身させた場合、オレンジの柱を表示する。
			for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(
					_attacker)) {
				pc.sendPackets(new S_SkillSound(pc.getId(), 230));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 230));
				break;
			}
			// 魔法を使う動作のエフェクト
			S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(),
					ActionCodes.ACTION_SkillBuff);
			_attacker.broadcastPacket(gfx);

			_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
		}

		return success;
	}

	private boolean magicAttack(L1MobSkill mobSkill, L1Character target) {
		L1SkillUse skillUse = new L1SkillUse();
		int skillId = mobSkill.getSkillId();
		boolean canUseSkill = false;

		if (skillId > 0) {
			canUseSkill = skillUse.checkUseSkill(null, skillId, target.getId(),
					target.getX(), target.getY(), null, 0,
					L1SkillUse.TYPE_NORMAL, _attacker);
		}

		if (canUseSkill == true) {
			if (mobSkill.getLeverage() > 0) {
				skillUse.setLeverage(mobSkill.getLeverage());
			}
			skillUse.handleCommands(null, skillId, target.getId(), target
					.getX(), target.getX(), null, 0, L1SkillUse.TYPE_NORMAL,
					_attacker);
			// 使用スキルによるsleepTimeの設定
			L1Skill skill = SkillTable.getInstance().findBySkillId(skillId);
			if (skill.getTarget().equals("attack") && skillId != 18) { // 有方向魔法
				_sleepTime = _attacker.getNpcTemplate().getAtkMagicSpeed();
			} else { // 無方向魔法
				_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
			}

			return true;
		}
		return false;
	}

	/*
	 * 物理攻撃
	 */
	private boolean physicalAttack(L1MobSkill skill, L1Character target) {
		Map<Integer, Integer> targetList = new ConcurrentHashMap<Integer, Integer>();
		int areaWidth = skill.getAreaWidth();
		int areaHeight = skill.getAreaHeight();
		int range = skill.getRange();
		int actId = skill.getActId();
		int gfxId = skill.getGfxId();

		// レンジ外
		if (_attacker.getLocation().getTileLineDistance(target.getLocation()) > range) {
			return false;
		}

		// 障害物がある場合攻撃不可能
		if (!(_attacker.glanceCheck(_attacker.getX(), _attacker.getY(), target.getX(), target.getY())
						|| _attacker.glanceCheck(target.getX(), target.getY(), _attacker.getX(), _attacker.getY()))) {
			return false;
		}

		_attacker.setHeading(_attacker.targetDirection(target.getX(), target
				.getY())); // 向きのセット

		if (areaHeight > 0) {
			// 範囲攻撃
			ArrayList<L1Object> objs = L1World.getInstance()
					.getVisibleBoxObjects(_attacker, _attacker.getHeading(),
							areaWidth, areaHeight);

			for (L1Object obj : objs) {
				if (!(obj instanceof L1Character)) { // ターゲットがキャラクター以外の場合何もしない。
					continue;
				}

				L1Character cha = (L1Character) obj;
				if (cha.isDead()) { // 死んでるキャラクターは対象外
					continue;
				}

				// ゴースト状態は対象外
				if (cha instanceof L1PcInstance) {
					if (((L1PcInstance) cha).isGhost()) {
						continue;
					}
				}

				// 障害物がある場合は対象外
				if (!(_attacker.glanceCheck(_attacker.getX(), _attacker.getY(), cha.getX(), cha.getY())
								|| _attacker.glanceCheck(cha.getX(), cha.getY(), _attacker.getX(), _attacker.getY()))) {
					continue;
				}

				if (target instanceof L1PcInstance
						|| target instanceof L1SummonInstance
						|| target instanceof L1PetInstance) {
					// 対PC
					if (obj instanceof L1PcInstance
							&& !((L1PcInstance) obj).isGhost()
							&& !((L1PcInstance) obj).isGmInvis()
							|| obj instanceof L1SummonInstance
							|| obj instanceof L1PetInstance) {
						targetList.put(obj.getId(), 0);
					}
				} else {
					// 対NPC
					if (obj instanceof L1MonsterInstance) {
						targetList.put(obj.getId(), 0);
					}
				}
			}
		} else {
			// 単体攻撃
			targetList.put(target.getId(), 0); // ターゲットのみ追加
		}

		if (targetList.size() == 0) {
			return false;
		}

		Iterator<Integer> ite = targetList.keySet().iterator();
		while (ite.hasNext()) {
			int targetId = ite.next();
			L1Attack attack = new L1Attack(_attacker, (L1Character) L1World
					.getInstance().findObject(targetId));
			if (attack.calcHit()) {
				if (skill.getLeverage() > 0) {
					attack.setLeverage(skill.getLeverage());
				}
				attack.calcDamage();
			}
			if (actId > 0) {
				attack.setActId(actId);
			}
			// 攻撃モーションは実際のターゲットに対してのみ行う
			if (targetId == target.getId()) {
				if (gfxId > 0) {
					_attacker.broadcastPacket(new S_SkillSound(_attacker
							.getId(), gfxId));
				}
				attack.action();
			}
			attack.commit();
		}

		_sleepTime = _attacker.getAtkSpeed();
		return true;
	}

	/*
	 * トリガーの条件のみチェック
	 */
	private boolean isSkillUseable(L1MobSkill skill, L1Character target,
			boolean withTriggerRandom) {
		if (target == null) {
			return false;
		}
		boolean useble = false;

		int type = skill.getType();
		if (withTriggerRandom || type == L1MobSkill.TYPE_SUMMON
				|| type == L1MobSkill.TYPE_POLY) {
			if (skill.getTriggerRandom() > 0) {
				int chance = _rnd.nextInt(100) + 1;
				if (chance < skill.getTriggerRandom()) {
					useble = true;
				} else {
					return false;
				}
			}
		}

		if (skill.getTriggerHp() > 0) {
			int hpRatio = (_attacker.getCurrentHp() * 100)
					/ _attacker.getMaxHp();
			if (hpRatio <= skill.getTriggerHp()) {
				useble = true;
			} else {
				return false;
			}
		}

		if (skill.getTriggerCompanionHp() > 0) {
			int hpRatio = (target.getCurrentHp() * 100) / target.getMaxHp();
			if (hpRatio <= skill.getTriggerCompanionHp()) {
				useble = true;
			} else {
				return false;
			}
		}

		if (skill.getTriggerRange() != 0) {
			int distance = _attacker.getLocation().getTileLineDistance(
					target.getLocation());

			if (skill.isTriggerDistance(distance)) {
				useble = true;
			} else {
				return false;
			}
		}

		if (skill.getTriggerCount() > 0) {
			if (getSkillUsedCount(skill) < skill.getTriggerCount()) {
				useble = true;
			} else {
				return false;
			}
		}
		return useble;
	}

	private L1NpcInstance searchMinCompanionHp() {
		L1NpcInstance minHpNpc = null;
		int minHpRatio = 100;
		int family = _attacker.getNpcTemplate().getFamily();

		for (L1Object object : L1World.getInstance().getVisibleObjects(
				_attacker)) {
			if (!(object instanceof L1NpcInstance)) {
				continue;
			}

			L1NpcInstance npc = (L1NpcInstance) object;
			if (npc.getNpcTemplate().getFamily() == family) {
				int hpRatio = (npc.getCurrentHp() * 100) / npc.getMaxHp();
				if (hpRatio < minHpRatio) {
					minHpRatio = hpRatio;
					minHpNpc = npc;
				}
			}
		}
		return minHpNpc;
	}

	private void summonMonsters(int summonId, int count) {
		for (int i = 0; i < count; i++) {
			L1SpawnUtil.summonMonster(_attacker, summonId);
		}
	}

	private boolean isInstanceOfTarget(L1Object obj) {
		return obj instanceof L1PcInstance || obj instanceof L1PetInstance
				|| obj instanceof L1SummonInstance;
	}

	private boolean satisfiesTargetConditions(L1Character cha, L1MobSkill skill) {
		int distance = _attacker.getLocation().getTileLineDistance(
				cha.getLocation());

		// 発動範囲外のキャラクターは対象外
		if (!skill.isTriggerDistance(distance)) {
			return false;
		}

		// 障害物がある場合は対象外
		if (!(_attacker.glanceCheck(_attacker.getX(), _attacker.getY(), cha.getX(), cha.getY())
						|| _attacker.glanceCheck(cha.getX(), cha.getY(), _attacker.getX(), _attacker.getY()))) {
			return false;
		}

		// ヘイトがない場合対象外
		if (!_attacker.getHateList().containsKey(cha)) {
			return false;
		}

		// 死んでるキャラクターは対象外
		if (cha.isDead()) {
			return false;
		}

		// ゴースト状態は対象外
		if (cha instanceof L1PcInstance) {
			if (((L1PcInstance) cha).isGhost()) {
				return false;
			}
		}
		return true;
	}

	private List<L1Character> makeTargetList(L1MobSkill skill) {
		List<L1Character> result = Lists.newArrayList();
		for (L1Object obj : L1World.getInstance().getVisibleObjects(_attacker)) {
			if (isInstanceOfTarget(obj)) {
				L1Character cha = (L1Character) obj;
				if (satisfiesTargetConditions(cha, skill)) {
					result.add(cha);
				}
			}
		}
		return result;
	}

	private L1Character chooseRandomTarget(L1MobSkill skill) {
		// ターゲット候補の選定
		List<L1Character> targetList = makeTargetList(skill);
		if (targetList.size() == 0) {
			return null;
		}
		return targetList.get(_rnd.nextInt(targetList.size()));
	}

	private L1Character chooseNewTarget(L1MobSkill skill) {
		int type = skill.getChangeTarget();
		if (type == L1MobSkill.CHANGE_TARGET_COMPANION) {
			return chooseCompaninon(skill);
		}
		if (type == L1MobSkill.CHANGE_TARGET_ME) {
			return _attacker;
		}
		if (type == L1MobSkill.CHANGE_TARGET_RANDOM) {
			return chooseRandomTarget(skill);
		}
		throw new IllegalArgumentException("Invalid ChangeTarget");
	}

	private L1Character chooseCompaninon(L1MobSkill skill) {
		if (skill.getTriggerCompanionHp() == 0) {
			throw new IllegalArgumentException("TriCompanionHp is 0");
		}

		L1NpcInstance companionNpc = searchMinCompanionHp();
		if (companionNpc == null) {
			return null;
		}

		int hpRatio = (companionNpc.getCurrentHp() * 100)
				/ companionNpc.getMaxHp();
		if (hpRatio <= skill.getTriggerCompanionHp()) {
			return companionNpc; // ターゲットの入れ替え
		}
		return null;
	}

	public boolean useRandomSkill(L1Character primaryTarget) {
		List<L1AvailableSkill> skills = availableSkills(primaryTarget, false);
		if (skills.isEmpty()) {
			return false;
		}
		return useSkill(Lists.getRandomElement(skills));
	}
}
