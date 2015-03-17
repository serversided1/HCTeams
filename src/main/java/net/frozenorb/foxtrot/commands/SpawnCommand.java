package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpawnCommand {

    @Command(names={ "spawn" }, permissionNode="")
    public static void spawn(Player sender) {
        if (sender.hasPermission("foxtrot.spawn")) {
            sender.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
        } else {
            // Make this pretty.
            sender.sendMessage(ChatColor.RED + "HCTeams does not have a spawn command! You must walk there!");
        }
    }

}