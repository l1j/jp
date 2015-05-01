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

package jp.l1j.server.packets.client;

import jp.l1j.server.ClientThread;
import jp.l1j.server.datatables.PetItemTable;
import jp.l1j.server.datatables.PetTypeTable;
import jp.l1j.server.model.instance.L1DollInstance;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1NpcInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.instance.L1SummonInstance;
import jp.l1j.server.model.L1Object;
import jp.l1j.server.model.L1World;
import jp.l1j.server.model.inventory.L1Inventory;
import jp.l1j.server.model.inventory.L1PcInventory;
import jp.l1j.server.packets.server.S_ItemName;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.random.RandomGenerator;
import jp.l1j.server.random.RandomGeneratorFactory;
import jp.l1j.server.templates.L1Npc;
import jp.l1j.server.templates.L1PetItem;
import jp.l1j.server.templates.L1PetType;

public class C_GiveItem extends ClientBasePacket {
	private static final String C_GIVE_ITEM = "[C] C_GiveItem";

	private static RandomGenerator _random = RandomGeneratorFactory.newRandom();

	public C_GiveItem(byte decrypt[], ClientThread client) {
		super(decrypt);
		int targetId = readD();
		readH();
		readH();
		int itemId = readD();
		int count = readD();

		L1PcInstance pc = client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}

		L1Object object = L1World.getInstance().findObject(targetId);
		if ((object == null) || !(object instanceof L1NpcInstance)) {
			return;
		}
		L1NpcInstance target = (L1NpcInstance) object;
		if (!isNpcItemReceivable(target.getNpcTemplate())) {
			return;
		}
		if (target.getNpcTemplate().isEqualityDrop()) {
			pc.sendPackets(new S_ServerMessage(942));
			// 相手のアイテムが重すぎるため、これ以上あげられません。
			return;
		}
		L1Inventory targetInv = target.getInventory();

		L1Inventory inv = pc.getInventory();
		L1ItemInstance item = inv.getItem(itemId);
		if (item == null) {
			return;
		}
		if (item.isEquipped()) {
			pc.sendPackets(new S_ServerMessage(141));
			// \f1装備しているものは、人に渡すことができません。
			return;
		}
		if (!item.getItem().isTradable()) {
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
			// \f1%0は捨てたりまたは他人に讓ることができません。
			return;
		}
		if (item.isSealed()) { // 封印された装備
			// \f1%0は捨てたりまたは他人に讓ることができません。
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
			return;
		}
		for (Object petObject : pc.getPetList().values()) {
			if (petObject instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petObject;
				if (item.getId() == pet.getItemObjId()) {
					pc.sendPackets(new S_ServerMessage(1187)); // ペットのアミュレットが使用中です。
					return;
				}
			}
		}
		// マジックドール使用中
		Object[] dollList = pc.getDollList().values().toArray();
		for (Object dollObject : dollList) {
			if (dollObject instanceof L1DollInstance) {
				L1DollInstance doll = (L1DollInstance) dollObject;
				if (doll.getItemObjId() == item.getId()) {
					pc.sendPackets(new S_ServerMessage(1181)); // 該当のマジックドールは現在使用中です。
					return;
				}
			}
		}
		// シェルマンに最高級サファイアを渡しhideを解除
		if(item.getItemId() == 40054 && target.getNpcTemplate().getNpcId() == 46291) {
			((L1NpcInstance)target).appearSwelmaen();
		}
		if (targetInv.checkAddItem(item, count) != L1Inventory.OK) {
			pc.sendPackets(new S_ServerMessage(942));
			// 相手のアイテムが重すぎるため、これ以上あげられません。
			return;
		}
		item = inv.tradeItem(item, count, targetInv);
		target.onGetItem(item, count);
		target.updateLight();
		pc.updateLight();

		L1PetType petType = PetTypeTable.getInstance().get(
				target.getNpcTemplate().getNpcId());
		if (petType == null || target.isDead()) {
			return;
		}
		// ペット捕獲
		if (item.getItemId() == petType.getTameItemId()) {
			tamePet(pc, target);
		}
		if (!(target instanceof L1PetInstance)) {
			return;
		}
		L1PetInstance pet = (L1PetInstance) target;
		// 　進化アイテム(デフォルト　進化の実・勝利の実)
		if (item.getItemId() == petType.getTransformItemId() && petType.canEvolve()) {
			evolvePet(pc, target, item.getItemId());
		}
		// ペット食物・装備
		if (item.getItem().getType2() == 0) { // 道具類
			// ペット装備類
			if ((item.getItem().getType() == 11) && (petType.useEquipment())) { // ペット装備使用可能判定
				usePetWeaponArmor(target, item);
			}
		}
	}

	private void usePetWeaponArmor(L1NpcInstance target, L1ItemInstance item) {
		if (!(target instanceof L1PetInstance)) {
			return;
		}
		L1PetInstance pet = (L1PetInstance) target;
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(
				item.getItemId());
		if (petItem.getUseType() == 1) { // 牙
			pet.usePetWeapon(pet, item);
		} else if (petItem.getUseType() == 0) { // アーマー
			pet.usePetArmor(pet, item);
		}
	}

	private final static String receivableImpls[] = new String[] { "L1Npc", // NPC
			"L1Monster", // モンスター
			"L1Guardian", // エルフの森の守護者
			"L1Teleporter", // テレポーター
			"L1Guard" }; // ガード

	private boolean isNpcItemReceivable(L1Npc npc) {
		for (String impl : receivableImpls) {
			if (npc.getImpl().equals(impl)) {
				return true;
			}
		}
		return false;
	}

	private void tamePet(L1PcInstance pc, L1NpcInstance target) {
		if (target instanceof L1PetInstance
				|| target instanceof L1SummonInstance) {
			return;
		}

		int petcost = 0;
		Object[] petlist = pc.getPetList().values().toArray();
		for (Object pet : petlist) {
			petcost += ((L1NpcInstance) pet).getPetcost();
		}
		int charisma = pc.getCha();
		if (pc.isCrown()) { // 君主
			charisma += 6;
		} else if (pc.isElf()) { // エルフ
			charisma += 12;
		} else if (pc.isWizard()) { // WIZ
			charisma += 6;
		} else if (pc.isDarkelf()) { // DE
			charisma += 6;
		} else if (pc.isDragonKnight()) { // ドラゴンナイト
			charisma += 6;
		} else if (pc.isIllusionist()) { // イリュージョニスト
			charisma += 6;
		}
		charisma -= petcost;

		L1PcInventory inv = pc.getInventory();
		if (charisma >= 6 && inv.getSize() < 180) {
			if (isTamePet(target)) {
				L1ItemInstance petamu = inv.storeItem(40314, 1); // ペットのアミュレット
				if (petamu != null) {
					new L1PetInstance(target, pc, petamu.getId());
					pc.sendPackets(new S_ItemName(petamu));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(324)); // てなずけるのに失敗しました。
			}
		}
	}

	private void evolvePet(L1PcInstance pc, L1NpcInstance target, int itemId) {
		if (!(target instanceof L1PetInstance)) {
			return;
		}
		L1PcInventory inv = pc.getInventory();
		L1PetInstance pet = (L1PetInstance) target;
		L1ItemInstance petamu = inv.getItem(pet.getItemObjId());
		if (pet.getLevel() >= 30 && // Lv30以上
				pc == pet.getMaster() && // 自分のペット
				petamu != null) {
			L1ItemInstance highpetamu = inv.storeItem(40316, 1);
			if (highpetamu != null) {
				pet.evolvePet( // 進化させる
						highpetamu.getId());
				pc.sendPackets(new S_ItemName(highpetamu));
				inv.removeItem(petamu, 1);
			}
		}
	}

	private boolean isTamePet(L1NpcInstance npc) {
		boolean isSuccess = false;
		int npcId = npc.getNpcTemplate().getNpcId();
		if (npcId == 45313) { // タイガー
			if (npc.getMaxHp() / 3 > npc.getCurrentHp() // HPが1/3未満で1/16の確率
					&& _random.nextInt(16) == 15) {
				isSuccess = true;
			}
		} else {
			if (npc.getMaxHp() / 3 > npc.getCurrentHp()) {
				isSuccess = true;
			}
		}

		if (npcId == 45313 || npcId == 45044 || npcId == 45711) { // タイガー、ラクーン、
			// 紀州犬の子犬
			if (npc.isResurrect()) { // RES後はテイム不可
				isSuccess = false;
			}
		}

		return isSuccess;
	}

	@Override
	public String getType() {
		return C_GIVE_ITEM;
	}
}