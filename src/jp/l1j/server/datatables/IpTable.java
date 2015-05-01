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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.server.utils.L1DatabaseFactory;
import jp.l1j.server.utils.NetAddressUtil;
import jp.l1j.server.utils.SqlUtil;

public class IpTable {
	private static Logger _log = Logger.getLogger(IpTable.class.getName());

	private static ArrayList<String> _banip;
	
	private static ArrayList<String> _host;

	private static ArrayList<Integer> _mask;

	public static boolean isInitialized;

	private static IpTable _instance;

	public static IpTable getInstance() {
		if (_instance == null) {
			_instance = new IpTable();
		}
		return _instance;
	}

	private IpTable() {
		if (!isInitialized) {
			_banip = new ArrayList<String>();
			_host = new ArrayList<String>();
			_mask = new ArrayList<Integer>();
			getIpTable();
		}
	}

	public void banIp(String ip, String host) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO ban_ips SET ip=?, host=?, mask=?");
			pstm.setString(1, ip);
			pstm.setString(2, host);
			pstm.setInt(3, 32);
			pstm.execute();
			_banip.add(ip);
			_host.add(host);
			_mask.add(32);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public boolean isBannedIp(String s) {
		return _banip.contains(s);
	}

	public boolean isBannedIpMask(String s) {
		for(int i=0;i < _banip.size();i++){
			String banip_tmp = _banip.get(i);
			int mask_tmp = _mask.get(i);
			if(NetAddressUtil.isScorp(s, banip_tmp, mask_tmp)){
				return true;
			}
		}
		return false;
	}

	public void getIpTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM ban_ips");
			rs = pstm.executeQuery();
			while (rs.next()) {
				_banip.add(rs.getString(1));
				_host.add(rs.getString(2));
				_mask.add(rs.getInt(3));
			}
			isInitialized = true;
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(rs);
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
	}

	public boolean liftBanIp(String ip, String host) {
		boolean ret = false;
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM ban_ips WHERE ip=?");
			pstm.setString(1, ip);
			pstm.execute();
			_banip.remove(_banip.indexOf(ip));
			_host.remove(_host.indexOf(host));
			_mask.remove(_mask.indexOf(32));
			ret = true;
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SqlUtil.close(pstm);
			SqlUtil.close(con);
		}
		return ret;
	}
}
