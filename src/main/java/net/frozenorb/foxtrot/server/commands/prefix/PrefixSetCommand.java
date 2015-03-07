package net.frozenorb.foxtrot.server.commands.prefix;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PrefixSetCommand {

    @Command(names={ "prefix set" }, permissionNode="op")
    public static void prefixList(Player sender, @Parameter(name="target") String target, @Parameter(name="prefix", wildcard=true) String prefix) {
        if (!prefix.equals("null")) {
            FoxtrotPlugin.getInstance().getServerHandler().getCustomPrefixes().put(target, ChatColor.translateAlternateColorCodes('&', prefix));
        } else {
            FoxtrotPlugin.getInstance().getServerHandler().getCustomPrefixes().remove(target);
        }

        FoxtrotPlugin.getInstance().getServerHandler().save();

        sender.sendMessage(ChatColor.YELLOW + "Prefix updated.");
    }

}