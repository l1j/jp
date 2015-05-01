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

import jp.l1j.server.datatables.NpcTalkDataTable;
import jp.l1j.server.model.L1DragonSlayer;
import jp.l1j.server.model.L1NpcTalkData;
import jp.l1j.server.model.L1Teleport;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_NpcTalkReturn;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1Npc;

public class L1DragonPortalInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @param template
	 */
	public L1DragonPortalInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int npcid = getNpcTemplate().getNpcId();
		int portalNumber = getPortalNumber();
		int X = 0;
		int Y = 0;
		short mapid = (short) (1005 + portalNumber);
		int objid = getId();
		L1NpcTalkData talking = NpcTalkDataTable.getInstance().getTemplate(npcid);
		String htmlid = null;
		String[] htmldata = null;
		if ((npcid >= 91051 && npcid <= 91054)) {
			if (portalNumber == -1) {
				return;
			}
			if (L1DragonSlayer.getInstance().getPlayersCount(portalNumber) >= 32) {
				pc.sendPackets(new S_ServerMessage(1536)); // 定員に達したため、入場できません。
			} else if (L1DragonSlayer.getInstance().getDragonSlayerStatus()[portalNumber] >= 5) {
				pc.sendPackets(new S_ServerMessage(1537)); // ドラゴンが目覚めたため、今は入場できません。
			} else {
				if (portalNumber < 6) { // ドラゴンポータル(地)
					if (pc.hasSkillEffect(BLOODSTAIN_OF_ANTHARAS)) {
						pc.sendPackets(new S_ServerMessage(1626));
						// 全身からドラゴンの血の匂いが漂っています。それが消えるまでは、ドラゴンポータルに入場できません。
						return;
					}
					X = 32599;
					Y = 32742;
				} else if (portalNumber < 12) { // ドラゴンポータル(水)
					if (pc.hasSkillEffect(BLOODSTAIN_OF_FAFURION)) {
						pc.sendPackets(new S_ServerMessage(1626));
						// 全身からドラゴンの血の匂いが漂っています。それが消えるまでは、ドラゴンポータルに入場できません。
						return;
					}
					X = 32927;
					Y = 32741;
				} else if (portalNumber < 18) { // ドラゴンポータル(風)
					if (pc.hasSkillEffect(BLOODSTAIN_OF_LINDVIOR)) {
						pc.sendPackets(new S_ServerMessage(1626));
						// 全身からドラゴンの血の匂いが漂っています。それが消えるまでは、ドラゴンポータルに入場できません。
						return;
					}
					X = 32673;
					Y = 32926;
				//} else if (portalNumber < 24) { // ドラゴンポータル(火)(未実装)
				//	if (pc.hasSkillEffect(BLOODSTAIN_OF_VALAKAS)) {
				//		pc.sendPackets(new S_ServerMessage(1626));
				//		// 全身からドラゴンの血の匂いが漂っています。それが消えるまでは、ドラゴンポータルに入場できません。
				//		return;
				//	}
				//	X = ;
				//	Y = ;
				}
				pc.setPortalNumber(portalNumber);
				L1DragonSlayer.getInstance().addPlayerList(pc, portalNumber);
				L1Teleport.teleport(pc, X, Y, mapid, 2, true);
			}
		} else if (npcid == 91066) { // ドラゴンポータル(隠された竜の地)
			int level = pc.getLevel();
			if (level >= 30 && level <= 51) {
				htmlid = "dsecret1";
			} else if (level >= 52) {
				htmlid = "dsecret2";
			} else {
				htmlid = "dsecret3";
			}
		}

		if (htmlid != null) {
			pc.sendPackets(new S_NpcTalkReturn(objid, htmlid, htmldata));
		} else {
			pc.sendPackets(new S_NpcTalkReturn(talking, objid, 1));
		}
	}
}
