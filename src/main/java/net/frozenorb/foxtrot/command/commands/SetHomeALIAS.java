package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/1/2014.
 */
public class SetHomeALIAS extends BaseCommand {

    public SetHomeALIAS() {
        super("sethome");
    }

    @Override
    public void syncExecute() {
        ((Player) sender).performCommand("f sethome");
    }
}
