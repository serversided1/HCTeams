package net.frozenorb.foxtrot.server.commands.highroller;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.util.UUIDUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HighrollerRemoveCommand {

    @Command(names={ "highroller remove", "highrollers remove" }, permissionNode="op")
    public static void highrollerRemove(Player sender, @Parameter(name="Player") UUID target) {
        String name = UUIDUtils.name(target);

        if (Foxtrot.getInstance().getServerHandler().getHighRollers().contains(name)) {
            Foxtrot.getInstance().getServerHandler().getHighRollers().remove(name);
            Foxtrot.getInstance().getServerHandler().save();
            sender.sendMessage(ChatColor.GREEN + "Removed " + name + "'s HighRoller tag.");
        } else {
            sender.sendMessage(ChatColor.RED + name + " isn't a HighRoller.");
        }
    }

}