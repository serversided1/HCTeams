package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by chasechocolate.
 */
public class Uninvite extends Subcommand {

    public Uninvite(String name, String errorMessage, String... aliases) {
        super(name, errorMessage, aliases);
    }

    @Override
    public void syncExecute() {
        Player p = (Player) sender;
        Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

        if(team == null){
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if(team.isOwner(p.getName()) || team.isCaptain(p.getName())){
            if(args.length <= 1){
                p.sendMessage(ChatColor.RED + "Usage: /f uninvite <all | player>");
                return;
            }

            if(args[1].equalsIgnoreCase("all")){
                team.getInvitations().clear();
                p.sendMessage(ChatColor.GRAY + "You have cleared all pending invitations.");
            } else {
                String remove = null;

                for(String name : team.getInvitations()){
                    if(name.equalsIgnoreCase(args[1])){
                        remove = name;
                        break;
                    }
                }

                if(remove != null){
                    team.getInvitations().remove(remove);
                    team.setChanged(true);
                    p.sendMessage(ChatColor.GREEN + "Cancelled pending invitation for " + remove + "!");
                } else {
                    p.sendMessage(ChatColor.RED + "No pending invitation for '" + args[1] + "'!");
                }
            }
        } else {
            p.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

    @Override
    public List<String> tabComplete(){
        List<String> players = new ArrayList<>();
        Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(sender.getName());

        if(team != null){
            if(team.getInvitations().size() > 0){
                players.add("all");

                for(String invitation : team.getInvitations()){
                    players.add(invitation);
                }
            }
        }

        return players;
    }
}
