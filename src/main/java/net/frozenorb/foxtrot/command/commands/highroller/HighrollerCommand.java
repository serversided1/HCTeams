package net.frozenorb.foxtrot.command.commands.highroller;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/6/2014.
 */
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