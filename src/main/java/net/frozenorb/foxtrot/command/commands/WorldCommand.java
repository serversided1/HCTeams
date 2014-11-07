package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Created by chasechocolate.
 */
public class WorldCommand {

    @Command(names={ "world" }, permissionNode="op")
    public static void world(Player sender, @Param(name="World") World world) {
        sender.teleport(world.getSpawnLocation());
    }

}