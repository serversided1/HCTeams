package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/25/2014.
 */
public class CitadelTickLootCommand {

    @Command(names={"citadel tickloot"}, permissionNode="op")
    public static void citadelTickLoot(Player sender) {
        FoxtrotPlugin.getInstance().getCitadelHandler().tickCitadelChests();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Ticked all Citadel loot.");
    }

}