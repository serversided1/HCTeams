package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class SetWorldBorderCommand {

    @Command(names={ "SetWorldBorder" }, permission="op")
    public static void setWorldBorder(Player sender, @Param(name="border") int border) {
        BorderListener.BORDER_SIZE = border;
        sender.sendMessage(ChatColor.GRAY + "The world border is now set to " + BorderListener.BORDER_SIZE + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                Foxtrot.getInstance().getMapHandler().saveBorder();
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}