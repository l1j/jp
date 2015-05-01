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

import java.util.logging.Logger;
import jp.l1j.configure.Config;
import jp.l1j.server.command.GMCommandConfigs;
import jp.l1j.server.controller.timer.MapTimeController;
import jp.l1j.server.controller.timer.ShutdownTimeController;
import jp.l1j.server.datatables.ArmorSetTable;
import jp.l1j.server.datatables.CookingRecipeTable;
import jp.l1j.server.datatables.DoorTable;
import jp.l1j.server.datatables.DropRateTable;
import jp.l1j.server.datatables.DropTable;
import jp.l1j.server.datatables.DungeonTable;
import jp.l1j.server.datatables.ItemRateTable;
import jp.l1j.server.datatables.ItemTable;
import jp.l1j.server.datatables.MagicDollTable;
import jp.l1j.server.datatables.MapTable;
import jp.l1j.server.datatables.MobGroupTable;
import jp.l1j.server.datatables.MobSkillTable;
import jp.l1j.server.datatables.NpcActionTable;
import jp.l1j.server.datatables.NpcChatTable;
import jp.l1j.server.datatables.NpcTable;
import jp.l1j.server.datatables.PetItemTable;
import jp.l1j.server.datatables.PetTypeTable;
import jp.l1j.server.datatables.PolyTable;
import jp.l1j.server.datatables.RandomDungeonTable;
import jp.l1j.server.datatables.ResolventTable;
import jp.l1j.server.datatables.RestartLocationTable;
import jp.l1j.server.datatables.ReturnLocationTable;
import jp.l1j.server.datatables.ShopTable;
import jp.l1j.server.datatables.SkillTable;
import jp.l1j.server.datatables.SprTable;
import jp.l1j.server.datatables.TrapTable;
import jp.l1j.server.datatables.WeaponSkillTable;
import jp.l1j.server.model.L1BossCycle;
import jp.l1j.server.model.instance.L1PcInstance;
import jp.l1j.server.model.item.executor.L1BeginnerItem;
import jp.l1j.server.model.item.executor.L1BlankScroll;
import jp.l1j.server.model.item.executor.L1BlessOfEva;
import jp.l1j.server.model.item.executor.L1BluePotion;
import jp.l1j.server.model.item.executor.L1BravePotion;
import jp.l1j.server.model.item.executor.L1CurePotion;
import jp.l1j.server.model.item.executor.L1Elixir;
import jp.l1j.server.model.item.executor.L1EnchantBonus;
import jp.l1j.server.model.item.executor.L1EnchantProtectScroll;
import jp.l1j.server.model.item.executor.L1ExtraPotion;
import jp.l1j.server.model.item.executor.L1FireCracker;
import jp.l1j.server.model.item.executor.L1FloraPotion;
import jp.l1j.server.model.item.executor.L1Furniture;
import jp.l1j.server.model.item.executor.L1GreenPotion;
import jp.l1j.server.model.item.executor.L1HealingPotion;
import jp.l1j.server.model.item.executor.L1MagicEye;
import jp.l1j.server.model.item.executor.L1MagicPotion;
import jp.l1j.server.model.item.executor.L1Material;
import jp.l1j.server.model.item.executor.L1MaterialChoice;
import jp.l1j.server.model.item.executor.L1PolyPotion;
import jp.l1j.server.model.item.executor.L1PolyScroll;
import jp.l1j.server.model.item.executor.L1PolyWand;
import jp.l1j.server.model.item.executor.L1Roulette;
import jp.l1j.server.model.item.executor.L1ShowMessage;
import jp.l1j.server.model.item.executor.L1SpawnWand;
import jp.l1j.server.model.item.executor.L1SpeedUpClock;
import jp.l1j.server.model.item.executor.L1SpellIcon;
import jp.l1j.server.model.item.executor.L1SpellItem;
import jp.l1j.server.model.item.executor.L1TeleportAmulet;
import jp.l1j.server.model.item.executor.L1ThirdSpeedPotion;
import jp.l1j.server.model.item.executor.L1TreasureBox;
import jp.l1j.server.model.item.executor.L1UniqueEnchantScroll;
import jp.l1j.server.model.item.executor.L1UnknownMaliceWeapon;
import jp.l1j.server.model.item.executor.L1WisdomPotion;
import jp.l1j.server.model.map.executor.L1MapLimiter;
import jp.l1j.server.packets.server.S_SystemMessage;

public class L1ReloadConfig implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1ReloadConfig.class.getName());

	private L1ReloadConfig() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ReloadConfig();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		pc.sendPackets(new S_SystemMessage("reloading the configs..."));
		// configs
		Config.load();
		GMCommandConfigs.getInstance().reload();
		L1BossCycle.reload();
		MapTimeController.getInstance().reload();
		if (Config.AUTO_SHUTDOWN) {
			ShutdownTimeController.getInstance().reload();
		}
		// datatables
		ArmorSetTable.getInstance().reload();
		CookingRecipeTable.getInstance().reload();
		DoorTable.getInstance().reload();
		DropRateTable.getInstance().reload();
		DropTable.getInstance().reload();
		DungeonTable.getInstance().reload();
		ItemTable.getInstance().reload();
		ItemRateTable.getInstance().reload();
		MagicDollTable.getInstance().reload();
		MapTable.getInstance().reload();
		MobGroupTable.getInstance().reload();
		MobSkillTable.getInstance().reload();
		NpcActionTable.getInstance().reload();
		NpcChatTable.getInstance().reload();
		NpcTable.getInstance().reload();
		PetItemTable.getInstance().reload();
		PetTypeTable.getInstance().reload();
		PolyTable.getInstance().reload();
		RandomDungeonTable.getInstance().reload();
		ResolventTable.getInstance().reload();
		RestartLocationTable.getInstance().reload();
		ReturnLocationTable.reload();
		ShopTable.getInstance().reload();
		SkillTable.getInstance().reload();
		SprTable.getInstance().reload();
		TrapTable.getInstance().reload();
		WeaponSkillTable.getInstance().reload();
		// xml files
		L1BeginnerItem.reload();
		L1BlankScroll.reload();
		L1BlessOfEva.reload();
		L1BluePotion.reload();
		L1BravePotion.reload();
		L1CurePotion.reload();
		L1Elixir.reload();
		L1EnchantBonus.reload();
		L1EnchantProtectScroll.reload();
		L1ExtraPotion.reload();
		L1FireCracker.reload();
		L1FloraPotion.reload();
		L1Furniture.reload();
		L1GreenPotion.reload();
		L1HealingPotion.reload();
		L1MagicEye.reload();
		L1MagicPotion.reload();
		L1MapLimiter.reload();
		L1Material.reload();
		L1MaterialChoice.reload();
		L1PolyPotion.reload();
		L1PolyScroll.reload();
		L1PolyWand.reload();
		L1Roulette.reload();
		L1ShowMessage.reload();
		L1SpawnWand.reload();
		L1SpeedUpClock.reload();
		L1SpellIcon.reload();
		L1SpellItem.reload();
		L1TeleportAmulet.reload();
		L1ThirdSpeedPotion.reload();
		L1TreasureBox.reload();
		L1UniqueEnchantScroll.reload();
		L1UnknownMaliceWeapon.reload();
		L1WisdomPotion.reload();
		pc.sendPackets(new S_SystemMessage("reload the configs is complete."));
	}
}
