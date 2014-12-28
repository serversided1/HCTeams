package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpawnCommand {

    @Command(names={ "spawn" }, permissionNode="")
    public static void spawn(Player sender) {
        if (sender.hasPermission("foxtrot.spawn")) {
            sender.teleport(FoxtrotPlugin.getInstance().getServerHandler().getSpawnLocation());
        } else {
            sender.sendMessage(ChatColor.RED + "HCTeams does not have a spawn command! You must walk there!");
        }
    }

}