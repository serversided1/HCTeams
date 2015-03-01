package net.frozenorb.foxtrot.commands;

import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import org.bukkit.entity.Player;

public class TagMeCommand {

    @Command(names={ "tagme" }, permissionNode="op")
    public static void tagMe(Player sender) {
        SpawnTagHandler.addSeconds(sender, SpawnTagHandler.MAX_SPAWN_TAG);
    }

}