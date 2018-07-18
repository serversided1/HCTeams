package net.frozenorb.foxtrot.events.koth.commands.koth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.events.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class KOTHCreateCommand {

    @Command(names={ "KOTH Create" }, permission="foxtrot.koth.admin")
    public static void kothCreate(Player sender, @Param(name="koth") String koth) {
        new KOTH(koth, sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created a KOTH named " + koth + ".");
    }

}