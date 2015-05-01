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

import java.util.StringTokenizer;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.map.L1InstanceMap;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1RemoveInstanceMap implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1RemoveInstanceMap.class.getName());

	private L1RemoveInstanceMap() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1RemoveInstanceMap();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int mapid = Integer.parseInt(st.nextToken());
			boolean flag = L1InstanceMap.getInstance().removeInstanceMap(mapid);
			if(flag){
				pc.sendPackets(new S_SystemMessage(String.format(I18N_REMOVED_THE_INSTANCE_MAP, mapid)));
				// インスタンスマップ: %d を削除しました。
			}else{
				pc.sendPackets(new S_SystemMessage(String.format(I18N_DOES_NOT_EXIST_THE_INSTANCE_MAP, mapid)));
				// インスタンスマップ: %d は存在しません。
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_1,
					cmdName, I18N_MAP_ID)));
			// .%s %s の形式で入力してください。
		}
	}
}
