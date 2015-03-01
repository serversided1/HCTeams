package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocationCommand {

    @Command(names={ "Location", "Here", "WhereAmI", "Loc" }, permissionNode="")
    public static void location(Player sender) {
        Location loc = sender.getLocation();
        Team owner = LandBoard.getInstance().getTeam(loc);

        if (owner != null) {
            sender.sendMessage(ChatColor.YELLOW + "You are in " + owner.getName(sender.getPlayer()) + ChatColor.YELLOW + "'s territory.");
            return;
        }

        if (!FoxtrotPlugin.getInstance().getServerHandler().isWarzone(loc)) {
            sender.sendMessage(ChatColor.YELLOW + "You are in " + ChatColor.GRAY + "The Wilderness" + ChatColor.YELLOW + "!");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "You are in the " + ChatColor.RED + "Warzone" + ChatColor.YELLOW + "!");
        }
    }

}