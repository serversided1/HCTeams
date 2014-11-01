package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.listener.EndListener;
import org.bukkit.ChatColor;

/**
 * Created by macguy8 on 11/1/2014.
 */
public class ToggleEnd extends BaseCommand {

    public ToggleEnd() {
        super("toggleend");
        setPermissionLevel("foxtrot.toggleend", "§cYou are not allowed to do this, ya silly goose!");
    }

    @Override
    public void syncExecute() {
        EndListener.endActive = !EndListener.endActive;
        sender.sendMessage(ChatColor.GRAY + "End enabled? " + ChatColor.DARK_AQUA + (EndListener.endActive ? "Yes" : "No"));
    }

}
