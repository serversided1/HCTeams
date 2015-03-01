package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.qlib.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHHelpCommand {

    @Command(names={ "KOTH Help" }, permissionNode="foxtrot.koth")
    public static void kothHelp(Player sender) {
        sender.sendMessage(ChatColor.RED + "/koth list - Lists KOTHs");
        sender.sendMessage(ChatColor.RED + "/koth activate <name> - Activates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth deactivate <name> - Deactivates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth loc <name> - Set a KOTH's cap location");
        sender.sendMessage(ChatColor.RED + "/koth time <name> <time> - Sets a KOTH's cap time");
        sender.sendMessage(ChatColor.RED + "/koth dist <name> <distance> - Sets a KOTH's cap distance");
        sender.sendMessage(ChatColor.RED + "/koth level <name> <level> - Sets a KOTH's level");
        sender.sendMessage(ChatColor.RED + "/koth tp <name> - TPs to a KOTH's");
        sender.sendMessage(ChatColor.RED + "/koth create <name> - Creates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth delete <name> - Deletes a KOTH");
    }

}