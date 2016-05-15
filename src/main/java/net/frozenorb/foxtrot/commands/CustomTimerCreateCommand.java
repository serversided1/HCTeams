package net.frozenorb.foxtrot.commands;

import lombok.Getter;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CustomTimerCreateCommand {

    @Getter private static Map<String, Long> customTimers = new HashMap<>();

    @Command(names={ "customtimer create" }, permission="op")
    public static void customTimerCreate(Player sender, @Param(name="time") int time, @Param(name="title", wildcard=true) String title) {
        if (time == 0) {
            customTimers.remove(title);
        } else {
            customTimers.put(title, System.currentTimeMillis() + (time * 1000));
        }
    }

}