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

package jp.l1j.server.utils;

import java.util.ArrayList;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.datatables.ExpBonusTable;
import jp.l1j.server.datatables.ExpTable;
import jp.l1j.server.datatables.PetTable;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_PacketBox;
import jp.l1j.server.packets.server.S_PetPack;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1MagicDoll;
import jp.l1j.server.templates.L1Pet;

// Referenced classes of package jp.l1j.server.utils:
// CalcStat

public class CalcExp {

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(CalcExp.class.getName());

	public static final int MAX_EXP = ExpTable.getExpByLevel(100) - 1;

	private CalcExp() {
	}

	public static void calcExp(L1PcInstance l1pcinstance, int targetid,
			ArrayList<L1Character> acquisitorList, ArrayList<Integer> hateList,
			int exp) {

		int i = 0;
		double party_level = 0;
		double dist = 0;
		int member_exp = 0;
		int member_lawful = 0;
		L1Object l1object = L1World.getInstance().findObject(targetid);
		L1NpcInstance npc = (L1NpcInstance) l1object;

		// ヘイトの合計を取得
		L1Character acquisitor;
		int hate = 0;
		int acquire_exp = 0;
		int acquire_lawful = 0;
		int party_exp = 0;
		int party_lawful = 0;
		int totalHateExp = 0;
		int totalHateLawful = 0;
		int partyHateExp = 0;
		int partyHateLawful = 0;
		int ownHateExp = 0;

		if (acquisitorList.size() != hateList.size()) {
			return;
		}
		for (i = hateList.size() - 1; i >= 0; i--) {
			acquisitor = acquisitorList.get(i);
			hate = hateList.get(i);
			if (acquisitor != null && !acquisitor.isDead()) {
				totalHateExp += hate;
				if (acquisitor instanceof L1PcInstance) {
					totalHateLawful += hate;
				}
			} else { // nullだったり死んでいたら排除
				acquisitorList.remove(i);
				hateList.remove(i);
			}
		}
		if (totalHateExp == 0) { // 取得者がいない場合
			return;
		}

		if (l1object != null && !(npc instanceof L1PetInstance)
				&& !(npc instanceof L1SummonInstance)) {
			// ホームタウンシステムリニューアルの為、狩りによる貢献度を廃止
			//if (!L1World.getInstance().isProcessingContributionTotal()
			//		&& l1pcinstance.getHomeTownId() > 0) {
			//	int contribution = npc.getLevel() / 10;
			//	l1pcinstance.addContribution(contribution);
			//}
			int lawful = npc.getLawful();

			if (l1pcinstance.isInParty()) { // パーティー中
				// パーティーのヘイトの合計を算出
				// パーティーメンバー以外にはそのまま配分
				partyHateExp = 0;
				partyHateLawful = 0;
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = acquisitorList.get(i);
					hate = hateList.get(i);
					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						if (pc == l1pcinstance) {
							partyHateExp += hate;
							partyHateLawful += hate;
						} else if (l1pcinstance.getParty().isMember(pc)) {
							partyHateExp += hate;
							partyHateLawful += hate;
						} else {
							if (totalHateExp > 0) {
								acquire_exp = (exp * hate / totalHateExp);
							}
							if (totalHateLawful > 0) {
								acquire_lawful = (lawful * hate / totalHateLawful);
							}
							addExpAndLawful(pc, acquire_exp, acquire_lawful);
						}
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) pet.getMaster();
						if (master == l1pcinstance) {
							partyHateExp += hate;
						} else if (l1pcinstance.getParty().isMember(master)) {
							partyHateExp += hate;
						} else {
							if (totalHateExp > 0) {
								acquire_exp = (exp * hate / totalHateExp);
							}
							addExpPet(pet, acquire_exp);
						}
					} else if (acquisitor instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) summon.getMaster();
						if (master == l1pcinstance) {
							partyHateExp += hate;
						} else if (l1pcinstance.getParty().isMember(master)) {
							partyHateExp += hate;
						} else {
						}
					}
				}
				if (totalHateExp > 0) {
					party_exp = (exp * partyHateExp / totalHateExp);
				}
				if (totalHateLawful > 0) {
					party_lawful = (lawful * partyHateLawful / totalHateLawful);
				}

				// EXP、ロウフル配分

				// プリボーナス
				double pri_bonus = 0;
				L1PcInstance leader = l1pcinstance.getParty().getLeader();
				if (leader.isCrown()
						&& (l1pcinstance.knownsObject(leader) || l1pcinstance
								.equals(leader))) {
					pri_bonus = 0.059;
				}

				// PT経験値の計算
				L1PcInstance[] ptMembers = l1pcinstance.getParty().getMembers();
				double pt_bonus = 0;
				for (L1PcInstance each : ptMembers) {
					if (l1pcinstance.knownsObject(each)
							|| l1pcinstance.equals(each)) {
						party_level += each.getLevel() * each.getLevel();
					}
					if (l1pcinstance.knownsObject(each)) {
						pt_bonus += 0.04;
					}
				}

				party_exp = (int) (party_exp * (1 + pt_bonus + pri_bonus));

				// 自キャラクターとそのペット・サモンのヘイトの合計を算出
				if (party_level > 0) {
					dist = ((l1pcinstance.getLevel() * l1pcinstance.getLevel()) / party_level);
				}
				member_exp = (int) (party_exp * dist);
				member_lawful = (int) (party_lawful * dist);

				ownHateExp = 0;
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = acquisitorList.get(i);
					hate = hateList.get(i);
					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						if (pc == l1pcinstance) {
							ownHateExp += hate;
						}
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) pet.getMaster();
						if (master == l1pcinstance) {
							ownHateExp += hate;
						}
					} else if (acquisitor instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) summon.getMaster();
						if (master == l1pcinstance) {
							ownHateExp += hate;
						}
					}
				}
				// 自キャラクターとそのペット・サモンに分配
				if (ownHateExp != 0) { // 攻撃に参加していた
					for (i = hateList.size() - 1; i >= 0; i--) {
						acquisitor = acquisitorList.get(i);
						hate = hateList.get(i);
						if (acquisitor instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) acquisitor;
							if (pc == l1pcinstance) {
								if (ownHateExp > 0) {
									acquire_exp = (member_exp * hate / ownHateExp);
								}
								addExpAndLawful(pc, acquire_exp, member_lawful);
							}
						} else if (acquisitor instanceof L1PetInstance) {
							L1PetInstance pet = (L1PetInstance) acquisitor;
							L1PcInstance master = (L1PcInstance) pet
									.getMaster();
							if (master == l1pcinstance) {
								if (ownHateExp > 0) {
									acquire_exp = (member_exp * hate / ownHateExp);
								}
								addExpPet(pet, acquire_exp);
							}
						} else if (acquisitor instanceof L1SummonInstance) {
						}
					}
				} else { // 攻撃に参加していなかった
					// 自キャラクターのみに分配
					addExpAndLawful(l1pcinstance, member_exp, member_lawful);
				}

				// パーティーメンバーとそのペット・サモンのヘイトの合計を算出
				for (int cnt = 0; cnt < ptMembers.length; cnt++) {
					if (l1pcinstance.knownsObject(ptMembers[cnt])) {
						if (party_level > 0) {
							dist = ((ptMembers[cnt].getLevel() * ptMembers[cnt]
									.getLevel()) / party_level);
						}
						member_exp = (int) (party_exp * dist);
						member_lawful = (int) (party_lawful * dist);

						ownHateExp = 0;
						for (i = hateList.size() - 1; i >= 0; i--) {
							acquisitor = acquisitorList.get(i);
							hate = hateList.get(i);
							if (acquisitor instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) acquisitor;
								if (pc == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							} else if (acquisitor instanceof L1PetInstance) {
								L1PetInstance pet = (L1PetInstance) acquisitor;
								L1PcInstance master = (L1PcInstance) pet
										.getMaster();
								if (master == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							} else if (acquisitor instanceof L1SummonInstance) {
								L1SummonInstance summon = (L1SummonInstance) acquisitor;
								L1PcInstance master = (L1PcInstance) summon
										.getMaster();
								if (master == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							}
						}
						// パーティーメンバーとそのペット・サモンに分配
						if (ownHateExp != 0) { // 攻撃に参加していた
							for (i = hateList.size() - 1; i >= 0; i--) {
								acquisitor = acquisitorList.get(i);
								hate = hateList.get(i);
								if (acquisitor instanceof L1PcInstance) {
									L1PcInstance pc = (L1PcInstance) acquisitor;
									if (pc == ptMembers[cnt]) {
										if (ownHateExp > 0) {
											acquire_exp = (member_exp * hate / ownHateExp);
										}
										addExpAndLawful(pc, acquire_exp,
												member_lawful);
									}
								} else if (acquisitor instanceof L1PetInstance) {
									L1PetInstance pet = (L1PetInstance) acquisitor;
									L1PcInstance master = (L1PcInstance) pet
											.getMaster();
									if (master == ptMembers[cnt]) {
										if (ownHateExp > 0) {
											acquire_exp = (member_exp * hate / ownHateExp);
										}
										addExpPet(pet, acquire_exp);
									}
								} else if (acquisitor instanceof L1SummonInstance) {
								}
							}
						} else { // 攻撃に参加していなかった
							// パーティーメンバーのみに分配
							addExpAndLawful(ptMembers[cnt], member_exp,
									member_lawful);
						}
					}
				}
			} else { // パーティーを組んでいない
				// EXP、ロウフルの分配
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = acquisitorList.get(i);
					hate = hateList.get(i);
					acquire_exp = (exp * hate / totalHateExp);
					if (acquisitor instanceof L1PcInstance) {
						if (totalHateLawful > 0) {
							acquire_lawful = (lawful * hate / totalHateLawful);
						}
					}

					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						addExpAndLawful(pc, acquire_exp, acquire_lawful);
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						addExpPet(pet, acquire_exp);
					} else if (acquisitor instanceof L1SummonInstance) {
					}
				}
			}
		}
	}

	public static double calcExpBonusRate(L1PcInstance pc) {
		double penalty = ExpTable.getPenaltyRate(pc.getLevel());
		double foodBonus = 1.0;
		if (pc.hasSkillEffect(COOKING_1_7_N)
				|| pc.hasSkillEffect(COOKING_1_7_S)) {
			foodBonus = 1.01;
		}
		if (pc.hasSkillEffect(COOKING_2_7_N)
				|| pc.hasSkillEffect(COOKING_2_7_S)
				|| pc.hasSkillEffect(COOKING_4_1)
				|| pc.hasSkillEffect(COOKING_4_2)
				|| pc.hasSkillEffect(COOKING_4_3)) {
			foodBonus = 1.02;
		}
		if (pc.hasSkillEffect(COOKING_3_7_N)
				|| pc.hasSkillEffect(COOKING_3_7_S)) {
			foodBonus = 1.03;
		}
		if (pc.hasSkillEffect(COOKING_4_4)
				|| pc.hasSkillEffect(COOKING_4_4)) {
			foodBonus = 1.04;
		}
		double bonus = (pc.getExpBonusPct()
				+ ExpBonusTable.getExpBonusRate(pc.getLevel())
				+ L1MagicDoll.getExpBonusByDoll(pc)) / 100.0 + 1.0;
		return Config.RATE_XP * penalty * foodBonus * bonus;
	}

	private static void addExpAndLawful(L1PcInstance pc, int exp, int lawful) {
		double blessOfAinBonus = 1.0; // アインハザードの祝福
		if (pc.getLevel() >= 49 && pc.getBlessOfAin() > 10000) {
			pc.calcBlessOfAin(-exp);
			blessOfAinBonus = (100 + Config.BLESS_OF_AIN_EXP_BONUS) / 100;
			pc.sendPackets(new S_PacketBox(S_PacketBox.BLESS_OF_AIN, pc.getBlessOfAin()));
		}
		pc.addLawful((int) (-lawful * Config.RATE_LA));
		pc.addExp((int) (exp * blessOfAinBonus * calcExpBonusRate(pc)));
		pc.addMonsterKill(1); // モンスター討伐数をインクリメント
	}

	private static void addExpPet(L1PetInstance pet, int exp) {
		L1PcInstance pc = (L1PcInstance) pet.getMaster();

		int petNpcId = pet.getNpcTemplate().getNpcId();
		int petItemObjId = pet.getItemObjId();

		int levelBefore = pet.getLevel();
		int totalExp = (int) (exp * Config.RATE_XP + pet.getExp());
		if (totalExp >= ExpTable.getExpByLevel(51)) {
			totalExp = ExpTable.getExpByLevel(51) - 1;
		}
		pet.setExp(totalExp);

		pet.setLevel(ExpTable.getLevelByExp(totalExp));

		int expPercentage = ExpTable.getExpPercentage(pet.getLevel(), totalExp);

		int gap = pet.getLevel() - levelBefore;
		for (int i = 1; i <= gap; i++) {
			IntRange hpUpRange = pet.getPetType().getHpUpRange();
			IntRange mpUpRange = pet.getPetType().getMpUpRange();
			pet.addMaxHp(hpUpRange.randomValue());
			pet.addMaxMp(mpUpRange.randomValue());
		}

		pet.setExpPercent(expPercentage);
		pc.sendPackets(new S_PetPack(pet, pc));

		if (gap != 0) { // レベルアップしたらDBに書き込む
			L1Pet petTemplate = PetTable.getInstance()
					.getTemplate(petItemObjId);
			if (petTemplate == null) { // PetTableにない
				_log.warning("L1Pet == null");
				return;
			}
			petTemplate.setExp(pet.getExp());
			petTemplate.setLevel(pet.getLevel());
			petTemplate.setHp(pet.getMaxHp());
			petTemplate.setMp(pet.getMaxMp());
			PetTable.getInstance().storePet(petTemplate); // DBに書き込み
			pc.sendPackets(new S_ServerMessage(320, pet.getName())); // \f1%0のレベルが上がりました。
		}
	}
}