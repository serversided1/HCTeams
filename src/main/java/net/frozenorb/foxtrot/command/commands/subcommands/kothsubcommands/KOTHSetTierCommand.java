package net.frozenorb.foxtrot.command.commands.subcommands.kothsubcommands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/4/2014.
 */
public class KOTHSetTierCommand {

    @Command(names={ "KOTH Tier" }, permissionNode="foxtrot.koth")
    public static void kothSetTier(Player sender, @Param(name="KOTH") KOTH target, @Param(name="Tier") int tier) {
        target.setTier(tier);
        sender.sendMessage(ChatColor.GRAY + "Set tier for the " + target.getName() + " KOTH.");
    }

}