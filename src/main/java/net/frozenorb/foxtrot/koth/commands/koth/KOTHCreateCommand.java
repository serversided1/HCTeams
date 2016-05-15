package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHCreateCommand {

    @Command(names={ "KOTH Create" }, permission="foxtrot.koth.admin")
    public static void kothCreate(Player sender, @Param(name="koth") String koth) {
        new KOTH(koth, sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created a KOTH named " + koth + ".");
    }

}