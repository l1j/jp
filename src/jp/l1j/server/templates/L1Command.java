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

import jp.l1j.server.utils.StringUtil;

public class L1Command {
	private static final String DEFAULT_PACKAGE = "jp.l1j.server.command.executor";
	private final String _name;
	private final int _level;
	private final String _executorClassName;

	public L1Command(String name, int level, String executorClassName) {
		_name = name;
		_level = level;
		_executorClassName = executorClassName;
	}

	public String getName() {
		return _name;
	}

	public int getLevel() {
		return _level;
	}

	public String getExecutorClassName() {
		return _executorClassName;
	}

	public String getExecutorClassFullName() {
		return StringUtil.complementClassName(_executorClassName,
				DEFAULT_PACKAGE);
	}
}
