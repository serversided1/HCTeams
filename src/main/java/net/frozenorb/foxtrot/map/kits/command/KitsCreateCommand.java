package net.frozenorb.foxtrot.map.kits.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kits.Kit;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class KitsCreateCommand {

    @Command(names = { "kits create" }, permission = "op")
    public static void kits_create(Player sender, @Param(name = "name", wildcard = true) String name) {
        if (Foxtrot.getInstance().getMapHandler().getKitManager().get(name) != null) {
            sender.sendMessage(ChatColor.RED + "That kit already exists.");
            return;
        }
            
        Kit kit = Foxtrot.getInstance().getMapHandler().getKitManager().getOrCreate(name);
        
        kit.update(sender.getInventory());
        Foxtrot.getInstance().getMapHandler().getKitManager().save();
        
        sender.sendMessage(ChatColor.YELLOW + "The " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW
                + " kit has been created from your inventory.");
    }

}
