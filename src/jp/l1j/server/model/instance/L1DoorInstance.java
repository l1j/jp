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
package jp.l1j.server.model.instance;

import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.model.L1Attack;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_DoActionGFX;
import jp.l1j.server.packets.server.S_Door;
import jp.l1j.server.packets.server.S_DoorPack;
import jp.l1j.server.packets.server.S_RemoveObject;
import jp.l1j.server.templates.L1DoorGfx;
import jp.l1j.server.templates.L1Npc;

public class L1DoorInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;
	private static final int DOOR_NPC_ID = 81158;

	public L1DoorInstance(L1Npc template) {
		super(template);
	}

	public L1DoorInstance(int doorId, L1DoorGfx gfx, L1Location loc,
			int hp, int keeper, boolean DoorOpen){
		super(NpcTable.getInstance().getTemplate(DOOR_NPC_ID));
		setDoorId(doorId);
		setMaxHp(hp);
		setCurrentHp(hp);
		setGfxId(gfx.getGfxId());
		setLocation(loc);
		setHomeX(loc.getX());
		setHomeY(loc.getY());
		setDirection(gfx.getDirection());
		int baseLoc = gfx.getDirection() == 0 ? loc.getX() : loc.getY();
		setLeftEdgeLocation(baseLoc + gfx.getLeftEdgeOffset());
		setRightEdgeLocation(baseLoc + gfx.getRightEdgeOffset());
		setKeeperId(keeper);
		if (DoorOpen) {
			open();
		}
	}

	@Override
	public void onAction(L1PcInstance pc) {
		if (getMaxHp() == 0) { // 破壊不可能なドアは対象外
			return;
		}

		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.addPcPoisonAttack(pc, this);
				attack.addChaserAttack();
				attack.addEvilAttack();
			}
			attack.action();
			attack.commit();
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_DoorPack(this));
		sendDoorPacket(perceivedFrom);
	}

	@Override
	public void deleteMe() {
		setDead(true);
		sendDoorPacket(null);

		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		removeAllKnownObjects();
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		if (getMaxHp() == 0) { // 破壊不可能なドアは対象外
			return;
		}
		if (getCurrentHp() <= 0 || isDead()) {
			return;
		}

		int newHp = getCurrentHp() - damage;
		if (newHp <= 0 && !isDead()) {
			die();
			return;
		}

		setCurrentHpDirect(newHp);
		updateStatus();
	}

	private void updateStatus() {
		int newStatus = 0;
		if ((getMaxHp() * 1 / 6) > getCurrentHp()) {
			newStatus = ActionCodes.ACTION_DoorAction5;
		} else if ((getMaxHp() * 2 / 6) > getCurrentHp()) {
			newStatus = ActionCodes.ACTION_DoorAction4;
		} else if ((getMaxHp() * 3 / 6) > getCurrentHp()) {
			newStatus = ActionCodes.ACTION_DoorAction3;
		} else if ((getMaxHp() * 4 / 6) > getCurrentHp()) {
			newStatus = ActionCodes.ACTION_DoorAction2;
		} else if ((getMaxHp() * 5 / 6) > getCurrentHp()) {
			newStatus = ActionCodes.ACTION_DoorAction1;
		}
		if (getStatus() == newStatus) {
			return;
		}
		setStatus(newStatus);
		broadcastPacket(new S_DoActionGFX(getId(), newStatus));
	}

	@Override
	public void setCurrentHp(int i) {
		int currentHp = i;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
		setCurrentHpDirect(currentHp);
	}

	private void die() {
		setCurrentHpDirect(0);
		setDead(true);
		setStatus(ActionCodes.ACTION_DoorDie);

		getMap().setPassable(getLocation(), true);

		broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_DoorDie));
		sendDoorPacket(null);
	}

	public void sendDoorPacket(L1PcInstance pc) {
		int entranceX = getEntranceX();
		int entranceY = getEntranceY();
		int leftEdgeLocation = getLeftEdgeLocation();
		int rightEdgeLocation = getRightEdgeLocation();

		int size = rightEdgeLocation - leftEdgeLocation;
		if (size == 0) { // 1マス分の幅のドア
			sendPacket(pc, entranceX, entranceY);
		} else { // 2マス分以上の幅があるドア
			if (getDirection() == 0) { // ／向き
				for (int x = leftEdgeLocation; x <= rightEdgeLocation; x++) {
					sendPacket(pc, x, entranceY);
				}
			} else { // ＼向き
				for (int y = leftEdgeLocation; y <= rightEdgeLocation; y++) {
					sendPacket(pc, entranceX, y);
				}
			}
		}
	}

	private boolean isPassable() {
		return isDead() || getOpenStatus() == ActionCodes.ACTION_Open;
	}

	private void sendPacket(L1PcInstance pc, int x, int y) {
		S_Door packet = new S_Door(x, y, getDirection(), isPassable());
		if (pc != null) { // onPerceive()経由の場合
			// 開いている場合は通行不可パケット送信不要
			if (getOpenStatus() == ActionCodes.ACTION_Close) {
				pc.sendPackets(packet);
			}
		} else {
			broadcastPacket(packet);
		}
	}

	public void open() {
		if (isDead() || getOpenStatus() == ActionCodes.ACTION_Open) {
			return;
		}

		setOpenStatus(ActionCodes.ACTION_Open);
		broadcastPacket(new S_DoorPack(this));
		broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Open));
		sendDoorPacket(null);
	}

	public void close() {
		if (isDead() || getOpenStatus() == ActionCodes.ACTION_Close) {
			return;
		}

		setOpenStatus(ActionCodes.ACTION_Close);
		broadcastPacket(new S_DoorPack(this));
		broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Close));
		sendDoorPacket(null);
	}

	public void repairGate() {
		if (getMaxHp() <= 1) {
			return;
		}

		setDead(false);
		setCurrentHp(getMaxHp());
		setStatus(0);
		setOpenStatus(ActionCodes.ACTION_Open);
		close();
	}

	private int _doorId = 0;

	public int getDoorId() {
		return _doorId;
	}

	public void setDoorId(int i) {
		_doorId = i;
	}

	private int _direction = 0; // ドアの向き

	public int getDirection() {
		return _direction;
	}

	public void setDirection(int i) {
		if (i != 0 && i != 1) {
			throw new IllegalArgumentException();
		}
		_direction = i;
	}

	public int getEntranceX() {
		int entranceX = 0;
		if (getDirection() == 0) { // ／向き
			entranceX = getX();
		} else { // ＼向き
			entranceX = getX() - 1;
		}
		return entranceX;
	}

	public int getEntranceY() {
		int entranceY = 0;
		if (getDirection() == 0) { // ／向き
			entranceY = getY() + 1;
		} else { // ＼向き
			entranceY = getY();
		}
		return entranceY;
	}

	private int _leftEdgeLocation = 0; // ドアの左端の座標(ドアの向きからX軸orY軸を決定する)

	public int getLeftEdgeLocation() {
		return _leftEdgeLocation;
	}

	public void setLeftEdgeLocation(int i) {
		_leftEdgeLocation = i;
	}

	private int _rightEdgeLocation = 0; // ドアの右端の座標(ドアの向きからX軸orY軸を決定する)

	public int getRightEdgeLocation() {
		return _rightEdgeLocation;
	}

	public void setRightEdgeLocation(int i) {
		_rightEdgeLocation = i;
	}

	private int _openStatus = ActionCodes.ACTION_Close;

	public int getOpenStatus() {
		return _openStatus;
	}

	private void setOpenStatus(int newStatus) {
		if (newStatus != ActionCodes.ACTION_Open
				&& newStatus != ActionCodes.ACTION_Close) {
			throw new IllegalArgumentException();
		}
		_openStatus = newStatus;
	}

	private int _keeperId = 0;

	public int getKeeperId() {
		return _keeperId;
	}

	public void setKeeperId(int i) {
		_keeperId = i;
	}
}
