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
import jp.l1j.server.model.instance.L1PcInstance;
import static jp.l1j.server.model.skill.L1SkillId.STATUS_THIRD_SPEED;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket, S_OtherCharPacks

public class S_OtherCharPacks extends ServerBasePacket {

	private static final String S_OTHER_CHAR_PACKS = "[S] S_OtherCharPacks";
	private static Logger _log = Logger.getLogger(S_OtherCharPacks.class
			.getName());

	private static final int STATUS_POISON = 1;
	private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
	private static final int STATUS_FREEZE = 8;
	private static final int STATUS_BRAVE = 16;
	private static final int STATUS_ELFBRAVE = 32;
	private static final int STATUS_FASTMOVABLE = 64;
	private static final int STATUS_GHOST = 128;

	private byte[] _byte = null;

	public S_OtherCharPacks(L1PcInstance pc, boolean isFindInvis) {
		buildPacket(pc, isFindInvis);
	}

	public S_OtherCharPacks(L1PcInstance pc) {
		buildPacket(pc, false);
	}

	private void buildPacket(L1PcInstance pc, boolean isFindInvis) {
		int status = STATUS_PC;

		if (pc.getPoison() != null) { // 毒状態
			if (pc.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}
		if (pc.isInvisble() && !isFindInvis) {
			status |= STATUS_INVISIBLE;
		}
		if (pc.isBrave()) {
			status |= STATUS_BRAVE;
		}
		if (pc.isElfBrave()) {
			// エルヴンワッフルの場合は、STATUS_BRAVEとSTATUS_ELFBRAVEを立てる。
			// STATUS_ELFBRAVEのみでは効果が無い？
			status |= STATUS_BRAVE;
			status |= STATUS_ELFBRAVE;
		}
		if (pc.isFastMovable()) {
			status |= STATUS_FASTMOVABLE;
		}

		// int addbyte = 0;
		// int addbyte1 = 1;

		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(pc.getX());
		writeH(pc.getY());
		writeD(pc.getId());
		if (pc.isDead()) {
			writeH(pc.getTempCharGfxAtDead());
		} else {
			writeH(pc.getTempCharGfx());
		}
		if (pc.isDead()) {
			writeC(pc.getStatus());
		} else {
			writeC(pc.getCurrentWeapon());
		}
		writeC(pc.getHeading());
		// writeC(0); // makes char invis (0x01), cannot move. spells display
		writeC(pc.getLightSize());
		writeC(pc.getMoveSpeed());
		writeD(0x0000); // exp
		// writeC(0x00);
		writeH(pc.getLawful());
		writeS(pc.getName());
		writeS(pc.getTitle());
		writeC(status);
		writeD(pc.getClanId());
		writeS(pc.getClanName()); // クラン名
		writeS(null); // ペッホチング？
		writeC(0); // ？
		/*
		 * if(pc.is_isInParty()) // パーティー中 { writeC(100 * pc.getCurrentHp() /
		 * pc.getMaxHp()); } else { writeC(0xFF); }
		 */

		writeC(0xFF);
		if (pc.hasSkillEffect(STATUS_THIRD_SPEED)) {
			writeC(0x08); // 三段加速
		} else {
			writeC(0);
		}
		writeC(0); // PC = 0, Mon = Lv
		writeC(0); // ？
		writeC(0xFF);
		writeC(0xFF);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_OTHER_CHAR_PACKS;
	}

}