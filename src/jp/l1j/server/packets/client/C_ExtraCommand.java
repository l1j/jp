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

package jp.l1j.server.packets.client;

import java.util.logging.Logger;
import jp.l1j.server.ClientThread;
import static jp.l1j.server.codes.ActionCodes.ACTION_Cheer;
import static jp.l1j.server.codes.ActionCodes.ACTION_Think;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_DoActionGFX;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_ExtraCommand extends ClientBasePacket {
	private static final String C_EXTRA_COMMAND = "[C] C_ExtraCommand";
	private static Logger _log = Logger.getLogger(C_ExtraCommand.class
			.getName());

	public C_ExtraCommand(byte abyte0[], ClientThread client)
			throws Exception {
		super(abyte0);
		int actionId = readC();
		L1PcInstance pc = client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
		if (pc.isInvisble()) { // インビジビリティ、ブラインドハイディング中
			return;
		}
		if (pc.isTeleport()) { // テレポート処理中
			return;
		}

		//XXX
		if(ACTION_Cheer>=actionId&&actionId>=ACTION_Think){
			for(L1Object obj:L1World.getInstance().getVisibleObjects(pc)){
				if(obj instanceof L1NpcInstance ){
					((L1NpcInstance)obj).receiveSocialAction(pc, actionId);
				}
			}
		}

		if (pc.hasSkillEffect(SHAPE_CHANGE)) { // 念の為、変身中は他プレイヤーに送信しない
			int gfxId = pc.getTempCharGfx();
			if (gfxId != 6080 && gfxId != 6094) { // 騎馬用ヘルム変身は例外
				return;
			}
		}
		S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), actionId);
		pc.broadcastPacket(gfx); // 周りのプレイヤーに送信
	}

	@Override
	public String getType() {
		return C_EXTRA_COMMAND;
	}
}
