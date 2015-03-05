package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamLeaderCommand {

    @Command(names={ "team newleader", "t newleader", "f newleader", "faction newleader", "fac newleader", "team leader", "t leader", "f leader", "faction leader", "fac leader" }, permissionNode="")
    public static void teamLeader(Player sender, @Parameter(name="player") OfflinePlayer target) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");
            return;
        }

        if (!team.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not on your team.");
            return;
        }

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.DARK_AQUA + target.getName() + " has been given ownership of " + team.getName() + ".");
            }
        }

        team.setOwner(target.getUniqueId());
        team.addCaptain(sender.getUniqueId());
    }

}