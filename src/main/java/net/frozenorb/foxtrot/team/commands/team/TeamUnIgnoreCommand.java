package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.bukkit.ChatColor.*;

public class TeamUnIgnoreCommand {

    @Command(names = {"team unignore", "t unignore", "f unignore", "faction unignore", "fac unignore"}, permission = "")
    public static void unignoreTeam(Player sender, @Param(name = "team") String target) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        Team targetTeam = Foxtrot.getInstance().getTeamHandler().getTeam(target);

        if (senderTeam != null) {
            if (targetTeam != null) {
                if (senderTeam.isCaptain(sender.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "You must be a captain to un-ignore another faction!");
                    return;
                }

                if (senderTeam.getIgnoring().contains(targetTeam.getUniqueId())) {
                    senderTeam.getIgnoring().remove(targetTeam.getUniqueId());

                    senderTeam.sendMessage(GREEN + "Your team has un-ignoring " + GOLD + targetTeam.getName() + GREEN + ".");
                } else {
                    sender.sendMessage(RED + "Your team does not ignore " + YELLOW + targetTeam.getName() + RED + "!");
                }
            } else {
                sender.sendMessage(RED + "That player doesn't have a team. Just /ignore them.");
            }
        } else {
            sender.sendMessage(RED + "Make a team to ignore another team!");
        }
    }
}
