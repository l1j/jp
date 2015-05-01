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

public class L1Pet {
	public L1Pet() {
	}

	private int _itemObjId;

	public int getItemObjId() {
		return _itemObjId;
	}

	public void setItemObjId(int i) {
		_itemObjId = i;
	}

	private int _objId;

	public int getObjId() {
		return _objId;
	}

	public void setObjId(int i) {
		_objId = i;
	}

	private int _npcid;

	public int getNpcId() {
		return _npcid;
	}

	public void setNpcId(int i) {
		_npcid = i;
	}

	private String _name;

	public String getName() {
		return _name;
	}

	public void setName(String s) {
		_name = s;
	}

	private int _level;

	public int getLevel() {
		return _level;
	}

	public void setLevel(int i) {
		_level = i;
	}

	private int _hp;

	public int getHp() {
		return _hp;
	}

	public void setHp(int i) {
		_hp = i;
	}

	private int _mp;

	public int getMp() {
		return _mp;
	}

	public void setMp(int i) {
		_mp = i;
	}

	private int _exp;

	public int getExp() {
		return _exp;
	}

	public void setExp(int i) {
		_exp = i;
	}

	private int _lawful;

	public int getLawful() {
		return _lawful;
	}

	public void setLawful(int i) {
		_lawful = i;
	}

	// 空腹度
	private int _food;

	public int getFood() {
		return _food;
	}

	public void setFood(int i) {
		_food = i;
	}
}