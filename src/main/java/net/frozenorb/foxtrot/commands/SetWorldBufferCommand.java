package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class SetWorldBufferCommand {

    @Command(names={ "SetWorldBuffer" }, permission="op")
    public static void setWorldBuffer(Player sender, @Param(name="worldBuffer") int newBuffer) {
        Foxtrot.getInstance().getMapHandler().setWorldBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The world buffer is now set to " + newBuffer + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                Foxtrot.getInstance().getMapHandler().saveWorldBuffer();
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}
