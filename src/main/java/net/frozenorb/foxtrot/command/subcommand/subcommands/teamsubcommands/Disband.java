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
public class Disband extends Subcommand {

    public Disband(String name, String errorMessage, String... aliases) {
        super(name, errorMessage, aliases);
    }

    @Override
    public void syncExecute() {
        Player player = (Player) sender;
        Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(player.getName());

        if(team == null){
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        //Owner check
        if(!(team.isOwner(player.getName()))){
            player.sendMessage(ChatColor.RED + "You must be the leader of the team to disband it!");
            return;
        }

        if (team.isRaidable()) {
            player.sendMessage(ChatColor.RED + "You cannot disband your team while raidable.");
            return;
        }

        //Disband team
        for(Player online : team.getOnlineMembers()){
            online.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + player.getName() + " has disbanded the team.");
        }

        FoxtrotPlugin.getInstance().getTeamManager().removeTeam(team.getName());
    }

    @Override
    public List<String> tabComplete() {
        return new ArrayList<String>();
    }
}