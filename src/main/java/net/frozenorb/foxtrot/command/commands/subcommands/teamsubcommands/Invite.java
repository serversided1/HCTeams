package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Invite {

    @Command(names={ "team invite", "t invite", "f invite", "faction invite", "fac invite" }, permissionNode="")
    public static void teamInvite(Player sender, @Param(name="Parameter") String params) {
        String[] args = ("arg1 " + params).split(" ");
		final Player p = (Player) sender;

		if (args.length == 2) {

			String name = args[1];
			if (Bukkit.getPlayer(args[1]) != null) {
				name = Bukkit.getPlayer(args[1]).getName();
			} else {
				sender.sendMessage(ChatColor.RED + "That player could not be found.");
				return;
			}
			Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());
			if (team == null) {
				sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
				return;
			}
			if (team.getMembers().size() >= Team.MAX_TEAM_SIZE) {
				sender.sendMessage(ChatColor.RED + "The max team size is " + Team.MAX_TEAM_SIZE + "!");
				return;
			}
			if (team.isOwner(p.getName()) || team.isCaptain(p.getName())) {
				if (!team.isMember(name)) {

					if (team.getInvitations().contains(name)) {
						sender.sendMessage(ChatColor.RED + "That player has already been invited.");
						return;
					}

                    if(team.isRaidable()){
                        sender.sendMessage(ChatColor.RED + "You may not invite players if your team is raidable! You must boost your DTR!");
                        return;
                    }

					team.getInvitations().add(name);
					Bukkit.getPlayerExact(name).sendMessage(ChatColor.GRAY + "You have been invited to team '§e" + team.getFriendlyName() + "§7'. Type '§3/team join §e" + team.getFriendlyName() + "§7' to join.");
					sender.sendMessage("§e" + name + " has been invited to the team!");
				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Player is already on your team.");
				}
			} else
				p.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
		} else {

		}

	}

}