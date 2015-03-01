package net.frozenorb.foxtrot.server.commands.highroller;

import net.frozenorb.qlib.command.annotations.Command;
import org.bukkit.entity.Player;

public class HighrollerCommand {

    @Command(names={ "highroller", "highrollers" }, permissionNode="op")
    public static void pvpTimer(Player sender) {
        String[] msges = {
                "§c/highroller list - Shows all HighRollers.",
                "§c/highroller add <player> - Add a HighRoller.",
                "§c/highroller remove <player> - Remove a HighRoller."};

        sender.sendMessage(msges);
    }

}