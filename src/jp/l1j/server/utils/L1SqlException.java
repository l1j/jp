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

package jp.l1j.server.utils;

import java.sql.SQLException;

/**
 * java.sql.SQLException例外を、非チェック例外へとラップします。
 * 
 * @see SQLException
 * @see L1QueryUtil
 */
public class L1SqlException extends RuntimeException {
	private static final long serialVersionUID = -2466780963010001937L;

	public L1SqlException(SQLException e) {
		super(e);
	}

	public L1SqlException(String message, SQLException e) {
		super(message, e);
	}
}