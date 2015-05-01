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
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.map.L1WorldMap;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1Move implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Move.class.getName());

	private L1Move() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Move();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int locx = Integer.parseInt(st.nextToken());
			int locy = Integer.parseInt(st.nextToken());
			short mapid;
			if (st.hasMoreTokens()) {
				mapid = Short.parseShort(st.nextToken());
			} else {
				mapid = pc.getMapId();
			}
			L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
			int tile = L1WorldMap.getInstance().getMap(mapid).getOriginalTile(locx, locy);
			String msg = String.format(I18N_MOVED_TO_LOC, locx, locy, mapid, tile);
			// 座標 (%d, %d, %d) %d に移動しました。
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_3,
					cmdName, I18N_AXIS_X, I18N_AXIS_Y, "["+I18N_MAP_ID+"]")));
			// .%s %s %s %s の形式で入力してください。
		}
	}
}
