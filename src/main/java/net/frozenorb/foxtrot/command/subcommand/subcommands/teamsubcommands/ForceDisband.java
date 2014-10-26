package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chasechocolate.
 */
public class ForceDisband extends Subcommand {

    public ForceDisband(String name, String errorMessage, String... aliases) {
        super(name, errorMessage, aliases);
    }

    @Override
    public void syncExecute(){
        if(!(sender.isOp()) || !(sender.getName().equalsIgnoreCase("Nauss")) || !(sender.hasPermission("foxtrot.forcedisband"))){
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
            return;
        }

        Player player = (Player) sender;
        Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(player.getName());

        if(team == null){
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        //Disband team
        for(Player online : team.getOnlineMembers()){
            online.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + " has force-disbanded the team.");
        }

        FoxtrotPlugin.getInstance().getTeamManager().removeTeam(team.getName());
    }

    @Override
    public List<String> tabComplete() {
        return new ArrayList<String>();
    }
}