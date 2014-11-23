package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceJoinCommand {

    @Command(names={ "ForceJoin" }, permissionNode="foxtrot.forcejoin")
    public static void forceJoin(Player sender, @Param(name="Team") Team team,  @Param(name="Target", defaultValue="self") Player target) {
        if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(target.getName()) != null) {
            if (target == sender) {
                sender.sendMessage(ChatColor.RED + "Leave your current team before attempting to forcejoin.");
            } else {
                sender.sendMessage(ChatColor.RED + "That player needs to leave their current team first!");
            }

            return;
        }

        team.addMember(target.getName());
        FoxtrotPlugin.getInstance().getTeamHandler().setTeam(target.getName(), team);
        target.sendMessage(ChatColor.GREEN + "You are now a member of §b" + team.getName() + "§a!");

        if (target != sender) {
            sender.sendMessage("§aPlayer added to team!");
        }
    }

}