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

package jp.l1j.server.command.executor;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.datatables.SprListTable;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1CastGfx2 implements L1CommandExecutor {
   private static Logger _log = Logger.getLogger(L1CastGfx2.class.getName());

   private L1CastGfx2() {
   }

   public static L1CommandExecutor getInstance() {
	  return new L1CastGfx2();
   }

   private int _startIndex;
   private int _sec;
   private int _count;
   private L1PcInstance _pc;
   private ArrayList<Integer> _sprList; 
   @Override
   public void execute(L1PcInstance pc, String cmdName, String arg) {
	  try {
		 StringTokenizer stringtokenizer = new StringTokenizer(arg);
		 int sprid = Integer.parseInt(stringtokenizer.nextToken());
		 _sec = Integer.parseInt(stringtokenizer.nextToken());
		 _count  = Integer.parseInt(stringtokenizer.nextToken());
		 _pc = pc;
		 
		 //開始spridがテーブルにあるか線形検索　ない場合は、+1づつ増加させて発見するまでループさせる
		 _sprList = SprListTable.getInstance().getTemplate();
		 boolean search_flag = false; 
		 for(;;){
			 for(int i = 0;i < _sprList.size();i++){
				 if(sprid == _sprList.get(i)){
					 search_flag = true;
					 _startIndex = i;
					 break;
				 }
			 }
			 if(search_flag){
				 break;
			 }
			 sprid++;
		 }
		 
		 class castgfxStart implements Runnable{
			@Override
			public void run(){
				int cnt = 0;
				for(;;){
					 _pc.sendPackets(new S_SkillSound(_pc.getId(), _sprList.get(_startIndex)));
					 _pc.broadcastPacket(new S_SkillSound(_pc.getId(), _sprList.get(_startIndex)));
					 _pc.sendPackets(new S_SystemMessage(String.valueOf(_sprList.get(_startIndex))));
					 _startIndex++;
					 
					 cnt++;
					 if(_count != 0 && cnt >= _count){
						 return;
					 }
					try {
						Thread.sleep(_sec);
					} catch (InterruptedException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
			}
		 }
		 castgfxStart cgs = new castgfxStart();
		 GeneralThreadPool.getInstance().execute(cgs);

	  } catch (Exception e) {
		 pc.sendPackets(new S_SystemMessage(String.format(I18N_COMMAND_FORMAT_3,
				 cmdName, I18N_GFX_ID, I18N_INTERVAL, I18N_AMOUNT)));
		 // .%s %s %s %s の形式で入力してください。
	  }
   }
}