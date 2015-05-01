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

import java.lang.reflect.Constructor;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SystemMessage;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.utils.IdFactory;

public class L1GfxId implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1GfxId.class.getName());

	private L1GfxId() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GfxId();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int gfxid = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);
			for (int i = 0; i < count; i++) {
				L1Npc l1npc = NpcTable.getInstance().getTemplate(45001);
				if (l1npc != null) {
					String s = l1npc.getImpl();
					Constructor constructor = Class.forName("jp.l1j.server.model.instance."
							+ s + "Instance").getConstructors()[0];
					Object aobj[] = { l1npc };
					L1NpcInstance npc = (L1NpcInstance) constructor.newInstance(aobj);
					npc.setId(IdFactory.getInstance().nextId());
					npc.setGfxId(gfxid + i);
					npc.setTempCharGfx(0);
					npc.setNameId("");
					npc.setMap(pc.getMapId());
					npc.setX(pc.getX() + i * 2);
					npc.setY(pc.getY() + i * 2);
					npc.setHomeX(npc.getX());
					npc.setHomeY(npc.getY());
					npc.setHeading(4);

					L1World.getInstance().storeObject(npc);
					L1World.getInstance().addVisibleObject(npc);
				}
			}
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_2,
					cmdName, I18N_GFX_ID, I18N_AMOUNT)));
			
			// .%s %s %s の形式で入力してください。
		}
	}
}
