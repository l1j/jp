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

package jp.l1j.server.packets.server;

import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1PcInstance;

public class S_Party extends ServerBasePacket {

	private static final String _S_Party = "[S] S_Party";
	private byte[] _byte = null;

	public S_Party(String htmlid, int objid) {
		buildPacket(htmlid, objid, "", "", 0);
	}

	public S_Party(String htmlid, int objid, String partyname,
			String partymembers) {

		buildPacket(htmlid, objid, partyname, partymembers, 1);
	}

	private void buildPacket(String htmlid, int objid, String partyname,
			String partymembers, int type) {
		writeC(Opcodes.S_OPCODE_SHOWHTML);
		writeD(objid);
		writeS(htmlid);
		writeH(type);
		writeH(0x02);
		writeS(partyname);
		writeS(partymembers);
	}
	//3.3C start
	public S_Party(int type, L1PcInstance pc) {// 3.3C パーティー
		switch (type) {
		case 104:
			newMember(pc);
			break;
		case 105:
			oldMember(pc);
			break;
		case 106:
			changeLeader(pc);
		case 110:
			refreshParty(pc);
			break;
		default:
			break;
		}
	}

	/**
	 * パーティー新規加入メンバー
	 * 
	 * @param pc
	 */
	public void newMember(L1PcInstance pc) {
		L1PcInstance leader = pc.getParty().getLeader();
		L1PcInstance member[] = pc.getParty().getMembers();
		double nowhp = 0.0d;
		double maxhp = 0.0d;
		if (pc.getParty() == null) {
			return;
		} else {
			writeC(Opcodes.S_OPCODE_PACKETBOX);
			writeC(S_PacketBox.UPDATE_OLD_PART_MEMBER);
			nowhp = leader.getCurrentHp();
			maxhp = leader.getMaxHp();
			writeC(member.length - 1);
			writeD(leader.getId());
			writeS(leader.getName());
			writeC((int) (nowhp / maxhp) * 100);
			writeD(leader.getMapId());
			writeH(leader.getX());
			writeH(leader.getY());
			for (int i = 0, a = member.length; i < a; i++) {
				if (member[i].getId() == leader.getId() || member[i] == null)
					continue;
				nowhp = member[i].getCurrentHp();
				maxhp = member[i].getMaxHp();
				writeD(member[i].getId());
				writeS(member[i].getName());
				writeC((int) (nowhp / maxhp) * 100);
				writeD(member[i].getMapId());
				writeH(member[i].getX());
				writeH(member[i].getY());
			}
			writeC(0x00);
		}
	}

	/**
	 * メンバー
	 * 
	 * @param pc
	 */
	public void oldMember(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(S_PacketBox.PATRY_UPDATE_MEMBER);
		writeD(pc.getId());
		writeS(pc.getName());
		writeD(pc.getMapId());
		writeH(pc.getX());
		writeH(pc.getY());
	}

	/**
	 * パーティーリーダー更新
	 * 
	 * @param pc
	 */
	public void changeLeader(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(S_PacketBox.PATRY_SET_MASTER);
		writeD(pc.getId());
		writeH(0x0000);
	}

	/**
	 * メンバー更新
	 * 
	 * @param pc
	 */
	public void refreshParty(L1PcInstance pc) {
		L1PcInstance member[] = pc.getParty().getMembers();
		if (pc.getParty() == null) {
			return;
		} else {
			writeC(Opcodes.S_OPCODE_PACKETBOX);
			writeC(S_PacketBox.PATRY_MEMBERS);
			writeC(member.length);
			for (int i = 0, a = member.length; i < a; i++) {
				writeD(member[i].getId());
				writeD(member[i].getMapId());
				writeH(member[i].getX());
				writeH(member[i].getY());
			}
			writeC(0x00);
		}
	}
	//3.3c end

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}

		return _byte;
	}

	@Override
	public String getType() {
		return _S_Party;
	}

}