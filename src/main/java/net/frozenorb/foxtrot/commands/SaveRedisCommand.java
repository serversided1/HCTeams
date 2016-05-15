package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.persist.RedisSaveTask;
import net.frozenorb.qlib.command.Command;
import org.bukkit.command.CommandSender;

public class SaveRedisCommand {

    @Command(names = {"SaveRedis", "Save"}, permission = "op")
    public static void saveRedis(CommandSender sender) {
        RedisSaveTask.save(sender, false);
    }

    @Command(names = {"SaveRedis ForceAll", "Save ForceAll"}, permission = "op")
    public static void saveRedisForceAll(CommandSender sender) {
        RedisSaveTask.save(sender, true);
    }

}