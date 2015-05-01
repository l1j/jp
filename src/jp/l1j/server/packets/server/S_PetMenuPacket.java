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

import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;

public class S_PetMenuPacket extends ServerBasePacket {

	private byte[] _byte = null;

	public S_PetMenuPacket(L1NpcInstance npc, int exppercet) {
		buildpacket(npc, exppercet);
	}

	private void buildpacket(L1NpcInstance npc, int exppercet) {
		writeC(Opcodes.S_OPCODE_SHOWHTML);

		if (npc instanceof L1PetInstance) { // ペット
			L1PetInstance pet = (L1PetInstance) npc;
			writeD(pet.getId());
			writeS("anicom");
			writeC(0x00);
			writeH(10);
			switch (pet.getCurrentPetStatus()) {
			case 1:
				writeS("$469"); // 攻撃態勢
				break;
			case 2:
				writeS("$470"); // 防御態勢
				break;
			case 3:
				writeS("$471"); // 休憩
				break;
			case 5:
				writeS("$472"); // 警戒
				break;
			default:
				writeS("$471"); // 休憩
				break;
			}
			writeS(Integer.toString(pet.getCurrentHp())); // 現在のＨＰ
			writeS(Integer.toString(pet.getMaxHp())); // 最大ＨＰ
			writeS(Integer.toString(pet.getCurrentMp())); // 現在のＭＰ
			writeS(Integer.toString(pet.getMaxMp())); // 最大ＭＰ
			writeS(Integer.toString(pet.getLevel())); // レベル

			// 名前の文字数が8を超えると落ちる
			// なぜか"セント バーナード","ブレイブ ラビット"はOK
			// String pet_name = pet.getName();
			// if (pet_name.equalsIgnoreCase("ハイ ドーベルマン")) {
			// pet_name = "ハイ ドーベルマ";
			// }
			// else if (pet_name.equalsIgnoreCase("ハイ セントバーナード")) {
			// pet_name = "ハイ セントバー";
			// }
			// writeS(pet_name);
			//writeS(""); // ペットの名前を表示させると不安定になるので、非表示にする
			writeS(pet.getName());

			String s = "$610";
			if (pet.getFood() > 80) {
				s = "$612"; // お腹いっぱい
			} else if (pet.getFood() > 60) {
				s = "$611"; // ややお腹いっぱい
			} else if (pet.getFood() > 30) {
				s = "$610"; // 普通。
			} else if (pet.getFood() > 10) {
				s = "$609"; // 空腹
			} else if (pet.getFood() >= 0) {
				s = "$608"; // とても空腹
			}
			writeS(s); // 空腹度
			writeS(Integer.toString(exppercet)); // 経験値
			writeS(Integer.toString(pet.getLawful())); // アライメント
		} else if (npc instanceof L1SummonInstance) { // サモンモンスター
			L1SummonInstance summon = (L1SummonInstance) npc;
			writeD(summon.getId());
			writeS("moncom");
			writeC(0x00);
			writeH(6); // 渡す引数文字の数の模様
			switch (summon.getCurrentPetStatus()) {
			case 1:
				writeS("$469"); // 攻撃態勢
				break;
			case 2:
				writeS("$470"); // 防御態勢
				break;
			case 3:
				writeS("$471"); // 休憩
				break;
			case 5:
				writeS("$472"); // 警戒
				break;
			default:
				writeS("$471"); // 休憩
				break;
			}
			writeS(Integer.toString(summon.getCurrentHp())); // 現在のＨＰ
			writeS(Integer.toString(summon.getMaxHp())); // 最大ＨＰ
			writeS(Integer.toString(summon.getCurrentMp())); // 現在のＭＰ
			writeS(Integer.toString(summon.getMaxMp())); // 最大ＭＰ
			writeS(Integer.toString(summon.getLevel())); // レベル
			// writeS(summon.getNpcTemplate().getNameId());
			// writeS(Integer.toString(0));
			// writeS(Integer.toString(790));
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}

		return _byte;
	}
}