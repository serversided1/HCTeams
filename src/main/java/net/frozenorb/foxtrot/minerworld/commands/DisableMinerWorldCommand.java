package net.frozenorb.foxtrot.minerworld.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DisableMinerWorldCommand {

    @Command(names = {"disableminerworld"}, permission = "op")
    public static void disableMinerWorld(Player sender) {
        Foxtrot.getInstance().getMinerWorldHandler().setEnabled(false);

        new BukkitRunnable() {
            @Override
            public void run() {
                Foxtrot.getInstance().getMinerWorldHandler().save();
            }
        }.runTaskAsynchronously(qLib.getInstance());

        for (UUID uuid : Foxtrot.getInstance().getMinerWorldHandler().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                continue;
            }

            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            player.sendMessage(ChatColor.RED + "Miner World has been disabled. You have been teleported to spawn.");
        }
    }

}
