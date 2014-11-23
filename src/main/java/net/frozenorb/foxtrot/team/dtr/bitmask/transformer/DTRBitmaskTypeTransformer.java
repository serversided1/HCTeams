package net.frozenorb.foxtrot.team.dtr.bitmask.transformer;

import net.frozenorb.foxtrot.command.objects.ParamTransformer;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/21/2014.
 */
public class DTRBitmaskTypeTransformer extends ParamTransformer {

    @Override
    public Object transform(CommandSender sender, String source) {
        for (DTRBitmaskType bitmaskType : DTRBitmaskType.values()) {
            if (bitmaskType.getName().equalsIgnoreCase(bitmaskType.getName())) {
                return (bitmaskType);
            }
        }

        sender.sendMessage(ChatColor.RED + "No bitmask type with the name " + source + " found.");
        return (null);
    }

}