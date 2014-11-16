package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHCommand {

    @Command(names={ "KOTH" }, permissionNode="foxtrot.koth")
    public static void koth(Player sender) {
        sender.sendMessage(ChatColor.GRAY + "/koth list - Lists KOTHs");
        sender.sendMessage(ChatColor.GRAY + "/koth activate <name> - Activates a KOTH");
        sender.sendMessage(ChatColor.GRAY + "/koth deactivate <name> - Deactivates a KOTH");
        sender.sendMessage(ChatColor.GRAY + "/koth loc <name> - Set a KOTH's cap location");
        sender.sendMessage(ChatColor.GRAY + "/koth time <name> <time> - Sets a KOTH's cap time");
        sender.sendMessage(ChatColor.GRAY + "/koth dist <name> <distance> - Sets a KOTH's cap distance");
        sender.sendMessage(ChatColor.GRAY + "/koth create <name> - Creates a KOTH");
        sender.sendMessage(ChatColor.GRAY + "/koth delete <name> - Deletes a KOTH");
    }

}