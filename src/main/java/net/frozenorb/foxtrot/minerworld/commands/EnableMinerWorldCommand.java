package net.frozenorb.foxtrot.minerworld.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EnableMinerWorldCommand {

    @Command(names = {"enableminerworld"}, permission = "op")
    public static void enableMinerWorld(Player sender) {
        Foxtrot.getInstance().getMinerWorldHandler().setEnabled(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                Foxtrot.getInstance().getMinerWorldHandler().save();
            }
        }.runTaskAsynchronously(qLib.getInstance());
        sender.sendMessage(ChatColor.YELLOW + "Miner World has been enabled.");
    }

}
