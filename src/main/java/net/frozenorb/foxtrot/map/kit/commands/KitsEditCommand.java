package net.frozenorb.foxtrot.map.kit.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kit.kits.Kit;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class KitsEditCommand {
    
    @Command(names = { "kits edit" }, permission = "op")
    public static void kit_edit(Player sender, @Param(name = "kit", wildcard = true) Kit kit) {
        kit.update(sender.getInventory());
        Foxtrot.getInstance().getMapHandler().getKitManager().save();
        
        sender.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW
                + " has been edited and saved.");
    }
}
