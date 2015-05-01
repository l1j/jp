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

public class L1Town {
	public L1Town() {
	}

	private int _townid;

	public int getTownid() {
		return _townid;
	}

	public void setTownId(int i) {
		_townid = i;
	}

	private String _name;

	public String getName() {
		return _name;
	}

	public void setName(String s) {
		_name = s;
	}

	private int _leaderId;

	public int getLeaderId() {
		return _leaderId;
	}

	public void setLeaderId(int i) {
		_leaderId = i;
	}

	private String _leaderName;

	public String getLeaderName() {
		return _leaderName;
	}

	public void setLeaderName(String s) {
		_leaderName = s;
	}

	private int _taxRate;

	public int getTaxRate() {
		return _taxRate;
	}

	public void setTaxRate(int i) {
		_taxRate = i;
	}

	private int _taxRateReserved;

	public int getTaxRateReserved() {
		return _taxRateReserved;
	}

	public void setTaxRateReserved(int i) {
		_taxRateReserved = i;
	}

	private int _salesMoney;

	public int getSalesMoney() {
		return _salesMoney;
	}

	public void setSalesMoney(int i) {
		_salesMoney = i;
	}

	private int _salesMoneyYesterday;

	public int getSalesMoneyYesterday() {
		return _salesMoneyYesterday;
	}

	public void setSalesMoneyYesterday(int i) {
		_salesMoneyYesterday = i;
	}

	private int _townTax;

	public int getTownTax() {
		return _townTax;
	}

	public void setTownTax(int i) {
		_townTax = i;
	}

	private int _townFixTax;

	public int getTownFixTax() {
		return _townFixTax;
	}

	public void setTownFixTax(int i) {
		_townFixTax = i;
	}
}
