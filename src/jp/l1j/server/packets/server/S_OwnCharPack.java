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

package jp.l1j.server.packets.server;

import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1PcInstance;
import static jp.l1j.server.model.skill.L1SkillId.STATUS_THIRD_SPEED;

// TODO コメント翻訳できる方お願いします。

public class S_OwnCharPack extends ServerBasePacket {

	private static final String S_OWN_CHAR_PACK = "[S] S_OwnCharPack";

	private static final int STATUS_POISON = 1;
	private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
	private static final int STATUS_FREEZE = 8;
	private static final int STATUS_BRAVE = 16;
	private static final int STATUS_ELFBRAVE = 32;
	private static final int STATUS_FASTMOVABLE = 64;
	private static final int STATUS_GHOST = 128;

	private byte[] _byte = null;

	public S_OwnCharPack(L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(L1PcInstance pc) {
		int status = STATUS_PC;

		// グール毒みたいな緑の毒
		// if (pc.isPoison()) {
		// status |= STATUS_POISON;
		// }

		if (pc.isInvisble() || pc.isGmInvis()) {
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
		if (pc.isGhost()) {
			status |= STATUS_GHOST;
		}

		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(pc.getX());
		writeH(pc.getY());
		writeD(pc.getId());
		writeH(pc.isDead() ? pc.getTempCharGfxAtDead() : pc.getTempCharGfx());
		writeC(pc.isDead() ? pc.getStatus() : pc.getCurrentWeapon());
		writeC(pc.getHeading());
		writeC(pc.getOwnLightSize());
		writeC(pc.getMoveSpeed());
		//writeD(1);
		writeD(pc.getExp());
		writeH(pc.getLawful());
		writeS(pc.getName());
		writeS(pc.getTitle());
		writeC(status);
		writeD(pc.getClanId());
		writeS(pc.getClanName());
		writeS(null); // ペッホチング？

		writeC(pc.getClanRank() > 0 ? pc.getClanRank() << 4 : 0xb0);

		if (pc.isInParty()) // パーティー中
		{
			writeC(100 * pc.getCurrentHp() / pc.getMaxHp());
		} else {
			writeC(0xFF);
		}
		if (pc.hasSkillEffect(STATUS_THIRD_SPEED)) {
			writeC(0x08); // 三段加速
		} else {
			writeC(0);
		}
		writeC(0); // 海底波浪模糊程度 (官方應該已經棄用這功能了, 這數值只對自身有效果)
		writeC(0); // 物件的等級

		//writeS(null); // 個人商店名稱1與名稱2:
		// 這數值只有在開啟個人商店的時候才會使用！
		// 使用方法 writeS("顯示商店名稱1" + '\255' + "顯示商店名稱2"); ('\255'這是用區分的字元)
		// 或 writeS("顯示商店名稱1" + '\255' + "");
		// 或 writeS("" + \255 + "顯示商店名稱2");
		writeH(0xFF);

		//writeH(0xFFFF);
		writeH(0xFF);
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
		return S_OWN_CHAR_PACK;
	}
}