package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceLeaderCommand {

    @Command(names={ "ForceLeader" }, permissionNode="foxtrot.forceleader")
    public static void forceLeader(Player sender, @Param(name="Team") Team team,  @Param(name="Target", defaultValue="self") String target) {
        if (target.equals("self")) {
            target = sender.getName();
        } else if (target.equals("null")) {
            target = null;
        }

        if (target != null && !FoxtrotPlugin.getInstance().getPlaytimeMap().contains(target)) {
            sender.sendMessage(ChatColor.RED + "That player has never played here before!");
        } else {
            if (target != null) {
                if (FoxtrotPlugin.getInstance().getTeamHandler().isOnTeam(target) && FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(target).isOwner(target)) {
                    sender.sendMessage(ChatColor.RED + "That player is the owner of their current team!");
                    return;
                }

                FoxtrotPlugin.getInstance().getTeamHandler().removePlayerFromTeam(target);
                FoxtrotPlugin.getInstance().getTeamHandler().setTeam(target, team);
            }

            team.setOwner(target);

            sender.sendMessage(ChatColor.GREEN + target + " is now the owner of §b" + team.getFriendlyName());
        }
    }

}