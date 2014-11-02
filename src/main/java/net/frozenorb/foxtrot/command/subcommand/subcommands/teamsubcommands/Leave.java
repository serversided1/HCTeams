package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class Leave extends Subcommand {

    @Getter
    private static HashMap<Player, Long> createCooldown = new HashMap<>();

	public Leave(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;
		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

		if (team == null) {
			p.sendMessage(ChatColor.GRAY + "You are not on a team!");
		} else {

			if (team.isOwner(p.getName()) && team.getSize() > 1) {

				p.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
				return;
			}

			if (FoxtrotPlugin.getInstance().getTeamManager().getOwner(p.getLocation()) == team) {
				sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
				return;
			}

			p.removeMetadata("teamChat", FoxtrotPlugin.getInstance());

			if (team.removeMember(sender.getName())) {
				FoxtrotPlugin.getInstance().getTeamManager().removePlayerFromTeam(sender.getName());
				FoxtrotPlugin.getInstance().getTeamManager().removeTeam(team.getName());
				p.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");

				LandBoard.getInstance().clear(team);

			} else {
				FoxtrotPlugin.getInstance().getTeamManager().removePlayerFromTeam(sender.getName());

				team.setChanged(true);
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (team.isMember(pl)) {
						pl.sendMessage(ChatColor.YELLOW + p.getName() + " has left the team.");
					}
				}
				p.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
			}

			NametagManager.reloadPlayer(p);
			NametagManager.sendTeamsToPlayer(p);

            //Apply proper 15 minute cooldown
            createCooldown.put(p, (System.currentTimeMillis() + (1000 * (60 * 15))));

            Runnable remove = new Runnable() {
                public void run() {
                    createCooldown.remove(p);
                }
            };
            Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), remove, 20 * 60 * 15);
		}

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}
}
