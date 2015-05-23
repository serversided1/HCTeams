package net.frozenorb.foxtrot.server.commands.betrayer;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BetrayerAddCommand {

    @Command(names={ "betrayer add" }, permissionNode="op")
    public static void betrayerAdd(Player sender, @Parameter(name="player") UUID player) {
        if (!Foxtrot.getInstance().getServerHandler().getBetrayers().contains(player)) {
            Foxtrot.getInstance().getServerHandler().getBetrayers().add(player);
            Foxtrot.getInstance().getServerHandler().save();
            sender.sendMessage(ChatColor.GREEN + "Added " + UUIDUtils.name(player) + "'s betrayer tag.");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is already a betrayer.");
        }
    }

}