package net.frozenorb.foxtrot.server.commands.betrayer;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.Betrayer;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static net.frozenorb.qlib.util.UUIDUtils.name;
import static org.bukkit.ChatColor.*;

public class BetrayerInfoCommand {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yy HH:mm:ss z");

    @Command(names = {"betrayer info", "bitch info"}, permission = "")
    public static void betrayerList(Player sender, @Param(name = "player") UUID player) {
        Betrayer betrayer = Foxtrot.getInstance().getServerHandler().getBetrayer(player);

        if (betrayer != null) {
            sender.sendMessage(GOLD + "=====" + WHITE + " Betrayer Information " + GOLD + "=====");
            sender.sendMessage(GOLD + "Betrayer: " + LIGHT_PURPLE + name(betrayer.getUuid()) + GOLD + " Added by: " + LIGHT_PURPLE + name(betrayer.getAddedBy()));
            sender.sendMessage(GOLD + "When: " + LIGHT_PURPLE + sdf.format(new Date(betrayer.getTime())) + GOLD + " Why: " + LIGHT_PURPLE + betrayer.getReason());
        } else {
            sender.sendMessage(RED + "Could not find betrayer info for " + YELLOW + name(player) + RED + "!");
        }
    }
}
