package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class TagMeCommand {

    @Command(names={ "tagme" }, permission="op")
    public static void tagMe(Player sender) {
        SpawnTagHandler.addOffensiveSeconds(sender, SpawnTagHandler.getMaxTagTime());
    }

}