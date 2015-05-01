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

import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.L1Party;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1PartyRecall implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1PartyRecall.class.getName());

	private L1PartyRecall() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1PartyRecall();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		L1PcInstance target = L1World.getInstance().getPlayer(arg);

		if (target != null) {
			L1Party party = target.getParty();
			if (party != null) {
				int x = pc.getX();
				int y = pc.getY() + 2;
				short map = pc.getMapId();
				L1PcInstance[] players = party.getMembers();
				for (L1PcInstance pc2 : players) {
					try {
						L1Teleport.teleport(pc2, x, y, map, 5, true);
						pc2.sendPackets(new S_SystemMessage(I18N_RECALLED_BY_GM));
						// GMにより召還されました。
					} catch (Exception e) {
						_log.log(Level.SEVERE, "", e);
					}
				}
			} else {
				pc.sendPackets(new S_SystemMessage(I18N_IS_NOT_PARTY_MEMBER));
				// パーティメンバーではありません。
			}
		} else {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_DOES_NOT_EXIST_CHAR, arg)));
			// %s はゲームワールド内に存在しません。
		}
	}
}
