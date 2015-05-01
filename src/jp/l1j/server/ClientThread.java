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

package jp.l1j.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.l1j.configure.Config;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.codes.Opcodes;
import jp.l1j.server.controller.LoginController;
import jp.l1j.server.datatables.CharBuffTable;
import jp.l1j.server.datatables.ReturnLocationTable;
import jp.l1j.server.model.L1DeathMatch;
import jp.l1j.server.model.L1DragonSlayer;
import jp.l1j.server.model.L1HardinQuest;
import jp.l1j.server.model.L1Trade;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1DollInstance;
import jp.l1j.server.model.instance.L1FollowerInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import jp.l1j.server.packets.PacketHandler;
import jp.l1j.server.packets.PacketOutput;
import jp.l1j.server.packets.server.S_Disconnect;
import jp.l1j.server.packets.server.S_PacketBox;
import jp.l1j.server.packets.server.S_SummonPack;
import jp.l1j.server.packets.server.ServerBasePacket;
import jp.l1j.server.templates.L1Account;
import jp.l1j.server.utils.Cipher;
import jp.l1j.server.utils.StreamUtil;
import jp.l1j.server.utils.SystemUtil;

// Referenced classes of package jp.l1j.server:
// PacketHandler, Logins, IpTable, LoginController,
// ClanTable, IdFactory
//
public class ClientThread implements Runnable, PacketOutput {

	private static Logger _log = Logger.getLogger(ClientThread.class.getName());

	private InputStream _in;

	private OutputStream _out;

	private PacketHandler _handler;

	private L1Account _account;

	private L1PcInstance _activeChar;

	private String _ip;

	private String _hostname;

	private Socket _csocket;
	// listspr変更対策
	private int _xorByte = (byte) 0xF0;
	private long _authdata;
	// listspr変更対策

	private int _loginStatus = 0;

	private static final byte[] FIRST_PACKET = {//TODO 3.53C
		(byte) 0x4d, (byte) 0xa7, (byte) 0x32, (byte) 0x5b, (byte) 0x2e,
		(byte) 0x7e, (byte) 0xed, (byte) 0x25, (byte) 0xa5, (byte) 0x54,
		(byte) 0xb3};

	/**
	 * for Test
	 */
	protected ClientThread() {
	}

	public ClientThread(Socket socket) throws IOException {
		_csocket = socket;
		_ip = socket.getInetAddress().getHostAddress();
		if (Config.HOSTNAME_LOOKUPS) {
			_hostname = socket.getInetAddress().getHostName();
		} else {
			_hostname = _ip;
		}
		_in = socket.getInputStream();
		_out = new BufferedOutputStream(socket.getOutputStream());
		// listspr変更対策
		if (Config.LOGINS_TO_AUTOENTICATION) {
			_xorByte = (int) (Math.random() * 253 + 1);
			_authdata = new BigInteger(Integer.toString(_xorByte)).modPow(
					new BigInteger(Config.RSA_KEY_E),
					new BigInteger(Config.RSA_KEY_D)).longValue();
		}
		// listspr変更対策
		// PacketHandler 初期設定
		_handler = new PacketHandler(this);
	}

	public String getIp() {
		return _ip;
	}

	public String getHostname() {
		return _hostname;
	}

	// ClientThreadによる一定間隔自動セーブを制限する為のフラグ（true:制限 false:制限無し）
	// 現在はC_LoginToServerが実行された際にfalseとなり、
	// C_NewCharSelectが実行された際にtrueとなる
	private boolean _charRestart = true;

	public void CharReStart(boolean flag) {
		_charRestart = flag;
	}

	private byte[] readPacket() throws Exception {
		try {
			int hiByte = _in.read();
			int loByte = _in.read();

			// listspr変更対策
			if (Config.LOGINS_TO_AUTOENTICATION) {
				hiByte ^= _xorByte;
				loByte ^= _xorByte;
			}
			// listspr変更対策
			if (loByte < 0) {
				throw new RuntimeException();
			}
			int dataLength = (loByte * 256 + hiByte) - 2;

			byte data[] = new byte[dataLength];

			int readSize = 0;

			for (int i = 0; i != -1 && readSize < dataLength; readSize += i) {
				i = _in.read(data, readSize, dataLength - readSize);
			}

			if (readSize != dataLength) {
				_log.warning("Incomplete Packet is sent to the server, closing connection.");
				throw new RuntimeException();
			}
			// listspr変更対策
			if (Config.LOGINS_TO_AUTOENTICATION) {
				for (int i = 0; i < dataLength; i++) {
					data[i] = (byte) (data[i] ^ _xorByte);
				}
			}
			// listspr変更対策
			return _cipher.decrypt(data);
		} catch (IOException e) {
			throw e;
		}
	}

	private long _lastSavedTime = System.currentTimeMillis();

	private long _lastSavedTime_inventory = System.currentTimeMillis();

	private Cipher _cipher;

	private void doAutoSave() throws Exception {
		if (_activeChar == null || _charRestart) {
			return;
		}
		try {
			// キャラクター情報
			if (Config.AUTOSAVE_INTERVAL * 1000 < System.currentTimeMillis()
					- _lastSavedTime) {
				_activeChar.save();
				_lastSavedTime = System.currentTimeMillis();
			}

			// 所持アイテム情報
			if (Config.AUTOSAVE_INTERVAL_INVENTORY * 1000 < System
					.currentTimeMillis()
					- _lastSavedTime_inventory) {
				_activeChar.saveInventory();
				_lastSavedTime_inventory = System.currentTimeMillis();
			}
		} catch (Exception e) {
			_log.warning("Client autosave failure.");
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw e;
		}
	}

	@Override
	public void run() {
		_log.info(String.format(I18N_CONNECTED_TO_THE_SERVER, _hostname));
		System.out.println(String.format(I18N_MEMORY_USEAGE, SystemUtil.getUsedMemoryMB()));
		System.out.println(I18N_WAITING_FOR_CLIENT);

		/*
		 * クライアントからのパケットをある程度制限する。 理由：不正の誤検出が多発する恐れがあるため
		 * ex1.サーバに過負荷が掛かっている場合、負荷が落ちたときにクライアントパケットを一気に処理し、結果的に不正扱いとなる。
		 * ex2.サーバ側のネットワーク（下り）にラグがある場合、クライアントパケットが一気に流れ込み、結果的に不正扱いとなる。
		 * ex3.クライアント側のネットワーク（上り）にラグがある場合、以下同様。
		 *
		 * 無制限にする前に不正検出方法を見直す必要がある。
		 */
		HcPacket movePacket = new HcPacket(M_CAPACITY);
		HcPacket hcPacket = new HcPacket(H_CAPACITY);
		GeneralThreadPool.getInstance().execute(movePacket);
		GeneralThreadPool.getInstance().execute(hcPacket);

		ClientThreadObserver observer = new ClientThreadObserver(
				Config.AUTOMATIC_KICK * 60 * 1000); // 自動切断までの時間（単位:ms）

		// クライアントスレッドの監視
		if (Config.AUTOMATIC_KICK > 0) {
			observer.start();
		}

		try {
			/**
			 * このキーは暗号化にのみ使われるもので、opcode等には影響を及ぼさない模様。
			 * 32bitなら何でも良いし、クライアント毎に変えても良い。
			 */
			// int key = 0x1a986541;
			String keyHax = Integer.toHexString((int) (Math.random() * 2147483647) + 1);
			int key = Integer.parseInt(keyHax, 16);

			byte Bogus = (byte) (FIRST_PACKET.length + 7);
			// listspr変更対策
			if (Config.LOGINS_TO_AUTOENTICATION) {
				_out.write((int) (_authdata & 0xff));
				_out.write((int) (_authdata >> 8 & 0xff));
				_out.write((int) (_authdata >> 16 & 0xff));
				_out.write((int) (_authdata >> 24 & 0xff));
				_out.flush();
			}
			// listspr変更対策
			_out.write(Bogus & 0xFF);
			_out.write(Bogus >> 8 & 0xFF);
			// _out.write(0x20); // 2.70C
			// _out.write(0x7d); // 3.0c
			_out.write(Opcodes.S_OPCODE_INITPACKET);// 3.5C
			_out.write((byte) (key & 0xFF));
			_out.write((byte) (key >> 8 & 0xFF));
			_out.write((byte) (key >> 16 & 0xFF));
			_out.write((byte) (key >> 24 & 0xFF));

			_out.write(FIRST_PACKET);
			_out.flush();

			_cipher = new Cipher(key);

			while (true) {
				if (Config.AUTOSAVE) {
					doAutoSave();
				}

				byte data[] = null;
				try {
					data = readPacket();
				} catch (Exception e) {
					break;
				}
				// _log.finest("[C]\n" + new
				// ByteArrayUtil(data).dumpToString());

				int opcode = data[0] & 0xFF;

				// 多重ログイン対策
				if (opcode == Opcodes.C_OPCODE_COMMONCLICK
						|| opcode == Opcodes.C_OPCODE_CHANGECHAR) {
					_loginStatus = 1;
				}
				if (opcode == Opcodes.C_OPCODE_LOGINTOSERVER) {
					if (_loginStatus != 1) {
						continue;
					}
				}

				if (opcode == Opcodes.C_OPCODE_LOGINTOSERVEROK
						|| opcode == Opcodes.C_OPCODE_RETURNTOLOGIN) {
					_loginStatus = 0;
				}

				if (opcode != Opcodes.C_OPCODE_KEEPALIVE) {
					// C_OPCODE_KEEPALIVE以外の何かしらのパケットを受け取ったらObserverへ通知
					observer.packetReceived();
				}
				// nullの場合はキャラクター選択前なのでOpcodeの取捨選択はせず全て実行
				if (_activeChar == null) {
					_handler.handlePacket(data, _activeChar);
					continue;
				}

				// 以降、PacketHandlerの処理状況がClientThreadに影響を与えないようにする為の処理
				// 目的はOpcodeの取捨選択とClientThreadとPacketHandlerの切り離し

				// 破棄してはいけないOpecode群
				// リスタート、アイテムドロップ、アイテム削除
				if (opcode == Opcodes.C_OPCODE_CHANGECHAR
						|| opcode == Opcodes.C_OPCODE_DROPITEM
						|| opcode == Opcodes.C_OPCODE_DELETEINVENTORYITEM) {
					_handler.handlePacket(data, _activeChar);
				} else if (opcode == Opcodes.C_OPCODE_MOVECHAR) {
					// 移動はなるべく確実に行う為、移動専用スレッドへ受け渡し
					movePacket.requestWork(data);
				} else {
					// パケット処理スレッドへ受け渡し
					hcPacket.requestWork(data);
				}
			}
		} catch (Throwable e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			try {
				if (_activeChar != null) {
					quitGame(_activeChar);

					synchronized (_activeChar) {
						_activeChar.saveInventory();
						// キャラクターをワールド内から除去
						_activeChar.logout();
						setActiveChar(null);
					}
				}

				// 念のため送信
				sendPacket(new S_Disconnect());

				StreamUtil.close(_out, _in);
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} finally {
				LoginController.getInstance().logout(this);
			}
		}
		_csocket = null;
		_log.fine("Server thread[C] stopped");
		if (_kick < 1) {
			_log.info(String.format(I18N_TERMINATED_THE_CONNECTION, _hostname));
			System.out.println(String.format(I18N_MEMORY_USEAGE, SystemUtil.getUsedMemoryMB()));
			System.out.println(I18N_WAITING_FOR_CLIENT);
		}
		return;
	}

	private int _kick = 0;

	public void kick() {
		sendPacket(new S_Disconnect());
		_kick = 1;
		StreamUtil.close(_out, _in);
	}

	private static final int M_CAPACITY = 3; // 移動要求を一辺に受け付ける最大容量

	private static final int H_CAPACITY = 2;// 行動要求を一辺に受け付ける最大容量

	// キャラクターの行動処理スレッド
	class HcPacket implements Runnable {
		private final Queue<byte[]> _queue;

		private PacketHandler _handler;

		public HcPacket() {
			_queue = new ConcurrentLinkedQueue<byte[]>();
			_handler = new PacketHandler(ClientThread.this);
		}

		public HcPacket(int capacity) {
			_queue = new LinkedBlockingQueue<byte[]>(capacity);
			_handler = new PacketHandler(ClientThread.this);
		}

		public void requestWork(byte data[]) {
			_queue.offer(data);
		}

		@Override
		public void run() {
			byte[] data;
			while (_csocket != null) {
				data = _queue.poll();
				if (data != null) {
					try {
						_handler.handlePacket(data, _activeChar);
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				} else {
					try {
						Thread.sleep(10);
					} catch (Exception e) {
					}
				}
			}
			return;
		}
	}

	private static Timer _observerTimer = new Timer();



	// クライアントスレッドの監視タイマー
	class ClientThreadObserver extends TimerTask {
		private int _checkct = 1;

		private final int _disconnectTimeMillis;

		public ClientThreadObserver(int disconnectTimeMillis) {
			_disconnectTimeMillis = disconnectTimeMillis;
		}

		public void start() {
			_observerTimer.scheduleAtFixedRate(ClientThreadObserver.this, 0,
					_disconnectTimeMillis);
		}

		@SuppressWarnings("null")
		@Override
		public void run() {

			final L1PcInstance pc = null;

			try {
				if (_csocket == null) {
					cancel();
					return;
				}

				if (_checkct > 0) {
					_checkct = 0;
					return;
				}

				if (_activeChar == null // キャラクター選択前
						|| _activeChar != null && !_activeChar.isPrivateShop() && !_activeChar.isGm()) { // 個人商店中とGMは除外
					kick();
					_log.warning(String.format(I18N_KILLED_THE_CONNECTION, _hostname));
					cancel();
					return;
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				cancel();
			}
		}

		public void packetReceived() {
			_checkct++;
		}
	}

	@Override
	public void sendPacket(ServerBasePacket packet) {
		synchronized (this) {
			try {
				byte content[] = packet.getContent();
				byte data[] = Arrays.copyOf(content, content.length);
				_cipher.encrypt(data);
				int length = data.length + 2;

				_out.write(length & 0xff);
				_out.write(length >> 8 & 0xff);
				_out.write(data);
				_out.flush();
			} catch (Exception e) {
			}
		}
	}

	public void close() throws IOException {
		_csocket.close();
	}

	public void setActiveChar(L1PcInstance pc) {
		_activeChar = pc;
	}

	public L1PcInstance getActiveChar() {
		return _activeChar;
	}

	public void setAccount(L1Account account) {
		_account = account;
	}

	public L1Account getAccount() {
		return _account;
	}

	public String getAccountName() {
		if (_account == null) {
			return null;
		}
		return _account.getName();
	}

	public static void quitGame(L1PcInstance pc) {
		// 死亡していたら街に戻し、空腹状態にする
		if (pc.isDead()) {
			int[] loc = ReturnLocationTable.getReturnLocation(pc, true);
			pc.setX(loc[0]);
			pc.setY(loc[1]);
			pc.setMap((short) loc[2]);
			pc.setCurrentHp(pc.getLevel());
			pc.setFood(40);
		}

		// トレードを中止する
		if (pc.getTradeID() != 0) { // トレード中
			L1Trade trade = new L1Trade();
			trade.TradeCancel(pc);
		}

		// 決闘を中止する
		if (pc.getFightId() != 0) {
			pc.setFightId(0);
			L1PcInstance fightPc = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getFightId());
			if (fightPc != null) {
				fightPc.setFightId(0);
				fightPc
						.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
			}
		}

		// パーティーを抜ける
		if (pc.isInParty()) { // パーティー中
			pc.getParty().leaveMember(pc);
		}

		// チャットパーティーを抜ける
		if (pc.isInChatParty()) { // チャットパーティー中
			pc.getChatParty().leaveMember(pc);
		}

		// ペットをワールドマップ上から消す
		// サモンの表示名を変更する
		Object[] petList = pc.getPetList().values().toArray();
		for (Object petObject : petList) {
			if (petObject instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petObject;
				// ペット空腹度タイマー停止
				pet.stopFoodTimer(pet);
				pet.dropItem(pet);
				pc.getPetList().remove(pet.getId());
				pet.deleteMe();
			}
			if (petObject instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) petObject;
				for (L1PcInstance visiblePc : L1World.getInstance()
						.getVisiblePlayer(summon)) {
					visiblePc.sendPackets(new S_SummonPack(summon, visiblePc,
							false));
				}
			}
		}

		// マジックドールをワールドマップ上から消す
		Object[] dollList = pc.getDollList().values().toArray();
		for (Object dollObject : dollList) {
			L1DollInstance doll = (L1DollInstance) dollObject;
			if (doll.isChargeDoll()) { // 課金マジックドールのタイマーを停止
				L1ItemInstance item = pc.getInventory().getItem(doll.getItemObjId());
				item.stopChargeTimer();
			}
			doll.deleteDoll();
		}

		// 従者をワールドマップ上から消し、同地点に再出現させる
		Object[] followerList = pc.getFollowerList().values().toArray();
		for (Object followerObject : followerList) {
			L1FollowerInstance follower = (L1FollowerInstance) followerObject;
			follower.setParalyzed(true);
			follower.spawn(follower.getNpcTemplate().getNpcId(), follower
					.getX(), follower.getY(), follower.getHeading(), follower
					.getMapId());
			follower.deleteMe();
		}

		L1DeathMatch.getInstance().checkLeaveGame(pc);
		if (L1HardinQuest.getInstance().getActiveMaps(pc.getMapId()) != null) {
			L1HardinQuest.getInstance().getActiveMaps(pc.getMapId())
					.checkLeaveGame(pc);
		}

		// ドラゴンポータル情報を削除する
		if (pc.getPortalNumber() != -1) {
			L1DragonSlayer.getInstance().removePlayer(pc, pc.getPortalNumber());
		}

		// エンチャントをDBのcharacter_buffに保存する
		CharBuffTable.delete(pc.getId());
		CharBuffTable.save(pc);
		pc.clearSkillEffectTimer();

		// マップリミッターを停止する
		pc.stopMapLimiter();

		// pcのモニターをstopする。
		pc.stopEtcMonitor();
		// オンライン状態をOFFにし、DBにキャラクター情報を書き込む
		pc.setOnlineStatus(0);
		pc.setLogoutTime();
		try {
			pc.save();
			pc.saveInventory();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}
}
