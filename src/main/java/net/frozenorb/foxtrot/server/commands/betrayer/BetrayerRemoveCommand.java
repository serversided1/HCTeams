package net.frozenorb.foxtrot.server.commands.betrayer;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BetrayerRemoveCommand {

    @Command(names={ "betrayer remove" }, permissionNode="op")
    public static void betrayerRemove(Player sender, @Parameter(name="player") UUID player) {
        if (Foxtrot.getInstance().getServerHandler().getBetrayers().contains(player)) {
            Foxtrot.getInstance().getServerHandler().getBetrayers().remove(player);
            Foxtrot.getInstance().getServerHandler().save();
            sender.sendMessage(ChatColor.GREEN + "Removed " + UUIDUtils.name(player) + "'s betrayer tag.");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " isn't a betrayer.");
        }
    }

}