package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocationCommand {

    @Command(names={ "Location", "Here", "WhereAmI", "Loc" }, permissionNode="")
    public static void location(Player sender, @Param(name="Target", defaultValue="self") String target) {
        Location loc = sender.getLocation();
        net.frozenorb.foxtrot.team.Team owner = LandBoard.getInstance().getTeam(loc);

        if (owner != null) {
            sender.sendMessage("§eYou are in §c" + owner.getName() + "§e's territory.");
            return;
        }

        if (!FoxtrotPlugin.getInstance().getServerHandler().isWarzone(loc)) {
            sender.sendMessage(ChatColor.YELLOW + "You are in §7The Wilderness§e!");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "You are in the §cWarzone§e!");
        }
    }

}