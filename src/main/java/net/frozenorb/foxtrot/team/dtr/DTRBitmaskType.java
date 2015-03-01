package net.frozenorb.foxtrot.team.dtr;

import net.frozenorb.qlib.command.interfaces.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DTRBitmaskType implements ParameterType<DTRBitmask> {

    public DTRBitmask transform(CommandSender sender, String source) {
        for (DTRBitmask bitmaskType : DTRBitmask.values()) {
            if (source.equalsIgnoreCase(bitmaskType.getName())) {
                return (bitmaskType);
            }
        }

        sender.sendMessage(ChatColor.RED + "No bitmask type with the name " + source + " found.");
        return (null);
    }

    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<>();

        for (DTRBitmask bitmask : DTRBitmask.values()) {
            if (StringUtils.startsWithIgnoreCase(bitmask.getName(), source)) {
                completions.add(bitmask.getName());
            }
        }

        return (completions);
    }

}