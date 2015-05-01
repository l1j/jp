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
package jp.l1j.server.model.classes;

import jp.l1j.server.model.instance.L1PcInstance;

public abstract class L1ClassFeature {
	public static L1ClassFeature newClassFeature(int classId) {
		if (classId == L1PcInstance.CLASSID_PRINCE
				|| classId == L1PcInstance.CLASSID_PRINCESS) {
			return new L1RoyalClassFeature();
		}
		if (classId == L1PcInstance.CLASSID_ELF_MALE
				|| classId == L1PcInstance.CLASSID_ELF_FEMALE) {
			return new L1ElfClassFeature();
		}
		if (classId == L1PcInstance.CLASSID_KNIGHT_MALE
				|| classId == L1PcInstance.CLASSID_KNIGHT_FEMALE) {
			return new L1KnightClassFeature();
		}
		if (classId == L1PcInstance.CLASSID_WIZARD_MALE
				|| classId == L1PcInstance.CLASSID_WIZARD_FEMALE) {
			return new L1WizardClassFeature();
		}
		if (classId == L1PcInstance.CLASSID_DARK_ELF_MALE
				|| classId == L1PcInstance.CLASSID_DARK_ELF_FEMALE) {
			return new L1DarkElfClassFeature();
		}
		if (classId == L1PcInstance.CLASSID_DRAGON_KNIGHT_MALE
				|| classId == L1PcInstance.CLASSID_DRAGON_KNIGHT_FEMALE) {
			return new L1DragonKnightClassFeature();
		}
		if (classId == L1PcInstance.CLASSID_ILLUSIONIST_MALE
				|| classId == L1PcInstance.CLASSID_ILLUSIONIST_FEMALE) {
			return new L1IllusionistClassFeature();
		}
		throw new IllegalArgumentException();
	}

	public abstract int getAcDefenseMax(int ac);

	public abstract int getMagicLevel(int playerLevel);

	public abstract String getClassNameInitial();

	public abstract int getMaxSpellLevel();

	public abstract int getSpellLearningInterval();
}