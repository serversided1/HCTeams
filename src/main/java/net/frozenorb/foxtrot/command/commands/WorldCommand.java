package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Created by chasechocolate.
 */
public class WorldCommand {

    @Command(names={ "world" }, permissionNode="op")
    public static void toggleEnd(Player sender, @Param(name="World") String world) {
        World worldObj = Bukkit.getWorld(world);

        if (worldObj != null) {
            sender.teleport(worldObj.getSpawnLocation());
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown world!");
        }
    }

}