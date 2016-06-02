package net.frozenorb.foxtrot.minerworld.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SetMinerPortalCommand {

    @Command(names = {"setminerportal"}, permission = "op")
    public static void setMinerPortal(Player sender) {
        Foxtrot.getInstance().getMinerWorldHandler().setPortalLocation(sender.getLocation());
        new BukkitRunnable() {
            @Override
            public void run() {
                Foxtrot.getInstance().getMinerWorldHandler().save();
            }
        }.runTaskAsynchronously(qLib.getInstance());
        sender.sendMessage(ChatColor.GREEN + "Miner World portal set to your location.");
    }

}
