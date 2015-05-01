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

package jp.l1j.server.templates;

public class L1EtcItem extends L1Item {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public L1EtcItem() {
	}

	private boolean _stackable;

	private int _locX;

	private int _locY;

	private short _mapId;

	private int _delayId;

	private int _delayTime;

	private int _delayEffect;

	private int _maxChargeCount;

	private boolean _isSealable; // ● 封印スクロールで封印可能

	@Override
	public boolean isStackable() {
		return _stackable;
	}

	public void setStackable(boolean stackable) {
		_stackable = stackable;
	}

	public void setLocX(int locX) {
		_locX = locX;
	}

	@Override
	public int getLocX() {
		return _locX;
	}

	public void setLocY(int locY) {
		_locY = locY;
	}

	@Override
	public int getLocY() {
		return _locY;
	}

	public void setMapId(short mapId) {
		_mapId = mapId;
	}

	@Override
	public short getMapId() {
		return _mapId;
	}

	public void setDelayId(int delayId) {
		_delayId = delayId;
	}

	@Override
	public int getDelayId() {
		return _delayId;
	}

	public void setDelayTime(int delayTime) {
		_delayTime = delayTime;
	}

	@Override
	public int getDelayTime() {
		return _delayTime;
	}

	public void setDelayEffect(int delayEffect) {
		_delayEffect = delayEffect;
	}

	public int getDelayEffect() {
		return _delayEffect;
	}

	public void setMaxChargeCount(int i) {
		_maxChargeCount = i;
	}

	@Override
	public int getMaxChargeCount() {
		return _maxChargeCount;
	}

	@Override
	public boolean isSealable() {
		return _isSealable;
	}

	public void setSealable(boolean flag) {
		_isSealable = flag;
	}


}
