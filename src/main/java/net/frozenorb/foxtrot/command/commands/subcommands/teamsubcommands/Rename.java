package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Rename {

    @Command(names={ "team rename", "t rename", "f rename", "faction rename", "fac rename" }, permissionNode="")
    public static void teamInvite(Player sender, @Param(name="Parameter") String params) {
        String[] args = ("arg1 " + params).split(" ");
        Player player = (Player) sender;
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if(team == null){
            player.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if(!(team.isOwner(player.getName()))){
            player.sendMessage(ChatColor.RED + "Only team owners can use this command!");
            return;
        }

        if(args.length == 2){
            if(args[1].matches("^[a-zA-Z0-9]*$")){
                if(FoxtrotPlugin.getInstance().getTeamHandler().getTeam(args[1]) == null){
                    FoxtrotPlugin.getInstance().getTeamHandler().renameTeam(team, args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Team renamed to " + args[1]);
                } else {
                    player.sendMessage(ChatColor.RED + "A team with that name already exists!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
            }

            return;
        }

        player.sendMessage(ChatColor.RED + "Please specify a new team name!");

        /*
		if(sender.hasPermission("foxtrot.rename")){
			if (args.length == 3) {
				String teamName = args[1];
				String newName = args[2];

				Team existing = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(teamName);

				if (existing == null) {
					sender.sendMessage(ChatColor.RED + "No team with the name '" + teamName + "' could be found!");
					return;
				}

				if (FoxtrotPlugin.getInstance().getTeamHandler().getTeam(newName) != null) {
					sender.sendMessage(ChatColor.RED + "A team with the name '" + newName + "' exists!");
					return;
				}

				FoxtrotPlugin.getInstance().getTeamHandler().renameTeam(existing, newName);
				sender.sendMessage(ChatColor.RED + "Team renamed to " + newName);
			} else {
				sender.sendMessage(ChatColor.RED + "/t rename <team> <newname>");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You are not allowed to do this!");
		}
		*/
	}

}
