package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.qlib.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamLeaveCommand {

    @Command(names={ "team leave", "t leave", "f leave", "faction leave", "fac leave" }, permissionNode="")
    public static void teamLeave(Player sender) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(sender);

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
            FoxtrotPlugin.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");
        } else {
            FoxtrotPlugin.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            team.flagForSave();

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the team.");
                }
            }

            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
        }

        NametagManager.reloadPlayer(sender);
        NametagManager.sendTeamsToPlayer(sender);
    }

}