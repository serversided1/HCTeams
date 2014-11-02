package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Demote {

    @Command(names={ "team demote", "t demote", "f demote", "faction demote", "fac demote" }, permissionNode="")
    public static void teamChat(Player sender, @Param(name="Parameter") String params) {
        String[] args = ("arg1 " + params).split(" ");
        final Player p = (Player) sender;

        if (args.length == 2) {

            String name = args[1];
            if (Bukkit.getPlayer(args[1]) != null) {
                name = Bukkit.getPlayer(args[1]).getName();
            }
            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());
            if (team == null) {
                sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
                return;
            }
            if (team.isOwner(p.getName()) || p.isOp()) {
                if (team.isMember(name)) {

                    if (!team.isCaptain(name)) {
                        p.sendMessage(ChatColor.RED + "You can only demote team Captains!");
                        return;
                    }

                    for (Player pm : team.getOnlineMembers()) {
                        pm.sendMessage(ChatColor.DARK_AQUA + team.getActualPlayerName(name) + " has been made a member!");
                    }

                    team.removeCaptain(name);

                } else {
                    p.sendMessage(ChatColor.DARK_AQUA + "Player is not on your team.");
                }
            } else
                p.sendMessage(ChatColor.DARK_AQUA + "Only team leaders can do this.");
        } else {

        }

    }

}