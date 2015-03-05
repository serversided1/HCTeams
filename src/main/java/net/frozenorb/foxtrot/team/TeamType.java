package net.frozenorb.foxtrot.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.interfaces.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TeamType implements ParameterType<Team> {

    public Team transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(((Player) sender).getUniqueId());

            if (team == null) {
                sender.sendMessage(ChatColor.GRAY + "You're not on a team!");
                return (null);
            }

            return (team);
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(source);

        if (team == null) {
            Player bukkitPlayer = FoxtrotPlugin.getInstance().getServer().getPlayer(source);

            if (bukkitPlayer != null) {
                team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(bukkitPlayer);

                if (team == null) {
                    sender.sendMessage(ChatColor.RED + "No team with the name or member " + source + " found.");
                    return (null);
                }
            } else {
                // There's no player or no team. Not much we can do.
                return (null);
            }
        }

        return (team);
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            if (StringUtils.startsWithIgnoreCase(team.getName(), source)) {
                completions.add(team.getName());
            }
        }

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
                completions.add(player.getName());
            }
        }

        return (completions);
    }

}