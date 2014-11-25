package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/25/2014.
 */
public class CitadelRescanLootCommand {

    @Command(names = {"citadel rescanloot"}, permissionNode = "op")
    public static void citadelRespawnLoot(Player sender) {
        FoxtrotPlugin.getInstance().getCitadelHandler().scanLoot();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Rescanned all Citadel loot.");
    }

}