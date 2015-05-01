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

import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.SqlUtil;

public class AcceleratorLogTable {
	private static Logger _log = Logger.getLogger(AcceleratorLogTable.class.getName());

	public void storeLogAccelerator(L1PcInstance pc) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO accelerator_logs SET account_id=?, char_id=?, clan_id=?, map_id=?, loc_x=?, loc_y=?, datetime=SYSDATE()");
			pstm.setInt(1, pc.getAccountId());
			pstm.setInt(2, pc.getId());
			pstm.setInt(3, pc.getClanId());
			pstm.setInt(4, pc.getMapId());
			pstm.setInt(5, pc.getX());
			pstm.setInt(6, pc.getY());
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}
}
