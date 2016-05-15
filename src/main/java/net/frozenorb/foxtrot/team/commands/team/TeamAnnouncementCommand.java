package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamAnnouncementCommand {

    // anouncement is here for those who can't spell.
    @Command(names={ "team announcement", "t announcement", "f announcement", "faction announcement", "fac announcement", "team anouncement", "t anouncement", "f anouncement", "faction anouncement", "fac anouncement" }, permission="")
    public static void teamAnnouncement(Player sender, @Param(name="new announcement", wildcard=true) String newAnnouncement) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (newAnnouncement.equalsIgnoreCase("clear")) {
            team.setAnnouncement(null);
            sender.sendMessage(ChatColor.YELLOW + "Team announcement cleared.");
            return;
        }

        team.setAnnouncement(newAnnouncement);
        team.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW  + " changed the team announcement to " + ChatColor.LIGHT_PURPLE + newAnnouncement);
    }

}