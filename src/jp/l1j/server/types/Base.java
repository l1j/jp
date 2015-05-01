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
package jp.l1j.server.types;

public class Base {

	/** X方向 */
	public static final int[] HEADING_TABLE_X = { 0, 1, 1, 1, 0, -1, -1, -1 };

	/** Y方向 */
	public static final int[] HEADING_TABLE_Y = { -1, -1, 0, 1, 1, 1, 0, -1 };

	/** スキル分類: 0=NORMAL 1=LOGIN 2=SPELLSC 3=NPCBUFF 4=GMBUFF */
	public static final int[] SKILL_TYPE = { 0, 1, 2, 3, 4 };

	/** ターゲット分類: 0=NULL 1=PC_PC 2=PC_NPC 3=NPC_PC 4=NPC_NPC */
	public static final int[] TARGET_TYPE = { 0, 1, 2, 3, 4 };

	/** */
	public static final double[] TAN_225 = { Math.tan(-22.5), Math.tan(22.5) };
	public static final double[] TAN_675 = { Math.tan(-62.5), Math.tan(62.5) };

	/** 被攻撃分類: 0=正常 1=バリア 2=凍結 4=アースバインド 8=ブリザード 16=ブリザードブレス*/
	public static final int[] STATUS_TYPE = { 0, 1, 2, 4, 8, 16 };

}
