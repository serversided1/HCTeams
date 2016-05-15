package net.frozenorb.foxtrot.server.commands.highroller;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HighrollerAddCommand {

    @Command(names={ "highroller add", "highrollers add" }, permission="op")
    public static void highrollerAdd(Player sender, @Param(name="player") UUID player) {
        if (!Foxtrot.getInstance().getServerHandler().getHighRollers().contains(player)) {
            Foxtrot.getInstance().getServerHandler().getHighRollers().add(player);
            Foxtrot.getInstance().getServerHandler().save();
            sender.sendMessage(ChatColor.GREEN + "Added " + UUIDUtils.name(player) + "'s HighRoller tag.");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is already a HighRoller.");
        }
    }

}