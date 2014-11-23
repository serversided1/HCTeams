package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/23/2014.
 */
public class CitadelReloadCommand {

    @Command(names = {"Citadel Reload"}, permissionNode = "op")
    public static void citadelReload(Player sender) {
        FoxtrotPlugin.getInstance().getCitadelHandler().reloadCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Reloaded the Citadel config.");
    }

}