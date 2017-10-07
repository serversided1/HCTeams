package net.frozenorb.foxtrot.commands;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.qlib.command.Command;

public class TagMeCommand {

    @Command(names={ "tagme" }, permission="op")
    public static void tagMe(Player sender) {
        SpawnTagHandler.addOffensiveSeconds(sender, SpawnTagHandler.getMaxTagTime());
    }

}