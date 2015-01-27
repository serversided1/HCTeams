package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import org.bukkit.entity.Player;

public class TagMeCommand {

    @Command(names={ "tagme" }, permissionNode="op")
    public static void tagMe(Player sender) {
        SpawnTagHandler.addSeconds(sender, 8);
    }

}