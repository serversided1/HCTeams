package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Create {

    @Command(names={ "team create", "t create", "f create", "faction create", "fac create" }, permissionNode="")
    public static void teamInvite(Player sender, @Param(name="Parameter") String params) {
        String[] args = ("arg1 " + params).split(" ");
		Player p = (Player) sender;
		if (args.length == 2) {
			if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName()) == null) {
				if (!(args[1].matches("^[a-zA-Z0-9]*$"))){
					p.sendMessage(ChatColor.GRAY + "Team names can only be alphabetical.");
					return;
				}
				String name = args[1];
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				if (name.length() > 16) {
					p.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
					return;

				}
				if (name.length() < 3) {
					p.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");

					return;
				}

				if (!FoxtrotPlugin.getInstance().getTeamHandler().teamExists(name)) {
					net.frozenorb.foxtrot.team.Team team = new net.frozenorb.foxtrot.team.Team(name);
					team.setOwner(p.getName());
					team.setFriendlyName(name);
                    team.setDtr(1);
					FoxtrotPlugin.getInstance().getTeamHandler().addTeam(team);
					FoxtrotPlugin.getInstance().getTeamHandler().setTeam(p.getName(), team);
					p.sendMessage(ChatColor.DARK_AQUA + "Team Created!");
					p.sendMessage(ChatColor.GRAY + "To learn more about teams, do /team");

					Bukkit.broadcastMessage("§eFaction §9" + team.getName() + "§e has been §acreated §eby §f" + p.getDisplayName());

				} else {
					p.sendMessage(ChatColor.GRAY + "That team already exists!");
				}
			} else {
				p.sendMessage(ChatColor.GRAY + "You're already in a team!");
			}
			return;
		} else {

		}
	}

}