package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/3/2014.
 */
public class Invites {

    @Command(names={ "team invites", "t invites", "f invites", "faction invites", "fac invites" }, permissionNode="")
    public static void teamInvites(Player sender) {
        StringBuilder yourInvites = new StringBuilder();

        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            if (team.getInvitations().contains(sender.getName())) {
                yourInvites.append(ChatColor.GRAY).append(team.getFriendlyName()).append(ChatColor.YELLOW).append(", ");
            }
        }

        if (yourInvites.length() > 2) {
            yourInvites.setLength(yourInvites.length() - 2);
        } else {
            yourInvites.append(ChatColor.GRAY).append("No pending invites.");
        }

        sender.sendMessage(ChatColor.YELLOW + "Your Invites: " + yourInvites.toString());

        Team current = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (current != null) {
            StringBuilder invitedToYourTeam = new StringBuilder();

            for (String invites : current.getInvitations()) {
                invitedToYourTeam.append(ChatColor.GRAY).append(invites).append(ChatColor.YELLOW).append(", ");
            }

            if (invitedToYourTeam.length() > 2) {
                invitedToYourTeam.setLength(invitedToYourTeam.length() - 2);
            } else {
                invitedToYourTeam.append(ChatColor.GRAY).append("No pending invites.");
            }

            sender.sendMessage(ChatColor.YELLOW + "Invited to your Team: " + invitedToYourTeam.toString());
        }
    }

}