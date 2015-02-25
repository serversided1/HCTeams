package net.frozenorb.foxtrot.team.subclaim.tabcompleter;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.objects.ParamTabCompleter;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubclaimTabCompleter extends ParamTabCompleter {

    @Override
    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<String>();
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