package net.frozenorb.foxtrot.team.subclaim;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.qlib.command.interfaces.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubclaimType implements ParameterType<Subclaim> {

    public Subclaim transform(CommandSender sender, String source) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return (null);
        }

        if (sender instanceof Player && source.equals("location")) {
            Subclaim subclaim = team.getSubclaim(((Player) sender).getLocation());

            if (subclaim == null) {
                sender.sendMessage(ChatColor.RED + "You are not inside of a subclaim.");
                return (null);
            }

            return (subclaim);
        }

        Subclaim subclaim = team.getSubclaim(source);

        if (subclaim == null) {
            sender.sendMessage(ChatColor.RED + "No subclaim with the name " + source + " found.");
            return (null);
        }

        return (subclaim);
    }

    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<>();
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            return (completions);
        }


        for (Subclaim subclaim : team.getSubclaims()) {
            if (StringUtils.startsWithIgnoreCase(subclaim.getName(), source)) {
                completions.add(subclaim.getName());
            }
        }

        return (completions);
    }


}