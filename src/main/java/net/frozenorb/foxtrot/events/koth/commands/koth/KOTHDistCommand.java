package net.frozenorb.foxtrot.events.koth.commands.koth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.events.EventType;
import net.frozenorb.foxtrot.events.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class KOTHDistCommand {

    @Command(names={ "KOTH Dist" }, permission="foxtrot.koth.admin")
    public static void kothDist(Player sender, @Param(name="koth") Event koth, @Param(name="distance") int distance) {
        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Can only set distance for KOTHs");
            return;
        }

        ((KOTH) koth).setCapDistance(distance);
        sender.sendMessage(ChatColor.GRAY + "Set max distance for the " + koth.getName() + " KOTH.");
    }

}