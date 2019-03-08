package net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.command;

import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.menu.ArcherUpgradeProgressMenu;
import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class ArcherUpgradesCommand {

	@Command(names = { "archerupgrades", "archerabilities" }, permission = "")
	public static void showProgress(Player player) {
		new ArcherUpgradeProgressMenu().openMenu(player);
	}

}
