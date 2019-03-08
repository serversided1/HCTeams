package net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.menu.ArcherUpgradeProgressMenu;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArcherUpgradesCommand {

	@Command(names = { "archerupgrades", "archerabilities" }, permission = "")
	public static void showProgress(Player player) {
		if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
			new ArcherUpgradeProgressMenu().openMenu(player);
		} else {
			player.sendMessage(ChatColor.RED + "You can't use that command on this server.");
		}
	}

}
