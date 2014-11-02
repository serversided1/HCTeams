package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.entity.Player;

public class HelpCommand {

    @Command(names={ "Help" }, permissionNode="")
    public static void help(Player sender) {
        sender.sendMessage("§eWelcome to Operation Foxtrot Alpha Testing! Please contact an admin for help.");
    }

}