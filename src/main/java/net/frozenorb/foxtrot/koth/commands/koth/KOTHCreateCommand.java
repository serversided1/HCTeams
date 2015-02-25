package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHCreateCommand {

    @Command(names={ "KOTH Create" }, permissionNode="foxtrot.koth.admin")
    public static void kothCreate(Player sender, @Param(name="KOTH") String target) {
        new KOTH(target, sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created a KOTH named " + target + ".");
    }

}