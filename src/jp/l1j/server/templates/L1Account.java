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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;
import jp.l1j.server.utils.Base64;
import jp.l1j.server.utils.IdFactory;
import jp.l1j.server.utils.L1QueryUtil;
import jp.l1j.server.utils.L1QueryUtil.EntityFactory;

/**
 * ログインの為の様々なインターフェースを提供する.
 */
public class L1Account {
	private static Logger _log = Logger.getLogger(L1Account.class.getName());

	// アカウントID
	private int _id;

	// アカウント名
	private String _name;

	/// パスワード(暗号化されている)
	private String _password;

	// アクセスレベル(GMか？)
	private int _accessLevel;

	// キャラクターの追加スロット数
	private int _characterSlot;

	// 最終アクティブ日
	private Timestamp _lastActivatedAt;

	// 接続先のIPアドレス
	private String _ip;

	// 接続先のホスト名
	private String _host;

	// アカウントの有効/無効(True:有効)
	private boolean _isActive;

	// パスワード認証結果の有効/無効(True:有効)
	private boolean _isValid = false;

	/**
	 * コンストラクタ.
	 */
	private L1Account() {
	}

	/**
	 * パスワードを暗号化する.
	 * 
	 * @param rawPassword
	 *            平文のパスワード
	 * @return String
	 */
	private static String encodePassword(final String rawPassword) {
		try {
			byte[] buf = rawPassword.getBytes("UTF-8");
			buf = MessageDigest.getInstance("SHA").digest(buf);
			return Base64.encodeBytes(buf);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	static L1Account create(int id, final String name,
			final String rawPassword, final String ip, final String host) {
		String password = encodePassword(rawPassword);
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		String sql = "INSERT INTO accounts SET id = ?, name = ?, password = ?, access_level = ?, character_slot = ?, last_activated_at = ?, ip = ?, host = ?, is_active = ?";
		L1QueryUtil.execute(sql, id, name, password, 0, 0, currentTime, ip, host, true);
		_log.info("created new account for " + name);
		return findById(id);
	}

	/**
	 * アカウントを新規作成する.
	 * 
	 * @param name
	 *            アカウント名
	 * @param rawPassword
	 *            平文パスワード
	 * @param ip
	 *            接続先のIPアドレス
	 * @param host
	 *            接続先のホスト名
	 * @return Account
	 */
	public static L1Account create(final String name, final String rawPassword,
			final String ip, final String host) {
		return create(IdFactory.getInstance().nextId(), name, rawPassword, ip, host);
	}

	public static List<L1Account> findAll() {
		return L1QueryUtil.selectAll(new Factory(), "SELECT * FROM accounts");
	}

	public static List<L1Account> findByCharacterLevel(int minLevel,
			int maxLevel) {
		return L1QueryUtil.selectAll(new Factory(),
						"SELECT * FROM accounts WHERE name IN (SELECT DISTINCT(account_id) FROM characters WHERE level BETWEEN ? AND ?)",
						minLevel, maxLevel);
	}

	public static L1Account findById(int id) {
		return L1QueryUtil.selectFirst(new Factory(),
				"SELECT * FROM accounts WHERE id = ?", id);
	}

	/**
	 * アカウント名からアカウント情報を検索する
	 * 
	 * @param name
	 *            アカウント名
	 * @return Account
	 */
	public static L1Account findByName(final String name) {
		return L1QueryUtil.selectFirst(new Factory(),
				"SELECT * FROM accounts WHERE name = ?", name);
	}

	/**
	 * 最終ログイン日をDBに反映する.
	 * 
	 */
	public void updateLastActivatedTime() {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		L1QueryUtil.execute(
				"UPDATE accounts SET last_activated_at = ? WHERE id = ?",
				currentTime, _id);
	}

	/**
	 * スロット数をDBに反映する.
	 * 
	 */
	public void updateCharacterSlot() {
		L1QueryUtil.execute(
				"UPDATE accounts SET character_slot = ? WHERE id = ?",
				_characterSlot, _id);
		_log.fine("update characterslot for " + _name);
	}

	/**
	 * キャラクター所有数をカウントする.
	 * 
	 * @return int
	 */
	public int countCharacters() {
		return L1QueryUtil.selectFirst(new CountFactory(),
				"SELECT count(*) as cnt FROM characters WHERE account_id = ?",
				_id);
	}

	/**
	 * アカウントを無効にする.
	 * 
	 * @param name
	 *            アカウント名
	 */
	public static void ban(final String name) {
		L1QueryUtil.execute(
				"UPDATE accounts SET is_active = 0, WHERE name = ?", name);
	}

	/**
	 * 入力されたパスワードとDB上のパスワードを照合する.
	 * 
	 * @param rawPassword
	 *            平文パスワード
	 * @return boolean
	 * @throws IllegalStateException
	 *             このオブジェクトに対しメソッドを2回以上呼び出した場合
	 */
	public boolean validatePassword(final String rawPassword) {
		if (_isValid) { // 認証成功後に再度認証された場合は例外。
			throw new IllegalStateException("Account was validated twice.");
		}
		_isValid = _password.equals(encodePassword(rawPassword));
		if (_isValid) {
			_password = null; // 認証が成功した場合、パスワードを破棄する。
		}
		return _isValid;
	}

	public int getId() {
		return _id;
	}

	/**
	 * アカウントが有効かどうかを返す(Trueで有効).
	 * 
	 * @return boolean
	 */
	public boolean isValid() {
		return _isValid;
	}

	/**
	 * アカウントがゲームマスタかどうか返す(Trueでゲームマスタ).
	 * 
	 * @return boolean
	 */
	public boolean isGameMaster() {
		return 0 < _accessLevel;
	}

	/**
	 * アカウント名を取得する.
	 * 
	 * @return String
	 */
	public String getName() {
		return _name;
	}

	/**
	 * 接続先のIPアドレスを取得する.
	 * 
	 * @return String
	 */
	public String getIp() {
		return _ip;
	}

	/**
	 * 最終ログイン日を取得する.
	 */
	public Timestamp getLastActivatedAt() {
		return _lastActivatedAt;
	}

	/**
	 * アクセスレベルを取得する.
	 * 
	 * @return int
	 */
	public int getAccessLevel() {
		return _accessLevel;
	}

	/**
	 * ホスト名を取得する.
	 * 
	 * @return String
	 */
	public String getHost() {
		return _host;
	}

	/**
	 * アクセス禁止情報を取得する.
	 * 
	 * @return boolean
	 */
	public boolean isActive() {
		return _isActive;
	}

	/**
	 * キャラクターの追加スロット数を取得する.
	 * 
	 * @return int
	 */
	public int getCharacterSlot() {
		return _characterSlot;
	}

	public void setCharacterSlot(int i) {
		_characterSlot = i;
	}

	private static class CountFactory implements EntityFactory<Integer> {
		@Override
		public Integer fromResultSet(ResultSet rs) throws SQLException {
			return rs.getInt("cnt");
		}
	}

	private static class Factory implements EntityFactory<L1Account> {
		@Override
		public L1Account fromResultSet(ResultSet rs) throws SQLException {
			L1Account result = new L1Account();
			result._id = rs.getInt("id");
			result._name = rs.getString("name");
			result._password = rs.getString("password");
			result._accessLevel = rs.getInt("access_level");
			result._characterSlot = rs.getInt("character_slot");
			result._lastActivatedAt = rs.getTimestamp("last_activated_at");
			result._ip = rs.getString("ip");
			result._host = rs.getString("host");
			result._isActive = rs.getBoolean("is_active");
			return result;
		}
	}
}
