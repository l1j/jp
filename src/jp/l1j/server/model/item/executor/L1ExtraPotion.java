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
package jp.l1j.server.model.item.executor;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import static jp.l1j.locale.I18N.*;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.model.skill.L1BuffUtil;
import static jp.l1j.server.model.skill.L1SkillId.*;
import jp.l1j.server.packets.server.S_HpUpdate;
import jp.l1j.server.packets.server.S_MpUpdate;
import jp.l1j.server.packets.server.S_OwnCharStatus;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.packets.server.S_SkillSound;
import jp.l1j.server.packets.server.S_SpMr;
import jp.l1j.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1ExtraPotion {

	private static Logger _log = Logger.getLogger(L1ExtraPotion.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "ItemEffectList")
	private static class ItemEffectList implements Iterable<L1ExtraPotion> {
		@XmlElement(name = "Item")
		private List<L1ExtraPotion> _list;

		public Iterator<L1ExtraPotion> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Effect {
		@XmlAttribute(name = "Time")
		private int _time;

		public int getTime() {
			return _time;
		}
		@XmlAttribute(name = "GfxId")
		private int _gfxId;

		public int getGfxId() {
			return _gfxId;
		}

		@XmlAttribute(name = "Str")
		private int _str = 0;

		public int getStr() {
			return _str;
		}

		@XmlAttribute(name = "Dex")
		private int _dex = 0;

		public int getDex() {
			return _dex;
		}

		@XmlAttribute(name = "Con")
		private int _con = 0;

		public int getCon() {
			return _con;
		}

		@XmlAttribute(name = "Int")
		private int _int = 0;

		public int getInt() {
			return _int;
		}

		@XmlAttribute(name = "Wis")
		private int _wis = 0;

		public int getWis() {
			return _wis;
		}

		@XmlAttribute(name = "Sp")
		private int _sp = 0;

		public int getSp() {
			return _sp;
		}

		@XmlAttribute(name = "Hit")
		private int _hit = 0;

		public int getHit() {
			return _hit;
		}

		@XmlAttribute(name = "Dmg")
		private int _dmg = 0;

		public int getDmg() {
			return _dmg;
		}


		@XmlAttribute(name = "BowHit")
		private int _bowHit = 0;

		public int getBowHit() {
			return _bowHit;
		}

		@XmlAttribute(name = "BowDmg")
		private int _bowDmg = 0;

		public int getBowDmg() {
			return _bowDmg;
		}

		@XmlAttribute(name = "Hp")
		private int _hp = 0;

		public int getHp() {
			return _hp;
		}

		@XmlAttribute(name = "Mp")
		private int _mp = 0;

		public int getMp() {
			return _mp;
		}

		@XmlAttribute(name = "Hpr")
		private int _hpr = 0;

		public int getHpr() {
			return _hpr;
		}

		@XmlAttribute(name = "Mpr")
		private int _mpr = 0;

		public int getMpr() {
			return _mpr;
		}

		@XmlAttribute(name = "Mr")
		private int _mr = 0;

		public int getMr() {
			return _mr;
		}

		@XmlAttribute(name = "Exp")
		private int _exp = 0;

		public int getExp() {
			return _exp;
		}

	}

	private static final String _path = "./data/xml/Item/ExtraPotion.xml";

	private static HashMap<Integer, L1ExtraPotion> _dataMap = new HashMap<Integer, L1ExtraPotion>();

	public static L1ExtraPotion get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _itemId;

	private int getItemId() {
		return _itemId;
	}

	@XmlAttribute(name = "Remove")
	private int _remove;

	private int getRemove() {
		return _remove;
	}

	@XmlElement(name = "Effect")
	private Effect _effect;

	public Effect getEffect() {
		return _effect;
	}

	private static void loadXml(HashMap<Integer, L1ExtraPotion> dataMap) {
		PerformanceTimer timer = new PerformanceTimer();
		try {
			JAXBContext context = JAXBContext.newInstance(L1ExtraPotion.ItemEffectList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(_path);
			ItemEffectList list = (ItemEffectList) um.unmarshal(file);

			for (L1ExtraPotion each : list) {
				if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
					System.out.println(String.format(I18N_DOES_NOT_EXIST_ITEM_LIST, each.getItemId()));
					// %s はアイテムリストに存在しません。
				} else {
					dataMap.put(each.getItemId(), each);
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, _path + "load failed.", e);
			System.exit(0);
		}
		System.out.println("loading extra potions...OK! " + timer.elapsedTimeMillis() + "ms");
	}

	public static void load() {
		loadXml(_dataMap);
	}
	
	public static void reload() {
		HashMap<Integer, L1ExtraPotion> dataMap = new HashMap<Integer, L1ExtraPotion>();
		loadXml(dataMap);
		_dataMap = dataMap;
	}

	public boolean use(L1PcInstance pc, L1ItemInstance item) {
		if (pc.hasSkillEffect(71) == true) { // ディケイ ポーションの状態
			pc.sendPackets(new S_ServerMessage(698)); // 魔力によって何も飲むことができません。
			return false;
		}

		// アブソルート バリアの解除
		L1BuffUtil.cancelBarrier(pc);

		int maxChargeCount = item.getItem().getMaxChargeCount();
		int chargeCount = item.getChargeCount();
		if (maxChargeCount > 0 && chargeCount <= 0) {
			// \f1何も起きませんでした。
			pc.sendPackets(new S_ServerMessage(79));
			return false;
		}

		Effect effect = getEffect();
		if (getItemId() == 50616) { // 祈りのポーション
			if (pc.hasSkillEffect(STATUS_EXP_UP))
				pc.removeSkillEffect(STATUS_EXP_UP);
			if (pc.hasSkillEffect(STATUS_EXP_UP_II))
				pc.removeSkillEffect(STATUS_EXP_UP_II);
			if (pc.hasSkillEffect(BLESS_OF_COMA1))
				pc.removeSkillEffect(BLESS_OF_COMA1);
			if (pc.hasSkillEffect(BLESS_OF_COMA2))
				pc.removeSkillEffect(BLESS_OF_COMA2);
			if (pc.hasSkillEffect(BLESS_OF_SAMURAI))
				pc.removeSkillEffect(BLESS_OF_SAMURAI);

				int i = effect.getExp();
				pc.addExpBonusPct(i);

			pc.setSkillEffect(STATUS_EXP_UP, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
		}
		if (getItemId() == 50617) { // 祈りのポーションII
			if (pc.hasSkillEffect(STATUS_EXP_UP))
				pc.removeSkillEffect(STATUS_EXP_UP);
			if (pc.hasSkillEffect(STATUS_EXP_UP_II))
				pc.removeSkillEffect(STATUS_EXP_UP_II);
			if (pc.hasSkillEffect(BLESS_OF_COMA1))
				pc.removeSkillEffect(BLESS_OF_COMA1);
			if (pc.hasSkillEffect(BLESS_OF_COMA2))
				pc.removeSkillEffect(BLESS_OF_COMA2);
			if (pc.hasSkillEffect(BLESS_OF_SAMURAI))
				pc.removeSkillEffect(BLESS_OF_SAMURAI);

				int i = effect.getExp();
				pc.addExpBonusPct(i);

			pc.setSkillEffect(STATUS_EXP_UP_II, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
		}
		if (getItemId() == 50618) { // 剣士のポーション
				// 潮風系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_SWORDMAN))
				pc.removeSkillEffect(POTION_OF_SWORDMAN);
			if (pc.hasSkillEffect(POTION_OF_MAGICIAN))
				pc.removeSkillEffect(POTION_OF_MAGICIAN);
			if (pc.hasSkillEffect(POTION_OF_RECOVERY))
				pc.removeSkillEffect(POTION_OF_RECOVERY);
			if (pc.hasSkillEffect(POTION_OF_MEDITATION))
				pc.removeSkillEffect(POTION_OF_MEDITATION);
			if (pc.hasSkillEffect(POTION_OF_LIFE))
				pc.removeSkillEffect(POTION_OF_LIFE);
			if (pc.hasSkillEffect(POTION_OF_MAGIC))
				pc.removeSkillEffect(POTION_OF_MAGIC);
			if (pc.hasSkillEffect(POTION_OF_MAGIC_RESIST))
				pc.removeSkillEffect(POTION_OF_MAGIC_RESIST);

				pc.addMaxHp(effect.getHp());
				pc.addHpr(effect.getHpr());
				pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.startHpRegeneration();

			pc.setSkillEffect(POTION_OF_SWORDMAN, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(822)); // 体の奥深くから不思議な力が湧き起こるのを感じます。
		}
		if (getItemId() == 50619) { // 術士のポーション
			// 潮風系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_SWORDMAN))
				pc.removeSkillEffect(POTION_OF_SWORDMAN);
			if (pc.hasSkillEffect(POTION_OF_MAGICIAN))
				pc.removeSkillEffect(POTION_OF_MAGICIAN);
			if (pc.hasSkillEffect(POTION_OF_RECOVERY))
				pc.removeSkillEffect(POTION_OF_RECOVERY);
			if (pc.hasSkillEffect(POTION_OF_MEDITATION))
				pc.removeSkillEffect(POTION_OF_MEDITATION);
			if (pc.hasSkillEffect(POTION_OF_LIFE))
				pc.removeSkillEffect(POTION_OF_LIFE);
			if (pc.hasSkillEffect(POTION_OF_MAGIC))
				pc.removeSkillEffect(POTION_OF_MAGIC);
			if (pc.hasSkillEffect(POTION_OF_MAGIC_RESIST))
				pc.removeSkillEffect(POTION_OF_MAGIC_RESIST);

				pc.addMaxMp(effect.getMp());
				pc.addMpr(effect.getMpr());
				pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.startMpRegeneration();

			pc.setSkillEffect(POTION_OF_MAGICIAN, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(822)); // 体の奥深くから不思議な力が湧き起こるのを感じます。
		}
		if (getItemId() == 50620) { // 治癒のポーション
			// 潮風系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_SWORDMAN))
				pc.removeSkillEffect(POTION_OF_SWORDMAN);
			if (pc.hasSkillEffect(POTION_OF_MAGICIAN))
				pc.removeSkillEffect(POTION_OF_MAGICIAN);
			if (pc.hasSkillEffect(POTION_OF_RECOVERY))
				pc.removeSkillEffect(POTION_OF_RECOVERY);
			if (pc.hasSkillEffect(POTION_OF_MEDITATION))
				pc.removeSkillEffect(POTION_OF_MEDITATION);
			if (pc.hasSkillEffect(POTION_OF_LIFE))
				pc.removeSkillEffect(POTION_OF_LIFE);
			if (pc.hasSkillEffect(POTION_OF_MAGIC))
				pc.removeSkillEffect(POTION_OF_MAGIC);
			if (pc.hasSkillEffect(POTION_OF_MAGIC_RESIST))
				pc.removeSkillEffect(POTION_OF_MAGIC_RESIST);

				pc.addHpr(effect.getHpr());
				pc.startHpRegeneration();

			pc.setSkillEffect(POTION_OF_RECOVERY, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(822)); // 体の奥深くから不思議な力が湧き起こるのを感じます。
		}
		if (getItemId() == 50621) { // 瞑想のポーション
			// 潮風系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_SWORDMAN))
				pc.removeSkillEffect(POTION_OF_SWORDMAN);
			if (pc.hasSkillEffect(POTION_OF_MAGICIAN))
				pc.removeSkillEffect(POTION_OF_MAGICIAN);
			if (pc.hasSkillEffect(POTION_OF_RECOVERY))
				pc.removeSkillEffect(POTION_OF_RECOVERY);
			if (pc.hasSkillEffect(POTION_OF_MEDITATION))
				pc.removeSkillEffect(POTION_OF_MEDITATION);
			if (pc.hasSkillEffect(POTION_OF_LIFE))
				pc.removeSkillEffect(POTION_OF_LIFE);
			if (pc.hasSkillEffect(POTION_OF_MAGIC))
				pc.removeSkillEffect(POTION_OF_MAGIC);
			if (pc.hasSkillEffect(POTION_OF_MAGIC_RESIST))
				pc.removeSkillEffect(POTION_OF_MAGIC_RESIST);

				pc.addMpr(effect.getMpr());
				pc.startMpRegeneration();

			pc.setSkillEffect(POTION_OF_MEDITATION, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(822)); // 体の奥深くから不思議な力が湧き起こるのを感じます。
		}
		if (getItemId() == 50622) { // 生命のポーション
			// 潮風系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_SWORDMAN))
				pc.removeSkillEffect(POTION_OF_SWORDMAN);
			if (pc.hasSkillEffect(POTION_OF_MAGICIAN))
				pc.removeSkillEffect(POTION_OF_MAGICIAN);
			if (pc.hasSkillEffect(POTION_OF_RECOVERY))
				pc.removeSkillEffect(POTION_OF_RECOVERY);
			if (pc.hasSkillEffect(POTION_OF_MEDITATION))
				pc.removeSkillEffect(POTION_OF_MEDITATION);
			if (pc.hasSkillEffect(POTION_OF_LIFE))
				pc.removeSkillEffect(POTION_OF_LIFE);
			if (pc.hasSkillEffect(POTION_OF_MAGIC))
				pc.removeSkillEffect(POTION_OF_MAGIC);
			if (pc.hasSkillEffect(POTION_OF_MAGIC_RESIST))
				pc.removeSkillEffect(POTION_OF_MAGIC_RESIST);

				pc.addMaxHp(effect.getHp());
				pc.sendPackets(new S_HpUpdate(pc.getCurrentHp(), pc.getMaxHp()));

			pc.setSkillEffect(POTION_OF_LIFE, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(822)); // 体の奥深くから不思議な力が湧き起こるのを感じます。
		}
		if (getItemId() == 50623) { // 魔法のポーション
			// 潮風系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_SWORDMAN))
				pc.removeSkillEffect(POTION_OF_SWORDMAN);
			if (pc.hasSkillEffect(POTION_OF_MAGICIAN))
				pc.removeSkillEffect(POTION_OF_MAGICIAN);
			if (pc.hasSkillEffect(POTION_OF_RECOVERY))
				pc.removeSkillEffect(POTION_OF_RECOVERY);
			if (pc.hasSkillEffect(POTION_OF_MEDITATION))
				pc.removeSkillEffect(POTION_OF_MEDITATION);
			if (pc.hasSkillEffect(POTION_OF_LIFE))
				pc.removeSkillEffect(POTION_OF_LIFE);
			if (pc.hasSkillEffect(POTION_OF_MAGIC))
				pc.removeSkillEffect(POTION_OF_MAGIC);
			if (pc.hasSkillEffect(POTION_OF_MAGIC_RESIST))
				pc.removeSkillEffect(POTION_OF_MAGIC_RESIST);

				pc.addMaxMp(effect.getMp());
				pc.sendPackets(new S_MpUpdate(pc.getCurrentMp(), pc.getMaxMp()));

			pc.setSkillEffect(POTION_OF_MAGIC, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(822)); // 体の奥深くから不思議な力が湧き起こるのを感じます。
		}
		if (getItemId() == 50624) { // 魔法抵抗のポーション
			// 潮風系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_SWORDMAN))
				pc.removeSkillEffect(POTION_OF_SWORDMAN);
			if (pc.hasSkillEffect(POTION_OF_MAGICIAN))
				pc.removeSkillEffect(POTION_OF_MAGICIAN);
			if (pc.hasSkillEffect(POTION_OF_RECOVERY))
				pc.removeSkillEffect(POTION_OF_RECOVERY);
			if (pc.hasSkillEffect(POTION_OF_MEDITATION))
				pc.removeSkillEffect(POTION_OF_MEDITATION);
			if (pc.hasSkillEffect(POTION_OF_LIFE))
				pc.removeSkillEffect(POTION_OF_LIFE);
			if (pc.hasSkillEffect(POTION_OF_MAGIC))
				pc.removeSkillEffect(POTION_OF_MAGIC);
			if (pc.hasSkillEffect(POTION_OF_MAGIC_RESIST))
				pc.removeSkillEffect(POTION_OF_MAGIC_RESIST);

				pc.addMr(effect.getMr());
				pc.sendPackets(new S_SpMr(pc));

			pc.setSkillEffect(POTION_OF_MAGIC_RESIST, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(822)); // 体の奥深くから不思議な力が湧き起こるのを感じます。
		}
		if (getItemId() == 50625) { // 腕力のポーション
					//深海系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_STR))  {
				pc.removeSkillEffect(POTION_OF_STR);
			} if (pc.hasSkillEffect(POTION_OF_DEX)) {
				pc.removeSkillEffect(POTION_OF_DEX);
			} if (pc.hasSkillEffect(POTION_OF_CON)) {
				pc.removeSkillEffect(POTION_OF_CON);
			} if (pc.hasSkillEffect(POTION_OF_INT)) {
				pc.removeSkillEffect(POTION_OF_INT);
			} if (pc.hasSkillEffect(POTION_OF_WIS)) {
				pc.removeSkillEffect(POTION_OF_WIS);
			} if (pc.hasSkillEffect(POTION_OF_RAGE)) {
				pc.removeSkillEffect(POTION_OF_RAGE);
			} if (pc.hasSkillEffect(POTION_OF_CONCENTRATION)) {
				pc.removeSkillEffect(POTION_OF_CONCENTRATION);
			}

				pc.addStr(effect.getStr());
				pc.sendPackets(new S_OwnCharStatus(pc));

			pc.setSkillEffect(POTION_OF_STR, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(820)); // 体の力が変化していくのを感じます。
		}
		if (getItemId() == 50626) { // 機敏のポーション
				//深海系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_STR))
				pc.removeSkillEffect(POTION_OF_STR);
			if (pc.hasSkillEffect(POTION_OF_DEX))
				pc.removeSkillEffect(POTION_OF_DEX);
			if (pc.hasSkillEffect(POTION_OF_CON))
				pc.removeSkillEffect(POTION_OF_CON);
			if (pc.hasSkillEffect(POTION_OF_INT))
				pc.removeSkillEffect(POTION_OF_INT);
			if (pc.hasSkillEffect(POTION_OF_WIS))
				pc.removeSkillEffect(POTION_OF_WIS);
			if (pc.hasSkillEffect(POTION_OF_RAGE))
				pc.removeSkillEffect(POTION_OF_RAGE);
			if (pc.hasSkillEffect(POTION_OF_CONCENTRATION))
				pc.removeSkillEffect(POTION_OF_CONCENTRATION);

				pc.addDex(effect.getDex());
				pc.sendPackets(new S_OwnCharStatus(pc));

			//pc.sendPackets(new S_Dexup(pc, effect.getDex(), effect.getTime()));
			pc.setSkillEffect(POTION_OF_DEX, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(820)); // 体の力が変化していくのを感じます。
		}
		if (getItemId() == 50627) { // 体力のポーション
				//深海系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_STR))
				pc.removeSkillEffect(POTION_OF_STR);
			if (pc.hasSkillEffect(POTION_OF_DEX))
				pc.removeSkillEffect(POTION_OF_DEX);
			if (pc.hasSkillEffect(POTION_OF_CON))
				pc.removeSkillEffect(POTION_OF_CON);
			if (pc.hasSkillEffect(POTION_OF_INT))
				pc.removeSkillEffect(POTION_OF_INT);
			if (pc.hasSkillEffect(POTION_OF_WIS))
				pc.removeSkillEffect(POTION_OF_WIS);
			if (pc.hasSkillEffect(POTION_OF_RAGE))
				pc.removeSkillEffect(POTION_OF_RAGE);
			if (pc.hasSkillEffect(POTION_OF_CONCENTRATION))
				pc.removeSkillEffect(POTION_OF_CONCENTRATION);

				pc.addCon((byte) effect.getCon());
				pc.sendPackets(new S_OwnCharStatus(pc));

			pc.setSkillEffect(POTION_OF_CON, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(820)); // 体の力が変化していくのを感じます。
		}
		if (getItemId() == 50628) { // 知力のポーション
				//深海系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_STR))
				pc.removeSkillEffect(POTION_OF_STR);
			if (pc.hasSkillEffect(POTION_OF_DEX))
				pc.removeSkillEffect(POTION_OF_DEX);
			if (pc.hasSkillEffect(POTION_OF_CON))
				pc.removeSkillEffect(POTION_OF_CON);
			if (pc.hasSkillEffect(POTION_OF_INT))
				pc.removeSkillEffect(POTION_OF_INT);
			if (pc.hasSkillEffect(POTION_OF_WIS))
				pc.removeSkillEffect(POTION_OF_WIS);
			if (pc.hasSkillEffect(POTION_OF_RAGE))
				pc.removeSkillEffect(POTION_OF_RAGE);
			if (pc.hasSkillEffect(POTION_OF_CONCENTRATION))
				pc.removeSkillEffect(POTION_OF_CONCENTRATION);

				pc.addInt((byte) effect.getInt());
				pc.sendPackets(new S_OwnCharStatus(pc));

			pc.setSkillEffect(POTION_OF_INT, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(820)); // 体の力が変化していくのを感じます。
		}
		if (getItemId() == 50629) { // 精神のポーション
				//深海系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_STR))
				pc.removeSkillEffect(POTION_OF_STR);
			if (pc.hasSkillEffect(POTION_OF_DEX))
				pc.removeSkillEffect(POTION_OF_DEX);
			if (pc.hasSkillEffect(POTION_OF_CON))
				pc.removeSkillEffect(POTION_OF_CON);
			if (pc.hasSkillEffect(POTION_OF_INT))
				pc.removeSkillEffect(POTION_OF_INT);
			if (pc.hasSkillEffect(POTION_OF_WIS))
				pc.removeSkillEffect(POTION_OF_WIS);
			if (pc.hasSkillEffect(POTION_OF_RAGE))
				pc.removeSkillEffect(POTION_OF_RAGE);
			if (pc.hasSkillEffect(POTION_OF_CONCENTRATION))
				pc.removeSkillEffect(POTION_OF_CONCENTRATION);

				pc.addWis((byte) effect.getWis());
				pc.sendPackets(new S_OwnCharStatus(pc));

			pc.setSkillEffect(POTION_OF_WIS, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(820)); // 体の力が変化していくのを感じます。
		}
		if (getItemId() == 50630) { // 憤怒のポーション
				//深海系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_STR))
				pc.removeSkillEffect(POTION_OF_STR);
			if (pc.hasSkillEffect(POTION_OF_DEX))
				pc.removeSkillEffect(POTION_OF_DEX);
			if (pc.hasSkillEffect(POTION_OF_CON))
				pc.removeSkillEffect(POTION_OF_CON);
			if (pc.hasSkillEffect(POTION_OF_INT))
				pc.removeSkillEffect(POTION_OF_INT);
			if (pc.hasSkillEffect(POTION_OF_WIS))
				pc.removeSkillEffect(POTION_OF_WIS);
			if (pc.hasSkillEffect(POTION_OF_RAGE))
				pc.removeSkillEffect(POTION_OF_RAGE);
			if (pc.hasSkillEffect(POTION_OF_CONCENTRATION))
				pc.removeSkillEffect(POTION_OF_CONCENTRATION);

				pc.addHitup(effect.getHit());
				pc.addDmgup(effect.getDmg());
				pc.addBowHitup(effect.getBowHit());
				pc.addBowDmgup(effect.getBowDmg());

			pc.setSkillEffect(POTION_OF_RAGE, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(820)); // 体の力が変化していくのを感じます。
		}
		if (getItemId() == 50631) { // 集中のポーション
				//深海系のポーションとは重複しない
			if (pc.hasSkillEffect(POTION_OF_STR))
				pc.removeSkillEffect(POTION_OF_STR);
			if (pc.hasSkillEffect(POTION_OF_DEX))
				pc.removeSkillEffect(POTION_OF_DEX);
			if (pc.hasSkillEffect(POTION_OF_CON))
				pc.removeSkillEffect(POTION_OF_CON);
			if (pc.hasSkillEffect(POTION_OF_INT))
				pc.removeSkillEffect(POTION_OF_INT);
			if (pc.hasSkillEffect(POTION_OF_WIS))
				pc.removeSkillEffect(POTION_OF_WIS);
			if (pc.hasSkillEffect(POTION_OF_RAGE))
				pc.removeSkillEffect(POTION_OF_RAGE);
			if (pc.hasSkillEffect(POTION_OF_CONCENTRATION))
				pc.removeSkillEffect(POTION_OF_CONCENTRATION);

				pc.addSp(effect.getSp());
				pc.sendPackets(new S_SpMr(pc));

			pc.setSkillEffect(POTION_OF_CONCENTRATION, effect.getTime() * 1000);
			pc.sendPackets(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.broadcastPacket(new S_SkillSound(pc.getId(), effect.getGfxId()));
			pc.sendPackets(new S_ServerMessage(820)); // 体の力が変化していくのを感じます。
		}

		if (getRemove() > 0) {
			if (chargeCount > 0) {
				item.setChargeCount(chargeCount - getRemove());
				pc.getInventory().updateItem(item,
						L1PcInventory.COL_CHARGE_COUNT);
			} else {
				pc.getInventory().removeItem(item, getRemove());
			}
		}

		return true;
	}
}

