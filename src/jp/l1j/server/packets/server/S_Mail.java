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

package jp.l1j.server.packets.server;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.datatables.MailTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.templates.L1Mail;

// Referenced classes of package jp.l1j.server.serverpackets:
// ServerBasePacket

public class S_Mail extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_WhoAmount.class.getName());
	private static final String S_MAIL = "[S] S_Mail";
	private byte[] _byte = null;

/**
 * 
 * //一般信件的標題 [Server] opcode = 48 3封 0000: [30][00 03][00][27 00 00
 * 00][00][09][01][12][32 32 33 33 0...'.......2233 0010: 32 31 00] [31 00]00
 * [00] [28 00 00 00] [01] 09 01 12 32 21.1...(.......2 0020: 32 33 33 32 31 00
 * 31 00 00 00 2a 00 00 00 00 09 23321.1...*..... 0030: 01 13 32 32 33 33 32 31
 * 00 31 00 00 00 93 0a 00 ..223321.1......
 * 
 * [Server] opcode = 48 2封 0000: 30 /00 02/ 00/ 27 00 00 00/ 00/ 09 01 12 32 32
 * 33 33 0...'.......2233 0010: 32 31 00 31 00 00 00 28 00 00 00 00 09 01 12 32
 * 21.1...(.......2 0020: 32 33 33 32 31 00 31 00 00 00 96 3d c4 79 1a 4d
 * 23321.1....=.y.M
 */
	// 打開收信夾 ?封信件顯示標題
	public S_Mail(L1PcInstance pc, int type) {
		ArrayList<L1Mail> mails = new ArrayList<L1Mail>();
		for (L1Mail mail : MailTable.getInstance().getAllMail()) {
			if(mail.getInBoxId() == pc.getId()){
				if (mail.getType() == type) {
					mails.add(mail);
				}
			}
		}
		if (mails.isEmpty()) {
			return;
		}

		writeC(Opcodes.S_OPCODE_MAIL);
		writeC(type);
		writeH(mails.size());
		
		for (int i = 0; i < mails.size(); i++) {
			L1Mail mail = mails.get(i);
			writeD(mail.getId());
			writeC(mail.getReadStatus());
			writeD((int) (mail.getDate().getTime() / 1000));
			writeC(mail.getSenderName().equalsIgnoreCase(pc.getName()) ? 1 : 0);
			writeS(mail.getSenderName().equalsIgnoreCase(pc.getName()) ? mail.getReceiverName() : mail.getSenderName());
			writeBytes(mail.getSubject());
		}
	}
/**
 * //無法傳送信件 [Server] opcode = 48 0000: 30 20 00 45 54 fa 00 b5
 */	
	public S_Mail(int type, boolean isDelivered) { // 受信者にメール通知
		writeC(Opcodes.S_OPCODE_MAIL);
		writeC(type);
		writeC(isDelivered ? 1 : 0);
	}

	public S_Mail(L1PcInstance pc, int mailId, boolean isDraft){
		MailTable.getInstance();
		L1Mail mail = MailTable.getMail(mailId);
		writeC(Opcodes.S_OPCODE_MAIL);
		writeC(0x50);
		writeD(mailId);
		writeC(isDraft ? 1 : 0);
		writeS(pc.getName());
		writeBytes(mail.getSubject());
	}

/**
 * //讀取一般信件 [Server] opcode = 48 0000: [30] [10] [29 00 00 00] [32 00] 00 00 a4
 * cb 00 03 08 00 0.)...2.........
 * 
 * //信件存到保管箱 [Server] opcode = 48 0000: [30] [40] [2b 00 00 00] [01] 95
 * 
 */
	public S_Mail(int mailId,int type) {
		// 刪除信件
		// 0x30: 刪除一般 0x31:刪除血盟 0x32:?般存到保管箱 0x40:刪除保管箱
		if (type == 0x30 || type == 0x31 || type == 0x32 || type == 0x40) {
			writeC(Opcodes.S_OPCODE_MAIL);
			writeC(type);
			writeD(mailId);
			writeC(1);
			return;
		}
		L1Mail mail = MailTable.getInstance().getMail(mailId);
		if (mail != null) {
			writeC(Opcodes.S_OPCODE_MAIL);
			writeC(type);
			writeD(mail.getId());
			writeBytes(mail.getContent());
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_MAIL;
	}
}
