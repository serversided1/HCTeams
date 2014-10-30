package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import org.bukkit.entity.Player;

/**
 * @author Connor Hollasch
 * @since 10/14/14
 */
public class StuckALIAS extends BaseCommand {

    public StuckALIAS() {
        super("stuck");
    }

    @Override
    public void syncExecute() {
        ((Player) sender).performCommand("f stuck");
    }
}
