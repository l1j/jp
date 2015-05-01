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
package jp.l1j.server.model.poison;


import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.L1Character;
import jp.l1j.server.packets.server.S_Poison;
import jp.l1j.server.packets.server.S_ServerMessage;

public abstract class L1Poison {
	protected static boolean isValidTarget(L1Character cha) {
		if (cha == null) {
			return false;
		}
		// 毒は重複しない
		if (cha.getPoison() != null) {
			return false;
		}

		if (!(cha instanceof L1PcInstance)) {
			return true;
		}

		L1PcInstance player = (L1PcInstance) cha;
		// ゼニス リング装備中、バフォ メットアーマー装備中 、ベノム レジスト中
		// アンタラス グランド プレートメイル、ローブ、レザーアーマー、スケイルメイル
		if (player.getInventory().checkEquipped(20298)
				|| player.getInventory().checkEquipped(20117)
				|| player.getInventory().checkEquipped(21172)
				|| player.getInventory().checkEquipped(21173)
				|| player.getInventory().checkEquipped(21174)
				|| player.getInventory().checkEquipped(21175)
				|| player.hasSkillEffect(104)) {
			return false;
		}
		return true;
	}

	// 微妙・・・素直にsendPacketsをL1Characterへ引き上げるべきかもしれない
	protected static void sendMessageIfPlayer(L1Character cha, int msgId) {
		if (!(cha instanceof L1PcInstance)) {
			return;
		}

		L1PcInstance player = (L1PcInstance) cha;
		player.sendPackets(new S_ServerMessage(msgId));
	}

	/**
	 * この毒のエフェクトIDを返す。
	 * 
	 * @see S_Poison#S_Poison(int, int)
	 * 
	 * @return S_Poisonで使用されるエフェクトID
	 */
	public abstract int getEffectId();

	/**
	 * この毒の効果を取り除く。<br>
	 * 
	 * @see L1Character#curePoison()
	 */
	public abstract void cure();
}
