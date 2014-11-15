package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/14/2014.
 */
public class ToggleDonorOnlyCommand {

    public static boolean donorOnly = false;

    @Command(names={ "ToggleDonorOnly" }, permissionNode="")
    public static void toggleDonorOnly(Player sender) {
        donorOnly = !donorOnly;
        sender.sendMessage(ChatColor.GREEN + "Donor only mode? " + ChatColor.YELLOW + donorOnly);
    }

}