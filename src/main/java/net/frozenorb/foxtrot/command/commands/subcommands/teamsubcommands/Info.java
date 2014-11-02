package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Info {

    @Command(names={ "team info", "t info", "f info", "faction info", "fac info", "team who", "t who", "f who", "faction who", "fac who", "team show", "t show", "f show", "faction show", "fac show" }, permissionNode="")
    public static void teamForceLeave(Player sender, @Param(name="Parameter", defaultValue="self") String params) {
		Player p = (Player) sender;
		if (!params.equals("self")) {
            //Try player
			Player target = Bukkit.getPlayer(params);

			if (target == null || !p.canSee(target)) {
                //Bad player, try faction name
                Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(params);

                if(team != null){
                    team.sendTeamInfo(p);
                } else {
                    if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(params) == null) {
                        p.sendMessage(ChatColor.GRAY + "Couldn't find a faction or player by that name.");
                    } else {
                        team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(params);
                        team.sendTeamInfo(p);
                    }
                }
			} else {
				if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(target.getName()) == null) {
					p.sendMessage(ChatColor.GRAY + "That player is online, but not on a team!");
				} else {
					net.frozenorb.foxtrot.team.Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(target.getName());

					team.sendTeamInfo(p);

				}
			}
		} else {
			if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName()) == null)
				p.sendMessage(ChatColor.GRAY + "You are not on a team!");
			else {
				net.frozenorb.foxtrot.team.Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());

				team.sendTeamInfo(p);

			}
		}
	}
}
