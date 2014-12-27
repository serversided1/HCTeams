package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/1/2014.
 */
public class SpawnDragonCommand {

    @Command(names = {"SpawnDragon"}, permissionNode="op")
    public static void spawnDragon(Player sender) {
        if (sender.getWorld().getEnvironment() != World.Environment.THE_END) {
            sender.sendMessage(ChatColor.RED + "You must be in the end.");
            return;
        }

        sender.getWorld().spawnCreature(sender.getLocation(), EntityType.ENDER_DRAGON);
        sender.sendMessage("Spawned enderdragon.");
    }

}