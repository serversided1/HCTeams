package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/1/2014.
 */
public class MapALIAS extends BaseCommand {

    public MapALIAS() {
        super("map");
    }

    @Override
    public void syncExecute() {
        ((Player) sender).performCommand("f map");
    }
}
