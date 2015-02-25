package net.frozenorb.foxtrot.team.dtr.bitmask.tabcompleter;

import net.frozenorb.foxtrot.command.objects.ParamTabCompleter;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DTRBitmaskTypeTabCompleter extends ParamTabCompleter {

    @Override
    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<String>();

        for (DTRBitmaskType bitmaskType : DTRBitmaskType.values()) {
            if (StringUtils.startsWithIgnoreCase(bitmaskType.getName(), source)) {
                completions.add(bitmaskType.getName());
            }
        }

        return (completions);
    }

}