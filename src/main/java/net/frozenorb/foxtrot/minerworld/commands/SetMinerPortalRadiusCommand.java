package net.frozenorb.foxtrot.minerworld.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SetMinerPortalRadiusCommand {

    @Command(names = {"setminerportalradius"}, permission = "op")
    public static void setMinerPortalRadius(Player sender, @Param(name = "radius") int radius) {
        Foxtrot.getInstance().getMinerWorldHandler().setPortalRadius(radius);
        new BukkitRunnable() {
            @Override
            public void run() {
                Foxtrot.getInstance().getMinerWorldHandler().save();
            }
        }.runTaskAsynchronously(qLib.getInstance());
        sender.sendMessage(ChatColor.GREEN + "Miner World portal radius set to " + radius + ".");
    }

}
