package net.frozenorb.foxtrot.server.commands.highroller;

import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class HighrollerCommand {

    @Command(names={ "highroller", "highrollers" }, permission="op")
    public static void highroller(Player sender) {
        String[] msges = {
                "§c/highroller list - Shows all HighRollers.",
                "§c/highroller add <player> - Add a HighRoller.",
                "§c/highroller remove <player> - Remove a HighRoller."};

        sender.sendMessage(msges);
    }

}