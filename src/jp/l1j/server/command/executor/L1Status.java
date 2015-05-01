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

import java.util.StringTokenizer;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_Lawful;
import jp.l1j.server.packets.server.S_OwnCharStatus;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1Status implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Status.class.getName());

	private L1Status() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Status();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String char_name = st.nextToken();
			String param = st.nextToken();
			int value = Integer.parseInt(st.nextToken());

			L1PcInstance target = null;
			if (char_name.equalsIgnoreCase("me")) {
				target = pc;
			} else {
				target = L1World.getInstance().getPlayer(char_name);
			}

			if (target == null) {
				pc.sendPackets(new S_ServerMessage(73, char_name)); // \f1%0はゲームをしていません。
				return;
			}

			// -- not use DB --
			if (param.equalsIgnoreCase("AC")) {
				target.addAc((byte) (value - target.getAc()));
			} else if (param.equalsIgnoreCase("MR")) {
				target.addMr((short) (value - target.getMr()));
			} else if (param.equalsIgnoreCase("HIT")) {
				target.addHitup((short) (value - target.getHitup()));
			} else if (param.equalsIgnoreCase("DMG")) {
				target.addDmgup((short) (value - target.getDmgup()));
				// -- use DB --
			} else {
				if (param.equalsIgnoreCase("HP")) {
					target.addBaseMaxHp((short) (value - target.getBaseMaxHp()));
					target.setCurrentHpDirect(target.getMaxHp());
				} else if (param.equalsIgnoreCase("MP")) {
					target.addBaseMaxMp((short) (value - target.getBaseMaxMp()));
					target.setCurrentMpDirect(target.getMaxMp());
				} else if (param.equalsIgnoreCase("LAWFUL")) {
					target.setLawful(value);
					S_Lawful s_lawful = new S_Lawful(target.getId(), target.getLawful());
					target.sendPackets(s_lawful);
					target.broadcastPacket(s_lawful);
				} else if (param.equalsIgnoreCase("KARMA")) {
					target.setKarma(value);
				} else if (param.equalsIgnoreCase("GM")) {
					if (value > 200) {
						value = 200;
					}
					target.setAccessLevel((short) value);
					target.sendPackets(new S_SystemMessage(I18N_PROMOTED_TO_GM));
					// GMに昇格しました。リスタートしてください。
				} else if (param.equalsIgnoreCase("STR")) {
					target.addBaseStr((byte) (value - target.getBaseStr()));
				} else if (param.equalsIgnoreCase("CON")) {
					target.addBaseCon((byte) (value - target.getBaseCon()));
				} else if (param.equalsIgnoreCase("DEX")) {
					target.addBaseDex((byte) (value - target.getBaseDex()));
				} else if (param.equalsIgnoreCase("INT")) {
					target.addBaseInt((byte) (value - target.getBaseInt()));
				} else if (param.equalsIgnoreCase("WIS")) {
					target.addBaseWis((byte) (value - target.getBaseWis()));
				} else if (param.equalsIgnoreCase("CHA")) {
					target.addBaseCha((byte) (value - target.getBaseCha()));
				} else {
					pc.sendPackets(new S_SystemMessage(String.format(I18N_IS_UNKNOWN_PARAM, param)));
					// %s は不明なパラメータです。
					return;
				}
				target.save(); // DBにキャラクター情報を書き込む
			}
			target.sendPackets(new S_OwnCharStatus(target));
			pc.sendPackets(new S_SystemMessage(String.format(I18N_CHANGED_THE_STATUS,
					target.getName(), param, value)));
			// %s の %s を %d に変更しました。
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_3,
					cmdName, I18N_CHAR_NAME+"|me", I18N_STATUS, I18N_VALUE)));
			// .%s %s %s %s の形式で入力してください。
		}
	}
}
