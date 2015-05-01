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

package jp.l1j.server.utils;

import java.util.List;
import java.util.logging.Logger;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_ServerMessage;

// Referenced classes of package jp.l1j.server.utils:
// FaceToFace

public class FaceToFace {

	private static Logger _log = Logger.getLogger(FaceToFace.class.getName());

	private FaceToFace() {
	}

	public static L1PcInstance faceToFace(L1PcInstance pc) {
		int pcX = pc.getX();
		int pcY = pc.getY();
		int pcHeading = pc.getHeading();
		List<L1PcInstance> players = L1World.getInstance().getVisiblePlayer(pc,
				1);

		if (players.size() == 0) { // 1セル以内にPCが居ない場合
			pc.sendPackets(new S_ServerMessage(93)); // \f1そこには誰もいません。
			return null;
		}
		for (L1PcInstance target : players) {
			int targetX = target.getX();
			int targetY = target.getY();
			int targetHeading = target.getHeading();
			if (pcHeading == 0 && pcX == targetX && pcY == (targetY + 1)) {
				if (targetHeading == 4) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0があなたを見ていません。
					return null;
				}
			} else if (pcHeading == 1 && pcX == (targetX - 1)
					&& pcY == (targetY + 1)) {
				if (targetHeading == 5) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0があなたを見ていません。
					return null;
				}
			} else if (pcHeading == 2 && pcX == (targetX - 1) && pcY == targetY) {
				if (targetHeading == 6) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0があなたを見ていません。
					return null;
				}
			} else if (pcHeading == 3 && pcX == (targetX - 1)
					&& pcY == (targetY - 1)) {
				if (targetHeading == 7) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0があなたを見ていません。
					return null;
				}
			} else if (pcHeading == 4 && pcX == targetX && pcY == (targetY - 1)) {
				if (targetHeading == 0) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0があなたを見ていません。
					return null;
				}
			} else if (pcHeading == 5 && pcX == (targetX + 1)
					&& pcY == (targetY - 1)) {
				if (targetHeading == 1) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0があなたを見ていません。
					return null;
				}
			} else if (pcHeading == 6 && pcX == (targetX + 1) && pcY == targetY) {
				if (targetHeading == 2) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0があなたを見ていません。
					return null;
				}
			} else if (pcHeading == 7 && pcX == (targetX + 1)
					&& pcY == (targetY + 1)) {
				if (targetHeading == 3) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0があなたを見ていません。
					return null;
				}
			}
		}
		pc.sendPackets(new S_ServerMessage(93)); // \f1そこには誰もいません。
		return null;
	}
}
