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

package jp.l1j.server.model.map;

import jp.l1j.server.codes.ActionCodes;
import jp.l1j.server.datatables.DoorTable;
import jp.l1j.server.model.Heading;
import jp.l1j.server.model.instance.L1DoorInstance;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.types.Point;
import jp.l1j.server.utils.BinaryUtils;

public class L1V1Map extends L1Map {
	private int _mapId;

	private int _baseMapId;

	private int _worldTopLeftX;

	private int _worldTopLeftY;

	private int _worldBottomRightX;

	private int _worldBottomRightY;

	private byte _map[][];

	private String _locationname;// TODO マップ名称検索用

	private boolean _isUnderwater;

	private boolean _isMarkable;

	private boolean _isTeleportable;

	private boolean _isEscapable;

	private boolean _isUseResurrection;

	private boolean _isUsePainwand;

	private boolean _isEnabledDeathPenalty;

	private boolean _isTakePets;

	private boolean _isRecallPets;

	private boolean _isUsableItem;

	private boolean _isUsableSkill;

	/*
	 * マップ情報を1面で保持するために仕方なくビットフラグ。 可読性が大きく下がるので良い子は真似しない。
	 */
	/**
	 * Mobなどの通行不可能になるオブジェクトがタイル上に存在するかを示すビットフラグ
	 */
	private static final byte BITFLAG_IS_IMPASSABLE = (byte) 128; // 1000 0000

	protected L1V1Map() {

	}

	public L1V1Map(int mapId,
			byte map[][],
			int worldTopLeftX,
			int worldTopLeftY,
			String locationname,// TODO マップ名称検索用
			boolean underwater, boolean markable, boolean teleportable,
			boolean escapable, boolean useResurrection, boolean usePainwand,
			boolean enabledDeathPenalty, boolean takePets, boolean recallPets,
			boolean usableItem, boolean usableSkill) {
		_mapId = mapId;
		_baseMapId = mapId;
		_map = map;
		_worldTopLeftX = worldTopLeftX;
		_worldTopLeftY = worldTopLeftY;

		_worldBottomRightX = worldTopLeftX + map.length - 1;
		_worldBottomRightY = worldTopLeftY + map[0].length - 1;

		_locationname = locationname;// TODO マップ名称検索用
		_isUnderwater = underwater;
		_isMarkable = markable;
		_isTeleportable = teleportable;
		_isEscapable = escapable;
		_isUseResurrection = useResurrection;
		_isUsePainwand = usePainwand;
		_isEnabledDeathPenalty = enabledDeathPenalty;
		_isTakePets = takePets;
		_isRecallPets = recallPets;
		_isUsableItem = usableItem;
		_isUsableSkill = usableSkill;
	}

	public L1V1Map(L1V1Map map) {
		_mapId = map._mapId;
		_baseMapId = map._mapId;

		// _mapをコピー
		_map = new byte[map._map.length][];
		for (int i = 0; i < map._map.length; i++) {
			_map[i] = map._map[i].clone();
		}

		_worldTopLeftX = map._worldTopLeftX;
		_worldTopLeftY = map._worldTopLeftY;
		_worldBottomRightX = map._worldBottomRightX;
		_worldBottomRightY = map._worldBottomRightY;

	}

	private int accessTile(int x, int y) {
		if (!isInMap(x, y)) { // XXX とりあえずチェックする。これは良くない。
			return 0;
		}

		return _map[x - _worldTopLeftX][y - _worldTopLeftY];
	}

	private int accessOriginalTile(int x, int y) {
		return accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE);
	}

	private void setTile(int x, int y, int tile) {
		if (!isInMap(x, y)) { // XXX とりあえずチェックする。これは良くない。
			return;
		}
		_map[x - _worldTopLeftX][y - _worldTopLeftY] = (byte) tile;
	}

	/*
	 * ものすごく良くない気がする
	 */
	public byte[][] getRawTiles() {
		return _map;
	}

	@Override
	public int getId() {
		return _mapId;
	}

	@Override
	public int getX() {
		return _worldTopLeftX;
	}

	@Override
	public int getY() {
		return _worldTopLeftY;
	}

	@Override
	public int getWidth() {
		return _worldBottomRightX - _worldTopLeftX + 1;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return _worldBottomRightY - _worldTopLeftY + 1;
	}

	@Override
	public int getOriginalTile(int x, int y) {
		return accessOriginalTile(x, y);
	}

	@Override
	public boolean isInMap(Point pt) {
		return isInMap(pt.getX(), pt.getY());
	}

	@Override
	public boolean isInMap(int x, int y) {
		// フィールドの茶色エリアの判定
		if (_mapId == 4 && (x < 32520 || y < 32070 || (y < 32190 && x < 33950))) {
			return false;
		}
		return (_worldTopLeftX <= x && x <= _worldBottomRightX
				&& _worldTopLeftY <= y && y <= _worldBottomRightY);
	}

	@Override
	public boolean isPassable(Point pt) {
		return isPassable(pt.getX(), pt.getY());
	}

	@Override
	public boolean isPassable(int x, int y) {
		return isPassable(x, y - 1, 4) || isPassable(x + 1, y, 6)
				|| isPassable(x, y + 1, 0) || isPassable(x - 1, y, 2);
	}

	@Override
	public boolean isPassable(Point pt, int heading) {
		return isPassable(pt.getX(), pt.getY(), heading);
	}

	@Override
	public boolean isPassable(int x, int y, int heading) {
		L1Location newLoc = new L1Location(x, y, _mapId);
		newLoc.forward(heading);
		// 現在のタイル
		int tile1 = accessTile(x, y);
		// 移動予定のタイル
		int tile2 = accessTile(newLoc.getX(), newLoc.getY());

		if ((tile2 & BITFLAG_IS_IMPASSABLE) == BITFLAG_IS_IMPASSABLE) {
			return false;
		}

		if (!((tile2 & 0x02) == 0x02 || (tile2 & 0x01) == 0x01)) {
			return false;
		}
		
		if (heading == Heading.UP) {
			int dir = DoorTable.getInstance().getDoorDirection(newLoc);
			return (tile1 & 0x02) == 0x02 && dir != 0;
		} else if (heading == Heading.UP_RIGHT) {
			return isPassableUpThenRight(x, y) || isPassableRightThenUp(x, y);
		} else if (heading == Heading.RIGHT) {
			int dir = DoorTable.getInstance().getDoorDirection(newLoc);
			return (tile1 & 0x01) == 0x01 && dir != 1;
		} else if (heading == Heading.DOWN_RIGHT) {
			return isPassableRightThenDown(x, y)
					|| isPassableDownThenRight(x, y);
		} else if (heading == Heading.DOWN) {
			int dir = DoorTable.getInstance().getDoorDirection(
					new L1Location(x, y, _mapId));
			return (tile2 & 0x02) == 0x02 && dir != 0;
		} else if (heading == Heading.DOWN_LEFT) {
			return isPassableDownThenLeft(x, y) || isPassableLeftThenDown(x, y);
		} else if (heading == Heading.LEFT) {
			int dir = DoorTable.getInstance().getDoorDirection(
					new L1Location(x, y, _mapId));
			return (tile2 & 0x01) == 0x01 && dir != 1;
		} else if (heading == Heading.UP_LEFT) {
			return isPassableUpThenLeft(x, y) || isPassableLeftThenUp(x, y);
		}

		return false;
	}

	private boolean isPassableLeftThenUp(int x, int y) {
		return isPassable(x, y, Heading.LEFT)
				&& isPassable(x - 1, y, Heading.UP);
	}

	private boolean isPassableUpThenLeft(int x, int y) {
		return isPassable(x, y, Heading.UP) && isPassable(x, y, Heading.LEFT);
	}

	private boolean isPassableLeftThenDown(int x, int y) {
		return isPassable(x, y, Heading.LEFT)
				&& isPassable(x - 1, y, Heading.DOWN);
	}

	private boolean isPassableDownThenLeft(int x, int y) {
		return isPassable(x, y, Heading.DOWN)
				&& isPassable(x, y + 1, Heading.LEFT);
	}

	private boolean isPassableDownThenRight(int x, int y) {
		return isPassable(x, y, Heading.DOWN)
				&& isPassable(x, y, Heading.RIGHT);
	}

	private boolean isPassableRightThenDown(int x, int y) {
		return (isPassable(x, y, Heading.RIGHT) && isPassable(x + 1, y,
				Heading.DOWN));
	}

	private boolean isPassableRightThenUp(int x, int y) {
		return isPassable(x, y, 2) && isPassable(x + 1, y, 0);
	}

	private boolean isPassableUpThenRight(int x, int y) {
		return (isPassable(x, y, Heading.UP) && isPassable(x, y - 1,
				Heading.RIGHT));
	}

	@Override
	public void setPassable(Point pt, boolean isPassable) {
		setPassable(pt.getX(), pt.getY(), isPassable);
	}

	@Override
	public void setPassable(int x, int y, boolean isPassable) {
		if (isPassable) {
			setTile(x, y, (short) (accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE)));
		} else {
			setTile(x, y, (short) (accessTile(x, y) | BITFLAG_IS_IMPASSABLE));
		}
	}

	@Override
	public boolean isSafetyZone(Point pt) {
		return isSafetyZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isSafetyZone(int x, int y) {
		int tile = accessOriginalTile(x, y);

		return (tile & 0x30) == 0x10;
	}

	@Override
	public boolean isCombatZone(Point pt) {
		return isCombatZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isCombatZone(int x, int y) {
		int tile = accessOriginalTile(x, y);

		return (tile & 0x30) == 0x20;
	}

	@Override
	public boolean isNormalZone(Point pt) {
		return isNormalZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isNormalZone(int x, int y) {
		int tile = accessOriginalTile(x, y);
		return (tile & 0x30) == 0x00;
	}

	@Override
	public boolean isArrowPassable(Point pt) {
		return isArrowPassable(pt.getX(), pt.getY());
	}

	@Override
	public boolean isArrowPassable(int x, int y) {
		return (accessOriginalTile(x, y) & 0x0e) != 0;
	}

	@Override
	public boolean isArrowPassable(Point pt, int heading) {
		return isArrowPassable(pt.getX(), pt.getY(), heading);
	}

	@Override
	public boolean isArrowPassable(int x, int y, int heading) {
		L1Location newLoc = new L1Location(x, y, _mapId);
		newLoc.forward(heading);
		// 現在のタイル
		int tile1 = accessTile(x, y);
		// 移動予定の座標
		int tile2 = accessTile(newLoc.getX(), newLoc.getY());

		if (heading == Heading.UP) {
			int dir = DoorTable.getInstance().getDoorDirection(newLoc);
			return (tile1 & 0x08) == 0x08 && dir != 1;
		} else if (heading == Heading.UP_RIGHT) {
			return isArrowPassableUpThenRight(x, y)
					|| isArrowPassableRightThenUp(x, y);
		} else if (heading == Heading.RIGHT) {
			int dir = DoorTable.getInstance().getDoorDirection(newLoc);
			return (tile1 & 0x04) == 0x04 && dir != 0;
		} else if (heading == Heading.DOWN_RIGHT) {
			return isArrowPassableRightThenDown(x, y)
					|| isArrowPassableDownThenRight(x, y);
		} else if (heading == Heading.DOWN) {
			int dir = DoorTable.getInstance().getDoorDirection(
					new L1Location(x, y, _mapId));
			return (tile2 & 0x08) == 0x08 && dir != 1;
		} else if (heading == Heading.DOWN_LEFT) {
			return isArrowPassableDownThenLeft(x, y)
					|| isArrowPassableLeftThenDown(x, y);
		} else if (heading == Heading.LEFT) {
			int dir = DoorTable.getInstance().getDoorDirection(
					new L1Location(x, y, _mapId));
			return (tile2 & 0x04) == 0x04 && dir != 0;
		} else if (heading == Heading.UP_LEFT) {
			return isArrowPassableUpThenLeft(x, y)
					|| isArrowPassableLeftThenUp(x, y);
		}

		return false;
	}

	private boolean isArrowPassableLeftThenUp(int x, int y) {
		return isArrowPassable(x, y, Heading.LEFT)
				&& isArrowPassable(x - 1, y, Heading.UP);
	}

	private boolean isArrowPassableUpThenLeft(int x, int y) {
		return isArrowPassable(x, y, Heading.UP)
				&& isArrowPassable(x, y, Heading.LEFT);
	}

	private boolean isArrowPassableLeftThenDown(int x, int y) {
		return isArrowPassable(x, y, Heading.LEFT)
				&& isArrowPassable(x - 1, y, Heading.DOWN);
	}

	private boolean isArrowPassableDownThenLeft(int x, int y) {
		return isArrowPassable(x, y, Heading.DOWN)
				&& isArrowPassable(x, y + 1, Heading.LEFT);
	}

	private boolean isArrowPassableDownThenRight(int x, int y) {
		return isArrowPassable(x, y, Heading.DOWN)
				&& isArrowPassable(x, y, Heading.RIGHT);
	}

	private boolean isArrowPassableRightThenDown(int x, int y) {
		return (isArrowPassable(x, y, Heading.RIGHT) && isArrowPassable(x + 1,
				y, Heading.DOWN));
	}

	private boolean isArrowPassableRightThenUp(int x, int y) {
		return isArrowPassable(x, y, 2) && isArrowPassable(x + 1, y, 0);
	}

	private boolean isArrowPassableUpThenRight(int x, int y) {
		return (isArrowPassable(x, y, Heading.UP) && isArrowPassable(x, y - 1,
				Heading.RIGHT));
	}

	@Override
	public String locationname() {// TODO マップ名称検索用
		return _locationname;
	}

	@Override
	public boolean isUnderwater() {
		return _isUnderwater;
	}

	@Override
	public boolean isMarkable() {
		return _isMarkable;
	}

	@Override
	public boolean isTeleportable() {
		return _isTeleportable;
	}

	@Override
	public boolean isEscapable() {
		return _isEscapable;
	}

	@Override
	public boolean isUseResurrection() {
		return _isUseResurrection;
	}

	@Override
	public boolean isUsePainwand() {
		return _isUsePainwand;
	}

	@Override
	public boolean isEnabledDeathPenalty() {
		return _isEnabledDeathPenalty;
	}

	@Override
	public boolean isTakePets() {
		return _isTakePets;
	}

	@Override
	public boolean isRecallPets() {
		return _isRecallPets;
	}

	@Override
	public boolean isUsableItem() {
		return _isUsableItem;
	}

	@Override
	public boolean isUsableSkill() {
		return _isUsableSkill;
	}

	@Override
	public boolean isFishingZone(int x, int y) {
		return accessOriginalTile(x, y) == 16;
	}

	@Override
	public boolean isExistDoor(int x, int y) {
		for (L1DoorInstance door : DoorTable.getInstance().getDoorList()) {
			if (_mapId != door.getMapId()) {
				continue;
			}
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				continue;
			}
			if (door.isDead()) {
				continue;
			}
			int leftEdgeLocation = door.getLeftEdgeLocation();
			int rightEdgeLocation = door.getRightEdgeLocation();
			int size = rightEdgeLocation - leftEdgeLocation;
			if (size == 0) { // 1マス分の幅のドア
				if (x == door.getX() && y == door.getY()) {
					return true;
				}
			} else { // 2マス分以上の幅があるドア
				if (door.getDirection() == 0) { // ／向き
					for (int doorX = leftEdgeLocation; doorX <= rightEdgeLocation; doorX++) {
						if (x == doorX && y == door.getY()) {
							return true;
						}
					}
				} else { // ＼向き
					for (int doorY = leftEdgeLocation; doorY <= rightEdgeLocation; doorY++) {
						if (x == door.getX() && y == doorY) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public L1V1Map clone() {
		L1V1Map result = (L1V1Map) super.clone();
		// _mapをdeep copy
		result._map = new byte[_map.length][];
		for (int i = 0; i < _map.length; i++) {
			result._map[i] = _map[i].clone();
		}
		return result;
	}

	@Override
	public String toString(Point pt) {
		int tile = accessOriginalTile(pt.getX(), pt.getY());
		return "" + tile + " " + BinaryUtils.byteToBinaryString((byte) tile);
	}

	@Override
	public void setId(int mapId) {
		_mapId = mapId;
	}

	@Override
	public int getBaseMapId() {
		return _baseMapId;
	}
}
