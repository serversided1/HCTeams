package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;

public class SpawnCommand {

    @Command(names={ "spawn" }, permission="")
    public static void spawn(Player sender) {
        if (sender.hasPermission("foxtrot.spawn")) {
            sender.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
        } else {
            // Make this pretty.
            String serverName = Foxtrot.getInstance().getServerHandler().getServerName();

            sender.sendMessage(ChatColor.RED + serverName + " does not have a spawn command! You must walk there!");
            sender.sendMessage(ChatColor.RED + "Spawn is located at 0,0.");
        }
    }

}
