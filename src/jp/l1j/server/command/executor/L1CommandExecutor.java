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

package jp.l1j.server.command.executor;

import jp.l1j.server.model.instance.L1PcInstance;

/**
 * コマンド実行処理インターフェース
 * 
 * コマンド処理クラスは、このインターフェースメソッド以外に<br>
 * public static L1CommandExecutor getInstance()<br>
 * を実装しなければならない。
 * 通常、自クラスをインスタンス化して返すが、必要に応じてキャッシュされたインスタンスを返したり、他のクラスをインスタンス化して返すことができる。
 */
public interface L1CommandExecutor {
	/**
	 * このコマンドを実行する。
	 * 
	 * @param pc
	 *            実行者
	 * @param cmdName
	 *            実行されたコマンド名
	 * @param arg
	 *            引数
	 */
	public void execute(L1PcInstance pc, String cmdName, String arg);
}
