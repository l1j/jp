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
package jp.l1j.server.model.instance;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1Quest;
import jp.l1j.server.packets.server.S_ChangeHeading;
import jp.l1j.server.packets.server.S_NpcTalkReturn;
import jp.l1j.server.templates.L1Npc;

public class L1QuestInstance extends L1NpcInstance {

	private static Logger _log = Logger.getLogger(L1QuestInstance.class
			.getName());

	private L1QuestInstance _npc = this;

	public L1QuestInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onNpcAI() {
		int npcId = getNpcTemplate().getNpcId();
		if (isAiRunning()) {
			return;
		}
		if (npcId == 71075 || npcId == 70957 || npcId == 81209) {
			return;			
		} else {
			setActived(false);
			startAI();
		}
	}

	@Override
	public void onAction(L1PcInstance pc) {
		L1Attack attack = new L1Attack(pc, this);
		if (attack.calcHit()) {
			attack.calcDamage();
			attack.calcStaffOfMana();
			attack.addPcPoisonAttack(pc, this);
			attack.addChaserAttack();
			attack.addEvilAttack();
		}
		attack.action();
		attack.commit();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int pcX = pc.getX();
		int pcY = pc.getY();
		int npcX = getX();
		int npcY = getY();

		if (pcX == npcX && pcY < npcY) {
			setHeading(0);
		} else if (pcX > npcX && pcY < npcY) {
			setHeading(1);
		} else if (pcX > npcX && pcY == npcY) {
			setHeading(2);
		} else if (pcX > npcX && pcY > npcY) {
			setHeading(3);
		} else if (pcX == npcX && pcY > npcY) {
			setHeading(4);
		} else if (pcX < npcX && pcY > npcY) {
			setHeading(5);
		} else if (pcX < npcX && pcY == npcY) {
			setHeading(6);
		} else if (pcX < npcX && pcY < npcY) {
			setHeading(7);
		}
		broadcastPacket(new S_ChangeHeading(this));

		int npcId = getNpcTemplate().getNpcId();
		if (npcId ==  71092 || npcId == 71093) { // 調査員
			if (pc.isKnight() && pc.getQuest().getStep(L1Quest.QUEST_LEVEL45) == 4) {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "searcherk1"));
			} else {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "searcherk4"));
			}
		} else if (npcId == 71094) { // エンディア
			if (pc.isDarkelf() && pc.getQuest().getStep(L1Quest.QUEST_LEVEL50) == 2) {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "endiaq1"));
			} else {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "endiaq4"));
			}
		} else if (npcId == 71062) { // カミット
			if (pc.getQuest().getStep(L1Quest.QUEST_CADMUS)
					== 2) {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "kamit1b"));
			} else {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "kamit1"));
			}
		} else if (npcId == 71075) { // 疲れ果てたリザードマンファイター
			if (pc.getQuest().getStep(L1Quest.QUEST_LIZARD)
					== 1) {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "llizard1b"));
			} else {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "llizard1a"));
			}
		} else if (npcId == 70957 || npcId == 81209) { // ロイ
			if (pc.getQuest().getStep(L1Quest.QUEST_ROI)
					!= 1) {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "roi1"));
			} else {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "roi2"));
			}
		} else if (npcId == 91312) { // ディカルデンの女諜報員
			if (pc.isElf() && (pc.getQuest().getStep(L1Quest.QUEST_LEVEL50) == 3)) {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "dspy2"));
			}
			else {
				pc.sendPackets(new S_NpcTalkReturn(getId(), "dspy1"));
			}
		}

		synchronized (this) {
			if (_monitor != null) {
				_monitor.cancel();
			}
			setRest(true);
			_monitor = new RestMonitor();
			_restTimer.schedule(_monitor, REST_MILLISEC);
		}
	}

	@Override
	public void onFinalAction(L1PcInstance pc, String action) {
		if (action.equalsIgnoreCase("start")) {
			int npcId = getNpcTemplate().getNpcId();
			if ((npcId == 71092 || npcId == 71093)
					&& pc.isKnight() && pc.getQuest().getStep(L1Quest.QUEST_LEVEL45) == 4) {
				L1Npc l1npc = NpcTable.getInstance().getTemplate(71093);
				L1FollowerInstance follow = new L1FollowerInstance(l1npc,
						this, pc);
				pc.sendPackets(new S_NpcTalkReturn(getId(), ""));
			} else if (npcId == 71094
					&& pc.isDarkelf() && pc.getQuest().getStep(L1Quest.QUEST_LEVEL50) == 2) {
				L1Npc l1npc = NpcTable.getInstance().getTemplate(71094);
				L1FollowerInstance follow = new L1FollowerInstance(l1npc,
						this, pc);
				pc.sendPackets(new S_NpcTalkReturn(getId(), ""));
			} else if (npcId == 71062
					&& pc.getQuest().getStep(L1Quest.QUEST_CADMUS)
					== 2) {
				L1Npc l1npc = NpcTable.getInstance().getTemplate(71062);
				L1FollowerInstance follow = new L1FollowerInstance(l1npc,
						this, pc);
				pc.sendPackets(new S_NpcTalkReturn(getId(), ""));
			} else if (npcId == 71075
					&& pc.getQuest().getStep(L1Quest.QUEST_LIZARD)
					== 1) {
				L1Npc l1npc = NpcTable.getInstance().getTemplate(71075);
				L1FollowerInstance follow = new L1FollowerInstance(l1npc,
						this, pc);
				pc.sendPackets(new S_NpcTalkReturn(getId(), ""));
			} else if (npcId == 70957 || npcId == 81209) {

				L1Npc l1npc = NpcTable.getInstance().getTemplate(70957);
				L1FollowerInstance follow = new L1FollowerInstance(l1npc,
						this, pc);
				pc.sendPackets(new S_NpcTalkReturn(getId(), ""));
			} else if ((npcId == 91312) && (pc.getQuest().getStep(L1Quest.QUEST_LEVEL50) == 3)) {
				L1Npc l1npc = NpcTable.getInstance().getTemplate(91312);
				new L1FollowerInstance(l1npc, this, pc);
				pc.sendPackets(new S_NpcTalkReturn(getId(), ""));
			}
		}
	}

	private static final long REST_MILLISEC = 10000;

	private static final Timer _restTimer = new Timer(true);

	private RestMonitor _monitor;

	public class RestMonitor extends TimerTask {
		@Override
		public void run() {
			setRest(false);
		}
	}

}
