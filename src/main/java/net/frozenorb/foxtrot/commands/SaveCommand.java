package net.frozenorb.foxtrot.commands;

import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.foxtrot.jedis.RedisSaveTask;
import org.bukkit.command.CommandSender;

public class SaveCommand {

    @Command(names={ "SaveRedis", "Save" }, permissionNode="op")
    public static void saveRedis(CommandSender sender) {
        RedisSaveTask.save(false);
    }

    @Command(names={ "SaveRedis ForceAll", "Save ForceAll" }, permissionNode="op")
    public static void saveRedisForceAll(CommandSender sender) {
        RedisSaveTask.save(true);
    }

}