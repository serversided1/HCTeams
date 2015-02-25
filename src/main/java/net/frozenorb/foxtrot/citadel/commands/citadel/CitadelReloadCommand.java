package net.frozenorb.foxtrot.citadel.commands.citadel;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelReloadCommand {

    @Command(names = {"citadel reload"}, permissionNode = "op")
    public static void citadelReload(Player sender) {
        FoxtrotPlugin.getInstance().getCitadelHandler().loadCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Reloaded the Citadel config.");
    }

}