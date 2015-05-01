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

package jp.l1j.server.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.ClientThread;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.exception.AccountAlreadyLoginException;
import jp.l1j.server.exception.GameServerFullException;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1Account;

public class LoginController {
	private static LoginController _instance;

	private static Logger _log = Logger.getLogger(LoginController.class.getName());

	private Map<String, ClientThread> _accounts = new ConcurrentHashMap<String, ClientThread>();

	private int _maxAllowedOnlinePlayers;

	private LoginController() {
	}

	public static LoginController getInstance() {
		if (_instance == null) {
			_instance = new LoginController();
		}
		return _instance;
	}

	public ClientThread[] getAllAccounts() {
		return _accounts.values().toArray(new ClientThread[_accounts.size()]);
	}

	public int getOnlinePlayerCount() {
		return _accounts.size();
	}

	public int getMaxAllowedOnlinePlayers() {
		return _maxAllowedOnlinePlayers;
	}

	public void setMaxAllowedOnlinePlayers(int maxAllowedOnlinePlayers) {
		_maxAllowedOnlinePlayers = maxAllowedOnlinePlayers;
	}

	private void kickClient(final ClientThread client) {
		if (client == null) {
			return;
		}

		GeneralThreadPool.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				if (client.getActiveChar() != null) {
					client.getActiveChar().sendPackets(new S_ServerMessage(357));
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
				client.kick();
			}
		});
	}

	public synchronized void login(ClientThread client, L1Account account)
			throws GameServerFullException, AccountAlreadyLoginException {
		if (!account.isValid()) {
			// パスワード認証がされていない、あるいは認証に失敗したアカウントが指定された。
			// このコードは、バグ検出の為にのみ存在する。
			throw new IllegalArgumentException(I18N_IS_NOT_AUTHENTICATED);
			// 認証されていないアカウントです。
		}
		if ((getMaxAllowedOnlinePlayers() <= getOnlinePlayerCount())
				&& !account.isGameMaster()) {
			throw new GameServerFullException();
		}
		if (_accounts.containsKey(account.getName())) {
			kickClient(_accounts.remove(account.getName()));
			throw new AccountAlreadyLoginException();
		}
		_accounts.put(account.getName(), client);
	}

	public synchronized boolean logout(ClientThread client) {
		if (client.getAccountName() == null) {
			return false;
		}
		return _accounts.remove(client.getAccountName()) != null;
	}
}
