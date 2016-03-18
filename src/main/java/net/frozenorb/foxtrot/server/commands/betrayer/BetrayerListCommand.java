package net.frozenorb.foxtrot.server.commands.betrayer;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BetrayerListCommand {

    @Command(names={ "betrayer list", "betrayers" }, permissionNode="op")
    public static void betrayerList(Player sender) {
        StringBuilder betrayers = new StringBuilder();

        for (UUID betrayer : Foxtrot.getInstance().getServerHandler().getBetrayers().keySet()) {
            betrayers.append(ChatColor.GRAY).append(UUIDUtils.name(betrayer)).append(ChatColor.GOLD).append(", ");
        }

        if (betrayers.length() > 2) {
            betrayers.setLength(betrayers.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "Betrayers: " + betrayers.toString());
    }

}