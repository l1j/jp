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
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Npc;

public class L1Summon implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Summon.class.getName());

	private L1Summon() {
	}

	public static L1Summon getInstance() {
		return new L1Summon();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			String nameid = tok.nextToken();
			int npcid = 0;
			try {
				npcid = Integer.parseInt(nameid);
			} catch (NumberFormatException e) {
				npcid = NpcTable.getInstance().findNpcIdByNameWithoutSpace(nameid);
				if (npcid == 0) {
					pc.sendPackets(new S_SystemMessage(I18N_DOES_NOT_EXIST_NPC));
					// NPCは存在しません。
					return;
				}
			}
			int count = 1;
			if (tok.hasMoreTokens()) {
				count = Integer.parseInt(tok.nextToken());
			}
			L1Npc npc = NpcTable.getInstance().getTemplate(npcid);
			for (int i = 0; i < count; i++) {
				L1SummonInstance summonInst = new L1SummonInstance(npc, pc);
				summonInst.setPetcost(0);
			}
			nameid = NpcTable.getInstance().getTemplate(npcid).getName();
			pc.sendPackets(new S_SystemMessage(String.format(I18N_SUMMON_MONSTER_2,
					nameid, npcid, count)));
			// %s(%d) (%d) を召還しました。
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_2,
				cmdName, I18N_NPC_ID+"|"+I18N_NPC_NAME, "["+I18N_AMOUNT+"]")));
			// .%s %s %s の形式で入力してください。
		}
	}
}
