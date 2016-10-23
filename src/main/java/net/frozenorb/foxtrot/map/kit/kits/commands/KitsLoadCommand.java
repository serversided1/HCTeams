package net.frozenorb.foxtrot.map.kit.kits.commands;

import net.frozenorb.foxtrot.map.kit.kits.Kit;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitsLoadCommand {

    @Command(names = {"kits load"}, permission = "op")
    public static void kits_load(Player sender, @Param(name = "kit") Kit kit) {
        kit.apply(sender);

        sender.sendMessage(ChatColor.YELLOW + "Applied the " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + ".");
    }

}
