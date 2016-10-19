package net.frozenorb.foxtrot.map.kit.kits.commands;

import net.frozenorb.foxtrot.map.kit.kits.Kit;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitsSaveCommand {

    @Command(names = {"kits edit"}, permission = "op")
    public static void kit_edit(Player sender, @Param(name = "kit") Kit kit) {
        kit.update(sender.getInventory());

        sender.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + " has been edited and saved.");
    }

}
