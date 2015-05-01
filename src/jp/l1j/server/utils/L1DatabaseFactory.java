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
package jp.l1j.server.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;

/**
 * DBへのアクセスするための各種インターフェースを提供する.
 */
public class L1DatabaseFactory {
	/** インスタンス. */
	private static L1DatabaseFactory _instance;

	/** DB接続情報をまとめたもの？. */
	private ComboPooledDataSource _source;

	/** メッセージログ用. */
	private static Logger _log = Logger.getLogger(L1DatabaseFactory.class
			.getName());

	/* DBへのアクセスに必要な各情報 */
	/** DB接続ドライバー. */
	private static String _driver;

	/** DBサーバのURL. */
	private static String _url;

	/** DBサーバに接続するユーザ名. */
	private static String _user;

	/** DBサーバに接続するパスワード. */
	private static String _password;

	/**
	 * DBへのアクセスに必要な各情報の保存.
	 * 
	 * @param driver
	 *            DB接続ドライバー
	 * @param url
	 *            DBサーバのURL
	 * @param user
	 *            DBサーバに接続するユーザ名
	 * @param password
	 *            DBサーバに接続するパスワード
	 */
	public static void setDatabaseSettings(final String driver,
			final String url, final String user, final String password) {
		_driver = driver;
		_url = url;
		_user = user;
		_password = password;
	}

	/**
	 * DB接続の情報の設定とテスト接続をする.
	 * 
	 * @throws SQLException
	 */
	public L1DatabaseFactory() throws SQLException {
		try {
			// DatabaseFactoryをL2Jから一部を除いて拝借
			_source = new ComboPooledDataSource();
			_source.setDriverClass(_driver);
			_source.setJdbcUrl(_url);
			_source.setUser(_user);
			_source.setPassword(_password);

			/* Test the connection */
			_source.getConnection().close();
		} catch (SQLException x) {
			_log.fine("Database Connection FAILED");
			// rethrow the exception
			throw x;
		} catch (Exception e) {
			_log.fine("Database Connection FAILED");
			throw new SQLException("could not init DB connection:" + e);
		}
	}

	/**
	 * サーバシャットダウン時にDBコネクションを切断する.
	 */
	public void shutdown() {
		try {
			_source.close();
		} catch (Exception e) {
			_log.log(Level.INFO, "", e);
		}
		try {
			_source = null;
		} catch (Exception e) {
			_log.log(Level.INFO, "", e);
		}
	}

	/**
	 * インスタンスを返す（nullなら作成する).
	 * 
	 * @return L1DatabaseFactory
	 * @throws SQLException
	 */
	public static L1DatabaseFactory getInstance() throws SQLException {
		if (_instance == null) {
			_instance = new L1DatabaseFactory();
		}
		return _instance;
	}

	/**
	 * DB接続をし、コネクションオブジェクトを返す.
	 * 
	 * @return Connection コネクションオブジェクト
	 * @throws SQLException
	 */
	public Connection getConnection() {
		Connection con = null;

		while (con == null) {
			try {
				con = _source.getConnection();
			} catch (SQLException e) {
				_log
						.warning("L1DatabaseFactory: getConnection() failed, trying again "
								+ e);
			}
		}
		return Config.DEBUG_MODE ? LeakCheckedConnection.create(con) : con;
	}
}
