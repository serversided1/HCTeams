package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by chasechocolate.
 */
public class Uninvite {

    @Command(names={ "team uninvite", "t uninvite", "f uninvite", "faction uninvite", "fac uninvite", "team revoke", "t revoke", "f revoke", "faction revoke", "fac revoke" }, permissionNode="")
    public static void teamInvite(Player sender, @Param(name="Parameter") String params) {
        String[] args = ("arg1 " + params).split(" ");
        Player p = (Player) sender;
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());

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

}