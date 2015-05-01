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

package jp.l1j.server.packets.client;

import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.ClientThread;
import jp.l1j.server.controller.LoginController;
import jp.l1j.server.datatables.IpTable;
import jp.l1j.server.exception.AccountAlreadyLoginException;
import jp.l1j.server.exception.GameServerFullException;
import jp.l1j.server.packets.server.S_CommonNews;
import jp.l1j.server.packets.server.S_LoginResult;
import jp.l1j.server.templates.L1Account;

public class C_AuthLogin extends ClientBasePacket {
	private static final String C_AUTH_LOGIN = "[C] C_AuthLogin";
	
	private static Logger _log = Logger.getLogger(C_AuthLogin.class.getName());

	public C_AuthLogin(byte[] decrypt, ClientThread client) {
		super(decrypt);
		String accountName = readS().toLowerCase();
		String password = readS();
		String ip = client.getIp();
		String host = client.getHostname();
		_log.finest("Request AuthLogin from user : " + accountName);
		if (!Config.ALLOW_2PC) {
			for (ClientThread tempClient : LoginController.getInstance().getAllAccounts()) {
				if (ip.equalsIgnoreCase(tempClient.getIp())) {
					_log.info(String.format(I18N_DENY_TO_MULTIPLE_LOGINS, accountName, host));
					// 多重ログインを拒否しました。account=%s host=%s
					client.sendPacket(new S_LoginResult(S_LoginResult.REASON_USER_OR_PASS_WRONG));
					return;
				}
			}
		}
		L1Account account = L1Account.findByName(accountName);
		if (IpTable.getInstance().isBannedIpMask(host)
				&& (account == null || account.getAccessLevel() == 0)) {
			_log.info(String.format(I18N_DENY_TO_CONNECT_FROM_SPECIFIC_IP, accountName, host));
			// 特定のIP範囲の接続を拒否しました。account=%s host=%s
			client.sendPacket(new S_LoginResult(S_LoginResult.REASON_USER_OR_PASS_WRONG));
			return;
		}
		if (account == null) {
			if (Config.AUTO_CREATE_ACCOUNTS) {
				account = L1Account.create(accountName, password, ip, host);
			} else {
				_log.warning("account missing for user " + accountName);
			}
		}
		if (account == null || !account.validatePassword(password)) {
			client.sendPacket(new S_LoginResult(S_LoginResult.REASON_USER_OR_PASS_WRONG));
			return;
		}
		if (!account.isActive()) { // BANアカウント
			_log.info(String.format(I18N_DENY_TO_LOGIN_BAN_ACCOUNT, accountName, host));
			// BANアカウントのログインを拒否しました。account=%s host=%s
			client.sendPacket(new S_LoginResult(S_LoginResult.REASON_USER_OR_PASS_WRONG));
			return;
		}
		try {
			LoginController.getInstance().login(client, account);
			account.updateLastActivatedTime(); // 最終ログイン日を更新する
			client.setAccount(account);
			client.sendPacket(new S_LoginResult(S_LoginResult.REASON_LOGIN_OK));
			client.sendPacket(new S_CommonNews());
		} catch (GameServerFullException e) {
			client.kick();
			_log.info(String.format(I18N_DENY_TO_LOGIN_BAN_ACCOUNT, host));
			// 最大接続人数に達しているためログインを拒否しました。host=%s
			return;
		} catch (AccountAlreadyLoginException e) {
			client.kick();
			_log.info(String.format(I18N_MULTIPLE_LOGINS_DETECTED, host));
			// 多重ログインを検出しました。%S の接続を切断します。
			return;
		}
	}

	@Override
	public String getType() {
		return C_AUTH_LOGIN;
	}
}