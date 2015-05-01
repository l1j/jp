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

package jp.l1j.server.packets.server;

import java.util.Map;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1PcInstance;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.utils.ByteArrayUtil;
import jp.l1j.server.utils.IntRange;
import jp.l1j.server.utils.collections.Maps;

// activeSpells(L1PcInstance pc)
// dataの各値に100を入れてみてアイコンが出るかどうかテストした
// ほとんどのスキルは、値の4倍の秒数が残り時間として表示される
// 一部のスキルは16倍の秒数が表示されるため、そのスキルについては係数16と記載した
// 0:メディテーション
// 1:None
// 2:None
// 3:ディクリースウェイト(係数16)
// 4:ディケイポーション
// 5:アブソリュートバリア
// 6:サイレンス
// 7:ベノムレジスト
// 8:ウィークネス
// 9:ディジーズ
// 10-16:None
// 17:ドレス イベイジョン
// 18:バーサーカー
// 19:ネイチャーズ タッチ
// 20:ウィンドシャックル
// 21:イレースマジック?
// 22:カウンターミラー?
//　23:アディショナル ファイアー(係数16)
//　24:エレメンタルフォールダウン
//　25:None
//　26:エレメンタルファイアー
//　27:None
//　28:気配を消しモンスターたちに気付かれないようにします(?)
//　29:None
//　30:ストライカー ゲイル
//　31:ソウル オブ フレイム
//　32:ポルートウォーター
//　33-40:None
//　44:スペルパワーが上昇します
//　45-51:None
//　52:コンセントレーション(係数16)
//　53:インサイト(係数16)
//　54:パニック(係数16)
//　55:モータルボディ
//　56:ホラー オブ デス
//　57:フィアー
//　58:ペイシェンス
//　59:ガード ブレイク
//　60:ドラゴン スキン(係数16)
//　61:移動速度が速くなります
//　62-101:None
//　102-103:?

public class S_ActiveSpells extends ServerBasePacket {
	private byte[] _byte = null;
	
	private static final Map<Integer, ActiveSkill> _indexes = Maps.newHashMap();
	
	private static Logger _log = Logger.getLogger(S_ActiveSpells.class.getName());

	private static class ActiveSkill {
		public final int id;
		public final int timeCoefficient;
		public ActiveSkill(int skillId, int timeCoefficient) {
			this.id = skillId;
			this.timeCoefficient = timeCoefficient;
		}
	}

	private static void addIndex(int skillIndex, int skillId) {
		addIndex(skillIndex, skillId, 4);
	}

	private static void addIndex(int skillIndex, int skillId, int timeCoefficient) {
		ActiveSkill skill = new ActiveSkill(skillId, timeCoefficient);
		_indexes.put(skillIndex, skill);
	}

	static {
		// スキルインデックス定義
		addIndex(0, MEDITATION);
		addIndex(3, DECREASE_WEIGHT, 16);
		addIndex(4, DECAY_POTION);
		addIndex(5, ABSOLUTE_BARRIER);
		addIndex(6, SILENCE);
		addIndex(7, VENOM_RESIST);
		addIndex(8, WEAKNESS);
		addIndex(9, DISEASE);
		addIndex(17, DRESS_EVASION);
		addIndex(18, BERSERKERS);
		addIndex(19, NATURES_TOUCH);
		// addIndex(20, WIND_SHACKLE); // ここで送っても攻撃速度は下がらない
		addIndex(21, ERASE_MAGIC);
		addIndex(22, COUNTER_MIRROR);
		addIndex(23, ADDITIONAL_FIRE, 16);
		addIndex(24, ELEMENTAL_FALL_DOWN);
		addIndex(26, ELEMENTAL_FIRE);
		// addIndex(28, /* 気配を消しモンスターたちに気付かれないようにします(?) */);
		addIndex(30, STRIKER_GALE);
		addIndex(31, SOUL_OF_FLAME);
		addIndex(32, POLLUTE_WATER);
		// addIndex(44, /* スペルパワーが上昇します */);
		addIndex(52, CONCENTRATION, 16);
		addIndex(53, INSIGHT, 16);
		addIndex(54, PANIC, 16); // TODO implement
		addIndex(55, MORTAL_BODY);
		addIndex(56, HORROR_OF_DEATH);
		// addIndex(57, /*フィアー*/);
		addIndex(58, PATIENCE);
		addIndex(59, GUARD_BRAKE);
		addIndex(60, DRAGON_SKIN, 16);
		// addIndex(61, /*移動速度が速くなります*/);
	}

	public S_ActiveSpells(L1PcInstance pc) {
		buildPacket(pc);
	}

	private String makeOverflowMessage(L1PcInstance pc, ActiveSkill skill) {
		StringBuilder result = new StringBuilder();
		result.append("スキルの効果時間が送信可能な範囲を超えています。\n");
		result.append("Hint: 効果時間を独自に設定している場合は、限界効果時間以下に設定してください。\n");
		String detail = String.format("プレイヤー名: %s, スキルID: %d\n", pc.getName(), skill.id);
		int time = pc.getSkillEffectTimeSec(skill.id);
		String detail2 = String.format("現在の効果時間: %d 秒, 限界効果時間: %d 秒以下", time,
				255 * skill.timeCoefficient);
		result.append(detail);
		result.append(detail2);
		return result.toString();
	}

	private byte getTime(L1PcInstance pc, int skillIndex) {
		ActiveSkill skill = _indexes.get(skillIndex);
		if (skill == null) {
			return 0;
		}
		int time = pc.getSkillEffectTimeSec(skill.id);
		if (time == -1) {
			return 0;
		}
		time /= skill.timeCoefficient;
		if (!IntRange.includes(time, 0, 255)) {
			// オーバーフローした。
			_log.warning(makeOverflowMessage(pc, skill));
			time = 255;
		}
		return (byte) time;
	}

	private byte[] activeSpells(L1PcInstance pc) {
		final int SIZE_OF_SPELLS = 104;
		byte[] data = new byte[SIZE_OF_SPELLS];
		for (int i = 0; i < SIZE_OF_SPELLS; i++) {
			data[i] = getTime(pc, i);
		}
		// 用途不明
		int unixTime = (int) (System.currentTimeMillis() / 1000);
		ByteArrayUtil.writeInteger(unixTime, data, 72);
		return data;
	}

	private void buildPacket(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_ACTIVESPELLS);
		writeC(0x14);
		writeBytes(activeSpells(pc));
		_byte = _bao.toByteArray();
	}

	@Override
	public byte[] getContent() {
		return _byte;
	}
}