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

public final class DeathPenaltyTable {
	private DeathPenaltyTable() {
	}

	private static final int _deathPenalty[] = {
			Config.LV1_DEATH_PENALTY, Config.LV2_DEATH_PENALTY, Config.LV3_DEATH_PENALTY,
			Config.LV4_DEATH_PENALTY, Config.LV5_DEATH_PENALTY, Config.LV6_DEATH_PENALTY,
			Config.LV7_DEATH_PENALTY, Config.LV8_DEATH_PENALTY, Config.LV9_DEATH_PENALTY,
			Config.LV10_DEATH_PENALTY, Config.LV11_DEATH_PENALTY, Config.LV12_DEATH_PENALTY,
			Config.LV13_DEATH_PENALTY, Config.LV14_DEATH_PENALTY, Config.LV15_DEATH_PENALTY,
			Config.LV16_DEATH_PENALTY, Config.LV17_DEATH_PENALTY, Config.LV18_DEATH_PENALTY,
			Config.LV19_DEATH_PENALTY, Config.LV20_DEATH_PENALTY, Config.LV21_DEATH_PENALTY,
			Config.LV22_DEATH_PENALTY, Config.LV23_DEATH_PENALTY, Config.LV24_DEATH_PENALTY,
			Config.LV25_DEATH_PENALTY, Config.LV26_DEATH_PENALTY, Config.LV27_DEATH_PENALTY,
			Config.LV28_DEATH_PENALTY, Config.LV29_DEATH_PENALTY, Config.LV30_DEATH_PENALTY,
			Config.LV31_DEATH_PENALTY, Config.LV32_DEATH_PENALTY, Config.LV33_DEATH_PENALTY,
			Config.LV34_DEATH_PENALTY, Config.LV35_DEATH_PENALTY, Config.LV36_DEATH_PENALTY,
			Config.LV37_DEATH_PENALTY, Config.LV38_DEATH_PENALTY, Config.LV39_DEATH_PENALTY,
			Config.LV40_DEATH_PENALTY, Config.LV41_DEATH_PENALTY, Config.LV42_DEATH_PENALTY,
			Config.LV43_DEATH_PENALTY, Config.LV44_DEATH_PENALTY, Config.LV45_DEATH_PENALTY,
			Config.LV46_DEATH_PENALTY, Config.LV47_DEATH_PENALTY, Config.LV48_DEATH_PENALTY,
			Config.LV49_DEATH_PENALTY, Config.LV50_DEATH_PENALTY, Config.LV51_DEATH_PENALTY,
			Config.LV52_DEATH_PENALTY, Config.LV53_DEATH_PENALTY, Config.LV54_DEATH_PENALTY,
			Config.LV55_DEATH_PENALTY, Config.LV56_DEATH_PENALTY, Config.LV57_DEATH_PENALTY,
			Config.LV58_DEATH_PENALTY, Config.LV59_DEATH_PENALTY, Config.LV60_DEATH_PENALTY,
			Config.LV61_DEATH_PENALTY, Config.LV62_DEATH_PENALTY, Config.LV63_DEATH_PENALTY,
			Config.LV64_DEATH_PENALTY, Config.LV65_DEATH_PENALTY, Config.LV66_DEATH_PENALTY,
			Config.LV67_DEATH_PENALTY, Config.LV68_DEATH_PENALTY, Config.LV69_DEATH_PENALTY,
			Config.LV70_DEATH_PENALTY, Config.LV71_DEATH_PENALTY, Config.LV72_DEATH_PENALTY,
			Config.LV73_DEATH_PENALTY, Config.LV74_DEATH_PENALTY, Config.LV75_DEATH_PENALTY,
			Config.LV76_DEATH_PENALTY, Config.LV77_DEATH_PENALTY, Config.LV78_DEATH_PENALTY,
			Config.LV79_DEATH_PENALTY, Config.LV80_DEATH_PENALTY, Config.LV81_DEATH_PENALTY,
			Config.LV82_DEATH_PENALTY, Config.LV83_DEATH_PENALTY, Config.LV84_DEATH_PENALTY,
			Config.LV85_DEATH_PENALTY, Config.LV86_DEATH_PENALTY, Config.LV87_DEATH_PENALTY,
			Config.LV88_DEATH_PENALTY, Config.LV89_DEATH_PENALTY, Config.LV90_DEATH_PENALTY,
			Config.LV91_DEATH_PENALTY, Config.LV92_DEATH_PENALTY, Config.LV93_DEATH_PENALTY,
			Config.LV94_DEATH_PENALTY, Config.LV95_DEATH_PENALTY, Config.LV96_DEATH_PENALTY,
			Config.LV97_DEATH_PENALTY, Config.LV98_DEATH_PENALTY, Config.LV99_DEATH_PENALTY};
	
	public static int getDeathPenaltyRate(int level) {	
		int deathPenalty = 100;
		if (level >= 1 && level <= 99) {
			deathPenalty = _deathPenalty[level - 1];
		}
		return deathPenalty;
	}
}
