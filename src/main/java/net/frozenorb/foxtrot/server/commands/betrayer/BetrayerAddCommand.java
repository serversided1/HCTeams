package net.frozenorb.foxtrot.server.commands.betrayer;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.Betrayer;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.bukkit.ChatColor.*;

public class BetrayerAddCommand {

    @Command(names = {"betrayer add"}, permissionNode = "op")
    public static void betrayerAdd(Player sender, @Parameter(name = "player") UUID player, @Parameter(name = "reason", wildcard=true) String reason) {
        if (Foxtrot.getInstance().getServerHandler().getBetrayer(player) == null) {
            Betrayer betrayer = new Betrayer(player, sender.getUniqueId(), reason);
            Foxtrot.getInstance().getServerHandler().getBetrayers().add(betrayer);
            Foxtrot.getInstance().getServerHandler().save();

            sender.sendMessage(GREEN + "Added " + RED + UUIDUtils.name(player) + GREEN + "'s betrayer tag for " + YELLOW +  reason + GREEN + ".");
        } else {
            sender.sendMessage(RED + UUIDUtils.name(player) + " is already a betrayer.");
        }
    }

}