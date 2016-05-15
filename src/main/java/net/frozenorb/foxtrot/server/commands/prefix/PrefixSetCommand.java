package net.frozenorb.foxtrot.server.commands.prefix;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PrefixSetCommand {

    @Command(names={ "prefix set" }, permission="op")
    public static void prefixSet(Player sender, @Param(name="player") UUID player, @Param(name="prefix", wildcard=true) String prefix) {
        if (!prefix.equals("null")) {
            Foxtrot.getInstance().getChatHandler().setCustomPrefix(player, ChatColor.translateAlternateColorCodes('&', prefix));
        } else {
            Foxtrot.getInstance().getChatHandler().setCustomPrefix(player, null);
        }

        sender.sendMessage(ChatColor.YELLOW + "Prefix updated.");
    }

}