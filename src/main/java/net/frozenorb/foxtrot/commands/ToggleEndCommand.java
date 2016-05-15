package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.listener.EndListener;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleEndCommand {

    @Command(names={ "ToggleEnd" }, permission="foxtrot.toggleend")
    public static void toggleEnd(Player sender) {
        EndListener.endActive = !EndListener.endActive;
        sender.sendMessage(ChatColor.YELLOW + "End enabled? " + ChatColor.LIGHT_PURPLE + (EndListener.endActive ? "Yes" : "No"));
    }

}