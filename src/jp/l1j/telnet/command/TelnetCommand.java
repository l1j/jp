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

package jp.l1j.telnet.command;

import java.util.StringTokenizer;
import jp.l1j.server.GameServer;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.datatables.ChatLogTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_ChatPacket;
import jp.l1j.server.storage.mysql.MySqlCharacterStorage;
import jp.l1j.server.utils.IntRange;
import static jp.l1j.telnet.command.TelnetCommandResult.*;

public interface TelnetCommand {
	TelnetCommandResult execute(String args);
}

class EchoCommand implements TelnetCommand {
	@Override
	public TelnetCommandResult execute(String args) {
		return new TelnetCommandResult(CMD_OK, args);
	}
}

class PlayerIdCommand implements TelnetCommand {
	@Override
	public TelnetCommandResult execute(String args) {
		L1PcInstance pc = L1World.getInstance().getPlayer(args);
		String result = pc == null ? "0" : String.valueOf(pc.getId());
		return new TelnetCommandResult(CMD_OK, result);
	}
}

class CharStatusCommand implements TelnetCommand {
	@Override
	public TelnetCommandResult execute(String args) {
		int id = Integer.valueOf(args);
		L1Object obj = L1World.getInstance().findObject(id);
		if (obj == null) {
			return new TelnetCommandResult(CMD_INTERNAL_ERROR, "ObjectId " + id
					+ " not found");
		}
		if (!(obj instanceof L1Character)) {
			return new TelnetCommandResult(CMD_INTERNAL_ERROR, "ObjectId " + id
					+ " is not a character");
		}
		L1Character cha = (L1Character) obj;
		StringBuilder result = new StringBuilder();
		result.append("Name: " + cha.getName() + "\r\n");
		result.append("Level: " + cha.getLevel() + "\r\n");
		result.append("MaxHp: " + cha.getMaxHp() + "\r\n");
		result.append("CurrentHp: " + cha.getCurrentHp() + "\r\n");
		result.append("MaxMp: " + cha.getMaxMp() + "\r\n");
		result.append("CurrentMp: " + cha.getCurrentMp() + "\r\n");
		return new TelnetCommandResult(CMD_OK, result.toString());
	}
}

class GlobalChatCommand implements TelnetCommand {
	@Override
	public TelnetCommandResult execute(String args) {
		StringTokenizer tok = new StringTokenizer(args, " ");
		String name = tok.nextToken();
		String text = args.substring(name.length() + 1);
		L1PcInstance pc = new MySqlCharacterStorage().loadCharacter(name);
		if (pc == null) {
			return new TelnetCommandResult(CMD_INTERNAL_ERROR, "キャラクターが存在しません。");
		}
		pc.getLocation().set(-1, -1, 0);
		ChatLogTable.getInstance().storeChat(pc, null, text, 3);

		L1World.getInstance().broadcastPacketToAll(
				new S_ChatPacket(pc, text, Opcodes.S_OPCODE_GLOBALCHAT, 3));
		return new TelnetCommandResult(CMD_OK, "");
	}
}

class ShutDownCommand implements TelnetCommand {
	@Override
	public TelnetCommandResult execute(String args) {
		int sec = args.isEmpty() ? 0 : Integer.parseInt(args);
		sec = IntRange.ensure(sec, 30, Integer.MAX_VALUE);

		GameServer.getInstance().shutdownWithCountdown(sec);
		return new TelnetCommandResult(CMD_OK, "");
	}
}
