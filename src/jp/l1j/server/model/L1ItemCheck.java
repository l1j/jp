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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.utils.SqlUtil;

/**
 * 負責物品狀態檢查是否作弊
 */
public class L1ItemCheck {
        private int itemId;
        private boolean isStackable = false;

        public boolean ItemCheck(L1ItemInstance item, L1PcInstance pc) {
                itemId = item.getItem().getItemId();
                int itemCount = item.getCount();
                boolean isCheat = false;

                if ((findWeapon() || findArmor()) && itemCount != 1) {
                        isCheat = true;
                } else if (findEtcItem()) {
                        // 不可堆疊的道具卻堆疊，就視為作弊
                        if (!isStackable && itemCount != 1) {
                                isCheat = true;
                                // 金幣大於20億以及金幣負值則為作弊
                        } else if (itemId == 40308
                                        && (itemCount > 2000000000 || itemCount < 0)) {
                                isCheat = true;
                                // 可堆疊道具(金幣除外)堆疊超過十萬個以及堆疊負值設定為作弊
                        } else if (isStackable && itemId != 40308
                                        && (itemCount > 100000 || itemCount < 0)) {
                                isCheat = true;
                        }
                }
                if (isCheat) {
                        // 作弊直接刪除物品
                        pc.getInventory().removeItem(item, itemCount);
                }
                return isCheat;
        }

        private boolean findWeapon() {
                Connection con = null;
                PreparedStatement pstm = null;
                ResultSet rs = null;
                boolean inWeapon = false;

                try {
                        con = L1DatabaseFactory.getInstance().getConnection();
                        pstm = con.prepareStatement("SELECT * FROM weapons WHERE id = ?");
                        pstm.setInt(1, itemId);
                        rs = pstm.executeQuery();
                        if (rs != null) {
                                if (rs.next()) {
                                        inWeapon = true;
                                }
                        }
                } catch (Exception e) {
                } finally {
                        SqlUtil.close(rs, pstm, con);
                }
                return inWeapon;
        }

        private boolean findArmor() {
                Connection con = null;
                PreparedStatement pstm = null;
                ResultSet rs = null;
                boolean inArmor = false;
                try {
                        con = L1DatabaseFactory.getInstance().getConnection();
                        pstm = con.prepareStatement("SELECT * FROM armors WHERE id = ?");
                        pstm.setInt(1, itemId);
                        rs = pstm.executeQuery();
                        if (rs != null) {
                                if (rs.next()) {
                                        inArmor = true;
                                }
                        }
                } catch (Exception e) {
                } finally {
                        SqlUtil.close(rs, pstm, con);
                }
                return inArmor;
        }

        private boolean findEtcItem() {
                Connection con = null;
                PreparedStatement pstm = null;
                ResultSet rs = null;
                boolean inEtcitem = false;
                try {
                        con = L1DatabaseFactory.getInstance().getConnection();
                        pstm = con.prepareStatement("SELECT * FROM etc_items WHERE id = ?");
                        pstm.setInt(1, itemId);
                        rs = pstm.executeQuery();
                        if (rs != null) {
                                if (rs.next()) {
                                        inEtcitem = true;
                                        isStackable = rs.getInt("stackable") == 1 ? true : false;
                                }
                        }
                } catch (Exception e) {
                } finally {
                        SqlUtil.close(rs, pstm, con);
                }
                return inEtcitem;
        }
}