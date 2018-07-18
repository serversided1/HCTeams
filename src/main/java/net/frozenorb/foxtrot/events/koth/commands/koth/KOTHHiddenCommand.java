package net.frozenorb.foxtrot.events.koth.commands.koth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class KOTHHiddenCommand {

    @Command(names={ "KOTH Hidden", "events hidden", "event hidden" }, permission="foxtrot.koth.admin")
    public static void kothHidden(Player sender, @Param(name="koth") Event koth, @Param(name="hidden") boolean hidden) {
        koth.setHidden(hidden);
        sender.sendMessage(ChatColor.GRAY + "Set visibility for the " + koth.getName() + " event.");
    }

}