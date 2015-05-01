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

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1PolyMorph;
import jp.l1j.server.model.instance.L1MonsterInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import jp.l1j.server.model.item.executor.L1ExtraPotion;
import jp.l1j.server.model.item.executor.L1FloraPotion;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.model.skill.executor.L1BuffSkillExecutor;
import jp.l1j.server.packets.server.S_CurseBlind;
import jp.l1j.server.packets.server.S_Dexup;
import jp.l1j.server.packets.server.S_HpUpdate;
import jp.l1j.server.packets.server.S_Liquor;
import jp.l1j.server.packets.server.S_MpUpdate;
import jp.l1j.server.packets.server.S_OwnCharAttrDef;
import jp.l1j.server.packets.server.S_OwnCharStatus;
import jp.l1j.server.packets.server.S_PacketBox;
import jp.l1j.server.packets.server.S_Paralysis;
import jp.l1j.server.packets.server.S_Poison;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillBrave;
import jp.l1j.server.packets.server.S_SkillHaste;
import jp.l1j.server.packets.server.S_SkillIconAura;
import jp.l1j.server.packets.server.S_SkillIconBlessOfEva;
import jp.l1j.server.packets.server.S_SkillIconBloodstain;
import jp.l1j.server.packets.server.S_SkillIconShield;
import jp.l1j.server.packets.server.S_SkillIconWisdomPotion;
import jp.l1j.server.packets.server.S_SpMr;
import jp.l1j.server.packets.server.S_Strup;
import jp.l1j.server.templates.L1Skill;

public interface L1SkillTimer {
	public int getRemainingTime();

	public void begin();

	public void end();

	public void kill();
}

/*
 * XXX 2008/02/13 vala 本来、このクラスはあるべきではないが暫定処置。
 */
class L1SkillStop {
	private static boolean stopSkillByExecutor(L1Character cha, int skillId) {
		L1Skill skill = SkillTable.getInstance().findBySkillId(skillId);
		if (skill == null) {
			return false;
		}
		L1BuffSkillExecutor exe = skill.newBuffSkillExecutor();
		if (exe == null) {
			return false;
		}
		exe.removeEffect(cha);

		sendPacket(cha, skillId);
		return true;
	}

	private static void sendPacket(L1Character cha, int skillId) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			sendStopMessage(pc, skillId);
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
	}

	public static void stopSkill(L1Character cha, int skillId) {
		if (stopSkillByExecutor(cha, skillId)) {
			return;
		}

		if (skillId == GLOWING_AURA) { // グローウィング オーラ
			cha.addHitup(-5);
			cha.addBowHitup(-5);
			cha.addMr(-20);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SpMr(pc));
				pc.sendPackets(new S_SkillIconAura(113, 0));
			}
		} else if (skillId == SHINING_SHIELD) { // シャイニング シールド
			cha.addAc(8);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(114, 0));
			}
		} else if (skillId == BRAVE_AURA) { // ブレイブ オーラ
			cha.addDmgup(-5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(116, 0));
			}
		} else if (skillId == BOUNCE_ATTACK) { // バウンスアタック
			cha.addHitup(-6);
			cha.addBowHitup(-6);
		} else if (skillId == SHIELD) { // シールド
			cha.addAc(2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(5, 0));
			}
		} else if (skillId == BLIND_HIDING) { // ブラインドハイディング
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.delBlindHiding();
			}
		} else if (skillId == SHADOW_ARMOR) { // シャドウ アーマー
			cha.addMr(-5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SpMr(pc));
				pc.sendPackets(new S_SkillIconShield(3, 0));
			}
		} else if (skillId == DRESS_DEXTERITY) { // ドレス デクスタリティー
			cha.addDex((byte) -3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 2, 0));
			}
		} else if (skillId == DRESS_MIGHTY) { // ドレス マイティー
			cha.addStr((byte) -3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 2, 0));
			}
		} else if (skillId == SHADOW_FANG) { // シャドウ ファング
			cha.addDmgup(-5);
		} else if (skillId == ENCHANT_WEAPON) { // エンチャント ウェポン
			cha.addDmgup(-2);
		} else if (skillId == BLESSED_ARMOR) { // ブレスド アーマー
			cha.addAc(3);
		} else if (skillId == EARTH_BLESS) { // アース ブレス
			cha.addAc(7);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(7, 0));
			}
		} else if (skillId == RESIST_MAGIC) { // レジスト マジック
			cha.addMr(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SpMr(pc));
			}
		} else if (skillId == CLEAR_MIND) { // クリアー マインド
			cha.addWis((byte) -3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.resetBaseMr();
			}
		} else if (skillId == RESIST_ELEMENTAL) { // レジスト エレメント
			cha.addWind(-10);
			cha.addWater(-10);
			cha.addFire(-10);
			cha.addEarth(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		} else if (skillId == ELEMENTAL_PROTECTION) { // エレメンタルプロテクション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				int attr = pc.getElfAttr();
				if (attr == 1) {
					cha.addEarth(-50);
				} else if (attr == 2) {
					cha.addFire(-50);
				} else if (attr == 4) {
					cha.addWater(-50);
				} else if (attr == 8) {
					cha.addWind(-50);
				}
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		} else if (skillId == IRON_SKIN) { // アイアン スキン
			cha.addAc(10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(10, 0));
			}
		} else if (skillId == EARTH_SKIN) { // アース スキン
			cha.addAc(6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(6, 0));
			}
		} else if (skillId == PHYSICAL_ENCHANT_STR) { // フィジカル エンチャント：STR
			cha.addStr((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 5, 0));
			}
		} else if (skillId == PHYSICAL_ENCHANT_DEX) { // フィジカル エンチャント：DEX
			cha.addDex((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 5, 0));
			}
		} else if (skillId == FIRE_WEAPON) { // ファイアー ウェポン
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(147, 0));
			}
		} else if (skillId == FIRE_BLESS) { // ファイアー ブレス
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(154, 0));
			}
		} else if (skillId == BURNING_WEAPON) { // バーニング ウェポン
			cha.addDmgup(-6);
			cha.addHitup(-6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(162, 0));
			}
		} else if (skillId == BLESS_WEAPON) { // ブレス ウェポン
			cha.addDmgup(-2);
			cha.addHitup(-2);
			cha.addBowHitup(-2);
		} else if (skillId == WIND_SHOT) { // ウィンド ショット
			cha.addBowHitup(-6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(148, 0));
			}
		} else if (skillId == STORM_EYE) { // ストーム アイ
			cha.addBowHitup(-2);
			cha.addBowDmgup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(155, 0));
			}
		} else if (skillId == STORM_SHOT) { // ストーム ショット
			cha.addBowDmgup(-6);
			cha.addBowHitup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(165, 0));
			}
		} else if (skillId == SHAPE_CHANGE) { // シェイプ チェンジ
			L1PolyMorph.undoPoly(cha);
		} else if (skillId == ADVANCE_SPIRIT) { // アドバンスド スピリッツ
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-pc.getAdvenHp());
				pc.addMaxMp(-pc.getAdvenMp());
				pc.setAdvenHp(0);
				pc.setAdvenMp(0);
				pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // パーティー中
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		} else if (skillId == HASTE || skillId == GREATER_HASTE) { // ヘイスト、グレーターヘイスト
			cha.setMoveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
		} else if (skillId == HOLY_WALK || skillId == MOVING_ACCELERATION
				|| skillId == WIND_WALK || skillId == BLOODLUST) { // ホーリーウォーク、ムービングアクセレーション、ウィンドウォーク、ブラッドラスト
			cha.setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			}
		} else if (skillId == AWAKEN_ANTHARAS) { // 覚醒：アンタラス
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addResistHold(-10);
			pc.addAc(3);
		} else if (skillId == AWAKEN_FAFURION) { // 覚醒：パプリオン
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addResistFreeze(-10);
		} else if (skillId == AWAKEN_VALAKAS) { // 覚醒：ヴァラカス
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addResistStun(-10);
			pc.addHitup(-5);
		} else if (skillId == ILLUSION_LICH) { // イリュージョン：リッチ
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addSp(-2);
				pc.sendPackets(new S_SpMr(pc));
			}
		} else if (skillId == ILLUSION_DIA_GOLEM) { // イリュージョン：ダイアモンドゴーレム
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(20);
			}
		} else if (skillId == ILLUSION_AVATAR) { // イリュージョン：アバター
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-10);
				pc.addBowDmgup(-10);
			}
		} else if (skillId == INSIGHT) { // インサイト
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addStr(-1);
				pc.addCon(-1);
				pc.addDex(-1);
				pc.addWis(-1);
				pc.addInt(-1);
			}
		} else if (skillId == PANIC) { // パニック
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addStr(1);
				pc.addCon(1);
				pc.addDex(1);
				pc.addWis(1);
				pc.addInt(1);
			}
		} else if (skillId == MIRROR_IMAGE || skillId == UNCANNY_DODGE) { // 鏡像、暗影閃避
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDodge((byte) -5); // 回避率 - 50%
				pc.sendPackets(new S_PacketBox(S_PacketBox.DODGE_RATE_PLUS, pc.getDodge()));
			}
		} else if (skillId == RESIST_FEAR) { // 恐懼無助
			cha.addNdodge((byte) -5); // 回避率 + 50%
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(S_PacketBox.DODGE_RATE_MINUS, pc.getNdodge()));
			}
		}

		// ****** 状態変化が解けた場合
		else if (skillId == CURSE_BLIND || skillId == DARKNESS) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_CurseBlind(0));
			}
		} else if (skillId == CURSE_PARALYZE) { // カーズ パラライズ
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PARALYSIS,
						false));
			}
		} else if (skillId == ICE_LANCE // アイスランス
				|| skillId == FREEZING_BLIZZARD) { // フリージングブリザード
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
		} else if (skillId == EARTH_BIND) { // アースバインド
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
		} else if (skillId == SHOCK_STUN || skillId == MASS_SHOCK_STUN) { // ショック
			// スタン
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
		} else if (skillId == FOG_OF_SLEEPING) { // フォグ オブ スリーピング
			cha.setSleeped(false);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, false));
				pc.sendPackets(new S_OwnCharStatus(pc));
			}
		} else if (skillId == SLOW || skillId == ENTANGLE
				|| skillId == MASS_SLOW) { // スロー、エンタングル、マススロー
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
		} else if (skillId == STATUS_FREEZE) { // Freeze
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
		} else if (skillId == STATUS_CUBE_IGNITION_TO_ALLY) { // キューブ[イグニション]：味方
			cha.addFire(-30);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		} else if (skillId == STATUS_CUBE_QUAKE_TO_ALLY) { // キューブ[クエイク]：味方
			cha.addEarth(-30);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		} else if (skillId == STATUS_CUBE_SHOCK_TO_ALLY) { // キューブ[ショック]：味方
			cha.addWind(-30);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		} else if (skillId == STATUS_CUBE_IGNITION_TO_ENEMY) { // キューブ[イグニション]：敵
		} else if (skillId == STATUS_CUBE_QUAKE_TO_ENEMY) { // キューブ[クエイク]：敵
		} else if (skillId == STATUS_CUBE_SHOCK_TO_ENEMY) { // キューブ[ショック]：敵
		} else if (skillId == STATUS_MR_REDUCTION_BY_CUBE_SHOCK) { // キューブ[ショック]によるMR減少
			// cha.addMr(10);
			// if (cha instanceof L1PcInstance) {
			// L1PcInstance pc = (L1PcInstance) cha;
			// pc.sendPackets(new S_SpMr(pc));
			// }
		} else if (skillId == STATUS_CUBE_BALANCE) { // キューブ[バランス]
		} else if (skillId == BONE_BREAK) { // ボーンブレイク
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
		}

		// ****** アイテム関係
		else if (skillId == STATUS_BRAVE || skillId == STATUS_ELFBRAVE
				|| skillId == STATUS_BRAVE2) { // ブレイブポーション等
			// TODO STATUS BRAVE2 ペットレース用
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			}
			cha.setBraveSpeed(0);
		} else if (skillId == STATUS_RIBRAVE) { // ユグドラの実
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				// XXX ユグドラの実のアイコンを消す方法が不明
			}
			cha.setBraveSpeed(0);
		} else if (skillId == STATUS_HASTE) { // グリーン ポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
		} else if (skillId == STATUS_THIRD_SPEED) { // 三段加速
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Liquor(pc.getId(), 0));
				pc.broadcastPacket(new S_Liquor(pc.getId(), 0));
			}
		} else if (skillId == STATUS_BLUE_POTION) { // ブルー ポーション
		} else if (skillId == STATUS_UNDERWATER_BREATH) { // エヴァの祝福＆マーメイドの鱗
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
			}
		} else if (skillId == STATUS_WISDOM_POTION) { // ウィズダム ポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addSp(-2);
				pc.addMpr(-2);
				pc.sendPackets(new S_SkillIconWisdomPotion(0));
			}
		} else if (skillId == STATUS_CHAT_PROHIBITED) { // チャット禁止
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_ServerMessage(288)); // チャットができるようになりました。
			}
		} else if (skillId == MAGIC_EYE_OF_ANTHARAS) { // 地竜の魔眼
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addResistHold(-5);
				pc.addDodge((byte) -1); // 回避率 - 10%
				pc.sendPackets(new S_PacketBox(S_PacketBox.DODGE_RATE_PLUS, pc.getDodge()));
			}
		} else if (skillId == MAGIC_EYE_OF_FAFURION) { // 水竜の魔眼
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addResistFreeze(-5);
			}
		} else if (skillId == MAGIC_EYE_OF_LINDVIOR) { // 風竜の魔眼
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addResistSleep(-5);
			}
		} else if (skillId == MAGIC_EYE_OF_VALAKAS) { // 火竜の魔眼
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addResistStun(-5);
			}
		} else if (skillId == MAGIC_EYE_OF_BIRTH) { // 誕生の魔眼
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addResistHold(-5);
				pc.addResistFreeze(-5);
				pc.addDodge((byte) -1); // 回避率 - 10%
				pc.sendPackets(new S_PacketBox(S_PacketBox.DODGE_RATE_PLUS, pc.getDodge()));
			}
		} else if (skillId == MAGIC_EYE_OF_SHAPE) { // 形象の魔眼
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addResistHold(-5);
				pc.addResistFreeze(-5);
				pc.addResistSleep(-5);
				pc.addDodge((byte) -1); // 回避率 - 10%
				pc.sendPackets(new S_PacketBox(S_PacketBox.DODGE_RATE_PLUS, pc.getDodge()));
			}
		} else if (skillId == MAGIC_EYE_OF_LIFE) { // 生命の魔眼
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addResistHold(-5);
				pc.addResistFreeze(-5);
				pc.addResistSleep(-5);
				pc.addResistStun(-5);
				pc.addDodge((byte) -1); // 回避率 - 10%
				pc.sendPackets(new S_PacketBox(S_PacketBox.DODGE_RATE_PLUS, pc.getDodge()));
			}
		} else if (skillId == STATUS_FLORA_POTION_STR) { // フローラ系ポーション：STR
			L1FloraPotion potion = L1FloraPotion.get(40922);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addStr((byte) -potion.getEffect(pc).getStr());
				pc.sendPackets(new S_Strup(pc, 6, 0));
			}
		} else if (skillId == STATUS_FLORA_POTION_DEX) { // フローラ系ポーション：DEX
			L1FloraPotion potion = L1FloraPotion.get(40923);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDex((byte) -potion.getEffect(pc).getDex());
				pc.sendPackets(new S_Dexup(pc, 6, 0));
			}
		} else if (skillId == STONE_OF_DRAGON) { // ドラゴンの石
			L1FloraPotion potion = L1FloraPotion.get(50555);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-potion.getEffect(pc).getHit());
				pc.addDmgup(-potion.getEffect(pc).getDmg());
				pc.addBowHitup(-potion.getEffect(pc).getBowHit());
				pc.addBowDmgup(-potion.getEffect(pc).getBowDmg());
				pc.addSp(-potion.getEffect(pc).getSp());
				pc.sendPackets(new S_SpMr(pc));
				//pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				//pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			}
		}else if (skillId == STATUS_EXP_UP) { // 祈りのポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50616);
				pc.addExpBonusPct(-extra.getEffect().getExp());
			}
		} else if (skillId == STATUS_EXP_UP_II) { // 祈りのポーションII
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50617);
				pc.addExpBonusPct(-extra.getEffect().getExp());
			}
		} else if (skillId == POTION_OF_SWORDMAN) {  // 剣士のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50618);
				pc.addMaxHp(-extra.getEffect().getHp());
				pc.addHpr(-extra.getEffect().getHpr());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_MAGICIAN) {  // 術士のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50619);
				pc.addMaxMp(-extra.getEffect().getMp());
				pc.addMpr(-extra.getEffect().getMpr());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_RECOVERY) {  // 治癒のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50620);
				pc.addHpr(-extra.getEffect().getHpr());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_MEDITATION) {  // 瞑想のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50621);
				pc.addMpr(-extra.getEffect().getMpr());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_LIFE) {  // 生命のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50622);
				pc.addMaxHp(-extra.getEffect().getHp());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_MAGIC) {  // 魔法のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50623);
				pc.addMaxMp(-extra.getEffect().getMp());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_MAGIC_RESIST) {  // 魔法抵抗のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50624);
				pc.addMr(-10);
				pc.sendPackets(new S_SpMr(pc));
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_STR) {  // 腕力のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50625);
				pc.addStr(-extra.getEffect().getStr());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_DEX) {  // 機敏のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50626);
				pc.addDex(-extra.getEffect().getDex());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_CON) {  // 体力のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50627);
				pc.addCon(-extra.getEffect().getCon());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_INT) {  // 知力のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50628);
				pc.addInt(-extra.getEffect().getInt());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_WIS) {  // 精神のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50629);
				pc.addWis(-extra.getEffect().getWis());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_RAGE) {  // 憤怒のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50630);
				pc.addHitup(-extra.getEffect().getHit());
				pc.addDmgup(-extra.getEffect().getDmg());
				pc.addBowHitup(-extra.getEffect().getBowHit());
				pc.addBowDmgup(-extra.getEffect().getBowDmg());
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		} else if (skillId == POTION_OF_CONCENTRATION) {  // 集中のポーション
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ExtraPotion extra = L1ExtraPotion.get(50631);
				pc.addSp(-extra.getEffect().getSp());
				pc.sendPackets(new S_SpMr(pc));
				pc.sendPackets(new S_ServerMessage(830)); // 体に感じた力が消えていきます。
			}
		}

		// ****** レイド関係
		else if (skillId == BLOODSTAIN_OF_ANTHARAS) { // アンタラスの血痕
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(2);
				pc.addWater(-50);
				pc.sendPackets(new S_SkillIconBloodstain(82, 0));
			}
		}
		else if (skillId == BLOODSTAIN_OF_FAFURION) { // パプリオンの血痕
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHpr(-3);
				pc.addMpr(-1);
				pc.addWind(-50);
				pc.sendPackets(new S_SkillIconBloodstain(85, 0));
			}
		}
		else if (skillId == BLOODSTAIN_OF_LINDVIOR) { // リンドビオルの血痕
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addSp(-1);
				// 魔法クリティカル+1%は、L1Magicで対応
				pc.addFire(-50);
				pc.sendPackets(new S_SkillIconBloodstain(88, 0));
			}
		}
		//else if (skillId == BLOODSTAIN_OF_VALAKAS) { // ヴァラカスの血痕(未実装)
		//	if (cha instanceof L1PcInstance) {
		//		L1PcInstance pc = (L1PcInstance) cha;
		//		pc.addDmgup(-1);
		//		pc.addHitup(-1);
		//		pc.addBowDmgup(-1);
		//		pc.addBowHitup(-1);
		//		pc.addEarth(-50);
		//		pc.sendPackets(new S_SkillIconBloodstain(91, 0));
		//	}
		//}
		else if (skillId == BLESS_OF_CRAY) { // クレイの祝福
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-100);
				pc.addMaxMp(-50);
				pc.addHpr(-3);
				pc.addMpr(-3);
				pc.addDmgup(-1);
				pc.addHitup(-5);
				pc.addBowHitup(-5);
				pc.addWeightReduction(-40);
				pc.addEarth(-30);
				pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		else if (skillId == BLESS_OF_SAEL) { // サエルの祝福
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-100);
				pc.addMaxMp(-50);
				pc.addHpr(-3);
				pc.addMpr(-3);
				pc.addDmgup(-1);
				pc.addHitup(-5);
				pc.addBowHitup(-5);
				pc.addWeightReduction(-40);
				pc.addWater(-30);
				pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		else if (skillId == BLESS_OF_GUNTER) { // グンターの助言
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-100);
				pc.addMaxMp(-50);
				pc.addHpr(-3);
				pc.addMpr(-3);
				pc.addDmgup(-1);
				pc.addHitup(-5);
				pc.addBowHitup(-5);
				pc.addWeightReduction(-40);
				pc.addWind(-30);
				pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		else if (skillId == BLESS_OF_COMA1) { // コマの祝福Ａ
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addStr(-5);
				pc.addDex(-5);
				pc.addCon(-1);
				pc.addHitup(-3);
				pc.addBowHitup(-3);
				pc.addAc(3);
			}
		}
		else if (skillId == BLESS_OF_COMA2) { // コマの祝福Ｂ
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addStr(-5);
				pc.addDex(-5);
				pc.addCon(-3);
				pc.addHitup(-5);
				pc.addBowHitup(-5);
				pc.addAc(8);
				pc.addSp(-1);
				pc.addExpBonusPct(-20);
				pc.sendPackets(new S_SpMr(pc));
			}
		}
		else if (skillId == BLESS_OF_SAMURAI) { // 武士の心得
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addExpBonusPct(-10);
			}
		}
		// ****** 毒関係
		else if (skillId == STATUS_POISON) { // ダメージ毒
			cha.curePoison();
		}

		sendPacket(cha, skillId);
	}

	// メッセージの表示（終了するとき）
	private static void sendStopMessage(L1PcInstance charaPc, int skillid) {
		L1Skill l1skills = SkillTable.getInstance().findBySkillId(skillid);
		if (l1skills == null || charaPc == null) {
			return;
		}

		int msgID = l1skills.getSysmsgIdStop();
		if (msgID > 0) {
			charaPc.sendPackets(new S_ServerMessage(msgID));
		}
	}
}

class L1SkillTimerThreadImpl extends Thread implements L1SkillTimer {
	public L1SkillTimerThreadImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;
	}

	@Override
	public void run() {
		for (int timeCount = _timeMillis / 1000; timeCount > 0; timeCount--) {
			try {
				Thread.sleep(1000);
				_remainingTime = timeCount;
			} catch (InterruptedException e) {
				return;
			}
		}
		_cha.removeSkillEffect(_skillId);
	}

	public int getRemainingTime() {
		return _remainingTime;
	}

	public void begin() {
		GeneralThreadPool.getInstance().execute(this);
	}

	public void end() {
		super.interrupt();
		L1SkillStop.stopSkill(_cha, _skillId);
	}

	public void kill() {
		if (Thread.currentThread().getId() == super.getId()) {
			return; // 呼び出し元スレッドが自分であれば止めない
		}
		super.interrupt();
	}

	private final L1Character _cha;
	private final int _timeMillis;
	private final int _skillId;
	private int _remainingTime;
}

class L1SkillTimerTimerImpl implements L1SkillTimer, Runnable {
	private static Logger _log = Logger.getLogger(L1SkillTimerTimerImpl.class
			.getName());
	private ScheduledFuture<?> _future = null;

	public L1SkillTimerTimerImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;

		_remainingTime = _timeMillis / 1000;
	}

	@Override
	public void run() {
		_remainingTime--;
		if (_remainingTime <= 0) {
			_cha.removeSkillEffect(_skillId);
		}
	}

	@Override
	public void begin() {
		_future = GeneralThreadPool.getInstance().scheduleAtFixedRate(this,
				1000, 1000);
	}

	@Override
	public void end() {
		kill();
		try {
			L1SkillStop.stopSkill(_cha, _skillId);
		} catch (Throwable e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void kill() {
		if (_future != null) {
			_future.cancel(false);
		}
	}

	@Override
	public int getRemainingTime() {
		return _remainingTime;
	}

	private final L1Character _cha;
	private final int _timeMillis;
	private final int _skillId;
	private int _remainingTime;
}