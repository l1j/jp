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
package jp.l1j.server.templates;

public class L1Weapon extends L1Item {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public L1Weapon() {
	}

	private int _range = 0; // ● 射程範囲

	@Override
	public int getRange() {
		return _range;
	}

	public void setRange(int i) {
		_range = i;
	}

	private int _hitModifier = 0; // ● 命中率補正

	@Override
	public int getHitModifier() {
		return _hitModifier;
	}

	public void setHitModifier(int i) {
		_hitModifier = i;
	}

	private int _dmgModifier = 0; // ● ダメージ補正

	@Override
	public int getDmgModifier() {
		return _dmgModifier;
	}

	public void setDmgModifier(int i) {
		_dmgModifier = i;
	}

	private int _doubleDmgChance; // ● DB、クロウの発動確率

	@Override
	public int getDoubleDmgChance() {
		return _doubleDmgChance;
	}

	public void setDoubleDmgChance(int i) {
		_doubleDmgChance = i;
	}

	private int _magicDmgModifier = 0; // ● 攻撃魔法のダメージ補正

	@Override
	public int getMagicDmgModifier() {
		return _magicDmgModifier;
	}

	public void setMagicDmgModifier(int i) {
		_magicDmgModifier = i;
	}

	private boolean _canbeDmg; // ● 損傷の有無

	@Override
	public boolean getCanbeDmg() {
		return _canbeDmg;
	}

	public void setCanbeDmg(boolean i) {
		_canbeDmg = i;
	}

	private int _weaknessExposure; // ● 弱点露出発動確率

	@Override
	public int getWeaknessExposure() {
		return _weaknessExposure;
	}

	public void setWeaknessExposure(int i) {
		_weaknessExposure = i;
	}

	private boolean _isTwohanded; // true:両手、false:片手
	
	@Override
	public boolean isTwohanded() {
		return _isTwohanded;
	}

	public void setIsTwohanded(boolean isTwohanded) {
		_isTwohanded = isTwohanded;
	}

}
