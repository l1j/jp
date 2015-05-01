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

package jp.l1j.server.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import jp.l1j.configure.Config;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.model.L1World;

public class AnnouncementsCycle {
	private int round = 0;
	private String line = null;
	private boolean firstboot = true;;
	private StringBuffer sb = new StringBuffer();
	private static AnnouncementsCycle _instance;
	private static L1World _world = L1World.getInstance();

	/** アナウンス設定ファイル */
	private static File dir = new File("data/announceCycle.txt");

	/** 読み込み用バッファ */
	private static BufferedReader buf;
	
	/** 最終更新時間 */
	private static long lastmodify = dir.lastModified();

	/** アナウンスリスト */
	List<String> list = new ArrayList<String>();

	private AnnouncementsCycle() {
		cycle();
	}

	public static AnnouncementsCycle getInstance() {
		if (_instance == null) {
			_instance = new AnnouncementsCycle();
		}
		return _instance;
	}

	/**
	 * アナウンス設定ファイルを読み込み
	 */
	private void scanfile() {
		try {
			fileEnsure(); // アナウンス設定ファイルの存在チェック
			if (dir.lastModified() > lastmodify || firstboot) { // ファイル更新チェック
				list.clear(); // アナウンスリストを初期化
				buf = new BufferedReader(new InputStreamReader(new FileInputStream(dir)));
				while ((line = buf.readLine()) != null) {
					if (line.startsWith("#")||line.isEmpty()) { // コメント行をスキップ
						continue;
					}
					sb.delete(0, sb.length()); // バッファをクリア
					list.add(line);
				}
				lastmodify = dir.lastModified(); // 最終更新時間
			} else {
				// ファイルが更新されていない場合、何もしない。
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				buf.close();
				firstboot = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ファイルの存在チェック
	 *
	 * @throws IOException
	 *
	 */
	private void fileEnsure() throws IOException {
		if (!dir.exists()) {
			dir.createNewFile();
		}
	}
	
	/**
	 * アナウンス間隔(デフォルトは10分間隔)
	 */
	private void cycle() {
		AnnouncementsCycleTask task = new AnnouncementsCycleTask();
		GeneralThreadPool.getInstance().scheduleAtFixedRate(task, 100000,
				60000 * Config.ANNOUNCEMENTS_CYCLE_TIME); // 指定した間隔でアナウンス
	}

	/**
	 * アナウンスタイムコントローラー
	 */
	class AnnouncementsCycleTask implements Runnable {		
		@Override
		public void run() {
			scanfile();
			if (Config.ANNOUNCEMENTS_TIME_DISPALY) {
				SimpleDateFormat formatter =
						new SimpleDateFormat(Config.ANNOUNCEMENTS_TIME_FORMAT);
				_world.broadcastServerMessage("("+ formatter.format(new Date(lastmodify)) + ")");
			}
			Iterator<String> iterator = list.listIterator();
			if (iterator.hasNext()) {
				round %= list.size();
				_world.broadcastServerMessage(list.get(round));
				round++;
			}
		}
	}
}
