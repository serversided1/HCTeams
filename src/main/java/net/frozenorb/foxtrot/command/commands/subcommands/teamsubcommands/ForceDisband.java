package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by chasechocolate.
 */
public class ForceDisband {

    @Command(names={ "team forcedisband", "t forcedisband", "f forcedisband", "faction forcedisband", "fac forcedisband" }, permissionNode="")
    public static void teamForceDisband(Player player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if(team == null){
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        //Disband team
        for(Player online : team.getOnlineMembers()){
            online.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + " has force-disbanded the team.");
        }

        FoxtrotPlugin.getInstance().getTeamHandler().removeTeam(team.getName());
    }

}