package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamSetHQCommand {

    @Command(names={ "team sethq", "t sethq", "f sethq", "faction sethq", "fac sethq", "team sethome", "t sethome", "f sethome", "faction sethome", "fac sethome", "sethome" }, permissionNode="")
    public static void teamSetHQ(Player sender) {
		Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

		if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getName()) || team.isCaptain(sender.getName())) {
            if (FoxtrotPlugin.getInstance().getTeamHandler().getOwner(sender.getLocation()) != team) {
                sender.sendMessage(ChatColor.RED + "You can only set HQ in your team's territory.");
                return;
            }

            team.setHQ(sender.getLocation());

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (team.isMember(player)) {
                    player.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's HQ point!");
                }
            }

            sender.sendMessage(ChatColor.DARK_AQUA + "Headquarters set.");
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
	}

}