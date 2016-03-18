package net.frozenorb.foxtrot.server.commands.betrayer;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class BetrayerInfoCommand {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yy HH:mm:ss z");

    @Command(names={ "betrayer info" }, permissionNode="op")
    public static void betrayerInfo(Player sender, @Parameter(name="player") UUID player) {
        if (Foxtrot.getInstance().getServerHandler().getBetrayers().containsKey(player)) {
            long date = Foxtrot.getInstance().getServerHandler().getBetrayers().get(player);
            sender.sendMessage("Date Added: " + sdf.format(new Date(date)));
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is not a betrayer.");
        }
    }
}
