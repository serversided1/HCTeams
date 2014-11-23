package net.frozenorb.foxtrot.team.transformer;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.objects.ParamTransformer;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/21/2014.
 */
public class TeamTransformer extends ParamTransformer {

    @Override
    public Object transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

            if (team == null) {
                sender.sendMessage(ChatColor.GRAY + "You're not on a team!");
            }

            return (team);
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(source);

        if (team == null) {
            Player bukkitPlayer = FoxtrotPlugin.getInstance().getServer().getPlayer(source);

            if (bukkitPlayer != null) {
                source = bukkitPlayer.getName();
            }

            team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(source);

            if (team == null) {
                sender.sendMessage(ChatColor.RED + "No team with the name or member " + source + " found.");
                return (null);
            }
        }

        return (team);
    }

}