package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class SetNetherBufferCommand {

    @Command(names={ "SetNetherBuffer" }, permission="op")
    public static void setNetherBuffer(Player sender, @Param(name="netherBuffer") int newBuffer) {
        Foxtrot.getInstance().getMapHandler().setNetherBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The nether buffer is now set to " + newBuffer + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                Foxtrot.getInstance().getMapHandler().saveNetherBuffer();
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}
