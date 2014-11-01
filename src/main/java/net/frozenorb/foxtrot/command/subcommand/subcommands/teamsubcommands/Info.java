package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Info extends Subcommand {

	public Info(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		Player p = (Player) sender;
		if (args.length > 1) {
            //Try player
			Player target = Bukkit.getPlayer(args[1]);

			if (target == null || !p.canSee(target)) {
                //Bad player, try faction name
                Team team = FoxtrotPlugin.getInstance().getTeamManager().getTeam(args[1]);

                if(team != null){
                    team.sendTeamInfo(p);
                } else {
                    p.sendMessage(ChatColor.GRAY + args[1] + " isn't a team's name.");
                }
			} else {
				if (FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(target.getName()) == null) {
					p.sendMessage(ChatColor.GRAY + "That player is online, but not on a team!");
				} else {
					net.frozenorb.foxtrot.team.Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(target.getName());

					team.sendTeamInfo(p);

				}
			}
		} else {
			if (FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName()) == null)
				p.sendMessage(ChatColor.GRAY + "You are not on a team!");
			else {
				net.frozenorb.foxtrot.team.Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

				team.sendTeamInfo(p);

			}
		}
	}
}
