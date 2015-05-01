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
package jp.l1j.server.model.skill;

import java.util.logging.Logger;
import jp.l1j.server.model.instance.L1PcInstance;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_HpUpdate;
import jp.l1j.server.packets.server.S_Liquor;
import jp.l1j.server.packets.server.S_MpUpdate;
import jp.l1j.server.packets.server.S_OwnCharAttrDef;
import jp.l1j.server.packets.server.S_OwnCharStatus2;
import jp.l1j.server.packets.server.S_SkillBrave;
import jp.l1j.server.packets.server.S_SkillHaste;
import jp.l1j.server.packets.server.S_SkillIconBloodstain;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_SpMr;

public class L1BuffUtil {
	private static Logger _log = Logger.getLogger(L1BuffUtil.class.getName());

	public static void haste(L1PcInstance pc, int timeMillis) {
		pc.setSkillEffect(STATUS_HASTE, timeMillis);

		int objId = pc.getId();
		pc.sendPackets(new S_SkillHaste(objId, 1, timeMillis / 1000));
		pc.broadcastPacket(new S_SkillHaste(objId, 1, 0));
		pc.sendPackets(new S_SkillSound(objId, 191));
		pc.broadcastPacket(new S_SkillSound(objId, 191));
		pc.setMoveSpeed(1);
	}

	public static void brave(L1PcInstance pc, int timeMillis) {
		if (pc.hasSkillEffect(STATUS_ELFBRAVE)) { // エルヴンワッフルとは重複しない
			pc.killSkillEffectTimer(STATUS_ELFBRAVE);
			pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			pc.setBraveSpeed(0);
		}
		if (pc.hasSkillEffect(HOLY_WALK)) { // ホーリーウォークとは重複しない
			pc.killSkillEffectTimer(HOLY_WALK);
			pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			pc.setBraveSpeed(0);
		}
		if (pc.hasSkillEffect(MOVING_ACCELERATION)) { // ムービングアクセレーションとは重複しない
			pc.killSkillEffectTimer(MOVING_ACCELERATION);
			pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			pc.setBraveSpeed(0);
		}
		if (pc.hasSkillEffect(WIND_WALK)) { // ウィンドウォークとは重複しない
			pc.killSkillEffectTimer(WIND_WALK);
			pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			pc.setBraveSpeed(0);
		}
		if (pc.hasSkillEffect(STATUS_RIBRAVE)) { // ユグドラの実とは重複しない
			pc.killSkillEffectTimer(STATUS_RIBRAVE);
			// XXX ユグドラの実のアイコンを消す方法が不明
			pc.setBraveSpeed(0);
		}
		if (pc.hasSkillEffect(BLOODLUST)) { // ブラッドラストとは重複しない
			pc.killSkillEffectTimer(BLOODLUST);
			pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			pc.setBraveSpeed(0);
		}

		pc.setSkillEffect(STATUS_BRAVE, timeMillis);

		int objId = pc.getId();
		pc.sendPackets(new S_SkillBrave(objId, 1, timeMillis / 1000));
		pc.broadcastPacket(new S_SkillBrave(objId, 1, 0));
		pc.sendPackets(new S_SkillSound(objId, 751));
		pc.broadcastPacket(new S_SkillSound(objId, 751));
		pc.setBraveSpeed(1);
	}
	
	public static void barrier(L1PcInstance pc, int timeMillis) {
		pc.setSkillEffect(ABSOLUTE_BARRIER, timeMillis);
	}
	
	public static void cancelBarrier(L1PcInstance pc) {
		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
		}
	}
	
	public static void thirdSpeed(L1PcInstance pc) {
		if (pc.hasSkillEffect(STATUS_THIRD_SPEED)) {
			pc.killSkillEffectTimer(STATUS_THIRD_SPEED);
		}
		pc.setSkillEffect(STATUS_THIRD_SPEED, 600 * 1000);
		pc.sendPackets(new S_SkillSound(pc.getId(), 8031));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 8031));
		pc.sendPackets(new S_Liquor(pc.getId(), 8));
		pc.broadcastPacket(new S_Liquor(pc.getId(), 8));
		//pc.sendPackets(new S_ServerMessage(1065));
	}

	public static void bloodstain(L1PcInstance pc, byte type, int time,
			boolean showGfx) {
		if (showGfx) {
			pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), 7783));
		}

		int skillId = 0;
		int iconType = 0;
		if (type == 0) { // アンタラスの血痕
			skillId = BLOODSTAIN_OF_ANTHARAS;
			if (!pc.hasSkillEffect(skillId)) {
				pc.addAc(-2);
				pc.addWater(50);
			}
			iconType = 82;
		} else if (type == 1) { // パプリオンの血痕
			skillId = BLOODSTAIN_OF_FAFURION;
			if (!pc.hasSkillEffect(skillId)) {
				pc.addHpr(3);
				pc.addMpr(1);
				pc.addWind(50);
			}
			iconType = 85;
		} else if (type == 2) { // リンドビオルの血痕
			skillId = BLOODSTAIN_OF_LINDVIOR;
			if (!pc.hasSkillEffect(skillId)) {
				pc.addSp(1);
				// 魔法クリティカル+1%は、L1Magicで対応
				pc.addFire(50);
			}
			iconType = 88;
		//} else if (type == 3) { // ヴァラカスの血痕(未実装)
		//	skillId = BLOODSTAIN_OF_VALAKAS;
		//	if (!pc.hasSkillEffect(skillId)) {
		//		pc.addDmgup(1);
		//		pc.addHitup(1);
		//		pc.addBowDmgup(1);
		//		pc.addBowHitup(1);
		//		pc.addEarth(50);
		//	}
		//	iconType = 91;
		}
		if (skillId > 0) {
			pc.sendPackets(new S_OwnCharAttrDef(pc));
			pc.sendPackets(new S_SkillIconBloodstain(iconType, time));
			pc.setSkillEffect(skillId, (time * 60 * 1000));
		}
	}

	public static void effectBlessOfDragonSlayer(L1PcInstance pc, int skillId,
			int time, int showGfx) {
		if (showGfx != 0) {
			pc.sendPackets(new S_SkillSound(pc.getId(), showGfx));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), showGfx));
		}

		if (!pc.hasSkillEffect(skillId)) {
			switch (skillId) {
			case BLESS_OF_CRAY: // クレイの祝福
				// 他の祝福と重複しない
				if (pc.hasSkillEffect(BLESS_OF_SAEL)) { // サエルの祝福
					pc.removeSkillEffect(BLESS_OF_SAEL);
				}
				if (pc.hasSkillEffect(BLESS_OF_GUNTER)) { // グンターの助言
					pc.removeSkillEffect(BLESS_OF_GUNTER);
				}
				pc.addMaxHp(100);
				pc.addMaxMp(50);
				pc.addHpr(3);
				pc.addMpr(3);
				pc.addDmgup(1);
				pc.addHitup(5);
				pc.addBowHitup(5);
				pc.addWeightReduction(40);
				pc.addEarth(30);
				break;
			case BLESS_OF_SAEL: // サエルの祝福
				// 他の祝福と重複しない
				if (pc.hasSkillEffect(BLESS_OF_CRAY)) { // クレイの祝福
					pc.removeSkillEffect(BLESS_OF_CRAY);
				}
				if (pc.hasSkillEffect(BLESS_OF_GUNTER)) { // グンターの助言
					pc.removeSkillEffect(BLESS_OF_GUNTER);
				}
				pc.addMaxHp(100);
				pc.addMaxMp(50);
				pc.addHpr(3);
				pc.addMpr(3);
				pc.addDmgup(1);
				pc.addHitup(5);
				pc.addBowHitup(5);
				pc.addWeightReduction(40);
				pc.addWater(30);
				break;
			case BLESS_OF_GUNTER: // グンターの助言
				// 他の祝福と重複しない
				if (pc.hasSkillEffect(BLESS_OF_CRAY)) { // クレイの祝福
					pc.removeSkillEffect(BLESS_OF_CRAY);
				}
				if (pc.hasSkillEffect(BLESS_OF_SAEL)) { // サエルの祝福
					pc.removeSkillEffect(BLESS_OF_SAEL);
				}
				pc.addMaxHp(100);
				pc.addMaxMp(50);
				pc.addHpr(3);
				pc.addMpr(3);
				pc.addDmgup(1);
				pc.addHitup(5);
				pc.addBowHitup(5);
				pc.addWeightReduction(40);
				pc.addWind(30);
				break;
			}
			pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_OwnCharStatus2(pc));
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		}
		pc.setSkillEffect(skillId, (time * 1000));
	}

	public static void effectBlessOfComa(L1PcInstance pc, int skillId,
			int time, int showGfx) {
		if (showGfx != 0) {
			pc.sendPackets(new S_SkillSound(pc.getId(), showGfx));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), showGfx));
		}

		if (!pc.hasSkillEffect(skillId)) {
			switch (skillId) {
			case BLESS_OF_COMA1: // コマの祝福Ａ
				if (pc.hasSkillEffect(BLESS_OF_COMA2)) {
					pc.removeSkillEffect(BLESS_OF_COMA2);
				} else if (pc.hasSkillEffect(STATUS_EXP_UP)) {
					pc.removeSkillEffect(STATUS_EXP_UP);
				} else if (pc.hasSkillEffect(STATUS_EXP_UP_II)) {
					pc.removeSkillEffect(STATUS_EXP_UP_II);
				} else if (pc.hasSkillEffect(BLESS_OF_SAMURAI)) {
					pc.removeSkillEffect(BLESS_OF_SAMURAI);
				}
				pc.addStr(5);
				pc.addDex(5);
				pc.addCon(1);
				pc.addHitup(3);
				pc.addBowHitup(3);
				pc.addAc(-3);
				break;
			case BLESS_OF_COMA2: // コマの祝福Ｂ
				if (pc.hasSkillEffect(BLESS_OF_COMA1)) {
					pc.removeSkillEffect(BLESS_OF_COMA1);
				} else if (pc.hasSkillEffect(STATUS_EXP_UP)) {
					pc.removeSkillEffect(STATUS_EXP_UP);
				} else if (pc.hasSkillEffect(STATUS_EXP_UP_II)) {
					pc.removeSkillEffect(STATUS_EXP_UP_II);
				} else if (pc.hasSkillEffect(BLESS_OF_SAMURAI)) {
					pc.removeSkillEffect(BLESS_OF_SAMURAI);
				}
				pc.addStr(5);
				pc.addDex(5);
				pc.addCon(3);
				pc.addHitup(5);
				pc.addBowHitup(5);
				pc.addAc(-8);
				pc.addSp(1);
				pc.addExpBonusPct(20);
				break;
			case BLESS_OF_SAMURAI: // 武士の心得
				if (pc.hasSkillEffect(BLESS_OF_COMA2)) {
					pc.removeSkillEffect(BLESS_OF_COMA2);
				} else if (pc.hasSkillEffect(STATUS_EXP_UP)) {
					pc.removeSkillEffect(STATUS_EXP_UP);
				} else if (pc.hasSkillEffect(STATUS_EXP_UP_II)) {
					pc.removeSkillEffect(STATUS_EXP_UP_II);
				} else if (pc.hasSkillEffect(BLESS_OF_SAMURAI)) {
					pc.removeSkillEffect(BLESS_OF_SAMURAI);
				}
				pc.addExpBonusPct(10);
				break;
			}
			pc.sendPackets(new S_OwnCharStatus2(pc));
			pc.sendPackets(new S_SpMr(pc));
		}
		pc.setSkillEffect(skillId, (time * 1000));
	}
}
