package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Prot3Sharp3Command {

    @Command(names={ "prot3sharp3" }, permission="")
    public static void prot3Sharp3(Player sender) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isSquads()) {
            return;
        }

        if (Foxtrot.getInstance().getP3S3AckMap().acknowledgedP3S3(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You've already acknowledged the kit change!");
        } else {
            Foxtrot.getInstance().getP3S3AckMap().markAcknowledgedP3S3(sender.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Acknowledged Protection 3 Sharpness 3 kit change.");
        }
    }

}