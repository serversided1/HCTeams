package net.frozenorb.foxtrot.server.commands.highroller;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class HighrollerAddCommand {

    @Command(names={ "highroller add", "highrollers add" }, permissionNode="op")
    public static void highrollerAdd(Player sender, @Parameter(name="Player") OfflinePlayer player) {
        if (!FoxtrotPlugin.getInstance().getServerHandler().getHighRollers().contains(player.getName())) {
            FoxtrotPlugin.getInstance().getServerHandler().getHighRollers().add(player.getName());
            FoxtrotPlugin.getInstance().getServerHandler().save();
            sender.sendMessage(ChatColor.GREEN + "Added " + player.getName() + "'s HighRoller tag.");
        } else {
            sender.sendMessage(ChatColor.RED + player.getName() + " is already a HighRoller.");
        }
    }

}