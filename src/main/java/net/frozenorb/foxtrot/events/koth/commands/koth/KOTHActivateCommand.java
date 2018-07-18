package net.frozenorb.foxtrot.events.koth.commands.koth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class KOTHActivateCommand {

    @Command(names={ "KOTH Activate", "KOTH Active", "events activate" }, permission="foxtrot.activatekoth")
    public static void kothActivate(Player sender, @Param(name="event") Event koth) {
        // Don't start a KOTH if another one is active.
        for (Event otherKoth : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (otherKoth.isActive()) {
                sender.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active.");
                return;
            }
        }

        if( (koth.getName().equalsIgnoreCase("citadel") || koth.getName().toLowerCase().contains("conquest")) && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Only ops can use the activate command for weekend events.");
            return;
        }

        koth.activate();
        sender.sendMessage(ChatColor.GRAY + "Activated " + koth.getName() + ".");
    }

}
