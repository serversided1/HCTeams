package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHDeleteCommand {

    @Command(names={ "KOTH Delete" }, permission="foxtrot.koth.admin")
    public static void kothDelete(Player sender, @Param(name="koth") KOTH koth) {
        Foxtrot.getInstance().getKOTHHandler().getKOTHs().remove(koth);
        Foxtrot.getInstance().getKOTHHandler().saveKOTHs();
        sender.sendMessage(ChatColor.GRAY + "Deleted KOTH " + koth.getName() + ".");
    }

}