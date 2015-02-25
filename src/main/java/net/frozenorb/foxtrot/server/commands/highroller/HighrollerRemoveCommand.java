package net.frozenorb.foxtrot.server.commands.highroller;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class HighrollerRemoveCommand {

    @Command(names={ "highroller remove", "highrollers remove" }, permissionNode="op")
    public static void highrollerRemove(Player sender, @Param(name="Player") OfflinePlayer player) {
        if (FoxtrotPlugin.getInstance().getServerHandler().getHighRollers().contains(player.getName())) {
            FoxtrotPlugin.getInstance().getServerHandler().getHighRollers().remove(player.getName());
            FoxtrotPlugin.getInstance().getServerHandler().save();
            sender.sendMessage(ChatColor.GREEN + "Removed " + player.getName() + "'s HighRoller tag.");
        } else {
            sender.sendMessage(ChatColor.RED + player.getName() + " isn't a HighRoller.");
        }
    }

}