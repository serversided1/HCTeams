package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHDeleteCommand {

    @Command(names={ "KOTH Delete" }, permissionNode="foxtrot.koth.admin")
    public static void kothDelete(Player sender, @Param(name="KOTH") KOTH target) {
        FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHs().remove(target);
        FoxtrotPlugin.getInstance().getKOTHHandler().saveKOTHs();
        sender.sendMessage(ChatColor.GRAY + "Deleted KOTH " + target.getName() + ".");
    }

}