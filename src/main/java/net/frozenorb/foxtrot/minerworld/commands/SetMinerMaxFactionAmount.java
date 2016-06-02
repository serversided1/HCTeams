package net.frozenorb.foxtrot.minerworld.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SetMinerMaxFactionAmount {

    @Command(names = {"setminermaxfactionamount"}, permission = "op")
    public static void setMinerMaxFactionAmount(Player sender, @Param(name = "members") int members) {
        Foxtrot.getInstance().getMinerWorldHandler().setMaxFactionAmount(members);
        new BukkitRunnable() {
            @Override
            public void run() {
                Foxtrot.getInstance().getMinerWorldHandler().save();
            }
        }.runTaskAsynchronously(qLib.getInstance());
        sender.sendMessage(ChatColor.GREEN + "Miner World maximum amount of members per faction set to " + members + ".");
    }

}
