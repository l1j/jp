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

package jp.l1j.server.model;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.skill.L1SkillId;
import jp.l1j.server.types.Point;
import jp.l1j.server.templates.L1MagicDoll;
import static jp.l1j.server.model.skill.L1SkillId.*;

public class MpRegeneration extends TimerTask {
	private static Logger _log = Logger.getLogger(MpRegeneration.class
			.getName());

	private final L1PcInstance _pc;

	private int _regenPoint = 0;

	private int _curPoint = 4;

	public MpRegeneration(L1PcInstance pc) {
		_pc = pc;
	}

	public void setState(int state) {
		if (_curPoint < state) {
			return;
		}

		_curPoint = state;
	}

	@Override
	public void run() {
		try {
			if (_pc.isDead()) {
				return;
			}

			_regenPoint += _curPoint;
			_curPoint = 4;

			if (64 <= _regenPoint) {
				_regenPoint = 0;
				regenMp();
			}
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void regenMp() {
		int baseMpr = 1;
		int wis = _pc.getWis();
		if (wis == 15 || wis == 16) {
			baseMpr = 2;
		} else if (wis >= 17) {
			baseMpr = 3;
		}

		if (_pc.hasSkillEffect(STATUS_BLUE_POTION)) { // ブルーポーション使用中
			if (wis < 11) { // WIS11未満でもMPR+1
				wis = 11;
			}
			baseMpr += wis - 10;
		}
		if (_pc.hasSkillEffect(MEDITATION)) { // メディテーション中
			baseMpr += 5;
		}
		if (_pc.hasSkillEffect(CONCENTRATION)) { // コンセントレーション中
			baseMpr += 2;
		}
		if (L1HouseLocation.isInHouse(_pc.getX(), _pc.getY(), _pc.getMapId())) {
			baseMpr += 3;
		}
		if (_pc.getMapId() == 16384 || _pc.getMapId() == 16896
				|| _pc.getMapId() == 17408 || _pc.getMapId() == 17920
				|| _pc.getMapId() == 18432 || _pc.getMapId() == 18944
				|| _pc.getMapId() == 19968 || _pc.getMapId() == 19456
				|| _pc.getMapId() == 20480 || _pc.getMapId() == 20992
				|| _pc.getMapId() == 21504 || _pc.getMapId() == 22016
				|| _pc.getMapId() == 22528 || _pc.getMapId() == 23040
				|| _pc.getMapId() == 23552 || _pc.getMapId() == 24064
				|| _pc.getMapId() == 24576 || _pc.getMapId() == 25088) { // 宿屋
			baseMpr += 3;
		}
		// マザーツリー
		if ((_pc.getLocation().isInScreen(new Point(33055,32336))
				&& _pc.getMapId() == 4 && _pc.isElf())) {
			baseMpr += 3;
		}
		// シルベリア（シーレン加護）・ベヒモス（アウラキア加護）
		if ((_pc.getLocation().isInScreen(new Point(32801,32863))
				&& (_pc.getMapId() == 1000 || _pc.getMapId() == 1001))) {
			baseMpr += 3;
		}
		// 傲慢の塔 休憩階(x6階)
		if ((_pc.getLocation().isInScreen(new Point(33806, 32863)) && _pc.getMapId() == 106)
				|| (_pc.getLocation().isInScreen(new Point(32782, 32863))
				&& (_pc.getMapId() == 106 || _pc.getMapId() == 116
				|| _pc.getMapId() == 126 || _pc.getMapId() == 136
				|| _pc.getMapId() == 146))
				|| (_pc.getLocation().isInScreen(new Point(32784, 32798))
				&& (_pc.getMapId() == 156 || _pc.getMapId() == 166
				|| _pc.getMapId() == 176 || _pc.getMapId() == 186
				|| _pc.getMapId() == 196))) {
			baseMpr += 3;
		}
		if (_pc.hasSkillEffect(COOKING_1_2_N)
				|| _pc.hasSkillEffect(COOKING_1_2_S)) {
			baseMpr += 3;
		}
 		if (_pc.hasSkillEffect(COOKING_2_4_N)
				|| _pc.hasSkillEffect(COOKING_2_4_S)
				|| _pc.hasSkillEffect(COOKING_3_5_N)
				|| _pc.hasSkillEffect(COOKING_3_5_S)
				|| _pc.hasSkillEffect(COOKING_4_1)
				|| _pc.hasSkillEffect(COOKING_4_2)
				|| _pc.hasSkillEffect(COOKING_4_3)) {
			baseMpr += 2;
		}
 		if (_pc.getOriginalMpr() > 0) { // オリジナルWIS MPR補正
 			baseMpr += _pc.getOriginalMpr();
 		}
		baseMpr += L1MagicDoll.getNatMprByDoll(_pc); // マジックドールによるMPR補正
		int itemMpr = 0;
		itemMpr += _pc.getMpr();

		if (_pc.getFood() < 3 || isOverWeight(_pc)) {
			baseMpr = 0;
			if (itemMpr > 0) {
				itemMpr = 0;
			}
		}
		int mpr = baseMpr + itemMpr;
		int newMp = _pc.getCurrentMp() + mpr;
		if (newMp < 0) {
			newMp = 0;
		}
		_pc.setCurrentMp(newMp);
	}

	private boolean isOverWeight(L1PcInstance pc) {
		// エキゾチックバイタライズ状態、アディショナルファイアー状態であれば、
		// 重量オーバーでは無いとみなす。
		if (pc.hasSkillEffect(EXOTIC_VITALIZE)
				|| pc.hasSkillEffect(ADDITIONAL_FIRE)
				|| pc.hasSkillEffect(AWAKEN_FAFURION)) {
			return false;
		}

		return (120 <= pc.getInventory().getWeight240()) ? true : false;
	}
}
