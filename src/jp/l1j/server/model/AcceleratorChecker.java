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

package jp.l1j.server.model;

import java.util.EnumMap;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.AcceleratorLogTable;
import jp.l1j.server.datatables.SprTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_Disconnect;
import jp.l1j.server.packets.server.S_Paralysis;
import jp.l1j.server.packets.server.S_SystemMessage;

/**
 * 加速器の使用をチェックするクラス。
 */
public class AcceleratorChecker {

	private static Logger _log =
		Logger.getLogger(AcceleratorChecker.class.getName());

	private final L1PcInstance _pc;

	private int _injusticeCount;

	private int _justiceCount;

	private static final int INJUSTICE_COUNT_LIMIT = Config.INJUSTICE_COUNT;

	private static final int JUSTICE_COUNT_LIMIT = Config.JUSTICE_COUNT;

	// 実際には移動と攻撃のパケット間隔はsprの理論値より5%ほど遅い。
	// それを考慮して-5としている。
	private static final double CHECK_STRICTNESS =
			(Config.CHECK_STRICTNESS - 5) / 100D;

	private static final double HASTE_RATE = 0.75; // 速度 * 1.33

	private static final double WAFFLE_RATE = 0.87; // 速度 * 1.15

	private static final double STANDARD_RATE = 1.00;
	
	private final EnumMap<ACT_TYPE, Long> _actTimers =
			new EnumMap<ACT_TYPE, Long>(ACT_TYPE.class);

	private final EnumMap<ACT_TYPE, Long> _checkTimers =
			new EnumMap<ACT_TYPE, Long>(ACT_TYPE.class);

	public static enum ACT_TYPE {
		MOVE, ATTACK, SPELL_DIR, SPELL_NODIR
	}

	// チェックの結果
	public static final int R_OK = 0;

	public static final int R_DETECTED = 1;

	public static final int R_DISPOSED = 2;

	public AcceleratorChecker(L1PcInstance pc) {
		_pc = pc;
		_injusticeCount = 0;
		_justiceCount = 0;
		long now = System.currentTimeMillis();
		for (ACT_TYPE each : ACT_TYPE.values()) {
			_actTimers.put(each, now);
			_checkTimers.put(each, now);
		}
	}

	/**
	 * アクションの間隔が不正でないかチェックし、適宜処理を行う。
	 * 
	 * @param type -
	 *            チェックするアクションのタイプ
	 * @return 問題がなかった場合は0、不正であった場合は1、不正動作が一定回数に達した ためプレイヤーを切断した場合は2を返す。
	 */
	public int checkInterval(ACT_TYPE type) {
		int result = R_OK;
		long now = System.currentTimeMillis();
		long interval = now - _actTimers.get(type);
		int rightInterval = getRightInterval(type);
		interval *= CHECK_STRICTNESS;
		double rate = (double) interval / rightInterval;
		if (0 < rate && rate < STANDARD_RATE) {
			_injusticeCount++;
			_justiceCount = 0;
			if (_injusticeCount >= INJUSTICE_COUNT_LIMIT) {
				doPunishment();
				return R_DISPOSED;
			}
			result = R_DETECTED;
		} else if (rate >= STANDARD_RATE) {
			_justiceCount++;
			if (_justiceCount >= JUSTICE_COUNT_LIMIT) {
				_injusticeCount = 0;
				_justiceCount = 0;
			}
		}
		_actTimers.put(type, now);
		return result;
	}

	/**
	 * 加速器検知の処罰
	 * @param punishmaent
	 */
	private void doPunishment() {
		int punishment_type = Math.abs(Config.PUNISHMENT_TYPE);
		int punishment_time = Math.abs(Config.PUNISHMENT_TIME);
		int punishment_mapid = Math.abs(Config.PUNISHMENT_MAP_ID);
		if (!_pc.isGm()) {// 一般ユーザーに対する処罰
			int x = _pc.getX() ,y = _pc.getY() ,mapid = _pc.getMapId();// 座標
			switch (punishment_type) {
			case 0:// 強制切断
				_pc.sendPackets(new S_SystemMessage(String.format(I18N_ACCELERRATOR_DISCONNECT_THE_CONNECTION, punishment_time)));
				// 速度超過を検出しました。%D秒後に接続を切断します。
				try {
					Thread.sleep(punishment_time * 1000);
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
				}
				_pc.saveInventory();
				_pc.sendPackets(new S_Disconnect());
				break;
			case 1:// 行動停止
				_pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
				_pc.sendPackets(new S_SystemMessage(String.format(I18N_ACCELERRATOR_STOP_THE_ACTION, punishment_time)));
				// 速度超過を検出しました。%D秒後に行動を停止します。
				try {
					Thread.sleep(punishment_time * 1000);
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
				}
				_pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
				break;
			case 2:// 指定MAPへ転送（隔離）
				L1Teleport.teleport(_pc, 32737, 32796, (short) punishment_mapid, 5, false);
				_pc.sendPackets(new S_SystemMessage(String.format(I18N_ACCELERRATOR_MOVE_TO_THE_ISOLATION_MAP, punishment_time)));
				// 速度超過を検出しました。%d秒後に隔離マップへ移動します。
				try {
					Thread.sleep(punishment_time * 1000);
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
				}
				L1Teleport.teleport(_pc, x, y, (short) mapid, 5, false);
				break;
			}
		} else {
			// GMは処罰しない。警告メッセージのみ
			if (Config.DEBUG_MODE) {
				_pc.sendPackets(new S_SystemMessage("加速器検知にひっかかっています。"));
				_pc.sendPackets(new S_SystemMessage(I18N_ACCELERRATOR_OVERSPEED_DETECTED));
				// 速度超過を検出しました。
			}
		}
		_injusticeCount = 0;
		_justiceCount = 0;
		if (Config.LOGGING_ACCELERATOR) {
			AcceleratorLogTable logaccelerator = new AcceleratorLogTable();
			logaccelerator.storeLogAccelerator(_pc);// 加速器検知ログ
		}
	}
		
	/**
	 * PCの状態から指定された種類のアクションの正しいインターバル(ms)を計算し、返す。
	 * 
	 * @param type -
	 *            アクションの種類
	 * @param _pc -
	 *            調べるPC
	 * @return 正しいインターバル(ms)
	 */
	private int getRightInterval(ACT_TYPE type) {
		int interval;
		switch (type) {
		case ATTACK:
			interval = SprTable.getInstance().getAttackSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon() + 1);
			break;
		case MOVE:
			interval = SprTable.getInstance().getMoveSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon());
			break;
		case SPELL_DIR:
			interval = SprTable.getInstance().getDirSpellSpeed(_pc.getTempCharGfx());
			break;
		case SPELL_NODIR:
			interval = SprTable.getInstance().getNodirSpellSpeed(_pc.getTempCharGfx());
			break;
		default:
			return 0;
		}
		if (_pc.isHaste()) { // ヘイスト
			interval *= HASTE_RATE;
		}
		if (_pc.isSlow()) { // スロー
			interval /= HASTE_RATE;
		}
		if (_pc.isBrave()) { // ブレイブポーション
			interval *= HASTE_RATE;
		}
		if (_pc.isElfBrave()) { // エルヴンワッフル
			interval *= WAFFLE_RATE;
		}
		if (type.equals(ACT_TYPE.MOVE) && _pc.isFastMovable()) {
			// ホーリーウォーク、ウィンドウォーク、ムービングアクセレーション
			interval *= HASTE_RATE;
		}
		if (type.equals(ACT_TYPE.ATTACK) && _pc.isFastAttackable()) {
			// ブラッドラスト
			interval *= HASTE_RATE * WAFFLE_RATE;
		}
		if (type.equals(ACT_TYPE.MOVE) && _pc.isRIBRAVE()) { // ユグドラの実
			interval *= WAFFLE_RATE;
		}
		if (_pc.isThirdSpeed()) { // 三段加速
			interval *= WAFFLE_RATE;
		}
		if (_pc.isWindShackle()) { // ウィンドシャックル
			interval /= 2;
		}
		if(_pc.getMapId() == 5143) { // 例外：ペットレース
			interval *= 0.1;
		}
		return interval;
	}
}
