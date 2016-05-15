package net.frozenorb.foxtrot.server.commands.prefix;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class PrefixListCommand {

    @Command(names={ "prefix list" }, permission="op")
    public static void prefixList(Player sender) {
        for (Map.Entry<UUID, String> prefixEntry : Foxtrot.getInstance().getChatHandler().getAllCustomPrefixes()) {
            sender.sendMessage(ChatColor.YELLOW + FrozenUUIDCache.name(prefixEntry.getKey()) + ": " + ChatColor.RESET + prefixEntry.getValue());
        }
    }

}