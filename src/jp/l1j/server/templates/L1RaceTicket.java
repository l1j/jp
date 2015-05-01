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

package jp.l1j.server.templates;

public class L1RaceTicket {

	private int _itemObjId;
	private int _round;
	private double _allotmentPercentage;
	private int _victory;
	private int _runnerNum;
	
	public int getItemObjId() {
		return _itemObjId;
	}

	public void setItemObjId(int i) {
		_itemObjId = i;
	}

	public void setAllotmentPercentage(double allotmentPercentage) {
		_allotmentPercentage = allotmentPercentage;
	}

	public double getAllotmentPercentage() {
		return _allotmentPercentage;
	}

	public void setRound(int round) {
		_round = round;
	}

	public int getRound() {
		return _round;
	}

	public void setVictory(int victory) {
		_victory = victory;
	}

	public int getVictory() {
		return _victory;
	}

	public void setRunnerNum(int runnerNum) {
		_runnerNum = runnerNum;
	}

	public int getRunnerNum() {
		return _runnerNum;
	}

}