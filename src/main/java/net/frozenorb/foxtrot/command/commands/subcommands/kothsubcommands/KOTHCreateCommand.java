package net.frozenorb.foxtrot.command.commands.subcommands.kothsubcommands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHCreateCommand {

    @Command(names={ "KOTH Create" }, permissionNode="foxtrot.koth.admin")
    public static void kothCreate(Player sender, @Param(name="KOTH") String target) {
        new KOTH(target, sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created a KOTH named " + target + ".");
    }

}