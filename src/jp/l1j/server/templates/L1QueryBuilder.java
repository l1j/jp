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

import java.util.List;
import jp.l1j.server.utils.StringUtil;
import jp.l1j.server.utils.collections.Lists;

public class L1QueryBuilder {
	private final String _tableName;
	private final int _id;
	private List<String> _columns = Lists.newArrayList();
	private List<Object> _values = Lists.newArrayList();
	private String _query = null;

	public L1QueryBuilder(String tableName, int id) {
		_tableName = tableName;
		_id = id;
	}

	void addColumn(String columnName, Object value) {
		if (_query != null) {
			throw new IllegalStateException();
		}

		_columns.add(String.format("`%s` = ?", columnName));
		_values.add(value);
	}

	public void buildQuery() {
		_query = "UPDATE " + _tableName + " SET "
				+ StringUtil.join(_columns, ", ") + " WHERE id = ?";
		_values.add(_id);
	}

	public String getQuery() {
		if (_query == null) {
			throw new IllegalStateException();
		}
		return _query;
	}

	public Object[] getArgs() {
		if (_query == null) {
			throw new IllegalStateException();
		}
		return _values.toArray();
	}
}