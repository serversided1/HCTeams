package net.frozenorb.foxtrot.events.dtc.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.events.dtc.DTC;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class DTCCreateCommand {

    @Command(names={ "DTC Create" }, permission="foxtrot.dtc.admin")
    public static void kothCreate(Player sender, @Param(name="dtc") String koth) {
        new DTC(koth, sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created a DTC named " + koth + ".");
    }

}
