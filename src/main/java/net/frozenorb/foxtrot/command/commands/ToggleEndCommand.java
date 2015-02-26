package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.listener.EndListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleEndCommand {

    @Command(names={ "ToggleEnd" }, permissionNode="foxtrot.toggleend")
    public static void toggleEnd(Player sender) {
        EndListener.endActive = !EndListener.endActive;
        sender.sendMessage(ChatColor.YELLOW + "End enabled? " + ChatColor.LIGHT_PURPLE + (EndListener.endActive ? "Yes" : "No"));
    }

}