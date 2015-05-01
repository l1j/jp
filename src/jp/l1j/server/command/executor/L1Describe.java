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
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_OutputRawString;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1Describe implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Describe.class.getName());

	private L1Describe() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Describe();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringBuilder msg = new StringBuilder();
			msg.append(String.format(I18N_DESC_DMG, pc.getDmgup()));
			msg.append(String.format(I18N_DESC_HIT, pc.getHitup()));
			msg.append(String.format(I18N_DESC_BOW_DMG, pc.getBowDmgup()));
			msg.append(String.format(I18N_DESC_BOW_HIT, pc.getBowHitup()));
			msg.append(String.format(I18N_DESC_MR, pc.getMr()));
			msg.append(String.format(I18N_DESC_HPR, pc.getHpr()));
			msg.append(String.format(I18N_DESC_MPR, pc.getMpr()));
			msg.append(String.format(I18N_DESC_RESIST_FREEZE, pc.getResistFreeze()));
			msg.append(String.format(I18N_DESC_RESIST_STUN, pc.getResistStun()));
			msg.append(String.format(I18N_DESC_RESIST_STONE, pc.getResistStone()));
			msg.append(String.format(I18N_DESC_RESIST_SLEEP, pc.getResistSleep()));
			msg.append(String.format(I18N_DESC_RESIST_HOLD, pc.getResistHold()));
			msg.append(String.format(I18N_DESC_RESIST_BLIND, pc.getResistBlind()));
			msg.append(String.format(I18N_DESC_KARMA, pc.getKarma()));
			msg.append(String.format(I18N_DESC_INVENTORY_SIZE, pc.getInventory().getSize()));
			msg.append(String.format(I18N_DESC_EXP_BONUS, pc.getExpBonusPct()));
			pc.sendPackets(new S_OutputRawString(pc.getId(), pc.getName(), msg.toString()));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_ERROR, cmdName)));
			// .%s コマンドエラー
		}
	}
}
