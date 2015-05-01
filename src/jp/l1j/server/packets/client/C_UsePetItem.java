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

import java.util.logging.Logger;
import jp.l1j.server.ClientThread;
import jp.l1j.server.datatables.PetItemTable;
import jp.l1j.server.datatables.PetTypeTable;
import jp.l1j.server.model.instance.L1ItemInstance;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.instance.L1PetInstance;
import jp.l1j.server.model.L1World;
import jp.l1j.server.packets.server.S_PetEquipment;
import jp.l1j.server.packets.server.S_ServerMessage;
import jp.l1j.server.templates.L1PetItem;
import jp.l1j.server.templates.L1PetType;

// Referenced classes of package jp.l1j.server.clientpackets:
// ClientBasePacket

public class C_UsePetItem extends ClientBasePacket {

	private static final String C_USE_PET_ITEM = "[C] C_UsePetItem";
	private static Logger _log = Logger.getLogger(C_UsePetItem.class.getName());

	public C_UsePetItem(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);

		int data = readC();
		int petId = readD();
		int listNo = readC();

		L1PetInstance pet = (L1PetInstance) L1World.getInstance().findObject(
				petId);
		L1PcInstance pc = clientthread.getActiveChar();

		if (pet == null && pc == null) {
			return;
		}
		L1ItemInstance item = pet.getInventory().getItems().get(listNo);
		if (item == null) {
			return;
		}

		if ((item.getItem().getType2() == 0)// 種別：その他のアイテム
				&& (item.getItem().getType() == 11)) { // petitem
			L1PetType petType = PetTypeTable.getInstance().get(
					pet.getNpcTemplate().getNpcId());
			if (petType != null) {
				if (petType.getTameItemId() != 0) {
					pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
					return;
				}
			}
			int itemId = item.getItem().getItemId();
			if (((itemId >= 40749) && (itemId <= 40752))
					|| ((itemId >= 40756) && (itemId <= 40758))) {
				usePetWeapon(pc, pet, item);
				pc.sendPackets(new S_PetEquipment(data, pet, listNo));
			} else if ((itemId >= 40761) && (itemId <= 40766)) {
				usePetArmor(pc, pet, item);
				pc.sendPackets(new S_PetEquipment(data, pet, listNo));
			} else {
				pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
			}
		} else {
			pc.sendPackets(new S_ServerMessage(74, item.getLogName()));
		}
	}

	private void usePetWeapon(L1PcInstance pc, L1PetInstance pet,
			L1ItemInstance weapon) {
		if (pet.getWeapon() == null) {
			setPetWeapon(pc, pet, weapon);
		} else { // 既に何かを装備している場合、前の装備をはずす
			if (pet.getWeapon().equals(weapon)) {
				removePetWeapon(pc, pet, pet.getWeapon());
			} else {
				removePetWeapon(pc, pet, pet.getWeapon());
				setPetWeapon(pc, pet, weapon);
			}
		}
	}

	private void usePetArmor(L1PcInstance pc, L1PetInstance pet,
			L1ItemInstance armor) {
		if (pet.getArmor() == null) {
			setPetArmor(pc, pet, armor);
		} else { // 既に何かを装備している場合、前の装備をはずす
			if (pet.getArmor().equals(armor)) {
				removePetArmor(pc, pet, pet.getArmor());
			} else {
				removePetArmor(pc, pet, pet.getArmor());
				setPetArmor(pc, pet, armor);
			}
		}
	}

	private void setPetWeapon(L1PcInstance pc, L1PetInstance pet,
			L1ItemInstance weapon) {
		int itemId = weapon.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.setHitByWeapon(petItem.getHitModifier());
		pet.setDamageByWeapon(petItem.getDamageModifier());
		pet.addStr(petItem.getAddStr());
		pet.addCon(petItem.getAddCon());
		pet.addDex(petItem.getAddDex());
		pet.addInt(petItem.getAddInt());
		pet.addWis(petItem.getAddWis());
		pet.addMaxHp(petItem.getAddHp());
		pet.addMaxMp(petItem.getAddMp());
		pet.addSp(petItem.getAddSp());
		pet.addMr(petItem.getAddMr());

		pet.setWeapon(weapon);
		weapon.setEquipped(true);
	}

	private void removePetWeapon(L1PcInstance pc, L1PetInstance pet,
			L1ItemInstance weapon) {
		int itemId = weapon.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.setHitByWeapon(0);
		pet.setDamageByWeapon(0);
		pet.addStr(-petItem.getAddStr());
		pet.addCon(-petItem.getAddCon());
		pet.addDex(-petItem.getAddDex());
		pet.addInt(-petItem.getAddInt());
		pet.addWis(-petItem.getAddWis());
		pet.addMaxHp(-petItem.getAddHp());
		pet.addMaxMp(-petItem.getAddMp());
		pet.addSp(-petItem.getAddSp());
		pet.addMr(-petItem.getAddMr());

		pet.setWeapon(null);
		weapon.setEquipped(false);
	}

	private void setPetArmor(L1PcInstance pc, L1PetInstance pet,
			L1ItemInstance armor) {
		int itemId = armor.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.addAc(petItem.getAddAc());
		pet.addStr(petItem.getAddStr());
		pet.addCon(petItem.getAddCon());
		pet.addDex(petItem.getAddDex());
		pet.addInt(petItem.getAddInt());
		pet.addWis(petItem.getAddWis());
		pet.addMaxHp(petItem.getAddHp());
		pet.addMaxMp(petItem.getAddMp());
		pet.addSp(petItem.getAddSp());
		pet.addMr(petItem.getAddMr());

		pet.setArmor(armor);
		armor.setEquipped(true);
	}

	private void removePetArmor(L1PcInstance pc, L1PetInstance pet,
			L1ItemInstance armor) {
		int itemId = armor.getItem().getItemId();
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(itemId);
		if (petItem == null) {
			return;
		}

		pet.addAc(-petItem.getAddAc());
		pet.addStr(-petItem.getAddStr());
		pet.addCon(-petItem.getAddCon());
		pet.addDex(-petItem.getAddDex());
		pet.addInt(-petItem.getAddInt());
		pet.addWis(-petItem.getAddWis());
		pet.addMaxHp(-petItem.getAddHp());
		pet.addMaxMp(-petItem.getAddMp());
		pet.addSp(-petItem.getAddSp());
		pet.addMr(-petItem.getAddMr());

		pet.setArmor(null);
		armor.setEquipped(false);
	}

	@Override
	public String getType() {
		return C_USE_PET_ITEM;
	}
}