package net.frozenorb.foxtrot.server.commands.betrayer;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.Betrayer;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BetrayerListCommand {

    @Command(names = {"betrayer list", "betrayers"}, permission = "")
    public static void betrayerList(Player sender) {
        StringBuilder betrayers = new StringBuilder();

        for (Betrayer betrayer : Foxtrot.getInstance().getServerHandler().getBetrayers()) {
            betrayers.append(ChatColor.GRAY).append(UUIDUtils.name(betrayer.getUuid())).append(ChatColor.GOLD).append(", ");
        }

        if (betrayers.length() > 2) {
            betrayers.setLength(betrayers.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "Betrayers: " + betrayers.toString());
    }

}