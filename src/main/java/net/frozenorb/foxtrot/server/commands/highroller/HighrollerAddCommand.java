package net.frozenorb.foxtrot.server.commands.highroller;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HighrollerAddCommand {

    @Command(names={ "highroller add", "highrollers add" }, permissionNode="op")
    public static void highrollerAdd(Player sender, @Parameter(name="Player") UUID target) {
        String name = UUIDUtils.name(target);

        if (!Foxtrot.getInstance().getServerHandler().getHighRollers().contains(name)) {
            Foxtrot.getInstance().getServerHandler().getHighRollers().add(name);
            Foxtrot.getInstance().getServerHandler().save();
            sender.sendMessage(ChatColor.GREEN + "Added " + name + "'s HighRoller tag.");
        } else {
            sender.sendMessage(ChatColor.RED + name + " is already a HighRoller.");
        }
    }

}