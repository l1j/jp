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

package jp.l1j.server.storage;

import jp.l1j.server.model.instance.L1PcInstance;

public interface CharacterStorage {
	public void createCharacter(L1PcInstance pc) throws Exception;

	public void deleteCharacter(int accountId, String charName)
			throws Exception;

	public void storeCharacter(L1PcInstance pc) throws Exception;

	public L1PcInstance loadCharacter(String charName) throws Exception;
}
