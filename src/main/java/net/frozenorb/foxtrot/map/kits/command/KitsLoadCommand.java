package net.frozenorb.foxtrot.map.kits.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.map.kits.Kit;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class KitsLoadCommand {
    
    @Command(names = { "kits load" }, permission = "op")
    public static void kits_load(Player sender, @Param(name = "kit", wildcard = true) Kit kit) {
        kit.apply(sender);
        
        sender.sendMessage(ChatColor.YELLOW + "Applied the " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + ".");
    }

}
