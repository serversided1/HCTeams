package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.jedis.RedisSaveTask;
import net.frozenorb.foxtrot.util.BackupUtils;
import net.frozenorb.foxtrot.util.FoxCallback;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaveCommand {

    @Command(names={ "Save" }, permissionNode="op")
    public static void save(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "/saveredis - saves changes to redis");
        sender.sendMessage(ChatColor.RED + "/saveserver - [alpha] takes a backup of the server directory");
    }

    @Command(names={ "SaveRedis" }, permissionNode="op")
    public static void saveRedis(CommandSender sender) {
        RedisSaveTask.save(false);
    }

    @Command(names={ "SaveServer" }, permissionNode="op")
    public static void saveServer(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Server save and backup started.");

        BackupUtils.fullBackup(new FoxCallback() {

            @Override
            public void call(Object object) {
                sender.sendMessage(ChatColor.YELLOW + "Server save and backup completed.");
            }

        });
    }

    @Command(names={ "SaveRedis ForceAll" }, permissionNode="op")
    public static void saveRedisForceAll(CommandSender sender) {
        RedisSaveTask.save(true);
    }

}