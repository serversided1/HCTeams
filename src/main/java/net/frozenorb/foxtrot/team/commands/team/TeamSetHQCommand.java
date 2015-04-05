package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamSetHQCommand {

    @Command(names={ "team sethq", "t sethq", "f sethq", "faction sethq", "fac sethq", "team sethome", "t sethome", "f sethome", "faction sethome", "fac sethome", "sethome", "sethq" }, permissionNode="")
    public static void teamSetHQ(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (LandBoard.getInstance().getTeam(sender.getLocation()) != team) {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You can only set HQ in your team's territory.");
                    return;
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "That action would normally be disallowed, but this check is being bypassed due to your rank.");
                }
            }

            team.setHQ(sender.getLocation());
            team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's HQ point!");
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

}