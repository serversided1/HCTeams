package net.frozenorb.foxtrot.koth;

import com.mysql.jdbc.StringUtils;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.interfaces.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KOTHType implements ParameterType<KOTH> {

    public KOTH transform(CommandSender sender, String source) {
        KOTH koth = FoxtrotPlugin.getInstance().getKOTHHandler().getKOTH(source);

        if (koth == null) {
            sender.sendMessage(ChatColor.RED + "No KOTH with the name " + source + " found.");
            return (null);
        }

        return (koth);
    }

    public List<String> tabComplete(Player sender, String source) {
        List<String> completions = new ArrayList<>();

        for (KOTH koth : FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHs()) {
            if (StringUtils.startsWithIgnoreCase(koth.getName(), source)) {
                completions.add(koth.getName());
            }
        }

        return (completions);
    }

}