package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamForceLeaveCommand {

    @Command(names={  "team forceleave", "t forceleave", "f forceleave", "faction forceleave", "fac forceleave", "t fl", "team fl" }, permissionNode="")
    public static void forceLeave(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) && team.getSize() > 1) {
            sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
            return;
        }

        if (LandBoard.getInstance().getTeam(sender.getLocation()) == team) {
            sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
            return;
        }

        if (team.removeMember(sender.getUniqueId())) {
            team.disband();
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");
        } else {
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            team.flagForSave();

            if (SpawnTagHandler.isTagged(sender)) {
                team.setDTR(team.getDTR() - 1);
                team.sendMessage(ChatColor.RED + sender.getName() + " forcibly left the team. Your team has lost 1 DTR.");

                sender.sendMessage(ChatColor.RED + "You have forcibly left your team. Your team lost 1 DTR.");
            } else {
                team.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the team.");

                sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
            }
        }

        FrozenNametagHandler.reloadPlayer(sender);
        FrozenNametagHandler.reloadOthersFor(sender);
    }
}
