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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Character;
import static jp.l1j.server.model.skill.L1SkillId.*;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_UseAttackSkill extends ServerBasePacket {

	private static final String S_USE_ATTACK_SKILL = "[S] S_UseAttackSkill";
	private static Logger _log = Logger.getLogger(S_UseAttackSkill.class
			.getName());

	private static AtomicInteger _sequentialNumber = new AtomicInteger(0);

	private byte[] _byte = null;

	public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx,
			int x, int y, int actionId) {
		buildPacket(cha, targetobj, spellgfx, x, y, actionId, 6, true);
	}

	public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx,
			int x, int y, int actionId, boolean motion) {
		buildPacket(cha, targetobj, spellgfx, x, y, actionId, 0, motion);
	}

	public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx,
			int x, int y, int actionId, int isHit) {
		buildPacket(cha, targetobj, spellgfx, x, y, actionId, isHit, true);
	}

	private void buildPacket(L1Character cha, int targetobj, int spellgfx,
			int x, int y, int actionId, int isHit, boolean withCastMotion) {
		if (cha instanceof L1PcInstance) {
			// シャドウ系変身中に攻撃魔法を使用するとクライアントが落ちるため暫定対応
			if (cha.hasSkillEffect(SHAPE_CHANGE)
					&& actionId == ActionCodes.ACTION_SkillAttack) {
				int tempchargfx = cha.getTempCharGfx();
				if (tempchargfx == 5727 || tempchargfx == 5730) {
					actionId = ActionCodes.ACTION_SkillBuff;
				} else if (tempchargfx == 5733 || tempchargfx == 5736) {
					// 補助魔法モーションにすると攻撃魔法のグラフィックと
					// 対象へのダメージモーションが発生しなくなるため
					// 攻撃モーションで代用
					actionId = ActionCodes.ACTION_Attack;
				}
			}
		}
		// 火の精の主がデフォルトだと攻撃魔法のグラフィックが発生しないので強制置き換え
		// どこか別で管理した方が良い？
		if (cha.getTempCharGfx() == 4013) {
			actionId = ActionCodes.ACTION_Attack;
		}

		int newheading = calcheading(cha.getX(), cha.getY(), x, y);
		cha.setHeading(newheading);
		writeC(Opcodes.S_OPCODE_ATTACKPACKET);
		writeC(actionId);
		writeD(withCastMotion ? cha.getId() : 0);
		writeD(targetobj);
		writeH(isHit);
		writeC(newheading);
		writeD(_sequentialNumber.incrementAndGet()); // 番号がダブらないように送る。
		writeH(spellgfx);
		writeC(6); // use_type 0:弓箭 6:遠距離魔法 8:遠距離範圍魔法
		writeH(cha.getX());
		writeH(cha.getY());
		writeH(x);
		writeH(y);
		writeC(0);
		writeC(0);
		writeC(0);		
		writeC(0);
		writeC(0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		} else {
			int seq = 0;
			synchronized (this) {
				seq = _sequentialNumber.incrementAndGet();
			}
			_byte[13] = (byte) (seq & 0xff);
			_byte[14] = (byte) (seq >> 8 & 0xff);
			_byte[15] = (byte) (seq >> 16 & 0xff);
			_byte[16] = (byte) (seq >> 24 & 0xff);
		}

		return _byte;
	}

	private static int calcheading(int myx, int myy, int tx, int ty) {
		int newheading = 0;
		if (tx > myx && ty > myy) {
			newheading = 3;
		}
		if (tx < myx && ty < myy) {
			newheading = 7;
		}
		if (tx > myx && ty == myy) {
			newheading = 2;
		}
		if (tx < myx && ty == myy) {
			newheading = 6;
		}
		if (tx == myx && ty < myy) {
			newheading = 0;
		}
		if (tx == myx && ty > myy) {
			newheading = 4;
		}
		if (tx < myx && ty > myy) {
			newheading = 5;
		}
		if (tx > myx && ty < myy) {
			newheading = 1;
		}
		return newheading;
	}

	@Override
	public String getType() {
		return S_USE_ATTACK_SKILL;
	}

}