package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxConstants;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class TellLocationCommand {

    @Command(names = {"telllocation", "tl"}, permission = "")
    public static void tellLocation(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You're not on a team!");
            return;
        }

        Location l = sender.getLocation();
        team.sendMessage(FoxConstants.teamChatFormat(sender, String.format("[%.1f, %.1f, %.1f]", l.getX(), l.getY(), l.getZ())));
    }

}
