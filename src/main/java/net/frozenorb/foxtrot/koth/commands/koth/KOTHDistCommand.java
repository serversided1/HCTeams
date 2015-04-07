package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHDistCommand {

    @Command(names={ "KOTH Dist" }, permissionNode="foxtrot.koth.admin")
    public static void kothDist(Player sender, @Parameter(name="koth") KOTH koth, @Parameter(name="distance") int distance) {
        koth.setCapDistance(distance);
        sender.sendMessage(ChatColor.GRAY + "Set max distance for the " + koth.getName() + " KOTH.");
    }

}