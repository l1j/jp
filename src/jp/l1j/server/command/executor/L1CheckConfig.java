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
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_OutputRawString;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1CheckConfig implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Describe.class.getName());

	private L1CheckConfig() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1CheckConfig();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringBuilder msg = new StringBuilder();
			msg.append(String.format(I18N_EXP, Config.RATE_XP) + " / ");
			msg.append(String.format(I18N_LAWFUL, Config.RATE_LA) + " / ");
			msg.append(String.format(I18N_KARMA, Config.RATE_KARMA) + " / ");
			msg.append(String.format(I18N_ITEM_DROP, Config.RATE_DROP_ITEMS) + " / ");
			msg.append(String.format(I18N_ADENA_DROP, Config.RATE_DROP_ADENA) + " / ");
			msg.append(String.format(I18N_ENCHANT_WEAPON, Config.ENCHANT_CHANCE_WEAPON) + " / ");
			msg.append(String.format(I18N_ENCHANT_ARMOR, Config.ENCHANT_CHANCE_ARMOR) + " / ");
			msg.append(String.format(I18N_ENCHANT_ATTRIBUTE, Config.ATTR_ENCHANT_CHANCE) + " / ");
			msg.append(String.format(I18N_ENCHANT_ACCESSORY, Config.ENCHANT_CHANCE_ACCESSORY) + " / ");
			msg.append(String.format(I18N_ENCHANT_DOLL, Config.ENCHANT_CHANCE_DOLL) + " / ");
			msg.append(String.format(I18N_WEIGHT_REDUCTION, Config.RATE_WEIGHT_LIMIT) + " / ");
			msg.append(String.format(I18N_MAX_USERS, Config.MAX_ONLINE_USERS) + " / ");
			msg.append(Config.ALT_NONPVP ? I18N_PVP : I18N_NON_PVP);
			pc.sendPackets(new S_OutputRawString(pc.getId(), I18N_SERVER_SETTINGS, msg.toString()));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_ERROR, cmdName)));
			// .%s コマンドエラー
		}
	}
}
