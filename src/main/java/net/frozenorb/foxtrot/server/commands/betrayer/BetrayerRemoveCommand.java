package net.frozenorb.foxtrot.server.commands.betrayer;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.Betrayer;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BetrayerRemoveCommand {

    @Command(names = {"betrayer remove"}, permission = "op")
    public static void betrayerRemove(Player sender, @Param(name = "player") UUID player) {
        Betrayer betrayer = Foxtrot.getInstance().getServerHandler().getBetrayer(player);
        if (betrayer != null) {
            Foxtrot.getInstance().getServerHandler().getBetrayers().remove(betrayer);
            Foxtrot.getInstance().getServerHandler().save();

            sender.sendMessage(ChatColor.GREEN + "Removed " + UUIDUtils.name(player) + "'s betrayer tag.");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " isn't a betrayer.");
        }
    }

}