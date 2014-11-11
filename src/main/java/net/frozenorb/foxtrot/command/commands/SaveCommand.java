package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.jedis.RedisSaveTask;
import org.bukkit.entity.Player;

public class SaveCommand {

    @Command(names={ "Save", "SaveMyShit", "SaveRedis" }, permissionNode="op")
    public static void playSound(Player sender) {
        RedisSaveTask.save();
        sender.sendMessage("§cSaved!");
    }

}