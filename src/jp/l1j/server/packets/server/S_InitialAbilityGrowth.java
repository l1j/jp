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
import jp.l1j.server.model.instance.L1PcInstance;

/**
 * @category 初期ステータスボーナス表示
 */
public class S_InitialAbilityGrowth extends ServerBasePacket {
	public S_InitialAbilityGrowth(L1PcInstance pc) {

		int Str = pc.getOriginalStr();// 腕力
		int Dex = pc.getOriginalDex();// 機敏
		int Con = pc.getOriginalCon();// 体力
		int Wis = pc.getOriginalWis();// 精神
		int Cha = pc.getOriginalCha();// 魅力
		int Int = pc.getOriginalInt();// 知力
		int[] growth = new int[6];

		// 君主
		if (pc.isCrown()) {
			int Initial[] = { 13, 10, 10, 11, 13, 10 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// ウィザード
		if (pc.isWizard()) {
			int[] Initial = { 8, 7, 12, 12, 8, 12 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// ナイト
		if (pc.isKnight()) {
			int[] Initial = { 16, 12, 14, 9, 12, 8 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// エルフ
		if (pc.isElf()) {
			int[] Initial = { 11, 12, 12, 12, 9, 12 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// ダークエルフ
		if (pc.isDarkelf()) {
			int[] Initial = { 12, 15, 8, 10, 9, 11 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// ドラゴンナイト
		if (pc.isDragonKnight()) {
			int[] Initial = { 13, 11, 14, 12, 8, 11 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}
		// 幻術師
		if (pc.isIllusionist()) {
			int[] Initial = { 11, 10, 12, 12, 8, 12 };
			growth[0] = Str - Initial[0];
			growth[1] = Dex - Initial[1];
			growth[2] = Con - Initial[2];
			growth[3] = Wis - Initial[3];
			growth[4] = Cha - Initial[4];
			growth[5] = Int - Initial[5];
		}

		buildPacket(pc, growth[0], growth[1], growth[2], growth[3], growth[4],
				growth[5]);
	}

	/**
	 * 
	 * @param pc
	 *
	 * @param Str
	 *            腕力
	 * @param Dex
	 *            機敏
	 * @param Con
	 *            体力
	 * @param Wis
	 *            精神
	 * @param Cha
	 *            魅力
	 * @param Int
	 *            知力
	 */
	private void buildPacket(L1PcInstance pc, int Str, int Dex, int Con,
			int Wis, int Cha, int Int) {
		int write1 = (Int * 16) + Str;
		int write2 = (Dex * 16) + Wis;
		int write3 = (Cha * 16) + Con;
		writeC(Opcodes.S_OPCODE_CHARRESET);
		writeC(0x04);
		writeC(write1);// 知力&腕力
		writeC(write2);// 機敏&精神
		writeC(write3);// 魅力&体力
		writeC(0x00);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}