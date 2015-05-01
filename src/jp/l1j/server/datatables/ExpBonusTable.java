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

package jp.l1j.server.datatables;

import jp.l1j.configure.Config;

public final class ExpBonusTable {
	private ExpBonusTable() {
	}

	private static final int _expBonus[] = {
			Config.LV1_EXP_BONUS, Config.LV2_EXP_BONUS, Config.LV3_EXP_BONUS,
			Config.LV4_EXP_BONUS, Config.LV5_EXP_BONUS, Config.LV6_EXP_BONUS,
			Config.LV7_EXP_BONUS, Config.LV8_EXP_BONUS, Config.LV9_EXP_BONUS,
			Config.LV10_EXP_BONUS, Config.LV11_EXP_BONUS, Config.LV12_EXP_BONUS,
			Config.LV13_EXP_BONUS, Config.LV14_EXP_BONUS, Config.LV15_EXP_BONUS,
			Config.LV16_EXP_BONUS, Config.LV17_EXP_BONUS, Config.LV18_EXP_BONUS,
			Config.LV19_EXP_BONUS, Config.LV20_EXP_BONUS, Config.LV21_EXP_BONUS,
			Config.LV22_EXP_BONUS, Config.LV23_EXP_BONUS, Config.LV24_EXP_BONUS,
			Config.LV25_EXP_BONUS, Config.LV26_EXP_BONUS, Config.LV27_EXP_BONUS,
			Config.LV28_EXP_BONUS, Config.LV29_EXP_BONUS, Config.LV30_EXP_BONUS,
			Config.LV31_EXP_BONUS, Config.LV32_EXP_BONUS, Config.LV33_EXP_BONUS,
			Config.LV34_EXP_BONUS, Config.LV35_EXP_BONUS, Config.LV36_EXP_BONUS,
			Config.LV37_EXP_BONUS, Config.LV38_EXP_BONUS, Config.LV39_EXP_BONUS,
			Config.LV40_EXP_BONUS, Config.LV41_EXP_BONUS, Config.LV42_EXP_BONUS,
			Config.LV43_EXP_BONUS, Config.LV44_EXP_BONUS, Config.LV45_EXP_BONUS,
			Config.LV46_EXP_BONUS, Config.LV47_EXP_BONUS, Config.LV48_EXP_BONUS,
			Config.LV49_EXP_BONUS, Config.LV50_EXP_BONUS, Config.LV51_EXP_BONUS,
			Config.LV52_EXP_BONUS, Config.LV53_EXP_BONUS, Config.LV54_EXP_BONUS,
			Config.LV55_EXP_BONUS, Config.LV56_EXP_BONUS, Config.LV57_EXP_BONUS,
			Config.LV58_EXP_BONUS, Config.LV59_EXP_BONUS, Config.LV60_EXP_BONUS,
			Config.LV61_EXP_BONUS, Config.LV62_EXP_BONUS, Config.LV63_EXP_BONUS,
			Config.LV64_EXP_BONUS, Config.LV65_EXP_BONUS, Config.LV66_EXP_BONUS,
			Config.LV67_EXP_BONUS, Config.LV68_EXP_BONUS, Config.LV69_EXP_BONUS,
			Config.LV70_EXP_BONUS, Config.LV71_EXP_BONUS, Config.LV72_EXP_BONUS,
			Config.LV73_EXP_BONUS, Config.LV74_EXP_BONUS, Config.LV75_EXP_BONUS,
			Config.LV76_EXP_BONUS, Config.LV77_EXP_BONUS, Config.LV78_EXP_BONUS,
			Config.LV79_EXP_BONUS, Config.LV80_EXP_BONUS, Config.LV81_EXP_BONUS,
			Config.LV82_EXP_BONUS, Config.LV83_EXP_BONUS, Config.LV84_EXP_BONUS,
			Config.LV85_EXP_BONUS, Config.LV86_EXP_BONUS, Config.LV87_EXP_BONUS,
			Config.LV88_EXP_BONUS, Config.LV89_EXP_BONUS, Config.LV90_EXP_BONUS,
			Config.LV91_EXP_BONUS, Config.LV92_EXP_BONUS, Config.LV93_EXP_BONUS,
			Config.LV94_EXP_BONUS, Config.LV95_EXP_BONUS, Config.LV96_EXP_BONUS,
			Config.LV97_EXP_BONUS, Config.LV98_EXP_BONUS, Config.LV99_EXP_BONUS};
	
	public static int getExpBonusRate(int level) {
		int expBonus = 0;
		if (level >= 1 && level <= 99) {
			expBonus = _expBonus[level - 1];
		}
		return expBonus;
	}
}
