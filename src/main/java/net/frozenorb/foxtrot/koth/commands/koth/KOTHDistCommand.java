package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHDistCommand {

    @Command(names={ "KOTH Dist" }, permission="foxtrot.koth.admin")
    public static void kothDist(Player sender, @Param(name="koth") KOTH koth, @Param(name="distance") int distance) {
        koth.setCapDistance(distance);
        sender.sendMessage(ChatColor.GRAY + "Set max distance for the " + koth.getName() + " KOTH.");
    }

}