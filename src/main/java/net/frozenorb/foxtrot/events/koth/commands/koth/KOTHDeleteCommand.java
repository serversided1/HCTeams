package net.frozenorb.foxtrot.events.koth.commands.koth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class KOTHDeleteCommand {

    @Command(names={ "KOTH Delete", "events delete", "event delete" }, permission="foxtrot.koth.admin")
    public static void kothDelete(Player sender, @Param(name="koth") Event koth) {
        Foxtrot.getInstance().getEventHandler().getEvents().remove(koth);
        Foxtrot.getInstance().getEventHandler().saveEvents();
        sender.sendMessage(ChatColor.GRAY + "Deleted event " + koth.getName() + ".");
    }

}