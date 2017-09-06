package net.frozenorb.foxtrot.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class CustomTimerCreateCommand {

    @Getter private static Map<String, Long> customTimers = new HashMap<>();

    @Command(names={ "customtimer create" }, permission="foxtrot.customtimer")
    public static void customTimerCreate(CommandSender sender, @Param(name="time") int time, @Param(name="title", wildcard=true) String title) {
        if (time == 0) {
            customTimers.remove(title);
        } else {
            customTimers.put(title, System.currentTimeMillis() + (time * 1000));
        }
    }

}