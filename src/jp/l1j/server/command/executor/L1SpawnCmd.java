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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.L1SpawnUtil;

public class L1SpawnCmd implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1SpawnCmd.class.getName());

	private L1SpawnCmd() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1SpawnCmd();
	}

	private void sendErrorMessage(L1PcInstance pc, String cmdName) {
		pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_3,
				cmdName, I18N_NPC_ID+"|"+I18N_NPC_NAME, "["+I18N_AMOUNT+"]", "["+I18N_RANGE+"]")));
		// .%s %s %s %s の形式で入力してください。
	}

	private int parseNpcId(String nameId) {
		int npcid = 0;
		try {
			npcid = Integer.parseInt(nameId);
		} catch (NumberFormatException e) {
			npcid = NpcTable.getInstance().findNpcIdByNameWithoutSpace(nameId);
		}
		return npcid;
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			String nameId = tok.nextToken();
			int count = 1;
			if (tok.hasMoreTokens()) {
				count = Integer.parseInt(tok.nextToken());
			}
			int randomrange = 0;
			if (tok.hasMoreTokens()) {
				randomrange = Integer.parseInt(tok.nextToken(), 10);
			}
			int npcid = parseNpcId(nameId);

			L1Npc npc = NpcTable.getInstance().getTemplate(npcid);
			if (npc == null) {
				pc.sendPackets(new S_SystemMessage(I18N_DOES_NOT_EXIST_NPC));
				// NPCは存在しません。
				return;
			}
			for (int i = 0; i < count; i++) {
				L1SpawnUtil.spawn(pc, npcid, randomrange, 0);
			}
			String msg = String.format(I18N_SUMMON_MONSTER_1, npc.getName(), npcid, count, randomrange);
			// %s(%d) (%d) を召還しました。 (範囲:%d)
			pc.sendPackets(new S_SystemMessage(msg));
		} catch (NoSuchElementException e) {
			sendErrorMessage(pc, cmdName);
		} catch (NumberFormatException e) {
			sendErrorMessage(pc, cmdName);
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_ERROR, cmdName)));
			// .%s コマンドエラー
		}
	}
}
