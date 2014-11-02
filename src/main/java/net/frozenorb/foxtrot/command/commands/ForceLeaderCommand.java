package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceLeaderCommand {

    @Command(names={ "ForceLeader" }, permissionNode="foxtrot.forceleader")
    public static void forceLeader(Player sender, @Param(name="Team") Team team,  @Param(name="Target", defaultValue="self") String target) {
        if (target.equals("self")) {
            target = sender.getName();
        }

        if (!FoxtrotPlugin.getInstance().getPlaytimeMap().contains(target)) {
            sender.sendMessage(ChatColor.RED + "That player has never played here before!");
        } else {
            if (FoxtrotPlugin.getInstance().getTeamHandler().isOnTeam(target) && FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(target).isOwner(target)) {
                sender.sendMessage(ChatColor.RED + "That player is the owner of their current team!");
                return;
            }

            FoxtrotPlugin.getInstance().getTeamHandler().removePlayerFromTeam(target);
            FoxtrotPlugin.getInstance().getTeamHandler().setTeam(target, team);

            team.addMember(target);
            team.setOwner(target);

            Player player = Bukkit.getPlayerExact(target);

            if (player != null) {
                player.sendMessage(ChatColor.YELLOW + "You are now the owner of §b" + team.getFriendlyName());
            }

            sender.sendMessage(ChatColor.YELLOW + target + " is now the owner of §b" + team.getFriendlyName());
        }
    }

}