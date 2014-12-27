package net.frozenorb.foxtrot.ctf.commands.ctf;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CTFCommand {

    @Command(names={ "ctf" }, permissionNode="")
    public static void ctf(Player sender) {
        sender.sendMessage(ChatColor.RED + "/flags - displays the status of all flags");
        sender.sendMessage(ChatColor.RED + "/ctf score - displays the teams with the more flag captures");
        sender.sendMessage(ChatColor.RED + "/ctf drop - drops a flag, if you have one");
        sender.sendMessage(ChatColor.RED + "/flag locate <flag> - displays more information about a specific flag");
    }

}