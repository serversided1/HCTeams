package net.frozenorb.foxtrot.minerworld.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.minerworld.blockregen.BlockRegenHandler;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MinerWorldSetRegenCommand {

    @Command(names = {"minerworldsetregen"}, permission = "op")
    public static void minerWorldSetRegen(Player sender, @Param(name="material") String material,  @Param(name="seconds") int seconds) {
        BlockRegenHandler.getRegenerationTime().put(Material.valueOf(material.toUpperCase()), seconds);

        new BukkitRunnable() {

            @Override
            public void run() {
                Foxtrot.getInstance().getMinerWorldHandler().save();
            }

        }.runTaskAsynchronously(qLib.getInstance());

        sender.sendMessage(ChatColor.YELLOW + "Regen time has been updated.");
    }

}
