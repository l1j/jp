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

import java.util.Collections;
import java.util.List;
import jp.l1j.server.utils.collections.Lists;

public class L1MobGroup {
	private final int _id;
	private final int _leaderId;
	private final List<L1NpcCount> _minions = Lists.newArrayList();
	private final boolean _isRemoveGroupIfLeaderDie;

	public L1MobGroup(int id, int leaderId, List<L1NpcCount> minions,
			boolean isRemoveGroupIfLeaderDie) {
		_id = id;
		_leaderId = leaderId;
		_minions.addAll(minions); // 参照コピーの方が速いが、不変性が保証できない
		_isRemoveGroupIfLeaderDie = isRemoveGroupIfLeaderDie;
	}

	public int getId() {
		return _id;
	}

	public int getLeaderId() {
		return _leaderId;
	}

	public List<L1NpcCount> getMinions() {
		return Collections.unmodifiableList(_minions);
	}

	public boolean isRemoveGroupIfLeaderDie() {
		return _isRemoveGroupIfLeaderDie;
	}

}