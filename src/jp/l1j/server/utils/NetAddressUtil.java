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

import java.math.BigInteger;
//参照 http://blog.zaq.ne.jp/oboe2uran/article/232/
public final class NetAddressUtil{
	private NetAddressUtil(){}
	/**
	* サブネットマスク検査.
	* 検査したいＩＰアドレス、セグメントＩＰアドレス、マスク長を指定して、
	* サブネットマスクしたＩＰアドレスの範囲であるかを検査する。
	* @param ipaddr 検査したいＩＰアドレス文字列、"xxx.xxx.xxx.xxx" 形式
	* @param segaddr セグメントＩＰアドレス、"xxx.xxx.xxx.xxx" 形式
	* @param len マスク長、1～32
	* @return true=OK,false=NG
	*/
	public static boolean isScorp(String ipaddr,String segaddr,int len){
		byte[] bmask = getMask(len);
		byte[] bseg = addrTobyte(segaddr);
		BigInteger minBigInt = new BigInteger(byteAND(bseg,bmask));
		byte[] bmax = byteOR(bseg,byteXOR(bmask,addrTobyte("255.255.255.255")));
		BigInteger maxBigInt = new BigInteger(bmax);
		BigInteger bchk = new BigInteger(addrTobyte(ipaddr));
		return minBigInt.compareTo(bchk) <= 0 && bchk.compareTo(maxBigInt) <= 0 ? true : false;
	}
	/**
	* サブネットマスクした値の最小アドレスを返す。
	* （例）
	* 192.168.87.22 とマスク長 24 を指定した場合、192.168.87.0 を byte[] で返す
	* @param segaddr IPアドレス"xxx.xxx.xxx.xxx" 形式
	* @param len マスク長、1～32
	* @return 最小アドレスbyte[]
	*/
	public static byte[] minSegment(String segaddr,int len){
		byte[] bmask = getMask(len);
		byte[] bseg = addrTobyte(segaddr);
		byte[] bw = byteAND(bseg,bmask);
		if (bw[0]==0 && bw[1]==0 && bw[2]==0 && bw[3]==0){
			 byte[] b = {0,0,0,0};
			 byte[] br = new byte[4];
			 int p = b.length > 4 ? 1 : 0;
			 for(int i=0;i < br.length;i++,p++){
			    br[i] = b[p];
			 }
			 	return br;
		}
		byte[] b = new BigInteger(1,bw).toByteArray();
		byte[] br = new byte[4];
		int p = b.length > 4 ? 1 : 0;
		for(int i=0;i < br.length;i++,p++){
			br[i] = b[p];
		}
		return br;
	}
	/**
	* サブネットマスクした値の最大アドレスを返す。
	* （例）
	* 192.168.87.22 とマスク長 24 を指定した場合、192.168.87.255 を byte[] で返す
	* @param segaddr IPアドレス"xxx.xxx.xxx.xxx" 形式
	* @param len マスク長、1～32
	* @return 最大アドレスbyte[]
	*/
	public static byte[] maxSegment(String segaddr,int len){
		byte[] bmask = getMask(len);
		byte[] bseg = addrTobyte(segaddr);
		byte[] bmax = byteOR(bseg,byteXOR(bmask,addrTobyte("255.255.255.255")));
		byte[] b = new BigInteger(1,bmax).toByteArray();
		byte[] br = new byte[4];
		int p = b.length > 4 ? 1 : 0;
		for(int i=0;i < br.length;i++,p++){
			br[i] = b[p];
		}
		return br;
	}
	/**
	* マスク値で、有効アドレス数を求める
	* @param len len マスク長、1～32
	* @return 有効アドレス数
	*/
	public static long countSegment(int len){
		byte[] bmask = getMask(len);
		byte[] bseg = new byte[]{-1,-1,-1,-1};
		byte[] bx = new BigInteger(1
		            ,byteOR(bseg,byteXOR(bmask,addrTobyte("255.255.255.255"))))
		         .toByteArray();
		byte[] bw = byteAND(bseg,bmask);
		if (bw[0]==0 && bw[1]==0 && bw[2]==0 && bw[3]==0){
			 byte[] b = {0,0,0,0};
			 byte[] br = new byte[4];
			 int p = b.length > 4 ? 1 : 0;
			 for(int i=0;i < br.length;i++,p++){
				 br[i] = b[p];
			 }
			 byte[] bmax = new byte[4];
			 byte[] bmin = new byte[4];
			 int k = bx.length > 4 ? 1 : 0;
			 for(int i=0;i < bmax.length;i++,k++){
				bmax[i] = bx[k];
				bmin[i] = br[k];
			 }
			 BigInteger bigintMax = new BigInteger(1,bmax);
			 BigInteger bigintMin = new BigInteger(1,bmin);
			 return bigintMax.longValue() - bigintMin.longValue() + 1;
		}
		byte[] bn = new BigInteger(1,bw).toByteArray();
		byte[] bmax = new byte[4];
		byte[] bmin = new byte[4];
		int p = bx.length > 4 ? 1 : 0;
		for(int i=0;i < bmax.length;i++,p++){
			 bmax[i] = bx[p];
			 bmin[i] = bn[p];
		}
		BigInteger bigintMax = new BigInteger(1,bmax);
		BigInteger bigintMin = new BigInteger(1,bmin);
		return bigintMax.longValue() - bigintMin.longValue() + 1;
	}
	/**
	* ＩＰアドレス文字列、"xxx.xxx.xxx.xxx" → byte[].
	* @param s ＩＰアドレス文字列
	* @return byte[]
	*/
	public static byte[] addrTobyte(String s){
		String[] sp = s.split("\\.");
		byte[] b = new byte[4];
		for(int i=0;i < 4;i++){
			b[i] = (byte)(Integer.parseInt(sp[i]));
		}
		return b;
	}

	/**
	* マスク長→サブネットマスク byte[]
	* @param n マスク長
	* @return サブネットマスク byte[]
	*/
	public static byte[] getMask(int n){
		byte[] b = new BigInteger(1,new byte[]{-1,-1,-1,-1})
		                   .shiftRight(n)
		                   .xor(new BigInteger(1,new byte[]{-1,-1,-1,-1}))
		                   .toByteArray();
		byte[] br = new byte[4];
		for(int i=0;i < br.length;i++){
			br[i] = b[i+1];
		}
		return br;
	}
	static byte[] byteAND(byte[] b1,byte[] b2){
		 byte[] r = new byte[b1.length];
		 for(int i=0;i < r.length;i++){
		    r[i] = (byte)(b1[i] & b2[i]);
		 }
		 return r;
	}
	static byte[] byteOR(byte[] b1,byte[] b2){
		byte[] r = new byte[b1.length];
		for(int i=0;i < r.length;i++){
			r[i] = (byte)(b1[i] | b2[i]);
		}
		return r;
	}
	static byte[] byteXOR(byte[] b1,byte[] b2){
		byte[] r = new byte[b1.length];
		for(int i=0;i < r.length;i++){
			r[i] = (byte)(b1[i] ^ b2[i]);
		}
		return r;
	}
}

