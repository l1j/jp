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

package jp.l1j.server.utils;

public class Cipher {
	/* 靜態私用變數 */
	/**
	 * 將亂數數值混淆用的混淆密碼 (32位元,靜態唯讀) 該數值只有在Cipher初始化時才會被調用
	 */
	private final static int _1 = 0x9c30d539;
	/**
	 * 初始的解碼數值
	 */
	private final static int _2 = 0x930fd7e2;
	/**
	 * 將亂數數值混淆用的混淆密碼 (32位元,靜態唯讀) 該數值只有在Cipher初始化時才會被調用
	 */
	private final static int _3 = 0x7c72e993;
	/**
	 * 將封包數值混淆用的混淆密碼 (32位元,靜態唯讀) 該數值只有在編碼或解碼時才會被調用
	 */
	private final static int _4 = 0x287effc3;

	/* 動態私用變數 */
	/**
	 * 參考用的編碼鑰匙 (位元組陣列長度為8,相當於一個64位元的長整數)
	 */
	private final byte[] eb = new byte[8];
	/**
	 * 參考用的解碼鑰匙 (位元組陣列長度為8,相當於一個64位元的長整數)
	 */
	private final byte[] db = new byte[8];
	/**
	 * 參考用的封包鑰匙 (位元組陣列長度為4,相當於一個32位元的整數)
	 */
	private final byte[] tb = new byte[4];

	// 初始化流程:
	// 1.建立新的鑰匙暫存器(keys),將編碼鑰匙與混淆鑰匙(_1)進行混淆並帶入keys[0],將初始的解碼數值帶入key[1]
	// 2.將key[0]向右反轉19個位元(0x13)並帶入key[0]
	// 3.將key[0]與key[1]與混淆鑰匙(_3)進行混淆並帶入key[1]
	// 4.將keys轉換為64位元的位元組陣列
	// @param key, 由亂數產生的編碼鑰匙
	/**
	 * 指定された暗号鍵を使用して、新しいCipherオブジェクトを作成します。
	 * 
	 * @param key
	 *            暗号化に使用される32bitの暗号鍵
	 */
	public Cipher(int key) {
		int[] keys = { key ^ _1, _2 };
		keys[0] = Integer.rotateLeft(keys[0], 0x13);
		keys[1] ^= keys[0] ^ _3;

		for (int i = 0; i < keys.length; i++) {
			for (int j = 0; j < tb.length; j++) {
				eb[(i * 4) + j] = db[(i * 4) + j] = (byte) (keys[i] >> (j * 8) & 0xff);
			}
		}
	}

	/**
	 * 指定されたbyte配列を暗号化します。
	 * 
	 * @param data
	 *            暗号化するbyte配列。
	 * @return 暗号化されたbyte配列。これはパラメータのbyte配列と同一のオブジェクトです。
	 */
	public byte[] encrypt(byte[] data) {
		for (int i = 0; i < tb.length; i++) {
			tb[i] = data[i];
		}

		data[0] ^= eb[0];

		for (int i = 1; i < data.length; i++) {
			data[i] ^= data[i - 1] ^ eb[i & 7];
		}

		data[3] ^= eb[2];
		data[2] ^= eb[3] ^ data[3];
		data[1] ^= eb[4] ^ data[2];
		data[0] ^= eb[5] ^ data[1];
		update(eb, tb);
		return data;
	}

	/**
	 * 指定されたbyte配列を復号化します。
	 * 
	 * @param data
	 *            復号化するbyte配列。
	 * @return 復号化されたbyte配列。これはパラメータのbyte配列と同一のオブジェクトです。
	 */
	public byte[] decrypt(byte[] data) {
		data[0] ^= db[5] ^ data[1];
		data[1] ^= db[4] ^ data[2];
		data[2] ^= db[3] ^ data[3];
		data[3] ^= db[2];

		for (int i = data.length - 1; i >= 1; i--) {
			data[i] ^= data[i - 1] ^ db[i & 7];
		}

		data[0] ^= db[0];
		update(db, data);
		return data;
	}

	/**
	 * 將指定的鑰匙進行混淆並與混淆鑰匙相加(_4)
	 * 
	 * @param data
	 *            , 受保護的資料
	 * @return data, 原始的資料
	 */
	private void update(byte[] data, byte[] ref) {
		for (int i = 0; i < tb.length; i++) {
			data[i] ^= ref[i];
		}

		int int32 = (((data[7] & 0xFF) << 24) | ((data[6] & 0xFF) << 16)
				| ((data[5] & 0xFF) << 8) | (data[4] & 0xFF))
				+ _4;

		for (int i = 0; i < tb.length; i++) {
			data[i + 4] = (byte) (int32 >> (i * 8) & 0xff);
		}
	}
}