package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Create {

    @Command(names={ "team create", "t create", "f create", "faction create", "fac create" }, permissionNode="")
    public static void teamCreate(Player sender, @Param(name="team") String name) {
        if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName()) == null) {
            if (!name.matches("^[a-zA-Z0-9]*$")) {
                sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
                return;
            }

            // Do we need this?
            //name = name.substring(0, 1).toUpperCase() + name.substring(1);

            if (name.length() > 16) {
                sender.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
                return;
            }

            if (name.length() < 3) {
                sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
                return;
            }

            if (!FoxtrotPlugin.getInstance().getTeamHandler().teamExists(name)) {
                net.frozenorb.foxtrot.team.Team team = new net.frozenorb.foxtrot.team.Team(name);

                team.setOwner(sender.getName());
                team.setFriendlyName(name);
                team.setDtr(1);

                FoxtrotPlugin.getInstance().getTeamHandler().addTeam(team);
                FoxtrotPlugin.getInstance().getTeamHandler().setTeam(sender.getName(), team);


                sender.sendMessage(ChatColor.DARK_AQUA + "Team Created!");
                sender.sendMessage(ChatColor.GRAY + "To learn more about teams, do /team");

                FactionActionTracker.logAction(team, "actions", "Faction created. [Created by: " + sender.getName() + "]");
                FoxtrotPlugin.getInstance().getServer().broadcastMessage("§eFaction §9" + team.getName() + "§e has been §acreated §eby §f" + sender.getDisplayName());
            } else {
                sender.sendMessage(ChatColor.GRAY + "That team already exists!");
            }
        } else {
            sender.sendMessage(ChatColor.GRAY + "You're already in a team!");
        }
	}

}