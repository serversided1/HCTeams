package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class Accept {

    @Command(names={ "team accept", "t accept", "f accept", "faction accept", "fac accept", "team a", "t a", "f a", "faction a", "fac a" }, permissionNode="")
    public static void teamAccept(Player sender, @Param(name="Parameter") String params) {
        String[] args = ("arg1 " + params).split(" ");

		Player p = (Player) sender;
		TeamHandler teamHandler = FoxtrotPlugin.getInstance().getTeamHandler();
		if (args.length > 1) {
			if (teamHandler.teamExists(args[1])) {
				Team team = teamHandler.getTeam(args[1]);

				if (team.getInvitations().contains(p.getName())) {
					if (FoxtrotPlugin.getInstance().getTeamHandler().isOnTeam(p.getName())) {
						sender.sendMessage(ChatColor.RED + "You are already on a team!");
						return;
					}

                    /*
                    if(!(p.isOp()) && Leave.getCreateCooldown().containsKey(p) && Leave.getCreateCooldown().get(p) > System.currentTimeMillis()){
                        long millisLeft = Leave.getCreateCooldown().get(p) - System.currentTimeMillis();

                        double value = (millisLeft / 1000D);
                        double sec = Math.round(10.0 * value) / 10.0;

                        p.sendMessage(ChatColor.translateAlternateColorCodes(
                                '&', "&cYou cannot join a team for another &c&l" + TimeUtils.getMMSS((int)sec) + "&c!"));
                        return;
                    }
                    */

					team.getInvitations().remove(p.getName());
					team.addMember(p.getName());
					FoxtrotPlugin.getInstance().getTeamHandler().setTeam(p.getName(), team);

					for (Player ps : Bukkit.getOnlinePlayers()) {
						if (team.isMember(ps)) {
							ps.sendMessage(ChatColor.YELLOW + p.getName() + " has joined the team!");
						}
					}
					NametagManager.reloadPlayer(p);
					NametagManager.sendTeamsToPlayer(p);
				} else {
					sender.sendMessage(ChatColor.RED + "This team has not invited you!");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "No such team could be found!");
			}
		} else
			sender.sendMessage(ChatColor.RED + "/t accept <teamName>");
	}

}