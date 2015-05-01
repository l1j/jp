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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

import jp.l1j.configure.Config;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.controller.timer.WarTimeController;
import jp.l1j.server.datatables.CastleTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_War;
import jp.l1j.server.templates.L1Castle;

// Referenced classes of package jp.l1j.server.model:
// L1War

public class L1War {
	private String _param1 = null;
	private String _param2 = null;
	private final ArrayList<String> _attackClanList = new ArrayList<String>();
	private String _defenceClanName = null;
	private int _warType = 0;
	private int _castleId = 0;
	private L1Castle _castle = null;
	private Calendar _warEndTime;

	private boolean _isWarTimerDelete = false;

	private static Logger _log = Logger.getLogger(L1War.class.getName());

	public L1War() {
	}

	class CastleWarTimer implements Runnable {
		public CastleWarTimer() {
		}

		@Override
		public void run() {
			for (;;) {
				try {
					Thread.sleep(1000);
					if (_warEndTime.before(WarTimeController.getInstance()
							.getRealTime())) {
						break;
					}
				} catch (Exception exception) {
					break;
				}
				if (_isWarTimerDelete) { // 戦争が終結していたらタイマー終了
					return;
				}
			}
			CeaseCastleWar(); // 攻城戦終結処理
			delete();
		}
	}

	class SimWarTimer implements Runnable {
		public SimWarTimer() {
		}

		@Override
		public void run() {
			for (int loop = 0; loop < 240; loop++) { // 240分
				try {
					Thread.sleep(60000);
				} catch (Exception exception) {
					break;
				}
				if (_isWarTimerDelete) { // 戦争が終結していたらタイマー終了
					return;
				}
			}
			CeaseWar(_param1, _param2); // 終結
			delete();
		}
	}

	public final void handleCommands(int war_type, String attack_clan_name,
			final String defence_clan_name) {
		// war_type - 1:攻城戦 2:模擬戦
		// attack_clan_name - 布告したクラン名
		// defence_clan_name - 布告されたクラン名（攻城戦時は、城主クラン）

		SetWarType(war_type);

		DeclareWar(attack_clan_name, defence_clan_name);

		_param1 = attack_clan_name;
		_param2 = defence_clan_name;
		InitAttackClan();
		AddAttackClan(attack_clan_name);
		SetDefenceClanName(defence_clan_name);

		if (war_type == 1) { // 攻城戦
			_castleId = GetCastleId();
			_castle = GetCastle();
			if (_castle != null) {
				Calendar cal = (Calendar) _castle.getWarTime().clone();
				cal.add(Config.ALT_WAR_TIME_UNIT, Config.ALT_WAR_TIME);
				_warEndTime = cal;
			}

			CastleWarTimer castle_war_timer = new CastleWarTimer();
			GeneralThreadPool.getInstance().execute(castle_war_timer); // タイマー開始
		} else if (war_type == 2) { // 模擬戦
			SimWarTimer sim_war_timer = new SimWarTimer();
			GeneralThreadPool.getInstance().execute(sim_war_timer); // タイマー開始
		}
		L1World.getInstance().addWar(this); // 戦争リストに追加
	}

	private void RequestCastleWar(int type, String clan1_name, String clan2_name) {
		if (clan1_name == null || clan2_name == null) {
			return;
		}

		L1Clan clan1 = L1World.getInstance().getClan(clan1_name);
		if (clan1 != null) {
			L1PcInstance clan1_member[] = clan1.getOnlineClanMember();
			for (int cnt = 0; cnt < clan1_member.length; cnt++) {
				clan1_member[cnt].sendPackets(new S_War(type, clan1_name,
						clan2_name));
			}
		}

		int attack_clan_num = GetAttackClanListSize();

		if (type == 1 || type == 2 || type == 3) { // 宣戦布告、降伏、終結
			L1Clan clan2 = L1World.getInstance().getClan(clan2_name);
			if (clan2 != null) {
				L1PcInstance clan2_member[] = clan2.getOnlineClanMember();
				for (int cnt = 0; cnt < clan2_member.length; cnt++) {
					if (type == 1) { // 宣戦布告
						clan2_member[cnt].sendPackets(new S_War(type,
								clan1_name, clan2_name));
					} else if (type == 2) { // 降伏
						clan2_member[cnt].sendPackets(new S_War(type,
								clan1_name, clan2_name));
						if (attack_clan_num >= 1) { // 攻撃側クランが一つ以上
							clan2_member[cnt].sendPackets(new S_War(4,
									clan2_name, clan1_name));
						} else {
							clan2_member[cnt].sendPackets(new S_ServerMessage( // %0血盟が%1血盟に降伏しました。
									228, clan1_name, clan2_name));
							RemoveAttackClan(clan1_name);
						}
					} else if (type == 3) { // 終結
						clan2_member[cnt].sendPackets(new S_War(type,
								clan1_name, clan2_name));
						if (attack_clan_num == 1) { // 攻撃側クランが一つ
							clan2_member[cnt].sendPackets(new S_War(4,
									clan2_name, clan1_name));
						} else {
							clan2_member[cnt].sendPackets(new S_ServerMessage( // %0血盟と%1血盟間の戦争が終結しました。
									227, clan1_name, clan2_name));
							RemoveAttackClan(clan1_name);
						}
					}
				}
			}
		}

		if ((type == 2 || type == 3) && attack_clan_num == 1) { // 降伏、終結で攻撃側クランが一つ
			_isWarTimerDelete = true;
			delete();
		}
	}

	private void RequestSimWar(int type, String clan1_name, String clan2_name) {
		if (clan1_name == null || clan2_name == null) {
			return;
		}

		L1Clan clan1 = L1World.getInstance().getClan(clan1_name);
		if (clan1 != null) {
			L1PcInstance clan1_member[] = clan1.getOnlineClanMember();
			for (int cnt = 0; cnt < clan1_member.length; cnt++) {
				clan1_member[cnt].sendPackets(new S_War(type, clan1_name,
						clan2_name));
			}
		}

		if (type == 1 || type == 2 || type == 3) { // 宣戦布告、降伏、終結
			L1Clan clan2 = L1World.getInstance().getClan(clan2_name);
			if (clan2 != null) {
				L1PcInstance clan2_member[] = clan2.getOnlineClanMember();
				for (int cnt = 0; cnt < clan2_member.length; cnt++) {
					if (type == 1) { // 宣戦布告
						clan2_member[cnt].sendPackets(new S_War(type,
								clan1_name, clan2_name));
					} else if (type == 2 || type == 3) { // 降伏、終結
						clan2_member[cnt].sendPackets(new S_War(type,
								clan1_name, clan2_name));
						clan2_member[cnt].sendPackets(new S_War(4, clan2_name,
								clan1_name));
					}
				}
			}
		}

		if (type == 2 || type == 3) { // 降伏、終結
			_isWarTimerDelete = true;
			delete();
		}
	}

	public final void WinCastleWar(String clan_name) { // クラウンを奪取して、攻撃側クランが勝利
		String defence_clan_name = GetDefenceClanName();
		L1World.getInstance().broadcastPacketToAll(new S_ServerMessage( // %0血盟が%1血盟との戦争で勝利しました。
				231, clan_name, defence_clan_name));

		L1Clan defence_clan = L1World.getInstance().getClan(defence_clan_name);
		if (defence_clan != null) {
			L1PcInstance defence_clan_member[] = defence_clan
					.getOnlineClanMember();
			for (int i = 0; i < defence_clan_member.length; i++) {
				for (String clanName : GetAttackClanList()) {
					defence_clan_member[i].sendPackets(new S_War(3,
							defence_clan_name, clanName));
				}
			}
		}

		String clanList[] = GetAttackClanList();
		for (int j = 0; j < clanList.length; j++) {
			if (clanList[j] != null) {
				L1World.getInstance().broadcastPacketToAll(new S_ServerMessage( // %0血盟と%1血盟間の戦争が終結しました。
						227, defence_clan_name, clanList[j]));
				L1Clan clan = L1World.getInstance().getClan(clanList[j]);
				if (clan != null) {
					L1PcInstance[] clan_member = clan.getOnlineClanMember();
					for (int k = 0; k < clan_member.length; k++) {
						clan_member[k].sendPackets(new S_War(3, clanList[j],
								defence_clan_name));
					}
				}
			}
		}

		_isWarTimerDelete = true;
		delete();
	}

	public final void CeaseCastleWar() { // 戦争時間満了し、防衛側クランが勝利
		String defence_clan_name = GetDefenceClanName();
		String clanList[] = GetAttackClanList();
		if (defence_clan_name != null) {
			L1World.getInstance().broadcastPacketToAll(new S_ServerMessage( // %0血盟が%1血盟との戦争で勝利しました。
					231, defence_clan_name, clanList[0]));
		}

		L1Clan defence_clan = L1World.getInstance().getClan(defence_clan_name);
		if (defence_clan != null) {
			L1PcInstance defence_clan_member[] = defence_clan
					.getOnlineClanMember();
			for (int i = 0; i < defence_clan_member.length; i++) {
				defence_clan_member[i].sendPackets(new S_War(4,
						defence_clan_name, clanList[0]));
			}
		}

		for (int j = 0; j < clanList.length; j++) {
			if (clanList[j] != null) {
				L1World.getInstance().broadcastPacketToAll(new S_ServerMessage( // %0血盟と%1血盟間の戦争が終結しました。
						227, defence_clan_name, clanList[j]));
				L1Clan clan = L1World.getInstance().getClan(clanList[j]);
				if (clan != null) {
					L1PcInstance[] clan_member = clan.getOnlineClanMember();
					for (int k = 0; k < clan_member.length; k++) {
						clan_member[k].sendPackets(new S_War(3, clanList[j],
								defence_clan_name));
					}
				}
			}
		}

		_isWarTimerDelete = true;
		delete();
	}

	public final void DeclareWar(final String clan1_name, final String clan2_name) { // _血盟が_血盟に宣戦布告しました。
		if (GetWarType() == 1) { // 攻城戦
			RequestCastleWar(1, clan1_name, clan2_name);
		} else { // 模擬戦
			RequestSimWar(1, clan1_name, clan2_name);
		}
	}

	public final void SurrenderWar(final String clan1_name, final String clan2_name) { // _血盟が_血盟に降伏しました。
		if (GetWarType() == 1) {
			RequestCastleWar(2, clan1_name, clan2_name);
		} else {
			RequestSimWar(2, clan1_name, clan2_name);
		}
	}

	public final void CeaseWar(final String clan1_name, final String clan2_name) { // _血盟と_血盟との戦争が終結しました。
		if (GetWarType() == 1) {
			RequestCastleWar(3, clan1_name, clan2_name);
		} else {
			RequestSimWar(3, clan1_name, clan2_name);
		}
	}

	public final void WinWar(final String clan1_name, final String clan2_name) { // _血盟が_血盟との戦争で勝利しました。
		if (GetWarType() == 1) {
			RequestCastleWar(4, clan1_name, clan2_name);
		} else {
			RequestSimWar(4, clan1_name, clan2_name);
		}
	}

	public final boolean CheckClanInWar(final String clan_name) { // クランが戦争に参加しているかチェックする
		boolean ret;
		if (GetDefenceClanName().toLowerCase().equals(clan_name.toLowerCase())) { // 防衛側クランをチェック
			ret = true;
		} else {
			ret = CheckAttackClan(clan_name); // 攻撃側クランをチェック
		}
		return ret;
	}

	public final boolean CheckClanInSameWar(String player_clan_name,
			final String target_clan_name) { // 自クランと相手クランが同じ戦争に参加しているかチェックする（同じクランの場合も含む）
		boolean player_clan_flag;
		boolean target_clan_flag;

		if (GetDefenceClanName().toLowerCase().equals(
				player_clan_name.toLowerCase())) { // 自クランに対して防衛側クランをチェック
			player_clan_flag = true;
		} else {
			player_clan_flag = CheckAttackClan(player_clan_name); // 自クランに対して攻撃側クランをチェック
		}

		if (GetDefenceClanName().toLowerCase().equals(
				target_clan_name.toLowerCase())) { // 相手クランに対して防衛側クランをチェック
			target_clan_flag = true;
		} else {
			target_clan_flag = CheckAttackClan(target_clan_name); // 相手クランに対して攻撃側クランをチェック
		}

		if (player_clan_flag == true && target_clan_flag == true) {
			return true;
		} else {
			return false;
		}
	}

	public final String GetEnemyClanName(final String player_clan_name) { // 相手のクラン名を取得する
		String enemy_clan_name = null;
		if (GetDefenceClanName().toLowerCase().equals(
				player_clan_name.toLowerCase())) { // 自クランが防衛側
			String clanList[] = GetAttackClanList();
			for (int cnt = 0; cnt < clanList.length; cnt++) {
				if (clanList[cnt] != null) {
					enemy_clan_name = clanList[cnt];
					return enemy_clan_name; // リストの先頭のクラン名を返す
				}
			}
		} else { // 自クランが攻撃側
			enemy_clan_name = GetDefenceClanName();
			return enemy_clan_name;
		}
		return enemy_clan_name;
	}

	public final void delete() {
		L1World.getInstance().removeWar(this); // 戦争リストから削除
	}

	public final int GetWarType() {
		return _warType;
	}

	public final void SetWarType(int war_type) {
		_warType = war_type;
	}

	public final String GetDefenceClanName() {
		return _defenceClanName;
	}

	public final void SetDefenceClanName(String defence_clan_name) {
		_defenceClanName = defence_clan_name;
	}

	public final void InitAttackClan() {
		_attackClanList.clear();
	}

	public final void AddAttackClan(String attack_clan_name) {
		if (!_attackClanList.contains(attack_clan_name)) {
			_attackClanList.add(attack_clan_name);
		}
	}

	public final void RemoveAttackClan(String attack_clan_name) {
		if (_attackClanList.contains(attack_clan_name)) {
			_attackClanList.remove(attack_clan_name);
		}
	}

	public final boolean CheckAttackClan(String attack_clan_name) {
		if (_attackClanList.contains(attack_clan_name)) {
			return true;
		}
		return false;
	}

	public final String[] GetAttackClanList() {
		return _attackClanList.toArray(new String[_attackClanList.size()]);
	}

	public final int GetAttackClanListSize() {
		return _attackClanList.size();
	}

	public final int GetCastleId() {
		int castle_id = 0;
		if (GetWarType() == 1) { // 攻城戦
			L1Clan clan = L1World.getInstance().getClan(GetDefenceClanName());
			if (clan != null) {
				castle_id = clan.getCastleId();
			}
		}
		return castle_id;
	}

	public final L1Castle GetCastle() {
		L1Castle l1castle = null;
		if (GetWarType() == 1) { // 攻城戦
			L1Clan clan = L1World.getInstance().getClan(GetDefenceClanName());
			if (clan != null) {
				int castle_id = clan.getCastleId();
				l1castle = CastleTable.getInstance().getCastleTable(castle_id);
			}
		}
		return l1castle;
	}
}
