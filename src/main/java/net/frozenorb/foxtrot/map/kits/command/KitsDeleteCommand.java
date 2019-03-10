package net.frozenorb.foxtrot.map.kits.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kits.Kit;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class KitsDeleteCommand {
    
    @Command(names = { "kits delete" }, permission = "op")
    public static void kits_delete(Player sender, @Param(name = "kit", wildcard = true) Kit kit) {
        Foxtrot.getInstance().getMapHandler().getKitManager().delete(kit);
        
        sender.sendMessage(
                ChatColor.YELLOW + "Kit " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + " has been deleted.");
    }
}
