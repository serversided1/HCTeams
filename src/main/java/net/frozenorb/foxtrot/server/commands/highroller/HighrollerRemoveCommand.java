package net.frozenorb.foxtrot.server.commands.highroller;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HighrollerRemoveCommand {

    @Command(names={ "highroller remove", "highrollers remove" }, permissionNode="op")
    public static void highrollerRemove(Player sender, @Parameter(name="player") UUID player) {
        if (Foxtrot.getInstance().getServerHandler().getHighRollers().contains(player)) {
            Foxtrot.getInstance().getServerHandler().getHighRollers().remove(player);
            Foxtrot.getInstance().getServerHandler().save();
            sender.sendMessage(ChatColor.GREEN + "Removed " + UUIDUtils.name(player) + "'s HighRoller tag.");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " isn't a HighRoller.");
        }
    }

}