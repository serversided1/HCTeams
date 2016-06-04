package net.frozenorb.foxtrot.minerworld.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveMinerWorldCommand {

    @Command(names = {"saveminerworld"}, permission = "op")
    public static void saveMinerWorld(Player sender) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Foxtrot.getInstance().getMinerWorldHandler().save();
            }

        }.runTaskAsynchronously(qLib.getInstance());

        sender.sendMessage(ChatColor.YELLOW + "Miner World has been saved.");
    }

}
