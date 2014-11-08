package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Promote {

    @Command(names={ "team promote", "t promote", "f promote", "faction promote", "fac promote", "team captain", "t captain", "f captain", "faction captain", "fac captain" }, permissionNode="")
    public static void teamPromote(Player sender, @Param(name="Player") OfflinePlayer target) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getName()) || sender.isOp()) {
            if (team.isMember(target.getName())) {
                if (team.isCaptain(target.getName())) {
                    sender.sendMessage(ChatColor.RED + "That player is already a Captain!");
                    return;
                }

                if (team.isOwner(target.getName())) {
                    sender.sendMessage(ChatColor.RED + "You can only promote team members!");
                    return;
                }

                for (Player player : team.getOnlineMembers()) {
                    player.sendMessage(ChatColor.DARK_AQUA + target.getName() + " has been made a Captain!");
                }

                team.addCaptain(target.getName());
            } else {
                sender.sendMessage(ChatColor.DARK_AQUA + "Player is not on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team leaders can do this.");
        }
	}

}