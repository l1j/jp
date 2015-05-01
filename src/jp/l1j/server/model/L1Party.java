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
package jp.l1j.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jp.l1j.configure.Config;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_HpMeter;
import jp.l1j.server.packets.server.S_Party;
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.model:
// L1Party

public class L1Party {
	private static Logger _log = Logger
		.getLogger(L1Party.class.getName());

	private final List<L1PcInstance> _membersList = new ArrayList<L1PcInstance>();

	private L1PcInstance _leader = null;

	public void addMember(L1PcInstance pc) {
		if (pc == null) {
			throw new NullPointerException();
		}
		if (_membersList.size() == Config.MAX_PT && !_leader.isGm()
				|| _membersList.contains(pc)) {
			return;
		}

		if (_membersList.isEmpty()) {
			// 最初のPTメンバーであればリーダーにする
			setLeader(pc);
		} else {
			createMiniHp(pc);
		}

		_membersList.add(pc);
		pc.setParty(this);
		showAddPartyInfo(pc);
		pc.startRefreshParty();
	}

	private void removeMember(L1PcInstance pc) {
		if (!_membersList.contains(pc)) {
			return;
		}
		pc.stopRefreshParty();
		_membersList.remove(pc);
		pc.setParty(null);
		if (!_membersList.isEmpty()) {
			deleteMiniHp(pc);
		}
	}

	public boolean isVacancy() {
		return _membersList.size() < Config.MAX_PT;
	}

	public int getVacancy() {
		return Config.MAX_PT - _membersList.size();
	}

	public boolean isMember(L1PcInstance pc) {
		return _membersList.contains(pc);
	}

	private void setLeader(L1PcInstance pc) {
		_leader = pc;
	}

	public L1PcInstance getLeader() {
		return _leader;
	}

	public boolean isLeader(L1PcInstance pc) {
		return pc.getId() == _leader.getId();
	}

	public String getMembersNameList() {
		String _result = new String("");
		for (L1PcInstance pc : _membersList) {
			_result = _result + pc.getName() + " ";
		}
		return _result;
	}

	private void createMiniHp(L1PcInstance pc) {
		// パーティー加入時、相互にHPを表示させる
		L1PcInstance[] members = getMembers();

		for (L1PcInstance member : members) {
			member.sendPackets(new S_HpMeter(pc.getId(), 100
					* pc.getCurrentHp() / pc.getMaxHp()));
			pc.sendPackets(new S_HpMeter(member.getId(), 100
					* member.getCurrentHp() / member.getMaxHp()));
		}
	}

	private void deleteMiniHp(L1PcInstance pc) {
		// パーティー離脱時、HPバーを削除する。
		L1PcInstance[] members = getMembers();

		for (L1PcInstance member : members) {
			pc.sendPackets(new S_HpMeter(member.getId(), 0xff));
			member.sendPackets(new S_HpMeter(pc.getId(), 0xff));
		}
	}

	public void updateMiniHP(L1PcInstance pc) {
		L1PcInstance[] members = getMembers();

		for (L1PcInstance member : members) { // パーティーメンバー分更新
			member.sendPackets(new S_HpMeter(pc.getId(), 100
					* pc.getCurrentHp() / pc.getMaxHp()));
		}
	}

	private void breakup() {
		L1PcInstance[] members = getMembers();

		for (L1PcInstance member : members) {
			removeMember(member);
			member.sendPackets(new S_ServerMessage(418)); // パーティーを解散しました。
		}
	}

	public void passLeader(L1PcInstance pc) {
		pc.getParty().setLeader(pc);
		for (L1PcInstance member : getMembers()) {
			member.sendPackets(new S_Party(0x6A, pc));
		}
	}

	public void leaveMember(L1PcInstance pc) {
		if (isLeader(pc) || getNumOfMembers() == 2) {
			// パーティーリーダーの場合
			breakup();
		} else {
			removeMember(pc);
			for (L1PcInstance member : getMembers()) {
				sendLeftMessage(member, pc);
			}
			sendLeftMessage(pc, pc);
		}
	}

	public void kickMember(L1PcInstance pc) {
		if (getNumOfMembers() == 2) {
			// パーティーメンバーが自分とリーダーのみ
			breakup();
		} else {
			removeMember(pc);
			for (L1PcInstance member : getMembers()) {
				sendLeftMessage(member, pc);
			}
			sendKickMessage(pc);
		}
	}

	private void showAddPartyInfo(L1PcInstance pc) {
		for (L1PcInstance member : getMembers()) {
			if (pc.getId() == getLeader().getId() && getNumOfMembers() == 1) {
				continue;
			}
			if (pc.getId() == member.getId()) {
				pc.sendPackets(new S_Party(0x68, pc));
			} else {
				member.sendPackets(new S_Party(0x69, pc));
			}
			member.sendPackets(new S_Party(0x6e, member));
			createMiniHp(member);
		}
	}

	public L1PcInstance[] getMembers() {
		return _membersList.toArray(new L1PcInstance[_membersList.size()]);
	}

	public int getNumOfMembers() {
		return _membersList.size();
	}

	private void sendKickMessage(L1PcInstance kickpc) {
		kickpc.sendPackets(new S_ServerMessage(419));
	}

	private void sendLeftMessage(L1PcInstance sendTo, L1PcInstance left) {
		// %0がパーティーから去りました。
		sendTo.sendPackets(new S_ServerMessage(420, left.getName()));
	}

}