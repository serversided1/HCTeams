package net.frozenorb.foxtrot.command.commands.koth;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHDeleteCommand {

    @Command(names={ "KOTH Delete" }, permissionNode="foxtrot.koth.admin")
    public static void kothDelete(Player sender, @Param(name="KOTH") KOTH target) {
        KOTHHandler.getKOTHs().remove(target);
        KOTHHandler.saveKOTHs();
        sender.sendMessage(ChatColor.GRAY + "Deleted KOTH " + target.getName() + ".");
    }

}