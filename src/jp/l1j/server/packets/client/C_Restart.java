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
import jp.l1j.server.datatables.ReturnLocationTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1CastleLocation;
import jp.l1j.server.model.L1Clan;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_CharVisualUpdate;
import jp.l1j.server.packets.server.S_MapID;
import jp.l1j.server.packets.server.S_OtherCharPacks;
import jp.l1j.server.packets.server.S_OwnCharPack;
import jp.l1j.server.packets.server.S_RemoveObject;
import jp.l1j.server.packets.server.S_Weather;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_Restart extends ClientBasePacket {

	private static Logger _log = Logger.getLogger(C_Restart.class.getName());

	private static final String C_RESTART = "[C] C_Restart";

	public C_Restart(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);
		L1PcInstance pc = clientthread.getActiveChar();

		int[] loc;
		int castle_id = 0;
		
		if (pc.getClanId() != 0) { // クラン所属
			L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
			if (clan != null) {
				castle_id = clan.getCastleId();
			}
		}
		
		if (pc.getHellTime() > 0) {
			loc = new int[3];
			loc[0] = 32701;
			loc[1] = 32777;
			loc[2] = 666;
		} else if (castle_id > 0 && L1CastleLocation.checkInWarArea(castle_id, pc)) {
			// 攻城戦時、城主血盟に所属するキャラクターは内城からリスタート
			loc = L1CastleLocation.getCastleLoc(castle_id);
		} else {
			loc = ReturnLocationTable.getReturnLocation(pc, true);
		}

		pc.removeAllKnownObjects();
		pc.broadcastPacket(new S_RemoveObject(pc));

		pc.setCurrentHp(pc.getLevel());
		pc.setFood(40);
		pc.setDead(false);
		pc.setStatus(0);
		L1World.getInstance().moveVisibleObject(pc, loc[2]);
		pc.setX(loc[0]);
		pc.setY(loc[1]);
		pc.setMap((short) loc[2]);
		pc.sendPackets(new S_MapID(pc.getMap().getBaseMapId(), pc.getMap().isUnderwater()));
		pc.broadcastPacket(new S_OtherCharPacks(pc));
		pc.sendPackets(new S_OwnCharPack(pc));
		pc.sendPackets(new S_CharVisualUpdate(pc));
		pc.startHpRegeneration();
		pc.startMpRegeneration();
		pc.sendPackets(new S_Weather(L1World.getInstance().getWeather()));
		if (pc.getHellTime() > 0) {
			pc.beginHell(false);
		}
		pc.stopPcDeleteTimer();
	}

	@Override
	public String getType() {
		return C_RESTART;
	}
}