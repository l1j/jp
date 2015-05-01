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
/*
 * $Header: /cvsroot/l2j/L2_Gameserver/java/net/sf/l2j/Server.java,v 1.5 2004/11/19 08:54:43 l2chef Exp $
 *
 * $Author: l2chef $
 * $Date: 2004/11/19 08:54:43 $
 * $Revision: 1.5 $
 * $Log: Server.java,v $
 * Revision 1.5  2004/11/19 08:54:43  l2chef
 * database is now used
 *
 * Revision 1.4  2004/07/08 22:42:28  l2chef
 * logfolder is created automatically
 *
 * Revision 1.3  2004/06/30 21:51:33  l2chef
 * using jdk logger instead of println
 *
 * Revision 1.2  2004/06/27 08:12:59  jeichhorn
 * Added copyright notice
 */
package jp.l1j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.GameServer;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.telnet.TelnetServer;

/**
 * l1j-jpのサーバを起動する.
 */
public class Server {
	/** メッセージログ用. */
	private static Logger _log = Logger.getLogger(Server.class.getName());

	/** ログ設定ファイルのフォルダー. */
	private static final String LOG_PROP = "./config/log.properties";

	/**
	 * サーバメイン.
	 * 
	 * @param args
	 *            コマンドライン引数
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		File logFolder = new File("log");
		logFolder.mkdir();

		try {
			InputStream is = new BufferedInputStream(new FileInputStream(LOG_PROP));
			LogManager.getLogManager().readConfiguration(is);
			is.close();
		} catch (IOException e) {
			_log.log(Level.SEVERE, "Failed to Load " + LOG_PROP + " File.", e);
			System.exit(0);
		}
		try {
			Config.load();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			System.exit(0);
		}

		// L1DatabaseFactory初期設定
		L1DatabaseFactory.setDatabaseSettings(Config.DB_DRIVER, Config.DB_URL,
				Config.DB_LOGIN, Config.DB_PASSWORD);
		L1DatabaseFactory.getInstance();

		try {
			GameServer.getInstance().initialize();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			System.exit(0);
		}
		if (Config.TELNET_SERVER) {
			TelnetServer.getInstance().start();
		}
	}
}
