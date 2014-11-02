package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.listener.EndListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/1/2014.
 */
public class ToggleEndCommand {

    @Command(names={ "ToggleEnd" }, permissionNode="foxtrot.toggleend")
    public static void toggleEnd(Player sender) {
        EndListener.endActive = !EndListener.endActive;
        sender.sendMessage(ChatColor.GRAY + "End enabled? " + ChatColor.DARK_AQUA + (EndListener.endActive ? "Yes" : "No"));
    }

}