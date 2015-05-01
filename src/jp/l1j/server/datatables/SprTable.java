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

package jp.l1j.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jp.l1j.server.codes.ActionCodes.*;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.PerformanceTimer;
import jp.l1j.server.utils.SqlUtil;

public class SprTable {

	private static Logger _log = Logger.getLogger(SprTable.class.getName());

	private static SprTable _instance;

	private static HashMap<Integer, Spr> _sprActions = new HashMap<Integer, Spr>();

	private static class Spr {
		private final HashMap<Integer, Integer> moveSpeed = new HashMap<Integer, Integer>();

		private final HashMap<Integer, Integer> attackSpeed = new HashMap<Integer, Integer>();

		private int nodirSpellSpeed = 1200;

		private int dirSpellSpeed = 1200;
	}

	public static SprTable getInstance() {
		if (_instance == null) {
			_instance = new SprTable();
		}
		return _instance;
	}

	private SprTable() {
		loadSprActions(_sprActions);
	}

	/**
	 * spr_actionsテーブルをロードする。
	 */
	public void loadSprActions(HashMap<Integer, Spr> sprActions) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Spr spr = null;
		try {
			PerformanceTimer timer = new PerformanceTimer();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spr_actions");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int key = rs.getInt("spr_id");
				if (!sprActions.containsKey(key)) {
					spr = new Spr();
					sprActions.put(key, spr);
				} else {
					spr = sprActions.get(key);
				}
				int actid = rs.getInt("act_id");
				int frameCount = rs.getInt("frame_count");
				int frameRate = rs.getInt("frame_rate");
				int speed = calcActionSpeed(frameCount, frameRate);
				switch (actid) {
				case ACTION_Walk:
				case ACTION_SwordWalk:
				case ACTION_AxeWalk:
				case ACTION_BowWalk:
				case ACTION_SpearWalk:
				case ACTION_StaffWalk:
				case ACTION_DaggerWalk:
				case ACTION_TwoHandSwordWalk:
				case ACTION_EdoryuWalk:
				case ACTION_ClawWalk:
				case ACTION_ThrowingKnifeWalk:
					spr.moveSpeed.put(actid, speed);
					break;
				case ACTION_SkillAttack:
					spr.dirSpellSpeed = speed;
					break;
				case ACTION_SkillBuff:
					spr.nodirSpellSpeed = speed;
					break;
				case ACTION_Attack:
				case ACTION_SwordAttack:
				case ACTION_AxeAttack:
				case ACTION_BowAttack:
				case ACTION_SpearAttack:
				case ACTION_StaffAttack:
				case ACTION_DaggerAttack:
				case ACTION_TwoHandSwordAttack:
				case ACTION_EdoryuAttack:
				case ACTION_ClawAttack:
				case ACTION_ThrowingKnifeAttack:
					spr.attackSpeed.put(actid, speed);
				default:
					break;
				}
			}
			System.out.println("loading spr actions...OK! " + timer.elapsedTimeMillis() + "ms");
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs, pstm, con);
		}
		_log.fine("loaded spr: " + sprActions.size() + " records");
	}

	public void reload() {
		HashMap<Integer, Spr> sprActions = new HashMap<Integer, Spr>();
		loadSprActions(sprActions);
		_sprActions = sprActions;
	}
	
	/**
	 * フレーム数とフレームレートからアクションの合計時間(ms)を計算して返す。
	 */
	private int calcActionSpeed(int frameCount, int frameRate) {
		return (int) (frameCount * 40 * (24D / frameRate));
	}

	/**
	 * 指定されたsprの攻撃速度を返す。もしsprに指定されたweapon_typeのデータが 設定されていない場合は、1.attackのデータを返す。
	 * 
	 * @param sprid -
	 *            調べるsprのID
	 * @param actid -
	 *            武器の種類を表す値。L1Item.getType1()の返り値 + 1 と一致する
	 * @return 指定されたsprの攻撃速度(ms)
	 */
	public int getAttackSpeed(int sprid, int actid) {
		if (_sprActions.containsKey(sprid)) {
			if (_sprActions.get(sprid).attackSpeed.containsKey(actid)) {
				return _sprActions.get(sprid).attackSpeed.get(actid);
			} else if (actid == ACTION_Attack) {
				return 0;
			} else {
				return _sprActions.get(sprid).attackSpeed.get(ACTION_Attack);
			}
		}
		return 0;
	}

	public int getMoveSpeed(int sprid, int actid) {
		if (_sprActions.containsKey(sprid)) {
			if (_sprActions.get(sprid).moveSpeed.containsKey(actid)) {
				return _sprActions.get(sprid).moveSpeed.get(actid);
			} else if (actid == ACTION_Walk) {
				return 0;
			} else {
				return _sprActions.get(sprid).moveSpeed.get(ACTION_Walk);
			}
		}
		return 0;
	}

	public int getDirSpellSpeed(int sprid) {
		if (_sprActions.containsKey(sprid)) {
			return _sprActions.get(sprid).dirSpellSpeed;
		}
		return 0;
	}

	public int getNodirSpellSpeed(int sprid) {
		if (_sprActions.containsKey(sprid)) {
			return _sprActions.get(sprid).nodirSpellSpeed;
		}
		return 0;
	}
}
