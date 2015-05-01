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

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.L1PolyMorph;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1Polyme implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Polyme.class.getName());

	private L1Polyme() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Polyme();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			Collection<L1PcInstance> players = null;
			if (arg.equalsIgnoreCase("gm")) { // GM モーフ
				players = new ArrayList<L1PcInstance>();
				players.add(pc);
				L1PolyMorph.doPoly(pc, 1080, 7200, L1PolyMorph.MORPH_BY_GM);
			}
			else if (arg.equalsIgnoreCase("dk")) { // GM用 デスナイト モーフ
				L1PolyMorph.doPoly(pc, 5641, 7200, L1PolyMorph.MORPH_BY_GM);
			} else {
				try {
					StringTokenizer st = new StringTokenizer(arg);
					int polyid = Integer.parseInt(st.nextToken());
					players = new ArrayList<L1PcInstance>();
					players.add(pc);
					L1PolyMorph.doPoly(pc, polyid, 7200, L1PolyMorph.MORPH_BY_GM);
				} catch (Exception exception) {
					pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_1,
							cmdName, I18N_GFX_ID)));
					// .%s %s の形式で入力してください。
				}
			}
		} catch (Exception e) {
					pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_1,
							cmdName, I18N_GFX_ID)));
					// .%s %s の形式で入力してください。
		}
	}
}
