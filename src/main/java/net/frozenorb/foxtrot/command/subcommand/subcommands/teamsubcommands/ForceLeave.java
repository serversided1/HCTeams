package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class ForceLeave extends Subcommand {

    public ForceLeave(String name, String errorMessage, String... aliases) {
        super(name, errorMessage, aliases);
    }

    @Override
    public void syncExecute(){
        if(!(sender.isOp()) || !(sender.hasPermission("foxtrot.forceleave"))){
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
            return;
        }

        Player player = (Player) sender;
        Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(player.getName());

        if(team == null){
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        player.removeMetadata("teamChat", FoxtrotPlugin.getInstance());
        team.remove(sender.getName());
        team.setOwner(null);
        FoxtrotPlugin.getInstance().getTeamManager().removePlayerFromTeam(sender.getName());
        player.sendMessage(ChatColor.GRAY + "Force-left your team.");
    }

    @Override
    public List<String> tabComplete() {
        return new ArrayList<String>();
    }
}