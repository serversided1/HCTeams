package net.frozenorb.foxtrot.server.commands.prefix;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class PrefixListCommand {

    @Command(names={ "prefix list" }, permissionNode="op")
    public static void prefixList(Player sender) {
        for (Map.Entry<String, String> prefixEntry : FoxtrotPlugin.getInstance().getServerHandler().getCustomPrefixes().entrySet()) {
            sender.sendMessage(ChatColor.YELLOW + prefixEntry.getKey() + ": " + ChatColor.RESET + prefixEntry.getValue());
        }
    }

}