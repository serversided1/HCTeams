package net.frozenorb.foxtrot.server.commands.prefix;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PrefixSetCommand {

    @Command(names={ "prefix set" }, permissionNode="op")
    public static void prefixList(Player sender, @Param(name="target") String target, @Param(name="prefix", wildcard=true) String prefix) {
        if (!prefix.equals("null")) {
            FoxtrotPlugin.getInstance().getServerHandler().getCustomPrefixes().put(target, ChatColor.translateAlternateColorCodes('&', prefix));
        } else {
            FoxtrotPlugin.getInstance().getServerHandler().getCustomPrefixes().remove(target);
        }

        FoxtrotPlugin.getInstance().getServerHandler().save();

        sender.sendMessage(ChatColor.YELLOW + "Prefix updated.");
    }

}