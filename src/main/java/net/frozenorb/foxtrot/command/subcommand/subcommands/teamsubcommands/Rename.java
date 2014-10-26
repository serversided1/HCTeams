package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Rename extends Subcommand {

	public Rename(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute(){
        //Usage: /t rename <name>
        Player player = (Player) sender;
        Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(player.getName());

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
                if(FoxtrotPlugin.getInstance().getTeamManager().getTeam(args[1]) == null){
                    FoxtrotPlugin.getInstance().getTeamManager().renameTeam(team, args[1]);
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

				Team existing = FoxtrotPlugin.getInstance().getTeamManager().getTeam(teamName);

				if (existing == null) {
					sender.sendMessage(ChatColor.RED + "No team with the name '" + teamName + "' could be found!");
					return;
				}

				if (FoxtrotPlugin.getInstance().getTeamManager().getTeam(newName) != null) {
					sender.sendMessage(ChatColor.RED + "A team with the name '" + newName + "' exists!");
					return;
				}

				FoxtrotPlugin.getInstance().getTeamManager().renameTeam(existing, newName);
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
