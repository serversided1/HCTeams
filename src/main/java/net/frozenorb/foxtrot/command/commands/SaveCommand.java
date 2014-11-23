package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.jedis.RedisSaveTask;
import org.bukkit.entity.Player;

public class SaveCommand {

    @Command(names={ "Save", "SaveRedis" }, permissionNode="op")
    public static void save(Player sender) {
        RedisSaveTask.save(false);
    }

    @Command(names={ "Save ForceAll", "SaveRedis ForceAll" }, permissionNode="op")
    public static void saveForceAll(Player sender) {
        RedisSaveTask.save(true);
    }

}