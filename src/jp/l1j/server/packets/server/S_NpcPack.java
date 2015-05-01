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

import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.datatables.NpcTalkDataTable;
import jp.l1j.server.model.instance.L1FieldObjectInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.L1NpcTalkData;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_NpcPack extends ServerBasePacket {

	private static final String S_NPC_PACK = "[S] S_NpcPack";
	private static Logger _log = Logger.getLogger(S_NpcPack.class.getName());

	private static final int STATUS_POISON = 1;
	private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
	private static final int STATUS_FREEZE = 8;
	private static final int STATUS_BRAVE = 16;
	private static final int STATUS_ELFBRAVE = 32;
	private static final int STATUS_FASTMOVABLE = 64;
	private static final int STATUS_GHOST = 128;

	private byte[] _byte = null;

	public S_NpcPack(L1NpcInstance npc) {
		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(npc.getX());
		writeH(npc.getY());
		writeD(npc.getId());
		if (npc.getTempCharGfx() == 0) {
			writeH(npc.getGfxId());
		} else {
			writeH(npc.getTempCharGfx());
		}
		if (npc.getNpcTemplate().isDoppel() && npc.getGfxId() != 31) { // スライムの姿をしていなければドッペル
			writeC(4); // 長剣
		} else {
			writeC(npc.getStatus());
		}
		writeC(npc.getHeading());
		writeC(npc.getLightSize());
		writeC(npc.getMoveSpeed());
		writeD(npc.getExp());
		writeH(npc.getTempLawful());
		writeS(npc.getNameId());
		if (npc instanceof L1FieldObjectInstance) { // SICの壁字、看板など
			L1NpcTalkData talkdata = NpcTalkDataTable.getInstance()
					.getTemplate(npc.getNpcTemplate().getNpcId());
			if (talkdata != null) {
				writeS(talkdata.getNormalAction()); // タイトルがHTML名として解釈される
			} else {
				writeS(null);
			}
		} else {
			writeS(npc.getTitle());
		}

		/**
		 * ｼｼﾆﾃ - 0:mob,item(atk pointer), 1:poisoned(), 2:invisable(), 4:pc,
		 * 8:cursed(), 16:brave(), 32:??, 64:??(??), 128:invisable but name
		 */
		int status = 0;
		if (npc.getPoison() != null) { // 毒状態
			if (npc.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}
		if (npc.getNpcTemplate().isDoppel()) {
			// PC属性だとエヴァの祝福を渡せないためWIZクエストのドッペルは例外
			if (npc.getNpcTemplate().getNpcId() != 81069) {
				status |= STATUS_PC;
			}
		}
		writeC(status);

		writeD(0); // 0以外にするとC_27が飛ぶ
		writeS(null);
		writeS(null); // マスター名？
		writeC(0);
		writeC(0xFF); // HP
		writeC(0);
		writeC(npc.getLevel());
		writeC(0);
		writeC(0xFF);
		writeC(0xFF);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}

		return _byte;
	}

	@Override
	public String getType() {
		return S_NPC_PACK;
	}

}
