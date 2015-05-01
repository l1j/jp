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

import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_MessageYN;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1Ress implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Ress.class.getName());

	private L1Ress() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Ress();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			int objid = pc.getId();
			pc.sendPackets(new S_SkillSound(objid, 759));
			pc.broadcastPacket(new S_SkillSound(objid, 759));
			pc.setCurrentHp(pc.getMaxHp());
			pc.setCurrentMp(pc.getMaxMp());
			for (L1PcInstance tg : L1World.getInstance().getVisiblePlayer(pc)) {
				if (tg.getCurrentHp() == 0 && tg.isDead()) {
					tg.sendPackets(new S_SystemMessage(I18N_RESUSCITATED_BY_GM));
					// GMによって蘇生されました。
					tg.broadcastPacket(new S_SkillSound(tg.getId(), 3944));
					tg.sendPackets(new S_SkillSound(tg.getId(), 3944));
					// 祝福された 復活スクロールと同じ効果
					tg.setTempID(objid);
					tg.sendPackets(new S_MessageYN(322, "")); // また復活したいですか？（Y/N）
				} else {
					tg.sendPackets(new S_SystemMessage(I18N_RECOVERED_BY_GM));
					// GMによって回復されました。
					tg.broadcastPacket(new S_SkillSound(tg.getId(), 832));
					tg.sendPackets(new S_SkillSound(tg.getId(), 832));
					tg.setCurrentHp(tg.getMaxHp());
					tg.setCurrentMp(tg.getMaxMp());
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_ERROR, cmdName)));
			// .%s コマンドエラー
		}
	}
}
