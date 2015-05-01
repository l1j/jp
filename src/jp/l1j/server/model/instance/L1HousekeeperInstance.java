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

import java.util.Calendar;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.HouseTable;
import jp.l1j.server.datatables.NpcTalkDataTable;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1NpcTalkData;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.item.L1ItemId;
import jp.l1j.server.packets.server.S_NpcTalkReturn;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1House;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.StringUtil;

public class L1HousekeeperInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;
	
	private static Logger _log = Logger.getLogger(L1HousekeeperInstance.class.getName());

	public L1HousekeeperInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance pc) {
		onAction(pc, 0);
	}

	@Override
	public void onAction(L1PcInstance pc, int skillId) {
		L1Attack attack = new L1Attack(pc, this, skillId);
		attack.calcHit();
		attack.action();
		attack.addChaserAttack();
		attack.addEvilAttack();
		attack.calcDamage();
		attack.calcStaffOfMana();
		attack.addPcPoisonAttack(pc, this);
		attack.commit();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int objid = getId();
		L1NpcTalkData talking = NpcTalkDataTable.getInstance().getTemplate(getNpcTemplate().getNpcId());
		int npcid = getNpcTemplate().getNpcId();
		String htmlid = null;
		String[] htmldata = null;
		boolean isOwner = false;
		if (talking != null) {
			// 話しかけたPCが所有者とそのクラン員かどうか調べる
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(houseId);
					if (npcid == house.getKeeperId()) {
						isOwner = true;
					}
				}
			}
			// 所有者とそのクラン員以外なら会話内容を変える
			if (!isOwner) {
				// Housekeeperが属するアジトを取得する
				L1House targetHouse = null;
				for (L1House house : HouseTable.getInstance().getHouseTableList()) {
					if (npcid == house.getKeeperId()) {
						targetHouse = house;
						break;
					}
				}
				// アジトがに所有者が居るかどうか調べる
				boolean isOccupy = false;
				String clanName = null;
				String leaderName = null;
				for (L1Clan targetClan : L1World.getInstance().getAllClans()) {
					if (targetHouse.getHouseId() == targetClan.getHouseId()) {
						isOccupy = true;
						clanName = targetClan.getClanName();
						leaderName = targetClan.getLeaderName();
						break;
					}
				}
				// 会話内容を設定する
				if (isOccupy) { // 所有者あり
					htmlid = "agname";
					htmldata = new String[] { clanName, leaderName, targetHouse.getHouseName() };
				} else { // 所有者なし(競売中)
					htmlid = "agnoname";
					htmldata = new String[] { targetHouse.getHouseName() };
				}
			}
			// html表示パケット送信
			if (htmlid != null) { // htmlidが指定されている場合
				if (htmldata != null) { // html指定がある場合は表示
					pc.sendPackets(new S_NpcTalkReturn(objid, htmlid, htmldata));
				} else {
					pc.sendPackets(new S_NpcTalkReturn(objid, htmlid));
				}
			} else {
				if (pc.getLawful() < -1000) { // プレイヤーがカオティック
					pc.sendPackets(new S_NpcTalkReturn(talking, objid, 2));
				} else {
					pc.sendPackets(new S_NpcTalkReturn(talking, objid, 1));
				}
			}
		}
	}

	@Override
	public void onFinalAction(L1PcInstance pc, String action) {
	}

	public void doFinalAction(L1PcInstance pc) {
	}

	private static final int FEE_AMOUNT = 2000;

	public void payFree(L1PcInstance pc) {
		if (!pc.getInventory().checkItem(L1ItemId.ADENA, FEE_AMOUNT)) {
			pc.sendPackets(new S_ServerMessage(189)); // \f1アデナが不足しています。
			return;
		}
		L1House house = HouseTable.getInstance().findByKeeperId(getNpcId());
		Calendar cal = house.getTaxDeadline();
		cal.set(Calendar.MINUTE, 0); // 分、秒は切り捨て
		cal.set(Calendar.SECOND, 0);
		Calendar next = Calendar.getInstance();
		next.add(Calendar.DATE, Config.HOUSE_TAX_INTERVAL);
		next.set(Calendar.MINUTE, 0); // 分、秒は切り捨て
		next.set(Calendar.SECOND, 0);
		if (next.compareTo(cal) > 0) {
			cal.add(Calendar.DATE, Config.HOUSE_TAX_INTERVAL);
			HouseTable.getInstance().updateHouse(house); // DBに書き込み
			pc.getInventory().consumeItem(L1ItemId.ADENA, FEE_AMOUNT);
		} else {
			pc.sendPackets(new S_SystemMessage(I18N_TAX_HAS_ALLREADY_PAID));
		}
	}

	public String[] makeHouseTaxStrings(L1PcInstance pc) {
		L1House house = HouseTable.getInstance().findByKeeperId(getNpcId());
		Calendar cal = house.getTaxDeadline();
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		return StringUtil.newArray(getNameId(), FEE_AMOUNT, month, day, hour);
	}
}

