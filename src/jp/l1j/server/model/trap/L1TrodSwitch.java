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

package jp.l1j.server.model.trap;

import jp.l1j.server.GeneralThreadPool;
import jp.l1j.server.model.L1HardinQuest;
import jp.l1j.server.model.L1Location;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1OrimQuest;
import jp.l1j.server.model.L1Teleport;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.storage.TrapStorage;

public class L1TrodSwitch extends L1Trap{
	private final int _switchId;
	
	private L1PcInstance _trodFrom;
	
	private L1Object _trapObj;
	
	public L1TrodSwitch(TrapStorage storage) {
		super(storage);
		_switchId= storage.getInt("switch_id");
	}

	@Override
	public void onTrod(L1PcInstance trodFrom, L1Object trapObj) {
		_trodFrom=trodFrom;
		_trapObj=trapObj;
		GeneralThreadPool.getInstance().execute(new SwitchThread());
	}
	
	class SwitchThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//今回はインスタンスマップの生成を考慮する
			//IF文はelse構成がなるべくでないようにする。
			try {
				int mapId=_trodFrom.getMapId();
				boolean onPc;
				L1Location loc;
				switch(_switchId){
				case 1://過去の話せる島のダンジョン2F　骨部屋へ
					if(!L1HardinQuest.getInstance().getActiveMaps(mapId).isActive())return;
					if(L1HardinQuest.getInstance().getActiveMaps(mapId).isDeleteTransactionNow())return;
					L1HardinQuest.getInstance().getActiveMaps(mapId)
						.tereportEntrance(_trodFrom, new L1Location(32787,32821,_trodFrom.getMapId()), 5);
				break;
				case 2://過去の話せる島のダンジョン2F-2
					if(!L1HardinQuest.getInstance().getActiveMaps(mapId).isActive())return;
					if(L1HardinQuest.getInstance().getActiveMaps(mapId).isAlreadyFirstSwitch())return;
					onPc =false;
					loc = new L1Location(32666,32817,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance ){
							onPc=true;
						}
					}
/*					if(!onPc) return;
					onPc =false;
					loc.set(32668,32817,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPc=true;
						}
					}
					if(!onPc) return;
					onPc =false;
					loc.set(32668,32819,mapId);;
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPc=true;
						}
					}
					if(!onPc) return;
					onPc =false;
					loc.set(32666,32819,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPc=true;
						}
					}
*/					if(!onPc) return;
					L1HardinQuest.getInstance().getActiveMaps(mapId).onFirstSwitch();
				//32667 32818 9000,
				//32668 32817 9000
				//32668 32819 9000
				//32666 32819
				break;
				case 3://過去の話せる島のダンジョン2F-1 光が差し込む
					if(!L1HardinQuest.getInstance().getActiveMaps(mapId).isActive()){
						return;
					}
					if(L1HardinQuest.getInstance().getActiveMaps(mapId).isAlreadySecondSwitch())return;
					onPc =false;
					loc = new L1Location(32712,32793,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance ){
							onPc=true;
						}
					}
	/*				if(!onPc) return;
					onPc =false;
					loc.set(32703,32791,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPc=true;
						}
					}
					if(!onPc) return;
					onPc =false;
					loc.set(32710,32803,mapId);;
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPc=true;
						}
					}
					if(!onPc) return;
					onPc =false;
					loc.set(32703,32800,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPc=true;
						}
					}*/
					if(!onPc) return;
					L1HardinQuest.getInstance().getActiveMaps(mapId).onSecondSwitch();
				//上 32712 32793
				//左 32703 32791
				//右 32710 32803
				//下 32703 32800
				//
					break;
				case 4://過去の話せる島のダンジョン2F-2 白魔法陣作成（ポータル）
					if(!L1HardinQuest.getInstance().getActiveMaps(mapId).isActive()){
						return;
					}
					if(L1HardinQuest.getInstance().getActiveMaps(mapId).isAlreadyPortal())return;
					onPc =false;
					loc = new L1Location(32809,32837,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance ){
							onPc=true;
						}
					}
/*					if(!onPc) return;
					onPc =false;
					loc.set(32807,32837,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPc=true;
						}
					}
					if(!onPc) return;
					onPc =false;
					loc.set(32809,32839,mapId);;
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPc=true;
						}
					}
					if(!onPc) return;
					onPc =false;
					loc.set(32807,32839,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPc=true;
						}
					}*/
					if(!onPc) return;
					L1HardinQuest.getInstance().getActiveMaps(mapId).onThirdSwitch();

					/*End*/
						//中心 32808 32838
						//上32809,32837
						//左32807,32837
						//右32809,32839
						//下32807,32839
					break;
				case 5://過去の話せる島のダンジョン2F-3 4人が赤い刻印に載ると黒い魔法陣が発生
					//星付与位置-1(左)･2(右)
					//上-1 32806 32863
					//上-2 32808 32864
					//左-1 32800 32864
					//左-2 32799 32866
					//右-1 32807 32870
					//右-2 32806 32872
					//下-1 32798 32872
					//下-2 32800 32873
					//魔法陣 32802 32868
					if(!L1HardinQuest.getInstance().getActiveMaps(mapId).isActive()){
						return;
					}
					int onPc_cnt=0;
					int[] onPlayerList=new int[8];
					loc = new L1Location(32806,32863,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPlayerList[1]=obj.getId();
							onPc_cnt+=1;
						}
					}
					loc.set(32808,32864,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPlayerList[0]=obj.getId();
							onPc_cnt+=1;
						}
					}
					loc.set(32800,32864,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPlayerList[2]=obj.getId();
							onPc_cnt+=1;
						}
					}
					loc.set(32799,32866,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPlayerList[3]=obj.getId();
							onPc_cnt+=1;
						}
					}
					loc.set(32807,32870,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPlayerList[4]=obj.getId();
							onPc_cnt+=1;
						}
					}
					loc.set(32806,32872,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPlayerList[5]=obj.getId();
							onPc_cnt+=1;
						}
					}
					loc.set(32798,32872,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPlayerList[6]=obj.getId();
							onPc_cnt+=1;
						}
					}
					loc.set(32800,32873,mapId);
					for(L1Object obj:L1World.getInstance().getVisiblePoint(loc,0)){
						if(obj instanceof L1PcInstance){
							onPlayerList[7]=obj.getId();
							onPc_cnt+=1;
						}
					}
					if(onPc_cnt>0){
					//if(onPc_cnt>3){
						L1HardinQuest.getInstance().getActiveMaps(mapId).setBlackRune(onPlayerList);//フラグ
					}
					break;
				case 6://single 過去の話せる島のダンジョン2F-4 黒い魔法陣を踏むとタリスマンが出現のちにテレポート
					//魔法陣 32802 32868
					if(L1HardinQuest.getInstance().getActiveMaps(mapId).isActive()){
						L1HardinQuest.getInstance().getActiveMaps(mapId).onBlackRune();
					}
					break;
					//赤い印が何を意味するのかは毎回ランダム
				case 7://「ささやく風がオリムの耳の中に流れて行きます。」
					if(L1HardinQuest.getInstance().getActiveMaps(mapId).isActive()){
						L1HardinQuest.getInstance().getActiveMaps(mapId).sendWisperWindow(0);//32684 32817
					}
					break;
				case 8://「ささやく風がオリムの耳の中に流れて行きます。」
					if(L1HardinQuest.getInstance().getActiveMaps(mapId).isActive()){
						L1HardinQuest.getInstance().getActiveMaps(mapId).sendWisperWindow(1);//32732 32789
					}
						break;
				case 9://「ささやく風がオリムの耳の中に流れて行きます。」
					if(L1HardinQuest.getInstance().getActiveMaps(mapId).isActive()){
						L1HardinQuest.getInstance().getActiveMaps(mapId).sendWisperWindow(2);//32760 32791
					}
					break;
				case 10://「ささやく風がオリムの耳の中に流れて行きます。」
					if(L1HardinQuest.getInstance().getActiveMaps(mapId).isActive()){
						L1HardinQuest.getInstance().getActiveMaps(mapId).sendWisperWindow(3);//32729 32854 
					}
						break;
				case 11://「ささやく風がオリムの耳の中に流れて行きます。」
					if(L1HardinQuest.getInstance().getActiveMaps(mapId).isActive()){
						L1HardinQuest.getInstance().getActiveMaps(mapId).sendWisperWindow(4);//32667 32874
					}
					break;
				case 12://過去の話せる島のダンジョン2F　入り口へ
					if(!L1HardinQuest.getInstance().getActiveMaps(mapId).isActive())return;
					if(L1HardinQuest.getInstance().getActiveMaps(mapId).isDeleteTransactionNow())return;
					L1HardinQuest.getInstance().getActiveMaps(mapId)
						.tereportEntrance(_trodFrom, new L1Location(32666,32797,_trodFrom.getMapId()), 5);
				break;
				case 14://過去のオリムの連絡船->船内へ
					if(L1OrimQuest.getInstance().getActiveMaps(mapId)==null)return;
					L1Teleport.teleport(_trodFrom, new L1Location(32677,32800,_trodFrom.getMapId()), 5,true);
				break;
				case 15://過去のオリムの連絡船->船内へ
					if(L1OrimQuest.getInstance().getActiveMaps(mapId)==null)return;
					L1Teleport.teleport(_trodFrom, new L1Location(32677,32864,_trodFrom.getMapId()), 5,true);
				break;
				case 16://過去のオリムの連絡船->船内へ
					if(L1OrimQuest.getInstance().getActiveMaps(mapId)==null)return;
					L1Teleport.teleport(_trodFrom, new L1Location(32741,32860,_trodFrom.getMapId()), 5,true);
				break;
				case 17://過去のオリムの連絡船->船内へ
					if(L1OrimQuest.getInstance().getActiveMaps(mapId)==null)return;
					L1Teleport.teleport(_trodFrom, new L1Location(32805,32862,_trodFrom.getMapId()), 5,true);
				break;
				case 18://船内->過去のオリムの連絡船へ
					if(L1OrimQuest.getInstance().getActiveMaps(mapId)==null)return;
					L1Teleport.teleport(_trodFrom, new L1Location(32792,32802,_trodFrom.getMapId()), 2,true);
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}
}