package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class Leader {

    @Command(names={ "team newleader", "t newleader", "f newleader", "faction newleader", "fac newleader", "team leader", "t leader", "f leader", "faction leader", "fac leader" }, permissionNode="")
    public static void teamLeader(Player sender, @Param(name="player") String leader) {
        Player bukkitPlayer = FoxtrotPlugin.getInstance().getServer().getPlayer(leader);

        if (bukkitPlayer != null) {
            leader = bukkitPlayer.getName();
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getName())) {
            if (team.isMember(leader)) {
                leader = team.getActualPlayerName(leader);

                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (team.isMember(player)) {
                        player.sendMessage(ChatColor.DARK_AQUA + leader + " is now the new leader!");
                    }
                }

                team.setOwner(leader);
                team.addCaptain(sender.getName());
            } else {
                sender.sendMessage(ChatColor.DARK_AQUA + "Player is not on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");
        }
	}

}