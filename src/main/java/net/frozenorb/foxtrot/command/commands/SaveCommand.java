package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.jedis.RedisSaveTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCommand {

    @Command(names={ "Save", "SaveRedis" }, permissionNode="op")
    public static void save(CommandSender sender) {
        RedisSaveTask.save(false);
    }

    @Command(names={ "Save ForceAll", "SaveRedis ForceAll" }, permissionNode="op")
    public static void saveForceAll(CommandSender sender) {
        RedisSaveTask.save(true);
    }

}