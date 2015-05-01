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

package jp.l1j.server.model.npc;

public class L1NpcHtml {
	private final String _name;
	private final String _args[];

	public static final L1NpcHtml HTML_CLOSE = new L1NpcHtml("");

	public L1NpcHtml(String name) {
		this(name, new String[] {});
	}

	public L1NpcHtml(String name, String... args) {
		if (name == null || args == null) {
			throw new NullPointerException();
		}
		_name = name;
		_args = args;
	}

	public String getName() {
		return _name;
	}

	public String[] getArgs() {
		return _args;
	}
}
