package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/1/2014.
 */
public class HomeALIAS extends BaseCommand {

    public HomeALIAS() {
        super("home");
    }

    @Override
    public void syncExecute() {
        ((Player) sender).performCommand("f home");
    }
}
